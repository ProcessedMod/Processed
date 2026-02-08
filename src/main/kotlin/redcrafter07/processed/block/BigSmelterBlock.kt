package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import redcrafter07.processed.block.tile_entities.BigSmelterBlockEntity
import redcrafter07.processed.multiblock.RecipeMultiblockBlock

class BigSmelterBlock : RecipeMultiblockBlock(Properties.of().pushReaction(PushReaction.BLOCK)) {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BigSmelterBlockEntity(pos, state)
}
