package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.tile_entities.TieredRecipeBlockEntity.RecipeData
import redcrafter07.processed.multiblock.AbstractRecipeMultiBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator
import redcrafter07.processed.recipe.ModRecipes
import redcrafter07.processed.recipe.TieredInput

class MassSpectrometerBlockEntity(pos: BlockPos, blockState: BlockState) :
    AbstractRecipeMultiBlockEntity(ModTileEntities.MASS_SPECTROMETER.get(), pos, blockState) {
    companion object {
        val validator = SquareMultiblockValidator.Builder(3, 3, 6).maps {
            put('o', Part.block(ModBlocks.BASIC_CASING).or(Part.ITEM_OUT))
            put('m', Part.block(ModBlocks.MAGNET))
            put('c', Part.controller())
            put('d', Part.block(ModBlocks.ION_DETECTOR))
            put('s', Part.block(ModBlocks.REINFORCED_GLASS))
            put('e', Part.ENERGY_IN)
            put('i', Part.ITEM_IN)
            put('v', Part.block(ModBlocks.HEAT_VENT))
        }.restrictions {
            add(Part.ITEM_IN, 1)
            add(Part.ITEM_OUT, 1)
            add(Part.ENERGY_IN, 1)
        }.addLayer(
            "oooooo",
            "oommmo",
            "ocoooo",
        ).addLayer(
            "oommmo",
            "odssse",
            "ooimmo",
        ).addLayer(
            "oooooo",
            "ovmmmo",
            "oooooo",
        ).build()
    }

    override fun validator() = validator

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
    override fun getDisplayName(): Component = Component.empty()

    override val tier: ProcessedTier = ProcessedTier.Advanced

    override fun getRecipe(level: Level): RecipeData? {
        if (!isAssembled) return null
        val input = specialBlocks[SpecialBlockType.ItemInput]?.map(level::getBlockEntity)
            ?.mapNotNull { if (it is InputItemHatchBlockEntity) it.handler else null } ?: listOf()

        for (handler in input) {
            for (slot in 0..<handler.slots) {
                val stack = handler.getStackInSlot(slot)
                if (stack.isEmpty) continue

                val input = TieredInput.single(stack, tier)
                val recipe = level.recipeManager.getRecipeFor(
                    ModRecipes.MASS_SPECTROMETRY.type, input, level
                ).map { it.value }.orElse(null) ?: continue
                val remainingItem = stack.craftingRemainingItem
                if (remainingItem.isEmpty) {
                    stack.shrink(1)
                    inputItemHandler.setStackInSlot(slot, stack)
                } else inputItemHandler.setStackInSlot(slot, remainingItem)

                val items = recipe.getRemainingItems(input, level).toMutableList()
                val result = recipe.getResultItem(level.registryAccess())
                if (!result.isEmpty) items.add(result)

                return RecipeData(items, recipe.scaledProcessingTime, recipe.energyUsage)
            }
        }
        return null
    }
}
