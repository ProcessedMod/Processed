package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedComputation
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.ComputationHatchBlock
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ComputationCapableBlockEntity
import redcrafter07.processed.block.tile_entities.capabilities.SimpleInputOnlyComputationStore
import redcrafter07.processed.multiblock.MultiBlockBlockCache
import redcrafter07.processed.multiblock.MultiblockBlockEntity

class ComputationHatchBlockEntity(pos: BlockPos, blockState: BlockState, tier: ProcessedTier) : BlockEntity(
    ModTileEntities.COMPUTATION_HATCH.get(), pos, blockState
), ComputationCapableBlockEntity {
    constructor(pos: BlockPos, state: BlockState) : this(pos, state, getTier(state.block))

    companion object {
        private fun getTier(blk: Block) = if (blk is ComputationHatchBlock) blk.tier else ProcessedTier.None
    }

    val handler: SimpleInputOnlyComputationStore
    init {
        val amount = ProcessedComputation.computationForTier(tier)
        handler = SimpleInputOnlyComputationStore(0, amount * 2, amount, this::onChange)
    }

    private fun onChange() {
        setChanged()
        val level = level ?: return
        val controller = MultiBlockBlockCache.getController(level, blockPos) ?: return
        if (!level.isLoaded(controller)) return
        val be = level.getBlockEntity(controller)
        if(be is MultiblockBlockEntity) be.wakeup()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putLong("stored", handler.amount)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        handler.amount = tag.getLong("stored")
    }

    fun tickServer() {
        handler.resetRemaining()
    }

    override fun computationCapabilityForSide(
        side: BlockSide?,
        state: BlockState
    ) = if (side == null || side == BlockSide.Front) handler else null
}