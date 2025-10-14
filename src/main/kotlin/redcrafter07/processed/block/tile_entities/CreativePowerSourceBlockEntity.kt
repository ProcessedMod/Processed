package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.block.machine_abstractions.TieredProcessedMachine
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedEnergyHandler

class CreativePowerSourceBlockEntity(pos: BlockPos, blockState: BlockState) :
    TieredProcessedMachine(ModTileEntities.CREATIVE_POWER_SOURCE.get(), pos, blockState) {
    override fun getDisplayName(): Component = Component.empty()
    override fun createMenu(
        p0: Int, p1: Inventory, p2: Player
    ): AbstractContainerMenu? = null

    init {
        useEnergyCapability(object : ProcessedEnergyHandler<CompoundTag>() {
            override fun setEnergyStored(energy: Int) {}
            override fun setMaxEnergyStored(maxEnergy: Int) {}
            override fun receiveEnergy(p0: Int, p1: Boolean) = p0
            override fun extractEnergy(p0: Int, p1: Boolean) = p0
            override fun getEnergyStored() = Int.MAX_VALUE
            override fun getMaxEnergyStored() = Int.MAX_VALUE
            override fun canExtract() = true
            override fun canReceive() = false
            override fun serializeNBT(p0: HolderLookup.Provider) = CompoundTag()
            override fun deserializeNBT(p0: HolderLookup.Provider, p1: CompoundTag) = Unit
        })
    }

    override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
        var foundCap = false
        for (direction in Direction.entries) {
            val newPos = pos.relative(direction)
            val cap: IEnergyStorage

            val cap1 = level.getCapability(Capabilities.EnergyStorage.BLOCK, newPos, direction.opposite)
            if (cap1 != null) cap = cap1
            else {
                val cap2 = level.getCapability(ProcessedPower.BLOCK, newPos, direction.opposite) ?: continue
                if (!tier.canInsertEnergy(cap2.minTier())) continue
                cap = cap2.energy()
            }
            foundCap = true
            cap.receiveEnergy(Int.MAX_VALUE, false)
        }
        if (!foundCap) stopMachine()
    }
}