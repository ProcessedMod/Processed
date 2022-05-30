package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.Processed;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class TileHelper {
    public static boolean canItemAmountBePutInSlot(ItemStackHandler h, int slot, ItemStack i, int stackSize) {
        Processed.getLOGGER().debug("Checking if slot is inbound");
        if(h.getSlots() <= slot) return false;
        Processed.getLOGGER().debug("Checking if item amount can be put in");
        if (h.getSlotLimit(slot) < h.getStackInSlot(slot).getCount() + stackSize && !i.isEmpty()) return false;
        Processed.getLOGGER().debug("checking if items match");
        return (h.getStackInSlot(slot).getItem().getTags().equals(i.getItem().getTags()));
    }

    public static boolean canItemBePutInSlot(ItemStackHandler h, int slot, ItemStack i) {
        return canItemAmountBePutInSlot(h, slot, i, 1);
    }
}
