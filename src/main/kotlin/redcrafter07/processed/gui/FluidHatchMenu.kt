package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import redcrafter07.processed.block.tile_entities.FluidHatch
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu

class FluidHatchMenu(
    containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity?
) : ProcessedContainerMenu(
    ModMenuTypes.FLUID_HATCH_MENU.get(), containerId, playerInventory
) {
    val hatch = hatch(blockEntity)

    companion object {
        fun hatch(b: BlockEntity?): FluidHatch {
            if (b == null) throw IllegalStateException("no block entity found  :<")
            if (b !is FluidHatch) throw IllegalStateException("non-fluid-hatch furnace block entity found :< $b")
            return b
        }
    }

    val level: Level = playerInventory.player.level()

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos())
    )

    init {
        addFluidSlot(hatch.inventoryHandler(), 0, 0, 0)
    }


    override fun customSlotCount() = 0

    override fun stillValid(player: Player): Boolean =
        ContainerLevelAccess.create(level, hatch.pos()).evaluate { _, pos ->
            player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
            ) <= 64.0
        }.orElse(false)
}