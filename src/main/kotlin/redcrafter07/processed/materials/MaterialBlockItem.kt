package redcrafter07.processed.materials

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import redcrafter07.processed.Translations
import redcrafter07.processed.block.cable.CableData

abstract class MaterialBlockItem(block: Block, override val material: Material) : BlockItem(block, PROPS),
    MaterialContainer {
    companion object {
        val PROPS: Properties = Properties().stacksTo(64)
    }

    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
        tooltip.add(Component.literal(material.info.chemicalDescription).withStyle(ChatFormatting.YELLOW))
        customHoverText(stack, context, tooltip, tooltipFlag)
        super.appendHoverText(stack, context, tooltip, tooltipFlag)
    }

    open fun customHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
    }

    class MetalBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialMetalBlock(material)
    }

    class OreBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialOre(material)
    }

    class RawMetalBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialRawMetalBlock(material)
    }

    class CableBlockItem(block: Block, material: Material) : MaterialBlockItem(block, material) {
        override fun getName(stack: ItemStack): Component = Translations.materialCable(material)

        override fun customHoverText(
            stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
        ) {
            val data = material.getExtraData(CableData::class.java) ?: return
            tooltip.add(Translations.cableTransferSpeedTooltip(data.tier.nameColored))
        }
    }
}