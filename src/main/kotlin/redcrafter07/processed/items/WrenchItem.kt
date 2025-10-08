package redcrafter07.processed.items

import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.Translations
import redcrafter07.processed.block.WrenchInteractableBlock

class WrenchItem : ModItem(Properties().stacksTo(1), "wrench") {
    companion object {
        fun getMode(stack: ItemStack): WrenchMode =
            stack.getOrDefault(ModDataComponents.WRENCH_MODE, WrenchMode.Config)

        fun setMode(stack: ItemStack, mode: WrenchMode): ItemStack {
            stack.set(ModDataComponents.WRENCH_MODE, mode)
            return stack
        }

        private val facingProperties = listOf(
            DirectionalBlock.FACING,
            HorizontalDirectionalBlock.FACING,
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.FACING
        )
    }

    override fun getDefaultInstance(): ItemStack = setMode(super.defaultInstance, WrenchMode.Config)

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player ?: return InteractionResult.PASS
        val stack = player.getItemInHand(context.hand)
        if (stack.item is WrenchItem) {
            val mode = getMode(stack)

            return when (mode) {
                WrenchMode.Rotate -> rotate(context)
                WrenchMode.Config -> configure(context)
            }
        }

        return super.useOn(context)
    }


    private fun rotate(context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos)
        val player = context.player ?: return InteractionResult.PASS

        for (property in facingProperties) {
            if (blockState.hasProperty(property)) {
                val facing = blockState.getValue(property)
                val newFacing = if (player.isShiftKeyDown) facing.counterClockWise else facing.clockWise
                val newState = blockState.setValue(property, newFacing)
                context.level.setBlock(context.clickedPos, newState, 3)
                return InteractionResult.SUCCESS
            }
        }

        return InteractionResult.PASS
    }

    private fun configure(context: UseOnContext): InteractionResult {
        val blockState = context.level.getBlockState(context.clickedPos)

        val block = blockState.block
        if (block is WrenchInteractableBlock) {
            block.onWrenchUse(context, blockState)
            return InteractionResult.SUCCESS
        }

        val blockEntity = context.level.getBlockEntity(context.clickedPos)
        if (blockEntity is WrenchInteractableBlock) {
            blockEntity.onWrenchUse(context, blockState)
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    override fun getAdditionalTooltip(
        stack: ItemStack, context: TooltipContext, flag: TooltipFlag
    ): List<MutableComponent> = listOf(Translations.wrenchModeTooltip(getMode(stack)))
}