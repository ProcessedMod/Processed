package redcrafter07.processed.block.tile_entities.capabilities

import redcrafter07.processed.ProcessedComputation

class SimpleInputOnlyComputationStore(var amount: Long, val maxAmount: Long, val maxInsertPerTick: Long, var onChange: Runnable?) :
    ProcessedComputation {
    private var remaining = maxInsertPerTick

    fun resetRemaining() {
        remaining = maxInsertPerTick
    }

    override fun receiveHashes(amount: Long, simulate: Boolean): Long {
        val prev =  this.amount
        val remainingAmount = (maxAmount - this.amount).coerceAtLeast(0L).coerceAtMost(remaining)
        if(amount <= remainingAmount) {
            if(!simulate) this.amount += amount
            return amount
        }
        if(!simulate) this.amount += remainingAmount
        if(!simulate && prev != this.amount) onChange?.run()
        return remainingAmount
    }

    override fun extractHashes(amount: Long, simulate: Boolean) = 0L
    override fun getStoredHashes() = amount
    override fun getMaxStoredHashes() = maxAmount
    override fun canExtract() = false
    override fun canReceive() = true
}