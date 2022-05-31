package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.blocks.PowerstoneReceiverBlock;
import com.redcrafter07.processed.data.recipes.BlockForgeRecipe;
import com.redcrafter07.processed.data.recipes.ModRecipeTypes;
import com.redcrafter07.processed.tileentity.helper.EnergyContainer;
import com.redcrafter07.processed.tileentity.helper.InventoryContainer;
import com.redcrafter07.processed.tileentity.helper.TileHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class BlockForgeTile extends TileEntity implements ITickableTileEntity {
    private InventoryContainer inventoryContainer = createHandler();
    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventoryContainer.getHandler());
    public EnergyContainer energy = new EnergyContainer(10000, "bforgein");

    public BlockForgeTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public BlockForgeTile() {
        this(ModTileEntities.BLOCK_FORGE_TILE.get());
    }

    @Override
    public void read(BlockState blockState, CompoundNBT nbt) {
        energy.read(nbt);
        inventoryContainer.read(nbt);
        super.read(blockState, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        nbt = energy.write(nbt);
        nbt = inventoryContainer.write(nbt);
        return super.write(nbt);
    }

    private InventoryContainer createHandler() {
        InventoryContainer c = new InventoryContainer(2, "bforgeio", (slot)->64, (slot)->true);
        return c;
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
        Inventory inv = new Inventory(inventoryContainer.getHandler().getSlots());
        for (int i = 0; i < inventoryContainer.getHandler().getSlots(); i++) {
            inv.setInventorySlotContents(i, inventoryContainer.getHandler().getStackInSlot(i));
        }

        Optional<BlockForgeRecipe> recipe = world.getRecipeManager()
                .getRecipe(ModRecipeTypes.BLOCK_FORGE_RECIPE, inv, world);

        recipe.ifPresent(iRecipe -> {
            ItemStack output = iRecipe.getRecipeOutput();

            if (TileHelper.canItemBePutInSlot(inventoryContainer.getHandler(), 1, output)) {
                if (energy.decreaseFillState(10)==10) {
                    inventoryContainer.getHandler().extractItem(0, 1, false);
                    inventoryContainer.getHandler().insertItem(1, output, false);
                }
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
