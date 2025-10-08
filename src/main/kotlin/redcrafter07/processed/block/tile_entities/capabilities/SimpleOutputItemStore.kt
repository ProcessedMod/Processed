package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack

class SimpleOutputItemStore(items: NonNullList<ItemStack>) : ProcessedItemStackHandler(items) {
    constructor(size: Int) : this(NonNullList.withSize(size, ItemStack.EMPTY))
    constructor() : this(NonNullList.create())

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = stack
    override fun isItemValid(slot: Int, stack: ItemStack): Boolean = false
}