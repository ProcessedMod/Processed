package redcrafter07.processed.materials

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import redcrafter07.processed.Translations
import redcrafter07.processed.materials.data.MaterialBase


abstract class MaterialItem(override val material: MaterialBase) : Item(DEFAULT_PROPERTIES), MaterialContainer {
    companion object { val DEFAULT_PROPERTIES: Properties = Properties().stacksTo(64); }
    override fun getName(stack: ItemStack): Component = description

    class Dust(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialDust(material)
    }
    class SmallDust(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialSmallDust(material)
    }
    class ImpureDust(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialImpureDust(material)
    }
    class WashedDust(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialWashedDust(material)
    }
    class PureDust(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialPureDust(material)
    }

    class Nugget(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialNugget(material)
    }

    class Ingot(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialIngot(material)
    }

    class Raw(material: MaterialBase) : MaterialItem(material) {
        override fun getDescription(): Component = Translations.materialRaw(material)
    }
}