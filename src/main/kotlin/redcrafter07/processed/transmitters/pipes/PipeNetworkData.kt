package redcrafter07.processed.transmitters.pipes

import net.minecraft.server.level.ServerLevel
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.transmitters.TransmitterNetworkData
import java.util.UUID

class PipeNetworkData(networks: MutableMap<UUID, PipeNetwork> = HashMap()) :
    TransmitterNetworkData<PipeNetwork>(networks) {
    override fun makeNetwork(uuid: UUID) = PipeNetwork(uuid)

    companion object {
        private val factory = factory(::PipeNetworkData, ::PipeNetwork)
        fun getOrCreate(level: ServerLevel): PipeNetworkData =
            level.dataStorage.computeIfAbsent(factory, "${ProcessedMod.ID}.pipe_networks")
        fun get(level: ServerLevel): PipeNetworkData? =
            level.dataStorage.get(factory, "${ProcessedMod.ID}.pipe_networks")
    }
}