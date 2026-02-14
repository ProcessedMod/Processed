package redcrafter07.processed.datagen.recipe

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.crafting.SizedIngredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.CrushingRecipe

class CrushingRecipeBuilder(
    result: ItemStack,
    private val ingredient: SizedIngredient,
    private val energyUsage: Int,
    private val processingTime: Int,
    private val tier: ProcessedTier = ProcessedTier.Rudimentary
) : SimpleChanceRecipeBuilder(result, "crushing") {
    override fun getRecipe() = CrushingRecipe(
        ingredient, resultStack, chanceResults, energyUsage, processingTime, tier
    )
}