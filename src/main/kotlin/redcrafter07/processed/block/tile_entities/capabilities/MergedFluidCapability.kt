package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

class MergedFluidCapability(val handlers: List<IFluidHandler>) : IFluidHandler {
    fun handlerForTank(tank: Int): Pair<Int, IFluidHandler>? {
        var tank = tank
        for (handler in handlers) {
            if (tank < handler.tanks) return Pair(tank, handler)
            tank -= handler.tanks
        }
        return null
    }

    override fun getTanks(): Int = handlers.sumOf { it.tanks }

    override fun getFluidInTank(tank: Int): FluidStack {
        val tankAndHandler = handlerForTank(tank) ?: return FluidStack.EMPTY
        return tankAndHandler.second.getFluidInTank(tankAndHandler.first)
    }

    override fun getTankCapacity(tank: Int): Int {
        val tankAndHandler = handlerForTank(tank) ?: return 0
        return tankAndHandler.second.getTankCapacity(tankAndHandler.first)
    }

    override fun isFluidValid(tank: Int, fluid: FluidStack): Boolean {
        val tankAndHandler = handlerForTank(tank) ?: return false
        return tankAndHandler.second.isFluidValid(tankAndHandler.first, fluid)
    }

    override fun fill(
        fluid: FluidStack, action: IFluidHandler.FluidAction
    ): Int = 0

    override fun drain(
        p0: FluidStack, p1: IFluidHandler.FluidAction
    ): FluidStack = FluidStack.EMPTY

    override fun drain(
        tank: Int, action: IFluidHandler.FluidAction
    ): FluidStack = FluidStack.EMPTY
}