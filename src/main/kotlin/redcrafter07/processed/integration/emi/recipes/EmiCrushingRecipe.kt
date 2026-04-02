package redcrafter07.processed.integration.emi.recipes

import dev.emi.emi.api.neoforge.NeoForgeEmiIngredient
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
import redcrafter07.processed.recipe.CrushingRecipe

class EmiCrushingRecipe(recipe: RecipeHolder<CrushingRecipe>) : EmiRecipe {
    val recipeInput: EmiIngredient = NeoForgeEmiIngredient.of(recipe.value.ingredient)
    val recipeOutputs: List<EmiStack>
    val time = recipe.value.scaledProcessingTime
    val recipeId: ResourceLocation = recipe.id
    val baseTier = recipe.value.baseTier
    val baseEnergy = recipe.value.energyUsage

    init {
        val recipe = recipe.value
        val outStacks = ArrayList<EmiStack>()
        if (!recipe.guaranteedItem.isEmpty) outStacks.add(EmiStack.of(recipe.guaranteedItem))
        recipe.extraOutputs.forEach { if (!it.item.isEmpty && it.probability > 0) outStacks.add(EmiUtil.emiStack(it)) }

        val outs = Array<EmiStack>(6) { EmiStack.EMPTY }
        for (i in 0..<outStacks.size.coerceAtMost(6)) outs[i] = outStacks[i]
        recipeOutputs = outs.toList()
    }

    override fun getCategory() = Categories.CRUSHING
    override fun getId(): ResourceLocation = recipeId
    override fun getInputs(): List<EmiIngredient> = listOf(recipeInput)
    override fun getOutputs(): List<EmiStack> = recipeOutputs.toList()
    override fun getDisplayWidth() = 140
    override fun getDisplayHeight() = 50
    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addSlot(recipeInput, 8, 16)

        widgets.addSlot(recipeOutputs[0], 68, 7).recipeContext(this)
        widgets.addSlot(recipeOutputs[1], 86, 7).recipeContext(this)
        widgets.addSlot(recipeOutputs[2], 104, 7).recipeContext(this)
        widgets.addSlot(recipeOutputs[3], 68, 25).recipeContext(this)
        widgets.addSlot(recipeOutputs[4], 86, 25).recipeContext(this)
        widgets.addSlot(recipeOutputs[5], 104, 25).recipeContext(this)

        val timeTicks = (time / baseTier.speedMultiplier).coerceAtLeast(1)
        val progress = EmiUtil.addProgress(ProgressBars.CRUSHER, widgets, 39, 21, timeTicks)
        val energyBar = widgets.add(EnergyWidget(128, 5, baseTier.scalePower(baseEnergy) * timeTicks))

        widgets.add(TierButton(0, 40, baseTier) {
            val timeTicks = (time / it.speedMultiplier).coerceAtLeast(1)
            progress.setTime(timeTicks)
            energyBar.amount = it.scalePower(baseEnergy) * timeTicks
        })
    }
}