package redcrafter07.processed.transmitters.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedPowerStore
import redcrafter07.processed.transmitters.TransmitterBlockEntity

class CableBlockEntity(pos: BlockPos, blockState: BlockState) :
    TransmitterBlockEntity(ModTileEntities.CABLE.get(), pos, blockState), EnergyCapableBlockEntity {
    val tier = (blockState.block as? CableBlock ?: throw IllegalStateException("CableBlockEntity for non-cable")).tier

    override fun getNetworkData(level: ServerLevel) = CableNetworkData.getOrCreate(level)

    override fun isConnectedTo(
        level: Level, direction: Direction
    ): Boolean {
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos)
        if (be is CableBlockEntity) return !be.disallowedConnections[direction.opposite]
        val cap1 = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction.opposite)
        if (cap1 != null) return true
        val cap2 = level.getCapability(ProcessedPower.BLOCK, pos, direction.opposite) ?: return false
        val energy = cap2.energy()
        if (energy.canReceive() && tier.canInsertEnergy(cap2.minTier())) return true
        if (energy.canExtract() && cap2.minTier().canInsertEnergy(tier)) return true
        return false
    }

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedPower? {
        val handler = if (side == null) EnergyHandler(this, blockPos)
        else if (connected[side.asDirectionNotRotated]) EnergyHandler(
            this, blockPos.relative(side.asDirectionNotRotated)
        )
        else return null

        return ProcessedPowerStore(tier, handler)
    }

    // Can feed up to two max power machines
    override fun getLimit() = tier.scalePower(64)

    class EnergyHandler(val cable: CableBlockEntity, val block: BlockPos) : IEnergyStorage {
        override fun receiveEnergy(amount: Int, sim: Boolean): Int {
            if (amount == 0 || cable.currentCapacity <= 0) return 0
            val lvl = cable.level ?: return 0
            if (lvl !is ServerLevel || lvl.isClientSide) return 0
            val network = cable.getOrMakeNetwork(lvl) ?: return 0

            var energyLeft = amount

            network.forEachEndpoint(true) { pos, dir ->
                val actualPos = pos.relative(dir)
                if (block != actualPos) {
                    if (energyLeft <= 0 || cable.currentCapacity <= 0) return@forEachEndpoint false

                    val cap: IEnergyStorage
                    val cap1 = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, actualPos, dir.opposite)
                    if (cap1 != null) cap = cap1
                    else {
                        val cap2 = lvl.getCapability(ProcessedPower.BLOCK, actualPos, dir.opposite)
                            ?: return@forEachEndpoint true
                        if (!cable.tier.canInsertEnergy(cap2.minTier())) return@forEachEndpoint true
                        cap = cap2.energy()
                    }

                    val targetCable =
                        (lvl.getBlockEntity(pos) as? TransmitterBlockEntity ?: return@forEachEndpoint true)
                    val limit = targetCable.remainingCapacity(cable.remainingCapacity(energyLeft))
                    try {
                        val extracted = cap.receiveEnergy(limit, sim)
                        energyLeft -= extracted
                        if (!sim) {
                            cable.useCapacity(extracted)
                            targetCable.useCapacity(extracted)
                        }
                    } catch (_: Exception) {
                    }
                }
                energyLeft > 0 && cable.currentCapacity > 0
            }

            return amount - energyLeft
        }

        override fun extractEnergy(p0: Int, p1: Boolean): Int = 0
        override fun getEnergyStored(): Int = 0
        override fun getMaxEnergyStored(): Int = 0
        override fun canExtract(): Boolean = false
        override fun canReceive(): Boolean = true
    }
}