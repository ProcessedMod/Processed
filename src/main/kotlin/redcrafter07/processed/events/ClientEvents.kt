package redcrafter07.processed.events

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.multiblock.MultiblockPreview

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object ClientEvents {
    @SubscribeEvent
    fun onRender(ev: RenderLevelStageEvent) {
        if (ev.stage != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return
        val level = Minecraft.getInstance().level ?: return
        MultiblockPreview.display(level, ev.camera, ev.poseStack)
    }
}