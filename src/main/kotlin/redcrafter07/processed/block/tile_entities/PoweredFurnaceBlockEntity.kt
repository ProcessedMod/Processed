package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.block.machine_abstractions.TieredProcessedMachine
import redcrafter07.processed.gui.PoweredFurnaceMenu

class PoweredFurnaceBlockEntity(pos: BlockPos, blockState: BlockState) :
    TieredProcessedMachine(ModTileEntities.POWERED_FURNACE.get(), pos, blockState) {

    var progress = 0
    var maxProgress = 78

    init {
        useItemCapability(IoState.Input)
        useItemCapability(IoState.Output)
        onTierChanged(tier, tier)
    }

    val data = object : ContainerData {
        override fun get(index: Int): Int = when (index) {
            0 -> progress
            1 -> maxProgress
            2 -> energyCapability.energyStored
            else -> 0
        }

        override fun set(index: Int, value: Int) = when (index) {
            0 -> progress = value
            1 -> maxProgress = value
            2 -> energyCapability.energyStored = value
            else -> Unit
        }

        override fun getCount(): Int = 3
    }

    public override fun onTierChanged(oldTier: ProcessedTier, newTier: ProcessedTier) = useScaledEnergyCapability(1000)

    override fun commonTick(level: Level, pos: BlockPos, state: BlockState) {
        if (hasRecipeAndSync()) {
            if (!useScaledPower(8)) {
                if (progress <= 0) return
                progress -= tier.speedMultiplier * 2
                if (progress < 0) progress = 0
                setChanged(level, pos, state)
                return
            }
            progress += tier.speedMultiplier // we could also just do `maxProgress = recipe.cookingTime` in `hasRecipeAndSync`, but this takes less computation power!

            if (progress > maxProgress) {
                progress = 0
                craftItem()
            }
            setChanged(level, pos, state)
        } else if (progress != 0) {
            progress = 0
            setChanged(level, pos, state)
        }
    }

    private fun craftItem() {
        val recipe = getCurrentRecipe() ?: return
        val level = level ?: return
        val result = recipe.getResultItem(level.registryAccess())

        val ingredient: ItemStack = recipe.ingredients.first().items[0]
        if (ingredient.isEmpty) return
        val remainingItems = recipe.getRemainingItems(SingleRecipeInput(inputItemHandler.getStackInSlot(0)))
        val hasRemainingItems = !remainingItems.isEmpty() && !remainingItems.first().isEmpty
        if (hasRemainingItems) inputItemHandler.setStackInSlot(0, remainingItems.first().copy())
        else {
            val input = inputItemHandler.getStackInSlot(0)
            inputItemHandler.setStackInSlot(0, input.copyWithCount(input.count - ingredient.count))
        }

        outputItemHandler.setStackInSlot(
            0, ItemStack(result.item, outputItemHandler.getStackInSlot(0).count + result.count)
        )
    }

    private fun hasRecipeAndSync(): Boolean {
        val recipe = getCurrentRecipe() ?: return false
        val level = level ?: return false
        val result = recipe.getResultItem(level.registryAccess())
        if (!canInsertAmountIntoOutputSlot(result.count) || !canInsertItemIntoOutputSlot(result.item)) return false
        maxProgress = recipe.getCookingTime()

        return true
    }

    private fun getCurrentRecipe(): SmeltingRecipe? {
        val level = level ?: return null

        val recipe = level.recipeManager.getRecipeFor(
            RecipeType.SMELTING, SingleRecipeInput(inputItemHandler.getStackInSlot(0)), level
        ).map { it.value }.orElse(null) ?: return null

        val result = recipe.getResultItem(level.registryAccess())
        val currentOutput = outputItemHandler.getStackInSlot(0)
        if (result.count + currentOutput.count > outputItemHandler.getSlotLimit(0) || result.count + currentOutput.count > currentOutput.maxStackSize) return null
        if (!currentOutput.isEmpty) {
            if (!ItemStack.isSameItemSameComponents(currentOutput, result)) return null
        }

        val ingredient: ItemStack = recipe.ingredients.first().items[0]
        if (ingredient.isEmpty) return null
        val remainingItems = recipe.getRemainingItems(SingleRecipeInput(inputItemHandler.getStackInSlot(0)))
        val hasRemainingItems = !remainingItems.isEmpty() && !remainingItems.first().isEmpty
        if (hasRemainingItems && inputItemHandler.getStackInSlot(0).count > ingredient.count) return null

        return recipe
    }

    private fun canInsertItemIntoOutputSlot(item: Item): Boolean {
        val outputStack = outputItemHandler.getStackInSlot(0)
        return outputStack.isEmpty || outputStack.`is`(item)
    }

    private fun canInsertAmountIntoOutputSlot(count: Int): Boolean {
        val outputStack = outputItemHandler.getStackInSlot(0)
        return outputStack.count + count <= outputItemHandler.getSlotLimit(0)
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
        PoweredFurnaceMenu(containerId, inventory, this, data)

    override fun getDisplayName(): Component = Translations.poweredFurnaceName(tier)
    override fun saveAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.saveAdditional(nbt, provider)
        nbt.putInt("progress", progress)
    }

    override fun loadAdditional(nbt: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(nbt, provider)
        progress = nbt.getInt("progress")
    }
}