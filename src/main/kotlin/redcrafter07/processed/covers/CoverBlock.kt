package redcrafter07.processed.covers

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import redcrafter07.processed.block.BlockProperties

class CoverBlock : Block(Properties.of()) {
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockProperties.FACING)
        super.createBlockStateDefinition(builder)
    }
}