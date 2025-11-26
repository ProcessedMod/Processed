package redcrafter07.processed.gui

import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.ICommonPacketListener
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.block.tile_entities.capabilities.FluidHandlerModifiable
import redcrafter07.processed.network.SetFluidMenuContentsPacket
import redcrafter07.processed.network.UpdateFluidMenuContentPacket

abstract class AbstractFluidContainerMenu(menuType: MenuType<*>, containerId: Int) :
    AbstractContainerMenu(menuType, containerId) {
    private val fluidSlots = ArrayList<FluidHandlerModifiable>()
    private val remoteFluidSlots = ArrayList<FluidStack>()
    private var conn: ICommonPacketListener? = null

    fun setConn(conn: ICommonPacketListener) {
        this.conn = conn
    }

    fun addFluidSlot(handler: FluidHandlerModifiable) {
        fluidSlots.add(handler)
        remoteFluidSlots.add(handler.getFluidInTank(0))
    }

    override fun sendAllDataToRemote() {
        super.sendAllDataToRemote()
        val conn = conn ?: return
        for (i in 0..<fluidSlots.size) remoteFluidSlots[i] = fluidSlots[i].getFluidInTank(0).copy()
        conn.send(SetFluidMenuContentsPacket(containerId, remoteFluidSlots))
    }

    override fun broadcastChanges() {
        super.broadcastChanges()
        val conn = conn ?: return
        for (i in 0..<fluidSlots.size) {
            val stack = fluidSlots[i].getFluidInTank(0)
            if(FluidStack.matches(stack, remoteFluidSlots[i])) continue
            remoteFluidSlots[i] = stack.copy()
            conn.send(UpdateFluidMenuContentPacket(containerId, i, stack))
        }
    }

    fun setFluid(slot: Int, fluid: FluidStack) {
        fluidSlots.getOrNull(slot)?.setFluidInTank(0, fluid)
    }
}