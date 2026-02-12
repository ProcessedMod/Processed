package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import redcrafter07.processed.block.cable.CableBlockEntity.Companion.Connected
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialContainer

class CableBlock(override val material: Material) : Block(Properties.of().noOcclusion()), EntityBlock,
    MaterialContainer {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CableBlockEntity(pos, state)

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is CableBlockEntity) blockEntity.updateShape()

        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val be = level.getBlockEntity(pos)
        return if (be is CableBlockEntity) shapeCache.value[be.connected.value and 0b111111] else shapeCache.value[0] // Only center block
    }

    override fun getVisualShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if (level is ServerLevel && !level.isClientSide) {
            val be = level.getBlockEntity(pos)
            if (be is CableBlockEntity) {
                val network = be.network
                if (network != null) CableNetworkData.getOrNull(level)?.remove(network)
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    companion object {
        // Start of the center of the cable, determines the thickness of it.
        const val START = .35
        const val END = 1 - START

        val SHAPE_CABLE_NORTH: VoxelShape = Shapes.box(START, START, 0.0, END, END, START)
        val SHAPE_CABLE_SOUTH: VoxelShape = Shapes.box(START, START, END, END, END, 1.0)
        val SHAPE_CABLE_WEST: VoxelShape = Shapes.box(0.0, START, START, START, END, END)
        val SHAPE_CABLE_EAST: VoxelShape = Shapes.box(END, START, START, 1.0, END, END)
        val SHAPE_CABLE_UP: VoxelShape = Shapes.box(START, END, START, END, 1.0, END)
        val SHAPE_CABLE_DOWN: VoxelShape = Shapes.box(START, 0.0, START, END, START, END)

        val shapeCache = lazy { makeShapes() }

        fun makeShapes(): Array<VoxelShape> {
            val list = arrayOfNulls<VoxelShape>(64) // 2^6 different states

            for (value in 0..<64) {
                val connected = Connected(value)
                // center
                var shape = Shapes.box(START, START, START, END, END, END)
                if (connected[Direction.NORTH]) shape = Shapes.join(shape, SHAPE_CABLE_NORTH, BooleanOp.OR)
                if (connected[Direction.SOUTH]) shape = Shapes.join(shape, SHAPE_CABLE_SOUTH, BooleanOp.OR)
                if (connected[Direction.WEST]) shape = Shapes.join(shape, SHAPE_CABLE_WEST, BooleanOp.OR)
                if (connected[Direction.EAST]) shape = Shapes.join(shape, SHAPE_CABLE_EAST, BooleanOp.OR)
                if (connected[Direction.UP]) shape = Shapes.join(shape, SHAPE_CABLE_UP, BooleanOp.OR)
                if (connected[Direction.DOWN]) shape = Shapes.join(shape, SHAPE_CABLE_DOWN, BooleanOp.OR)
                list[value] = shape
            }

            return list.requireNoNulls()
        }
    }
}