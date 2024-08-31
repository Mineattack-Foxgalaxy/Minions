package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyExpressionValue(method = "createMetadataPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"))
    public List<ServerPlayerEntity> ignoreFakePlayers(List<ServerPlayerEntity> original) {
        return original.stream()
                .filter(player -> !(player instanceof MinionFakePlayer))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
