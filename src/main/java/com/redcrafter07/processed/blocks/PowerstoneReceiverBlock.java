package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PowerstoneReceiverBlock extends Block {
    public static final BooleanProperty PLUGGED = BooleanProperty.create("plugged");

    public PowerstoneReceiverBlock(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(PLUGGED,false));
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(7, 0, 7, 9, 3, 9),
            Block.makeCuboidShape(5, 0, 4, 11, 1, 5),
            Block.makeCuboidShape(5, 0, 11, 11, 1, 12),
            Block.makeCuboidShape(4, 0, 5, 5, 1, 11),
            Block.makeCuboidShape(11, 0, 5, 12, 1, 11),
            Block.makeCuboidShape(5, 0, 7, 7, 1, 9),
            Block.makeCuboidShape(9, 0, 7, 11, 1, 9),
            Block.makeCuboidShape(7, 0, 9, 9, 1, 11),
            Block.makeCuboidShape(7, 0, 5, 9, 1, 7),
            Block.makeCuboidShape(7, 3, 7, 9, 5.5, 9),
            Block.makeCuboidShape(6.5, 3, 7, 7, 5, 9),
            Block.makeCuboidShape(9, 3, 7, 9.5, 5, 9),
            Block.makeCuboidShape(7, 3, 6.5, 9, 5, 7),
            Block.makeCuboidShape(7, 3, 9, 9, 5, 9.5),
            Block.makeCuboidShape(5, 0, 5, 6, 1, 6),
            Block.makeCuboidShape(10, 0, 5, 11, 1, 6),
            Block.makeCuboidShape(9.95, 0, 10, 10.95, 1, 11),
            Block.makeCuboidShape(4.949999999999999, 0, 10, 5.949999999999999, 1, 11)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(PLUGGED);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.POWERSTONE_RECEIVER_TILE.get().create();
    }
}
