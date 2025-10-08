package redcrafter07.processed.gui

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import redcrafter07.processed.gui.RenderUtils.renderDefault
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu

class GenericMachineMenuScreen(menu: ProcessedMachineMenu<*>, inventory: Inventory, val component: Component) :
    AbstractContainerScreen<ProcessedMachineMenu<*>>(menu, inventory, menu.title) {
    override fun init() {
        super.init()
        addRenderableOnly(menu.getProgressBar(leftPos, topPos))
        val energyWidget = menu.getEnergyContainer(leftPos, topPos)
        if (energyWidget != null) addRenderableWidget(energyWidget)
    }

    override fun renderBg(guiGraphics: GuiGraphics, dt: Float, mouseX: Int, mouseY: Int) {
        renderDefault(this, guiGraphics)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderTooltip(guiGraphics, mouseX, mouseY)
    }
}