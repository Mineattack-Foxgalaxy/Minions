package io.github.skippyall.minions.minion;

import com.mojang.serialization.Codec;
import io.github.skippyall.minions.Minions;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MinionPersistentState extends SavedData {
    public static final Codec<MinionPersistentState> CODEC = MinionData.CODEC.listOf().xmap(MinionPersistentState::new, MinionPersistentState::getMinionDataList);

    public static SavedDataType<MinionPersistentState> TYPE = new SavedDataType<>(
            Identifier.fromNamespaceAndPath(Minions.MOD_ID, "minion"),
            MinionPersistentState::new,
            MinionPersistentState.CODEC,
            null
    );

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
        setDirty();
    }

    public boolean isMinion(UUID uuid) {
        return minionData.containsKey(uuid);
    }

    public boolean isMinionNameTaken(String name) {
        return getMinionWithName(name).isPresent();
    }

    public Optional<MinionData> getMinionWithName(String name) {
        return minionData.values().stream()
                .filter(data -> data.name().equals(name))
                .findFirst();
    }

    public static MinionPersistentState get(MinecraftServer server) {
        return server.getLevel(Level.OVERWORLD).getDataStorage().computeIfAbsent(TYPE);
    }
}
