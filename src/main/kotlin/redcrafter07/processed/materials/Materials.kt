package redcrafter07.processed.materials

import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.util.FastColor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument

object Materials {
    val MATERIALS = ArrayList<Material>()

    var ALUMINIUM = register(
        "aluminium",
        color(0xd0, 0xd5, 0xd9),
        "Al",
        BlockTags.NEEDS_STONE_TOOL,
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
            .requiresCorrectToolForDrops(),
        BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops()
    )
    var STEEL = register(
        "steel",
        color(0x49, 0x4b, 0x4d),
        "Fe",
        BlockTags.NEEDS_IRON_TOOL,
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
            .requiresCorrectToolForDrops(),
        BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops()
    )
    var NICKEL = register(
        "nickel",
        color(0xd4, 0xd4, 0xa9),
        "Ni",
        BlockTags.NEEDS_IRON_TOOL,
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
            .requiresCorrectToolForDrops(),
        BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops()
    )
    var TITANIUM = register(
        "titanium",
        color(0xcf, 0x71, 0xaf),
        "Ti",
        BlockTags.NEEDS_DIAMOND_TOOL,
        BlockBehaviour.Properties.of().instrument(NoteBlockInstrument.BASEDRUM).strength(5f, 10f)
            .requiresCorrectToolForDrops(),
        BlockBehaviour.Properties.of().sound(SoundType.STONE).strength(5f, 10f).requiresCorrectToolForDrops()
    )


    fun register(
        identifier: String,
        color: Int,
        chemicalDescription: String,
        materialTag: TagKey<Block>,
        metalBlockProperties: BlockBehaviour.Properties,
        oreBlockProperties: BlockBehaviour.Properties
    ): Material {
        val material =
            Material(identifier, color, chemicalDescription, materialTag, metalBlockProperties, oreBlockProperties)
        MATERIALS.add(material)
        return material
    }

    private fun color(r: Int, g: Int, b: Int): Int = FastColor.ABGR32.color(0xff, r, g, b)
}