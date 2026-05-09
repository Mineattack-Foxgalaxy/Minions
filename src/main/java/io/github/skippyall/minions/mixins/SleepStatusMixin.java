package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.SleepStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SleepStatus.class)
public class SleepStatusMixin {
    @WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;isSpectator()Z"))
    public boolean excludeMinions(ServerPlayer instance, Operation<Boolean> original) {
        if (instance instanceof MinionFakePlayer minion && !minion.getData().getConfig().getOption(MinionConfigOptions.countForSleeping)) {
            return true;
        } else {
            return original.call(instance);
        }
    }
}
