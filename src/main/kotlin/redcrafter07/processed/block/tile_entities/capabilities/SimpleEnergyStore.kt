package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import kotlin.math.min

class SimpleEnergyStore(
    private var capacity: Int, private val maxReceive: Int, private val maxExtract: Int, private var energy: Int
) : ProcessedEnergyHandler<CompoundTag>() {
    constructor(capacity: Int) : this(capacity, capacity, capacity, 0)
    constructor(capacity: Int, maxReceive: Int, maxExtract: Int) : this(capacity, maxReceive, maxExtract, 0)

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if (!canReceive()) return 0

        val energyReceived = min(capacity - energy, min(this.maxReceive, maxReceive))
        if (!simulate) {
            energy += energyReceived
            setChanged()
        }
        return energyReceived
    }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
        if (!canExtract()) return 0

        val energyExtracted = min(energy, min(this.maxExtract, maxExtract))
        if (!simulate) {
            energy -= energyExtracted
            setChanged()
        }
        return energyExtracted
    }

    override fun getEnergyStored(): Int {
        return energy
    }

    override fun getMaxEnergyStored(): Int {
        return capacity
    }

    override fun canExtract(): Boolean {
        return maxExtract > 0
    }

    override fun canReceive(): Boolean {
        return maxReceive > 0
    }

    override fun serializeNBT(provider: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        tag.putInt("energy", energy)
        return tag
    }

    override fun deserializeNBT(provider: HolderLookup.Provider, nbt: CompoundTag) {
        energy = nbt.getInt("energy")
    }

    override fun setEnergyStored(energy: Int) {
        this.energy = energy
        setChanged()
    }

    override fun setMaxEnergyStored(maxEnergy: Int) {
        capacity = maxEnergy
        setChanged()
    }
}