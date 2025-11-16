package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.LevelAccessor

interface MultiblockValidator {
    /**
     * Validates that the multiblock is correct, starting at the controller. Returns null if incorrect, and all blocks that are part of it, if correct.
     */
    fun getBlocks(level: LevelAccessor, controller: BlockPos, facing: Direction): Result?

    data class Result(val blocks: Set<BlockPos>, val importantBlocks: Map<MultiblockBlockEntity.SpecialBlockType, BlockPos>)
}