package redcrafter07.processed.block.cable

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import java.util.UUID

class CableNetwork(
    id: UUID,
    blocks: MutableSet<BlockPos> = HashSet(),
    endpoints: MutableList<Pair<BlockPos, Direction>> = ArrayList(),
    roundRobinOffset: Int = 0,
) : PipeLikeNetwork(id, blocks, endpoints, roundRobinOffset) {
    override fun setDirty(level: ServerLevel) {
        CableNetworkData.getOrNull(level)?.setDirty()
    }

    override fun removeNetwork(level: ServerLevel) {
        CableNetworkData.getOrNull(level)?.remove(id)
    }
}