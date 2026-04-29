//partially code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.fakeplayer.NetHandlerPlayServerFake;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(PlayerList.class)
public class PlayerListMixin {

    @Inject(method = "load", at = @At(value = "RETURN", shift = At.Shift.BEFORE))
    private void fixStartingPos(ServerPlayer player, ProblemReporter errorReporter, CallbackInfoReturnable<Optional<ValueInput>> cir)
    {
        if (player instanceof MinionFakePlayer)
        {
            ((MinionFakePlayer) player).fixStartingPosition.run();
        }
    }

    @WrapOperation(method = "placeNewPlayer", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)Lnet/minecraft/server/network/ServerGamePacketListenerImpl;"))
    private ServerGamePacketListenerImpl replaceNetworkHandler(MinecraftServer server, Connection connection, ServerPlayer serverPlayer, CommonListenerCookie commonListenerCookie, Operation<ServerGamePacketListenerImpl> original)
    {
        if (serverPlayer instanceof MinionFakePlayer fake) {
            return new NetHandlerPlayServerFake(server, connection, fake, commonListenerCookie);
        } else {
            return original.call(server, connection, serverPlayer, commonListenerCookie);
        }
    }

    @WrapOperation(method = "respawn", at = @At(value = "NEW", target = "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/server/level/ServerLevel;Lcom/mojang/authlib/GameProfile;Lnet/minecraft/server/level/ClientInformation;)Lnet/minecraft/server/level/ServerPlayer;"))
    public ServerPlayer makePlayerForRespawn(MinecraftServer minecraftServer, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation, Operation<ServerPlayer> original, ServerPlayer serverPlayer, boolean bl) {
        if (serverPlayer instanceof MinionFakePlayer minion) {
            return MinionFakePlayer.respawnFake(minecraftServer, serverLevel, gameProfile, clientInformation);
        }
        return original.call(minecraftServer, serverLevel, gameProfile, clientInformation);
    }

    @WrapOperation(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V"))
    public void noLoginMessage(PlayerList instance, Component message, boolean overlay, Operation<Void> original, @Local(argsOnly = true) ServerPlayer player) {
        if(!(player instanceof MinionFakePlayer minion && !minion.getData().config().getOption(MinionConfigOptions.sendLoginMessage))) {
            original.call(instance, message, overlay);
        }
    }

    @ModifyReceiver(method = "canPlayerLogin", at = @At(value = "INVOKE", target = "Ljava/util/List;size()I"))
    public List<ServerPlayer> noMinionCounting(List<ServerPlayer> instance) {
        return instance.stream()
                .filter(player -> !(player instanceof MinionFakePlayer minion && !minion.getData().config().getOption(MinionConfigOptions.countForPlayerLimit)))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
