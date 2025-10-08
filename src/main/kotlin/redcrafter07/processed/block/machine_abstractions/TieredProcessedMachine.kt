package redcrafter07.processed.block.machine_abstractions

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max
import kotlin.math.min

abstract class TieredProcessedMachine(type: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    ProcessedMachine(
        type, pos, blockState
    ) {
    var tier = ProcessedTier.DEFAULT
        set(value) {
            val old = field
            field = value
            onTierChanged(old, field)
        }


    public override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        tier = ProcessedTier.TIERS[min(max(nbt.getInt("machine_tier"), 0), ProcessedTier.TIERS.size - 1)]
        super.loadAdditional(nbt, provider)
    }

    public override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        nbt.putInt("machine_tier", tier.tier)
        super.saveAdditional(nbt, provider)
    }

    /**
     * Gets called when the tier gets changed.
     *
     * NOTE: this **DOES** get called on construction
     */
    protected open fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) {
    }

    protected fun useScaledEnergyCapability(capacity: Int) {
        useEnergyCapability(tier.scaledPower(capacity), tier.maxPower)
    }

    protected fun useScaledOutputEnergyCapability(capacity: Int) {
        useOutputEnergyCapability(tier.scaledPower(capacity), tier.maxPower)
    }

    protected fun useScaledPower(amps: Int): Boolean {
        return usePower(tier.powerUsageForAmps(amps))
    }
}