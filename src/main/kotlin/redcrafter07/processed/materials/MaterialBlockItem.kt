package redcrafter07.processed.materials

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import redcrafter07.processed.Translations

abstract class MaterialBlockItem(block: Block, val material: Material) : BlockItem(block, PROPS) {
    companion object {
        val PROPS: Properties = Properties().stacksTo(64)
    }

    class MetalBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialMetalBlock(material)
    }

    class OreBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialOre(material)
    }
}