package redcrafter07.processed.transmitters

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
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.data.ModelProperty
import redcrafter07.processed.Translations
import redcrafter07.processed.block.WrenchInteractableBlock
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.*

abstract class TransmitterBlockEntity(typ: BlockEntityType<*>, pos: BlockPos, blockState: BlockState) :
    BlockEntity(typ, pos, blockState), WrenchInteractableBlock {

    val connected = Connected()
    val disallowedConnections = Connected()
    var network: UUID? = null

    protected abstract fun getNetworkData(level: ServerLevel): TransmitterNetworkData<*>

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
        network = if (tag.contains("network")) try {
            tag.getUUID("network")
        } catch (_: IllegalArgumentException) {
            null
        } else null
    }

    fun reloadNetwork(level: Level?) {
        val level = level ?: this.level ?: return
        if (level.isClientSide || level !is ServerLevel) return
        getNetworkData(level).invalidate(blockPos, network)
    }

    fun updateVisual(level: Level) {
        val prev = connected.value
        connected.value = 0
        for (d in Direction.entries) if(isConnected(level, d)) connected[d] = true
        if(connected.value == prev) return

        level.blockEntityChanged(blockPos)
        sync()
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

    protected abstract fun isConnectedTo(level: Level, direction: Direction): Boolean

    fun isConnected(level: Level, direction: Direction): Boolean {
        if (disallowedConnections[direction]) return false
        return isConnectedTo(level, direction)
    }

    override fun getModelData(): ModelData = ModelData.builder().with(TRANSMITTER_PROPERTY, connected).build()

    override fun onWrenchUse(ctx: UseOnContext, state: BlockState) {
        val level = level ?: return
        if (level.isClientSide) return
        val clickOffset = ctx.clickLocation - ctx.clickedPos.toVec3()
        val direction = actualDirection(clickOffset.x, clickOffset.y, clickOffset.z, ctx.clickedFace)
        disallowedConnections[direction] = !disallowedConnections[direction]
        reloadNetwork(level)
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
            if (x < TransmitterBlock.START) return Direction.WEST
            if (x >= TransmitterBlock.END) return Direction.EAST
            if (y < TransmitterBlock.START) return Direction.DOWN
            if (y >= TransmitterBlock.END) return Direction.UP
            if (z < TransmitterBlock.START) return Direction.NORTH
            if (z >= TransmitterBlock.END) return Direction.SOUTH
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