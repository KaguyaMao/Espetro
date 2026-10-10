/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity
 *  com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity$Type
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModEntities
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.init.ModTags$Items
 *  com.atsuishio.superbwarfare.tools.OBB
 *  com.atsuishio.superbwarfare.tools.OBB$Part
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  com.atsuishio.superbwarfare.tools.VectorTool
 *  net.minecraft.ChatFormatting
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.jetbrains.annotations.NotNull
 *  org.joml.Matrix4d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.joml.Vector4d
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.core.animation.AnimatableManager$ControllerRegistrar
 *  software.bernie.geckolib.core.animation.AnimationController
 *  software.bernie.geckolib.core.animation.AnimationState
 *  software.bernie.geckolib.core.animation.RawAnimation
 *  software.bernie.geckolib.core.object.PlayState
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.entity.projectile.CannonShellEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.joml.Vector4d;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import tech.vvp.vvp.entity.vehicle.VvpVehicleBase;
import tech.vvp.vvp.init.ModItems;

public class D30Entity
extends VvpVehicleBase {
    public static final EntityDataAccessor<Float> TARGET_PITCH = SynchedEntityData.m_135353_(D30Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    public static final EntityDataAccessor<Float> TARGET_YAW = SynchedEntityData.m_135353_(D30Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    public static final EntityDataAccessor<Boolean> LOADED = SynchedEntityData.m_135353_(D30Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    public static final EntityDataAccessor<Integer> SHOOT_ANIM_TIME = SynchedEntityData.m_135353_(D30Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    public OBB pitchController = new OBB(OBB.vec3ToVector3d((Vec3)this.m_20182_()), new Vector3d(0.1, 0.15, 0.1), new Quaterniond(), OBB.Part.INTERACTIVE);
    public OBB yawController = new OBB(OBB.vec3ToVector3d((Vec3)this.m_20182_()), new Vector3d(0.145, 0.145, 0.15), new Quaterniond(), OBB.Part.INTERACTIVE);
    public OBB body = new OBB(OBB.vec3ToVector3d((Vec3)this.m_20182_()), new Vector3d(0.8, 0.6, 1.5), new Quaterniond(), OBB.Part.BODY);
    private static final float PROJECTILE_VELOCITY = 15.0f;
    private static final float GRAVITY = 0.05f;
    private static final RawAnimation SHOOT_ANIM = RawAnimation.begin().thenPlay("animation.d30.shoot");
    public double interactionTick = 1.0;
    public int cooldown;
    private int lastShootAnimTime = 0;

    public D30Entity(EntityType<D30Entity> type, Level world) {
        super(type, world);
    }

    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(new AnimationController[]{new AnimationController((GeoAnimatable)this, "shoot", 0, this::shootPredicate)});
    }

    private PlayState shootPredicate(AnimationState<D30Entity> state) {
        int currentTime = (Integer)this.f_19804_.m_135370_(SHOOT_ANIM_TIME);
        if (currentTime != this.lastShootAnimTime && currentTime > 0) {
            state.getController().forceAnimationReset();
            state.getController().setAnimation(SHOOT_ANIM);
            this.lastShootAnimTime = currentTime;
        }
        return PlayState.CONTINUE;
    }

    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(TARGET_PITCH, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(TARGET_YAW, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(LOADED, (Object)false);
        this.f_19804_.m_135372_(SHOOT_ANIM_TIME, (Object)0);
    }

    public void m_7380_(@NotNull CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128350_("TargetPitch", ((Float)this.f_19804_.m_135370_(TARGET_PITCH)).floatValue());
        compound.m_128350_("TargetYaw", ((Float)this.f_19804_.m_135370_(TARGET_YAW)).floatValue());
        compound.m_128379_("Loaded", ((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue());
    }

    public void m_7378_(@NotNull CompoundTag compound) {
        super.m_7378_(compound);
        this.f_19804_.m_135381_(TARGET_PITCH, (Object)Float.valueOf(compound.m_128457_("TargetPitch")));
        this.f_19804_.m_135381_(TARGET_YAW, (Object)Float.valueOf(compound.m_128457_("TargetYaw")));
        this.f_19804_.m_135381_(LOADED, (Object)compound.m_128471_("Loaded"));
    }

    public boolean m_5829_() {
        return true;
    }

    public boolean m_6094_() {
        return false;
    }

    public void m_5997_(double x, double y, double z) {
    }

    public void m_6478_(MoverType type, Vec3 pos) {
    }

    public void m_20256_(Vec3 motion) {
        super.m_20256_(Vec3.f_82478_);
    }

    @NotNull
    public InteractionResult m_6096_(@NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.m_21205_();
        OBB lookingObb = OBB.getLookingObb((Player)player, (double)player.getEntityReach());
        if (stack.m_41619_()) {
            if (lookingObb == this.yawController) {
                Vec3 centerPos = new Vec3(this.yawController.center.x, this.yawController.center.y, this.yawController.center.z);
                this.interactEvent(centerPos);
                float delta = (player.m_6144_() ? -0.04f : 0.04f) * (float)this.interactionTick;
                this.f_19804_.m_135381_(TARGET_YAW, (Object)Float.valueOf(Mth.m_14036_((float)(((Float)this.f_19804_.m_135370_(TARGET_YAW)).floatValue() + delta), (float)-180.0f, (float)180.0f)));
                player.m_6674_(InteractionHand.MAIN_HAND);
                return InteractionResult.SUCCESS;
            }
            if (lookingObb == this.pitchController) {
                Vec3 centerPos = new Vec3(this.pitchController.center.x, this.pitchController.center.y, this.pitchController.center.z);
                this.interactEvent(centerPos);
                float delta = (player.m_6144_() ? 0.04f : -0.04f) * (float)this.interactionTick;
                this.f_19804_.m_135381_(TARGET_PITCH, (Object)Float.valueOf(Mth.m_14036_((float)(((Float)this.f_19804_.m_135370_(TARGET_PITCH)).floatValue() + delta), (float)-70.0f, (float)7.0f)));
                player.m_6674_(InteractionHand.MAIN_HAND);
                return InteractionResult.SUCCESS;
            }
            if (player.m_6144_() && lookingObb != this.yawController && lookingObb != this.pitchController) {
                if (this.cooldown == 0 && ((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue()) {
                    this.shoot(player);
                    player.m_6674_(InteractionHand.MAIN_HAND);
                    return InteractionResult.SUCCESS;
                }
                if (!((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue()) {
                    if (!this.m_9236_().f_46443_) {
                        player.m_5661_((Component)Component.m_237113_((String)"Not loaded! Use 122mm shell").m_130940_(ChatFormatting.RED), true);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }
        if (stack.m_150930_((Item)ModItems.SHELL_122MM.get())) {
            if (!this.m_9236_().f_46443_) {
                if (((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue()) {
                    player.m_5661_((Component)Component.m_237113_((String)"Already loaded!").m_130940_(ChatFormatting.YELLOW), true);
                } else {
                    this.f_19804_.m_135381_(LOADED, (Object)true);
                    if (!player.m_7500_()) {
                        stack.m_41774_(1);
                    }
                    this.m_9236_().m_5594_(null, this.m_20097_(), (SoundEvent)ModSounds.TYPE_63_RELOAD.get(), SoundSource.PLAYERS, 1.5f, 0.8f);
                    player.m_5661_((Component)Component.m_237113_((String)"Shell loaded!").m_130940_(ChatFormatting.GREEN), true);
                    this.cooldown = 10;
                }
            }
            player.m_6674_(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }
        if (player.m_6144_() && stack.m_204117_(ModTags.Items.TOOLS_CROWBAR)) {
            this.retrieve(player);
            player.m_6674_(InteractionHand.MAIN_HAND);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public void interactEvent(Vec3 vec3) {
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel)level;
            this.interactionTick += 1.5;
            if (this.cooldown <= 0) {
                this.cooldown = 4;
                serverLevel.m_6263_(null, vec3.f_82479_, vec3.f_82480_, vec3.f_82481_, (SoundEvent)ModSounds.HAND_WHEEL_ROT.get(), SoundSource.PLAYERS, 0.5f, this.f_19796_.m_188501_() * 0.05f + 0.975f);
            }
        }
    }

    public void shoot(Player player) {
        int i;
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.f_19804_.m_135381_(LOADED, (Object)false);
        Vec3 shootPos = this.getShootPos();
        Vec3 barrelDir = this.getBarrelVector(1.0f);
        CannonShellEntity shell = new CannonShellEntity((EntityType)ModEntities.CANNON_SHELL.get(), (Level)serverLevel);
        shell.m_6034_(shootPos.f_82479_, shootPos.f_82480_, shootPos.f_82481_);
        shell.m_5602_((Entity)player);
        shell.setDamage(400.0f);
        shell.setExplosionDamage(350.0f);
        shell.setExplosionRadius(8.0f);
        shell.setType(CannonShellEntity.Type.HE);
        shell.m_6686_(barrelDir.f_82479_, barrelDir.f_82480_, barrelDir.f_82481_, 15.0f, 0.5f);
        serverLevel.m_7967_((Entity)shell);
        serverLevel.m_6263_(null, shootPos.f_82479_, shootPos.f_82480_, shootPos.f_82481_, (SoundEvent)ModSounds.RPG_FIRE_3P.get(), SoundSource.PLAYERS, 7.0f, 0.7f + this.f_19796_.m_188501_() * 0.2f);
        ParticleTool.spawnMediumCannonMuzzleParticles((Vec3)barrelDir, (Vec3)shootPos, (ServerLevel)serverLevel, (Entity)this);
        Vec3 artilleryPos = this.m_20182_();
        serverLevel.m_6907_().forEach(p -> {
            if (p.m_20238_(artilleryPos) < 225.0) {
                p.m_6469_(serverLevel.m_269111_().m_269036_((Entity)this, (Entity)player), 0.0f);
            }
        });
        for (i = 0; i < 20; ++i) {
            double offsetX = (this.f_19796_.m_188500_() - 0.5) * 2.5;
            double offsetZ = (this.f_19796_.m_188500_() - 0.5) * 2.5;
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123759_, artilleryPos.f_82479_ + offsetX, artilleryPos.f_82480_ + 0.1, artilleryPos.f_82481_ + offsetZ, 1, 0.0, 0.05, 0.0, 0.02);
        }
        for (i = 0; i < 15; ++i) {
            Vec3 smokeOffset = barrelDir.m_82490_(0.5 + (double)i * 0.3);
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123755_, shootPos.f_82479_ + smokeOffset.f_82479_, shootPos.f_82480_ + smokeOffset.f_82480_, shootPos.f_82481_ + smokeOffset.f_82481_, 2, 0.1, 0.1, 0.1, 0.01);
        }
        for (int angle = 0; angle < 360; angle += 15) {
            double rad = Math.toRadians(angle);
            double ringX = Math.cos(rad) * 1.5;
            double ringZ = Math.sin(rad) * 1.5;
            serverLevel.m_8767_((ParticleOptions)ParticleTypes.f_123777_, shootPos.f_82479_ + ringX, shootPos.f_82480_, shootPos.f_82481_ + ringZ, 1, ringX * 0.3, 0.0, ringZ * 0.3, 0.15);
        }
        Matrix4d turretTransform = this.getTurretTransform(1.0f);
        Vector4d casingPos = this.transformPosition(turretTransform, -0.8, 0.3, -0.8);
        ItemEntity casingEntity = new ItemEntity((Level)serverLevel, casingPos.x, casingPos.y, casingPos.z, new ItemStack((ItemLike)ModItems.SHELL_122MM_CASING.get()));
        Vec3 ejectDir = this.getLeftVector(1.0f).m_82520_(0.0, 0.3, 0.0).m_82541_();
        casingEntity.m_20256_(ejectDir.m_82490_(0.4 + this.f_19796_.m_188500_() * 0.2));
        casingEntity.m_32010_(20);
        serverLevel.m_7967_((Entity)casingEntity);
        this.cooldown = 20;
        this.f_19804_.m_135381_(SHOOT_ANIM_TIME, (Object)((Integer)this.f_19804_.m_135370_(SHOOT_ANIM_TIME) + 1));
        player.m_5661_((Component)Component.m_237113_((String)"FIRE!").m_130944_(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), true);
    }

    public Vec3 getShootPos() {
        Matrix4d barrelTransform = this.getBarrelTransform(1.0f);
        Vector4d pos = this.transformPosition(barrelTransform, 0.0, 0.0, 2.0);
        return new Vec3(pos.x, pos.y, pos.z);
    }

    public Vec3 getLeftVector(float partialTicks) {
        Matrix4d turretTransform = this.getTurretTransform(partialTicks);
        Vector4d left = this.transformPosition(turretTransform, 1.0, 0.0, 0.0);
        Vec3 center = this.m_20182_();
        return new Vec3(left.x - center.f_82479_, left.y - center.f_82480_, left.z - center.f_82481_).m_82541_();
    }

    public void m_6075_() {
        super.m_6075_();
        if (this.cooldown > 0) {
            --this.cooldown;
        }
        this.interactionTick *= 0.9;
        if (this.interactionTick < 1.0) {
            this.interactionTick = 1.0;
        }
        this.m_6210_();
    }

    public void travel() {
        float diffY = ((Float)this.f_19804_.m_135370_(TARGET_YAW)).floatValue() - this.getTurretYRot();
        this.setTurretYRot(this.getTurretYRot() + 0.2f * diffY);
        float diffX = ((Float)this.f_19804_.m_135370_(TARGET_PITCH)).floatValue() - this.getTurretXRot();
        this.setTurretXRot(Mth.m_14036_((float)(this.getTurretXRot() + 0.2f * diffX), (float)-70.0f, (float)7.0f));
    }

    private void retrieve(Player player) {
        ItemStack shell;
        if (this.m_9236_().f_46443_) {
            return;
        }
        for (ItemStack stack : this.getRetrieveItems()) {
            ItemStack copy = stack.m_41777_();
            if (player.m_36356_(copy)) continue;
            player.m_36176_(copy, false);
        }
        if (((Boolean)this.f_19804_.m_135370_(LOADED)).booleanValue() && !player.m_36356_(shell = new ItemStack((ItemLike)ModItems.SHELL_122MM.get()))) {
            player.m_36176_(shell, false);
        }
        this.m_146870_();
    }

    @NotNull
    public List<ItemStack> getRetrieveItems() {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack((ItemLike)ModItems.D30_ITEM.get()));
        return list;
    }

    @NotNull
    public List<OBB> getOBBs() {
        return List.of(this.pitchController, this.yawController, this.body);
    }

    public void updateOBB() {
        Matrix4d turretTransform = this.getTurretTransform(1.0f);
        Vector4d yawPos = this.transformPosition(turretTransform, 0.6, -0.29, -0.9);
        this.yawController.center.set(yawPos.x, yawPos.y, yawPos.z);
        this.yawController.rotation().set((Quaterniondc)VectorTool.combineRotationsTurret((float)1.0f, (VehicleEntity)this));
        Vector4d pitchPos = this.transformPosition(turretTransform, 0.84, -0.02, -0.66);
        this.pitchController.center.set(pitchPos.x, pitchPos.y, pitchPos.z);
        this.pitchController.rotation().set((Quaterniondc)VectorTool.combineRotationsTurret((float)1.0f, (VehicleEntity)this));
        Matrix4d vehicleTransform = this.getVehicleTransform(1.0f);
        Vector4d bodyPos = this.transformPosition(vehicleTransform, 0.0, 0.2, 0.0);
        this.body.center.set(bodyPos.x, bodyPos.y, bodyPos.z);
        this.body.rotation().set((Quaterniondc)VectorTool.combineRotations((float)1.0f, (VehicleEntity)this));
    }

    public boolean banHand(LivingEntity entity) {
        return false;
    }

    public boolean m_7310_(@NotNull Entity passenger) {
        return false;
    }
}

