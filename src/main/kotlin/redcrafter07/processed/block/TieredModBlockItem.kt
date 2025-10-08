package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.block.machine_abstractions.TieredProcessedBlock

class TieredModBlockItem(val tieredProcessedBlock: TieredProcessedBlock, itemProperties: Properties) :
    BlockItem(tieredProcessedBlock, itemProperties) {
    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltip: MutableList<Component>, flag: TooltipFlag
    ) {
        tieredProcessedBlock.getDescription(tooltip, flag)

        val block = block
        if (block is AdditionalBlockInfo) {
            val additionalTooltip: Component? = block.getAdditionalTooltip(stack, context, flag)
            if (additionalTooltip != null) tooltip.add(additionalTooltip)
        }

        tooltip.add(Component.empty())
        super.appendHoverText(stack, context, tooltip, flag)
    }

    override fun getName(itemStack: ItemStack): Component {
        return tieredProcessedBlock.getName()
    }
}