package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import redcrafter07.processed.block.LaunchControllerBlock
import redcrafter07.processed.block.tile_entities.LaunchControllerBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedContainerMenu

class LaunchControllerMenu(
    containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity?, val data: ContainerData
) : ProcessedContainerMenu(
    ModMenuTypes.LAUNCH_CONTROLLER_MENU.get(), containerId, playerInventory
) {
    val be = requireFurnaceBlockEntity(blockEntity)

    companion object {
        fun requireFurnaceBlockEntity(b: BlockEntity?): LaunchControllerBlockEntity {
            if (b == null) throw IllegalStateException("no block entity found  :<")
            if (b !is LaunchControllerBlockEntity) throw IllegalStateException("non-powered furnace block entity found :<")
            return b
        }
    }

    init {
        checkContainerSize(playerInventory, 2)
    }

    val level: Level = playerInventory.player.level()

    init {
        addDataSlots(data)
    }

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), SimpleContainerData(4)
    )

    override fun customSlotCount(): Int = 0

    override fun stillValid(player: Player): Boolean =
        ContainerLevelAccess.create(level, be.blockPos).evaluate { level, pos ->
            if (level.getBlockState(pos).block is LaunchControllerBlock) return@evaluate player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
            ) <= 64.0
            return@evaluate false
        }.orElse(false)
}