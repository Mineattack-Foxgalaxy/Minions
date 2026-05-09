//partially code from https://github.com/gnembon/fabric-carpet
package io.github.skippyall.minions.minion.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.gui.minion.MinionGui;
import io.github.skippyall.minions.listener.SerializableListenerManager;
import io.github.skippyall.minions.minion.MinionData;
import io.github.skippyall.minions.minion.MinionItem;
import io.github.skippyall.minions.minion.MinionListener;
import io.github.skippyall.minions.minion.MinionPersistentState;
import io.github.skippyall.minions.minion.MinionProfileUtils;
import io.github.skippyall.minions.minion.MinionRuntime;
import io.github.skippyall.minions.module.ModuleInventory;
import io.github.skippyall.minions.registration.MinionConfigOptions;
import io.github.skippyall.minions.registration.MinionItems;
import io.github.skippyall.minions.registration.SpecialAbilities;
import net.fabricmc.fabric.impl.networking.context.PacketContextImpl;
import net.minecraft.core.BlockPos;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundEntityPositionSyncPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class MinionFakePlayer extends ServerPlayer {
    public Runnable fixStartingPosition = () -> {};

    private EntityPlayerActionPack actionPack;

    private final ModuleInventory moduleInventory = new ModuleInventory(this);
    private final MinionRuntime instructionManager = new MinionRuntime(this);

    public static void spawnMinion(MinionData data, ServerLevel level, @Nullable Vec3 pos, @Nullable Vec2 rot) {
        spawnMinion(data, level, pos, rot, false);
    }

    public static void spawnMinion(MinionData data, ServerLevel level, @Nullable Vec3 pos, @Nullable Vec2 rot, boolean force) {
        if(!data.isSpawned() || force) {
            MinecraftServer server = level.getServer();

            PropertyMap skin = data.getSkin().orElse(null);

            GameProfile profile = MinionProfileUtils.makeNewMinionProfile(data.getUuid(), data.getName(), skin);
            server.schedule(server.wrapRunnable(() -> doSpawn(data, profile, server, level, pos, rot)));
        }
    }

    private static void doSpawn(MinionData data, GameProfile profile, MinecraftServer server, ServerLevel level, @Nullable Vec3 pos, @Nullable Vec2 rot) {
        MinionFakePlayer instance = new MinionFakePlayer(server, level, profile, ClientInformation.createDefault());
        data.setSpawned(true);

        if(pos != null && rot != null) {
            instance.fixStartingPosition = () -> instance.snapTo(pos.x, pos.y, pos.z, rot.x, rot.y);
        }
        FakeClientConnection connection = new FakeClientConnection(PacketFlow.SERVERBOUND);
        //noinspection UnstableApiUsage
        connection.getPacketContext().set(PacketContextImpl.REGISTRY_ACCESS, server.registryAccess());
        //noinspection UnstableApiUsage
        connection.getPacketContext().set(PacketContextImpl.SERVER_INSTANCE, server);
        //noinspection UnstableApiUsage
        connection.getPacketContext().set(PacketContextImpl.GAME_PROFILE, profile);

        server.getPlayerList().placeNewPlayer(connection, instance, new CommonListenerCookie(profile, 0, instance.clientInformation(), false));
        loadPlayerData(instance);
        instance.stopRiding(); // otherwise the created fake player will be on the vehicle
        System.out.println(instance.position());
        if(pos != null && rot != null) {
            instance.teleportTo(level, pos.x, pos.y, pos.z, Set.of(), rot.x, rot.y, true);
        }
        instance.setDeltaMovement(0,0,0);
        instance.setHealth(20.0F);
        instance.unsetRemoved();
        instance.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6F);
        instance.getAttribute(Attributes.WAYPOINT_TRANSMIT_RANGE).setBaseValue(0);
        instance.gameMode.changeGameModeForPlayer(GameType.SURVIVAL);
        server.getPlayerList().broadcastAll(new ClientboundRotateHeadPacket(instance, (byte) (instance.yHeadRot * 256 / 360)), level.dimension());
        server.getPlayerList().broadcastAll(ClientboundEntityPositionSyncPacket.of(instance), level.dimension());
        instance.level().getChunkSource().move(instance);
        instance.entityData.set(DATA_PLAYER_MODE_CUSTOMISATION, (byte) 0x7f); // show all model layers (incl. capes)
        instance.getAbilities().flying = false;

        instance.listeners().forEach(listener -> listener.onMinionSpawn(instance));
    }

    private static void loadPlayerData(MinionFakePlayer player)
    {
        try (ProblemReporter.ScopedCollector scopedCollector = new ProblemReporter.ScopedCollector(player.problemPath(), Minions.LOGGER))
        {
            Optional<ValueInput> optional = player.level().getServer().getPlayerList().loadPlayerData(player.nameAndId()).map((compoundTag) -> TagValueInput.create(scopedCollector, player.registryAccess(), compoundTag));
            optional.ifPresent( valueInput -> {
                player.load(valueInput);
                player.loadAndSpawnEnderPearls(valueInput);
                player.loadAndSpawnParentVehicle(valueInput);
            });
        }
    }

    public static MinionFakePlayer respawnFake(MinecraftServer server, ServerLevel level, GameProfile profile, ClientInformation cli)
    {
        return new MinionFakePlayer(server, level, profile, cli);
    }

    private MinionFakePlayer(MinecraftServer server, ServerLevel worldIn, GameProfile profile, ClientInformation cli)
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
        return MinionPersistentState.get(getServer()).getMinionData(getUUID());
    }

    public SerializableListenerManager<MinionListener> listeners() {
        return getData().getListeners();
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
        return moduleInventory.hasAbility(SpecialAbilities.MOB_SPAWNING) || getData().getConfig().getOption(MinionConfigOptions.spawnAndDespawnMobs);
    }

    public boolean canDespawnMobs() {
        return canSpawnMobs();
    }

    public MinecraftServer getServer() {
        return level().getServer();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
        if(player instanceof ServerPlayer spe) {
            new MinionGui(spe, this);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onEquipItem(final EquipmentSlot slot, final ItemStack previous, final ItemStack stack)
    {
        if (!isUsingItem()) super.onEquipItem(slot, previous, stack);
    }

    public void kill(Component reason)
    {
        listeners().forEach(listener -> listener.onMinionRemove(this));

        shakeOff();

        if (reason.getContents() instanceof TranslatableContents text && text.getKey().equals("multiplayer.disconnect.duplicate_login")) {
            this.connection.onDisconnect(new DisconnectionDetails(reason));
        } else {
            this.getServer().schedule(new TickTask(this.getServer().getTickCount(), () -> {
                this.connection.onDisconnect(new DisconnectionDetails(reason));
            }));
        }

        getData().setSpawned(false);
    }

    @Override
    public void tick()
    {
        actionPack.onUpdate();
        if (this.getServer().getTickCount() % 10 == 0)
        {
            this.connection.resetPosition();
            this.level().getChunkSource().move(this);
        }
        try
        {
            super.tick();
            this.doTick();
            instructionManager.tick();
        }
        catch (NullPointerException ignored)
        {
            // happens with that paper port thingy - not sure what that would fix, but hey
            // the game not gonna crash violently.
        }

    }

    @Override
    public boolean startRiding(Entity entityToRide, boolean force, boolean sendEventAndTriggers) {
        if (super.startRiding(entityToRide, force, sendEventAndTriggers)) {
            // from ClientPacketListener.handleSetEntityPassengersPacket
            if (entityToRide instanceof AbstractBoat) {
                this.yRotO = entityToRide.getYRot();
                this.setYRot(entityToRide.getYRot());
                this.setYHeadRot(entityToRide.getYHeadRot());
            }
            return true;
        } else {
            return false;
        }
    }

    private void shakeOff()
    {
        if (getVehicle() instanceof Player) stopRiding();
        for (Entity passenger : getIndirectPassengers())
        {
            if (passenger instanceof Player) passenger.stopRiding();
        }
    }

    @Override
    public void die(DamageSource cause)
    {
        shakeOff();
        super.die(cause);
        setHealth(20);
        this.foodData = new FoodData();
        kill(this.getCombatTracker().getDeathMessage());
    }

    @Override
    public String getIpAddress()
    {
        return "127.0.0.1";
    }

    @Override
    public boolean allowsListing() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
        doCheckFallDamage(0.0, y, 0.0, onGround);
    }

    @Override
    public ServerPlayer teleport(TeleportTransition target)
    {
        super.teleport(target);
        if (wonGame) {
            ServerboundClientCommandPacket p = new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN);
            connection.handleClientCommand(p);
        }

        // If above branch was taken, *this* has been removed and replaced, the new instance has been set
        // on 'our' connection (which is now theirs, but we still have a ref).
        if (connection.player.isChangingDimension()) {
            connection.player.hasChangedDimension();
        }
        return connection.player;
    }

    @Override
    public void dropAllDeathLoot(ServerLevel world, DamageSource damageSource) {
        super.dropAllDeathLoot(world, damageSource);
        ItemEntity entity = drop(toItemStack(world.getServer()), true, false);
        if (entity != null) {
            entity.setUnlimitedLifetime();
        }
    }

    private ItemStack toItemStack(MinecraftServer server) {
        ItemStack stack = new ItemStack(MinionItems.MINION_ITEM);
        MinionItem.setData(server, getData(), stack);
        return stack;
    }

    @Override
    public void addAdditionalSaveData(ValueOutput view) {
        super.addAdditionalSaveData(view);
        moduleInventory.writeData(view.child("modules"));
        instructionManager.save(view.child("instructionManager"));
    }

    @Override
    public void readAdditionalSaveData(ValueInput view) {
        super.readAdditionalSaveData(view);
        moduleInventory.readData(view.childOrEmpty("modules"));
        instructionManager.load(view.childOrEmpty("instructionManager"));
    }
}