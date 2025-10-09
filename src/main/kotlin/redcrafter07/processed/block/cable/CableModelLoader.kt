package redcrafter07.processed.block.cable

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import net.minecraft.client.renderer.block.model.ItemOverrides
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.Material
import net.minecraft.client.resources.model.ModelBaker
import net.minecraft.client.resources.model.ModelState
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry
import java.util.function.Function

object CableModelLoader : IGeometryLoader<CableModelLoader.CableModel> {
    override fun read(p0: JsonObject, p1: JsonDeserializationContext): CableModel = CableModel

    object CableModel : IUnbakedGeometry<CableModel> {
        override fun bake(
            p0: IGeometryBakingContext,
            p1: ModelBaker,
            p2: Function<Material?, TextureAtlasSprite?>,
            p3: ModelState,
            p4: ItemOverrides
        ): BakedModel = CableBakedModel

    }
}