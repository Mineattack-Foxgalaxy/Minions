package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientboundPlayerInfoUpdatePacket.Entry.class)
public class ClientboundPlayerInfoUpdatePacket$EntryMixin {
    @ModifyArg(method = "<init>(Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundPlayerInfoUpdatePacket$Entry;<init>(Ljava/util/UUID;Lcom/mojang/authlib/GameProfile;ZILnet/minecraft/world/level/GameType;Lnet/minecraft/network/chat/Component;ZILnet/minecraft/network/chat/RemoteChatSession$Data;)V"), index = 2)
    private static boolean removeMinionFromTabList(boolean original, @Local(argsOnly = true) ServerPlayer player) {
        if(player instanceof MinionFakePlayer minion && !minion.getData().config().getOption(MinionConfigOptions.showInTabList)) {
            return false;
        }

        return original;
    }
}
