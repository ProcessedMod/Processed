package redcrafter07.processed.multiblock

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import it.unimi.dsi.fastutil.ints.Int2LongAVLTreeMap
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.chunk.ChunkAccess
import redcrafter07.processed.Attachments
import java.util.stream.LongStream
import kotlin.collections.set
import kotlin.jvm.optionals.getOrNull
import kotlin.streams.asStream

/**
 * Stores a map mapping all blocks associated with a multiblock to their specific controller.
 */
class MultiBlockBlockCache {
    companion object {
        fun remove(access: ChunkAccess) = access.removeData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)

        fun getOrDefault(access: ChunkAccess): MultiBlockBlockCache {
            if (!access.hasData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)) access.setData(
                Attachments.MULTIBLOCK_CHUNK_ATTACHMENT, MultiBlockBlockCache()
            )
            return access.getData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT)
        }

        fun get(access: ChunkAccess): MultiBlockBlockCache? =
            access.getExistingData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT).getOrNull()

        fun chunk(level: LevelAccessor, block: BlockPos): ChunkAccess = level.getChunk(
            SectionPos.blockToSectionCoord(block.x), SectionPos.blockToSectionCoord(block.z)
        )

        /**
         * Removes a block and returns if it was in the map
         */
        fun removeBlock(level: LevelAccessor, block: BlockPos): Boolean {
            val chunk = chunk(level, block)
            val me = get(chunk) ?: return false
            val v = me.removeBlock(block)
            if (v) me.set(chunk)
            return v
        }

        fun getController(level: LevelAccessor, block: BlockPos): BlockPos? =
            get(chunk(level, block))?.getController(block)

        fun setController(level: LevelAccessor, block: BlockPos, controller: BlockPos) {
            val chunk = chunk(level, block)
            val me = getOrDefault(chunk)
            me.setController(block, controller)
            me.set(chunk)
        }

        // See MultiBlockBlockCache::multiblockMap
        fun key(pos: BlockPos): Int {
            // Because the chunks are all relative to 0,0, increasing the coordinate by 16 each chunk, relative to the current chunk is always %16 ((a - 16n) % 16 == a % 16)
            // and %16 is equivalent to &0xf (ignoring everything but the 4 lowest bits).
            val x = pos.x and 0xf
            val y = pos.y
            val z = pos.z and 0xf
            return x or (z shl 4) or (y shl 8)
        }

        fun value(pos: BlockPos): Long = pos.asLong()
        fun value(pos: Long): BlockPos = BlockPos.of(pos)

        val CODEC = object : Codec<MultiBlockBlockCache> {
            override fun <T> encode(
                input: MultiBlockBlockCache, ops: DynamicOps<T>, prefix: T
            ): DataResult<T> {
                val stream = input.multiblockMap.iterator().asSequence().asStream()
                    .flatMapToLong { (key, value) -> LongStream.of(key.toLong(), value) }
                return DataResult.success(ops.createLongList(stream))
            }

            override fun <T> decode(
                ops: DynamicOps<T>, input: T
            ): DataResult<Pair<MultiBlockBlockCache, T>> {
                return ops.getLongStream(input).map { stream ->
                    val me = MultiBlockBlockCache()
                    val iter = stream.iterator()
                    while (iter.hasNext()) {
                        val key = iter.next().toInt()
                        if (!iter.hasNext()) break
                        val value = iter.next()
                        me.multiblockMap[key] = value
                    }
                    Pair(me, input)
                }
            }
        }
    }

    /**
     * Removes a block and returns if it was in the map
     */
    fun removeBlock(block: BlockPos): Boolean = multiblockMap.remove(key(block)) != null
    fun contains(block: BlockPos): Boolean = multiblockMap.containsKey(key(block))
    fun getController(block: BlockPos): BlockPos? {
        val v = multiblockMap[key(block)] ?: return null
        return value(v)
    }

    fun setController(block: BlockPos, controller: BlockPos) {
        multiblockMap[key(block)] = value(controller)
    }

    // Key: x,y,z in *this* chunk relative to chunkPos. x in 0..<16, z in 0..<16, packed into a 32-bit int by bits 0-4 with x, 4-8 with z, and 8-32 with y:
    //
    // │ 0  1  2  3 │ 4  5  6  7 │ 8 9 10 11  …  29 30 31 32 │
    // ┌────────────┬────────────┬───────────────────────────┐
    // │ x (4 bits) │ z (4 bits) │ y (24 bits, 0..<16777216) │
    // └────────────┴────────────┴───────────────────────────┘
    //
    // Value: Packed Block Position relative to chunkPos. Hope that the controller isn't too far away (it shouldn't be)
    val multiblockMap: MutableMap<Int, Long> = Int2LongAVLTreeMap()

    fun set(access: ChunkAccess) {
        if (multiblockMap.isEmpty()) remove(access)
        else access.setData(Attachments.MULTIBLOCK_CHUNK_ATTACHMENT, this)
    }

    override fun toString(): String {
        val builder = StringBuilder("{\n")
        for (entry in multiblockMap.entries) {
            builder.append("    ")
            builder.append(entry.key)
            builder.append(" => ")
            builder.append(entry.value)
            builder.append(",\n")
        }
        builder.append('}')
        return builder.toString()
    }
}