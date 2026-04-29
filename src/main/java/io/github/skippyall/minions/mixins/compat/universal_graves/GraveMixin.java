package io.github.skippyall.minions.mixins.compat.universal_graves;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.graves.grave.Grave;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Grave.class)
public class GraveMixin {
    @ModifyArg(method = "createBlock", at = @At(value = "INVOKE", target = "Leu/pb4/graves/grave/Grave;<init>(JLcom/mojang/authlib/GameProfile;BLnet/minecraft/world/entity/HumanoidArm;Lnet/minecraft/core/BlockPos;Lnet/minecraft/resources/ResourceLocation;Leu/pb4/graves/grave/GraveType;JJILnet/minecraft/network/chat/Component;Ljava/util/Collection;Ljava/util/Collection;ZI)V"))
    private static boolean createGrave(boolean profile, @Local(argsOnly = true) ServerPlayer player) {
        if(player instanceof MinionFakePlayer) {
            return false;
        }
        return profile;
    }
}
