package redcrafter07.processed.datagen.recipe

import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.data.DustMaterial
import redcrafter07.processed.materials.data.IngotMaterial
import redcrafter07.processed.materials.data.OreMaterial
import redcrafter07.processed.materials.isntVanilla
import redcrafter07.processed.rl
import java.util.*

class OreGenRecipeBuilder<T>(
    val material: T, baseTier: ProcessedTier, dissolver: Fluid?
) where T : OreMaterial, T : DustMaterial, T : IngotMaterial {
    val impureDustSifting = SiftingRecipeBuilder(
        material.dust().defaultInstance, sIng(material.impureDustTag()), 8, 300, baseTier
    )
    val rawCrushingRecipe = CrushingRecipeBuilder(
        s(material.impureDust(), 2), sIng(material.rawMaterialTag()), 8, 100, baseTier
    )
    val waterWashingRecipe = WashingRecipeBuilder(
        material.washedDust().defaultInstance, sIng(material.impureDustTag()), sIng(Fluids.WATER, 500), 8, 200, baseTier
    )
    val dissolverWashingRecipe = dissolver?.let {
        WashingRecipeBuilder(
            ItemStack(material.washedDust(), 3), sIng(material.impureDustTag(), 2), sIng(it, 25), 8, 320, baseTier
        )
    }

    private val washedDustBlastingRecipe: SimpleCookingRecipeBuilder = SimpleCookingRecipeBuilder.blasting(
        Ingredient.of(material.washedDustTag()), RecipeCategory.MISC, material.ingot(), 0.1f, 400
    ).unlockedBy("has_washed_dust", has(material.washedDustTag()))
    private val washedDustSmeltingRecipe: SimpleCookingRecipeBuilder = SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(material.washedDustTag()), RecipeCategory.MISC, material.ingot(), 0.1f, 400
    ).unlockedBy("has_washed_dust", has(material.washedDustTag()))

    private val purifiedDustBlastingRecipe: SimpleCookingRecipeBuilder = SimpleCookingRecipeBuilder.blasting(
        Ingredient.of(material.pureDustTag()), RecipeCategory.MISC, ItemStack(material.ingot(), 4), 0.5f, 200
    ).unlockedBy("has_pure_dust_material", has(material.pureDustTag()))
    private val purifiedDustSmeltingRecipe: SimpleCookingRecipeBuilder = SimpleCookingRecipeBuilder.smelting(
        Ingredient.of(material.pureDustTag()), RecipeCategory.MISC, ItemStack(material.ingot(), 4), 0.5f, 200
    ).unlockedBy("has_pure_dust_material", has(material.pureDustTag()))


    val purifyingRecipe = PurifyingRecipeBuilder(
        material.pureDust().defaultInstance, sIng(material.washedDustTag(), 2), 8, 160, baseTier
    )

    fun addSiftingChance(itemStack: ItemStack, chance: Int) =
        this.apply { impureDustSifting.addChanceOutput(itemStack, chance) }

    fun addPurifyingChance(itemStack: ItemStack, chance: Int) =
        this.apply { purifyingRecipe.addChanceOutput(itemStack, chance) }

    fun addDissolvedWashingChance(itemStack: ItemStack, chance: Int) =
        this.apply { dissolverWashingRecipe?.addChanceOutput(itemStack, chance) }

    fun save(output: RecipeOutput) {
        val identifier = material.identifier
        rawCrushingRecipe.save(output, rl("raw_$identifier"))
        impureDustSifting.save(output, rl("impure_${identifier}_dust"))
        waterWashingRecipe.save(output, rl("${identifier}_washing"))
        dissolverWashingRecipe?.save(output, rl("${identifier}_dissolving"))

        washedDustSmeltingRecipe.save(output, rl("${identifier}_washed_dust"))
        washedDustBlastingRecipe.save(output, rl("${identifier}_washed_dust_blasting"))

        purifiedDustSmeltingRecipe.save(output, rl("${identifier}_purified_dust"))
        purifiedDustBlastingRecipe.save(output, rl("${identifier}_purified_dust_blasting"))

        purifyingRecipe.save(output, rl("${identifier}_purifying"))

        if (isntVanilla(material.rawHolder)) {
            SimpleCookingRecipeBuilder.blasting(
                Ingredient.of(material.rawMaterialTag()), RecipeCategory.MISC, material.ingot(), 0.1f, 260
            ).unlockedBy("has_raw_material", has(material.rawMaterialTag())).save(output, "raw_${identifier}_blasting")
            SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(material.rawMaterialTag()), RecipeCategory.MISC, material.ingot(), 0.1f, 260
            ).unlockedBy("has_raw_material", has(material.rawMaterialTag())).save(output, "raw_$identifier")
        }
    }

    private fun has(tag: TagKey<Item>) = CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
        InventoryChangeTrigger.TriggerInstance(
            Optional.empty(),
            InventoryChangeTrigger.TriggerInstance.Slots.ANY,
            listOf(ItemPredicate.Builder.item().of(tag).build())
        )
    )
}