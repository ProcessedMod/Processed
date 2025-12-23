package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.items.IItemHandler

interface ItemHatch {
    fun inventoryHandler(): IItemHandler
    fun dropContents(level: Level, pos: BlockPos)
    fun pos(): BlockPos
}