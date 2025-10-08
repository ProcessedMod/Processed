package redcrafter07.processed.gui.widgets

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import redcrafter07.processed.Translations
import redcrafter07.processed.block.machine_abstractions.IoState
import redcrafter07.processed.gui.RenderUtils

class IoToggleButton(
    x: Int,
    y: Int,
    private val buttonName: Component,
    private var state: IoState,
    private val supportedStates: Set<IoState>,
    private val item: ItemStack?,
    private val onChange: OnChange
) : AbstractWidget(x + 1, y + 1, 20, 20, Translations.ioButtonMessage(buttonName, state)) {
    init {
        tooltip = Tooltip.create(Translations.ioButtonTooltip(state))
    }

    @OnlyIn(Dist.CLIENT)
    fun interface OnChange {
        fun onChange(button: IoToggleButton, newState: IoState)
    }


    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.blit(RenderUtils.WIDGETS_TEXTURE, x - 1, y - 1, state.id * 22, 0, 22, 22)

        if (item != null && !item.isEmpty) graphics.renderFakeItem(item, x + 2, y + 2)
    }

    public override fun isValidClickButton(button: Int): Boolean {
        return (button == 0 || button == 1) // left or right click
    }

    override fun onClick(mouseX: Double, mouseY: Double, button: Int) {
        if (button == 0) nextState()
        else previousState()
        this.onChange.onChange(this, this.state)
        this.tooltip = Tooltip.create(Translations.ioButtonTooltip(state))
        this.message = Translations.ioButtonMessage(buttonName, state)
    }

    private fun nextState() {
        do this.state = this.state.next
        while (invalidState(this.state))
    }

    private fun previousState() {
        do this.state = this.state.previous
        while (invalidState(this.state))
    }

    private fun invalidState(state: IoState?): Boolean {
        return state != IoState.None && !supportedStates.contains(state)
    }

    public override fun updateWidgetNarration(output: NarrationElementOutput) {
        this.defaultButtonNarrationText(output)
    }
}