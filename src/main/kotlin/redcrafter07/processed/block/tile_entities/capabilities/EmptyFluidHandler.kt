package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

object EmptyFluidHandler : FluidHandlerModifiable {
    override fun setFluidInTank(tank: Int, stack: FluidStack) = Unit
    override fun getTanks(): Int = 0
    override fun getFluidInTank(p0: Int): FluidStack = FluidStack.EMPTY
    override fun getTankCapacity(p0: Int): Int = 0
    override fun isFluidValid(p0: Int, p1: FluidStack): Boolean = false
    override fun fill(p0: FluidStack, p1: IFluidHandler.FluidAction): Int = 0
    override fun drain(p0: FluidStack, p1: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY
    override fun drain(p0: Int, p1: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY
}