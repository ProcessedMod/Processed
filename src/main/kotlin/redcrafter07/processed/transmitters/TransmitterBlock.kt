package redcrafter07.processed.transmitters

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.PushReaction
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import redcrafter07.processed.transmitters.TransmitterBlockEntity.Companion.Connected

abstract class TransmitterBlock(properties: Properties) :
    Block(properties.noOcclusion().pushReaction(PushReaction.BLOCK)), EntityBlock {
    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        val be = level.getBlockEntity(pos)
        if (!level.isClientSide && level is ServerLevel && be is TransmitterBlockEntity) {
            be.reloadNetwork(level)
            be.updateVisual(level)
        }

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = if (!blockEntityType.isValid(state)) null
    else if (level.isClientSide || level !is ServerLevel) null
    else BlockEntityTicker { _, _, _, entity -> (entity as? TransmitterBlockEntity)?.tickServer() }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val be = level.getBlockEntity(pos)
        return if (be is TransmitterBlockEntity) shapeCache.value[be.connected.value and 0b111111] else shapeCache.value[0] // Only center block
    }

    override fun getVisualShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if (level is ServerLevel && !level.isClientSide) {
            val be = level.getBlockEntity(pos)
            if (be is TransmitterBlockEntity) be.reloadNetwork(level)
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        for (d in Direction.entries) if (level.getBlockState(pos.relative(d)).`is`(this)) return
        val be = level.getBlockEntity(pos)
        if (be is TransmitterBlockEntity) be.reloadNetwork(level)
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        if (placer != null) {
            val be = level.getBlockEntity(pos)
            if (be is TransmitterBlockEntity) be.updateVisual(level)
        }


        super.setPlacedBy(level, pos, state, placer, stack)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    companion object {
        // Start of the center of the transmitter, determines the thickness of it.
        const val START = .35
        const val END = 1 - START

        val SHAPE_NORTH: VoxelShape = Shapes.box(START, START, 0.0, END, END, START)
        val SHAPE_SOUTH: VoxelShape = Shapes.box(START, START, END, END, END, 1.0)
        val SHAPE_WEST: VoxelShape = Shapes.box(0.0, START, START, START, END, END)
        val SHAPE_EAST: VoxelShape = Shapes.box(END, START, START, 1.0, END, END)
        val SHAPE_UP: VoxelShape = Shapes.box(START, END, START, END, 1.0, END)
        val SHAPE_DOWN: VoxelShape = Shapes.box(START, 0.0, START, END, START, END)

        val shapeCache = lazy { makeShapes() }

        fun makeShapes(): Array<VoxelShape> {
            val list = arrayOfNulls<VoxelShape>(64) // 2^6 different states

            for (value in 0..<64) {
                val connected = Connected(value)
                // center
                var shape = Shapes.box(START, START, START, END, END, END)
                if (connected[Direction.NORTH]) shape = Shapes.join(shape, SHAPE_NORTH, BooleanOp.OR)
                if (connected[Direction.SOUTH]) shape = Shapes.join(shape, SHAPE_SOUTH, BooleanOp.OR)
                if (connected[Direction.WEST]) shape = Shapes.join(shape, SHAPE_WEST, BooleanOp.OR)
                if (connected[Direction.EAST]) shape = Shapes.join(shape, SHAPE_EAST, BooleanOp.OR)
                if (connected[Direction.UP]) shape = Shapes.join(shape, SHAPE_UP, BooleanOp.OR)
                if (connected[Direction.DOWN]) shape = Shapes.join(shape, SHAPE_DOWN, BooleanOp.OR)
                list[value] = shape
            }

            return list.requireNoNulls()
        }
    }
}