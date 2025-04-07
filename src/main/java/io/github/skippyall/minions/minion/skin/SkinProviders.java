package io.github.skippyall.minions.minion.skin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import io.github.skippyall.minions.Minions;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class SkinProviders {
    public static final Registry<Codec<? extends SkinProvider>> REGISTRY = new SimpleRegistry<>(RegistryKey.ofRegistry(Identifier.of(Minions.MOD_ID, "skin_providers")), Lifecycle.stable());

    public static final Codec<SkinProvider> CODEC = REGISTRY.getCodec().dispatchStable(SkinProvider::getCodec, codec -> codec.fieldOf("data"));

    public static <T extends Codec<? extends SkinProvider>> T register(T skinProvider, Identifier id) {
        Registry.register(REGISTRY, id, skinProvider);
        return skinProvider;
    }

    public static void register() {
        register(UUIDSkinProvider.CODEC, Identifier.of(Minions.MOD_ID, "uuid"));
        register(NameSkinProvider.CODEC, Identifier.of(Minions.MOD_ID, "name"));
        register(DirectSkinProvider.CODEC, Identifier.of(Minions.MOD_ID, "direct"));
    }
}
