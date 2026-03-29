package redcrafter07.processed.block

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.ProcessedComputation
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.tile_entities.ComputationHatchBlockEntity
import redcrafter07.processed.materials.MaterialContainer

class ComputationHatchBlock(override val tier: ProcessedTier) : Block(Properties.of()), EntityBlock, MaterialContainer,
    TieredBlock {
    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.FACING)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(BlockStateProperties.FACING, context.clickedFace)

    override fun newBlockEntity(pos: BlockPos, state: BlockState) = ComputationHatchBlockEntity(pos, state, tier)
    override val material = tier.material
    override fun getName() = Translations.computationHatchName(tier)
    override fun getDescription(tooltips: MutableList<Component>, flag: TooltipFlag) {
        tooltips.add(Translations.computationHatchTooltip(ProcessedComputation.computationForTier(tier) * 2))
    }

    override fun <T : BlockEntity> getTicker(
        level: Level, state: BlockState, blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> =
        BlockEntityTicker { _, _, _, be -> if (be is ComputationHatchBlockEntity) be.tickServer() }
}