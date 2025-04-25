package io.github.skippyall.minions.minion;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MinionPersistentState extends PersistentState {
    public static final Codec<MinionPersistentState> CODEC = MinionData.CODEC.listOf().xmap(MinionPersistentState::new, MinionPersistentState::getMinionDataList);

    public static PersistentStateType<MinionPersistentState> TYPE = new PersistentStateType<>("minion", MinionPersistentState::new, MinionPersistentState.CODEC, null);

    public static MinionPersistentState INSTANCE;

    private final Map<UUID, MinionData> minionData = new HashMap<>();

    public MinionPersistentState() {

    }

    public MinionPersistentState(List<MinionData> dataList) {
        for (MinionData data : dataList) {
            minionData.put(data.uuid(), data);
        }
    }

    public MinionData getMinionData(UUID uuid) {
        return minionData.get(uuid);
    }

    public Map<UUID, MinionData> getMinionData() {
        return minionData;
    }

    public List<MinionData> getMinionDataList() {
        return List.copyOf(minionData.values());
    }

    public void updateMinionData(MinionData data) {
        minionData.put(data.uuid(), data);
        markDirty();
    }

    public boolean isMinion(UUID uuid) {
        return minionData.containsKey(uuid);
    }

    public boolean isMinionNameTaken(String name) {
        return minionData.values().stream().anyMatch(data -> data.name().equals(name));
    }

    public static void create(MinecraftServer server) {
        INSTANCE = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(TYPE);
    }
}
