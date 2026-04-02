package redcrafter07.processed.datagen.recipe

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.MolecularAnalysisRecipe

class MolecularAnalysisRecipeBuilder(
    val research: ResourceLocation,
    val energyUsage: Int,
    val processingTime: Int,
    val tier: ProcessedTier = ProcessedTier.Advanced
) : SimpleRecipeBuilder(ItemStack.EMPTY, "molecular_analysis") {
    override fun getRecipe() = MolecularAnalysisRecipe(research, energyUsage, processingTime, tier)
}