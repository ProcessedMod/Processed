package com.redcrafter07.processed.integration.jei;

import com.redcrafter07.processed.Processed;
import com.redcrafter07.processed.blocks.ModBlocks;
import com.redcrafter07.processed.data.recipes.*;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;
import java.util.stream.Collectors;

@JeiPlugin
public class ProcessedJei implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Processed.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new CraftingStationRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new LightningConcentratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new ProcessorAssemblerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new AdvancedLightningConcentratorRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(
                new BlockForgeRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().world).getRecipeManager();
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.PROCESSOR_RECIPE).stream()
                        .filter(r -> r instanceof CraftingStationRecipe).collect(Collectors.toList()),
                CraftingStationRecipeCategory.UID);

        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.LIGHTNING_RECIPE).stream()
                        .filter(r -> r instanceof LightningConcentratorRecipe).collect(Collectors.toList()),
                LightningConcentratorRecipeCategory.UID);

        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.PROCESSOR_ASSEMBLY_RECIPE).stream()
                        .filter(r -> r instanceof ProcessorAssemblerRecipe).collect(Collectors.toList()),
                ProcessorAssemblerRecipeCategory.UID);
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.ADVANCED_LIGHTNING_RECIPE).stream()
                        .filter(r -> r instanceof AdvancedLightningConcentratorRecipe).collect(Collectors.toList()),
                AdvancedLightningConcentratorRecipeCategory.UID);
        /*registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.CHARGED_CRAFTING_RECIPE).stream()
                .filter(r -> r instanceof ChargedCraftingTableRecipe).collect(Collectors.toList()),
                ChargedCraftingTableRecipeCategory.UID);*/
        registration.addRecipes(rm.getRecipesForType(ModRecipeTypes.BLOCK_FORGE_RECIPE).stream()
                        .filter(r -> r instanceof BlockForgeRecipe).collect(Collectors.toList()),
                BlockForgeRecipeCategory.UID);
    }


    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(()->ModBlocks.CRAFTING_STATION.get().asItem()), CraftingStationRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(()->ModBlocks.LIGHTNING_CONCENTRATOR.get().asItem()), LightningConcentratorRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(()->ModBlocks.PROCESSOR_ASSEMBLER.get().asItem()), ProcessorAssemblerRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(()->ModBlocks.ADVANCED_LIGHTNING_CONCENTRATOR.get().asItem()), AdvancedLightningConcentratorRecipeCategory.UID);
        registration.addRecipeCatalyst(new ItemStack(()->ModBlocks.BLOCK_FORGE.get().asItem()), BlockForgeRecipeCategory.UID);
    }
}
