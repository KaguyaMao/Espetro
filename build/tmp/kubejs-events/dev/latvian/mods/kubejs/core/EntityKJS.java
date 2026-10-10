/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.EndTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.commands.TeleportCommand
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.decoration.ItemFrame
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Team
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.latvian.mods.kubejs.core.MessageSenderKJS;
import dev.latvian.mods.kubejs.core.NoMixinException;
import dev.latvian.mods.kubejs.core.WithPersistentData;
import dev.latvian.mods.kubejs.entity.RayTraceResultJS;
import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.player.EntityArrayList;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.rhino.util.HideFromJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import java.util.Set;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface EntityKJS
extends WithPersistentData,
MessageSenderKJS,
ScriptTypeHolder {
    default public Entity kjs$self() {
        return (Entity)this;
    }

    default public Level kjs$getLevel() {
        return this.kjs$self().m_9236_();
    }

    @Nullable
    default public MinecraftServer kjs$getServer() {
        return this.kjs$getLevel().m_7654_();
    }

    default public String kjs$getType() {
        return String.valueOf(RegistryInfo.ENTITY_TYPE.getId(this.kjs$self().m_6095_()));
    }

    default public GameProfile kjs$getProfile() {
        return new GameProfile(this.kjs$self().m_20148_(), this.kjs$self().m_6302_());
    }

    @Override
    default public Component kjs$getName() {
        return this.kjs$self().m_7755_();
    }

    @Override
    default public Component kjs$getDisplayName() {
        return this.kjs$self().m_5446_();
    }

    @Override
    default public void kjs$tell(Component message) {
        this.kjs$self().m_213846_(message);
    }

    @Override
    default public int kjs$runCommand(String command) {
        Level level = this.kjs$getLevel();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            return level2.m_7654_().m_129892_().m_230957_(this.kjs$self().m_20203_(), command);
        }
        return 0;
    }

    @Override
    default public int kjs$runCommandSilent(String command) {
        Level level = this.kjs$getLevel();
        if (level instanceof ServerLevel) {
            ServerLevel level2 = (ServerLevel)level;
            return level2.m_7654_().m_129892_().m_230957_(this.kjs$self().m_20203_().m_81324_(), command);
        }
        return 0;
    }

    default public boolean kjs$isPlayer() {
        return false;
    }

    @Nullable
    default public ItemStack kjs$getItem() {
        return null;
    }

    default public boolean kjs$isFrame() {
        return this instanceof ItemFrame;
    }

    default public boolean kjs$isLiving() {
        return false;
    }

    default public boolean kjs$isMonster() {
        return !this.kjs$self().m_6095_().m_20674_().m_21609_();
    }

    default public boolean kjs$isAnimal() {
        return this.kjs$self().m_6095_().m_20674_().m_21610_();
    }

    default public boolean kjs$isAmbientCreature() {
        return this.kjs$self().m_6095_().m_20674_() == MobCategory.AMBIENT;
    }

    default public boolean kjs$isWaterCreature() {
        return this.kjs$self().m_6095_().m_20674_() == MobCategory.WATER_CREATURE;
    }

    default public boolean kjs$isPeacefulCreature() {
        return this.kjs$self().m_6095_().m_20674_().m_21609_();
    }

    default public void kjs$setX(double x) {
        this.kjs$setPosition(x, this.kjs$self().m_20186_(), this.kjs$self().m_20189_());
    }

    default public void kjs$setY(double y) {
        this.kjs$setPosition(this.kjs$self().m_20185_(), y, this.kjs$self().m_20189_());
    }

    default public void kjs$setZ(double z) {
        this.kjs$setPosition(this.kjs$self().m_20185_(), this.kjs$self().m_20186_(), z);
    }

    default public double kjs$getMotionX() {
        return this.kjs$self().m_20184_().f_82479_;
    }

    default public void kjs$setMotionX(double x) {
        Vec3 m = this.kjs$self().m_20184_();
        this.kjs$self().m_20334_(x, m.f_82480_, m.f_82481_);
    }

    default public double kjs$getMotionY() {
        return this.kjs$self().m_20184_().f_82480_;
    }

    default public void kjs$setMotionY(double y) {
        Vec3 m = this.kjs$self().m_20184_();
        this.kjs$self().m_20334_(m.f_82479_, y, m.f_82481_);
    }

    default public double kjs$getMotionZ() {
        return this.kjs$self().m_20184_().f_82481_;
    }

    default public void kjs$setMotionZ(double z) {
        Vec3 m = this.kjs$self().m_20184_();
        this.kjs$self().m_20334_(m.f_82479_, m.f_82480_, z);
    }

    default public void kjs$teleportTo(ResourceLocation dimension, double x, double y, double z, float yaw, float pitch) {
        Level previousLevel = this.kjs$getLevel();
        ServerLevel level = this.kjs$getServer().m_129880_(ResourceKey.m_135785_((ResourceKey)Registries.f_256858_, (ResourceLocation)dimension));
        if (level == null) {
            throw new IllegalArgumentException("Invalid dimension!");
        }
        if (!Level.m_46741_((BlockPos)BlockPos.m_274561_((double)x, (double)y, (double)z))) {
            throw new IllegalArgumentException("Invalid coordinates!");
        }
        if (Float.isNaN(yaw) || Float.isNaN(pitch)) {
            throw new IllegalArgumentException("Invalid rotation!");
        }
        if (level == previousLevel) {
            this.kjs$setPositionAndRotation(x, y, z, yaw, pitch);
            return;
        }
        try {
            TeleportCommand.m_139014_((CommandSourceStack)this.kjs$self().m_20203_(), (Entity)this.kjs$self(), (ServerLevel)level, (double)x, (double)y, (double)z, Set.of(), (float)yaw, (float)pitch, null);
        }
        catch (CommandSyntaxException e) {
            throw new IllegalArgumentException(e.getRawMessage().getString());
        }
    }

    default public void kjs$setPosition(BlockContainerJS block) {
        this.kjs$teleportTo(block.getDimension(), block.getX(), block.getY(), block.getZ(), this.kjs$self().m_146908_(), this.kjs$self().m_146909_());
    }

    default public void kjs$setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
        this.kjs$self().m_7678_(x, y, z, yaw, pitch);
    }

    default public void kjs$setPosition(double x, double y, double z) {
        this.kjs$setPositionAndRotation(x, y, z, this.kjs$self().m_146908_(), this.kjs$self().m_146909_());
    }

    default public void kjs$setRotation(float yaw, float pitch) {
        this.kjs$setPositionAndRotation(this.kjs$self().m_20185_(), this.kjs$self().m_20186_(), this.kjs$self().m_20189_(), yaw, pitch);
    }

    default public EntityArrayList kjs$getPassengers() {
        return new EntityArrayList(this.kjs$getLevel(), this.kjs$self().m_20197_());
    }

    default public String kjs$getTeamId() {
        Team team = this.kjs$self().m_5647_();
        return team == null ? "" : team.m_5758_();
    }

    default public boolean kjs$isOnScoreboardTeam(String teamId) {
        PlayerTeam team = this.kjs$self().m_20193_().m_6188_().m_83489_(teamId);
        return team != null && this.kjs$self().m_20031_((Team)team);
    }

    default public Direction kjs$getFacing() {
        if (this.kjs$self().m_146909_() > 45.0f) {
            return Direction.DOWN;
        }
        if (this.kjs$self().m_146909_() < -45.0f) {
            return Direction.UP;
        }
        return this.kjs$self().m_6350_();
    }

    default public BlockContainerJS kjs$getBlock() {
        return new BlockContainerJS(this.kjs$getLevel(), this.kjs$self().m_20183_());
    }

    default public CompoundTag kjs$getNbt() {
        CompoundTag nbt = new CompoundTag();
        this.kjs$self().m_20240_(nbt);
        return nbt;
    }

    default public void kjs$setNbt(@Nullable CompoundTag nbt) {
        if (nbt != null) {
            this.kjs$self().m_20258_(nbt);
        }
    }

    default public Entity kjs$mergeNbt(@Nullable CompoundTag tag) {
        if (tag == null || tag.m_128456_()) {
            return this.kjs$self();
        }
        CompoundTag nbt = this.kjs$getNbt();
        for (String k : tag.m_128431_()) {
            Tag t = tag.m_128423_(k);
            if (t == null || t == EndTag.f_128534_) {
                nbt.m_128473_(k);
                continue;
            }
            nbt.m_128365_(k, tag.m_128423_(k));
        }
        this.kjs$setNbt(nbt);
        return this.kjs$self();
    }

    default public void kjs$playSound(SoundEvent id, float volume, float pitch) {
        this.kjs$getLevel().m_6263_(null, this.kjs$self().m_20185_(), this.kjs$self().m_20186_(), this.kjs$self().m_20189_(), id, this.kjs$self().m_5720_(), volume, pitch);
    }

    default public void kjs$playSound(SoundEvent id) {
        this.kjs$playSound(id, 1.0f, 1.0f);
    }

    default public void kjs$spawn() {
        this.kjs$getLevel().m_7967_(this.kjs$self());
    }

    default public void kjs$attack(float hp) {
        this.kjs$self().m_6469_(this.kjs$self().m_269291_().m_269264_(), hp);
    }

    default public double kjs$getDistance(double x, double y, double z) {
        return Math.sqrt(this.kjs$self().m_20275_(x, y, z));
    }

    default public double kjs$getDistanceSq(BlockPos pos) {
        return this.kjs$self().m_20275_((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5);
    }

    default public double kjs$getDistance(BlockPos pos) {
        return Math.sqrt(this.kjs$getDistanceSq(pos));
    }

    default public RayTraceResultJS kjs$rayTrace(double distance, boolean fluids) {
        double entityDistanceSquared;
        double distanceSquared;
        AABB bound;
        Entity entity = this.kjs$self();
        HitResult hitResult = entity.m_19907_(distance, 0.0f, fluids);
        Vec3 eyePosition = entity.m_146892_();
        Vec3 lookVector = entity.m_20252_(1.0f);
        Vec3 traceEnd = eyePosition.m_82520_(lookVector.f_82479_ * distance, lookVector.f_82480_ * distance, lookVector.f_82481_ * distance);
        EntityHitResult entityHitResult = ProjectileUtil.m_37287_((Entity)entity, (Vec3)eyePosition, (Vec3)traceEnd, (AABB)(bound = entity.m_20191_().m_82369_(lookVector.m_82490_(distance)).m_82377_(1.0, 1.0, 1.0)), ent -> !ent.m_5833_() && ent.m_6087_(), (double)(distanceSquared = hitResult.m_6662_() != HitResult.Type.MISS ? hitResult.m_82450_().m_82557_(eyePosition) : distance * distance));
        if (entityHitResult != null && ((entityDistanceSquared = eyePosition.m_82557_(entityHitResult.m_82450_())) < distanceSquared || hitResult.m_6662_() == HitResult.Type.MISS)) {
            hitResult = entityHitResult;
        }
        return new RayTraceResultJS(entity, hitResult, distance);
    }

    default public RayTraceResultJS kjs$rayTrace(double distance) {
        return this.kjs$rayTrace(distance, true);
    }

    @HideFromJS
    @Nullable
    default public CompoundTag kjs$getRawPersistentData() {
        throw new NoMixinException();
    }

    @HideFromJS
    default public void kjs$setRawPersistentData(@Nullable CompoundTag tag) {
        throw new NoMixinException();
    }

    @Override
    default public ScriptType kjs$getScriptType() {
        return this.kjs$getLevel().kjs$getScriptType();
    }
}

