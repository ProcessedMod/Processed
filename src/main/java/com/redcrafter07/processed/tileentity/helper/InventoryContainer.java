package com.redcrafter07.processed.tileentity.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class InventoryContainer {
    private ItemStackHandler h;
    private String id;

    public InventoryContainer(int size, String id, Function<Integer, Integer> getSlotLimit, Function<Integer, Boolean> isItemValid) {
        this.id = id;
        this.h = new ItemStackHandler(size) {
            @Override
            public int getSlotLimit(int slot) {
                return getSlotLimit.apply(slot);
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return isItemValid.apply(slot);
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack))
                    return stack;

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    public ItemStackHandler getHandler() {
        return h;
    }

    public void read(CompoundNBT nbt) {
        this.h.deserializeNBT(nbt);
    }

    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("itemcontainercontents" + this.id, this.h.serializeNBT());
        return nbt;
    }
}
