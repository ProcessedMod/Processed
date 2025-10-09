package redcrafter07.processed.gui.widgets

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.RenderUtils.fill
import java.util.function.Supplier
import kotlin.math.max
import kotlin.math.min

class EnergyBarWidget(
    x: Int, y: Int, width: Int, height: Int, val maxEnergy: Int, val energySupplier: Supplier<Int>
) : AbstractWidget(x, y, width, height, Component.empty()) {
    public override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (width < 2 || height < 2) return
        val x = getX()
        val y = getY()

        fill(graphics, x, y, 1, height - 1, RenderUtils.DARK_GRAY)
        fill(graphics, x, y, width - 1, 1, RenderUtils.DARK_GRAY)

        fill(graphics, x + width - 1, y, 1, 1, RenderUtils.GRAY)
        fill(graphics, x, y + height - 1, 1, 1, RenderUtils.GRAY)
        if (width != 2 && height != 2) fill(graphics, x + 1, y + 1, width - 2, height - 2, RenderUtils.GRAY)

        fill(graphics, x + 1, y + height - 1, width - 1, 1, RenderUtils.WHITE)
        fill(graphics, x + width - 1, y + 1, 1, height - 1, RenderUtils.WHITE)

        val energy = energySupplier.get()
        val energyHeight = min(max((energy * (height - 2) / maxEnergy), 0), height - 2)

        fill(graphics, x + 1, y - 1 + height - energyHeight, width - 2, energyHeight, RenderUtils.ENERGY)

        val onesFlag = Screen.hasShiftDown()
        if (isHovered) graphics.renderTooltip(
            Minecraft.getInstance().font, Translations.energyBarTooltip(
                getEnergyComponent(energy, onesFlag),
                getEnergyComponent(maxEnergy, onesFlag),
            ), mouseX, mouseY
        )
    }

    public override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    }

    override fun clicked(mouseX: Double, mouseY: Double): Boolean {
        return false
    }

    companion object {
        fun getEnergyComponent(energy: Int): MutableComponent {
            if (energy >= 2000000) return Translations.energyBarUnitMillion(energy / 1000000)
            if (energy >= 2000) return Translations.energyBarUnitThousand(energy / 1000)
            return Translations.energyBarUnitOnes(energy)
        }

        fun getEnergyComponent(energy: Int, keepAsOnes: Boolean): MutableComponent =
            if (keepAsOnes) Translations.energyBarUnitOnes(energy) else getEnergyComponent(energy)
    }
}