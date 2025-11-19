package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedPowerStore
import redcrafter07.processed.block.tile_entities.capabilities.SimpleEnergyStore

class EnergyHatchBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    ModTileEntities.ENERGY_HATCH.get(), pos, blockState
), EnergyCapableBlockEntity {
    val handler = SimpleEnergyStore(ProcessedTier.Nuclear.maxPower, ProcessedTier.Nuclear.energyMultiplier, 0)
    val wrapper = ProcessedPowerStore(ProcessedTier.Nuclear, handler)

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

    override fun energyCapabilityForSide(side: BlockSide?, state: BlockState): ProcessedPower? =
        if (side == null || side == BlockSide.Front) wrapper else null

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("energy", handler.serializeNBT(registries))
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        handler.deserializeNBT(registries, tag.getCompound("energy"))
    }
}