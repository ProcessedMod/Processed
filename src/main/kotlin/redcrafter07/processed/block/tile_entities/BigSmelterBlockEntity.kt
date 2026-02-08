package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.item.crafting.SingleRecipeInput
import net.minecraft.world.item.crafting.SmeltingRecipe
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.Translations
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.multiblock.AbstractRecipeMultiBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator

class BigSmelterBlockEntity(pos: BlockPos, blockState: BlockState) :
    AbstractRecipeMultiBlockEntity(ModTileEntities.BIG_SMELTER.get(), pos, blockState) {
    companion object {
        val validator = SquareMultiblockValidator.Builder(3, 3, 3).maps {
            put('w', Part.block(ModBlocks.BASIC_CASING).or(Part.ITEM_IN).or(Part.ITEM_OUT).or(Part.ENERGY_IN))
            put('c', Part.controller())
            put(' ', Part.air())
        }.restrictions {
            add(Part.ITEM_IN, 1)
            add(Part.ITEM_OUT, 1)
            add(Part.ENERGY_IN, 1)
        }.addLayer(
            "www",
            "www",
            "www",
        ).addLayer(
            "www",
            "w w",
            "wcw",
        ).addLayer(
            "www",
            "www",
            "www",
        ).build()
    }

    override fun validator() = validator

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
    override fun getDisplayName(): Component = Translations.bigSmelterName()

    override val tier: ProcessedTier = ProcessedTier.Advanced

    override fun getRecipe(level: Level): TieredRecipeBlockEntity.RecipeData? {
        if (!isAssembled) return null
        var item: ItemStack? = null
        var recipe: SmeltingRecipe? = null
        var amount = 0
        val input = specialBlocks[SpecialBlockType.ItemInput]?.map(level::getBlockEntity)
            ?.mapNotNull { if (it is InputItemHatchBlockEntity) it.handler else null } ?: listOf()

        for (handler in input) {
            for (slot in 0..<handler.slots) {
                val stack = handler.getStackInSlot(slot)
                if (stack.isEmpty) continue
                recipe = level.recipeManager.getRecipeFor(RecipeType.SMELTING, SingleRecipeInput(stack), level)
                    .map { it.value }.orElse(null) ?: continue
                item = stack.copyWithCount(1)
                amount = handler.extractItem(slot, 8, false).count
                break
            }
            if (item != null) break
        }
        if (item == null || recipe == null) return null
        val output = recipe.getRemainingItems(SingleRecipeInput(item)).toMutableList()
        output.add(recipe.assemble(SingleRecipeInput(item), level.registryAccess()))
        output.removeIf { it.isEmpty }
        output.forEach { it.count *= amount }
        return TieredRecipeBlockEntity.RecipeData(output, recipe.cookingTime, 8)
    }
}
