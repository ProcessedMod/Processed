package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction

class SimpleOutputFluidStore(tanks: NonNullList<FluidStack>, capacity: Int) : SimpleFluidStore(tanks, capacity) {
    constructor(tanks: Int, capacity: Int) : this(NonNullList.withSize(tanks, FluidStack.EMPTY), capacity)

    override fun fill(stack: FluidStack, action: FluidAction): Int = stack.amount
    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = false
}