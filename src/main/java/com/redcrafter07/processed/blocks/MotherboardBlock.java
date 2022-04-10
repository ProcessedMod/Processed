package com.redcrafter07.processed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Stream;

public class MotherboardBlock extends HorizontalBlock {
    public static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(3, 1, 2, 8, 2, 5),
            Block.makeCuboidShape(4, 1, 6, 7, 2, 9),
            Block.makeCuboidShape(2, 1, 12, 3, 2, 13),
            Block.makeCuboidShape(9, 1, 9, 14, 2, 10),
            Block.makeCuboidShape(9, 1, 3, 14, 2, 4),
            Block.makeCuboidShape(9, 1, 5, 14, 2, 6),
            Block.makeCuboidShape(9, 1, 7, 14, 2, 8),
            Block.makeCuboidShape(4, 1, 11, 8, 2, 12),
            Block.makeCuboidShape(4, 1, 13, 8, 2, 14),
            Block.makeCuboidShape(0, 1, 15, 4, 2, 16),
            Block.makeCuboidShape(5, 1, 15, 7, 2, 16),
            Block.makeCuboidShape(8, 1, 15, 10, 2, 16),
            Block.makeCuboidShape(11, 1, 15, 12, 2, 16),
            Block.makeCuboidShape(13, 1, 15, 15, 2, 16),
            Block.makeCuboidShape(15, 1, 0, 16, 2, 1),
            Block.makeCuboidShape(13, 1, 0, 14, 2, 2),
            Block.makeCuboidShape(9, 1, 0, 12, 2, 2),
            Block.makeCuboidShape(1, 1, 0, 8, 2, 1),
            Block.makeCuboidShape(15, 1, 3, 16, 2, 6),
            Block.makeCuboidShape(15, 1, 7, 16, 2, 10),
            Block.makeCuboidShape(15, 1, 11, 16, 2, 13)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(2, 1, 8, 5, 2, 13),
            Block.makeCuboidShape(6, 1, 9, 9, 2, 12),
            Block.makeCuboidShape(12, 1, 13, 13, 2, 14),
            Block.makeCuboidShape(9, 1, 2, 10, 2, 7),
            Block.makeCuboidShape(3, 1, 2, 4, 2, 7),
            Block.makeCuboidShape(5, 1, 2, 6, 2, 7),
            Block.makeCuboidShape(7, 1, 2, 8, 2, 7),
            Block.makeCuboidShape(11, 1, 8, 12, 2, 12),
            Block.makeCuboidShape(13, 1, 8, 14, 2, 12),
            Block.makeCuboidShape(15, 1, 12, 16, 2, 16),
            Block.makeCuboidShape(15, 1, 9, 16, 2, 11),
            Block.makeCuboidShape(15, 1, 6, 16, 2, 8),
            Block.makeCuboidShape(15, 1, 4, 16, 2, 5),
            Block.makeCuboidShape(15, 1, 1, 16, 2, 3),
            Block.makeCuboidShape(0, 1, 0, 1, 2, 1),
            Block.makeCuboidShape(0, 1, 2, 2, 2, 3),
            Block.makeCuboidShape(0, 1, 4, 2, 2, 7),
            Block.makeCuboidShape(0, 1, 8, 1, 2, 15),
            Block.makeCuboidShape(3, 1, 0, 6, 2, 1),
            Block.makeCuboidShape(7, 1, 0, 10, 2, 1),
            Block.makeCuboidShape(11, 1, 0, 13, 2, 1)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(8, 1, 11, 13, 2, 14),
            Block.makeCuboidShape(9, 1, 7, 12, 2, 10),
            Block.makeCuboidShape(13, 1, 3, 14, 2, 4),
            Block.makeCuboidShape(2, 1, 6, 7, 2, 7),
            Block.makeCuboidShape(2, 1, 12, 7, 2, 13),
            Block.makeCuboidShape(2, 1, 10, 7, 2, 11),
            Block.makeCuboidShape(2, 1, 8, 7, 2, 9),
            Block.makeCuboidShape(8, 1, 4, 12, 2, 5),
            Block.makeCuboidShape(8, 1, 2, 12, 2, 3),
            Block.makeCuboidShape(12, 1, 0, 16, 2, 1),
            Block.makeCuboidShape(9, 1, 0, 11, 2, 1),
            Block.makeCuboidShape(6, 1, 0, 8, 2, 1),
            Block.makeCuboidShape(4, 1, 0, 5, 2, 1),
            Block.makeCuboidShape(1, 1, 0, 3, 2, 1),
            Block.makeCuboidShape(0, 1, 15, 1, 2, 16),
            Block.makeCuboidShape(2, 1, 14, 3, 2, 16),
            Block.makeCuboidShape(4, 1, 14, 7, 2, 16),
            Block.makeCuboidShape(8, 1, 15, 15, 2, 16),
            Block.makeCuboidShape(0, 1, 10, 1, 2, 13),
            Block.makeCuboidShape(0, 1, 6, 1, 2, 9),
            Block.makeCuboidShape(0, 1, 3, 1, 2, 5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 16),
            Block.makeCuboidShape(11, 1, 3, 14, 2, 8),
            Block.makeCuboidShape(7, 1, 4, 10, 2, 7),
            Block.makeCuboidShape(3, 1, 2, 4, 2, 3),
            Block.makeCuboidShape(6, 1, 9, 7, 2, 14),
            Block.makeCuboidShape(12, 1, 9, 13, 2, 14),
            Block.makeCuboidShape(10, 1, 9, 11, 2, 14),
            Block.makeCuboidShape(8, 1, 9, 9, 2, 14),
            Block.makeCuboidShape(4, 1, 4, 5, 2, 8),
            Block.makeCuboidShape(2, 1, 4, 3, 2, 8),
            Block.makeCuboidShape(0, 1, 0, 1, 2, 4),
            Block.makeCuboidShape(0, 1, 5, 1, 2, 7),
            Block.makeCuboidShape(0, 1, 8, 1, 2, 10),
            Block.makeCuboidShape(0, 1, 11, 1, 2, 12),
            Block.makeCuboidShape(0, 1, 13, 1, 2, 15),
            Block.makeCuboidShape(15, 1, 15, 16, 2, 16),
            Block.makeCuboidShape(14, 1, 13, 16, 2, 14),
            Block.makeCuboidShape(14, 1, 9, 16, 2, 12),
            Block.makeCuboidShape(15, 1, 1, 16, 2, 8),
            Block.makeCuboidShape(10, 1, 15, 13, 2, 16),
            Block.makeCuboidShape(6, 1, 15, 9, 2, 16),
            Block.makeCuboidShape(3, 1, 15, 5, 2, 16)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    public MotherboardBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(HORIZONTAL_FACING)) {
            case NORTH:
                return SHAPE_N;
            case SOUTH:
                return SHAPE_S;
            case EAST:
                return SHAPE_E;
            case WEST:
                return SHAPE_W;
            default:
                return SHAPE_N;
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(Screen.hasShiftDown())   {
            tooltip.add(new TranslationTextComponent("info.processed.motherboard"));
        }   else    {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
