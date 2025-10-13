package redcrafter07.processed.integration.theoneprobe

import mcjty.theoneprobe.TheOneProbe
import mcjty.theoneprobe.api.IProbeHitData
import mcjty.theoneprobe.api.IProbeInfo
import mcjty.theoneprobe.api.IProbeInfoProvider
import mcjty.theoneprobe.api.NumberFormat
import mcjty.theoneprobe.api.ProbeMode
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.widgets.EnergyBarWidget
import redcrafter07.processed.rl

object TheOneProbeIntegration {
    fun init() {
        TheOneProbe.theOneProbeImp.registerProvider(ProcessedEnergyProvider)
    }

    object ProcessedEnergyProvider : IProbeInfoProvider {
        override fun getID(): ResourceLocation = rl("tiered_energy")

        override fun addProbeInfo(
            mode: ProbeMode, info: IProbeInfo, player: Player, level: Level, state: BlockState, hit: IProbeHitData
        ) {
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val cap =
                level.getCapability(ProcessedPower.BLOCK, hit.pos, null)?.energy() ?: return
            if (cap.maxEnergyStored <= 0) return

            val style =
                info.defaultProgressStyle().prefix(EnergyBarWidget.getEnergyComponent(cap.energyStored, false))
                    .suffix(Component.empty()).filledColor(RenderUtils.ENERGY).alternateFilledColor(RenderUtils.ENERGY)
                    .numberFormat(NumberFormat.NONE)
            info.progress(cap.energyStored, cap.maxEnergyStored, style)
        }
    }
}