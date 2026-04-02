package redcrafter07.processed.integration.emi.recipes

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.RecipeHolder
import redcrafter07.processed.gui.widgets.ProgressBars
import redcrafter07.processed.integration.emi.Categories
import redcrafter07.processed.integration.emi.EmiUtil
import redcrafter07.processed.integration.emi.widgets.EnergyWidget
import redcrafter07.processed.integration.emi.widgets.TierButton
import redcrafter07.processed.recipe.MassSpectrometerRecipe

class EmiMassSpectrometryRecipe(recipe: RecipeHolder<MassSpectrometerRecipe>) : EmiRecipe {
    val recipeInput: EmiIngredient = EmiIngredient.of(recipe.value.ingredient)
    val recipeOutput: EmiStack = EmiStack.of(recipe.value.result)
    val time = recipe.value.scaledProcessingTime
    val recipeId: ResourceLocation = recipe.id
    val baseTier = recipe.value.baseTier
    val baseEnergy = recipe.value.energyUsage

    override fun getCategory() = Categories.MASS_SPECTROMETRY
    override fun getId(): ResourceLocation = recipeId
    override fun getInputs(): List<EmiIngredient> = listOf(recipeInput)
    override fun getOutputs(): List<EmiStack> = listOf(recipeOutput)
    override fun getDisplayWidth() = 100
    override fun getDisplayHeight() = 50
    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addSlot(recipeInput, 8, 16)
        widgets.addSlot(recipeOutput, 60, 16).recipeContext(this)

        val timeTicks = (time / baseTier.speedMultiplier).coerceAtLeast(1)
        val progress = EmiUtil.addProgress(ProgressBars.CRUSHER, widgets, 35, 21, timeTicks)
        val energyBar = widgets.add(EnergyWidget(88, 5, baseTier.scalePower(baseEnergy) * timeTicks))

        widgets.add(TierButton(0, 40, baseTier) {
            val timeTicks = (time / it.speedMultiplier).coerceAtLeast(1)
            progress.setTime(timeTicks)
            energyBar.amount = it.scalePower(baseEnergy) * timeTicks
        })
    }
}