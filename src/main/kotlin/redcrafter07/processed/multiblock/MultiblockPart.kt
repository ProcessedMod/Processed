package redcrafter07.processed.multiblock

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

enum class MultiblockPart {
    Ignored, Empty, Casing, Controller;

    companion object {
        fun makeBlockMap(wall: Block, controller: Block): Map<Block, MultiblockPart> = mapOf(
            Pair(Blocks.AIR, Empty),
            Pair(wall, Casing),
            Pair(controller, Controller),
        )
    }
}