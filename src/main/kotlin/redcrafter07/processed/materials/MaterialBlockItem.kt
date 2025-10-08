package redcrafter07.processed.materials

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import redcrafter07.processed.Translations

abstract class MaterialBlockItem(val material: Material, block: Block) : BlockItem(block, PROPS) {
    companion object {
        val PROPS: Properties = Properties().stacksTo(64)
    }

    class MetalBlockItem(material: Material, block: Block) : MaterialBlockItem(material, block) {
        override fun getName(stack: ItemStack): Component = Translations.materialMetalBlock(material)
    }

    class OreBlockItem(material: Material, block: Block) : MaterialBlockItem(material, block) {
        override fun getName(stack: ItemStack): Component = Translations.materialOre(material)
    }
}