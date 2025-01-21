package io.github.skippyall.minions.minion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import eu.pb4.polymer.core.api.other.PolymerComponent;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.component.ComponentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

public record MinionData(UUID uuid, String name, @NotNull Optional<UUID> skinUuid) {
    public static final Codec<MinionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Uuids.CODEC.fieldOf("uuid").forGetter(MinionData::uuid),
                    Codec.STRING.fieldOf("name").forGetter(MinionData::name),
                    Uuids.CODEC.optionalFieldOf("skinUuid").orElse(Optional.empty()).forGetter(MinionData::skinUuid)
            ).apply(instance, MinionData::new)
    );
    public static final ComponentType<MinionData> COMPONENT = Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Minions.MOD_ID, "minion_data"), ComponentType.<MinionData>builder().codec(CODEC).build());

    public MinionData withUuid(UUID uuid) {
        return new MinionData(uuid, name, skinUuid);
    }

    public MinionData withName(String name) {
        return new MinionData(uuid, name, skinUuid);
    }

    public MinionData withSkin(Optional<UUID> skinUuid) {
        return new MinionData(uuid, name, skinUuid);
    }

    public NbtCompound writeNbt() {
        return (NbtCompound) MinionData.CODEC.encode(this, NbtOps.INSTANCE, null).getOrThrow();
    }

    public static MinionData readNbt(NbtCompound nbt) {
        return MinionData.CODEC.decode(NbtOps.INSTANCE, nbt).getOrThrow().getFirst();
    }

    public static MinionData fromMinion(MinionFakePlayer minion) {
        return new MinionData(minion.getUuid(), minion.getMinionName(), minion.getSkinUuid());
    }

    public static void register() {
        PolymerComponent.registerDataComponent(COMPONENT);
    }
}
