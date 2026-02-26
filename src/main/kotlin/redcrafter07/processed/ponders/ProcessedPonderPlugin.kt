package redcrafter07.processed.ponders

import net.createmod.ponder.api.registration.PonderPlugin
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper
import net.minecraft.resources.ResourceLocation
import redcrafter07.processed.ProcessedMod

object ProcessedPonderPlugin : PonderPlugin {
    override fun getModId() = ProcessedMod.ID

    override fun registerScenes(helper: PonderSceneRegistrationHelper<ResourceLocation>) {
        RocketLauncherScene.add(helper)
    }
}