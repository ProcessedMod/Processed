package redcrafter07.processed.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.Translations

class ResearchContainingItem : Item(Properties().stacksTo(1)) {
    override fun appendHoverText(
        stack: ItemStack, context: TooltipContext, tooltipComponents: MutableList<Component>, tooltipFlag: TooltipFlag
    ) {
        val research = stack.get(ModDataComponents.RESEARCH)
        if (research != null) {
            val research = Component.translatable(research.toLanguageKey("item"))
            tooltipComponents.add(Translations.itemContainedResearch(research))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}