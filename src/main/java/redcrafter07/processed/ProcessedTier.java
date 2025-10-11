package redcrafter07.processed;

import net.minecraft.network.chat.MutableComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ProcessedTier(int tier, int speedMultiplier, int energyMultiplier) {
    @NotNull
    public String getNamed() {
        return "tier_" + tier;
    }

    @NotNull
    public MutableComponent getName() {
        return Translations.INSTANCE.tierName(tier);
    }

    @NotNull
    public MutableComponent getNameColored() {
        return Translations.INSTANCE.tierNameColored(tier);
    }

    /// The maximum amount of power a machine can take or give.
    public int getMaxPower() {
        return energyMultiplier * 32;
    }

    /// Returns the power this machine would use for basePower RF at tier 1.
    public int scalePower(int basePower) {
        return basePower * energyMultiplier;
    }

    public @NotNull ProcessedTier min(@NotNull ProcessedTier other) {
        return other.tier >= tier ? this : other;
    }

    /// Returns if a cable of this tier can insert into some target tier.
    public boolean canInsertEnergy(@NotNull ProcessedTier target) {
        return target.tier <= tier;
    }

    @NotNull
    public static ProcessedTier None = new ProcessedTier(-1, 0, 0);

    // Steam age-ish?
    @NotNull
    public static ProcessedTier Rudimentary = new ProcessedTier(0, 1, 1);
    @NotNull
    public static ProcessedTier Basic = new ProcessedTier(1, 3, 4);
    // Basic Fuel
    @NotNull
    public static ProcessedTier Advanced = new ProcessedTier(2, 9, 16);
    // Advanced Fuel
    @NotNull
    public static ProcessedTier IEnergyProMax = new ProcessedTier(3, 27, 64);
    // Fission and bad Fusion
    @NotNull
    public static ProcessedTier Nuclear = new ProcessedTier(4, 81, 256);
    // Fusion
    @NotNull
    public static ProcessedTier Quantum = new ProcessedTier(5, 243, 1024);
    // Void energy or sum idfk lmao
    @NotNull
    public static ProcessedTier Void = new ProcessedTier(6, 729, 4096);
    // Idk void energy but it uses more electricity lol
    @NotNull
    public static ProcessedTier Ultimate = new ProcessedTier(7, 2187, 16384);

    @NotNull
    public static List<ProcessedTier> TIERS = List.of(Rudimentary, Basic, Advanced, IEnergyProMax, Nuclear, Quantum,
        Void, Ultimate);
}
