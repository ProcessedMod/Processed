package redcrafter07.processed.transmitters.transporters

import net.minecraft.server.level.ServerLevel
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.transmitters.TransmitterNetworkData
import java.util.UUID

class TransporterNetworkData(networks: MutableMap<UUID, TransporterNetwork> = HashMap()) :
    TransmitterNetworkData<TransporterNetwork>(networks) {
    override fun makeNetwork(uuid: UUID) = TransporterNetwork(uuid)

    companion object {
        private val factory = factory(::TransporterNetworkData, ::TransporterNetwork)
        fun getOrCreate(level: ServerLevel): TransporterNetworkData =
            level.dataStorage.computeIfAbsent(factory, "${ProcessedMod.ID}.transporter_networks")
        fun get(level: ServerLevel): TransporterNetworkData? =
            level.dataStorage.get(factory, "${ProcessedMod.ID}.transporter_networks")
    }
}