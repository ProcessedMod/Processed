package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.block.Block
import redcrafter07.processed.Translations

class ModBlockItem(block: Block, itemProperties: Properties, val itemID: String): BlockItem(block, itemProperties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component?>,
        flag: TooltipFlag
    ) {
        tooltip.add(Translations.blockItemTooltip(itemID))

        val block = block
        if (block is AdditionalBlockInfo) {
            val additionalTooltip: Component? = block.getAdditionalTooltip(stack, context, flag)
            if (additionalTooltip != null) tooltip.add(additionalTooltip)
        }

        super.appendHoverText(stack, context, tooltip, flag)
    }
}