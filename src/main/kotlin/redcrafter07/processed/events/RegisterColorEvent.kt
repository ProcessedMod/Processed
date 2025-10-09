package redcrafter07.processed.events

import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.core.BlockPos
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.MaterialContainer

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object RegisterColorEvent {
    @SubscribeEvent
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        event.register(ItemColorProvider, *ModItems.DUST_ITEMS.toTypedArray())
        event.register(ItemColorProvider, *ModItems.INGOT_ITEMS.toTypedArray())
        event.register(ItemColorProvider, *ModItems.NUGGET_ITEMS.toTypedArray())
        event.register(ItemColorProvider, *ModItems.RAW_ITEMS.toTypedArray())
        event.register(ItemColorProvider, *ModBlocks.MATERIAL_BLOCK_ITEMS.toTypedArray())
    }

    private object ItemColorProvider : ItemColor {
        override fun getColor(stack: ItemStack, tintIndex: Int): Int {
            return when (val item = stack.item) {
                is MaterialContainer -> item.getColor(tintIndex) ?: 0xFFFFFF
                is ItemColor -> item.getColor(stack, tintIndex)
                else -> 0xFFFFFF
            }
        }
    }

    @SubscribeEvent
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        event.register(BlockColorProvider, *ModBlocks.MATERIAL_BLOCKS.stream().map { it.get() }.toList().toTypedArray())
    }

    private object BlockColorProvider : BlockColor {
        override fun getColor(state: BlockState, level: BlockAndTintGetter?, pos: BlockPos?, tintIndex: Int): Int {
            return when (val block = state.block) {
                is MaterialContainer -> block.getColor(tintIndex) ?: 0xFFFFFF
                is BlockColor -> block.getColor(state, level, pos, tintIndex)
                else -> 0xFFFFFF
            }
        }
    }
}