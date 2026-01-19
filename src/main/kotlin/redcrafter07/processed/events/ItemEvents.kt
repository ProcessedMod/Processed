package redcrafter07.processed.events

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent.MouseScrollingEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.Translations
import redcrafter07.processed.items.WrenchItem
import redcrafter07.processed.network.WrenchModeChangePacket

@EventBusSubscriber(modid = ProcessedMod.ID, value = [Dist.CLIENT])
object ItemEvents {
    @SubscribeEvent
    fun onMouseScroll(event: MouseScrollingEvent) {
        val connection = Minecraft.getInstance().connection ?: return
        val player = Minecraft.getInstance().player

        if (player != null && player.isShiftKeyDown) {
            val itemStack = player.mainHandItem

            if (itemStack.item is WrenchItem) {
                val mode = WrenchItem.getMode(itemStack)

                val newMode = if (event.scrollDeltaY > 0) mode.next else mode.previous
                WrenchItem.setMode(itemStack, newMode)

                player.getInventory().setChanged()
                event.setCanceled(true)

                connection.send(WrenchModeChangePacket(newMode))
                player.displayClientMessage(Translations.wrenchModeTooltip(newMode), true)
            }
        }
    }
}