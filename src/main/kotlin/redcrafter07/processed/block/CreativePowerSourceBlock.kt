package redcrafter07.processed.block

import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.RotationType
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock
import redcrafter07.processed.block.tile_entities.CreativePowerSourceBlockEntity

class CreativePowerSourceBlock(tier: ProcessedTier) :
    TieredProcessedBlock(Properties.of(), "block.processed.creative_power_source", tier, ::CreativePowerSourceBlockEntity) {
    override fun rotationType() = RotationType.NonRotatable
}