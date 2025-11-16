package redcrafter07.processed.materials

import net.minecraft.network.chat.Component
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import redcrafter07.processed.Translations


abstract class MaterialItem(override val material: Material) : Item(DEFAULT_PROPERTIES), MaterialContainer {
    companion object { val DEFAULT_PROPERTIES: Properties = Properties().stacksTo(64); }

    abstract val tag: TagKey<Item>

    class Dust(material: Material) : MaterialItem(material) {
        override val tag: TagKey<Item> = material.dustTag
        override fun getName(stack: ItemStack): Component = Translations.materialDust(material)
    }

    class Nugget(material: Material) : MaterialItem(material) {
        override val tag: TagKey<Item> = material.nuggetTag
        override fun getName(stack: ItemStack): Component = Translations.materialNugget(material)
    }

    class Ingot(material: Material) : MaterialItem(material) {
        override val tag: TagKey<Item> = material.ingotTag
        override fun getName(stack: ItemStack): Component = Translations.materialIngot(material)
    }

    class Raw(material: Material) : MaterialItem(material) {
        override val tag: TagKey<Item> = material.rawTag
        override fun getName(stack: ItemStack): Component = Translations.materialRaw(material)
    }
}