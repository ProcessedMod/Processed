package com.redcrafter07.processed.tileentity;

import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.blocks.PowerstoneConverter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PowerstoneConverterTile extends TileEntity implements ITickableTileEntity {
    public static final BooleanProperty PLUGGED = PowerstoneConverter.PLUGGED;

    public PowerstoneConverterTile(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public PowerstoneConverterTile()    {
        this(ModTileEntities.POWERSTONE_CONVERTER_TILE.get());
    }

    public boolean checkPlug(int x, int y, int z, int radius, World worldIn) {
        x -= radius;
        y -= radius;
        z -= radius;

        int u = x;
        int v = y;
        int w = z;

        for (int k = 0; k < (radius * 2 + 1); k++) {
            x = u;
            for (int j = 0; j < (radius * 2 + 1); j++) {
                z = w;
                for (int i = 0; i < (radius * 2 + 1); i++) {
                    Block block = worldIn.getBlockState(new BlockPos(x, y, z)).getBlock();

                    if (block == ModBlocks.POWERSTONE_PLUG.get().getBlock()) {
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

    public void updateBlockState(BlockPos pos, World worldIn, BlockState state) {
        boolean plugged = checkPlug(pos.getX(), pos.getY(), pos.getZ(), 15, worldIn);
        if (plugged) {
            worldIn.setBlockState(pos, state.with(PLUGGED, Boolean.TRUE));
        } else {
            worldIn.setBlockState(pos, state.with(PLUGGED, Boolean.FALSE));
        }
    }

    @Override
    public void tick() {
        if (world.isRemote()) return;

        updateBlockState(getPos(), world, getBlockState());
    }
}
