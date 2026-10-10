/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.item.misc.MonitorItem
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.Holder
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.Level$ExplosionInteraction
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.items.ItemHandlerHelper
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.registries.RegistryObject
 */
package com.redabysslucia.dragonrise_reforge.entities.special;

import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.misc.MonitorItem;
import com.redabysslucia.dragonrise_reforge.init.ModSounds;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class R6DroneEntity
extends Entity {
    public static final double BASE_SPEED = 0.28;
    public static final double SPRINT_MULTIPLIER = 1.3;
    public static final double GRAVITY = 0.08;
    public static final double ACCEL_GROUND = 0.6;
    public static final double ACCEL_AIR = 0.05;
    public static final double JUMP_SPEED = 0.62;
    public static final double JUMP_MIN_VY = 0.25;
    public static final double JUMP_HORIZONTAL_SPEED = 0.6;
    public static final int JUMP_COOLDOWN = 40;
    private static final EntityDataAccessor<Optional<UUID>> CONTROLLER = SynchedEntityData.m_135353_(R6DroneEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135041_);
    private boolean forward;
    private boolean back;
    private boolean left;
    private boolean right;
    private boolean sprint;
    private boolean jump;
    private float health = 5.0f;
    private int jumpCooldown;
    private boolean wasOnGround;
    private double prevPrevX;
    private double prevPrevY;
    private double prevPrevZ;
    private double prevX;
    private double prevY;
    private double prevZ;
    private double smoothX;
    private double smoothY;
    private double smoothZ;
    private boolean smoothInit;
    private static R6DroneEntity cachedClientDrone;
    private static String cachedClientDroneUuid;
    private static long cachedClientDroneLevelTick;
    private static final double MAX_SPLINE_STEP = 0.9;
    private static final double SMOOTH_ALPHA = 0.25;

    /*
     * Enabled aggressive block sorting
     */
    private void playDroneSound(RegistryObject<SoundEvent> sound, float volume) {
        if (this.m_9236_().m_5776_()) {
            return;
        }
        Player controller = this.getController();
        if (controller instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)controller;
            if (this.isMonitorControlling(controller)) {
                sound.getHolder().ifPresent(holder -> sp.f_8906_.m_9829_((Packet)new ClientboundSoundPacket(holder, SoundSource.PLAYERS, this.m_20185_(), this.m_20186_(), this.m_20189_(), volume, 1.0f, this.m_9236_().m_213780_().m_188505_())));
                return;
            }
        }
        this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)sound.get(), SoundSource.PLAYERS, volume, 1.0f);
    }

    /*
     * Enabled aggressive block sorting
     */
    protected void playDroneSound(SoundEvent sound, float volume) {
        if (this.m_9236_().m_5776_()) {
            return;
        }
        Player controller = this.getController();
        if (controller instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)controller;
            if (this.isMonitorControlling(controller)) {
                sp.f_8906_.m_9829_((Packet)new ClientboundSoundPacket(Holder.m_205709_((Object)sound), SoundSource.PLAYERS, this.m_20185_(), this.m_20186_(), this.m_20189_(), volume, 1.0f, this.m_9236_().m_213780_().m_188505_()));
                return;
            }
        }
        this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), sound, SoundSource.PLAYERS, volume, 1.0f);
    }

    public R6DroneEntity(EntityType<? extends R6DroneEntity> type, Level level) {
        super(type, level);
        this.m_274367_(0.6f);
    }

    protected void m_8097_() {
        this.f_19804_.m_135372_(CONTROLLER, Optional.empty());
    }

    public void m_8119_() {
        boolean monitoring;
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            this.prevPrevX = this.prevX;
            this.prevPrevY = this.prevY;
            this.prevPrevZ = this.prevZ;
            this.prevX = this.m_20185_();
            this.prevY = this.m_20186_();
            this.prevZ = this.m_20189_();
            LocalPlayer local = Minecraft.m_91087_().f_91074_;
            if (local == null || !this.isMonitorControlling((Player)local)) {
                this.smoothInit = false;
            }
            return;
        }
        Player controller = this.getController();
        boolean bl = monitoring = controller != null && this.isMonitorControlling(controller);
        if (monitoring) {
            this.m_146922_(controller.m_146908_());
            this.m_146926_(Mth.m_14036_((float)controller.m_146909_(), (float)-90.0f, (float)90.0f));
        } else {
            this.jump = false;
            this.sprint = false;
            this.right = false;
            this.left = false;
            this.back = false;
            this.forward = false;
        }
        if (!this.wasOnGround && this.m_20096_()) {
            this.playDroneSound(ModSounds.R6_DRONE_DOWN, 1.0f);
        }
        this.wasOnGround = this.m_20096_();
        if (this.jumpCooldown > 0) {
            --this.jumpCooldown;
        }
        Vec3 vel = this.m_20184_();
        vel = vel.m_82520_(0.0, -0.08, 0.0);
        double fwd = (this.forward ? 1.0 : 0.0) - (this.back ? 1.0 : 0.0);
        double strafe = (this.right ? 1.0 : 0.0) - (this.left ? 1.0 : 0.0);
        double yaw = Math.toRadians(this.m_146908_());
        Vec3 forwardVec = new Vec3((double)(-Mth.m_14031_((float)((float)yaw))), 0.0, (double)Mth.m_14089_((float)((float)yaw)));
        Vec3 rightVec = new Vec3(-forwardVec.f_82481_, 0.0, forwardVec.f_82479_);
        Vec3 wish = forwardVec.m_82490_(fwd).m_82549_(rightVec.m_82490_(strafe));
        if (wish.m_82556_() > 1.0E-6) {
            wish = wish.m_82541_();
        }
        double speed = 0.28 * (this.sprint ? 1.3 : 1.0);
        Vec3 target = wish.m_82490_(speed);
        Vec3 horiz = new Vec3(vel.f_82479_, 0.0, vel.f_82481_);
        Vec3 newHoriz = horiz.m_165921_(target, this.m_20096_() ? 0.6 : 0.05);
        vel = new Vec3(newHoriz.f_82479_, vel.f_82480_, newHoriz.f_82481_);
        if (this.jumpCooldown <= 0 && this.m_20096_() && this.jump) {
            Vec3 look = this.m_20154_();
            double vx = look.f_82479_ * 0.6;
            double vy = look.f_82480_ * 0.62 + 0.25;
            double vz = look.f_82481_ * 0.6;
            vel = new Vec3(vx, vy, vz);
            this.jumpCooldown = 40;
            this.playDroneSound(ModSounds.R6_DRONE_JUMP, 1.0f);
        }
        this.m_20256_(vel);
        this.m_6478_(MoverType.SELF, vel);
    }

    public void processInput(short keys) {
        this.left = (keys & 1) > 0;
        this.right = (keys & 2) > 0;
        this.forward = (keys & 4) > 0;
        this.back = (keys & 8) > 0;
        this.jump = (keys & 0x10) > 0;
        this.sprint = (keys & 0x100) > 0;
    }

    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        ItemStack stack = player.m_21120_(hand);
        if (stack.m_150930_((Item)ModItems.MONITOR.get())) {
            if (!player.m_6144_()) {
                if (this.getController() == null) {
                    this.f_19804_.m_135381_(CONTROLLER, Optional.of(player.m_20148_()));
                    MonitorItem.link((ItemStack)stack, (String)this.m_20149_());
                    player.m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.monitor.linked").m_130940_(ChatFormatting.GREEN), true);
                    if (!this.m_9236_().m_5776_()) {
                        player.m_9236_().m_5594_(null, player.m_20183_(), SoundEvents.f_11686_, SoundSource.PLAYERS, 0.5f, 1.0f);
                    }
                } else {
                    player.m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.drone.already_linked").m_130940_(ChatFormatting.RED), true);
                }
            } else {
                this.recycle(player);
            }
        } else if (player.m_6144_()) {
            this.recycle(player);
        }
        return InteractionResult.m_19078_((boolean)this.m_9236_().m_5776_());
    }

    private void recycle(Player player) {
        String linked;
        ItemStack ctrlStack;
        if (this.m_9236_().m_5776_()) {
            return;
        }
        Player controller = this.getController();
        if (controller != null && (ctrlStack = controller.m_21205_()).m_150930_((Item)ModItems.MONITOR.get()) && (linked = ctrlStack.m_41784_().m_128461_("LinkedDrone")).equals(this.m_20149_())) {
            MonitorItem.disLink((ItemStack)ctrlStack, (Player)controller);
        }
        ItemHandlerHelper.giveItemToPlayer((Player)player, (ItemStack)new ItemStack((ItemLike)this.getDeployItem()));
        player.m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.drone.unlinked").m_130940_(ChatFormatting.GREEN), true);
        this.m_146870_();
    }

    protected Item getDeployItem() {
        return (Item)com.redabysslucia.dragonrise_reforge.init.ModItems.R6_DRONE.get();
    }

    public boolean m_6469_(DamageSource source, float amount) {
        if (this.m_9236_().m_5776_() || this.m_213877_()) {
            return false;
        }
        this.health -= amount;
        if (this.health <= 0.0f) {
            this.destroy();
        }
        return true;
    }

    private void destroy() {
        String linked;
        ItemStack stack;
        if (this.m_9236_().m_5776_() || this.m_213877_()) {
            return;
        }
        Player controller = this.getController();
        if (controller != null && (stack = controller.m_21205_()).m_150930_((Item)ModItems.MONITOR.get()) && (linked = stack.m_41784_().m_128461_("LinkedDrone")).equals(this.m_20149_())) {
            MonitorItem.disLink((ItemStack)stack, (Player)controller);
        }
        this.m_146870_();
        this.m_9236_().m_254849_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), 0.8f, Level.ExplosionInteraction.NONE);
    }

    public float getRenderYaw(float partialTick) {
        LocalPlayer local;
        if (this.m_9236_().m_5776_() && (local = Minecraft.m_91087_().f_91074_) != null && this.getController() == local && this.isMonitorControlling((Player)local)) {
            return local.m_146908_();
        }
        return Mth.m_14179_((float)partialTick, (float)this.f_19859_, (float)this.m_146908_());
    }

    public float getRenderPitch(float partialTick) {
        LocalPlayer local;
        if (this.m_9236_().m_5776_() && (local = Minecraft.m_91087_().f_91074_) != null && this.getController() == local && this.isMonitorControlling((Player)local)) {
            return local.m_146909_();
        }
        return Mth.m_14179_((float)partialTick, (float)this.f_19860_, (float)this.m_146909_());
    }

    public static R6DroneEntity findDrone(Level level, String uuidString) {
        R6DroneEntity drone;
        UUID uuid;
        if (uuidString == null || uuidString.length() != 36) {
            return null;
        }
        try {
            uuid = UUID.fromString(uuidString);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
        if (level.m_5776_()) {
            if (cachedClientDrone != null && !cachedClientDrone.m_213877_() && cachedClientDrone.m_9236_() == level && cachedClientDroneUuid != null && cachedClientDroneUuid.equals(uuidString)) {
                return cachedClientDrone;
            }
            long tick = level.m_46467_();
            if (cachedClientDroneLevelTick == tick && cachedClientDroneUuid != null && cachedClientDroneUuid.equals(uuidString)) {
                return cachedClientDrone;
            }
            cachedClientDroneLevelTick = tick;
            cachedClientDroneUuid = uuidString;
            cachedClientDrone = null;
            for (Entity ent : ((ClientLevel)level).m_104735_()) {
                R6DroneEntity drone2;
                if (!ent.m_20148_().equals(uuid) || !(ent instanceof R6DroneEntity)) continue;
                cachedClientDrone = drone2 = (R6DroneEntity)ent;
                break;
            }
            return cachedClientDrone;
        }
        Entity e = (Entity)((ServerLevel)level).m_142646_().m_142694_(uuid);
        return e instanceof R6DroneEntity ? (drone = (R6DroneEntity)e) : null;
    }

    public Player getController() {
        UUID uuid = ((Optional)this.f_19804_.m_135370_(CONTROLLER)).orElse(null);
        if (uuid == null) {
            return null;
        }
        return this.m_9236_().m_46003_(uuid);
    }

    public boolean isMonitorControlling(Player player) {
        ItemStack stack = player.m_21205_();
        return stack.m_150930_((Item)ModItems.MONITOR.get()) && stack.m_41784_().m_128471_("Using") && stack.m_41784_().m_128471_("Linked");
    }

    public Vec3 getRenderPosition(float partialTick) {
        double cz;
        double dz;
        double cy;
        double dy;
        if (!this.m_9236_().m_5776_()) {
            return this.m_20182_();
        }
        double cx = this.m_20185_();
        double dx = cx - this.prevX;
        double stepSqr = dx * dx + (dy = (cy = this.m_20186_()) - this.prevY) * dy + (dz = (cz = this.m_20189_()) - this.prevZ) * dz;
        if (stepSqr > 0.81) {
            return new Vec3(Mth.m_14139_((double)partialTick, (double)this.f_19790_, (double)cx), Mth.m_14139_((double)partialTick, (double)this.f_19791_, (double)cy), Mth.m_14139_((double)partialTick, (double)this.f_19792_, (double)cz));
        }
        return new Vec3(R6DroneEntity.catmullRom(partialTick, this.prevPrevX, this.prevX, cx), R6DroneEntity.catmullRom(partialTick, this.prevPrevY, this.prevY, cy), R6DroneEntity.catmullRom(partialTick, this.prevPrevZ, this.prevZ, cz));
    }

    private static double catmullRom(float t, double p0, double p1, double p2) {
        double p3 = p2 + (p2 - p1);
        double t2 = t * t;
        double t3 = t2 * (double)t;
        return 0.5 * (2.0 * p1 + (-p0 + p2) * (double)t + (2.0 * p0 - 5.0 * p1 + 4.0 * p2 - p3) * t2 + (-p0 + 3.0 * p1 - 3.0 * p2 + p3) * t3);
    }

    public Vec3 advanceSmoothPosition(float partialTick) {
        double cz;
        double dz;
        double cy;
        double dy;
        boolean transientStep;
        Vec3 target = this.getRenderPosition(partialTick);
        if (!this.smoothInit) {
            this.smoothX = target.f_82479_;
            this.smoothY = target.f_82480_;
            this.smoothZ = target.f_82481_;
            this.smoothInit = true;
            return target;
        }
        double cx = this.m_20185_();
        double dx = cx - this.prevX;
        boolean bl = transientStep = dx * dx + (dy = (cy = this.m_20186_()) - this.prevY) * dy + (dz = (cz = this.m_20189_()) - this.prevZ) * dz > 0.81;
        if (transientStep) {
            this.smoothX = target.f_82479_;
            this.smoothY = target.f_82480_;
            this.smoothZ = target.f_82481_;
            return target;
        }
        this.smoothX += (target.f_82479_ - this.smoothX) * 0.25;
        this.smoothY += (target.f_82480_ - this.smoothY) * 0.25;
        this.smoothZ += (target.f_82481_ - this.smoothZ) * 0.25;
        return new Vec3(this.smoothX, this.smoothY, this.smoothZ);
    }

    public Vec3 getSmoothPositionOrNull() {
        if (!this.smoothInit) {
            return null;
        }
        return new Vec3(this.smoothX, this.smoothY, this.smoothZ);
    }

    public boolean m_6087_() {
        return !this.m_213877_();
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        return NetworkHooks.getEntitySpawningPacket((Entity)this);
    }

    public void m_7380_(CompoundTag tag) {
        tag.m_128350_("Health", this.health);
        UUID uuid = ((Optional)this.f_19804_.m_135370_(CONTROLLER)).orElse(null);
        if (uuid != null) {
            tag.m_128362_("Controller", uuid);
        }
    }

    public void m_7378_(CompoundTag tag) {
        if (tag.m_128441_("Health")) {
            this.health = tag.m_128457_("Health");
        }
        if (tag.m_128403_("Controller")) {
            this.f_19804_.m_135381_(CONTROLLER, Optional.of(tag.m_128342_("Controller")));
        }
    }

    static {
        cachedClientDroneLevelTick = -1L;
    }
}

