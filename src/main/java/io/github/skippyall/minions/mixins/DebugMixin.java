package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mixin(SectionedEntityCache.class)
@Mixin(Entity.class)
public class DebugMixin {
    /*@Inject(method = "forEachInBox", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ChunkSectionPos;asLong(III)J", shift = At.Shift.BEFORE, ordinal = 0))
    private void debug(Box box, LazyIterationConsumer<EntityTrackingSection<?>> consumer, CallbackInfo ci) {
        System.out.println("call");
    }*/
    @Inject(method = "readNbt", at = @At("HEAD"))
    public void debug(NbtCompound nbt, CallbackInfo ci) {
        if ((Object) this instanceof MinionFakePlayer) {
            System.out.println("readNBT");
        }
    }

    @Inject(method = "setPos", at = @At("HEAD"))
    public void debug(double x, double y, double z, CallbackInfo ci) {
        if ((Object) this instanceof MinionFakePlayer) {
            System.out.println("Set Minion Pos to " + x + " " + y + " " + z);
        }
    }
}
