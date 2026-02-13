package redcrafter07.processed.materials

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import redcrafter07.processed.Translations
import redcrafter07.processed.rl

open class Material(
    val info: MaterialInfo,
    val materialTag: TagKey<Block>,
    val metalBlockProperties: BlockBehaviour.Properties,
    val oreBlockProperties: BlockBehaviour.Properties
) {
    val component: MutableComponent get() = Translations.materialName(identifier)

    val identifier: String get() = info.identifier
    val dustPath: String get() = "${identifier}_dust"
    val rawPath: String get() = "raw_${identifier}"
    val nuggetPath: String get() = "${identifier}_nugget"
    val ingotPath: String get() = "${identifier}_ingot"
    val metalBlockPath: String get() = "${identifier}_block"
    val rawMetalBlockPath: String get() = "raw_${identifier}_block"
    val oreBlockPath: String get() = "${identifier}_ore"

    val dust: Item get() = BuiltInRegistries.ITEM.get(rl(dustPath))
    val raw: Item get() = BuiltInRegistries.ITEM.get(rl(rawPath))
    val nugget: Item get() = BuiltInRegistries.ITEM.get(rl(nuggetPath))
    val ingot: Item get() = BuiltInRegistries.ITEM.get(rl(ingotPath))

    val metalBlockItem: Item get() = BuiltInRegistries.ITEM.get(rl(metalBlockPath))
    val rawMetalBlockItem: Item get() = BuiltInRegistries.ITEM.get(rl(rawMetalBlockPath))
    val oreBlockItem: Item get() = BuiltInRegistries.ITEM.get(rl(oreBlockPath))

    val metalBlock: Block get() = BuiltInRegistries.BLOCK.get(rl(metalBlockPath))
    val rawMetalBlock: Block get() = BuiltInRegistries.BLOCK.get(rl(rawMetalBlockPath))
    val oreBlock: Block get() = BuiltInRegistries.BLOCK.get(rl(oreBlockPath))

    val dustTag: TagKey<Item> get() = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "dusts/$identifier"))
    val rawTag: TagKey<Item> get() = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "raw_materials/$identifier"))
    val nuggetTag: TagKey<Item> get() = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "nuggets/$identifier"))
    val ingotTag: TagKey<Item> get() = ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "ingots/$identifier"))

    fun <T> getExtraData(clazz: Class<T>): T? = info.getExtraData(clazz)

    override fun toString(): String = "Material[$identifier]"
}