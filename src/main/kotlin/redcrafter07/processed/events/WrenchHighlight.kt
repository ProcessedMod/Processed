package redcrafter07.processed.events

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.core.Direction
import net.minecraft.util.Mth
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderHighlightEvent
import org.joml.Matrix4f
import org.joml.Quaternionf
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.cable.CableBlockEntity
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.rl

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME)
object WrenchHighlight {
    val UP: Matrix4f
    val DOWN: Matrix4f
    val NORTH: Matrix4f
    val SOUTH: Matrix4f
    val WEST: Matrix4f
    val EAST: Matrix4f
    init {
        val pose = PoseStack()
        pose.translate(0f, 1f, 0f)
        UP = pose.last().pose()
    }
    init {
        val pose = PoseStack()
        val quaternion = Quaternionf()
        quaternion.rotateX(Mth.PI)
        pose.mulPose(quaternion)
        pose.translate(0f, 0f, -1f)
        DOWN = pose.last().pose()
    }
    init {
        val pose = PoseStack()
        val quaternion = Quaternionf()
        quaternion.rotateY(Mth.HALF_PI)
        quaternion.rotateZ(Mth.HALF_PI)
        quaternion.rotateX(Mth.PI)
        pose.mulPose(quaternion)
        pose.translate(0f, 0f, -1f)
        NORTH = pose.last().pose()
    }
    init {
        val pose = PoseStack()
        val quaternion = Quaternionf()
        quaternion.rotateY(Mth.HALF_PI)
        quaternion.rotateZ(Mth.HALF_PI)
        pose.mulPose(quaternion)
        pose.translate(0f, 1f, 0f)
        SOUTH = pose.last().pose()
    }
    init {
        val pose = PoseStack()
        val quaternion = Quaternionf()
        quaternion.rotateZ(Mth.HALF_PI)
        pose.mulPose(quaternion)
        WEST = pose.last().pose()
    }
    init {
        val pose = PoseStack()
        val quaternion = Quaternionf()
        quaternion.rotateZ(-Mth.HALF_PI)
        pose.mulPose(quaternion)
        pose.translate(-1f, 1f, 0f)
        EAST = pose.last().pose()
    }

    @SubscribeEvent
    fun onHighlight(e: RenderHighlightEvent.Block) {
        val player = Minecraft.getInstance().player ?: return
        if (!player.mainHandItem.`is`(ModItems.WRENCH)) return

        val level = player.level() ?: return
        val pos = e.target.blockPos
        val be = level.getBlockEntity(pos) ?: return
        if (be !is CableBlockEntity) return
        e.poseStack.pushPose()

        val pose = e.poseStack.last()
        val consumer = e.multiBufferSource.getBuffer(RenderType.text(rl("textures/block/cable.png")))
        val x = pos.x - e.camera.position.x
        val y = pos.y - e.camera.position.y
        val z = pos.z - e.camera.position.z
        e.poseStack.translate(x, y, z)

        when(e.target.direction) {
            Direction.UP -> e.poseStack.mulPose(UP)
            Direction.DOWN -> e.poseStack.mulPose(DOWN)
            Direction.NORTH -> e.poseStack.mulPose(NORTH)
            Direction.SOUTH -> e.poseStack.mulPose(SOUTH)
            Direction.WEST -> e.poseStack.mulPose(WEST)
            Direction.EAST -> e.poseStack.mulPose(EAST)
        }

        consumer.addVertex(pose, 1f, 0f, 0f).setUv(0f, 0f).setUv2(0, 0).setColor(-1)
        consumer.addVertex(pose, 0f, 0f, 0f).setUv(0f, 1f).setUv2(0, 0).setColor(-1)
        consumer.addVertex(pose, 0f, 0f, 1f).setUv(1f, 1f).setUv2(0, 0).setColor(-1)
        consumer.addVertex(pose, 1f, 0f, 1f).setUv(1f, 0f).setUv2(0, 0).setColor(-1)

        e.poseStack.popPose()
    }
}