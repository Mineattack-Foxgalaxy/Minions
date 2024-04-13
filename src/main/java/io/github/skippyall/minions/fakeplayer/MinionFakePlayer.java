package io.github.skippyall.minions.fakeplayer;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import io.github.skippyall.minions.minion.MinionInventory;
import io.github.skippyall.minions.minion.ModuleInventory;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityPositionS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import java.util.UUID;

public class MinionFakePlayer extends ServerPlayerEntity {
    public Runnable fixStartingPosition = () -> {};
    public boolean isAShadow;

    private float moveForward;
    private float moveSideways;

    private boolean programmable;
    private ModuleInventory moduleInventory = new ModuleInventory();

    public static void createMinion(String username, ServerWorld level, ServerPlayerEntity owner, boolean canProgram, Vec3d pos, double yaw, double pitch) {
        MinecraftServer server = level.getServer();
        server.getUserCache().findByNameAsync(username).thenAcceptAsync((optional) -> {
            GameProfile profile = null;
            if(optional.isPresent()){
                UUID uuid = optional.get().getId();
                ProfileResult result = server.getSessionService().fetchProfile(uuid, true);
                if(result != null) {
                    profile = result.profile();
                }
            }
            if(profile == null) {
                profile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
            }
            MinionFakePlayer instance = new MinionFakePlayer(server, level, profile, SyncedClientOptions.createDefault(), false, canProgram);
            instance.fixStartingPosition = () -> instance.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), instance, new ConnectedClientData(profile, 0, instance.getClientOptions()));
            instance.teleport(level, pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            instance.setHealth(20.0F);
            instance.unsetRemoved();
            instance.interactionManager.changeGameMode(GameMode.SURVIVAL);
            server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(instance, (byte) (instance.headYaw * 256 / 360)), level.getRegistryKey());//instance.dimension);
            server.getPlayerManager().sendToDimension(new EntityPositionS2CPacket(instance), level.getRegistryKey());//instance.dimension);
            //instance.world.getChunkManager(). updatePosition(instance);
            instance.dataTracker.set(PLAYER_MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
            instance.getAbilities().flying = false;
            instance.setStepHeight(0.6F);
        });


    }

    @SuppressWarnings("unused") //Don't know if I'll need this
    public static MinionFakePlayer createShadow(MinecraftServer server, ServerPlayerEntity player)
    {
        player.getServer().getPlayerManager().remove(player);
        player.networkHandler.disconnect(Text.translatable("multiplayer.disconnect.duplicate_login"));
        ServerWorld worldIn = player.getServerWorld();//.getWorld(player.dimension);
        GameProfile gameprofile = player.getGameProfile();
        MinionFakePlayer playerShadow = new MinionFakePlayer(server, worldIn, gameprofile, player.getClientOptions(), true, false);
        playerShadow.setSession(player.getSession());
        server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), playerShadow, new ConnectedClientData(gameprofile, 0, player.getClientOptions()));

        playerShadow.setHealth(player.getHealth());
        playerShadow.networkHandler.requestTeleport(player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
        playerShadow.interactionManager.changeGameMode(player.interactionManager.getGameMode());
        ((ServerPlayerInterface) playerShadow).getActionPack().copyFrom(((ServerPlayerInterface) player).getActionPack());
        playerShadow.dataTracker.set(PLAYER_MODEL_PARTS, player.getDataTracker().get(PLAYER_MODEL_PARTS));


        server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(playerShadow, (byte) (player.headYaw * 256 / 360)), playerShadow.getWorld().getRegistryKey());
        server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.ADD_PLAYER, playerShadow));
        //player.world.getChunkManager().updatePosition(playerShadow);
        playerShadow.getAbilities().flying = player.getAbilities().flying;
        return playerShadow;
    }

    public static MinionFakePlayer respawnFake(MinecraftServer server, ServerWorld level, GameProfile profile, SyncedClientOptions cli, boolean programmable)
    {
        return new MinionFakePlayer(server, level, profile, cli, false, programmable);
    }

    private MinionFakePlayer(MinecraftServer server, ServerWorld worldIn, GameProfile profile, SyncedClientOptions cli, boolean shadow, boolean programmable)
    {
        super(server, worldIn, profile, cli);
        isAShadow = shadow;

    }

    public boolean isProgrammable() {
        return programmable;
    }

    public ModuleInventory getModuleInventory() {
        return moduleInventory;
    }

    public EntityPlayerActionPack getActionPack() {
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

    @Override
    public void kill()
    {
        kill(Text.literal("Killed"));
    }

    public void kill(Text reason)
    {
        shakeOff();

        if (reason.getContent() instanceof TranslatableTextContent text && text.getKey().equals("multiplayer.disconnect.duplicate_login")) {
            this.networkHandler.onDisconnected(reason);
        } else {
            this.server.send(new ServerTask(this.server.getTicks(), () -> {
                this.networkHandler.onDisconnected(reason);
            }));
        }
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
    public Entity moveToWorld(ServerWorld serverLevel)
    {
        super.moveToWorld(serverLevel);
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
        EntityPlayerActionPack actionPack = getActionPack();
        if (moveForward != 0) {
            actionPack.setForward(moveForward > 0 ? 1 : -1);
        }
    }

    public void moveSideways(float sideways) {
        this.moveSideways += sideways;
        EntityPlayerActionPack actionPack = getActionPack();
        if (moveSideways != 0) {
            actionPack.setStrafing(moveSideways > 0 ? 1 : -1);
        }
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        float newForward = (float) (moveForward - movement.z);
        float newSideways = (float) (moveSideways - movement.x);
        Vec3d newMovement = movement;
        if ((newForward < 0 && moveForward >= 0) || (newForward > 0 && moveForward <= 0)) {
            newMovement = new Vec3d(newMovement.x, newMovement.y, moveForward);
            moveForward = 0;
            getActionPack().setForward(0);
        }else {
            moveForward = newForward;
        }
        if ((newSideways < 0 && moveSideways >= 0) || (newSideways > 0 && moveSideways <= 0)) {
            newMovement = new Vec3d(newMovement.x, newMovement.y, moveSideways);
            moveSideways = 0;
            getActionPack().setStrafing(0);
        }else {
            moveSideways = newSideways;
        }
        super.move(movementType, newMovement);
    }
}