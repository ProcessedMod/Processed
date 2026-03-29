package redcrafter07.processed;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static redcrafter07.processed.ProcessedModKt.rl;

public interface ProcessedComputation {
    /**
     * @param amount The amount to insert
     * @param simulate If to actually change the container or not
     * @return The amount of hashes that were inserted (if the container is full, this is 0)
     */
    long receiveHashes(long amount, boolean simulate);

    /**
     * @param amount The amount to extract
     * @param simulate If to actually change the container or not
     * @return The amount of hashes that were extracted (if the container is empty, this is 0)
     */
    long extractHashes(long amount, boolean simulate);
    long getStoredHashes();
    long getMaxStoredHashes();
    boolean canExtract();
    boolean canReceive();

    @NotNull BlockCapability<@NotNull ProcessedComputation, @Nullable Direction> BLOCK = BlockCapability.createSided(
        rl("computation"), ProcessedComputation.class);
    @NotNull ItemCapability<@NotNull ProcessedComputation, @NotNull Void> ITEM = ItemCapability.createVoid(rl("computation"),
        ProcessedComputation.class);

    static long computationForTier(ProcessedTier tier) {
        int max = (tier.tier() - ProcessedTier.Advanced.tier());
        long base = 1;
        for (int i = 0; i < max; ++i) base *= 1024;
        return 400L * base;
    }
}