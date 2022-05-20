package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.blocks.PowerstoneReceiverBlock;
import com.redcrafter07.processed.data.recipes.AdvancedLightningConcentratorRecipe;
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

public class AdvancedLightningConcentratorTile extends TileEntity implements ITickableTileEntity {
    private final ItemStackHandler itemHandler = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    public int fillState;

    public AdvancedLightningConcentratorTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public AdvancedLightningConcentratorTile() {
        this(ModTileEntities.ADVANCED_LIGHTNING_CONCENTRATOR_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        fillState = nbt.getInt("FillState");
        itemHandler.deserializeNBT(nbt.getCompound("advancedLightningConcentratorContents"));
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt.putInt("FillState", fillState);
        nbt.put("advancedLightningConcentratorContents", itemHandler.serializeNBT());
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

    private void lightning() {
        if (!this.world.isRemote()) {
            EntityType.LIGHTNING_BOLT.spawn((ServerWorld) world, null, null, pos, SpawnReason.TRIGGERED, true, true);
        }
    }

    public void craft() {
        Inventory inv = new Inventory(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inv.setInventorySlotContents(i, itemHandler.getStackInSlot(i));
        }

        Optional<AdvancedLightningConcentratorRecipe> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.ADVANCED_LIGHTNING_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {
            ItemStack output = iRecipe.getRecipeOutput();

            if (getTileData().getInt("FillState") >= 10000) {
                lightning();
                getTileData().putInt("FillState", getTileData().getInt("FillState") - 4000);
                itemHandler.extractItem(0, 1, false);
                itemHandler.insertItem(0, output, false);
            }

            markDirty();
        });
    }

    @Override
    public void tick() {
        int fillState = getTileData().getInt("FillState");

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        BlockState upperBlockState = world.getBlockState(new BlockPos(x, y + 1, z));

        if (upperBlockState.getBlock() ==
                ModBlocks.POWERSTONE_RECEIVER.get().getBlock()
                && upperBlockState.get(PowerstoneReceiverBlock.PLUGGED) &&
                fillState < 10000)
            getTileData().putInt("FillState", fillState + 1);

        if (!world.isRemote)

            craft();
    }
}
