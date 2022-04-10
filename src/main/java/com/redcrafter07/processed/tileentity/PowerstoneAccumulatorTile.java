package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.PowerstoneAccumulatorBlock;
import com.redcrafter07.processed.item.ModItems;
import net.minecraft.block.BlockState;
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

public class PowerstoneAccumulatorTile extends TileEntity implements ITickableTileEntity {
    public int fillState;

    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public PowerstoneAccumulatorTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public PowerstoneAccumulatorTile() {
        this(ModTileEntities.POWERSTONE_ACCUMULATOR_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        nbt.getInt("FillState");
        itemHandler.deserializeNBT(nbt.getCompound("powerstoneAccumulatorContents"));
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putInt("FillState", fillState);
        nbt.put("powerstoneAccumulatorContents", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot) {
                    case 0:
                        return stack.getItem() == ModItems.OVERLOAD_BATTERY.get();
                    default:
                        return true;
                }
            }

            @Override
            public int getSlotLimit(int slot) {
                return 1;
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

    @Override
    public void tick() {
        int currentState = world.getTileEntity(pos).getTileData().getInt("FillState");
        System.out.println(currentState);
        if(currentState > 0) world.setBlockState(pos,world.getBlockState(pos).with(PowerstoneAccumulatorBlock.FILLED, true));
        else world.setBlockState(pos,world.getBlockState(pos).with(PowerstoneAccumulatorBlock.FILLED, false));
        markDirty();
        if (itemHandler.getStackInSlot(0).getCount() > 0 && itemHandler.getStackInSlot(0).getDamage() < 100 && currentState < 1500) {
            world.getTileEntity(pos).getTileData().putInt("FillState", currentState + 1);
            itemHandler.getStackInSlot(0).setDamage(itemHandler.getStackInSlot(0).getDamage() + 1);
        }
    }
}
