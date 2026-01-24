package redcrafter07.processed.block.machine_abstractions

import net.minecraft.core.BlockPos
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.block.tile_entities.capabilities.SimpleDroppingContainer
import redcrafter07.processed.items.ModItems

abstract class ProcessedBlock(properties: Properties) : Block(properties.pushReaction(PushReaction.BLOCK)),
    EntityBlock {
    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        if (stack.`is`(ModItems.WRENCH)) return ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

    open fun rotationType() = RotationType.RotatableHorizontal

    protected open fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {}

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        when (rotationType()) {
            RotationType.RotatableHorizontal -> builder.add(BlockProperties.HORIZONTAL_FACING)
            RotationType.Rotatable -> builder.add(BlockProperties.FACING)
            RotationType.NonRotatable -> Unit
        }
        addBlockStateDefinition(builder)
    }

    protected open fun getBlockState(context: BlockPlaceContext): BlockState? = null

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        var state = getBlockState(context)
        if (state == null) state = defaultBlockState()
        return when (rotationType()) {
            RotationType.RotatableHorizontal -> state.setValue(
                BlockProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite
            )

            RotationType.Rotatable -> state.setValue(BlockProperties.FACING, context.clickedFace)
            RotationType.NonRotatable -> state
        }
    }

    override fun <T : BlockEntity> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = BlockEntityTicker { lv, pos, tickState, ticker ->
        if (ticker is ProcessedMachine) ticker.handleTick(lv, pos, tickState)
    }


    open fun addDrops(drops: SimpleDroppingContainer, blockEntity: BlockEntity?) {
    }

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if (state.`is`(newState.block)) return super.onRemove(state, level, pos, newState, movedByPiston)
        val be = level.getBlockEntity(pos)
        if (be is ProcessedMachine) be.dropItems()
        val container = SimpleDroppingContainer()
        addDrops(container, be)
        if (container.containerSize > 0) Containers.dropContents(level, pos, container)


        super.onRemove(state, level, pos, newState, movedByPiston)
    }
}