package redcrafter07.processed.integration.emi.recipes

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import redcrafter07.processed.ProcessedComputation
import redcrafter07.processed.gui.widgets.ProgressBars
import redcrafter07.processed.integration.emi.Categories
import redcrafter07.processed.integration.emi.EmiUtil
import redcrafter07.processed.integration.emi.widgets.ComputationWidget
import redcrafter07.processed.integration.emi.widgets.EnergyWidget
import redcrafter07.processed.integration.emi.widgets.TierButton
import redcrafter07.processed.recipe.MolecularExtruderRecipe

class EmiMolecularExtrudingRecipe(recipe: RecipeHolder<MolecularExtruderRecipe>) : EmiRecipe {
    val researchInput: EmiIngredient = EmiStack.of(recipe.value.inputStack).setChance(0f)
    val ingredient: EmiIngredient = EmiIngredient.of(recipe.value.ingredient)
    val recipeOutput: EmiStack = EmiStack.of(recipe.value.result)
    val time = recipe.value.scaledProcessingTime
    val recipeId: ResourceLocation = recipe.id
    val baseTier = recipe.value.baseTier
    val baseEnergy = recipe.value.energyUsage

    override fun getCategory() = Categories.MOLECULAR_EXTRUDING
    override fun getId(): ResourceLocation = recipeId
    override fun getInputs(): List<EmiIngredient> = listOf(researchInput)
    override fun getOutputs(): List<EmiStack> = listOf(recipeOutput)
    override fun getDisplayWidth() = 120
    override fun getDisplayHeight() = 50
    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addSlot(researchInput, 7, 15)
        widgets.addSlot(ingredient, 25, 15)
        widgets.addSlot(recipeOutput, 72, 15).recipeContext(this)

        val timeTicks = (time / baseTier.speedMultiplier).coerceAtLeast(1)
        val progress = EmiUtil.addProgress(ProgressBars.CRUSHER, widgets, 47, 20, timeTicks)
        val energyBar = widgets.add(EnergyWidget(108, 5, baseTier.scalePower(baseEnergy) * timeTicks))
        val computationBar = widgets.add(ComputationWidget(96, 5, ProcessedComputation.computationForTier(baseTier)))

        widgets.add(TierButton(0, 40, baseTier) {
            val timeTicks = (time / it.speedMultiplier).coerceAtLeast(1)
            progress.setTime(timeTicks)
            energyBar.amount = it.scalePower(baseEnergy) * timeTicks
            computationBar.amountPerTick = ProcessedComputation.computationForTier(it)
        })
    }
}