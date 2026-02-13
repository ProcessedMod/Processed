package redcrafter07.processed.datagen.recipe

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.neoforged.neoforge.common.conditions.ICondition

abstract class SimpleRecipeBuilder(protected val result: ItemStack, private val prefix: String?) : RecipeBuilder {
    protected val criteria: MutableMap<String, Criterion<*>> = HashMap()
    protected val conditions: MutableList<ICondition> = ArrayList()

    override fun unlockedBy(
        name: String, criterion: Criterion<*>
    ): RecipeBuilder = this.apply { criteria[name] = criterion }

    override fun group(ignored: String?): RecipeBuilder = this

    fun addCondition(condition: ICondition) = this.apply { conditions.add(condition) }

    override fun getResult(): Item = this.result.item

    protected abstract fun getRecipe(): Recipe<*>

    @Deprecated(replaceWith = ReplaceWith("save"), message = "Save without id uses minecraft:, which is bad.")
    final override fun save(recipeOutput: RecipeOutput) = throw UnsupportedOperationException()
    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val advancement = if (criteria.isEmpty()) null else {
            val builder = recipeOutput.advancement().addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .requirements(AdvancementRequirements.Strategy.OR)
            criteria.forEach(builder::addCriterion)
            builder.build(id.withPrefix("recipes/"))
        }
        val id = if(prefix == null) id else id.withPrefix("$prefix/")
        recipeOutput.accept(id, getRecipe(), advancement, *conditions.toTypedArray())
    }
}