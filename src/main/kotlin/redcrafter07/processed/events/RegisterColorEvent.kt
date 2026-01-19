package redcrafter07.processed.events

import net.minecraft.client.color.block.BlockColor
import net.minecraft.client.color.item.ItemColor
import net.minecraft.core.BlockPos
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent
import redcrafter07.processed.ProcessedMod
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.items.ModItems
import redcrafter07.processed.materials.MaterialContainer

@EventBusSubscriber(modid = ProcessedMod.ID, bus = EventBusSubscriber.Bus.MOD)
object RegisterColorEvent {
    @SubscribeEvent
    /// Register all tinted items of this mods
    fun registerItemColors(event: RegisterColorHandlersEvent.Item) {
        // Go through all items and filter out any that don't have custom colors. This filter should have all the interfaces/classes in ItemColorProvider.
        // Then turn that stream of items into a list and then into a typed array, to spread that array over the varargs of event.register, and as such register
        // all the items with ItemColorProvider.
        event.register(
            ItemColorProvider,
            *ModItems.ITEMS.entries.map { it.get() }
                .filter { it is MaterialContainer || it is ItemColor || it is ModFluids.TintedBucketItem || (it is BlockItem && it.block is MaterialContainer) }
                .toList().toTypedArray())
    }

    private object ItemColorProvider : ItemColor {
        override fun getColor(stack: ItemStack, tintIndex: Int): Int {
            return when (val item = stack.item) {
                is MaterialContainer -> item.getColor(tintIndex) ?: 0xFFFFFF
                is ItemColor -> item.getColor(stack, tintIndex)
                is BlockItem -> {
                    val block = item.block
                    if(block is MaterialContainer) block.getColor(tintIndex) ?: 0xFFFFFF
                    else 0xFFFFFF
                }
                is ModFluids.TintedBucketItem -> item.getColor(stack, tintIndex)
                else -> 0xFFFFFF
            }
        }
    }

    @SubscribeEvent
    /// Register all tinted blocks of this mods
    fun registerBlockColors(event: RegisterColorHandlersEvent.Block) {
        // Go through all blocks and filter out any that don't have custom colors. This filter should have all the interfaces/classes in BlockColorProvider.
        // Then turn that stream of blocks into a list and then into a typed array, to spread that array over the varargs of event.register, and as such register
        // all the blocks with BlockColorProvider.
        event.register(BlockColorProvider, *ModBlocks.BLOCKS.entries.stream().map { it.get() }.filter {
            it is MaterialContainer || it is BlockColor
        }.toList().toTypedArray())
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