package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock
import redcrafter07.processed.block.tile_entities.ComputerBlockEntity

class ComputerBlock : ProcessedBlock(Properties.of()) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = ComputerBlockEntity(pos, state)
}