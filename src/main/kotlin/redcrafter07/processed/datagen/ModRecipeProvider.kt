package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.*
import net.minecraft.world.item.crafting.Ingredient
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.datagen.recipe.CrushingRecipeBuilder
import redcrafter07.processed.datagen.recipe.MassSpectrometerRecipeBuilder
import redcrafter07.processed.datagen.recipe.MolecularAnalysisRecipeBuilder
import redcrafter07.processed.datagen.recipe.MolecularExtruderRecipeBuilder
import redcrafter07.processed.datagen.recipe.OreGenRecipeBuilder
import redcrafter07.processed.datagen.recipe.s
import redcrafter07.processed.datagen.recipe.sIng
import redcrafter07.processed.fluid.ModFluids
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.MinableOreMaterial
import redcrafter07.processed.materials.data.SpaceOreMaterial
import redcrafter07.processed.rl
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        OreGenRecipeBuilder(
            Materials.URANIUM, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get()
        ).addSiftingChance(s(Materials.IRON.smallDust()), 30).addSiftingChance(s(Materials.URANIUM.smallDust()), 20)
            .addSiftingChance(s(Materials.NICKEL.smallDust()), 12)
            .addDissolvedWashingChance(s(Materials.URANIUM.washedDust()), 30)
            .addDissolvedWashingChance(s(Materials.STEEL.smallDust(), 2), 15)
            .addPurifyingChance(s(Materials.URANIUM.dust()), 20)
            .addPurifyingChance(s(Materials.TITANIUM.smallDust()), 15)
            .addPurifyingChance(s(Materials.ALUMINIUM.smallDust()), 27).save(recipeOutput)

        crushOre(Materials.URANIUM, recipeOutput)
        dustRecipes(Materials.URANIUM, recipeOutput)
        dustIngotRecipes(Materials.URANIUM, recipeOutput, 160)
        ingotFromOre(Materials.URANIUM, recipeOutput, 200)

        OreGenRecipeBuilder(
            Materials.TITANIUM, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get()
        ).save(recipeOutput)
        crushOre(Materials.TITANIUM, recipeOutput)
        dustRecipes(Materials.TITANIUM, recipeOutput)
        dustIngotRecipes(Materials.TITANIUM, recipeOutput, 160)
        ingotFromOre(Materials.TITANIUM, recipeOutput, 200)

        OreGenRecipeBuilder(Materials.NICKEL, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get()).save(recipeOutput)
        crushOre(Materials.NICKEL, recipeOutput)
        dustRecipes(Materials.NICKEL, recipeOutput)
        dustIngotRecipes(Materials.NICKEL, recipeOutput, 160)
        ingotFromOre(Materials.NICKEL, recipeOutput, 200)

        OreGenRecipeBuilder(Materials.ALUMINIUM, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get()).save(
            recipeOutput
        )
        crushOre(Materials.ALUMINIUM, recipeOutput)
        dustRecipes(Materials.ALUMINIUM, recipeOutput)
        dustIngotRecipes(Materials.ALUMINIUM, recipeOutput, 160)
        ingotFromOre(Materials.ALUMINIUM, recipeOutput, 200)

        OreGenRecipeBuilder(Materials.IRON, ProcessedTier.Rudimentary, ModFluids.FUEL.still.get()).save(recipeOutput)
        crushOre(Materials.IRON, recipeOutput)
        dustRecipes(Materials.IRON, recipeOutput)
        dustIngotRecipes(Materials.IRON, recipeOutput, 160)


        OreGenRecipeBuilder(Materials.NAQUADAH, ProcessedTier.Advanced, ModFluids.FUEL.still.get()).save(
            recipeOutput
        )
        dustRecipes(Materials.NAQUADAH, recipeOutput)
        dustIngotRecipes(Materials.NAQUADAH, recipeOutput, 160)
        research(Materials.NAQUADAH, recipeOutput)
    }

    fun crushOre(
        material: MinableOreMaterial,
        output: RecipeOutput,
        energy: Int = 8,
        time: Int = 100,
        tier: ProcessedTier = ProcessedTier.Rudimentary
    ) {
        CrushingRecipeBuilder(
            s(material.rawMaterial()), sIng(material.oreBlockItemTag()), energy, time, tier
        ).save(output, rl("${material.identifier}_ore"))
    }

    fun dustRecipes(material: DustMaterial, output: RecipeOutput) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.smallDust(), 9).requires(material.dustTag())
            .unlockedBy("has_dust", has(material.dustTag())).save(output, rl("${material.identifier}_small_dust"))
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.dust())
            .requires(Ingredient.of(material.smallDustTag()), 9).unlockedBy("has_dust", has(material.smallDustTag()))
            .save(output, rl("${material.identifier}_dust"))
    }

    fun <T> dustIngotRecipes(material: T, output: RecipeOutput, time: Int) where T : DustMaterial, T : IngotMaterial {
        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(material.dustTag()), RecipeCategory.MISC, material.ingot(), 0.1f, time
        ).unlockedBy("has_dust", has(material.dustTag())).save(output, rl("${material.identifier}_ingot_from_dust"))
        SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(material.dustTag()), RecipeCategory.MISC, material.ingot(), 0.1f, time / 2
        ).unlockedBy("has_dust", has(material.dustTag()))
            .save(output, rl("${material.identifier}_ingot_from_dust_blasting"))

        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(material.smallDustTag()), RecipeCategory.MISC, material.nugget(), 0.1f, time / 9
        ).unlockedBy("has_dust", has(material.smallDustTag()))
            .save(output, rl("${material.identifier}_nugget_from_dust"))
        SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(material.smallDustTag()), RecipeCategory.MISC, material.nugget(), 0.1f, time / 18
        ).unlockedBy("has_dust", has(material.smallDustTag()))
            .save(output, rl("${material.identifier}_nugget_from_dust_blasting"))
    }

    fun <T> ingotFromOre(material: T, output: RecipeOutput, time: Int) where T : IngotMaterial, T : MinableOreMaterial {
        SimpleCookingRecipeBuilder.smelting(
            Ingredient.of(material.oreBlockItemTag()), RecipeCategory.MISC, material.ingot(), 0.1f, time
        ).unlockedBy("has_ore", has(material.oreBlockItemTag()))
            .save(output, rl("${material.identifier}_ingot_from_ore"))
        SimpleCookingRecipeBuilder.blasting(
            Ingredient.of(material.oreBlockItemTag()), RecipeCategory.MISC, material.ingot(), 0.1f, time / 2
        ).unlockedBy("has_ore", has(material.oreBlockItemTag()))
            .save(output, rl("${material.identifier}_ingot_from_ore_blasting"))
    }

    fun <T : SpaceOreMaterial> research(
        material: T, output: RecipeOutput, spectrometryTime: Int = 1000, // 50s
        analyserTime: Int = 2400, // 2 min
        extrudingTime: Int = 15, // 0.75 s
        tier: ProcessedTier = ProcessedTier.Advanced
    ) {
        val research = rl(material.identifier + "_ore")

        MassSpectrometerRecipeBuilder(
            Ingredient.of(material.ore()), research, 32, spectrometryTime, tier
        ).save(output, rl("research_${material.identifier}_ore"))
        MolecularAnalysisRecipeBuilder(
            research, 24, analyserTime, tier
        ).save(output, rl("research_${material.identifier}_ore"))
        MolecularExtruderRecipeBuilder(
            Ingredient.of(material.ore()), s(material.rawMaterial()), research, 8, extrudingTime, tier
        ).save(output, rl("raw_${material.identifier}"))
    }
}