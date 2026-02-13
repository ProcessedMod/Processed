package redcrafter07.processed.block.cable

import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.Materials

object Pipelikes {
    val cables = listOf(
        Pair(ProcessedTier.Basic, Materials.STEEL),
        Pair(ProcessedTier.Advanced, Materials.NICKEL),
        Pair(ProcessedTier.Nuclear, Materials.TITANIUM),
        Pair(ProcessedTier.Ultimate, Materials.URANIUM),
    )
    val itemPipes = listOf(
        Pair(ProcessedTier.Basic.speedMultiplier, Materials.STEEL),
        Pair(ProcessedTier.Advanced.speedMultiplier, Materials.NICKEL),
        Pair(ProcessedTier.Nuclear.speedMultiplier, Materials.TITANIUM),
        Pair(ProcessedTier.Ultimate.speedMultiplier, Materials.URANIUM),
    )
}