package io.github.skippyall.minions.minion.skin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.input.TextInput;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Base64SkinProvider implements SkinProvider {
    @Override
    public CompletableFuture<Optional<PropertyMap>> openSkinMenu(ServerPlayerEntity player) {
        return TextInput.inputString(player, Text.translatable("minions.gui.look.skin.base64.title"), "")
                .thenApply(base64String -> {
                    PropertyMap map = new PropertyMap();
                    map.put("textures", new Property("textures", base64String));
                    return Optional.of(map);
                });
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("minions.gui.look.skin.base64");
    }
}
