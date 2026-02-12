package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.tile_entities.PurifierBlockEntity

class PurifierBlock(tier: ProcessedTier) : TieredRecipeBlock(
    Properties.of().sound(SoundType.STONE), "block.processed.purifier", tier, ::PurifierBlockEntity
) {
    override fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(BlockProperties.WORKING)
    }

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val be = level.getBlockEntity(pos)
        if (be is PurifierBlockEntity) {
            player.openMenu(be) { data -> data.writeBlockPos(pos) }
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }
}