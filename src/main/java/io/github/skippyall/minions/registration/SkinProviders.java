package io.github.skippyall.minions.registration;

import io.github.skippyall.minions.minion.skin.Base64SkinProvider;
import io.github.skippyall.minions.minion.skin.NameSkinProvider;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.minion.skin.UUIDSkinProvider;
import io.github.skippyall.minions.Minions;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SkinProviders {
    public static NameSkinProvider NAME = register(new NameSkinProvider(), "name");
    public static UUIDSkinProvider UUID = register(new UUIDSkinProvider(), "uuid");
    public static Base64SkinProvider BASE64 = register(new Base64SkinProvider(), "base64");

    public static <T extends SkinProvider> T register(T skinProvider, String path) {
        return Registry.register(MinionRegistries.SKIN_PROVIDERS, Identifier.of(Minions.MOD_ID, path), skinProvider);
    }

    public static void register() {

    }
}
