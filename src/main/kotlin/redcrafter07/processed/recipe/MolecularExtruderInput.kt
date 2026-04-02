package redcrafter07.processed.recipe

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput

class MolecularExtruderInput(val item: ItemStack, val research: ResourceLocation): RecipeInput {
    override fun getItem(p0: Int): ItemStack = item

    override fun size() = 1
}