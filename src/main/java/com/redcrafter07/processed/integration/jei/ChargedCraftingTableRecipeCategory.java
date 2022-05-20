package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.AdvancedLightningConcentratorRecipe;
import com.redcrafter07.processed.data.recipes.ChargedCraftingTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.SlotItemHandler;

public class ChargedCraftingTableRecipeCategory implements IRecipeCategory<ChargedCraftingTableRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Processed.MOD_ID, "charged_crafting");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Processed.MOD_ID, "textures/gui/charged_crafting_table.png");
    private static final ItemStack ICON_STACK = new ItemStack(ModBlocks.CHARGED_CRAFTING_TABLE.get());
    private static final String TITLE = ModBlocks.CHARGED_CRAFTING_TABLE.get().getTranslatedName().getString();

    private final IDrawable background;
    private final IDrawable icon;

    public ChargedCraftingTableRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(ICON_STACK);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends ChargedCraftingTableRecipe> getRecipeClass() {
        return ChargedCraftingTableRecipe.class;
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


    public void setIngredients(ChargedCraftingTableRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    public void setRecipe(IRecipeLayout recipeLayout, ChargedCraftingTableRecipe recipe, IIngredients ingredients) {
        int i = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                recipeLayout.getItemStacks().init(i, true, 26 + x * 18, 16 + y * 18);
                i++;
            }
        }
        recipeLayout.getItemStacks().init(i, false, 133, 34);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
