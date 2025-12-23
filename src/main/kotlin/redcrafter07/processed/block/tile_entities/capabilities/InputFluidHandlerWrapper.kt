package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class InputFluidHandlerWrapper(val handler: IFluidHandler) : IFluidHandler {
    override fun getTanks(): Int = handler.tanks
    override fun getFluidInTank(tank: Int): FluidStack = handler.getFluidInTank(tank)
    override fun getTankCapacity(tank: Int): Int = handler.getTankCapacity(tank)
    override fun isFluidValid(tank: Int, fluid: FluidStack): Boolean = handler.isFluidValid(tank, fluid)
    override fun fill(fluid: FluidStack, action: IFluidHandler.FluidAction): Int = handler.fill(fluid, action)
    override fun drain(fluid: FluidStack, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY
    override fun drain(tank: Int, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY
}