package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.energy.IEnergyStorage
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.ProcessedTier

class ProcessedPowerStore<T : IEnergyStorage>(val tier: ProcessedTier, val energyStore: T) : ProcessedPower {
    override fun minTier(): ProcessedTier = tier
    override fun energy(): IEnergyStorage = energyStore
}