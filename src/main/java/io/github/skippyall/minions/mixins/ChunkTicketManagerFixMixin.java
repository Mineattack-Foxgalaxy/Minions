package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.server.level.DistanceManager;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = DistanceManager.class)
public class ChunkTicketManagerFixMixin {
    @Shadow
    @Final
    private static Logger LOGGER;

    @WrapOperation(method = "removePlayer", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;remove(Ljava/lang/Object;)Z", remap = false))
    public boolean filterIfNull(ObjectSet instance, Object o, Operation<Boolean> original) {
        if (instance != null) {
            return original.call(instance, o);
        }

        LOGGER.error("Prevented NPE in handleChunkLeave");

        return false;//Unused
    }

    @WrapOperation(method = "removePlayer", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/objects/ObjectSet;isEmpty()Z", remap = false))
    public boolean filterIfNull(ObjectSet instance, Operation<Boolean> original) {
        if (instance != null) {
            return original.call(instance);
        }

        LOGGER.error("Prevented NPE in handleChunkLeave");

        return true;//Unused
    }
}
