package redcrafter07.processed.transmitters

import redcrafter07.processed.ProcessedTier
import redcrafter07.processed.materials.Materials

object Transmitters {
    val cables = listOf(
        Pair(ProcessedTier.Basic, Materials.STEEL),
        Pair(ProcessedTier.Advanced, Materials.NICKEL),
        Pair(ProcessedTier.Nuclear, Materials.TITANIUM),
        Pair(ProcessedTier.Ultimate, Materials.URANIUM),
    )
    val transporters = listOf(
        Pair(ProcessedTier.Basic.speedMultiplier, Materials.STEEL),
        Pair(ProcessedTier.Advanced.speedMultiplier, Materials.NICKEL),
        Pair(ProcessedTier.Nuclear.speedMultiplier, Materials.TITANIUM),
        Pair(ProcessedTier.Ultimate.speedMultiplier, Materials.URANIUM),
    )
    val pipes = listOf(
        Pair(ProcessedTier.Basic.speedMultiplier * 10, Materials.STEEL),
        Pair(ProcessedTier.Advanced.speedMultiplier * 10, Materials.NICKEL),
        Pair(ProcessedTier.Nuclear.speedMultiplier * 10, Materials.TITANIUM),
        Pair(ProcessedTier.Ultimate.speedMultiplier * 10, Materials.URANIUM),
    )
}