package io.github.skippyall.minions.fakeplayer;


import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.skippyall.minions.mixins.EntityAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class EntityPlayerActionPack
{
    private final ServerPlayerEntity player;

    private final Map<ActionType, Action> actions = new EnumMap<>(ActionType.class);

    private BlockPos currentBlock;
    private int blockHitDelay;
    private boolean isHittingBlock;
    private float curBlockDamageMP;

    private boolean sneaking;
    private boolean sprinting;
    private float forward;
    private float strafing;

    private int itemUseCooldown;

    public EntityPlayerActionPack(ServerPlayerEntity playerIn)
    {
        player = playerIn;
        stopAll();
    }
    public void copyFrom(EntityPlayerActionPack other)
    {
        actions.putAll(other.actions);
        currentBlock = other.currentBlock;
        blockHitDelay = other.blockHitDelay;
        isHittingBlock = other.isHittingBlock;
        curBlockDamageMP = other.curBlockDamageMP;

        sneaking = other.sneaking;
        sprinting = other.sprinting;
        forward = other.forward;
        strafing = other.strafing;

        itemUseCooldown = other.itemUseCooldown;
    }

    public EntityPlayerActionPack start(ActionType type, Action action)
    {
        Action previous = actions.remove(type);
        if (previous != null) type.stop(player, previous);
        if (action != null)
        {
            actions.put(type, action);
            type.start(player, action); // noop
        }
        return this;
    }

    public EntityPlayerActionPack setSneaking(boolean doSneak)
    {
        sneaking = doSneak;
        player.setSneaking(doSneak);
        if (sprinting && sneaking)
            setSprinting(false);
        return this;
    }
    public EntityPlayerActionPack setSprinting(boolean doSprint)
    {
        sprinting = doSprint;
        player.setSprinting(doSprint);
        if (sneaking && sprinting)
            setSneaking(false);
        return this;
    }

    public EntityPlayerActionPack setForward(float value)
    {
        forward = value;
        return this;
    }
    public EntityPlayerActionPack setStrafing(float value)
    {
        strafing = value;
        return this;
    }
    public EntityPlayerActionPack look(Direction direction)
    {
        return switch (direction)
        {
            case NORTH -> look(180, 0);
            case SOUTH -> look(0, 0);
            case EAST  -> look(-90, 0);
            case WEST  -> look(90, 0);
            case UP    -> look(player.getYaw(), -90);
            case DOWN  -> look(player.getYaw(), 90);
        };
    }
    public EntityPlayerActionPack look(Vec2f rotation)
    {
        return look(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack look(float yaw, float pitch)
    {
        player.setYaw(yaw % 360); //setYaw
        player.setPitch(MathHelper.clamp(pitch, -90, 90)); // setPitch
        // maybe player.moveTo(player.getX(), player.getY(), player.getZ(), yaw, Mth.clamp(pitch,-90.0F, 90.0F));
        return this;
    }

    public EntityPlayerActionPack lookAt(Vec3d position)
    {
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, position);
        return this;
    }

    public EntityPlayerActionPack turn(float yaw, float pitch)
    {
        return look(player.getYaw() + yaw, player.getPitch() + pitch);
    }

    public EntityPlayerActionPack turn(Vec2f rotation)
    {
        return turn(rotation.x, rotation.y);
    }

    public EntityPlayerActionPack stopMovement()
    {
        setSneaking(false);
        setSprinting(false);
        forward = 0.0F;
        strafing = 0.0F;
        return this;
    }

    public EntityPlayerActionPack stop(ActionType type) {
        type.stop(player, actions.get(type));
        actions.remove(type);
        return this;
    }


    public EntityPlayerActionPack stopAll()
    {
        for (ActionType type : actions.keySet()) type.stop(player, actions.get(type));
        actions.clear();
        return stopMovement();
    }

    public EntityPlayerActionPack mount(boolean onlyRideables)
    {

        //test what happens
        List<Entity> entities;
        if (onlyRideables)
        {
            entities = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(3.0D, 1.0D, 3.0D),
                    e -> (e instanceof MinecartEntity || e instanceof BoatEntity || e instanceof AbstractHorseEntity) && ((EntityAccessor)e).invokeCanAddPassenger(player));
        }
        else
        {
            entities = player.getWorld().getOtherEntities(player, player.getBoundingBox().expand(3.0D, 1.0D, 3.0D));
        }
        if (entities.size()==0)
            return this;
        Entity closest = null;
        double distance = Double.POSITIVE_INFINITY;
        Entity currentVehicle = player.getVehicle();
        for (Entity e: entities)
        {
            if (e == player || (currentVehicle == e))
                continue;
            double dd = player.squaredDistanceTo(e);
            if (dd<distance)
            {
                distance = dd;
                closest = e;
            }
        }
        if (closest == null) return this;
        if (closest instanceof AbstractHorseEntity && onlyRideables)
            ((AbstractHorseEntity) closest).interactMob(player, Hand.MAIN_HAND);
        else
            player.startRiding(closest, !onlyRideables);
        return this;
    }
    public EntityPlayerActionPack dismount()
    {
        player.stopRiding();
        return this;
    }

    public void onUpdate()
    {
        Map<ActionType, Boolean> actionAttempts = new HashMap<>();
        actions.values().removeIf(e -> e.done);
        for (Map.Entry<ActionType, Action> e : actions.entrySet())
        {
            ActionType type = e.getKey();
            Action action = e.getValue();
            // skipping attack if use was successful
            if (!(actionAttempts.getOrDefault(ActionType.USE, false) && type == ActionType.ATTACK))
            {
                Boolean actionStatus = action.tick(this, type);
                if (actionStatus != null)
                    actionAttempts.put(type, actionStatus);
            }
            // optionally retrying use after successful attack and unsuccessful use
            if (type == ActionType.ATTACK
                    && actionAttempts.getOrDefault(ActionType.ATTACK, false)
                    && !actionAttempts.getOrDefault(ActionType.USE, true) )
            {
                // according to MinecraftClient.handleInputEvents
                Action using = actions.get(ActionType.USE);
                if (using != null) // this is always true - we know use worked, but just in case
                {
                    using.retry(this, ActionType.USE);
                }
            }
        }
        float vel = sneaking?0.3F:1.0F;
        // The != 0.0F checks are needed given else real players can't control minecarts, however it works with fakes and else they don't stop immediately
        if (forward != 0.0F || player instanceof MinionFakePlayer) {
            player.forwardSpeed = forward * vel;
        }
        if (strafing != 0.0F || player instanceof MinionFakePlayer) {
            player.sidewaysSpeed = strafing * vel;
        }
    }

    static HitResult getTarget(ServerPlayerEntity player)
    {
        double reach = player.interactionManager.isCreative() ? 5 : 4.5f;
        return Tracer.rayTrace(player, 1, reach, false);
    }

    private void dropItemFromSlot(int slot, boolean dropAll)
    {
        PlayerInventory inv = player.getInventory(); // getInventory;
        if (!inv.getStack(slot).isEmpty())
            player.dropItem(inv.removeStack(slot,
                    dropAll ? inv.getStack(slot).getCount() : 1
            ), false, true); // scatter, keep owner
    }

    public void drop(int selectedSlot, boolean dropAll)
    {
        PlayerInventory inv = player.getInventory(); // getInventory;
        if (selectedSlot == -2) // all
        {
            for (int i = inv.size(); i >= 0; i--)
                dropItemFromSlot(i, dropAll);
        }
        else // one slot
        {
            if (selectedSlot == -1)
                selectedSlot = inv.selectedSlot;
            dropItemFromSlot(selectedSlot, dropAll);
        }
    }

    public void setSlot(int slot)
    {
        player.getInventory().selectedSlot = slot-1;
        player.networkHandler.sendPacket(new UpdateSelectedSlotS2CPacket(slot-1));
    }

    public enum ActionType
    {
        USE(true)
                {
                    @Override
                    boolean execute(ServerPlayerEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
                        if (ap.itemUseCooldown > 0)
                        {
                            ap.itemUseCooldown--;
                            return true;
                        }
                        if (player.isUsingItem())
                        {
                            return true;
                        }
                        HitResult hit = getTarget(player);
                        for (Hand hand : Hand.values())
                        {
                            switch (hit.getType())
                            {
                                case BLOCK:
                                {
                                    player.updateLastActionTime();
                                    ServerWorld world = player.getServerWorld();
                                    BlockHitResult blockHit = (BlockHitResult) hit;
                                    BlockPos pos = blockHit.getBlockPos();
                                    Direction side = blockHit.getSide();
                                    if (pos.getY() < player.getWorld().getTopY() - (side == Direction.UP ? 1 : 0) && world.canPlayerModifyAt(player, pos))
                                    {
                                        ActionResult result = player.interactionManager.interactBlock(player, world, player.getStackInHand(hand), hand, blockHit);
                                        if (result.isAccepted())
                                        {
                                            if (result.shouldSwingHand()) player.swingHand(hand);
                                            ap.itemUseCooldown = 3;
                                            return true;
                                        }
                                    }
                                    break;
                                }
                                case ENTITY:
                                {
                                    player.updateLastActionTime();
                                    EntityHitResult entityHit = (EntityHitResult) hit;
                                    Entity entity = entityHit.getEntity();
                                    boolean handWasEmpty = player.getStackInHand(hand).isEmpty();
                                    boolean itemFrameEmpty = (entity instanceof ItemFrameEntity) && ((ItemFrameEntity) entity).getHeldItemStack().isEmpty();
                                    Vec3d relativeHitPos = entityHit.getPos().subtract(entity.getX(), entity.getY(), entity.getZ());
                                    if (entity.interactAt(player, relativeHitPos, hand).isAccepted())
                                    {
                                        ap.itemUseCooldown = 3;
                                        return true;
                                    }
                                    // fix for SS itemframe always returns CONSUME even if no action is performed
                                    if (player.interact(entity, hand).isAccepted() && !(handWasEmpty && itemFrameEmpty))
                                    {
                                        ap.itemUseCooldown = 3;
                                        return true;
                                    }
                                    break;
                                }
                            }
                            ItemStack handItem = player.getStackInHand(hand);
                            if (player.interactionManager.interactItem(player, player.getWorld(), handItem, hand).isAccepted())
                            {
                                ap.itemUseCooldown = 3;
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    void inactiveTick(ServerPlayerEntity player, Action action)
                    {
                        EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
                        ap.itemUseCooldown = 0;
                        player.stopUsingItem();
                    }
                },
        ATTACK(true) {
            @Override
            boolean execute(ServerPlayerEntity player, Action action) {
                HitResult hit = getTarget(player);
                switch (hit.getType()) {
                    case ENTITY: {
                        EntityHitResult entityHit = (EntityHitResult) hit;
                        if (!action.isContinuous)
                        {
                            player.attack(entityHit.getEntity());
                            player.swingHand(Hand.MAIN_HAND);
                        }
                        player.resetLastAttackedTicks();
                        player.updateLastActionTime();
                        return true;
                    }
                    case BLOCK: {
                        EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
                        if (ap.blockHitDelay > 0)
                        {
                            ap.blockHitDelay--;
                            return false;
                        }
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos pos = blockHit.getBlockPos();
                        Direction side = blockHit.getSide();
                        if (player.isBlockBreakingRestricted(player.getWorld(), pos, player.interactionManager.getGameMode())) return false;
                        if (ap.currentBlock != null && player.getWorld().getBlockState(ap.currentBlock).isAir())
                        {
                            ap.currentBlock = null;
                            return false;
                        }
                        BlockState state = player.getWorld().getBlockState(pos);
                        boolean blockBroken = false;
                        if (player.interactionManager.getGameMode().isCreative())
                        {
                            player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, side, player.getWorld().getTopY(), -1);
                            ap.blockHitDelay = 5;
                            blockBroken = true;
                        }
                        else  if (ap.currentBlock == null || !ap.currentBlock.equals(pos))
                        {
                            if (ap.currentBlock != null)
                            {
                                player.interactionManager.processBlockBreakingAction(ap.currentBlock, PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, side, player.getWorld().getTopY(), -1);
                            }
                            player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, side, player.getWorld().getTopY(), -1);
                            boolean notAir = !state.isAir();
                            if (notAir && ap.curBlockDamageMP == 0)
                            {
                                state.onBlockBreakStart(player.getWorld(), pos, player);
                            }
                            if (notAir && state.calcBlockBreakingDelta(player, player.getWorld(), pos) >= 1)
                            {
                                ap.currentBlock = null;
                                //instamine??
                                blockBroken = true;
                            }
                            else
                            {
                                ap.currentBlock = pos;
                                ap.curBlockDamageMP = 0;
                            }
                        }
                        else
                        {
                            ap.curBlockDamageMP += state.calcBlockBreakingDelta(player, player.getWorld(), pos);
                            if (ap.curBlockDamageMP >= 1)
                            {
                                player.interactionManager.processBlockBreakingAction(pos, PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, side, player.getWorld().getTopY(), -1);
                                ap.currentBlock = null;
                                ap.blockHitDelay = 5;
                                blockBroken = true;
                            }
                            player.getWorld().setBlockBreakingInfo(-1, pos, (int) (ap.curBlockDamageMP * 10));

                        }
                        player.updateLastActionTime();
                        player.swingHand(Hand.MAIN_HAND);
                        return blockBroken;
                    }
                }
                return false;
            }

            @Override
            void inactiveTick(ServerPlayerEntity player, Action action)
            {
                EntityPlayerActionPack ap = ((ServerPlayerInterface) player).getActionPack();
                if (ap.currentBlock == null) return;
                player.getWorld().setBlockBreakingInfo(-1, ap.currentBlock, -1);
                player.interactionManager.processBlockBreakingAction(ap.currentBlock, PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, Direction.DOWN, player.getWorld().getTopY(), -1);
                ap.currentBlock = null;
            }
        },
        JUMP(true)
                {
                    @Override
                    boolean execute(ServerPlayerEntity player, Action action)
                    {
                        if (action.limit == 1)
                        {
                            if (player.isOnGround()) player.jump(); // onGround
                        }
                        else
                        {
                            player.setJumping(true);
                        }
                        return false;
                    }

                    @Override
                    void inactiveTick(ServerPlayerEntity player, Action action)
                    {
                        player.setJumping(false);
                    }
                },
        DROP_ITEM(true)
                {
                    @Override
                    boolean execute(ServerPlayerEntity player, Action action)
                    {
                        player.updateLastActionTime();
                        player.dropSelectedItem(false); // dropSelectedItem
                        return false;
                    }
                },
        DROP_STACK(true)
                {
                    @Override
                    boolean execute(ServerPlayerEntity player, Action action)
                    {
                        player.updateLastActionTime();
                        player.dropSelectedItem(true); // dropSelectedItem
                        return false;
                    }
                },
        SWAP_HANDS(true)
                {
                    @Override
                    boolean execute(ServerPlayerEntity player, Action action)
                    {
                        player.updateLastActionTime();
                        ItemStack itemStack_1 = player.getStackInHand(Hand.OFF_HAND);
                        player.setStackInHand(Hand.OFF_HAND, player.getStackInHand(Hand.MAIN_HAND));
                        player.setStackInHand(Hand.MAIN_HAND, itemStack_1);
                        return false;
                    }
                };

        public final boolean preventSpectator;

        ActionType(boolean preventSpectator)
        {
            this.preventSpectator = preventSpectator;
        }

        void start(ServerPlayerEntity player, Action action) {}
        abstract boolean execute(ServerPlayerEntity player, Action action);
        void inactiveTick(ServerPlayerEntity player, Action action) {}
        void stop(ServerPlayerEntity player, Action action)
        {
            inactiveTick(player, action);
        }
    }

    public static class Action
    {
        public boolean done = false;
        public final int limit;
        public final int interval;
        public final int offset;
        private int count;
        private int next;
        private final boolean isContinuous;

        private Action(int limit, int interval, int offset, boolean continuous)
        {
            this.limit = limit;
            this.interval = interval;
            this.offset = offset;
            next = interval + offset;
            isContinuous = continuous;
        }

        public static Action once()
        {
            return new Action(1, 1, 0, false);
        }

        public static Action continuous()
        {
            return new Action(-1, 1, 0, true);
        }

        public static Action interval(int interval)
        {
            return new Action(-1, interval, 0, false);
        }

        public static Action interval(int interval, int offset)
        {
            return new Action(-1, interval, offset, false);
        }

        Boolean tick(EntityPlayerActionPack actionPack, ActionType type)
        {
            next--;
            Boolean cancel = null;
            if (next <= 0)
            {
                if (interval == 1 && !isContinuous)
                {
                    // need to allow entity to tick, otherwise won't have effect (bow)
                    // actions are 20 tps, so need to clear status mid tick, allowing entities process it till next time
                    if (!type.preventSpectator || !actionPack.player.isSpectator())
                    {
                        type.inactiveTick(actionPack.player, this);
                    }
                }

                if (!type.preventSpectator || !actionPack.player.isSpectator())
                {
                    cancel = type.execute(actionPack.player, this);
                }
                count++;
                if (count == limit)
                {
                    type.stop(actionPack.player, null);
                    done = true;
                    return cancel;
                }
                next = interval;
            }
            else
            {
                if (!type.preventSpectator || !actionPack.player.isSpectator())
                {
                    type.inactiveTick(actionPack.player, this);
                }
            }
            return cancel;
        }

        void retry(EntityPlayerActionPack actionPack, ActionType type)
        {
            //assuming action run but was unsuccesful that tick, but opportunity emerged to retry it, lets retry it.
            if (!type.preventSpectator || !actionPack.player.isSpectator())
            {
                type.execute(actionPack.player, this);
            }
            count++;
            if (count == limit)
            {
                type.stop(actionPack.player, null);
                done = true;
            }
        }
    }
}
