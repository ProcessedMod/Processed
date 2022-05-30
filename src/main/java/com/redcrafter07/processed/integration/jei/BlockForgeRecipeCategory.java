package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.AdvancedLightningConcentratorRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BlockForgeRecipeCategory implements IRecipeCategory<AdvancedLightningConcentratorRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Processed.MOD_ID, "block_forging");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Processed.MOD_ID, "textures/gui/advanced_lightning_concentrator.png");
    private static final ItemStack ICON_STACK = new ItemStack(ModBlocks.ADVANCED_LIGHTNING_CONCENTRATOR.get());
    private static final String TITLE = ModBlocks.ADVANCED_LIGHTNING_CONCENTRATOR.get().getTranslatedName().getString();

    private final IDrawable background;
    private final IDrawable icon;

    public BlockForgeRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(ICON_STACK);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends AdvancedLightningConcentratorRecipe> getRecipeClass() {
        return AdvancedLightningConcentratorRecipe.class;
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


    public void setIngredients(AdvancedLightningConcentratorRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    public void setRecipe(IRecipeLayout recipeLayout, AdvancedLightningConcentratorRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 79, 35);
        recipeLayout.getItemStacks().init(1, false, 112, 34);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
