package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.OutputItemHandlerWrapper
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedItemStackHandler
import redcrafter07.processed.gui.ItemHatchMenu
import java.util.*

class OutputItemHatchBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ModTileEntities.OUTPUT_ITEM_HATCH.get(), pos, blockState
), ItemCapableBlockEntity, MenuProvider, ItemHatch {
    val handler = ProcessedItemStackHandler(4)
    val wrapper = OutputItemHandlerWrapper(handler)
    override fun inventoryHandler() = wrapper
    override fun pos(): BlockPos = blockPos
    override fun dropContents(level: Level, pos: BlockPos) = Containers.dropContents(level, pos, handler.items)

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? = ClientboundBlockEntityDataPacket.create(this)
    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState) =
        if (side == null || side == BlockSide.Front) wrapper else null

    override fun getDisplayName(): Component = Component.empty()

    override fun createMenu(
        containerId: Int, inventory: Inventory, player: Player
    ) = ItemHatchMenu(containerId, inventory, this)

    fun openMenu(player: Player): OptionalInt = player.openMenu(this) { data -> data.writeBlockPos(blockPos) }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("inventory", handler.serializeNBT(registries))
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        handler.deserializeNBT(registries, tag.getCompound("inventory"))
    }

}