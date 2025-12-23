package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block

class TieredModBlockItem<T>(val tieredBlock: T, itemProperties: Properties) :
    BlockItem(tieredBlock, itemProperties) where T : Block, T : TieredBlock {
    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag
    ) {
        tieredBlock.getDescription(tooltip, flag)

        val block = tieredBlock
        if (block is AdditionalBlockInfo) {
            val additionalTooltip: Component? = block.getAdditionalTooltip(stack, context, flag)
            if (additionalTooltip != null) tooltip.add(additionalTooltip)
        }

        tooltip.add(Component.empty())
        super.appendHoverText(stack, context, tooltip, flag)
    }

    override fun getName(itemStack: ItemStack): Component {
        return tieredBlock.name
    }
}