package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

abstract class SingleDirectionalProcessedBlock(properties: Properties) : ProcessedBlock(properties) {
    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? = defaultBlockState()
    public override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) =
        addBlockStateDefinition(builder)
}