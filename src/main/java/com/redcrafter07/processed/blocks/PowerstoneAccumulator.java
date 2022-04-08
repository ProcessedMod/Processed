package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.container.PowerstoneAccumulatorContainer;
import com.redcrafter07.processed.tileentity.ModTileEntities;
import com.redcrafter07.processed.tileentity.PowerstoneAccumulatorTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class PowerstoneAccumulator extends Block {
    public static final IntegerProperty FILL_STATE = IntegerProperty.create("fill_state", 0, 1500);

    public PowerstoneAccumulator(Properties properties) {
        super(properties);
        setDefaultState(getDefaultState().with(FILL_STATE, 0));
    }

    public static final VoxelShape SHAPE = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 16, 1, 1),
            Block.makeCuboidShape(0, 0, 15, 16, 1, 16),
            Block.makeCuboidShape(0, 0, 1, 1, 1, 15),
            Block.makeCuboidShape(15, 0, 1, 16, 1, 15),
            Block.makeCuboidShape(0, 15, 15, 16, 16, 16),
            Block.makeCuboidShape(0, 15, 1, 1, 16, 15),
            Block.makeCuboidShape(0, 15, 0, 16, 16, 1),
            Block.makeCuboidShape(15, 15, 1, 16, 16, 15),
            Block.makeCuboidShape(1, 1, 1, 15, 15, 15),
            Block.makeCuboidShape(2, 15, 2, 14, 16, 14)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FILL_STATE);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity instanceof PowerstoneAccumulatorTile) {
                INamedContainerProvider containerProvider = createContainerProvider(world, blockPos);

                NetworkHooks.openGui(((ServerPlayerEntity) playerEntity), containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Container Provider missing!");
            }
        }


        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos blockPos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.processed.powerstone_accumulator");
            }

            @Nullable
            @Override
            public Container createMenu(int p_createMenu_1_, PlayerInventory inv, PlayerEntity player) {
                return new PowerstoneAccumulatorContainer(p_createMenu_1_, world, blockPos, inv, player);
            }
        };
    }

    ;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.POWERSTONE_ACCUMULATOR_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
