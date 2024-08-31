package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerListS2CPacket.Entry.class)
public class PlayerListEntryS2CPacket$EntryMixin {
    @ModifyArg(method = "<init>(Lnet/minecraft/server/network/ServerPlayerEntity;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/PlayerListS2CPacket$Entry;<init>(Ljava/util/UUID;Lcom/mojang/authlib/GameProfile;ZILnet/minecraft/world/GameMode;Lnet/minecraft/text/Text;Lnet/minecraft/network/encryption/PublicPlayerSession$Serialized;)V"))
    private static boolean removeMinionFromTabList(boolean original, @Local(argsOnly = true) ServerPlayerEntity player) {
        if(player instanceof MinionFakePlayer) {
            return false;
        }

        return original;
    }
}
