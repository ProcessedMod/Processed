package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.LightningConcentratorRecipe;
import com.redcrafter07.processed.data.recipes.ProcessorAssemblerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ProcessorAssemblerRecipeCategory implements IRecipeCategory<ProcessorAssemblerRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(Processed.MOD_ID, "processor_assembly");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Processed.MOD_ID, "textures/gui/processor_assembler.png");
    private static final ItemStack ICON_STACK = new ItemStack(ModBlocks.PROCESSOR_ASSEMBLER.get());
    private static final String TITLE = ModBlocks.PROCESSOR_ASSEMBLER.get().getTranslatedName().getString();

    private final IDrawable background;
    private final IDrawable icon;

    public ProcessorAssemblerRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(ICON_STACK);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends ProcessorAssemblerRecipe> getRecipeClass() {
        return ProcessorAssemblerRecipe.class;
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
    public void setIngredients(ProcessorAssemblerRecipe recipe, IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ProcessorAssemblerRecipe recipe, IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, false, 79, 5);
        recipeLayout.getItemStacks().init(1, true, 10, 53);
        recipeLayout.getItemStacks().init(2, true, 44, 53);
        recipeLayout.getItemStacks().init(3, true, 113, 53);
        recipeLayout.getItemStacks().init(4, true, 147, 53);

        recipeLayout.getItemStacks().set(ingredients);
    }
}
