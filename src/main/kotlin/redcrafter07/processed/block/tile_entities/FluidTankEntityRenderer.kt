package redcrafter07.processed.block.tile_entities

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import redcrafter07.processed.gui.RenderUtils

object FluidTankEntityRenderer : BlockEntityRenderer<FluidTankBlockEntity> {
    override fun render(
        blockEntity: FluidTankBlockEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val fluid = blockEntity.fluidHandler.getFluidInTank(0)
        if (fluid.isEmpty) return
        val sprite = RenderUtils.getFluidTexture(fluid, false)
        val color = RenderUtils.getFluidColor(fluid)
        val alpha = 1f
        val red = (color shr 16 and 0xff) / 255.0f
        val green = (color shr 8 and 0xff) / 255.0f
        val blue = (color and 0xff) / 255.0f
        val height = 0.75f * blockEntity.size + 0.125f
        val buffer = bufferSource.getBuffer(RenderType.translucent())

        poseStack.pushPose()
        poseStack.translate(0.0, 0.0, 0.0)

        val xzMin = 3f / 16f
        val xzMax = 13f / 16f
        val yMin = 2f / 16f

        val uMin = sprite.getU(xzMin)
        val uMax = sprite.getU(xzMax)
        val vMin = sprite.getV(xzMin)
        val vMax = sprite.getV(xzMax)

        RenderUtils.renderCube(
            buffer,
            poseStack,
            xzMax,
            xzMin,
            yMin,
            height,
            xzMin,
            xzMax,
            uMin,
            uMax,
            vMin,
            vMax,
            red,
            green,
            blue,
            alpha,
            packedLight,
            packedOverlay
        )

        poseStack.popPose()
    }
}