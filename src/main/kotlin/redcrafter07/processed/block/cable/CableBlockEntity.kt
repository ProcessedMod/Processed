package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import net.neoforged.neoforge.energy.IEnergyStorage
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.materials.MaterialContainer
import java.util.function.BiFunction
import java.util.function.Consumer

class CableBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModTileEntities.CABLE.get(), pos, blockState), EnergyCapableBlockEntity {

    val connected = Connected()
    val disallowedConnections = Connected()

    val capacity: Int get() = 10 * pipeTransferSpeed
    var storedEnergy = 0

    val energyHandler = object : IEnergyStorage {
        override fun receiveEnergy(amount: Int, sim: Boolean): Int {
            val capacity = capacity
            if (storedEnergy >= capacity) return amount
            if (!sim) {
                setChanged()
                storedEnergy = minOf(storedEnergy + amount, capacity)
            }
            val maxStore = capacity - storedEnergy
            return if (amount > maxStore) amount - maxStore else 0
        }

        override fun extractEnergy(p0: Int, p1: Boolean): Int = 0
        override fun getEnergyStored(): Int = storedEnergy
        override fun getMaxEnergyStored(): Int = capacity
        override fun canExtract(): Boolean = false
        override fun canReceive(): Boolean = true
    }

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState): IEnergyStorage = energyHandler

    private var outputCacheInner: Map<BlockPos, Int>? = null
    val outputs: Map<BlockPos, Int>
        get() {
            val outputCache = outputCacheInner
            if (outputCache != null) return outputCache
            val outputs = HashMap<BlockPos, Int>()

            traverse(worldPosition, pipeTransferSpeed) { pipe, transferSpeed ->
                val transferSpeed = minOf(transferSpeed, pipe.pipeTransferSpeed)

                for (direction in Direction.entries) {
                    val pos = pipe.blockPos.relative(direction)
                    val be = level!!.getBlockEntity(pos)
                    if (be != null && be is CableBlockEntity) return@traverse transferSpeed
                    val cap = level!!.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction.opposite)
                    if (cap != null && cap.canReceive()) outputs.compute(pos) { _, value ->
                        maxOf(
                            value ?: 0,
                            transferSpeed
                        )
                    }
                }

                transferSpeed
            }

            outputCacheInner = outputs
            return outputs
        }
    val pipeTransferSpeed: Int
        get() {
            val blk = blockState.block
            if (blk !is MaterialContainer) return -1
            return blk.material.getExtraData(CableData::class.java)?.transferRate ?: -1
        }

    fun serverTick() {
        if (storedEnergy <= 0) return
        val lvl = level ?: return
        val outputs = outputs
        val maxExtractableEnergy = minOf(pipeTransferSpeed, storedEnergy)
        var energyLeft = maxExtractableEnergy

        for (entry in outputs.entries) {
            if (energyLeft <= 0) break
            val cap = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, entry.key, null) ?: continue
            val insertEnergyAmount = minOf(energyLeft, entry.value)
            try {
                val leftovers = cap.receiveEnergy(insertEnergyAmount, false)
                energyLeft = energyLeft - insertEnergyAmount + leftovers
            } catch (_: Exception) {
            }
        }
        storedEnergy = storedEnergy - maxExtractableEnergy + energyLeft
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("stored_energy", storedEnergy)
        tag.putInt("connected", connected.value)
        tag.putInt("disallowedConnections", disallowedConnections.value)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        storedEnergy = tag.getInt("stored_energy")
        connected.value = tag.getInt("connected")
        disallowedConnections.value = tag.getInt("disallowedConnections")
    }

    fun updateShape() {
        val level = level ?: return
        if (level.isClientSide || level !is ServerLevel) return
        for (direction in Direction.entries) connected[direction] = isConnected(level, direction)
        ProcessedMod.LOG.info("Updating shape @ {}", blockPos)
        sync()
        traverse(worldPosition) { it.outputCacheInner = null }
    }

    fun sync() {
        val level = level
        if (level is ServerLevel) {
            val state = blockState
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS)
            setChanged()
        }
    }

    fun traverse(pos: BlockPos, f: Consumer<CableBlockEntity>) = traverse(pos, Unit) { model, _ -> f.accept(model) }

    fun <T> traverse(pos: BlockPos, data: T, f: BiFunction<CableBlockEntity, T, T>) {
        val set = HashSet<BlockPos>()
        set.add(pos)
        val data = f.apply(this, data)
        val level = level ?: return
        traverse(pos, f, set, level, data)
    }

    fun <T> traverse(
        pos: BlockPos, f: BiFunction<CableBlockEntity, T, T>, set: MutableSet<BlockPos>, level: Level, data: T
    ) {
        for (direction in Direction.entries) {
            val newPos = pos.relative(direction)
            if (set.contains(newPos) || !connected[direction]) continue
            set.add(newPos)
            val blockEntity = level.getBlockEntity(newPos)
            if (blockEntity is CableBlockEntity) {
                val data = f.apply(blockEntity, data)
                traverse(newPos, f, set, level, data)
            }
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? = ClientboundBlockEntityDataPacket.create(this)
    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

    override fun onDataPacket(
        net: Connection, pkt: ClientboundBlockEntityDataPacket, lookupProvider: HolderLookup.Provider
    ) {
        super.onDataPacket(net, pkt, lookupProvider)

        val level = level ?: return
        if (level.isClientSide) {
            ProcessedMod.LOG.info("Updating client @ {}", blockPos)
            level.sendBlockUpdated(this.worldPosition, blockState, blockState, Block.UPDATE_ALL)
            requestModelDataUpdate()
        }
    }

    fun isConnected(level: Level, direction: Direction): Boolean {
        if (disallowedConnections[direction]) return false
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos) ?: return false
        if (be is CableBlockEntity) return true
        val cap1 = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null)
        return cap1 != null
    }

    override fun getModelData(): ModelData = ModelData.builder().with(TRANSMITTER_PROPERTY, connected).build()

    companion object {
        val TRANSMITTER_PROPERTY = ModelProperty<Connected>()
    }

    class Connected(var value: Int) {
        constructor() : this(0)

        fun setSide(side: Direction) {
            this.value = this.value or 1.shl(side.get3DDataValue())
        }

        fun clearSide(side: Direction) {
            this.value = this.value and 1.shl(side.get3DDataValue()).inv()
        }

        operator fun set(side: Direction, value: Boolean) {
            if (value) setSide(side)
            else clearSide(side)
        }

        operator fun get(side: Direction): Boolean = (this.value and 1.shl(side.get3DDataValue())) > 0
    }
}