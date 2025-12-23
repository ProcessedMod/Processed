package redcrafter07.processed.materials.data

import redcrafter07.processed.ProcessedTier

data class ItemPipeData(val speed: Int) {
    constructor(tier: ProcessedTier) : this(tier.speedMultiplier)
}