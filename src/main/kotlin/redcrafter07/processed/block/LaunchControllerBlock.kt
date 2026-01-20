package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.multiblock.MultiblockBlock

class LaunchControllerBlock : MultiblockBlock(Properties.of().pushReaction(PushReaction.BLOCK)) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = LaunchControllerBlockEntity(pos, state)

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val be = level.getBlockEntity(pos)
        if (be is LaunchControllerBlockEntity) {
            player.openMenu(be) { data -> data.writeBlockPos(pos) }
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }
}
