package io.github.skippyall.minions.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import io.github.skippyall.minions.minion.skin.Base64SkinProvider;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
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
    @ModifyExpressionValue(method = "buildPlayerStatus", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/players/PlayerList;getPlayers()Ljava/util/List;"))
    public List<ServerPlayer> ignoreFakePlayers(List<ServerPlayer> original) {
        return original.stream()
                .filter(player -> !(player instanceof MinionFakePlayer minion
                        && !minion.getData().config().getOption(MinionConfigOptions.showInServerList)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Inject(method = "handleCustomClickAction", at = @At("HEAD"), cancellable = true)
    private void onCustomClickAction(ResourceLocation id, Optional<Tag> payload, CallbackInfo ci) {
        if(id.equals(Base64SkinProvider.CUSTOM_DIALOG_ACTION)) {
            Base64SkinProvider.onCustomDialogAction(payload);
            ci.cancel();
        }
    }
}
