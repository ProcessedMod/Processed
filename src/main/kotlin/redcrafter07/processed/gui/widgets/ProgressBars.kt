package redcrafter07.processed.gui.widgets

import redcrafter07.processed.gui.widgets.ProgressBarWidget.ProgressBarData
import redcrafter07.processed.gui.widgets.ProgressBarWidget.ProgressBarDirection
import redcrafter07.processed.rl

object ProgressBars {
    val POWERED_FURNACE: ProgressBarData = down(
        "textures/gui/progress_bars/powered_furnace.png", 8, 16, "powered_furnace"
    )
    val SIFTER = right("textures/gui/progress_bars/sifter.png", 16, 8, "sifter")

    // TODO:
    val CRUSHER = right("textures/gui/progress_bars/crusher.png", 16, 8, "crusher")
    val PURIFIER = right("textures/gui/progress_bars/purifier.png", 16, 8, "purifier")
    val WASHER = right("textures/gui/progress_bars/purifier.png", 16, 8, "washer")

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun down(path: String, width: Int, height: Int, id: String): ProgressBarData {
        return ProgressBarData(rl(path), width, height, ProgressBarDirection.Down, id)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun up(path: String, width: Int, height: Int, id: String): ProgressBarData {
        return ProgressBarData(rl(path), width, height, ProgressBarDirection.Up, id)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun right(path: String, width: Int, height: Int, id: String): ProgressBarData {
        return ProgressBarData(rl(path), width, height, ProgressBarDirection.Right, id)
    }

    /**
     * @param width This should be *half* the width of the file, as it only matches the width of the "off-state" of the progress bar. However, the file has both the on and off state and thus is double the width of one of the states
     */
    private fun left(path: String, width: Int, height: Int, id: String): ProgressBarData {
        return ProgressBarData(rl(path), width, height, ProgressBarDirection.Left, id)
    }
}