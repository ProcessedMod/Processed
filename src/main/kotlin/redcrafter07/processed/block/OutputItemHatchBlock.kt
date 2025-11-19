package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.ItemHatch
import redcrafter07.processed.block.tile_entities.OutputItemHatchBlockEntity

class OutputItemHatchBlock : Block(Properties.of()), EntityBlock {
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(BlockStateProperties.FACING, context.clickedFace)

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        val be = level.getBlockEntity(pos)
        if (be is OutputItemHatchBlockEntity) {
            be.openMenu(player)
            return InteractionResult.sidedSuccess(level.isClientSide())
        }
        return InteractionResult.PASS
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = OutputItemHatchBlockEntity(pos, state)

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if (!newState.`is`(state.block)) {
            val be = level.getBlockEntity(pos)
            if (be is ItemHatch) be.dropContents(level, pos)
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}