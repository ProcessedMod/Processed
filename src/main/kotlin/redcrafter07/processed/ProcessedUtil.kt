package redcrafter07.processed

import net.minecraft.core.Direction
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