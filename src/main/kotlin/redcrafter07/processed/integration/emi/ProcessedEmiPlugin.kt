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
        addWorkstations(registry, Categories.SIFTING, ModBlocks.BLOCKS_SIFTER)
        addWorkstations(registry, Categories.CRUSHING, ModBlocks.BLOCKS_CRUSHER)
        addWorkstations(registry, Categories.PURIFYING, ModBlocks.BLOCKS_PURIFIER)
        addWorkstations(registry, Categories.WASHING, ModBlocks.BLOCKS_WASHER)

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
    }

    fun <T : Block> addWorkstations(
        registry: EmiRegistry, category: EmiRecipeCategory, blocks: Iterable<DeferredHolder<Block, T>>
    ) {
        for (block in blocks) registry.addWorkstation(category, EmiStack.of(block.get()))
    }
}