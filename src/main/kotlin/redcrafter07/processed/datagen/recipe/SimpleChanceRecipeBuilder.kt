package redcrafter07.processed.datagen.recipe

import net.minecraft.world.item.ItemStack
import redcrafter07.processed.recipe.ChanceItemRecipeOutput

abstract class SimpleChanceRecipeBuilder(result: ItemStack, prefix: String?) : SimpleRecipeBuilder(result, prefix) {
    protected val chanceResults: MutableList<ChanceItemRecipeOutput> = ArrayList()

    fun addChanceOutput(output: ChanceItemRecipeOutput) = this.apply { chanceResults.add(output) }
    fun addChanceOutput(item: ItemStack, amount: Int) = this.addChanceOutput(ChanceItemRecipeOutput(item, amount))
}