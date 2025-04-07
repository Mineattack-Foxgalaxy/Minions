package io.github.skippyall.minions.minion;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.skin.SkinProvider;
import io.github.skippyall.minions.minion.skin.SkinProviders;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public record MinionData(UUID uuid, String name, @Nullable SkinProvider skin, boolean isSpawned) {
    public static final Codec<MinionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("uuid").forGetter(MinionData::uuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::name),
                    SkinProviders.CODEC.optionalFieldOf("skin", null).forGetter(MinionData::skin),
                    Codec.BOOL.optionalFieldOf("isSpawned", false).forGetter(MinionData::isSpawned)
            ).apply(instance, MinionData::new)
    );

    public static final ComponentType<UUID> COMPONENT = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, "minion_data"), ComponentType.<UUID>builder().codec(Uuids.CODEC).build());

    public static MinionData createDefault() {
        return new MinionData(UUID.randomUUID(), "Minion", null, false);
    }

    public MinionData withUuid(UUID uuid) {
        return new MinionData(uuid, name, skin, isSpawned);
    }

    public MinionData withName(String name) {
        return new MinionData(uuid, name, skin, isSpawned);
    }

    public MinionData withSkin(@Nullable SkinProvider skin) {
        return new MinionData(uuid, name, skin, isSpawned);
    }

    public CompletableFuture<@Nullable PropertyMap> getSkin(MinecraftServer server) {
        if(skin != null) {
            return skin.getSkin(server);
        }
        return CompletableFuture.completedFuture(null);
    }

    public MinionData withSpawned(boolean isSpawned) {
        return new MinionData(uuid, name, skin, isSpawned);
    }

    public NbtCompound writeNbt() {
        return (NbtCompound) MinionData.CODEC.encode(this, NbtOps.INSTANCE, null).result().orElse(new NbtCompound());
    }

    public static MinionData readNbt(NbtCompound nbt) {
        return MinionData.CODEC.decode(NbtOps.INSTANCE, nbt).resultOrPartial().map(Pair::getFirst).orElseGet(MinionData::createDefault);
    }

    public static void register() {
        PolymerComponent.registerDataComponent(COMPONENT);
    }
}
