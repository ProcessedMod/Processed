package redcrafter07.processed.mixins;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redcrafter07.processed.Attachments;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    @Inject(method = "tickChunk", at = @At("HEAD"))
    public void tickChunk$processed(LevelChunk c, int randomTickSpeed, CallbackInfo ci) {
        final var data = c.getExistingData(Attachments.INSTANCE.getCOVERS());
        if (data.isEmpty()) return;
        //noinspection DataFlowIssue
        data.get().tick(((ServerLevel) (Object) this), c.getPos());
    }
}
