package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import net.neoforged.neoforge.energy.IEnergyStorage
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.Translations
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedPowerStore
import redcrafter07.processed.materials.MaterialContainer
import redcrafter07.processed.materials.data.CableData
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.*

class CableBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModTileEntities.CABLE.get(), pos, blockState), EnergyCapableBlockEntity, WrenchInteractableBlock {

    val cableTier = lazy {
        val blk = blockState.block
        if (blk !is MaterialContainer) throw IllegalStateException("CableBlock is not a material container")
        blk.material.getExtraData(CableData::class.java)?.tier
            ?: throw IllegalStateException("material data for material ${blk.material.identifier} does not have CableeData.")
    }

    val connected = Connected()
    val disallowedConnections = Connected()
    var network: UUID? = null

    class EnergyHandler(val cable: CableBlockEntity, val block: BlockPos) : IEnergyStorage {
        override fun receiveEnergy(amount: Int, sim: Boolean): Int {
            val lvl = cable.level ?: return 0
            if (lvl !is ServerLevel || lvl.isClientSide) return 0
            if (amount == 0) return 0
            var energyLeft = amount
            if (cable.network == null) cable.scanNetwork()
            val networkId = cable.network ?: return 0

            val network = CableNetworkData.getNetwork(lvl, networkId, cable.blockPos)
            if(network == null) {
                cable.network = null
                return 0
            }

            network.forEachEndpoint(true) { pos, dir ->
                val actualPos = pos.relative(dir)
                if (block != actualPos) {
                    if (energyLeft <= 0) return@forEachEndpoint false

                    val cap: IEnergyStorage
                    val cap1 = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, actualPos, dir)
                    if (cap1 != null) cap = cap1
                    else {
                        val cap2 =
                            lvl.getCapability(ProcessedPower.BLOCK, actualPos, dir) ?: return@forEachEndpoint false
                        if (!cable.cableTier.value.canInsertEnergy(cap2.minTier())) return@forEachEndpoint false
                        cap = cap2.energy()
                    }

                    try {
                        energyLeft -= cap.receiveEnergy(energyLeft, sim)
                    } catch (_: Exception) {
                    }
                }
                energyLeft > 0
            }

            return amount - energyLeft
        }

        override fun extractEnergy(p0: Int, p1: Boolean): Int = 0
        override fun getEnergyStored(): Int = 0
        override fun getMaxEnergyStored(): Int = 0
        override fun canExtract(): Boolean = false
        override fun canReceive(): Boolean = true
    }

    fun scanNetwork(): Boolean {
        val level = level ?: return false
        if (level !is ServerLevel || level.isClientSide) return false

        val id = network
        val data = CableNetworkData.getOrMake(level)
        val network = if (id == null) data.newNetwork()
        else data.getNetwork(id, blockPos) ?: data.newNetwork()
        network.clearBlocks(level)
        this.network = network.id

        val toScan = arrayListOf(blockPos)
        while (toScan.isNotEmpty()) {
            val pos = toScan.removeFirstOrNull() ?: break
            if (network.containsBlock(pos)) continue
            val be = level.getBlockEntity(pos)
            if (be !is CableBlockEntity) continue

            network.addBlock(pos, level)
            val prev = be.network
            be.network = network.id
            if (prev != null && prev != network.id) data.remove(prev)

            for (d in Direction.entries) {
                val newPos = pos.relative(d)
                if (!be.isConnected(level, d)) continue
                be.connected[d] = true
                if (level.getBlockState(newPos).`is`(blockState.block)) {
                    toScan.add(newPos)
                    continue
                } else network.addEndpoint(pos, d, level)
            }

            level.blockEntityChanged(pos)
        }

        sync()
        return true
    }

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedPower? {
        val handler = if (side == null) EnergyHandler(this, blockPos)
        else if (connected[side.asDirectionNotRotated]) EnergyHandler(this, blockPos)
        else return null

        return ProcessedPowerStore(cableTier.value, handler)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        network?.let { tag.putString("network", it.toString()) }
        tag.putInt("connected", connected.value)
        tag.putInt("disallowedConnections", disallowedConnections.value)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        connected.value = tag.getInt("connected")
        disallowedConnections.value = tag.getInt("disallowedConnections")
        network = if (tag.contains("network", Tag.TAG_STRING.toInt())) try {
            UUID.fromString(tag.getString("network"))
        } catch (_: IllegalArgumentException) {
            null
        }
        else null
    }

    fun updateShape() {
        val level = level ?: return
        if (level.isClientSide || level !is ServerLevel) return
        for (direction in Direction.entries) connected[direction] = isConnected(level, direction)
        scanNetwork()
    }

    fun sync() {
        val level = level
        if (level is ServerLevel) {
            val state = blockState
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_CLIENTS)
            setChanged()
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
            level.sendBlockUpdated(this.worldPosition, blockState, blockState, Block.UPDATE_ALL)
            requestModelDataUpdate()
        }
    }

    fun isConnected(level: Level, direction: Direction): Boolean {
        if (disallowedConnections[direction]) return false
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos)
        if (be is CableBlockEntity) return !be.disallowedConnections[direction.opposite]
        val cap1 = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction.opposite)
        if (cap1 != null) return true
        val cap2 = level.getCapability(ProcessedPower.BLOCK, pos, direction.opposite)
        return cap2 != null && cableTier.value.canInsertEnergy(cap2.minTier())
    }

    override fun getModelData(): ModelData = ModelData.builder().with(TRANSMITTER_PROPERTY, connected).build()

    override fun onWrenchUse(ctx: UseOnContext, state: BlockState) {
        val level = level ?: return
        if (level.isClientSide) return
        val clickOffset = ctx.clickLocation - ctx.clickedPos.toVec3()
        val direction = actualDirection(clickOffset.x, clickOffset.y, clickOffset.z, ctx.clickedFace)
        disallowedConnections[direction] = !disallowedConnections[direction]
        updateShape()
        level.updateNeighborsAt(blockPos, blockState.block)

        val player = ctx.player ?: return
        if (player !is ServerPlayer) return
        val state = if (disallowedConnections[direction]) Translations.pipeLikeStateSplit() else {
            if (connected[direction]) Translations.pipeLikeStateConnected()
            else Translations.pipeLikeStateDisconnected()
        }
        player.displayClientMessage(Translations.pipeLikeState(state), true)
    }

    companion object {
        fun actualDirection(originalX: Double, originalY: Double, originalZ: Double, direction: Direction): Direction {
            var x = originalX
            var y = originalY
            var z = originalZ
            when (direction) {
                Direction.WEST -> x = 0.5
                Direction.EAST -> x = 0.5
                Direction.DOWN -> y = 0.5
                Direction.UP -> y = 0.5
                Direction.NORTH -> z = 0.5
                Direction.SOUTH -> z = 0.5
            }
            if (x < CableBlock.START) return Direction.WEST
            if (x >= CableBlock.END) return Direction.EAST
            if (y < CableBlock.START) return Direction.DOWN
            if (y >= CableBlock.END) return Direction.UP
            if (z < CableBlock.START) return Direction.NORTH
            if (z >= CableBlock.END) return Direction.SOUTH
            return direction
        }

        val TRANSMITTER_PROPERTY = ModelProperty<Connected>()

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
}