package redcrafter07.processed.transmitters

import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.block.model.ItemTransform
import net.minecraft.client.renderer.block.model.ItemTransforms
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.ChunkRenderTypeSet
import net.neoforged.neoforge.client.model.IDynamicBakedModel
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer
import org.joml.Vector3f
import redcrafter07.processed.transmitters.TransmitterBlockEntity.Companion.Connected
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus

class TransmitterBakedModel(center: ResourceLocation, side: ResourceLocation, val s: Double) : IDynamicBakedModel {
    val e = 1 - s

    val center = lazy { Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(center) }
    val side = lazy { Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(side) }

    override fun getQuads(
        p0: BlockState?, p1: Direction?, p2: RandomSource, modelData: ModelData, p4: RenderType?
    ): List<BakedQuad> {
        val quads = ArrayList<BakedQuad>()
        val connected = modelData.get(TransmitterBlockEntity.TRANSMITTER_PROPERTY) ?: itemConnected

        val center = center.value
        val side = side.value

        if (connected[Direction.UP]) {
            quads.add(quad(v(e, 1, s), v(e, 1, e), v(e, e, e), v(e, e, s), side))
            quads.add(quad(v(s, 1, e), v(s, 1, s), v(s, e, s), v(s, e, e), side))
            quads.add(quad(v(s, 1, s), v(e, 1, s), v(e, e, s), v(s, e, s), side))
            quads.add(quad(v(s, e, e), v(e, e, e), v(e, 1, e), v(s, 1, e), side))
        } else quads.add(quad(v(s, e, e), v(e, e, e), v(e, e, s), v(s, e, s), center))

        if (connected[Direction.DOWN]) {
            quads.add(quad(v(e, s, s), v(e, s, e), v(e, 0, e), v(e, 0, s), side))
            quads.add(quad(v(s, s, e), v(s, s, s), v(s, 0, s), v(s, 0, e), side))
            quads.add(quad(v(s, s, s), v(e, s, s), v(e, 0, s), v(s, 0, s), side))
            quads.add(quad(v(s, 0, e), v(e, 0, e), v(e, s, e), v(s, s, e), side))
        } else quads.add(quad(v(s, s, s), v(e, s, s), v(e, s, e), v(s, s, e), center))

        if (connected[Direction.EAST]) {
            quads.add(quad(v(1, e, e), v(1, e, s), v(e, e, s), v(e, e, e), side))
            quads.add(quad(v(1, s, s), v(1, s, e), v(e, s, e), v(e, s, s), side))
            quads.add(quad(v(1, e, s), v(1, s, s), v(e, s, s), v(e, e, s), side))
            quads.add(quad(v(1, s, e), v(1, e, e), v(e, e, e), v(e, s, e), side))
        } else quads.add(quad(v(e, s, s), v(e, e, s), v(e, e, e), v(e, s, e), center))

        if (connected[Direction.WEST]) {
            quads.add(quad(v(s, e, e), v(s, e, s), v(0, e, s), v(0, e, e), side))
            quads.add(quad(v(s, s, s), v(s, s, e), v(0, s, e), v(0, s, s), side))
            quads.add(quad(v(s, e, s), v(s, s, s), v(0, s, s), v(0, e, s), side))
            quads.add(quad(v(s, s, e), v(s, e, e), v(0, e, e), v(0, s, e), side))
        } else quads.add(quad(v(s, s, e), v(s, e, e), v(s, e, s), v(s, s, s), center))

        if (connected[Direction.NORTH]) {
            quads.add(quad(v(s, e, s), v(e, e, s), v(e, e, 0), v(s, e, 0), side))
            quads.add(quad(v(s, s, 0), v(e, s, 0), v(e, s, s), v(s, s, s), side))
            quads.add(quad(v(e, s, 0), v(e, e, 0), v(e, e, s), v(e, s, s), side))
            quads.add(quad(v(s, s, s), v(s, e, s), v(s, e, 0), v(s, s, 0), side))
        } else quads.add(quad(v(s, e, s), v(e, e, s), v(e, s, s), v(s, s, s), center))

        if (connected[Direction.SOUTH]) {
            quads.add(quad(v(s, e, 1), v(e, e, 1), v(e, e, e), v(s, e, e), side))
            quads.add(quad(v(s, s, e), v(e, s, e), v(e, s, 1), v(s, s, 1), side))
            quads.add(quad(v(e, s, e), v(e, e, e), v(e, e, 1), v(e, s, 1), side))
            quads.add(quad(v(s, s, 1), v(s, e, 1), v(s, e, e), v(s, s, e), side))
        } else quads.add(quad(v(s, s, e), v(e, s, e), v(e, e, e), v(s, e, e), center))

        return quads
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getTransforms(): ItemTransforms = itemTransforms

    override fun useAmbientOcclusion() = true
    override fun isGui3d() = false
    override fun usesBlockLight() = false
    override fun isCustomRenderer() = false

    @Suppress("OVERRIDE_DEPRECATION")
    override fun getParticleIcon(): TextureAtlasSprite = center.value
    override fun getRenderTypes(itemStack: ItemStack, fabulous: Boolean): List<RenderType> = listOf(RenderType.CUTOUT)
    override fun getRenderTypes(state: BlockState, rand: RandomSource, data: ModelData): ChunkRenderTypeSet =
        ChunkRenderTypeSet.of(RenderType.CUTOUT)

    override fun getOverrides(): ItemOverrides = ItemOverrides.EMPTY

    companion object {
        val itemConnected = Connected()

        val itemTransforms: ItemTransforms

        init {
            val gui = ItemTransform(v(30, 225, 0), v(0, 0, 0), v(.625, .625, .625))
            val ground = ItemTransform(v(0, 0, 0), v(0, .1875, 0), v(.25, .25, .25))
            val fixed = ItemTransform(v(0, 0, 0), v(0, 0, 0), v(.5, .5, .5))
            val thirdPersonRighthand = ItemTransform(v(75, 45, 0), v(0, .015625, 0), v(.375, .375, .375))
            val firstPersonRighthand = ItemTransform(v(0, 45, 0), v(0, 0, 0), v(.4, .4, .4))
            val firstPersonLefthand = ItemTransform(v(0, 225, 0), v(0, 0, 0), v(.4, .4, .4))

            itemTransforms = ItemTransforms(
                thirdPersonRighthand,
                thirdPersonRighthand,
                firstPersonLefthand,
                firstPersonRighthand,
                ItemTransform.NO_TRANSFORM,
                gui,
                ground,
                fixed,
                ImmutableMap.of()
            )
        }

        fun quad(v1: Vector3f, v2: Vector3f, v3: Vector3f, v4: Vector3f, sprite: TextureAtlasSprite): BakedQuad {
            val normal = (v3 - v2).cross(v1 - v2).normalize()

            val builder = QuadBakingVertexConsumer()
            builder.setSprite(sprite)
            builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z))
            builder.setTintIndex(0)
            putVertex(builder, normal, v1.x, v1.y, v1.z, 0f, 0f, sprite)
            putVertex(builder, normal, v2.x, v2.y, v2.z, 0f, 1f, sprite)
            putVertex(builder, normal, v3.x, v3.y, v3.z, 1f, 1f, sprite)
            putVertex(builder, normal, v4.x, v4.y, v4.z, 1f, 0f, sprite)
            return builder.bakeQuad()
        }

        private fun putVertex(
            builder: VertexConsumer,
            normal: Vector3f,
            x: Float,
            y: Float,
            z: Float,
            u: Float,
            v: Float,
            sprite: TextureAtlasSprite
        ) {
            val iu = sprite.getU(u)
            val iv = sprite.getV(v)
            builder.addVertex(x, y, z).setUv(iu, iv).setUv2(0, 0).setColor(1f, 1f, 1f, 1f)
                .setNormal(normal.x, normal.y, normal.z)
        }

        fun v(x: Number, y: Number, z: Number): Vector3f {
            return Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
        }
    }
}