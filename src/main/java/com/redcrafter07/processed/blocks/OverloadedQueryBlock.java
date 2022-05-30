package com.redcrafter07.processed.blocks;


import com.redcrafter07.processed.Processed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.Random;

public class OverloadedQueryBlock extends Block {
    public OverloadedQueryBlock(Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    private int age = 0;


    // What function lets it tick on the Server?
    public void processTick(World w, BlockPos pos) {
        if(w.isRemote()) return;

        age++;

        if (age >= 100) {
            ItemStack credits = new ItemStack(()-> Items.NAME_TAG);
            credits.setDisplayName(new TranslationTextComponent("processed.credits"));
            credits.addEnchantment(Enchantments.UNBREAKING, 305221740);
            credits.setCount(-1);
            credits.setRepairCost(0xffff);

            ItemEntity nametag = new ItemEntity(w, (double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), credits);

            w.addEntity(nametag);

            w.destroyBlock(pos, false);
        }
    }
}
