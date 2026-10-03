/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.config.server.ExplosionConfig
 *  com.atsuishio.superbwarfare.entity.projectile.HandGrenadeEntity
 *  com.atsuishio.superbwarfare.init.ModParticleTypes
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.SimpleParticleType
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundAddEntityPacket
 *  net.minecraft.network.protocol.game.ClientboundStopSoundPacket
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityDimensions
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.decoration.HangingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.DiodeBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.items.ItemHandlerHelper
 */
package com.redabysslucia.dragonrise_reforge.entities.special;

import com.atsuishio.superbwarfare.config.server.ExplosionConfig;
import com.atsuishio.superbwarfare.entity.projectile.HandGrenadeEntity;
import com.atsuishio.superbwarfare.init.ModParticleTypes;
import com.redabysslucia.dragonrise_reforge.init.ModEntities;
import com.redabysslucia.dragonrise_reforge.init.ModItems;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

public class ClusterChargeEntity
extends HangingEntity
implements OwnableEntity {
    public static final int PLACE_SOUND_DELAY_TICKS = 16;
    public static final int ARMING_SOUND_TICKS = 60;
    public static final int FIRING_START_TICK = 76;
    public static final int SHOT_COUNT = 5;
    public static final int SHOT_INTERVAL_TICKS = 10;
    public static final float GRENADE_VELOCITY = 0.87f;
    public static final int GRENADE_FUSE_TICKS = 40;
    public static final float FAN_ANGLE_DEGREES = 60.0f;
    public static final double CONE_RADIUS = 5.0;
    private static final SoundEvent FIRE_SOUND = SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("superbwarfare", "m_79_fire_1p"));
    private static final SoundEvent ARMING_SOUND = SoundEvent.m_262824_((ResourceLocation)new ResourceLocation("dragonrise_reforge", "cluster_charge_starting"));
    private int corner;
    private int shotsFired;
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.m_135353_(ClusterChargeEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135041_);

    public ClusterChargeEntity(EntityType<? extends ClusterChargeEntity> type, Level level) {
        super(type, level);
    }

    public ClusterChargeEntity(EntityType<? extends ClusterChargeEntity> type, LivingEntity owner, Level level) {
        super(type, level);
        if (owner != null) {
            this.setOwnerUUID(owner.m_20148_());
        }
    }

    public ClusterChargeEntity(EntityType<? extends ClusterChargeEntity> type, LivingEntity owner, Level level, BlockPos pos, Direction direction, int corner) {
        super(type, level, pos);
        this.corner = corner;
        if (owner != null) {
            this.setOwnerUUID(owner.m_20148_());
        }
        this.m_6022_(direction);
    }

    public ClusterChargeEntity(Level level) {
        this((EntityType<? extends ClusterChargeEntity>)((EntityType)ModEntities.CLUSTER_CHARGE.get()), level);
    }

    public ClusterChargeEntity(LivingEntity owner, Level level) {
        this((EntityType<? extends ClusterChargeEntity>)((EntityType)ModEntities.CLUSTER_CHARGE.get()), owner, level);
    }

    public ClusterChargeEntity(LivingEntity owner, Level level, BlockPos pos, Direction direction, int corner) {
        this((EntityType<? extends ClusterChargeEntity>)((EntityType)ModEntities.CLUSTER_CHARGE.get()), owner, level, pos, direction, corner);
    }

    public boolean m_6469_(DamageSource source, float amount) {
        if (source.m_7640_() instanceof ClusterChargeEntity) {
            return false;
        }
        return super.m_6469_(source, amount);
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(OWNER_UUID, Optional.empty());
    }

    protected float m_6380_(Pose pose, EntityDimensions dimensions) {
        return 0.0f;
    }

    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128405_("Corner", this.corner);
        tag.m_128405_("ShotsFired", this.shotsFired);
        if (this.m_21805_() != null) {
            tag.m_128362_("Owner", this.m_21805_());
        }
        tag.m_128344_("Facing", (byte)this.f_31699_.m_122411_());
    }

    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (tag.m_128441_("Corner")) {
            this.corner = tag.m_128451_("Corner");
        }
        if (tag.m_128441_("ShotsFired")) {
            this.shotsFired = tag.m_128451_("ShotsFired");
        }
        UUID uuid = null;
        if (tag.m_128403_("Owner")) {
            uuid = tag.m_128342_("Owner");
        } else if (tag.m_128441_("Owner")) {
            try {
                uuid = UUID.fromString(tag.m_128461_("Owner"));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        if (uuid != null) {
            try {
                this.setOwnerUUID(uuid);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        if (tag.m_128441_("Facing")) {
            this.m_6022_(Direction.m_122376_((int)tag.m_128445_("Facing")));
        }
    }

    protected void m_6022_(Direction direction) {
        this.f_31699_ = direction;
        if (direction.m_122434_().m_122479_()) {
            this.m_146926_(0.0f);
            this.m_146922_(direction.m_122416_() * 90);
        } else {
            this.m_146926_(-90 * direction.m_122421_().m_122540_());
            this.m_146922_(0.0f);
        }
        this.f_19860_ = this.m_146909_();
        this.f_19859_ = this.m_146908_();
        this.m_7087_();
    }

    public void m_7087_() {
        Direction dir = this.f_31699_;
        if (dir == null) {
            return;
        }
        double d0 = 0.46875;
        double centerX = (double)this.f_31698_.m_123341_() + 0.5 - (double)dir.m_122429_() * d0;
        double centerY = (double)this.f_31698_.m_123342_() + 0.5 - (double)dir.m_122430_() * d0;
        double centerZ = (double)this.f_31698_.m_123343_() + 0.5 - (double)dir.m_122431_() * d0;
        double halfWidth = (double)this.m_7076_() / 32.0;
        double halfHeight = (double)this.m_7068_() / 32.0;
        Vec3 cornerOffset = this.calculateCornerOffset(dir, this.corner, halfWidth, halfHeight);
        double finalX = centerX + cornerOffset.f_82479_;
        double finalY = centerY + cornerOffset.f_82480_;
        double finalZ = centerZ + cornerOffset.f_82481_;
        this.m_20343_(finalX, finalY, finalZ);
        double dx = (double)this.m_7076_() / 32.0;
        double dy = (double)this.m_7068_() / 32.0;
        double dz = (double)this.m_7076_() / 32.0;
        switch (dir.m_122434_()) {
            case X: {
                dx = 0.03125;
                break;
            }
            case Y: {
                dy = 0.03125;
                break;
            }
            case Z: {
                dz = 0.03125;
            }
        }
        this.m_20011_(new AABB(finalX - dx, finalY - dy, finalZ - dz, finalX + dx, finalY + dy, finalZ + dz));
    }

    private Vec3 calculateCornerOffset(Direction direction, int corner, double width, double height) {
        if (corner < 0 || corner > 3) {
            return Vec3.f_82478_;
        }
        boolean left = corner == 0 || corner == 1;
        boolean top = corner == 0 || corner == 3;
        double signY = top ? 1.0 : -1.0;
        return switch (direction) {
            case Direction.NORTH -> {
                double signX = left ? 1.0 : -1.0;
                yield new Vec3(signX * width, signY * height, 0.0);
            }
            case Direction.SOUTH -> {
                double signX = left ? -1.0 : 1.0;
                yield new Vec3(signX * width, signY * height, 0.0);
            }
            case Direction.WEST -> {
                double signZ = left ? -1.0 : 1.0;
                yield new Vec3(0.0, signY * height, signZ * width);
            }
            case Direction.EAST -> {
                double signZ = left ? 1.0 : -1.0;
                yield new Vec3(0.0, signY * height, signZ * width);
            }
            default -> Vec3.f_82478_;
        };
    }

    public int m_7076_() {
        return 8;
    }

    public int m_7068_() {
        return 8;
    }

    public void m_5553_(Entity pBrokenEntity) {
    }

    public void m_7084_() {
        this.m_5496_(SoundEvents.f_12015_, 1.0f, 1.0f);
    }

    public boolean m_7088_() {
        if (!this.m_9236_().m_45786_((Entity)this)) {
            return false;
        }
        BlockState blockstate = this.m_9236_().m_8055_(this.f_31698_.m_121945_(this.f_31699_.m_122424_()));
        boolean wallOk = blockstate.m_280296_() || this.f_31699_.m_122434_().m_122479_() && DiodeBlock.m_52586_((BlockState)blockstate);
        return wallOk && this.m_9236_().m_6249_((Entity)this, this.m_20191_(), f_31697_).isEmpty();
    }

    public Packet<ClientGamePacketListener> m_5654_() {
        int data = this.corner * 10 + this.f_31699_.m_122411_();
        return new ClientboundAddEntityPacket((Entity)this, data, this.m_31748_());
    }

    public void m_141965_(ClientboundAddEntityPacket packet) {
        super.m_141965_(packet);
        this.corner = packet.m_131509_() / 10;
        this.m_6022_(Direction.m_122376_((int)(packet.m_131509_() % 10)));
    }

    public void setOwnerUUID(UUID pUuid) {
        this.f_19804_.m_135381_(OWNER_UUID, Optional.ofNullable(pUuid));
    }

    public UUID m_21805_() {
        return ((Optional)this.f_19804_.m_135370_(OWNER_UUID)).orElse(null);
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return entity == this.m_269323_();
    }

    public boolean isFacingLeft() {
        return this.corner == 0 || this.corner == 1;
    }

    public boolean m_6087_() {
        return !this.m_213877_();
    }

    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        if (this.isOwnedBy((LivingEntity)player) && player.m_6144_()) {
            if (!this.m_9236_().m_5776_()) {
                this.m_146870_();
            }
            if (!player.m_150110_().f_35937_) {
                ItemHandlerHelper.giveItemToPlayer((Player)player, (ItemStack)new ItemStack((ItemLike)ModItems.CLUSTER_CHARGE.get()));
            }
        }
        return InteractionResult.m_19078_((boolean)this.m_9236_().m_5776_());
    }

    public void m_8119_() {
        super.m_8119_();
        if (this.m_9236_().m_5776_()) {
            return;
        }
        if (this.f_19797_ == 16) {
            this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), ARMING_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        if (this.f_19797_ >= 16) {
            this.spawnPortSparks();
        }
        if (this.f_19797_ >= 76 && this.shotsFired < 5 && (this.f_19797_ - 76) % 10 == 0) {
            this.fireGrenade(this.shotsFired);
            ++this.shotsFired;
        }
        if (this.shotsFired >= 5) {
            this.m_146870_();
        }
    }

    public void m_142687_(Entity.RemovalReason reason) {
        if (!this.m_9236_().m_5776_() && this.f_19797_ >= 16 && this.f_19797_ <= 76) {
            ClientboundStopSoundPacket stopPacket = new ClientboundStopSoundPacket(ARMING_SOUND.m_11660_(), SoundSource.BLOCKS);
            for (Player player : this.m_9236_().m_6907_()) {
                if (!(player instanceof ServerPlayer)) continue;
                ServerPlayer serverPlayer = (ServerPlayer)player;
                serverPlayer.f_8906_.m_9829_((Packet)stopPacket);
            }
        }
        super.m_142687_(reason);
    }

    private void spawnPortSparks() {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        Vec3 launchDir = Vec3.m_82528_((Vec3i)this.f_31699_.m_122424_().m_122436_());
        Vec3 port = Vec3.m_82512_((Vec3i)this.f_31698_).m_82549_(launchDir.m_82490_(0.45));
        serverLevel.m_8767_((ParticleOptions)((SimpleParticleType)ModParticleTypes.FIRE_STAR.get()), port.f_82479_, port.f_82480_, port.f_82481_, 2, launchDir.f_82479_ * 0.35, launchDir.f_82480_ * 0.35, launchDir.f_82481_ * 0.35, 0.35);
    }

    private void fireGrenade(int shotIndex) {
        if (this.m_9236_().m_5776_()) {
            return;
        }
        Direction launchDir = this.f_31699_.m_122424_();
        Vec3 baseDir = Vec3.m_82528_((Vec3i)launchDir.m_122436_());
        float angle = -30.0f + 15.0f * (float)shotIndex;
        Vec3 dir = baseDir.m_82524_((float)Math.toRadians(angle));
        BlockPos behind = this.f_31698_.m_5484_(launchDir, 2);
        Vec3 spawn = Vec3.m_82512_((Vec3i)behind).m_82549_(dir.m_82490_(0.25));
        if (this.f_31699_ == Direction.UP) {
            Vec3 up = new Vec3(0.0, 1.0, 0.0);
            Vec3 side = baseDir.m_82537_(up);
            if (side.m_82556_() < 1.0E-6) {
                side = new Vec3(1.0, 0.0, 0.0);
            }
            side = side.m_82541_();
            double rad = Math.toRadians(angle);
            spawn = spawn.m_82549_(side.m_82490_(5.0 * Math.sin(rad))).m_82549_(baseDir.m_82490_(5.0 * (1.0 - Math.cos(rad))));
        }
        LivingEntity owner = this.m_269323_();
        HandGrenadeEntity grenade = new HandGrenadeEntity(owner, this.m_9236_());
        grenade.setLife(40);
        grenade.setExplosionRadius(((Integer)ExplosionConfig.M67_GRENADE_EXPLOSION_RADIUS.get()).floatValue() / 3.0f);
        grenade.m_6034_(spawn.f_82479_, spawn.f_82480_, spawn.f_82481_);
        grenade.m_6686_(dir.f_82479_, dir.f_82480_, dir.f_82481_, 0.87f, 0.0f);
        this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), FIRE_SOUND, SoundSource.BLOCKS, 1.0f, 1.0f);
        this.m_9236_().m_7967_((Entity)grenade);
    }
}

