package com.redcrafter07.processed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import java.util.stream.Stream;

public class TeslaCoilBlock extends Block {
    public TeslaCoilBlock(Properties properties) {
        super(properties);
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(7, 8, 7, 9, 15, 9),
            Block.makeCuboidShape(4, 1, 4, 12, 8, 12),
            Block.makeCuboidShape(5, 15, 5, 11, 19, 6),
            Block.makeCuboidShape(5, 15, 10, 11, 19, 11),
            Block.makeCuboidShape(5, 15, 6, 6, 19, 10),
            Block.makeCuboidShape(10, 15, 6, 11, 19, 10),
            Block.makeCuboidShape(9, 15.5, 6, 10, 18.5, 10),
            Block.makeCuboidShape(6, 15.5, 6, 7, 18.5, 10),
            Block.makeCuboidShape(7, 15.5, 6, 9, 18.5, 7),
            Block.makeCuboidShape(7, 15.5, 9, 9, 18.5, 10),
            Block.makeCuboidShape(6, 15, 6, 10, 15, 10),
            Block.makeCuboidShape(4, 0, 4, 6, 1, 12),
            Block.makeCuboidShape(10, 0, 4, 12, 1, 12),
            Block.makeCuboidShape(6, 0, 4, 10, 1, 6),
            Block.makeCuboidShape(6, 0, 10, 10, 1, 12),
            Block.makeCuboidShape(7.25, 0.5, 7.25, 8.75, 1, 8.75)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
