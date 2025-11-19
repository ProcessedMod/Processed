package redcrafter07.processed.items

import net.minecraft.core.Direction
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import redcrafter07.processed.Translations
import redcrafter07.processed.block.WrenchInteractableBlock

class WrenchItem : ModItem(Properties().stacksTo(1), "wrench") {
    companion object {
        fun getMode(stack: ItemStack): WrenchMode = stack.getOrDefault(ModDataComponents.WRENCH_MODE, WrenchMode.Config)

        fun setMode(stack: ItemStack, mode: WrenchMode): ItemStack {
            stack.set(ModDataComponents.WRENCH_MODE, mode)
            return stack
        }
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

        if (blockState.hasProperty(BlockStateProperties.FACING)) {
            val facing = blockState.getValue(BlockStateProperties.FACING)
            val newData = facing.get3DDataValue() + if (player.isShiftKeyDown) -1 else 1
            val newFacing = Direction.from3DDataValue(if (newData > 5) 0 else if (newData < 0) 5 else newData)
            val newState = blockState.setValue(BlockStateProperties.FACING, newFacing)
            context.level.setBlock(context.clickedPos, newState, 3)
        } else if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            val facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            val newFacing = if (player.isShiftKeyDown) facing.counterClockWise else facing.clockWise
            val newState = blockState.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing)
            context.level.setBlock(context.clickedPos, newState, 3)
        } else return InteractionResult.PASS

        return InteractionResult.SUCCESS
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