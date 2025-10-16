package redcrafter07.processed.integration.jade

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.Vec2
import net.neoforged.fml.earlydisplay.RenderElement.clamp
import redcrafter07.processed.gui.RenderUtils
import snownee.jade.api.ui.Element
import kotlin.math.floor
import kotlin.math.max

class EnergyElement(val component: Component, progress: Float) : Element() {
    val font: Font = Minecraft.getInstance().font
    val progress = clamp(progress, 0f, 1f)

    override fun getSize(): Vec2 {
        val width = max(font.width(component) + 6, 70)
        val height = font.lineHeight + 4
        return Vec2(width.toFloat(), height.toFloat())
    }

    override fun render(
        graphics: GuiGraphics, rawX: Float, rawY: Float, maxX: Float, maxY: Float
    ) {
        val x = floor(rawX).toInt()
        val y = floor(rawY).toInt()
        val width = floor(maxX - rawX).toInt()
        val height = floor(maxY - rawY).toInt()
        graphics.renderOutline(x, y, width, height, RenderUtils.BLACK)
        graphics.fill(
            x + 1, y + 1, x + 1 + (progress * (width - 2).toFloat()).toInt(), y + height - 1, RenderUtils.ENERGY
        )
        graphics.drawScrollingString(font, component, x + 1, x + width - 3, y + 3, -1)
    }
}