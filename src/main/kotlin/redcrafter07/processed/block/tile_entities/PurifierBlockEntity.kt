package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.gui.PurifierMenu
import redcrafter07.processed.recipe.ModRecipes
import redcrafter07.processed.recipe.TieredInput

class PurifierBlockEntity(pos: BlockPos, blockState: BlockState) :
    TieredRecipeBlockEntity(ModTileEntities.PURIFIER.get(), pos, blockState) {

    init {
        useItemCapability(IoState.Input)
        useItemCapability(IoState.Output, 6)
        onTierChanged(tier, tier)
    }

    public override fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) = useScaledEnergyCapability(1024)

    override fun getRecipe(): RecipeData? {
        val level = level ?: return null

        for (slot in 0..<inputItemHandler.slots) {
            val stack = inputItemHandler.getStackInSlot(slot)
            val input = TieredInput.single(stack, tier)
            val recipe = level.recipeManager.getRecipeFor(
                ModRecipes.PURIFYING.type, input, level
            ).map { it.value }.orElse(null) ?: return null
            val remainingItem = stack.craftingRemainingItem
            if (remainingItem.isEmpty) {
                stack.shrink(recipe.ingredient.count())
                inputItemHandler.setStackInSlot(slot, stack)
            } else inputItemHandler.setStackInSlot(slot, remainingItem)

            val items = recipe.getRemainingItems(input, level).toMutableList()
            val result = recipe.getResultItem(level.registryAccess())
            if (!result.isEmpty) items.add(result)

            return RecipeData(items, recipe.scaledProcessingTime, recipe.energyUsage)
        }
        return null
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
        PurifierMenu(containerId, inventory, this, data)

    override fun getDisplayName(): Component = Translations.purifierName(tier)
}