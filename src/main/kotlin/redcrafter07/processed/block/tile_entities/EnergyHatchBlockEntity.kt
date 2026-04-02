package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedPower
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.EnergyHatchBlock
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.EnergyCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.ProcessedPowerStore
import redcrafter07.processed.block.tile_entities.capabilities.SimpleEnergyStore
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import redcrafter07.processed.multiblock.MultiblockBlockEntity

class EnergyHatchBlockEntity(pos: BlockPos, blockState: BlockState, tier: ProcessedTier) : BlockEntity(
    ModTileEntities.ENERGY_HATCH.get(), pos, blockState
), EnergyCapableBlockEntity {
    constructor(pos: BlockPos, state: BlockState) : this(pos, state, getTier(state.block))

    companion object {
        private fun getTier(blk: Block) = if (blk is EnergyHatchBlock) blk.tier else ProcessedTier.None
    }

    val handler = SimpleEnergyStore(tier.maxPower * 16, tier.maxPower, 0)
    val wrapper = ProcessedPowerStore(tier, handler)

    init {
        handler.setOnChange(this::onChange)
    }

    private fun onChange() {
        setChanged()
        val level = level ?: return
        val controller = MultiBlockBlockCache.getController(level, blockPos) ?: return
        if (!level.isLoaded(controller)) return
        val be = level.getBlockEntity(controller)
        if(be is MultiblockBlockEntity) be.wakeup()
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