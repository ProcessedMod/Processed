package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

class HeatVentBlock : Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
    init {
        registerDefaultState(defaultBlockState().setValue(BlockProperties.EMITTING_HEAT, false))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockProperties.EMITTING_HEAT)
        super.createBlockStateDefinition(builder)
    }

    override fun getLightEmission(state: BlockState, level: BlockGetter, pos: BlockPos) =
        if (state.getValue(BlockProperties.EMITTING_HEAT)) 14
        else 0
}