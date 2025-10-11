package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock
import redcrafter07.processed.block.tile_entities.CreativePowerSourceBlockEntity

class CreativePowerSourceBlock(tier: ProcessedTier) :
    TieredProcessedBlock(Properties.of(), "block.creative_power_source", tier, ::CreativePowerSourceBlockEntity) {
    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        neighborBlock: Block,
        neighborPos: BlockPos,
        movedByPiston: Boolean
    ) {
        val be = level.getBlockEntity(pos)
        if (be is CreativePowerSourceBlockEntity) be.startMachine()
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston)
    }
}