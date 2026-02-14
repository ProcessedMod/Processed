package redcrafter07.processed.block.machine_abstractions

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier

abstract class TieredProcessedMachine(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    ProcessedMachine(
        type, pos, blockState
    ) {
    override var tier = (blockState.block as? TieredProcessedBlock)?.tier ?: ProcessedTier.None
        set(value) {
            val old = field
            field = value
            onTierChanged(old, field)
        }

    /**
     * Gets called when the tier gets changed.
     *
     * NOTE: this **DOES** get called on construction
     */
    protected open fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) {
    }

    protected fun useScaledEnergyCapability(capacity: Int) {
        useEnergyCapability(tier.scalePower(capacity), tier.maxPower)
    }

    protected fun useScaledOutputEnergyCapability(capacity: Int) {
        useOutputEnergyCapability(tier.scalePower(capacity), tier.maxPower)
    }

    protected fun useScaledPower(baseEnergy: Int): Boolean {
        return usePower(tier.scalePower(baseEnergy))
    }
}