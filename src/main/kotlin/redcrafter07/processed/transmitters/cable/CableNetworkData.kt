package redcrafter07.processed.transmitters.cable

import net.minecraft.server.level.ServerLevel
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.transmitters.TransmitterNetworkData
import java.util.UUID

class CableNetworkData(networks: MutableMap<UUID, CableNetwork> = HashMap()) :
    TransmitterNetworkData<CableNetwork>(networks) {
    override fun makeNetwork(uuid: UUID) = CableNetwork(uuid)

    companion object {
        private val factory = factory(::CableNetworkData, ::CableNetwork)
        fun getOrCreate(level: ServerLevel): CableNetworkData =
            level.dataStorage.computeIfAbsent(factory, "${ProcessedMod.ID}.cable_networks")
        fun get(level: ServerLevel): CableNetworkData? =
            level.dataStorage.get(factory, "${ProcessedMod.ID}.cable_networks")
    }
}