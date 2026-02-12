package redcrafter07.processed.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.neoforged.neoforge.fluids.FluidStack
import redcrafter07.processed.ProcessedTier

class TieredInput<I : RecipeInput>(val input: I, val tier: ProcessedTier) : RecipeInput {
    override fun getItem(index: Int): ItemStack = input.getItem(index)
    override fun size() = input.size()
    override fun isEmpty() = input.isEmpty

    /**
     * Returns if this machine's tier can craft a recipe of `tier`
     */
    fun canCraftRecipe(tier: ProcessedTier): Boolean = this.tier.tier >= tier.tier

    companion object {
        fun single(stack: ItemStack, tier: ProcessedTier): TieredInput<SingleRecipeInput> = TieredInput(
            SingleRecipeInput(stack), tier
        )

        fun washing(item: ItemStack, dissolver: FluidStack, tier: ProcessedTier) =
            TieredInput(WashingRecipeInput(item, dissolver), tier)
    }
}