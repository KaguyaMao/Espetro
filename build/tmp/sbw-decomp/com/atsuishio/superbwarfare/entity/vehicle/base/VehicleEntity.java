/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.VehiclePropertyModifier$DefaultImpls
 *  com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.UnmodifiableIterator
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.math.Axis
 *  javax.annotation.ParametersAreNonnullByDefault
 *  kotlin.Deprecated
 *  kotlin.Lazy
 *  kotlin.LazyKt
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.ReplaceWith
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmName
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.MutablePropertyReference1
 *  kotlin.jvm.internal.MutablePropertyReference1Impl
 *  kotlin.jvm.internal.Reflection
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.ranges.RangesKt
 *  kotlin.reflect.KProperty
 *  kotlin.sequences.Sequence
 *  kotlin.sequences.SequencesKt
 *  kotlin.text.StringsKt
 *  kotlinx.serialization.DeserializationStrategy
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.CameraType
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Holder
 *  net.minecraft.core.NonNullList
 *  net.minecraft.core.Position
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.IntArrayTag
 *  net.minecraft.nbt.IntTag
 *  net.minecraft.nbt.ListTag
 *  net.minecraft.nbt.StringTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.game.ClientGamePacketListener
 *  net.minecraft.network.protocol.game.ClientboundSetPassengersPacket
 *  net.minecraft.network.protocol.game.ClientboundSoundPacket
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.level.TicketType
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.ContainerHelper
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.MenuProvider
 *  net.minecraft.world.SimpleMenuProvider
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.Entity$MoveFunction
 *  net.minecraft.world.entity.Entity$RemovalReason
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.HasCustomInventoryScreen
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.MoverType
 *  net.minecraft.world.entity.OwnableEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.entity.projectile.AbstractArrow
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.entity.projectile.ProjectileUtil
 *  net.minecraft.world.entity.vehicle.DismountHelper
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.NameTagItem
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.CollisionGetter
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.gameevent.GameEvent
 *  net.minecraft.world.level.material.FluidState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec2
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.scores.Team
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.common.util.FakePlayer
 *  net.minecraftforge.common.util.LazyOptional
 *  net.minecraftforge.energy.IEnergyStorage
 *  net.minecraftforge.entity.IEntityAdditionalSpawnData
 *  net.minecraftforge.fluids.FluidType
 *  net.minecraftforge.items.ItemHandlerHelper
 *  net.minecraftforge.network.NetworkHooks
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Math
 *  org.joml.Matrix4d
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3d
 *  org.joml.Vector3dc
 *  org.joml.Vector3f
 *  org.joml.Vector4d
 */
package com.atsuishio.superbwarfare.entity.vehicle.base;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.advancement.CriteriaRegister;
import com.atsuishio.superbwarfare.annotation.ExcludeBvrSync;
import com.atsuishio.superbwarfare.capability.energy.SyncedEntityEnergyStorage;
import com.atsuishio.superbwarfare.capability.energy.VehicleEnergyStorage;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance;
import com.atsuishio.superbwarfare.client.lighting.VehicleLightingHandler;
import com.atsuishio.superbwarfare.client.model.entity.VehicleModelInstance;
import com.atsuishio.superbwarfare.compat.valkyrienskies.ValkyrienSkiesCompat;
import com.atsuishio.superbwarfare.config.server.SyncConfig;
import com.atsuishio.superbwarfare.config.server.VehicleConfig;
import com.atsuishio.superbwarfare.data.DataLoader;
import com.atsuishio.superbwarfare.data.ObjectToList;
import com.atsuishio.superbwarfare.data.StringOrVec3;
import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.data.gun.DefaultGunData;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.data.gun.ShootPos;
import com.atsuishio.superbwarfare.data.gun.SoundInfo;
import com.atsuishio.superbwarfare.data.vehicle.DefaultVehicleData;
import com.atsuishio.superbwarfare.data.vehicle.VehicleData;
import com.atsuishio.superbwarfare.data.vehicle.VehiclePropertyModifier;
import com.atsuishio.superbwarfare.data.vehicle.subdata.CameraPos;
import com.atsuishio.superbwarfare.data.vehicle.subdata.DestroyInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.DismountInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.OBBInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.RadarInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleContainerType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.EntityUtilKt;
import com.atsuishio.superbwarfare.entity.IBvrSyncableEntity;
import com.atsuishio.superbwarfare.entity.OBBEntity;
import com.atsuishio.superbwarfare.entity.mixin.OBBHitter;
import com.atsuishio.superbwarfare.entity.vehicle.BasicGeoVehicleEntity;
import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.MortarEntity;
import com.atsuishio.superbwarfare.entity.vehicle.Tom6Entity;
import com.atsuishio.superbwarfare.entity.vehicle.VehicleModelEntry;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleClientUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleDestroyUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleEffectUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleEngineUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleLootUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleMiscUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleMotionUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleVecUtils;
import com.atsuishio.superbwarfare.entity.vehicle.utils.VehicleWeaponUtils;
import com.atsuishio.superbwarfare.event.ClientMouseHandler;
import com.atsuishio.superbwarfare.event.GunEventHandler;
import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModSerializers;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.inventory.handler.VehicleContainerHandler;
import com.atsuishio.superbwarfare.inventory.menu.HugeVehicleContainerMenu;
import com.atsuishio.superbwarfare.inventory.menu.LargeVehicleContainerMenu;
import com.atsuishio.superbwarfare.inventory.menu.MediumVehicleContainerMenu;
import com.atsuishio.superbwarfare.inventory.menu.MiniVehicleContainerMenu;
import com.atsuishio.superbwarfare.inventory.menu.SmallVehicleContainerMenu;
import com.atsuishio.superbwarfare.item.IVehicleInteract;
import com.atsuishio.superbwarfare.item.container.ContainerBlockItem;
import com.atsuishio.superbwarfare.item.misc.VehicleKeyItem;
import com.atsuishio.superbwarfare.mixins.EntityOnGroundAccessor;
import com.atsuishio.superbwarfare.network.message.receive.ClientIndicatorMessage;
import com.atsuishio.superbwarfare.network.message.receive.ClientVehicleItemMessage;
import com.atsuishio.superbwarfare.network.message.receive.EntityRelationSyncMessage;
import com.atsuishio.superbwarfare.network.message.receive.VehicleShootClientMessage;
import com.atsuishio.superbwarfare.resource.model.VehicleLODModelReloadListener;
import com.atsuishio.superbwarfare.resource.model.VehicleModelReloadListener;
import com.atsuishio.superbwarfare.resource.vehicle.VehicleModelPojo;
import com.atsuishio.superbwarfare.resource.vehicle.VehicleResource;
import com.atsuishio.superbwarfare.tools.BvrSyncExclusion;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.atsuishio.superbwarfare.tools.DamageTypeTool;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.FormatTool;
import com.atsuishio.superbwarfare.tools.InventoryTool;
import com.atsuishio.superbwarfare.tools.JsonUtil;
import com.atsuishio.superbwarfare.tools.MinecraftUtil;
import com.atsuishio.superbwarfare.tools.OBB;
import com.atsuishio.superbwarfare.tools.ProjectileCalculator;
import com.atsuishio.superbwarfare.tools.RadarScanner;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.ServerSyncedEntityHandler;
import com.atsuishio.superbwarfare.tools.SoundTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.atsuishio.superbwarfare.tools.VectorToolKt;
import com.atsuishio.superbwarfare.world.saveddata.TDMSavedData;
import com.github.mcmodderanchor.simplebedrockmodel.v2.common.model.baked.BakedBedrockModel;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.math.Axis;
import java.lang.invoke.LambdaMetafactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import kotlinx.serialization.DeserializationStrategy;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Position;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.Vector4d;

/*
 * Duplicate member names - consider using --renamedupmembers true
 */
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u00c7\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0013\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u0007\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b7\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010%\n\u0002\bn\n\u0002\u0018\u0002\n\u0002\b5\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\n\n\u0002\b+\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u001d\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b=\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b+\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0003\b\u00d7\u0001\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0016\u0018\u0000 \u00f1\b2\u00020\u00012\u00020\u00022\u00020\u00032\u00020\u00042\u00020\u00052\u00020\u00062\u00020\u0007:\u0002\u00f1\bB\u001b\u0012\n\u0010\b\u001a\u0006\u0012\u0002\b\u00030\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u0012\u001a\n\u0012\u0004\u0012\u00020\u0000\u0018\u00010\u000fH\u0016J\u000e\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00150\u0014H\u0016J\u0012\u0010/\u001a\u0004\u0018\u0001002\u0006\u00101\u001a\u00020\u001eH\u0016J\u0014\u0010/\u001a\u0004\u0018\u0001002\b\u00102\u001a\u0004\u0018\u00010\u0001H\u0016J\u0012\u00103\u001a\u0004\u0018\u00010%2\u0006\u00101\u001a\u00020\u001eH\u0016J\u001a\u00103\u001a\u0004\u0018\u00010%2\u0006\u00101\u001a\u00020\u001e2\u0006\u00104\u001a\u00020\u001eH\u0016J\u001c\u00103\u001a\u0004\u0018\u00010%2\b\u00102\u001a\u0004\u0018\u00010\u00012\u0006\u00104\u001a\u00020\u001eH\u0016J\u0014\u00103\u001a\u0004\u0018\u00010%2\b\u00102\u001a\u0004\u0018\u00010\u0001H\u0016J\u0012\u00103\u001a\u0004\u0018\u00010%2\u0006\u00105\u001a\u00020$H\u0016J\u0012\u00106\u001a\u0004\u0018\u00010$2\u0006\u00101\u001a\u00020\u001eH\u0016J\u001a\u00106\u001a\u0004\u0018\u00010$2\u0006\u00101\u001a\u00020\u001e2\u0006\u00104\u001a\u00020\u001eH\u0016J&\u00107\u001a\u0002082\u0006\u00101\u001a\u00020\u001e2\u0006\u00104\u001a\u00020\u001e2\f\u00109\u001a\b\u0012\u0004\u0012\u00020%0:H\u0016J\u001e\u00107\u001a\u0002082\u0006\u00101\u001a\u00020\u001e2\f\u00109\u001a\b\u0012\u0004\u0012\u00020%0:H\u0016J \u00107\u001a\u0002082\b\u00105\u001a\u0004\u0018\u00010$2\f\u00109\u001a\b\u0012\u0004\u0012\u00020%0:H\u0016J\t\u0010\u00f5\u0002\u001a\u000208H\u0002J\u0017\u0010\u00f6\u0002\u001a\u0002082\f\u0010\u00f7\u0002\u001a\u0007\u0012\u0002\b\u00030\u00f8\u0002H\u0016J\u0013\u0010\u00f9\u0002\u001a\u0002082\b\u0010\u00fa\u0002\u001a\u00030\u00fb\u0002H\u0016J\u001b\u0010\u0097\u0003\u001a\u0002082\u0007\u0010\u0098\u0003\u001a\u00020\u001c2\u0007\u0010\u0099\u0003\u001a\u00020\u001cH\u0016J\u0011\u0010\u00ae\u0003\u001a\n\u0012\u0005\u0012\u00030\u00b0\u00030\u00af\u0003H\u0016J\t\u0010\u00b1\u0003\u001a\u000208H\u0004J\t\u0010\u00b2\u0003\u001a\u00020\u001eH\u0016J\u0011\u0010\u00b3\u0003\u001a\u00030\u00b0\u00032\u0007\u0010\u00b4\u0003\u001a\u00020\u001eJ\u001a\u0010\u00b5\u0003\u001a\u00030\u00b0\u00032\u0007\u0010\u00b4\u0003\u001a\u00020\u001e2\u0007\u0010\u00b6\u0003\u001a\u00020\u001eJ\u001a\u0010\u00ba\u0003\u001a\u0002082\u0007\u0010\u00b4\u0003\u001a\u00020\u001e2\b\u0010\u00bb\u0003\u001a\u00030\u00b0\u0003J\t\u0010\u00bc\u0003\u001a\u000208H\u0016J\u0007\u0010\u00bd\u0003\u001a\u000208J\t\u0010\u00be\u0003\u001a\u00020 H\u0016J\u001c\u0010\u00bf\u0003\u001a\u00020 2\u0007\u0010\u00b4\u0003\u001a\u00020\u001e2\b\u0010\u00c0\u0003\u001a\u00030\u00b0\u0003H\u0016J\u0010\u0010\u00c1\u0003\u001a\u00020 2\u0007\u0010\u00b4\u0003\u001a\u00020\u001eJ\u0013\u0010\u00c2\u0003\u001a\u0002082\b\u0010\u00c3\u0003\u001a\u00030\u00c4\u0003H\u0016J\t\u0010\u00c5\u0003\u001a\u000208H\u0016J\u0013\u0010\u00c6\u0003\u001a\u0002082\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u0001H\u0016J\t\u0010\u00c8\u0003\u001a\u00020 H\u0016J\u0013\u0010\u00c9\u0003\u001a\u0002082\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u0001H\u0016J)\u0010\u00ca\u0003\u001a\u0005\u0018\u00010\u00cb\u00032\u0007\u0010\u00cc\u0003\u001a\u00020\u001e2\b\u0010\u00cd\u0003\u001a\u00030\u00ce\u00032\b\u0010\u00cf\u0003\u001a\u00030\u00b1\u0001H\u0016J\u0011\u0010\u00d1\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010<H\u0002J\u0012\u0010\u00d2\u0003\u001a\u0002082\u0007\u0010\u00d3\u0003\u001a\u00020\u001eH\u0004JO\u0010\u00d4\u0003\u001a\u000208\"\u0005\b\u0000\u0010\u00d5\u00032\u0010\u0010\u00d6\u0003\u001a\u000b\u0012\u0007\u0012\u0005\u0018\u0001H\u00d5\u00030<2\u0007\u0010\u00d3\u0003\u001a\u00020\u001e2\n\u0010\u00d7\u0003\u001a\u0005\u0018\u0001H\u00d5\u00032\u0010\u0010\u00d8\u0003\u001a\u000b\u0012\u0005\u0012\u0003H\u00d5\u0003\u0018\u00010:H\u0004\u00a2\u0006\u0003\u0010\u00d9\u0003J\t\u0010\u00da\u0003\u001a\u000208H\u0004J\u0011\u0010\u00db\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010<H\u0016J\u0012\u0010\u00e2\u0003\u001a\u0002082\u0007\u0010\u00e3\u0003\u001a\u00020\u0001H\u0014J\u0012\u0010\u00e4\u0003\u001a\u0002082\u0007\u0010\u00e3\u0003\u001a\u00020\u0001H\u0014J\t\u0010\u00e5\u0003\u001a\u00020FH\u0016J\n\u0010\u00e6\u0003\u001a\u00030\u00e7\u0003H\u0016J\t\u0010\u00e8\u0003\u001a\u00020eH\u0016J\u000b\u0010\u00e9\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u0014\u0010\u00ea\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u001b\u0010\u00ec\u0003\u001a\u00020 2\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u0014\u0010\u00ee\u0003\u001a\u00020\u001e2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u0012\u0010\u00ef\u0003\u001a\u00020\u001e2\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\u0011\u0010f\u001a\u00020e2\u0007\u0010\u00f2\u0003\u001a\u00020eH\u0016J\u0012\u0010\u00f3\u0003\u001a\u00020e2\u0007\u0010\u00f2\u0003\u001a\u00020eH\u0016J\u0012\u0010\u00f4\u0003\u001a\u00020e2\u0007\u0010\u00f2\u0003\u001a\u00020eH\u0016J\u0012\u0010\u00f5\u0003\u001a\u0002082\u0007\u0010\u00f6\u0003\u001a\u00020eH\u0016J$\u0010\u00f7\u0003\u001a\u0002082\u0007\u0010\u00f8\u0003\u001a\u00020e2\u0007\u0010\u00f9\u0003\u001a\u00020e2\u0007\u0010\u00fa\u0003\u001a\u00020eH\u0016J\t\u0010\u00fb\u0003\u001a\u00020 H\u0016J\t\u0010\u00fc\u0003\u001a\u00020 H\u0016J\t\u0010\u008b\u0004\u001a\u000208H\u0014J\u0012\u0010\u008c\u0004\u001a\u0002082\u0007\u0010\u008d\u0004\u001a\u00020\u001eH\u0016J\u0012\u0010\u008e\u0004\u001a\u00020 2\u0007\u0010\u008d\u0004\u001a\u00020\u001eH\u0004J\f\u0010\u00ff\u0003\u001a\u0005\u0018\u00010\u0084\u0004H\u0016J\t\u0010\u0095\u0004\u001a\u00020 H\u0016J\u0015\u0010\u0096\u0004\u001a\u00020 2\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u0004H\u0016J\u0015\u0010\u0099\u0004\u001a\u00020\u001e2\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u0004H\u0016J\u0011\u0010\u0099\u0004\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u0099\u0004\u001a\u00020\u001e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0015\u0010\u009b\u0004\u001a\u00020\u001e2\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u0004H\u0016J\u0011\u0010\u009b\u0004\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u009b\u0004\u001a\u00020\u001e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0019\u0010\u009b\u0004\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001e2\u0006\u00104\u001a\u00020\u001eH\u0016J\u0012\u0010\u009c\u0004\u001a\u00020\u001e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0019\u0010\u009c\u0004\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001e2\u0006\u00104\u001a\u00020\u001eH\u0016J*\u0010\u009d\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\u0007\u0010\u009a\u0004\u001a\u00020$2\n\u0010\u009e\u0004\u001a\u0005\u0018\u00010\u00b4\u0002H\u0016J6\u0010\u009d\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\u0007\u0010\u009a\u0004\u001a\u00020$2\n\u0010\u009f\u0004\u001a\u0005\u0018\u00010\u00a0\u00042\n\u0010\u009e\u0004\u001a\u0005\u0018\u00010\u00b4\u0002H\u0016J-\u0010\u009d\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\n\u0010\u009f\u0004\u001a\u0005\u0018\u00010\u00a0\u00042\n\u0010\u009e\u0004\u001a\u0005\u0018\u00010\u00b4\u0002H\u0016J\u001e\u0010\u00a1\u0004\u001a\u0002082\t\u0010\u00a2\u0004\u001a\u0004\u0018\u00010%2\b\u0010\u00a3\u0004\u001a\u00030\u00b4\u0002H\u0016J\u001e\u0010\u00a4\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u001d\u0010\u00a4\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\u0006\u00101\u001a\u00020\u001eH\u0016J,\u0010\u00a4\u0004\u001a\u0002082\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u00042\t\u0010\u00a2\u0004\u001a\u0004\u0018\u00010%2\n\u0010\u00a5\u0004\u001a\u0005\u0018\u00010\u00b4\u0002H\u0016J\u0011\u0010\u00a6\u0004\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001eH\u0016J\t\u0010\u00a7\u0004\u001a\u00020 H\u0016J\u0011\u0010\u00a7\u0004\u001a\u00020 2\u0006\u00101\u001a\u00020\u001eH\u0016J\u001a\u0010\u00a8\u0004\u001a\u0002082\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00a9\u0004\u001a\u00020\u001eH\u0016J\"\u0010\u00aa\u0004\u001a\u0002082\u0006\u00101\u001a\u00020\u001e2\u0006\u0010)\u001a\u00020\u001e2\u0007\u0010\u00ab\u0004\u001a\u00020 H\u0016J\u0011\u0010\u00ac\u0004\u001a\n\u0012\u0005\u0012\u00030\u00ae\u00040\u00ad\u0004H\u0016J\u0013\u0010\u00af\u0004\u001a\u0002082\b\u0010\u00b0\u0004\u001a\u00030\u00b1\u0004H\u0016J\u0013\u0010\u00b2\u0004\u001a\u0002082\b\u0010\u00b3\u0004\u001a\u00030\u00b1\u0004H\u0016J\u0013\u0010\u00b4\u0004\u001a\u0002082\b\u0010\u00b5\u0004\u001a\u00030\u00b6\u0004H\u0014J\u0013\u0010\u00b7\u0004\u001a\u0002082\b\u0010\u00b5\u0004\u001a\u00030\u00b6\u0004H\u0016J\u0013\u0010\u00b8\u0004\u001a\u0002082\b\u0010\u00b9\u0004\u001a\u00030\u00b6\u0004H\u0016J\u001e\u0010\u00ba\u0004\u001a\u00030\u00bb\u00042\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u00012\b\u0010\u00bc\u0004\u001a\u00030\u00bd\u0004H\u0016J*\u0010\u00be\u0004\u001a\u0005\u0018\u00010\u00bb\u00042\b\u0010\u00c0\u0003\u001a\u00030\u00b0\u00032\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u00012\b\u0010\u00bc\u0004\u001a\u00030\u00bd\u0004H\u0016J\u0013\u0010\u00c2\u0004\u001a\u0002082\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u0001H\u0017J\u001c\u0010\u00c3\u0004\u001a\u00020 2\b\u0010\u00c4\u0004\u001a\u00030\u00ea\u00022\u0007\u0010\u008d\u0004\u001a\u00020eH\u0016J\n\u0010\u00c5\u0004\u001a\u00030\u00c6\u0004H\u0016J\u001c\u0010\u00c7\u0004\u001a\u00020e2\b\u0010\u00c4\u0004\u001a\u00030\u00ea\u00022\u0007\u0010\u00c8\u0004\u001a\u00020eH\u0016J\u0012\u0010\u00c9\u0004\u001a\u0002082\u0007\u0010\u00ca\u0004\u001a\u00020eH\u0016J&\u0010\u00cb\u0004\u001a\u0002082\u0007\u0010\u00ca\u0004\u001a\u00020e2\t\u0010\u00cc\u0004\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00cd\u0004\u001a\u00020 H\u0016J\t\u0010\u00ce\u0004\u001a\u00020 H\u0014J \u0010\u00cf\u0004\u001a\u00020 2\u0015\u0010\u00d0\u0004\u001a\u0010\u0012\u0005\u0012\u00030\u00d2\u0004\u0012\u0004\u0012\u00020\u001c0\u00d1\u0004H\u0016J\t\u0010\u00d3\u0004\u001a\u00020 H\u0016J\t\u0010\u00d7\u0004\u001a\u00020eH\u0016J\t\u0010\u00d8\u0004\u001a\u00020\u001eH\u0016J\t\u0010\u00d9\u0004\u001a\u00020eH\u0016J\t\u0010\u00da\u0004\u001a\u00020eH\u0016J\t\u0010\u00db\u0004\u001a\u00020eH\u0016J\t\u0010\u00dc\u0004\u001a\u00020eH\u0016J\t\u0010\u00dd\u0004\u001a\u00020eH\u0016J\t\u0010\u00de\u0004\u001a\u00020eH\u0017J\t\u0010\u00df\u0004\u001a\u00020eH\u0017J\t\u0010\u00e0\u0004\u001a\u000208H\u0016J\u001d\u0010\u00e1\u0004\u001a\u0002082\b\u0010\u00e2\u0004\u001a\u00030\u00e3\u00042\b\u0010\u00e4\u0004\u001a\u00030\u00b4\u0002H\u0016J\u001d\u0010\u00e5\u0004\u001a\u0002082\b\u0010\u00e6\u0004\u001a\u00030\u00e7\u00042\b\u0010\u00e2\u0004\u001a\u00030\u00e3\u0004H\u0015J\t\u0010\u00e8\u0004\u001a\u00020 H\u0016J\t\u0010\u00e9\u0004\u001a\u00020 H\u0016J\u0012\u0010\u00ea\u0004\u001a\u00020 2\u0007\u0010\u00cc\u0004\u001a\u00020\u0001H\u0016J\u0012\u0010\u00eb\u0004\u001a\u00020 2\u0007\u0010\u00e3\u0003\u001a\u00020\u0001H\u0014J\t\u0010\u00ee\u0004\u001a\u00020\u001eH\u0016J\t\u0010\u00ef\u0004\u001a\u00020eH\u0016J\t\u0010\u00f0\u0004\u001a\u000208H\u0016J\u0007\u0010\u00f1\u0004\u001a\u00020 J\t\u0010\u00f2\u0004\u001a\u000208H\u0016J\t\u0010\u00f3\u0004\u001a\u000208H\u0016J\u0013\u0010\u00f4\u0004\u001a\u0002082\b\u0010\u00f5\u0004\u001a\u00030\u00f6\u0004H\u0002J\u0013\u0010\u00f4\u0004\u001a\u0002082\b\u0010\u00f7\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0007\u0010\u00f8\u0004\u001a\u000208J\u001d\u0010\u00f9\u0004\u001a\u00030\u00b4\u00022\u0007\u0010\u00fa\u0004\u001a\u00020e2\n\u0010\u00fb\u0004\u001a\u0005\u0018\u00010\u00fc\u0004J\t\u0010\u00fd\u0004\u001a\u00020 H\u0016J\u0007\u0010\u00fe\u0004\u001a\u00020 J\t\u0010\u00ff\u0004\u001a\u000208H\u0016J\u0013\u0010\u0082\u0005\u001a\u00030\u0081\u00052\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0012\u0010\u0084\u0005\u001a\u00020 2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0012\u0010\u0085\u0005\u001a\u00020e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0012\u0010\u0086\u0005\u001a\u00020e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\t\u0010\u0088\u0005\u001a\u00020eH\u0016J\t\u0010\u0089\u0005\u001a\u00020eH\u0016J\t\u0010\u008a\u0005\u001a\u000208H\u0004J\u0012\u0010\u008d\u0005\u001a\u0002082\u0007\u0010\u008e\u0005\u001a\u00020\u0004H\u0016J\t\u0010\u008f\u0005\u001a\u000208H\u0016JA\u0010\u0090\u0005\u001a\u0002082\b\u0010\u0091\u0005\u001a\u00030\u0092\u00052\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u00022\u0007\u0010\u0093\u0005\u001a\u00020e2\u0007\u0010\u0094\u0005\u001a\u00020\u000b2\u0007\u0010\u0095\u0005\u001a\u00020e2\u0007\u0010\u0096\u0005\u001a\u00020\u001eH\u0016JB\u0010\u0090\u0005\u001a\u0002082\b\u0010\u0091\u0005\u001a\u00030\u0092\u00052\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u00022\u0007\u0010\u0093\u0005\u001a\u00020e2\u0007\u0010\u0094\u0005\u001a\u00020\u000b2\u0007\u0010\u0096\u0005\u001a\u00020\u001e2\b\u0010\u0097\u0005\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u0098\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u0099\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u009a\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u009b\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u009c\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u009d\u0005\u001a\u0002082\b\u0010\u00a5\u0004\u001a\u00030\u00b4\u0002H\u0016J\t\u0010\u009e\u0005\u001a\u000208H\u0016J\t\u0010\u009f\u0005\u001a\u000208H\u0016J\f\u0010\u00a0\u0005\u001a\u0005\u0018\u00010\u00b4\u0002H\u0016J\t\u0010\u00a1\u0005\u001a\u000208H\u0016J\t\u0010\u00a2\u0005\u001a\u000208H\u0016J\u0011\u0010\u00a3\u0005\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0013\u0010\u00a4\u0005\u001a\u0002082\b\u0010\u00a3\u0004\u001a\u00030\u00b4\u0002H\u0016J\u001c\u0010\u00a5\u0005\u001a\u0002082\u0007\u0010\u009f\u0004\u001a\u00020$2\b\u0010\u00a6\u0005\u001a\u00030\u0098\u0004H\u0016J\u0012\u0010\u00a7\u0005\u001a\u0002082\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\u0012\u0010\u00ac\u0005\u001a\u0002082\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0002J\u0012\u0010\u00ad\u0005\u001a\u0002082\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\u001c\u0010\u00ae\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00af\u0005\u001a\u00020e2\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\u001c\u0010\u00b0\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00af\u0005\u001a\u00020e2\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J%\u0010\u00b1\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00af\u0005\u001a\u00020e2\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00b2\u0005\u001a\u00020$H\u0016J\u001b\u0010\u00b3\u0005\u001a\u0002082\u0006\u00102\u001a\u00020\u00012\b\u0010\u00b4\u0005\u001a\u00030\u00b5\u0005H\u0016J0\u0010\u00b6\u0005\u001a\u0002082\u0006\u00102\u001a\u00020\u00012\b\u0010\u00b4\u0005\u001a\u00030\u00b5\u00052\b\u0010\u0097\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$H\u0016J\t\u0010\u00c5\u0005\u001a\u000208H\u0004J\u0015\u0010\u00c6\u0005\u001a\u00030\u00b9\u00052\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$H\u0016J\u001e\u0010\u00c6\u0005\u001a\u00030\u00b9\u00052\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u0015\u0010\u00c7\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$H\u0016J\u001e\u0010\u00c7\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J$\u0010\u00c7\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00b2\u0005\u001a\u00020$2\u0007\u0010\u00af\u0005\u001a\u00020e2\u0006\u00101\u001a\u00020\u001eH\u0016J\n\u0010\u00c8\u0005\u001a\u00030\u00b4\u0002H\u0016J\u0015\u0010\u00c9\u0005\u001a\u00030\u00c2\u00052\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$H\u0016J\u001e\u0010\u00c9\u0005\u001a\u00030\u00c2\u00052\t\u0010\u00b2\u0005\u001a\u0004\u0018\u00010$2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u001b\u0010\u00ca\u0005\u001a\u00030\u00b4\u00022\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u0015\u0010\u00cb\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u001e\u0010\u00ca\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u001c\u0010\u00ca\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u009a\u0004\u001a\u00020$2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u001e\u0010\u00cc\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u001c\u0010\u00cd\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001d\u0010\u00ce\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J \u0010\u00ce\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001e\u0010\u00ce\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\u0007\u0010\u009a\u0004\u001a\u00020$2\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001d\u0010\u00cf\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u001e\u0010\u00cf\u0005\u001a\u00030\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001c\u0010\u00cf\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u009a\u0004\u001a\u00020$2\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001c\u0010\u00d0\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001e\u0010\u00d1\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J \u0010\u00d2\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001d\u0010\u00d2\u0005\u001a\u0005\u0018\u00010\u00b4\u00022\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J&\u0010\u00d3\u0005\u001a\u0004\u0018\u00010\u00012\u0007\u0010\u00d4\u0005\u001a\u00020\u00012\u0007\u0010\u00d5\u0005\u001a\u00020\u001c2\u0007\u0010\u00d6\u0005\u001a\u00020eH\u0016J\u0014\u0010\u00d7\u0005\u001a\u00020e2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u0011\u0010\u00d7\u0005\u001a\u00020e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u00d7\u0005\u001a\u00020e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0014\u0010\u00d7\u0005\u001a\u00020e2\t\u0010\u00a2\u0004\u001a\u0004\u0018\u00010%H\u0016J\u0014\u0010\u00d8\u0005\u001a\u00020e2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u0011\u0010\u00d8\u0005\u001a\u00020e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u00d8\u0005\u001a\u00020e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0014\u0010\u00d8\u0005\u001a\u00020e2\t\u0010\u00a2\u0004\u001a\u0004\u0018\u00010%H\u0016J\u0014\u0010\u00d9\u0005\u001a\u00020e2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\u0011\u0010\u00d9\u0005\u001a\u00020e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u00d9\u0005\u001a\u00020e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0014\u0010\u00d9\u0005\u001a\u00020e2\t\u0010\u00a2\u0004\u001a\u0004\u0018\u00010%H\u0016J\u001e\u0010\u00da\u0005\u001a\u0002082\t\u0010\u009f\u0004\u001a\u0004\u0018\u00010$2\b\u0010\u00a6\u0005\u001a\u00030\u0098\u0004H\u0016J\u0013\u0010\u00db\u0005\u001a\u0002082\b\u0010\u00a3\u0004\u001a\u00030\u00b4\u0002H\u0016J\t\u0010\u00dc\u0005\u001a\u000208H\u0016J\t\u0010\u00dd\u0005\u001a\u000208H\u0016J\u0013\u0010\u00de\u0005\u001a\u0002082\b\u0010\u00df\u0005\u001a\u00030\u00e0\u0005H\u0016J\n\u0010\u00e1\u0005\u001a\u00030\u00e2\u0005H\u0016J\t\u0010\u00e3\u0005\u001a\u000208H\u0016J\t\u0010\u00e4\u0005\u001a\u000208H\u0016J\t\u0010\u00e5\u0005\u001a\u000208H\u0016J\t\u0010\u00e6\u0005\u001a\u00020eH\u0016J\u0013\u0010\u00e7\u0005\u001a\u00030\u00b9\u00052\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u0013\u0010\u00e8\u0005\u001a\u00030\u00b9\u00052\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u0013\u0010\u00e9\u0005\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u00ee\u0005\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u00ef\u0005\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\t\u0010\u00f0\u0005\u001a\u00020 H\u0016J\t\u0010\u0083\u0006\u001a\u00020 H\u0016J\u0013\u0010\u0098\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u0099\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u009a\u0006\u001a\u00020eH\u0016J\u0013\u0010\u009b\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u009c\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u009d\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u0013\u0010\u009e\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J/\u0010\u009f\u0006\u001a\u00030\u00a0\u00062\b\u0010\u00a1\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u0098\u0003\u001a\u00020\u001c2\u0007\u0010\u0099\u0003\u001a\u00020\u001c2\u0007\u0010\u00a2\u0006\u001a\u00020\u001cH\u0016J\t\u0010\u00a3\u0006\u001a\u000208H\u0016JG\u0010\u00a4\u0006\u001a\u0002082\u0007\u0010\u0098\u0003\u001a\u00020\u001c2\u0007\u0010\u0099\u0003\u001a\u00020\u001c2\u0007\u0010\u00a2\u0006\u001a\u00020\u001c2\u0007\u0010\u00a5\u0006\u001a\u00020e2\u0007\u0010\u00fa\u0003\u001a\u00020e2\u0006\u0010T\u001a\u00020\u001e2\u0007\u0010\u00a6\u0006\u001a\u00020 H\u0016J\u001c\u0010\u00a7\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00a8\u0006\u001a\u00020\u001c2\u0007\u0010\u00a9\u0006\u001a\u00020\u001cH\u0005J\u0013\u0010\u00aa\u0006\u001a\u00030\u00b4\u00022\u0007\u00102\u001a\u00030\u0098\u0004H\u0016J\u001c\u0010\u00ab\u0006\u001a\u00030\u00b4\u00022\u0007\u00102\u001a\u00030\u0098\u00042\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u0013\u0010\u00ac\u0006\u001a\u00030\u00b4\u00022\u0007\u00102\u001a\u00030\u0098\u0004H\u0016J\u001c\u0010\u00ad\u0006\u001a\u00030\u00b4\u00022\u0007\u00102\u001a\u00030\u0098\u00042\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u0011\u0010\u00ae\u0006\u001a\u00020 2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u00af\u0006\u001a\u0002082\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\u001f\u0010\u00b0\u0006\u001a\u00030\u00b4\u00022\n\u0010\u00ed\u0003\u001a\u0005\u0018\u00010\u0098\u00042\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\t\u0010\u00b5\u0006\u001a\u00020 H\u0016J\u0013\u0010\u00b6\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J\u0013\u0010\u00b7\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00af\u0005\u001a\u00020eH\u0016J$\u0010\u00b8\u0006\u001a\u0002082\u0007\u0010\u00b9\u0006\u001a\u00020\u001c2\u0007\u0010\u00ba\u0006\u001a\u00020\u001c2\u0007\u0010\u00bb\u0006\u001a\u00020\u001cH\u0016J\u0013\u0010\u00bc\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u009a\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00bd\u0006\u001a\u00020e2\u0007\u0010\u009a\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00be\u0006\u001a\u00020e2\u0007\u0010\u00bf\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00a5\u0001\u001a\u00020e2\u0007\u0010\u009a\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00a2\u0001\u001a\u00020e2\u0007\u0010\u00bf\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00c0\u0006\u001a\u00020e2\u0007\u0010\u00bf\u0006\u001a\u00020eH\u0016J\u0012\u0010\u00c1\u0006\u001a\u00020e2\u0007\u0010\u00bf\u0006\u001a\u00020eH\u0016J\u001c\u0010\u00c2\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001c\u0010\u00c8\u0005\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001c\u0010\u00c3\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\u001c\u0010\u00c4\u0006\u001a\u00030\u00b4\u00022\u0007\u0010\u00ed\u0003\u001a\u00020\u00012\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\t\u0010\u00c5\u0006\u001a\u00020 H\u0016J\u0013\u0010\u00cc\u0006\u001a\u0002082\b\u0010\u00cd\u0006\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u00ce\u0006\u001a\u0002082\b\u0010\u00cf\u0006\u001a\u00030\u00b4\u0002H\u0016J,\u0010\u00d0\u0006\u001a\u00020\u001c2\u0007\u0010\u00d1\u0006\u001a\u00020\u001c2\u0007\u0010\u00d2\u0006\u001a\u00020 2\u0006\u00101\u001a\u00020\u001e2\u0007\u0010\u00d3\u0006\u001a\u00020 H\u0016J\u0012\u0010\u00d6\u0006\u001a\u00020 2\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u0013\u0010\u00d6\u0006\u001a\u00020 2\b\u00102\u001a\u0004\u0018\u00010\u0001H\u0016J\u0015\u0010\u00d7\u0006\u001a\u00020 2\n\u0010\u00ed\u0003\u001a\u0005\u0018\u00010\u0098\u0004H\u0016J\u0012\u0010\u00d8\u0006\u001a\u00020 2\u0007\u0010\u00eb\u0003\u001a\u00020\u001eH\u0016J\u0013\u0010\u00d8\u0006\u001a\u00020 2\b\u00102\u001a\u0004\u0018\u00010\u0001H\u0016J\u0015\u0010\u00d9\u0006\u001a\u00020\u001e2\n\u0010\u0097\u0004\u001a\u0005\u0018\u00010\u0098\u0004H\u0016J\u0011\u0010\u00d9\u0006\u001a\u00020\u001e2\u0006\u00101\u001a\u00020\u001eH\u0016J\u0012\u0010\u00d9\u0006\u001a\u00020\u001e2\u0007\u0010\u009a\u0004\u001a\u00020$H\u0016J\u0012\u0010\u00da\u0006\u001a\u00020\u001e2\u0007\u0010\u00e5\u0003\u001a\u00020%H\u0016J\f\u0010\u00db\u0006\u001a\u0005\u0018\u00010\u00b0\u0003H\u0016J\u0011\u0010\u00dc\u0006\u001a\u00020 2\u0006\u00101\u001a\u00020\u001eH\u0016J1\u0010\u00dd\u0006\u001a\u0005\u0018\u00010\u00de\u00062\u0007\u0010\u00fa\u0004\u001a\u00020e2\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u00012\u0007\u0010\u00d2\u0006\u001a\u00020 2\u0007\u0010\u00df\u0006\u001a\u00020 H\u0017J1\u0010\u00e0\u0006\u001a\u0005\u0018\u00010\u00b4\u00022\u0007\u0010\u00fa\u0004\u001a\u00020e2\b\u0010\u00c7\u0003\u001a\u00030\u00b1\u00012\u0007\u0010\u00d2\u0006\u001a\u00020 2\u0007\u0010\u00df\u0006\u001a\u00020 H\u0017J\u0014\u0010\u00e1\u0006\u001a\u00020 2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0017J9\u0010\u00e2\u0006\u001a\f\u0012\u0007\u0012\u0005\u0018\u0001H\u00d5\u00030\u00ab\u0003\"\u0005\b\u0000\u0010\u00d5\u00032\u0011\u0010\u00e3\u0006\u001a\f\u0012\u0007\u0012\u0005\u0018\u0001H\u00d5\u00030\u00e4\u00062\n\u0010\u00e5\u0006\u001a\u0005\u0018\u00010\u00e6\u0006H\u0016J-\u0010\u00e2\u0006\u001a\f\u0012\u0007\u0012\u0005\u0018\u0001H\u00d5\u00030\u00ab\u0003\"\u0005\b\u0000\u0010\u00d5\u00032\u0011\u0010\u00e3\u0006\u001a\f\u0012\u0007\u0012\u0005\u0018\u0001H\u00d5\u00030\u00e4\u0006H\u0016J\t\u0010\u00e7\u0006\u001a\u000208H\u0016J\t\u0010\u00e8\u0006\u001a\u000208H\u0016J\u0014\u0010\u00e9\u0006\u001a\u00020\u001c2\t\u0010\u00ed\u0003\u001a\u0004\u0018\u00010\u0001H\u0016J\t\u0010\u00ea\u0006\u001a\u00020 H\u0016J\t\u0010\u00eb\u0006\u001a\u000208H\u0016J\u0013\u0010\u00ec\u0006\u001a\u0002082\b\u0010\u0097\u0005\u001a\u00030\u00b4\u0002H\u0016J\t\u0010\u00ed\u0006\u001a\u000208H\u0016J\t\u0010\u00ee\u0006\u001a\u00020\u001eH\u0016J\u0019\u0010\u00ef\u0006\u001a\u0002082\u000e\u0010\u00f0\u0006\u001a\t\u0012\u0005\u0012\u00030\u00b4\u00020<H\u0016J\u0013\u0010\u00f1\u0006\u001a\u00030\u00b9\u00052\u0007\u0010\u00fa\u0004\u001a\u00020eH\u0016J\t\u0010\u00f2\u0006\u001a\u000208H\u0016J\t\u0010\u00f3\u0006\u001a\u000208H\u0016J\u0014\u0010\u00f6\u0006\u001a\u00030\u00b4\u00022\b\u0010\u00f7\u0006\u001a\u00030\u00b4\u0002H\u0016J\u001d\u0010\u00f8\u0006\u001a\u0002082\b\u0010\u00f9\u0006\u001a\u00030\u00fa\u00062\b\u0010\u00e6\u0004\u001a\u00030\u00b4\u0002H\u0016J\u001d\u0010\u00fb\u0006\u001a\u0002082\b\u0010\u00fc\u0006\u001a\u00030\u00fa\u00062\b\u0010\u00fd\u0006\u001a\u00030\u00b4\u0002H\u0016J\u001c\u0010\u00fe\u0006\u001a\u0002082\u0007\u0010\u00ff\u0006\u001a\u00020 2\b\u0010\u00fd\u0006\u001a\u00030\u00b4\u0002H\u0016J\u0013\u0010\u0080\u0007\u001a\u0002082\b\u0010\u0081\u0007\u001a\u00030\u00e6\u0006H\u0016J\u0013\u0010\u0082\u0007\u001a\u0002082\b\u0010\u0081\u0007\u001a\u00030\u00e6\u0006H\u0016J\t\u0010\u0083\u0007\u001a\u000208H\u0016J$\u0010\u0084\u0007\u001a\u0002082\u0007\u0010\u00b9\u0006\u001a\u00020\u001c2\u0007\u0010\u00ba\u0006\u001a\u00020\u001c2\u0007\u0010\u00bb\u0006\u001a\u00020\u001cH\u0016J\t\u0010\u0085\u0007\u001a\u000208H\u0016J\b\u0010\u0086\u0007\u001a\u00030\u0087\u0007J\t\u0010\u0088\u0007\u001a\u000208H\u0016J\n\u0010\u0089\u0007\u001a\u00030\u008a\u0007H\u0016J\n\u0010\u008b\u0007\u001a\u00030\u008a\u0007H\u0016J\f\u0010\u008c\u0007\u001a\u0005\u0018\u00010\u0081\u0005H\u0016J\t\u0010\u008d\u0007\u001a\u00020\u001cH\u0016J\t\u0010\u008e\u0007\u001a\u00020\u001eH\u0016J\t\u0010\u008f\u0007\u001a\u00020 H\u0016J\t\u0010\u0090\u0007\u001a\u00020 H\u0016J\t\u0010\u0091\u0007\u001a\u00020 H\u0016J\u0010\u0010\u0092\u0007\u001a\t\u0012\u0005\u0012\u00030\u00b0\u00030\u0014H\u0016J\u0012\u0010\u00bc\b\u001a\u00020 2\u0007\u0010\u00ed\u0003\u001a\u00020\u0001H\u0016J\t\u0010\u00bd\b\u001a\u00020 H\u0016J\t\u0010\u00db\b\u001a\u000208H\u0016J\t\u0010\u00dc\b\u001a\u00020 H\u0016J\t\u0010\u00dd\b\u001a\u00020 H\u0016J\t\u0010\u00de\b\u001a\u00020 H\u0016J\t\u0010\u00df\b\u001a\u00020 H\u0016J\t\u0010\u00e0\b\u001a\u00020 H\u0016J\u001f\u0010\u00e6\b\u001a\u00030\u00e7\b2\u0007\u0010\u00e5\u0003\u001a\u00020%2\n\u0010\u00c7\u0003\u001a\u0005\u0018\u00010\u00b1\u0001H\u0017J\u001f\u0010\u00e8\b\u001a\u00030\u00e7\b2\u0007\u0010\u00e5\u0003\u001a\u00020%2\n\u0010\u00c7\u0003\u001a\u0005\u0018\u00010\u00b1\u0001H\u0017J\u000f\u0010\u00e9\b\u001a\b\u0012\u0004\u0012\u00020=0<H\u0016J\t\u0010\u00ea\b\u001a\u00020?H\u0016J\t\u0010\u00eb\b\u001a\u000208H\u0016J\t\u0010\u00ec\b\u001a\u000208H\u0016J\u000b\u0010\u00ed\b\u001a\u0004\u0018\u00010=H\u0016J\u000b\u0010\u00ee\b\u001a\u0004\u0018\u00010IH\u0016J\u0010\u0010\u00ef\b\u001a\t\u0012\u0004\u0012\u00020\u001e0\u00f8\u0002H\u0016J\t\u0010\u00f0\b\u001a\u000208H\u0016R\u0019\u0010\u000e\u001a\n\u0012\u0004\u0012\u00020\u0000\u0018\u00010\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R!\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\"\u001a\u0010\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020%\u0018\u00010#X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010&\u001a\n\u0012\u0004\u0012\u00020$\u0018\u00010'X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R<\u0010*\u001a\u000e\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020%0#2\u0012\u0010)\u001a\u000e\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020%0#8V@VX\u0096\u000e\u00a2\u0006\f\u001a\u0004\b+\u0010,\"\u0004\b-\u0010.R\u0016\u0010;\u001a\n\u0012\u0004\u0012\u00020=\u0018\u00010<X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010>\u001a\u0004\u0018\u00010?X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010@\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010A\u001a\u00020B8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010C\u001a\u00020\u001e8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010D\u001a\u00020\u001e8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010E\u001a\u0004\u0018\u00010F8\u0000@\u0000X\u0081\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010G\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010H\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R0\u0010J\u001a\b\u0012\u0004\u0012\u00020I0\u00142\f\u0010)\u001a\b\u0012\u0004\u0012\u00020I0\u0014@TX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bK\u0010\u0017\"\u0004\bL\u0010MR\u001c\u0010N\u001a\u0004\u0018\u00010OX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bP\u0010Q\"\u0004\bR\u0010SR\u001a\u0010T\u001a\u00020\u001eX\u0084\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bU\u0010V\"\u0004\bW\u0010XR\u001a\u0010Y\u001a\u00020\u001cX\u0084\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bZ\u0010[\"\u0004\b\\\u0010]R\u001a\u0010^\u001a\u00020\u001cX\u0084\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b_\u0010[\"\u0004\b`\u0010]R\u001a\u0010a\u001a\u00020\u001cX\u0084\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bb\u0010[\"\u0004\bc\u0010]R\u001a\u0010d\u001a\u00020eX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bf\u0010g\"\u0004\bh\u0010iR\u001a\u0010j\u001a\u00020eX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bk\u0010g\"\u0004\bl\u0010iR\u001a\u0010m\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bn\u0010V\"\u0004\bo\u0010XR\u001a\u0010p\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bq\u0010V\"\u0004\br\u0010XR\u001a\u0010s\u001a\u00020 X\u0096\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\bt\u0010u\"\u0004\bv\u0010wR,\u0010z\u001a\u00020y2\u0006\u0010x\u001a\u00020y8V@VX\u0096\u008e\u0002\u00a2\u0006\u0013\u001a\u0004\b}\u0010~\"\u0005\b\u007f\u0010\u0080\u0001*\u0004\b{\u0010|R/\u0010\u0081\u0001\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0083\u0001\u0010u\"\u0005\b\u0084\u0001\u0010w*\u0005\b\u0082\u0001\u0010|R(\u0010\u0086\u0001\u001a\u00020\u001c2\u0007\u0010\u0085\u0001\u001a\u00020\u001c8F@FX\u0086\u000e\u00a2\u0006\u000e\u001a\u0005\b\u0087\u0001\u0010[\"\u0005\b\u0088\u0001\u0010]R(\u0010\u0089\u0001\u001a\u00020\u001c2\u0007\u0010\u0085\u0001\u001a\u00020\u001c8F@FX\u0086\u000e\u00a2\u0006\u000e\u001a\u0005\b\u008a\u0001\u0010[\"\u0005\b\u008b\u0001\u0010]R(\u0010\u008c\u0001\u001a\u00020\u001c2\u0007\u0010\u0085\u0001\u001a\u00020\u001c8F@FX\u0086\u000e\u00a2\u0006\u000e\u001a\u0005\b\u008d\u0001\u0010[\"\u0005\b\u008e\u0001\u0010]R(\u0010\u008f\u0001\u001a\u00020\u001c2\u0007\u0010\u0085\u0001\u001a\u00020\u001c8F@FX\u0086\u000e\u00a2\u0006\u000e\u001a\u0005\b\u0090\u0001\u0010[\"\u0005\b\u0091\u0001\u0010]R\u001d\u0010\u0092\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0093\u0001\u0010g\"\u0005\b\u0094\u0001\u0010iR\u001d\u0010\u0095\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0096\u0001\u0010g\"\u0005\b\u0097\u0001\u0010iR\u001d\u0010\u0098\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0099\u0001\u0010g\"\u0005\b\u009a\u0001\u0010iR\u001d\u0010\u009b\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009c\u0001\u0010g\"\u0005\b\u009d\u0001\u0010iR\u001d\u0010\u009e\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009f\u0001\u0010g\"\u0005\b\u00a0\u0001\u0010iR\u001d\u0010\u00a1\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a2\u0001\u0010g\"\u0005\b\u00a3\u0001\u0010iR\u001d\u0010\u00a4\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a5\u0001\u0010g\"\u0005\b\u00a6\u0001\u0010iR\u001d\u0010\u00a7\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a8\u0001\u0010g\"\u0005\b\u00a9\u0001\u0010iR\u001d\u0010\u00aa\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00ab\u0001\u0010g\"\u0005\b\u00ac\u0001\u0010iR\u001d\u0010\u00ad\u0001\u001a\u00020\u001eX\u0084\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00ae\u0001\u0010V\"\u0005\b\u00af\u0001\u0010XR\"\u0010\u00b0\u0001\u001a\u0005\u0018\u00010\u00b1\u0001X\u0086\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00b2\u0001\u0010\u00b3\u0001\"\u0006\b\u00b4\u0001\u0010\u00b5\u0001R\u001d\u0010\u00b6\u0001\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00b7\u0001\u0010[\"\u0005\b\u00b8\u0001\u0010]R\u001d\u0010\u00b9\u0001\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00ba\u0001\u0010[\"\u0005\b\u00bb\u0001\u0010]R\u001d\u0010\u00bc\u0001\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00bd\u0001\u0010V\"\u0005\b\u00be\u0001\u0010XR\u000f\u0010\u00bf\u0001\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00c0\u0001\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00c1\u0001\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00c2\u0001\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u00c3\u0001\u001a\u00020 X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u00c4\u0001\u001a\u000f\u0012\u0004\u0012\u00020$\u0012\u0004\u0012\u00020 0\u00c5\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u00c6\u0001\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00c7\u0001\u0010[\"\u0005\b\u00c8\u0001\u0010]R\u001d\u0010\u00c9\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00ca\u0001\u0010g\"\u0005\b\u00cb\u0001\u0010iR\u001d\u0010\u00cc\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00cd\u0001\u0010g\"\u0005\b\u00ce\u0001\u0010iR\u001d\u0010\u00cf\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d0\u0001\u0010g\"\u0005\b\u00d1\u0001\u0010iR\u001d\u0010\u00d2\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d3\u0001\u0010g\"\u0005\b\u00d4\u0001\u0010iR\u001d\u0010\u00d5\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d6\u0001\u0010g\"\u0005\b\u00d7\u0001\u0010iR\u001d\u0010\u00d8\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d9\u0001\u0010g\"\u0005\b\u00da\u0001\u0010iR\u001d\u0010\u00db\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00dc\u0001\u0010g\"\u0005\b\u00dd\u0001\u0010iR\u001d\u0010\u00de\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00df\u0001\u0010g\"\u0005\b\u00e0\u0001\u0010iR\u001d\u0010\u00e1\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e2\u0001\u0010g\"\u0005\b\u00e3\u0001\u0010iR\u001d\u0010\u00e4\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e5\u0001\u0010g\"\u0005\b\u00e6\u0001\u0010iR\u001d\u0010\u00e7\u0001\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e8\u0001\u0010[\"\u0005\b\u00e9\u0001\u0010]R\u001d\u0010\u00ea\u0001\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00eb\u0001\u0010[\"\u0005\b\u00ec\u0001\u0010]R\u001d\u0010\u00ed\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00ee\u0001\u0010g\"\u0005\b\u00ef\u0001\u0010iR\u001d\u0010\u00f0\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00f1\u0001\u0010g\"\u0005\b\u00f2\u0001\u0010iR\u001d\u0010\u00f3\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00f4\u0001\u0010g\"\u0005\b\u00f5\u0001\u0010iR\u001d\u0010\u00f6\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00f7\u0001\u0010g\"\u0005\b\u00f8\u0001\u0010iR\u001d\u0010\u00f9\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00fa\u0001\u0010g\"\u0005\b\u00fb\u0001\u0010iR\u001d\u0010\u00fc\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00fd\u0001\u0010g\"\u0005\b\u00fe\u0001\u0010iR\u001d\u0010\u00ff\u0001\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0080\u0002\u0010g\"\u0005\b\u0081\u0002\u0010iR\u001d\u0010\u0082\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0083\u0002\u0010g\"\u0005\b\u0084\u0002\u0010iR\u001d\u0010\u0085\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0086\u0002\u0010g\"\u0005\b\u0087\u0002\u0010iR\u001d\u0010\u0088\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0089\u0002\u0010g\"\u0005\b\u008a\u0002\u0010iR\u001d\u0010\u008b\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u008c\u0002\u0010g\"\u0005\b\u008d\u0002\u0010iR\u001d\u0010\u008e\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u008f\u0002\u0010g\"\u0005\b\u0090\u0002\u0010iR\u001d\u0010\u0091\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0092\u0002\u0010g\"\u0005\b\u0093\u0002\u0010iR\u001d\u0010\u0094\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0095\u0002\u0010g\"\u0005\b\u0096\u0002\u0010iR\u001d\u0010\u0097\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0098\u0002\u0010g\"\u0005\b\u0099\u0002\u0010iR\u001d\u0010\u009a\u0002\u001a\u00020 X\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009b\u0002\u0010u\"\u0005\b\u009c\u0002\u0010wR\u001d\u0010\u009d\u0002\u001a\u00020 X\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u009e\u0002\u0010u\"\u0005\b\u009f\u0002\u0010wR\u001d\u0010\u00a0\u0002\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a1\u0002\u0010V\"\u0005\b\u00a2\u0002\u0010XR\u001d\u0010\u00a3\u0002\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a4\u0002\u0010V\"\u0005\b\u00a5\u0002\u0010XR\u001d\u0010\u00a6\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00a7\u0002\u0010g\"\u0005\b\u00a8\u0002\u0010iR\u001d\u0010\u00a9\u0002\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00aa\u0002\u0010g\"\u0005\b\u00ab\u0002\u0010iR/\u0010\u00ac\u0002\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00ae\u0002\u0010g\"\u0005\b\u00af\u0002\u0010i*\u0005\b\u00ad\u0002\u0010|R\u001d\u0010\u00b0\u0002\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00b1\u0002\u0010V\"\u0005\b\u00b2\u0002\u0010XR \u0010\u00b3\u0002\u001a\u00030\u00b4\u0002X\u0096\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00b5\u0002\u0010\u00b6\u0002\"\u0006\b\u00b7\u0002\u0010\u00b8\u0002R \u0010\u00b9\u0002\u001a\u00030\u00b4\u0002X\u0096\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00ba\u0002\u0010\u00b6\u0002\"\u0006\b\u00bb\u0002\u0010\u00b8\u0002R\u001d\u0010\u00bc\u0002\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00bd\u0002\u0010[\"\u0005\b\u00be\u0002\u0010]R\u001d\u0010\u00bf\u0002\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00c0\u0002\u0010[\"\u0005\b\u00c1\u0002\u0010]R\u001d\u0010\u00c2\u0002\u001a\u00020\u001cX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00c3\u0002\u0010[\"\u0005\b\u00c4\u0002\u0010]R\u001d\u0010\u00c5\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00c6\u0002\u0010g\"\u0005\b\u00c7\u0002\u0010iR\u001d\u0010\u00c8\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00c9\u0002\u0010g\"\u0005\b\u00ca\u0002\u0010iR\u001d\u0010\u00cb\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00cc\u0002\u0010g\"\u0005\b\u00cd\u0002\u0010iR\u001d\u0010\u00ce\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00cf\u0002\u0010g\"\u0005\b\u00d0\u0002\u0010iR\u001d\u0010\u00d1\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d2\u0002\u0010g\"\u0005\b\u00d3\u0002\u0010iR\u001d\u0010\u00d4\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00d5\u0002\u0010g\"\u0005\b\u00d6\u0002\u0010iR\"\u0010\u00d7\u0002\u001a\u0005\u0018\u00010\u00b4\u0002X\u0086\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00d8\u0002\u0010\u00b6\u0002\"\u0006\b\u00d9\u0002\u0010\u00b8\u0002R\u001d\u0010\u00da\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00db\u0002\u0010g\"\u0005\b\u00dc\u0002\u0010iR\u001d\u0010\u00dd\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00de\u0002\u0010g\"\u0005\b\u00df\u0002\u0010iR\u001d\u0010\u00e0\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e1\u0002\u0010g\"\u0005\b\u00e2\u0002\u0010iR\u001d\u0010\u00e3\u0002\u001a\u00020eX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e4\u0002\u0010g\"\u0005\b\u00e5\u0002\u0010iR\u001d\u0010\u00e6\u0002\u001a\u00020 X\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00e7\u0002\u0010u\"\u0005\b\u00e8\u0002\u0010wR$\u0010\u00e9\u0002\u001a\u0005\u0018\u00010\u00ea\u00028VX\u0096\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00eb\u0002\u0010\u00ec\u0002\"\u0006\b\u00ed\u0002\u0010\u00ee\u0002R \u0010\u00ef\u0002\u001a\u00030\u00f0\u0002X\u0096\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00f1\u0002\u0010\u00f2\u0002\"\u0006\b\u00f3\u0002\u0010\u00f4\u0002R/\u0010\u00fc\u0002\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00fc\u0002\u0010u\"\u0005\b\u00fe\u0002\u0010w*\u0005\b\u00fd\u0002\u0010|R/\u0010\u00ff\u0002\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00ff\u0002\u0010u\"\u0005\b\u0081\u0003\u0010w*\u0005\b\u0080\u0003\u0010|R/\u0010\u0082\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0082\u0003\u0010u\"\u0005\b\u0084\u0003\u0010w*\u0005\b\u0083\u0003\u0010|R/\u0010\u0085\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0085\u0003\u0010u\"\u0005\b\u0087\u0003\u0010w*\u0005\b\u0086\u0003\u0010|R/\u0010\u0088\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0088\u0003\u0010u\"\u0005\b\u008a\u0003\u0010w*\u0005\b\u0089\u0003\u0010|R/\u0010\u008b\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u008b\u0003\u0010u\"\u0005\b\u008d\u0003\u0010w*\u0005\b\u008c\u0003\u0010|R/\u0010\u008e\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u008e\u0003\u0010u\"\u0005\b\u0090\u0003\u0010w*\u0005\b\u008f\u0003\u0010|R/\u0010\u0091\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0091\u0003\u0010u\"\u0005\b\u0093\u0003\u0010w*\u0005\b\u0092\u0003\u0010|R/\u0010\u0094\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8W@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0094\u0003\u0010u\"\u0005\b\u0096\u0003\u0010w*\u0005\b\u0095\u0003\u0010|R/\u0010\u009a\u0003\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u009c\u0003\u0010g\"\u0005\b\u009d\u0003\u0010i*\u0005\b\u009b\u0003\u0010|R/\u0010\u009e\u0003\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a0\u0003\u0010g\"\u0005\b\u00a1\u0003\u0010i*\u0005\b\u009f\u0003\u0010|R/\u0010\u00a2\u0003\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a4\u0003\u0010u\"\u0005\b\u00a5\u0003\u0010w*\u0005\b\u00a3\u0003\u0010|R\u0015\u0010\u00a6\u0003\u001a\u00030\u00a7\u0003\u00a2\u0006\n\n\u0000\u001a\u0006\b\u00a8\u0003\u0010\u00a9\u0003R?\u0010\u00aa\u0003\u001a/\u0012\u000f\u0012\r \u00ac\u0003*\u0005\u0018\u00010\u00a7\u00030\u00a7\u0003 \u00ac\u0003*\u0016\u0012\u000f\u0012\r \u00ac\u0003*\u0005\u0018\u00010\u00a7\u00030\u00a7\u0003\u0018\u00010\u00ab\u00030\u00ab\u0003X\u0082\u000e\u00a2\u0006\u0005\n\u0003\u0010\u00ad\u0003R\u001d\u0010\u00b7\u0003\u001a\u00020\u001eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00b8\u0003\u0010V\"\u0005\b\u00b9\u0003\u0010XR\u0017\u0010\u00d0\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010<X\u0082\u0004\u00a2\u0006\u0002\n\u0000R.\u0010\u00dc\u0003\u001a\u0011\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u001e\u0018\u00010\u00dd\u0003X\u0096\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00de\u0003\u0010\u00df\u0003\"\u0006\b\u00e0\u0003\u0010\u00e1\u0003R\u0018\u0010\u00f0\u0003\u001a\u00030\u00b4\u00028VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00f1\u0003\u0010\u00b6\u0002R \u0010\u00fd\u0003\u001a\u00030\u00fe\u0003X\u0084.\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00ff\u0003\u0010\u0080\u0004\"\u0006\b\u0081\u0004\u0010\u0082\u0004R'\u0010\u0083\u0004\u001a\n\u0012\u0005\u0012\u00030\u0084\u00040\u00ab\u0003X\u0084\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u0085\u0004\u0010\u0086\u0004\"\u0006\b\u0087\u0004\u0010\u0088\u0004R'\u0010\u0089\u0004\u001a\u00020 2\u0006\u0010)\u001a\u00020 @DX\u0086\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u0089\u0004\u0010u\"\u0005\b\u008a\u0004\u0010wR(\u0010\u0090\u0004\u001a\u00020\u001e2\u0007\u0010\u008f\u0004\u001a\u00020\u001e8V@VX\u0096\u000e\u00a2\u0006\u000e\u001a\u0005\b\u0091\u0004\u0010V\"\u0005\b\u0092\u0004\u0010XR\u0016\u0010\u0093\u0004\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0094\u0004\u0010VR\u0019\u0010\u00bf\u0004\u001a\u0004\u0018\u00010\u00018VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00c0\u0004\u0010\u00c1\u0004R'\u0010\u00d4\u0004\u001a\u00020e2\u0006\u0010)\u001a\u00020e8V@VX\u0096\u000e\u00a2\u0006\u000e\u001a\u0005\b\u00d5\u0004\u0010g\"\u0005\b\u00d6\u0004\u0010iR\u0016\u0010\u00ec\u0004\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00ed\u0004\u0010VR\u001a\u0010\u0080\u0005\u001a\u0005\u0018\u00010\u0081\u00058VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u0082\u0005\u0010\u0083\u0005R\u0016\u0010\u0087\u0005\u001a\u00020 8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0087\u0005\u0010uR\u0017\u0010\u008b\u0005\u001a\u00020\u00018VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u008c\u0005\u0010\u00c1\u0004R\u0016\u0010\u00a8\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00a9\u0005\u0010gR\u0016\u0010\u00aa\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00ab\u0005\u0010gR:\u0010\u00b7\u0005\u001a\u001d\u0012\u0004\u0012\u00020$\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020e\u0012\u0005\u0012\u00030\u00b9\u00050\u00dd\u00030\u00b8\u0005X\u0084\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00ba\u0005\u0010\u00bb\u0005\"\u0006\b\u00bc\u0005\u0010\u00bd\u0005R:\u0010\u00be\u0005\u001a\u001d\u0012\u0004\u0012\u00020$\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020e\u0012\u0005\u0012\u00030\u00b4\u00020\u00dd\u00030\u00b8\u0005X\u0084\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00bf\u0005\u0010\u00bb\u0005\"\u0006\b\u00c0\u0005\u0010\u00bd\u0005R:\u0010\u00c1\u0005\u001a\u001d\u0012\u0004\u0012\u00020$\u0012\u0012\u0012\u0010\u0012\u0004\u0012\u00020e\u0012\u0005\u0012\u00030\u00c2\u00050\u00dd\u00030\u00b8\u0005X\u0084\u000e\u00a2\u0006\u0012\n\u0000\u001a\u0006\b\u00c3\u0005\u0010\u00bb\u0005\"\u0006\b\u00c4\u0005\u0010\u00bd\u0005R\u0016\u0010\u00ea\u0005\u001a\u00020\u001c8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00eb\u0005\u0010[R\u0016\u0010\u00ec\u0005\u001a\u00020\u001c8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00ed\u0005\u0010[R\u001a\u0010\u00f1\u0005\u001a\u0005\u0018\u00010\u00b4\u00028VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00f2\u0005\u0010\u00b6\u0002R\u0016\u0010\u00f3\u0005\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00f4\u0005\u0010VR\u0016\u0010\u00f5\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00f6\u0005\u0010gR\u0016\u0010\u00f7\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00f8\u0005\u0010gR\u0016\u0010\u00f9\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00fa\u0005\u0010gR\u0016\u0010\u00fb\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00fc\u0005\u0010gR\u0016\u0010\u00fd\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00fe\u0005\u0010gR\u0016\u0010\u00ff\u0005\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0080\u0006\u0010gR\u001a\u0010\u0081\u0006\u001a\u0005\u0018\u00010\u00b4\u00028VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u0082\u0006\u0010\u00b6\u0002R\u001a\u0010\u0084\u0006\u001a\u0005\u0018\u00010\u00b4\u00028VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u0085\u0006\u0010\u00b6\u0002R\u001a\u0010\u0086\u0006\u001a\u0005\u0018\u00010\u00b4\u00028VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u0087\u0006\u0010\u00b6\u0002R\u0016\u0010\u0088\u0006\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0089\u0006\u0010VR\u0016\u0010\u008a\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u008b\u0006\u0010gR\u0016\u0010\u008c\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u008d\u0006\u0010gR\u0016\u0010\u008e\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u008f\u0006\u0010gR\u0016\u0010\u0090\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0091\u0006\u0010gR\u0016\u0010\u0092\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0093\u0006\u0010gR\u0016\u0010\u0094\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0095\u0006\u0010gR\u0016\u0010\u0096\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0097\u0006\u0010gR\u001a\u0010\u00b1\u0006\u001a\u0005\u0018\u00010\u00b2\u00068VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00b3\u0006\u0010\u00b4\u0006R\u0016\u0010\u00c6\u0006\u001a\u00020\u001c8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00c7\u0006\u0010[R\u0016\u0010\u00c8\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00c9\u0006\u0010gR\u0016\u0010\u00ca\u0006\u001a\u00020e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00cb\u0006\u0010gR\u001a\u0010\u00d4\u0006\u001a\u0005\u0018\u00010\u00b2\u00068VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00d5\u0006\u0010\u00b4\u0006R\u0019\u0010\u00f4\u0006\u001a\u0004\u0018\u00010\u00018VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00f5\u0006\u0010\u00c1\u0004R\u0016\u0010\u0093\u0007\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0094\u0007\u0010VR\u0016\u0010\u0095\u0007\u001a\u00020\u001e8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u0096\u0007\u0010VR/\u0010\u0097\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0099\u0007\u0010g\"\u0005\b\u009a\u0007\u0010i*\u0005\b\u0098\u0007\u0010|R/\u0010\u009b\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u009d\u0007\u0010g\"\u0005\b\u009e\u0007\u0010i*\u0005\b\u009c\u0007\u0010|R/\u0010\u009f\u0007\u001a\u00020\u001e2\u0006\u0010x\u001a\u00020\u001e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a1\u0007\u0010V\"\u0005\b\u00a2\u0007\u0010X*\u0005\b\u00a0\u0007\u0010|R/\u0010\u00a3\u0007\u001a\u00020\u001e2\u0006\u0010x\u001a\u00020\u001e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a5\u0007\u0010V\"\u0005\b\u00a6\u0007\u0010X*\u0005\b\u00a4\u0007\u0010|R/\u0010\u00a7\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a9\u0007\u0010g\"\u0005\b\u00aa\u0007\u0010i*\u0005\b\u00a8\u0007\u0010|R/\u0010\u00ab\u0007\u001a\u00020\u001e2\u0006\u0010x\u001a\u00020\u001e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00ad\u0007\u0010V\"\u0005\b\u00ae\u0007\u0010X*\u0005\b\u00ac\u0007\u0010|R/\u0010\u00af\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00b1\u0007\u0010g\"\u0005\b\u00b2\u0007\u0010i*\u0005\b\u00b0\u0007\u0010|R\u001d\u0010\u00b3\u0007\u001a\u00020eX\u0096\u000e\u00a2\u0006\u0010\n\u0000\u001a\u0005\b\u00b4\u0007\u0010g\"\u0005\b\u00b5\u0007\u0010iR/\u0010\u00b6\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00b8\u0007\u0010g\"\u0005\b\u00b9\u0007\u0010i*\u0005\b\u00b7\u0007\u0010|R/\u0010\u00ba\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00bc\u0007\u0010g\"\u0005\b\u00bd\u0007\u0010i*\u0005\b\u00bb\u0007\u0010|R/\u0010\u00be\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c0\u0007\u0010u\"\u0005\b\u00c1\u0007\u0010w*\u0005\b\u00bf\u0007\u0010|R/\u0010\u00c2\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c4\u0007\u0010u\"\u0005\b\u00c5\u0007\u0010w*\u0005\b\u00c3\u0007\u0010|R/\u0010\u00c6\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c8\u0007\u0010g\"\u0005\b\u00c9\u0007\u0010i*\u0005\b\u00c7\u0007\u0010|R/\u0010\u00ca\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00cc\u0007\u0010u\"\u0005\b\u00cd\u0007\u0010w*\u0005\b\u00cb\u0007\u0010|R/\u0010\u00ce\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00d0\u0007\u0010g\"\u0005\b\u00d1\u0007\u0010i*\u0005\b\u00cf\u0007\u0010|R/\u0010\u00d2\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00d4\u0007\u0010u\"\u0005\b\u00d5\u0007\u0010w*\u0005\b\u00d3\u0007\u0010|R/\u0010\u00d6\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00d8\u0007\u0010g\"\u0005\b\u00d9\u0007\u0010i*\u0005\b\u00d7\u0007\u0010|R/\u0010\u00da\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00dc\u0007\u0010u\"\u0005\b\u00dd\u0007\u0010w*\u0005\b\u00db\u0007\u0010|R/\u0010\u00de\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00e0\u0007\u0010g\"\u0005\b\u00e1\u0007\u0010i*\u0005\b\u00df\u0007\u0010|R/\u0010\u00e2\u0007\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00e4\u0007\u0010u\"\u0005\b\u00e5\u0007\u0010w*\u0005\b\u00e3\u0007\u0010|R/\u0010\u00e6\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00e8\u0007\u0010g\"\u0005\b\u00e9\u0007\u0010i*\u0005\b\u00e7\u0007\u0010|R;\u0010\u00ea\u0007\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00142\f\u0010x\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00148V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a3\u0005\u0010\u0017\"\u0005\b\u00ec\u0007\u0010M*\u0005\b\u00eb\u0007\u0010|R/\u0010\u00ed\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00ef\u0007\u0010g\"\u0005\b\u00f0\u0007\u0010i*\u0005\b\u00ee\u0007\u0010|R/\u0010\u00f1\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00f3\u0007\u0010g\"\u0005\b\u00f4\u0007\u0010i*\u0005\b\u00f2\u0007\u0010|R/\u0010\u00f5\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00f7\u0007\u0010g\"\u0005\b\u00f8\u0007\u0010i*\u0005\b\u00f6\u0007\u0010|R/\u0010\u00f9\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00fb\u0007\u0010g\"\u0005\b\u00fc\u0007\u0010i*\u0005\b\u00fa\u0007\u0010|R/\u0010\u00fd\u0007\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00ff\u0007\u0010g\"\u0005\b\u0080\b\u0010i*\u0005\b\u00fe\u0007\u0010|R/\u0010\u0081\b\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0083\b\u0010g\"\u0005\b\u0084\b\u0010i*\u0005\b\u0082\b\u0010|R/\u0010\u0085\b\u001a\u00020\u001e2\u0006\u0010x\u001a\u00020\u001e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u0087\b\u0010V\"\u0005\b\u0088\b\u0010X*\u0005\b\u0086\b\u0010|R/\u0010\u0089\b\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u008b\b\u0010g\"\u0005\b\u008c\b\u0010i*\u0005\b\u008a\b\u0010|R1\u0010\u008d\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u008f\b\u0010\u0090\b\"\u0006\b\u0091\b\u0010\u0092\b*\u0005\b\u008e\b\u0010|R1\u0010\u0093\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u0095\b\u0010\u0090\b\"\u0006\b\u0096\b\u0010\u0092\b*\u0005\b\u0094\b\u0010|R1\u0010\u0097\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u0099\b\u0010\u0090\b\"\u0006\b\u009a\b\u0010\u0092\b*\u0005\b\u0098\b\u0010|R1\u0010\u009b\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u009d\b\u0010\u0090\b\"\u0006\b\u009e\b\u0010\u0092\b*\u0005\b\u009c\b\u0010|RI\u0010\u009f\b\u001a\u000f\u0012\u000b\u0012\t\u0012\u0005\u0012\u00030\u00fb\u00020\u00140\u00142\u0013\u0010x\u001a\u000f\u0012\u000b\u0012\t\u0012\u0005\u0012\u00030\u00fb\u00020\u00140\u00148V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00a1\b\u0010\u0017\"\u0005\b\u00a2\b\u0010M*\u0005\b\u00a0\b\u0010|R1\u0010\u00a3\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u00a5\b\u0010\u0090\b\"\u0006\b\u00a6\b\u0010\u0092\b*\u0005\b\u00a4\b\u0010|R1\u0010\u00a7\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u00a9\b\u0010\u0090\b\"\u0006\b\u00aa\b\u0010\u0092\b*\u0005\b\u00a8\b\u0010|R3\u0010\u00ab\b\u001a\b\u0012\u0004\u0012\u00020$0<2\f\u0010)\u001a\b\u0012\u0004\u0012\u00020$0<8V@VX\u0096\u000e\u00a2\u0006\u000e\u001a\u0005\b\u00ac\b\u0010\u0017\"\u0005\b\u00ad\b\u0010MR)\u0010\u00ae\b\u001a\u00020$2\u0006\u0010)\u001a\u00020$8V@VX\u0096\u000e\u00a2\u0006\u0010\u001a\u0006\b\u00af\b\u0010\u0090\b\"\u0006\b\u00b0\b\u0010\u0092\bR1\u0010\u00b1\b\u001a\u00020$2\u0006\u0010x\u001a\u00020$8V@VX\u0096\u008e\u0002\u00a2\u0006\u0017\u001a\u0006\b\u00b3\b\u0010\u0090\b\"\u0006\b\u00b4\b\u0010\u0092\b*\u0005\b\u00b2\b\u0010|R\u001c\u0010\u00b5\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00148VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00b6\b\u0010\u0017R\u0019\u0010\u00b7\b\u001a\u0004\u0018\u00010\u00018VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00b8\b\u0010\u00c1\u0004R\u0019\u0010\u00b9\b\u001a\u0004\u0018\u00010\u00008VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00ba\b\u0010\u00bb\bR/\u0010\u00be\b\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c0\b\u0010g\"\u0005\b\u00c1\b\u0010i*\u0005\b\u00bf\b\u0010|R/\u0010\u00c2\b\u001a\u00020e2\u0006\u0010x\u001a\u00020e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c4\b\u0010g\"\u0005\b\u00c5\b\u0010i*\u0005\b\u00c3\b\u0010|R/\u0010\u00c6\b\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00c6\b\u0010u\"\u0005\b\u00c8\b\u0010w*\u0005\b\u00c7\b\u0010|R/\u0010\u00c9\b\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00cb\b\u0010u\"\u0005\b\u00cc\b\u0010w*\u0005\b\u00ca\b\u0010|R/\u0010\u00cd\b\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00cf\b\u0010u\"\u0005\b\u00d0\b\u0010w*\u0005\b\u00ce\b\u0010|R/\u0010\u00d1\b\u001a\u00020\u001e2\u0006\u0010x\u001a\u00020\u001e8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00d3\b\u0010V\"\u0005\b\u00d4\b\u0010X*\u0005\b\u00d2\b\u0010|R/\u0010\u00d5\b\u001a\u00020 2\u0006\u0010x\u001a\u00020 8V@VX\u0096\u008e\u0002\u00a2\u0006\u0015\u001a\u0005\b\u00d7\b\u0010u\"\u0005\b\u00d8\b\u0010w*\u0005\b\u00d6\b\u0010|R\u0018\u0010\u00d9\b\u001a\u00030\u0081\u00058VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00da\b\u0010\u0083\u0005R\u001a\u0010\u00e1\b\u001a\u0005\u0018\u00010\u00e2\b8VX\u0096\u0004\u00a2\u0006\b\u001a\u0006\b\u00e3\b\u0010\u00e4\bR\u0016\u0010\u00e5\b\u001a\u00020 8VX\u0096\u0004\u00a2\u0006\u0007\u001a\u0005\b\u00e5\b\u0010u\u00a8\u0006\u00f2\b"}, d2={"Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;", "Lnet/minecraft/world/entity/Entity;", "Lcom/atsuishio/superbwarfare/data/vehicle/VehiclePropertyModifier;", "Lnet/minecraft/world/entity/HasCustomInventoryScreen;", "Lcom/atsuishio/superbwarfare/entity/OBBEntity;", "Lcom/atsuishio/superbwarfare/entity/vehicle/BasicGeoVehicleEntity;", "Lcom/atsuishio/superbwarfare/entity/IBvrSyncableEntity;", "Lnet/minecraftforge/entity/IEntityAdditionalSpawnData;", "pEntityType", "Lnet/minecraft/world/entity/EntityType;", "pLevel", "Lnet/minecraft/world/level/Level;", "<init>", "(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", "anim", "Lcom/atsuishio/superbwarfare/client/animation/entity/VehicleAnimationInstance;", "getAnim", "()Lcom/atsuishio/superbwarfare/client/animation/entity/VehicleAnimationInstance;", "getAnimationInstance", "modelEntriesValue", "", "Lcom/atsuishio/superbwarfare/entity/vehicle/VehicleModelEntry;", "getModelEntriesValue", "()Ljava/util/List;", "modelEntriesValue$delegate", "Lkotlin/Lazy;", "getModelEntries", "cachedEnvRate", "", "envRateCachedTick", "", "cachedCreativeAmmoBox", "", "creativeAmmoBoxCacheTick", "gunDataMapCache", "", "", "Lcom/atsuishio/superbwarfare/data/gun/GunData;", "gunDataMapWeaponKeys", "", "inventoryDirty", "value", "gunDataMap", "getGunDataMap", "()Ljava/util/Map;", "setGunDataMap", "(Ljava/util/Map;)V", "getSeat", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/SeatInfo;", "seatIndex", "passenger", "getGunData", "weaponIndex", "name", "getGunName", "modifyGunData", "", "consumer", "Ljava/util/function/Consumer;", "obbCache", "", "Lcom/atsuishio/superbwarfare/tools/OBB;", "combinedAabbCache", "Lnet/minecraft/world/phys/AABB;", "combinedAabbCacheTick", "blockCollisionCoords", "", "blockCollisionCount", "blockCollisionCacheTick", "vehicleDataStrong", "Lcom/atsuishio/superbwarfare/data/vehicle/VehicleData;", "cachedObbOnGround", "obbOnGroundCacheTick", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/OBBInfo;", "obb", "getObb", "setObb", "(Ljava/util/List;)V", "engineInfo", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineInfo;", "getEngineInfo", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineInfo;", "setEngineInfo", "(Lcom/atsuishio/superbwarfare/data/vehicle/subdata/EngineInfo;)V", "interpolationSteps", "getInterpolationSteps", "()I", "setInterpolationSteps", "(I)V", "xO", "getXO", "()D", "setXO", "(D)V", "yO", "getYO", "setYO", "zO", "getZO", "setZO", "roll", "", "getRoll", "()F", "setRoll", "(F)V", "prevRoll", "getPrevRoll", "setPrevRoll", "repairCoolDown", "getRepairCoolDown", "setRepairCoolDown", "hurtWarnCoolDown", "getHurtWarnCoolDown", "setHurtWarnCoolDown", "crash", "getCrash", "()Z", "setCrash", "(Z)V", "<set-?>", "Lorg/joml/Quaternionf;", "loiterParams", "getLoiterParams$delegate", "(Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;)Ljava/lang/Object;", "getLoiterParams", "()Lorg/joml/Quaternionf;", "setLoiterParams", "(Lorg/joml/Quaternionf;)V", "loiterActive", "getLoiterActive$delegate", "getLoiterActive", "setLoiterActive", "v", "loiterCenterX", "getLoiterCenterX", "setLoiterCenterX", "loiterCenterY", "getLoiterCenterY", "setLoiterCenterY", "loiterCenterZ", "getLoiterCenterZ", "setLoiterCenterZ", "loiterRadius", "getLoiterRadius", "setLoiterRadius", "turretYRot", "getTurretYRot", "setTurretYRot", "turretXRot", "getTurretXRot", "setTurretXRot", "turretYRotO", "getTurretYRotO", "setTurretYRotO", "turretXRotO", "getTurretXRotO", "setTurretXRotO", "turretYRotLock", "getTurretYRotLock", "setTurretYRotLock", "gunYRot", "getGunYRot", "setGunYRot", "gunXRot", "getGunXRot", "setGunXRot", "gunYRotO", "getGunYRotO", "setGunYRotO", "gunXRotO", "getGunXRotO", "setGunXRotO", "noPassengerTime", "getNoPassengerTime", "setNoPassengerTime", "damageDebugResultReceiver", "Lnet/minecraft/world/entity/player/Player;", "getDamageDebugResultReceiver", "()Lnet/minecraft/world/entity/player/Player;", "setDamageDebugResultReceiver", "(Lnet/minecraft/world/entity/player/Player;)V", "lastTickSpeed", "getLastTickSpeed", "setLastTickSpeed", "lastTickVerticalSpeed", "getLastTickVerticalSpeed", "setLastTickVerticalSpeed", "collisionCoolDown", "getCollisionCoolDown", "setCollisionCoolDown", "wasEngineRunning", "wasHornWorking", "wasStuka", "wasHeliCrash", "wasVehicleSkip", "weaponFiringState", "", "targetSpeed", "getTargetSpeed", "setTargetSpeed", "rudderRot", "getRudderRot", "setRudderRot", "rudderRotO", "getRudderRotO", "setRudderRotO", "leftWheelRot", "getLeftWheelRot", "setLeftWheelRot", "rightWheelRot", "getRightWheelRot", "setRightWheelRot", "leftWheelRotO", "getLeftWheelRotO", "setLeftWheelRotO", "rightWheelRotO", "getRightWheelRotO", "setRightWheelRotO", "leftTrackO", "getLeftTrackO", "setLeftTrackO", "rightTrackO", "getRightTrackO", "setRightTrackO", "leftTrack", "getLeftTrack", "setLeftTrack", "rightTrack", "getRightTrack", "setRightTrack", "recoilShake", "getRecoilShake", "setRecoilShake", "recoilShakeO", "getRecoilShakeO", "setRecoilShakeO", "flap1LRot", "getFlap1LRot", "setFlap1LRot", "flap1LRotO", "getFlap1LRotO", "setFlap1LRotO", "flap1RRot", "getFlap1RRot", "setFlap1RRot", "flap1RRotO", "getFlap1RRotO", "setFlap1RRotO", "flap1L2Rot", "getFlap1L2Rot", "setFlap1L2Rot", "flap1L2RotO", "getFlap1L2RotO", "setFlap1L2RotO", "flap1R2Rot", "getFlap1R2Rot", "setFlap1R2Rot", "flap1R2RotO", "getFlap1R2RotO", "setFlap1R2RotO", "flap2LRot", "getFlap2LRot", "setFlap2LRot", "flap2LRotO", "getFlap2LRotO", "setFlap2LRotO", "flap2RRot", "getFlap2RRot", "setFlap2RRot", "flap2RRotO", "getFlap2RRotO", "setFlap2RRotO", "flap3Rot", "getFlap3Rot", "setFlap3Rot", "flap3RotO", "getFlap3RotO", "setFlap3RotO", "gearRot", "getGearRot", "setGearRot", "engineStart", "getEngineStart", "setEngineStart", "engineStartOver", "getEngineStartOver", "setEngineStartOver", "holdTick", "getHoldTick", "setHoldTick", "holdPowerTick", "getHoldPowerTick", "setHoldPowerTick", "liftSpeed", "getLiftSpeed", "setLiftSpeed", "destroyRot", "getDestroyRot", "setDestroyRot", "liftOffset", "getLiftOffset$delegate", "getLiftOffset", "setLiftOffset", "jumpCoolDown", "getJumpCoolDown", "setJumpCoolDown", "deltaMovementO", "Lnet/minecraft/world/phys/Vec3;", "getDeltaMovementO", "()Lnet/minecraft/world/phys/Vec3;", "setDeltaMovementO", "(Lnet/minecraft/world/phys/Vec3;)V", "positionO", "getPositionO", "setPositionO", "absoluteSpeed", "getAbsoluteSpeed", "setAbsoluteSpeed", "absoluteSpeedO", "getAbsoluteSpeedO", "setAbsoluteSpeedO", "absoluteSpeedLerp", "getAbsoluteSpeedLerp", "setAbsoluteSpeedLerp", "pitchAngle", "getPitchAngle", "setPitchAngle", "pitchVelocity", "getPitchVelocity", "setPitchVelocity", "prevPitchAngle", "getPrevPitchAngle", "setPrevPitchAngle", "rollAngle", "getRollAngle", "setRollAngle", "rollVelocity", "getRollVelocity", "setRollVelocity", "prevRollAngle", "getPrevRollAngle", "setPrevRollAngle", "prevMotion", "getPrevMotion", "setPrevMotion", "fakePitchO", "getFakePitchO", "setFakePitchO", "fakeRollO", "getFakeRollO", "setFakeRollO", "fakePitch", "getFakePitch", "setFakePitch", "fakeRoll", "getFakeRoll", "setFakeRoll", "wasGearUp", "getWasGearUp", "setWasGearUp", "lastDamageSource", "Lnet/minecraft/world/damagesource/DamageSource;", "getLastDamageSource", "()Lnet/minecraft/world/damagesource/DamageSource;", "setLastDamageSource", "(Lnet/minecraft/world/damagesource/DamageSource;)V", "lastDamageStamp", "", "getLastDamageStamp", "()J", "setLastDamageStamp", "(J)V", "initOBB", "onSyncedDataUpdated", "key", "Lnet/minecraft/network/syncher/EntityDataAccessor;", "processInput", "keys", "", "forwardInputDown", "forwardInputDown$delegate", "setForwardInputDown", "backInputDown", "backInputDown$delegate", "setBackInputDown", "leftInputDown", "leftInputDown$delegate", "setLeftInputDown", "rightInputDown", "rightInputDown$delegate", "setRightInputDown", "upInputDown", "upInputDown$delegate", "setUpInputDown", "downInputDown", "downInputDown$delegate", "setDownInputDown", "fireInputDown", "fireInputDown$delegate", "setFireInputDown", "decoyInputDown", "decoyInputDown$delegate", "setDecoyInputDown", "sprintInputDown", "sprintInputDown$delegate", "setSprintInputDown", "mouseInput", "x", "y", "mouseMoveSpeedX", "getMouseMoveSpeedX$delegate", "getMouseMoveSpeedX", "setMouseMoveSpeedX", "mouseMoveSpeedY", "getMouseMoveSpeedY$delegate", "getMouseMoveSpeedY", "setMouseMoveSpeedY", "locked", "getLocked$delegate", "getLocked", "setLocked", "inventory", "Lcom/atsuishio/superbwarfare/inventory/handler/VehicleContainerHandler;", "getInventory", "()Lcom/atsuishio/superbwarfare/inventory/handler/VehicleContainerHandler;", "itemHandler", "Lnet/minecraftforge/common/util/LazyOptional;", "kotlin.jvm.PlatformType", "Lnet/minecraftforge/common/util/LazyOptional;", "getItems", "Lnet/minecraft/core/NonNullList;", "Lnet/minecraft/world/item/ItemStack;", "resizeItems", "getContainerSize", "getItem", "slot", "removeItem", "pAmount", "maxStackSize", "getMaxStackSize", "setMaxStackSize", "setItem", "pStack", "setChanged", "clearContent", "hasContainer", "canPlaceItem", "stack", "canTakeItem", "remove", "reason", "Lnet/minecraft/world/entity/Entity$RemovalReason;", "clearTowingInfo", "openCustomInventoryScreen", "player", "hasMenu", "openMenu", "createMenu", "Lnet/minecraft/world/inventory/AbstractContainerMenu;", "pContainerId", "pPlayerInventory", "Lnet/minecraft/world/entity/player/Inventory;", "pPlayer", "orderedPassengers", "generatePassengersList", "initSeatData", "targetSize", "padList", "T", "list", "defaultValue", "onRemove", "(Ljava/util/List;ILjava/lang/Object;Ljava/util/function/Consumer;)V", "checkSeatsSize", "getOrderedPassengers", "entityIndexOverride", "Ljava/util/function/Function;", "getEntityIndexOverride", "()Ljava/util/function/Function;", "setEntityIndexOverride", "(Ljava/util/function/Function;)V", "addPassenger", "pPassenger", "removePassenger", "data", "computed", "Lcom/atsuishio/superbwarfare/data/vehicle/DefaultVehicleData;", "getStepHeight", "getFirstPassenger", "getNthEntity", "index", "changeSeat", "entity", "getSeatIndex", "getTagSeatIndex", "thirdPersonCameraPosition", "getThirdPersonCameraPosition", "tickDelta", "getYaw", "getPitch", "setZRot", "rot", "turretTurnSound", "diffX", "diffY", "pitch", "shouldSendHitParticles", "shouldSendHitSounds", "energyStorage", "Lcom/atsuishio/superbwarfare/capability/energy/SyncedEntityEnergyStorage;", "getEnergyStorage", "()Lcom/atsuishio/superbwarfare/capability/energy/SyncedEntityEnergyStorage;", "setEnergyStorage", "(Lcom/atsuishio/superbwarfare/capability/energy/SyncedEntityEnergyStorage;)V", "energyOptional", "Lnet/minecraftforge/energy/IEnergyStorage;", "getEnergyOptional", "()Lnet/minecraftforge/common/util/LazyOptional;", "setEnergyOptional", "(Lnet/minecraftforge/common/util/LazyOptional;)V", "isInitialized", "setInitialized", "defineSynchedData", "consumeEnergy", "amount", "canConsume", "pEnergy", "energy", "getEnergy", "setEnergy", "maxEnergy", "getMaxEnergy", "hasEnergyStorage", "canShoot", "living", "Lnet/minecraft/world/entity/LivingEntity;", "vehicleWeaponRpm", "weaponName", "getWeaponHeat", "getShootAnimationTimer", "vehicleShoot", "targetPos", "uuid", "Ljava/util/UUID;", "afterShoot", "gunData", "shootVec", "playShootSound3p", "pos", "getWeaponIndex", "hasWeapon", "setWeaponIndex", "selectedWeaponIndex", "changeWeapon", "isScroll", "getAddEntityPacket", "Lnet/minecraft/network/protocol/Packet;", "Lnet/minecraft/network/protocol/game/ClientGamePacketListener;", "writeSpawnData", "buffer", "Lnet/minecraft/network/FriendlyByteBuf;", "readSpawnData", "additionalData", "readAdditionalSaveData", "compound", "Lnet/minecraft/nbt/CompoundTag;", "addAdditionalSaveData", "buildBvrSyncNbt", "tag", "interact", "Lnet/minecraft/world/InteractionResult;", "hand", "Lnet/minecraft/world/InteractionHand;", "onCrowbarInteract", "lastDriver", "getLastDriver", "()Lnet/minecraft/world/entity/Entity;", "setDriverAngle", "hurt", "source", "getDamageModifier", "Lcom/atsuishio/superbwarfare/entity/vehicle/damage/DamageModifier;", "getSourceAngle", "multiplier", "heal", "pHealAmount", "onHurt", "attacker", "send", "updateInWaterStateAndDoFluidPushing", "isInFluidType", "predicate", "Ljava/util/function/BiPredicate;", "Lnet/minecraftforge/fluids/FluidType;", "isInLava", "health", "getHealth", "setHealth", "getMaxHealth", "getDecoyReloadTime", "getTurretMaxHealth", "getLeftWheelMaxHealth", "getRightWheelMaxHealth", "getMainEngineMaxHealth", "getSubEngineMaxHealth", "getWheelMaxHealth", "getEngineMaxHealth", "lavaHurt", "makeStuckInBlock", "pState", "Lnet/minecraft/world/level/block/state/BlockState;", "pMotionMultiplier", "playStepSound", "pPos", "Lnet/minecraft/core/BlockPos;", "canBeCollidedWith", "isPickable", "skipAttackInteraction", "canAddPassenger", "maxPassengers", "getMaxPassengers", "maxRepairCoolDown", "repairAmount", "baseTick", "hasCreativeAmmoBoxCached", "towedTick", "towingTick", "keepChunkLoaded", "chunkPos", "Lnet/minecraft/world/level/ChunkPos;", "position", "vehicleRadar", "getRadarVec", "partialTicks", "stringOrVec3", "Lcom/atsuishio/superbwarfare/data/StringOrVec3;", "canFreeze", "checkObbOnGroundCached", "updateOBB", "shootSoundInstance", "Lnet/minecraft/sounds/SoundEvent;", "getShootSoundInstance", "()Lnet/minecraft/sounds/SoundEvent;", "isWeaponFiring", "weaponShootingVolume", "weaponShootingPitch", "isFiring", "shootingVolume", "shootingPitch", "updateBackupAmmoCount", "ammoSupplier", "getAmmoSupplier", "handlePartDamaged", "obbEntity", "handlePartHealth", "addRandomParticle", "particleOptions", "Lnet/minecraft/core/particles/ParticleOptions;", "randomPos", "level", "speed", "count", "vec3", "defaultPartDamageEffect", "onTurretDamaged", "onLeftWheelDamaged", "onRightWheelDamaged", "onEngine1Damaged", "onEngine2Damaged", "clearArrow", "lowHealthWarning", "turretBurnEffectPos", "playLowHealthParticle", "adjustTurretAngle", "getSelectedWeapon", "turretAutoAimFromVector", "turretAutoAimFromUuid", "pLiving", "onPassengerTurned", "customTurretMinPitch", "getCustomTurretMinPitch", "customTurretMaxPitch", "getCustomTurretMaxPitch", "clampRotation", "copyEntityData", "getTransformDirection", "ticks", "getTransformDirectionNoOrientation", "getTransformDirectionFromString", "string", "positionRider", "callback", "Lnet/minecraft/world/entity/Entity$MoveFunction;", "passengerPos", "positionTransform", "Ljava/util/HashMap;", "Lorg/joml/Matrix4d;", "getPositionTransform", "()Ljava/util/HashMap;", "setPositionTransform", "(Ljava/util/HashMap;)V", "vectorTransform", "getVectorTransform", "setVectorTransform", "rotationTransform", "Lorg/joml/Quaterniond;", "getRotationTransform", "setRotationTransform", "registerTransforms", "getTransformFromString", "getVectorFromString", "cameraDirection", "getRotationFromString", "getShootPos", "bombHitPos", "getShootPosForHud", "getShootDirectionForHud", "getDefaultBarrelDirection", "getShootVec", "getViewVec", "getViewPos", "getSeekVec", "getPlayerLookAtEntityOnVehicle", "shooter", "entityReach", "partialTick", "getProjectileVelocity", "getProjectileGravity", "getProjectileSpread", "passengerWeaponAutoAimFormUuid", "passengerWeaponAutoAimFormVector", "adjustWeaponControllerAngle", "destroy", "vehicleExplosion", "destroyInfo", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/DestroyInfo;", "createCustomExplosion", "Lcom/atsuishio/superbwarfare/tools/CustomExplosion$Builder;", "crashPassengers", "explodePassengers", "travel", "getEngineSoundVolume", "getVehicleTransform", "getVehicleTransformWithCustomPitch", "getVehicleYOffsetTransform", "rotateOffsetHeight", "getRotateOffsetHeight", "laserBaseScale", "getLaserBaseScale", "getVehicleFlatTransform", "getClientVehicleTransform", "hasTurret", "turretPos", "getTurretPos", "turretControllerIndex", "getTurretControllerIndex", "turretTurnXSpeed", "getTurretTurnXSpeed", "turretTurnYSpeed", "getTurretTurnYSpeed", "turretMinYaw", "getTurretMinYaw", "turretMaxYaw", "getTurretMaxYaw", "turretMinPitch", "getTurretMinPitch", "turretMaxPitch", "getTurretMaxPitch", "barrelPosition", "getBarrelPosition", "hasPassengerWeaponStation", "passengerWeaponStationPosition", "getPassengerWeaponStationPosition", "passengerWeaponStationBarrelPosition", "getPassengerWeaponStationBarrelPosition", "passengerWeaponStationControllerIndex", "getPassengerWeaponStationControllerIndex", "passengerWeaponYSpeed", "getPassengerWeaponYSpeed", "passengerWeaponXSpeed", "getPassengerWeaponXSpeed", "passengerWeaponMinPitch", "getPassengerWeaponMinPitch", "passengerWeaponMaxPitch", "getPassengerWeaponMaxPitch", "passengerWeaponMinYaw", "getPassengerWeaponMinYaw", "passengerWeaponMaxYaw", "getPassengerWeaponMaxYaw", "turretCustomPitch", "getTurretCustomPitch", "getTurretTransform", "getTurretVector", "pPartialTicks", "getBarrelTransform", "getGunTransform", "getPassengerWeaponStationBarrelTransform", "getPassengerWeaponStationVector", "transformPosition", "Lorg/joml/Vector4d;", "transform", "z", "handleClientSync", "lerpTo", "yaw", "interpolate", "getDismountOffset", "vehicleWidth", "passengerWidth", "getDismountLocationForPassenger", "getDismountLocationForIndex", "dismount", "getEjectionPosition", "allowEjection", "removeSeatIndexTag", "getEjectionMovement", "vehicleIcon", "Lnet/minecraft/resources/ResourceLocation;", "getVehicleIcon", "()Lnet/minecraft/resources/ResourceLocation;", "allowFreeCam", "getUpVec", "getRightVec", "push", "pX", "pY", "pZ", "getBarrelVector", "getBarrelXRot", "getBarrelYRot", "pPartialTick", "getTurretYaw", "getTurretPitch", "getCameraPos", "getZoomPos", "getZoomDirection", "isAlwaysTicking", "mouseSensitivity", "getMouseSensitivity", "passengerRenderScale", "getPassengerRenderScale", "mass", "getMass", "setDeltaMovement", "pDeltaMovement", "addDeltaMovement", "pAddend", "getSensitivity", "original", "zoom", "isOnGround", "vehicleItemIcon", "getVehicleItemIcon", "isEnclosed", "banHand", "hidePassenger", "getAmmoCount", "getAmmo", "getPickResult", "useAircraftCamera", "getCameraRotation", "Lnet/minecraft/world/phys/Vec2;", "isFirstPerson", "getCameraPosition", "useFixedCameraPos", "getCapability", "cap", "Lnet/minecraftforge/common/capabilities/Capability;", "side", "Lnet/minecraft/core/Direction;", "invalidateCaps", "reviveCaps", "getDefaultZoom", "canCrushEntities", "fixedEngine", "releaseSmokeDecoy", "releaseDecoy", "countDecoyItem", "terrainCompact", "positions", "getWheelsTransform", "moveOnDragonTeeth", "collideBlocks", "lastAttacker", "getLastAttacker", "vCollide", "pVec", "vMove", "pType", "Lnet/minecraft/world/entity/MoverType;", "move", "movementType", "movement", "setOnGroundWithKnownMovement", "onGround", "bounceHorizontal", "direction", "bounceVertical", "preventStacking", "pushNew", "supportEntities", "getRandom", "Lnet/minecraft/util/RandomSource;", "crushEntities", "getForwardDirection", "Lorg/joml/Vector3f;", "getRightDirection", "getEngineSound", "getAcceleration", "getTrackAnimationLength", "hasDecoy", "hasSmokeDecoy", "engineRunning", "getRetrieveItems", "hudColor", "getHudColor", "laserColor", "getLaserColor", "power", "getPower$delegate", "getPower", "setPower", "deltaRot", "getDeltaRot$delegate", "getDeltaRot", "setDeltaRot", "decoyCount", "getDecoyCount$delegate", "getDecoyCount", "setDecoyCount", "decoyReloadCoolDown", "getDecoyReloadCoolDown$delegate", "getDecoyReloadCoolDown", "setDecoyReloadCoolDown", "synchedPropellerRot", "getSynchedPropellerRot$delegate", "getSynchedPropellerRot", "setSynchedPropellerRot", "decoyItemCount", "getDecoyItemCount$delegate", "getDecoyItemCount", "setDecoyItemCount", "propellerRot", "getPropellerRot$delegate", "getPropellerRot", "setPropellerRot", "propellerRotO", "getPropellerRotO", "setPropellerRotO", "planeBreak", "getPlaneBreak$delegate", "getPlaneBreak", "setPlaneBreak", "synchedGearRot", "getSynchedGearRot$delegate", "getSynchedGearRot", "setSynchedGearRot", "gearUp", "getGearUp$delegate", "getGearUp", "setGearUp", "subEngineDamaged", "getSubEngineDamaged$delegate", "getSubEngineDamaged", "setSubEngineDamaged", "subEngineHealth", "getSubEngineHealth$delegate", "getSubEngineHealth", "setSubEngineHealth", "mainEngineDamaged", "getMainEngineDamaged$delegate", "getMainEngineDamaged", "setMainEngineDamaged", "mainEngineHealth", "getMainEngineHealth$delegate", "getMainEngineHealth", "setMainEngineHealth", "leftWheelDamaged", "getLeftWheelDamaged$delegate", "getLeftWheelDamaged", "setLeftWheelDamaged", "leftWheelHealth", "getLeftWheelHealth$delegate", "getLeftWheelHealth", "setLeftWheelHealth", "rightWheelDamaged", "getRightWheelDamaged$delegate", "getRightWheelDamaged", "setRightWheelDamaged", "rightWheelHealth", "getRightWheelHealth$delegate", "getRightWheelHealth", "setRightWheelHealth", "turretDamaged", "getTurretDamaged$delegate", "getTurretDamaged", "setTurretDamaged", "turretHealth", "getTurretHealth$delegate", "getTurretHealth", "setTurretHealth", "selectedWeapon", "getSelectedWeapon$delegate", "setSelectedWeapon", "chargeProgress", "getChargeProgress$delegate", "getChargeProgress", "setChargeProgress", "laserScale", "getLaserScale$delegate", "getLaserScale", "setLaserScale", "laserScaleO", "getLaserScaleO$delegate", "getLaserScaleO", "setLaserScaleO", "laserLength", "getLaserLength$delegate", "getLaserLength", "setLaserLength", "serverYaw", "getServerYaw$delegate", "getServerYaw", "setServerYaw", "serverPitch", "getServerPitch$delegate", "getServerPitch", "setServerPitch", "cannonRecoilTime", "getCannonRecoilTime$delegate", "getCannonRecoilTime", "setCannonRecoilTime", "cannonRecoilForce", "getCannonRecoilForce$delegate", "getCannonRecoilForce", "setCannonRecoilForce", "override", "getOverride$delegate", "getOverride", "()Ljava/lang/String;", "setOverride", "(Ljava/lang/String;)V", "skinId", "getSkinId$delegate", "getSkinId", "setSkinId", "lastAttackerUUID", "getLastAttackerUUID$delegate", "getLastAttackerUUID", "setLastAttackerUUID", "lastDriverUUID", "getLastDriverUUID$delegate", "getLastDriverUUID", "setLastDriverUUID", "dogTagIcon", "getDogTagIcon$delegate", "getDogTagIcon", "setDogTagIcon", "aiTurretTargetUUID", "getAiTurretTargetUUID$delegate", "getAiTurretTargetUUID", "setAiTurretTargetUUID", "aiPassengerWeaponTargetUUID", "getAiPassengerWeaponTargetUUID$delegate", "getAiPassengerWeaponTargetUUID", "setAiPassengerWeaponTargetUUID", "towingUUIDs", "getTowingUUIDs", "setTowingUUIDs", "towingUUID", "getTowingUUID", "setTowingUUID", "towedByUUID", "getTowedByUUID$delegate", "getTowedByUUID", "setTowedByUUID", "towingEntities", "getTowingEntities", "towingEntity", "getTowingEntity", "towedByEntity", "getTowedByEntity", "()Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;", "isTowing", "isTowingAny", "yawWhileShoot", "getYawWhileShoot$delegate", "getYawWhileShoot", "setYawWhileShoot", "hornVolume", "getHornVolume$delegate", "getHornVolume", "setHornVolume", "isWreck", "isWreck$delegate", "setWreck", "sympatheticDetonated", "getSympatheticDetonated$delegate", "getSympatheticDetonated", "setSympatheticDetonated", "turretBurned", "getTurretBurned$delegate", "getTurretBurned", "setTurretBurned", "turretBurnTimer", "getTurretBurnTimer$delegate", "getTurretBurnTimer", "setTurretBurnTimer", "hoverMode", "getHoverMode$delegate", "getHoverMode", "setHoverMode", "hornSound", "getHornSound", "horn", "hornWorking", "stuka", "heliCrash", "vehicleSkip", "drift", "vehicleType", "Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;", "getVehicleType", "()Lcom/atsuishio/superbwarfare/data/vehicle/subdata/VehicleType;", "isAmphibious", "firstPersonAmmoComponent", "Lnet/minecraft/network/chat/Component;", "thirdPersonAmmoComponent", "getOBBs", "getCombinedAABB", "invalidateAABBCache", "refreshBoundingBoxFromOBBs", "getCollisionOBB", "getCollisionOBBInfo", "getEnergyDataAccessor", "generateWreckageLoot", "Companion", "superbwarfare"})
@SourceDebugExtension(value={"SMAP\nVehicleEntity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VehicleEntity.kt\ncom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 5 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n*L\n1#1,5442:1\n774#2:5443\n865#2,2:5444\n1863#2,2:5447\n1863#2:5449\n1864#2:5454\n1863#2:5455\n1864#2:5460\n1863#2:5461\n1864#2:5469\n1863#2,2:5470\n1611#2,9:5472\n1863#2:5481\n1864#2:5483\n1620#2:5484\n295#2,2:5485\n295#2,2:5487\n1611#2,9:5489\n1863#2:5498\n1864#2:5500\n1620#2:5501\n1#3:5446\n1#3:5482\n1#3:5499\n11132#4:5450\n11467#4,3:5451\n11122#4:5456\n11457#4,3:5457\n381#5,7:5462\n*S KotlinDebug\n*F\n+ 1 VehicleEntity.kt\ncom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity\n*L\n716#1:5443\n716#1:5444,2\n925#1:5447,2\n1656#1:5449\n1656#1:5454\n1769#1:5455\n1769#1:5460\n3027#1:5461\n3027#1:5469\n3207#1:5470,2\n4937#1:5472,9\n4937#1:5481\n4937#1:5483\n4937#1:5484\n5069#1:5485,2\n5079#1:5487,2\n149#1:5489,9\n149#1:5498\n149#1:5500\n149#1:5501\n4937#1:5482\n149#1:5499\n1659#1:5450\n1659#1:5451,3\n1770#1:5456\n1770#1:5457,3\n3029#1:5462,7\n*E\n"})
public class VehicleEntity
extends Entity
implements VehiclePropertyModifier,
HasCustomInventoryScreen,
OBBEntity,
BasicGeoVehicleEntity,
IBvrSyncableEntity,
IEntityAdditionalSpawnData {
    @NotNull
    public static final Companion Companion;
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    @Nullable
    private final VehicleAnimationInstance<VehicleEntity> anim;
    @NotNull
    private final Lazy modelEntriesValue$delegate;
    private double cachedEnvRate;
    private int envRateCachedTick;
    private boolean cachedCreativeAmmoBox;
    private int creativeAmmoBoxCacheTick;
    @Nullable
    private Map<String, GunData> gunDataMapCache;
    @Nullable
    private Set<String> gunDataMapWeaponKeys;
    private boolean inventoryDirty;
    @Nullable
    private List<OBB> obbCache;
    @Nullable
    private AABB combinedAabbCache;
    private int combinedAabbCacheTick;
    @JvmField
    @NotNull
    public double[] blockCollisionCoords;
    @JvmField
    public int blockCollisionCount;
    @JvmField
    public int blockCollisionCacheTick;
    @JvmField
    @Nullable
    public VehicleData vehicleDataStrong;
    private boolean cachedObbOnGround;
    private int obbOnGroundCacheTick;
    @NotNull
    private List<OBBInfo> obb;
    @Nullable
    private EngineInfo engineInfo;
    private int interpolationSteps;
    private double xO;
    private double yO;
    private double zO;
    private float roll;
    private float prevRoll;
    private int repairCoolDown;
    private int hurtWarnCoolDown;
    private boolean crash;
    private float turretYRot;
    private float turretXRot;
    private float turretYRotO;
    private float turretXRotO;
    private float turretYRotLock;
    private float gunYRot;
    private float gunXRot;
    private float gunYRotO;
    private float gunXRotO;
    private int noPassengerTime;
    @Nullable
    private Player damageDebugResultReceiver;
    private double lastTickSpeed;
    private double lastTickVerticalSpeed;
    private int collisionCoolDown;
    private boolean wasEngineRunning;
    private boolean wasHornWorking;
    private boolean wasStuka;
    private boolean wasHeliCrash;
    private boolean wasVehicleSkip;
    @NotNull
    private final Map<String, Boolean> weaponFiringState;
    private double targetSpeed;
    private float rudderRot;
    private float rudderRotO;
    private float leftWheelRot;
    private float rightWheelRot;
    private float leftWheelRotO;
    private float rightWheelRotO;
    private float leftTrackO;
    private float rightTrackO;
    private float leftTrack;
    private float rightTrack;
    private double recoilShake;
    private double recoilShakeO;
    private float flap1LRot;
    private float flap1LRotO;
    private float flap1RRot;
    private float flap1RRotO;
    private float flap1L2Rot;
    private float flap1L2RotO;
    private float flap1R2Rot;
    private float flap1R2RotO;
    private float flap2LRot;
    private float flap2LRotO;
    private float flap2RRot;
    private float flap2RRotO;
    private float flap3Rot;
    private float flap3RotO;
    private float gearRot;
    private boolean engineStart;
    private boolean engineStartOver;
    private int holdTick;
    private int holdPowerTick;
    private float liftSpeed;
    private float destroyRot;
    private int jumpCoolDown;
    @NotNull
    private Vec3 deltaMovementO;
    @NotNull
    private Vec3 positionO;
    private double absoluteSpeed;
    private double absoluteSpeedO;
    private double absoluteSpeedLerp;
    private float pitchAngle;
    private float pitchVelocity;
    private float prevPitchAngle;
    private float rollAngle;
    private float rollVelocity;
    private float prevRollAngle;
    @Nullable
    private Vec3 prevMotion;
    private float fakePitchO;
    private float fakeRollO;
    private float fakePitch;
    private float fakeRoll;
    private boolean wasGearUp;
    @Nullable
    private DamageSource lastDamageSource;
    private long lastDamageStamp;
    @NotNull
    private final VehicleContainerHandler inventory;
    private LazyOptional<VehicleContainerHandler> itemHandler;
    private int maxStackSize;
    @NotNull
    private final List<Entity> orderedPassengers;
    @Nullable
    private Function<Entity, Integer> entityIndexOverride;
    protected SyncedEntityEnergyStorage energyStorage;
    @NotNull
    private LazyOptional<IEnergyStorage> energyOptional;
    private boolean isInitialized;
    @NotNull
    private HashMap<String, Function<Float, Matrix4d>> positionTransform;
    @NotNull
    private HashMap<String, Function<Float, Vec3>> vectorTransform;
    @NotNull
    private HashMap<String, Function<Float, Quaterniond>> rotationTransform;
    private float propellerRotO;
    @NotNull
    public static final String TAG_SEAT_INDEX = "SBWSeatIndex";
    private static final int ENV_RATE_RECOMPUTE_INTERVAL = 4;
    private static final int OBB_GROUND_CACHE_TICKS = 2;
    private static final int BACKUP_AMMO_UPDATE_INTERVAL = 100;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> HEALTH;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> OVERRIDE;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> SKIN_ID;
    @ExcludeBvrSync(nbtKey="LastAttacker")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> LAST_ATTACKER_UUID;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> LAST_DRIVER_UUID;
    @ExcludeBvrSync(nbtKey="DogTagIcon")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<List<List<Short>>> DOG_TAG_ICON;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> AI_TURRET_TARGET_UUID;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> AI_PASSENGER_WEAPON_TARGET_UUID;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> DELTA_ROT;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> MOUSE_SPEED_X;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> MOUSE_SPEED_Y;
    @ExcludeBvrSync(nbtKey="SelectedWeapon")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<List<Integer>> SELECTED_WEAPON;
    @ExcludeBvrSync(nbtKey="TurretHealth")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> TURRET_HEALTH;
    @ExcludeBvrSync(nbtKey="LeftWheelHealth")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> L_WHEEL_HEALTH;
    @ExcludeBvrSync(nbtKey="RightWheelHealth")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> R_WHEEL_HEALTH;
    @ExcludeBvrSync(nbtKey="MainEngineHealth")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> MAIN_ENGINE_HEALTH;
    @ExcludeBvrSync(nbtKey="SubEngineHealth")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> SUB_ENGINE_HEALTH;
    @ExcludeBvrSync(nbtKey="TurretDamaged")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> TURRET_DAMAGED;
    @ExcludeBvrSync(nbtKey="LeftWheelDamaged")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> L_WHEEL_DAMAGED;
    @ExcludeBvrSync(nbtKey="RightWheelDamaged")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> R_WHEEL_DAMAGED;
    @ExcludeBvrSync(nbtKey="MainEngineDamaged")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> MAIN_ENGINE_DAMAGED;
    @ExcludeBvrSync(nbtKey="SubEngineDamaged")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> SUB_ENGINE_DAMAGED;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> HORN_VOLUME;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playTrackSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playEngineSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playSwimSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playHornSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playStukaSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playHeliCrashSound;
    @JvmField
    @NotNull
    public static Consumer<VehicleEntity> playVehicleSkipSound;
    @JvmField
    @Nullable
    public static BiConsumer<VehicleEntity, String> playFireSound;
    @JvmField
    public static boolean ignoreEntityGroundCheckStepping;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> SERVER_YAW;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> SERVER_PITCH;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> CANNON_RECOIL_TIME;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> CANNON_RECOIL_FORCE;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> POWER;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> YAW_WHILE_SHOOT;
    @ExcludeBvrSync(nbtKey="DecoyCount")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> DECOY_COUNT;
    @ExcludeBvrSync(nbtKey="DecoyReloadCoolDown")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> DECOY_RELOAD_COOLDOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> DECOY_ITEM_COUNT;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> SYNCHED_PROPELLER_ROT;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> PROPELLER_ROT;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> SYNCHED_GEAR_ROT;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> GEAR_UP;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> FORWARD_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> BACK_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> LEFT_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> RIGHT_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> UP_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> DOWN_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> DECOY_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> FIRE_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> SPRINT_INPUT_DOWN;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> PLANE_BREAK;
    @ExcludeBvrSync(nbtKey="Energy")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> ENERGY;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> LASER_LENGTH;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> LASER_SCALE;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> LASER_SCALE_O;
    @ExcludeBvrSync(nbtKey="ChargeProgress")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> CHARGE_PROGRESS;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> IS_WRECK;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> SYMPATHETIC_DETONATED;
    @ExcludeBvrSync(nbtKey="TurretBurned")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> TURRET_BURNED;
    @ExcludeBvrSync(nbtKey="TurretBurnTimer")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Integer> TURRET_BURN_TIMER;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> HOVER_MODE;
    @ExcludeBvrSync(nbtKey="Locked")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> LOCKED;
    @NotNull
    private static final EntityDataAccessor<Map<String, GunData>> GUN_DATA_MAP;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Quaternionf> LOITER_PARAMS;
    @ExcludeBvrSync(nbtKey="LoiterActive")
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Boolean> LOITER_ACTIVE;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<CompoundTag> TOWING_UUIDS;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<String> TOWED_BY_UUID;
    @JvmField
    @NotNull
    public static final EntityDataAccessor<Float> LIFT_OFFSET;

    public VehicleEntity(@NotNull EntityType<?> pEntityType, @NotNull Level pLevel) {
        Intrinsics.checkNotNullParameter(pEntityType, (String)"pEntityType");
        Intrinsics.checkNotNullParameter((Object)pLevel, (String)"pLevel");
        super(pEntityType, pLevel);
        this.anim = pLevel.f_46443_ ? VehicleAnimationInstance.Companion.create(this) : null;
        this.modelEntriesValue$delegate = LazyKt.lazy(() -> VehicleEntity.modelEntriesValue_delegate$lambda$2(this));
        this.cachedEnvRate = 1.0;
        this.envRateCachedTick = -4;
        this.creativeAmmoBoxCacheTick = Integer.MIN_VALUE;
        this.combinedAabbCacheTick = -1;
        this.blockCollisionCoords = new double[0];
        this.blockCollisionCacheTick = -1;
        this.obbOnGroundCacheTick = Integer.MIN_VALUE;
        this.obb = CollectionsKt.emptyList();
        this.repairCoolDown = this.maxRepairCoolDown();
        this.weaponFiringState = new LinkedHashMap();
        Vec3 vec3 = this.m_20184_();
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getDeltaMovement(...)");
        this.deltaMovementO = vec3;
        Vec3 vec32 = Vec3.f_82478_;
        Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"ZERO");
        this.positionO = vec32;
        this.inventory = new VehicleContainerHandler(102, this);
        this.itemHandler = LazyOptional.of(() -> VehicleEntity.itemHandler$lambda$5(this));
        this.maxStackSize = 64;
        this.orderedPassengers = this.generatePassengersList();
        LazyOptional lazyOptional = LazyOptional.of(() -> VehicleEntity.energyOptional$lambda$20(this));
        Intrinsics.checkNotNullExpressionValue((Object)lazyOptional, (String)"of(...)");
        this.energyOptional = lazyOptional;
        this.positionTransform = new HashMap();
        this.vectorTransform = new HashMap();
        this.rotationTransform = new HashMap();
        this.registerTransforms();
        this.initOBB();
        if (this.hasEnergyStorage()) {
            this.setEnergyStorage(new VehicleEnergyStorage(this));
        }
        this.isInitialized = true;
        this.setHealth(this.getMaxHealth());
    }

    @Nullable
    public final VehicleAnimationInstance<VehicleEntity> getAnim() {
        return this.anim;
    }

    @Nullable
    public VehicleAnimationInstance<VehicleEntity> getAnimationInstance() {
        return this.anim;
    }

    private final List<VehicleModelEntry> getModelEntriesValue() {
        Lazy lazy = this.modelEntriesValue$delegate;
        return (List)lazy.getValue();
    }

    @Override
    @NotNull
    public List<VehicleModelEntry> getModelEntries() {
        return this.getModelEntriesValue();
    }

    @NotNull
    public Map<String, GunData> getGunDataMap() {
        if (this.gunDataMapCache != null && this.gunDataMapWeaponKeys != null) {
            Map<String, GunData> map = this.gunDataMapCache;
            Intrinsics.checkNotNull(map);
            return map;
        }
        Map<String, DefaultGunData> weapons = this.computed().weapons();
        Map rawMap = (Map)this.f_19804_.m_135370_(GUN_DATA_MAP);
        LinkedHashMap newMap = new LinkedHashMap();
        for (Map.Entry<String, DefaultGunData> kv : weapons.entrySet()) {
            GunData existing = (GunData)rawMap.get(kv.getKey());
            if (existing != null) {
                existing.updateDefaultDataSupplier((Function0<DefaultGunData>)((Function0)() -> VehicleEntity._get_gunDataMap_$lambda$3(kv)));
                ((Map)newMap).put(kv.getKey(), existing);
                continue;
            }
            ((Map)newMap).put(kv.getKey(), GunData.Companion.from(new ItemStack((ItemLike)ModItems.VEHICLE_GUN.get()), (Function0<DefaultGunData>)((Function0)() -> VehicleEntity._get_gunDataMap_$lambda$4(kv))));
        }
        this.gunDataMapCache = newMap;
        this.gunDataMapWeaponKeys = weapons.keySet();
        return newMap;
    }

    public void setGunDataMap(@NotNull Map<String, GunData> value) {
        Intrinsics.checkNotNullParameter(value, (String)"value");
        this.f_19804_.m_135381_(GUN_DATA_MAP, (Object)MapsKt.toMap(value));
        this.gunDataMapCache = null;
        this.gunDataMapWeaponKeys = null;
    }

    @Nullable
    public SeatInfo getSeat(int seatIndex) {
        return (SeatInfo)CollectionsKt.getOrNull(this.computed().seats(), (int)seatIndex);
    }

    @Nullable
    public SeatInfo getSeat(@Nullable Entity passenger) {
        return this.getSeat(this.getSeatIndex(passenger));
    }

    @Nullable
    public GunData getGunData(int seatIndex) {
        Integer n = (Integer)CollectionsKt.getOrNull(this.getSelectedWeapon(), (int)seatIndex);
        if (n == null) {
            return null;
        }
        return this.getGunData(seatIndex, (int)n);
    }

    @Nullable
    public GunData getGunData(int seatIndex, int weaponIndex) {
        SeatInfo seatInfo = this.getSeat(seatIndex);
        if (seatInfo == null) {
            return null;
        }
        SeatInfo seat = seatInfo;
        String string = (String)CollectionsKt.getOrNull(seat.weapons(), (int)weaponIndex);
        if (string == null) {
            return null;
        }
        String name = string;
        return this.getGunData(name);
    }

    @Nullable
    public GunData getGunData(@Nullable Entity passenger, int weaponIndex) {
        return this.getGunData(this.getSeatIndex(passenger), weaponIndex);
    }

    @Nullable
    public GunData getGunData(@Nullable Entity passenger) {
        return this.getGunData(passenger, this.getSelectedWeapon(this.getSeatIndex(passenger)));
    }

    @Nullable
    public GunData getGunData(@NotNull String name) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        return this.getGunDataMap().get(name);
    }

    @Nullable
    public String getGunName(int seatIndex) {
        if (seatIndex < 0) {
            return null;
        }
        SeatInfo seatInfo = this.getSeat(seatIndex);
        if (seatInfo == null) {
            return null;
        }
        SeatInfo seat = seatInfo;
        Integer n = (Integer)CollectionsKt.getOrNull(this.getSelectedWeapon(), (int)seatIndex);
        if (n == null) {
            return null;
        }
        int weaponIndex = n;
        if (weaponIndex < 0) {
            return null;
        }
        List<String> weapons = seat.weapons();
        if (weaponIndex >= weapons.size()) {
            return null;
        }
        return this.getGunName(seatIndex, weaponIndex);
    }

    @Nullable
    public String getGunName(int seatIndex, int weaponIndex) {
        Object object = this.getSeat(seatIndex);
        return object != null && (object = ((SeatInfo)object).weapons()) != null ? (String)CollectionsKt.getOrNull((List)object, (int)weaponIndex) : null;
    }

    public void modifyGunData(int seatIndex, int weaponIndex, @NotNull Consumer<GunData> consumer) {
        Intrinsics.checkNotNullParameter(consumer, (String)"consumer");
        this.modifyGunData(this.getGunName(seatIndex, weaponIndex), consumer);
    }

    public void modifyGunData(int seatIndex, @NotNull Consumer<GunData> consumer) {
        Intrinsics.checkNotNullParameter(consumer, (String)"consumer");
        this.modifyGunData(this.getGunName(seatIndex), consumer);
    }

    public void modifyGunData(@Nullable String name, @NotNull Consumer<GunData> consumer) {
        Intrinsics.checkNotNullParameter(consumer, (String)"consumer");
        if (name == null) {
            return;
        }
        Map<String, GunData> map = this.getGunDataMap();
        GunData gunData = map.get(name);
        if (gunData == null) {
            return;
        }
        GunData data = gunData;
        consumer.accept(data);
        data.save();
        this.f_19804_.m_276349_(GUN_DATA_MAP, map, true);
    }

    @NotNull
    public List<OBBInfo> getObb() {
        return this.obb;
    }

    protected void setObb(@NotNull List<OBBInfo> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        this.obb = list;
    }

    @Nullable
    public EngineInfo getEngineInfo() {
        return this.engineInfo;
    }

    public void setEngineInfo(@Nullable EngineInfo engineInfo) {
        this.engineInfo = engineInfo;
    }

    protected final int getInterpolationSteps() {
        return this.interpolationSteps;
    }

    protected final void setInterpolationSteps(int n) {
        this.interpolationSteps = n;
    }

    protected final double getXO() {
        return this.xO;
    }

    protected final void setXO(double d) {
        this.xO = d;
    }

    protected final double getYO() {
        return this.yO;
    }

    protected final void setYO(double d) {
        this.yO = d;
    }

    protected final double getZO() {
        return this.zO;
    }

    protected final void setZO(double d) {
        this.zO = d;
    }

    public float getRoll() {
        return this.roll;
    }

    public void setRoll(float f) {
        this.roll = f;
    }

    public float getPrevRoll() {
        return this.prevRoll;
    }

    public void setPrevRoll(float f) {
        this.prevRoll = f;
    }

    public int getRepairCoolDown() {
        return this.repairCoolDown;
    }

    public void setRepairCoolDown(int n) {
        this.repairCoolDown = n;
    }

    public int getHurtWarnCoolDown() {
        return this.hurtWarnCoolDown;
    }

    public void setHurtWarnCoolDown(int n) {
        this.hurtWarnCoolDown = n;
    }

    public boolean getCrash() {
        return this.crash;
    }

    public void setCrash(boolean bl) {
        this.crash = bl;
    }

    @NotNull
    public Quaternionf getLoiterParams() {
        return EntityUtilKt.getValue(LOITER_PARAMS, this, $$delegatedProperties[0]);
    }

    public void setLoiterParams(@NotNull Quaternionf quaternionf) {
        Intrinsics.checkNotNullParameter((Object)quaternionf, (String)"<set-?>");
        EntityUtilKt.setValue(LOITER_PARAMS, this, $$delegatedProperties[0], quaternionf);
    }

    private static Object getLoiterParams$delegate(VehicleEntity vehicleEntity) {
        return LOITER_PARAMS;
    }

    public boolean getLoiterActive() {
        return EntityUtilKt.getValue(LOITER_ACTIVE, this, $$delegatedProperties[1]);
    }

    public void setLoiterActive(boolean bl) {
        EntityUtilKt.setValue(LOITER_ACTIVE, this, $$delegatedProperties[1], bl);
    }

    private static Object getLoiterActive$delegate(VehicleEntity vehicleEntity) {
        return LOITER_ACTIVE;
    }

    public final double getLoiterCenterX() {
        return this.getLoiterParams().x();
    }

    public final void setLoiterCenterX(double v) {
        this.setLoiterParams(new Quaternionf((float)v, this.getLoiterParams().y(), this.getLoiterParams().z(), this.getLoiterParams().w()));
    }

    public final double getLoiterCenterY() {
        return this.getLoiterParams().y();
    }

    public final void setLoiterCenterY(double v) {
        this.setLoiterParams(new Quaternionf(this.getLoiterParams().x(), (float)v, this.getLoiterParams().z(), this.getLoiterParams().w()));
    }

    public final double getLoiterCenterZ() {
        return this.getLoiterParams().z();
    }

    public final void setLoiterCenterZ(double v) {
        this.setLoiterParams(new Quaternionf(this.getLoiterParams().x(), this.getLoiterParams().y(), (float)v, this.getLoiterParams().w()));
    }

    public final double getLoiterRadius() {
        return this.getLoiterParams().w();
    }

    public final void setLoiterRadius(double v) {
        this.setLoiterParams(new Quaternionf(this.getLoiterParams().x(), this.getLoiterParams().y(), this.getLoiterParams().z(), (float)v));
    }

    public float getTurretYRot() {
        return this.turretYRot;
    }

    public void setTurretYRot(float f) {
        this.turretYRot = f;
    }

    public float getTurretXRot() {
        return this.turretXRot;
    }

    public void setTurretXRot(float f) {
        this.turretXRot = f;
    }

    public float getTurretYRotO() {
        return this.turretYRotO;
    }

    public void setTurretYRotO(float f) {
        this.turretYRotO = f;
    }

    public float getTurretXRotO() {
        return this.turretXRotO;
    }

    public void setTurretXRotO(float f) {
        this.turretXRotO = f;
    }

    public float getTurretYRotLock() {
        return this.turretYRotLock;
    }

    public void setTurretYRotLock(float f) {
        this.turretYRotLock = f;
    }

    public float getGunYRot() {
        return this.gunYRot;
    }

    public void setGunYRot(float f) {
        this.gunYRot = f;
    }

    public float getGunXRot() {
        return this.gunXRot;
    }

    public void setGunXRot(float f) {
        this.gunXRot = f;
    }

    public float getGunYRotO() {
        return this.gunYRotO;
    }

    public void setGunYRotO(float f) {
        this.gunYRotO = f;
    }

    public float getGunXRotO() {
        return this.gunXRotO;
    }

    public void setGunXRotO(float f) {
        this.gunXRotO = f;
    }

    protected final int getNoPassengerTime() {
        return this.noPassengerTime;
    }

    protected final void setNoPassengerTime(int n) {
        this.noPassengerTime = n;
    }

    @Nullable
    public final Player getDamageDebugResultReceiver() {
        return this.damageDebugResultReceiver;
    }

    public final void setDamageDebugResultReceiver(@Nullable Player player) {
        this.damageDebugResultReceiver = player;
    }

    public double getLastTickSpeed() {
        return this.lastTickSpeed;
    }

    public void setLastTickSpeed(double d) {
        this.lastTickSpeed = d;
    }

    public double getLastTickVerticalSpeed() {
        return this.lastTickVerticalSpeed;
    }

    public void setLastTickVerticalSpeed(double d) {
        this.lastTickVerticalSpeed = d;
    }

    public int getCollisionCoolDown() {
        return this.collisionCoolDown;
    }

    public void setCollisionCoolDown(int n) {
        this.collisionCoolDown = n;
    }

    public double getTargetSpeed() {
        return this.targetSpeed;
    }

    public void setTargetSpeed(double d) {
        this.targetSpeed = d;
    }

    public float getRudderRot() {
        return this.rudderRot;
    }

    public void setRudderRot(float f) {
        this.rudderRot = f;
    }

    public float getRudderRotO() {
        return this.rudderRotO;
    }

    public void setRudderRotO(float f) {
        this.rudderRotO = f;
    }

    public float getLeftWheelRot() {
        return this.leftWheelRot;
    }

    public void setLeftWheelRot(float f) {
        this.leftWheelRot = f;
    }

    public float getRightWheelRot() {
        return this.rightWheelRot;
    }

    public void setRightWheelRot(float f) {
        this.rightWheelRot = f;
    }

    public float getLeftWheelRotO() {
        return this.leftWheelRotO;
    }

    public void setLeftWheelRotO(float f) {
        this.leftWheelRotO = f;
    }

    public float getRightWheelRotO() {
        return this.rightWheelRotO;
    }

    public void setRightWheelRotO(float f) {
        this.rightWheelRotO = f;
    }

    public float getLeftTrackO() {
        return this.leftTrackO;
    }

    public void setLeftTrackO(float f) {
        this.leftTrackO = f;
    }

    public float getRightTrackO() {
        return this.rightTrackO;
    }

    public void setRightTrackO(float f) {
        this.rightTrackO = f;
    }

    public float getLeftTrack() {
        return this.leftTrack;
    }

    public void setLeftTrack(float f) {
        this.leftTrack = f;
    }

    public float getRightTrack() {
        return this.rightTrack;
    }

    public void setRightTrack(float f) {
        this.rightTrack = f;
    }

    public double getRecoilShake() {
        return this.recoilShake;
    }

    public void setRecoilShake(double d) {
        this.recoilShake = d;
    }

    public double getRecoilShakeO() {
        return this.recoilShakeO;
    }

    public void setRecoilShakeO(double d) {
        this.recoilShakeO = d;
    }

    public float getFlap1LRot() {
        return this.flap1LRot;
    }

    public void setFlap1LRot(float f) {
        this.flap1LRot = f;
    }

    public float getFlap1LRotO() {
        return this.flap1LRotO;
    }

    public void setFlap1LRotO(float f) {
        this.flap1LRotO = f;
    }

    public float getFlap1RRot() {
        return this.flap1RRot;
    }

    public void setFlap1RRot(float f) {
        this.flap1RRot = f;
    }

    public float getFlap1RRotO() {
        return this.flap1RRotO;
    }

    public void setFlap1RRotO(float f) {
        this.flap1RRotO = f;
    }

    public float getFlap1L2Rot() {
        return this.flap1L2Rot;
    }

    public void setFlap1L2Rot(float f) {
        this.flap1L2Rot = f;
    }

    public float getFlap1L2RotO() {
        return this.flap1L2RotO;
    }

    public void setFlap1L2RotO(float f) {
        this.flap1L2RotO = f;
    }

    public float getFlap1R2Rot() {
        return this.flap1R2Rot;
    }

    public void setFlap1R2Rot(float f) {
        this.flap1R2Rot = f;
    }

    public float getFlap1R2RotO() {
        return this.flap1R2RotO;
    }

    public void setFlap1R2RotO(float f) {
        this.flap1R2RotO = f;
    }

    public float getFlap2LRot() {
        return this.flap2LRot;
    }

    public void setFlap2LRot(float f) {
        this.flap2LRot = f;
    }

    public float getFlap2LRotO() {
        return this.flap2LRotO;
    }

    public void setFlap2LRotO(float f) {
        this.flap2LRotO = f;
    }

    public float getFlap2RRot() {
        return this.flap2RRot;
    }

    public void setFlap2RRot(float f) {
        this.flap2RRot = f;
    }

    public float getFlap2RRotO() {
        return this.flap2RRotO;
    }

    public void setFlap2RRotO(float f) {
        this.flap2RRotO = f;
    }

    public float getFlap3Rot() {
        return this.flap3Rot;
    }

    public void setFlap3Rot(float f) {
        this.flap3Rot = f;
    }

    public float getFlap3RotO() {
        return this.flap3RotO;
    }

    public void setFlap3RotO(float f) {
        this.flap3RotO = f;
    }

    public float getGearRot() {
        return this.gearRot;
    }

    public void setGearRot(float f) {
        this.gearRot = f;
    }

    public boolean getEngineStart() {
        return this.engineStart;
    }

    public void setEngineStart(boolean bl) {
        this.engineStart = bl;
    }

    public boolean getEngineStartOver() {
        return this.engineStartOver;
    }

    public void setEngineStartOver(boolean bl) {
        this.engineStartOver = bl;
    }

    public int getHoldTick() {
        return this.holdTick;
    }

    public void setHoldTick(int n) {
        this.holdTick = n;
    }

    public int getHoldPowerTick() {
        return this.holdPowerTick;
    }

    public void setHoldPowerTick(int n) {
        this.holdPowerTick = n;
    }

    public float getLiftSpeed() {
        return this.liftSpeed;
    }

    public void setLiftSpeed(float f) {
        this.liftSpeed = f;
    }

    public float getDestroyRot() {
        return this.destroyRot;
    }

    public void setDestroyRot(float f) {
        this.destroyRot = f;
    }

    public float getLiftOffset() {
        return ((Number)EntityUtilKt.getValue(LIFT_OFFSET, this, $$delegatedProperties[2])).floatValue();
    }

    public void setLiftOffset(float f) {
        EntityUtilKt.setValue(LIFT_OFFSET, this, $$delegatedProperties[2], Float.valueOf(f));
    }

    private static Object getLiftOffset$delegate(VehicleEntity vehicleEntity) {
        return LIFT_OFFSET;
    }

    public int getJumpCoolDown() {
        return this.jumpCoolDown;
    }

    public void setJumpCoolDown(int n) {
        this.jumpCoolDown = n;
    }

    @NotNull
    public Vec3 getDeltaMovementO() {
        return this.deltaMovementO;
    }

    public void setDeltaMovementO(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"<set-?>");
        this.deltaMovementO = vec3;
    }

    @NotNull
    public Vec3 getPositionO() {
        return this.positionO;
    }

    public void setPositionO(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"<set-?>");
        this.positionO = vec3;
    }

    public double getAbsoluteSpeed() {
        return this.absoluteSpeed;
    }

    public void setAbsoluteSpeed(double d) {
        this.absoluteSpeed = d;
    }

    public double getAbsoluteSpeedO() {
        return this.absoluteSpeedO;
    }

    public void setAbsoluteSpeedO(double d) {
        this.absoluteSpeedO = d;
    }

    public double getAbsoluteSpeedLerp() {
        return this.absoluteSpeedLerp;
    }

    public void setAbsoluteSpeedLerp(double d) {
        this.absoluteSpeedLerp = d;
    }

    public final float getPitchAngle() {
        return this.pitchAngle;
    }

    public final void setPitchAngle(float f) {
        this.pitchAngle = f;
    }

    public final float getPitchVelocity() {
        return this.pitchVelocity;
    }

    public final void setPitchVelocity(float f) {
        this.pitchVelocity = f;
    }

    public final float getPrevPitchAngle() {
        return this.prevPitchAngle;
    }

    public final void setPrevPitchAngle(float f) {
        this.prevPitchAngle = f;
    }

    public final float getRollAngle() {
        return this.rollAngle;
    }

    public final void setRollAngle(float f) {
        this.rollAngle = f;
    }

    public final float getRollVelocity() {
        return this.rollVelocity;
    }

    public final void setRollVelocity(float f) {
        this.rollVelocity = f;
    }

    public final float getPrevRollAngle() {
        return this.prevRollAngle;
    }

    public final void setPrevRollAngle(float f) {
        this.prevRollAngle = f;
    }

    @Nullable
    public final Vec3 getPrevMotion() {
        return this.prevMotion;
    }

    public final void setPrevMotion(@Nullable Vec3 vec3) {
        this.prevMotion = vec3;
    }

    public final float getFakePitchO() {
        return this.fakePitchO;
    }

    public final void setFakePitchO(float f) {
        this.fakePitchO = f;
    }

    public final float getFakeRollO() {
        return this.fakeRollO;
    }

    public final void setFakeRollO(float f) {
        this.fakeRollO = f;
    }

    public final float getFakePitch() {
        return this.fakePitch;
    }

    public final void setFakePitch(float f) {
        this.fakePitch = f;
    }

    public final float getFakeRoll() {
        return this.fakeRoll;
    }

    public final void setFakeRoll(float f) {
        this.fakeRoll = f;
    }

    public final boolean getWasGearUp() {
        return this.wasGearUp;
    }

    public final void setWasGearUp(boolean bl) {
        this.wasGearUp = bl;
    }

    @Nullable
    public DamageSource getLastDamageSource() {
        if (this.m_9236_().m_46467_() - this.getLastDamageStamp() > 40L) {
            this.setLastDamageSource(null);
        }
        return this.lastDamageSource;
    }

    public void setLastDamageSource(@Nullable DamageSource damageSource) {
        this.lastDamageSource = damageSource;
    }

    public long getLastDamageStamp() {
        return this.lastDamageStamp;
    }

    public void setLastDamageStamp(long l) {
        this.lastDamageStamp = l;
    }

    private final void initOBB() {
        this.obbCache = null;
        this.invalidateAABBCache();
        this.setObb(CollectionsKt.toList((Iterable)((DefaultVehicleData)this.data().getDefault().copy()).getObb()));
    }

    public void m_7350_(@NotNull EntityDataAccessor<?> key) {
        Intrinsics.checkNotNullParameter(key, (String)"key");
        super.m_7350_(key);
        if (Intrinsics.areEqual(key, OVERRIDE)) {
            this.data().update();
        }
        if (Intrinsics.areEqual(key, GUN_DATA_MAP)) {
            this.gunDataMapCache = null;
            this.gunDataMapWeaponKeys = null;
        }
        if (Intrinsics.areEqual(key, IS_WRECK) && this.isWreck() && this.m_9236_().f_46443_ && this.f_19797_ > 1) {
            VehicleLightingHandler.emitVehicleExplosionLight$default(this, 0.0f, 2, null);
        }
    }

    public void processInput(short keys) {
        this.setLeftInputDown((keys & 1) > 0);
        this.setRightInputDown((keys & 2) > 0);
        this.setForwardInputDown((keys & 4) > 0);
        this.setBackInputDown((keys & 8) > 0);
        this.setUpInputDown((keys & 0x10) > 0);
        this.setDownInputDown((keys & 0x20) > 0);
        this.setDecoyInputDown((keys & 0x40) > 0);
        this.setFireInputDown((keys & 0x80) > 0);
        this.setSprintInputDown((keys & 0x100) > 0);
    }

    @JvmName(name="forwardInputDown")
    public boolean forwardInputDown() {
        return EntityUtilKt.getValue(FORWARD_INPUT_DOWN, this, $$delegatedProperties[3]);
    }

    public void setForwardInputDown(boolean bl) {
        EntityUtilKt.setValue(FORWARD_INPUT_DOWN, this, $$delegatedProperties[3], bl);
    }

    private static Object forwardInputDown$delegate(VehicleEntity vehicleEntity) {
        return FORWARD_INPUT_DOWN;
    }

    @JvmName(name="backInputDown")
    public boolean backInputDown() {
        return EntityUtilKt.getValue(BACK_INPUT_DOWN, this, $$delegatedProperties[4]);
    }

    public void setBackInputDown(boolean bl) {
        EntityUtilKt.setValue(BACK_INPUT_DOWN, this, $$delegatedProperties[4], bl);
    }

    private static Object backInputDown$delegate(VehicleEntity vehicleEntity) {
        return BACK_INPUT_DOWN;
    }

    @JvmName(name="leftInputDown")
    public boolean leftInputDown() {
        return EntityUtilKt.getValue(LEFT_INPUT_DOWN, this, $$delegatedProperties[5]);
    }

    public void setLeftInputDown(boolean bl) {
        EntityUtilKt.setValue(LEFT_INPUT_DOWN, this, $$delegatedProperties[5], bl);
    }

    private static Object leftInputDown$delegate(VehicleEntity vehicleEntity) {
        return LEFT_INPUT_DOWN;
    }

    @JvmName(name="rightInputDown")
    public boolean rightInputDown() {
        return EntityUtilKt.getValue(RIGHT_INPUT_DOWN, this, $$delegatedProperties[6]);
    }

    public void setRightInputDown(boolean bl) {
        EntityUtilKt.setValue(RIGHT_INPUT_DOWN, this, $$delegatedProperties[6], bl);
    }

    private static Object rightInputDown$delegate(VehicleEntity vehicleEntity) {
        return RIGHT_INPUT_DOWN;
    }

    @JvmName(name="upInputDown")
    public boolean upInputDown() {
        return EntityUtilKt.getValue(UP_INPUT_DOWN, this, $$delegatedProperties[7]);
    }

    public void setUpInputDown(boolean bl) {
        EntityUtilKt.setValue(UP_INPUT_DOWN, this, $$delegatedProperties[7], bl);
    }

    private static Object upInputDown$delegate(VehicleEntity vehicleEntity) {
        return UP_INPUT_DOWN;
    }

    @JvmName(name="downInputDown")
    public boolean downInputDown() {
        return EntityUtilKt.getValue(DOWN_INPUT_DOWN, this, $$delegatedProperties[8]);
    }

    public void setDownInputDown(boolean bl) {
        EntityUtilKt.setValue(DOWN_INPUT_DOWN, this, $$delegatedProperties[8], bl);
    }

    private static Object downInputDown$delegate(VehicleEntity vehicleEntity) {
        return DOWN_INPUT_DOWN;
    }

    @JvmName(name="fireInputDown")
    public boolean fireInputDown() {
        return EntityUtilKt.getValue(FIRE_INPUT_DOWN, this, $$delegatedProperties[9]);
    }

    public void setFireInputDown(boolean bl) {
        EntityUtilKt.setValue(FIRE_INPUT_DOWN, this, $$delegatedProperties[9], bl);
    }

    private static Object fireInputDown$delegate(VehicleEntity vehicleEntity) {
        return FIRE_INPUT_DOWN;
    }

    @JvmName(name="decoyInputDown")
    public boolean decoyInputDown() {
        return EntityUtilKt.getValue(DECOY_INPUT_DOWN, this, $$delegatedProperties[10]);
    }

    public void setDecoyInputDown(boolean bl) {
        EntityUtilKt.setValue(DECOY_INPUT_DOWN, this, $$delegatedProperties[10], bl);
    }

    private static Object decoyInputDown$delegate(VehicleEntity vehicleEntity) {
        return DECOY_INPUT_DOWN;
    }

    @JvmName(name="sprintInputDown")
    public boolean sprintInputDown() {
        return EntityUtilKt.getValue(SPRINT_INPUT_DOWN, this, $$delegatedProperties[11]);
    }

    public void setSprintInputDown(boolean bl) {
        EntityUtilKt.setValue(SPRINT_INPUT_DOWN, this, $$delegatedProperties[11], bl);
    }

    private static Object sprintInputDown$delegate(VehicleEntity vehicleEntity) {
        return SPRINT_INPUT_DOWN;
    }

    public void mouseInput(double x, double y) {
        this.setMouseMoveSpeedX((float)x);
        this.setMouseMoveSpeedY((float)y);
    }

    public float getMouseMoveSpeedX() {
        return ((Number)EntityUtilKt.getValue(MOUSE_SPEED_X, this, $$delegatedProperties[12])).floatValue();
    }

    public void setMouseMoveSpeedX(float f) {
        EntityUtilKt.setValue(MOUSE_SPEED_X, this, $$delegatedProperties[12], Float.valueOf(f));
    }

    private static Object getMouseMoveSpeedX$delegate(VehicleEntity vehicleEntity) {
        return MOUSE_SPEED_X;
    }

    public float getMouseMoveSpeedY() {
        return ((Number)EntityUtilKt.getValue(MOUSE_SPEED_Y, this, $$delegatedProperties[13])).floatValue();
    }

    public void setMouseMoveSpeedY(float f) {
        EntityUtilKt.setValue(MOUSE_SPEED_Y, this, $$delegatedProperties[13], Float.valueOf(f));
    }

    private static Object getMouseMoveSpeedY$delegate(VehicleEntity vehicleEntity) {
        return MOUSE_SPEED_Y;
    }

    public boolean getLocked() {
        return EntityUtilKt.getValue(LOCKED, this, $$delegatedProperties[14]);
    }

    public void setLocked(boolean bl) {
        EntityUtilKt.setValue(LOCKED, this, $$delegatedProperties[14], bl);
    }

    private static Object getLocked$delegate(VehicleEntity vehicleEntity) {
        return LOCKED;
    }

    @NotNull
    public final VehicleContainerHandler getInventory() {
        return this.inventory;
    }

    @NotNull
    public NonNullList<ItemStack> getItems() {
        return this.inventory.getItems();
    }

    protected final void resizeItems() {
        int i;
        int oldSize;
        int newSize = this.getContainerSize();
        if (newSize == (oldSize = this.inventory.getSlots())) {
            return;
        }
        NonNullList oldStacks = NonNullList.m_122780_((int)oldSize, (Object)ItemStack.f_41583_);
        for (i = 0; i < oldSize; ++i) {
            oldStacks.set(i, (Object)this.inventory.getStackInSlot(i));
        }
        this.inventory.setSize(newSize);
        int n = Math.min(oldSize, newSize);
        for (i = 0; i < n; ++i) {
            this.inventory.setStackInSlot(i, (ItemStack)oldStacks.get(i));
        }
        if (newSize < oldSize) {
            for (i = newSize; i < oldSize; ++i) {
                Object object = oldStacks.get(i);
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
                ItemStack stack = (ItemStack)object;
                if (stack.m_41619_()) continue;
                this.m_5552_(stack, 0.5f);
            }
        }
    }

    public int getContainerSize() {
        return this.computed().getVehicleContainerType().getSize();
    }

    @NotNull
    public final ItemStack getItem(int slot) {
        if (!this.hasContainer() || slot >= this.getContainerSize() || slot < 0) {
            ItemStack itemStack = ItemStack.f_41583_;
            Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"EMPTY");
            return itemStack;
        }
        ItemStack itemStack = this.inventory.getStackInSlot(slot);
        Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"getStackInSlot(...)");
        return itemStack;
    }

    @NotNull
    public final ItemStack removeItem(int slot, int pAmount) {
        if (!this.hasContainer() || slot >= this.getContainerSize() || slot < 0) {
            ItemStack itemStack = ItemStack.f_41583_;
            Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"EMPTY");
            return itemStack;
        }
        ItemStack itemStack = this.inventory.extractItem(slot, pAmount, false);
        Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"extractItem(...)");
        return itemStack;
    }

    public int getMaxStackSize() {
        return this.maxStackSize;
    }

    public void setMaxStackSize(int n) {
        this.maxStackSize = n;
    }

    public final void setItem(int slot, @NotNull ItemStack pStack) {
        Intrinsics.checkNotNullParameter((Object)pStack, (String)"pStack");
        if (!this.hasContainer() || slot >= this.getContainerSize() || slot < 0) {
            return;
        }
        int limit = Math.min(this.getMaxStackSize(), pStack.m_41741_());
        if (!pStack.m_41619_() && pStack.m_41613_() > limit) {
            Mod.LOGGER.warn("try inserting ItemStack {} exceeding the maximum stack size: {}, clamped to {}", (Object)pStack.m_41720_(), (Object)limit, (Object)limit);
            pStack.m_41764_(limit);
        }
        this.inventory.setStackInSlot(slot, pStack);
    }

    public void setChanged() {
        if (this.m_9236_().f_46443_) {
            return;
        }
        this.inventoryDirty = true;
        VehicleContainerHandler vehicleContainerHandler = this.itemHandler.resolve().orElse(null);
        if (vehicleContainerHandler == null) {
            return;
        }
        VehicleContainerHandler item = vehicleContainerHandler;
        Entity entity = this;
        int n = this.m_19879_();
        CompoundTag compoundTag = item.serializeNBT();
        Intrinsics.checkNotNullExpressionValue((Object)compoundTag, (String)"serializeNBT(...)");
        MinecraftUtil.sendPacketToTrackingThis(entity, new ClientVehicleItemMessage(n, (Tag)compoundTag));
    }

    public final void clearContent() {
        this.inventory.clear();
    }

    public boolean hasContainer() {
        return this.getContainerSize() > 0;
    }

    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        int stackCount;
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        if (!this.hasContainer() || slot >= this.getContainerSize() || slot < 0) {
            return false;
        }
        ItemStack itemStack = this.inventory.getStackInSlot(slot);
        Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"getStackInSlot(...)");
        ItemStack currentStack = itemStack;
        if (!currentStack.m_41619_() && currentStack.m_41720_() != stack.m_41720_()) {
            return false;
        }
        int currentCount = currentStack.m_41613_();
        int combinedCount = currentCount + (stackCount = stack.m_41613_());
        return combinedCount <= this.getMaxStackSize() && combinedCount <= stack.m_41741_();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean canTakeItem(int slot) {
        if (!this.hasContainer()) return false;
        if (0 > slot) return false;
        if (slot >= this.getContainerSize()) return false;
        return true;
    }

    public void m_142687_(@NotNull Entity.RemovalReason reason) {
        Intrinsics.checkNotNullParameter((Object)reason, (String)"reason");
        if (!this.m_9236_().f_46443_) {
            ServerSyncedEntityHandler.unregister(this);
        }
        if (!this.m_9236_().f_46443_ && reason != Entity.RemovalReason.DISCARDED && reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            int n = this.inventory.getSlots();
            for (int i = 0; i < n; ++i) {
                ItemStack stack;
                Intrinsics.checkNotNullExpressionValue((Object)this.inventory.getStackInSlot(i), (String)"getStackInSlot(...)");
                if (stack.m_41619_()) continue;
                this.m_5552_(stack, 0.5f);
            }
        }
        this.clearTowingInfo();
        this.vehicleDataStrong = null;
        super.m_142687_(reason);
    }

    /*
     * WARNING - void declaration
     */
    public void clearTowingInfo() {
        if (!this.m_9236_().f_46443_) {
            for (String uuid : CollectionsKt.toList((Iterable)this.getTowingUUIDs())) {
                Entity entity;
                Level level = this.m_9236_();
                Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
                Entity towed = EntityFindUtil.findEntity(level, uuid);
                if (towed instanceof VehicleEntity) {
                    ((VehicleEntity)towed).setTowedByUUID("");
                    continue;
                }
                Entity entity2 = towed;
                if (entity2 != null && (entity2 = entity2.getPersistentData()) != null) {
                    entity2.m_128473_("TowedByUUID");
                }
                if ((entity = towed) == null || (entity = entity.getPersistentData()) == null) continue;
                entity.m_128473_("TowedByShuttle");
            }
            this.setTowingUUIDs(new ArrayList());
            VehicleEntity vehicleEntity = this.getTowedByEntity();
            if (vehicleEntity != null) {
                void $this$filterTo$iv$iv;
                VehicleEntity tower = vehicleEntity;
                boolean bl = false;
                Iterable $this$filter$iv = tower.getTowingUUIDs();
                boolean $i$f$filter = false;
                Iterable iterable = $this$filter$iv;
                Collection destination$iv$iv = new ArrayList();
                boolean $i$f$filterTo = false;
                for (Object element$iv$iv : $this$filterTo$iv$iv) {
                    String it = (String)element$iv$iv;
                    boolean bl2 = false;
                    if (!(!Intrinsics.areEqual((Object)it, (Object)this.f_19821_))) continue;
                    destination$iv$iv.add(element$iv$iv);
                }
                List filtered = (List)destination$iv$iv;
                tower.setTowingUUIDs(CollectionsKt.toMutableList((Collection)filtered));
            }
            this.setTowedByUUID("");
        }
    }

    public void m_213583_(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        if (player instanceof ServerPlayer) {
            this.openMenu(player);
        }
    }

    public boolean hasMenu() {
        return this.computed().getVehicleContainerType().hasMenu();
    }

    public void openMenu(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        if (player instanceof ServerPlayer) {
            NetworkHooks.openScreen((ServerPlayer)((ServerPlayer)player), (MenuProvider)((MenuProvider)new SimpleMenuProvider((arg_0, arg_1, arg_2) -> VehicleEntity.openMenu$lambda$8(this, arg_0, arg_1, arg_2), (Component)Component.m_237115_((String)this.m_6095_().m_20675_()))), arg_0 -> VehicleEntity.openMenu$lambda$9(this, arg_0));
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        Intrinsics.checkNotNullParameter((Object)pPlayerInventory, (String)"pPlayerInventory");
        Intrinsics.checkNotNullParameter((Object)pPlayer, (String)"pPlayer");
        if (!pPlayer.m_5833_() && this.hasMenu()) {
            DefaultVehicleData computed = this.computed();
            VehicleContainerType type = computed.getVehicleContainerType();
            if (!type.hasMenu()) {
                return null;
            }
            return switch (WhenMappings.$EnumSwitchMapping$0[type.ordinal()]) {
                case 1 -> new MiniVehicleContainerMenu(pContainerId, pPlayerInventory, this.m_19879_());
                case 2 -> new SmallVehicleContainerMenu(pContainerId, pPlayerInventory, this.m_19879_());
                case 3 -> new MediumVehicleContainerMenu(pContainerId, pPlayerInventory, this.m_19879_());
                case 4 -> new LargeVehicleContainerMenu(pContainerId, pPlayerInventory, this.m_19879_());
                case 5 -> new HugeVehicleContainerMenu(pContainerId, pPlayerInventory, this.m_19879_());
                default -> null;
            };
        }
        return null;
    }

    private final List<Entity> generatePassengersList() {
        int n = this.getMaxPassengers();
        ArrayList<Object> arrayList = new ArrayList<Object>(n);
        int n2 = 0;
        while (n2 < n) {
            int n3;
            int n4 = n3 = n2++;
            ArrayList<Object> arrayList2 = arrayList;
            boolean bl = false;
            arrayList2.add(null);
        }
        return arrayList;
    }

    protected final void initSeatData(int targetSize) {
        this.padList(this.orderedPassengers, targetSize, null, null);
    }

    protected final <T> void padList(@NotNull List<T> list, int targetSize, @Nullable T defaultValue, @Nullable Consumer<T> onRemove) {
        Intrinsics.checkNotNullParameter(list, (String)"list");
        while (targetSize != list.size()) {
            if (targetSize > list.size()) {
                list.add(defaultValue);
                continue;
            }
            Object last = CollectionsKt.removeLast(list);
            if (last == null || onRemove == null) continue;
            onRemove.accept(last);
        }
    }

    protected final void checkSeatsSize() {
        int targetSize = this.computed().seats().size();
        if (targetSize == this.orderedPassengers.size()) {
            return;
        }
        this.initSeatData(targetSize);
    }

    @NotNull
    public List<Entity> getOrderedPassengers() {
        this.checkSeatsSize();
        return this.orderedPassengers;
    }

    @Nullable
    public Function<Entity, Integer> getEntityIndexOverride() {
        return this.entityIndexOverride;
    }

    public void setEntityIndexOverride(@Nullable Function<Entity, Integer> function) {
        this.entityIndexOverride = function;
    }

    /*
     * Unable to fully structure code
     */
    protected void m_20348_(@NotNull Entity pPassenger) {
        Intrinsics.checkNotNullParameter((Object)pPassenger, (String)"pPassenger");
        if (!(pPassenger.m_20202_() == this)) {
            $i$a$-check-VehicleEntity$addPassenger$1 = false;
            $i$a$-check-VehicleEntity$addPassenger$1 = "Use x.startRiding(y), not y.addPassenger(x)";
            throw new IllegalStateException($i$a$-check-VehicleEntity$addPassenger$1.toString());
        }
        this.checkSeatsSize();
        index = 0;
        indexOverride = this.getEntityIndexOverride();
        if (indexOverride == null) ** GOTO lbl-1000
        v0 = indexOverride.apply(pPassenger);
        var4_6 = -1;
        if (v0 == null || v0 != var4_6) {
            index = ((Number)indexOverride.apply(pPassenger)).intValue();
        } else lbl-1000:
        // 2 sources

        {
            index = 0;
            for (Entity passenger : this.orderedPassengers) {
                if (passenger == null) break;
                ++index;
            }
        }
        if (index >= this.getMaxPassengers() || index < 0) {
            return;
        }
        this.orderedPassengers.set(index, pPassenger);
        pPassenger.getPersistentData().m_128405_("SBWSeatIndex", index);
        this.f_19823_ = ImmutableList.copyOf((Collection)this.orderedPassengers.stream().filter((Predicate<Object>)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Z, addPassenger$lambda$13(kotlin.jvm.functions.Function1 java.lang.Object ), (Ljava/lang/Object;)Z)((Function1)(Function1)LambdaMetafactory.metafactory(null, null, null, (Ljava/lang/Object;)Ljava/lang/Object;, addPassenger$lambda$12(net.minecraft.world.entity.Entity ), (Lnet/minecraft/world/entity/Entity;)Ljava/lang/Boolean;)())).toList());
        this.m_146852_(GameEvent.f_268500_, pPassenger);
        this.setChanged();
        if (!this.m_9236_().f_46443_) {
            this.updateBackupAmmoCount();
        }
    }

    protected void m_20351_(@NotNull Entity pPassenger) {
        Intrinsics.checkNotNullParameter((Object)pPassenger, (String)"pPassenger");
        if (!(pPassenger.m_20202_() != this)) {
            boolean bl = false;
            String string = "Use x.stopRiding(y), not y.removePassenger(x)";
            throw new IllegalStateException(string.toString());
        }
        this.checkSeatsSize();
        int index = this.getSeatIndex(pPassenger);
        if (index == -1) {
            return;
        }
        this.orderedPassengers.set(index, null);
        this.f_19823_ = ImmutableList.copyOf((Collection)this.orderedPassengers.stream().filter(arg_0 -> VehicleEntity.removePassenger$lambda$16(VehicleEntity::removePassenger$lambda$15, arg_0)).toList());
        pPassenger.f_19851_ = 60;
        this.m_146852_(GameEvent.f_268533_, pPassenger);
    }

    @NotNull
    public VehicleData data() {
        VehicleData d = this.vehicleDataStrong;
        if (d == null) {
            this.vehicleDataStrong = d = VehicleData.from(this);
        }
        return d;
    }

    @NotNull
    public DefaultVehicleData computed() {
        DefaultVehicleData defaultVehicleData = this.data().compute();
        Intrinsics.checkNotNullExpressionValue((Object)defaultVehicleData, (String)"compute(...)");
        return defaultVehicleData;
    }

    public float getStepHeight() {
        return this.computed().getUpStep();
    }

    @Nullable
    public Entity m_146895_() {
        this.checkSeatsSize();
        if (this.orderedPassengers.isEmpty()) {
            return null;
        }
        return (Entity)CollectionsKt.firstOrNull(this.orderedPassengers);
    }

    @Nullable
    public Entity getNthEntity(int index) {
        this.checkSeatsSize();
        if (index >= this.orderedPassengers.size() || index < 0) {
            return null;
        }
        return this.orderedPassengers.get(index);
    }

    /*
     * WARNING - void declaration
     */
    public boolean changeSeat(@NotNull Entity entity, int index) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        if (index < 0 || index >= this.getMaxPassengers()) {
            return false;
        }
        this.checkSeatsSize();
        if (this.orderedPassengers.get(index) != null) {
            return false;
        }
        if (!this.orderedPassengers.contains(entity)) {
            return false;
        }
        this.orderedPassengers.set(this.orderedPassengers.indexOf(entity), null);
        this.orderedPassengers.set(index, entity);
        entity.getPersistentData().m_128405_(TAG_SEAT_INDEX, index);
        Level level = this.m_9236_();
        if (level instanceof ServerLevel) {
            void $this$forEach$iv;
            List list = ((ServerLevel)level).m_8795_(arg_0 -> VehicleEntity.changeSeat$lambda$18(VehicleEntity::changeSeat$lambda$17, arg_0));
            Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getPlayers(...)");
            Iterable iterable = list;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                ServerPlayer p = (ServerPlayer)element$iv;
                boolean bl = false;
                ServerPlayer serverPlayer = p;
                Intrinsics.checkNotNull((Object)serverPlayer);
                serverPlayer.f_8906_.m_9829_((Packet)new ClientboundSetPassengersPacket((Entity)this));
            }
        }
        return true;
    }

    public int getSeatIndex(@Nullable Entity entity) {
        this.checkSeatsSize();
        return this.orderedPassengers.indexOf(entity);
    }

    public int getTagSeatIndex(@NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return entity.getPersistentData().m_128451_(TAG_SEAT_INDEX);
    }

    @NotNull
    public Vec3 getThirdPersonCameraPosition() {
        Vec3 pos = this.computed().getThirdPersonCameraPos();
        return new Vec3(pos.f_82481_ + ClientMouseHandler.custom3pDistanceLerp, pos.f_82480_, pos.f_82479_);
    }

    public float getRoll(float tickDelta) {
        return Mth.m_14179_((float)tickDelta, (float)this.getPrevRoll(), (float)this.getRoll());
    }

    public float getYaw(float tickDelta) {
        return Mth.m_14179_((float)tickDelta, (float)this.f_19859_, (float)this.m_146908_());
    }

    public float getPitch(float tickDelta) {
        return Mth.m_14179_((float)tickDelta, (float)this.f_19860_, (float)this.m_146909_());
    }

    public void setZRot(float rot) {
        this.setRoll(rot);
    }

    public void turretTurnSound(float diffX, float diffY, float pitch) {
        if (this instanceof MortarEntity) {
            return;
        }
        if (this.m_9236_().f_46443_ && ((double)org.joml.Math.abs((float)diffY) > 0.5 || (double)org.joml.Math.abs((float)diffX) > 0.5)) {
            this.m_9236_().m_7785_(this.m_20185_(), this.m_20186_() + (double)this.m_20206_() * 0.5, this.m_20189_(), (SoundEvent)ModSounds.TURRET_TURN.get(), this.m_5720_(), (float)Math.min(0.15 * (double)Math.max(Mth.m_14154_((float)diffX), Mth.m_14154_((float)diffY)), 0.75), this.f_19796_.m_188501_() * 0.05f + pitch, false);
        }
    }

    public boolean shouldSendHitParticles() {
        return this.computed().getSendHitParticles();
    }

    public boolean shouldSendHitSounds() {
        return true;
    }

    @NotNull
    protected final SyncedEntityEnergyStorage getEnergyStorage() {
        SyncedEntityEnergyStorage syncedEntityEnergyStorage = this.energyStorage;
        if (syncedEntityEnergyStorage != null) {
            return syncedEntityEnergyStorage;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"energyStorage");
        return null;
    }

    protected final void setEnergyStorage(@NotNull SyncedEntityEnergyStorage syncedEntityEnergyStorage) {
        Intrinsics.checkNotNullParameter((Object)((Object)syncedEntityEnergyStorage), (String)"<set-?>");
        this.energyStorage = syncedEntityEnergyStorage;
    }

    @NotNull
    protected final LazyOptional<IEnergyStorage> getEnergyOptional() {
        return this.energyOptional;
    }

    protected final void setEnergyOptional(@NotNull LazyOptional<IEnergyStorage> lazyOptional) {
        Intrinsics.checkNotNullParameter(lazyOptional, (String)"<set-?>");
        this.energyOptional = lazyOptional;
    }

    public final boolean isInitialized() {
        return this.isInitialized;
    }

    protected final void setInitialized(boolean bl) {
        this.isInitialized = bl;
    }

    protected void m_8097_() {
        List<Integer> list;
        int n;
        int n2;
        SynchedEntityData $this$defineSynchedData_u24lambda_u2424 = this.f_19804_;
        boolean bl = false;
        $this$defineSynchedData_u24lambda_u2424.m_135372_(OVERRIDE, (Object)"");
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SKIN_ID, (Object)"");
        $this$defineSynchedData_u24lambda_u2424.m_135372_(HEALTH, (Object)Float.valueOf(this.getMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LAST_ATTACKER_UUID, (Object)"undefined");
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LAST_DRIVER_UUID, (Object)"undefined");
        int n3 = 16;
        Object object = DOG_TAG_ICON;
        SynchedEntityData synchedEntityData = $this$defineSynchedData_u24lambda_u2424;
        ArrayList<List> arrayList = new ArrayList<List>(n3);
        for (n2 = 0; n2 < n3; ++n2) {
            int n4 = n = n2;
            list = arrayList;
            boolean bl2 = false;
            int n5 = 16;
            ArrayList<Short> arrayList2 = new ArrayList<Short>(n5);
            int n6 = 0;
            while (n6 < n5) {
                int n7;
                int n8 = n7 = n6++;
                ArrayList<Short> arrayList3 = arrayList2;
                boolean bl3 = false;
                arrayList3.add((short)-1);
            }
            ((ArrayList)list).add((Integer)((Object)arrayList2));
        }
        list = arrayList;
        synchedEntityData.m_135372_(object, list);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(GUN_DATA_MAP, (Object)MapsKt.emptyMap());
        $this$defineSynchedData_u24lambda_u2424.m_135372_(AI_TURRET_TARGET_UUID, (Object)"undefined");
        $this$defineSynchedData_u24lambda_u2424.m_135372_(AI_PASSENGER_WEAPON_TARGET_UUID, (Object)"undefined");
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DELTA_ROT, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(MOUSE_SPEED_X, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(MOUSE_SPEED_Y, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TURRET_HEALTH, (Object)Float.valueOf(this.getTurretMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(L_WHEEL_HEALTH, (Object)Float.valueOf(this.getLeftWheelMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(R_WHEEL_HEALTH, (Object)Float.valueOf(this.getRightWheelMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(MAIN_ENGINE_HEALTH, (Object)Float.valueOf(this.getMainEngineMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SUB_ENGINE_HEALTH, (Object)Float.valueOf(this.getSubEngineMaxHealth()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TURRET_DAMAGED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(L_WHEEL_DAMAGED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(R_WHEEL_DAMAGED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(MAIN_ENGINE_DAMAGED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SUB_ENGINE_DAMAGED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(CANNON_RECOIL_TIME, (Object)0);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(CANNON_RECOIL_FORCE, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(POWER, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(YAW_WHILE_SHOOT, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SERVER_YAW, (Object)Float.valueOf(this.m_146908_()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SERVER_PITCH, (Object)Float.valueOf(this.m_146909_()));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DECOY_COUNT, (Object)0);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DECOY_RELOAD_COOLDOWN, (Object)this.getDecoyReloadTime());
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DECOY_ITEM_COUNT, (Object)0);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SYNCHED_GEAR_ROT, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(GEAR_UP, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(FORWARD_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(BACK_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LEFT_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(RIGHT_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(UP_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DOWN_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(FIRE_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(DECOY_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SPRINT_INPUT_DOWN, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(PLANE_BREAK, (Object)Float.valueOf(0.0f));
        n3 = this.getMaxPassengers();
        object = SELECTED_WEAPON;
        synchedEntityData = $this$defineSynchedData_u24lambda_u2424;
        arrayList = new ArrayList(n3);
        n2 = 0;
        while (n2 < n3) {
            int it = n = n2++;
            list = arrayList;
            boolean bl4 = false;
            ((ArrayList)list).add(0);
        }
        list = arrayList;
        synchedEntityData.m_135372_(object, list);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(ENERGY, (Object)0);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SYNCHED_PROPELLER_ROT, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(PROPELLER_ROT, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(HORN_VOLUME, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LASER_LENGTH, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LASER_SCALE, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LASER_SCALE_O, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(CHARGE_PROGRESS, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LIFT_OFFSET, (Object)Float.valueOf(0.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(IS_WRECK, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(SYMPATHETIC_DETONATED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TURRET_BURNED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(HOVER_MODE, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TURRET_BURN_TIMER, (Object)0);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LOCKED, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LOITER_PARAMS, (Object)new Quaternionf(0.0f, 318.0f, 0.0f, 400.0f));
        $this$defineSynchedData_u24lambda_u2424.m_135372_(LOITER_ACTIVE, (Object)false);
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TOWING_UUIDS, (Object)new CompoundTag());
        $this$defineSynchedData_u24lambda_u2424.m_135372_(TOWED_BY_UUID, (Object)"");
    }

    public void consumeEnergy(int amount) {
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to consume energy of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
            return;
        }
        if (this.m_9236_() instanceof ServerLevel) {
            this.getEnergyStorage().extractEnergy(amount, false);
        }
    }

    protected final boolean canConsume(int amount) {
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to check if can consume energy of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
            return false;
        }
        return this.getEnergy() >= amount;
    }

    public int getEnergy() {
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to get energy of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
            return Integer.MAX_VALUE;
        }
        return this.getEnergyStorage().getEnergyStored();
    }

    public void setEnergy(int pEnergy) {
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to set energy of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
            return;
        }
        int targetEnergy = Mth.m_14045_((int)pEnergy, (int)0, (int)this.getMaxEnergy());
        int n = targetEnergy > this.getEnergyStorage().getEnergyStored() ? this.getEnergyStorage().receiveEnergy(targetEnergy - this.getEnergyStorage().getEnergyStored(), false) : this.getEnergyStorage().extractEnergy(this.getEnergyStorage().getEnergyStored() - targetEnergy, false);
    }

    @Nullable
    public IEnergyStorage getEnergyStorage() {
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to get energy storage of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
        }
        return (IEnergyStorage)this.getEnergyStorage();
    }

    public int getMaxEnergy() {
        int n;
        if (!this.hasEnergyStorage()) {
            Mod.LOGGER.warn("Trying to get max energy of vehicle {}, but it has no energy storage", (Object)this.m_7755_());
            n = Integer.MAX_VALUE;
        } else {
            n = this.computed().getMaxEnergy();
        }
        return n;
    }

    public boolean hasEnergyStorage() {
        return this.computed().getMaxEnergy() > 0;
    }

    public boolean canShoot(@Nullable LivingEntity living) {
        GunData gunData = this.getGunData(this.getSeatIndex((Entity)living));
        return gunData != null && gunData.canShoot(this.getAmmoSupplier());
    }

    public int vehicleWeaponRpm(@Nullable LivingEntity living) {
        GunData data = this.getGunData(this.getSeatIndex((Entity)living));
        if (data == null || ((Number)data.get(GunProp.RPM)).intValue() <= 0) {
            return 60;
        }
        return ((Number)data.get(GunProp.RPM)).intValue();
    }

    public int vehicleWeaponRpm(int seatIndex) {
        GunData data = this.getGunData(seatIndex);
        if (data == null || ((Number)data.get(GunProp.RPM)).intValue() <= 0) {
            return 60;
        }
        return ((Number)data.get(GunProp.RPM)).intValue();
    }

    public int vehicleWeaponRpm(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 1;
        }
        GunData data = gunData;
        return RangesKt.coerceAtLeast((int)((Number)data.get(GunProp.RPM)).intValue(), (int)1);
    }

    public int getWeaponHeat(@Nullable LivingEntity living) {
        GunData gunData = this.getGunData(this.getSeatIndex((Entity)living));
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return (int)org.joml.Math.round((double)gunData2.heat.get());
    }

    public int getWeaponHeat(int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return (int)org.joml.Math.round((double)gunData2.heat.get());
    }

    public int getWeaponHeat(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return (int)org.joml.Math.round((double)gunData2.heat.get());
    }

    public int getWeaponHeat(int seatIndex, int weaponIndex) {
        GunData gunData = this.getGunData(seatIndex, weaponIndex);
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return (int)org.joml.Math.round((double)gunData2.heat.get());
    }

    public int getShootAnimationTimer(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return gunData2.shootAnimationTimer.get();
    }

    public int getShootAnimationTimer(int seatIndex, int weaponIndex) {
        GunData gunData = this.getGunData(seatIndex, weaponIndex);
        if (gunData == null) {
            return 0;
        }
        GunData gunData2 = gunData;
        return gunData2.shootAnimationTimer.get();
    }

    public void vehicleShoot(@Nullable LivingEntity living, @NotNull String weaponName, @Nullable Vec3 targetPos) {
        GunData gunData;
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        if (this.isWreck()) {
            return;
        }
        GunData gunData2 = gunData = this.getGunData(weaponName);
        Intrinsics.checkNotNull((Object)gunData2);
        Mod.Companion.queueServerWork(((Number)gunData2.get(GunProp.SHOOT_DELAY_TIME)).intValue(), () -> VehicleEntity.vehicleShoot$lambda$26(this, weaponName, living, targetPos));
        this.afterShoot(gunData, this.getShootVec(weaponName, 1.0f));
        this.playShootSound3p(living, weaponName);
        if (living != null) {
            ShootPos shootPos = gunData.get(GunProp.SHOOT_POS);
            ArrayList<Vec3> list = shootPos.getPositions();
            int size = list.size();
            int index = shootPos.getBoundUpWithAmmoAmount() ? Mth.m_14045_((int)(gunData.ammo.get() - 1), (int)0, (int)size) : gunData.fireIndex.get() % size;
            UUID uUID = living.m_20148_();
            Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
            UUID uUID2 = this.f_19820_;
            Intrinsics.checkNotNullExpressionValue((Object)uUID2, (String)"uuid");
            MinecraftUtil.sendPacketToAll(new VehicleShootClientMessage(uUID, uUID2, index, weaponName));
        }
    }

    public void vehicleShoot(@Nullable LivingEntity living, @NotNull String weaponName, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        GunData gunData;
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        if (this.isWreck()) {
            return;
        }
        GunData gunData2 = gunData = this.getGunData(weaponName);
        Intrinsics.checkNotNull((Object)gunData2);
        Mod.Companion.queueServerWork(((Number)gunData2.get(GunProp.SHOOT_DELAY_TIME)).intValue(), () -> VehicleEntity.vehicleShoot$lambda$28(this, weaponName, living, uuid, targetPos));
        this.afterShoot(gunData, this.getShootVec(weaponName, 1.0f));
        this.playShootSound3p(living, weaponName);
        if (living != null) {
            ShootPos shootPos = gunData.get(GunProp.SHOOT_POS);
            ArrayList<Vec3> list = shootPos.getPositions();
            int size = list.size();
            int index = shootPos.getBoundUpWithAmmoAmount() ? Mth.m_14045_((int)(gunData.ammo.get() - 1), (int)0, (int)size) : gunData.fireIndex.get() % size;
            UUID uUID = living.m_20148_();
            Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
            UUID uUID2 = this.f_19820_;
            Intrinsics.checkNotNullExpressionValue((Object)uUID2, (String)"uuid");
            MinecraftUtil.sendPacketToAll(new VehicleShootClientMessage(uUID, uUID2, index, weaponName));
        }
    }

    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        GunData gunData;
        if (this.isWreck()) {
            return;
        }
        int seatIndex = this.getSeatIndex((Entity)living);
        GunData gunData2 = gunData = this.getGunData(seatIndex);
        Intrinsics.checkNotNull((Object)gunData2);
        Mod.Companion.queueServerWork(((Number)gunData2.get(GunProp.SHOOT_DELAY_TIME)).intValue(), () -> VehicleEntity.vehicleShoot$lambda$30(this, seatIndex, living, uuid, targetPos));
        this.afterShoot(gunData, this.getShootVec((Entity)living, 1.0f));
        this.playShootSound3p(living, seatIndex);
        if (living != null) {
            ShootPos shootPos = gunData.get(GunProp.SHOOT_POS);
            ArrayList<Vec3> list = shootPos.getPositions();
            int size = list.size();
            int index = shootPos.getBoundUpWithAmmoAmount() ? Mth.m_14045_((int)(gunData.ammo.get() - 1), (int)0, (int)size) : gunData.fireIndex.get() % size;
            UUID uUID = living.m_20148_();
            Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
            UUID uUID2 = this.f_19820_;
            Intrinsics.checkNotNullExpressionValue((Object)uUID2, (String)"uuid");
            String string = this.getGunName(seatIndex);
            if (string == null) {
                string = "";
            }
            MinecraftUtil.sendPacketToAll(new VehicleShootClientMessage(uUID, uUID2, index, string));
        }
    }

    public void afterShoot(@Nullable GunData gunData, @NotNull Vec3 shootVec) {
        int recoilTime;
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        if (gunData != null && (recoilTime = ((Number)gunData.get(GunProp.RECOIL_TIME)).intValue()) > 0) {
            if (recoilTime > this.getCannonRecoilTime()) {
                this.setCannonRecoilTime(recoilTime);
            }
            Vec3 vec3 = this.m_20252_(1.0f);
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getViewVector(...)");
            float angle = (float)Mth.m_14175_((double)(-VehicleVecUtils.getYRotFromVector(vec3) + VehicleVecUtils.getYRotFromVector(shootVec)));
            Vec3 vo = new Vec3(0.0, 0.0, 1.0);
            double f = 0.3 * (double)this.getCannonRecoilForce() * (double)(this.getCannonRecoilTime() / recoilTime);
            Vec3 v1 = vo.m_82524_(this.getYawWhileShoot() * ((float)Math.PI / 180)).m_82490_(f);
            Vec3 v2 = vo.m_82524_(angle * ((float)Math.PI / 180)).m_82490_((double)((Number)gunData.get(GunProp.RECOIL_FORCE)).floatValue());
            Vec3 v3 = v1.m_82549_(v2);
            double d = -VehicleVecUtils.getYRotFromVector(vo);
            Intrinsics.checkNotNull((Object)v3);
            this.setYawWhileShoot((float)Mth.m_14175_((double)(d + VehicleVecUtils.getYRotFromVector(v3))));
            this.setCannonRecoilForce((float)v3.m_82553_());
            gunData.shakePlayers(this);
        }
    }

    public void playShootSound3p(@Nullable LivingEntity living, @NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return;
        }
        GunData gunData2 = gunData;
        Vec3 pos = this.getShootPos(weaponName, 1.0f);
        this.playShootSound3p(living, gunData2, pos);
    }

    public void playShootSound3p(@Nullable LivingEntity living, int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return;
        }
        GunData gunData2 = gunData;
        Vec3 pos = this.getShootPos((Entity)living, 1.0f);
        this.playShootSound3p(living, gunData2, pos);
    }

    public void playShootSound3p(@Nullable LivingEntity living, @Nullable GunData gunData, @Nullable Vec3 pos) {
        block7: {
            SoundEvent fire3PVeryFar;
            SoundEvent fire3PFar;
            GunData shootGunData;
            Level level = this.m_9236_();
            ServerLevel serverLevel = level instanceof ServerLevel ? (ServerLevel)level : null;
            if (serverLevel == null) {
                return;
            }
            ServerLevel serverLevel2 = serverLevel;
            if (gunData == null) {
                return;
            }
            SoundInfo soundInfo = gunData.get(GunProp.SOUND_INFO);
            float pitch = this.getWeaponHeat(living) <= 60 ? 1.0f : (float)(1.0 - 0.011 * (double)Math.abs(60 - this.getWeaponHeat(living)));
            Entity listener = null;
            listener = living != null && (living.m_20202_() != this || living.m_20202_() == null) ? null : (Entity)((shootGunData = this.getGunData((Entity)living)) != null && Intrinsics.areEqual((Object)shootGunData, (Object)gunData) ? living : null);
            double soundRadius = ((Number)gunData.get(GunProp.SOUND_RADIUS)).doubleValue();
            SoundEvent fire3P = soundInfo.fire3P;
            if (fire3P != null) {
                Vec3 vec3 = pos;
                if (vec3 != null) {
                    Vec3 it = vec3;
                    boolean bl = false;
                    SoundTool.playDistantSound(serverLevel2, fire3P, it, (float)(soundRadius * (double)0.4f), pitch, listener);
                }
            }
            if ((fire3PFar = soundInfo.fire3PFar) != null) {
                Vec3 vec3 = pos;
                if (vec3 != null) {
                    Vec3 it = vec3;
                    boolean bl = false;
                    SoundTool.playDistantSound(serverLevel2, fire3PFar, it, (float)(soundRadius * (double)0.7f), pitch, listener);
                }
            }
            if ((fire3PVeryFar = soundInfo.getFire3PVeryFar()) == null) break block7;
            Vec3 vec3 = pos;
            if (vec3 != null) {
                Vec3 it = vec3;
                boolean bl = false;
                SoundTool.playDistantSound(serverLevel2, fire3PVeryFar, it, (float)soundRadius, pitch, listener);
            }
        }
    }

    public int getWeaponIndex(int seatIndex) {
        Integer n;
        List<Integer> list = this.getSelectedWeapon();
        boolean bl = 0 <= seatIndex ? seatIndex < list.size() : false;
        if (bl) {
            n = list.get(seatIndex);
        } else {
            int it = seatIndex;
            boolean bl2 = false;
            n = -1;
        }
        return ((Number)n).intValue();
    }

    public boolean hasWeapon() {
        return this.computed().seats().stream().filter(arg_0 -> VehicleEntity.hasWeapon$lambda$36(VehicleEntity::hasWeapon$lambda$35, arg_0)).flatMap(arg_0 -> VehicleEntity.hasWeapon$lambda$38(VehicleEntity::hasWeapon$lambda$37, arg_0)).filter(arg_0 -> VehicleEntity.hasWeapon$lambda$40(VehicleEntity::hasWeapon$lambda$39, arg_0)).anyMatch(arg_0 -> VehicleEntity.hasWeapon$lambda$42(arg_0 -> VehicleEntity.hasWeapon$lambda$41(this, arg_0), arg_0));
    }

    public boolean hasWeapon(int seatIndex) {
        if (seatIndex < 0 || seatIndex >= this.getMaxPassengers()) {
            return false;
        }
        return this.getGunData(seatIndex) != null;
    }

    public void setWeaponIndex(int seatIndex, int selectedWeaponIndex) {
        Integer n = (Integer)CollectionsKt.getOrNull(this.getSelectedWeapon(), (int)seatIndex);
        if (n == null) {
            return;
        }
        int oldIndex = n;
        if (oldIndex == selectedWeaponIndex) {
            return;
        }
        this.modifyGunData(seatIndex, oldIndex, arg_0 -> VehicleEntity.setWeaponIndex$lambda$43(this, arg_0));
        List newList = CollectionsKt.toMutableList((Collection)this.getSelectedWeapon());
        newList.set(seatIndex, selectedWeaponIndex);
        this.setSelectedWeapon(newList);
        if (!this.m_9236_().f_46443_) {
            this.updateBackupAmmoCount();
        }
    }

    public void changeWeapon(int seatIndex, int value, boolean isScroll) {
        if (seatIndex < 0 || seatIndex >= this.getMaxPassengers()) {
            return;
        }
        List<String> weapons = this.computed().seats().get(seatIndex).weapons();
        if (weapons.isEmpty()) {
            return;
        }
        int count = weapons.size();
        int currentIndex = this.getWeaponIndex(seatIndex);
        int typeIndex = Mth.m_14045_((int)(isScroll ? (value + currentIndex + count) % count : value), (int)0, (int)(count - 1));
        if (typeIndex == currentIndex) {
            return;
        }
        GunData gunData = this.getGunData(weapons.get(typeIndex));
        if (gunData == null) {
            return;
        }
        GunData weapon = gunData;
        this.setWeaponIndex(seatIndex, typeIndex);
        SoundEvent sound = weapon.get(GunProp.SOUND_INFO).getChange();
        if (sound != null) {
            this.m_9236_().m_6269_(null, (Entity)this, sound, this.m_5720_(), 1.0f, 1.0f);
        }
    }

    @NotNull
    public Packet<ClientGamePacketListener> m_5654_() {
        Packet packet = NetworkHooks.getEntitySpawningPacket((Entity)this);
        Intrinsics.checkNotNullExpressionValue((Object)packet, (String)"getEntitySpawningPacket(...)");
        return packet;
    }

    public void writeSpawnData(@NotNull FriendlyByteBuf buffer) {
        Intrinsics.checkNotNullParameter((Object)buffer, (String)"buffer");
        buffer.writeFloat(this.getTurretYRot());
        buffer.writeFloat(this.getTurretXRot());
        buffer.writeFloat(this.getGunYRot());
        buffer.writeFloat(this.getGunXRot());
        buffer.writeFloat(this.getTurretYRotLock());
        buffer.writeFloat(this.getSynchedGearRot());
        buffer.writeBoolean(this.getGearUp());
        buffer.writeBoolean(this.getHoverMode());
        buffer.writeBoolean(this.getLoiterActive());
    }

    public void readSpawnData(@NotNull FriendlyByteBuf additionalData) {
        Intrinsics.checkNotNullParameter((Object)additionalData, (String)"additionalData");
        this.setTurretYRot(additionalData.readFloat());
        this.setTurretXRot(additionalData.readFloat());
        this.setGunYRot(additionalData.readFloat());
        this.setGunXRot(additionalData.readFloat());
        this.setTurretYRotLock(additionalData.readFloat());
        this.setSynchedGearRot(additionalData.readFloat());
        this.setGearUp(additionalData.readBoolean());
        this.setHoverMode(additionalData.readBoolean());
        this.setLoiterActive(additionalData.readBoolean());
        this.setTurretYRotO(this.getTurretYRot());
        this.setTurretXRotO(this.getTurretXRot());
        this.setGunYRotO(this.getGunYRot());
        this.setGunXRotO(this.getGunXRot());
    }

    /*
     * WARNING - void declaration
     */
    protected void m_7378_(@NotNull CompoundTag compound) {
        List list;
        int n;
        Intrinsics.checkNotNullParameter((Object)compound, (String)"compound");
        VehicleData.from(this).update();
        this.setOverride(compound.m_128461_("Override"));
        this.setSkinId(compound.m_128461_("SkinId"));
        CompoundTag state = compound.m_128469_("WeaponState");
        Map newMap = new LinkedHashMap();
        for (String key : state.m_128431_()) {
            CompoundTag tag = state.m_128469_(key).m_6426_();
            tag.m_128359_("id", "superbwarfare:vehicle_gun");
            tag.m_128405_("Count", 1);
            Map map = newMap;
            ItemStack itemStack = ItemStack.m_41712_((CompoundTag)tag);
            Intrinsics.checkNotNullExpressionValue((Object)itemStack, (String)"of(...)");
            GunData gunData = GunData.Companion.from$default(GunData.Companion, itemStack, null, 2, null);
            map.put(key, gunData);
        }
        this.setGunDataMap(newMap);
        this.setHealth(compound.m_128441_("Health") ? compound.m_128457_("Health") : this.getMaxHealth());
        this.setTurretHealth(compound.m_128441_("TurretHealth") ? compound.m_128457_("TurretHealth") : this.getTurretMaxHealth());
        this.setLeftWheelHealth(compound.m_128441_("LeftWheelHealth") ? compound.m_128457_("LeftWheelHealth") : this.getLeftWheelMaxHealth());
        this.setRightWheelHealth(compound.m_128441_("RightWheelHealth") ? compound.m_128457_("RightWheelHealth") : this.getRightWheelMaxHealth());
        this.setMainEngineHealth(compound.m_128441_("MainEngineHealth") ? compound.m_128457_("MainEngineHealth") : this.getMainEngineMaxHealth());
        this.setSubEngineHealth(compound.m_128441_("SubEngineHealth") ? compound.m_128457_("SubEngineHealth") : this.getSubEngineMaxHealth());
        this.setTurretDamaged(compound.m_128471_("TurretDamaged"));
        this.setLeftWheelDamaged(compound.m_128471_("LeftWheelDamaged"));
        this.setRightWheelDamaged(compound.m_128471_("RightWheelDamaged"));
        this.setMainEngineDamaged(compound.m_128471_("MainEngineDamaged"));
        this.setSubEngineDamaged(compound.m_128471_("SubEngineDamaged"));
        this.setPower(compound.m_128457_("Power"));
        this.setDecoyCount(compound.m_128451_("DecoyCount"));
        this.setDecoyReloadCoolDown(compound.m_128451_("DecoyReloadCoolDown"));
        this.setSynchedGearRot(compound.m_128457_("GearRot"));
        this.setGearUp(compound.m_128471_("GearUp"));
        this.setPropellerRot(compound.m_128457_("PropellerRot"));
        this.setChargeProgress(compound.m_128457_("ChargeProgress"));
        this.setLastAttackerUUID(compound.m_128461_("LastAttacker"));
        this.setLastDriverUUID(compound.m_128461_("LastDriver"));
        Tag dogTagTag = compound.m_128423_("DogTagIcon");
        List list2 = new ArrayList();
        if (dogTagTag instanceof ListTag) {
            Iterable $this$forEach$iv = (Iterable)dogTagTag;
            boolean $i$f$forEach = false;
            for (Object element$iv : $this$forEach$iv) {
                Tag it = (Tag)element$iv;
                n = 0;
                List sl = new ArrayList();
                if (it instanceof IntArrayTag) {
                    void $this$mapTo$iv$iv;
                    void $this$map$iv;
                    Intrinsics.checkNotNullExpressionValue((Object)((IntArrayTag)it).m_128648_(), (String)"getAsIntArray(...)");
                    List list3 = sl;
                    boolean $i$f$map = false;
                    void var16_26 = $this$map$iv;
                    Collection destination$iv$iv = new ArrayList(((void)$this$map$iv).length);
                    boolean $i$f$mapTo = false;
                    int n2 = ((void)$this$mapTo$iv$iv).length;
                    for (int i = 0; i < n2; ++i) {
                        void num;
                        void item$iv$iv;
                        void var22_32 = item$iv$iv = $this$mapTo$iv$iv[i];
                        Collection collection = destination$iv$iv;
                        boolean bl = false;
                        collection.add((short)num);
                    }
                    list3.addAll((List)destination$iv$iv);
                }
                list2.add(sl);
            }
        }
        this.setDogTagIcon(list2);
        this.setServerYaw(compound.m_128457_("ServerYaw"));
        this.setServerPitch(compound.m_128457_("ServerPitch"));
        if (compound.m_128441_("TurretYRot")) {
            this.setTurretYRot(compound.m_128457_("TurretYRot"));
            this.setTurretXRot(compound.m_128457_("TurretXRot"));
            this.setGunYRot(compound.m_128457_("GunYRot"));
            this.setGunXRot(compound.m_128457_("GunXRot"));
            this.setTurretYRotLock(compound.m_128457_("TurretYRotLock"));
            this.setTurretYRotO(this.getTurretYRot());
            this.setTurretXRotO(this.getTurretXRot());
            this.setGunYRotO(this.getGunYRot());
            this.setGunXRotO(this.getGunXRot());
        }
        if (compound.m_128441_("HoverMode")) {
            this.setHoverMode(compound.m_128471_("HoverMode"));
        }
        this.setWreck(compound.m_128471_("IsWreck"));
        this.setSympatheticDetonated(compound.m_128471_("SympatheticDetonated"));
        this.setTurretBurned(compound.m_128471_("TurretBurned"));
        this.setTurretBurnTimer(compound.m_128451_("TurretBurnTimer"));
        Tag selectedWeaponTag = compound.m_128423_("SelectedWeapon");
        int[] selected = selectedWeaponTag instanceof IntArrayTag ? ((IntArrayTag)selectedWeaponTag).m_128648_() : new int[this.getMaxPassengers()];
        VehicleEntity vehicleEntity = this;
        if (selected.length != this.getMaxPassengers()) {
            Object element$iv;
            int n3 = this.getMaxPassengers();
            VehicleEntity vehicleEntity2 = vehicleEntity;
            element$iv = new ArrayList(n3);
            int it = 0;
            while (it < n3) {
                int sl = n = it++;
                Object object = element$iv;
                boolean bl = false;
                ((ArrayList)object).add(0);
            }
            list = (List)element$iv;
            vehicleEntity = vehicleEntity2;
        } else {
            Intrinsics.checkNotNull((Object)selected);
            list = ArraysKt.toMutableList((int[])selected);
        }
        vehicleEntity.setSelectedWeapon(list);
        Tag energyNBT = compound.m_128423_("Energy");
        if (this.hasEnergyStorage() && energyNBT instanceof IntTag) {
            this.getEnergyStorage().deserializeNBT(energyNBT);
        }
        this.resizeItems();
        if (compound.m_128441_("Inventory")) {
            this.inventory.deserializeNBT(compound.m_128469_("Inventory"));
        } else {
            NonNullList items = NonNullList.m_122780_((int)this.getContainerSize(), (Object)ItemStack.f_41583_);
            ContainerHelper.m_18980_((CompoundTag)compound, (NonNullList)items);
            Intrinsics.checkNotNull((Object)items);
            this.inventory.setItems((NonNullList<ItemStack>)items);
        }
        this.setLocked(compound.m_128471_("Locked"));
        if (compound.m_128441_("LoiterX")) {
            this.setLoiterParams(new Quaternionf(compound.m_128457_("LoiterX"), compound.m_128457_("LoiterY"), compound.m_128457_("LoiterZ"), compound.m_128457_("LoiterR")));
        }
        if (compound.m_128441_("LoiterActive")) {
            this.setLoiterActive(compound.m_128471_("LoiterActive"));
        }
        if (compound.m_128441_("TowingUUIDs")) {
            ListTag listTag = compound.m_128437_("TowingUUIDs", 8);
            List list4 = new ArrayList();
            int n4 = ((Collection)listTag).size();
            for (int i = 0; i < n4; ++i) {
                String string = listTag.m_128778_(i);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
                list4.add(string);
            }
            this.setTowingUUIDs(list4);
        } else if (compound.m_128441_("TowingUUID")) {
            List list5;
            String oldUuid = compound.m_128461_("TowingUUID");
            Intrinsics.checkNotNull((Object)oldUuid);
            if (!StringsKt.isBlank((CharSequence)oldUuid)) {
                Object[] objectArray = new String[]{oldUuid};
                list5 = CollectionsKt.mutableListOf((Object[])objectArray);
            } else {
                list5 = new ArrayList();
            }
            this.setTowingUUIDs(list5);
        }
        this.setTowedByUUID(compound.m_128461_("TowedByUUID"));
    }

    /*
     * WARNING - void declaration
     */
    public void m_7380_(@NotNull CompoundTag compound) {
        String skinIdString;
        Intrinsics.checkNotNullParameter((Object)compound, (String)"compound");
        this.checkSeatsSize();
        compound.m_128350_("Health", this.getHealth());
        String overrideString = this.getOverride();
        if (!StringsKt.isBlank((CharSequence)overrideString)) {
            compound.m_128359_("Override", overrideString);
        }
        if (!StringsKt.isBlank((CharSequence)(skinIdString = this.getSkinId()))) {
            compound.m_128359_("SkinId", skinIdString);
        }
        compound.m_128359_("LastAttacker", this.getLastAttackerUUID());
        compound.m_128359_("LastDriver", this.getLastDriverUUID());
        ListTag listTag = new ListTag();
        Iterable $this$forEach$iv = this.getDogTagIcon();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            void $this$mapTo$iv$iv;
            void $this$map$iv;
            List it = (List)element$iv;
            boolean bl = false;
            short[] sArray = CollectionsKt.toShortArray((Collection)it);
            ListTag listTag2 = listTag;
            boolean $i$f$map = false;
            void var15_19 = $this$map$iv;
            Collection destination$iv$iv = new ArrayList(((void)$this$map$iv).length);
            boolean $i$f$mapTo = false;
            int n = ((void)$this$mapTo$iv$iv).length;
            for (int i = 0; i < n; ++i) {
                void num;
                void item$iv$iv;
                void var21_25 = item$iv$iv = $this$mapTo$iv$iv[i];
                Collection collection = destination$iv$iv;
                boolean bl2 = false;
                collection.add((int)num);
            }
            List list = (List)destination$iv$iv;
            listTag2.add((Object)new IntArrayTag(list));
        }
        compound.m_128365_("DogTagIcon", (Tag)listTag);
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<String, GunData> entry : this.getGunDataMap().entrySet()) {
            CompoundTag itemTag;
            String weaponName = entry.getKey();
            GunData gunData = entry.getValue();
            CompoundTag stackTag = gunData.stack.m_41739_(new CompoundTag());
            stackTag.m_128473_("id");
            stackTag.m_128473_("Count");
            if (stackTag.m_128425_("tag", 10) && (itemTag = stackTag.m_128469_("tag")).m_128425_("GunData", 10)) {
                CompoundTag gunTag = itemTag.m_128469_("GunData");
                gunTag.m_128473_("BackupAmmoCount");
            }
            if (stackTag.m_128456_()) continue;
            tag.m_128365_(weaponName, (Tag)stackTag);
        }
        if (!tag.m_128456_()) {
            compound.m_128365_("WeaponState", (Tag)tag);
        }
        compound.m_128350_("TurretHealth", this.getTurretHealth());
        compound.m_128350_("LeftWheelHealth", this.getLeftWheelHealth());
        compound.m_128350_("RightWheelHealth", this.getRightWheelHealth());
        compound.m_128350_("MainEngineHealth", this.getMainEngineHealth());
        compound.m_128350_("SubEngineHealth", this.getSubEngineHealth());
        compound.m_128379_("TurretDamaged", this.getTurretDamaged());
        compound.m_128379_("LeftWheelDamaged", this.getLeftWheelDamaged());
        compound.m_128379_("RightWheelDamaged", this.getRightWheelDamaged());
        compound.m_128379_("MainEngineDamaged", this.getMainEngineDamaged());
        compound.m_128379_("SubEngineDamaged", this.getSubEngineDamaged());
        compound.m_128350_("Power", this.getPower());
        compound.m_128405_("DecoyCount", this.getDecoyCount());
        compound.m_128405_("DecoyReloadCoolDown", this.getDecoyReloadCoolDown());
        compound.m_128350_("GearRot", this.getSynchedGearRot());
        compound.m_128379_("GearUp", this.getGearUp());
        compound.m_128350_("PropellerRot", this.getPropellerRot());
        compound.m_128350_("ChargeProgress", this.getChargeProgress());
        compound.m_128350_("ServerYaw", this.getServerYaw());
        compound.m_128350_("ServerPitch", this.getServerPitch());
        compound.m_128350_("TurretYRot", this.getTurretYRot());
        compound.m_128350_("TurretXRot", this.getTurretXRot());
        compound.m_128350_("GunYRot", this.getGunYRot());
        compound.m_128350_("GunXRot", this.getGunXRot());
        compound.m_128350_("TurretYRotLock", this.getTurretYRotLock());
        compound.m_128379_("HoverMode", this.getHoverMode());
        if (this.getMaxPassengers() > 0) {
            compound.m_128408_("SelectedWeapon", this.getSelectedWeapon());
        }
        if (this.hasEnergyStorage()) {
            compound.m_128365_("Energy", this.getEnergyStorage().serializeNBT());
        }
        compound.m_128379_("IsWreck", this.isWreck());
        compound.m_128379_("SympatheticDetonated", this.getSympatheticDetonated());
        compound.m_128379_("TurretBurned", this.getTurretBurned());
        compound.m_128405_("TurretBurnTimer", this.getTurretBurnTimer());
        this.resizeItems();
        compound.m_128365_("Inventory", (Tag)this.inventory.serializeNBT());
        compound.m_128379_("Locked", this.getLocked());
        Quaternionf lp = this.getLoiterParams();
        compound.m_128350_("LoiterX", lp.x());
        compound.m_128350_("LoiterY", lp.y());
        compound.m_128350_("LoiterZ", lp.z());
        compound.m_128350_("LoiterR", lp.w());
        compound.m_128379_("LoiterActive", this.getLoiterActive());
        ListTag listTag3 = new ListTag();
        for (String uuid : this.getTowingUUIDs()) {
            listTag3.add((Object)StringTag.m_129297_((String)uuid));
        }
        compound.m_128365_("TowingUUIDs", (Tag)listTag3);
        compound.m_128359_("TowingUUID", this.getTowingUUID());
        compound.m_128359_("TowedByUUID", this.getTowedByUUID());
    }

    @Override
    public void buildBvrSyncNbt(@NotNull CompoundTag tag) {
        Intrinsics.checkNotNullParameter((Object)tag, (String)"tag");
        String string = this.m_20078_();
        if (string == null) {
            return;
        }
        String encodeId = string;
        tag.m_128359_("id", encodeId);
        tag.m_128405_("EntityId", this.m_19879_());
        tag.m_128347_("PosX", this.m_20185_());
        tag.m_128347_("PosY", this.m_20186_());
        tag.m_128347_("PosZ", this.m_20189_());
        tag.m_128347_("MotionX", this.m_20184_().f_82479_);
        tag.m_128347_("MotionY", this.m_20184_().f_82480_);
        tag.m_128347_("MotionZ", this.m_20184_().f_82481_);
        tag.m_128350_("Yaw", this.m_146908_());
        tag.m_128350_("Pitch", this.m_146909_());
        tag.m_128350_("TurretYRot", this.getTurretYRot());
        tag.m_128350_("TurretXRot", this.getTurretXRot());
        tag.m_128350_("GunYRot", this.getGunYRot());
        tag.m_128350_("GunXRot", this.getGunXRot());
        tag.m_128350_("Health", this.getHealth());
        tag.m_128379_("IsWreck", this.isWreck());
        tag.m_128379_("EngineRunning", this.engineRunning());
        tag.m_128350_("LaserScale", this.getLaserScale());
        CompoundTag gunAmmoTag = new CompoundTag();
        for (Map.Entry<String, GunData> entry : this.getGunDataMap().entrySet()) {
            String name = entry.getKey();
            GunData gd = entry.getValue();
            int ammoCost = ((Number)gd.get(GunProp.AMMO_COST_PER_SHOOT)).intValue();
            int shots = ammoCost <= 0 ? 999 : gd.currentAvailableAmmo(null) / ammoCost;
            gunAmmoTag.m_128405_(name, shots);
        }
        tag.m_128365_("GunAmmo", (Tag)gunAmmoTag);
        tag.m_128362_("UUID", this.f_19820_);
    }

    @NotNull
    public InteractionResult m_6096_(@NotNull Player player, @NotNull InteractionHand hand) {
        InteractionResult mainRes;
        InteractionResult interactionResult;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)hand, (String)"hand");
        if (player.m_20202_() == this) {
            return InteractionResult.PASS;
        }
        ItemStack mainStack = player.m_21205_();
        Item mainItem = mainStack.m_41720_();
        if (this.getLocked() && !(mainItem instanceof VehicleKeyItem)) {
            player.m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.vehicle.locked").m_130940_(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }
        if (mainStack.m_204117_(ModTags.Items.TOOLS_CROWBAR)) {
            Intrinsics.checkNotNull((Object)mainStack);
            interactionResult = this.onCrowbarInteract(mainStack, player, hand);
        } else if (mainItem instanceof IVehicleInteract) {
            IVehicleInteract iVehicleInteract = (IVehicleInteract)mainItem;
            Intrinsics.checkNotNull((Object)mainStack);
            interactionResult = iVehicleInteract.onInteractVehicle(this, mainStack, player, hand);
        } else {
            interactionResult = null;
        }
        InteractionResult interactionResult2 = mainRes = interactionResult;
        if (interactionResult2 != null) {
            return interactionResult2;
        }
        if (mainStack.m_41720_() instanceof NameTagItem && mainStack.m_41788_()) {
            this.m_6593_(mainStack.m_41786_());
            mainStack.m_41774_(1);
            InteractionResult interactionResult3 = InteractionResult.m_19078_((boolean)this.m_9236_().m_5776_());
            Intrinsics.checkNotNullExpressionValue((Object)interactionResult3, (String)"sidedSuccess(...)");
            return interactionResult3;
        }
        if (this.hasMenu() && player.m_6144_()) {
            this.openMenu(player);
            InteractionResult interactionResult4 = InteractionResult.m_19078_((boolean)player.m_9236_().f_46443_);
            Intrinsics.checkNotNullExpressionValue((Object)interactionResult4, (String)"sidedSuccess(...)");
            return interactionResult4;
        }
        if (!player.m_6144_() && (mainStack.m_150930_((Item)ModItems.C4_BOMB.get()) || mainStack.m_150930_((Item)ModItems.DETONATOR.get())) && this.getMaxPassengers() > 0) {
            return InteractionResult.PASS;
        }
        if (!player.m_6144_() && this.getMaxPassengers() > 0) {
            if (((Boolean)VehicleConfig.SAME_TEAM_ENTER_VEHICLE.get()).booleanValue()) {
                for (Entity passenger : this.m_20197_()) {
                    if (passenger.m_5647_() == null) continue;
                    Intrinsics.checkNotNull((Object)passenger);
                    if (!TDMSavedData.Companion.enabledTDM(passenger) && passenger.m_5647_() == player.m_5647_()) continue;
                    return InteractionResult.PASS;
                }
                if (this.getLastDriver() != null && !SeekTool.IN_SAME_TEAM.test((Entity)player, this.getLastDriver())) {
                    Entity entity = this.getLastDriver();
                    if ((entity != null ? entity.m_5647_() : null) != null) {
                        return InteractionResult.PASS;
                    }
                }
            }
            if (this.isWreck()) {
                return InteractionResult.PASS;
            }
            if (this.m_146895_() == null) {
                if (player instanceof FakePlayer) {
                    return InteractionResult.PASS;
                }
                VehicleVecUtils.setDriverAngle(this, player);
                player.m_6858_(false);
                if (player.m_9236_() instanceof ServerLevel) {
                    return player.m_20329_((Entity)this) ? InteractionResult.CONSUME : InteractionResult.PASS;
                }
            } else if (!(this.m_146895_() instanceof Player)) {
                if (player instanceof FakePlayer) {
                    return InteractionResult.PASS;
                }
                Entity entity = this.m_146895_();
                Intrinsics.checkNotNull((Object)entity);
                entity.m_8127_();
                VehicleVecUtils.setDriverAngle(this, player);
                player.m_6858_(false);
                if (player.m_9236_() instanceof ServerLevel) {
                    return player.m_20329_((Entity)this) ? InteractionResult.CONSUME : InteractionResult.PASS;
                }
            }
            if (this.m_7310_((Entity)player)) {
                if (player instanceof FakePlayer) {
                    return InteractionResult.PASS;
                }
                player.m_6858_(false);
                if (player.m_9236_() instanceof ServerLevel) {
                    return player.m_20329_((Entity)this) ? InteractionResult.CONSUME : InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    public InteractionResult onCrowbarInteract(@NotNull ItemStack stack, @NotNull Player player, @NotNull InteractionHand hand) {
        block6: {
            block5: {
                Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
                Intrinsics.checkNotNullParameter((Object)player, (String)"player");
                Intrinsics.checkNotNullParameter((Object)hand, (String)"hand");
                if (!player.m_6144_()) break block5;
                ImmutableList immutableList = this.f_19823_;
                Intrinsics.checkNotNullExpressionValue((Object)immutableList, (String)"passengers");
                if (!(!((Collection)immutableList).isEmpty())) break block6;
            }
            return null;
        }
        if (this.isWreck()) {
            return InteractionResult.PASS;
        }
        for (ItemStack item : this.getRetrieveItems()) {
            ItemHandlerHelper.giveItemToPlayer((Player)player, (ItemStack)item);
        }
        this.m_142687_(Entity.RemovalReason.DISCARDED);
        this.m_146870_();
        return InteractionResult.SUCCESS;
    }

    @Nullable
    public Entity getLastDriver() {
        Level level = this.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        return EntityFindUtil.findEntity(level, this.getLastDriverUUID());
    }

    @Deprecated(message="")
    public void setDriverAngle(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        VehicleVecUtils.setDriverAngle(this, player);
    }

    public boolean m_6469_(@NotNull DamageSource source, float amount) {
        OBBHitter accessor;
        OBB.Part part;
        Entity projectile;
        Entity entity;
        block20: {
            block19: {
                Entity lastDriver;
                block18: {
                    Intrinsics.checkNotNullParameter((Object)source, (String)"source");
                    if (source.m_269533_(ModTags.DamageTypes.VEHICLE_IMMUNE)) {
                        return false;
                    }
                    if (DamageTypeTool.isGunDamage(source) && source.m_7639_() != null) {
                        Entity entity2 = source.m_7639_();
                        Intrinsics.checkNotNull((Object)entity2);
                        if (entity2.m_20202_() == this && !source.m_276093_(ModDamageTypes.CUSTOM_EXPLOSION)) {
                            return false;
                        }
                    }
                    lastDriver = this instanceof OwnableEntity ? (Entity)((OwnableEntity)this).m_269323_() : this.getLastDriver();
                    entity = source.m_7639_();
                    if (entity == null || lastDriver == null || !SeekTool.IS_FRIENDLY.test(lastDriver, entity) || lastDriver.m_5647_() == null || entity.m_5647_() == null || entity.m_5647_() != lastDriver.m_5647_()) break block18;
                    Team team = entity.m_5647_();
                    Intrinsics.checkNotNull((Object)team);
                    if (!team.m_6260_()) break block19;
                }
                if (entity != lastDriver || source.m_276093_(ModDamageTypes.VEHICLE_STRIKE) || source.m_276093_(ModDamageTypes.CUSTOM_EXPLOSION)) break block20;
            }
            return false;
        }
        if (this.damageDebugResultReceiver != null) {
            Player player = this.damageDebugResultReceiver;
            Intrinsics.checkNotNull((Object)player);
            player.m_213846_((Component)DamageHandler.INSTANCE.getDamageInfo(this, source, amount));
        }
        float computedAmount = amount;
        if (!source.m_269533_(ModTags.DamageTypes.BYPASSES_VEHICLE)) {
            computedAmount = this.getDamageModifier().compute(this, source, amount);
        }
        this.setCrash(source.m_276093_(ModDamageTypes.VEHICLE_STRIKE));
        if (entity != null) {
            this.setLastAttackerUUID(entity.m_20149_());
        }
        if ((projectile = source.m_7640_()) instanceof Projectile && (part = (accessor = OBBHitter.Companion.getInstance(projectile)).sbw$getCurrentHitPart()) != null) {
            OBB.Part part2 = part;
            switch (WhenMappings.$EnumSwitchMapping$1[part2.ordinal()]) {
                case 1: {
                    this.setTurretHealth(this.getTurretHealth() - computedAmount);
                    break;
                }
                case 2: {
                    this.setLeftWheelHealth(this.getLeftWheelHealth() - computedAmount);
                    break;
                }
                case 3: {
                    this.setRightWheelHealth(this.getRightWheelHealth() - computedAmount);
                    break;
                }
                case 4: {
                    this.setMainEngineHealth(this.getMainEngineHealth() - computedAmount);
                    break;
                }
                case 5: {
                    this.setSubEngineHealth(this.getSubEngineHealth() - computedAmount);
                }
            }
        }
        this.setLastDamageSource(source);
        this.setLastDamageStamp(this.m_9236_().m_46467_());
        this.onHurt(computedAmount, source.m_7639_(), true);
        if (entity instanceof ServerPlayer) {
            CriteriaRegister.INSTANCE.getVEHICLE_HURT().trigger((ServerPlayer)entity, source, computedAmount);
        }
        return super.m_6469_(source, computedAmount);
    }

    @NotNull
    public DamageModifier getDamageModifier() {
        DamageModifier damageModifier = this.data().damageModifier();
        Intrinsics.checkNotNullExpressionValue((Object)damageModifier, (String)"damageModifier(...)");
        return damageModifier;
    }

    public float getSourceAngle(@NotNull DamageSource source, float multiplier) {
        Intrinsics.checkNotNullParameter((Object)source, (String)"source");
        return VehicleVecUtils.getDamageSourceAngle(this, source, multiplier);
    }

    public void heal(float pHealAmount) {
        if (this.m_9236_() instanceof ServerLevel && this.getHealth() > 0.0f) {
            this.setHealth(this.getHealth() + pHealAmount);
        }
    }

    public void onHurt(float pHealAmount, @Nullable Entity attacker, boolean send) {
        if (this.m_9236_() instanceof ServerLevel) {
            Holder holder = Holder.m_205709_((Object)ModSounds.INDICATION_VEHICLE.get());
            MinecraftServer minecraftServer = this.m_20194_();
            Intrinsics.checkNotNull((Object)minecraftServer);
            for (ServerPlayer player : minecraftServer.m_6846_().m_11314_()) {
                if (!Intrinsics.areEqual((Object)player, (Object)attacker) || !(pHealAmount > 0.0f) || !(this.getHealth() > 0.0f) || !send || this instanceof DroneEntity) continue;
                player.f_8906_.m_9829_((Packet)new ClientboundSoundPacket(holder, SoundSource.PLAYERS, player.m_20185_(), player.m_20188_(), player.m_20189_(), 0.25f + 2.75f * pHealAmount / this.getMaxHealth(), this.f_19796_.m_188501_() * 0.1f + 0.9f, player.m_9236_().f_46441_.m_188505_()));
                Intrinsics.checkNotNull((Object)player);
                MinecraftUtil.sendPacket((Player)player, new ClientIndicatorMessage(3, 5));
            }
            if (pHealAmount > 0.0f && send) {
                this.setRepairCoolDown(this.maxRepairCoolDown());
                List passengers = this.m_20197_();
                for (Entity entity : passengers) {
                    if (!(entity instanceof ServerPlayer)) continue;
                    ((ServerPlayer)entity).f_8906_.m_9829_((Packet)new ClientboundSoundPacket(holder, SoundSource.PLAYERS, ((ServerPlayer)entity).m_20185_(), ((ServerPlayer)entity).m_20188_(), ((ServerPlayer)entity).m_20189_(), 0.25f + 4.75f * pHealAmount / this.getMaxHealth(), this.f_19796_.m_188501_() * 0.1f + 0.6f, ((ServerPlayer)entity).m_9236_().f_46441_.m_188505_()));
                }
            }
            this.setHealth(this.getHealth() - org.joml.Math.min((float)pHealAmount, (float)(this.getMaxHealth() + 1.0f)));
        }
        Entity driver = this.getLastDriver();
        if (this.getLocked() && driver instanceof Player && !Intrinsics.areEqual((Object)attacker, (Object)driver) && this.getHurtWarnCoolDown() <= 0 && !this.isWreck()) {
            if (this.m_9236_() instanceof ServerLevel) {
                Object[] objectArray = new Object[]{FormatTool.format1D$default(this.m_20185_(), null, 2, null), FormatTool.format1D$default(this.m_20186_(), null, 2, null), FormatTool.format1D$default(this.m_20189_(), null, 2, null), this.m_5446_()};
                ((Player)driver).m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.vehicle.lock_hurt", (Object[])objectArray).m_130940_(ChatFormatting.YELLOW), false);
            }
            this.setHurtWarnCoolDown(60);
        }
    }

    protected boolean m_20073_() {
        int interval;
        if (!this.isInitialized) {
            return super.m_20073_();
        }
        int n = this.getEngineInfo() instanceof EngineInfo.Ship ? 1 : (interval = this.getVehicleType() == VehicleType.AIRPLANE || this.getVehicleType() == VehicleType.HELICOPTER || this.getVehicleType() == VehicleType.AIRSHIP ? 20 : 4);
        if (this.f_19797_ % interval != 0) {
            return this.m_20069_();
        }
        return super.m_20073_();
    }

    public boolean isInFluidType(@NotNull BiPredicate<FluidType, Double> predicate) {
        Intrinsics.checkNotNullParameter(predicate, (String)"predicate");
        OBB oBB = this.getCollisionOBB();
        if (oBB == null) {
            return super.isInFluidType(predicate);
        }
        OBB collisionOBB = oBB;
        AABB obbAABB = OBB.Companion.getWorldAABB(collisionOBB).m_82406_(0.001);
        if (obbAABB.m_82392_() || obbAABB.m_82309_() <= 0.0) {
            return super.isInFluidType(predicate);
        }
        Level level = this.m_9236_();
        int minX = Mth.m_14107_((double)obbAABB.f_82288_);
        int maxX = Mth.m_14165_((double)obbAABB.f_82291_);
        int minY = Mth.m_14107_((double)obbAABB.f_82289_);
        int maxY = Mth.m_14165_((double)obbAABB.f_82292_);
        int minZ = Mth.m_14107_((double)obbAABB.f_82290_);
        int maxZ = Mth.m_14165_((double)obbAABB.f_82293_);
        for (int x = minX; x < maxX; ++x) {
            for (int y = minY; y < maxY; ++y) {
                for (int z = minZ; z < maxZ; ++z) {
                    AABB blockAABB;
                    double height;
                    BlockPos pos = new BlockPos(x, y, z);
                    FluidState fluidState = level.m_6425_(pos);
                    if (fluidState.m_76178_() || !((height = (double)((float)y + fluidState.m_76155_((BlockGetter)level, pos))) >= obbAABB.f_82289_) || !OBB.Companion.isColliding(collisionOBB, blockAABB = new AABB((double)x, (double)y, (double)z, (double)(x + 1), height, (double)(z + 1))) || !predicate.test(fluidState.getFluidType(), height)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean m_20077_() {
        if (this.getCollisionOBB() == null) {
            return super.m_20077_();
        }
        return this.isInFluidType(VehicleEntity::isInLava$lambda$49);
    }

    public float getHealth() {
        Object object = this.f_19804_.m_135370_(HEALTH);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
        return ((Number)object).floatValue();
    }

    public void setHealth(float value) {
        this.f_19804_.m_135381_(HEALTH, (Object)Float.valueOf(RangesKt.coerceIn((float)value, (float)(-this.getMaxHealth() - (float)10), (float)this.getMaxHealth())));
    }

    public float getMaxHealth() {
        return this.computed().getMaxHealth();
    }

    public int getDecoyReloadTime() {
        return this.computed().getDecoyReloadTime();
    }

    public float getTurretMaxHealth() {
        return this.computed().getPartHealth().getTurret();
    }

    public float getLeftWheelMaxHealth() {
        return this.computed().getPartHealth().getLeftWheel();
    }

    public float getRightWheelMaxHealth() {
        return this.computed().getPartHealth().getRightWheel();
    }

    public float getMainEngineMaxHealth() {
        return this.computed().getPartHealth().getMainEngine();
    }

    public float getSubEngineMaxHealth() {
        return this.computed().getPartHealth().getSubEngine();
    }

    @Deprecated(message="Use vehicle data or getLeft/RightWheelMaxHealth() instead", replaceWith=@ReplaceWith(expression="computed().partHealth.leftWheel", imports={}))
    public float getWheelMaxHealth() {
        return 50.0f;
    }

    @Deprecated(message="Use vehicle data or getMain/SubEngineMaxHealth() instead", replaceWith=@ReplaceWith(expression="computed().partHealth.mainEngine", imports={}))
    public float getEngineMaxHealth() {
        return 50.0f;
    }

    public void m_20093_() {
        if (this.f_19797_ % 10 == 0) {
            DamageSource damageSource = this.m_269291_().m_269233_();
            Intrinsics.checkNotNullExpressionValue((Object)damageSource, (String)"lava(...)");
            this.m_6469_(damageSource, 4.0f);
        }
    }

    public void m_7601_(@NotNull BlockState pState, @NotNull Vec3 pMotionMultiplier) {
        Intrinsics.checkNotNullParameter((Object)pState, (String)"pState");
        Intrinsics.checkNotNullParameter((Object)pMotionMultiplier, (String)"pMotionMultiplier");
    }

    @ParametersAreNonnullByDefault
    protected void m_7355_(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        Intrinsics.checkNotNullParameter((Object)pPos, (String)"pPos");
        Intrinsics.checkNotNullParameter((Object)pState, (String)"pState");
        this.m_5496_((SoundEvent)ModSounds.WHEEL_VEHICLE_STEP.get(), (float)(this.m_20184_().m_82553_() * 0.1), this.f_19796_.m_188501_() * 0.15f + 1.05f);
    }

    public boolean m_5829_() {
        return this.enableAABB();
    }

    public boolean m_6087_() {
        return !this.m_213877_();
    }

    public boolean m_7313_(@NotNull Entity attacker) {
        Intrinsics.checkNotNullParameter((Object)attacker, (String)"attacker");
        return this.m_20363_(attacker) || super.m_7313_(attacker);
    }

    protected boolean m_7310_(@NotNull Entity pPassenger) {
        Intrinsics.checkNotNullParameter((Object)pPassenger, (String)"pPassenger");
        return this.m_20197_().size() < this.getMaxPassengers();
    }

    public int getMaxPassengers() {
        return this.computed().seats().size();
    }

    public int maxRepairCoolDown() {
        return this.computed().getRepairCooldown();
    }

    public float repairAmount() {
        return this.computed().getRepairAmount();
    }

    /*
     * WARNING - void declaration
     */
    public void m_6075_() {
        List<Vec3> terrainCompat;
        DefaultVehicleData computed = this.computed();
        if (this.m_9236_().f_46443_) {
            if (this.prevMotion == null) {
                this.prevMotion = this.m_20184_();
            }
            this.fakePitchO = this.fakePitch;
            this.fakeRollO = this.fakeRoll;
            this.prevPitchAngle = this.pitchAngle;
            this.prevRollAngle = this.rollAngle;
            if (!this.wasEngineRunning && this.engineRunning()) {
                playEngineSound.accept(this);
                playSwimSound.accept(this);
                if (computed.getEngineType() == EngineType.TRACK) {
                    playTrackSound.accept(this);
                }
            }
            if (!this.wasHornWorking && this.hornWorking()) {
                playHornSound.accept(this);
            }
            if (!this.wasStuka && this.stuka() && this.getEngineInfo() instanceof EngineInfo.Aircraft) {
                EngineInfo engineInfo = this.getEngineInfo();
                Intrinsics.checkNotNull((Object)engineInfo, (String)"null cannot be cast to non-null type com.atsuishio.superbwarfare.data.vehicle.subdata.EngineInfo.Aircraft");
                if (((EngineInfo.Aircraft)engineInfo).getHasStukaSound()) {
                    playStukaSound.accept(this);
                }
            }
            if (!this.wasHeliCrash && this.heliCrash()) {
                playHeliCrashSound.accept(this);
            }
            if (!this.wasVehicleSkip && this.vehicleSkip()) {
                playVehicleSkipSound.accept(this);
            }
            if (playFireSound != null) {
                for (Map.Entry<String, GunData> entry : this.getGunDataMap().entrySet()) {
                    weaponName = entry.getKey();
                    GunData gunData = entry.getValue();
                    if (gunData.get(GunProp.SOUND_INFO).getFireSoundInstances() == null) continue;
                    boolean firing = gunData.shootTimer.get() > 0;
                    boolean was = this.weaponFiringState.getOrDefault(weaponName, false);
                    if (!was && firing) {
                        BiConsumer<VehicleEntity, String> biConsumer = playFireSound;
                        Intrinsics.checkNotNull(biConsumer);
                        biConsumer.accept(this, (String)weaponName);
                    }
                    Boolean bl = firing;
                    this.weaponFiringState.put((String)weaponName, bl);
                }
            }
        } else {
            Map<String, GunData> map = this.getGunDataMap();
            GunData firstGun = (GunData)CollectionsKt.firstOrNull((Iterable)map.values());
            if (firstGun != null) {
                if (this.f_19797_ - this.envRateCachedTick >= 4) {
                    double d;
                    VehicleEntity vehicleEntity = this;
                    weaponName = (GunData)CollectionsKt.firstOrNull((Iterable)map.values());
                    if (weaponName != null) {
                        void it;
                        Object object = weaponName;
                        VehicleEntity vehicleEntity2 = vehicleEntity;
                        boolean bl = false;
                        double d2 = GunEventHandler.INSTANCE.computeEnvironmentRate(this, (GunData)it);
                        vehicleEntity = vehicleEntity2;
                        d = d2;
                    } else {
                        d = 1.0;
                    }
                    vehicleEntity.cachedEnvRate = d;
                    this.envRateCachedTick = this.f_19797_;
                }
                weaponName = map.entrySet().iterator();
                while (weaponName.hasNext()) {
                    GunData gunData = (GunData)((Map.Entry)weaponName.next()).getValue();
                    GunEventHandler.INSTANCE.gunTick(this, gunData, true, this.cachedEnvRate);
                }
            }
            this.f_19804_.m_276349_(GUN_DATA_MAP, map, true);
        }
        this.wasEngineRunning = this.engineRunning();
        this.wasHornWorking = this.hornWorking();
        this.wasStuka = this.stuka();
        this.wasHeliCrash = this.heliCrash();
        this.wasVehicleSkip = this.vehicleSkip();
        this.setPrevRoll(this.getRoll());
        this.setTurretYRotO(this.getTurretYRot());
        this.setTurretXRotO(this.getTurretXRot());
        this.setGunYRotO(this.getGunYRot());
        this.setGunXRotO(this.getGunXRot());
        this.setLeftWheelRotO(this.getLeftWheelRot());
        this.setRightWheelRotO(this.getRightWheelRot());
        this.setLeftTrackO(this.getLeftTrack());
        this.setRightTrackO(this.getRightTrack());
        this.setRudderRotO(this.getRudderRot());
        this.setPropellerRotO(this.getPropellerRot());
        this.setRecoilShakeO(this.getRecoilShake());
        if (this.getJumpCoolDown() > 0 && this.m_20096_()) {
            int map = this.getJumpCoolDown();
            this.setJumpCoolDown(map + -1);
        }
        this.setLastTickSpeed(new Vec3(this.m_20184_().f_82479_, this.m_20184_().f_82480_ + 0.06, this.m_20184_().f_82481_).m_82553_());
        this.setLastTickVerticalSpeed(this.m_20184_().f_82480_ + 0.06);
        if (this.getCollisionCoolDown() > 0) {
            int map = this.getCollisionCoolDown();
            this.setCollisionCoolDown(map + -1);
        }
        this.setLaserScaleO(this.getLaserScale());
        this.setFlap1LRotO(this.getFlap1LRot());
        this.setFlap1RRotO(this.getFlap1RRot());
        this.setFlap1L2RotO(this.getFlap1L2Rot());
        this.setFlap1R2RotO(this.getFlap1R2Rot());
        this.setFlap2LRotO(this.getFlap2LRot());
        this.setFlap2RRotO(this.getFlap2RRot());
        this.setFlap3RotO(this.getFlap3Rot());
        this.setDeltaMovementO(this.m_20184_());
        this.setPositionO(this.m_20182_());
        this.setAbsoluteSpeedO(this.getAbsoluteSpeed());
        super.m_6075_();
        if (this.getLaserScale() > 0.0f) {
            this.setLaserScale(org.joml.Math.max((float)(this.getLaserScale() - 0.1f), (float)0.0f));
            this.setLaserScale(this.getLaserScale() * 0.9f);
        }
        if (this.getLaserScale() == 0.0f) {
            this.setLaserLength(0.0f);
        }
        if (this.getRepairCoolDown() > 0) {
            int map = this.getRepairCoolDown();
            this.setRepairCoolDown(map + -1);
        }
        if (this.getHurtWarnCoolDown() > 0) {
            int map = this.getHurtWarnCoolDown();
            this.setHurtWarnCoolDown(map + -1);
        }
        if (this.getHealth() >= this.getMaxHealth()) {
            this.setRepairCoolDown(this.maxRepairCoolDown());
        }
        float delta = org.joml.Math.abs((float)(this.m_146908_() - this.f_19859_));
        while (this.m_146908_() > 180.0f) {
            this.m_146922_(this.m_146908_() - 360.0f);
            this.f_19859_ = this.m_146908_() - delta;
        }
        while (this.m_146908_() <= -180.0f) {
            this.m_146922_(this.m_146908_() + 360.0f);
            this.f_19859_ = delta + this.m_146908_();
        }
        float deltaX = org.joml.Math.abs((float)(this.m_146909_() - this.f_19860_));
        while (this.m_146909_() > 180.0f) {
            this.m_146926_(this.m_146909_() - 360.0f);
            this.f_19860_ = this.m_146909_() - deltaX;
        }
        while (this.m_146909_() <= -180.0f) {
            this.m_146926_(this.m_146909_() + 360.0f);
            this.f_19860_ = deltaX + this.m_146909_();
        }
        float deltaZ = org.joml.Math.abs((float)(this.getRoll() - this.getPrevRoll()));
        while (this.getRoll() > 180.0f) {
            this.setZRot(this.getRoll() - 360.0f);
            this.setPrevRoll(this.getRoll() - deltaZ);
        }
        while (this.getRoll() <= -180.0f) {
            this.setZRot(this.getRoll() + 360.0f);
            this.setPrevRoll(deltaZ + this.getRoll());
        }
        this.handleClientSync();
        if (this.m_9236_() instanceof ServerLevel && this.getHealth() <= 0.0f && !this.isWreck()) {
            this.setWreck(true);
            this.destroy();
        }
        if (this.isWreck()) {
            if (!(this.getVehicleType() != VehicleType.AIRPLANE && this.getVehicleType() != VehicleType.HELICOPTER && this.getVehicleType() != VehicleType.AIRSHIP || !this.m_20096_() && !this.isInFluidType() || this.getSympatheticDetonated())) {
                this.setSympatheticDetonated(true);
                DestroyInfo destroyInfo = computed.getDestroyInfo();
                if (destroyInfo.getExplodePassengers()) {
                    if (this.getCrash() && destroyInfo.getCrashPassengers()) {
                        this.crashPassengers();
                    } else {
                        this.explodePassengers();
                    }
                }
                this.vehicleExplosion(destroyInfo);
                this.m_20153_();
            }
            if (this.getHealth() <= -this.getMaxHealth()) {
                this.m_146870_();
                this.createCustomExplosion().radius(5.0f).damage(1.0f).keepBlock().explode();
                this.generateWreckageLoot();
            }
            if (this.getVehicleType() != VehicleType.AIRPLANE && this.getVehicleType() != VehicleType.HELICOPTER && this.getVehicleType() != VehicleType.AIRSHIP) {
                this.m_20153_();
            }
        }
        this.travel();
        this.towedTick();
        this.towingTick();
        this.vehicleRadar();
        if (!this.m_20096_() && this.getEngineStartOver() && this.getEnergy() > 1024 && !this.isWreck()) {
            List list = this.m_20197_();
            Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getPassengers(...)");
            if (!((Collection)list).isEmpty() && computed.getEngineType() == EngineType.AIRCRAFT && this.getLoiterActive()) {
                VehicleEngineUtils.aircraftLoiter(this);
            }
        }
        if (this.getHealth() <= computed.getSelfHurtPercent() * this.getMaxHealth()) {
            this.onHurt(computed.getSelfHurtAmount(), this.getLastAttacker(), false);
        } else if (this.getRepairCoolDown() == 0 && this.getHealth() > 0.0f) {
            this.heal(this.repairAmount());
        }
        if (this.getMaxPassengers() > 0 && this.m_146895_() != null) {
            Entity entity = this.m_146895_();
            Intrinsics.checkNotNull((Object)entity);
            this.setLastDriverUUID(entity.m_20149_());
        }
        if (this.m_20197_().isEmpty()) {
            int destroyInfo = this.noPassengerTime;
            this.noPassengerTime = destroyInfo + 1;
            if (this.noPassengerTime > 200 && !this.getLocked()) {
                this.setLastDriverUUID("undefined");
            }
        } else {
            this.noPassengerTime = 0;
        }
        this.setMouseMoveSpeedX(this.getMouseMoveSpeedX() * 0.95f);
        this.setMouseMoveSpeedY(this.getMouseMoveSpeedY() * 0.95f);
        if (this.hasTurret()) {
            Entity turretController = this.getNthEntity(this.getTurretControllerIndex());
            if (turretController instanceof Player) {
                this.adjustTurretAngle();
            } else if (turretController instanceof Mob) {
                this.turretAutoAimFromUuid(this.getAiTurretTargetUUID(), (LivingEntity)turretController);
            }
            if (turretController == null) {
                this.setTurretYRotLock(0.0f);
            }
        }
        if (this.hasPassengerWeaponStation()) {
            Entity passengerWeaponStationController = this.getNthEntity(this.getPassengerWeaponStationControllerIndex());
            if (passengerWeaponStationController instanceof Player || passengerWeaponStationController == null) {
                this.adjustWeaponControllerAngle();
            } else if (passengerWeaponStationController instanceof Mob) {
                this.passengerWeaponAutoAimFormUuid(this.getAiPassengerWeaponTargetUUID(), (LivingEntity)passengerWeaponStationController);
            }
        }
        int gunData = ((Collection)this.data().getDefault().seats()).size();
        for (int i = 0; i < gunData; ++i) {
            ItemStack stack;
            GunData gunData2;
            Entity mob = this.getNthEntity(i);
            if (mob instanceof Mob && this.canShoot((LivingEntity)mob) && ((Mob)mob).m_5448_() != null && this.getGunData(mob) != null && ((Mob)mob).m_9236_() instanceof ServerLevel) {
                LivingEntity target;
                Intrinsics.checkNotNull((Object)((Mob)mob).m_5448_());
                ((Mob)mob).m_21391_((Entity)target, 30.0f, 30.0f);
                int rpm = (int)org.joml.Math.ceil((float)(20.0f / ((float)this.vehicleWeaponRpm((LivingEntity)mob) / (float)60)));
                if (this.f_19797_ % rpm == 0 && this.canShoot((LivingEntity)mob)) {
                    Vec3 vec3 = this.getShootDirectionForHud(mob, 1.0f);
                    Vec3 vec32 = this.getShootPos(mob, 1.0f).m_82505_(VectorTool.lerpGetEntityBoundingBoxCenter((Entity)target, 1.0f));
                    Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"vectorTo(...)");
                    if (VectorToolKt.angleTo(vec3, vec32) < 4.0) {
                        this.vehicleShoot((LivingEntity)mob, target.m_20148_(), null);
                    }
                }
            }
            if (!(mob instanceof Player) || !(this.m_9236_() instanceof ServerLevel) || this.f_19797_ % 20 != 0 || (gunData2 = this.getGunData(mob)) == null) continue;
            if (GunData.selectedAmmoConsumer$default(gunData2, null, 1, null).getType() == AmmoConsumer.AmmoConsumeType.ENERGY) {
                if (this.canConsume(((Number)gunData2.get(GunProp.AMMO_COST_PER_SHOOT)).intValue())) continue;
                ((Player)mob).m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.not.enough.energy"), true);
                continue;
            }
            if (this.getAmmoCount((LivingEntity)mob) >= ((Number)gunData2.get(GunProp.AMMO_COST_PER_SHOOT)).intValue() || Intrinsics.areEqual((Object)(stack = GunData.selectedAmmoConsumer$default(gunData2, null, 1, null).stack()), (Object)ItemStack.f_41583_) || InventoryTool.hasCreativeAmmoBox(this) || gunData2.reloading()) continue;
            ((Player)mob).m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.need.ammo").m_7220_((Component)Component.m_237113_((String)"[").m_7220_(stack.m_41786_()).m_130946_("]").m_130940_(ChatFormatting.YELLOW)), true);
        }
        float deltaT = Math.abs(this.getTurretYRot() - this.getTurretYRotO());
        while (this.getTurretYRot() > 360.0f) {
            this.setTurretYRot(this.getTurretYRot() - 720.0f);
            this.setTurretYRotO(this.getTurretYRot() - deltaT);
        }
        while (this.getTurretYRot() <= -360.0f) {
            this.setTurretYRot(this.getTurretYRot() + 720.0f);
            this.setTurretYRotO(deltaT + this.getTurretYRot());
        }
        float deltaG = Math.abs(this.getGunYRot() - this.getGunYRotO());
        while (this.getGunYRot() > 180.0f) {
            this.setGunYRot(this.getGunYRot() - 360.0f);
            this.setGunYRotO(this.getGunYRot() - deltaT);
        }
        while (this.getGunYRot() <= -180.0f) {
            this.setGunYRot(this.getGunYRot() + 360.0f);
            this.setGunYRotO(deltaG + this.getGunYRot());
        }
        if (this.getCannonRecoilTime() > 0) {
            this.setCannonRecoilTime(this.getCannonRecoilTime() - 1);
        }
        this.setRecoilShake((double)Mth.m_14154_((float)this.getCannonRecoilForce()) * 7.0E-7 * Math.pow(this.getCannonRecoilTime(), 4.0) * Math.sin(0.6283185307179586 * ((double)this.getCannonRecoilTime() - 2.5)));
        this.setCannonRecoilForce(this.getCannonRecoilForce() * 0.93f);
        if (this.f_19797_ % 4 == 0) {
            this.clearArrow();
            this.preventStacking();
            this.moveOnDragonTeeth();
            this.collideBlocks();
        }
        this.supportEntities();
        this.crushEntities();
        Vec3 vec3 = this.m_20184_().m_82520_(0.0, -computed.getGravity(), 0.0);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"add(...)");
        this.m_20256_(vec3);
        Vec3 vec33 = this.m_20184_();
        Intrinsics.checkNotNullExpressionValue((Object)vec33, (String)"getDeltaMovement(...)");
        this.m_6478_(MoverType.SELF, vec33);
        if (!this.m_9236_().f_46443_ && this.m_6084_()) {
            if (this.m_20096_() && this.isWreck()) {
                ServerSyncedEntityHandler.unregister(this);
            } else {
                Object object = SyncConfig.SYNC_ENTITY_INTERVAL.get();
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
                if (this.f_19797_ % ((Number)object).intValue() == 0) {
                    ServerSyncedEntityHandler.register$default(this, null, 2, null);
                    MinecraftServer srv = this.m_20194_();
                    if (srv != null) {
                        ResourceLocation dim = this.m_9236_().m_46472_().m_135782_();
                        Intrinsics.checkNotNull((Object)dim);
                        EntityRelationSyncMessage msg = new EntityRelationSyncMessage(dim, CollectionsKt.listOf((Object)this.m_19879_()), null, null, 12, null);
                        for (ServerPlayer player : srv.m_6846_().m_11314_()) {
                            if (!player.m_6084_() || !SeekTool.IS_FRIENDLY.test((Entity)player, this)) continue;
                            Intrinsics.checkNotNull((Object)player);
                            MinecraftUtil.sendPacketTo((Player)player, (Object)msg);
                        }
                    }
                }
            }
        }
        if (this.hasEnergyStorage() && this.f_19797_ % 20 == 0) {
            Iterator iterator = this.inventory.getItems().iterator();
            Intrinsics.checkNotNullExpressionValue((Object)iterator, (String)"iterator(...)");
            Iterator srv = iterator;
            while (srv.hasNext()) {
                ItemStack stack = (ItemStack)srv.next();
                int neededEnergy = this.getMaxEnergy() - this.getEnergy();
                if (neededEnergy <= 0) break;
                Optional energyCap = stack.getCapability(ForgeCapabilities.ENERGY).resolve();
                if (energyCap.isEmpty()) continue;
                Object t = energyCap.get();
                Intrinsics.checkNotNullExpressionValue(t, (String)"get(...)");
                IEnergyStorage energyStorage = (IEnergyStorage)t;
                int stored = energyStorage.getEnergyStored();
                if (stored <= 0) continue;
                int energyToExtract = org.joml.Math.min((int)stored, (int)neededEnergy);
                int extracted = energyStorage.extractEnergy(energyToExtract, false);
                this.setEnergy(this.getEnergy() + extracted);
            }
        }
        if (this.m_9236_() instanceof ServerLevel && (this.inventoryDirty || this.f_19797_ % 100 == 0)) {
            this.inventoryDirty = false;
            this.updateBackupAmmoCount();
        }
        this.setHornVolume(this.getHornVolume() * 0.5f);
        if (this.hasDecoy() && this.m_9236_() instanceof ServerLevel) {
            this.setDecoyItemCount(this.countDecoyItem());
            boolean hasInfiniteDecoyAmmo = this.hasCreativeAmmoBoxCached();
            if (this.hasSmokeDecoy()) {
                this.releaseSmokeDecoy(this.getTurretVector(1.0f));
            } else {
                this.releaseDecoy();
            }
            if (this.getDecoyReloadCoolDown() > 0) {
                if ((this.getDecoyItemCount() > 0 || hasInfiniteDecoyAmmo) && this.getDecoyCount() == 0) {
                    int stack = this.getDecoyReloadCoolDown();
                    this.setDecoyReloadCoolDown(stack + -1);
                    v13 = stack;
                } else {
                    this.setDecoyReloadCoolDown(this.getDecoyReloadTime());
                    v13 = Unit.INSTANCE;
                }
            }
            if (this.getDecoyReloadCoolDown() == 0 && (this.getDecoyItemCount() > 0 || hasInfiniteDecoyAmmo) && this.getDecoyCount() == 0) {
                VehicleWeaponUtils.reloadDecoy(this);
            }
        }
        if (!((Collection)(terrainCompat = computed.getTerrainCompat())).isEmpty() && (this.getVehicleType() != VehicleType.AIRPLANE && this.getVehicleType() != VehicleType.HELICOPTER && this.getVehicleType() != VehicleType.AIRSHIP || !this.isWreck())) {
            this.terrainCompact(terrainCompat);
        }
        if (this.getLeftTrack() < 0.0f) {
            this.setLeftTrackO(this.getTrackAnimationLength());
            this.setLeftTrack(this.getTrackAnimationLength());
        }
        if (this.getLeftTrack() > (float)this.getTrackAnimationLength()) {
            this.setLeftTrackO(0.0f);
            this.setLeftTrack(0.0f);
        }
        if (this.getRightTrack() < 0.0f) {
            this.setRightTrackO(this.getTrackAnimationLength());
            this.setRightTrack(this.getTrackAnimationLength());
        }
        if (this.getRightTrack() > (float)this.getTrackAnimationLength()) {
            this.setRightTrackO(0.0f);
            this.setRightTrack(0.0f);
        }
        if (this.getTurretBurnTimer() > 0) {
            int stack = this.getTurretBurnTimer();
            this.setTurretBurnTimer(stack + -1);
        }
        if (this.m_9236_().f_46443_) {
            this.setAbsoluteSpeedLerp(Mth.m_14139_((double)0.2, (double)this.getAbsoluteSpeedLerp(), (double)this.getPositionO().m_82505_(this.m_20182_()).m_82553_()));
            this.setAbsoluteSpeed(this.getAbsoluteSpeedLerp());
            this.fakeRoll *= 0.8f;
            this.fakePitch *= 0.8f;
            if (this.prevMotion != null) {
                Vec3 acceleration;
                Vec3 motion = this.m_20184_();
                Vec3 vec34 = this.prevMotion;
                if (vec34 != null) {
                    Vec3 it = vec34;
                    boolean bl = false;
                    v15 = motion.m_82546_(it);
                } else {
                    v15 = acceleration = null;
                }
                if (acceleration != null && acceleration.m_82553_() > 0.02) {
                    acceleration = acceleration.m_82541_().m_82490_(0.02);
                }
                float yaw = this.m_146908_();
                float sinYaw = Mth.m_14031_((float)(yaw * ((float)Math.PI / 180)));
                float cosYaw = Mth.m_14089_((float)(yaw * ((float)Math.PI / 180)));
                Vec3 forward = new Vec3(-((double)sinYaw), 0.0, (double)cosYaw);
                Vec3 right = new Vec3(-((double)cosYaw), 0.0, -((double)sinYaw));
                Vec3 vec35 = acceleration;
                Intrinsics.checkNotNull((Object)vec35);
                double accelForward = vec35.m_82542_(1.0, 0.0, 1.0).m_82526_(forward);
                double accelRight = acceleration.m_82542_(1.0, 0.0, 1.0).m_82526_(right);
                float targetPitch = (float)((double)15 * accelForward);
                float omegaP = (float)Math.PI * 4;
                float zetaP = 0.6f;
                float angularAccelP = omegaP * omegaP * (targetPitch - this.pitchAngle) - (float)2 * zetaP * omegaP * this.pitchVelocity;
                this.pitchVelocity += angularAccelP * 0.05f;
                this.pitchAngle += this.pitchVelocity * 0.05f;
                float targetRoll = (float)((double)20 * accelRight);
                float omegaR = (float)Math.PI * 4;
                float zetaR = 0.6f;
                float angularAccelR = omegaR * omegaR * (targetRoll - this.rollAngle) - (float)2 * zetaR * omegaR * this.rollVelocity;
                this.rollVelocity += angularAccelR * 0.05f;
                this.rollAngle += this.rollVelocity * 0.05f;
                this.prevMotion = motion;
                this.fakePitch -= this.pitchAngle * computed.getInertiaRotateRate();
                this.fakeRoll -= this.rollAngle * computed.getInertiaRotateRate();
            }
        }
        this.lowHealthWarning();
        if (!this.enableAABB()) {
            this.handlePartDamaged(this);
            this.handlePartHealth();
            this.updateOBB();
            this.refreshBoundingBoxFromOBBs();
        }
        if (this.m_9236_() instanceof ServerLevel && ((Boolean)VehicleConfig.VEHICLE_CHUNK_LOADING.get()).booleanValue() && computed.getKeepChunkLoaded()) {
            Vec3 vec36 = this.m_20182_();
            Intrinsics.checkNotNullExpressionValue((Object)vec36, (String)"position(...)");
            this.keepChunkLoaded(vec36);
            Vec3 vec37 = this.m_20182_().m_82549_(this.m_20184_().m_82541_().m_82490_(16.0));
            Intrinsics.checkNotNullExpressionValue((Object)vec37, (String)"add(...)");
            this.keepChunkLoaded(vec37);
        }
    }

    public final boolean hasCreativeAmmoBoxCached() {
        if (this.f_19797_ != this.creativeAmmoBoxCacheTick) {
            this.cachedCreativeAmmoBox = InventoryTool.hasCreativeAmmoBox(this);
            this.creativeAmmoBoxCacheTick = this.f_19797_;
        }
        return this.cachedCreativeAmmoBox;
    }

    public void towedTick() {
        VehicleMotionUtils.towedTick(this);
    }

    public void towingTick() {
        VehicleMotionUtils.towingTick(this);
    }

    private final void keepChunkLoaded(ChunkPos chunkPos) {
        Level level = this.m_9236_();
        Intrinsics.checkNotNull((Object)level, (String)"null cannot be cast to non-null type net.minecraft.server.level.ServerLevel");
        ((ServerLevel)level).m_7726_().m_8387_(TicketType.f_9448_, chunkPos, 3, (Object)this.m_19879_());
    }

    public void keepChunkLoaded(@NotNull Vec3 position) {
        Intrinsics.checkNotNullParameter((Object)position, (String)"position");
        ChunkPos currentChunk = new ChunkPos(BlockPos.m_274446_((Position)((Position)position)));
        this.keepChunkLoaded(currentChunk);
        Vec3 aheadPos = position.m_82549_(this.m_20184_().m_82541_().m_82490_(16.0));
        ChunkPos aheadChunk = new ChunkPos(BlockPos.m_274446_((Position)((Position)aheadPos)));
        if (!Intrinsics.areEqual((Object)aheadChunk, (Object)currentChunk)) {
            this.keepChunkLoaded(aheadChunk);
        }
    }

    public final void vehicleRadar() {
        if (!((Boolean)SyncConfig.SYNC_ENTITY_OVER_RANGE.get()).booleanValue()) {
            return;
        }
        ObjectToList<RadarInfo> objectToList = this.computed().getRadar();
        if (objectToList == null) {
            return;
        }
        ObjectToList<RadarInfo> radars = objectToList;
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        Level level2 = this.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level2, (String)"level(...)");
        Player player = EntityFindUtil.findPlayer(level2, this.getLastDriverUUID());
        if (player == null) {
            return;
        }
        for (RadarInfo radarInfo : radars) {
            StringOrVec3 stringOrVec3 = radarInfo.getDirection();
            Vec3 dirVec = this.getRadarVec(1.0f, stringOrVec3);
            double baseYRot = -VehicleVecUtils.getYRotFromVector(dirVec) + (double)180;
            Vec3 vec3 = this.m_20182_();
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"position(...)");
            RadarScanner.RadarConfig config = new RadarScanner.RadarConfig(player, vec3, radarInfo.getRange(), radarInfo.getAngle(), baseYRot, radarInfo.getRotateSpeed(), RadarScanner.SearchType.VEHICLES, radarInfo.getMinTargetHeight(), radarInfo.getMaxTargetHeight(), radarInfo.getShareWithTeammates(), false, "vehicle_" + this.m_19879_(), radarInfo.getAffectedByStealthTarget());
            RadarScanner.INSTANCE.sendRadarConfig(config, (ServerLevel)level);
            RadarScanner.INSTANCE.scan((ServerLevel)level, config).sendToClients(player, (ServerLevel)level, config.getShareWithTeammates());
        }
    }

    @NotNull
    public final Vec3 getRadarVec(float partialTicks, @Nullable StringOrVec3 stringOrVec3) {
        if (stringOrVec3 == null) {
            Vec3 vec3 = this.m_20252_(partialTicks);
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getViewVector(...)");
            return vec3;
        }
        if (stringOrVec3.isString()) {
            String string = stringOrVec3.getString();
            Intrinsics.checkNotNull((Object)string);
            return this.getVectorFromString(string, partialTicks);
        }
        Vec3 vec3 = stringOrVec3.getVec3();
        Intrinsics.checkNotNull((Object)vec3);
        Vec3 vec32 = vec3;
        Vector4d worldPosition = VehicleVecUtils.transformPosition(this.getVehicleTransform(partialTicks), vec32.f_82479_ + stringOrVec3.getVec3().f_82479_, vec32.f_82480_ + stringOrVec3.getVec3().f_82480_, vec32.f_82481_ + stringOrVec3.getVec3().f_82481_);
        Vector4d worldPositionO = VehicleVecUtils.transformPosition(this.getVehicleTransform(partialTicks), vec32.f_82479_, vec32.f_82480_, vec32.f_82481_);
        Vec3 startPos = new Vec3(worldPositionO.x, worldPositionO.y, worldPositionO.z);
        Vec3 endPos = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        Vec3 vec33 = startPos.m_82505_(endPos).m_82541_();
        Intrinsics.checkNotNullExpressionValue((Object)vec33, (String)"normalize(...)");
        return vec33;
    }

    public boolean m_142079_() {
        return false;
    }

    public final boolean checkObbOnGroundCached() {
        if (this.f_19797_ - this.obbOnGroundCacheTick >= 2) {
            this.cachedObbOnGround = VehicleMotionUtils.checkObbOnGround(this);
            this.obbOnGroundCacheTick = this.f_19797_;
        }
        return this.cachedObbOnGround;
    }

    /*
     * WARNING - void declaration
     */
    public void updateOBB() {
        HashMap transformCache = new HashMap(4);
        Iterable $this$forEach$iv = this.getObb();
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Object object;
            void $this$getOrPut$iv;
            OBBInfo obbInfo = (OBBInfo)element$iv;
            boolean bl = false;
            Map map = transformCache;
            String key$iv = obbInfo.getTransform();
            boolean $i$f$getOrPut = false;
            Object value$iv = $this$getOrPut$iv.get(key$iv);
            if (value$iv == null) {
                boolean bl2 = false;
                Matrix4d answer$iv = this.getTransformFromString(obbInfo.getTransform());
                $this$getOrPut$iv.put(key$iv, answer$iv);
                object = answer$iv;
            } else {
                object = value$iv;
            }
            Matrix4d transform = (Matrix4d)object;
            OBB obb = obbInfo.getOBB();
            Vector4d worldPos = this.transformPosition(transform, obbInfo.getPosition().f_82479_, obbInfo.getPosition().f_82480_, obbInfo.getPosition().f_82481_);
            if (this.hasTurret() && this.getSympatheticDetonated() && (StringsKt.equals$default((String)obbInfo.getTransform(), (String)"Turret", (boolean)false, (int)2, null) || StringsKt.equals$default((String)obbInfo.getTransform(), (String)"Barrel", (boolean)false, (int)2, null))) {
                obb.setExtents(new Vector3d(0.0, 0.0, 0.0));
            }
            obb.center.set((Vector3dc)VectorToolKt.toVector3d(new Vec3(worldPos.x, worldPos.y, worldPos.z)));
            obb.updateRotation(this.getRotationFromString(obbInfo.getRotation()));
            Vec3 rotate = obbInfo.getCustomRotate();
            obb.rotation().mul((Quaterniondc)new Quaterniond((Quaternionfc)Axis.f_252436_.m_252977_((float)rotate.f_82480_)));
            obb.rotation().mul((Quaterniondc)new Quaterniond((Quaternionfc)Axis.f_252529_.m_252977_((float)rotate.f_82479_)));
            obb.rotation().mul((Quaterniondc)new Quaterniond((Quaternionfc)Axis.f_252403_.m_252977_((float)rotate.f_82481_)));
        }
    }

    @Nullable
    public SoundEvent getShootSoundInstance() {
        for (GunData gunData : this.getGunDataMap().values()) {
            SoundEvent instance = gunData.get(GunProp.SOUND_INFO).getFireSoundInstances();
            if (instance == null) continue;
            return instance;
        }
        return SoundEvents.f_271165_;
    }

    @NotNull
    public SoundEvent getShootSoundInstance(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            SoundEvent soundEvent = SoundEvents.f_271165_;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent, (String)"EMPTY");
            return soundEvent;
        }
        GunData gunData2 = gunData;
        SoundEvent soundEvent = gunData2.get(GunProp.SOUND_INFO).getFireSoundInstances();
        if (soundEvent == null) {
            SoundEvent soundEvent2 = SoundEvents.f_271165_;
            soundEvent = soundEvent2;
            Intrinsics.checkNotNullExpressionValue((Object)soundEvent2, (String)"EMPTY");
        }
        return soundEvent;
    }

    public boolean isWeaponFiring(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return false;
        }
        GunData gunData2 = gunData;
        return gunData2.shootTimer.get() > 0;
    }

    public float weaponShootingVolume(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0.0f;
        }
        GunData gunData2 = gunData;
        return (float)gunData2.shootTimer.get() * 0.25f;
    }

    public float weaponShootingPitch(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 1.0f;
        }
        GunData gunData2 = gunData;
        return (float)((double)(0.98f + (float)gunData2.shootTimer.get() * 0.01f) - (gunData2.heat.get() > 80.0 ? (gunData2.heat.get() - (double)80) * 0.01 : 0.0));
    }

    public boolean isFiring() {
        int n = ((Collection)this.computed().seats()).size();
        for (int seatIndex = 0; seatIndex < n; ++seatIndex) {
            GunData gunData;
            SoundEvent instance;
            if (this.getGunData(seatIndex) == null || (instance = gunData.get(GunProp.SOUND_INFO).getFireSoundInstances()) == null || gunData.shootTimer.get() <= 0) continue;
            return true;
        }
        return false;
    }

    public float shootingVolume() {
        int n = ((Collection)this.computed().seats()).size();
        for (int seatIndex = 0; seatIndex < n; ++seatIndex) {
            GunData gunData;
            SoundEvent instance;
            if (this.getGunData(seatIndex) == null || (instance = gunData.get(GunProp.SOUND_INFO).getFireSoundInstances()) == null) continue;
            return (float)gunData.shootTimer.get() * 0.25f;
        }
        return 0.0f;
    }

    public float shootingPitch() {
        int n = ((Collection)this.computed().seats()).size();
        for (int seatIndex = 0; seatIndex < n; ++seatIndex) {
            GunData gunData;
            SoundEvent instance;
            if (this.getGunData(seatIndex) == null || (instance = gunData.get(GunProp.SOUND_INFO).getFireSoundInstances()) == null) continue;
            return (float)((double)(0.98f + (float)gunData.shootTimer.get() * 0.01f) - (gunData.heat.get() > 80.0 ? (gunData.heat.get() - (double)80) * 0.01 : 0.0));
        }
        return 1.0f;
    }

    protected final void updateBackupAmmoCount() {
        int n = this.getMaxPassengers();
        for (int i = 0; i < n; ++i) {
            GunData currentData;
            if (this.getGunData(i) == null) continue;
            if (!currentData.useBackpackAmmo()) {
                if (currentData.backupAmmoCount.get() == 0) continue;
                this.modifyGunData(i, VehicleEntity::updateBackupAmmoCount$lambda$54);
                continue;
            }
            currentData.cachedBackupAmmo = -1;
            int count = currentData.countBackupAmmo(this.getAmmoSupplier());
            if (currentData.backupAmmoCount.get() == count) continue;
            this.modifyGunData(i, arg_0 -> VehicleEntity.updateBackupAmmoCount$lambda$55(count, arg_0));
        }
    }

    @NotNull
    public Entity getAmmoSupplier() {
        return this;
    }

    public void handlePartDamaged(@NotNull OBBEntity obbEntity) {
        Intrinsics.checkNotNullParameter((Object)obbEntity, (String)"obbEntity");
        VehicleEffectUtils.handlePartDamaged(this, obbEntity);
    }

    public void handlePartHealth() {
        VehicleEffectUtils.handlePartHealth(this);
    }

    public void addRandomParticle(@NotNull ParticleOptions particleOptions, @NotNull Vec3 pos, float randomPos, @NotNull Level level, float speed, int count) {
        Intrinsics.checkNotNullParameter((Object)particleOptions, (String)"particleOptions");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)level, (String)"level");
        VehicleEffectUtils.addRandomParticle(this, particleOptions, pos, randomPos, level, speed, count);
    }

    public void addRandomParticle(@NotNull ParticleOptions particleOptions, @NotNull Vec3 pos, float randomPos, @NotNull Level level, int count, @NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)particleOptions, (String)"particleOptions");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        Intrinsics.checkNotNullParameter((Object)level, (String)"level");
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"vec3");
        VehicleEffectUtils.addRandomParticle(this, particleOptions, pos, randomPos, level, count, vec3);
    }

    public void defaultPartDamageEffect(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        VehicleEffectUtils.defaultPartDamageEffect(this, pos);
    }

    public void onTurretDamaged(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        this.defaultPartDamageEffect(pos);
    }

    public void onLeftWheelDamaged(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        this.defaultPartDamageEffect(pos);
    }

    public void onRightWheelDamaged(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        this.defaultPartDamageEffect(pos);
    }

    public void onEngine1Damaged(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        this.defaultPartDamageEffect(pos);
    }

    public void onEngine2Damaged(@NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        this.defaultPartDamageEffect(pos);
    }

    public void clearArrow() {
        if (this.f_19797_ % 5 != 0) {
            return;
        }
        List list = this.m_9236_().m_6249_((Entity)this, this.m_20191_().m_82377_(0.0, 0.5, 0.0), arg_0 -> VehicleEntity.clearArrow$lambda$57(VehicleEntity::clearArrow$lambda$56, arg_0));
        Intrinsics.checkNotNullExpressionValue((Object)list, (String)"getEntities(...)");
        Iterable $this$forEach$iv = list;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            Entity obj = (Entity)element$iv;
            boolean bl = false;
            obj.m_146870_();
        }
    }

    public void lowHealthWarning() {
        VehicleEffectUtils.lowHealthWarning(this);
    }

    @Nullable
    public Vec3 turretBurnEffectPos() {
        return VehicleEffectUtils.turretBurnEffectPos(this);
    }

    public void playLowHealthParticle() {
        VehicleEffectUtils.playLowHealthParticle(this);
    }

    public void adjustTurretAngle() {
        VehicleWeaponUtils.adjustTurretAngle(this);
    }

    public int getSelectedWeapon(int seatIndex) {
        Integer n;
        List<Integer> list = this.getSelectedWeapon();
        boolean bl = 0 <= seatIndex ? seatIndex < list.size() : false;
        if (bl) {
            n = list.get(seatIndex);
        } else {
            int it = seatIndex;
            boolean bl2 = false;
            n = -1;
        }
        return ((Number)n).intValue();
    }

    public void turretAutoAimFromVector(@NotNull Vec3 shootVec) {
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        VehicleWeaponUtils.turretAutoAimFromVector(this, shootVec);
    }

    public void turretAutoAimFromUuid(@NotNull String uuid, @NotNull LivingEntity pLiving) {
        Intrinsics.checkNotNullParameter((Object)uuid, (String)"uuid");
        Intrinsics.checkNotNullParameter((Object)pLiving, (String)"pLiving");
        VehicleWeaponUtils.turretAutoAimFromUuid(this, uuid, pLiving);
    }

    public void m_7340_(@NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        this.clampRotation(entity);
    }

    public float getCustomTurretMinPitch() {
        return 0.0f;
    }

    public float getCustomTurretMaxPitch() {
        return 0.0f;
    }

    private final void clampRotation(Entity entity) {
        int index = this.getSeatIndex(entity);
        List<SeatInfo> seats = this.computed().seats();
        if (index < 0 || index >= seats.size()) {
            return;
        }
        SeatInfo seat = seats.get(index);
        Vec3 vec3 = this.getTransformDirection(1.0f, entity);
        if (Intrinsics.areEqual((Object)seat.transform, (Object)"Barrel") && this.getTurretControllerIndex() == this.getSeatIndex(entity) || Intrinsics.areEqual((Object)seat.transform, (Object)"WeaponStationBarrel") && this.getPassengerWeaponStationControllerIndex() == this.getSeatIndex(entity)) {
            vec3 = this.getTransformDirectionFromString(1.0f, entity, "Turret");
        }
        float minPitch = -seat.getMaxPitch() + this.getCustomTurretMaxPitch();
        float maxPitch = -seat.getMinPitch() - this.getCustomTurretMinPitch();
        float f = (float)Mth.m_14175_((double)((double)entity.m_146909_() - -VehicleVecUtils.getXRotFromVector(vec3)));
        float f1 = Mth.m_14036_((float)f, (float)minPitch, (float)maxPitch);
        entity.f_19860_ += f1 - f;
        entity.m_146926_(entity.m_146909_() + f1 - f);
        float minYaw = seat.getMinYaw();
        float maxYaw = seat.getMaxYaw();
        float f2 = (float)Mth.m_14175_((double)((double)entity.m_146908_() - -VehicleVecUtils.getYRotFromVector(vec3)));
        float f3 = Mth.m_14036_((float)f2, (float)minYaw, (float)maxYaw);
        entity.f_19859_ += f3 - f2;
        entity.m_146922_(entity.m_146908_() + f3 - f2);
        if (Intrinsics.areEqual((Object)seat.transform, (Object)"Turret") && this.getTurretControllerIndex() == this.getSeatIndex(entity)) {
            if (!entity.m_9236_().f_46443_) {
                return;
            }
            if (MinecraftUtil.getMc().f_91066_.m_92176_() != CameraType.FIRST_PERSON) {
                return;
            }
            float f4 = (float)Mth.m_14175_((double)((double)entity.m_146908_() - -VehicleVecUtils.getYRotFromVector(vec3)));
            float f5 = Mth.m_14036_((float)f2, (float)-16.0f, (float)16.0f);
            entity.f_19859_ += f5 - f4;
            entity.m_146922_(entity.m_146908_() + f5 - f4);
        }
    }

    public void copyEntityData(@NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        entity.m_146922_(entity.m_146908_() + this.getDestroyRot());
        int index = this.getSeatIndex(entity);
        SeatInfo seat = this.computed().seats().get(index);
        Vec3 vec3 = this.getTransformDirection(1.0f, entity);
        float yaw = -((float)VehicleVecUtils.getYRotFromVector(vec3));
        if (seat.getRotateWithVehicle()) {
            float vehicleYawDelta = Mth.m_14177_((float)(this.m_146908_() - this.f_19859_));
            entity.m_146922_(entity.m_146908_() + vehicleYawDelta);
        }
        if ((Intrinsics.areEqual((Object)seat.transform, (Object)"Vehicle") || Intrinsics.areEqual((Object)seat.transform, (Object)"VehicleFlat")) && !seat.getCanRotateHead()) {
            entity.m_146922_(yaw);
        }
        if (!seat.getCanRotateBody()) {
            entity.m_5618_(yaw);
        }
    }

    @NotNull
    public Vec3 getTransformDirection(float ticks, @NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        int index = this.getSeatIndex(entity);
        SeatInfo seat = this.computed().seats().get(index);
        float passengerRot = seat.getOrientation();
        Matrix4d transform = this.getTransformFromString(seat.transform, ticks).rotate((Quaternionfc)Axis.f_252436_.m_252977_(-passengerRot));
        Intrinsics.checkNotNull((Object)transform);
        Vector4d posO = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d pos = this.transformPosition(transform, 0.0, 0.0, 1.0);
        Vec3 vec3 = new Vec3(posO.x, posO.y, posO.z).m_82505_(new Vec3(pos.x, pos.y, pos.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getTransformDirectionNoOrientation(float ticks, @NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        int index = this.getSeatIndex(entity);
        SeatInfo seat = this.computed().seats().get(index);
        Matrix4d transform = this.getTransformFromString(seat.transform, ticks);
        Vector4d posO = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d pos = this.transformPosition(transform, 0.0, 0.0, 1.0);
        Vec3 vec3 = new Vec3(posO.x, posO.y, posO.z).m_82505_(new Vec3(pos.x, pos.y, pos.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getTransformDirectionFromString(float ticks, @NotNull Entity entity, @NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        Intrinsics.checkNotNullParameter((Object)string, (String)"string");
        int index = this.getSeatIndex(entity);
        SeatInfo seat = this.computed().seats().get(index);
        float passengerRot = seat.getOrientation();
        Matrix4d transform = this.getTransformFromString(string, ticks).rotate((Quaternionfc)Axis.f_252436_.m_252977_(-passengerRot));
        Intrinsics.checkNotNull((Object)transform);
        Vector4d posO = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d pos = this.transformPosition(transform, 0.0, 0.0, 1.0);
        Vec3 vec3 = new Vec3(posO.x, posO.y, posO.z).m_82505_(new Vec3(pos.x, pos.y, pos.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    public void m_19956_(@NotNull Entity passenger, @NotNull Entity.MoveFunction callback) {
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        Intrinsics.checkNotNullParameter((Object)callback, (String)"callback");
        if (!this.m_20363_(passenger)) {
            return;
        }
        int index = this.getSeatIndex(passenger);
        List<SeatInfo> seats = this.computed().seats();
        if (index < 0 || index >= seats.size()) {
            return;
        }
        SeatInfo seat = seats.get(index);
        this.passengerPos(passenger, callback, seat.getPosition(), seat.transform);
    }

    public void passengerPos(@NotNull Entity passenger, @NotNull Entity.MoveFunction callback, @NotNull Vec3 vec3, @Nullable String string) {
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        Intrinsics.checkNotNullParameter((Object)callback, (String)"callback");
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"vec3");
        Vector4d worldPosition = this.transformPosition(this.getTransformFromString(string), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
        EntityType entityType = passenger.m_6095_();
        Intrinsics.checkNotNullExpressionValue((Object)entityType, (String)"getType(...)");
        double yOffset = VehicleConfig.getPassengerYOffset(entityType);
        passenger.m_6034_(worldPosition.x, worldPosition.y + yOffset, worldPosition.z);
        callback.m_20372_(passenger, worldPosition.x, worldPosition.y + yOffset, worldPosition.z);
        this.copyEntityData(passenger);
    }

    @NotNull
    protected final HashMap<String, Function<Float, Matrix4d>> getPositionTransform() {
        return this.positionTransform;
    }

    protected final void setPositionTransform(@NotNull HashMap<String, Function<Float, Matrix4d>> hashMap) {
        Intrinsics.checkNotNullParameter(hashMap, (String)"<set-?>");
        this.positionTransform = hashMap;
    }

    @NotNull
    protected final HashMap<String, Function<Float, Vec3>> getVectorTransform() {
        return this.vectorTransform;
    }

    protected final void setVectorTransform(@NotNull HashMap<String, Function<Float, Vec3>> hashMap) {
        Intrinsics.checkNotNullParameter(hashMap, (String)"<set-?>");
        this.vectorTransform = hashMap;
    }

    @NotNull
    protected final HashMap<String, Function<Float, Quaterniond>> getRotationTransform() {
        return this.rotationTransform;
    }

    protected final void setRotationTransform(@NotNull HashMap<String, Function<Float, Quaterniond>> hashMap) {
        Intrinsics.checkNotNullParameter(hashMap, (String)"<set-?>");
        this.rotationTransform = hashMap;
    }

    protected final void registerTransforms() {
        ((Map)this.positionTransform).put("VehicleFlat", arg_0 -> VehicleEntity.registerTransforms$lambda$60(this, arg_0));
        ((Map)this.positionTransform).put("Turret", arg_0 -> VehicleEntity.registerTransforms$lambda$61(this, arg_0));
        ((Map)this.positionTransform).put("Barrel", arg_0 -> VehicleEntity.registerTransforms$lambda$62(this, arg_0));
        ((Map)this.positionTransform).put("WeaponStation", arg_0 -> VehicleEntity.registerTransforms$lambda$63(this, arg_0));
        ((Map)this.positionTransform).put("WeaponStationBarrel", arg_0 -> VehicleEntity.registerTransforms$lambda$64(this, arg_0));
        ((Map)this.positionTransform).put("Default", arg_0 -> VehicleEntity.registerTransforms$lambda$65(this, arg_0));
        ((Map)this.vectorTransform).put("Turret", arg_0 -> VehicleEntity.registerTransforms$lambda$66(this, arg_0));
        ((Map)this.vectorTransform).put("Barrel", arg_0 -> VehicleEntity.registerTransforms$lambda$67(this, arg_0));
        ((Map)this.vectorTransform).put("WeaponStationBarrel", arg_0 -> VehicleEntity.registerTransforms$lambda$68(this, arg_0));
        ((Map)this.vectorTransform).put("DeltaMovement", arg_0 -> VehicleEntity.registerTransforms$lambda$69(this, arg_0));
        ((Map)this.vectorTransform).put("Up", arg_0 -> VehicleEntity.registerTransforms$lambda$70(this, arg_0));
        ((Map)this.vectorTransform).put("Default", arg_0 -> VehicleEntity.registerTransforms$lambda$71(this, arg_0));
        ((Map)this.rotationTransform).put("WeaponStation", arg_0 -> VehicleEntity.registerTransforms$lambda$72(this, arg_0));
        ((Map)this.rotationTransform).put("WeaponStationBarrel", arg_0 -> VehicleEntity.registerTransforms$lambda$73(this, arg_0));
        ((Map)this.rotationTransform).put("Turret", arg_0 -> VehicleEntity.registerTransforms$lambda$74(this, arg_0));
        ((Map)this.rotationTransform).put("Barrel", arg_0 -> VehicleEntity.registerTransforms$lambda$75(this, arg_0));
        ((Map)this.rotationTransform).put("RotationsYaw", arg_0 -> VehicleEntity.registerTransforms$lambda$76(this, arg_0));
        ((Map)this.rotationTransform).put("Default", arg_0 -> VehicleEntity.registerTransforms$lambda$77(this, arg_0));
    }

    @NotNull
    public Matrix4d getTransformFromString(@Nullable String string) {
        return this.getTransformFromString(string, 1.0f);
    }

    @NotNull
    public Matrix4d getTransformFromString(@Nullable String string, float ticks) {
        Function<Float, Matrix4d> function = ((Map)this.positionTransform).getOrDefault(string, this.positionTransform.get("Default"));
        Intrinsics.checkNotNull(function);
        Matrix4d matrix4d = function.apply(Float.valueOf(ticks));
        Intrinsics.checkNotNullExpressionValue((Object)matrix4d, (String)"apply(...)");
        return matrix4d;
    }

    @NotNull
    public Vec3 getVectorFromString(@Nullable String string) {
        return this.getVectorFromString(string, 0.0f);
    }

    @NotNull
    public Vec3 getVectorFromString(@Nullable String string, float ticks) {
        Function<Float, Vec3> function = ((Map)this.vectorTransform).getOrDefault(string, this.vectorTransform.get("Default"));
        Intrinsics.checkNotNull(function);
        Vec3 vec3 = function.apply(Float.valueOf(ticks));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"apply(...)");
        return vec3;
    }

    /*
     * Unable to fully structure code
     */
    @NotNull
    public Vec3 getVectorFromString(@NotNull String string, float ticks, int seatIndex) {
        block9: {
            block8: {
                Intrinsics.checkNotNullParameter((Object)string, (String)"string");
                entity = this.getNthEntity(seatIndex);
                var5_5 = string;
                switch (var5_5.hashCode()) {
                    case 1059157114: {
                        if (var5_5.equals("Passenger")) break;
                        ** break;
                    }
                    case -1744063760: {
                        if (!var5_5.equals("ClientCamera")) {
                            ** break;
                        }
                        break block8;
                    }
                    case 2076354: {
                        if (!var5_5.equals("Bomb")) ** break;
                        v0 = this.bombHitPos(this.getNthEntity(seatIndex)).m_82546_(this.getShootPosForHud(this.getNthEntity(seatIndex), 1.0f));
                        v1 = v0;
                        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"subtract(...)");
                        break block9;
                    }
                }
                v2 = entity;
                var6_6 = v2 != null ? v2.m_20252_(ticks) : this.m_20252_(ticks);
                Intrinsics.checkNotNull((Object)var6_6);
                v1 = var6_6;
                break block9;
            }
            if (entity != null && entity.m_9236_().f_46443_) {
                v1 = this.cameraDirection();
            } else {
                v3 = this.m_20252_(ticks);
                v1 = v3;
                Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"getViewVector(...)");
            }
            break block9;
lbl31:
            // 4 sources

            v1 = this.getVectorFromString(string, ticks);
        }
        return v1;
    }

    @NotNull
    public Vec3 cameraDirection() {
        return new Vec3(MinecraftUtil.getMc().f_91063_.m_109153_().m_253058_());
    }

    @NotNull
    public Quaterniond getRotationFromString(@Nullable String string) {
        return this.getRotationFromString(string, 0.0f);
    }

    @NotNull
    public Quaterniond getRotationFromString(@Nullable String string, float ticks) {
        Function<Float, Quaterniond> function = ((Map)this.rotationTransform).getOrDefault(string, this.rotationTransform.get("Default"));
        Intrinsics.checkNotNull(function);
        Quaterniond quaterniond = function.apply(Float.valueOf(ticks));
        Intrinsics.checkNotNullExpressionValue((Object)quaterniond, (String)"apply(...)");
        return quaterniond;
    }

    @NotNull
    public Vec3 getShootPos(int seatIndex, float ticks) {
        return this.getShootPos(this.getNthEntity(seatIndex), ticks);
    }

    @NotNull
    public Vec3 bombHitPos(@Nullable Entity entity) {
        Vec3 vec3;
        GunData gunData = this.getGunData(entity);
        if (gunData != null && this.m_9236_().f_46443_) {
            Level level = this.m_9236_();
            Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
            vec3 = ProjectileCalculator.calculatePreciseImpactPoint(level, this.getShootPosForHud(entity, 1.0f), this.getShootVec(entity, 1.0f), this.m_20184_().m_82553_() * ((Number)gunData.get(GunProp.VELOCITY)).doubleValue(), -((double)this.getProjectileGravity(entity)));
        } else {
            Vec3 vec32 = Vec3.f_82478_;
            Intrinsics.checkNotNull((Object)vec32);
            vec3 = vec32;
        }
        return vec3;
    }

    @NotNull
    public Vec3 getShootPos(@Nullable Entity entity, float ticks) {
        GunData data = this.getGunData(this.getSeatIndex(entity));
        if (data != null) {
            Vec3 vec3 = data.firePosition();
            Vector4d worldPosition = this.transformPosition(this.getTransformFromString(data.get(GunProp.SHOOT_POS).getTransform(), ticks), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
            return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        }
        Vec3 vec3 = this.m_20299_(ticks);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getEyePosition(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getShootPos(@NotNull String weaponName, float ticks) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData data = this.getGunData(weaponName);
        if (data != null) {
            Vec3 vec3 = data.firePosition();
            Vector4d worldPosition = this.transformPosition(this.getTransformFromString(data.get(GunProp.SHOOT_POS).getTransform(), ticks), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
            return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        }
        Vec3 vec3 = this.m_20299_(ticks);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getEyePosition(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getShootPosForHud(@Nullable Entity entity, float ticks) {
        GunData data = this.getGunData(this.getSeatIndex(entity));
        if (data != null) {
            Vec3 vec3 = data.firePositionForHud();
            Vector4d worldPosition = this.transformPosition(this.getTransformFromString(data.get(GunProp.SHOOT_POS).getTransform(), ticks), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
            return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        }
        Vec3 vec3 = this.m_20299_(ticks);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getEyePosition(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getShootDirectionForHud(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        GunData gunData = this.getGunData(this.getSeatIndex(entity));
        if (gunData == null) {
            Vec3 vec3 = this.m_20252_(partialTicks);
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getViewVector(...)");
            return vec3;
        }
        GunData data = gunData;
        StringOrVec3 stringOrVec3 = data.fireDirectionForHud();
        if (stringOrVec3 == null) {
            return this.getViewVec(entity, partialTicks);
        }
        if (stringOrVec3.isString()) {
            String string = stringOrVec3.getString();
            Intrinsics.checkNotNull((Object)string);
            return this.getVectorFromString(string, partialTicks, this.getSeatIndex(entity));
        }
        Vec3 vec3 = stringOrVec3.getVec3();
        Intrinsics.checkNotNull((Object)vec3);
        Vec3 vec32 = vec3;
        Vector4d worldPosition = this.transformPosition(this.getTransformFromString(data.get(GunProp.SHOOT_POS).getTransform(), partialTicks), vec32.f_82479_ + stringOrVec3.getVec3().f_82479_, vec32.f_82480_ + stringOrVec3.getVec3().f_82480_, vec32.f_82481_ + stringOrVec3.getVec3().f_82481_);
        Vector4d worldPositionO = this.transformPosition(this.getTransformFromString(data.get(GunProp.SHOOT_POS).getTransform(), partialTicks), vec32.f_82479_, vec32.f_82480_, vec32.f_82481_);
        Vec3 startPos = new Vec3(worldPositionO.x, worldPositionO.y, worldPositionO.z);
        Vec3 endPos = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        Vec3 vec33 = startPos.m_82505_(endPos).m_82541_();
        Intrinsics.checkNotNullExpressionValue((Object)vec33, (String)"normalize(...)");
        return vec33;
    }

    @Nullable
    public Vec3 getDefaultBarrelDirection(int seatIndex, float ticks) {
        return this.getDefaultBarrelDirection(this.getNthEntity(seatIndex), ticks);
    }

    @Nullable
    public Vec3 getDefaultBarrelDirection(@Nullable Entity entity, float partialTicks) {
        return VehicleVecUtils.getDefaultBarrelDirection(this, entity, partialTicks);
    }

    @Nullable
    public Vec3 getDefaultBarrelDirection(@NotNull String weaponName, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        return VehicleVecUtils.getDefaultBarrelDirection(this, weaponName, partialTicks);
    }

    @Nullable
    public Vec3 getShootVec(int seatIndex, float ticks) {
        return this.getShootVec(this.getNthEntity(seatIndex), ticks);
    }

    @NotNull
    public Vec3 getShootVec(@Nullable Entity entity, float partialTicks) {
        return VehicleVecUtils.getShootVec(this, entity, partialTicks);
    }

    @NotNull
    public Vec3 getShootVec(@NotNull String weaponName, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        return VehicleVecUtils.getShootVec(this, weaponName, partialTicks);
    }

    @NotNull
    public Vec3 getViewVec(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getViewVec(this, entity, partialTicks);
    }

    @Nullable
    public Vec3 getViewPos(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getViewPos(this, entity, partialTicks);
    }

    @Nullable
    public Vec3 getSeekVec(@Nullable Entity entity, float partialTicks) {
        return VehicleVecUtils.getSeekVec(this, entity, partialTicks);
    }

    @Nullable
    public Vec3 getSeekVec(int seatIndex, float partialTicks) {
        return VehicleVecUtils.getSeekVec(this, this.getNthEntity(seatIndex), partialTicks);
    }

    @Nullable
    public Entity getPlayerLookAtEntityOnVehicle(@NotNull Entity shooter, double entityReach, float partialTick) {
        Intrinsics.checkNotNullParameter((Object)shooter, (String)"shooter");
        Vec3 eye = this.getShootPosForHud(shooter, partialTick);
        double distance = entityReach * entityReach;
        HitResult hitResult = TraceTool.pickNew(eye, 512.0, this);
        Vec3 viewVec = this.getViewVec(shooter, partialTick);
        Vec3 toVec = eye.m_82520_(viewVec.f_82479_ * entityReach, viewVec.f_82480_ * entityReach, viewVec.f_82481_ * entityReach);
        AABB aabb = this.m_20191_().m_82369_(viewVec.m_82490_(entityReach)).m_82400_(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.m_37287_((Entity)this, (Vec3)eye, (Vec3)toVec, (AABB)aabb, arg_0 -> VehicleEntity.getPlayerLookAtEntityOnVehicle$lambda$78(shooter, arg_0), (double)distance);
        if (entityHitResult != null) {
            hitResult = (HitResult)entityHitResult;
        }
        if (hitResult.m_6662_() == HitResult.Type.ENTITY && entityHitResult != null) {
            return entityHitResult.m_82443_();
        }
        return null;
    }

    public float getProjectileVelocity(@Nullable Entity entity) {
        GunData gunData = this.getGunData(this.getSeatIndex(entity));
        if (gunData == null) {
            return 25.0f;
        }
        GunData gunData2 = gunData;
        if (gunData2.get(GunProp.ADD_SHOOTER_DELTA_MOVEMENT).booleanValue()) {
            return (float)(this.m_20184_().m_82553_() * ((Number)gunData2.get(GunProp.VELOCITY)).doubleValue());
        }
        return (float)((Number)gunData2.get(GunProp.VELOCITY)).doubleValue();
    }

    public float getProjectileVelocity(int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return 25.0f;
        }
        GunData gunData2 = gunData;
        if (gunData2.get(GunProp.ADD_SHOOTER_DELTA_MOVEMENT).booleanValue()) {
            return (float)(this.m_20184_().m_82553_() * ((Number)gunData2.get(GunProp.VELOCITY)).doubleValue());
        }
        return (float)((Number)gunData2.get(GunProp.VELOCITY)).doubleValue();
    }

    public float getProjectileVelocity(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 25.0f;
        }
        GunData gunData2 = gunData;
        if (gunData2.get(GunProp.ADD_SHOOTER_DELTA_MOVEMENT).booleanValue()) {
            return (float)(this.m_20184_().m_82553_() * ((Number)gunData2.get(GunProp.VELOCITY)).doubleValue());
        }
        return (float)((Number)gunData2.get(GunProp.VELOCITY)).doubleValue();
    }

    public float getProjectileVelocity(@Nullable GunData gunData) {
        if (gunData == null) {
            return 25.0f;
        }
        if (gunData.get(GunProp.ADD_SHOOTER_DELTA_MOVEMENT).booleanValue()) {
            return (float)(this.m_20184_().m_82553_() * ((Number)gunData.get(GunProp.VELOCITY)).doubleValue());
        }
        return (float)((Number)gunData.get(GunProp.VELOCITY)).doubleValue();
    }

    public float getProjectileGravity(@Nullable Entity entity) {
        GunData gunData = this.getGunData(this.getSeatIndex(entity));
        if (gunData == null) {
            return 0.0f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.GRAVITY)).doubleValue();
    }

    public float getProjectileGravity(int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return 0.0f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.GRAVITY)).doubleValue();
    }

    public float getProjectileGravity(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0.0f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.GRAVITY)).doubleValue();
    }

    public float getProjectileGravity(@Nullable GunData gunData) {
        if (gunData == null) {
            return 0.0f;
        }
        return (float)((Number)gunData.get(GunProp.GRAVITY)).doubleValue();
    }

    public float getProjectileSpread(@Nullable Entity entity) {
        GunData gunData = this.getGunData(this.getSeatIndex(entity));
        if (gunData == null) {
            return 0.5f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.SPREAD)).doubleValue();
    }

    public float getProjectileSpread(int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return 0.5f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.SPREAD)).doubleValue();
    }

    public float getProjectileSpread(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0.5f;
        }
        GunData gunData2 = gunData;
        return (float)((Number)gunData2.get(GunProp.SPREAD)).doubleValue();
    }

    public float getProjectileSpread(@Nullable GunData gunData) {
        if (gunData == null) {
            return 0.5f;
        }
        return (float)((Number)gunData.get(GunProp.SPREAD)).doubleValue();
    }

    public void passengerWeaponAutoAimFormUuid(@Nullable String uuid, @NotNull LivingEntity pLiving) {
        Intrinsics.checkNotNullParameter((Object)pLiving, (String)"pLiving");
        VehicleWeaponUtils.passengerWeaponAutoAimFormUuid(this, uuid, pLiving);
    }

    public void passengerWeaponAutoAimFormVector(@NotNull Vec3 shootVec) {
        Intrinsics.checkNotNullParameter((Object)shootVec, (String)"shootVec");
        VehicleWeaponUtils.passengerWeaponAutoAimFormVector(this, shootVec);
    }

    public void adjustWeaponControllerAngle() {
        VehicleWeaponUtils.adjustWeaponControllerAngle(this);
    }

    public void destroy() {
        Entity driver = this.getLastDriver();
        if (this.getLocked() && driver instanceof Player) {
            Object[] objectArray = new Object[]{FormatTool.format1D$default(this.m_20185_(), null, 2, null), FormatTool.format1D$default(this.m_20186_(), null, 2, null), FormatTool.format1D$default(this.m_20189_(), null, 2, null), this.m_5446_()};
            ((Player)driver).m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.vehicle.lock_destroy", (Object[])objectArray).m_130940_(ChatFormatting.RED), false);
        }
        VehicleDestroyUtils.destroy(this);
    }

    public void vehicleExplosion(@NotNull DestroyInfo destroyInfo) {
        Intrinsics.checkNotNullParameter((Object)destroyInfo, (String)"destroyInfo");
        VehicleDestroyUtils.vehicleExplosion(this, destroyInfo);
    }

    @NotNull
    public CustomExplosion.Builder createCustomExplosion() {
        return new CustomExplosion.Builder(this).attacker(this.getLastAttacker());
    }

    public void crashPassengers() {
        VehicleDestroyUtils.crashPassengers(this);
    }

    public void explodePassengers() {
        VehicleDestroyUtils.explodePassengers(this);
    }

    public void travel() {
        DefaultVehicleData computed = this.computed();
        EngineType engineType = computed.getEngineType();
        if (engineType == EngineType.EMPTY) {
            return;
        }
        if (engineType == EngineType.FIXED) {
            this.fixedEngine();
            return;
        }
        if (this.getEngineInfo() == null) {
            JsonObject engineInfo = computed.getEngineInfo();
            try {
                Object serializer = switch (WhenMappings.$EnumSwitchMapping$2[engineType.ordinal()]) {
                    case 1 -> EngineInfo.Wheel.Companion.serializer();
                    case 2 -> EngineInfo.Track.Companion.serializer();
                    case 3 -> EngineInfo.Helicopter.Companion.serializer();
                    case 4 -> EngineInfo.Ship.Companion.serializer();
                    case 5 -> EngineInfo.Aircraft.Companion.serializer();
                    case 6 -> EngineInfo.WheelChair.Companion.serializer();
                    case 7 -> EngineInfo.Tom6.Companion.serializer();
                    case 8 -> EngineInfo.AirShip.Companion.serializer();
                    default -> null;
                };
                this.setEngineInfo(serializer == null ? null : (EngineInfo)DataLoader.INSTANCE.getJSON().decodeFromJsonElement((DeserializationStrategy)serializer, JsonUtil.toKxJson((JsonElement)engineInfo)));
            }
            catch (Exception e) {
                Mod.LOGGER.error("Failed to parse engine info for vehicle {}, {}", (Object)this, (Object)e);
            }
        } else {
            EngineInfo engineInfo = this.getEngineInfo();
            Intrinsics.checkNotNull((Object)engineInfo);
            engineInfo.work(this);
        }
    }

    public float getEngineSoundVolume() {
        float f;
        DefaultVehicleData computed = this.computed();
        EngineType engineType = computed.getEngineType();
        if (engineType == EngineType.EMPTY || engineType == EngineType.FIXED) {
            return 0.0f;
        }
        EngineInfo engineInfo = this.getEngineInfo();
        if (engineInfo != null) {
            f = engineInfo.getEngineSoundVolume();
        } else {
            JsonElement jsonElement = computed.getEngineInfo().get("EngineSoundVolume");
            f = jsonElement != null ? jsonElement.getAsFloat() : 0.4f;
        }
        float engineSoundVolume = f;
        return switch (WhenMappings.$EnumSwitchMapping$2[engineType.ordinal()]) {
            case 2 -> org.joml.Math.max((float)Mth.m_14154_((float)this.getPower()), (float)Mth.m_14154_((float)(1.4f * this.getDeltaRot()))) * engineSoundVolume;
            case 3 -> this.getSynchedPropellerRot() * engineSoundVolume;
            case 8 -> Mth.m_14036_((float)(Mth.m_14154_((float)this.getPower()) * engineSoundVolume + engineSoundVolume), (float)engineSoundVolume, (float)1.5f);
            default -> Mth.m_14154_((float)this.getPower()) * engineSoundVolume;
        };
    }

    @NotNull
    public Matrix4d getVehicleTransform(float ticks) {
        Matrix4d transformV = this.getVehicleYOffsetTransform(ticks);
        Matrix4d transform = new Matrix4d();
        Vector4d worldPosition = this.transformPosition(transform, 0.0, -this.getRotateOffsetHeight(), 0.0);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        return transformV;
    }

    @NotNull
    public Matrix4d getVehicleTransformWithCustomPitch(float ticks) {
        Matrix4d transformV = this.getVehicleYOffsetTransform(ticks);
        Matrix4d transform = new Matrix4d();
        Vector4d worldPosition = this.transformPosition(transform, 0.0, -this.getRotateOffsetHeight(), 0.0);
        transformV.translate(worldPosition.x, worldPosition.y, worldPosition.z);
        transformV.rotate((Quaternionfc)Axis.f_252529_.m_252977_(this.getTurretCustomPitch()));
        return transformV;
    }

    @NotNull
    public Matrix4d getVehicleYOffsetTransform(float partialTicks) {
        return VehicleVecUtils.getVehicleYOffsetTransform(this, partialTicks);
    }

    public double getRotateOffsetHeight() {
        return this.computed().getRotateOffsetHeight();
    }

    public double getLaserBaseScale() {
        return this.computed().getLaserScale();
    }

    @NotNull
    public Matrix4d getVehicleFlatTransform(float partialTicks) {
        return VehicleVecUtils.getVehicleFlatTransform(this, partialTicks);
    }

    @NotNull
    public Matrix4d getClientVehicleTransform(float partialTicks) {
        return VehicleVecUtils.getClientVehicleTransform(this, partialTicks);
    }

    public boolean hasTurret() {
        return this.getTurretPos() != null;
    }

    @Nullable
    public Vec3 getTurretPos() {
        return this.computed().getTurretPos();
    }

    public int getTurretControllerIndex() {
        return this.computed().getTurretControllerIndex();
    }

    public float getTurretTurnXSpeed() {
        return this.computed().getTurretTurnSpeed().f_82470_;
    }

    public float getTurretTurnYSpeed() {
        return this.computed().getTurretTurnSpeed().f_82471_;
    }

    public float getTurretMinYaw() {
        return this.computed().getTurretYawRange().f_82470_;
    }

    public float getTurretMaxYaw() {
        return this.computed().getTurretYawRange().f_82471_;
    }

    public float getTurretMinPitch() {
        return this.computed().getTurretPitchRange().f_82470_;
    }

    public float getTurretMaxPitch() {
        return this.computed().getTurretPitchRange().f_82471_;
    }

    @Nullable
    public Vec3 getBarrelPosition() {
        return this.computed().getBarrelPos();
    }

    public boolean hasPassengerWeaponStation() {
        return this.getPassengerWeaponStationPosition() != null;
    }

    @Nullable
    public Vec3 getPassengerWeaponStationPosition() {
        return this.computed().getPassengerWeaponStationPos();
    }

    @Nullable
    public Vec3 getPassengerWeaponStationBarrelPosition() {
        return this.computed().getPassengerWeaponStationBarrelPos();
    }

    public int getPassengerWeaponStationControllerIndex() {
        return this.computed().getPassengerWeaponStationControllerIndex();
    }

    public float getPassengerWeaponYSpeed() {
        return this.computed().getPassengerWeaponStationTurnSpeed().f_82471_;
    }

    public float getPassengerWeaponXSpeed() {
        return this.computed().getPassengerWeaponStationTurnSpeed().f_82470_;
    }

    public float getPassengerWeaponMinPitch() {
        return this.computed().getPassengerWeaponStationPitchRange().f_82470_;
    }

    public float getPassengerWeaponMaxPitch() {
        return this.computed().getPassengerWeaponStationPitchRange().f_82471_;
    }

    public float getPassengerWeaponMinYaw() {
        return this.computed().getPassengerWeaponStationYawRange().f_82470_;
    }

    public float getPassengerWeaponMaxYaw() {
        return this.computed().getPassengerWeaponStationYawRange().f_82471_;
    }

    public float getTurretCustomPitch() {
        return this.computed().getTurretCustomPitch();
    }

    @NotNull
    public Matrix4d getTurretTransform(float partialTicks) {
        return VehicleVecUtils.getTurretTransform(this, partialTicks);
    }

    @NotNull
    public Vec3 getTurretVector(float pPartialTicks) {
        return VehicleVecUtils.getTurretVector(this, pPartialTicks);
    }

    @NotNull
    public Matrix4d getBarrelTransform(float partialTicks) {
        return VehicleVecUtils.getBarrelTransform(this, partialTicks);
    }

    @NotNull
    public Matrix4d getGunTransform(float partialTicks) {
        return VehicleVecUtils.getGunTransform(this, partialTicks);
    }

    @NotNull
    public Matrix4d getPassengerWeaponStationBarrelTransform(float partialTicks) {
        return VehicleVecUtils.getPassengerWeaponStationBarrelTransform(this, partialTicks);
    }

    @NotNull
    public Vec3 getPassengerWeaponStationVector(float partialTicks) {
        return VehicleVecUtils.getPassengerWeaponStationVector(this, partialTicks);
    }

    @NotNull
    public Vector4d transformPosition(@NotNull Matrix4d transform, double x, double y, double z) {
        Intrinsics.checkNotNullParameter((Object)transform, (String)"transform");
        Vector4d vector4d = transform.transform(new Vector4d(x, y, z, 1.0));
        Intrinsics.checkNotNullExpressionValue((Object)vector4d, (String)"transform(...)");
        return vector4d;
    }

    public void handleClientSync() {
        if (!this.m_9236_().f_46443_) {
            this.setServerYaw(this.m_146908_());
            this.setServerPitch(this.m_146909_());
        }
        if (this.m_6109_()) {
            this.interpolationSteps = 0;
            this.m_217006_(this.m_20185_(), this.m_20186_(), this.m_20189_());
        }
        if (this.interpolationSteps <= 0) {
            return;
        }
        double interpolatedX = this.m_20185_() + (this.xO - this.m_20185_()) / (double)this.interpolationSteps;
        double interpolatedY = this.m_20186_() + (this.yO - this.m_20186_()) / (double)this.interpolationSteps;
        double interpolatedZ = this.m_20189_() + (this.zO - this.m_20189_()) / (double)this.interpolationSteps;
        float diffY = Mth.m_14177_((float)(this.getServerYaw() - this.m_146908_()));
        float diffX = Mth.m_14177_((float)(this.getServerPitch() - this.m_146909_()));
        this.m_146922_(this.m_146908_() + 0.1f * diffY);
        this.m_146926_(this.m_146909_() + 0.1f * diffX);
        this.m_6034_(interpolatedX, interpolatedY, interpolatedZ);
        this.interpolationSteps += -1;
    }

    public void m_6453_(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.xO = x;
        this.yO = y;
        this.zO = z;
        this.setServerYaw(yaw);
        this.setServerPitch(pitch);
        this.interpolationSteps = 10;
    }

    @Deprecated(message="")
    @NotNull
    protected final Vec3 getDismountOffset(double vehicleWidth, double passengerWidth) {
        return VehicleMiscUtils.getDismountOffset(this, vehicleWidth, passengerWidth);
    }

    @NotNull
    public Vec3 m_7688_(@NotNull LivingEntity passenger) {
        Vec3 vec3;
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        int index = this.getTagSeatIndex((Entity)passenger);
        if (index < 0) {
            Vec3 vec32 = super.m_7688_(passenger);
            Intrinsics.checkNotNull((Object)vec32);
            vec3 = vec32;
        } else {
            vec3 = this.getDismountLocationForIndex(passenger, index);
        }
        return vec3;
    }

    @NotNull
    public Vec3 getDismountLocationForIndex(@NotNull LivingEntity passenger, int index) {
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        List<SeatInfo> seats = this.computed().seats();
        if (index >= seats.size()) {
            return this.dismount(passenger);
        }
        DismountInfo dismountInfo = seats.get(index).getDismountInfo();
        if (dismountInfo != null) {
            Vec3 vec3 = dismountInfo.getPosition();
            if (vec3 != null) {
                Vector4d worldPosition = this.transformPosition(this.getTransformFromString(dismountInfo.getTransform()), vec3.f_82479_, vec3.f_82480_, vec3.f_82481_);
                return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
            }
            return this.dismount(passenger);
        }
        return this.dismount(passenger);
    }

    @NotNull
    public Vec3 dismount(@NotNull LivingEntity passenger) {
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        Vec3 vec3d = VehicleMiscUtils.getDismountOffset(this, this.m_20205_() * Mth.f_13994_, passenger.m_20205_() * Mth.f_13994_);
        double ox = this.m_20185_() - vec3d.f_82479_;
        double oz = this.m_20189_() + vec3d.f_82481_;
        BlockPos exitPos = new BlockPos((int)ox, (int)this.m_20186_(), (int)oz);
        BlockPos floorPos = exitPos.m_7495_();
        if (!this.m_9236_().m_46801_(floorPos)) {
            double floorHeight;
            List list = new ArrayList();
            double exitHeight = this.m_9236_().m_45573_(exitPos);
            if (DismountHelper.m_38439_((double)exitHeight)) {
                list.add(new Vec3(ox, (double)exitPos.m_123342_() + exitHeight, oz));
            }
            if (DismountHelper.m_38439_((double)(floorHeight = this.m_9236_().m_45573_(floorPos)))) {
                list.add(new Vec3(ox, (double)floorPos.m_123342_() + floorHeight, oz));
            }
            UnmodifiableIterator unmodifiableIterator = passenger.m_7431_().iterator();
            Intrinsics.checkNotNullExpressionValue((Object)unmodifiableIterator, (String)"iterator(...)");
            UnmodifiableIterator unmodifiableIterator2 = unmodifiableIterator;
            while (unmodifiableIterator2.hasNext()) {
                Pose entityPose = (Pose)unmodifiableIterator2.next();
                for (Vec3 vec3d2 : list) {
                    if (!DismountHelper.m_150279_((CollisionGetter)((CollisionGetter)this.m_9236_()), (Vec3)vec3d2, (LivingEntity)passenger, (Pose)entityPose)) continue;
                    passenger.m_20124_(entityPose);
                    return vec3d2;
                }
            }
        }
        Vec3 vec3 = super.m_7688_(passenger);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getDismountLocationForPassenger(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getEjectionPosition(@NotNull LivingEntity passenger, int index) {
        Intrinsics.checkNotNullParameter((Object)passenger, (String)"passenger");
        List<SeatInfo> seats = this.computed().seats();
        if (index >= seats.size()) {
            Vec3 vec3 = passenger.m_20182_();
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"position(...)");
            return vec3;
        }
        DismountInfo dismountInfo = seats.get(index).getDismountInfo();
        if (dismountInfo != null) {
            Vec3 vec3 = dismountInfo.getEjectPosition();
            if (vec3 == null) {
                Vec3 vec32 = passenger.m_20182_();
                Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"position(...)");
                return vec32;
            }
            Vec3 vec33 = vec3;
            Vector4d worldPosition = this.transformPosition(this.getTransformFromString(dismountInfo.getTransform()), vec33.f_82479_, vec33.f_82480_, vec33.f_82481_);
            return new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        }
        Vec3 vec3 = passenger.m_20182_();
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"position(...)");
        return vec3;
    }

    public boolean allowEjection(int seatIndex) {
        Object object = (SeatInfo)CollectionsKt.getOrNull(this.computed().seats(), (int)seatIndex);
        return object != null && (object = ((SeatInfo)object).getDismountInfo()) != null ? ((DismountInfo)object).getCanEject() : false;
    }

    public void removeSeatIndexTag(@NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        entity.getPersistentData().m_128473_(TAG_SEAT_INDEX);
    }

    @NotNull
    public Vec3 getEjectionMovement(@Nullable LivingEntity entity, int index) {
        Object object = (SeatInfo)CollectionsKt.getOrNull(this.computed().seats(), (int)index);
        if (object == null || (object = ((SeatInfo)object).getDismountInfo()) == null) {
            Vec3 vec3 = this.m_20184_();
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getDeltaMovement(...)");
            return vec3;
        }
        Object dismountInfo = object;
        double force = ((DismountInfo)dismountInfo).getEjectForce();
        StringOrVec3 stringOrVec3 = ((DismountInfo)dismountInfo).getEjectDirection();
        if (stringOrVec3 == null) {
            Vec3 vec3 = this.m_20184_().m_82549_(this.getUpVec(1.0f).m_82490_(force));
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"add(...)");
            return vec3;
        }
        if (stringOrVec3.isString()) {
            Vec3 vec3 = this.m_20184_();
            String string = stringOrVec3.getString();
            Intrinsics.checkNotNull((Object)string);
            Vec3 vec32 = vec3.m_82549_(this.getVectorFromString(string, 1.0f, this.getSeatIndex((Entity)entity)).m_82490_(force));
            Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"add(...)");
            return vec32;
        }
        Vec3 vec3 = stringOrVec3.getVec3();
        Intrinsics.checkNotNull((Object)vec3);
        Vec3 vec33 = vec3;
        Vector4d worldPosition = this.transformPosition(this.getTransformFromString(((DismountInfo)dismountInfo).getTransform()), vec33.f_82479_ + stringOrVec3.getVec3().f_82479_, vec33.f_82480_ + stringOrVec3.getVec3().f_82480_, vec33.f_82481_ + stringOrVec3.getVec3().f_82481_);
        Vector4d worldPositionO = this.transformPosition(this.getTransformFromString(((DismountInfo)dismountInfo).getTransform()), vec33.f_82479_, vec33.f_82480_, vec33.f_82481_);
        Vec3 startPos = new Vec3(worldPositionO.x, worldPositionO.y, worldPositionO.z);
        Vec3 endPos = new Vec3(worldPosition.x, worldPosition.y, worldPosition.z);
        Vec3 vec34 = this.m_20184_().m_82549_(startPos.m_82505_(endPos).m_82541_().m_82490_(force));
        Intrinsics.checkNotNullExpressionValue((Object)vec34, (String)"add(...)");
        return vec34;
    }

    @Nullable
    public ResourceLocation getVehicleIcon() {
        return this.computed().getVehicleIcon();
    }

    public boolean allowFreeCam() {
        return this.computed().getAllowFreeCam();
    }

    @NotNull
    public Vec3 getUpVec(float ticks) {
        Matrix4d transform = this.getVehicleTransform(ticks);
        Vector4d force0 = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d force1 = this.transformPosition(transform, 0.0, 1.0, 0.0);
        Vec3 vec3 = new Vec3(force0.x, force0.y, force0.z).m_82505_(new Vec3(force1.x, force1.y, force1.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    @NotNull
    public Vec3 getRightVec(float ticks) {
        Matrix4d transform = this.getVehicleTransform(ticks);
        Vector4d force0 = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d force1 = this.transformPosition(transform, -1.0, 0.0, 0.0);
        Vec3 vec3 = new Vec3(force0.x, force0.y, force0.z).m_82505_(new Vec3(force1.x, force1.y, force1.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    public void m_5997_(double pX, double pY, double pZ) {
    }

    @NotNull
    public Vec3 getBarrelVector(float pPartialTicks) {
        Matrix4d transform = this.getBarrelTransform(pPartialTicks);
        Vector4d rootPosition = this.transformPosition(transform, 0.0, 0.0, 0.0);
        Vector4d targetPosition = this.transformPosition(transform, 0.0, 0.0, 1.0);
        Vec3 vec3 = new Vec3(rootPosition.x, rootPosition.y, rootPosition.z).m_82505_(new Vec3(targetPosition.x, targetPosition.y, targetPosition.z));
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"vectorTo(...)");
        return vec3;
    }

    public float getBarrelXRot(float pPartialTicks) {
        return Mth.m_14179_((float)pPartialTicks, (float)(this.getTurretXRotO() - this.f_19860_), (float)(this.getTurretXRot() - this.m_146909_()));
    }

    public float getBarrelYRot(float pPartialTick) {
        return -Mth.m_14179_((float)pPartialTick, (float)(this.getTurretYRotO() - this.f_19859_), (float)(this.getTurretYRot() - this.m_146908_()));
    }

    public float getGunXRot(float pPartialTicks) {
        return Mth.m_14179_((float)pPartialTicks, (float)(this.getGunXRotO() - this.f_19860_), (float)(this.getGunXRot() - this.m_146909_()));
    }

    public float getGunYRot(float pPartialTick) {
        return -Mth.m_14179_((float)pPartialTick, (float)(this.getGunYRotO() - this.f_19859_), (float)(this.getGunYRot() - this.m_146908_()));
    }

    public float getTurretYaw(float pPartialTick) {
        return Mth.m_14179_((float)pPartialTick, (float)this.getTurretYRotO(), (float)this.getTurretYRot());
    }

    public float getTurretPitch(float pPartialTick) {
        return Mth.m_14179_((float)pPartialTick, (float)this.getTurretXRotO(), (float)this.getTurretXRot());
    }

    @NotNull
    public Vec3 getCameraPos(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getCameraPos(this, entity, partialTicks);
    }

    @NotNull
    public Vec3 cameraDirection(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getCameraDirection(this, entity, partialTicks);
    }

    @NotNull
    public Vec3 getZoomPos(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getZoomPos(this, entity, partialTicks);
    }

    @NotNull
    public Vec3 getZoomDirection(@NotNull Entity entity, float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return VehicleVecUtils.getZoomDirection(this, entity, partialTicks);
    }

    public boolean m_142389_() {
        return true;
    }

    public double getMouseSensitivity() {
        return this.computed().getMouseSensitivity();
    }

    public float getPassengerRenderScale() {
        return this.computed().getPassengerRenderScale();
    }

    public float getMass() {
        return this.computed().getMass();
    }

    public void m_20256_(@NotNull Vec3 pDeltaMovement) {
        Vec3 acceleration;
        Intrinsics.checkNotNullParameter((Object)pDeltaMovement, (String)"pDeltaMovement");
        Vec3 currentMomentum = this.m_20184_();
        double currentSpeedSq = currentMomentum.m_82556_();
        double newSpeedSq = pDeltaMovement.m_82556_();
        if (newSpeedSq > currentSpeedSq && (acceleration = pDeltaMovement.m_82546_(currentMomentum)).m_82556_() > 8.0) {
            Vec3 limitedAcceleration = acceleration.m_82541_().m_82490_(0.125);
            Vec3 finalMomentum = currentMomentum.m_82549_(limitedAcceleration);
            super.m_20256_(finalMomentum);
            return;
        }
        super.m_20256_(pDeltaMovement);
    }

    public void m_246865_(@NotNull Vec3 pAddend) {
        Intrinsics.checkNotNullParameter((Object)pAddend, (String)"pAddend");
        Vec3 pAddend2 = pAddend;
        double length = pAddend2.m_82553_();
        if (length > 0.1) {
            pAddend2 = pAddend2.m_82490_(0.1 / length);
        }
        super.m_246865_(pAddend2);
    }

    public double getSensitivity(double original, boolean zoom, int seatIndex, boolean isOnGround) {
        SeatInfo seat = this.computed().seats().get(seatIndex);
        Vec3 sensitivity = seat.getSensitivity();
        return zoom ? sensitivity.f_82479_ * original : (MinecraftUtil.getMc().f_91066_.m_92176_().m_90612_() ? sensitivity.f_82480_ * original : sensitivity.f_82481_ * original);
    }

    @Nullable
    public ResourceLocation getVehicleItemIcon() {
        return this.computed().getContainerIcon();
    }

    public boolean isEnclosed(int index) {
        List<SeatInfo> seats = this.computed().seats();
        SeatInfo seatInfo = (SeatInfo)CollectionsKt.getOrNull(seats, (int)index);
        if (seatInfo == null) {
            return false;
        }
        SeatInfo seat = seatInfo;
        if (seat.isEnclosed() == null) {
            return seat.getHidePassenger();
        }
        Boolean bl = seat.isEnclosed();
        Intrinsics.checkNotNull((Object)bl);
        return bl;
    }

    public boolean isEnclosed(@Nullable Entity passenger) {
        return this.isEnclosed(this.getSeatIndex(passenger));
    }

    public boolean banHand(@Nullable LivingEntity entity) {
        int index = this.getSeatIndex((Entity)entity);
        GunData gunData = this.getGunData(index);
        SeatInfo seatInfo = (SeatInfo)CollectionsKt.getOrNull(this.computed().seats(), (int)index);
        if (seatInfo == null) {
            return false;
        }
        SeatInfo seat = seatInfo;
        return gunData != null || seat.getBanHand();
    }

    public boolean hidePassenger(int index) {
        List<SeatInfo> seats = this.computed().seats();
        if (index < 0 || index >= seats.size()) {
            return false;
        }
        SeatInfo seat = seats.get(index);
        return seat.getHidePassenger();
    }

    public boolean hidePassenger(@Nullable Entity passenger) {
        return this.hidePassenger(this.getSeatIndex(passenger));
    }

    public int getAmmoCount(@Nullable LivingEntity living) {
        GunData gunData = this.getGunData(this.getSeatIndex((Entity)living));
        if (gunData == null) {
            return 0;
        }
        GunData data = gunData;
        return this.getAmmo(data);
    }

    public int getAmmoCount(int seatIndex) {
        GunData gunData = this.getGunData(seatIndex);
        if (gunData == null) {
            return 0;
        }
        GunData data = gunData;
        return this.getAmmo(data);
    }

    public int getAmmoCount(@NotNull String weaponName) {
        Intrinsics.checkNotNullParameter((Object)weaponName, (String)"weaponName");
        GunData gunData = this.getGunData(weaponName);
        if (gunData == null) {
            return 0;
        }
        GunData data = gunData;
        return this.getAmmo(data);
    }

    public int getAmmo(@NotNull GunData data) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        return data.useBackpackAmmo() ? data.backupAmmoCount.get() : data.ammo.get();
    }

    @Nullable
    public ItemStack m_142340_() {
        if (!this.getRetrieveItems().isEmpty()) {
            return (ItemStack)CollectionsKt.firstOrNull(this.getRetrieveItems());
        }
        EntityType entityType = this.m_6095_();
        Intrinsics.checkNotNullExpressionValue((Object)entityType, (String)"getType(...)");
        return ContainerBlockItem.Companion.createInstance(entityType);
    }

    public boolean useAircraftCamera(int seatIndex) {
        SeatInfo seat = (SeatInfo)CollectionsKt.getOrNull(this.computed().seats(), (int)seatIndex);
        if (seat != null) {
            CameraPos data;
            CameraPos cameraPos = data = seat.getCameraPos();
            return cameraPos != null ? cameraPos.getUseAircraftCamera() : false;
        }
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public Vec2 getCameraRotation(float partialTicks, @NotNull Player player, boolean zoom, boolean isFirstPerson) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        return VehicleClientUtils.getCameraRotation(this, partialTicks, player, zoom, isFirstPerson);
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public Vec3 getCameraPosition(float partialTicks, @NotNull Player player, boolean zoom, boolean isFirstPerson) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        return VehicleClientUtils.getCameraPosition(this, partialTicks, player, zoom, isFirstPerson);
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean useFixedCameraPos(@Nullable Entity entity) {
        return VehicleClientUtils.useFixedCameraPos(this, entity);
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        Intrinsics.checkNotNullParameter(cap, (String)"cap");
        if (cap == ForgeCapabilities.ENERGY && this.hasEnergyStorage()) {
            LazyOptional lazyOptional = this.energyOptional.cast();
            Intrinsics.checkNotNullExpressionValue((Object)lazyOptional, (String)"cast(...)");
            return lazyOptional;
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER && this.hasContainer()) {
            LazyOptional lazyOptional = this.itemHandler.cast();
            Intrinsics.checkNotNullExpressionValue((Object)lazyOptional, (String)"cast(...)");
            return lazyOptional;
        }
        LazyOptional lazyOptional = super.getCapability(cap, side);
        Intrinsics.checkNotNullExpressionValue((Object)lazyOptional, (String)"getCapability(...)");
        return lazyOptional;
    }

    @NotNull
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        Intrinsics.checkNotNullParameter(cap, (String)"cap");
        return this.getCapability(cap, null);
    }

    public void invalidateCaps() {
        super.invalidateCaps();
        if (this.hasContainer()) {
            this.itemHandler.invalidate();
        }
        if (this.hasEnergyStorage()) {
            this.energyOptional.invalidate();
        }
    }

    public void reviveCaps() {
        super.reviveCaps();
        if (this.hasContainer()) {
            this.itemHandler = LazyOptional.of(() -> VehicleEntity.reviveCaps$lambda$79(this));
        }
        if (this.hasEnergyStorage()) {
            this.energyOptional = LazyOptional.of(() -> VehicleEntity.reviveCaps$lambda$80(this));
        }
    }

    public double getDefaultZoom(@Nullable Entity entity) {
        GunData gunData;
        GunData gunData2 = gunData = this.getGunData(this.getSeatIndex(entity));
        return gunData2 != null ? ((Number)gunData2.get(GunProp.DEFAULT_ZOOM)).doubleValue() : 1.0;
    }

    public boolean canCrushEntities() {
        return true;
    }

    public void fixedEngine() {
        this.m_6478_(MoverType.SELF, new Vec3(0.0, this.m_20184_().f_82480_, 0.0));
        if (this.m_20096_()) {
            Vec3 vec3 = Vec3.f_82478_;
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"ZERO");
            this.m_20256_(vec3);
        } else {
            this.m_20256_(new Vec3(0.0, this.m_20184_().f_82480_, 0.0));
        }
    }

    public void releaseSmokeDecoy(@NotNull Vec3 vec3) {
        Intrinsics.checkNotNullParameter((Object)vec3, (String)"vec3");
        VehicleWeaponUtils.releaseSmokeDecoy(this, vec3);
    }

    public void releaseDecoy() {
        VehicleWeaponUtils.releaseDecoy(this);
    }

    public int countDecoyItem() {
        int n;
        if (this.hasSmokeDecoy()) {
            Entity entity = this;
            Object object = ModItems.VEHICLE_SMOKE_AMMO.get();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
            n = InventoryTool.countItem(entity, (Item)object);
        } else {
            Entity entity = this;
            Object object = ModItems.FLYING_FLARE_AMMO.get();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"get(...)");
            n = InventoryTool.countItem(entity, (Item)object);
        }
        return n;
    }

    public void terrainCompact(@NotNull List<Vec3> positions) {
        Intrinsics.checkNotNullParameter(positions, (String)"positions");
        VehicleMotionUtils.terrainCompact(this, positions);
    }

    @NotNull
    public Matrix4d getWheelsTransform(float partialTicks) {
        return VehicleMotionUtils.getWheelsTransform(this, partialTicks);
    }

    public void moveOnDragonTeeth() {
        VehicleMotionUtils.handleVehicleMoveOnDragonTeeth(this);
    }

    public void collideBlocks() {
        if (this.f_19797_ % 4 != 0) {
            return;
        }
        if (this.computed().getEngineType() == EngineType.FIXED) {
            return;
        }
        if (this.m_20184_().m_82556_() < 0.01) {
            return;
        }
        VehicleMotionUtils.collideBlocks(this);
    }

    @Nullable
    public Entity getLastAttacker() {
        Level level = this.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        return EntityFindUtil.findEntity(level, this.getLastAttackerUUID());
    }

    @NotNull
    public Vec3 vCollide(@NotNull Vec3 pVec) {
        Vec3 vec3;
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)pVec, (String)"pVec");
        this.blockCollisionCacheTick = -1;
        if (ignoreEntityGroundCheckStepping) {
            ignoreEntityGroundCheckStepping = false;
            bl = true;
        } else {
            bl = this.m_20096_();
        }
        boolean effectiveOnGround = bl;
        Vec3 vec32 = VehicleMotionUtils.resolveObbWorldCollision(this, pVec);
        boolean flag = !(pVec.f_82479_ == vec32.f_82479_);
        boolean flag1 = !(pVec.f_82480_ == vec32.f_82480_);
        boolean flag2 = !(pVec.f_82481_ == vec32.f_82481_);
        boolean flag3 = effectiveOnGround || flag1 && pVec.f_82480_ < 0.0;
        float stepHeight = this.getStepHeight();
        if (stepHeight > 0.0f && flag3 && (flag || flag2)) {
            List list;
            AABB movedBox;
            List stepUpObbs;
            OBB collisionObb;
            Vec3 vec31 = VehicleMotionUtils.resolveObbWorldCollision(this, new Vec3(pVec.f_82479_, (double)stepHeight, pVec.f_82481_));
            Vec3 vec322 = VehicleMotionUtils.resolveObbWorldCollision(this, new Vec3(0.0, (double)stepHeight, 0.0));
            if (vec322.f_82480_ < (double)stepHeight) {
                collisionObb = this.getCollisionOBB();
                Vec3 horizPart = null;
                if (collisionObb != null) {
                    stepUpObbs = CollectionsKt.listOf((Object)collisionObb.move(new Vec3(vec322.f_82479_, vec322.f_82480_, vec322.f_82481_)));
                    horizPart = VehicleMotionUtils.resolveObbWorldCollision(this, new Vec3(pVec.f_82479_, 0.0, pVec.f_82481_), stepUpObbs);
                } else {
                    movedBox = this.m_20191_().m_82383_(vec322);
                    list = this.m_9236_().m_183134_((Entity)this, movedBox.m_82363_(pVec.f_82479_, 0.0, pVec.f_82481_));
                    horizPart = Entity.m_198894_((Entity)this, (Vec3)new Vec3(pVec.f_82479_, 0.0, pVec.f_82481_), (AABB)movedBox, (Level)this.m_9236_(), (List)list);
                }
                Vec3 vec33 = horizPart.m_82549_(vec322);
                if (vec33.m_165925_() > vec31.m_165925_()) {
                    vec31 = vec33;
                }
            }
            if (vec31.m_165925_() > vec32.m_165925_()) {
                Vec3 vec33;
                collisionObb = this.getCollisionOBB();
                Vec3 stepDown = null;
                if (collisionObb != null) {
                    stepUpObbs = CollectionsKt.listOf((Object)collisionObb.move(new Vec3(vec31.f_82479_, vec31.f_82480_, vec31.f_82481_)));
                    stepDown = VehicleMotionUtils.resolveObbWorldCollision(this, new Vec3(0.0, -vec31.f_82480_ + pVec.f_82480_, 0.0), stepUpObbs);
                    OBB steppedObb = ((OBB)stepUpObbs.get(0)).move(stepDown);
                    Pair<Double, Double> pair = VehicleMotionUtils.checkBottomSupportRatio(this, steppedObb);
                    double supportRatio = ((Number)pair.component1()).doubleValue();
                    double correction = ((Number)pair.component2()).doubleValue();
                    if (supportRatio >= 0.75 && correction > 0.0) {
                        stepDown = new Vec3(stepDown.f_82479_, stepDown.f_82480_ + correction, stepDown.f_82481_);
                    }
                } else {
                    movedBox = this.m_20191_().m_82383_(vec31);
                    list = this.m_9236_().m_183134_((Entity)this, movedBox.m_82363_(0.0, -vec31.f_82480_ + pVec.f_82480_, 0.0));
                    stepDown = Entity.m_198894_((Entity)this, (Vec3)new Vec3(0.0, -vec31.f_82480_ + pVec.f_82480_, 0.0), (AABB)movedBox, (Level)this.m_9236_(), (List)list);
                }
                if (!ValkyrienSkiesCompat.hasMod()) {
                    Vec3 vec34 = vec31.m_82549_(stepDown);
                    vec33 = vec34;
                    Intrinsics.checkNotNullExpressionValue((Object)vec34, (String)"add(...)");
                } else {
                    Entity entity = this;
                    Vec3 vec35 = vec31.m_82549_(stepDown);
                    Intrinsics.checkNotNullExpressionValue((Object)vec35, (String)"add(...)");
                    AABB aABB = this.m_20191_();
                    Intrinsics.checkNotNullExpressionValue((Object)aABB, (String)"getBoundingBox(...)");
                    Level level = this.m_9236_();
                    Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
                    vec33 = ValkyrienSkiesCompat.adjustMovementForShipCollisions(entity, vec35, aABB, level);
                }
                return vec33;
            }
        }
        if (!ValkyrienSkiesCompat.hasMod()) {
            vec3 = vec32;
        } else {
            Entity entity = this;
            AABB aABB = this.m_20191_();
            Intrinsics.checkNotNullExpressionValue((Object)aABB, (String)"getBoundingBox(...)");
            Level level = this.m_9236_();
            Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
            vec3 = ValkyrienSkiesCompat.adjustMovementForShipCollisions(entity, vec32, aABB, level);
        }
        return vec3;
    }

    public void vMove(@NotNull MoverType pType, @NotNull Vec3 pPos) {
        Intrinsics.checkNotNullParameter((Object)pType, (String)"pType");
        Intrinsics.checkNotNullParameter((Object)pPos, (String)"pPos");
        Vec3 pPos2 = pPos;
        this.m_9236_().m_46473_().m_6180_("move");
        pPos2 = this.m_5763_(pPos2, pType);
        Vec3 vec3 = this.vCollide(pPos2);
        double d0 = vec3.m_82556_();
        if (d0 > 1.0E-7) {
            this.m_6034_(this.m_20185_() + vec3.f_82479_, this.m_20186_() + vec3.f_82480_, this.m_20189_() + vec3.f_82481_);
        }
        this.m_9236_().m_46473_().m_7238_();
        this.m_9236_().m_46473_().m_6180_("rest");
        boolean flag4 = !Mth.m_14082_((double)pPos2.f_82479_, (double)vec3.f_82479_);
        boolean flag = !Mth.m_14082_((double)pPos2.f_82481_, (double)vec3.f_82481_);
        this.f_19862_ = flag4 || flag;
        this.f_19863_ = !(pPos2.f_82480_ == vec3.f_82480_);
        this.f_201939_ = this.f_19863_ && pPos2.f_82480_ < 0.0;
        this.f_185931_ = this.f_19862_ ? this.m_196406_(vec3) : false;
        this.m_289603_(this.f_201939_, vec3);
        if (!this.m_20096_() && this.checkObbOnGroundCached()) {
            this.m_6853_(true);
        }
        BlockPos blockpos = this.m_216986_(0.2f);
        BlockState blockstate = this.m_9236_().m_8055_(blockpos);
        if (this.m_213877_()) {
            this.m_9236_().m_46473_().m_7238_();
        } else {
            if (this.f_19862_) {
                Vec3 vec31 = this.m_20184_();
                if (this.f_185931_) {
                    this.m_20334_(flag4 ? vec31.f_82479_ * 0.3 : vec31.f_82479_, vec31.f_82480_, flag ? vec31.f_82481_ * 0.3 : vec31.f_82481_);
                } else {
                    this.m_20334_(flag4 ? 0.0 : vec31.f_82479_, vec31.f_82480_, flag ? 0.0 : vec31.f_82481_);
                }
            }
            Block block = blockstate.m_60734_();
            if (!(pPos2.f_82480_ == vec3.f_82480_)) {
                block.m_5548_((BlockGetter)this.m_9236_(), (Entity)this);
            }
            if (this.m_20096_()) {
                block.m_141947_(this.m_9236_(), blockpos, blockstate, (Entity)this);
            }
            this.m_9236_().m_46473_().m_7238_();
        }
    }

    public void m_6478_(@NotNull MoverType movementType, @NotNull Vec3 movement) {
        Intrinsics.checkNotNullParameter((Object)movementType, (String)"movementType");
        Intrinsics.checkNotNullParameter((Object)movement, (String)"movement");
        if (!this.m_9236_().m_5776_()) {
            ignoreEntityGroundCheckStepping = true;
        }
        if (this.getCollisionOBBInfo() != null) {
            this.vMove(movementType, movement);
        } else {
            super.m_6478_(movementType, movement);
        }
        if (this.getLastTickSpeed() < 0.2 || this.getCollisionCoolDown() > 0 || this instanceof DroneEntity) {
            return;
        }
        Entity driver = this.getLastDriver();
        if (this.f_19863_) {
            if (this.getVehicleType() == VehicleType.AIRPLANE && ((double)this.getSynchedGearRot() > 0.15 && !(this instanceof Tom6Entity) || Mth.m_14154_((float)this.getRoll()) > 20.0f || Mth.m_14154_((float)this.m_146909_()) > 30.0f)) {
                RegistryAccess registryAccess = this.m_9236_().m_9598_();
                Intrinsics.checkNotNullExpressionValue((Object)registryAccess, (String)"registryAccess(...)");
                Entity entity = this;
                Entity entity2 = driver;
                if (entity2 == null) {
                    entity2 = this;
                }
                this.m_6469_(ModDamageTypes.causeVehicleStrikeDamage(registryAccess, entity, entity2), this.isWreck() ? 0.0f : (float)((double)((float)8 + Mth.m_14154_((float)(this.getRoll() * 0.2f))) * (this.getLastTickSpeed() - 0.4) * (this.getLastTickSpeed() - 0.4)));
                Direction direction = Direction.m_122366_((double)this.m_20184_().m_7096_(), (double)this.m_20184_().m_7098_(), (double)this.m_20184_().m_7094_()).m_122424_();
                Intrinsics.checkNotNullExpressionValue((Object)direction, (String)"getOpposite(...)");
                this.bounceVertical(direction);
            } else if ((double)Mth.m_14154_((float)((float)this.getLastTickVerticalSpeed())) > 0.4) {
                RegistryAccess registryAccess = this.m_9236_().m_9598_();
                Intrinsics.checkNotNullExpressionValue((Object)registryAccess, (String)"registryAccess(...)");
                Entity entity = this;
                Entity entity3 = driver;
                if (entity3 == null) {
                    entity3 = this;
                }
                this.m_6469_(ModDamageTypes.causeVehicleStrikeDamage(registryAccess, entity, entity3), this.isWreck() ? 0.0f : (float)((double)24 * (((double)Mth.m_14154_((float)((float)this.getLastTickVerticalSpeed())) - 0.4) * (this.getLastTickSpeed() - 0.4) * (this.getLastTickSpeed() - 0.4))));
                if (!this.m_9236_().f_46443_) {
                    this.m_9236_().m_6269_(null, (Entity)this, (SoundEvent)ModSounds.VEHICLE_STRIKE.get(), this.m_5720_(), 1.0f, 1.0f);
                }
                Direction direction = Direction.m_122366_((double)this.m_20184_().m_7096_(), (double)this.m_20184_().m_7098_(), (double)this.m_20184_().m_7094_()).m_122424_();
                Intrinsics.checkNotNullExpressionValue((Object)direction, (String)"getOpposite(...)");
                this.bounceVertical(direction);
            }
        }
        if (this.f_19862_) {
            RegistryAccess registryAccess = this.m_9236_().m_9598_();
            Intrinsics.checkNotNullExpressionValue((Object)registryAccess, (String)"registryAccess(...)");
            Entity entity = this;
            Entity entity4 = driver;
            if (entity4 == null) {
                entity4 = this;
            }
            this.m_6469_(ModDamageTypes.causeVehicleStrikeDamage(registryAccess, entity, entity4), (float)((double)18 * ((this.getLastTickSpeed() - 0.2) * (this.getLastTickSpeed() - 0.2))));
            Direction direction = Direction.m_122366_((double)this.m_20184_().m_7096_(), (double)this.m_20184_().m_7098_(), (double)this.m_20184_().m_7094_()).m_122424_();
            Intrinsics.checkNotNullExpressionValue((Object)direction, (String)"getOpposite(...)");
            this.bounceHorizontal(direction);
            if (!this.m_9236_().f_46443_) {
                this.m_9236_().m_6269_(null, (Entity)this, (SoundEvent)ModSounds.VEHICLE_STRIKE.get(), this.m_5720_(), 1.0f, 1.0f);
            }
            this.setCollisionCoolDown(4);
            this.setCrash(true);
            this.setPower(this.getPower() * 0.8f);
        }
    }

    public void m_289603_(boolean onGround, @NotNull Vec3 movement) {
        Intrinsics.checkNotNullParameter((Object)movement, (String)"movement");
        if (this.getCollisionOBBInfo() != null) {
            Intrinsics.checkNotNull((Object)this, (String)"null cannot be cast to non-null type com.atsuishio.superbwarfare.mixins.EntityOnGroundAccessor");
            ((EntityOnGroundAccessor)((Object)this)).sbw$setOnGroundRaw(onGround);
        } else {
            super.m_289603_(onGround, movement);
        }
    }

    public void bounceHorizontal(@NotNull Direction direction) {
        Intrinsics.checkNotNullParameter((Object)direction, (String)"direction");
        VehicleMotionUtils.bounceHorizontal(this, direction);
    }

    public void bounceVertical(@NotNull Direction direction) {
        Intrinsics.checkNotNullParameter((Object)direction, (String)"direction");
        VehicleMotionUtils.bounceVertical(this, direction);
    }

    public void preventStacking() {
        VehicleMotionUtils.preventStacking(this);
    }

    public void pushNew(double pX, double pY, double pZ) {
        Vec3 vec3 = this.m_20184_().m_82520_(pX, pY, pZ);
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"add(...)");
        this.m_20256_(vec3);
    }

    public void supportEntities() {
        VehicleMotionUtils.supportEntities(this);
    }

    @NotNull
    public final RandomSource getRandom() {
        RandomSource randomSource = this.f_19796_;
        Intrinsics.checkNotNullExpressionValue((Object)randomSource, (String)"random");
        return randomSource;
    }

    public void crushEntities() {
        VehicleMotionUtils.crushEntities(this);
    }

    @NotNull
    public Vector3f getForwardDirection() {
        Vector3f vector3f = new Vector3f(Mth.m_14031_((float)(-this.m_146908_() * ((float)Math.PI / 180))), 0.0f, Mth.m_14089_((float)(this.m_146908_() * ((float)Math.PI / 180)))).normalize();
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"normalize(...)");
        return vector3f;
    }

    @NotNull
    public Vector3f getRightDirection() {
        Vector3f vector3f = new Vector3f(Mth.m_14089_((float)(-this.m_146908_() * ((float)Math.PI / 180))), 0.0f, Mth.m_14031_((float)(this.m_146908_() * ((float)Math.PI / 180)))).normalize();
        Intrinsics.checkNotNullExpressionValue((Object)vector3f, (String)"normalize(...)");
        return vector3f;
    }

    @Nullable
    public SoundEvent getEngineSound() {
        return this.computed().getEngineSound();
    }

    public double getAcceleration() {
        return this.getAbsoluteSpeed() - this.getAbsoluteSpeedO();
    }

    public int getTrackAnimationLength() {
        return 100;
    }

    public boolean hasDecoy() {
        return this.computed().getHasDecoy();
    }

    public boolean hasSmokeDecoy() {
        return this.computed().getSmokeDecoy();
    }

    public boolean engineRunning() {
        return this.getVehicleType() == VehicleType.AIRSHIP ? this.getHealth() > 0.0f : org.joml.Math.abs((float)this.getPower()) > 0.0f;
    }

    @NotNull
    public List<ItemStack> getRetrieveItems() {
        return CollectionsKt.listOf((Object)ContainerBlockItem.Companion.createInstance(this));
    }

    public int getHudColor() {
        return this.computed().getHudColor().get();
    }

    public int getLaserColor() {
        return this.computed().getLaserColor().get();
    }

    public float getPower() {
        return ((Number)EntityUtilKt.getValue(POWER, this, $$delegatedProperties[15])).floatValue();
    }

    public void setPower(float f) {
        EntityUtilKt.setValue(POWER, this, $$delegatedProperties[15], Float.valueOf(f));
    }

    private static Object getPower$delegate(VehicleEntity vehicleEntity) {
        return POWER;
    }

    public float getDeltaRot() {
        return ((Number)EntityUtilKt.getValue(DELTA_ROT, this, $$delegatedProperties[16])).floatValue();
    }

    public void setDeltaRot(float f) {
        EntityUtilKt.setValue(DELTA_ROT, this, $$delegatedProperties[16], Float.valueOf(f));
    }

    private static Object getDeltaRot$delegate(VehicleEntity vehicleEntity) {
        return DELTA_ROT;
    }

    public int getDecoyCount() {
        return ((Number)EntityUtilKt.getValue(DECOY_COUNT, this, $$delegatedProperties[17])).intValue();
    }

    public void setDecoyCount(int n) {
        EntityUtilKt.setValue(DECOY_COUNT, this, $$delegatedProperties[17], n);
    }

    private static Object getDecoyCount$delegate(VehicleEntity vehicleEntity) {
        return DECOY_COUNT;
    }

    public int getDecoyReloadCoolDown() {
        return ((Number)EntityUtilKt.getValue(DECOY_RELOAD_COOLDOWN, this, $$delegatedProperties[18])).intValue();
    }

    public void setDecoyReloadCoolDown(int n) {
        EntityUtilKt.setValue(DECOY_RELOAD_COOLDOWN, this, $$delegatedProperties[18], n);
    }

    private static Object getDecoyReloadCoolDown$delegate(VehicleEntity vehicleEntity) {
        return DECOY_RELOAD_COOLDOWN;
    }

    public float getSynchedPropellerRot() {
        return ((Number)EntityUtilKt.getValue(SYNCHED_PROPELLER_ROT, this, $$delegatedProperties[19])).floatValue();
    }

    public void setSynchedPropellerRot(float f) {
        EntityUtilKt.setValue(SYNCHED_PROPELLER_ROT, this, $$delegatedProperties[19], Float.valueOf(f));
    }

    private static Object getSynchedPropellerRot$delegate(VehicleEntity vehicleEntity) {
        return SYNCHED_PROPELLER_ROT;
    }

    public int getDecoyItemCount() {
        return ((Number)EntityUtilKt.getValue(DECOY_ITEM_COUNT, this, $$delegatedProperties[20])).intValue();
    }

    public void setDecoyItemCount(int n) {
        EntityUtilKt.setValue(DECOY_ITEM_COUNT, this, $$delegatedProperties[20], n);
    }

    private static Object getDecoyItemCount$delegate(VehicleEntity vehicleEntity) {
        return DECOY_ITEM_COUNT;
    }

    public float getPropellerRot() {
        return ((Number)EntityUtilKt.getValue(PROPELLER_ROT, this, $$delegatedProperties[21])).floatValue();
    }

    public void setPropellerRot(float f) {
        EntityUtilKt.setValue(PROPELLER_ROT, this, $$delegatedProperties[21], Float.valueOf(f));
    }

    private static Object getPropellerRot$delegate(VehicleEntity vehicleEntity) {
        return PROPELLER_ROT;
    }

    public float getPropellerRotO() {
        return this.propellerRotO;
    }

    public void setPropellerRotO(float f) {
        this.propellerRotO = f;
    }

    public float getPlaneBreak() {
        return ((Number)EntityUtilKt.getValue(PLANE_BREAK, this, $$delegatedProperties[22])).floatValue();
    }

    public void setPlaneBreak(float f) {
        EntityUtilKt.setValue(PLANE_BREAK, this, $$delegatedProperties[22], Float.valueOf(f));
    }

    private static Object getPlaneBreak$delegate(VehicleEntity vehicleEntity) {
        return PLANE_BREAK;
    }

    public float getSynchedGearRot() {
        return ((Number)EntityUtilKt.getValue(SYNCHED_GEAR_ROT, this, $$delegatedProperties[23])).floatValue();
    }

    public void setSynchedGearRot(float f) {
        EntityUtilKt.setValue(SYNCHED_GEAR_ROT, this, $$delegatedProperties[23], Float.valueOf(f));
    }

    private static Object getSynchedGearRot$delegate(VehicleEntity vehicleEntity) {
        return SYNCHED_GEAR_ROT;
    }

    public boolean getGearUp() {
        return EntityUtilKt.getValue(GEAR_UP, this, $$delegatedProperties[24]);
    }

    public void setGearUp(boolean bl) {
        EntityUtilKt.setValue(GEAR_UP, this, $$delegatedProperties[24], bl);
    }

    private static Object getGearUp$delegate(VehicleEntity vehicleEntity) {
        return GEAR_UP;
    }

    public boolean getSubEngineDamaged() {
        return EntityUtilKt.getValue(SUB_ENGINE_DAMAGED, this, $$delegatedProperties[25]);
    }

    public void setSubEngineDamaged(boolean bl) {
        EntityUtilKt.setValue(SUB_ENGINE_DAMAGED, this, $$delegatedProperties[25], bl);
    }

    private static Object getSubEngineDamaged$delegate(VehicleEntity vehicleEntity) {
        return SUB_ENGINE_DAMAGED;
    }

    public float getSubEngineHealth() {
        return ((Number)EntityUtilKt.getValue(SUB_ENGINE_HEALTH, this, $$delegatedProperties[26])).floatValue();
    }

    public void setSubEngineHealth(float f) {
        EntityUtilKt.setValue(SUB_ENGINE_HEALTH, this, $$delegatedProperties[26], Float.valueOf(f));
    }

    private static Object getSubEngineHealth$delegate(VehicleEntity vehicleEntity) {
        return SUB_ENGINE_HEALTH;
    }

    public boolean getMainEngineDamaged() {
        return EntityUtilKt.getValue(MAIN_ENGINE_DAMAGED, this, $$delegatedProperties[27]);
    }

    public void setMainEngineDamaged(boolean bl) {
        EntityUtilKt.setValue(MAIN_ENGINE_DAMAGED, this, $$delegatedProperties[27], bl);
    }

    private static Object getMainEngineDamaged$delegate(VehicleEntity vehicleEntity) {
        return MAIN_ENGINE_DAMAGED;
    }

    public float getMainEngineHealth() {
        return ((Number)EntityUtilKt.getValue(MAIN_ENGINE_HEALTH, this, $$delegatedProperties[28])).floatValue();
    }

    public void setMainEngineHealth(float f) {
        EntityUtilKt.setValue(MAIN_ENGINE_HEALTH, this, $$delegatedProperties[28], Float.valueOf(f));
    }

    private static Object getMainEngineHealth$delegate(VehicleEntity vehicleEntity) {
        return MAIN_ENGINE_HEALTH;
    }

    public boolean getLeftWheelDamaged() {
        return EntityUtilKt.getValue(L_WHEEL_DAMAGED, this, $$delegatedProperties[29]);
    }

    public void setLeftWheelDamaged(boolean bl) {
        EntityUtilKt.setValue(L_WHEEL_DAMAGED, this, $$delegatedProperties[29], bl);
    }

    private static Object getLeftWheelDamaged$delegate(VehicleEntity vehicleEntity) {
        return L_WHEEL_DAMAGED;
    }

    public float getLeftWheelHealth() {
        return ((Number)EntityUtilKt.getValue(L_WHEEL_HEALTH, this, $$delegatedProperties[30])).floatValue();
    }

    public void setLeftWheelHealth(float f) {
        EntityUtilKt.setValue(L_WHEEL_HEALTH, this, $$delegatedProperties[30], Float.valueOf(f));
    }

    private static Object getLeftWheelHealth$delegate(VehicleEntity vehicleEntity) {
        return L_WHEEL_HEALTH;
    }

    public boolean getRightWheelDamaged() {
        return EntityUtilKt.getValue(R_WHEEL_DAMAGED, this, $$delegatedProperties[31]);
    }

    public void setRightWheelDamaged(boolean bl) {
        EntityUtilKt.setValue(R_WHEEL_DAMAGED, this, $$delegatedProperties[31], bl);
    }

    private static Object getRightWheelDamaged$delegate(VehicleEntity vehicleEntity) {
        return R_WHEEL_DAMAGED;
    }

    public float getRightWheelHealth() {
        return ((Number)EntityUtilKt.getValue(R_WHEEL_HEALTH, this, $$delegatedProperties[32])).floatValue();
    }

    public void setRightWheelHealth(float f) {
        EntityUtilKt.setValue(R_WHEEL_HEALTH, this, $$delegatedProperties[32], Float.valueOf(f));
    }

    private static Object getRightWheelHealth$delegate(VehicleEntity vehicleEntity) {
        return R_WHEEL_HEALTH;
    }

    public boolean getTurretDamaged() {
        return EntityUtilKt.getValue(TURRET_DAMAGED, this, $$delegatedProperties[33]);
    }

    public void setTurretDamaged(boolean bl) {
        EntityUtilKt.setValue(TURRET_DAMAGED, this, $$delegatedProperties[33], bl);
    }

    private static Object getTurretDamaged$delegate(VehicleEntity vehicleEntity) {
        return TURRET_DAMAGED;
    }

    public float getTurretHealth() {
        return ((Number)EntityUtilKt.getValue(TURRET_HEALTH, this, $$delegatedProperties[34])).floatValue();
    }

    public void setTurretHealth(float f) {
        EntityUtilKt.setValue(TURRET_HEALTH, this, $$delegatedProperties[34], Float.valueOf(f));
    }

    private static Object getTurretHealth$delegate(VehicleEntity vehicleEntity) {
        return TURRET_HEALTH;
    }

    @NotNull
    public List<Integer> getSelectedWeapon() {
        return EntityUtilKt.getValue(SELECTED_WEAPON, this, $$delegatedProperties[35]);
    }

    public void setSelectedWeapon(@NotNull List<Integer> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        EntityUtilKt.setValue(SELECTED_WEAPON, this, $$delegatedProperties[35], list);
    }

    private static Object getSelectedWeapon$delegate(VehicleEntity vehicleEntity) {
        return SELECTED_WEAPON;
    }

    public float getChargeProgress() {
        return ((Number)EntityUtilKt.getValue(CHARGE_PROGRESS, this, $$delegatedProperties[36])).floatValue();
    }

    public void setChargeProgress(float f) {
        EntityUtilKt.setValue(CHARGE_PROGRESS, this, $$delegatedProperties[36], Float.valueOf(f));
    }

    private static Object getChargeProgress$delegate(VehicleEntity vehicleEntity) {
        return CHARGE_PROGRESS;
    }

    public float getLaserScale() {
        return ((Number)EntityUtilKt.getValue(LASER_SCALE, this, $$delegatedProperties[37])).floatValue();
    }

    public void setLaserScale(float f) {
        EntityUtilKt.setValue(LASER_SCALE, this, $$delegatedProperties[37], Float.valueOf(f));
    }

    private static Object getLaserScale$delegate(VehicleEntity vehicleEntity) {
        return LASER_SCALE;
    }

    public float getLaserScaleO() {
        return ((Number)EntityUtilKt.getValue(LASER_SCALE_O, this, $$delegatedProperties[38])).floatValue();
    }

    public void setLaserScaleO(float f) {
        EntityUtilKt.setValue(LASER_SCALE_O, this, $$delegatedProperties[38], Float.valueOf(f));
    }

    private static Object getLaserScaleO$delegate(VehicleEntity vehicleEntity) {
        return LASER_SCALE_O;
    }

    public float getLaserLength() {
        return ((Number)EntityUtilKt.getValue(LASER_LENGTH, this, $$delegatedProperties[39])).floatValue();
    }

    public void setLaserLength(float f) {
        EntityUtilKt.setValue(LASER_LENGTH, this, $$delegatedProperties[39], Float.valueOf(f));
    }

    private static Object getLaserLength$delegate(VehicleEntity vehicleEntity) {
        return LASER_LENGTH;
    }

    public float getServerYaw() {
        return ((Number)EntityUtilKt.getValue(SERVER_YAW, this, $$delegatedProperties[40])).floatValue();
    }

    public void setServerYaw(float f) {
        EntityUtilKt.setValue(SERVER_YAW, this, $$delegatedProperties[40], Float.valueOf(f));
    }

    private static Object getServerYaw$delegate(VehicleEntity vehicleEntity) {
        return SERVER_YAW;
    }

    public float getServerPitch() {
        return ((Number)EntityUtilKt.getValue(SERVER_PITCH, this, $$delegatedProperties[41])).floatValue();
    }

    public void setServerPitch(float f) {
        EntityUtilKt.setValue(SERVER_PITCH, this, $$delegatedProperties[41], Float.valueOf(f));
    }

    private static Object getServerPitch$delegate(VehicleEntity vehicleEntity) {
        return SERVER_PITCH;
    }

    public int getCannonRecoilTime() {
        return ((Number)EntityUtilKt.getValue(CANNON_RECOIL_TIME, this, $$delegatedProperties[42])).intValue();
    }

    public void setCannonRecoilTime(int n) {
        EntityUtilKt.setValue(CANNON_RECOIL_TIME, this, $$delegatedProperties[42], n);
    }

    private static Object getCannonRecoilTime$delegate(VehicleEntity vehicleEntity) {
        return CANNON_RECOIL_TIME;
    }

    public float getCannonRecoilForce() {
        return ((Number)EntityUtilKt.getValue(CANNON_RECOIL_FORCE, this, $$delegatedProperties[43])).floatValue();
    }

    public void setCannonRecoilForce(float f) {
        EntityUtilKt.setValue(CANNON_RECOIL_FORCE, this, $$delegatedProperties[43], Float.valueOf(f));
    }

    private static Object getCannonRecoilForce$delegate(VehicleEntity vehicleEntity) {
        return CANNON_RECOIL_FORCE;
    }

    @NotNull
    public String getOverride() {
        return EntityUtilKt.getValue(OVERRIDE, this, $$delegatedProperties[44]);
    }

    public void setOverride(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(OVERRIDE, this, $$delegatedProperties[44], string);
    }

    private static Object getOverride$delegate(VehicleEntity vehicleEntity) {
        return OVERRIDE;
    }

    @NotNull
    public String getSkinId() {
        return EntityUtilKt.getValue(SKIN_ID, this, $$delegatedProperties[45]);
    }

    public void setSkinId(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(SKIN_ID, this, $$delegatedProperties[45], string);
    }

    private static Object getSkinId$delegate(VehicleEntity vehicleEntity) {
        return SKIN_ID;
    }

    @NotNull
    public String getLastAttackerUUID() {
        return EntityUtilKt.getValue(LAST_ATTACKER_UUID, this, $$delegatedProperties[46]);
    }

    public void setLastAttackerUUID(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(LAST_ATTACKER_UUID, this, $$delegatedProperties[46], string);
    }

    private static Object getLastAttackerUUID$delegate(VehicleEntity vehicleEntity) {
        return LAST_ATTACKER_UUID;
    }

    @NotNull
    public String getLastDriverUUID() {
        return EntityUtilKt.getValue(LAST_DRIVER_UUID, this, $$delegatedProperties[47]);
    }

    public void setLastDriverUUID(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(LAST_DRIVER_UUID, this, $$delegatedProperties[47], string);
    }

    private static Object getLastDriverUUID$delegate(VehicleEntity vehicleEntity) {
        return LAST_DRIVER_UUID;
    }

    @NotNull
    public List<List<Short>> getDogTagIcon() {
        return EntityUtilKt.getValue(DOG_TAG_ICON, this, $$delegatedProperties[48]);
    }

    public void setDogTagIcon(@NotNull List<? extends List<Short>> list) {
        Intrinsics.checkNotNullParameter(list, (String)"<set-?>");
        EntityUtilKt.setValue(DOG_TAG_ICON, this, $$delegatedProperties[48], list);
    }

    private static Object getDogTagIcon$delegate(VehicleEntity vehicleEntity) {
        return DOG_TAG_ICON;
    }

    @NotNull
    public String getAiTurretTargetUUID() {
        return EntityUtilKt.getValue(AI_TURRET_TARGET_UUID, this, $$delegatedProperties[49]);
    }

    public void setAiTurretTargetUUID(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(AI_TURRET_TARGET_UUID, this, $$delegatedProperties[49], string);
    }

    private static Object getAiTurretTargetUUID$delegate(VehicleEntity vehicleEntity) {
        return AI_TURRET_TARGET_UUID;
    }

    @NotNull
    public String getAiPassengerWeaponTargetUUID() {
        return EntityUtilKt.getValue(AI_PASSENGER_WEAPON_TARGET_UUID, this, $$delegatedProperties[50]);
    }

    public void setAiPassengerWeaponTargetUUID(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(AI_PASSENGER_WEAPON_TARGET_UUID, this, $$delegatedProperties[50], string);
    }

    private static Object getAiPassengerWeaponTargetUUID$delegate(VehicleEntity vehicleEntity) {
        return AI_PASSENGER_WEAPON_TARGET_UUID;
    }

    @NotNull
    public List<String> getTowingUUIDs() {
        CompoundTag tag = (CompoundTag)this.f_19804_.m_135370_(TOWING_UUIDS);
        ListTag listTag = tag.m_128437_("list", 8);
        List result = new ArrayList();
        int n = ((Collection)listTag).size();
        for (int i = 0; i < n; ++i) {
            String string = listTag.m_128778_(i);
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            result.add(string);
        }
        return result;
    }

    public void setTowingUUIDs(@NotNull List<String> value) {
        Intrinsics.checkNotNullParameter(value, (String)"value");
        CompoundTag tag = new CompoundTag();
        ListTag listTag = new ListTag();
        for (String uuid : value) {
            listTag.add((Object)StringTag.m_129297_((String)uuid));
        }
        tag.m_128365_("list", (Tag)listTag);
        this.f_19804_.m_135381_(TOWING_UUIDS, (Object)tag);
    }

    @NotNull
    public String getTowingUUID() {
        String string = (String)CollectionsKt.firstOrNull(this.getTowingUUIDs());
        if (string == null) {
            string = "";
        }
        return string;
    }

    public void setTowingUUID(@NotNull String value) {
        Intrinsics.checkNotNullParameter((Object)value, (String)"value");
        if (StringsKt.isBlank((CharSequence)value)) {
            this.setTowingUUIDs(new ArrayList());
        } else {
            List<String> current = this.getTowingUUIDs();
            if (current.isEmpty()) {
                Object[] objectArray = new String[]{value};
                this.setTowingUUIDs(CollectionsKt.mutableListOf((Object[])objectArray));
            } else {
                current.set(0, value);
                this.setTowingUUIDs(current);
            }
        }
    }

    @NotNull
    public String getTowedByUUID() {
        return EntityUtilKt.getValue(TOWED_BY_UUID, this, $$delegatedProperties[51]);
    }

    public void setTowedByUUID(@NotNull String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        EntityUtilKt.setValue(TOWED_BY_UUID, this, $$delegatedProperties[51], string);
    }

    private static Object getTowedByUUID$delegate(VehicleEntity vehicleEntity) {
        return TOWED_BY_UUID;
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public List<Entity> getTowingEntities() {
        void $this$mapNotNullTo$iv$iv;
        Iterable $this$mapNotNull$iv = this.getTowingUUIDs();
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            Entity entity;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            String uuid = (String)element$iv$iv;
            boolean bl2 = false;
            if (StringsKt.isBlank((CharSequence)uuid)) {
                entity = null;
            } else {
                Level level = this.m_9236_();
                Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
                entity = EntityFindUtil.findEntity(level, uuid);
            }
            if (entity == null) continue;
            Entity it$iv$iv = entity;
            boolean bl3 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    @Nullable
    public Entity getTowingEntity() {
        String uuid = this.getTowingUUID();
        if (StringsKt.isBlank((CharSequence)uuid)) {
            return null;
        }
        Level level = this.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        return EntityFindUtil.findEntity(level, uuid);
    }

    @Nullable
    public VehicleEntity getTowedByEntity() {
        if (StringsKt.isBlank((CharSequence)this.getTowedByUUID())) {
            return null;
        }
        Level level = this.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        Entity entity = EntityFindUtil.findEntity(level, this.getTowedByUUID());
        return entity instanceof VehicleEntity ? (VehicleEntity)entity : null;
    }

    public boolean isTowing(@NotNull Entity entity) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        return this.getTowingUUIDs().contains(entity.m_20149_());
    }

    public boolean isTowingAny() {
        return !((Collection)this.getTowingUUIDs()).isEmpty();
    }

    public float getYawWhileShoot() {
        return ((Number)EntityUtilKt.getValue(YAW_WHILE_SHOOT, this, $$delegatedProperties[52])).floatValue();
    }

    public void setYawWhileShoot(float f) {
        EntityUtilKt.setValue(YAW_WHILE_SHOOT, this, $$delegatedProperties[52], Float.valueOf(f));
    }

    private static Object getYawWhileShoot$delegate(VehicleEntity vehicleEntity) {
        return YAW_WHILE_SHOOT;
    }

    public float getHornVolume() {
        return ((Number)EntityUtilKt.getValue(HORN_VOLUME, this, $$delegatedProperties[53])).floatValue();
    }

    public void setHornVolume(float f) {
        EntityUtilKt.setValue(HORN_VOLUME, this, $$delegatedProperties[53], Float.valueOf(f));
    }

    private static Object getHornVolume$delegate(VehicleEntity vehicleEntity) {
        return HORN_VOLUME;
    }

    public boolean isWreck() {
        return EntityUtilKt.getValue(IS_WRECK, this, $$delegatedProperties[54]);
    }

    public void setWreck(boolean bl) {
        EntityUtilKt.setValue(IS_WRECK, this, $$delegatedProperties[54], bl);
    }

    private static Object isWreck$delegate(VehicleEntity vehicleEntity) {
        return IS_WRECK;
    }

    public boolean getSympatheticDetonated() {
        return EntityUtilKt.getValue(SYMPATHETIC_DETONATED, this, $$delegatedProperties[55]);
    }

    public void setSympatheticDetonated(boolean bl) {
        EntityUtilKt.setValue(SYMPATHETIC_DETONATED, this, $$delegatedProperties[55], bl);
    }

    private static Object getSympatheticDetonated$delegate(VehicleEntity vehicleEntity) {
        return SYMPATHETIC_DETONATED;
    }

    public boolean getTurretBurned() {
        return EntityUtilKt.getValue(TURRET_BURNED, this, $$delegatedProperties[56]);
    }

    public void setTurretBurned(boolean bl) {
        EntityUtilKt.setValue(TURRET_BURNED, this, $$delegatedProperties[56], bl);
    }

    private static Object getTurretBurned$delegate(VehicleEntity vehicleEntity) {
        return TURRET_BURNED;
    }

    public int getTurretBurnTimer() {
        return ((Number)EntityUtilKt.getValue(TURRET_BURN_TIMER, this, $$delegatedProperties[57])).intValue();
    }

    public void setTurretBurnTimer(int n) {
        EntityUtilKt.setValue(TURRET_BURN_TIMER, this, $$delegatedProperties[57], n);
    }

    private static Object getTurretBurnTimer$delegate(VehicleEntity vehicleEntity) {
        return TURRET_BURN_TIMER;
    }

    public boolean getHoverMode() {
        return EntityUtilKt.getValue(HOVER_MODE, this, $$delegatedProperties[58]);
    }

    public void setHoverMode(boolean bl) {
        EntityUtilKt.setValue(HOVER_MODE, this, $$delegatedProperties[58], bl);
    }

    private static Object getHoverMode$delegate(VehicleEntity vehicleEntity) {
        return HOVER_MODE;
    }

    @NotNull
    public SoundEvent getHornSound() {
        return this.computed().getHornSound();
    }

    public void horn() {
        this.setHornVolume(this.getHornVolume() + 0.7f);
    }

    public boolean hornWorking() {
        return (double)org.joml.Math.abs((float)this.getHornVolume()) > 0.05;
    }

    public boolean stuka() {
        return this.m_146909_() > 5.0f && this.m_146909_() < 175.0f && this.m_20184_().f_82480_ < -0.4 && !this.m_20096_();
    }

    public boolean heliCrash() {
        return this.getVehicleType() == VehicleType.HELICOPTER && this.getHealth() < this.getMaxHealth() * 0.1f && !this.m_20096_();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean vehicleSkip() {
        if (!(this.getEngineInfo() instanceof EngineInfo.Wheel)) return false;
        if (this.getEngineInfo() instanceof EngineInfo.WheelChair) return false;
        if (!(this.getEngineInfo() instanceof EngineInfo.Track ? this.drift() : this.upInputDown())) return false;
        if (!this.m_20096_()) return false;
        double d = this.m_20184_().m_165925_();
        double d2 = this.getEngineInfo() instanceof EngineInfo.Track ? 4.0E-4 : 0.01;
        if (!(d > d2)) return false;
        return true;
    }

    public boolean drift() {
        return this.upInputDown() && (this.rightInputDown() || this.leftInputDown());
    }

    @Nullable
    public VehicleType getVehicleType() {
        return this.computed().getType();
    }

    public boolean isAmphibious() {
        return VehicleMiscUtils.isAmphibious(this);
    }

    @OnlyIn(value=Dist.CLIENT)
    @NotNull
    public Component firstPersonAmmoComponent(@NotNull GunData data, @Nullable Player player) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        return VehicleClientUtils.firstPersonAmmoComponent(this, data, player);
    }

    @OnlyIn(value=Dist.CLIENT)
    @NotNull
    public Component thirdPersonAmmoComponent(@NotNull GunData data, @Nullable Player player) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        return VehicleClientUtils.thirdPersonAmmoComponent(this, data, player);
    }

    @Override
    @NotNull
    public List<OBB> getOBBs() {
        if (this.obbCache == null) {
            this.obbCache = SequencesKt.toMutableList((Sequence)SequencesKt.map((Sequence)CollectionsKt.asSequence((Iterable)this.getObb()), VehicleEntity::getOBBs$lambda$82));
        }
        List<OBB> list = this.obbCache;
        Intrinsics.checkNotNull(list);
        return list;
    }

    @NotNull
    public AABB getCombinedAABB() {
        if (this.enableAABB() || this.getCollisionOBBInfo() == null) {
            AABB aABB = this.m_20191_();
            Intrinsics.checkNotNullExpressionValue((Object)aABB, (String)"getBoundingBox(...)");
            return aABB;
        }
        if (this.combinedAabbCache != null && this.combinedAabbCacheTick == this.f_19797_) {
            AABB aABB = this.combinedAabbCache;
            Intrinsics.checkNotNull((Object)aABB);
            return aABB;
        }
        this.combinedAabbCache = VehicleMotionUtils.calculateCombinedAABBOptimized(this);
        this.combinedAabbCacheTick = this.f_19797_;
        AABB aABB = this.combinedAabbCache;
        Intrinsics.checkNotNull((Object)aABB);
        return aABB;
    }

    public void invalidateAABBCache() {
        this.combinedAabbCache = null;
        this.combinedAabbCacheTick = -1;
    }

    public void refreshBoundingBoxFromOBBs() {
        if (this.enableAABB()) {
            return;
        }
        this.invalidateAABBCache();
        AABB aabb = this.getCombinedAABB();
        if (!aabb.m_82392_() && aabb.m_82309_() > 0.0) {
            this.m_20011_(aabb);
        }
    }

    @Nullable
    public OBB getCollisionOBB() {
        Object v0;
        block1: {
            Iterable $this$firstOrNull$iv = this.getOBBs();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                OBB it = (OBB)element$iv;
                boolean bl = false;
                if (!(it.part == OBB.Part.COLLISION)) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        return v0;
    }

    @Nullable
    public OBBInfo getCollisionOBBInfo() {
        Object v0;
        block1: {
            Iterable $this$firstOrNull$iv = this.getObb();
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                OBBInfo it = (OBBInfo)element$iv;
                boolean bl = false;
                if (!(it.getPart() == OBB.Part.COLLISION)) continue;
                v0 = element$iv;
                break block1;
            }
            v0 = null;
        }
        return v0;
    }

    @NotNull
    public EntityDataAccessor<Integer> getEnergyDataAccessor() {
        return ENERGY;
    }

    public void generateWreckageLoot() {
        VehicleLootUtils.generateWreckageLoot(this);
    }

    @Override
    public DefaultVehicleData computeProperties(VehicleData data, DefaultVehicleData rawData) {
        return VehiclePropertyModifier.DefaultImpls.computeProperties((VehiclePropertyModifier)this, (VehicleData)data, (DefaultVehicleData)rawData);
    }

    @Override
    public boolean enableAABB() {
        return OBBEntity.DefaultImpls.enableAABB(this);
    }

    @Override
    public boolean isInObb(@NotNull BlockPos pos, @NotNull Vec3 vec3) {
        return OBBEntity.DefaultImpls.isInObb((OBBEntity)this, pos, vec3);
    }

    @Override
    public boolean isInObb(@NotNull Entity entity, @NotNull Vec3 vec3) {
        return OBBEntity.DefaultImpls.isInObb((OBBEntity)this, entity, vec3);
    }

    /*
     * WARNING - void declaration
     */
    private static final List modelEntriesValue_delegate$lambda$2(VehicleEntity this$0) {
        void $this$mapNotNullTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        if (!this$0.m_9236_().f_46443_) {
            return CollectionsKt.emptyList();
        }
        List<VehicleModelPojo> models = VehicleResource.Companion.compute(this$0).getModels();
        Iterable $this$mapNotNull$iv = models;
        boolean $i$f$mapNotNull = false;
        Iterable iterable = $this$mapNotNull$iv;
        Collection destination$iv$iv = new ArrayList();
        boolean $i$f$mapNotNullTo = false;
        void $this$forEach$iv$iv$iv = $this$mapNotNullTo$iv$iv;
        boolean $i$f$forEach = false;
        Iterator iterator = $this$forEach$iv$iv$iv.iterator();
        while (iterator.hasNext()) {
            VehicleModelEntry vehicleModelEntry;
            Object element$iv$iv$iv;
            Object element$iv$iv = element$iv$iv$iv = iterator.next();
            boolean bl = false;
            VehicleModelPojo pojo = (VehicleModelPojo)element$iv$iv;
            boolean bl2 = false;
            if (pojo.model == null) {
                vehicleModelEntry = null;
            } else if (pojo.texture == null) {
                vehicleModelEntry = null;
            } else {
                ResourceLocation modelPath;
                BakedBedrockModel bakedModel;
                int distance = pojo.distance;
                BakedBedrockModel bakedBedrockModel = bakedModel = distance > 0 ? (BakedBedrockModel)VehicleLODModelReloadListener.INSTANCE.getModel(modelPath) : (BakedBedrockModel)VehicleModelReloadListener.INSTANCE.getModel(modelPath);
                if (bakedModel == null) {
                    vehicleModelEntry = null;
                } else {
                    ResourceLocation texture;
                    BakedBedrockModel it;
                    boolean bl3 = false;
                    VehicleModelInstance instance = new VehicleModelInstance(it);
                    vehicleModelEntry = new VehicleModelEntry(instance, texture, pojo.emissiveTexture, distance);
                }
            }
            if (vehicleModelEntry == null) continue;
            VehicleModelEntry it$iv$iv = vehicleModelEntry;
            boolean bl4 = false;
            destination$iv$iv.add(it$iv$iv);
        }
        return (List)destination$iv$iv;
    }

    private static final DefaultGunData _get_gunDataMap_$lambda$3(Map.Entry $kv) {
        Intrinsics.checkNotNullParameter((Object)$kv, (String)"$kv");
        return (DefaultGunData)$kv.getValue();
    }

    private static final DefaultGunData _get_gunDataMap_$lambda$4(Map.Entry $kv) {
        Intrinsics.checkNotNullParameter((Object)$kv, (String)"$kv");
        return (DefaultGunData)$kv.getValue();
    }

    private static final VehicleContainerHandler itemHandler$lambda$5(VehicleEntity this$0) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        return this$0.inventory;
    }

    private static final AbstractContainerMenu openMenu$lambda$8(VehicleEntity this$0, int containerId, Inventory inv, Player player) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNull((Object)inv);
        Intrinsics.checkNotNull((Object)player);
        return this$0.createMenu(containerId, inv, player);
    }

    private static final void openMenu$lambda$9(VehicleEntity this$0, FriendlyByteBuf buf) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        buf.writeInt(this$0.m_19879_());
    }

    private static final boolean addPassenger$lambda$12(Entity obj) {
        return Objects.nonNull(obj);
    }

    private static final boolean addPassenger$lambda$13(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean removePassenger$lambda$15(Entity obj) {
        return Objects.nonNull(obj);
    }

    private static final boolean removePassenger$lambda$16(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean changeSeat$lambda$17(ServerPlayer it) {
        return true;
    }

    private static final boolean changeSeat$lambda$18(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final IEnergyStorage energyOptional$lambda$20(VehicleEntity this$0) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        return (IEnergyStorage)this$0.getEnergyStorage();
    }

    private static final void vehicleShoot$lambda$26$lambda$25(VehicleEntity this$0, LivingEntity $living, String $weaponName, Vec3 $targetPos, GunData data) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$weaponName, (String)"$weaponName");
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        if (!data.canShoot(this$0.getAmmoSupplier())) {
            return;
        }
        Entity entity = this$0.getAmmoSupplier();
        Entity entity2 = (Entity)$living;
        Level level = this$0.m_9236_();
        Intrinsics.checkNotNull((Object)level, (String)"null cannot be cast to non-null type net.minecraft.server.level.ServerLevel");
        data.shoot(new ShootParameters(entity, entity2, (ServerLevel)level, this$0.getShootPos($weaponName, 1.0f), this$0.getShootVec($weaponName, 1.0f), data, ((Number)data.get(GunProp.SPREAD)).doubleValue(), true, null, $targetPos));
    }

    private static final void vehicleShoot$lambda$26(VehicleEntity this$0, String $weaponName, LivingEntity $living, Vec3 $targetPos) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$weaponName, (String)"$weaponName");
        this$0.modifyGunData($weaponName, arg_0 -> VehicleEntity.vehicleShoot$lambda$26$lambda$25(this$0, $living, $weaponName, $targetPos, arg_0));
    }

    private static final void vehicleShoot$lambda$28$lambda$27(VehicleEntity this$0, LivingEntity $living, String $weaponName, UUID $uuid, Vec3 $targetPos, GunData data) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$weaponName, (String)"$weaponName");
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        if (!data.canShoot(this$0.getAmmoSupplier())) {
            return;
        }
        Entity entity = this$0.getAmmoSupplier();
        Entity entity2 = (Entity)$living;
        Level level = this$0.m_9236_();
        Intrinsics.checkNotNull((Object)level, (String)"null cannot be cast to non-null type net.minecraft.server.level.ServerLevel");
        data.shoot(new ShootParameters(entity, entity2, (ServerLevel)level, this$0.getShootPos($weaponName, 1.0f), this$0.getShootVec($weaponName, 1.0f), data, ((Number)data.get(GunProp.SPREAD)).doubleValue(), true, $uuid, $targetPos));
    }

    private static final void vehicleShoot$lambda$28(VehicleEntity this$0, String $weaponName, LivingEntity $living, UUID $uuid, Vec3 $targetPos) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)$weaponName, (String)"$weaponName");
        this$0.modifyGunData($weaponName, arg_0 -> VehicleEntity.vehicleShoot$lambda$28$lambda$27(this$0, $living, $weaponName, $uuid, $targetPos, arg_0));
    }

    private static final void vehicleShoot$lambda$30$lambda$29(VehicleEntity this$0, LivingEntity $living, UUID $uuid, Vec3 $targetPos, GunData data) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        if (!data.canShoot(this$0.getAmmoSupplier())) {
            return;
        }
        Entity entity = this$0.getAmmoSupplier();
        Entity entity2 = (Entity)$living;
        Level level = this$0.m_9236_();
        Intrinsics.checkNotNull((Object)level, (String)"null cannot be cast to non-null type net.minecraft.server.level.ServerLevel");
        data.shoot(new ShootParameters(entity, entity2, (ServerLevel)level, this$0.getShootPos((Entity)$living, 1.0f), this$0.getShootVec((Entity)$living, 1.0f), data, ((Number)data.get(GunProp.SPREAD)).doubleValue(), true, $uuid, $targetPos));
    }

    private static final void vehicleShoot$lambda$30(VehicleEntity this$0, int $seatIndex, LivingEntity $living, UUID $uuid, Vec3 $targetPos) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        this$0.modifyGunData($seatIndex, arg_0 -> VehicleEntity.vehicleShoot$lambda$30$lambda$29(this$0, $living, $uuid, $targetPos, arg_0));
    }

    private static final boolean hasWeapon$lambda$35(SeatInfo seat) {
        SeatInfo seatInfo = seat;
        Intrinsics.checkNotNull((Object)seatInfo);
        return !((Collection)seatInfo.weapons()).isEmpty();
    }

    private static final boolean hasWeapon$lambda$36(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final Stream hasWeapon$lambda$37(SeatInfo seat) {
        SeatInfo seatInfo = seat;
        Intrinsics.checkNotNull((Object)seatInfo);
        return seatInfo.weapons().stream();
    }

    private static final Stream hasWeapon$lambda$38(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Stream)$tmp0.invoke(p0);
    }

    private static final boolean hasWeapon$lambda$39(String name) {
        CharSequence charSequence = name;
        return !(charSequence == null || charSequence.length() == 0);
    }

    private static final boolean hasWeapon$lambda$40(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final boolean hasWeapon$lambda$41(VehicleEntity this$0, String name) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNull((Object)name);
        return this$0.getGunData(name) != null;
    }

    private static final boolean hasWeapon$lambda$42(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final void setWeaponIndex$lambda$43(VehicleEntity this$0, GunData gunData) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)gunData, (String)"gunData");
        if (gunData.get(GunProp.WITHDRAW_AMMO_WHEN_CHANGE_SLOT).booleanValue()) {
            gunData.withdrawAmmo(this$0.getAmmoSupplier());
        }
    }

    private static final boolean isInLava$lambda$49(FluidType type, Double d) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)d, (String)"<unused var>");
        return type == ((IForgeRegistry)ForgeRegistries.FLUID_TYPES.get()).getValue(new ResourceLocation("minecraft", "lava"));
    }

    private static final void updateBackupAmmoCount$lambda$54(GunData it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        it.backupAmmoCount.reset();
    }

    private static final void updateBackupAmmoCount$lambda$55(int $count, GunData it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        it.backupAmmoCount.set($count);
    }

    private static final boolean clearArrow$lambda$56(Entity e) {
        return e instanceof AbstractArrow;
    }

    private static final boolean clearArrow$lambda$57(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final Matrix4d registerTransforms$lambda$60(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getVehicleFlatTransform(partialTicks.floatValue());
    }

    private static final Matrix4d registerTransforms$lambda$61(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getTurretTransform(partialTicks.floatValue());
    }

    private static final Matrix4d registerTransforms$lambda$62(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getBarrelTransform(partialTicks.floatValue());
    }

    private static final Matrix4d registerTransforms$lambda$63(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getGunTransform(partialTicks.floatValue());
    }

    private static final Matrix4d registerTransforms$lambda$64(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getPassengerWeaponStationBarrelTransform(partialTicks.floatValue());
    }

    private static final Matrix4d registerTransforms$lambda$65(VehicleEntity this$0, Float ticks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)ticks, (String)"ticks");
        return this$0.getVehicleTransform(ticks.floatValue());
    }

    private static final Vec3 registerTransforms$lambda$66(VehicleEntity this$0, Float pPartialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)pPartialTicks, (String)"pPartialTicks");
        return this$0.getTurretVector(pPartialTicks.floatValue());
    }

    private static final Vec3 registerTransforms$lambda$67(VehicleEntity this$0, Float pPartialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)pPartialTicks, (String)"pPartialTicks");
        return this$0.getBarrelVector(pPartialTicks.floatValue());
    }

    private static final Vec3 registerTransforms$lambda$68(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.getPassengerWeaponStationVector(partialTicks.floatValue());
    }

    private static final Vec3 registerTransforms$lambda$69(VehicleEntity this$0, Float f) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)f, (String)"<unused var>");
        return this$0.m_20184_().m_82541_();
    }

    private static final Vec3 registerTransforms$lambda$70(VehicleEntity this$0, Float ticks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)ticks, (String)"ticks");
        return this$0.getUpVec(ticks.floatValue());
    }

    private static final Vec3 registerTransforms$lambda$71(VehicleEntity this$0, Float partialTicks) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)partialTicks, (String)"partialTicks");
        return this$0.m_20252_(partialTicks.floatValue());
    }

    private static final Quaterniond registerTransforms$lambda$72(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotationsPassengerWeaponStation(tick.floatValue(), this$0);
    }

    private static final Quaterniond registerTransforms$lambda$73(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotationsPassengerWeaponStationBarrel(tick.floatValue(), this$0);
    }

    private static final Quaterniond registerTransforms$lambda$74(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotationsTurret(tick.floatValue(), this$0);
    }

    private static final Quaterniond registerTransforms$lambda$75(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotationsBarrel(tick.floatValue(), this$0);
    }

    private static final Quaterniond registerTransforms$lambda$76(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotationsYaw(tick.floatValue(), this$0);
    }

    private static final Quaterniond registerTransforms$lambda$77(VehicleEntity this$0, Float tick) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        Intrinsics.checkNotNullParameter((Object)tick, (String)"tick");
        return VectorTool.combineRotations(tick.floatValue(), this$0);
    }

    private static final boolean getPlayerLookAtEntityOnVehicle$lambda$78(Entity $shooter, Entity p) {
        Intrinsics.checkNotNullParameter((Object)$shooter, (String)"$shooter");
        Entity entity = p;
        Intrinsics.checkNotNull((Object)entity);
        return !entity.m_5833_() && p.m_6084_() && SeekTool.BASIC_FILTER.test(p) && !p.m_6095_().m_204039_(ModTags.EntityTypes.DECOY) && SeekTool.NOT_IN_SMOKE.test(p) && p != $shooter && !Intrinsics.areEqual((Object)p.m_20202_(), (Object)$shooter.m_20202_()) && !(p instanceof Projectile);
    }

    private static final VehicleContainerHandler reviveCaps$lambda$79(VehicleEntity this$0) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        return this$0.inventory;
    }

    private static final IEnergyStorage reviveCaps$lambda$80(VehicleEntity this$0) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        return (IEnergyStorage)new VehicleEnergyStorage(this$0);
    }

    private static final OBB getOBBs$lambda$82(OBBInfo it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getOBB();
    }

    private static final void playTrackSound$lambda$85(VehicleEntity it) {
    }

    private static final void playEngineSound$lambda$86(VehicleEntity it) {
    }

    private static final void playSwimSound$lambda$87(VehicleEntity it) {
    }

    private static final void playHornSound$lambda$88(VehicleEntity it) {
    }

    private static final void playStukaSound$lambda$89(VehicleEntity it) {
    }

    private static final void playHeliCrashSound$lambda$90(VehicleEntity it) {
    }

    private static final void playVehicleSkipSound$lambda$91(VehicleEntity it) {
    }

    private static final void playFireSound$lambda$92(VehicleEntity vehicleEntity, String string) {
        Intrinsics.checkNotNullParameter((Object)vehicleEntity, (String)"<unused var>");
        Intrinsics.checkNotNullParameter((Object)string, (String)"<unused var>");
    }

    static {
        Object[] objectArray = new KProperty[]{Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "loiterParams", "getLoiterParams()Lorg/joml/Quaternionf;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "loiterActive", "getLoiterActive()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "liftOffset", "getLiftOffset()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "forwardInputDown", "forwardInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "backInputDown", "backInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "leftInputDown", "leftInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "rightInputDown", "rightInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "upInputDown", "upInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "downInputDown", "downInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "fireInputDown", "fireInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "decoyInputDown", "decoyInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "sprintInputDown", "sprintInputDown()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "mouseMoveSpeedX", "getMouseMoveSpeedX()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "mouseMoveSpeedY", "getMouseMoveSpeedY()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "locked", "getLocked()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "power", "getPower()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "deltaRot", "getDeltaRot()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "decoyCount", "getDecoyCount()I", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "decoyReloadCoolDown", "getDecoyReloadCoolDown()I", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "synchedPropellerRot", "getSynchedPropellerRot()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "decoyItemCount", "getDecoyItemCount()I", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "propellerRot", "getPropellerRot()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "planeBreak", "getPlaneBreak()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "synchedGearRot", "getSynchedGearRot()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "gearUp", "getGearUp()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "subEngineDamaged", "getSubEngineDamaged()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "subEngineHealth", "getSubEngineHealth()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "mainEngineDamaged", "getMainEngineDamaged()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "mainEngineHealth", "getMainEngineHealth()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "leftWheelDamaged", "getLeftWheelDamaged()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "leftWheelHealth", "getLeftWheelHealth()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "rightWheelDamaged", "getRightWheelDamaged()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "rightWheelHealth", "getRightWheelHealth()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "turretDamaged", "getTurretDamaged()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "turretHealth", "getTurretHealth()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "selectedWeapon", "getSelectedWeapon()Ljava/util/List;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "chargeProgress", "getChargeProgress()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "laserScale", "getLaserScale()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "laserScaleO", "getLaserScaleO()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "laserLength", "getLaserLength()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "serverYaw", "getServerYaw()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "serverPitch", "getServerPitch()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "cannonRecoilTime", "getCannonRecoilTime()I", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "cannonRecoilForce", "getCannonRecoilForce()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "override", "getOverride()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "skinId", "getSkinId()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "lastAttackerUUID", "getLastAttackerUUID()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "lastDriverUUID", "getLastDriverUUID()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "dogTagIcon", "getDogTagIcon()Ljava/util/List;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "aiTurretTargetUUID", "getAiTurretTargetUUID()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "aiPassengerWeaponTargetUUID", "getAiPassengerWeaponTargetUUID()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "towedByUUID", "getTowedByUUID()Ljava/lang/String;", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "yawWhileShoot", "getYawWhileShoot()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "hornVolume", "getHornVolume()F", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "isWreck", "isWreck()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "sympatheticDetonated", "getSympatheticDetonated()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "turretBurned", "getTurretBurned()Z", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "turretBurnTimer", "getTurretBurnTimer()I", 0))), Reflection.mutableProperty1((MutablePropertyReference1)((MutablePropertyReference1)new MutablePropertyReference1Impl(VehicleEntity.class, "hoverMode", "getHoverMode()Z", 0)))};
        $$delegatedProperties = objectArray;
        Companion = new Companion(null);
        EntityDataAccessor entityDataAccessor = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor, (String)"defineId(...)");
        HEALTH = entityDataAccessor;
        EntityDataAccessor entityDataAccessor2 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor2, (String)"defineId(...)");
        OVERRIDE = entityDataAccessor2;
        EntityDataAccessor entityDataAccessor3 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor3, (String)"defineId(...)");
        SKIN_ID = entityDataAccessor3;
        EntityDataAccessor entityDataAccessor4 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor4, (String)"defineId(...)");
        LAST_ATTACKER_UUID = entityDataAccessor4;
        EntityDataAccessor entityDataAccessor5 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor5, (String)"defineId(...)");
        LAST_DRIVER_UUID = entityDataAccessor5;
        EntityDataAccessor entityDataAccessor6 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)((EntityDataSerializer)ModSerializers.SHORT_LIST_LIST_SERIALIZER.get()));
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor6, (String)"defineId(...)");
        DOG_TAG_ICON = entityDataAccessor6;
        EntityDataAccessor entityDataAccessor7 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor7, (String)"defineId(...)");
        AI_TURRET_TARGET_UUID = entityDataAccessor7;
        EntityDataAccessor entityDataAccessor8 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor8, (String)"defineId(...)");
        AI_PASSENGER_WEAPON_TARGET_UUID = entityDataAccessor8;
        EntityDataAccessor entityDataAccessor9 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor9, (String)"defineId(...)");
        DELTA_ROT = entityDataAccessor9;
        EntityDataAccessor entityDataAccessor10 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor10, (String)"defineId(...)");
        MOUSE_SPEED_X = entityDataAccessor10;
        EntityDataAccessor entityDataAccessor11 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor11, (String)"defineId(...)");
        MOUSE_SPEED_Y = entityDataAccessor11;
        EntityDataAccessor entityDataAccessor12 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)((EntityDataSerializer)ModSerializers.INT_LIST_SERIALIZER.get()));
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor12, (String)"defineId(...)");
        SELECTED_WEAPON = entityDataAccessor12;
        EntityDataAccessor entityDataAccessor13 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor13, (String)"defineId(...)");
        TURRET_HEALTH = entityDataAccessor13;
        EntityDataAccessor entityDataAccessor14 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor14, (String)"defineId(...)");
        L_WHEEL_HEALTH = entityDataAccessor14;
        EntityDataAccessor entityDataAccessor15 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor15, (String)"defineId(...)");
        R_WHEEL_HEALTH = entityDataAccessor15;
        EntityDataAccessor entityDataAccessor16 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor16, (String)"defineId(...)");
        MAIN_ENGINE_HEALTH = entityDataAccessor16;
        EntityDataAccessor entityDataAccessor17 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor17, (String)"defineId(...)");
        SUB_ENGINE_HEALTH = entityDataAccessor17;
        EntityDataAccessor entityDataAccessor18 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor18, (String)"defineId(...)");
        TURRET_DAMAGED = entityDataAccessor18;
        EntityDataAccessor entityDataAccessor19 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor19, (String)"defineId(...)");
        L_WHEEL_DAMAGED = entityDataAccessor19;
        EntityDataAccessor entityDataAccessor20 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor20, (String)"defineId(...)");
        R_WHEEL_DAMAGED = entityDataAccessor20;
        EntityDataAccessor entityDataAccessor21 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor21, (String)"defineId(...)");
        MAIN_ENGINE_DAMAGED = entityDataAccessor21;
        EntityDataAccessor entityDataAccessor22 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor22, (String)"defineId(...)");
        SUB_ENGINE_DAMAGED = entityDataAccessor22;
        EntityDataAccessor entityDataAccessor23 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor23, (String)"defineId(...)");
        HORN_VOLUME = entityDataAccessor23;
        playTrackSound = VehicleEntity::playTrackSound$lambda$85;
        playEngineSound = VehicleEntity::playEngineSound$lambda$86;
        playSwimSound = VehicleEntity::playSwimSound$lambda$87;
        playHornSound = VehicleEntity::playHornSound$lambda$88;
        playStukaSound = VehicleEntity::playStukaSound$lambda$89;
        playHeliCrashSound = VehicleEntity::playHeliCrashSound$lambda$90;
        playVehicleSkipSound = VehicleEntity::playVehicleSkipSound$lambda$91;
        playFireSound = VehicleEntity::playFireSound$lambda$92;
        EntityDataAccessor entityDataAccessor24 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor24, (String)"defineId(...)");
        SERVER_YAW = entityDataAccessor24;
        EntityDataAccessor entityDataAccessor25 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor25, (String)"defineId(...)");
        SERVER_PITCH = entityDataAccessor25;
        EntityDataAccessor entityDataAccessor26 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor26, (String)"defineId(...)");
        CANNON_RECOIL_TIME = entityDataAccessor26;
        EntityDataAccessor entityDataAccessor27 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor27, (String)"defineId(...)");
        CANNON_RECOIL_FORCE = entityDataAccessor27;
        EntityDataAccessor entityDataAccessor28 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor28, (String)"defineId(...)");
        POWER = entityDataAccessor28;
        EntityDataAccessor entityDataAccessor29 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor29, (String)"defineId(...)");
        YAW_WHILE_SHOOT = entityDataAccessor29;
        EntityDataAccessor entityDataAccessor30 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor30, (String)"defineId(...)");
        DECOY_COUNT = entityDataAccessor30;
        EntityDataAccessor entityDataAccessor31 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor31, (String)"defineId(...)");
        DECOY_RELOAD_COOLDOWN = entityDataAccessor31;
        EntityDataAccessor entityDataAccessor32 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor32, (String)"defineId(...)");
        DECOY_ITEM_COUNT = entityDataAccessor32;
        EntityDataAccessor entityDataAccessor33 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor33, (String)"defineId(...)");
        SYNCHED_PROPELLER_ROT = entityDataAccessor33;
        EntityDataAccessor entityDataAccessor34 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor34, (String)"defineId(...)");
        PROPELLER_ROT = entityDataAccessor34;
        EntityDataAccessor entityDataAccessor35 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor35, (String)"defineId(...)");
        SYNCHED_GEAR_ROT = entityDataAccessor35;
        EntityDataAccessor entityDataAccessor36 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor36, (String)"defineId(...)");
        GEAR_UP = entityDataAccessor36;
        EntityDataAccessor entityDataAccessor37 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor37, (String)"defineId(...)");
        FORWARD_INPUT_DOWN = entityDataAccessor37;
        EntityDataAccessor entityDataAccessor38 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor38, (String)"defineId(...)");
        BACK_INPUT_DOWN = entityDataAccessor38;
        EntityDataAccessor entityDataAccessor39 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor39, (String)"defineId(...)");
        LEFT_INPUT_DOWN = entityDataAccessor39;
        EntityDataAccessor entityDataAccessor40 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor40, (String)"defineId(...)");
        RIGHT_INPUT_DOWN = entityDataAccessor40;
        EntityDataAccessor entityDataAccessor41 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor41, (String)"defineId(...)");
        UP_INPUT_DOWN = entityDataAccessor41;
        EntityDataAccessor entityDataAccessor42 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor42, (String)"defineId(...)");
        DOWN_INPUT_DOWN = entityDataAccessor42;
        EntityDataAccessor entityDataAccessor43 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor43, (String)"defineId(...)");
        DECOY_INPUT_DOWN = entityDataAccessor43;
        EntityDataAccessor entityDataAccessor44 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor44, (String)"defineId(...)");
        FIRE_INPUT_DOWN = entityDataAccessor44;
        EntityDataAccessor entityDataAccessor45 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor45, (String)"defineId(...)");
        SPRINT_INPUT_DOWN = entityDataAccessor45;
        EntityDataAccessor entityDataAccessor46 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor46, (String)"defineId(...)");
        PLANE_BREAK = entityDataAccessor46;
        EntityDataAccessor entityDataAccessor47 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor47, (String)"defineId(...)");
        ENERGY = entityDataAccessor47;
        EntityDataAccessor entityDataAccessor48 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor48, (String)"defineId(...)");
        LASER_LENGTH = entityDataAccessor48;
        EntityDataAccessor entityDataAccessor49 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor49, (String)"defineId(...)");
        LASER_SCALE = entityDataAccessor49;
        EntityDataAccessor entityDataAccessor50 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor50, (String)"defineId(...)");
        LASER_SCALE_O = entityDataAccessor50;
        EntityDataAccessor entityDataAccessor51 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor51, (String)"defineId(...)");
        CHARGE_PROGRESS = entityDataAccessor51;
        EntityDataAccessor entityDataAccessor52 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor52, (String)"defineId(...)");
        IS_WRECK = entityDataAccessor52;
        EntityDataAccessor entityDataAccessor53 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor53, (String)"defineId(...)");
        SYMPATHETIC_DETONATED = entityDataAccessor53;
        EntityDataAccessor entityDataAccessor54 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor54, (String)"defineId(...)");
        TURRET_BURNED = entityDataAccessor54;
        EntityDataAccessor entityDataAccessor55 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor55, (String)"defineId(...)");
        TURRET_BURN_TIMER = entityDataAccessor55;
        EntityDataAccessor entityDataAccessor56 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor56, (String)"defineId(...)");
        HOVER_MODE = entityDataAccessor56;
        EntityDataAccessor entityDataAccessor57 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor57, (String)"defineId(...)");
        LOCKED = entityDataAccessor57;
        EntityDataAccessor entityDataAccessor58 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)((EntityDataSerializer)ModSerializers.VEHICLE_GUN_DATA_MAP_SERIALIZER.get()));
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor58, (String)"defineId(...)");
        GUN_DATA_MAP = entityDataAccessor58;
        EntityDataAccessor entityDataAccessor59 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_268624_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor59, (String)"defineId(...)");
        LOITER_PARAMS = entityDataAccessor59;
        EntityDataAccessor entityDataAccessor60 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor60, (String)"defineId(...)");
        LOITER_ACTIVE = entityDataAccessor60;
        EntityDataAccessor entityDataAccessor61 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135042_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor61, (String)"defineId(...)");
        TOWING_UUIDS = entityDataAccessor61;
        EntityDataAccessor entityDataAccessor62 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135030_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor62, (String)"defineId(...)");
        TOWED_BY_UUID = entityDataAccessor62;
        EntityDataAccessor entityDataAccessor63 = SynchedEntityData.m_135353_(VehicleEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
        Intrinsics.checkNotNullExpressionValue((Object)entityDataAccessor63, (String)"defineId(...)");
        LIFT_OFFSET = entityDataAccessor63;
        BvrSyncExclusion.INSTANCE.scanClass(VehicleEntity.class);
        objectArray = new String[]{"Inventory", "LoiterX", "LoiterY", "LoiterZ", "LoiterR", "PropellerRotO"};
        BvrSyncExclusion.INSTANCE.registerDirectKeys(VehicleEntity.class, (String[])objectArray);
    }

    @Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0010\n\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b$\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0011\u001a\u0014\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00120\u00120\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00120\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010!\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\"\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010#\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010$\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010%\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010&\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010)\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010*\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010+\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010,\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010-\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010.\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010(0'8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R \u0010/\u001a\u0010\u0012\u0004\u0012\u00020(\u0012\u0004\u0012\u00020\u0005\u0018\u0001008\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00101\u001a\u00020 8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u00102\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00103\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00104\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00105\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00106\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00107\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00108\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00109\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010;\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010<\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010=\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010>\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010?\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010@\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010A\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010B\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010C\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010D\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010E\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010F\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010G\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010H\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010I\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010J\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010K\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010L\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010M\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010N\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010O\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010P\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010Q\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010R\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010S\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R&\u0010T\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020V0U0\u000bX\u0084\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\bW\u0010XR\u0016\u0010Y\u001a\b\u0012\u0004\u0012\u00020Z0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010[\u001a\b\u0012\u0004\u0012\u00020 0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\\\u001a\b\u0012\u0004\u0012\u00020]0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010^\u001a\b\u0012\u0004\u0012\u00020\u00050\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010_\u001a\b\u0012\u0004\u0012\u00020\f0\u000b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006`"}, d2={"Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity$Companion;", "", "<init>", "()V", "TAG_SEAT_INDEX", "", "ENV_RATE_RECOMPUTE_INTERVAL", "", "OBB_GROUND_CACHE_TICKS", "BACKUP_AMMO_UPDATE_INTERVAL", "HEALTH", "Lnet/minecraft/network/syncher/EntityDataAccessor;", "", "OVERRIDE", "SKIN_ID", "LAST_ATTACKER_UUID", "LAST_DRIVER_UUID", "DOG_TAG_ICON", "", "", "AI_TURRET_TARGET_UUID", "AI_PASSENGER_WEAPON_TARGET_UUID", "DELTA_ROT", "MOUSE_SPEED_X", "MOUSE_SPEED_Y", "SELECTED_WEAPON", "TURRET_HEALTH", "L_WHEEL_HEALTH", "R_WHEEL_HEALTH", "MAIN_ENGINE_HEALTH", "SUB_ENGINE_HEALTH", "TURRET_DAMAGED", "", "L_WHEEL_DAMAGED", "R_WHEEL_DAMAGED", "MAIN_ENGINE_DAMAGED", "SUB_ENGINE_DAMAGED", "HORN_VOLUME", "playTrackSound", "Ljava/util/function/Consumer;", "Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;", "playEngineSound", "playSwimSound", "playHornSound", "playStukaSound", "playHeliCrashSound", "playVehicleSkipSound", "playFireSound", "Ljava/util/function/BiConsumer;", "ignoreEntityGroundCheckStepping", "SERVER_YAW", "SERVER_PITCH", "CANNON_RECOIL_TIME", "CANNON_RECOIL_FORCE", "POWER", "YAW_WHILE_SHOOT", "DECOY_COUNT", "DECOY_RELOAD_COOLDOWN", "DECOY_ITEM_COUNT", "SYNCHED_PROPELLER_ROT", "PROPELLER_ROT", "SYNCHED_GEAR_ROT", "GEAR_UP", "FORWARD_INPUT_DOWN", "BACK_INPUT_DOWN", "LEFT_INPUT_DOWN", "RIGHT_INPUT_DOWN", "UP_INPUT_DOWN", "DOWN_INPUT_DOWN", "DECOY_INPUT_DOWN", "FIRE_INPUT_DOWN", "SPRINT_INPUT_DOWN", "PLANE_BREAK", "ENERGY", "LASER_LENGTH", "LASER_SCALE", "LASER_SCALE_O", "CHARGE_PROGRESS", "IS_WRECK", "SYMPATHETIC_DETONATED", "TURRET_BURNED", "TURRET_BURN_TIMER", "HOVER_MODE", "LOCKED", "GUN_DATA_MAP", "", "Lcom/atsuishio/superbwarfare/data/gun/GunData;", "getGUN_DATA_MAP", "()Lnet/minecraft/network/syncher/EntityDataAccessor;", "LOITER_PARAMS", "Lorg/joml/Quaternionf;", "LOITER_ACTIVE", "TOWING_UUIDS", "Lnet/minecraft/nbt/CompoundTag;", "TOWED_BY_UUID", "LIFT_OFFSET", "superbwarfare"})
    public static final class Companion {
        private Companion() {
        }

        @NotNull
        protected final EntityDataAccessor<Map<String, GunData>> getGUN_DATA_MAP() {
            return GUN_DATA_MAP;
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    @Metadata(mv={2, 0, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;
        public static final /* synthetic */ int[] $EnumSwitchMapping$2;

        static {
            int[] nArray = new int[VehicleContainerType.values().length];
            try {
                nArray[VehicleContainerType.MINI.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[VehicleContainerType.SMALL.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[VehicleContainerType.MEDIUM.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[VehicleContainerType.LARGE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[VehicleContainerType.HUGE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[OBB.Part.values().length];
            try {
                nArray[OBB.Part.TURRET.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[OBB.Part.WHEEL_LEFT.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[OBB.Part.WHEEL_RIGHT.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[OBB.Part.MAIN_ENGINE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[OBB.Part.SUB_ENGINE.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
            nArray = new int[EngineType.values().length];
            try {
                nArray[EngineType.WHEEL.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.TRACK.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.HELICOPTER.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.SHIP.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.AIRCRAFT.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.WHEELCHAIR.ordinal()] = 6;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.TOM6.ordinal()] = 7;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[EngineType.AIRSHIP.ordinal()] = 8;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$2 = nArray;
        }
    }
}
