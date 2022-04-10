package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.container.ProcessorAssemblerContainer;
import com.redcrafter07.processed.tileentity.ModTileEntities;
import com.redcrafter07.processed.tileentity.ProcessorAssemblerTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import java.util.List;
import java.util.stream.Stream;

public class ProcessorAssemblerBlock extends HorizontalBlock {

    public ProcessorAssemblerBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.makeCuboidShape(1.9999999999999991, 0, 3.9999999999999982, 14, 4, 12),
            Block.makeCuboidShape(2.999999999999999, 4, 4.999999999999998, 13, 5, 11),
            Block.makeCuboidShape(3.999999999999999, 5, 5.999999999999998, 12, 9, 10),
            Block.makeCuboidShape(-8.881784197001252e-16, 9, 3.9999999999999982, 16, 16, 12),
            Block.makeCuboidShape(4.999999999999999, 16, 4.999999999999998, 11, 17, 11),
            Block.makeCuboidShape(6.499999999999999, 17, 6.499999999999998, 9.5, 17.25, 9.5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.makeCuboidShape(4.113867571225319, 0, 1.8552210324261065, 12.11386757122532, 4, 13.855221032426108),
            Block.makeCuboidShape(5.113867571225319, 4, 2.8552210324261065, 11.11386757122532, 5, 12.855221032426108),
            Block.makeCuboidShape(6.113867571225319, 5, 3.8552210324261065, 10.11386757122532, 9, 11.855221032426108),
            Block.makeCuboidShape(4.113867571225319, 9, -0.14477896757389264, 12.11386757122532, 16, 15.855221032426108),
            Block.makeCuboidShape(5.113867571225319, 16, 4.8552210324261065, 11.11386757122532, 17, 10.855221032426108),
            Block.makeCuboidShape(6.613867571225319, 17, 6.3552210324261065, 9.61386757122532, 17.25, 9.355221032426108)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.makeCuboidShape(3.9999999999999982, 0, -8.881784197001252e-16, 12, 4, 16),
            Block.makeCuboidShape(4.999999999999998, 4, -8.881784197001252e-16, 11, 5, 16),
            Block.makeCuboidShape(5.999999999999998, 5, -8.881784197001252e-16, 10, 9, 16),
            Block.makeCuboidShape(3.9999999999999982, 9, -8.881784197001252e-16, 12, 16, 16),
            Block.makeCuboidShape(4.999999999999998, 16, -8.881784197001252e-16, 11, 17, 16),
            Block.makeCuboidShape(6.499999999999998, 17, -8.881784197001252e-16, 9.5, 17.25, 9.5)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.makeCuboidShape(4.113867571225319, 0, 1.8552210324261065, 12.11386757122532, 4, 13.855221032426108),
            Block.makeCuboidShape(5.113867571225319, 4, 2.8552210324261065, 11.11386757122532, 5, 12.855221032426108),
            Block.makeCuboidShape(6.113867571225319, 5, 3.8552210324261065, 10.11386757122532, 9, 11.855221032426108),
            Block.makeCuboidShape(4.113867571225319, 9, -0.14477896757389264, 12.11386757122532, 16, 15.855221032426108),
            Block.makeCuboidShape(5.113867571225319, 16, 4.8552210324261065, 11.11386757122532, 17, 10.855221032426108),
            Block.makeCuboidShape(6.613867571225319, 17, 6.3552210324261065, 9.61386757122532, 17.25, 9.355221032426108)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        switch (state.get(HORIZONTAL_FACING)) {
            case NORTH:
            case SOUTH:
                return SHAPE_N;
            case EAST:
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
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (!world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if (tileEntity instanceof ProcessorAssemblerTile) {
                INamedContainerProvider containerProvider = createContainerProvider(world, blockPos);

                NetworkHooks.openGui(((ServerPlayerEntity) playerEntity), containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Container Provider missing!");
            }
        }


        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("info.processed.processor_assembler"));
        } else {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos blockPos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.processed.processor_assembler");
            }

            @Nullable
            @Override
            public Container createMenu(int p_createMenu_1_, PlayerInventory inv, PlayerEntity player) {
                return new ProcessorAssemblerContainer(p_createMenu_1_, world, blockPos, inv, player);
            }
        };
    }

    ;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.PROCESSOR_ASSEMBLER_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
