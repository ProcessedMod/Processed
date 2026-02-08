package redcrafter07.processed.multiblock

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.Util
import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos.blockToSectionCoord
import net.minecraft.core.Vec3i
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.model.data.ModelData
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i

object MultiblockPreview {
    private var shown: Map<BlockPos, Data>? = null
    private var area: Vec3i = Vec3i.ZERO
    private var pos: BlockPos = BlockPos.ZERO
    private var controllerPos: BlockPos = BlockPos.ZERO
    private var lastUpdate = Util.getMillis()

    // Max render distance: 64 blocks
    private const val MAX_DIST = 128 * 128 * 128

    fun update(be: MultiblockBlockEntity?) {
        if (shown != null && be != null && be.blockPos == controllerPos) {
            shown = null
            return
        }
        shown = null
        if (be == null) return
        val blocks = HashMap<BlockPos, Data>()
        val bePos = be.blockPos
        controllerPos = bePos
        be.displayBlocks { pos, states ->
            val states = states.filter { !it.isAir }
            if (!states.isEmpty()) blocks[pos.offset(bePos)] = Data(states, 0)
        }
        var min = bePos
        var max = bePos
        for (pos in blocks.keys) {
            min = BlockPos.min(min, pos)
            max = BlockPos.max(max, pos)
        }
        pos = min
        area = max.subtract(min)
        shown = blocks
    }

    fun removeIfLastDisplayed(controllerPos: BlockPos) {
        if (this.controllerPos == controllerPos) shown = null
    }

    fun display(level: Level, camera: Camera, poseStack: PoseStack) {
        val shown = shown ?: return
        if (!level.hasChunk(blockToSectionCoord(pos.x), blockToSectionCoord(pos.z)) || !level.hasChunk(
                blockToSectionCoord(pos.x + area.x), blockToSectionCoord(pos.z + area.z)
            )
        ) return
        if (camera.position.toVec3i().distSqr(pos) > MAX_DIST) return
        if (Util.getMillis() - lastUpdate > 500) {
            lastUpdate = Util.getMillis()
            shown.values.forEach(Data::increaseOffset)
        }

        var displayedAny = false
        val cam = camera.position

        val buf = Minecraft.getInstance().renderBuffers().bufferSource()

        for (entry in shown) {
            val existing = level.getBlockState(entry.key)
            val shown = entry.value.blocks[entry.value.offset]
            if (!existing.isAir && entry.value.blocks.any { it.`is`(existing.block) || existing.`is`(it.block) }) continue

            displayedAny = true
            poseStack.pushPose()
            poseStack.translate(
                entry.key.x.toDouble() - cam.x, entry.key.y.toDouble() - cam.y, entry.key.z.toDouble() - cam.z
            )
            poseStack.translate(0.15f, 0.15f, 0.15f)
            poseStack.scale(0.7f, 0.7f, 0.7f)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") Minecraft.getInstance().blockRenderer.renderSingleBlock(
                shown, poseStack, buf, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, null
            )
            poseStack.popPose()
        }

        if (!displayedAny) update(null)
    }

    private data class Data(val blocks: List<BlockState>, var offset: Int) {
        fun increaseOffset() {
            offset = (offset + 1) % blocks.size
        }
    }
}