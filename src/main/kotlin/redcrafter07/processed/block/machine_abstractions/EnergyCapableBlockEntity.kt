package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.energy.IEnergyStorage

interface EnergyCapableBlockEntity {
    fun energyCapabilityForSide(side: BlockSide?, state: BlockState): IEnergyStorage?
}