package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.graves.grave.Grave;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Grave.class)
public class GraveCompatMixin {
    @ModifyArg(method = "createBlock", at = @At(value = "INVOKE", target = "Leu/pb4/graves/grave/Grave;<init>(JLcom/mojang/authlib/GameProfile;BLnet/minecraft/util/Arm;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Identifier;Leu/pb4/graves/grave/GraveType;JJILnet/minecraft/text/Text;Ljava/util/Collection;Ljava/util/Collection;ZI)V"))
    private static boolean createGrave(boolean profile, @Local(argsOnly = true) ServerPlayerEntity player) {
        if(player instanceof MinionFakePlayer) {
            return false;
        }
        return profile;
    }
}
