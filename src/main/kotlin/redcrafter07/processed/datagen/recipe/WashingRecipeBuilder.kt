package redcrafter07.processed.datagen.recipe

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.WashingRecipe

class WashingRecipeBuilder(
    result: ItemStack,
    private val ingredient: SizedIngredient,
    private val dissolver: SizedFluidIngredient,
    private val energyUsage: Int,
    private val processingTime: Int,
    private val tier: ProcessedTier = ProcessedTier.Rudimentary
) : SimpleChanceRecipeBuilder(result, "washing") {
    override fun getRecipe() = WashingRecipe(
        ingredient, dissolver, result, chanceResults, energyUsage, processingTime, tier
    )
}