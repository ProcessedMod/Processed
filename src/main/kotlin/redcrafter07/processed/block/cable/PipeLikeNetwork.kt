package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import java.util.*

// Endpoints are the position of the cable, meaning the actual block is `pos.relative(direction)`.
abstract class PipeLikeNetwork(
    val id: UUID,
    private val blocks: MutableSet<BlockPos> = HashSet(),
    private val endpoints: MutableList<Pair<BlockPos, Direction>> = ArrayList(),
    private var roundRobinOffset: Int = 0,
) {
    /**
     * Adds an endpoint. Note that the position of the block with the capability should be `pos.relative(direction)`
     */
    fun addEndpoint(pos: BlockPos, direction: Direction, level: ServerLevel) {
        if (!endpoints.contains(Pair(pos, direction))) {
            endpoints.add(Pair(pos, direction))
            setDirty(level)
        }
    }

    fun isEmpty() = blocks.isEmpty()

    fun containsBlock(block: BlockPos) = blocks.contains(block)

    fun addBlock(block: BlockPos, level: ServerLevel) {
        if (blocks.add(block)) setDirty(level)
    }

    fun clearBlocks(level: ServerLevel) {
        blocks.clear()
        endpoints.clear()
        setDirty(level)
    }

    abstract fun setDirty(level: ServerLevel)
    abstract fun removeNetwork(level: ServerLevel)

    /**
     * Iterates through all endpoints until either all were iterated through or `f` returns false.
     * @return Returns `true` if f returned false, otherwise returns `false`.
     */
    fun forEachEndpoint(roundRobin: Boolean, f: (BlockPos, Direction) -> Boolean): Boolean {
        if (!roundRobin) {
            for (v in endpoints) if (!f(v.first, v.second)) return true
            return false
        }

        val start = roundRobinOffset
        while (++roundRobinOffset != start) {
            if (roundRobinOffset >= endpoints.size) roundRobinOffset = 0
            val endpoint = endpoints[roundRobinOffset]
            if (!f(endpoint.first, endpoint.second)) return true
        }
        return false
    }

    // TODO: Figure out if it's worth doing more complex saving. Specifically, saving a chunk coordinate (which is 2 ints),
    //  and then a list of ints that are 'offsets' into that chunk, where the last 8 bits are the x and y offset,
    //  which is only 8 bits in total (a chunk is 16x16 and 0..<16 is representable by a u4), and the rest 23 / 24 bits
    //  are for the height (allowing for chunks with a maximum height of 16,777,216 blocks
    fun save(tag: CompoundTag): CompoundTag {
        val blocksList = IntArray(blocks.size * 3)
        val endpointsList = IntArray(endpoints.size * 4)
        tag.putInt("roundRobinOffset", roundRobinOffset)

        for ((i, b) in blocks.withIndex()) {
            val offset = i * 3
            blocksList[offset] = b.x
            blocksList[offset + 1] = b.y
            blocksList[offset + 2] = b.z
        }
        for ((i, b) in endpoints.withIndex()) {
            val offset = i * 4
            endpointsList[offset] = b.first.x
            endpointsList[offset + 1] = b.first.y
            endpointsList[offset + 2] = b.first.z
            endpointsList[offset + 3] = b.second.get3DDataValue()
        }

        tag.putIntArray("blocks", blocksList)
        tag.putIntArray("endpoints", endpointsList)
        return tag
    }

    companion object {
        fun <T> load(
            tag: CompoundTag,
            uuid: UUID,
            ctor: (UUID, MutableSet<BlockPos>, MutableList<Pair<BlockPos, Direction>>, Int) -> T
        ): T? {
            var roundRobinOffset = 0
            if (tag.contains("roundRobinOffset", Tag.TAG_INT.toInt())) roundRobinOffset = tag.getInt("roundRobinOffset")

            if (tag.contains("blocks", Tag.TAG_INT_ARRAY.toInt())) return null
            if (tag.contains("endpoints", Tag.TAG_LONG.toInt())) return null
            val blocksList = tag.getIntArray("blocks")
            val endpointsList = tag.getIntArray("endpoints")
            val blocks = HashSet<BlockPos>(blocksList.size / 3)
            val endpoints = ArrayList<Pair<BlockPos, Direction>>(endpointsList.size / 4)

            for (i in 0..<blocksList.size / 3) {
                val offset = i * 3
                blocks.add(BlockPos(blocksList[offset], blocksList[offset + 1], blocksList[offset + 2]))
            }

            for (i in 0..<endpointsList.size / 4) {
                val offset = i * 4
                val pos = BlockPos(endpointsList[offset], endpointsList[offset + 1], endpointsList[offset + 2])
                val dir = Direction.from3DDataValue(endpointsList[offset + 3])
                endpoints.add(Pair(pos, dir))
            }

            return ctor(uuid, blocks, endpoints, roundRobinOffset)
        }
    }
}