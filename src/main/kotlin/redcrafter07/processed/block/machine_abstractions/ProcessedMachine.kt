package redcrafter07.processed.block.machine_abstractions

import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.common.util.INBTSerializable
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.block.tile_entities.capabilities.*
import redcrafter07.processed.gui.ConfigScreen

abstract class ProcessedMachine(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) : BlockEntity(
    type, pos, blockState
), WrenchInteractableBlock, ItemCapableBlockEntity, EnergyCapableBlockEntity, FluidCapableBlockEntity, MenuProvider {
    companion object {
        val EMPTY_ITEM_HANDLER = EmptyItemHandler()
    }

    abstract val tier: ProcessedTier

    val sides: MutableList<IoState> = listOf(
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None,
        IoState.None
    ).toMutableList()

    var isRunning = true
        private set
    val capabilityHandlers = CapabilityHandlers(this)

    fun stopMachine() {
        isRunning = false
        sync()
    }

    fun startMachine() {
        isRunning = true
        sync()
    }

    fun getSide(itemOrFluid: Boolean, side: BlockSide): IoState {
        val idx = if (itemOrFluid) side.id else side.id + 6
        return sides[idx]
    }

    fun setSide(itemOrFluid: Boolean, side: BlockSide, value: IoState) {
        val idx = if (itemOrFluid) side.id else side.id + 6
        sides[idx] = value
        setChanged()
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        val byteArray = tag.getByteArray("io_states")
        for (i in 0..11) sides[i] = if (byteArray.size <= i) IoState.None else IoState.BY_ID.apply(byteArray[i].toInt())

        capabilityHandlers.deserializeNBT(registries, tag.getCompound("capability_handlers"))
        invalidateCapabilities()
    }

    override fun saveAdditional(tag: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(tag, provider)

        tag.putByteArray("io_states", sides.stream().map { it.id.toByte() }.toList())
        tag.put("capability_handlers", capabilityHandlers.serializeNBT(provider))
    }


    fun sync() {
        val level = level
        if (level is ServerLevel) {
            val state = blockState
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL)
            setChanged()
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? = ClientboundBlockEntityDataPacket.create(this)
    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

    override fun onWrenchUse(ctx: UseOnContext, state: BlockState) {
        val me = ctx.level.getBlockEntity(ctx.clickedPos)
        if (me is ProcessedMachine) {
            if (supportedFluidHandlers.size < 2 && supportedItemHandlers.size < 2) return
            if (ctx.level.isClientSide) Minecraft.getInstance().setScreen(ConfigScreen(this, ctx.clickedPos))
        }
    }

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedPower? =
        capabilityHandlers.energyCapability

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState): IItemHandler? =
        if (side == null) null else capabilityHandlers.getItemHandlerForState(getSide(true, side))

    override fun fluidCapabilityForSide(side: BlockSide?, state: BlockState): IFluidHandler? =
        if (side == null) null else capabilityHandlers.getFluidHandlerForState(getSide(false, side))

    fun handleTick(level: Level, pos: BlockPos, state: BlockState) {
        tickNoProcessing(level, pos, state)
        if (!isRunning) return
        if (level is ClientLevel && level.isClientSide) clientTick(level, pos, state)
        else if (level is ServerLevel && !level.isClientSide) serverTick(level, pos, state)
        commonTick(level, pos, state)
    }

    open fun clientTick(level: ClientLevel, pos: BlockPos, state: BlockState) {
    }

    open fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
    }

    open fun commonTick(level: Level, pos: BlockPos, state: BlockState) {
    }

    /**
     * This function should NOT do any processing of items, as this gets called even when the machine is disabled.
     */
    open fun tickNoProcessing(level: Level, pos: BlockPos, state: BlockState) {}

    // ###########################################
    // #  E N E R G Y   C A P A B I L I T I E S  #
    // ###########################################

    protected fun useEnergyCapabilityBidirectional(capacity: Int, transferRate: Int) {
        capabilityHandlers.energyStore = SimpleEnergyStore(capacity, transferRate, transferRate, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useEnergyCapability(capacity: Int, maxInsert: Int) {
        capabilityHandlers.energyStore = SimpleEnergyStore(capacity, maxInsert, 0, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useOutputEnergyCapability(capacity: Int, maxExtract: Int) {
        capabilityHandlers.energyStore = SimpleEnergyStore(capacity, 0, maxExtract, 0)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useEnergyCapability(capability: ProcessedEnergyHandler<CompoundTag>?) {
        this.capabilityHandlers.energyStore = capability
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopEnergyCapability() {
        this.capabilityHandlers.energyStore = null
        this.invalidateCapabilities()
        this.setChanged()
    }

    /**
     * Returns the [IEnergyStorage] for the state.
     * If no Capability for that state was registered, it returns an empty [IEnergyStorage]
     *
     * @see EmptyEnergyStorage
     */
    val energyCapability: EnergyStorageModifiable
        get() = this.capabilityHandlers.energyStore ?: EmptyEnergyStorage

    fun hasEnergyCapability(): Boolean = capabilityHandlers.energyStore != null

    val energyHandlerOrNull: EnergyStorageModifiable? get() = capabilityHandlers.energyStore

    /**
     * Tries to use some power
     * @param amount The amount of power you want to use
     * @return Returns if we managed to use that much amount of power (if it returns false, we don't have enough power)
     */
    protected fun usePower(amount: Int): Boolean {
        val energyStore: ProcessedEnergyHandler<CompoundTag> = capabilityHandlers.energyStore ?: return false
        if (energyStore.energyStored < amount) return false
        energyStore.setEnergyStored(energyStore.energyStored - amount)
        return true
    }

    // #######################################
    // #  I T E M   C A P A B I L I T I E S  #
    // #######################################

    protected fun useItemCapability(state: IoState) {
        if (state == IoState.None) return
        capabilityHandlers.setItemHandlerForState(
            state, if (state == IoState.Output) SimpleOutputItemStore(1) else ProcessedItemStackHandler(1)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useItemCapability(state: IoState, size: Int) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(
            state, if (state == IoState.Output) SimpleOutputItemStore(size) else ProcessedItemStackHandler(size)
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useItemCapability(state: IoState, capability: ProcessedItemHandler<CompoundTag>?) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(state, capability)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopItemCapability(state: IoState) {
        if (state == IoState.None) return
        this.capabilityHandlers.setItemHandlerForState(state, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopItemCapability() {
        this.capabilityHandlers.setItemHandlerForState(IoState.Input, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Output, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Additional, null)
        this.capabilityHandlers.setItemHandlerForState(IoState.Extra, null)
        this.invalidateCapabilities()
        this.setChanged()
    }


    /**
     * Returns the [IItemHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [IItemHandlerModifiable]
     *
     * @see EmptyItemHandler
     */
    fun getItemCapability(state: IoState): IItemHandlerModifiable {
        val capability = capabilityHandlers.getItemHandlerForState(state) ?: return EMPTY_ITEM_HANDLER
        return capability
    }

    fun hasItemCapability(state: IoState): Boolean {
        return capabilityHandlers.getItemHandlerForState(state) != null
    }

    fun getItemCapabilityOrNull(state: IoState): IItemHandlerModifiable? {
        return capabilityHandlers.getItemHandlerForState(state)
    }

    val inputItemHandler: IItemHandlerModifiable get() = getItemCapability(IoState.Input)
    val outputItemHandler: IItemHandlerModifiable get() = getItemCapability(IoState.Output)
    val additionalItemHandler: IItemHandlerModifiable get() = getItemCapability(IoState.Additional)
    val extraItemHandler: IItemHandlerModifiable get() = getItemCapability(IoState.Extra)
    val supportedItemHandlers: Set<IoState> get() = capabilityHandlers.supportedItemHandlers


    private fun dropItemForState(state: IoState) {
        val level = level ?: return

        val capability = this.capabilityHandlers.getItemHandlerForState(state) ?: return
        if (capability.slots < 1) return
        val inventory = SimpleContainer(capability.slots)
        for (i in 0..<capability.slots) inventory.setItem(i, capability.getStackInSlot(i))
        Containers.dropContents(level, this.worldPosition, inventory)
    }

    fun dropItems() {
        dropItemForState(IoState.Input)
        dropItemForState(IoState.Output)
        dropItemForState(IoState.Additional)
        dropItemForState(IoState.Extra)
    }

    // #########################################
    // #  F L U I D   C A P A B I L I T I E S  #
    // #########################################

    protected fun useFluidCapability(state: IoState, capacity: Int) {
        useFluidCapability(state, capacity, 1)
    }

    protected fun useFluidCapability(state: IoState, capacity: Int, slots: Int) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(
            state, (if (state == IoState.Output) SimpleOutputFluidStore(
                slots, capacity
            ) else SimpleFluidStore(
                slots, capacity
            ))
        )
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun useFluidCapability(state: IoState, capability: ProcessedFluidHandler<CompoundTag>?) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(state, capability)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopFluidCapability(state: IoState) {
        if (state == IoState.None) return
        this.capabilityHandlers.setFluidHandlerForState(state, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    protected fun stopFluidCapability() {
        this.capabilityHandlers.setFluidHandlerForState(IoState.Input, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Output, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Additional, null)
        this.capabilityHandlers.setFluidHandlerForState(IoState.Extra, null)
        this.invalidateCapabilities()
        this.setChanged()
    }

    /**
     * Returns the [FluidHandlerModifiable] for the state.
     * If no Capability for that state was registered, it returns an empty [FluidHandlerModifiable]
     *
     * @see EmptyFluidHandler
     */
    protected fun getFluidCapability(state: IoState): FluidHandlerModifiable {
        val capability: FluidHandlerModifiable =
            capabilityHandlers.getFluidHandlerForState(state) ?: return EmptyFluidHandler
        return capability
    }

    protected fun hasFluidCapability(state: IoState): Boolean {
        return capabilityHandlers.getFluidHandlerForState(state) != null
    }

    fun getFluidCapabilityOrNull(state: IoState): FluidHandlerModifiable? {
        return capabilityHandlers.getFluidHandlerForState(state)
    }

    val inputFluidHandler: FluidHandlerModifiable get() = getFluidCapability(IoState.Input)
    val outputFluidHandler: FluidHandlerModifiable get() = getFluidCapability(IoState.Output)
    val additionalFluidHandler: FluidHandlerModifiable get() = getFluidCapability(IoState.Additional)
    val extraFluidHandler: FluidHandlerModifiable get() = getFluidCapability(IoState.Extra)
    val supportedFluidHandlers: Set<IoState> get() = capabilityHandlers.supportedFluidHandlers

    class CapabilityHandlers(private val attachedMachine: ProcessedMachine) : INBTSerializable<CompoundTag> {
        var supportedItemHandlers = hashSetOf(IoState.None).toMutableSet()
        var supportedFluidHandlers = hashSetOf(IoState.None).toMutableSet()

        var energyStore: ProcessedEnergyHandler<CompoundTag>?
            get() = energyCapability?.energyStore
            set(value) {
                energyCapability?.energyStore?.setOnChange(null)
                if (value == null) energyCapability = null
                else {
                    value.setOnChange(attachedMachine::sync)
                    energyCapability = ProcessedPowerStore(attachedMachine.tier, value)
                }
            }

        var energyCapability: ProcessedPowerStore<ProcessedEnergyHandler<CompoundTag>>? = null
            private set

        private var inputItemHandler: ProcessedItemHandler<CompoundTag>? = null
        private var outputItemHandler: ProcessedItemHandler<CompoundTag>? = null
        private var additionalItemHandler: ProcessedItemHandler<CompoundTag>? = null
        private var extraItemHandler: ProcessedItemHandler<CompoundTag>? = null
        private val mergedIoItemCapabilityHandler: IItemHandlerModifiable = MergedIoItemCapability(this)

        private var inputFluidHandler: ProcessedFluidHandler<CompoundTag>? = null
        private var outputFluidHandler: ProcessedFluidHandler<CompoundTag>? = null
        private var additionalFluidHandler: ProcessedFluidHandler<CompoundTag>? = null
        private var extraFluidHandler: ProcessedFluidHandler<CompoundTag>? = null
        private val mergedIoFluidCapabilityHandler: FluidHandlerModifiable = MergedIoFluidCapability(this)


        fun getItemHandlerForState(state: IoState): IItemHandlerModifiable? {
            return when (state) {
                IoState.None -> null
                IoState.Input -> inputItemHandler
                IoState.Output -> outputItemHandler
                IoState.InputOutput -> if (inputItemHandler == null && outputItemHandler == null) null else mergedIoItemCapabilityHandler
                IoState.Additional -> additionalItemHandler
                IoState.Extra -> extraItemHandler
            }
        }

        fun setItemHandlerForState(state: IoState, handler: ProcessedItemHandler<CompoundTag>?) {
            handler?.setOnChange(attachedMachine::sync)

            if (handler != null) {
                supportedItemHandlers.add(state)
                if ((state == IoState.Input && supportedItemHandlers.contains(IoState.Output)) || (state == IoState.Output && supportedItemHandlers.contains(
                        IoState.Input
                    ))
                ) supportedItemHandlers.add(IoState.InputOutput)
            } else if (state != IoState.None) {
                supportedItemHandlers.remove(state)
                if (state == IoState.Input || state == IoState.Output) supportedItemHandlers.remove(IoState.InputOutput)
            }

            when (state) {
                IoState.None, IoState.InputOutput -> Unit
                IoState.Input -> inputItemHandler = handler
                IoState.Output -> outputItemHandler = handler
                IoState.Additional -> additionalItemHandler = handler
                IoState.Extra -> extraItemHandler = handler
            }
        }


        fun getFluidHandlerForState(state: IoState): FluidHandlerModifiable? {
            return when (state) {
                IoState.None -> null
                IoState.Input -> inputFluidHandler
                IoState.Output -> outputFluidHandler
                IoState.InputOutput -> if (inputFluidHandler == null && outputFluidHandler == null) null else mergedIoFluidCapabilityHandler
                IoState.Additional -> additionalFluidHandler
                IoState.Extra -> extraFluidHandler
            }
        }

        fun setFluidHandlerForState(state: IoState, handler: ProcessedFluidHandler<CompoundTag>?) {
            handler?.setOnChange(attachedMachine::sync)

            if (handler != null) {
                supportedFluidHandlers.add(state)
                if ((state == IoState.Input && supportedFluidHandlers.contains(IoState.Output)) || (state == IoState.Output && supportedFluidHandlers.contains(
                        IoState.Input
                    ))
                ) supportedFluidHandlers.add(IoState.InputOutput)
            } else if (state != IoState.None) {
                supportedFluidHandlers.remove(state)
                if (state == IoState.Input || state == IoState.Output) supportedFluidHandlers.remove(IoState.InputOutput)
            }

            when (state) {
                IoState.None, IoState.InputOutput -> Unit
                IoState.Input -> inputFluidHandler = handler
                IoState.Output -> outputFluidHandler = handler
                IoState.Additional -> additionalFluidHandler = handler
                IoState.Extra -> extraFluidHandler = handler
            }
        }

        override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
            val tag = CompoundTag()

            // energy
            val energyStoreNbt = energyCapability?.energyStore?.serializeNBT(provider)

            if (energyStoreNbt != null) tag.put("energyStore", energyStoreNbt)


            // items
            val inputItemNbt = inputItemHandler?.serializeNBT(provider)
            val outputItemNbt = outputItemHandler?.serializeNBT(provider)
            val additionalItemNbt = additionalItemHandler?.serializeNBT(provider)
            val auxiliaryItemNbt = extraItemHandler?.serializeNBT(provider)

            if (inputItemNbt != null) tag.put("inputItemNbt", inputItemNbt)
            if (outputItemNbt != null) tag.put("outputItemNbt", outputItemNbt)
            if (additionalItemNbt != null) tag.put("additionalItemNbt", additionalItemNbt)
            if (auxiliaryItemNbt != null) tag.put("auxiliaryItemNbt", auxiliaryItemNbt)


            // fluids
            val inputFluidNbt = inputFluidHandler?.serializeNBT(provider)
            val outputFluidNbt = outputFluidHandler?.serializeNBT(provider)
            val additionalFluidNbt = additionalFluidHandler?.serializeNBT(provider)
            val auxiliaryFluidNbt = extraFluidHandler?.serializeNBT(provider)

            if (inputFluidNbt != null) tag.put("inputFluidNbt", inputFluidNbt)
            if (outputFluidNbt != null) tag.put("outputFluidNbt", outputFluidNbt)
            if (additionalFluidNbt != null) tag.put("additionalFluidNbt", additionalFluidNbt)
            if (auxiliaryFluidNbt != null) tag.put("auxiliaryFluidNbt", auxiliaryFluidNbt)

            return tag
        }

        override fun deserializeNBT(provider: HolderLookup.Provider, tag: CompoundTag) {
            // energy
            if (tag.contains("energyStore", 10)) energyCapability?.energyStore?.deserializeNBT(
                provider, tag.getCompound("energyStore")
            )

            // items
            if (tag.contains("inputItemNbt", 10)) inputItemHandler?.deserializeNBT(
                provider, tag.getCompound("inputItemNbt")
            )
            if (tag.contains("outputItemNbt", 10)) outputItemHandler?.deserializeNBT(
                provider, tag.getCompound("outputItemNbt")
            )
            if (tag.contains("additionalItemNbt", 10)) additionalItemHandler?.deserializeNBT(
                provider, tag.getCompound("additionalItemNbt")
            )
            if (tag.contains("auxiliaryItemNbt", 10)) extraItemHandler?.deserializeNBT(
                provider, tag.getCompound("auxiliaryItemNbt")
            )

            // fluids
            if (tag.contains("inputFluidNbt", 10) && inputFluidHandler != null) inputFluidHandler?.deserializeNBT(
                provider, tag.getCompound("inputFluidNbt")
            )
            if (tag.contains("outputFluidNbt", 10) && outputFluidHandler != null) outputFluidHandler?.deserializeNBT(
                provider, tag.getCompound("outputFluidNbt")
            )
            if (tag.contains("additionalFluidNbt", 10)) additionalFluidHandler?.deserializeNBT(
                provider, tag.getCompound("additionalFluidNbt")
            )
            if (tag.contains("auxiliaryFluidNbt", 10)) extraFluidHandler?.deserializeNBT(
                provider, tag.getCompound("auxiliaryFluidNbt")
            )
        }
    }
}