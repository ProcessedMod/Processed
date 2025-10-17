package redcrafter07.processed.gui.widgets

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.util.FastColor
import org.joml.Vector2i
import redcrafter07.processed.Translations
import redcrafter07.processed.miner.Planetoid
import java.time.Duration
import java.util.function.Consumer
import kotlin.jvm.optionals.getOrNull

class PlanetoidWidget(
    val planetoid: Planetoid,
    val key: ResourceKey<Planetoid>,
    val onPress: Consumer<ResourceKey<Planetoid>>,
    scale: Double,
    offset: Vector2i,
    isFocusedPlanetoid: Boolean,
) : AbstractWidget(
    realX(planetoid, isFocusedPlanetoid, offset, scale),
    realY(planetoid, isFocusedPlanetoid, offset, scale),
    (planetoid.size.toDouble() * scale).toInt(),
    (planetoid.size.toDouble() * scale).toInt(),
    Component.empty()
) {
    companion object {
        fun realX(planetoid: Planetoid, isFocusedPlanetoid: Boolean, offset: Vector2i, scale: Double): Int {
            val x = if (isFocusedPlanetoid) 0 else planetoid.x
            return ((x + 1000 - planetoid.size / 2).toDouble() * scale).toInt() + offset.x
        }

        fun realY(planetoid: Planetoid, isFocusedPlanetoid: Boolean, offset: Vector2i, scale: Double): Int {
            val y = if (isFocusedPlanetoid) 0 else planetoid.y
            return ((y + 550 - planetoid.size / 2).toDouble() * scale).toInt() + offset.y
        }
    }

    val scaledSize = (planetoid.size.toDouble() * scale).toInt()
    val scaledRadiusSquared: Int

    init {
        val scaledRadius = planetoid.size.toDouble() * scale / 2
        scaledRadiusSquared = (scaledRadius * scaledRadius).toInt()
    }

    val textureSize = lazy {
        val resourceManager = Minecraft.getInstance().resourceManager
        try {
            val res = resourceManager.getResourceOrThrow(planetoid.texture)
            res.open().use {
                val img = NativeImage.read(it)
                Vector2i(img.width, img.height)
            }
        } catch (_: Exception) {
            return@lazy Vector2i(0)
        }
    }

    init {
        val tooltip = Translations.planetoidWidgetTooltip(planetoid.name)
        if (planetoid.distance.isPresent) {
            val distance = Translations.unitKilometers(planetoid.distance.get())
            tooltip.append("\n").append(Translations.planetoidDistance(distance))
        }
        if (planetoid.gravity.isPresent) tooltip.append("\n")
            .append(Translations.planetoidGravity(planetoid.gravity.get()))
        this.tooltip = Tooltip.create(tooltip)
        setTooltipDelay(Duration.ZERO)
    }

    override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean {
        if (!active || !visible) return false
        val offsetX = mouseX.toInt() - (x + scaledSize / 2)
        val offsetY = mouseY.toInt() - (y + scaledSize / 2)
        return (offsetX * offsetX + offsetY * offsetY) < scaledRadiusSquared
    }

    override fun isValidClickButton(button: Int) = button == 0

    override fun clicked(mouseX: Double, mouseY: Double): Boolean = active && visible && isMouseOver(mouseX, mouseY)

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        onPress.accept(key)
        super.onClick(mouseX, mouseY, button)
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        isHovered = isMouseOver(mouseX.toDouble(), mouseY.toDouble())

        val color = planetoid.color.getOrNull()?.value
        if (color != null) {
            val red = FastColor.ARGB32.red(color).toFloat() / 255f
            val green = FastColor.ARGB32.green(color).toFloat() / 255f
            val blue = FastColor.ARGB32.blue(color).toFloat() / 255f
            val alpha = FastColor.ARGB32.alpha(color).toFloat() / 255f
            graphics.setColor(red, green, blue, alpha)
        }
        graphics.blit(
            planetoid.texture,
            x,
            y,
            width,
            height,
            0f,
            0f,
            textureSize.value.x,
            textureSize.value.y,
            textureSize.value.x,
            textureSize.value.y
        )
        if (color != null) graphics.setColor(1f, 1f, 1f, 1f)
    }

    override fun updateWidgetNarration(output: NarrationElementOutput) {
        output.add(NarratedElementType.TITLE, Translations.planetoidWidgetTooltip(planetoid.name))
        if (planetoid.distance.isPresent) {
            val distance = Translations.unitKilometersLong(planetoid.distance.get())
            output.add(NarratedElementType.HINT, Translations.planetoidDistance(distance))
        }
        if (planetoid.gravity.isPresent) output.add(
            NarratedElementType.HINT, Translations.planetoidGravity(planetoid.gravity.get())
        )
    }
}