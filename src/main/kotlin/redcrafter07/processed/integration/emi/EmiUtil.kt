package redcrafter07.processed.integration.emi

import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import redcrafter07.processed.integration.emi.widgets.ProgressWidget
import redcrafter07.processed.recipe.ChanceItemRecipeOutput

object EmiUtil {
    fun emiStack(item: ChanceItemRecipeOutput): EmiStack =
        EmiStack.of(item.item).setChance(item.probability.toFloat() / 100f)

    fun addProgress(
        data: ProgressBarWidget.ProgressBarData, widgets: WidgetHolder, x: Int, y: Int, timeTicks: Int
    ): ProgressWidget = widgets.add(ProgressWidget(x, y, data, timeTicks))
}
