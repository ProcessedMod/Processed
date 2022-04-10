package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PowerstoneConverterBlock extends Block {
    public static final BooleanProperty PLUGGED = BooleanProperty.create("plugged");

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(15, 0, 1, 16, 1, 15),
            Block.makeCuboidShape(5, 0, 5, 11, 11, 11),
            Block.makeCuboidShape(0, 0, 0, 16, 1, 1),
            Block.makeCuboidShape(0, 0, 15, 16, 1, 16),
            Block.makeCuboidShape(0, 10, 15, 16, 11, 16),
            Block.makeCuboidShape(0, 10, 0, 16, 11, 1),
            Block.makeCuboidShape(0, 0, 1, 1, 1, 15),
            Block.makeCuboidShape(0, 10, 1, 1, 11, 15),
            Block.makeCuboidShape(15, 10, 1, 16, 11, 15),
            Block.makeCuboidShape(1, 0, 5, 5, 1, 11),
            Block.makeCuboidShape(1, 10, 5, 5, 11, 11),
            Block.makeCuboidShape(11, 10, 5, 15, 11, 11),
            Block.makeCuboidShape(11, 0, 5, 15, 1, 11),
            Block.makeCuboidShape(5, 0, 1, 11, 1, 5),
            Block.makeCuboidShape(5, 10, 1, 11, 11, 5),
            Block.makeCuboidShape(5, 10, 11, 11, 11, 15),
            Block.makeCuboidShape(5, 0, 11, 11, 1, 15),
            Block.makeCuboidShape(0, 11, 0, 16, 16, 1),
            Block.makeCuboidShape(0, 11, 15, 16, 16, 16),
            Block.makeCuboidShape(0, 11, 1, 1, 16, 15),
            Block.makeCuboidShape(15, 11, 1, 16, 16, 15)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public PowerstoneConverterBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(PLUGGED, Boolean.FALSE));
    }

    @Override
    public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        boolean plugged = blockState.get(PLUGGED);
        if (plugged) {
            return 15;
        } else {
            return 0;
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.POWERSTONE_CONVERTER_TILE.get().create();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLUGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        switch(side) {
            case UP:
            case DOWN:
                return false;
            default:
                return true;
        }
    }
}
