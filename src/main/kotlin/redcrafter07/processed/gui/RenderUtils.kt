package redcrafter07.processed.gui

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.util.FastColor
import net.minecraft.world.inventory.InventoryMenu
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.rl

object RenderUtils {
    val WIDGETS_TEXTURE = rl("textures/gui/widgets.png")
    val GUI_BASE_TEXTURE = rl("textures/gui/gui_base.png")
    const val GUI_BASE_TEXTURE_WIDTH = 176
    const val GUI_BASE_TEXTURE_HEIGHT = 166

    fun renderSlot(graphics: GuiGraphics, x: Int, y: Int) {
        graphics.blit(WIDGETS_TEXTURE, x, y, 0, 22, 18, 18)
    }

    fun renderDefault(screen: AbstractContainerScreen<*>, graphics: GuiGraphics) {
        val xOff = screen.guiLeft
        val yOff = screen.guiTop
        graphics.blit(
            GUI_BASE_TEXTURE, xOff, yOff, 0, 0, GUI_BASE_TEXTURE_WIDTH, GUI_BASE_TEXTURE_HEIGHT
        )
        for (slot in screen.getMenu().slots) renderSlot(graphics, xOff + slot.x - 1, yOff + slot.y - 1)
    }

    fun color(red: Int, green: Int, blue: Int, alpha: Int): Int {
        return FastColor.ARGB32.color(alpha, red, green, blue)
    }

    fun color(red: Int, green: Int, blue: Int): Int {
        return color(red, green, blue, 255)
    }

    fun fill(guiGraphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int, color: Int) {
        guiGraphics.fill(x, y, x + width, y + height, color)
    }

    fun getFluidTexture(fluidStack: FluidStack, flowing: Boolean): TextureAtlasSprite {
        val properties = IClientFluidTypeExtensions.of(fluidStack.fluid)
        val spriteLocation = if (flowing) properties.flowingTexture else properties.stillTexture
        return Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(spriteLocation)
    }

    fun getFluidColor(fluidStack: FluidStack): Int {
        val properties = IClientFluidTypeExtensions.of(fluidStack.fluid)
        return properties.tintColor
    }

    fun renderCube(
        buffer: VertexConsumer,
        poseStack: PoseStack,
        xMax: Float,
        xMin: Float,
        yMin: Float,
        height: Float,
        zMin: Float,
        zMax: Float,
        uMin: Float,
        uMax: Float,
        vMin: Float,
        vMax: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        packedLight: Int,
        packedOverlay: Int
    ) {

        val vHeight = vMax - vMin

        // top
        addVertexWithUV(
            buffer, poseStack, xMax, height, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMax, height, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, height, zMin, uMin, vMax, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, height, zMax, uMax, vMax, red, green, blue, alpha, packedLight, packedOverlay
        )

        // north
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMin,
            height,
            zMin,
            uMin,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMax,
            height,
            zMin,
            uMax,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )

        // south
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMax,
            height,
            zMax,
            uMin,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMin,
            height,
            zMax,
            uMax,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )

        // east
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMin, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMax,
            height,
            zMin,
            uMin,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMax,
            height,
            zMax,
            uMax,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMax, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )

        // west
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMin,
            height,
            zMax,
            uMin,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer,
            poseStack,
            xMin,
            height,
            zMin,
            uMax,
            vMin + (vHeight * height),
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )

        // down
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMin, uMax, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMax, yMin, zMax, uMin, vMin, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMax, uMin, vMax, red, green, blue, alpha, packedLight, packedOverlay
        )
        addVertexWithUV(
            buffer, poseStack, xMin, yMin, zMin, uMax, vMax, red, green, blue, alpha, packedLight, packedOverlay
        )
    }

    fun addVertexWithUV(
        buffer: VertexConsumer,
        matrixStack: PoseStack,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float,
        packedLight: Int,
        packedOverlay: Int
    ) {
        buffer.addVertex(matrixStack.last().pose(), x, y, z).setColor(red, green, blue, alpha).setUv(u, v)
            .setLight(packedLight).setOverlay(packedOverlay).setNormal(1f, 0f, 0f)
    }

    val WHITE: Int = color(0xff, 0xff, 0xff)
    val GRAY: Int = color(0x8b, 0x8b, 0x8b)
    val DARK_GRAY: Int = color(0x37, 0x37, 0x37)
    val ENERGY: Int = color(0x0e, 0xa5, 0xe9)
}