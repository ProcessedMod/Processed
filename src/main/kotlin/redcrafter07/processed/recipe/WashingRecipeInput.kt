package redcrafter07.processed.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

class WashingRecipeInput(val item: ItemStack, val dissolver: FluidStack): RecipeInput {
    override fun getItem(index: Int): ItemStack = item
    override fun size() = 1
}