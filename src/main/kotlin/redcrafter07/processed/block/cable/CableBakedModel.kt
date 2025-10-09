package redcrafter07.processed.block.cable

import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.client.ChunkRenderTypeSet
import net.neoforged.neoforge.client.model.IDynamicBakedModel
import net.neoforged.neoforge.client.model.data.ModelData
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer
import org.joml.Vector3f
import redcrafter07.processed.rl
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.minus

object CableBakedModel : IDynamicBakedModel {
    override fun getQuads(
        p0: BlockState?, p1: Direction?, p2: RandomSource, modelData: ModelData, p4: RenderType?
    ): List<BakedQuad> {
        val quads = ArrayList<BakedQuad>()
        val connected = modelData.get(CableBlockEntity.TRANSMITTER_PROPERTY) ?: CableBlockEntity.Connected()
        val sprite = cableTexture.value

        val o = .4

        if (connected[Direction.UP]) {
            quads.add(quad(v(1 - o, 1, o), v(1 - o, 1, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), sprite))
            quads.add(quad(v(o, 1, 1 - o), v(o, 1, o), v(o, 1 - o, o), v(o, 1 - o, 1 - o), sprite))
            quads.add(quad(v(o, 1, o), v(1 - o, 1, o), v(1 - o, 1 - o, o), v(o, 1 - o, o), sprite))
            quads.add(quad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1, 1 - o), v(o, 1, 1 - o), sprite))
        } else quads.add(quad(v(o, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, o), v(o, 1 - o, o), sprite))

        if (connected[Direction.DOWN]) {
            quads.add(quad(v(1 - o, o, o), v(1 - o, o, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, 0, o), sprite))
            quads.add(quad(v(o, o, 1 - o), v(o, o, o), v(o, 0, o), v(o, 0, 1 - o), sprite))
            quads.add(quad(v(o, o, o), v(1 - o, o, o), v(1 - o, 0, o), v(o, 0, o), sprite))
            quads.add(quad(v(o, 0, 1 - o), v(1 - o, 0, 1 - o), v(1 - o, o, 1 - o), v(o, o, 1 - o), sprite))
        } else quads.add(quad(v(o, o, o), v(1 - o, o, o), v(1 - o, o, 1 - o), v(o, o, 1 - o), sprite))

        if (connected[Direction.EAST]) {
            quads.add(quad(v(1, 1 - o, 1 - o), v(1, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), sprite))
            quads.add(quad(v(1, o, o), v(1, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, o), sprite))
            quads.add(quad(v(1, 1 - o, o), v(1, o, o), v(1 - o, o, o), v(1 - o, 1 - o, o), sprite))
            quads.add(quad(v(1, o, 1 - o), v(1, 1 - o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), sprite))
        } else quads.add(quad(v(1 - o, o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 1 - o), v(1 - o, o, 1 - o), sprite))

        if (connected[Direction.WEST]) {
            quads.add(quad(v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(0, 1 - o, o), v(0, 1 - o, 1 - o), sprite))
            quads.add(quad(v(o, o, o), v(o, o, 1 - o), v(0, o, 1 - o), v(0, o, o), sprite))
            quads.add(quad(v(o, 1 - o, o), v(o, o, o), v(0, o, o), v(0, 1 - o, o), sprite))
            quads.add(quad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(0, 1 - o, 1 - o), v(0, o, 1 - o), sprite))
        } else quads.add(quad(v(o, o, 1 - o), v(o, 1 - o, 1 - o), v(o, 1 - o, o), v(o, o, o), sprite))

        if (connected[Direction.NORTH]) {
            quads.add(quad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, 1 - o, 0), v(o, 1 - o, 0), sprite))
            quads.add(quad(v(o, o, 0), v(1 - o, o, 0), v(1 - o, o, o), v(o, o, o), sprite))
            quads.add(quad(v(1 - o, o, 0), v(1 - o, 1 - o, 0), v(1 - o, 1 - o, o), v(1 - o, o, o), sprite))
            quads.add(quad(v(o, o, o), v(o, 1 - o, o), v(o, 1 - o, 0), v(o, o, 0), sprite))
        } else quads.add(quad(v(o, 1 - o, o), v(1 - o, 1 - o, o), v(1 - o, o, o), v(o, o, o), sprite))

        if (connected[Direction.SOUTH]) {
            quads.add(quad(v(o, 1 - o, 1), v(1 - o, 1 - o, 1), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), sprite))
            quads.add(quad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, o, 1), v(o, o, 1), sprite))
            quads.add(quad(v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(1 - o, 1 - o, 1), v(1 - o, o, 1), sprite))
            quads.add(quad(v(o, o, 1), v(o, 1 - o, 1), v(o, 1 - o, 1 - o), v(o, o, 1 - o), sprite))
        } else quads.add(quad(v(o, o, 1 - o), v(1 - o, o, 1 - o), v(1 - o, 1 - o, 1 - o), v(o, 1 - o, 1 - o), sprite))

        return quads
    }

    override fun useAmbientOcclusion() = true
    override fun isGui3d() = false
    override fun usesBlockLight() = false
    override fun isCustomRenderer() = false
    @Suppress("OVERRIDE_DEPRECATION")
    override fun getParticleIcon(): TextureAtlasSprite = cableTexture.value
    override fun getRenderTypes(itemStack: ItemStack, fabulous: Boolean): List<RenderType> = listOf(RenderType.CUTOUT)
    override fun getRenderTypes(state: BlockState, rand: RandomSource, data: ModelData): ChunkRenderTypeSet =
        ChunkRenderTypeSet.of(RenderType.CUTOUT)

    override fun getOverrides(): ItemOverrides = ItemOverrides.EMPTY

    val cableTexture =
        lazy { Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(rl("block/cable")) }

    fun quad(v1: Vector3f, v2: Vector3f, v3: Vector3f, v4: Vector3f, sprite: TextureAtlasSprite): BakedQuad {
        val normal = (v3 - v2).cross(v1 - v2).normalize()

        val builder = QuadBakingVertexConsumer()
        builder.setSprite(sprite)
        builder.setDirection(Direction.getNearest(normal.x, normal.y, normal.z))
        builder.setTintIndex(1)
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