package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.InputItemHandlerWrapper
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedItemStackHandler
import redcrafter07.processed.gui.InputItemHatchMenu
import java.util.OptionalInt

class InputItemHatchBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ModTileEntities.INPUT_ITEM_HATCH.get(), pos, blockState
), ItemCapableBlockEntity, MenuProvider {
    var handler = ProcessedItemStackHandler(4)
    var wrapper = InputItemHandlerWrapper(handler)

    init {
        handler.setOnChange(this::sync)
    }

    fun sync() {
        val level = level
        if (level is ServerLevel) {
            val state = blockState
            level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL)
            setChanged()
        }
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? = ClientboundBlockEntityDataPacket.create(this)
    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState) =
        if (side == null || side == BlockSide.Front) wrapper else null

    override fun getDisplayName(): Component = Component.empty()

    override fun createMenu(
        containerId: Int, inventory: Inventory, player: Player
    ) = InputItemHatchMenu(containerId, inventory, this)

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