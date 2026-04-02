package redcrafter07.processed.items

import net.minecraft.core.registries.BuiltInRegistries
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
            // try getting the item. if that doesn't work, translate with language key of item.
            val name = BuiltInRegistries.ITEM.getOptional(research)
                .map { it.defaultInstance.hoverName }
                .orElseGet { Component.translatable(research.toLanguageKey("item")) }
            tooltipComponents.add(Translations.itemContainedResearch(name))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}