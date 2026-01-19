package redcrafter07.processed.gui.inventory

import io.netty.buffer.Unpooled
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem
import net.neoforged.neoforge.network.connection.ConnectionType
import redcrafter07.processed.block.tile_entities.capabilities.FluidHandlerModifiable
import redcrafter07.processed.gui.sync.SyncData
import redcrafter07.processed.gui.sync.SyncFieldList
import redcrafter07.processed.gui.widgets.FluidWidget
import redcrafter07.processed.rpc.MenuRPCSender
import redcrafter07.processed.rpc.RPCMethod
import redcrafter07.processed.rpc.wrappers.FluidStackList

abstract class AbstractProcessedContainerMenu(menuType: MenuType<*>, containerId: Int, player: Player) :
    AbstractContainerMenu(menuType, containerId) {
    companion object {
        const val SET_FLUID_MENU_CONTENTS: String = "processed:fluid_container_menu/set_contents"
        const val UPDATE_FLUID_MENU_CONTENT: String = "processed:fluid_container_menu/update_content"
        const val ITEM_USED: String = "processed:fluid_container_menu/item_used"

        const val SYNC_ALL: String = "processed:sync_menu_data/sync_all"
        const val UPDATE_SINGLE: String = "processed:sync_menu_data/update_single"
    }

    // Fluid stuff
    private val fluidSlots = ArrayList<FluidHandlerModifiable>()
    private val remoteFluidSlots = ArrayList<FluidStack>()
    private var fluidStateId = 0

    // Synchronisation stuff
    val syncData = SyncData(this)

    val rpcSender = MenuRPCSender.of(this, player)

    private fun nextFluidStateId() = fluidStateId.let { fluidStateId++; it }

    fun addFluidSlot(handler: FluidHandlerModifiable) {
        fluidSlots.add(handler)
        remoteFluidSlots.add(handler.getFluidInTank(0))
    }


    override fun sendAllDataToRemote() {
        super.sendAllDataToRemote()
        for (i in 0..<fluidSlots.size) remoteFluidSlots[i] = fluidSlots[i].getFluidInTank(0).copy()
        rpcSender.sendToClient(SET_FLUID_MENU_CONTENTS, FluidStackList(remoteFluidSlots), nextFluidStateId())

        val registryAccess = rpcSender.player.level().registryAccess()
        val fields = syncData.synchronisedFields()
            .map { name -> syncData.encodeField(name, registryAccess)?.let { Pair(name, it) } }.toList()
        rpcSender.sendToClient(SYNC_ALL, SyncFieldList(fields))
        syncData.clearDirtyFields(registryAccess)
    }

    override fun broadcastChanges() {
        super.broadcastChanges()
        for (i in 0..<fluidSlots.size) {
            val stack = fluidSlots[i].getFluidInTank(0)
            if (FluidStack.matches(stack, remoteFluidSlots[i])) continue
            remoteFluidSlots[i] = stack.copy()
            rpcSender.sendToClient(UPDATE_FLUID_MENU_CONTENT, i, stack, nextFluidStateId())
        }

        syncData.detectChanges()
        if (!syncData.hasChanges) return
        val registryAccess = rpcSender.player.level().registryAccess()
        for (field in syncData.dirtyFields()) {
            val data = syncData.encodeField(field, registryAccess) ?: continue
            rpcSender.sendToClient(UPDATE_SINGLE, field, data)
        }
        syncData.clearDirtyFields(registryAccess)
    }

    fun sendFluidHandlerClickToServer(slot: Int, insertionKind: FluidWidget.InsertionKind) {
        rpcSender.sendToServer(ITEM_USED, slot, insertionKind, fluidStateId, stateId)
    }

    @RPCMethod(SYNC_ALL)
    protected fun syncAllData(list: SyncFieldList) {
        val registryAccess = rpcSender.player.level().registryAccess()
        for (entry in list.inner) {
            val buf =
                RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(entry.second), registryAccess, ConnectionType.OTHER)
            syncData.synchronise(entry.first, buf)
        }
    }

    @RPCMethod(UPDATE_SINGLE)
    protected fun updateField(field: String, data: ByteArray) {
        val registryAccess = rpcSender.player.level().registryAccess()

        val buf = RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), registryAccess, ConnectionType.OTHER)
        syncData.synchronise(field, buf)
    }

    @RPCMethod(SET_FLUID_MENU_CONTENTS)
    protected fun setFluidMenuContents(fluidSlots: FluidStackList, fluidStateId: Int) {
        this.fluidStateId = fluidStateId
        val fluidSlots = fluidSlots.inner
        for (i in 0..<fluidSlots.size) setFluid(i, fluidSlots[i])
    }

    @RPCMethod(UPDATE_FLUID_MENU_CONTENT)
    protected fun updateFluidContent(slot: Int, content: FluidStack, fluidStateId: Int) {
        this.fluidStateId = fluidStateId
        setFluid(slot, content)
    }

    @RPCMethod(ITEM_USED)
    protected fun fluidHandlerClick(
        slot: Int, insertionKind: FluidWidget.InsertionKind, fluidStateId: Int, stateId: Int
    ) {
        var carried = carried.copy()

        val cap = fluidSlots.getOrNull(slot) ?: return

        val itemCap = carried.getCapability(Capabilities.FluidHandler.ITEM)
        if (itemCap == null) {
            // buckets
            val itemItem = carried.item
            if (itemItem !is BucketItem) return
            val kind = if (insertionKind == FluidWidget.InsertionKind.Both) {
                if (itemItem.content.isSame(Fluids.EMPTY)) FluidWidget.InsertionKind.ExtractOnly
                else FluidWidget.InsertionKind.InsertOnly
            } else insertionKind

            when (kind) {
                FluidWidget.InsertionKind.InsertOnly -> {
                    if (itemItem.content.isSame(Fluids.EMPTY) || cap.fill(
                            FluidStack(itemItem.content, 1000), IFluidHandler.FluidAction.SIMULATE
                        ) != 1000
                    ) return
                    cap.fill(FluidStack(itemItem.content, 1000), IFluidHandler.FluidAction.EXECUTE)
                    carried = Items.BUCKET.defaultInstance
                }

                FluidWidget.InsertionKind.ExtractOnly -> {
                    if (!itemItem.content.isSame(Fluids.EMPTY)) return
                    val fluid = cap.drain(1000, IFluidHandler.FluidAction.SIMULATE)
                    if (fluid.amount != 1000) return
                    cap.drain(1000, IFluidHandler.FluidAction.EXECUTE)
                    carried = fluid.fluid.bucket.defaultInstance
                }

                else -> throw IllegalStateException()
            }
        } else when (insertionKind) {
            FluidWidget.InsertionKind.InsertOnly -> {
                val stack = itemCap.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                val filled = cap.fill(stack, IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) itemCap.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            }

            FluidWidget.InsertionKind.ExtractOnly -> {
                val stack = cap.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                val filled = itemCap.fill(stack, IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) cap.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            }

            FluidWidget.InsertionKind.Both -> {
                val stack = itemCap.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                val filled = cap.fill(stack, IFluidHandler.FluidAction.EXECUTE)
                if (filled > 0) itemCap.drain(filled, IFluidHandler.FluidAction.EXECUTE)
                else {
                    val stack = cap.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
                    val filled = itemCap.fill(stack, IFluidHandler.FluidAction.EXECUTE)
                    if (filled > 0) cap.drain(filled, IFluidHandler.FluidAction.EXECUTE)
                }
            }
        }

        if (itemCap is IFluidHandlerItem) carried = itemCap.container

        if (!rpcSender.player.isCreative && !ItemStack.isSameItemSameComponents(this.carried, carried)) {
            this.carried = carried
        }

        if (fluidStateId == this.fluidStateId && stateId == this.stateId) broadcastChanges()
        else sendAllDataToRemote()
    }

    fun getFluid(slot: Int): IFluidHandler? = fluidSlots.getOrNull(slot)

    fun setFluid(slot: Int, fluid: FluidStack) {
        fluidSlots.getOrNull(slot)?.setFluidInTank(0, fluid)
    }
}