package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandlerModifiable

class OutputItemHandlerWrapper(val handler: IItemHandlerModifiable) : IItemHandlerModifiable {
    override fun getSlots() = handler.slots
    override fun getStackInSlot(slot: Int): ItemStack = handler.getStackInSlot(slot)
    override fun getSlotLimit(slot: Int) = handler.getSlotLimit(slot)
    override fun isItemValid(slot: Int, item: ItemStack) = handler.isItemValid(slot, item)
    override fun setStackInSlot(slot: Int, stack: ItemStack) = handler.setStackInSlot(slot, stack)
    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack =
        handler.extractItem(slot, amount, simulate)

    override fun insertItem(slot: Int, item: ItemStack, simulate: Boolean): ItemStack = item
}