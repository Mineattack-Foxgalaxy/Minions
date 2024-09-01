package io.github.skippyall.minions.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import io.github.skippyall.minions.Minions;
import io.github.skippyall.minions.minion.*;
import io.github.skippyall.minions.mixins.GameProfileMixin;
import io.github.skippyall.minions.program.runtime.MinionRuntime;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
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

import java.util.UUID;

public class MinionFakePlayer extends ServerPlayerEntity {
    public Runnable fixStartingPosition = () -> {};

    private float moveForward;
    private float moveSideways;

    private boolean programmable;
    private final ModuleInventory moduleInventory = new ModuleInventory();
    private final MinionRuntime runtime = new MinionRuntime(this);

    public static void createMinion(String username, ServerWorld level, ServerPlayerEntity owner, boolean canProgram, Vec3d pos, double yaw, double pitch) {
        MinecraftServer server = level.getServer();
        server.getUserCache().findByNameAsync(username).thenAcceptAsync((optional) -> {
            try {
                GameProfile profile = null;
                if(optional.isPresent()){
                    UUID uuid = optional.get().getId();
                    ProfileResult result = server.getSessionService().fetchProfile(uuid, true);
                    if(result != null) {
                        profile = result.profile();
                    }
                }
                if(profile == null) {
                    profile = new GameProfile(new UUID(0,0), username);
                }
                GameProfile newProfile = new GameProfile(UUID.randomUUID(), username);
                newProfile.getProperties().putAll(profile.getProperties());
                GameProfile finalProfile = newProfile;
                ((GameProfileMixin)finalProfile).setId(UUID.randomUUID());
                Minions.addExecuteOnNextTick(() -> {
                    MinionFakePlayer instance = new MinionFakePlayer(server, level, finalProfile, SyncedClientOptions.createDefault());
                    instance.programmable = canProgram;
                    instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
                    server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(finalProfile, 0, instance.getClientOptions(), false));
                    instance.teleport(level, pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
                    instance.setHealth(20.0F);
                    instance.unsetRemoved();
                    instance.getAttributeInstance(EntityAttributes.GENERIC_STEP_HEIGHT).setBaseValue(0.6F);
                    instance.interactionManager.changeGameMode(GameMode.SURVIVAL);
                    server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), level.getRegistryKey());//instance.dimension);
                    server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(instance), level.getRegistryKey());//instance.dimension);
                    //instance.world.getChunkManager(). updatePosition(instance);
                    instance.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
                    instance.getAbilities().flying = false;
                    MinionPersistentState.INSTANCE.addMinion(instance);
                });
            }catch (Throwable ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void spawnMinionAt(MinionData data, ServerWorld level, @Nullable Vec3d pos, @Nullable Vec2f rot) {
        MinecraftServer server = level.getServer();
        server.getUserCache().findByNameAsync(data.name).thenAcceptAsync((optional) -> {
            GameProfile profile = null;
            if (optional.isPresent()) {
                ProfileResult result = server.getSessionService().fetchProfile(optional.get().getId(), true);
                if (result != null) {
                    profile = result.profile();
                }
            }
            if (profile == null) {
                profile = new GameProfile(new UUID(0, 0), data.name);
            }
            GameProfile newProfile = new GameProfile(data.uuid, data.name);
            newProfile.getProperties().putAll(profile.getProperties());
            GameProfile finalProfile = newProfile;
            ((GameProfileMixin)finalProfile).setId(data.uuid);
            Minions.addExecuteOnNextTick(() -> {
                MinionFakePlayer instance = new MinionFakePlayer(server, level, finalProfile, SyncedClientOptions.createDefault());
                if(pos != null && rot != null) {
                    instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, rot.x, rot.y);
                }
                System.out.println(instance.getPos());
                server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(finalProfile, 0, instance.getClientOptions(), false));
                System.out.println(instance.getPos());
                if(pos != null && rot != null) {
                    instance.teleport(level, pos.x, pos.y, pos.z, rot.x, rot.y);
                }
                instance.setVelocity(0,0,0);
                instance.setHealth(20.0F);
                instance.unsetRemoved();
                instance.interactionManager.changeGameMode(GameMode.SURVIVAL);
                server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), level.getRegistryKey());//instance.dimension);
                server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(instance), level.getRegistryKey());//instance.dimension);
                //instance.world.getChunkManager(). updatePosition(instance);
                instance.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
                instance.getAbilities().flying = false;
            });
        });
    }

    public static MinionFakePlayer respawnFake(MinecraftServer server, ServerWorld level, GameProfile profile, SyncedClientOptions cli)
    {
        return new MinionFakePlayer(server, level, profile, cli);
    }

    private MinionFakePlayer(MinecraftServer server, ServerWorld worldIn, GameProfile profile, SyncedClientOptions cli)
    {
        super(server, worldIn, profile, cli);
    }

    public boolean isProgrammable() {
        return programmable;
    }

    public void setProgrammable(boolean programmable) {
        this.programmable = programmable;
    }

    public ModuleInventory getModuleInventory() {
        return moduleInventory;
    }

    public MinionRuntime getRuntime() {
        return runtime;
    }

    public EntityPlayerActionPack getMinionActionPack() {
        return ((ServerPlayerInterface)this).getActionPack();
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if(player instanceof ServerPlayerEntity spe) {
            MinionInventory.openInventory(spe, this);
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

    /*@Override
    public void kill()
    {
        kill(Text.literal("Killed"));
    }*/

    public void kill(Text reason)
    {
        shakeOff();

        if (reason.getContent() instanceof TranslatableTextContent text && text.getKey().equals("multiplayer.disconnect.duplicate_login")) {
            this.networkHandler.onDisconnected(new DisconnectionInfo(reason));
        } else {
            this.server.send(new ServerTask(this.server.getTicks(), () -> {
                this.networkHandler.onDisconnected(new DisconnectionInfo(reason));
            }));
        }

        MinionPersistentState.INSTANCE.removeMinion(this);
    }

    @Override
    public void tick()
    {
        if (this.getServer().getTicks() % 10 == 0)
        {
            this.networkHandler.syncWithPlayerPosition();
            this.getServerWorld().getChunkManager().updatePosition(this);
        }
        try
        {
            super.tick();
            this.playerTick();
        }
        catch (NullPointerException ignored)
        {
            // happens with that paper port thingy - not sure what that would fix, but hey
            // the game not gonna crash violently.
        }
        runtime.tick();

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
    public Entity teleportTo(TeleportTarget target)
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

    public void moveForward(float forward) {
        this.moveForward += forward;
        EntityPlayerActionPack actionPack = getMinionActionPack();
        if (moveForward != 0) {
            actionPack.setForward(moveForward > 0 ? 1 : -1);
        }
    }

    public void moveSideways(float sideways) {
        this.moveSideways += sideways;
        EntityPlayerActionPack actionPack = getMinionActionPack();
        if (moveSideways != 0) {
            actionPack.setStrafing(moveSideways > 0 ? 1 : -1);
        }
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        float newForward = (float) (moveForward - movement.z);
        float newSideways = (float) (moveSideways - movement.x);
        Vec3d newMovement = movement;
        if ((newForward < 0 && moveForward > 0) || (newForward > 0 && moveForward < 0)) {
            newMovement = new Vec3d(newMovement.x, newMovement.y, moveForward);
            moveForward = 0;
            getMinionActionPack().setForward(0);
        }else {
            moveForward = newForward;
        }
        if ((newSideways < 0 && moveSideways > 0) || (newSideways > 0 && moveSideways < 0)) {
            newMovement = new Vec3d(newMovement.x, newMovement.y, moveSideways);
            moveSideways = 0;
            getMinionActionPack().setStrafing(0);
        }else {
            moveSideways = newSideways;
        }
        super.move(movementType, newMovement);
    }

    @Override
    protected void drop(ServerWorld world, DamageSource damageSource) {
        super.drop(world, damageSource);
        dropItem(toItemStack(), true, false);
    }

    private ItemStack toItemStack() {
        ItemStack stack = new ItemStack(Minions.MINION_ITEM);
        MinionItem.setData(MinionData.fromMinion(this), stack);
        if (!getMinionName().equals("Minion")) {
            stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(getMinionName()));
        }
        return stack;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("modules", moduleInventory.writeNbt(new NbtCompound(), getRegistryManager()));
        nbt.putBoolean("programmable", programmable);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        moduleInventory.readNbt(nbt.getCompound("modules"), getRegistryManager());
        programmable = nbt.getBoolean("programmable");
    }

    public String getMinionName() {
        return getGameProfile().getName();
    }
}