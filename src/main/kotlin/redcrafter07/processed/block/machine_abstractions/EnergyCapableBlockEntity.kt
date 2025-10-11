package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedPower

interface EnergyCapableBlockEntity {
    fun energyCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedPower?
}