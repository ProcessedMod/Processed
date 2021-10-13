package com.redcrafter07.ultrautilities.tileentity;

import com.redcrafter07.ultrautilities.data.recipes.CraftingStationRecipe;
import com.redcrafter07.ultrautilities.data.recipes.ModRecipeTypes;
import com.redcrafter07.ultrautilities.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class OverloadStationTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public OverloadStationTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public OverloadStationTile()    {
        this(ModTileEntities.OVERLOAD_STATION_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        itemHandler.deserializeNBT((CompoundNBT) nbt.get("craftingStationContents"));
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("craftingStationContents", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    private ItemStackHandler createHandler()    {
        return new ItemStackHandler(2) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot)   {
                    case 0:
                        return stack.getItem() == ModItems.OVERLOAD_PROCESSOR.get() || stack.getItem() == ModItems.POWERED_OVERLOAD_PROCESSOR.get();
                    case 1:
                        return stack.getItem() == ModItems.PROCESSOR_SWORD.get();
                    default: return true;
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if(!isItemValid(slot, stack))   {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)    {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void tick() {
        if(world.isRemote)  return;

        //if(itemHandler.getStackInSlot(1).getItem() != ModItems.PROCESSOR_SWORD.get()) return;

        if(itemHandler.getStackInSlot(0).getCount() != 0)    {
            int damageRemover = 1;
            if(itemHandler.getStackInSlot(0).getItem() == ModItems.POWERED_OVERLOAD_PROCESSOR.get()) damageRemover = 4;
            itemHandler.getStackInSlot(1).getItem().setDamage(itemHandler.getStackInSlot(1), itemHandler.getStackInSlot(1).getItem().getDamage(itemHandler.getStackInSlot(1)) - damageRemover);
        }
    }
}
