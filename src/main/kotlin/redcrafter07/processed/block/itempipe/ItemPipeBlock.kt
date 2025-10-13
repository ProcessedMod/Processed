package redcrafter07.processed.block.itempipe

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
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
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialContainer

class ItemPipeBlock(override val material: Material) : Block(Properties.of().noOcclusion()), EntityBlock,
    MaterialContainer {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = ItemPipeBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T?>? {
        if (level.isClientSide || blockEntityType != ModTileEntities.ITEM_PIPE.get()) return null
        return BlockEntityTicker { _, _, _, be ->
            if (be is ItemPipeBlockEntity) be.itemsTransferred = 0
        }
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ItemPipeBlockEntity) blockEntity.updateShape()

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
    }

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is ItemPipeBlockEntity) blockEntity.updateShape()

        super.setPlacedBy(level, pos, state, placer, stack)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val be = level.getBlockEntity(pos)
        return if (be is ItemPipeBlockEntity) shapeCache.value[be.connected.value and 0b111111] else shapeCache.value[0] // Only center block
    }

    override fun getVisualShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext

    ): VoxelShape = Shapes.empty()

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    companion object {
        // Start of the center of the pipe, determines the thickness of it.
        const val START = .35
        const val END = 1 - START

        val SHAPE_PIPE_NORTH: VoxelShape = Shapes.box(START, START, 0.0, END, END, START)
        val SHAPE_PIPE_SOUTH: VoxelShape = Shapes.box(START, START, END, END, END, 1.0)
        val SHAPE_PIPE_WEST: VoxelShape = Shapes.box(0.0, START, START, START, END, END)
        val SHAPE_PIPE_EAST: VoxelShape = Shapes.box(END, START, START, 1.0, END, END)
        val SHAPE_PIPE_UP: VoxelShape = Shapes.box(START, END, START, END, 1.0, END)
        val SHAPE_PIPE_DOWN: VoxelShape = Shapes.box(START, 0.0, START, END, START, END)

        val shapeCache = lazy { makeShapes() }

        fun makeShapes(): Array<VoxelShape> {
            val list = arrayOfNulls<VoxelShape>(64) // 2^6 different states

            for (value in 0..<64) {
                val connected = ItemPipeBlockEntity.Connected(value)
                // center
                var shape = Shapes.box(START, START, START, END, END, END)
                if (connected[Direction.NORTH]) shape = Shapes.join(shape, SHAPE_PIPE_NORTH, BooleanOp.OR)
                if (connected[Direction.SOUTH]) shape = Shapes.join(shape, SHAPE_PIPE_SOUTH, BooleanOp.OR)
                if (connected[Direction.WEST]) shape = Shapes.join(shape, SHAPE_PIPE_WEST, BooleanOp.OR)
                if (connected[Direction.EAST]) shape = Shapes.join(shape, SHAPE_PIPE_EAST, BooleanOp.OR)
                if (connected[Direction.UP]) shape = Shapes.join(shape, SHAPE_PIPE_UP, BooleanOp.OR)
                if (connected[Direction.DOWN]) shape = Shapes.join(shape, SHAPE_PIPE_DOWN, BooleanOp.OR)
                list[value] = shape
            }

            return list.requireNoNulls()
        }
    }
}