package redcrafter07.processed

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.IntArrayTag
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

val FACING_PROPERTIES = listOf(
    DirectionalBlock.FACING,
    HorizontalDirectionalBlock.FACING,
    BlockStateProperties.HORIZONTAL_FACING,
    BlockStateProperties.FACING
)


fun getFacingDirection(state: BlockState): Direction {
    for (property in FACING_PROPERTIES) if (state.hasProperty(property)) return state.getValue(property)
    return Direction.NORTH
}

fun saveBlockPositions(blocks: MutableList<BlockPos>): IntArrayTag {
    val list = ArrayList<Int>()

    for (block in blocks) {
        list.add(block.x)
        list.add(block.y)
        list.add(block.z)
    }

    return IntArrayTag(list)
}

fun loadBlockPositions(blocks: IntArray): MutableList<BlockPos> {
    val list = ArrayList<BlockPos>()

    for (i in 0..<blocks.size / 3) {
        val offset = i * 3
        list.add(BlockPos(blocks[offset], blocks[offset + 1], blocks[offset + 2]))
    }

    return list
}