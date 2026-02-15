package redcrafter07.processed.transmitters.transporters

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.materials.MaterialContainer
import redcrafter07.processed.materials.data.MaterialBase
import redcrafter07.processed.transmitters.TransmitterBlock

class TransporterBlock(override val material: MaterialBase, val speed: Int) : TransmitterBlock(Properties.of()),
    MaterialContainer {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = TransporterBlockEntity(pos, state)
}