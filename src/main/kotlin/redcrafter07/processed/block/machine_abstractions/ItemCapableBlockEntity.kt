package redcrafter07.processed.block.machine_abstractions

import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.IItemHandler

interface ItemCapableBlockEntity {
    fun itemCapabilityForSide(side: BlockSide?, state: BlockState): IItemHandler?
}