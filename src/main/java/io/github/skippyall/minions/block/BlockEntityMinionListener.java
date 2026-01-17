package io.github.skippyall.minions.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionPersistentState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

public abstract class BlockEntityMinionListener<E extends BlockEntity> implements MinionListener {
    protected RegistryKey<World> worldKey;
    protected BlockPos pos;
    protected BlockEntityType<E> type;

    public BlockEntityMinionListener(RegistryKey<World> worldKey, BlockPos pos, BlockEntityType<E> type) {
        this.worldKey = worldKey;
        this.pos = pos;
        this.type = type;
    }

    public static <L extends BlockEntityMinionListener<?>> Codec<L> getCodec(BiFunction<RegistryKey<World>, BlockPos, L> constructor) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        World.CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                        BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos)
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

    public boolean removeIfBeRemoved(MinecraftServer server, UUID minion) {
        if(getBlockEntityState(server) == BlockEntityState.REMOVED) {
            remove(minion);
            return true;
        }
        return false;
    }

    public void add(UUID minion) {
        MinionPersistentState.INSTANCE.getMinionData(minion).listeners().addListener(this);
        MinionPersistentState.INSTANCE.markDirty();
    }

    public void remove(UUID minion) {
        MinionPersistentState.INSTANCE.getMinionData(minion).listeners().removeListener(this);
        MinionPersistentState.INSTANCE.markDirty();
    }

    public enum BlockEntityState {
        LOADED,
        REMOVED,
        UNLOADED
    }
}
