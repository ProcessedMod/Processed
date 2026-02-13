package redcrafter07.processed.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.Tags
import net.neoforged.neoforge.common.crafting.SizedIngredient
import redcrafter07.processed.datagen.recipe.SiftingRecipeBuilder
import redcrafter07.processed.materials.Materials
import redcrafter07.processed.rl
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) :
    RecipeProvider(output, registries) {
    override fun buildRecipes(recipeOutput: RecipeOutput) {
        SiftingRecipeBuilder(sIng(Tags.Items.GRAVELS), 8, 200)
            .addChanceOutput(s(Materials.ALUMINIUM.dust, 2), 50)
            .addChanceOutput(s(Materials.NICKEL.dust, 4), 70)
            .addChanceOutput(s(Materials.URANIUM.dust), 10)
            .addChanceOutput(s(Materials.TITANIUM.dust), 5)
            .save(recipeOutput, rl("gravel"))
    }

    private fun sIng(tag: TagKey<Item>) = SizedIngredient.of(tag, 1)
    private fun sIng(item: ItemLike) = SizedIngredient.of(item, 1)
    private fun sIng(tag: TagKey<Item>, count: Int) = SizedIngredient.of(tag, count)
    private fun sIng(item: ItemLike, count: Int) = SizedIngredient.of(item, count)
    private fun s(item: ItemLike) = item.asItem().defaultInstance
    private fun s(item: ItemLike, count: Int) = ItemStack(item.asItem(), count)
}