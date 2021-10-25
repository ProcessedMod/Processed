package com.redcrafter07.ultrautilities.tileentity;

import com.redcrafter07.ultrautilities.data.recipes.CraftingStationRecipe;
import com.redcrafter07.ultrautilities.data.recipes.ModRecipeTypes;
import com.redcrafter07.ultrautilities.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
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

public class CraftingStationTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public CraftingStationTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public CraftingStationTile()    {
        this(ModTileEntities.CRAFTING_STATION_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("craftingStationContents"));
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("craftingStationContents", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    private ItemStackHandler createHandler()    {
        return new ItemStackHandler(3) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot)   {
                    case 0:
                        return stack.getItem() == ModItems.PROCESSOR_CORE.get();
                    case 1:
                    case 2:
                        return stack.getItem() != ModItems.PROCESSOR_CORE.get();
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

    public void craft() {
         Inventory inv = new Inventory(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setInventorySlotContents(i, itemHandler.getStackInSlot(i));
        }

        Optional<CraftingStationRecipe> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.PROCESSOR_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {
            ItemStack output = iRecipe.getRecipeOutput();

//            System.out.println(itemHandler.getStackInSlot(2).getCount());

            if(itemHandler.getStackInSlot(2).getCount() < 1)   {
                itemHandler.extractItem(0, 1, false);
                itemHandler.extractItem(1, 1, false);
                itemHandler.insertItem(2, output, false);
            }


            markDirty();
        });
    }

    @Override
    public void tick() {
        if(world.isRemote)  return;


        craft();
    }
}
