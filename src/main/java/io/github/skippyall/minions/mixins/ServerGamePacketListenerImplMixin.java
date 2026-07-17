package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {
    @Shadow
    public ServerPlayer player;

    @WrapWithCondition(method = "removePlayerFromWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public boolean noLogoutMessage(PlayerList instance, Component message, boolean overlay) {
        return !(player instanceof MinionFakePlayer minion && !minion.getData().getConfig().getOption(MinionConfigOptions.sendLogoutMessage));
    }
}
