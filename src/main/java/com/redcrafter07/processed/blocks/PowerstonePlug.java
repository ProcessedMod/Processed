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

public class PowerstonePlug extends Block {
    public PowerstonePlug(Properties properties) {
        super(properties);
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 1),
            Block.makeCuboidShape(0, 0, 15, 16, 1, 16),
            Block.makeCuboidShape(0, 0, 1, 1, 1, 15),
            Block.makeCuboidShape(15, 0, 1, 16, 1, 15),
            Block.makeCuboidShape(6, 0, 6, 10, 1, 10),
            Block.makeCuboidShape(10, 0, 7, 15, 1, 9),
            Block.makeCuboidShape(1, 0, 7, 6, 1, 9),
            Block.makeCuboidShape(7, 0, 1, 9, 1, 6),
            Block.makeCuboidShape(7, 0, 10, 9, 1, 15),
            Block.makeCuboidShape(7, 1, 7, 9, 12, 9),
            Block.makeCuboidShape(6, 10, 6, 10, 14, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }
}
