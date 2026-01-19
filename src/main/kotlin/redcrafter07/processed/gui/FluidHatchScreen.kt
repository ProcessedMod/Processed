package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.gui.widgets.FluidWidget

class FluidHatchScreen(menu: FluidHatchMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<FluidHatchMenu>(menu, playerInventory, title) {
    override fun renderBg(
        guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int
    ) {
        RenderUtils.renderDefault(this, guiGraphics)
    }

    companion object {
        const val SCREEN_WIDTH = 176
    }

    override fun init() {
        super.init()

        addRenderableWidget(
            FluidWidget(
                (SCREEN_WIDTH - 60) / 2 + leftPos,
                topPos + 6,
                60,
                true,
                0,
                menu,
            )
        )
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}