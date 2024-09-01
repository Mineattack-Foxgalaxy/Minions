package io.github.skippyall.minions.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerSaveHandler.class)
public class Debug2Mixin {
    @Inject(method = "method_55788", at = @At("HEAD"))
    public void debug(PlayerEntity playerEntity, NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        System.out.println("loadPlayerData " + playerEntity.getNameForScoreboard());
    }

    @Inject(method = "loadPlayerData(Lnet/minecraft/entity/player/PlayerEntity;Ljava/lang/String;)Ljava/util/Optional;", at = @At("RETURN"))
    public void debug(PlayerEntity player, String extension, CallbackInfoReturnable<Optional<NbtCompound>> cir) {
        System.out.println(cir.getReturnValue().isEmpty() + player.getUuidAsString());
    }
}
