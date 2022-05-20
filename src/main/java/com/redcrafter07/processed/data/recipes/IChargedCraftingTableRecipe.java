package com.redcrafter07.processed.data.recipes;

import com.redcrafter07.processed.Processed;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public interface IChargedCraftingTableRecipe extends IRecipe<Inventory> {
    //Credit: Kaupenjoe
    ResourceLocation TYPE_ID = new ResourceLocation(Processed.MOD_ID, "charged_crafting");

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
