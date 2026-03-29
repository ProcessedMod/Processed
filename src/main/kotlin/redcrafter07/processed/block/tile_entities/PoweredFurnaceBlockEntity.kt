package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.gui.PoweredFurnaceMenu

class PoweredFurnaceBlockEntity(pos: BlockPos, blockState: BlockState) :
    TieredRecipeBlockEntity(ModTileEntities.POWERED_FURNACE.get(), pos, blockState) {

    init {
        useItemCapability(IoState.Input)
        useItemCapability(IoState.Output)
        onTierChanged(tier, tier)
    }

    public override fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) = useScaledEnergyCapability(1024)

    override fun getRecipe(): RecipeData? {
        val level = level ?: return null

        val stack = inputItemHandler.getStackInSlot(0)
        val recipe = level.recipeManager.getRecipeFor(
            RecipeType.SMELTING, SingleRecipeInput(stack), level
        ).map { it.value }.orElse(null) ?: return null
        val remainingItem = stack.craftingRemainingItem
        if(remainingItem.isEmpty) {
            stack.shrink(1)
            inputItemHandler.setStackInSlot(0, stack)
        } else inputItemHandler.setStackInSlot(0, remainingItem)

        val result = recipe.getResultItem(level.registryAccess())

        return RecipeData(result, recipe.cookingTime, 8)
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
        PoweredFurnaceMenu(containerId, inventory, this, data)

    override fun getDisplayName(): Component = Translations.poweredFurnaceName(tier)
}