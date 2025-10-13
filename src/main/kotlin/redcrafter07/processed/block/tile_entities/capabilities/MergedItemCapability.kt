package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class MergedItemCapability(val handlers: List<IItemHandler>) : IItemHandler {
    fun handlerForSlot(slot: Int): Pair<Int, IItemHandler>? {
        var slot = slot
        for (handler in handlers) {
            if (slot < handler.slots) return Pair(slot, handler)
            slot -= handler.slots
        }
        return null
    }

    override fun getSlots(): Int = handlers.sumOf { it.slots }
    override fun getStackInSlot(slot: Int): ItemStack {
        val slotAndHandler = handlerForSlot(slot) ?: return ItemStack.EMPTY
        return slotAndHandler.second.getStackInSlot(slotAndHandler.first)
    }

    override fun insertItem(
        slot: Int, item: ItemStack, simulate: Boolean
    ): ItemStack {
        val slotAndHandler = handlerForSlot(slot) ?: return item
        return slotAndHandler.second.insertItem(slotAndHandler.first, item, simulate)
    }

    override fun extractItem(slot: Int, maxAmount: Int, simulate: Boolean): ItemStack {
        val slotAndHandler = handlerForSlot(slot) ?: return ItemStack.EMPTY
        return slotAndHandler.second.extractItem(slotAndHandler.first, maxAmount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        val slotAndHandler = handlerForSlot(slot) ?: return 0
        return slotAndHandler.second.getSlotLimit(slotAndHandler.first)
    }

    override fun isItemValid(slot: Int, item: ItemStack): Boolean {
        val slotAndHandler = handlerForSlot(slot) ?: return false
        return slotAndHandler.second.isItemValid(slotAndHandler.first, item)
    }
}