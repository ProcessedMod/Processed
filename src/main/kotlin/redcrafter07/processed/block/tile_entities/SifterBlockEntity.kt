package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.gui.SifterMenu
import redcrafter07.processed.recipe.ModRecipes

class SifterBlockEntity(pos: BlockPos, blockState: BlockState) :
    TieredRecipeBlockEntity(ModTileEntities.SIFTER.get(), pos, blockState) {

    init {
        useItemCapability(IoState.Input)
        useItemCapability(IoState.Output, 6)
        onTierChanged(tier, tier)
    }

    public override fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) = useScaledEnergyCapability(1000)

    override fun getRecipe(): RecipeData? {
        val level = level ?: return null

        val stack = inputItemHandler.getStackInSlot(0)
        val input = SingleRecipeInput(stack)
        val recipe = level.recipeManager.getRecipeFor(
            ModRecipes.SIFTING.type, input, level
        ).map { it.value }.orElse(null) ?: return null
        val remainingItem = stack.craftingRemainingItem
        if (remainingItem.isEmpty) {
            stack.shrink(1)
            inputItemHandler.setStackInSlot(0, stack)
        } else inputItemHandler.setStackInSlot(0, remainingItem)

        val items = recipe.getRemainingItems(input, level).toMutableList()
        val result = recipe.getResultItem(level.registryAccess())
        if (!result.isEmpty) items.add(result)

        return RecipeData(items,  recipe.processingTime, recipe.energyUsage)
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
        SifterMenu(containerId, inventory, this, data)

    override fun getDisplayName(): Component = Translations.sifterName(tier)
}