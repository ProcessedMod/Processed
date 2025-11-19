package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.block.tile_entities.EnergyHatchBlockEntity

class EnergyHatchBlock : Block(Properties.of()), EntityBlock {
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(BlockStateProperties.FACING, context.clickedFace)

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = EnergyHatchBlockEntity(pos, state)
}