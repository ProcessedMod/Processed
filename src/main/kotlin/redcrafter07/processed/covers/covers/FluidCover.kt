package redcrafter07.processed.covers.covers

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import redcrafter07.processed.Translations
import redcrafter07.processed.covers.Cover
import redcrafter07.processed.covers.CoverBehavior

class FluidCover(block: Block) : Cover(block) {
    override fun makeCoverBehavior(position: BlockPos, level: Level, direction: Direction) =
        Behavior(position, level, direction, this)

    class Behavior(position: BlockPos, level: Level, direction: Direction, cover: Cover) :
        CoverBehavior(position, level, direction, cover) {
        var pushing = true

        override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            tag.putBoolean("pushing", pushing)
            super.saveAdditional(tag, registries)
        }

        override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
            pushing = tag.getBoolean("pushing")
            super.loadAdditional(tag, registries)
        }

        override fun ticks() = true
        override fun tick(level: ServerLevel): Boolean {
            val self = getCapability(level, Capabilities.FluidHandler.BLOCK) ?: return false
            val other =
                getCapability(level, Capabilities.FluidHandler.BLOCK, position.relative(direction), direction.opposite)
                    ?: return false

            return if(pushing) transfer(self, other) else transfer(other, self)
        }

        private fun transfer(from: IFluidHandler, to: IFluidHandler): Boolean {
            val fluidStack = from.drain(Int.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE)
            val filled = to.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE)
            from.drain(filled, IFluidHandler.FluidAction.EXECUTE)
            return filled > 0
        }

        override fun getDisplayName(): Component = Translations.itemFluidCover()

        override fun createMenu(
            containerId: Int, inventory: Inventory, p2: Player
        ) = FluidCoverMenu(containerId, inventory, this, position, direction)
    }
}