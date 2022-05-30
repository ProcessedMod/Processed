package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.data.recipes.ChargedCraftingTableRecipe;
import com.redcrafter07.processed.data.recipes.ModRecipeTypes;
import net.minecraft.block.BlockState;
import net.minecraft.block.CraftingTableBlock;
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

public class ChargedCraftingTableTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private int fillState;
    private int maxFillState = 20000;

    public ChargedCraftingTableTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public ChargedCraftingTableTile() {
        this(ModTileEntities.CHARGED_CRAFTING_TABLE_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("advancedLightningConcentratorContents"));
        fillState = nbt.getInt("FillState");
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("advancedLightningConcentratorContents", itemHandler.serializeNBT());
        nbt.putInt("FillState", fillState);
        return super.write(nbt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(10) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot == 9) {
                    return false;
                }
                return true;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!isItemValid(slot, stack)) {
                    return stack;
                }

                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        return super.getCapability(cap, side);
    }

    public void craft() {
        Inventory inv = new Inventory(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setInventorySlotContents(i, itemHandler.getStackInSlot(i));
        }

        Optional<ChargedCraftingTableRecipe> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.CHARGED_CRAFTING_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {
            ItemStack output = iRecipe.getRecipeOutput();

            int cost = iRecipe.getEnergyCost();



            for (int i = 0; i < 9; i++) {
                itemHandler.extractItem(i, 1, false);
            }

            itemHandler.insertItem(9, output, false);

            markDirty();
        });
    }

    @Override
    public void tick() {
        if (world.isRemote) return;

        craft();
    }
}
