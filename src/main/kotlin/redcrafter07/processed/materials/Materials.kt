package redcrafter07.processed.materials

import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.data.CableData
import redcrafter07.processed.materials.data.ItemPipeData

object Materials {
    val MATERIALS = ArrayList<Material>()

    val ALUMINIUM = MaterialInfo("aluminium").color(0xd0, 0xd5, 0xd9).addType(MaterialInfo.Types.All)
        .nuggetVariant(MaterialInfo.NuggetVariant.LongHoriz).rawBlockVariant(MaterialInfo.RawBlockVariant.GoldLike)
        .register()

    val STEEL = MaterialInfo("steel").color(0x49, 0x4b, 0x4d).addType(MaterialInfo.Types.MetalBlock)
        .addType(MaterialInfo.Types.IngotLike).addType(MaterialInfo.Types.Dust)
        .withExtraData { CableData(ProcessedTier.Basic) }.withExtraData { ItemPipeData(ProcessedTier.Basic) }
        .nuggetVariant(MaterialInfo.NuggetVariant.LongVert).register()

    val NICKEL = MaterialInfo("nickel").color(0x4d, 0xd4, 0xa9).addType(MaterialInfo.Types.All)
        .withExtraData { CableData(ProcessedTier.Advanced) }.withExtraData { ItemPipeData(ProcessedTier.Advanced) }
        .nuggetVariant(MaterialInfo.NuggetVariant.ShortHoriz).register()

    val TITANIUM = MaterialInfo("titanium").color(0xcf, 0x71, 0xaf).addType(MaterialInfo.Types.All)
        .withExtraData { CableData(ProcessedTier.Void) }.withExtraData { ItemPipeData(ProcessedTier.Void) }
        .nuggetVariant(MaterialInfo.NuggetVariant.ShortVert).rawBlockVariant(MaterialInfo.RawBlockVariant.GoldLike)
        .register()

    val URANIUM = MaterialInfo("uranium").color(0x3c, 0xff, 0x49).addType(MaterialInfo.Types.All)
        .withExtraData { CableData(ProcessedTier.Nuclear) }.withExtraData { ItemPipeData(ProcessedTier.Nuclear) }
        .register()

    val NICKEL_TITANIUM = MaterialInfo("nickle_titanium").color(0x8f, 0xa3, 0xa9).addType(MaterialInfo.Types.Dust)
        .addType(MaterialInfo.Types.IngotLike).addType(MaterialInfo.Types.MetalBlock).register()


    fun register(
        info: MaterialInfo,
        materialTag: TagKey<Block>,
        metalBlockProperties: BlockBehaviour.Properties,
        oreBlockProperties: BlockBehaviour.Properties
    ): Material {
        val material = Material(info, materialTag, metalBlockProperties, oreBlockProperties)
        MATERIALS.add(material)
        return material
    }
}