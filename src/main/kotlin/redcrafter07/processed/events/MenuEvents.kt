package redcrafter07.processed.events

import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.EntityTickEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.gui.inventory.AbstractProcessedContainerMenu

@EventBusSubscriber(modid = ProcessedMod.ID)
object MenuEvents {
    @SubscribeEvent
    fun onTickPlayer(e: EntityTickEvent.Pre) {
        val ent = e.entity
        if(ent !is ServerPlayer) return
        val menu = ent.containerMenu
        if(menu is AbstractProcessedContainerMenu) menu.syncData.onTick()
    }
}