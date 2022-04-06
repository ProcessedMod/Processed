package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.LightningConcentratorBlock;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.LightningConcentratorRecipe;
import com.redcrafter07.processed.data.recipes.ModRecipeTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class LightningConcentratorTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public LightningConcentratorTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public LightningConcentratorTile()    {
        this(ModTileEntities.LIGHTNING_CONCENTRATOR_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        itemHandler.deserializeNBT(nbt.getCompound("lightningConcentratorContents"));
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.put("lightningConcentratorContents", itemHandler.serializeNBT());
        return super.write(nbt);
    }

    private ItemStackHandler createHandler()    {
        return new ItemStackHandler(5) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                switch (slot)   {
                    case 0:
                    case 1:
                    case 3:
                    case 4:
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

    private void lightning()    {
        if(!this.world.isRemote()) {
                EntityType.LIGHTNING_BOLT.spawn((ServerWorld)world, null, null, pos, SpawnReason.TRIGGERED, true, true);
            }
    }

    public boolean findEnvironmentalSuppressor(int x, int y, int z, int radius) {
        x-=radius;
        y-=radius;
        z-=radius;

        int u = x;
        int v = y;
        int w = z;
        for(int k =0; k < (radius*2+1); k++) {
            x = u;
            for(int j =0; j < (radius*2+1); j++) {
                z = w;
                for(int i =0; i < (radius*2+1); i++) {
                    Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();

                    if(block == ModBlocks.ENVIRONMENTAL_SUPPRESSOR.get().getBlock()) {
                        return true;
                    }
                    z++;
                }
                x++;
            }
            y++;
        }

        return false;
    }

    public void craft() {
        Inventory inv = new Inventory(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setInventorySlotContents(i, itemHandler.getStackInSlot(i));
        }

        Optional<LightningConcentratorRecipe> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.LIGHTNING_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {
            ItemStack output = iRecipe.getRecipeOutput();

            BlockPos blockPos = this.getPos();

            if(world.isThundering() || findEnvironmentalSuppressor(blockPos.getX(), blockPos.getY(), blockPos.getZ(), 15)) {
                lightning();
                itemHandler.extractItem(0, 1, false);
                itemHandler.extractItem(1, 1, false);
                itemHandler.extractItem(2, 1, false);
                itemHandler.extractItem(3, 1, false);
                itemHandler.extractItem(4, 1, false);
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

