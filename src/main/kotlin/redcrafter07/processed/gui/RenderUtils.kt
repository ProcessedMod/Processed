package redcrafter07.processed.gui

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferUploader
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.network.chat.FormattedText
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
    val CRT_FG = color(0x00, 0xff, 0x66)
    val CRT_FG_MUTED = color(0x66, 0xff, 0x66)

    fun renderCrt(graphics: GuiGraphics, x: Int, y: Int, width: Int, height: Int) {
        graphics.fill(x, y, x + width, y + height, color(0x28, 0x28, 0x28))
        graphics.fill(x, y + height - 1, x + width, y + height, color(0x15, 0x15, 0x15))
        graphics.fill(x + width - 1, y, x + width, y + height, color(0x15, 0x15, 0x15))
    }

    fun renderSlot(graphics: GuiGraphics, x: Int, y: Int, width: Int = 18, height: Int = 18) {
        graphics.blitWithBorder(WIDGETS_TEXTURE, x, y, 0, 22, width, height, 18, 18, 1)
    }

    fun renderDefault(screen: AbstractContainerScreen<*>, graphics: GuiGraphics) {
        val xOff = screen.guiLeft
        val yOff = screen.guiTop
        graphics.blit(
            GUI_BASE_TEXTURE, xOff, yOff, 0, 0, GUI_BASE_TEXTURE_WIDTH, GUI_BASE_TEXTURE_HEIGHT
        )
        for (slot in screen.getMenu().slots) renderSlot(graphics, xOff + slot.x - 1, yOff + slot.y - 1)
    }

    fun drawWrapping(
        graphics: GuiGraphics, font: Font, s: FormattedText, x: Int, y: Int, width: Int
    ) {
        var y = y
        for (seq in font.split(s, width)) {
            graphics.drawString(font, seq, x, y, 0x00ff66, true)
            y += 9
        }
    }

    fun drawSpriteDuplicated(
        graphics: GuiGraphics, sprite: TextureAtlasSprite, x: Int, y: Int, width: Int, height: Int, color: Int = -1
    ) {
        val spriteWidth = sprite.contents().width()
        val spriteHeight = sprite.contents().height()
        val fullX = width / spriteWidth
        val fullY = height / spriteHeight
        if (fullX > 0 && fullY > 0) {
            for (xOff in 0..<fullX) for (yOff in 0..<fullY) drawSprite(
                graphics, sprite, x + xOff * spriteWidth, y + yOff * spriteHeight, spriteWidth, spriteHeight, color
            )
        }

        val remWidth = width % spriteWidth
        val remHeight = height % spriteHeight
        if (remWidth == 0 && remHeight == 0) return
        val partHoriz = remWidth.toFloat() / spriteWidth
        val partVert = remHeight.toFloat() / spriteHeight
        val endX = x + fullX * spriteWidth
        val endY = y + fullY * spriteHeight
        for (xOff in 0..<fullX) drawSpritePartially(
            graphics, sprite, x + xOff * spriteWidth, endY, spriteWidth, remHeight, 1f, partVert, color
        )
        for (yOff in 0..<fullY) drawSpritePartially(
            graphics, sprite, endX, y + yOff * spriteHeight, remWidth, spriteHeight, partHoriz, 1f, color
        )
        drawSpritePartially(graphics, sprite, endX, endY, remWidth, remHeight, partHoriz, partVert, color)
    }

    private fun drawSpritePartially(
        graphics: GuiGraphics,
        sprite: TextureAtlasSprite,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        partHoriz: Float,
        partVert: Float,
        color: Int
    ) {
        RenderSystem.setShaderTexture(0, sprite.atlasLocation())
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader)
        RenderSystem.enableBlend()
        val pose = graphics.pose().last().pose()
        val bufferBuilder =
            Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        val x2 = x + width
        val y2 = y + height

        val u0 = sprite.u0
        val v0 = sprite.v0
        val u1 = sprite.getU(partHoriz)
        val v1 = sprite.getV(partVert)

        bufferBuilder.addVertex(pose, x.toFloat(), y.toFloat(), 0f).setUv(u0, v0).setColor(color)
        bufferBuilder.addVertex(pose, x.toFloat(), y2.toFloat(), 0f).setUv(u0, v1).setColor(color)
        bufferBuilder.addVertex(pose, x2.toFloat(), y2.toFloat(), 0f).setUv(u1, v1).setColor(color)
        bufferBuilder.addVertex(pose, x2.toFloat(), y.toFloat(), 0f).setUv(u1, v0).setColor(color)
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
        RenderSystem.disableBlend()
    }

    fun drawSprite(
        graphics: GuiGraphics, sprite: TextureAtlasSprite, x: Int, y: Int, width: Int, height: Int, color: Int
    ) {
        RenderSystem.setShaderTexture(0, sprite.atlasLocation())
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader)
        RenderSystem.enableBlend()
        val pose = graphics.pose().last().pose()
        val bufferBuilder =
            Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        val x2 = x + width
        val y2 = y + height

        val u0 = sprite.u0
        val v0 = sprite.v0
        val u1 = sprite.u1
        val v1 = sprite.v1

        bufferBuilder.addVertex(pose, x.toFloat(), y.toFloat(), 0f).setUv(u0, v0).setColor(color)
        bufferBuilder.addVertex(pose, x.toFloat(), y2.toFloat(), 0f).setUv(u0, v1).setColor(color)
        bufferBuilder.addVertex(pose, x2.toFloat(), y2.toFloat(), 0f).setUv(u1, v1).setColor(color)
        bufferBuilder.addVertex(pose, x2.toFloat(), y.toFloat(), 0f).setUv(u1, v0).setColor(color)
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow())
        RenderSystem.disableBlend()
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
    val BLACK: Int = color(0, 0, 0)
    val GRAY: Int = color(0x8b, 0x8b, 0x8b)
    val DARK_GRAY: Int = color(0x37, 0x37, 0x37)
    val ENERGY: Int = color(0x0e, 0xa5, 0xe9)
    val COMPUTATION: Int = color(0x8c, 0x21, 0xff)
    val PROGRESS_RED: Int = color(0xdc, 0x26, 0x26)
}