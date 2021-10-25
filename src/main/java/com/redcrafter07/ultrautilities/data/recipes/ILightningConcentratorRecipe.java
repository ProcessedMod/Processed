package com.redcrafter07.ultrautilities.data.recipes;

import com.redcrafter07.ultrautilities.UltraUtilities;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface ILightningConcentratorRecipe extends IRecipe<Inventory> {
    //Credit: Kaupenjoe
    ResourceLocation TYPE_ID = new ResourceLocation(UltraUtilities.MOD_ID, "lightning");

    @Override
    default IRecipeType<?> getType(){
        return Registry.RECIPE_TYPE.getOptional(TYPE_ID).get();
    }

    @Override
    default boolean canFit(int width, int height) {
        return true;
    }

    @Override
    default boolean isDynamic(){
        return true;
    }
}
