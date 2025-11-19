package redcrafter07.processed.block.tile_entities

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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.Translations
import redcrafter07.processed.block.WrenchInteractableBlock
import redcrafter07.processed.block.cable.CableBlockEntity.Companion.Connected
import redcrafter07.processed.block.cable.CableBlockEntity.Companion.TRANSMITTER_PROPERTY
import redcrafter07.processed.block.cable.CableBlockEntity.Companion.actualDirection
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.materials.MaterialContainer
import redcrafter07.processed.materials.data.ItemPipeData
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.function.BiFunction
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.min

class ItemPipeBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(ModTileEntities.ITEM_PIPE.get(), pos, blockState), ItemCapableBlockEntity, WrenchInteractableBlock {

    val transferSpeed = lazy {
        val blk = blockState.block
        if (blk !is MaterialContainer) throw IllegalStateException("ItemPipe is not a material container")
        blk.material.getExtraData(ItemPipeData::class.java)?.speed
            ?: throw IllegalStateException("material data for material ${blk.material.identifier} does not have ItemPipeData.")
    }

    val connected = Connected()
    val disallowedConnections = Connected()
    var itemsTransferred = 0

    class ItemHandler(val pipe: ItemPipeBlockEntity, val block: BlockPos) : IItemHandler {
        override fun getSlots(): Int = 1
        override fun getStackInSlot(p0: Int): ItemStack = ItemStack.EMPTY
        override fun insertItem(
            slot: Int, item: ItemStack, simulate: Boolean
        ): ItemStack {
            val lvl = pipe.level ?: return item
            var item = item.copy()
            var simItemsTransferred = pipe.itemsTransferred

            for (entry in pipe.outputs.entries) {
                if (entry.key == block) continue
                if (item.isEmpty) break
                val cap = lvl.getCapability(Capabilities.ItemHandler.BLOCK, entry.key, entry.value.second) ?: continue
                try {
                    for (slot in 0..<cap.slots) {
                        if (simItemsTransferred >= entry.value.first || item.isEmpty) break
                        val left = entry.value.first - simItemsTransferred
                        val removed = max(0, item.count - left)
                        if (item.count - removed <= 0) break

                        val newItem = item.copyWithCount(item.count - removed)
                        val rejected = cap.insertItem(slot, newItem, simulate)
                        simItemsTransferred += newItem.count - rejected.count

                        if (rejected.isEmpty) item.count = removed
                        else {
                            item = rejected
                            item.count += removed
                        }
                    }
                } catch (_: Exception) {
                }
            }

            if (!simulate) pipe.itemsTransferred = simItemsTransferred
            return item
        }

        override fun extractItem(
            p0: Int, p1: Int, p2: Boolean
        ): ItemStack = ItemStack.EMPTY

        override fun getSlotLimit(p0: Int): Int = 0
        override fun isItemValid(p0: Int, p1: ItemStack): Boolean = true
    }

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState) =
        ItemHandler(this, if (side == null) blockPos else blockPos.relative(side.asDirectionNotRotated))

    private var outputCacheInner: Map<BlockPos, Pair<Int, Direction>>? = null
    val outputs: Map<BlockPos, Pair<Int, Direction>>
        get() {
            val outputCache = outputCacheInner
            if (outputCache != null) return outputCache
            val outputs = HashMap<BlockPos, Pair<Int, Direction>>()

            val level =
                level ?: throw IllegalStateException("tried to update the output cache while not having a level")
            traverse(worldPosition, transferSpeed.value) { pipe, transferSpeed ->
                val speed = min(transferSpeed, pipe.transferSpeed.value)

                for (direction in Direction.entries) {
                    if (!pipe.connected[direction]) continue
                    val pos = pipe.blockPos.relative(direction)
                    val be = level.getBlockEntity(pos)
                    if (be != null && be is ItemPipeBlockEntity) continue
                    level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction.opposite) ?: continue
                    outputs.compute(pos) { _, value -> Pair(value?.first ?: speed, direction.opposite) }
                }

                speed
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

    fun traverse(pos: BlockPos, f: Consumer<ItemPipeBlockEntity>) = traverse(pos, Unit) { model, _ -> f.accept(model) }

    fun <T> traverse(pos: BlockPos, data: T, f: BiFunction<ItemPipeBlockEntity, T, T>) {
        val set = hashSetOf(pos)
        val data = f.apply(this, data)
        val level = level ?: return
        traverse(pos, f, set, level, data, this)
    }

    fun <T> traverse(
        pos: BlockPos,
        f: BiFunction<ItemPipeBlockEntity, T, T>,
        set: MutableSet<BlockPos>,
        level: Level,
        data: T,
        be: ItemPipeBlockEntity
    ) {
        for (direction in Direction.entries) {
            val newPos = pos.relative(direction)
            if (set.contains(newPos) || !be.connected[direction]) continue
            set.add(newPos)
            val blockEntity = level.getBlockEntity(newPos)
            if (blockEntity is ItemPipeBlockEntity) {
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
        val be = level.getBlockEntity(pos)
        if (be is ItemPipeBlockEntity) return !be.disallowedConnections[direction.opposite]
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction.opposite) != null
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
}