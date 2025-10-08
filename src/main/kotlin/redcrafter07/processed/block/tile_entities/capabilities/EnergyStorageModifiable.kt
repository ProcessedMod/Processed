package redcrafter07.processed.block.tile_entities.capabilities

import net.neoforged.neoforge.energy.IEnergyStorage

interface EnergyStorageModifiable: IEnergyStorage {
    fun setEnergyStored(energy: Int)
    fun setMaxEnergyStored(maxEnergy: Int)
}