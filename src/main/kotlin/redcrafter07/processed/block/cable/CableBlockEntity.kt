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
import redcrafter07.processed.ProcessedTier
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
import java.util.function.BiFunction
import java.util.function.Consumer

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

    val energyHandler = object : IEnergyStorage {
        override fun receiveEnergy(amount: Int, sim: Boolean): Int {
            val lvl = level ?: return 0
            var energyLeft = amount

            for (entry in outputs.entries) {
                if (energyLeft <= 0) return amount
                val cap: IEnergyStorage
                val cap1 = lvl.getCapability(Capabilities.EnergyStorage.BLOCK, entry.key, entry.value.second)
                if (cap1 != null) cap = cap1
                else {
                    val cap2 = lvl.getCapability(ProcessedPower.BLOCK, entry.key, entry.value.second) ?: continue
                    if (!cableTier.value.canInsertEnergy(cap2.minTier())) continue
                    cap = cap2.energy()
                }

                try {
                    energyLeft -= cap.receiveEnergy(energyLeft, sim)
                } catch (_: Exception) {
                }
            }

            return amount - energyLeft
        }

        override fun extractEnergy(p0: Int, p1: Boolean): Int = 0
        override fun getEnergyStored(): Int = 0
        override fun getMaxEnergyStored(): Int = 0
        override fun canExtract(): Boolean = false
        override fun canReceive(): Boolean = true
    }
    val energyCapability = ProcessedPowerStore(cableTier.value, energyHandler)

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState) =
        if (side == null) energyCapability else if (connected[side.asDirectionNotRotated]) energyCapability else null

    private var outputCacheInner: Map<BlockPos, Pair<ProcessedTier, Direction>>? = null
    val outputs: Map<BlockPos, Pair<ProcessedTier, Direction>>
        get() {
            val outputCache = outputCacheInner
            if (outputCache != null) return outputCache
            val outputs = HashMap<BlockPos, Pair<ProcessedTier, Direction>>()

            val level =
                level ?: throw IllegalStateException("tried to update the output cache while not having a level")

            traverse(worldPosition, cableTier.value) { cable, transferTier ->
                val tier = transferTier.min(cable.cableTier.value)

                for (direction in Direction.entries) {
                    val pos = cable.blockPos.relative(direction)
                    if (!cable.connected[direction]) continue

                    val be = level.getBlockEntity(pos)
                    if (be != null && be is CableBlockEntity) continue
                    var cap = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos, direction.opposite)
                    if (cap == null) {
                        val cap1 = level.getCapability(ProcessedPower.BLOCK, pos, direction.opposite) ?: continue
                        if (!tier.canInsertEnergy(cap1.minTier())) continue
                        cap = cap1.energy()
                    }
                    if (!cap.canReceive()) continue
                    outputs.compute(pos) { _, value ->
                        Pair(value?.first ?: tier, direction.opposite)
                    }
                }

                tier
            }
            outputCacheInner = outputs
            return outputs
        }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("connected", connected.value)
        tag.putInt("disallowedConnections", disallowedConnections.value)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        connected.value = tag.getInt("connected")
        disallowedConnections.value = tag.getInt("disallowedConnections")
    }

    fun updateShape() {
        val level = level ?: return
        if (level.isClientSide || level !is ServerLevel) return
        for (direction in Direction.entries) connected[direction] = isConnected(level, direction)
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
        val set = hashSetOf(pos)
        val data = f.apply(this, data)
        val level = level ?: return
        traverse(pos, f, set, level, data, this)
    }

    fun <T> traverse(
        pos: BlockPos,
        f: BiFunction<CableBlockEntity, T, T>,
        set: MutableSet<BlockPos>,
        level: Level,
        data: T,
        be: CableBlockEntity
    ) {
        for (direction in Direction.entries) {
            val newPos = pos.relative(direction)
            if (set.contains(newPos) || !be.connected[direction]) continue
            set.add(newPos)
            val blockEntity = level.getBlockEntity(newPos)
            if (blockEntity is CableBlockEntity) {
                val data = f.apply(blockEntity, data)
                traverse(newPos, f, set, level, data, blockEntity)
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
            level.sendBlockUpdated(this.worldPosition, blockState, blockState, Block.UPDATE_ALL)
            requestModelDataUpdate()
        }
    }

    fun isConnected(level: Level, direction: Direction): Boolean {
        if (disallowedConnections[direction]) return false
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos) ?: return false
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
        player.sendSystemMessage(Translations.pipeLikeState(state))
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