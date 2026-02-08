package redcrafter07.processed.multiblock

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.state.BlockState

interface MultiblockValidator {
    /**
     * Validates that the multiblock is correct, starting at the controller. Returns null if incorrect, and all blocks that are part of it, if correct.
     */
    fun getBlocks(level: LevelAccessor, controller: BlockPos, facing: Direction): Result?
    fun displayBlocks(displayer: BlockDisplayer, regs: RegistryAccess, facing: Direction)

    fun interface BlockDisplayer {
        fun display(pos: BlockPos, blocks: List<BlockState>)
        fun display(dx: Int, dy: Int, dz: Int, blocks: List<BlockState>) = this.display(BlockPos(dx, dy, dz), blocks)
    }

    data class Result(
        val blocks: Set<BlockPos>, val importantBlocks: Map<MultiblockBlockEntity.SpecialBlockType, List<BlockPos>>
    )
}