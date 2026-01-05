package io.github.skippyall.minions.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.skippyall.minions.minion.MinionListener;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class BlockEntityMinionListener implements MinionListener {
    protected RegistryKey<World> worldKey;
    protected BlockPos pos;

    public BlockEntityMinionListener(RegistryKey<World> worldKey, BlockPos pos) {
        this.worldKey = worldKey;
        this.pos = pos;
    }

    public static final Codec<BlockEntityMinionListener> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    World.CODEC.fieldOf("world").forGetter(listener -> listener.worldKey),
                    BlockPos.CODEC.fieldOf("pos").forGetter(listener -> listener.pos)
            ).apply(instance, BlockEntityMinionListener::new));


    private BlockEntityState getBlockEntityState(MinecraftServer server) {
        World world = server.getWorld(worldKey);
        if(world == null || !world.isPosLoaded(pos)) {
            return BlockEntityState.UNLOADED;
        }

        if(world.getBlockEntity(pos) instanceof MinionListeningBlockEntity) {
            return BlockEntityState.LOADED;
        } else {
            return BlockEntityState.REMOVED;
        }
    }

    public Optional<MinionListeningBlockEntity> getBlockEntity(MinecraftServer server) {
        World world = server.getWorld(worldKey);
        if(world != null && world.isPosLoaded(pos) && world.getBlockEntity(pos) instanceof MinionListeningBlockEntity be) {
            return Optional.of(be);
        }
        return Optional.empty();
    }

    public enum BlockEntityState {
        LOADED,
        REMOVED,
        UNLOADED
    }
}
