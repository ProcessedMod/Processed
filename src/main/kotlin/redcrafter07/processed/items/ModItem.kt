package redcrafter07.processed.items

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.Translations

open class ModItem(properties: Properties, val itemID: String) : Item(properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltip: MutableList<Component?>,
        flag: TooltipFlag
    ) {
        tooltip.add(Translations.itemTooltip(itemID))
        for (comp in getAdditionalTooltip(stack, context, flag)) tooltip.add(comp)

        tooltip.add(Component.empty())
        super.appendHoverText(stack, context, tooltip, flag)
    }

    open fun getAdditionalTooltip(stack: ItemStack, context: TooltipContext, flag: TooltipFlag): List<MutableComponent> = listOf()
}