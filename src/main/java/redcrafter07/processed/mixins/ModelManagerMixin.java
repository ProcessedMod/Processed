package redcrafter07.processed.mixins;

import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import redcrafter07.processed.dynpack.DynPackBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ModelManager.class)
class ModelManagerMixin {
    @Inject(method = "reload", at = @At("HEAD"))
    public void processed$reload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                 ResourceManager resourceManager, ProfilerFiller preparationsProfiler,
                                 ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor,
                                 CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        DynPackBuilder.INSTANCE.addMaterialBlocks();
        DynPackBuilder.INSTANCE.addMaterialItems();
    }
}