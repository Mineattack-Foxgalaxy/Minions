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
    //private final List<UUID> minionUuids = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for(MinionData data : minionData.values()) {
            list.add(data.writeNbt());
        }
        nbt.put("minions", list);

        /*NbtList uuids = new NbtList();
        for(UUID uuid : minionUuids) {
            NbtCompound compound = new NbtCompound();
            compound.putUuid("uuid", uuid);
            uuids.add(compound);
        }
        nbt.put("uuids", uuids);*/
        return nbt;
    }

    public static MinionPersistentState read(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = compound.getList("minions", NbtElement.COMPOUND_TYPE);
        MinionPersistentState instance = new MinionPersistentState();
        for(NbtElement element : list) {
            if(element instanceof NbtCompound compound1) {
                MinionData data = MinionData.readNbt((NbtCompound) element);
                instance.minionData.put(data.uuid(), data);
            }
        }

        /*NbtList uuids = compound.getList("uuids", NbtElement.COMPOUND_TYPE);
        for(NbtElement element : uuids) {
            instance.minionUuids.add(((NbtCompound) element).getUuid("uuid"));
        }*/
        return instance;
    }

    /*public void addMinionUUID(UUID uuid) {
        if(!minionUuids.contains(uuid)) {
            minionUuids.add(uuid);
        }
    }*/

    public void addMinion(MinionData data) {
        System.out.println("add Minion " + data.name());
        minionData.put(data.uuid(), data);
        markDirty();
    }

    public void removeMinion(MinionData minionData) {
        removeMinion(minionData.uuid());
    }

    public void removeMinion(UUID minionUUID) {
        minionData.remove(minionUUID);
        markDirty();
    }

    public MinionData getMinionData(UUID uuid) {
        return minionData.get(uuid);
    }

    public Map<UUID, MinionData> getMinionData() {
        return minionData;
    }

    public void updateMinionData(MinionData data) {
        minionData.put(data.uuid(), data);
    }

    public boolean isMinion(UUID uuid) {
        return minionData.containsKey(uuid);
    }

    public static void create(MinecraftServer server) {
        INSTANCE = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(TYPE, "minion");
    }
}
