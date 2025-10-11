package redcrafter07.processed;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static redcrafter07.processed.ProcessedModKt.rl;

public interface ProcessedPower {
    @NotNull ProcessedTier minTier();

    @NotNull IEnergyStorage energy();

    @NotNull BlockCapability<@NotNull ProcessedPower, @Nullable Direction> BLOCK = BlockCapability.createSided(
        rl("energy"), ProcessedPower.class);
    @NotNull ItemCapability<@NotNull ProcessedPower, @NotNull Void> ITEM = ItemCapability.createVoid(rl("energy"),
        ProcessedPower.class);
}