package redcrafter07.processed.gui.inventory

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine
import redcrafter07.processed.gui.widgets.EnergyBarWidget
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import java.awt.Rectangle

abstract class ProcessedMachineMenu<T : ProcessedMachine>(
    menuType: MenuType<*>?,
    containerId: Int,
    playerInventory: Inventory,
    val blockEntity: T,
) : ProcessedContainerMenu(menuType, containerId, playerInventory) {
    companion object {
        // default position (right) assuming a 176x166 gui
        val ENERGY_DEFAULT_RIGHT: Rectangle = Rectangle(158, 20, 10, 50)

        // default position (left) assuming a 176x166 gui
        val ENERGY_DEFAULT_LEFT: Rectangle = Rectangle(6, 20, 10, 50)
    }

    abstract fun getProgressBar(offX: Int, offY: Int): ProgressBarWidget

    abstract val title: Component?

    protected fun getEnergyBarPosition(): Rectangle {
        return ENERGY_DEFAULT_RIGHT
    }

    open fun getEnergy(): Int = blockEntity.energyCapability.energyStored

    fun getEnergyContainer(offX: Int, offY: Int): EnergyBarWidget? {
        val energyCapability = blockEntity.energyCapability
        if (energyCapability.maxEnergyStored < 1) return null
        val position = getEnergyBarPosition()
        return EnergyBarWidget(
            offX + position.x,
            offY + position.y,
            position.width,
            position.height,
            energyCapability.maxEnergyStored,
            this::getEnergy,
        )
    }
}