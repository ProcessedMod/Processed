package redcrafter07.processed.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock
import redcrafter07.processed.block.tile_entities.TieredRecipeBlockEntity

open class TieredRecipeBlock(
    properties: Properties,
    baseName: String,
    tier: ProcessedTier,
    blockEntity: BlockEntityType.BlockEntitySupplier<TieredRecipeBlockEntity>,
) : TieredProcessedBlock(
    properties, baseName, tier, { pos, state -> blockEntity.create(pos, state) }) {
    override fun addBlockStateDefinition(stateDefinition: StateDefinition.Builder<Block, BlockState>) {
        stateDefinition.add(BlockProperties.WORKING)
    }
}