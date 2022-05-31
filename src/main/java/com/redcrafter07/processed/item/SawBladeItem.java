package com.redcrafter07.processed.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.Objects;

public class SawBladeItem extends ModItem {
    private float damageWhenHeld = 3;

    public SawBladeItem(Properties properties, String itemID, float damage) {
        super(properties, itemID);
        damageWhenHeld = damage;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if(isSelected) {
            entityIn.performHurtAnimation();

            Item item = stack.getItem();

            stack.shrink(1);

            entityIn.entityDropItem(new ItemStack(item, 1), 3F);

            entityIn.attackEntityFrom(new DamageSource("BLADE"), damageWhenHeld);

        }
    }
}
