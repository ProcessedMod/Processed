package redcrafter07.processed.block.cable

import redcrafter07.processed.block.machine_abstractions.ProcessedTier

data class CableData(val transferRate: Int) {
    constructor(tier: ProcessedTier) : this(tier.powerUsageForAmps(4))
}