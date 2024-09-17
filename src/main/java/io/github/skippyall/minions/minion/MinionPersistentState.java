package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinionPersistentState extends PersistentState {
    public static Type<MinionPersistentState> TYPE = new Type<>(MinionPersistentState::new, MinionPersistentState::read, null);

    public static MinionPersistentState INSTANCE;

    private List<MinionData> minionData = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList list = new NbtList();
        for(MinionData data : minionData) {
            list.add(data.writeNbt());
        }
        nbt.put("minions", list);
        return nbt;
    }

    public static MinionPersistentState read(NbtCompound compound, RegistryWrapper.WrapperLookup lookup) {
        NbtList list = compound.getList("minions", NbtElement.COMPOUND_TYPE);
        MinionPersistentState instance = new MinionPersistentState();
        for(NbtElement element : list) {
            instance.addMinion(MinionData.readNbt((NbtCompound) element));
        }
        return instance;
    }

    public void addMinion(MinionFakePlayer minion) {
        addMinion(MinionData.fromMinion(minion));
    }

    public void addMinion(MinionData data) {
        System.out.println("add Minion " + data.name);
        minionData.add(data);
        markDirty();
    }

    public void removeMinion(MinionFakePlayer minionData) {
        removeMinion(minionData.getUuid());
    }

    public void removeMinion(UUID minionUUID) {
        MinionData removal = null;
        for (MinionData data : minionData) {
            if (data.uuid.equals(minionUUID)) {
                removal = data;
            }
        }
        if (removal != null) {
            minionData.remove(removal);
        }
        markDirty();
    }

    public List<MinionData> getMinionData() {
        return minionData;
    }

    public static void create(MinecraftServer server) {
        INSTANCE = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(TYPE, "minion");
    }
}
