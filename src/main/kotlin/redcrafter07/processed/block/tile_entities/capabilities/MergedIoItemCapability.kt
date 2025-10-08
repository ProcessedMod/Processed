package redcrafter07.processed.block.tile_entities.capabilities

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.IItemHandlerModifiable
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine

class MergedIoItemCapability(val handlers: ProcessedMachine.CapabilityHandlers) : IItemHandlerModifiable {
    private fun get(state: IoState): IItemHandler = handlers.getItemHandlerForState(state) ?: EmptyItemHandler.INSTANCE

    override fun getSlots(): Int {
        return get(IoState.Output).slots + get(IoState.Input).slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        val handler = handlers.getItemHandlerForState(IoState.Output) ?: return ItemStack.EMPTY
        if (slot >= handler.slots || slot < 0) return ItemStack.EMPTY
        return handler.getStackInSlot(slot)
    }

    override fun setStackInSlot(slot: Int, stack: ItemStack) {
        val realSlot = slot - get(IoState.Output).slots

        val handler = get(IoState.Input)
        if (handler is IItemHandlerModifiable) {
            if (realSlot < 0 || realSlot >= handler.slots) return
            handler.setStackInSlot(realSlot, stack)
        }
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
        val realSlot = slot - get(IoState.Output).slots
        val handler = get(IoState.Input)

        if (realSlot >= handler.slots || realSlot < 0) return stack
        return handler.insertItem(realSlot, stack, simulate)
    }

    override fun extractItem(slot: Int, maxAmount: Int, simulate: Boolean): ItemStack {
        val handler = get(IoState.Output)
        if (slot >= handler.slots || slot < 0) return ItemStack.EMPTY

        return handler.extractItem(
            slot, maxAmount, simulate
        )
    }

    override fun getSlotLimit(slot: Int): Int {
        val handlerInput = get(IoState.Input)
        val handlerOutput = get(IoState.Output)

        if (slot < 0) return 0
        if (slot < handlerOutput.slots) return handlerOutput.getSlotLimit(slot)
        if (slot < handlerInput.slots + handlerOutput.slots) return handlerInput.getSlotLimit(slot - handlerOutput.slots)
        return 0
    }

    override fun isItemValid(slot: Int, stack: ItemStack): Boolean {
        val handlerInput = get(IoState.Input)
        val handlerOutput = get(IoState.Output)

        if (slot < 0) return false
        if (slot < handlerOutput.slots) return handlerOutput.isItemValid(slot, stack)
        if (slot < handlerInput.slots + handlerOutput.slots) return handlerInput.isItemValid(
            slot - handlerOutput.slots, stack
        )
        return false
    }
}