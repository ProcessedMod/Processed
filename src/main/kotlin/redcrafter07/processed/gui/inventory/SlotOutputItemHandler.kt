package redcrafter07.processed.gui.inventory

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable

class SlotOutputItemHandler(val itemHandler: IItemHandler, slot: Int, x: Int, y: Int) : Slot(EMPTY_INVENTORY, slot, x, y) {
    companion object {
        val EMPTY_INVENTORY: Container = SimpleContainer(0)
    }

    override fun mayPlace(stack: ItemStack): Boolean {
        return false
    }

    override fun getItem(): ItemStack {
        return itemHandler.getStackInSlot(slotIndex)
    }

    override fun set(stack: ItemStack) {
        if (itemHandler is IItemHandlerModifiable) {
            itemHandler.setStackInSlot(slotIndex, stack)
            this.setChanged()
        }
    }

    override fun onQuickCraft(oldStackIn: ItemStack, newStackIn: ItemStack) {
    }

    override fun getMaxStackSize(): Int {
        return itemHandler.getSlotLimit(slotIndex)
    }

    override fun getMaxStackSize(stack: ItemStack): Int {
        val maxAdd = stack.copy()
        val maxInput = stack.maxStackSize
        maxAdd.count = maxInput
        if (itemHandler is IItemHandlerModifiable) {
            val currentStack = itemHandler.getStackInSlot(slotIndex)
            itemHandler.setStackInSlot(slotIndex, ItemStack.EMPTY)
            val remainder = itemHandler.insertItem(slotIndex, maxAdd, true)
            itemHandler.setStackInSlot(slotIndex, currentStack)
            return maxInput - remainder.count
        } else {
            val remainder = itemHandler.insertItem(slotIndex, maxAdd, true)
            val current = itemHandler.getStackInSlot(slotIndex).count
            val added = maxInput - remainder.count
            return current + added
        }
    }

    override fun mayPickup(playerIn: Player): Boolean {
        return !itemHandler.extractItem(slotIndex, 1, true).isEmpty
    }

    override fun remove(amount: Int): ItemStack {
        return itemHandler.extractItem(slotIndex, amount, false)
    }
}