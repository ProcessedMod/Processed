package redcrafter07.processed.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.util.Mth
import redcrafter07.processed.rl

open class RocketEntityRenderer(context: EntityRendererProvider.Context) : EntityRenderer<RocketEntity>(context) {
    val model = RocketEntityModel(context.bakeLayer(RocketEntityModel.LAYER_LOCATION))

    companion object {
        val TEXTURE = rl("textures/entity/rocket.png")
        val TEXTURE_DEBUG = rl("textures/entity/rocket-debug.png")
    }

    override fun getTextureLocation(p0: RocketEntity) =
        if (Minecraft.getInstance().player?.isShiftKeyDown ?: false) TEXTURE_DEBUG else TEXTURE

    override fun render(
        entity: RocketEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int
    ) {
        poseStack.pushPose()
        val yBodyRot = Mth.rotLerp(partialTicks, entity.yRot, entity.yRot)
        val xRot = Mth.lerp(partialTicks, entity.xRotO, entity.xRot)

        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f - yBodyRot))
        poseStack.scale(-1.0f, -1.0f, 1.0f)
        poseStack.translate(0.0f, -1.501f, 0.0f)

        model.setupAnim(entity, 0f, 0f, 0f, 0f, xRot)
        val renderType = model.renderType(getTextureLocation(entity))
        if (renderType != null) {
            val vertexConsumer: VertexConsumer = bufferSource.getBuffer(renderType)
            val i = OverlayTexture.pack(0f, false)
            this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, i, -1)
        }
        poseStack.popPose()
    }
}