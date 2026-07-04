package redcrafter07.processed;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redcrafter07.processed.materials.Materials;
import redcrafter07.processed.materials.data.MaterialBase;

import java.util.List;

public record ProcessedTier(int tier, int speedMultiplier, int energyMultiplier, @NotNull MaterialBase material,
                            int color) {
    public ProcessedTier(int tier, int speedMultiplier, int energyMultiplier, @NotNull MaterialBase material,
                         ChatFormatting color) {
        this(tier, speedMultiplier, energyMultiplier, material, color.getColor() == null ? -1 : color.getColor());
    }

    private static ProcessedTier makeNextTier(ProcessedTier prev, MaterialBase material, ChatFormatting color) {
        return new ProcessedTier(prev.tier + 1, prev.speedMultiplier * 3, prev.energyMultiplier * 4, material, color);
    }

    @NotNull
    public static ProcessedTier fromTierNumber(int tier) {
        return TIERS.get(Math.clamp(tier, 0, TIERS.size()));
    }

    @Nullable
    public static ProcessedTier fromTierNumberOptional(int tier) {
        if(tier < 0 || tier >= TIERS.size()) return null;
        return TIERS.get(tier);
    }

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
        return getName().withColor(color);
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
    public static final ProcessedTier None = new ProcessedTier(-1, 0, 0, Materials.INSTANCE.getSTEEL(), -1);

    // Steam age-ish?
    @NotNull
    public static final ProcessedTier Rudimentary = new ProcessedTier(0, 1, 1, Materials.INSTANCE.getTIN(),
        ChatFormatting.GRAY);
    @NotNull
    public static final ProcessedTier Basic = makeNextTier(Rudimentary, Materials.INSTANCE.getSTEEL(),
        ChatFormatting.YELLOW);
    // Basic Fuel
    @NotNull
    public static final ProcessedTier Advanced = makeNextTier(Basic, Materials.INSTANCE.getNICKEL(),
        ChatFormatting.BLUE);
    // Advanced Fuel
    @NotNull
    public static final ProcessedTier IEnergyProMax = makeNextTier(Advanced, Materials.INSTANCE.getALUMINIUM(),
        ChatFormatting.LIGHT_PURPLE);
    // Fission and bad Fusion
    @NotNull
    public static final ProcessedTier Nuclear = makeNextTier(IEnergyProMax, Materials.INSTANCE.getURANIUM(),
        ChatFormatting.AQUA);
    // Fusion
    @NotNull
    public static final ProcessedTier Quantum = makeNextTier(Nuclear, Materials.INSTANCE.getNICKEL_TITANIUM(),
        ChatFormatting.DARK_PURPLE);
    // Void energy or sum idfk lmao
    @NotNull
    public static final ProcessedTier Void = makeNextTier(Quantum, Materials.INSTANCE.getTITANIUM(),
        ChatFormatting.GOLD);
    // Idk void energy but it uses more electricity lol
    @NotNull
    public static final ProcessedTier Ultimate = makeNextTier(Void, Materials.INSTANCE.getNAQUADAH(),
        ChatFormatting.DARK_RED);

    @NotNull
    public static final List<ProcessedTier> TIERS = List.of(Rudimentary, Basic, Advanced, IEnergyProMax, Nuclear,
        Quantum, Void, Ultimate);

    public static List<ProcessedTier> tiersFrom(ProcessedTier start) {
        return TIERS.subList(start.tier, TIERS.size());
    }

    @NotNull
    public static Codec<@NotNull ProcessedTier> CODEC = Codec.INT.xmap(ProcessedTier::fromTierNumber,
        ProcessedTier::tier);

    @NotNull
    public static StreamCodec<@NotNull ByteBuf, @NotNull ProcessedTier> STREAM_CODEC = ByteBufCodecs.INT.map(
        ProcessedTier::fromTierNumber, ProcessedTier::tier);
}