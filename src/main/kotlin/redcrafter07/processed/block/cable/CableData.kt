package redcrafter07.processed.block.cable

import redcrafter07.processed.ProcessedTier

data class CableData(val transferRate: Int) {
    constructor(tier: ProcessedTier) : this(tier.scalePower(32))
}