package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.fakeplayer.MinionFakePlayer;
import net.minecraft.nbt.*;
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

    public static class MinionData {
        public UUID uuid;
        public String name;
        public boolean programmable;

        public MinionData(UUID uuid, String name, boolean programmable) {
            this.uuid = uuid;
            this.name = name;
            this.programmable = programmable;
        }

        public NbtCompound writeNbt() {
            NbtCompound nbt = new NbtCompound();

            /*NbtList posList = new NbtList();
            posList.add(NbtDouble.of(pos.getX()));
            posList.add(NbtDouble.of(pos.getY()));
            posList.add(NbtDouble.of(pos.getZ()));
            nbt.put("pos", posList);

            NbtList rotList = new NbtList();
            rotList.add(NbtFloat.of(rot.x));
            rotList.add(NbtFloat.of(rot.y));
            nbt.put("rotation", rotList);*/

            nbt.putUuid("uuid", uuid);
            nbt.putString("name", name);
            nbt.putBoolean("programmable", programmable);

            return nbt;
        }

        public static MinionData readNbt(NbtCompound nbt) {
            /*NbtList posList = nbt.getList("pos", NbtElement.DOUBLE_TYPE);
            double x = posList.getDouble(0);
            double y = posList.getDouble(1);
            double z = posList.getDouble(2);
            Vec3d pos = new Vec3d(x, y, z);

            NbtList rotList = nbt.getList("rotation", NbtElement.FLOAT_TYPE);
            float yaw = rotList.getFloat(0);
            float pitch = rotList.getFloat(1);
            Vec2f rot = new Vec2f(yaw, pitch);*/

            UUID uuid = nbt.getUuid("uuid");
            String name = nbt.getString("name");
            boolean programmable = nbt.getBoolean("programmable");

            return new MinionData(uuid, name, programmable);
        }

        public static MinionData fromMinion(MinionFakePlayer minion) {
            return new MinionData(minion.getUuid(), minion.getMinionName(), minion.isProgrammable());
        }
    }
}
