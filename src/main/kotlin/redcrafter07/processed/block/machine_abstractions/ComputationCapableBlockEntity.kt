package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedComputation

interface ComputationCapableBlockEntity {
    fun computationCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedComputation?
}