package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import redcrafter07.processed.block.tile_entities.capabilities.FluidHandlerModifiable

interface FluidHatch {
    fun inventoryHandler(): FluidHandlerModifiable
    fun pos(): BlockPos
}