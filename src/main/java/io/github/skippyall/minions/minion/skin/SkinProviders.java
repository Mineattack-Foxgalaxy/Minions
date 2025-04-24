package io.github.skippyall.minions.minion.skin;

import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.Minions;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class SkinProviders {
    public static final Registry<SkinProvider> SKIN_PROVIDERS = new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of(Minions.MOD_ID, "skin_providers")), Lifecycle.stable());

    public static NameSkinProvider NAME = register(new NameSkinProvider(), "name");
    public static UUIDSkinProvider UUID = register(new UUIDSkinProvider(), "uuid");
    public static Base64SkinProvider BASE64 = register(new Base64SkinProvider(), "base64");


    public static <T extends SkinProvider> T register(T skinProvider, String path) {
        return Registry.register(SKIN_PROVIDERS, Identifier.of(Minions.MOD_ID, path), skinProvider);
    }

    public static void register() {

    }
}
