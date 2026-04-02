package redcrafter07.processed.integration.emi

import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.stack.EmiStack
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredHolder
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.integration.emi.recipes.EmiCrushingRecipe
import redcrafter07.processed.integration.emi.recipes.EmiMassSpectrometryRecipe
import redcrafter07.processed.integration.emi.recipes.EmiMolecularAnalysisRecipe
import redcrafter07.processed.integration.emi.recipes.EmiMolecularExtrudingRecipe
import redcrafter07.processed.integration.emi.recipes.EmiPurifyingRecipe
import redcrafter07.processed.integration.emi.recipes.EmiSiftingRecipe
import redcrafter07.processed.integration.emi.recipes.EmiWashingRecipe
import redcrafter07.processed.recipe.ModRecipes

object ProcessedEmiPlugin : EmiPlugin {
    override fun register(registry: EmiRegistry) {
        // add electric furnaces to smelting
        addWorkstations(registry, VanillaEmiRecipeCategories.SMELTING, ModBlocks.BLOCKS_POWERED_FURNACE)
        registry.addCategory(Categories.SIFTING)
        registry.addCategory(Categories.CRUSHING)
        registry.addCategory(Categories.PURIFYING)
        registry.addCategory(Categories.WASHING)
        registry.addCategory(Categories.MASS_SPECTROMETRY)
        registry.addCategory(Categories.MOLECULAR_ANALYSIS)
        registry.addCategory(Categories.MOLECULAR_EXTRUDING)
        addWorkstations(registry, Categories.SIFTING, ModBlocks.BLOCKS_SIFTER)
        addWorkstations(registry, Categories.CRUSHING, ModBlocks.BLOCKS_CRUSHER)
        addWorkstations(registry, Categories.PURIFYING, ModBlocks.BLOCKS_PURIFIER)
        addWorkstations(registry, Categories.WASHING, ModBlocks.BLOCKS_WASHER)
        registry.addWorkstation(Categories.MASS_SPECTROMETRY, EmiStack.of(ModBlocks.MASS_SPECTROMETER))
        registry.addWorkstation(Categories.MOLECULAR_ANALYSIS, EmiStack.of(ModBlocks.MOLECULAR_ANALYZER))
        registry.addWorkstation(Categories.MOLECULAR_EXTRUDING, EmiStack.of(ModBlocks.MOLECULAR_EXTRUDER))

        val recipeManager = registry.recipeManager

        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.SIFTING.type)) {
            registry.addRecipe(EmiSiftingRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.CRUSHING.type)) {
            registry.addRecipe(EmiCrushingRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.PURIFYING.type)) {
            registry.addRecipe(EmiPurifyingRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.WASHING.type)) {
            registry.addRecipe(EmiWashingRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.MASS_SPECTROMETRY.type)) {
            registry.addRecipe(EmiMassSpectrometryRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.MOLECULAR_ANALYSIS.type)) {
            registry.addRecipe(EmiMolecularAnalysisRecipe(recipe))
        }
        for (recipe in recipeManager.getAllRecipesFor(ModRecipes.MOLECULAR_EXTRUDING.type)) {
            registry.addRecipe(EmiMolecularExtrudingRecipe(recipe))
        }
    }

    fun <T : Block> addWorkstations(
        registry: EmiRegistry, category: EmiRecipeCategory, blocks: Iterable<DeferredHolder<Block, T>>
    ) {
        for (block in blocks) registry.addWorkstation(category, EmiStack.of(block.get()))
    }
}