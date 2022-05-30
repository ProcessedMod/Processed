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

public class ModItem extends Item {

    private String itemID;

    private boolean showDescription=false;

    public ModItem(Properties props, String itemID, boolean showDescription) {
        this(props, itemID);
        this.showDescription=showDescription;
    }

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
            if(this.showDescription)
                tooltip.add(new TranslationTextComponent("item.processed." + this.itemID));
            tooltip.add(new TranslationTextComponent("info.processed.shift"));
        }

        super.addInformation(stack, worldIn, tooltip, flagIn);
    }
}
