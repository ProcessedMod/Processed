package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.SlotItemHandler
import redcrafter07.processed.block.tile_entities.InputItemHatchBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu

class InputItemHatchMenu(
    containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity?
) : ProcessedContainerMenu(
    ModMenuTypes.INPUT_HATCH_MENU.get(), containerId, playerInventory
) {
    val be = requiteBlockEntity(blockEntity)

    companion object {
        fun requiteBlockEntity(b: BlockEntity?): InputItemHatchBlockEntity {
            if (b == null) throw IllegalStateException("no block entity found  :<")
            if (b !is InputItemHatchBlockEntity) throw IllegalStateException("non-powered furnace block entity found :<")
            return b
        }

        // 166 = screen height, 94 = player inventory height
        const val SCREEN_HEIGHT = 166 - 94
        const val SCREEN_WIDTH = 176
    }

    val level: Level = playerInventory.player.level()

    init {
        val slotsRowColumn = Mth.sqrt(be.handler.slots.toFloat()).toInt()
        val size = 18 * slotsRowColumn
        val xOff = (SCREEN_WIDTH - size) / 2
        val yOff = (SCREEN_HEIGHT - size) / 2

        for (y in 0..<slotsRowColumn) {
            for (x in 0..<slotsRowColumn) {
                addSlot(SlotItemHandler(be.handler, x + y * slotsRowColumn, xOff + x * 18, yOff + y * 18))
            }
        }
    }

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos())
    )

    override fun customSlotCount(): Int = be.handler.slots

    override fun stillValid(player: Player): Boolean =
        ContainerLevelAccess.create(level, be.blockPos).evaluate { _, pos ->
            player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
            ) <= 64.0
        }.orElse(false)
}