package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import redcrafter07.processed.block.tile_entities.FluidTankBlockEntity

class FluidTankBlock() : Block(Properties.of().noOcclusion().sound(SoundType.STONE).pushReaction(PushReaction.DESTROY)),
    EntityBlock {
    companion object {
        val SHAPE: VoxelShape = box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0)
    }

    override fun getVisualShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape = SHAPE

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = FluidTankBlockEntity(pos, state)

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val be = level.getBlockEntity(pos)
        if (be is FluidTankBlockEntity) {
            val result= be.useItemOn(stack, player, hand)
            if (result != null) return result
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }
}