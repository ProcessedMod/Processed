package redcrafter07.processed.materials

import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.cable.CableData
import redcrafter07.processed.block.itempipe.ItemPipeData

object Materials {
    val MATERIALS = ArrayList<Material>()

    val ALUMINIUM =
        MaterialInfo("aluminium").color(0xd0, 0xd5, 0xd9).chemicalDescription("Al").addType(MaterialInfo.Types.All)
            .nuggetVariant(MaterialInfo.NuggetVariant.LongHoriz).rawBlockVariant(MaterialInfo.RawBlockVariant.GoldLike)
            .register(
                BlockTags.NEEDS_STONE_TOOL,
                BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                    .requiresCorrectToolForDrops(),
                BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
            )
    val STEEL =
        MaterialInfo("steel").color(0x49, 0x4b, 0x4d).chemicalDescription("Fe").addType(MaterialInfo.Types.MetalBlock)
            .addType(MaterialInfo.Types.IngotLike).addType(MaterialInfo.Types.Dust)
            .withExtraData { CableData(ProcessedTier.Basic) }.withExtraData { ItemPipeData(ProcessedTier.Basic) }
            .nuggetVariant(MaterialInfo.NuggetVariant.LongVert).register(
                BlockTags.NEEDS_IRON_TOOL,
                BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                    .requiresCorrectToolForDrops(),
                BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
            )
    val NICKEL =
        MaterialInfo("nickel").color(0x4d, 0xd4, 0xa9).chemicalDescription("Ni").addType(MaterialInfo.Types.All)
            .withExtraData { CableData(ProcessedTier.Advanced) }.withExtraData { ItemPipeData(ProcessedTier.Advanced) }
            .nuggetVariant(MaterialInfo.NuggetVariant.ShortHoriz).register(
                BlockTags.NEEDS_IRON_TOOL,
                BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                    .requiresCorrectToolForDrops(),
                BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
            )
    val TITANIUM =
        MaterialInfo("titanium").color(0xcf, 0x71, 0xaf).chemicalDescription("Ti").addType(MaterialInfo.Types.All)
            .withExtraData { CableData(ProcessedTier.Void) }.withExtraData { ItemPipeData(ProcessedTier.Void) }
            .nuggetVariant(MaterialInfo.NuggetVariant.ShortVert).rawBlockVariant(MaterialInfo.RawBlockVariant.GoldLike)
            .register(
                BlockTags.NEEDS_DIAMOND_TOOL,
                BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                    .requiresCorrectToolForDrops(),
                BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
            )
    val URANIUM =
        MaterialInfo("uranium").color(0x3c, 0xff, 0x49).chemicalDescription("U").addType(MaterialInfo.Types.All)
            .withExtraData { CableData(ProcessedTier.Nuclear) }.withExtraData { ItemPipeData(ProcessedTier.Nuclear) }
            .register(
                BlockTags.NEEDS_DIAMOND_TOOL,
                BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                    .requiresCorrectToolForDrops(),
                BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
            )
    val NICKEL_TITANIUM = MaterialInfo("nickle_titanium").color(0x8f, 0xa3, 0xa9).ofMaterials(
        Pair(TITANIUM, 2),
        Pair(NICKEL, 4),
    ).addType(MaterialInfo.Types.Dust).addType(MaterialInfo.Types.IngotLike).addType(MaterialInfo.Types.MetalBlock)
        .register(
            BlockTags.NEEDS_DIAMOND_TOOL,
            BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
                .requiresCorrectToolForDrops(),
            BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops(),
        )


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