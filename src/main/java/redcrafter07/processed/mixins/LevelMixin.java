package redcrafter07.processed.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {
    @Inject(method = "getCapability", at = @At("RETURN"), cancellable = true)
    private static <T, C extends @Nullable Object> void getCapability(BlockCapability<T, C> cap, BlockPos pos,
                                                                      C context,
                                                                      CallbackInfoReturnable<@Nullable T> ci) {
        if (context instanceof Direction d) //noinspection unchecked
            ci.setReturnValue(wrapCapability(ci.getReturnValue(), (BlockCapability<T, Direction>) cap, d, pos));
    }

    @Inject(method = "getCapability", at = @At("RETURN"), cancellable = true)
    private static <T, C extends @Nullable Object> void getCapability(BlockCapability<T, C> cap, BlockPos pos,
                                                                      @Nullable BlockState state,
                                                                      @Nullable BlockEntity blockEntity, C context,
                                                                      CallbackInfoReturnable<@Nullable T> ci) {
        if (context instanceof Direction d) //noinspection unchecked
            ci.setReturnValue(wrapCapability(ci.getReturnValue(), (BlockCapability<T, Direction>) cap, d, pos));
    }

    @Nullable
    private static <T> T wrapCapability(@Nullable T value, BlockCapability<T, Direction> cap, Direction ctx,
                                        BlockPos pos) {
        return switch (value) {
            case IItemHandler handler when cap == Capabilities.ItemHandler.BLOCK -> value;
            case IEnergyStorage handler when cap == Capabilities.EnergyStorage.BLOCK -> value;
            case IFluidHandler handler when cap == Capabilities.FluidHandler.BLOCK -> value;
            case null, default -> value;
        };
    }
}

