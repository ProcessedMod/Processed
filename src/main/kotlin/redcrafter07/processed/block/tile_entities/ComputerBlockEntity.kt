package redcrafter07.processed.block.tile_entities

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import redcrafter07.processed.ProcessedComputation
import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.block.machine_abstractions.ProcessedMachine

class ComputerBlockEntity(pos: BlockPos, blockState: BlockState) :
    ProcessedMachine(ModTileEntities.COMPUTER.get(), pos, blockState) {
    override val tier: ProcessedTier
        get() = ProcessedTier.Advanced

    init {
        useEnergyCapability(tier.scalePower(1024), tier.maxPower)
    }

    private var lastPushedDir: Direction? = null
    private val max = ProcessedComputation.computationForTier(tier)

    override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState): Boolean {
        if (!usePower(tier.scalePower(8))) return false

        var amountLeft = max

        lastPushedDir?.let { amountLeft = tryInsertIntoDir(level, amountLeft, it) }
        if (amountLeft >= 8) lastPushedDir = null

        for (dir in Direction.entries) {
            if (amountLeft <= 0) return true
            amountLeft = tryInsertIntoDir(level, amountLeft, dir)
        }

        return amountLeft < 8
    }

    private fun tryInsertIntoDir(level: ServerLevel, amountLeft: Long, dir: Direction): Long {
        val pos = blockPos.relative(dir)
        val cap = level.getCapability(ProcessedComputation.BLOCK, pos, dir.opposite) ?: level.getCapability(
            ProcessedComputation.BLOCK,
            pos,
            null
        ) ?: return amountLeft
        val received = cap.receiveHashes(amountLeft, false)
        if (received > 0) lastPushedDir = dir
        return amountLeft - received
    }

    // GUI Only
    override fun getDisplayName(): MutableComponent = Component.empty()
    override fun createMenu(i0: Int, i1: Inventory, i2: Player) = null
}