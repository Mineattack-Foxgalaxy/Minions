package io.github.skippyall.minions.listener;

import com.mojang.datafixers.util.Function3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.fakeplayer.MinionFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public abstract class BlockEntityMinionListener<E extends BlockEntity> implements MinionListener {
    protected ResourceKey<Level> worldKey;
    protected BlockPos pos;
    protected UUID minionUuid;
    protected BlockEntityType<E> type;
    protected @Nullable MinionFakePlayer minion;

    protected BlockEntityMinionListener(ResourceKey<Level> worldKey, BlockPos pos, UUID minionUuid, BlockEntityType<E> type) {
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

    public static <T extends BlockEntityMinionListener<?>> @Nullable T getListener(Level world, BlockPos pos, @Nullable UUID minionUuid, Class<T> clazz) {
        if(minionUuid != null) {
            for (MinionListener listener : MinionPersistentState.get(world.getServer()).getMinionData(minionUuid).getListeners()) {
                if (listener instanceof BlockEntityMinionListener<?> tl && tl.pos.equals(pos) && tl.worldKey.equals(world.dimension()) && clazz.isInstance(tl)) {
                    return clazz.cast(tl);
                }
            }
        }
        return null;
    }

    public static <L extends BlockEntityMinionListener<?>> Codec<L> getCodec(Function3<ResourceKey<Level>, BlockPos, UUID, L> constructor) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        Level.RESOURCE_KEY_CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                        BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos),
                        UUIDUtil.AUTHLIB_CODEC.fieldOf("minionUuid").forGetter(listener -> listener.minionUuid)
                ).apply(instance, constructor));
    }

    private BlockEntityState getBlockEntityState(MinecraftServer server) {
        Level world = server.getLevel(worldKey);
        if(world == null || !world.isLoaded(pos)) {
            return BlockEntityState.UNLOADED;
        }

        if(world.getBlockEntity(pos, type).isPresent()) {
            return BlockEntityState.LOADED;
        } else {
            return BlockEntityState.REMOVED;
        }
    }

    public Optional<E> getBlockEntity(MinecraftServer server) {
        Level world = server.getLevel(worldKey);
        if(world != null && world.isLoaded(pos)) {
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

    public void add(MinecraftServer server) {
        MinionPersistentState.get(server).getMinionData(minionUuid).getListeners().addListener(this);
        MinionPersistentState.get(server).setDirty();
        this.minion = (MinionFakePlayer) server.getPlayerList().getPlayer(minionUuid);
    }

    public void remove(MinecraftServer server) {
        MinionPersistentState.get(server).getMinionData(minionUuid).getListeners().removeListener(this);
        MinionPersistentState.get(server).setDirty();
    }

    public enum BlockEntityState {
        LOADED,
        REMOVED,
        UNLOADED
    }
}
