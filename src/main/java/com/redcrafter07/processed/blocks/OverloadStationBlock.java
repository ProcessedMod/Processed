package com.redcrafter07.processed.blocks;


import com.redcrafter07.processed.container.OverloadStationContainer;
import com.redcrafter07.processed.tileentity.ModTileEntities;
import com.redcrafter07.processed.tileentity.OverloadStationTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class OverloadStationBlock extends Block {
    public OverloadStationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if(!world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if(tileEntity instanceof OverloadStationTile)   {
                INamedContainerProvider containerProvider = createContainerProvider(world, blockPos);

                NetworkHooks.openGui(((ServerPlayerEntity) playerEntity), containerProvider, tileEntity.getPos());
            }   else {
                throw new IllegalStateException("Container Provider missing!");
            }
        }


        return ActionResultType.SUCCESS;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(Screen.hasShiftDown())   {
            tooltip.add(new TranslationTextComponent("info.processed.overload_station"));
        }   else    {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos blockPos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.processed.overload_station");
            }

            @Nullable
            @Override
            public Container createMenu(int p_createMenu_1_, PlayerInventory inv, PlayerEntity player) {
                return new OverloadStationContainer(p_createMenu_1_, world, blockPos, inv, player);
            }
        };
    };

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.OVERLOAD_STATION_TILE.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
