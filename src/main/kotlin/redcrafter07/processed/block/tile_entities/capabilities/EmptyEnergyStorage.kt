package redcrafter07.processed.block.tile_entities.capabilities

object EmptyEnergyStorage : EnergyStorageModifiable {
    override fun setEnergyStored(energy: Int) = Unit
    override fun setMaxEnergyStored(maxEnergy: Int) = Unit
    override fun receiveEnergy(p0: Int, p1: Boolean): Int = 0
    override fun extractEnergy(p0: Int, p1: Boolean): Int = 0
    override fun getEnergyStored(): Int = 0
    override fun getMaxEnergyStored(): Int = 0
    override fun canExtract(): Boolean = false
    override fun canReceive(): Boolean = false
}