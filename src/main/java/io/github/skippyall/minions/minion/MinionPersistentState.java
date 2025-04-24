package io.github.skippyall.minions.minion;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinionPersistentState extends PersistentState {
    public static Type<MinionPersistentState> TYPE = new Type<>(MinionPersistentState::new, MinionPersistentState::read, null);

    public static MinionPersistentState INSTANCE;

    private final Map<UUID, MinionData> minionData = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for(MinionData data : minionData.values()) {
            list.add(data.writeNbt());
        }
        nbt.put("minions", list);

        return nbt;
    }

    public static MinionPersistentState read(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = compound.getList("minions", NbtElement.COMPOUND_TYPE);
        MinionPersistentState instance = new MinionPersistentState();
        for(NbtElement element : list) {
            if(element instanceof NbtCompound compound1) {
                MinionData data = MinionData.readNbt(compound1);
                instance.minionData.put(data.uuid(), data);
            }
        }
        return instance;
    }

    public MinionData getMinionData(UUID uuid) {
        return minionData.get(uuid);
    }

    public Map<UUID, MinionData> getMinionData() {
        return minionData;
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
        INSTANCE = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(TYPE, "minion");
    }
}
