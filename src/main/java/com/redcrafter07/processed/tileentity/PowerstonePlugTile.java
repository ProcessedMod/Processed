package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.blocks.PowerstonePlug;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PowerstonePlugTile extends TileEntity implements ITickableTileEntity {
    public static final BooleanProperty POWERED = PowerstonePlug.POWERED;

    public PowerstonePlugTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public PowerstonePlugTile()    {
        this(ModTileEntities.POWERSTONE_PLUG_TILE.get());
    }

    public boolean checkAccumulator(int x, int y, int z, World worldIn) {
        y-=1;

        return worldIn.getBlockState(new BlockPos(x,y,z)).getBlock() == ModBlocks.POWERSTONE_ACCUMULATOR.get().getBlock();
    }

    public void updateBlockState(BlockPos pos, World worldIn, BlockState state) {
        boolean powered = checkAccumulator(pos.getX(), pos.getY(), pos.getZ(), worldIn);
        if (powered) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.TRUE));
        } else {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.FALSE));
        }
    }

    @Override
    public void tick() {
        if (world.isRemote()) return;

        updateBlockState(getPos(), world, getBlockState());
    }
}
