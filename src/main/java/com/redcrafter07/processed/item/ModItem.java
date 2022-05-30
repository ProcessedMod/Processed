package com.redcrafter07.processed.item;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public abstract class ModItem extends Item {

    private String itemID;

    public ModItem(Properties p_i48487_1_, String itemID) {
        this(p_i48487_1_);
        this.itemID = itemID;
    }

    private ModItem(Properties props) {
        super(props);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("info.processed." + this.itemID));
        } else {
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
