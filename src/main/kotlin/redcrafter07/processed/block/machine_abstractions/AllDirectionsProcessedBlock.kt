package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

abstract class AllDirectionsProcessedBlock(properties: Properties) : ProcessedBlock(properties) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        var blockState: BlockState? = this.getBlockState(context)
        if (blockState == null) blockState = defaultBlockState()
        return blockState.setValue(STATE_FACING, context.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(STATE_HORIZONTAL_FACING)
        addBlockStateDefinition(builder)
    }
}