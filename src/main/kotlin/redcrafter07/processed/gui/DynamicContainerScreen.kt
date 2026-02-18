package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu

open class DynamicContainerScreen<T: ProcessedContainerMenu>(menu: T, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<T>(menu, playerInventory, title) {
    override fun renderBg(graphics: GuiGraphics, p1: Float, p2: Int, p3: Int) {
        RenderUtils.renderDefault(this, graphics)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}