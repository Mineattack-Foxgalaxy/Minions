//partially code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.minion.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.registration.MinionItems;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.gui.MinionGui;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import io.github.skippyall.minions.module.ModuleInventory;
import io.github.skippyall.minions.module.SpecialAbilities;
import io.github.skippyall.minions.util.SerializableListenerManager;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Consumer;

public class MinionFakePlayer extends ServerPlayerEntity {
    public Runnable fixStartingPosition = () -> {};

    private EntityPlayerActionPack actionPack;

    private final ModuleInventory moduleInventory = new ModuleInventory(this);
    private final MinionRuntime instructionManager = new MinionRuntime(this);

    public static void spawnMinion(MinionData data, ServerWorld level, @Nullable Vec3d pos, @Nullable Vec2f rot) {
        spawnMinion(data, level, pos, rot, false);
    }

    public static void spawnMinion(MinionData data, ServerWorld level, @Nullable Vec3d pos, @Nullable Vec2f rot, boolean force) {
        if(!data.isSpawned() || force) {
            MinecraftServer server = level.getServer();

            PropertyMap skin = data.skin().orElse(null);

            GameProfile profile = MinionProfileUtils.makeNewMinionProfile(data.uuid(), data.name(), skin);
            server.send(server.createTask(() -> doSpawn(data, profile, server, level, pos, rot)));
        }
    }

    private static void doSpawn(MinionData data, GameProfile profile, MinecraftServer server, ServerWorld level, @Nullable Vec3d pos, @Nullable Vec2f rot) {
        MinionFakePlayer instance = new MinionFakePlayer(server, level, profile, SyncedClientOptions.createDefault());
        MinionPersistentState.get(server).updateMinionData(data.withSpawned(true));

        if(pos != null && rot != null) {
            instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
        }
        server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(profile, 0, instance.getClientOptions(), false));
        System.out.println(instance.getPos());
        if(pos != null && rot != null) {
            instance.teleport(level, pos.x, pos.y, pos.z, Set.of(), rot.x, rot.y, true);
        }
        instance.setVelocity(0,0,0);
        instance.setHealth(20.0F);
        instance.unsetRemoved();
        instance.getAttributeInstance(EntityAttributes.STEP_HEIGHT).setBaseValue(0.6F);
        instance.interactionManager.changeGameMode(GameMode.SURVIVAL);
        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), level.getRegistryKey());
        server.getPlayerManager().sendToDimension(EntityPositionSyncS2CPacket.create(instance), level.getRegistryKey());
        instance.getWorld().getChunkManager().updatePosition(instance);
        instance.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
        instance.getAbilities().flying = false;

        instance.listeners().forEach(listener -> listener.onMinionSpawn(instance));
    }

    public static MinionFakePlayer respawnFake(MinecraftServer server, ServerWorld level, GameProfile profile, SyncedClientOptions cli)
    {
        return new MinionFakePlayer(server, level, profile, cli);
    }

    private MinionFakePlayer(MinecraftServer server, ServerWorld worldIn, GameProfile profile, SyncedClientOptions cli)
    {
        super(server, worldIn, profile, cli);
        actionPack = new EntityPlayerActionPack(this);
    }

    public ModuleInventory getModuleInventory() {
        return moduleInventory;
    }

    public EntityPlayerActionPack getMinionActionPack() {
        return actionPack;
    }

    public MinionRuntime getInstructionManager() {
        return instructionManager;
    }

    public MinionData getData() {
        return MinionPersistentState.get(getServer()).getMinionData(getUuid());
    }

    public SerializableListenerManager<MinionListener> listeners() {
        return getData().listeners();
    }

    public void addMinionListener(MinionListener listener) {
        listeners().addListener(listener);
    }

    public void removeMinionListener(MinionListener listener) {
        listeners().removeListener(listener);
    }

    public void forEachMinionListener(Consumer<MinionListener> listenerConsumer) {
        listeners().forEach(listenerConsumer);
    }

    public boolean canSpawnMobs() {
        return moduleInventory.hasAbility(SpecialAbilities.MOB_SPAWNING);
    }

    public boolean canDespawnMobs() {
        return moduleInventory.hasAbility(SpecialAbilities.MOB_SPAWNING);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(player instanceof ServerPlayerEntity spe) {
            MinionGui.openInventory(spe, this);
        }
        return ActionResult.CONSUME;
    }

    @Override
    public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
        return interact(player, hand);
    }

    @Override
    public void onEquipStack(final EquipmentSlot slot, final ItemStack previous, final ItemStack stack)
    {
        if (!isUsingItem()) super.onEquipStack(slot, previous, stack);
    }

    public void kill(Text reason)
    {
        listeners().forEach(listener -> listener.onMinionRemove(this));

        shakeOff();

        if (reason.getContent() instanceof TranslatableTextContent text && text.getKey().equals("multiplayer.disconnect.duplicate_login")) {
            this.networkHandler.onDisconnected(new DisconnectionInfo(reason));
        } else {
            this.getServer().send(new ServerTask(this.getServer().getTicks(), () -> {
                this.networkHandler.onDisconnected(new DisconnectionInfo(reason));
            }));
        }

        MinionPersistentState.get(getServer()).updateMinionData(getData().withSpawned(false));
    }

    @Override
    public void tick()
    {
        actionPack.onUpdate();
        if (this.getServer().getTicks() % 10 == 0)
        {
            this.networkHandler.syncWithPlayerPosition();
            this.getWorld().getChunkManager().updatePosition(this);
        }
        try
        {
            super.tick();
            this.playerTick();
            instructionManager.tick();
        }
        catch (NullPointerException ignored)
        {
            // happens with that paper port thingy - not sure what that would fix, but hey
            // the game not gonna crash violently.
        }

    }

    private void shakeOff()
    {
        if (getVehicle() instanceof PlayerEntity) stopRiding();
        for (Entity passenger : getPassengersDeep())
        {
            if (passenger instanceof PlayerEntity) passenger.stopRiding();
        }
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        shakeOff();
        super.onDeath(cause);
        setHealth(20);
        this.hungerManager = new HungerManager();
        kill(this.getDamageTracker().getDeathMessage());
    }

    @Override
    public String getIp()
    {
        return "127.0.0.1";
    }

    @Override
    public boolean allowsServerListing() {
        return false;
    }

    @Override
    protected void fall(double y, boolean onGround, BlockState state, BlockPos pos) {
        handleFall(0.0, y, 0.0, onGround);
    }

    @Override
    public ServerPlayerEntity teleportTo(TeleportTarget target)
    {
        super.teleportTo(target);
        if (notInAnyWorld) {
            ClientStatusC2SPacket p = new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.PERFORM_RESPAWN);
            networkHandler.onClientStatus(p);
        }

        // If above branch was taken, *this* has been removed and replaced, the new instance has been set
        // on 'our' connection (which is now theirs, but we still have a ref).
        if (networkHandler.player.isInTeleportationState()) {
            networkHandler.player.onTeleportationDone();
        }
        return networkHandler.player;
    }

    @Override
    public void drop(ServerWorld world, DamageSource damageSource) {
        super.drop(world, damageSource);
        ItemEntity entity = dropStack(world, toItemStack(getServer()));
        if (entity != null) {
            entity.setNeverDespawn();
        }
    }

    private ItemStack toItemStack(MinecraftServer server) {
        ItemStack stack = new ItemStack(MinionItems.MINION_ITEM);
        MinionItem.setData(server, getData(), stack);
        return stack;
    }

    @Override
    public void writeCustomData(WriteView view) {
        super.writeCustomData(view);
        moduleInventory.writeData(view.get("modules"));
        instructionManager.save(view.get("instructionManager"));
    }

    @Override
    public void readCustomData(ReadView view) {
        super.readCustomData(view);
        moduleInventory.readData(view.getReadView("modules"));
        instructionManager.load(view.getReadView("instructionManager"));
    }
}