package redcrafter07.processed.integration.emi.widgets

import dev.emi.emi.api.widget.Bounds
import dev.emi.emi.api.widget.Widget
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import redcrafter07.processed.Translations
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import redcrafter07.processed.gui.widgets.ProgressBarWidget.ProgressBarDirection

class ProgressWidget(
    val x: Int, val y: Int, val data: ProgressBarWidget.ProgressBarData, timeTicks: Int
) : Widget() {
    private var timeMillis = timeTicks * 50

    fun setTime(timeTicks: Int) {
        timeMillis = timeTicks * 50
    }

    override fun getBounds(): Bounds = Bounds(x, y, data.width, data.height)

    private fun blit(graphics: GuiGraphics, x: Int, y: Int, offX: Int, offY: Int, width: Int, height: Int) {
        graphics.blit(data.texture, x, y, offX.toFloat(), offY.toFloat(), width, height, data.width * 2, data.height)
    }

    override fun render(draw: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        if(timeMillis <= 0) {
            blit(draw, x, y, data.width, 0, data.width, data.height)
            return
        }

        blit(draw, x, y, 0, 0, data.width, data.height)
        when (data.direction) {
            ProgressBarDirection.Up -> {
                val height = ((System.currentTimeMillis() % timeMillis) * data.height / timeMillis).toInt()
                blit(draw, x, y + data.height - height, data.width, data.height - height, data.width, height)
            }

            ProgressBarDirection.Down -> {
                val height = ((System.currentTimeMillis() % timeMillis) * data.height / timeMillis).toInt()
                blit(draw, x, y, data.width, 0, data.width, height)
            }

            ProgressBarDirection.Left -> {
                val width = ((System.currentTimeMillis() % timeMillis) * data.width / timeMillis).toInt()
                blit(draw, x + data.width - width, y, data.width * 2 - width, 0, width, data.height)
            }

            ProgressBarDirection.Right -> {
                val width = ((System.currentTimeMillis() % timeMillis) * data.width / timeMillis).toInt()
                blit(draw, x, y, data.width, 0, width, data.height)
            }
        }
    }

    override fun getTooltip(mouseX: Int, mouseY: Int): List<ClientTooltipComponent> = listOf(
        ClientTooltipComponent.create(
            Translations.guiProgressTooltip(timeMillis / 50).visualOrderText
        )
    )
}