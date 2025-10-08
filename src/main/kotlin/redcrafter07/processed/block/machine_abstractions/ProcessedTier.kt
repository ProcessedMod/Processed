package redcrafter07.processed.block.machine_abstractions

import net.minecraft.network.chat.MutableComponent
import redcrafter07.processed.Translations

class ProcessedTier(val tier: Int, val speedMultiplier: Int, val energyMultiplier: Int) {
    val named: String get() = "tier_$tier"
    val name: MutableComponent get() = Translations.tierName(tier)
    val nameColored: MutableComponent get() = Translations.tierNameColored(tier)

    /// 4 amps
    val maxPower: Int get() = energyMultiplier * 32

    fun powerUsageForAmps(amps: Int): Int {
        if (amps !in 1..4) throw IllegalStateException("Illegal amount of amps: $amps A")
        // 1A base power == 8 RF, 4A base power == 32 RF
        return scaledPower(8 * amps)
    }

    fun scaledPower(basePower: Int): Int = basePower * energyMultiplier

    companion object {
        val DEFAULT = ProcessedTier(-1, 0, 0)

        val TIERS = listOf(
            ProcessedTier(0, 1, 1),         // Rudimentary (Steam age-ish?)
            ProcessedTier(1, 3, 4),         // Basic
            ProcessedTier(2, 9, 16),        // Advanced (Basic Fuel)
            ProcessedTier(3, 27, 64),       // iEnergy Pro Max (Advanced Fuel)
            ProcessedTier(4, 81, 256),      // Nuclear (Fission and bad Fusion)
            ProcessedTier(5, 243, 1024),    // Quantum (Fusion)
            ProcessedTier(6, 729, 4096),    // Void    (Void energy or sum idfk lmao)
            ProcessedTier(7, 2187, 16384),  // Ultimate (Idk void energy but it uses more electricity lol)
        )
    }
}