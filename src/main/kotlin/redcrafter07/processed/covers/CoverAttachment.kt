package redcrafter07.processed.covers

import com.mojang.datafixers.util.Pair
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.attachment.IAttachmentHolder
import net.neoforged.neoforge.attachment.IAttachmentSerializer
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.network.RPCFunctions
import java.util.function.Consumer

// Packed Coordinates:
// Because a cover attachment is attached to a chunk, the x and z are in [0..=15] and need only 4 bits.
// bits 0-2: Direction
// bits 3-6: Position X
// bits 7-10: Position Z
// bits 11-31: Position Y
class CoverAttachment(private val undeserializedCovers: MutableMap<Int, CompoundTag> = HashMap()) {
    private val ticking: MutableSet<Int> = HashSet()
    val map: MutableMap<Int, CoverBehavior> = HashMap()

    /// Tries placing a cover, returning if it succeeded.
    fun placeCover(
        pos: BlockPos, dir: Direction, chunk: ChunkPos, level: ServerLevel, cover: () -> CoverBehavior
    ): Boolean {
        val v = pack(dir, pos, chunk)
        if (map.containsKey(v)) return false
        val behavior = cover()
        map[v] = behavior
        val rl = behavior.cover.location.value
        for (p in level.chunkSource.chunkMap.getPlayers(chunk, false)) RPCFunctions.coverPlaced.sendToClient(
            p, pos, dir, rl
        )

        if (behavior.ticks()) ticking.add(v)
        return true
    }

    val isEmpty: Boolean get() = undeserializedCovers.isEmpty() && map.isEmpty()

    fun removeCovers(pos: BlockPos, chunk: ChunkPos, dropper: Consumer<List<ItemStack>>) {
        val k = pack(pos, chunk)
        if (k == -1) throw IllegalStateException("Position outside of chunk")
        for (dir in Direction.entries) {
            val actualK = k or dir.get3DDataValue()
            ticking.remove(actualK)
            val v = map.remove(actualK) ?: continue
            val items = try {
                v.dropItems()
            } catch (e: Exception) {
                val dim = v.level.dimension().location()
                ProcessedMod.LOG.info(
                    "Failed to drop items for cover at ${pos.x} ${pos.y} ${pos.z}, facing $dir in level $dim", e
                )
                continue
            }
            dropper.accept(items)
        }
    }

    fun removeCover(pos: BlockPos, chunk: ChunkPos, dir: Direction, dropper: Consumer<List<ItemStack>>) {
        val k = pack(dir, pos, chunk)
        if (k == -1) throw IllegalStateException("Position outside of chunk")
        ticking.remove(k)
        val v = map.remove(k) ?: return
        val items = try {
            v.dropItems()
        } catch (e: Exception) {
            val dim = v.level.dimension().location()
            ProcessedMod.LOG.info(
                "Failed to drop items for cover at ${pos.x} ${pos.y} ${pos.z}, facing $dir in level $dim", e
            )
            return
        }
        dropper.accept(items)
    }

    fun asClient(): ClientCoverAttachment {
        if (isEmpty) return ClientCoverAttachment(hashMapOf())
        val map = HashMap<Int, Cover>()

        for ((k, v) in this.map) map[k] = v.cover
        for ((k, v) in this.undeserializedCovers) {
            try {
                val rl = ResourceLocation.parse(v.getString("id"))
                map[k] = Cover.REGISTRY.get(rl) ?: continue
            } catch (_: Exception) {
            }
        }

        return ClientCoverAttachment(map)
    }

    fun tick(level: ServerLevel, chunk: ChunkPos) {
        val didWork = undeserializedCovers.isNotEmpty()
        while (undeserializedCovers.isNotEmpty()) {
            val k = undeserializedCovers.keys.first()
            val unpacked = unpack(k, chunk)
            val tag = undeserializedCovers.remove(k) ?: break

            val rl = try {
                val id = tag.getString("id")
                ResourceLocation.parse(id)
            } catch (e: Exception) {
                val pos = unpacked.second
                val dir = unpacked.first
                val lvl = level.dimension().location()
                ProcessedMod.LOG.error(
                    "Failed to get id for cover at ${pos.x} ${pos.y} ${pos.z}, facing $dir, in $lvl", e
                )
                continue
            }
            try {
                val cover = Cover.REGISTRY.get(rl) ?: throw NullPointerException("No cover registered for $rl")
                val behavior = cover.makeCoverBehavior(unpacked.second, level, unpacked.first)
                map[k] = behavior
                if (behavior.ticks()) ticking.add(k)
                behavior.load(tag, level.registryAccess())
            } catch (e: Exception) {
                val pos = unpacked.second
                val dir = unpacked.first
                val lvl = level.dimension().location()
                ProcessedMod.LOG.error(
                    "Failed to load cover $rl at ${pos.x} ${pos.y} ${pos.z}, facing $dir, in $lvl", e
                )
            }
        }
        if (didWork) level.getChunk(chunk.x, chunk.z).isUnsaved = true

        if (ticking.isEmpty()) return
        for (k in ticking) {
            val v = map[k] ?: continue
            try {
                v.executeTick(level)
            } catch (e: Exception) {
                val pos = v.position
                val level = v.level.dimension().location()
                ProcessedMod.LOG.info(
                    "Error while ticking cover at ${pos.x} ${pos.y} ${pos.z}, facing ${v.direction}, in level $level", e
                )
            }
        }
    }

    companion object {
        /**
         * @return The valid position or -1 if the position was outside the chunk.
         *  -1 is an invalid bit pattern, because directions only go from 0-5, but in this case the index would be 7.
         */
        fun pack(dir: Direction, pos: BlockPos, chunk: ChunkPos): Int {
            val offsetX = pos.x - chunk.minBlockX
            val offsetZ = pos.z - chunk.minBlockZ
            if (offsetZ !in 0..15 || offsetX !in 0..15) return -1
            return dir.get3DDataValue() or (offsetX shl 3) or (offsetZ shl 7) or (pos.y shl 11)
        }

        fun unpack(pos: Int, chunk: ChunkPos): Pair<Direction, BlockPos> {
            val x = ((pos shr 3) and 0xf) + chunk.minBlockX
            val z = ((pos shr 7) and 0xf) + chunk.minBlockZ
            val y = pos shr 11
            val dir = Direction.from3DDataValue(pos and 0b111)
            return Pair(dir, BlockPos(x, y, z))
        }

        /**
         * @return The valid position or -1 if the position was outside the chunk.
         *  -1 is an invalid bit pattern, because directions only go from 0-5, but in this case the index would be 7.
         */
        fun pack(pos: BlockPos, chunk: ChunkPos): Int {
            val offsetX = pos.x - chunk.minBlockX
            val offsetZ = pos.z - chunk.minBlockZ
            if (offsetZ !in 0..15 || offsetX !in 0..15) return -1
            return (offsetX shl 3) or (offsetZ shl 7) or (pos.y shl 11)
        }

        private val MAP_CODEC = ByteBufCodecs.map<RegistryFriendlyByteBuf, Int, CompoundTag, Map<Int, CompoundTag>>(
            HashMap<Int, CompoundTag>::newHashMap, ByteBufCodecs.INT, ByteBufCodecs.COMPOUND_TAG
        )
        val STREAM_CODEC = object : StreamCodec<RegistryFriendlyByteBuf, CoverAttachment> {
            override fun decode(buf: RegistryFriendlyByteBuf) = CoverAttachment(MAP_CODEC.decode(buf).toMutableMap())

            override fun encode(
                buf: RegistryFriendlyByteBuf, attachment: CoverAttachment
            ) {
                val v = attachment.map.mapValues { it.value.save(buf.registryAccess()) }
                MAP_CODEC.encode(buf, v)
            }

        }
    }

    object Serializer : IAttachmentSerializer<CompoundTag, CoverAttachment> {
        override fun read(
            attachmentHolder: IAttachmentHolder, tag: CompoundTag, registries: HolderLookup.Provider
        ): CoverAttachment {
            val attachment = CoverAttachment()
            for (key in tag.allKeys) {
                val k = key.toIntOrNull() ?: continue
                if (!tag.contains(key, CompoundTag.TAG_COMPOUND.toInt())) continue
                attachment.undeserializedCovers[k] = tag.getCompound(key)
            }
            return attachment
        }

        override fun write(
            attachment: CoverAttachment, registries: HolderLookup.Provider
        ): CompoundTag? {
            if (attachment.map.isEmpty() && attachment.undeserializedCovers.isEmpty()) return null
            val tag = CompoundTag()
            for ((k, v) in attachment.map) tag.put(k.toString(), v.save(registries))

            for ((k, v) in attachment.undeserializedCovers) tag.put(k.toString(), v)
            return tag
        }
    }
}