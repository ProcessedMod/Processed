package redcrafter07.processed.items

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import redcrafter07.processed.ProcessedTier

class EngineItem(val tier: ProcessedTier) : Item(Properties().component(ModDataComponents.ENGINE_TIER, tier)) {
    override fun getName(stack: ItemStack): Component = tier.name.append(" Engine")
}