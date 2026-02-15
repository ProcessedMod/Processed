package redcrafter07.processed.transmitters.transporters

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import redcrafter07.processed.transmitters.TransmitterNetwork
import java.util.UUID

class TransporterNetwork(
    id: UUID,
    blocks: MutableSet<BlockPos> = HashSet(),
    endpoints: MutableList<Pair<BlockPos, Direction>> = ArrayList(),
    roundRobinOffset: Int = 0,
    invalid: Boolean = false
) : TransmitterNetwork(id, blocks, endpoints, roundRobinOffset, invalid)