package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.OutputFluidHandlerWrapper
import redcrafter07.processed.block.tile_entities.capabilities.SimpleFluidStore
import redcrafter07.processed.gui.FluidHatchMenu
import java.util.*

class OutputFluidHatchBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ModTileEntities.OUTPUT_FLUID_HATCH.get(), pos, blockState
), FluidCapableBlockEntity, MenuProvider, FluidHatch {
    val handler = SimpleFluidStore(1, 4000)
    val wrapper = OutputFluidHandlerWrapper(handler)

    init { handler.setOnChange(this::setChanged) }

    override fun inventoryHandler() = wrapper
    override fun pos(): BlockPos = blockPos

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? = ClientboundBlockEntityDataPacket.create(this)
    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = saveWithoutMetadata(provider)

    override fun fluidCapabilityForSide(side: BlockSide?, state: BlockState) =
        if(side == null || side == BlockSide.Front) wrapper else null

    override fun getDisplayName(): Component = Component.empty()

    override fun createMenu(
        containerId: Int, inventory: Inventory, player: Player
    ) = FluidHatchMenu(containerId, inventory, this)

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