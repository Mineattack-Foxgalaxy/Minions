package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.world.ChunkTicketManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ChunkTicketManager.class, remap = false)
public class ChunkTicketManagerFixMixin {
    @WrapOperation(method = "handleChunkLeave", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z"))
    public boolean filterIfNull(ObjectSet instance, Object o, Operation<Boolean> original) {
        if (instance != null) {
            return original.call(instance, o);
        }

        return false;//Unused
    }

    @WrapOperation(method = "handleChunkLeave", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;isEmpty()Z"))
    public boolean filterIfNull(ObjectSet instance, Operation<Boolean> original) {
        if (instance != null) {
            return original.call(instance);
        }

        return true;//Unused
    }
}
