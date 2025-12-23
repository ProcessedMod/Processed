package redcrafter07.processed.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import redcrafter07.processed.rl


// Made with Blockbench 5.0.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


class RocketEntityModel(root: ModelPart) : EntityModel<RocketEntity>() {
    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION = ModelLayerLocation(rl("rocket"), "main")

        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition()
            val partDefinition = meshDefinition.root

            partDefinition.addOrReplaceChild(
                "engine",
                CubeListBuilder.create().texOffs(72, 1)
                    .addBox(-3.0f, -6.0f, -3.0f, 6.0f, 3.0f, 6.0f, CubeDeformation(0.0f)).texOffs(81, 11)
                    .addBox(-2.0f, -8.0f, -2.0f, 4.0f, 2.0f, 4.0f, CubeDeformation(0.0f)).texOffs(56, 11)
                    .addBox(-4.0f, -3.0f, -4.0f, 8.0f, 4.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 14.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-7.0f, -39.0f, -7.0f, 14.0f, 35.0f, 14.0f, CubeDeformation(0.0f)).texOffs(42, 0)
                    .addBox(-5.0f, -4.0f, -5.0f, 10.0f, 1.0f, 10.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 9.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "engineLeft",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(-4.0f, -12.0f, -4.0f, 8.0f, 29.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 66)
                    .addBox(0.0f, -18.0f, -4.0f, 6.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 56)
                    .addBox(-2.0f, -16.0f, -4.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 76)
                    .addBox(-4.0f, -14.0f, -4.0f, 9.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(8, 109)
                    .addBox(4.0f, 1.0f, -2.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).texOffs(24, 86)
                    .addBox(-3.0f, 18.0f, -3.0f, 6.0f, 2.0f, 6.0f, CubeDeformation(0.0f)).texOffs(24, 52)
                    .addBox(-2.0f, 17.0f, -2.0f, 4.0f, 1.0f, 4.0f, CubeDeformation(0.0f)).texOffs(0, 86)
                    .addBox(-4.0f, 20.0f, -4.0f, 8.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(-13.0f, -3.0f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "engineRight",
                CubeListBuilder.create().texOffs(0, 49)
                    .addBox(7.0f, -12.0f, -4.0f, 8.0f, 29.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 66)
                    .addBox(5.0f, -18.0f, -4.0f, 6.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 56)
                    .addBox(5.0f, -16.0f, -4.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(32, 76)
                    .addBox(6.0f, -14.0f, -4.0f, 9.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(8, 109)
                    .addBox(5.0f, 1.0f, -2.0f, 2.0f, 2.0f, 2.0f, CubeDeformation(0.0f)).texOffs(24, 52)
                    .addBox(9.0f, 17.0f, -2.0f, 4.0f, 1.0f, 4.0f, CubeDeformation(0.0f)).texOffs(24, 86)
                    .addBox(8.0f, 18.0f, -3.0f, 6.0f, 2.0f, 6.0f, CubeDeformation(0.0f)).texOffs(0, 86)
                    .addBox(7.0f, 20.0f, -4.0f, 8.0f, 3.0f, 8.0f, CubeDeformation(0.0f)),
                PartPose.offset(2.0f, -3.0f, 0.0f)
            )

            val stands =
                partDefinition.addOrReplaceChild("stands", CubeListBuilder.create(), PartPose.offset(13.0f, 6.0f, 0.0f))

            val stand4 = stands.addOrReplaceChild(
                "stand4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0f, 0.0f, 0.0f, 0.0f, -1.5708f, 0.0f)
            )

            stand4.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 97)
                    .addBox(-1.0f, -1.0f, -4.0f, 2.0f, 19.0f, 2.0f, CubeDeformation(0.0f)).texOffs(3, 113)
                    .addBox(-1.0f, -1.0f, -2.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(5.0f, 0.0f, -5.0f, 0.0f, -0.7854f, 0.0f)
            )

            val stand3 =
                stands.addOrReplaceChild("stand3", CubeListBuilder.create(), PartPose.offset(3.0f, 0.0f, -5.0f))

            stand3.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 97)
                    .addBox(-1.0f, -1.0f, -4.0f, 2.0f, 19.0f, 2.0f, CubeDeformation(0.0f)).texOffs(3, 113)
                    .addBox(-1.0f, -1.0f, -2.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(2.0f, 0.0f, 0.0f, 0.0f, -0.7854f, 0.0f)
            )

            val stand1 =
                stands.addOrReplaceChild("stand1", CubeListBuilder.create(), PartPose.offset(-29.0f, 0.0f, -5.0f))

            stand1.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 97)
                    .addBox(-1.0f, -1.0f, -4.0f, 2.0f, 19.0f, 2.0f, CubeDeformation(0.0f)).texOffs(3, 113)
                    .addBox(-1.0f, -1.0f, -2.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-2.0f, 0.0f, 0.0f, 0.0f, 0.7854f, 0.0f)
            )

            val stand2 = stands.addOrReplaceChild(
                "stand2", CubeListBuilder.create(), PartPose.offsetAndRotation(-26.0f, 0.0f, 0.0f, 0.0f, 1.5708f, 0.0f)
            )

            stand2.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 97)
                    .addBox(-1.0f, -1.0f, -4.0f, 2.0f, 19.0f, 2.0f, CubeDeformation(0.0f)).texOffs(3, 113)
                    .addBox(-1.0f, -1.0f, -2.0f, 2.0f, 2.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offsetAndRotation(-5.0f, 0.0f, -5.0f, 0.0f, 0.7854f, 0.0f)
            )

            partDefinition.addOrReplaceChild(
                "drill",
                CubeListBuilder.create().texOffs(112, 123)
                    .addBox(-2.0f, -51.0f, -2.0f, 4.0f, 1.0f, 4.0f, CubeDeformation(0.0f)).texOffs(72, 117)
                    .addBox(-5.0f, -52.0f, -5.0f, 10.0f, 1.0f, 10.0f, CubeDeformation(0.0f)).texOffs(80, 103)
                    .addBox(-6.0f, -53.0f, -6.0f, 12.0f, 1.0f, 12.0f, CubeDeformation(0.0f)).texOffs(88, 91)
                    .addBox(-5.0f, -55.0f, -5.0f, 10.0f, 2.0f, 10.0f, CubeDeformation(0.0f)).texOffs(50, 117)
                    .addBox(-4.0f, -57.0f, -4.0f, 8.0f, 2.0f, 8.0f, CubeDeformation(0.0f)).texOffs(102, 116)
                    .addBox(-3.0f, -58.0f, -3.0f, 6.0f, 1.0f, 6.0f, CubeDeformation(0.0f)).texOffs(120, 110)
                    .addBox(-1.0f, -59.0f, -1.0f, 2.0f, 1.0f, 2.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 20.0f, 0.0f)
            )

            return LayerDefinition.create(meshDefinition, 128, 128)
        }
    }

    val engine: ModelPart = root.getChild("engine")
    val body: ModelPart = root.getChild("body")
    val engineLeft: ModelPart = root.getChild("engineLeft")
    val engineRight: ModelPart = root.getChild("engineRight")
    val stands: ModelPart = root.getChild("stands")
    val drill: ModelPart = root.getChild("drill")

    override fun setupAnim(entity: RocketEntity, p1: Float, p2: Float, p3: Float, p4: Float, p5: Float) {
        stands.visible = entity.hasStands
    }

    override fun renderToBuffer(
        poseStack: PoseStack, vertexConsumer: VertexConsumer, packedLight: Int, packedOverlay: Int, color: Int
    ) {
        engine.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        engineLeft.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        engineRight.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        if (stands.visible) stands.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
        drill.render(poseStack, vertexConsumer, packedLight, packedOverlay, color)
    }
}