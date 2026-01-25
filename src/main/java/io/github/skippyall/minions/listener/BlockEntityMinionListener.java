package io.github.skippyall.minions.listener;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class BlockEntityMinionListener<E extends BlockEntity> implements MinionListener {
    protected RegistryKey<World> worldKey;
    protected BlockPos pos;
    protected UUID minionUuid;
    protected BlockEntityType<E> type;
    protected @Nullable MinionFakePlayer minion;

    protected BlockEntityMinionListener(RegistryKey<World> worldKey, BlockPos pos, UUID minionUuid, BlockEntityType<E> type) {
        this.worldKey = worldKey;
        this.pos = pos;
        this.minionUuid = minionUuid;
        this.type = type;
    }

    @Override
    public void onMinionSpawn(MinionFakePlayer minion) {
        MinionListener.super.onMinionSpawn(minion);
        this.minion = minion;
        removeIfBeRemoved(minion.getServer());
    }

    @Override
    public void onMinionRemove(MinionFakePlayer minion) {
        MinionListener.super.onMinionRemove(minion);
        this.minion = null;
    }

    public static <T extends BlockEntityMinionListener<?>> T getListener(World world, BlockPos pos, UUID minionUuid, Class<T> clazz) {
        if(minionUuid != null) {
            for (MinionListener listener : MinionPersistentState.get(world.getServer()).getMinionData(minionUuid).listeners()) {
                if (listener instanceof BlockEntityMinionListener<?> tl && tl.pos.equals(pos) && tl.worldKey.equals(world.getRegistryKey()) && clazz.isInstance(tl)) {
                    return clazz.cast(tl);
                }
            }
        }
        return null;
    }

    public static <L extends BlockEntityMinionListener<?>> Codec<L> getCodec(Function3<RegistryKey<World>, BlockPos, UUID, L> constructor) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        World.CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                        BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos),
                        Uuids.CODEC.fieldOf("minionUuid").forGetter(listener -> listener.minionUuid)
                ).apply(instance, constructor));
    }

    private BlockEntityState getBlockEntityState(MinecraftServer server) {
        World world = server.getWorld(worldKey);
        if(world == null || !world.isPosLoaded(pos)) {
            return BlockEntityState.UNLOADED;
        }

        if(world.getBlockEntity(pos, type).isPresent()) {
            return BlockEntityState.LOADED;
        } else {
            return BlockEntityState.REMOVED;
        }
    }

    public Optional<E> getBlockEntity(MinecraftServer server) {
        World world = server.getWorld(worldKey);
        if(world != null && world.isPosLoaded(pos)) {
            return world.getBlockEntity(pos, type);
        }
        return Optional.empty();
    }

    public boolean removeIfBeRemoved(MinecraftServer server) {
        if(getBlockEntityState(server) == BlockEntityState.REMOVED) {
            remove(server);
            return true;
        }
        return false;
    }

    protected void add(MinecraftServer server) {
        MinionPersistentState.get(server).getMinionData(minionUuid).listeners().addListener(this);
        MinionPersistentState.get(server).markDirty();
        this.minion = (MinionFakePlayer) server.getPlayerManager().getPlayer(minionUuid);
    }

    public void remove(MinecraftServer server) {
        MinionPersistentState.get(server).getMinionData(minionUuid).listeners().removeListener(this);
        MinionPersistentState.get(server).markDirty();
    }

    public enum BlockEntityState {
        LOADED,
        REMOVED,
        UNLOADED
    }
}
