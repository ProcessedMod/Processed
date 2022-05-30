package com.redcrafter07.processed.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class FishiStaff extends Item {
    public FishiStaff(Properties properties) {
        super(properties);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if(Screen.hasShiftDown())   {
            tooltip.add(new TranslationTextComponent("info.processed.fishistaff"));
        }   else    {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public boolean hitEntity(ItemStack i, LivingEntity he, LivingEntity e) {
        he.setFire(10);
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {

        if (p_77659_2_.isCreative())
            p_77659_2_.setGameType(GameType.SURVIVAL);
        else
            p_77659_2_.setGameType(GameType.CREATIVE);

        return ActionResult.resultConsume(p_77659_2_.getHeldItem(p_77659_3_));
    }
}
