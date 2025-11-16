package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler

class InputItemHandlerWrapper(val handler: IItemHandler) : IItemHandler {
    override fun getSlots() = handler.slots
    override fun getStackInSlot(slot: Int): ItemStack = handler.getStackInSlot(slot)
    override fun insertItem(slot: Int, item: ItemStack, simulate: Boolean): ItemStack = handler.insertItem(slot, item, simulate)
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = ItemStack.EMPTY
    override fun getSlotLimit(slot: Int) = handler.getSlotLimit(slot)
    override fun isItemValid(slot: Int, item: ItemStack) = handler.isItemValid(slot, item)
}