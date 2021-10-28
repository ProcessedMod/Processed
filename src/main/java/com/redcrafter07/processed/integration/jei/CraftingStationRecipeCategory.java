package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.CraftingStationRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class CraftingStationRecipeCategory implements IRecipeCategory<CraftingStationRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Processed.MOD_ID, "processor");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Processed.MOD_ID, "textures/gui/crafting_station.png");
    private static final ItemStack ICON_STACK = new ItemStack(ModBlocks.CRAFTING_STATION.get());
    private static final String TITLE = ModBlocks.CRAFTING_STATION.get().getTranslatedName().getString();

    private final IDrawable background;
    private final IDrawable icon;

    public CraftingStationRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(ICON_STACK);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends CraftingStationRecipe> getRecipeClass() {
        return CraftingStationRecipe.class;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setIngredients(CraftingStationRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, CraftingStationRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 9, 12);
        recipeLayout.getItemStacks().init(1, true, 53, 12);
        recipeLayout.getItemStacks().init(2, false, 151, 54);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
