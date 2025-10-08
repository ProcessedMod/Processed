package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler

interface FluidHandlerModifiable: IFluidHandler {
    fun setFluidInTank(tank: Int, stack: FluidStack)
}