package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

class PoweredFurnaceBlock(tier: ProcessedTier) : TieredProcessedBlock(
    Properties.of().sound(SoundType.STONE), "block.processed.powered_furnace", tier, ::PoweredFurnaceBlockEntity
) {
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val be = level.getBlockEntity(pos)
        if (be is PoweredFurnaceBlockEntity) {
            player.openMenu(be) { data -> data.writeBlockPos(pos) }
            return InteractionResult.sidedSuccess(level.isClientSide())
        }
        return InteractionResult.PASS
    }
}