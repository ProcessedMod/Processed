package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock

abstract class MultiblockBlock(properties: Properties) : ProcessedBlock(properties) {
    public override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if(newState.`is`(state.block)) return
        MultiblockPreview.removeIfLastDisplayed(pos)
        val be = level.getBlockEntity(pos)
        if (be is MultiblockBlockEntity) be.onRemove(level)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (player.isShiftKeyDown) {
            val be = level.getBlockEntity(pos)
            if (be is MultiblockBlockEntity) {
                if (level.isClientSide) MultiblockPreview.update(be)
                return InteractionResult.sidedSuccess(level.isClientSide)
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult)
    }
}
