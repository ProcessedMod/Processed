package redcrafter07.processed

import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.BlockProperties

val FACING_PROPERTIES = listOf(
    BlockProperties.FACING,
    BlockProperties.HORIZONTAL_FACING,
    DirectionalBlock.FACING,
    HorizontalDirectionalBlock.FACING,
)

fun <T> nonNullListOf(vararg values: T): NonNullList<T> {
    if(values.isEmpty()) return NonNullList.create()
    val list = NonNullList.createWithCapacity<T>(values.size)
    list.addAll(values)
    return list
}

fun getFacingDirection(state: BlockState): Direction {
    for (property in FACING_PROPERTIES) if (state.hasProperty(property)) return state.getValue(property)
    return Direction.NORTH
}