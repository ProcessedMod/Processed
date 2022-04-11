package com.redcrafter07.processed.blocks;

import com.redcrafter07.processed.container.AdvancedLightningConcentratorContainer;
import com.redcrafter07.processed.container.LightningConcentratorContainer;
import com.redcrafter07.processed.tileentity.AdvancedLightningConcentratorTile;
import com.redcrafter07.processed.tileentity.LightningConcentratorTile;
import com.redcrafter07.processed.tileentity.ModTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
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

public class AdvancedLightningConcentratorBlock extends Block {
    public AdvancedLightningConcentratorBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if(!world.isRemote()) {
            TileEntity tileEntity = world.getTileEntity(blockPos);
            if(tileEntity instanceof AdvancedLightningConcentratorTile)   {
                INamedContainerProvider containerProvider = createContainerProvider(world, blockPos);

                NetworkHooks.openGui(((ServerPlayerEntity) playerEntity), containerProvider, tileEntity.getPos());
            }   else {
                throw new IllegalStateException("Container Provider missing!");
            }
        }


        return ActionResultType.SUCCESS;
    }

    private INamedContainerProvider createContainerProvider(World world, BlockPos blockPos) {
        return new INamedContainerProvider() {
            @Override
            public ITextComponent getDisplayName() {
                return new TranslationTextComponent("screen.processed.advanced_lightning_concentrator");
            }

            @Nullable
            @Override
            public Container createMenu(int p_createMenu_1_, PlayerInventory inv, PlayerEntity player) {
                return new AdvancedLightningConcentratorContainer(p_createMenu_1_, world, blockPos, inv, player);
            }
        };
    };

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return ModTileEntities.ADVANCED_LIGHTNING_CONCENTRATOR_TILE.get().create();
    }
}
