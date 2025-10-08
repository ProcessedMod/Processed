package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.machine_abstractions.ProcessedBlock

abstract class MultiblockBlock(properties: Properties) : ProcessedBlock(properties) {
    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (neighborState.block !is CasingBlock && pos != neighborPos) {
            val be = level.getBlockEntity(pos)
            if (be is MultiblockBlockEntity) be.recheck()
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    public override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        val be = level.getBlockEntity(pos)
        if (be is MultiblockBlockEntity) be.onRemove(level)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}
