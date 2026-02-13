package redcrafter07.processed.materials

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.materials.data.MaterialBase

abstract class MaterialBlockItem(block: Block, override val material: MaterialBase) : BlockItem(block, PROPS),
    MaterialContainer {
    companion object {
        val PROPS: Properties = Properties().stacksTo(64)
    }

    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
        customHoverText(stack, context, tooltip, tooltipFlag)
        super.appendHoverText(stack, context, tooltip, tooltipFlag)
    }

    open fun customHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
    }

    override fun getName(stack: ItemStack): Component = description

    class OreBlockItem(block: Block, material: MaterialBase) : MaterialBlockItem(block, material) {
        override fun getDescription(): Component = Translations.materialOre(material)
    }

    class CableBlockItem(block: Block, material: MaterialBase, val tier: ProcessedTier) : MaterialBlockItem(block, material) {
        override fun getDescription(): Component = Translations.materialCable(material)

        override fun customHoverText(
            stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
        ) {
            tooltip.add(Translations.cableTierTooltip(tier.nameColored))
        }
    }

    class ItemPipeBlockItem(block: Block, material: MaterialBase, val speed: Int) : MaterialBlockItem(block, material) {
        override fun getDescription(): Component = Translations.materialItemPipe(material)

        override fun customHoverText(
            stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, tooltipFlag: TooltipFlag
        ) {
            tooltip.add(Translations.itemPipeTooltip(speed))
        }
    }
}