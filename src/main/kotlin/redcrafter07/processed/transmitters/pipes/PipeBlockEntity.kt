package redcrafter07.processed.transmitters.pipes

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import redcrafter07.processed.block.machine_abstractions.BlockSide
import redcrafter07.processed.block.machine_abstractions.FluidCapableBlockEntity
import redcrafter07.processed.block.tile_entities.ModTileEntities
import redcrafter07.processed.transmitters.TransmitterBlockEntity

class PipeBlockEntity(pos: BlockPos, blockState: BlockState) :
    TransmitterBlockEntity(ModTileEntities.PIPE.get(), pos, blockState), FluidCapableBlockEntity {
    val speed = (blockState.block as? PipeBlock
        ?: throw IllegalStateException("PipeBlockEntity for non-pipe")).speed

    override fun getNetworkData(level: ServerLevel) = PipeNetworkData.getOrCreate(level)

    override fun isConnectedTo(
        level: Level, direction: Direction
    ): Boolean {
        val pos = blockPos.relative(direction)
        val be = level.getBlockEntity(pos)
        if (be is PipeBlockEntity) return !be.disallowedConnections[direction.opposite]
        return level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction.opposite) != null
    }

    override fun fluidCapabilityForSide(side: BlockSide?, state: BlockState): IFluidHandler? {
        return if (side == null) ItemHandler(this, blockPos)
        else if (connected[side.asDirectionNotRotated]) ItemHandler(this, blockPos.relative(side.asDirectionNotRotated))
        else null
    }

    // Can feed up to two max power machines
    override fun getLimit() = speed

    class ItemHandler(val transporter: PipeBlockEntity, val block: BlockPos) : IFluidHandler {
        override fun fill(
            resource: FluidStack, action: IFluidHandler.FluidAction
        ): Int {
            val sim = action == IFluidHandler.FluidAction.SIMULATE

            if (resource.isEmpty || transporter.currentCapacity <= 0) return 0
            val lvl = transporter.level ?: return 0
            if (lvl !is ServerLevel || lvl.isClientSide) return 0
            val network = transporter.getOrMakeNetwork(lvl) ?: return 0

            var remaining = resource.amount

            network.forEachEndpoint(true) { pos, dir ->
                val actualPos = pos.relative(dir)
                if (block != actualPos) {
                    if (remaining <= 0 || transporter.currentCapacity <= 0) return@forEachEndpoint false

                    val cap = lvl.getCapability(Capabilities.FluidHandler.BLOCK, actualPos, dir.opposite)
                        ?: return@forEachEndpoint true

                    val targetTransporter =
                        (lvl.getBlockEntity(pos) as? TransmitterBlockEntity ?: return@forEachEndpoint true)

                    if (targetTransporter.currentCapacity <= 0 || transporter.currentCapacity <= 0 || remaining <= 0) return@forEachEndpoint false
                    val limit = targetTransporter.remainingCapacity(transporter.remainingCapacity(remaining))
                    try {
                        val amount = cap.fill(resource.copyWithAmount(limit), action)
                        if (amount < 0) return@forEachEndpoint true
                        remaining -= amount
                        if (!sim) {
                            transporter.useCapacity(amount)
                            targetTransporter.useCapacity(amount)
                        }
                    } catch (_: Exception) {
                    }
                }
                remaining > 0 && transporter.currentCapacity > 0
            }

            val initial = resource.amount
            return if (remaining <= 0) initial
            else if (remaining >= initial) 0
            else initial - remaining
        }

        override fun getTanks() = 1
        override fun getFluidInTank(tank: Int): FluidStack = FluidStack.EMPTY
        override fun getTankCapacity(tank: Int) = transporter.currentCapacity
        override fun isFluidValid(tank: Int, stack: FluidStack) = true
        override fun drain(resource: FluidStack, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY
        override fun drain(maxDrain: Int, action: IFluidHandler.FluidAction): FluidStack = FluidStack.EMPTY

    }
}