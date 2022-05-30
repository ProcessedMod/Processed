package com.redcrafter07.processed.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class OverloadedQueryBlock extends Block {
    public OverloadedQueryBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState st, World w, BlockPos pos, PlayerEntity e, Hand h, BlockRayTraceResult rtxr) {
        if(w.isRemote()) return ActionResultType.CONSUME;

        ItemStack credits = new ItemStack(()-> Items.NAME_TAG);
        credits.setDisplayName(new TranslationTextComponent("processed.credits"));
        credits.addEnchantment(Enchantments.UNBREAKING, 305221740);

        ItemEntity nametag = new ItemEntity(w, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), credits);

        w.addEntity(nametag);

        w.destroyBlock(pos, false);

        return ActionResultType.CONSUME;
    }
}
