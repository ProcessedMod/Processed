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
            val root = meshDefinition.root

            root.addOrReplaceChild(
                "engine",
                CubeListBuilder.create().texOffs(-6, -4)
                    .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 3.0F, 6.0F, CubeDeformation(0.0F)).texOffs(-9, -6)
                    .addBox(-4.0F, -3.0F, -4.0F, 8.0F, 4.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-3, -2)
                    .addBox(-2.0F, -8.0F, -2.0F, 4.0F, 2.0F, 4.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 14.0F, 0.0F)
            )

            root.addOrReplaceChild(
                "body",
                CubeListBuilder.create().texOffs(-18, -12)
                    .addBox(-7.0F, -39.0F, -7.0F, 14.0F, 35.0F, 14.0F, CubeDeformation(0.0F)).texOffs(-12, -8)
                    .addBox(-5.0F, -4.0F, -5.0F, 10.0F, 1.0F, 10.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 9.0F, 0.0F)
            )

            root.addOrReplaceChild(
                "engineLeft",
                CubeListBuilder.create().texOffs(-12, -6)
                    .addBox(-4.0F, -12.0F, -4.0F, 8.0F, 29.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-10, -6)
                    .addBox(0.0F, -18.0F, -4.0F, 6.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-12, -6)
                    .addBox(-2.0F, -16.0F, -4.0F, 8.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-14, -6)
                    .addBox(-4.0F, -14.0F, -4.0F, 9.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-1, 0)
                    .addBox(4.0F, 1.0F, -2.0F, 2.0F, 2.0F, 2.0F, CubeDeformation(0.0F)).texOffs(-6, -4)
                    .addBox(-3.0F, 18.0F, -3.0F, 6.0F, 2.0F, 6.0F, CubeDeformation(0.0F)).texOffs(-3, -2)
                    .addBox(-2.0F, 17.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation(0.0F)).texOffs(-9, -6)
                    .addBox(-4.0F, 20.0F, -4.0F, 8.0F, 3.0F, 8.0F, CubeDeformation(0.0F)),
                PartPose.offset(-13.0F, -3.0F, 0.0F)
            )

            root.addOrReplaceChild(
                "engineRight",
                CubeListBuilder.create().texOffs(-12, -6).mirror()
                    .addBox(7.0F, -12.0F, -4.0F, 8.0F, 29.0F, 8.0F, CubeDeformation(0.0F)).mirror(false).texOffs(-9, -6)
                    .mirror().addBox(5.0F, -18.0F, -4.0F, 6.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).mirror(false)
                    .texOffs(-11, -6).mirror().addBox(5.0F, -16.0F, -4.0F, 8.0F, 2.0F, 8.0F, CubeDeformation(0.0F))
                    .mirror(false).texOffs(-13, -6).mirror()
                    .addBox(6.0F, -14.0F, -4.0F, 9.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).mirror(false).texOffs(-1, 0)
                    .addBox(5.0F, 1.0F, -2.0F, 2.0F, 2.0F, 2.0F, CubeDeformation(0.0F)).texOffs(-3, -2)
                    .addBox(9.0F, 17.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation(0.0F)).texOffs(-6, -4)
                    .addBox(8.0F, 18.0F, -3.0F, 6.0F, 2.0F, 6.0F, CubeDeformation(0.0F)).texOffs(-9, -6)
                    .addBox(7.0F, 20.0F, -4.0F, 8.0F, 3.0F, 8.0F, CubeDeformation(0.0F)),
                PartPose.offset(2.0F, -3.0F, 0.0F)
            )

            val stands = root.addOrReplaceChild(
                "stands", CubeListBuilder.create(), PartPose.offset(13.0F, 6.0F, 0.0F)
            )

            val stand1 =
                stands.addOrReplaceChild("stand1", CubeListBuilder.create(), PartPose.offset(-29.0F, 0.0F, -5.0F))

            stand1.addOrReplaceChild(
                "cube_r3",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 19.0F, 2.0F, CubeDeformation(0.0F)).texOffs(-3, -3)
                    .addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 5.0F, CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-2.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
            )

            val stand2 = stands.addOrReplaceChild(
                "stand2", CubeListBuilder.create(), PartPose.offsetAndRotation(-26.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F)
            )

            stand2.addOrReplaceChild(
                "cube_r4",
                CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 19.0F, 2.0F, CubeDeformation(0.0F)).texOffs(-3, -3)
                    .addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 5.0F, CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-5.0F, 0.0F, -5.0F, 0.0F, 0.7854F, 0.0F)
            )

            val stand4 = stands.addOrReplaceChild(
                "stand4", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F)
            )

            stand4.addOrReplaceChild(
                "cube_r1",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 19.0F, 2.0F, CubeDeformation(0.0F)).mirror(false).texOffs(-3, -3)
                    .mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 5.0F, CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(5.0F, 0.0F, -5.0F, 0.0F, -0.7854F, 0.0F)
            )

            val stand3 =
                stands.addOrReplaceChild("stand3", CubeListBuilder.create(), PartPose.offset(3.0F, 0.0F, -5.0F))

            stand3.addOrReplaceChild(
                "cube_r2",
                CubeListBuilder.create().texOffs(0, 0).mirror()
                    .addBox(-1.0F, -1.0F, -4.0F, 2.0F, 19.0F, 2.0F, CubeDeformation(0.0F)).mirror(false).texOffs(-3, -3)
                    .mirror().addBox(-1.0F, -1.0F, -2.0F, 2.0F, 2.0F, 5.0F, CubeDeformation(0.0F)).mirror(false),
                PartPose.offsetAndRotation(2.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
            )

            root.addOrReplaceChild(
                "drill",
                CubeListBuilder.create().texOffs(-3, -2)
                    .addBox(-2.0F, -51.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation(0.0F)).texOffs(-12, -8)
                    .addBox(-5.0F, -52.0F, -5.0F, 10.0F, 1.0F, 10.0F, CubeDeformation(0.0F)).texOffs(-15, -10)
                    .addBox(-6.0F, -53.0F, -6.0F, 12.0F, 1.0F, 12.0F, CubeDeformation(0.0F)).texOffs(-12, -8)
                    .addBox(-5.0F, -55.0F, -5.0F, 10.0F, 2.0F, 10.0F, CubeDeformation(0.0F)).texOffs(-9, -6)
                    .addBox(-4.0F, -57.0F, -4.0F, 8.0F, 2.0F, 8.0F, CubeDeformation(0.0F)).texOffs(-6, -4)
                    .addBox(-3.0F, -58.0F, -3.0F, 6.0F, 1.0F, 6.0F, CubeDeformation(0.0F)).texOffs(0, 0)
                    .addBox(-1.0F, -59.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 20.0F, 0.0F)
            )

            return LayerDefinition.create(meshDefinition, 64, 64)
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