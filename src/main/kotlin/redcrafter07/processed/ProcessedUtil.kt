package redcrafter07.processed

import net.minecraft.core.Direction
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


fun getFacingDirection(state: BlockState): Direction {
    for (property in FACING_PROPERTIES) if (state.hasProperty(property)) return state.getValue(property)
    return Direction.NORTH
}