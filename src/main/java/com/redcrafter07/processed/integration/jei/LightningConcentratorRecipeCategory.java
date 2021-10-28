package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.CraftingStationRecipe;
import com.redcrafter07.processed.data.recipes.LightningConcentratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LightningConcentratorRecipeCategory implements IRecipeCategory<LightningConcentratorRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Processed.MOD_ID, "lightning");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Processed.MOD_ID, "textures/gui/lightning_concentrator.png");
    private static final ItemStack ICON_STACK = new ItemStack(ModBlocks.LIGHTNING_CONCENTRATOR.get());
    private static final String TITLE = ModBlocks.LIGHTNING_CONCENTRATOR.get().getTranslatedName().getString();

    private final IDrawable background;
    private final IDrawable icon;

    public LightningConcentratorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(ICON_STACK);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends LightningConcentratorRecipe> getRecipeClass() {
        return LightningConcentratorRecipe.class;
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
    public void setIngredients(LightningConcentratorRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, LightningConcentratorRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 10, 17);
        recipeLayout.getItemStacks().init(1, true, 10, 53);
        recipeLayout.getItemStacks().init(2, true, 79, 35);
        recipeLayout.getItemStacks().init(3, true, 148, 17);
        recipeLayout.getItemStacks().init(4, true, 148, 53);
        recipeLayout.getItemStacks().init(5, false, 112, 34);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
