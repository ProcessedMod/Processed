package redcrafter07.processed.datagen.recipe

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.MassSpectrometerRecipe

class MassSpectrometerRecipeBuilder(
    val ingredient: Ingredient,
    val research: ResourceLocation,
    val energyUsage: Int,
    val processingTime: Int,
    val tier: ProcessedTier = ProcessedTier.Advanced
) : SimpleRecipeBuilder(ItemStack.EMPTY, "mass_spectrometry") {
    override fun getRecipe() = MassSpectrometerRecipe(ingredient, research, energyUsage, processingTime, tier)
}