package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class MinionData {
    public UUID uuid;
    public String name;

    public MinionData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putUuid("uuid", uuid);
        nbt.putString("name", name);

        return nbt;
    }

    public static MinionData readNbt(NbtCompound nbt) {
        UUID uuid = nbt.getUuid("uuid");
        String name = nbt.getString("name");

        return new MinionData(uuid, name);
    }

    public static MinionData fromMinion(MinionFakePlayer minion) {
        return new MinionData(minion.getUuid(), minion.getMinionName());
    }
}
