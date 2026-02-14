package redcrafter07.processed.transmitters

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelState
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry
import redcrafter07.processed.rl
import java.util.function.Function

object TransmitterModelLoader : IGeometryLoader<TransmitterModelLoader.TransmitterModel> {
    override fun read(p0: JsonObject, p1: JsonDeserializationContext): TransmitterModel {
        val centerEl = p0.get("center")
        val sideEl = p0.get("side")
        val size = p0.get("start")?.asDouble ?: TransmitterBlock.START
        var centerRL = rl("block/transmitter_center")
        var sideRL = rl("block/transmitter_side")
        try {
            if(centerEl != null) centerRL = ResourceLocation.parse(centerEl.asString)
        } catch (_: Exception) {}
        try {
            if(sideEl != null) sideRL = ResourceLocation.parse(sideEl.asString)
        } catch (_: Exception) {}

        return TransmitterModel(centerRL, sideRL, size)
    }

    class TransmitterModel(val center: ResourceLocation, val side: ResourceLocation, val start: Double) : IUnbakedGeometry<TransmitterModel> {
        override fun bake(
            p0: IGeometryBakingContext,
            p1: ModelBaker,
            p2: Function<Material?, TextureAtlasSprite?>,
            p3: ModelState,
            p4: ItemOverrides
        ): BakedModel = TransmitterBakedModel(center, side, start)
    }
}