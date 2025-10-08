package redcrafter07.processed.block

import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

interface AdditionalBlockInfo {
    fun getAdditionalTooltip(stack: ItemStack, context: Item.TooltipContext, flag: TooltipFlag): MutableComponent? {
        return null
    }
}