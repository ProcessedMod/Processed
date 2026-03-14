package redcrafter07.processed.block

import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty

object BlockProperties {
    val WORKING: BooleanProperty = BooleanProperty.create("working")
    val FACING: DirectionProperty = BlockStateProperties.FACING
    val HORIZONTAL_FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    val EMITTING_HEAT: BooleanProperty = BooleanProperty.create("emitting_heat")
}