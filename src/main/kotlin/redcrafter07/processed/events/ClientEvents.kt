package redcrafter07.processed.events

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Containers
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.chunk.status.ChunkStatus
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderHighlightEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import redcrafter07.processed.Attachments
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.BlockProperties
import redcrafter07.processed.covers.Cover
import redcrafter07.processed.covers.CoverAttachment
import redcrafter07.processed.gui.RenderUtils
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.multiblock.MultiblockPreview
import redcrafter07.processed.network.RPCFunctions
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import java.util.function.Function

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.CLIENT])
object ClientEvents {
    const val DELTA = -0.004f

    // 0 | (10 << 16)  (from OverlayTexture)
    const val OVERLAY = 655360

    @SubscribeEvent
    fun onRender(ev: RenderLevelStageEvent) {
        if (ev.stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            val level = Minecraft.getInstance().level ?: return
            MultiblockPreview.display(level, ev.camera, ev.poseStack)
        } else if (ev.stage == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
            val mc = Minecraft.getInstance()
            val level = mc.level ?: return
            val p = mc.player ?: return

            val playerChunk = ChunkPos(p.blockPosition())
            val renderDist = mc.options.effectiveRenderDistance
            val start = ChunkPos(playerChunk.x - renderDist, playerChunk.z - renderDist)
            val cam = ev.camera.position
            val pose = ev.poseStack
            val blockRenderer = mc.blockRenderer
            val buffer = mc.renderBuffers().bufferSource()
            for (xo in 0..<renderDist * 2) {
                for (zo in 0..<renderDist * 2) {
                    val c = level.getChunk(start.x + xo, start.z + zo, ChunkStatus.FULL, false) ?: continue
                    val data = c.getExistingData(Attachments.COVERS_CLIENT)
                    if (data.isEmpty) continue
                    for ((k, v) in data.get().map) {
                        val k = CoverAttachment.unpack(k, c.pos)
                        pose.pushPose()
                        pose.translate(k.second.x - cam.x, k.second.y - cam.y, k.second.z - cam.z)

//                        val light = v.position.offset(0, 1, 0)
//                        val block = level.lightEngine.getLayerListener(LightLayer.BLOCK).getLightValue(light)
//                        val sky = level.lightEngine.getLayerListener(LightLayer.SKY).getLightValue(light)
                        val state = v.block.defaultBlockState().setValue(BlockProperties.FACING, k.first)
                        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") blockRenderer.renderSingleBlock(
                            state, pose, buffer, LightTexture.FULL_SKY, OVERLAY, ModelData.EMPTY, null
                        )

                        pose.popPose()
                    }
                }
            }
        }
    }

    val LOOKUP = listOf(
        listOf(
            // we look up
            Direction.NORTH,
            Direction.SOUTH,
            Direction.DOWN,
            Direction.UP,
            Direction.WEST,
            Direction.EAST,
        ),
        listOf(
            // we look down
            Direction.NORTH,
            Direction.SOUTH,
            Direction.UP,
            Direction.DOWN,
            Direction.EAST,
            Direction.WEST,
        ),
        listOf(
            // we look south
            Direction.DOWN,
            Direction.UP,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.EAST,
            Direction.WEST,
        ),
        listOf(
            // we look north
            Direction.DOWN,
            Direction.UP,
            Direction.SOUTH,
            Direction.NORTH,
            Direction.WEST,
            Direction.EAST,
        ),
        listOf(
            // we look east
            Direction.DOWN,
            Direction.UP,
            Direction.WEST,
            Direction.EAST,
            Direction.NORTH,
            Direction.SOUTH,
        ),
        listOf(
            // we look west
            Direction.DOWN,
            Direction.UP,
            Direction.EAST,
            Direction.WEST,
            Direction.SOUTH,
            Direction.NORTH,
        ),
    )

    private fun getCoverSprites(
        atlas: Function<ResourceLocation, TextureAtlasSprite>, level: Level, dir: Direction, pos: BlockPos
    ): Array<TextureAtlasSprite?>? {
        val chunk = level.getChunk(pos)
        val data = chunk.getExistingData(Attachments.COVERS_CLIENT)
        if (data.isEmpty) return null
        val packed = CoverAttachment.pack(pos, chunk.pos)
        if (packed == -1) return null
        val textures = Direction.entries.map { LOOKUP[dir.get3DDataValue()][it.get3DDataValue()] }
            .map { data.get().map[it.get3DDataValue() or packed] }
            .map { if (it != null) atlas.apply(it.textureLocation.value) else null }

        return arrayOf(
            textures[3], textures[0], textures[3],
            textures[4], textures[2], textures[5],
            textures[3], textures[1], textures[3],
        )
    }

    @SubscribeEvent
    fun onClick(ev: PlayerInteractEvent.RightClickBlock) {
        val level = ev.level
        if (level.isClientSide || level !is ServerLevel) return

        if (!ev.entity.isShiftKeyDown) return
        val isWrench = ev.itemStack.`is`(ModItems.WRENCH)
        if (!ev.itemStack.isEmpty && !isWrench) return


        val c = ev.level.getChunk(ev.pos)
        val data = c.getExistingData(Attachments.COVERS)
        if (data.isEmpty) return
        var hitThird = hitThird(ev.hitVec)
        if ((ev.hitVec.direction.axisDirection == Direction.AxisDirection.POSITIVE) == (ev.hitVec.direction.axis != Direction.Axis.Z)) hitThird =
            Pair(2 - hitThird.first, hitThird.second)
        val dir =
            LOOKUP[ev.hitVec.direction.get3DDataValue()][thirdToDir[hitThird.first + hitThird.second * 3].get3DDataValue()]
        val cover = data.get().map[CoverAttachment.pack(dir, ev.pos, c.pos)] ?: return
        ev.isCanceled = true


        if (isWrench) {
            data.get().removeCover(ev.pos, c.pos, dir) {
                if (ev.entity.isCreative) return@removeCover
                for (item in it) Containers.dropItemStack(
                    level, ev.pos.x.toDouble(), ev.pos.y.toDouble(), ev.pos.z.toDouble(), item
                )
            }
            for (p in level.chunkSource.chunkMap.getPlayers(
                c.pos, false
            )) RPCFunctions.singleCoverRemoved.sendToClient(p, ev.pos, dir)
        } else Cover.openMenu(ev.entity, cover)
    }

    val thirdToDir = arrayOf(
        Direction.SOUTH, Direction.DOWN, Direction.SOUTH,
        Direction.WEST, Direction.NORTH, Direction.EAST,
        Direction.SOUTH, Direction.UP, Direction.SOUTH,
    )

    @SubscribeEvent
    fun onRender(ev: RenderHighlightEvent.Block) {
        val mc = Minecraft.getInstance() ?: return
        val p = mc.player ?: return
        if (!p.isShiftKeyDown) return
        val level = mc.level ?: return

        val x = ev.target.blockPos.x - ev.camera.position.x
        val y = ev.target.blockPos.y - ev.camera.position.y
        val z = ev.target.blockPos.z - ev.camera.position.z

        val atlas = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS)

        val sprites = getCoverSprites(atlas, level, ev.target.direction, ev.target.blockPos) ?: return

        if ((ev.target.direction.axisDirection == Direction.AxisDirection.POSITIVE) == (ev.target.direction.axis != Direction.Axis.Z)) {
            val v = sprites[3]
            sprites[3] = sprites[5]
            sprites[5] = v
        }

        val buffer = ev.multiBufferSource.getBuffer(RenderType.TRANSLUCENT)

        ev.poseStack.pushPose()
        ev.poseStack.translate(x, y, z)

        val positions: FloatArray
        val uvs: FloatArray

        when (ev.target.direction) {
            Direction.NORTH -> {
                positions = floatArrayOf(
                    0f, 0f, DELTA,
                    0f, 1f, DELTA,
                    1f, 1f, DELTA,
                    1f, 0f, DELTA,
                )
                uvs = floatArrayOf(1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f)
            }

            Direction.WEST -> {
                positions = floatArrayOf(DELTA, 1f, 1f, DELTA, 1f, 0f, DELTA, 0f, 0f, DELTA, 0f, 1f)
                uvs = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 1f, 1f)
            }

            Direction.DOWN -> {
                positions = floatArrayOf(1f, DELTA, 0f, 1f, DELTA, 1f, 0f, DELTA, 1f, 0f, DELTA, 0f)
                uvs = floatArrayOf(1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f)
            }

            Direction.SOUTH -> {
                positions = floatArrayOf(
                    0f, 1f, DELTA,
                    0f, 0f, DELTA,
                    1f, 0f, DELTA,
                    1f, 1f, DELTA,
                )
                uvs = floatArrayOf(
                    0f, 0f,
                    0f, 1f,
                    1f, 1f,
                    1f, 0f,
                )
            }

            Direction.EAST -> {
                positions = floatArrayOf(DELTA, 0f, 1f, DELTA, 0f, 0f, DELTA, 1f, 0f, DELTA, 1f, 1f)
                uvs = floatArrayOf(0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f)
            }

            Direction.UP -> {
                positions = floatArrayOf(0f, DELTA, 0f, 0f, DELTA, 1f, 1f, DELTA, 1f, 1f, DELTA, 0f)
                uvs = floatArrayOf(1f, 1f, 1f, 0f, 0f, 0f, 0f, 1f)
            }
        }
        when (ev.target.direction) {
            Direction.NORTH, Direction.WEST, Direction.DOWN -> {}
            Direction.SOUTH -> {
                ev.poseStack.translate(0f, 0f, 1.009f)
            }

            Direction.EAST -> {
                ev.poseStack.translate(1.008f, 0f, 0f)
            }

            Direction.UP -> {
                ev.poseStack.translate(0f, 1.008f, 0f)
            }
        }

        ev.poseStack.scale(1 / 3f, 1 / 3f, 1 / 3f)

        val xy = hitThird(ev.target)

        for (y in 0..<3) {
            ev.poseStack.pushPose()
            for (x in 0..<3) {
                val sprite = sprites[y * 3 + x]
                if (sprite != null) drawBox(
                    buffer,
                    ev.poseStack.last(),
                    positions,
                    uvs,
                    if (x == xy.first && y == xy.second) -1 else COL_DARK,
                    sprite
                )
                moveRight(ev.poseStack, ev.target.direction)

            }
            ev.poseStack.popPose()
            moveUp(ev.poseStack, ev.target.direction)
        }

        mc.renderBuffers().bufferSource().endBatch(RenderType.TRANSLUCENT)
        ev.poseStack.popPose()
    }

    private fun hitThird(target: BlockHitResult): Pair<Int, Int> {
        val pos = target.location.subtract(target.blockPos.toVec3()).scale(3.0)
        return when (target.direction) {
            Direction.DOWN -> Pair(pos.x.toInt(), pos.z.toInt())
            Direction.UP -> Pair(pos.x.toInt(), pos.z.toInt())
            Direction.NORTH -> Pair(pos.x.toInt(), pos.y.toInt())
            Direction.SOUTH -> Pair(pos.x.toInt(), pos.y.toInt())
            Direction.WEST -> Pair(pos.z.toInt(), pos.y.toInt())
            Direction.EAST -> Pair(pos.z.toInt(), pos.y.toInt())
        }
    }

    private val COL_DARK = RenderUtils.color(0x80, 0x80, 0x80)

    private fun moveRight(poseStack: PoseStack, dir: Direction) {
        when (dir.axis) {
            Direction.Axis.X -> poseStack.translate(0f, 0f, 1f)
            Direction.Axis.Y -> poseStack.translate(1f, 0f, 0f)
            Direction.Axis.Z -> poseStack.translate(1f, 0f, 0f)
        }
    }

    private fun moveUp(poseStack: PoseStack, dir: Direction) {
        when (dir.axis) {
            Direction.Axis.X -> poseStack.translate(0f, 1f, 0f)
            Direction.Axis.Y -> poseStack.translate(0f, 0f, 1f)
            Direction.Axis.Z -> poseStack.translate(0f, 1f, 0f)
        }
    }

    private fun drawBox(
        buf: VertexConsumer,
        pose: PoseStack.Pose,
        positions: FloatArray,
        uvs: FloatArray,
        col: Int,
        sprite: TextureAtlasSprite
    ) {
        val u = sprite.u0
        val v = sprite.v0
        val uo = sprite.u1 - sprite.u0
        val vo = sprite.v1 - sprite.v0

        val light = LightTexture.FULL_BRIGHT
        buf.addVertex(pose, positions[0], positions[1], positions[2]).setColor(col)
            .setUv(u + uvs[0] * uo, v + uvs[1] * vo).setLight(light).setNormal(0f, 0f, 0f)
        buf.addVertex(pose, positions[3], positions[4], positions[5]).setColor(col)
            .setUv(u + uvs[2] * uo, v + uvs[3] * vo).setLight(light).setNormal(0f, 0f, 0f)
        buf.addVertex(pose, positions[6], positions[7], positions[8]).setColor(col)
            .setUv(u + uvs[4] * uo, v + uvs[5] * vo).setLight(light).setNormal(0f, 0f, 0f)
        buf.addVertex(pose, positions[9], positions[10], positions[11]).setColor(col)
            .setUv(u + uvs[6] * uo, v + uvs[7] * vo).setLight(light).setNormal(0f, 0f, 0f)
    }
}