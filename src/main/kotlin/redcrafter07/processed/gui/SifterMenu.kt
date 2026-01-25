package redcrafter07.processed.gui

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.neoforge.items.SlotItemHandler
import redcrafter07.processed.block.SifterBlock
import redcrafter07.processed.block.tile_entities.SifterBlockEntity
import redcrafter07.processed.gui.inventory.ProcessedMachineMenu
import redcrafter07.processed.gui.inventory.SlotOutputItemHandler
import redcrafter07.processed.gui.widgets.ProgressBarWidget
import redcrafter07.processed.gui.widgets.ProgressBars

class SifterMenu(
    containerId: Int, playerInventory: Inventory, blockEntity: BlockEntity?, val data: ContainerData
) : ProcessedMachineMenu<SifterBlockEntity>(
    ModMenuTypes.SIFTER_MENU.get(), containerId, playerInventory, requireSifterBlockEntity(blockEntity)
) {
    companion object {
        fun requireSifterBlockEntity(b: BlockEntity?): SifterBlockEntity {
            if (b == null) throw IllegalStateException("no block entity found  :<")
            if (b !is SifterBlockEntity) throw IllegalStateException("non-powered furnace block entity found :<")
            return b
        }
    }

    val level: Level = playerInventory.player.level()

    init {
        addSlot(SlotItemHandler(this.blockEntity.inputItemHandler, 0, 21, 33))

        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 0, 96, 24))
        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 1, 114, 24))
        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 2, 132, 24))
        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 3, 96, 42))
        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 4, 114, 42))
        addSlot(SlotOutputItemHandler(this.blockEntity.outputItemHandler, 5, 132, 42))

        addDataSlots(data)
    }

    constructor(id: Int, inventory: Inventory, extraData: FriendlyByteBuf) : this(
        id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()), SimpleContainerData(3)
    )

    override fun getEnergy(): Int = data.get(2)

    private val progress: Double
        get() {
            val progress = data.get(0)
            val max = data.get(1)
            return if (max > 0 && progress > 0) progress.toDouble() / max.toDouble() else 0.0
        }

    override fun getProgressBar(offX: Int, offY: Int): ProgressBarWidget =
        ProgressBars.SIFTER.create(offX + 60, offY + 40, this::progress)

    override val title: Component
        get() = blockEntity.displayName

    override fun stillValid(player: Player): Boolean =
        ContainerLevelAccess.create(level, blockEntity.blockPos).evaluate { level, pos ->
            if (level.getBlockState(pos).block is SifterBlock) return@evaluate player.distanceToSqr(
                pos.x.toDouble() + 0.5,
                pos.y.toDouble() + 0.5,
                pos.z.toDouble() + 0.5,
            ) <= 64.0
            return@evaluate false
        }.orElse(false)
}