package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.tile_entities.PoweredFurnaceBlockEntity

class PoweredFurnaceBlock(tier: ProcessedTier) : TieredRecipeBlock(
    Properties.of().sound(SoundType.STONE), "block.processed.powered_furnace", tier, ::PoweredFurnaceBlockEntity
) {
    override fun useWithoutItem(
        state: BlockState, level: Level, pos: BlockPos, player: Player, hitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val be = level.getBlockEntity(pos)
        if (be is PoweredFurnaceBlockEntity) {
            player.openMenu(be) { data -> data.writeBlockPos(pos) }
            return InteractionResult.CONSUME
        }
        return InteractionResult.PASS
    }

    override fun getOcclusionShape(state: BlockState, level: BlockGetter, pos: BlockPos): VoxelShape = Shapes.empty()
    override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
}