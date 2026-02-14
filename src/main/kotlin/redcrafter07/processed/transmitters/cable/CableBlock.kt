package redcrafter07.processed.transmitters.cable

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.MaterialContainer
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.transmitters.TransmitterBlock

class CableBlock(override val material: MaterialBase, val tier: ProcessedTier) : TransmitterBlock(Properties.of()),
    MaterialContainer {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CableBlockEntity(pos, state)
}