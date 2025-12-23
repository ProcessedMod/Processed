package redcrafter07.processed.block

import net.minecraft.network.chat.Component
import net.minecraft.world.item.TooltipFlag
import redcrafter07.processed.ProcessedTier

interface TieredBlock {
    val tier: ProcessedTier
    fun getDescription(tooltips: MutableList<Component>, flag: TooltipFlag)
}