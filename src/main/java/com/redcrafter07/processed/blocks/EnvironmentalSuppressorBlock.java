package com.redcrafter07.processed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class EnvironmentalSuppressorBlock extends Block {

    public EnvironmentalSuppressorBlock(Properties properties) {
        super(properties);
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(6.5, 28, 6.5, 7, 32, 7),
            Block.makeCuboidShape(4, 0, 4, 12, 1, 12),
            Block.makeCuboidShape(7, 4, 7, 9, 5, 9),
            Block.makeCuboidShape(7, 28, 7, 9, 29, 9),
            Block.makeCuboidShape(7, 1, 7, 9, 4, 9),
            Block.makeCuboidShape(7, 5, 7, 9, 28, 9),
            Block.makeCuboidShape(7, 28, 6, 9, 32, 7),
            Block.makeCuboidShape(9, 28, 6.5, 9.5, 32, 7),
            Block.makeCuboidShape(6.5, 28, 9, 7, 32, 9.5),
            Block.makeCuboidShape(9, 28, 9, 9.5, 32, 9.5),
            Block.makeCuboidShape(6, 28, 7, 7, 32, 9),
            Block.makeCuboidShape(7, 28, 9, 9, 32, 10),
            Block.makeCuboidShape(7, 27.5, 9, 9, 28, 9.5),
            Block.makeCuboidShape(7, 27.5, 6.5, 9, 28, 7),
            Block.makeCuboidShape(9, 28, 7, 10, 32, 9),
            Block.makeCuboidShape(9, 27.5, 7, 9.5, 28, 9),
            Block.makeCuboidShape(6.5, 27.5, 7, 7, 28, 9),
            Block.makeCuboidShape(3, 0, 5, 4, 1, 11),
            Block.makeCuboidShape(12, 0, 5, 13, 1, 11),
            Block.makeCuboidShape(10, 1, 7, 11, 3, 9),
            Block.makeCuboidShape(5, 1, 7, 6, 3, 9),
            Block.makeCuboidShape(6, 1, 7, 7, 5, 9),
            Block.makeCuboidShape(9, 1, 7, 10, 5, 9),
            Block.makeCuboidShape(5, 0, 3, 11, 1, 4),
            Block.makeCuboidShape(5, 0, 12, 11, 1, 13),
            Block.makeCuboidShape(7, 1, 10, 9, 3, 11),
            Block.makeCuboidShape(9, 1, 9, 10, 3, 10),
            Block.makeCuboidShape(6, 1, 9, 7, 3, 10),
            Block.makeCuboidShape(9, 1, 6, 10, 3, 7),
            Block.makeCuboidShape(6, 1, 6, 7, 3, 7),
            Block.makeCuboidShape(7, 1, 5, 9, 3, 6),
            Block.makeCuboidShape(7, 1, 6, 9, 5, 7),
            Block.makeCuboidShape(7, 1, 9, 9, 5, 10)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        float chance = 0.5F;

        if(chance < rand.nextFloat()) {
            worldIn.addParticle(ParticleTypes.END_ROD, pos.getX()+0.5D, pos.getY() + 1D, pos.getZ()+0.5D, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D);
        }

        super.animateTick(stateIn, worldIn, pos, rand);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(Screen.hasShiftDown())   {
            tooltip.add(new TranslationTextComponent("info.processed.environmental_suppressor"));
        }   else    {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
