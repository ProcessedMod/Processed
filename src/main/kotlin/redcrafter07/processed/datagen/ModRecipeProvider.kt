package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.neoforged.neoforge.common.Tags
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.datagen.recipe.OreGenRecipeBuilder
import redcrafter07.processed.datagen.recipe.SiftingRecipeBuilder
import redcrafter07.processed.datagen.recipe.s
import redcrafter07.processed.datagen.recipe.sIng
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.rl
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        SiftingRecipeBuilder(sIng(Tags.Items.GRAVELS), 8, 200).addChanceOutput(s(Materials.ALUMINIUM.dust(), 2), 50)
            .addChanceOutput(s(Materials.NICKEL.dust(), 4), 70).addChanceOutput(s(Materials.URANIUM.dust()), 10)
            .addChanceOutput(s(Materials.TITANIUM.dust()), 5).save(recipeOutput, rl("gravel"))

        OreGenRecipeBuilder(Materials.URANIUM, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get())
            .addSiftingChance(s(Materials.IRON.smallDust()), 30)
            .addSiftingChance(s(Materials.URANIUM.smallDust()), 20)
            .addSiftingChance(s(Materials.NICKEL.smallDust()), 12)
            .addDissolvedWashingChance(s(Materials.URANIUM.washedDust()), 30)
            .addDissolvedWashingChance(s(Materials.STEEL.smallDust(), 2), 15)
            .addPurifyingChance(s(Materials.URANIUM.dust()), 20)
            .addPurifyingChance(s(Materials.TITANIUM.smallDust()), 15)
            .addPurifyingChance(s(Materials.ALUMINIUM.smallDust()), 27)
            .save(recipeOutput)
    }
}