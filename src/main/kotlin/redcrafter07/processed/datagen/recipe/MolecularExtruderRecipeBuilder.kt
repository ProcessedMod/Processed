package redcrafter07.processed.datagen.recipe

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.recipe.MolecularExtruderRecipe

class MolecularExtruderRecipeBuilder(
    val ingredient: Ingredient,
    result: ItemStack,
    val research: ResourceLocation,
    val energyUsage: Int,
    val processingTime: Int,
    val tier: ProcessedTier = ProcessedTier.Advanced
) : SimpleRecipeBuilder(result, "molecular_extruding") {
    override fun getRecipe() = MolecularExtruderRecipe(research, ingredient, resultStack, energyUsage, processingTime, tier)
}