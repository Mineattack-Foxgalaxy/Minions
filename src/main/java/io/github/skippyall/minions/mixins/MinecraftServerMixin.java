package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.skin.Base64SkinProvider;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @ModifyExpressionValue(method = "createMetadataPlayers", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;getPlayerList()Ljava/util/List;"))
    public List<ServerPlayerEntity> ignoreFakePlayers(List<ServerPlayerEntity> original) {
        return original.stream()
                .filter(player -> !(player instanceof MinionFakePlayer minion
                        && !minion.getData().config().getOption(MinionConfigOptions.showInServerList)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
    private void onCustomClickAction(Identifier id, Optional<NbtElement> payload, CallbackInfo ci) {
        if(id.equals(Base64SkinProvider.CUSTOM_DIALOG_ACTION)) {
            Base64SkinProvider.onCustomDialogAction(payload);
            ci.cancel();
        }
    }
}
