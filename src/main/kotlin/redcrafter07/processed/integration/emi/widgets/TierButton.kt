package redcrafter07.processed.integration.emi.widgets

import dev.emi.emi.api.widget.Bounds
import dev.emi.emi.api.widget.Widget
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import redcrafter07.processed.ProcessedTier
import java.util.function.Consumer

class TierButton(val x: Int, val y: Int, var tier: ProcessedTier, val setTier: Consumer<ProcessedTier>) : Widget() {
    val baseTier = tier

    override fun getBounds(): Bounds {
        val width = Minecraft.getInstance().font.width(tier.nameColored)
        return Bounds(x, y, width, Minecraft.getInstance().font.lineHeight)
    }

    override fun render(draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        draw.drawString(Minecraft.getInstance().font, tier.nameColored, x, y, -1, false)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int): Boolean {
        val newTier = when (button) {
            0 -> ProcessedTier.fromTierNumberOptional(tier.tier + 1)
            1 -> ProcessedTier.fromTierNumberOptional(tier.tier - 1)
            2 -> baseTier
            else -> null
        }
        if (newTier != null) {
            if (newTier.tier < baseTier.tier) {
                setTier.accept(baseTier)
                tier = baseTier
            } else {
                setTier.accept(newTier)
                tier = newTier
            }
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }
}