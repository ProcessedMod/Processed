package redcrafter07.processed.events

import net.minecraft.server.level.ServerLevel
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.LevelTickEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.transmitters.cable.CableNetworkData
import redcrafter07.processed.transmitters.pipes.PipeNetworkData
import redcrafter07.processed.transmitters.transporters.TransporterNetworkData

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME)
object ServerEvents {
    @SubscribeEvent
    fun onAfterLevelTick(ev: LevelTickEvent.Post) {
        val level = ev.level
        if (level.isClientSide || level !is ServerLevel) return

        CableNetworkData.get(level)?.doTick(level)
        TransporterNetworkData.get(level)?.doTick(level)
        PipeNetworkData.get(level)?.doTick(level)
    }
}