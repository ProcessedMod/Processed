package redcrafter07.processed.datagen.recipe

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.crafting.SizedIngredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.SiftingRecipe

class SiftingRecipeBuilder(
    result: ItemStack,
    private val ingredient: SizedIngredient,
    var energyUsage: Int,
    var processingTime: Int,
    var tier: ProcessedTier = ProcessedTier.Rudimentary
) : SimpleChanceRecipeBuilder(result, "sifting") {
    constructor(
        ingredient: SizedIngredient,
        energyUsage: Int,
        processingTime: Int,
        tier: ProcessedTier = ProcessedTier.Rudimentary
    ) : this(ItemStack.EMPTY, ingredient, energyUsage, processingTime, tier)

    override fun getRecipe() = SiftingRecipe(
        ingredient, resultStack, chanceResults, energyUsage, processingTime, tier
    )
}