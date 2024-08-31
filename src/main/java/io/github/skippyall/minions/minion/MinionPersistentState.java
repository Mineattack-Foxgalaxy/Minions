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

    public static List<MinionData> minionData = new ArrayList<>();

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
        for(NbtElement element : list) {
            minionData.add(MinionData.readNbt((NbtCompound) element));
        }
        return new MinionPersistentState();
    }

    public static void create(MinecraftServer server) {
        INSTANCE = server.getWorld(World.OVERWORLD).getPersistentStateManager().getOrCreate(TYPE, "minion");
    }

    public static class MinionData {
        public UUID uuid;

        public MinionData(UUID uuid) {
            this.uuid = uuid;
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

            return new MinionData(uuid);
        }

        public static MinionData fromMinion(MinionFakePlayer minion) {
            return new MinionData(minion.getUuid());
        }
    }
}
