package redcrafter07.processed.materials

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredItem
import java.util.function.Supplier

fun <T> getMaybeUnregistered(v: Supplier<T>?): T =
    (v ?: throw NullPointerException("Trying to access an unregistered material item or block")).get()

private val EMPTY_RL = ResourceLocation.fromNamespaceAndPath("", "")
val VANILLA_HOLDER_ITEM: DeferredItem<Item> = DeferredItem.createItem(EMPTY_RL)
val VANILLA_HOLDER_BLOCK: DeferredBlock<Block> = DeferredBlock.createBlock(EMPTY_RL)

fun isntVanilla(holder: DeferredItem<*>?) = when (holder) {
    VANILLA_HOLDER_ITEM -> false
    else -> true
}

fun isntVanilla(holder: DeferredBlock<*>?) = when (holder) {
    VANILLA_HOLDER_BLOCK -> false
    else -> true
}