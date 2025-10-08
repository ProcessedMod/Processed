package redcrafter07.processed.gui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Renderable
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier

class ProgressBarWidget(
    val x: Int, val y: Int, val data: ProgressBarData, val progressSupplier: Supplier<Double>
) : Renderable {
    private fun blit(graphics: GuiGraphics, x: Int, y: Int, offX: Int, offY: Int, width: Int, height: Int) {
        graphics.blit(data.texture, x, y, offX.toFloat(), offY.toFloat(), width, height, data.width * 2, data.height)
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        blit(graphics, x, y, 0, 0, data.width, data.height)

        when (data.direction) {
            ProgressBarDirection.Up -> {
                val height = (progressSupplier.get() * data.height).toInt()
                blit(graphics, x, y + data.height - height, data.width, data.height - height, data.width, height)
            }
            ProgressBarDirection.Down -> {
                val height = (progressSupplier.get() * data.height).toInt()
                blit(graphics, x, y, data.width, 0, data.width, height)
            }
            ProgressBarDirection.Left -> {
                val width = (progressSupplier.get() * data.width).toInt()
                blit(graphics, x, y, data.width, 0, width, data.height)
            }
            ProgressBarDirection.Right -> {
                val width = (progressSupplier.get() * data.width).toInt()
                blit(graphics, x + data.width - width, y, data.width * 2 - width, 0, width, data.height)
            }
        }
    }

    enum class ProgressBarDirection {
        Down, Up, Right, Left
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    data class ProgressBarData(
        val texture: ResourceLocation, val width: Int, val height: Int, val direction: ProgressBarDirection
    ) {
        fun create(x: Int, y: Int, supplier: Supplier<Double>): ProgressBarWidget {
            return ProgressBarWidget(x, y, this, supplier)
        }
    }
}