package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.nbt.NbtCompound;

import java.util.UUID;

public class MinionData {
    public UUID uuid;
    public String name;
    public UUID skinUuid;

    public MinionData(UUID uuid, String name, UUID skinUuid) {
        this.uuid = uuid;
        this.name = name;
        this.skinUuid = skinUuid;
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();

        nbt.putUuid("uuid", uuid);
        nbt.putString("name", name);
        if(skinUuid != null) {
            nbt.putUuid("skinUuid", skinUuid);
        }

        return nbt;
    }

    public static MinionData readNbt(NbtCompound nbt) {
        UUID uuid = nbt.getUuid("uuid");
        String name = nbt.getString("name");
        UUID skinUuid = null;
        if(nbt.contains("skinUuid")) {
            skinUuid = nbt.getUuid("skinUuid");
        }

        return new MinionData(uuid, name, skinUuid);
    }

    public static MinionData fromMinion(MinionFakePlayer minion) {
        return new MinionData(minion.getUuid(), minion.getMinionName(), minion.getSkinUuid());
    }
}
