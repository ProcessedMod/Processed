package redcrafter07.processed.integration.emi.widgets

import dev.emi.emi.api.widget.Bounds
import dev.emi.emi.api.widget.Widget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.gui.RenderUtils.fill

class ComputationWidget(val x: Int, val y: Int, val height: Int, var amountPerTick: Long) : Widget() {
    constructor(x: Int, y: Int, amountPerTick: Long) : this(x, y, 40, amountPerTick)

    override fun getBounds() = Bounds(x, y, 10, height)

    override fun render(
        draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float
    ) {
        fill(draw, x, y, 1, height - 1, RenderUtils.DARK_GRAY)
        fill(draw, x, y, 9, 1, RenderUtils.DARK_GRAY)
        fill(draw, x + 9, y, 1, 1, RenderUtils.GRAY)
        fill(draw, x, y + height - 1, 1, 1, RenderUtils.GRAY)
        fill(draw, x + 1, y + height - 1, 9, 1, RenderUtils.WHITE)
        fill(draw, x + 9, y + 1, 1, height - 1, RenderUtils.WHITE)
        fill(draw, x + 1, y + 1, 8, height - 2, RenderUtils.COMPUTATION)
    }

    override fun getTooltip(mouseX: Int, mouseY: Int): List<ClientTooltipComponent> = listOf(
        ClientTooltipComponent.create(
            Translations.hashes(amountPerTick).append("/t").visualOrderText
        )
    )
}