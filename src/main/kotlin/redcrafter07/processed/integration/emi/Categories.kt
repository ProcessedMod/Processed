package redcrafter07.processed.integration.emi

import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories
import dev.emi.emi.api.stack.EmiStack
import redcrafter07.processed.block.ModBlocks
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import redcrafter07.processed.gui.widgets.ProgressBars
import redcrafter07.processed.rl

object Categories {
    val SIFTING = EmiRecipeCategory(rl("sifting"), EmiStack.of(ModBlocks.BLOCKS_SIFTER.first()))
    val CRUSHING = EmiRecipeCategory(rl("crushing"), EmiStack.of(ModBlocks.BLOCKS_CRUSHER.first()))
    val PURIFYING = EmiRecipeCategory(rl("purifying"), EmiStack.of(ModBlocks.BLOCKS_PURIFIER.first()))
    val WASHING = EmiRecipeCategory(rl("washing"), EmiStack.of(ModBlocks.BLOCKS_WASHER.first()))
    val MASS_SPECTROMETRY = EmiRecipeCategory(rl("mass_spectrometry"), EmiStack.of(ModBlocks.MASS_SPECTROMETER))
    val MOLECULAR_ANALYSIS = EmiRecipeCategory(rl("molecular_analysis"), EmiStack.of(ModBlocks.MOLECULAR_ANALYZER))
    val MOLECULAR_EXTRUDING = EmiRecipeCategory(rl("molecular_extruding"), EmiStack.of(ModBlocks.MOLECULAR_EXTRUDER))

    fun fromProgressBar(progressBar: ProgressBarWidget.ProgressBarData): EmiRecipeCategory? = when (progressBar) {
        ProgressBars.POWERED_FURNACE -> VanillaEmiRecipeCategories.SMELTING
        ProgressBars.SIFTER -> SIFTING
        ProgressBars.CRUSHER -> CRUSHING
        ProgressBars.PURIFIER -> PURIFYING
        ProgressBars.WASHER -> WASHING
        else -> null
    }
}