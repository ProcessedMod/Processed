package redcrafter07.processed.multiblock

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import redcrafter07.processed.block.BlockProperties

abstract class RecipeMultiblockBlock(properties: Properties) : MultiblockBlock(properties) {
    override fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(BlockProperties.WORKING)
        super.addBlockStateDefinition(stateDefinition)
    }
}