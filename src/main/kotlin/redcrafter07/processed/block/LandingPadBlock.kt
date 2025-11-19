package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class LandingPadBlock : Block(Properties.ofFullCopy(Blocks.DEEPSLATE)) {
    companion object {
        val STATE: EnumProperty<State> = EnumProperty.create<State>("state", State::class.java)

        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
        val SHAPE_CENTER: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0)
    }

    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos) = 1f

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(STATE)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext) =
        if (state.getValue(STATE) == State.Center) SHAPE_CENTER else SHAPE

    private fun is3x3LandingPadArea(level: Level, center: BlockPos): Boolean {
        for (x in -1..1) {
            for (z in -1..1) {
                val state = level.getBlockState(BlockPos(center.x + x, center.y, center.z + z))
                if (!state.`is`(this)) return false
                if (state.getValue(STATE) != State.None) return false
            }
        }

        return true
    }

    private fun makeCenter(level: Level, center: BlockPos) {
        fun upd(pos: BlockPos, updater: BlockState.() -> BlockState) {
            level.setBlockAndUpdate(pos, updater(level.getBlockState(pos)))
        }
        upd(center) { setValue(STATE, State.Center) }
        upd(BlockPos(center.x + 1, center.y, center.z + 1)) { setValue(STATE, State.NorthWest) }
        upd(BlockPos(center.x + 1, center.y, center.z - 1)) { setValue(STATE, State.NorthEast) }
        upd(BlockPos(center.x - 1, center.y, center.z + 1)) { setValue(STATE, State.SouthWest) }
        upd(BlockPos(center.x - 1, center.y, center.z - 1)) { setValue(STATE, State.SouthEast) }
    }

    private fun removeCenter(level: Level, center: BlockPos, currentPos: BlockPos) {
        fun upd(pos: BlockPos, updater: BlockState.() -> BlockState) {
            if (pos == currentPos) return
            val state = level.getBlockState(pos)
            if (!state.`is`(this)) return
            level.setBlock(pos, updater(state), UPDATE_CLIENTS)
        }

        upd(center) { setValue(STATE, State.None) }
        upd(BlockPos(center.x + 1, center.y, center.z + 1)) { setValue(STATE, State.None) }
        upd(BlockPos(center.x + 1, center.y, center.z - 1)) { setValue(STATE, State.None) }
        upd(BlockPos(center.x - 1, center.y, center.z + 1)) { setValue(STATE, State.None) }
        upd(BlockPos(center.x - 1, center.y, center.z - 1)) { setValue(STATE, State.None) }
    }

    fun getCenter(level: Level, pos: BlockPos): BlockPos? {
        for (x in pos.x - 1..pos.x + 1) {
            for (z in pos.z - 1..pos.z + 1) {
                val state = level.getBlockState(BlockPos(x, pos.y, z))
                if (!state.`is`(this)) continue
                if (state.getValue(STATE) == State.Center) return BlockPos(x, pos.y, z)
            }
        }
        return null
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
        for (x in -1..1) for (z in -1..1) {
            val center = BlockPos(pos.x + x, pos.y, pos.z + z)
            if (is3x3LandingPadArea(level, center)) return makeCenter(level, center)
        }
    }

    override fun onRemove(
        state: BlockState, level: Level, pos: BlockPos, newState: BlockState, movedByPiston: Boolean
    ) {
        if(newState.`is`(this)) return

        when (state.getValue(STATE)) {
            State.Center -> removeCenter(level, pos, pos)
            else -> {
                val center = getCenter(level, pos) ?: return
                val block = level.getBlockState(center).block
                if (block is LandingPadBlock) block.removeCenter(level, center, pos)
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        (super.getStateForPlacement(context) ?: defaultBlockState()).setValue(STATE, State.None)

    enum class State(val sName: String) : StringRepresentable {
        None("none"), Center("center"), NorthWest("northwest"), NorthEast("northeast"), SouthWest("southwest"), SouthEast(
            "southeast"
        );

        override fun getSerializedName() = sName
    }
}