package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.fluids.capability.IFluidHandler

interface FluidCapableBlockEntity {
    fun fluidCapabilityForSide(side: BlockSide?, state: BlockState): IFluidHandler?
}