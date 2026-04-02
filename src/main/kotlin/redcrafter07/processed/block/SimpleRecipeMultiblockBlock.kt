package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import redcrafter07.processed.multiblock.AbstractRecipeMultiBlockEntity
import redcrafter07.processed.multiblock.RecipeMultiblockBlock

class SimpleRecipeMultiblockBlock(
    val beConstructor: (BlockPos, BlockState) -> AbstractRecipeMultiBlockEntity,
    properties: Properties = Properties.of()
) : RecipeMultiblockBlock(properties.pushReaction(PushReaction.BLOCK)) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = beConstructor(pos, state)
}