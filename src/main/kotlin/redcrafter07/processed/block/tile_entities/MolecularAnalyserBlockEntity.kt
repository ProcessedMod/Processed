package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedComputation
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.block.tile_entities.TieredRecipeBlockEntity.RecipeData
import redcrafter07.processed.multiblock.AbstractRecipeMultiBlockEntity
import redcrafter07.processed.multiblock.Part
import redcrafter07.processed.multiblock.SquareMultiblockValidator
import redcrafter07.processed.recipe.ModRecipes
import redcrafter07.processed.recipe.TieredInput
import kotlin.math.max
import kotlin.math.min

class MolecularAnalyserBlockEntity(pos: BlockPos, blockState: BlockState) :
    AbstractRecipeMultiBlockEntity(ModTileEntities.MOLECULAR_ANALYZER.get(), pos, blockState) {
    companion object {
        val validator = SquareMultiblockValidator.Builder(7, 6, 9).maps {
            put('o', Part.block(ModBlocks.BASIC_CASING).or(Part.ENERGY_IN).or(Part.COMPUTATION_IN))
            put('c', Part.controller())
            put('q', Part.block(ModBlocks.COMPUTER))
            put('k', Part.block(Blocks.COPPER_BLOCK))
            put('a', Part.block(ModBlocks.MATERIAL_ANALYSER_CORE))
            put('i', Part.ITEM_IN)
            put('p', Part.ITEM_OUT)
            put('v', Part.block(ModBlocks.HEAT_VENT))
            put(' ', Part.ignored())
        }.restrictions {
            add(Part.ITEM_IN, 1)
            add(Part.ITEM_OUT, 1)
            add(Part.ENERGY_IN, 1)
            add(Part.COMPUTATION_IN, 1)
        }.addLayer(
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "ocooooooo",
        ).addLayer(
            "ooooooooo",
            "oqqkaaaao",
            "oqkkaaaao",
            "ooooooooo",
            "ooooooooo",
            "oo     oo",
            "oo     oo",
        ).addLayer(
            "ooooooooo",
            "oqqkaaaao",
            "oqkkaaaao",
            "ooooooooo",
            "ooooipooo",
            "oo     oo",
            "oo     oo",
        ).addLayer(
            "ooooooooo",
            "oqqkaaaao",
            "oqkkaaaao",
            "ooooooooo",
            "ooooooooo",
            "oo     oo",
            "oo     oo",
        ).addLayer(
            "ooooooooo",
            "oqqkaaaao",
            "oqkkaaaao",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "oo     oo",
        ).addLayer(
            "ooooooooo",
            "ovvvvvvvo",
            "ovvvvvvvo",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
            "ooooooooo",
        ).build()
    }

    override fun validator() = validator

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu? = null
    override fun getDisplayName(): Component = Component.empty()

    override val tier: ProcessedTier = ProcessedTier.Advanced

    override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean {
        if (recipeData == null) recipeData = getRecipe(level)

        val recipeData = recipeData ?: return setWorking(level, pos, state, false)
        if (recipeData.progress >= recipeData.maxProgress) {
            if (tryInsertRecipeOutputs(level, pos, state, recipeData)) this.recipeData = getRecipe(level)
            else return false
        }

        if (!useHashes() || !useScaledPower(recipeData.baseEnergyUsage)) {
            recipeData.progress = max(0, recipeData.progress - 2 * tier.speedMultiplier)
            setChanged()
            setWorking(level, pos, state, false)
            return recipeData.progress > 0
        }

        setWorking(level, pos, state, true)
        recipeData.progress = min(recipeData.progress + tier.speedMultiplier, recipeData.maxProgress)
        return true
    }

    fun useHashes(): Boolean {
        val amount = ProcessedComputation.computationForTier(tier)
        if (amount <= 0) return true
        if (!isAssembled) return false
        val compIn = specialBlocks[SpecialBlockType.ComputationInput] ?: return false
        val level = level ?: return false
        var remaining = amount

        for (block in compIn) {
            val be = level.getBlockEntity(block)
            if (be is ComputationHatchBlockEntity) remaining -= remaining.coerceAtMost(be.handler.amount)
            if (remaining <= 0) break
        }
        if (remaining > 0) return false
        remaining = amount
        for (block in compIn) {
            val be = level.getBlockEntity(block)
            if (be is ComputationHatchBlockEntity) {
                val toExtract = remaining.coerceAtMost(be.handler.amount)
                remaining -= toExtract
                be.handler.amount -= toExtract
            }
            if (remaining <= 0) return true
        }

        return true
    }

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
                    ModRecipes.MOLECULAR_ANALYSIS.type, input, level
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
