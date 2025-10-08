package redcrafter07.processed.block

import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.BlockState

interface WrenchInteractableBlock {
    fun onWrenchUse(ctx: UseOnContext, state: BlockState)
}