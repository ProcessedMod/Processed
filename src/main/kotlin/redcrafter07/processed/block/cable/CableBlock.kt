package redcrafter07.processed.block.cable

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
import redcrafter07.processed.materials.Material
import redcrafter07.processed.materials.MaterialContainer

class CableBlock(override val material: Material) : Block(Properties.of().noOcclusion()), EntityBlock,
    MaterialContainer {
    override fun newBlockEntity(pos: BlockPos, state: BlockState) = CableBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T?>
    ): BlockEntityTicker<T>? = if (level.isClientSide) null
    else BlockEntityTicker { _, _, _, be -> if (be is CableBlockEntity) be.serverTick() }

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

    override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is CableBlockEntity) blockEntity.updateShape()

        super.setPlacedBy(level, pos, state, placer, stack)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val be = level.getBlockEntity(pos)
        if (be is CableBlockEntity) return shapeCache.value[be.connected.value and 0b111111]
        return Shapes.empty()
    }

    override fun getVisualShape(
        state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext
    ): VoxelShape = Shapes.empty()

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
    override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true

    companion object {
        val SHAPE_CABLE_NORTH: VoxelShape = Shapes.box(.4, .4, 0.0, .6, .6, .4)
        val SHAPE_CABLE_SOUTH: VoxelShape = Shapes.box(.4, .4, .6, .6, .6, 1.0)
        val SHAPE_CABLE_WEST: VoxelShape = Shapes.box(0.0, .4, .4, .4, .6, .6)
        val SHAPE_CABLE_EAST: VoxelShape = Shapes.box(.6, .4, .4, 1.0, .6, .6)
        val SHAPE_CABLE_UP: VoxelShape = Shapes.box(.4, .6, .4, .6, 1.0, .6)
        val SHAPE_CABLE_DOWN: VoxelShape = Shapes.box(.4, 0.0, .4, .6, .4, .6)

        val shapeCache = lazy { makeShapes() }

        fun makeShapes(): Array<VoxelShape> {
            val list = arrayOfNulls<VoxelShape>(64) // 2^6 different states

            for (value in 0..<64) {
                val connected = CableBlockEntity.Connected(value)
                // center
                var shape = Shapes.box(.4, .4, .4, .6, .6, .6)
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