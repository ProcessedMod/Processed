package redcrafter07.processed.transmitters.transporters

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.items.IItemHandler
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.ItemCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.transmitters.TransmitterBlockEntity

class TransporterBlockEntity(pos: BlockPos, blockState: BlockState) :
    TransmitterBlockEntity(ModTileEntities.TRANSPORTER.get(), pos, blockState), ItemCapableBlockEntity {
    val speed = (blockState.block as? TransporterBlock
        ?: throw IllegalStateException("TransporterBlockEntity for non-transporter")).speed

    override fun getNetworkData(level: ServerLevel) = TransporterNetworkData.getOrCreate(level)

    override fun isConnectedTo(
        level: Level, direction: Direction
    ): Boolean {
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos)
        if (be is TransporterBlockEntity) return !be.disallowedConnections[direction.opposite]
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction.opposite) != null
    }

    override fun itemCapabilityForSide(side: BlockSide?, state: BlockState): IItemHandler? {
        return if (side == null) ItemHandler(this, blockPos)
        else if (connected[side.asDirectionNotRotated]) ItemHandler(this, blockPos.relative(side.asDirectionNotRotated))
        else null
    }

    // Can feed up to two max power machines
    override fun getLimit() = speed

    class ItemHandler(val transporter: TransporterBlockEntity, val block: BlockPos) : IItemHandler {
        override fun insertItem(
            slot: Int, stack: ItemStack, sim: Boolean
        ): ItemStack {
            if (stack.isEmpty || transporter.currentCapacity <= 0) return stack
            val lvl = transporter.level ?: return stack
            if (lvl !is ServerLevel || lvl.isClientSide) return stack
            val network = transporter.getOrMakeNetwork(lvl) ?: return stack

            var remaining = stack.copy()

            network.forEachEndpoint(true) { pos, dir ->
                val actualPos = pos.relative(dir)
                if (block != actualPos) {
                    if (remaining.isEmpty || transporter.currentCapacity <= 0) return@forEachEndpoint false

                    val cap = lvl.getCapability(Capabilities.ItemHandler.BLOCK, actualPos, dir.opposite)
                        ?: return@forEachEndpoint true

                    val targetTransporter =
                        (lvl.getBlockEntity(pos) as? TransmitterBlockEntity ?: return@forEachEndpoint true)

                    for (slot in 0..<cap.slots) {
                        if(targetTransporter.currentCapacity <= 0 || transporter.currentCapacity <= 0 || remaining.isEmpty) break
                        val limit = targetTransporter.remainingCapacity(transporter.remainingCapacity(remaining.count))
                        try {
                            if (remaining.count == limit) {
                                remaining = cap.insertItem(slot, remaining, sim)
                                if (!sim) {
                                    transporter.useCapacity(limit - remaining.count)
                                    targetTransporter.useCapacity(limit - remaining.count)
                                }
                            } else {
                                var item = remaining.copyWithCount(limit)
                                item = cap.insertItem(slot, item, sim)
                                if (!sim) {
                                    transporter.useCapacity(limit - item.count)
                                    targetTransporter.useCapacity(limit - item.count)
                                }

                                remaining.shrink(limit - item.count)
                            }
                        } catch (_: Exception) {
                        }
                    }
                }
                !remaining.isEmpty && transporter.currentCapacity > 0
            }

            return remaining
        }

        override fun getSlots() = 1
        override fun getStackInSlot(slot: Int): ItemStack = ItemStack.EMPTY
        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack = ItemStack.EMPTY
        override fun getSlotLimit(slot: Int) = transporter.currentCapacity
        override fun isItemValid(slot: Int, stack: ItemStack) = true
    }
}