package redcrafter07.processed.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import redcrafter07.processed.gui.AbstractFluidContainerMenu;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "initMenu", at = @At("HEAD"))
    private void initMenu$processed(AbstractContainerMenu menu, CallbackInfo ci) {
        if (menu instanceof AbstractFluidContainerMenu fluidMenu) {
            //noinspection DataFlowIssue
            fluidMenu.setConn(((ServerPlayer) (Object) this).connection);
        }
    }
}
