/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.comparisons.ComparisonsKt
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.JvmStatic
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.math.MathKt
 *  kotlin.ranges.RangesKt
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.CameraType
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Vec3i
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.HumanoidArm
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.Pose
 *  net.minecraft.world.entity.npc.AbstractVillager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ClipContext
 *  net.minecraft.world.level.ClipContext$Block
 *  net.minecraft.world.level.ClipContext$Fluid
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.BellBlock
 *  net.minecraft.world.level.block.CrossCollisionBlock
 *  net.minecraft.world.level.block.DoorBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult$Type
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Pre
 *  net.minecraftforge.client.event.RenderHandEvent
 *  net.minecraftforge.client.event.RenderNameTagEvent
 *  net.minecraftforge.client.event.RenderPlayerEvent$Pre
 *  net.minecraftforge.client.event.ViewportEvent$ComputeCameraAngles
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFogColor
 *  net.minecraftforge.client.event.ViewportEvent$ComputeFov
 *  net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
 *  net.minecraftforge.common.capabilities.ForgeCapabilities
 *  net.minecraftforge.energy.IEnergyStorage
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$RenderTickEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.eventbus.api.Event$Result
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 *  org.lwjgl.glfw.GLFW
 *  software.bernie.geckolib.core.animatable.model.CoreGeoBone
 *  software.bernie.geckolib.core.animation.AnimationProcessor
 *  top.theillusivec4.curios.api.CuriosApi
 *  top.theillusivec4.curios.api.type.capability.ICuriosItemHandler
 */
package com.atsuishio.superbwarfare.event;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.api.event.ClientVehicleFireEvent;
import com.atsuishio.superbwarfare.client.ClientSyncedEntityHandler;
import com.atsuishio.superbwarfare.client.animation.AnimationCurves;
import com.atsuishio.superbwarfare.client.animation.entity.VehicleAnimationInstance;
import com.atsuishio.superbwarfare.client.lighting.LightPositionRegistry;
import com.atsuishio.superbwarfare.client.lighting.MuzzleFlashHelper;
import com.atsuishio.superbwarfare.client.lighting.VehicleLightingHandler;
import com.atsuishio.superbwarfare.client.overlay.CrossHairOverlay;
import com.atsuishio.superbwarfare.client.overlay.OverlayTraceHandler;
import com.atsuishio.superbwarfare.client.overlay.VehicleMainWeaponHudOverlay;
import com.atsuishio.superbwarfare.client.shader.ThermalShaderHandler;
import com.atsuishio.superbwarfare.config.client.DisplayConfig;
import com.atsuishio.superbwarfare.config.server.MiscConfig;
import com.atsuishio.superbwarfare.data.gun.Ammo;
import com.atsuishio.superbwarfare.data.gun.AmmoConsumer;
import com.atsuishio.superbwarfare.data.gun.FireMode;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.SeekType;
import com.atsuishio.superbwarfare.data.gun.SeekWeaponInfo;
import com.atsuishio.superbwarfare.data.gun.SoundInfo;
import com.atsuishio.superbwarfare.data.gun.value.AttachmentType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.EngineType;
import com.atsuishio.superbwarfare.data.vehicle.subdata.SeatInfo;
import com.atsuishio.superbwarfare.entity.vehicle.DroneEntity;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.event.ClickEventHandler;
import com.atsuishio.superbwarfare.event.ModVersionEventHandler;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.init.ModKeyMappings;
import com.atsuishio.superbwarfare.init.ModMobEffects;
import com.atsuishio.superbwarfare.init.ModPerks;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import com.atsuishio.superbwarfare.item.gun.GunItem;
import com.atsuishio.superbwarfare.item.gun.launcher.SuperStarShooterItem;
import com.atsuishio.superbwarfare.network.message.send.AimVillagerMessage;
import com.atsuishio.superbwarfare.network.message.send.ArtilleryIndicatorFireMessage;
import com.atsuishio.superbwarfare.network.message.send.LoiterOverrideMessage;
import com.atsuishio.superbwarfare.network.message.send.LungeMineAttackMessage;
import com.atsuishio.superbwarfare.network.message.send.MeleeAttackMessage;
import com.atsuishio.superbwarfare.network.message.send.PlayerStopRidingMessage;
import com.atsuishio.superbwarfare.network.message.send.SeekingWeaponWarningMessage;
import com.atsuishio.superbwarfare.network.message.send.ShootMessage;
import com.atsuishio.superbwarfare.network.message.send.VehicleDisconnectTowingMessage;
import com.atsuishio.superbwarfare.network.message.send.VehicleFireMessage;
import com.atsuishio.superbwarfare.network.message.send.VehicleMovementMessage;
import com.atsuishio.superbwarfare.network.message.send.VehicleUnloadPassengersMessage;
import com.atsuishio.superbwarfare.network.message.send.WeaponZoomingMessage;
import com.atsuishio.superbwarfare.perk.Perk;
import com.atsuishio.superbwarfare.perk.PerkInstance;
import com.atsuishio.superbwarfare.resource.gun.DefaultGunResource;
import com.atsuishio.superbwarfare.resource.gun.GunResource;
import com.atsuishio.superbwarfare.tools.EntityFindUtil;
import com.atsuishio.superbwarfare.tools.FormatToolKt;
import com.atsuishio.superbwarfare.tools.MathTool;
import com.atsuishio.superbwarfare.tools.MillisTimer;
import com.atsuishio.superbwarfare.tools.MinecraftUtil;
import com.atsuishio.superbwarfare.tools.RangeTool;
import com.atsuishio.superbwarfare.tools.SeekTool;
import com.atsuishio.superbwarfare.tools.TraceTool;
import com.atsuishio.superbwarfare.tools.VectorTool;
import com.atsuishio.superbwarfare.tools.VectorToolKt;
import com.atsuishio.superbwarfare.world.saveddata.TDMSavedData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderNameTagEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationProcessor;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.FORGE)
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000\u00a0\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0015\n\u0002\u0010\u0013\n\u0002\b\u0016\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\n\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0095\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0098\u0001H\u0007J\u0013\u0010\u0099\u0001\u001a\u00020>2\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001H\u0007J\u0013\u0010\u009c\u0001\u001a\u00020>2\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001H\u0007J\t\u0010\u009d\u0001\u001a\u00020>H\u0002J\u0014\u0010\u009e\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u009f\u0001H\u0007J\t\u0010\u00a0\u0001\u001a\u00020>H\u0007J\u0012\u0010\u00a1\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\n\u0010\u00a2\u0001\u001a\u00030\u0096\u0001H\u0007J\n\u0010\u00a3\u0001\u001a\u00030\u0096\u0001H\u0007J\u0013\u0010\u00aa\u0001\u001a\u00020>2\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001H\u0007J\u0012\u0010\u00ab\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\n\u0010\u00ac\u0001\u001a\u00030\u0096\u0001H\u0007J\n\u0010\u00ad\u0001\u001a\u00030\u0096\u0001H\u0007J\u001c\u0010\u00ae\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\u001c\u0010\u00b1\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\u0012\u0010\u00b2\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u001c\u0010\u00b3\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\u001c\u0010\u00b4\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\u0012\u0010\u00b5\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u0012\u0010\u00b6\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u001c\u0010\u00b7\u0001\u001a\u00030\u0096\u00012\b\u0010\u00b8\u0001\u001a\u00030\u00b9\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u001c\u0010\u00ba\u0001\u001a\u00030\u0096\u00012\b\u0010\u00b8\u0001\u001a\u00030\u00b9\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u0019\u0010\u00bb\u0001\u001a\u00020>2\u0007\u0010\u00bc\u0001\u001a\u00020]2\u0007\u0010\u00bd\u0001\u001a\u00020]J\u0019\u0010\u00bb\u0001\u001a\u00020>2\u0007\u0010\u00bc\u0001\u001a\u00020]2\u0007\u0010\u00be\u0001\u001a\u00020oJ\u0012\u0010\u00bf\u0001\u001a\u00030\u0096\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\b\u0010\u00c0\u0001\u001a\u00030\u0096\u0001J\b\u0010\u00c1\u0001\u001a\u00030\u0096\u0001J\n\u0010\u00c2\u0001\u001a\u00030\u0096\u0001H\u0002J\u0013\u0010\u00c3\u0001\u001a\u00020>2\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001H\u0007J\u001c\u0010\u00c4\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J$\u0010\u00c5\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\u0007\u0010\u00c6\u0001\u001a\u00020\u00052\u0007\u0010\u00c7\u0001\u001a\u00020\u0005J\u001c\u0010\u00c8\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001J\u0014\u0010\u00c9\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00ca\u0001H\u0007J\b\u0010\u00cb\u0001\u001a\u00030\u0096\u0001J\u0012\u0010\u00cc\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\b\u0010\u00cd\u0001\u001a\u00030\u0096\u0001J\u0012\u0010\u00ce\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\b\u0010\u00cf\u0001\u001a\u00030\u0096\u0001J&\u0010\u00d0\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00d1\u0001\u001a\u00030\u00d2\u00012\b\u0010\u00d3\u0001\u001a\u00030\u00b9\u0001J\u001c\u0010\u00d4\u0001\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\b\u0010\u00d1\u0001\u001a\u00030\u00d2\u0001J\u0014\u0010\u00d5\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00ca\u0001H\u0007J\t\u0010\u00d6\u0001\u001a\u00020CH\u0002J\u0014\u0010\u00d7\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00d8\u0001H\u0007J\u001e\u0010\u00d9\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00d8\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J\u0014\u0010\u00db\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0098\u0001H\u0007J\u0014\u0010\u00dc\u0001\u001a\u00030\u0096\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J\u0014\u0010\u00dd\u0001\u001a\u00030\u0096\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J<\u0010\u00de\u0001\u001a\u00030\u0096\u00012\f\u0010\u00df\u0001\u001a\u0007\u0012\u0002\b\u00030\u00e0\u00012\u0007\u0010\u00e1\u0001\u001a\u00020C2\u0007\u0010\u00e2\u0001\u001a\u00020C2\u0007\u0010\u00e3\u0001\u001a\u00020C2\u0007\u0010\u00e4\u0001\u001a\u00020>H\u0007J\u0014\u0010\u00e5\u0001\u001a\u00030\u0096\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J\u001e\u0010\u00c9\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00d8\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J\\\u0010\u00e6\u0001\u001a\u00030\u0096\u00012\b\u0010\u00e7\u0001\u001a\u00030\u00e8\u00012\u0007\u0010\u00e9\u0001\u001a\u00020C2\u0007\u0010\u00ea\u0001\u001a\u00020C2\u0007\u0010\u00eb\u0001\u001a\u00020C2\u0007\u0010\u00ec\u0001\u001a\u00020C2\u0007\u0010\u00ed\u0001\u001a\u00020C2\u0007\u0010\u00ee\u0001\u001a\u00020C2\u0007\u0010\u00ef\u0001\u001a\u00020C2\u0007\u0010\u00f0\u0001\u001a\u00020CH\u0007J\u0012\u0010\u00f1\u0001\u001a\u00020C2\u0007\u0010\u00f2\u0001\u001a\u00020CH\u0007J\u0012\u0010\u00f3\u0001\u001a\u00020C2\u0007\u0010\u00f2\u0001\u001a\u00020CH\u0007J\u0012\u0010\u00f4\u0001\u001a\u00020C2\u0007\u0010\u00f2\u0001\u001a\u00020CH\u0007J\u0012\u0010\u00f5\u0001\u001a\u00020C2\u0007\u0010\u00f2\u0001\u001a\u00020CH\u0007J\u0012\u0010\u00f6\u0001\u001a\u00020C2\u0007\u0010\u00f2\u0001\u001a\u00020CH\u0007J\n\u0010\u00f7\u0001\u001a\u00030\u0096\u0001H\u0002J\n\u0010\u00f8\u0001\u001a\u00030\u0096\u0001H\u0002J\u001e\u0010\u00f9\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00d8\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J%\u0010\u00fa\u0001\u001a\u00030\u0096\u00012\u0007\u0010\u00fb\u0001\u001a\u00020\u00052\u0007\u0010\u00fc\u0001\u001a\u00020\u00052\u0007\u0010\u00fd\u0001\u001a\u00020\u0005H\u0007J\u0014\u0010\u00fe\u0001\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00d8\u0001H\u0002J\u001e\u0010\u00ff\u0001\u001a\u00030\u0096\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u00012\b\u0010\u00af\u0001\u001a\u00030\u00b0\u0001H\u0002J\u0014\u0010\u0080\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0081\u0002H\u0007J\u0014\u0010\u0082\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0081\u0002H\u0007J\u001b\u0010\u0083\u0002\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u00012\u0007\u0010\u0084\u0002\u001a\u00020oJ\u0014\u0010\u0085\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0086\u0002H\u0007J\u0014\u0010\u0087\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0088\u0002H\u0007J\u0014\u0010\u0089\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u0088\u0002H\u0007J\b\u0010\u008a\u0002\u001a\u00030\u0096\u0001J\b\u0010\u008b\u0002\u001a\u00030\u0096\u0001J\u0014\u0010\u008c\u0002\u001a\u00030\u0096\u00012\b\u0010\u00bc\u0001\u001a\u00030\u00da\u0001H\u0002J:\u0010\u008d\u0002\u001a\u00030\u0096\u00012\u0007\u0010\u00e9\u0001\u001a\u00020C2\u0007\u0010\u00ea\u0001\u001a\u00020C2\u0016\u0010\u008e\u0002\u001a\f\u0012\u0007\b\u0001\u0012\u00030\u00e8\u00010\u008f\u0002\"\u00030\u00e8\u0001H\u0007\u00a2\u0006\u0003\u0010\u0090\u0002J\u0012\u0010\u0091\u0002\u001a\u00030\u0096\u00012\b\u0010\u009a\u0001\u001a\u00030\u009b\u0001J\u001f\u0010\u0092\u0002\u001a\u00020>2\b\u0010\u00af\u0001\u001a\u00030\u00b0\u00012\n\u0010\u0093\u0002\u001a\u0005\u0018\u00010\u0094\u0002H\u0007J\n\u0010\u0095\u0002\u001a\u00030\u0096\u0001H\u0007J\n\u0010\u0096\u0002\u001a\u00030\u0096\u0001H\u0007J\n\u0010\u0097\u0002\u001a\u00030\u0096\u0001H\u0007J\u001e\u0010\u0098\u0002\u001a\u00030\u0096\u00012\b\u0010\u0099\u0002\u001a\u00030\u009a\u00022\b\u0010\u009b\u0002\u001a\u00030\u009c\u0002H\u0007J\u0016\u0010\u009d\u0002\u001a\u00030\u0096\u00012\n\u0010\u009a\u0001\u001a\u0005\u0018\u00010\u009b\u0001H\u0007J\u0016\u0010\u009e\u0002\u001a\u00030\u0096\u00012\n\u0010\u009a\u0001\u001a\u0005\u0018\u00010\u009b\u0001H\u0007J\u0016\u0010\u009f\u0002\u001a\u00030\u0096\u00012\n\u0010\u009a\u0001\u001a\u0005\u0018\u00010\u009b\u0001H\u0007J\u0014\u0010\u00a0\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00a1\u0002H\u0007J\u0014\u0010\u00a2\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00a3\u0002H\u0007J\u0014\u0010\u00a4\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00a5\u0002H\u0007J\u0014\u0010\u00a6\u0002\u001a\u00030\u0096\u00012\b\u0010\u0097\u0001\u001a\u00030\u00a7\u0002H\u0007R\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0006\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0007\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\b\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\t\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\n\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u000b\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\f\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\r\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u000e\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u000f\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0010\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0011\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0012\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0013\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0014\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0015\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0016\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0017\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0018\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u0019\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001a\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001c\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001d\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001e\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u001f\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010 \u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010!\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\"\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010#\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010$\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010%\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010&\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010'\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010(\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010)\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010*\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010+\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010,\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010-\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010.\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010/\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00100\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00101\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00103\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00104\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00105\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00106\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00107\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00108\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u00109\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010:\u001a\u00020;8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010<\u001a\u00020;8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010=\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010?\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010@\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010A\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010B\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010D\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010E\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010F\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010G\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010H\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010I\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010J\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010K\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010L\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010M\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010N\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010O\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010P\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010Q\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010R\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010S\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010T\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010U\u001a\u00020\u001b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010V\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010W\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010X\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010Y\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010Z\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010[\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\\\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010^\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010_\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010`\u001a\u0004\u0018\u00010a8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010b\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010c\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010d\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010e\u001a\u00020C8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010f\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010g\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010h\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010i\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010j\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010k\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010l\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010m\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010n\u001a\u0004\u0018\u00010o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010p\u001a\u0004\u0018\u00010o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010q\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010r\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010s\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010t\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010u\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010v\u001a\u0004\u0018\u00010]8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010w\u001a\u0004\u0018\u00010o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010x\u001a\u0004\u0018\u00010o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010y\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010z\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010{\u001a\u0004\u0018\u00010|8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010}\u001a\u00020~8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0012\u0010\u007f\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0080\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0081\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0082\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0083\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0084\u0001\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0085\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0086\u0001\u001a\u0002028\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0087\u0001\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0088\u0001\u001a\u00030\u0089\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u008a\u0001\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u008b\u0001\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u008c\u0001\u001a\u0005\u0018\u00010\u008d\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u008e\u0001\u001a\u0005\u0018\u00010\u008d\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u008f\u0001\u001a\u00020CX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000f\u0010\u0090\u0001\u001a\u00020CX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0091\u0001\u001a\u00020o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0092\u0001\u001a\u00020o8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0093\u0001\u001a\u0005\u0018\u00010\u0094\u00018\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0002\n\u0000R*\u0010\u00a4\u0001\u001a\u00020>8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0019\n\u0000\u0012\u0005\b\u00a5\u0001\u0010\u0003\u001a\u0006\b\u00a6\u0001\u0010\u00a7\u0001\"\u0006\b\u00a8\u0001\u0010\u00a9\u0001\u00a8\u0006\u00a8\u0002"}, d2={"Lcom/atsuishio/superbwarfare/event/ClientEventHandler;", "", "<init>", "()V", "zoomTime", "", "zoomPos", "zoomPosZ", "swayTime", "swayX", "swayY", "moveTime", "sprintTime", "movePosX", "movePosY", "moveRotZ", "sprintBasicRotX", "sprintBasicRotY", "sprintBasicRotZ", "sprintPosX", "sprintPosY", "sprintBasicPosX", "sprintBasicPosY", "sprintBasicPosZ", "movePosHorizon", "velocityY", "turnRot", "", "cameraRot", "fireRecoilTime", "firePosTimer", "fireRotTimer", "boltMove", "firePosZ", "customAnimSpeed", "recoilHorizon", "recoilY", "recoilForce", "droneFov", "droneFovLerp", "currentFov", "bowPullTimer", "bowPower", "bowPullPos", "gunSpread", "fireSpread", "fireCooldown", "lookDistance", "cameraLocation", "switchVehicleWeaponCooldown", "", "drawTime", "shellIndex", "shellIndexTime", "randomShell", "customZoom", "artilleryIndicatorZoom", "artilleryIndicatorCustomZoom", "clientTimer", "Lcom/atsuishio/superbwarfare/tools/MillisTimer;", "clientTimerVehicle", "holdingFireKey", "", "bowPull", "zoom", "breath", "stamina", "", "switchTime", "moveFadeTime", "sprintFadeTime", "exhaustion", "holdFireVehicle", "zoomVehicle", "burstFireAmount", "customRpm", "gunMelee", "holdingFireKeyTicks", "holdingFireKeyTicks0", "shouldPlayDischargeSound", "revolverPreTime", "revolverWheelPreTime", "shakeTime", "shakeRadius", "shakeAmplitude", "shakePos", "shakeType", "lerpShake", "usingLunge", "lungeAttack", "lungeDraw", "lungeSprint", "lockedEntity", "Lnet/minecraft/world/entity/Entity;", "dismountCountdown", "aimVillagerCountdown", "lastCameraType", "Lnet/minecraft/client/CameraType;", "cameraPitch", "cameraYaw", "cameraRoll", "noSprintTicks", "canDoubleJump", "holdArtilleryIndicator", "holdToEjection", "isEditing", "shootCoolDown", "nearestEntity", "seekingEntity", "lockingEntity", "seekingPos", "Lnet/minecraft/world/phys/Vec3;", "lockingPos", "seekingTime", "guideType", "lockOn", "nearestEntityVehicle", "seekingEntityVehicle", "lockingEntityVehicle", "seekingPosVehicle", "lockingPosVehicle", "seekingTimeVehicle", "lockOnVehicle", "lastOperatingGunUUID", "Ljava/util/UUID;", "keysCache", "", "loiterLastForwardTapTick", "loiterForwardTapCount", "unloadPassengersHoldTicks", "unloadPassengersLastTapTick", "unloadPassengersTapCount", "wasUnloadPassengersDown", "disconnectTowingLastTapTick", "disconnectTowingTapCount", "wasDisconnectTowingDown", "tdmSavedData", "Lcom/atsuishio/superbwarfare/world/saveddata/TDMSavedData;", "activeThermalImaging", "fov", "modelViewMatrix", "Lorg/joml/Matrix4f;", "projectionMatrix", "lastX", "lastY", "bombHitPosO", "bombHitPos", "missileLockingPos", "Lnet/minecraft/core/BlockPos;", "handleWeaponTurn", "", "event", "Lnet/minecraftforge/client/event/RenderHandEvent;", "isFreeCam", "player", "Lnet/minecraft/world/entity/player/Player;", "isNacelleCam", "isMoving", "handleClientTick", "Lnet/minecraftforge/event/TickEvent$ClientTickEvent;", "hasThermalImagingGoggles", "handleThermalImaging", "turnOnThermalImaging", "turnOffThermalImaging", "handsomeGogglesActive", "getHandsomeGogglesActive$annotations", "getHandsomeGogglesActive", "()Z", "setHandsomeGogglesActive", "(Z)V", "isWearingHandsomeGoggles", "handleHandsomeGoggles", "turnOnHandsomeGoggles", "turnOffHandsomeGoggles", "handleShootDelay", "stack", "Lnet/minecraft/world/item/ItemStack;", "handleArtilleryIndicator", "calculateBombHitPos", "handleControlVehicle", "lockWeaponSeeking", "vehicleWeaponSeeking", "seekFailure", "playLockingSound", "data", "Lcom/atsuishio/superbwarfare/data/gun/GunData;", "playLockedSound", "noClip", "entity", "e", "pos", "weaponZooming", "staminaSystem", "handlePlayerSprint", "handleVariableDecrease", "isProne", "handleGunMelee", "doGunMeleeAttack", "angle", "customRange", "handleLungeAttack", "handleWeaponFire", "Lnet/minecraftforge/event/TickEvent$RenderTickEvent;", "handleGunShoot", "shootClient", "handleClientShoot", "playGunClientSounds", "handleVehicleGunShoot", "clientShootVehicle", "vehicle", "Lcom/atsuishio/superbwarfare/entity/vehicle/base/VehicleEntity;", "gunData", "playVehicleClientSounds", "handleWeaponBreathSway", "getDelta", "computeCameraAngles", "Lnet/minecraftforge/client/event/ViewportEvent$ComputeCameraAngles;", "handleDroneCamera", "Lnet/minecraft/world/entity/LivingEntity;", "onRenderHand", "handleWeaponSway", "handleWeaponMove", "gunRootMove", "animationProcessor", "Lsoftware/bernie/geckolib/core/animation/AnimationProcessor;", "customX", "customY", "customZ", "useCustomAnim", "handleWeaponZoom", "handleShootAnimation", "bone", "Lsoftware/bernie/geckolib/core/animatable/model/CoreGeoBone;", "x", "y", "z", "rotX", "rotY", "rotZ", "zoomMultiply", "customSpeed", "getBoneRotX", "t", "getBoneRotY", "getBoneRotZ", "getBoneMoveY", "getBoneMoveZ", "handleWeaponShell", "handleGunRecoil", "handleShockCamera", "handleReloadShake", "boneRotX", "boneRotY", "boneRotZ", "handlePlayerCamera", "handleBowPullAnimation", "captureFov", "Lnet/minecraftforge/client/event/ViewportEvent$ComputeFov;", "onFovUpdate", "look", "target", "setPlayerInvisible", "Lnet/minecraftforge/client/event/RenderPlayerEvent$Pre;", "handleRenderCrossHair", "Lnet/minecraftforge/client/event/RenderGuiOverlayEvent$Pre;", "handleAvoidRenderingHotbar", "resetGunStatus", "resetLungeMineStatus", "handleWeaponDraw", "handleShells", "shells", "", "(FF[Lsoftware/bernie/geckolib/core/animatable/model/CoreGeoBone;)V", "aimAtVillager", "canOpenEditScreen", "hand", "Lnet/minecraft/world/InteractionHand;", "onOpenEditScreen", "onCloseEditScreen", "editModelShake", "stopSoundEvent", "location", "Lnet/minecraft/resources/ResourceLocation;", "source", "Lnet/minecraft/sounds/SoundSource;", "stopVehicleSeekSound", "stopWeaponSeekSound", "stopVehicleReloadSound", "onRenderNameTag", "Lnet/minecraftforge/client/event/RenderNameTagEvent;", "onPlayerLoggedIn", "Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;", "onFogColor", "Lnet/minecraftforge/client/event/ViewportEvent$ComputeFogColor;", "onClientVehicleFire", "Lcom/atsuishio/superbwarfare/api/event/ClientVehicleFireEvent;", "superbwarfare"})
@SourceDebugExtension(value={"SMAP\nClientEventHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ClientEventHandler.kt\ncom/atsuishio/superbwarfare/event/ClientEventHandler\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 MinecraftUtil.kt\ncom/atsuishio/superbwarfare/tools/MinecraftUtil\n*L\n1#1,3150:1\n774#2:3151\n865#2,2:3152\n1053#2:3154\n1557#2:3155\n1628#2,3:3156\n1#3:3159\n144#4,6:3160\n*S KotlinDebug\n*F\n+ 1 ClientEventHandler.kt\ncom/atsuishio/superbwarfare/event/ClientEventHandler\n*L\n1435#1:3151\n1435#1:3152,2\n1436#1:3154\n1443#1:3155\n1443#1:3156,3\n1824#1:3160,6\n*E\n"})
public final class ClientEventHandler {
    @NotNull
    public static final ClientEventHandler INSTANCE = new ClientEventHandler();
    @JvmField
    public static double zoomTime;
    @JvmField
    public static double zoomPos;
    @JvmField
    public static double zoomPosZ;
    @JvmField
    public static double swayTime;
    @JvmField
    public static double swayX;
    @JvmField
    public static double swayY;
    @JvmField
    public static double moveTime;
    @JvmField
    public static double sprintTime;
    @JvmField
    public static double movePosX;
    @JvmField
    public static double movePosY;
    @JvmField
    public static double moveRotZ;
    @JvmField
    public static double sprintBasicRotX;
    @JvmField
    public static double sprintBasicRotY;
    @JvmField
    public static double sprintBasicRotZ;
    @JvmField
    public static double sprintPosX;
    @JvmField
    public static double sprintPosY;
    @JvmField
    public static double sprintBasicPosX;
    @JvmField
    public static double sprintBasicPosY;
    @JvmField
    public static double sprintBasicPosZ;
    @JvmField
    public static double movePosHorizon;
    @JvmField
    public static double velocityY;
    @JvmField
    @NotNull
    public static double[] turnRot;
    @JvmField
    @NotNull
    public static double[] cameraRot;
    @JvmField
    public static double fireRecoilTime;
    @JvmField
    public static double firePosTimer;
    @JvmField
    public static double fireRotTimer;
    @JvmField
    public static double boltMove;
    @JvmField
    public static double firePosZ;
    @JvmField
    public static double customAnimSpeed;
    @JvmField
    public static double recoilHorizon;
    @JvmField
    public static double recoilY;
    @JvmField
    public static double recoilForce;
    @JvmField
    public static double droneFov;
    @JvmField
    public static double droneFovLerp;
    @JvmField
    public static double currentFov;
    @JvmField
    public static double bowPullTimer;
    @JvmField
    public static double bowPower;
    @JvmField
    public static double bowPullPos;
    @JvmField
    public static double gunSpread;
    @JvmField
    public static double fireSpread;
    @JvmField
    public static double fireCooldown;
    @JvmField
    public static double lookDistance;
    @JvmField
    public static double cameraLocation;
    @JvmField
    public static int switchVehicleWeaponCooldown;
    @JvmField
    public static double drawTime;
    @JvmField
    public static int shellIndex;
    @JvmField
    @NotNull
    public static double[] shellIndexTime;
    @JvmField
    @NotNull
    public static double[] randomShell;
    @JvmField
    public static double customZoom;
    @JvmField
    public static double artilleryIndicatorZoom;
    @JvmField
    public static double artilleryIndicatorCustomZoom;
    @JvmField
    @NotNull
    public static MillisTimer clientTimer;
    @JvmField
    @NotNull
    public static MillisTimer clientTimerVehicle;
    @JvmField
    public static boolean holdingFireKey;
    @JvmField
    public static boolean bowPull;
    @JvmField
    public static boolean zoom;
    @JvmField
    public static boolean breath;
    @JvmField
    public static float stamina;
    @JvmField
    public static double switchTime;
    @JvmField
    public static double moveFadeTime;
    @JvmField
    public static double sprintFadeTime;
    @JvmField
    public static boolean exhaustion;
    @JvmField
    public static boolean holdFireVehicle;
    @JvmField
    public static boolean zoomVehicle;
    @JvmField
    public static int burstFireAmount;
    @JvmField
    public static int customRpm;
    @JvmField
    public static int gunMelee;
    @JvmField
    public static int holdingFireKeyTicks;
    @JvmField
    public static float holdingFireKeyTicks0;
    @JvmField
    public static boolean shouldPlayDischargeSound;
    @JvmField
    public static double revolverPreTime;
    @JvmField
    public static double revolverWheelPreTime;
    @JvmField
    public static double shakeTime;
    @JvmField
    public static double shakeRadius;
    @JvmField
    public static double shakeAmplitude;
    @JvmField
    @NotNull
    public static double[] shakePos;
    @JvmField
    public static double shakeType;
    @JvmField
    public static double lerpShake;
    @JvmField
    public static boolean usingLunge;
    @JvmField
    public static int lungeAttack;
    @JvmField
    public static int lungeDraw;
    @JvmField
    public static int lungeSprint;
    @JvmField
    @Nullable
    public static Entity lockedEntity;
    @JvmField
    public static int dismountCountdown;
    @JvmField
    public static int aimVillagerCountdown;
    @JvmField
    @Nullable
    public static CameraType lastCameraType;
    @JvmField
    public static float cameraPitch;
    @JvmField
    public static float cameraYaw;
    @JvmField
    public static float cameraRoll;
    @JvmField
    public static float noSprintTicks;
    @JvmField
    public static boolean canDoubleJump;
    @JvmField
    public static int holdArtilleryIndicator;
    @JvmField
    public static int holdToEjection;
    @JvmField
    public static boolean isEditing;
    @JvmField
    public static int shootCoolDown;
    @JvmField
    @Nullable
    public static Entity nearestEntity;
    @JvmField
    @Nullable
    public static Entity seekingEntity;
    @JvmField
    @Nullable
    public static Entity lockingEntity;
    @JvmField
    @Nullable
    public static Vec3 seekingPos;
    @JvmField
    @Nullable
    public static Vec3 lockingPos;
    @JvmField
    public static int seekingTime;
    @JvmField
    public static int guideType;
    @JvmField
    public static boolean lockOn;
    @JvmField
    @Nullable
    public static Entity nearestEntityVehicle;
    @JvmField
    @Nullable
    public static Entity seekingEntityVehicle;
    @JvmField
    @Nullable
    public static Entity lockingEntityVehicle;
    @JvmField
    @Nullable
    public static Vec3 seekingPosVehicle;
    @JvmField
    @Nullable
    public static Vec3 lockingPosVehicle;
    @JvmField
    public static int seekingTimeVehicle;
    @JvmField
    public static boolean lockOnVehicle;
    @JvmField
    @Nullable
    public static UUID lastOperatingGunUUID;
    @JvmField
    public static short keysCache;
    @JvmField
    public static int loiterLastForwardTapTick;
    @JvmField
    public static int loiterForwardTapCount;
    @JvmField
    public static int unloadPassengersHoldTicks;
    @JvmField
    public static int unloadPassengersLastTapTick;
    @JvmField
    public static int unloadPassengersTapCount;
    @JvmField
    public static boolean wasUnloadPassengersDown;
    @JvmField
    public static int disconnectTowingLastTapTick;
    @JvmField
    public static int disconnectTowingTapCount;
    @JvmField
    public static boolean wasDisconnectTowingDown;
    @JvmField
    @NotNull
    public static TDMSavedData tdmSavedData;
    @JvmField
    public static boolean activeThermalImaging;
    @JvmField
    public static double fov;
    @JvmField
    @Nullable
    public static Matrix4f modelViewMatrix;
    @JvmField
    @Nullable
    public static Matrix4f projectionMatrix;
    private static float lastX;
    private static float lastY;
    @JvmField
    @NotNull
    public static Vec3 bombHitPosO;
    @JvmField
    @NotNull
    public static Vec3 bombHitPos;
    @JvmField
    @Nullable
    public static BlockPos missileLockingPos;
    private static boolean handsomeGogglesActive;

    private ClientEventHandler() {
    }

    @SubscribeEvent
    public final void handleWeaponTurn(@NotNull RenderHandEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        float xRotOffset = Mth.m_14179_((float)event.getPartialTick(), (float)player.f_108588_, (float)player.f_108586_);
        float yRotOffset = Mth.m_14179_((float)event.getPartialTick(), (float)player.f_108587_, (float)player.f_108585_);
        float xRot = player.m_5686_(event.getPartialTick()) - xRotOffset;
        float yRot = player.m_5675_(event.getPartialTick()) - yRotOffset;
        ClientEventHandler.turnRot[0] = RangesKt.coerceIn((double)(0.05 * (double)xRot), (double)-5.0, (double)5.0) * (1.0 - 0.75 * zoomTime);
        ClientEventHandler.turnRot[1] = RangesKt.coerceIn((double)(0.05 * (double)yRot), (double)-10.0, (double)10.0) * (1.0 - 0.75 * zoomTime);
        ClientEventHandler.turnRot[2] = RangesKt.coerceIn((double)(0.1 * (double)yRot), (double)-10.0, (double)10.0) * (1.0 - zoomTime);
    }

    @JvmStatic
    public static final boolean isFreeCam(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Entity vehicle = player.m_20202_();
        return vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).allowFreeCam() && ModKeyMappings.FREE_CAMERA.m_90857_();
    }

    @JvmStatic
    public static final boolean isNacelleCam(@NotNull Player player) {
        GunData data;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity && (data = ((VehicleEntity)vehicle).getGunData((Entity)player)) != null) {
            return data.get(GunProp.USE_NACELLE_CAMERA) != false && zoomVehicle;
        }
        return false;
    }

    private final boolean isMoving() {
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return false;
        }
        LocalPlayer player = localPlayer;
        return MinecraftUtil.getMc().f_91066_.f_92086_.m_90857_() || MinecraftUtil.getMc().f_91066_.f_92088_.m_90857_() || MinecraftUtil.getMc().f_91066_.f_92085_.m_90857_() || MinecraftUtil.getMc().f_91066_.f_92087_.m_90857_() || player.m_20142_();
    }

    @SubscribeEvent
    public final void handleClientTick(@NotNull TickEvent.ClientTickEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        if (MinecraftUtil.getMc().m_260875_() <= 20) {
            this.handleGunShoot();
            this.handleVehicleGunShoot();
        }
        ItemStack stack = player.m_21205_();
        if (MinecraftUtil.getNotInGame() && !ClickEventHandler.switchZoom) {
            zoom = false;
        }
        if (player.m_20096_() && canDoubleJump) {
            canDoubleJump = false;
        }
        recoilForce *= 0.55;
        ClientSyncedEntityHandler.clean();
        ClientEventHandler.isProne((Player)player);
        this.handleVariableDecrease();
        this.aimAtVillager((Player)player);
        CrossHairOverlay.handleRenderDamageIndicator();
        this.staminaSystem();
        this.handlePlayerSprint();
        Player player2 = (Player)player;
        Intrinsics.checkNotNull((Object)stack);
        this.handleLungeAttack(player2, stack);
        this.handleGunMelee((Player)player, stack);
        this.weaponZooming(stack);
        this.lockWeaponSeeking((Player)player, stack);
        this.vehicleWeaponSeeking((Player)player);
        this.handleThermalImaging((Player)player);
        this.handleHandsomeGoggles((Player)player);
        this.handleShootDelay((Player)player, stack);
        this.handleControlVehicle((Player)player, stack);
        this.handleArtilleryIndicator((Player)player, stack);
        this.calculateBombHitPos((Player)player);
        LightPositionRegistry.tick();
    }

    @JvmStatic
    public static final boolean hasThermalImagingGoggles() {
        Boolean bl = CuriosApi.getCuriosInventory((LivingEntity)((LivingEntity)MinecraftUtil.getLocalPlayer())).map(arg_0 -> ClientEventHandler.hasThermalImagingGoggles$lambda$1(ClientEventHandler::hasThermalImagingGoggles$lambda$0, arg_0)).orElseGet(ClientEventHandler::hasThermalImagingGoggles$lambda$2);
        Intrinsics.checkNotNullExpressionValue((Object)bl, (String)"orElseGet(...)");
        return bl;
    }

    public final void handleThermalImaging(@NotNull Player player) {
        SeatInfo seat;
        int index;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        boolean hasThermalImagingGoggles = ClientEventHandler.hasThermalImagingGoggles();
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity && (index = ((VehicleEntity)vehicle).getSeatIndex((Entity)player)) != -1 && (seat = (SeatInfo)CollectionsKt.getOrNull(((VehicleEntity)vehicle).computed().seats(), (int)index)) != null && seat.hasThermalImaging) {
            hasThermalImagingGoggles = true;
        }
        if (!activeThermalImaging || !hasThermalImagingGoggles) {
            activeThermalImaging = false;
            ClientEventHandler.turnOffThermalImaging();
        } else if (Minecraft.m_91087_().f_91063_.m_109149_() == null) {
            ClientEventHandler.turnOnThermalImaging();
        }
    }

    @JvmStatic
    public static final void turnOnThermalImaging() {
        ThermalShaderHandler.Companion.setActive(true);
        MinecraftUtil.getMc().f_91063_.m_109128_(Mod.Companion.loc("shaders/post/night_vision.json"));
    }

    @JvmStatic
    public static final void turnOffThermalImaging() {
        if (ThermalShaderHandler.Companion.isActive()) {
            MinecraftUtil.getMc().f_91063_.m_109086_();
            ThermalShaderHandler.Companion.setActive(false);
        }
    }

    public static final boolean getHandsomeGogglesActive() {
        return handsomeGogglesActive;
    }

    public static final void setHandsomeGogglesActive(boolean bl) {
        handsomeGogglesActive = bl;
    }

    @JvmStatic
    public static /* synthetic */ void getHandsomeGogglesActive$annotations() {
    }

    @JvmStatic
    public static final boolean isWearingHandsomeGoggles(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        return player.m_6844_(EquipmentSlot.HEAD).m_150930_((Item)ModItems.HANDSOME_GOGGLES.get());
    }

    public final void handleHandsomeGoggles(@NotNull Player player) {
        boolean shouldBeActive;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        boolean wearing = ClientEventHandler.isWearingHandsomeGoggles(player);
        boolean isFirstPerson = MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.FIRST_PERSON;
        boolean bl = shouldBeActive = wearing && isFirstPerson;
        if (shouldBeActive && !handsomeGogglesActive) {
            handsomeGogglesActive = false;
            ClientEventHandler.turnOnHandsomeGoggles();
        } else if (!shouldBeActive && handsomeGogglesActive) {
            ClientEventHandler.turnOffHandsomeGoggles();
        }
    }

    @JvmStatic
    public static final void turnOnHandsomeGoggles() {
        handsomeGogglesActive = true;
        MinecraftUtil.getMc().f_91063_.m_109128_(Mod.Companion.loc("shaders/post/handsome_goggles.json"));
    }

    @JvmStatic
    public static final void turnOffHandsomeGoggles() {
        handsomeGogglesActive = false;
        MinecraftUtil.getMc().f_91063_.m_109086_();
    }

    public final void handleShootDelay(@NotNull Player player, @NotNull ItemStack stack) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        Item item = stack.m_41720_();
        if (item instanceof GunItem) {
            GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
            UUID uuid = null;
            try {
                uuid = data.gunDataTag.m_128342_("UUID");
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (MinecraftUtil.getNotInGame()) {
                burstFireAmount = 0;
            }
            if (uuid == null || !Intrinsics.areEqual((Object)uuid, (Object)lastOperatingGunUUID)) {
                this.resetGunStatus();
                this.resetLungeMineStatus();
            }
            lastOperatingGunUUID = uuid;
            if ((holdingFireKey || zoom && stack.m_150930_((Item)ModItems.MINIGUN.get())) && ((GunItem)item).canShoot(data, (Entity)player)) {
                holdingFireKeyTicks = RangesKt.coerceAtMost((int)(holdingFireKeyTicks + 1), (int)(((Number)data.get(GunProp.SHOOT_DELAY)).intValue() + 1));
                MuzzleFlashHelper.spawnToolFlash(player, stack);
                if (stack.m_150930_((Item)ModItems.MINIGUN.get())) {
                    float rpm = ((Number)data.get(GunProp.RPM)).floatValue() / 3600.0f;
                    player.m_5496_((SoundEvent)ModSounds.MINIGUN_ROTATE.get(), 1.0f, 0.7f + rpm);
                }
                if (stack.m_150930_((Item)ModItems.QL_1031.get()) && player.f_19797_ % 5 == 0) {
                    double random = (Math.random() - 0.5) * (double)2;
                    player.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_276452_, player.m_20185_() + random, player.m_20188_() + 0.5 * random, player.m_20189_() + random, 0.0, 0.0, 0.0);
                }
            }
        } else {
            lastOperatingGunUUID = null;
        }
    }

    public final void handleArtilleryIndicator(@NotNull Player player, @NotNull ItemStack stack) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        if ((stack.m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get()) || stack.m_150930_((Item)ModItems.MONITOR.get()) && player.m_21206_().m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get())) && holdingFireKey) {
            if ((holdArtilleryIndicator = RangesKt.coerceIn((int)(holdArtilleryIndicator + 1), (int)0, (int)20)) >= 19 && shootCoolDown == 0) {
                MinecraftUtil.sendPacketToServer(ArtilleryIndicatorFireMessage.INSTANCE);
                shootCoolDown = 10;
            }
        } else {
            holdArtilleryIndicator = 0;
        }
        if (shootCoolDown > 0) {
            int n = shootCoolDown;
            shootCoolDown = n + -1;
        }
    }

    public final void calculateBombHitPos(@NotNull Player player) {
        Entity entity;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Entity entity2 = player.m_20202_();
        VehicleEntity vehicleEntity = entity2 instanceof VehicleEntity ? (VehicleEntity)entity2 : null;
        if (vehicleEntity == null) {
            return;
        }
        VehicleEntity vehicle = vehicleEntity;
        GunData gunData = vehicle.getGunData((Entity)player);
        bombHitPosO = bombHitPos;
        if (gunData != null && Intrinsics.areEqual((Object)gunData.get(GunProp.CROSSHAIR), (Object)"@AirBomb")) {
            entity = vehicle.bombHitPos((Entity)player);
        } else {
            entity2 = Vec3.f_82478_;
            Intrinsics.checkNotNull((Object)entity2);
            entity = entity2;
        }
        bombHitPos = entity;
    }

    public final void handleControlVehicle(@NotNull Player player, @NotNull ItemStack stack) {
        int currentTick;
        int forwardJustPressed;
        Entity vehicle;
        short keys;
        block36: {
            block35: {
                Intrinsics.checkNotNullParameter((Object)player, (String)"player");
                Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
                keys = 0;
                vehicle = player.m_20202_();
                if (!MinecraftUtil.getNotInGame() && vehicle instanceof VehicleEntity && Intrinsics.areEqual((Object)((VehicleEntity)vehicle).m_146895_(), (Object)player)) break block35;
                if (!stack.m_150930_((Item)ModItems.MONITOR.get()) || stack.m_41783_() == null) break block36;
                CompoundTag compoundTag = stack.m_41783_();
                Intrinsics.checkNotNull((Object)compoundTag);
                if (!compoundTag.m_128471_("Using")) break block36;
                CompoundTag compoundTag2 = stack.m_41783_();
                Intrinsics.checkNotNull((Object)compoundTag2);
                if (!compoundTag2.m_128471_("Linked")) break block36;
            }
            if (ModKeyMappings.MOVE_LEFT.m_90857_()) {
                keys = (short)(keys | 1);
            }
            if (ModKeyMappings.MOVE_RIGHT.m_90857_()) {
                keys = (short)(keys | 2);
            }
            if (ModKeyMappings.MOVE_FORWARD.m_90857_()) {
                keys = (short)(keys | 4);
            }
            if (ModKeyMappings.MOVE_BACKWARD.m_90857_()) {
                keys = (short)(keys | 8);
            }
            if (ModKeyMappings.MOVE_SPACE.m_90857_()) {
                keys = (short)(keys | 0x10);
            }
            if (ModKeyMappings.MOVE_SHIFT.m_90857_()) {
                keys = (short)(keys | 0x20);
            }
            if (ModKeyMappings.RELEASE_DECOY.m_90857_()) {
                keys = (short)(keys | 0x40);
            }
            if (holdFireVehicle) {
                keys = (short)(keys | 0x80);
            }
            if (ModKeyMappings.MOVE_CTRL.m_90857_()) {
                keys = (short)(keys | 0x100);
            }
        }
        if (keys != keysCache) {
            boolean blockLoiter;
            boolean bl = blockLoiter = vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).getLoiterActive() && ((VehicleEntity)vehicle).computed().getEngineType() == EngineType.AIRCRAFT;
            if (!blockLoiter) {
                MinecraftUtil.sendPacketToServer(new VehicleMovementMessage(keys));
            } else {
                int forwardBit = 4;
                int n = forwardJustPressed = (keys & forwardBit) != 0 && (keysCache & forwardBit) == 0 ? 1 : 0;
                if (forwardJustPressed != 0) {
                    int currentTick2 = player.f_19797_;
                    if (currentTick2 - loiterLastForwardTapTick <= 10) {
                        MinecraftUtil.sendPacketToServer(LoiterOverrideMessage.INSTANCE);
                        loiterForwardTapCount = 0;
                        loiterLastForwardTapTick = -20;
                    } else {
                        loiterForwardTapCount = 1;
                        loiterLastForwardTapTick = currentTick2;
                        Object[] objectArray = new Object[]{ModKeyMappings.MOVE_FORWARD.getKey().m_84875_().getString()};
                        player.m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.loiter_override_hint", (Object[])objectArray), true);
                    }
                }
            }
            keysCache = keys;
        }
        if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).allowEjection(((VehicleEntity)vehicle).getSeatIndex((Entity)player)) && ModKeyMappings.DISMOUNT.m_90857_()) {
            if ((holdToEjection = RangesKt.coerceIn((int)(holdToEjection + 1), (int)0, (int)10)) >= 10) {
                MinecraftUtil.sendPacketToServer(new PlayerStopRidingMessage(true));
                ClientEventHandler.stopVehicleReloadSound(player);
            }
        } else {
            holdToEjection = 0;
        }
        if (vehicle instanceof VehicleEntity && Intrinsics.areEqual((Object)((VehicleEntity)vehicle).m_146895_(), (Object)player) && ((VehicleEntity)vehicle).f_19823_.size() > 1) {
            boolean unloadDown = ModKeyMappings.UNLOAD_PASSENGERS.m_90857_();
            boolean unloadJustPressed = unloadDown && !wasUnloadPassengersDown;
            wasUnloadPassengersDown = unloadDown;
            if (unloadDown) {
                forwardJustPressed = unloadPassengersHoldTicks;
                if ((unloadPassengersHoldTicks = forwardJustPressed + 1) >= 20) {
                    MinecraftUtil.sendPacketToServer(new VehicleUnloadPassengersMessage(false));
                    unloadPassengersHoldTicks = 0;
                }
            } else {
                unloadPassengersHoldTicks = 0;
            }
            if (unloadJustPressed) {
                currentTick = player.f_19797_;
                if (currentTick - unloadPassengersLastTapTick <= 10) {
                    MinecraftUtil.sendPacketToServer(new VehicleUnloadPassengersMessage(true));
                    unloadPassengersTapCount = 0;
                    unloadPassengersLastTapTick = -20;
                    unloadPassengersHoldTicks = 0;
                } else {
                    unloadPassengersTapCount = 1;
                    unloadPassengersLastTapTick = currentTick;
                    Object[] objectArray = new Object[]{ModKeyMappings.UNLOAD_PASSENGERS.getKey().m_84875_().getString(), ModKeyMappings.UNLOAD_PASSENGERS.getKey().m_84875_().getString()};
                    player.m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.unload_passengers_hint", (Object[])objectArray), true);
                }
            }
        } else {
            wasUnloadPassengersDown = false;
            unloadPassengersHoldTicks = 0;
        }
        if (vehicle instanceof VehicleEntity && Intrinsics.areEqual((Object)((VehicleEntity)vehicle).m_146895_(), (Object)player)) {
            boolean towingDown = ModKeyMappings.DISCONNECT_TOWING.m_90857_();
            boolean towingJustPressed = towingDown && !wasDisconnectTowingDown;
            wasDisconnectTowingDown = towingDown;
            if (towingJustPressed) {
                currentTick = player.f_19797_;
                if (currentTick - disconnectTowingLastTapTick <= 10) {
                    MinecraftUtil.sendPacketToServer(VehicleDisconnectTowingMessage.INSTANCE);
                    disconnectTowingTapCount = 0;
                    disconnectTowingLastTapTick = -20;
                } else {
                    disconnectTowingTapCount = 1;
                    disconnectTowingLastTapTick = currentTick;
                    Object[] objectArray = new Object[]{ModKeyMappings.DISCONNECT_TOWING.getKey().m_84875_().getString()};
                    player.m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.disconnect_towing_hint", (Object[])objectArray), true);
                }
            }
        } else {
            wasDisconnectTowingDown = false;
        }
    }

    /*
     * Unable to fully structure code
     */
    public final void lockWeaponSeeking(@NotNull Player player, @NotNull ItemStack stack) {
        block42: {
            block49: {
                block43: {
                    block52: {
                        block53: {
                            block44: {
                                block50: {
                                    block51: {
                                        block45: {
                                            block48: {
                                                block46: {
                                                    block47: {
                                                        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
                                                        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
                                                        item = stack.m_41720_();
                                                        if (!(item instanceof GunItem)) break block42;
                                                        data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
                                                        lockTime = ((Number)data.get(GunProp.SEEK_TIME)).intValue();
                                                        fovAdjust = ((Number)MinecraftUtil.getMc().f_91066_.m_231837_().m_231551_()).floatValue() / 80.0f;
                                                        seekAngle = ((Number)data.get(GunProp.SEEK_ANGLE)).doubleValue() * (double)fovAdjust;
                                                        range = ((Number)data.get(GunProp.SEEK_RANGE)).doubleValue();
                                                        maxGuidedRange = ((Number)data.get(GunProp.MAX_GUIDED_RANGE)).doubleValue();
                                                        canGuidedByRadar = data.get(GunProp.CAN_GUIDED_BY_RADAR);
                                                        affectedByStealthTarget = data.get(GunProp.AFFECTED_BY_STEALTH_TARGET);
                                                        cameraPos = MinecraftUtil.getMc().f_91063_.m_109153_().m_90583_();
                                                        if (!(ClientEventHandler.zoomTime > 0.7)) break block43;
                                                        ClientEventHandler.nearestEntity = new SeekTool.Builder((Entity)player, false, 2, null).withinRangeSeekWeapon(range, maxGuidedRange, affectedByStealthTarget, canGuidedByRadar).withinAngle(seekAngle).baseFilter().heightRange(((Number)data.get(GunProp.MIN_TARGET_HEIGHT)).doubleValue(), ((Number)data.get(GunProp.MAX_TARGET_HEIGHT)).doubleValue()).smokeFilter().noVehicle().noClip().buildWithClosestSeekWeapon(canGuidedByRadar);
                                                        Intrinsics.checkNotNull((Object)cameraPos);
                                                        v0 = player.m_20252_(1.0f);
                                                        Intrinsics.checkNotNullExpressionValue((Object)v0, (String)"getViewVector(...)");
                                                        decoy = TraceTool.findLookDecoy(player, cameraPos, v0, range);
                                                        if (decoy != null && decoy.m_6095_().m_204039_(ModTags.EntityTypes.DECOY)) {
                                                            ClientEventHandler.nearestEntity = decoy;
                                                            this.seekFailure(player);
                                                        }
                                                        if (data.get(GunProp.SEEK_TYPE) != SeekType.HOLD_FIRE) break block44;
                                                        if (ClientEventHandler.nearestEntity != null && !player.m_6144_()) break block45;
                                                        result = player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(player.m_20252_(1.0f).m_82490_(512.0)), ClipContext.Block.VISUAL, ClipContext.Fluid.ANY, (Entity)player));
                                                        ClientEventHandler.seekingPos = result.m_82450_();
                                                        if (ClientEventHandler.seekingTime > lockTime + 2 && !ClientEventHandler.lockOn) {
                                                            ClientEventHandler.lockOn = true;
                                                        }
                                                        if (ClientEventHandler.lockingPos == null) break block46;
                                                        v1 = player.m_20154_();
                                                        Intrinsics.checkNotNullExpressionValue((Object)v1, (String)"getLookAngle(...)");
                                                        v2 = player.m_146892_();
                                                        v3 = ClientEventHandler.lockingPos;
                                                        Intrinsics.checkNotNull((Object)v3);
                                                        v4 = v2.m_82505_(v3);
                                                        Intrinsics.checkNotNullExpressionValue((Object)v4, (String)"vectorTo(...)");
                                                        if (VectorToolKt.angleTo(v1, v4) > seekAngle) break block47;
                                                        v5 = (Entity)player;
                                                        v6 = ClientEventHandler.lockingPos;
                                                        Intrinsics.checkNotNull((Object)v6);
                                                        if (this.noClip(v5, v6)) break block46;
                                                    }
                                                    ClientEventHandler.seekingTime = 0;
                                                    this.seekFailure(player);
                                                }
                                                if (!ClientEventHandler.holdingFireKey) break block48;
                                                if (ClientEventHandler.seekingPos == null) ** GOTO lbl-1000
                                                v7 = ClientEventHandler.seekingPos;
                                                Intrinsics.checkNotNull((Object)v7);
                                                if (v7.m_82557_(player.m_146892_()) < range * range) {
                                                    var18_17 = ClientEventHandler.seekingTime;
                                                    if ((ClientEventHandler.seekingTime = var18_17 + 1) == 1) {
                                                        ClientEventHandler.lockingPos = ClientEventHandler.seekingPos;
                                                    }
                                                } else lbl-1000:
                                                // 2 sources

                                                {
                                                    ClientEventHandler.seekingTime = 0;
                                                    ClientEventHandler.lockingPos = null;
                                                }
                                                ClientEventHandler.guideType = 1;
                                                break block49;
                                            }
                                            if (ClientEventHandler.lockOn) {
                                                if (ClientEventHandler.lockingPos != null) {
                                                    v8 = ClientEventHandler.lockingPos;
                                                    Intrinsics.checkNotNull((Object)v8);
                                                    MinecraftUtil.sendPacketToServer(new ShootMessage(ClientEventHandler.gunSpread, ClientEventHandler.zoom, null, v8.m_252839_()));
                                                }
                                                ClientEventHandler.lockOn = false;
                                            }
                                            this.seekFailure(player);
                                            break block49;
                                        }
                                        if (ClientEventHandler.seekingTime > lockTime + 2 && !ClientEventHandler.lockOn) {
                                            ClientEventHandler.lockingEntity = ClientEventHandler.seekingEntity;
                                            ClientEventHandler.lockOn = true;
                                        }
                                        if (ClientEventHandler.seekingEntity == null) break block50;
                                        v9 = player.m_20154_();
                                        Intrinsics.checkNotNullExpressionValue((Object)v9, (String)"getLookAngle(...)");
                                        v10 = player.m_146892_();
                                        v11 = ClientEventHandler.seekingEntity;
                                        Intrinsics.checkNotNull((Object)v11);
                                        v12 = v10.m_82505_(VectorTool.lerpGetEntityBoundingBoxCenter(v11, 1.0f));
                                        Intrinsics.checkNotNullExpressionValue((Object)v12, (String)"vectorTo(...)");
                                        if (VectorToolKt.angleTo(v9, v12) > seekAngle || !SeekTool.NOT_IN_SMOKE.test(ClientEventHandler.seekingEntity)) break block51;
                                        v13 = (Entity)player;
                                        v14 = ClientEventHandler.seekingEntity;
                                        Intrinsics.checkNotNull((Object)v14);
                                        if (this.noClip(v13, v14)) break block50;
                                    }
                                    this.seekFailure(player);
                                }
                                if (ClientEventHandler.holdingFireKey) {
                                    if (ClientEventHandler.seekingEntity == null) {
                                        ClientEventHandler.seekingEntity = ClientEventHandler.nearestEntity;
                                    }
                                    if (ClientEventHandler.nearestEntity != null && ClientEventHandler.lockingPos == null) {
                                        var17_15 = ClientEventHandler.seekingTime;
                                        ClientEventHandler.seekingTime = var17_15 + 1;
                                        v15 = ClientEventHandler.seekingEntity;
                                        Intrinsics.checkNotNull((Object)v15);
                                        if (!(v15.f_19823_.isEmpty() && !(ClientEventHandler.seekingEntity instanceof VehicleEntity) || player.f_19797_ % 3 != 0 || ClientEventHandler.lockOn)) {
                                            v16 = ClientEventHandler.seekingEntity;
                                            Intrinsics.checkNotNull((Object)v16);
                                            v17 = v16.m_20148_();
                                            Intrinsics.checkNotNullExpressionValue((Object)v17, (String)"getUUID(...)");
                                            MinecraftUtil.sendPacketToServer(new SeekingWeaponWarningMessage(false, v17));
                                        }
                                        ClientEventHandler.guideType = 0;
                                    }
                                } else {
                                    if (ClientEventHandler.lockOn) {
                                        if (ClientEventHandler.lockingEntity != null) {
                                            v18 = ClientEventHandler.lockingEntity;
                                            Intrinsics.checkNotNull((Object)v18);
                                            v19 = v18.m_20148_();
                                            v20 = ClientEventHandler.lockingEntity;
                                            Intrinsics.checkNotNull((Object)v20);
                                            MinecraftUtil.sendPacketToServer(new ShootMessage(ClientEventHandler.gunSpread, ClientEventHandler.zoom, v19, v20.m_146892_().m_252839_()));
                                        }
                                        ClientEventHandler.lockOn = false;
                                    }
                                    this.seekFailure(player);
                                }
                                break block49;
                            }
                            if (data.get(GunProp.SEEK_TYPE) != SeekType.HOLD_ZOOM) break block49;
                            if (ClientEventHandler.seekingTime > lockTime + 2 && !ClientEventHandler.lockOn) {
                                ClientEventHandler.lockingEntity = ClientEventHandler.seekingEntity;
                                ClientEventHandler.lockOn = true;
                            }
                            if (ClientEventHandler.seekingEntity == null) break block52;
                            v21 = player.m_20154_();
                            Intrinsics.checkNotNullExpressionValue((Object)v21, (String)"getLookAngle(...)");
                            v22 = player.m_146892_();
                            v23 = ClientEventHandler.seekingEntity;
                            Intrinsics.checkNotNull((Object)v23);
                            v24 = v22.m_82505_(VectorTool.lerpGetEntityBoundingBoxCenter(v23, 1.0f));
                            Intrinsics.checkNotNullExpressionValue((Object)v24, (String)"vectorTo(...)");
                            if (VectorToolKt.angleTo(v21, v24) > seekAngle || !SeekTool.NOT_IN_SMOKE.test(ClientEventHandler.seekingEntity)) break block53;
                            v25 = (Entity)player;
                            v26 = ClientEventHandler.seekingEntity;
                            Intrinsics.checkNotNull((Object)v26);
                            if (this.noClip(v25, v26)) break block52;
                        }
                        this.seekFailure(player);
                    }
                    if (ClientEventHandler.zoomTime > 0.7) {
                        if (ClientEventHandler.seekingEntity == null) {
                            ClientEventHandler.seekingEntity = ClientEventHandler.nearestEntity;
                        }
                        if (ClientEventHandler.nearestEntity != null && data.hasEnoughAmmoToShoot((Entity)player)) {
                            var17_16 = ClientEventHandler.seekingTime;
                            ClientEventHandler.seekingTime = var17_16 + 1;
                            v27 = ClientEventHandler.seekingEntity;
                            Intrinsics.checkNotNull((Object)v27);
                            if (!(v27.f_19823_.isEmpty() && !(ClientEventHandler.seekingEntity instanceof VehicleEntity) || player.f_19797_ % 3 != 0 || ClientEventHandler.lockOn)) {
                                v28 = ClientEventHandler.seekingEntity;
                                Intrinsics.checkNotNull((Object)v28);
                                v29 = v28.m_20148_();
                                Intrinsics.checkNotNullExpressionValue((Object)v29, (String)"getUUID(...)");
                                MinecraftUtil.sendPacketToServer(new SeekingWeaponWarningMessage(false, v29));
                            }
                        }
                    } else {
                        this.seekFailure(player);
                    }
                    if (ClientEventHandler.lockOn && ClientEventHandler.holdingFireKey && ClientEventHandler.lockingEntity != null) {
                        v30 = ClientEventHandler.lockingEntity;
                        Intrinsics.checkNotNull((Object)v30);
                        v31 = v30.m_20148_();
                        v32 = ClientEventHandler.lockingEntity;
                        Intrinsics.checkNotNull((Object)v32);
                        MinecraftUtil.sendPacketToServer(new ShootMessage(ClientEventHandler.gunSpread, ClientEventHandler.zoom, v31, v32.m_146892_().m_252839_()));
                        ClientEventHandler.holdingFireKey = false;
                    }
                    break block49;
                }
                this.seekFailure(player);
            }
            if (ClientEventHandler.nearestEntity != null) {
                v33 = ClientEventHandler.nearestEntity;
                Intrinsics.checkNotNull((Object)v33);
                if (v33.m_6095_().m_204039_(ModTags.EntityTypes.DECOY)) {
                    this.seekFailure(player);
                }
            }
            if (ClientEventHandler.lockingEntity != null) {
                v34 = ClientEventHandler.lockingEntity;
                Intrinsics.checkNotNull((Object)v34);
                if (!v34.m_6084_()) {
                    this.seekFailure(player);
                }
            }
            if (ClientEventHandler.seekingTime == 2) {
                this.playLockingSound(data, player);
            }
            if (ClientEventHandler.seekingTime > lockTime) {
                this.playLockedSound(data, player);
                if (ClientEventHandler.guideType == 0 && ClientEventHandler.lockingEntity != null) {
                    v35 = ClientEventHandler.lockingEntity;
                    Intrinsics.checkNotNull((Object)v35);
                    if ((!v35.f_19823_.isEmpty() || ClientEventHandler.lockingEntity instanceof VehicleEntity) && player.f_19797_ % 2 == 0) {
                        v36 = ClientEventHandler.lockingEntity;
                        Intrinsics.checkNotNull((Object)v36);
                        v37 = v36.m_20148_();
                        Intrinsics.checkNotNullExpressionValue((Object)v37, (String)"getUUID(...)");
                        MinecraftUtil.sendPacketToServer(new SeekingWeaponWarningMessage(true, v37));
                    }
                }
            }
        }
    }

    /*
     * Unable to fully structure code
     */
    public final void vehicleWeaponSeeking(@NotNull Player player) {
        block35: {
            block36: {
                block34: {
                    block30: {
                        block33: {
                            block31: {
                                block32: {
                                    Intrinsics.checkNotNullParameter((Object)player, (String)"player");
                                    var4_2 = player.m_20202_();
                                    v0 = var4_2 instanceof VehicleEntity != false ? (VehicleEntity)var4_2 : null;
                                    if (v0 == null) {
                                        return;
                                    }
                                    vehicle = v0;
                                    v1 = vehicle.getGunData((Entity)player);
                                    if (v1 == null) {
                                        return;
                                    }
                                    data = v1;
                                    v2 = data.get(GunProp.SEEK_WEAPON_INFO);
                                    if (v2 == null) {
                                        return;
                                    }
                                    seekWeaponInfo = v2;
                                    lockTime = seekWeaponInfo.getSeekTime();
                                    seekAngle = seekWeaponInfo.getSeekAngle();
                                    seekRange = seekWeaponInfo.getSeekRange();
                                    cameraPos = MinecraftUtil.getMc().f_91063_.m_109153_().m_90583_();
                                    v3 = vehicle.getSeekVec((Entity)player, 1.0f);
                                    if (v3 == null) {
                                        return;
                                    }
                                    seekVec = v3;
                                    minTargetHeight = seekWeaponInfo.getMinTargetHeight();
                                    maxTargetHeight = seekWeaponInfo.getMaxTargetHeight();
                                    minTargetSize = seekWeaponInfo.getMinTargetSize();
                                    maxGuidedRange = seekWeaponInfo.getMaxGuidedRange();
                                    canGuidedByRadar = seekWeaponInfo.getCanGuidedByRadar();
                                    affectedByStealthTarget = seekWeaponInfo.getAffectedByStealthTarget();
                                    ClientEventHandler.nearestEntityVehicle = new SeekTool.Builder((Entity)player, false, 2, null).withinRangeSeekWeapon(seekRange, maxGuidedRange, affectedByStealthTarget, canGuidedByRadar).withinAngle(cameraPos, seekVec, seekAngle).baseFilter().heightRange(minTargetHeight, maxTargetHeight).sizeBiggerThan(minTargetSize).smokeFilter().noVehicle().noClip().notFriendly().buildWithClosest(cameraPos, seekVec, canGuidedByRadar);
                                    Intrinsics.checkNotNull((Object)cameraPos);
                                    decoy = TraceTool.findLookDecoy(player, cameraPos, seekVec, seekRange);
                                    if (decoy != null && decoy.m_6095_().m_204039_(ModTags.EntityTypes.DECOY)) {
                                        ClientEventHandler.nearestEntityVehicle = decoy;
                                        this.seekFailure(player);
                                    }
                                    if (!seekWeaponInfo.getOnlyLockBlock()) break block30;
                                    result = player.m_9236_().m_45547_(new ClipContext(cameraPos, cameraPos.m_82549_(seekVec.m_82490_(seekRange)), ClipContext.Block.VISUAL, ClipContext.Fluid.ANY, (Entity)player));
                                    ClientEventHandler.seekingPosVehicle = result.m_82450_();
                                    if (ClientEventHandler.seekingTimeVehicle > lockTime + 2 && !ClientEventHandler.lockOnVehicle) {
                                        ClientEventHandler.lockOnVehicle = true;
                                    }
                                    if (ClientEventHandler.lockingPosVehicle == null) break block31;
                                    v4 = ClientEventHandler.lockingPosVehicle;
                                    Intrinsics.checkNotNull((Object)v4);
                                    v5 = cameraPos.m_82505_(v4);
                                    Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"vectorTo(...)");
                                    if (VectorToolKt.angleTo(seekVec, v5) > seekAngle) break block32;
                                    v6 = (Entity)player;
                                    v7 = ClientEventHandler.lockingPosVehicle;
                                    Intrinsics.checkNotNull((Object)v7);
                                    if (this.noClip(v6, v7)) break block31;
                                }
                                this.seekFailure(player);
                            }
                            if (!ModKeyMappings.VEHICLE_SEEK.m_90857_()) break block33;
                            if (ClientEventHandler.seekingPosVehicle == null) ** GOTO lbl-1000
                            v8 = ClientEventHandler.seekingPosVehicle;
                            Intrinsics.checkNotNull((Object)v8);
                            if (v8.m_82557_(cameraPos) < seekRange * seekRange) {
                                var24_19 = ClientEventHandler.seekingTimeVehicle;
                                if ((ClientEventHandler.seekingTimeVehicle = var24_19 + 1) == 1) {
                                    ClientEventHandler.lockingPosVehicle = ClientEventHandler.seekingPosVehicle;
                                }
                            } else lbl-1000:
                            // 2 sources

                            {
                                this.seekFailure(player);
                            }
                            break block34;
                        }
                        this.seekFailure(player);
                        break block34;
                    }
                    if (seekWeaponInfo.getOnlyLockEntity()) {
                        if (ClientEventHandler.seekingTimeVehicle > lockTime + 2 && !ClientEventHandler.lockOnVehicle) {
                            ClientEventHandler.lockingEntityVehicle = ClientEventHandler.seekingEntityVehicle;
                            ClientEventHandler.lockOnVehicle = true;
                        }
                        if (ModKeyMappings.VEHICLE_SEEK.m_90857_()) {
                            if (ClientEventHandler.seekingEntityVehicle == null) {
                                ClientEventHandler.seekingEntityVehicle = ClientEventHandler.nearestEntityVehicle;
                            }
                            if (ClientEventHandler.seekingEntityVehicle != null && ClientEventHandler.lockingPosVehicle == null) {
                                var23_18 = ClientEventHandler.seekingTimeVehicle;
                                ClientEventHandler.seekingTimeVehicle = var23_18 + 1;
                                v9 = ClientEventHandler.seekingEntityVehicle;
                                Intrinsics.checkNotNull((Object)v9);
                                if (!(v9.m_20197_().isEmpty() && !(ClientEventHandler.seekingEntityVehicle instanceof VehicleEntity) || player.f_19797_ % 3 != 0 || ClientEventHandler.lockOnVehicle)) {
                                    v10 = ClientEventHandler.seekingEntityVehicle;
                                    Intrinsics.checkNotNull((Object)v10);
                                    v11 = v10.m_20148_();
                                    Intrinsics.checkNotNullExpressionValue((Object)v11, (String)"getUUID(...)");
                                    MinecraftUtil.sendPacketToServer(new SeekingWeaponWarningMessage(false, v11));
                                }
                            }
                        } else {
                            this.seekFailure(player);
                        }
                    }
                }
                if (ClientEventHandler.seekingEntityVehicle == null) break block35;
                v12 = ClientEventHandler.seekingEntityVehicle;
                Intrinsics.checkNotNull((Object)v12);
                v13 = cameraPos.m_82505_(VectorTool.lerpGetEntityBoundingBoxCenter(v12, 1.0f));
                Intrinsics.checkNotNullExpressionValue((Object)v13, (String)"vectorTo(...)");
                if (VectorToolKt.angleTo(seekVec, v13) > seekAngle || !SeekTool.NOT_IN_SMOKE.test(ClientEventHandler.seekingEntityVehicle)) break block36;
                v14 = (Entity)player;
                v15 = ClientEventHandler.seekingEntityVehicle;
                Intrinsics.checkNotNull((Object)v15);
                if (this.noClip(v14, v15)) break block35;
            }
            this.seekFailure(player);
        }
        if (ClientEventHandler.lockingEntityVehicle != null) {
            v16 = ClientEventHandler.lockingEntityVehicle;
            Intrinsics.checkNotNull((Object)v16);
            if (!v16.m_6084_()) {
                this.seekFailure(player);
            }
        }
        if (ClientEventHandler.seekingTimeVehicle == 2) {
            this.playLockingSound(data, player);
        }
        if (ClientEventHandler.seekingTimeVehicle > lockTime) {
            this.playLockedSound(data, player);
            if (seekWeaponInfo.getOnlyLockEntity() && ClientEventHandler.lockingEntityVehicle != null) {
                v17 = ClientEventHandler.lockingEntityVehicle;
                Intrinsics.checkNotNull((Object)v17);
                if ((!v17.f_19823_.isEmpty() || ClientEventHandler.lockingEntityVehicle instanceof VehicleEntity) && player.f_19797_ % 2 == 0) {
                    v18 = ClientEventHandler.lockingEntityVehicle;
                    Intrinsics.checkNotNull((Object)v18);
                    v19 = v18.m_20148_();
                    Intrinsics.checkNotNullExpressionValue((Object)v19, (String)"getUUID(...)");
                    MinecraftUtil.sendPacketToServer(new SeekingWeaponWarningMessage(true, v19));
                }
            }
        }
    }

    public final void seekFailure(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        seekingTimeVehicle = 0;
        lockOnVehicle = false;
        lockingEntityVehicle = null;
        seekingEntityVehicle = null;
        lockingPosVehicle = null;
        seekingTime = 0;
        lockOn = false;
        lockingEntity = null;
        seekingEntity = null;
        lockingPos = null;
        VehicleMainWeaponHudOverlay.lock = false;
        ClientEventHandler.stopVehicleSeekSound(player);
    }

    public final void playLockingSound(@NotNull GunData data, @NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        SoundInfo soundInfo = data.get(GunProp.SOUND_INFO);
        SoundEvent sound = soundInfo.getLocking();
        player.m_5496_(sound, 2.0f, 1.0f);
    }

    public final void playLockedSound(@NotNull GunData data, @NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)data, (String)"data");
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        SoundInfo soundInfo = data.get(GunProp.SOUND_INFO);
        SoundEvent sound = soundInfo.getLocked();
        player.m_5496_(sound, 2.0f, 1.0f);
    }

    public final boolean noClip(@NotNull Entity entity, @NotNull Entity e) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        Intrinsics.checkNotNullParameter((Object)e, (String)"e");
        return entity.m_9236_().m_45547_(new ClipContext(entity.m_146892_(), e.m_146892_(), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).m_6662_() != HitResult.Type.BLOCK;
    }

    public final boolean noClip(@NotNull Entity entity, @NotNull Vec3 pos) {
        Intrinsics.checkNotNullParameter((Object)entity, (String)"entity");
        Intrinsics.checkNotNullParameter((Object)pos, (String)"pos");
        return entity.m_9236_().m_45547_(new ClipContext(entity.m_146892_(), pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity)).m_6662_() != HitResult.Type.BLOCK;
    }

    public final void weaponZooming(@NotNull ItemStack stack) {
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        if (stack.m_41720_() instanceof GunItem) {
            MinecraftUtil.sendPacketToServer(new WeaponZoomingMessage(zoomTime >= 0.7));
        }
    }

    public final void staminaSystem() {
        if (MinecraftUtil.getMc().m_91104_()) {
            return;
        }
        if (MinecraftUtil.getLocalPlayer() == null) {
            return;
        }
        if (breath) {
            stamina += 0.5f;
        } else if (stamina > 0.0f) {
            stamina = RangesKt.coerceAtLeast((float)(stamina - 0.5f), (float)0.0f);
        }
        if (stamina >= 100.0f) {
            exhaustion = true;
            breath = false;
        }
        if (exhaustion && stamina <= 0.0f) {
            exhaustion = false;
        }
        if (ModKeyMappings.BREATH.m_90857_() && zoom) {
            switchTime = RangesKt.coerceAtMost((double)(switchTime + 0.65), (double)5.0);
        } else if (switchTime > 0.0 && stamina == 0.0f) {
            switchTime = RangesKt.coerceAtLeast((double)(switchTime - 0.15), (double)0.0);
        }
    }

    public final void handlePlayerSprint() {
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (player.m_6144_() || player.m_20159_() || player.m_20069_() || zoom) {
            noSprintTicks = 3.0f;
        }
        if (noSprintTicks > 0.0f) {
            float f = noSprintTicks;
            noSprintTicks = f + -1.0f;
        }
        if (zoom || holdingFireKey) {
            player.m_6858_(false);
        }
    }

    private final void handleVariableDecrease() {
        int n;
        if (holdingFireKeyTicks > 0 && !holdingFireKey && (holdingFireKeyTicks = (n = holdingFireKeyTicks) + -1) == 0) {
            holdingFireKeyTicks0 = 0.0f;
        }
        if (dismountCountdown > 0) {
            n = dismountCountdown;
            dismountCountdown = n + -1;
        }
        if (aimVillagerCountdown > 0) {
            n = aimVillagerCountdown;
            aimVillagerCountdown = n + -1;
        }
        if (switchVehicleWeaponCooldown > 0) {
            n = switchVehicleWeaponCooldown;
            switchVehicleWeaponCooldown = n + -1;
        }
    }

    @JvmStatic
    public static final boolean isProne(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Level level = player.m_9236_();
        if (player.m_20089_() == Pose.SWIMMING && !player.m_6069_()) {
            return true;
        }
        Vec3 forward = new Vec3(player.m_20154_().f_82479_, 0.0, player.m_20154_().f_82481_).m_82541_();
        return player.m_6047_() && level.m_8055_(BlockPos.m_274561_((double)(player.m_20185_() + 0.7 * forward.f_82479_), (double)(player.m_20186_() + 0.5), (double)(player.m_20189_() + 0.7 * forward.f_82481_))).m_60815_() && !level.m_8055_(BlockPos.m_274561_((double)(player.m_20185_() + 0.7 * forward.f_82479_), (double)(player.m_20186_() + 1.5), (double)(player.m_20189_() + 0.7 * forward.f_82481_))).m_60815_();
    }

    public final void handleGunMelee(@NotNull Player player, @NotNull ItemStack stack) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        Item item = stack.m_41720_();
        if (item instanceof GunItem) {
            GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
            Entity vehicle = player.m_20202_();
            if (!(!((GunItem)item).hasMeleeAttack(data) || gunMelee != 0 || !(drawTime < 0.01) || !ModKeyMappings.MELEE.m_90857_() && (!data.meleeOnly() || !holdingFireKey) || vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player) || holdFireVehicle || MinecraftUtil.getNotInGame() || isEditing || GunData.Companion.from$default((GunData.Companion)GunData.Companion, (ItemStack)stack, null, (int)2, null).reload.normal() || GunData.Companion.from$default((GunData.Companion)GunData.Companion, (ItemStack)stack, null, (int)2, null).reload.empty() || data.reloading() || data.charging() || player.m_36335_().m_41519_(item))) {
                gunMelee = ((Number)data.get(GunProp.MELEE_DURATION)).intValue();
                fireCooldown = (double)gunMelee + 4.0;
            }
            if (gunMelee == ((Number)data.get(GunProp.MELEE_DURATION)).intValue() - ((Number)data.get(GunProp.MELEE_DAMAGE_TIME)).intValue()) {
                this.doGunMeleeAttack(player, ((Number)data.get(GunProp.MELEE_ANGLE)).intValue(), ((Number)data.get(GunProp.MELEE_RANGE)).doubleValue());
            }
        }
        if (gunMelee > 0) {
            int n = gunMelee;
            gunMelee = n + -1;
        }
    }

    /*
     * WARNING - void declaration
     */
    public final void doGunMeleeAttack(@NotNull Player player, double angle, double customRange) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        player.m_5496_(SoundEvents.f_12317_, 1.0f, 1.0f);
        Entity lookingEntity = TraceTool.findMeleeEntity((Entity)player, player.getEntityReach() + customRange);
        List<Entity> targetEntities = SeekTool.seekLivingEntities((Entity)player, player.getEntityReach() + customRange, angle / (double)2);
        List attackList = new ArrayList();
        if (lookingEntity != null) {
            ((Collection)attackList).add(lookingEntity);
        }
        if (!targetEntities.isEmpty()) {
            void $this$sortedBy$iv;
            void $this$filterTo$iv$iv;
            Iterable $this$filter$iv = targetEntities;
            boolean $i$f$filter = false;
            Iterable iterable = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                Entity it = (Entity)element$iv$iv;
                boolean bl = false;
                if (!(it.m_6084_() && !Intrinsics.areEqual((Object)it, (Object)lookingEntity))) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            $this$filter$iv = (List)destination$iv$iv;
            boolean $i$f$sortedBy = false;
            List list = CollectionsKt.sortedWith((Iterable)$this$sortedBy$iv, (Comparator)new Comparator(player){
                final /* synthetic */ Player $player$inlined;
                {
                    this.$player$inlined = player;
                }

                public final int compare(T a, T b) {
                    Entity it = (Entity)a;
                    boolean bl = false;
                    Vec3 vec3 = this.$player$inlined.m_20154_();
                    Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getLookAngle(...)");
                    Vec3 vec32 = this.$player$inlined.m_146892_().m_82505_(it.m_146892_());
                    Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"vectorTo(...)");
                    it = (Entity)b;
                    Comparable comparable = Double.valueOf(VectorToolKt.angleTo(vec3, vec32));
                    bl = false;
                    Vec3 vec33 = this.$player$inlined.m_20154_();
                    Intrinsics.checkNotNullExpressionValue((Object)vec33, (String)"getLookAngle(...)");
                    Vec3 vec34 = this.$player$inlined.m_146892_().m_82505_(it.m_146892_());
                    Intrinsics.checkNotNullExpressionValue((Object)vec34, (String)"vectorTo(...)");
                    return ComparisonsKt.compareValues((Comparable)comparable, (Comparable)Double.valueOf(VectorToolKt.angleTo(vec33, vec34)));
                }
            });
            CollectionsKt.addAll((Collection)attackList, (Iterable)list);
        }
        player.m_6674_(InteractionHand.MAIN_HAND);
        Iterable $this$map$iv = attackList;
        boolean $i$f$map = false;
        Iterable $i$f$sortedBy = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            Object element$iv$iv;
            element$iv$iv = (Entity)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(it.m_20148_());
        }
        List list = (List)destination$iv$iv;
        MinecraftUtil.sendPacketToServer(new MeleeAttackMessage(list));
    }

    public final void handleLungeAttack(@NotNull Player player, @NotNull ItemStack stack) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        if (stack.m_150930_((Item)ModItems.LUNGE_MINE.get()) && lungeAttack == 0 && lungeDraw == 0 && usingLunge) {
            lungeAttack = 18;
            usingLunge = false;
            player.m_5496_(SoundEvents.f_12317_, 1.0f, 1.0f);
        }
        if (stack.m_150930_((Item)ModItems.LUNGE_MINE.get()) && (lungeAttack >= 9 && (double)lungeAttack <= 10.5 || lungeSprint > 0)) {
            Entity lookingEntity = OverlayTraceHandler.playerReachEntity;
            BlockHitResult result = player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(player.m_20154_().m_82490_(player.getBlockReach() + 0.5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player));
            Vec3 looking = Vec3.m_82528_((Vec3i)((Vec3i)player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(player.m_20154_().m_82490_(player.getBlockReach() + 0.5)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player)).m_82425_()));
            BlockState blockState = player.m_9236_().m_8055_(BlockPos.m_274561_((double)looking.f_82479_, (double)looking.f_82480_, (double)looking.f_82481_));
            if (lookingEntity != null) {
                UUID uUID = lookingEntity.m_20148_();
                Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
                Vec3 vec3 = result.m_82450_();
                Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getLocation(...)");
                MinecraftUtil.sendPacketToServer(new LungeMineAttackMessage(0, uUID, vec3));
                lungeSprint = 0;
                lungeAttack = 0;
                lungeDraw = 15;
            } else if ((blockState.m_60815_() || blockState.m_60734_() instanceof DoorBlock || blockState.m_60734_() instanceof CrossCollisionBlock || blockState.m_60734_() instanceof BellBlock) && lungeSprint == 0) {
                UUID uUID = player.m_20148_();
                Intrinsics.checkNotNullExpressionValue((Object)uUID, (String)"getUUID(...)");
                Vec3 vec3 = result.m_82450_();
                Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getLocation(...)");
                MinecraftUtil.sendPacketToServer(new LungeMineAttackMessage(1, uUID, vec3));
                lungeSprint = 0;
                lungeAttack = 0;
                lungeDraw = 15;
            }
        }
        if (lungeSprint > 0) {
            int n = lungeSprint;
            lungeSprint = n + -1;
        }
        if (lungeAttack > 0) {
            int n = lungeAttack;
            lungeAttack = n + -1;
        }
        if (lungeDraw > 0) {
            int n = lungeDraw;
            lungeDraw = n + -1;
        }
    }

    @SubscribeEvent
    public final void handleWeaponFire(@NotNull TickEvent.RenderTickEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        if (MinecraftUtil.getMc().m_260875_() > 20) {
            this.handleVehicleGunShoot();
            this.handleGunShoot();
        }
    }

    public final void handleGunShoot() {
        ItemStack stack;
        Item item;
        if (MinecraftUtil.getClientLevel() == null) {
            return;
        }
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (MinecraftUtil.getNotInGame()) {
            holdingFireKey = false;
        }
        if (!((item = (stack = player.m_21205_()).m_41720_()) instanceof GunItem)) {
            clientTimer.stop();
            fireSpread = 0.0;
            gunSpread = 0.0;
            return;
        }
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        DefaultGunResource resource = GunResource.compute(stack);
        FireMode mode = GunData.selectedFireModeInfo$default((GunData)data, null, (int)1, null).mode;
        double partialHoldingFireKeyTicks = Mth.m_14139_((double)this.getDelta(), (double)holdingFireKeyTicks0, (double)holdingFireKeyTicks);
        holdingFireKeyTicks0 = holdingFireKeyTicks;
        if (partialHoldingFireKeyTicks > (double)holdingFireKeyTicks && partialHoldingFireKeyTicks > ((Number)data.get(GunProp.SHOOT_DELAY)).doubleValue() * 0.25 && shouldPlayDischargeSound) {
            SoundEvent dischargeSound = resource.dischargeSound;
            if (dischargeSound != null) {
                player.m_5496_(dischargeSound, (float)partialHoldingFireKeyTicks * 0.03f, 0.6f + (float)partialHoldingFireKeyTicks * 0.02f);
            }
            shouldPlayDischargeSound = false;
            burstFireAmount = 0;
        }
        if (!((GunItem)item).canShoot(data, (Entity)player)) {
            burstFireAmount = 0;
        }
        float times = RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        double basicDev = ((Number)data.get(GunProp.SPREAD)).doubleValue();
        double walk = this.isMoving() ? 0.3 * basicDev : 0.0;
        double sprint = player.m_20142_() ? 0.25 * basicDev : 0.0;
        double crouching = player.m_6047_() ? -0.15 * basicDev : 0.0;
        double prone = ClientEventHandler.isProne((Player)player) ? -0.3 * basicDev : 0.0;
        double jump = player.m_20096_() ? 0.0 : 0.35 * basicDev;
        double ride = player.m_20096_() ? -0.25 * basicDev : 0.0;
        double zoomSpread = 1.0 - (1.0 - ((Number)data.get(GunProp.ZOOM_SPREAD_RATE)).doubleValue()) * zoomTime;
        double spread = data.isShotgun() || stack.m_150930_((Item)ModItems.MINIGUN.get()) ? 1.2 * zoomSpread * (basicDev + 0.2 * (walk + sprint + crouching + prone + jump + ride) + fireSpread) : zoomSpread * (0.7 * basicDev + walk + sprint + crouching + prone + jump + ride + 0.8 * fireSpread);
        gunSpread = Mth.m_14139_((double)(0.14 * (double)times), (double)gunSpread, (double)spread);
        double weight = ((Number)data.get(GunProp.WEIGHT)).doubleValue();
        double speed = (double)5 / (weight + (double)4);
        fireCooldown = noSprintTicks == 0.0f && player.m_20142_() && !zoom && !holdingFireKey ? RangesKt.coerceIn((double)(fireCooldown + (double)((float)3 * times)), (double)0.0, (double)24.0) : RangesKt.coerceIn((double)(fireCooldown - (double)6 * speed * (double)times), (double)0.0, (double)40.0);
        int rpm = RangesKt.coerceIn((int)(((Number)data.get(GunProp.RPM)).intValue() + customRpm), (int)1, (int)114514);
        double rps = (double)rpm / 60.0;
        int cooldown = MathKt.roundToInt((double)((double)1000 / rps));
        if (clientTimer.getProgress() == 0L && stack.m_150930_((Item)ModItems.TRACHELIUM.get()) && holdingFireKey) {
            revolverPreTime = RangesKt.coerceIn((double)(revolverPreTime + 0.3 * (double)times), (double)0.0, (double)1.0);
            revolverWheelPreTime = RangesKt.coerceIn((double)(revolverWheelPreTime + 0.32 * (double)times), (double)0.0, (double)(revolverPreTime > 0.7 ? 1.0 : 0.55));
        } else {
            revolverPreTime = RangesKt.coerceIn((double)(revolverPreTime - 1.2 * (double)times), (double)0.0, (double)1.0);
        }
        Entity vehicle = player.m_20202_();
        if (!(!holdingFireKey && burstFireAmount <= 0 || holdingFireKeyTicks < ((Number)data.get(GunProp.SHOOT_DELAY)).intValue() || vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player) || holdFireVehicle || !((GunItem)item).canShoot(data, (Entity)player) || ((GunItem)item).useSpecialFireProcedure(data) || !(fireCooldown == 0.0) || !(sprintBasicRotX * sprintBasicRotY * sprintBasicRotZ < 1.0E-4) || !(drawTime < 0.01) || MinecraftUtil.getNotInGame() || isEditing)) {
            if (mode == FireMode.SEMI) {
                if (clientTimer.getProgress() == 0L) {
                    clientTimer.start();
                    this.shootClient((Player)player);
                }
            } else {
                if (!clientTimer.started()) {
                    clientTimer.start();
                    clientTimer.setProgress((long)cooldown + 1L);
                }
                if (clientTimer.getProgress() >= (long)cooldown) {
                    long newProgress = clientTimer.getProgress();
                    do {
                        this.shootClient((Player)player);
                    } while ((newProgress -= (long)cooldown) - (long)cooldown > 0L);
                    clientTimer.setProgress(newProgress);
                }
            }
            if (MinecraftUtil.getNotInGame()) {
                clientTimer.stop();
            }
        } else {
            if (mode != FireMode.SEMI && clientTimer.getProgress() >= (long)cooldown) {
                clientTimer.stop();
            }
            fireSpread = 0.0;
        }
        if (mode == FireMode.SEMI && clientTimer.getProgress() >= (long)cooldown) {
            clientTimer.stop();
        }
        if (GunData.Companion.from$default((GunData.Companion)GunData.Companion, (ItemStack)stack, null, (int)2, null).reload.normal() || GunData.Companion.from$default((GunData.Companion)GunData.Companion, (ItemStack)stack, null, (int)2, null).reload.empty()) {
            customRpm = 0;
        }
        data.save();
    }

    public final void shootClient(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        ItemStack stack = player.m_21205_();
        Item item = stack.m_41720_();
        GunItem gunItem = item instanceof GunItem ? (GunItem)item : null;
        if (gunItem == null) {
            return;
        }
        GunItem item2 = gunItem;
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        if (!item2.canShoot(data, (Entity)player) || item2.useSpecialFireProcedure(data)) {
            return;
        }
        FireMode mode = GunData.selectedFireModeInfo$default((GunData)data, null, (int)1, null).mode;
        if (mode != FireMode.AUTO) {
            holdingFireKey = false;
        }
        if (data.get(GunProp.CLEAR_HOLD_PROGRESS_AFTER_SHOOT).booleanValue()) {
            holdingFireKeyTicks = 0;
        }
        if (mode == FireMode.BURST && burstFireAmount == 1) {
            fireCooldown = ((Number)data.get(GunProp.BURST_COOLDOWN)).intValue();
        }
        if (burstFireAmount > 0) {
            int n = burstFireAmount;
            burstFireAmount = n + -1;
        }
        for (Perk.Type type : Perk.Type.getEntries()) {
            Comparable comparable;
            List<PerkInstance> instance = data.perk.getInstances(type);
            Iterator iterator = ((Iterable)instance).iterator();
            if (!iterator.hasNext()) {
                comparable = null;
            } else {
                PerkInstance it = (PerkInstance)iterator.next();
                boolean bl = false;
                Comparable comparable2 = Integer.valueOf(it.perk().getModifiedCustomRPM(customRpm, data, it));
                while (iterator.hasNext()) {
                    PerkInstance it2 = (PerkInstance)iterator.next();
                    $i$a$-maxOfOrNull-ClientEventHandler$shootClient$1 = false;
                    Comparable comparable3 = Integer.valueOf(it2.perk().getModifiedCustomRPM(customRpm, data, it2));
                    if (comparable2.compareTo(comparable3) >= 0) continue;
                    comparable2 = comparable3;
                }
                comparable = comparable2;
            }
            Integer n = (Integer)comparable;
            customRpm = n != null ? n : customRpm;
        }
        if (stack.m_150930_((Item)ModItems.DEVOTION.get())) {
            customRpm = RangesKt.coerceAtMost((int)(customRpm + 15), (int)500);
        }
        if (((Number)data.get(GunProp.BOLT_ACTION_TIME)).intValue() > 0 && data.hasEnoughAmmoToShoot((Entity)player)) {
            data.bolt.needed.set(true);
        }
        revolverPreTime = 0.0;
        revolverWheelPreTime = 0.0;
        this.playGunClientSounds(player);
        this.handleClientShoot();
    }

    public final void handleClientShoot() {
        UUID uUID;
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        ItemStack stack = player.m_21205_();
        if (!(stack.m_41720_() instanceof GunItem)) {
            return;
        }
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        if (lockedEntity != null) {
            Entity entity = lockedEntity;
            Intrinsics.checkNotNull((Object)entity);
            uUID = entity.m_20148_();
        } else {
            uUID = null;
        }
        MinecraftUtil.sendPacketToServer(new ShootMessage(gunSpread, zoom, uUID, null));
        fireRecoilTime = 10.0;
        MuzzleFlashHelper.FlashParams flashParams = MuzzleFlashHelper.calculateFromStack(stack);
        if (flashParams != null) {
            Vec3 vec3 = player.m_146892_();
            Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"getEyePosition(...)");
            Vec3 vec32 = player.m_20154_();
            Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"getLookAngle(...)");
            MuzzleFlashHelper.spawnFlashCone(vec3, vec32, flashParams);
        }
        if (!(((Number)data.get(GunProp.RECOIL)).doubleValue() == 0.0)) {
            player.m_20256_(player.m_20184_().m_82549_(player.m_20252_(1.0f).m_82490_(-((Number)data.get(GunProp.RECOIL)).doubleValue())));
        }
        double gunRecoilY = ((Number)data.get(GunProp.RECOIL_Y)).doubleValue() * (double)10;
        recoilY = (double)((float)((double)2 * Math.random() - 1.0)) * gunRecoilY;
        if (shellIndex < 5) {
            int n = shellIndex;
            shellIndex = n + 1;
        }
        noSprintTicks = 7.0f;
        ClientEventHandler.shellIndexTime[ClientEventHandler.shellIndex] = 0.001;
        ClientEventHandler.randomShell[0] = 1.0 + 0.2 * (Math.random() - 0.5);
        ClientEventHandler.randomShell[1] = 0.2 + (Math.random() - 0.5);
        ClientEventHandler.randomShell[2] = 0.7 + (Math.random() - 0.5);
    }

    public final void playGunClientSounds(@NotNull Player player) {
        SoundEvent fire1p;
        float pitch;
        Boolean flag;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        ItemStack stack = player.m_21205_();
        Item item = stack.m_41720_();
        if (!(item instanceof GunItem)) {
            return;
        }
        if (Intrinsics.areEqual((Object)item, (Object)ModItems.SENTINEL.get()) && (flag = stack.getCapability(ForgeCapabilities.ENERGY).map(arg_0 -> ClientEventHandler.playGunClientSounds$lambda$8(ClientEventHandler::playGunClientSounds$lambda$7, arg_0)).orElseGet(ClientEventHandler::playGunClientSounds$lambda$9)).booleanValue()) {
            player.m_5496_((SoundEvent)ModSounds.SENTINEL_CHARGE_FIRE_1P.get(), 2.0f, (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + 1.0));
            return;
        }
        if (Intrinsics.areEqual((Object)item, (Object)ModItems.SECONDARY_CATACLYSM.get())) {
            boolean isChargedFire;
            Boolean hasEnoughEnergy = stack.getCapability(ForgeCapabilities.ENERGY).map(arg_0 -> ClientEventHandler.playGunClientSounds$lambda$11(ClientEventHandler::playGunClientSounds$lambda$10, arg_0)).orElseGet(ClientEventHandler::playGunClientSounds$lambda$12);
            boolean bl = isChargedFire = zoom && hasEnoughEnergy != false;
            if (isChargedFire) {
                player.m_5496_((SoundEvent)ModSounds.SECONDARY_CATACLYSM_FIRE_1P_CHARGE.get(), 2.0f, (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                return;
            }
        }
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        Perk perk = data.perk.get(Perk.Type.AMMO);
        SoundInfo soundInfo = data.get(GunProp.SOUND_INFO);
        float f = pitch = data.heat.get() <= 75.0 ? 1.0f : (float)(1.0 - 0.02 * Math.abs((double)75 - data.heat.get()));
        if (Intrinsics.areEqual((Object)perk, (Object)ModPerks.INSTANCE.getBEAST_BULLET().get())) {
            player.m_5496_((SoundEvent)ModSounds.HENG.get(), 1.0f, (float)(((double)2 * Math.random() - 1.0) * (double)0.1f + (double)pitch));
        }
        boolean isSilent = data.attachment.get(AttachmentType.BARREL) == 2;
        SoundEvent soundEvent = fire1p = isSilent ? soundInfo.getFire1PSilent() : soundInfo.getFire1P();
        if (fire1p != null) {
            player.m_5496_(fire1p, 4.0f, (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)pitch));
        }
        double shooterHeight = player.m_146892_().m_82554_(Vec3.m_82528_((Vec3i)((Vec3i)player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(new Vec3(0.0, -1.0, 0.0).m_82490_(10.0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player)).m_82425_())));
        int delay$iv = (int)(1.0 + 1.5 * shooterHeight);
        boolean $i$f$queueClientWorkIfDelayed = false;
        if (delay$iv > 0) {
            Mod.Companion.queueClientWork(delay$iv, new Runnable(stack, data, player, shooterHeight){
                final /* synthetic */ ItemStack $stack$inlined;
                final /* synthetic */ GunData $data$inlined;
                final /* synthetic */ Player $player$inlined;
                final /* synthetic */ double $shooterHeight$inlined;
                {
                    this.$stack$inlined = itemStack;
                    this.$data$inlined = gunData;
                    this.$player$inlined = player;
                    this.$shooterHeight$inlined = d;
                }

                public final void run() {
                    boolean bl = false;
                    if (GunResource.compute((ItemStack)this.$stack$inlined).ejectShell) {
                        if (GunData.selectedAmmoConsumer$default(this.$data$inlined, null, 1, null).getType() == AmmoConsumer.AmmoConsumeType.PLAYER_AMMO) {
                            Ammo ammo = GunData.selectedAmmoConsumer$default(this.$data$inlined, null, 1, null).getPlayerAmmoType();
                            Intrinsics.checkNotNull((Object)((Object)ammo));
                            Ammo ammoType = ammo;
                            switch (WhenMappings.$EnumSwitchMapping$0[ammoType.ordinal()]) {
                                case 1: {
                                    this.$player$inlined.m_5496_((SoundEvent)ModSounds.SHELL_CASING_SHOTGUN.get(), (float)RangesKt.coerceAtLeast((double)(0.75 - 0.12 * this.$shooterHeight$inlined), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                                    break;
                                }
                                case 2: 
                                case 3: {
                                    this.$player$inlined.m_5496_((SoundEvent)ModSounds.SHELL_CASING_50CAL.get(), (float)RangesKt.coerceAtLeast((double)(1.0 - 0.15 * this.$shooterHeight$inlined), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                                    break;
                                }
                                default: {
                                    this.$player$inlined.m_5496_((SoundEvent)ModSounds.SHELL_CASING_NORMAL.get(), (float)RangesKt.coerceAtLeast((double)(1.5 - 0.2 * this.$shooterHeight$inlined), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                                    break;
                                }
                            }
                        } else {
                            this.$player$inlined.m_5496_((SoundEvent)ModSounds.SHELL_CASING_NORMAL.get(), (float)RangesKt.coerceAtLeast((double)(1.5 - 0.2 * this.$shooterHeight$inlined), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                        }
                    }
                }
            });
        } else {
            boolean bl = false;
            if (GunResource.compute((ItemStack)stack).ejectShell) {
                if (GunData.selectedAmmoConsumer$default(data, null, 1, null).getType() == AmmoConsumer.AmmoConsumeType.PLAYER_AMMO) {
                    Ammo ammo = GunData.selectedAmmoConsumer$default(data, null, 1, null).getPlayerAmmoType();
                    Intrinsics.checkNotNull((Object)((Object)ammo));
                    Ammo ammoType = ammo;
                    switch (WhenMappings.$EnumSwitchMapping$0[ammoType.ordinal()]) {
                        case 1: {
                            player.m_5496_((SoundEvent)ModSounds.SHELL_CASING_SHOTGUN.get(), (float)RangesKt.coerceAtLeast((double)(0.75 - 0.12 * shooterHeight), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                            break;
                        }
                        case 2: 
                        case 3: {
                            player.m_5496_((SoundEvent)ModSounds.SHELL_CASING_50CAL.get(), (float)RangesKt.coerceAtLeast((double)(1.0 - 0.15 * shooterHeight), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                            break;
                        }
                        default: {
                            player.m_5496_((SoundEvent)ModSounds.SHELL_CASING_NORMAL.get(), (float)RangesKt.coerceAtLeast((double)(1.5 - 0.2 * shooterHeight), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                            break;
                        }
                    }
                } else {
                    player.m_5496_((SoundEvent)ModSounds.SHELL_CASING_NORMAL.get(), (float)RangesKt.coerceAtLeast((double)(1.5 - 0.2 * shooterHeight), (double)0.0), (float)(((double)2 * Math.random() - 1.0) * (double)0.05f + (double)1.0f));
                }
            }
        }
    }

    public final void handleVehicleGunShoot() {
        Entity vehicle;
        if (MinecraftUtil.getClientLevel() == null) {
            return;
        }
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (MinecraftUtil.getNotInGame()) {
            clientTimerVehicle.stop();
            holdFireVehicle = false;
        }
        if ((vehicle = player.m_20202_()) instanceof VehicleEntity && ((VehicleEntity)vehicle).hasWeapon(((VehicleEntity)vehicle).getSeatIndex((Entity)player))) {
            GunData gunData = ((VehicleEntity)vehicle).getGunData(((VehicleEntity)vehicle).getSeatIndex((Entity)player));
            if (gunData == null) {
                return;
            }
            GunData gunData2 = gunData;
            if (!((VehicleEntity)vehicle).canShoot((LivingEntity)player)) {
                holdFireVehicle = false;
                return;
            }
            int rpm = ((VehicleEntity)vehicle).vehicleWeaponRpm((LivingEntity)player);
            if (rpm == 0) {
                rpm = 240;
            }
            double rps = (double)rpm / 60.0;
            int cooldown = MathKt.roundToInt((double)((double)1000 / rps));
            if (holdFireVehicle) {
                if (Intrinsics.areEqual((Object)gunData2.get(GunProp.DEFAULT_FIRE_MODE), (Object)"Semi")) {
                    if (clientTimerVehicle.getProgress() == 0L) {
                        clientTimerVehicle.start();
                        this.clientShootVehicle((Player)player, (VehicleEntity)vehicle, gunData2);
                    }
                } else {
                    if (!clientTimerVehicle.started()) {
                        clientTimerVehicle.start();
                        clientTimerVehicle.setProgress((long)cooldown + 1L);
                    }
                    if (clientTimerVehicle.getProgress() >= (long)cooldown) {
                        long newProgress = clientTimerVehicle.getProgress();
                        do {
                            this.clientShootVehicle((Player)player, (VehicleEntity)vehicle, gunData2);
                        } while ((newProgress -= (long)cooldown) - (long)cooldown > 0L);
                        clientTimerVehicle.setProgress(newProgress);
                    }
                }
                if (MinecraftUtil.getNotInGame()) {
                    clientTimerVehicle.stop();
                }
            } else if (clientTimerVehicle.getProgress() >= (long)cooldown) {
                clientTimerVehicle.stop();
            }
        } else {
            clientTimerVehicle.stop();
        }
    }

    public final void clientShootVehicle(@NotNull Player player, @NotNull VehicleEntity vehicle, @NotNull GunData gunData) {
        Object object;
        UUID uUID;
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        Intrinsics.checkNotNullParameter((Object)gunData, (String)"gunData");
        if (lockingEntityVehicle != null) {
            Entity entity = lockingEntityVehicle;
            Intrinsics.checkNotNull((Object)entity);
            uUID = entity.m_20148_();
        } else {
            uUID = null;
        }
        if (lockingPosVehicle != null) {
            Vec3 vec3 = lockingPosVehicle;
            Intrinsics.checkNotNull((Object)vec3);
            object = vec3.m_252839_();
        } else {
            BlockPos blockPos;
            SeekWeaponInfo seekWeaponInfo = gunData.get(GunProp.SEEK_WEAPON_INFO);
            object = (seekWeaponInfo != null ? seekWeaponInfo.getInputBlockPos() : false) ? ((blockPos = missileLockingPos) != null && (blockPos = blockPos.m_252807_()) != null ? blockPos.m_252839_() : null) : null;
        }
        MinecraftUtil.sendPacketToServer(new VehicleFireMessage(uUID, (Vector3f)object, null, null, 12, null));
        if (MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.FIRST_PERSON || zoomVehicle) {
            this.playVehicleClientSounds(player, vehicle);
        }
    }

    public final void playVehicleClientSounds(@NotNull Player player, @NotNull VehicleEntity vehicle) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)vehicle, (String)"vehicle");
        GunData gunData = vehicle.getGunData(vehicle.getSeatIndex((Entity)player));
        if (gunData == null) {
            return;
        }
        GunData gunData2 = gunData;
        SoundInfo soundInfo = gunData2.get(GunProp.SOUND_INFO);
        SoundEvent soundEvent = soundInfo.getFire1P();
        if (soundEvent == null) {
            return;
        }
        SoundEvent sound = soundEvent;
        float pitch = vehicle.getWeaponHeat((LivingEntity)player) <= 60 ? 1.0f : (float)(1.0 - 0.011 * (double)Math.abs(60 - vehicle.getWeaponHeat((LivingEntity)player)));
        player.m_5496_(sound, 1.0f, pitch);
    }

    @SubscribeEvent
    public final void handleWeaponBreathSway(@NotNull TickEvent.RenderTickEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        ItemStack stack = player.m_21205_();
        Item item = stack.m_41720_();
        GunItem gunItem = item instanceof GunItem ? (GunItem)item : null;
        if (gunItem == null) {
            return;
        }
        GunItem item2 = gunItem;
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity && Intrinsics.areEqual((Object)player, (Object)((VehicleEntity)vehicle).m_146895_()) && ((VehicleEntity)vehicle).hidePassenger((Entity)player)) {
            return;
        }
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        float times = (float)2 * RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        float pose = player.m_6047_() && player.m_20206_() >= 1.0f && !ClientEventHandler.isProne((Player)player) ? 0.85f : (ClientEventHandler.isProne((Player)player) ? (data.attachment.get(AttachmentType.GRIP) == 3 || item2.hasBipod(data) ? 0.0f : 0.25f) : 1.0f);
        int stockType = data.attachment.get(AttachmentType.STOCK);
        double sway = switch (stockType) {
            case 1 -> 1.0;
            case 2 -> 0.55;
            default -> 0.8;
        };
        float customWeight = RangesKt.coerceIn((float)((float)((Number)data.get(GunProp.WEIGHT)).doubleValue()), (float)1.0f, (float)30.0f);
        if (!breath && zoom) {
            float newPitch = (float)((double)player.m_146909_() - (double)0.01f * Math.sin(0.03 * (double)player.f_19797_) * (double)pose * Mth.m_216263_((RandomSource)RandomSource.m_216327_(), (double)0.1, (double)1.0) * (double)times * sway * (1.0 - 0.03 * (double)customWeight));
            player.m_146926_(newPitch);
            player.f_19860_ = player.m_146909_();
            float newYaw = (float)((double)player.m_146908_() - (double)0.005f * Math.cos(0.025 * ((double)player.f_19797_ + Math.PI * 2)) * (double)pose * Mth.m_216263_((RandomSource)RandomSource.m_216327_(), (double)0.05, (double)1.25) * (double)times * sway * (1.0 - 0.03 * (double)customWeight));
            player.m_146922_(newYaw);
            player.f_19859_ = player.m_146908_();
        }
    }

    private final float getDelta() {
        return MinecraftUtil.getMc().m_91297_();
    }

    @SubscribeEvent
    public final void computeCameraAngles(@NotNull ViewportEvent.ComputeCameraAngles event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (MinecraftUtil.getClientLevel() == null) {
            return;
        }
        Entity entity = event.getCamera().m_90592_();
        LivingEntity livingEntity = entity instanceof LivingEntity ? (LivingEntity)entity : null;
        if (livingEntity == null) {
            return;
        }
        LivingEntity entity2 = livingEntity;
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        ItemStack stack = entity2.m_21205_();
        if (stack.m_150930_((Item)ModItems.MONITOR.get()) && stack.m_41784_().m_128471_("Using") && stack.m_41784_().m_128471_("Linked")) {
            this.handleDroneCamera(event, entity2);
        }
        float yaw = event.getYaw();
        float pitch = event.getPitch();
        float roll = event.getRoll();
        shakeTime = Mth.m_14139_((double)(0.05 * (double)this.getDelta()), (double)shakeTime, (double)0.0);
        Entity vehicle = player.m_20202_();
        if (shakeTime > 0.0) {
            boolean onVehicle;
            float shakeRadiusAmplitude = RangesKt.coerceIn((float)((float)(1.0 - player.m_20182_().m_82554_(new Vec3(shakePos[0], shakePos[1], shakePos[2])) / shakeRadius)), (float)0.0f, (float)1.0f);
            boolean bl = onVehicle = vehicle != null;
            if (shakeType > 0.0) {
                event.setYaw((float)((double)yaw + shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * shakeType * (onVehicle ? 0.1 : 1.0)));
                event.setPitch((float)((double)pitch - shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * shakeType * (onVehicle ? 0.1 : 1.0)));
                cameraRoll = (float)((double)roll - shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * (onVehicle ? 0.1 : 1.0));
            } else {
                event.setYaw((float)((double)yaw - shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * shakeType * (onVehicle ? 0.1 : 1.0)));
                event.setPitch((float)((double)pitch + shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * shakeType * (onVehicle ? 0.1 : 1.0)));
                cameraRoll = (float)((double)roll + shakeTime * Math.sin(1.5707963267948966 * shakeTime) * shakeAmplitude * (double)shakeRadiusAmplitude * (onVehicle ? 0.1 : 1.0));
            }
        }
        cameraPitch = event.getPitch();
        cameraYaw = event.getYaw();
        cameraRoll *= 0.99f;
        if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player)) {
            return;
        }
        if (stack.m_41720_() instanceof GunItem) {
            this.handleWeaponSway(entity2);
            this.handleWeaponMove(entity2);
            this.handleWeaponZoom(entity2);
            this.handleWeaponFire(event, entity2);
            this.handleWeaponShell();
            this.handleGunRecoil();
            Intrinsics.checkNotNull((Object)stack);
            this.handleBowPullAnimation(entity2, stack);
            this.handleWeaponDraw(entity2);
            this.handlePlayerCamera(event);
        }
        this.handleShockCamera(event, entity2);
    }

    private final void handleDroneCamera(ViewportEvent.ComputeCameraAngles event, LivingEntity entity) {
        ItemStack stack = entity.m_21205_();
        Level level = entity.m_9236_();
        Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
        String string = stack.m_41784_().m_128461_("LinkedDrone");
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
        DroneEntity droneEntity = EntityFindUtil.findDrone(level, string);
        if (droneEntity == null) {
            return;
        }
        DroneEntity drone = droneEntity;
        cameraRoll = drone.getRoll((float)event.getPartialTick() * (1.0f - drone.getPitch((float)event.getPartialTick()) / (float)90));
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public final void onRenderHand(@NotNull RenderHandEvent event) {
        Entity vehicle;
        ItemStack stack;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        InteractionHand leftHand = MinecraftUtil.getMc().f_91066_.m_232107_().m_231551_() == HumanoidArm.RIGHT ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        InteractionHand rightHand = MinecraftUtil.getMc().f_91066_.m_232107_().m_231551_() == HumanoidArm.RIGHT ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack rightHandItem = player.m_21120_(rightHand);
        if (event.getHand() == leftHand) {
            if (rightHandItem.m_41720_() instanceof GunItem) {
                event.setCanceled(true);
            }
            if (rightHandItem.m_150930_((Item)ModItems.LUNGE_MINE.get())) {
                event.setCanceled(true);
            }
            if (player.m_6117_() && player.m_21211_().m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get())) {
                event.setCanceled(true);
            }
        }
        if (event.getHand() == rightHand) {
            if (rightHandItem.m_41720_() instanceof GunItem && drawTime > 0.15) {
                event.setCanceled(true);
            }
            if (player.m_6117_() && player.m_21211_().m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get())) {
                event.setCanceled(true);
            }
        }
        if ((stack = player.m_21205_()).m_150930_((Item)ModItems.MONITOR.get()) && stack.m_41784_().m_128471_("Using") && stack.m_41784_().m_128471_("Linked")) {
            Level level = player.m_9236_();
            Intrinsics.checkNotNullExpressionValue((Object)level, (String)"level(...)");
            String string = stack.m_41784_().m_128461_("LinkedDrone");
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getString(...)");
            if (EntityFindUtil.findDrone(level, string) != null) {
                event.setCanceled(true);
            }
        }
        if ((vehicle = player.m_20202_()) instanceof VehicleEntity && (((VehicleEntity)vehicle).banHand((LivingEntity)player) || !zoom && MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.FIRST_PERSON && ModKeyMappings.FREE_CAMERA.m_90857_())) {
            event.setCanceled(true);
        }
    }

    private final void handleWeaponSway(LivingEntity entity) {
        ItemStack stack = entity.m_21205_();
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player == null) {
            return;
        }
        Player player2 = player;
        Item item = stack.m_41720_();
        GunItem gunItem = item instanceof GunItem ? (GunItem)item : null;
        if (gunItem == null) {
            return;
        }
        GunItem item2 = gunItem;
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        float times = (float)2 * RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        double pose = player2.m_6144_() && player2.m_20206_() >= 1.0f && ClientEventHandler.isProne(player2) ? 0.85 : (ClientEventHandler.isProne(player2) ? (data.attachment.get(AttachmentType.GRIP) == 3 || item2.hasBipod(data) ? 0.0 : 0.25) : 1.0);
        swayX = pose * -0.008 * Math.sin(swayTime += 0.05 * (double)times) * (1.0 - 0.95 * zoomTime);
        swayY = pose * 0.125 * Math.sin(swayTime - 1.585) * (1.0 - 0.95 * zoomTime) - (double)3 * moveRotZ;
    }

    private final void handleWeaponMove(LivingEntity entity) {
        ItemStack stack = entity.m_21205_();
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player == null) {
            return;
        }
        Player player2 = player;
        Item item = stack.m_41720_();
        GunItem gunItem = item instanceof GunItem ? (GunItem)item : null;
        if (gunItem == null) {
            return;
        }
        GunItem item2 = gunItem;
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        float times = 3.7f * RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        double moveSpeed = ((Player)entity).m_20184_().m_165924_();
        double animSpeed = entity.m_20096_() ? (((Player)entity).m_20142_() ? 1.8 : 2.0) : 0.005;
        double customWeight = RangesKt.coerceIn((double)((Number)data.get(GunProp.WEIGHT)).doubleValue(), (double)1.0, (double)50.0);
        if (!isEditing) {
            double d = !((Player)entity).m_20142_() && MinecraftUtil.getMc().f_91066_.f_92085_.m_90857_() && firePosTimer == 0.0 && !(item2 instanceof SuperStarShooterItem) ? Mth.m_14139_((double)(0.2 * (double)times), (double)moveRotZ, (double)0.14) * (1.0 - zoomTime) : (moveRotZ = Mth.m_14139_((double)(0.2 * (double)times), (double)moveRotZ, (double)0.0) * (1.0 - zoomTime));
            if (((Player)entity).m_20142_() && !data.reloading() && firePosTimer == 0.0 && !ModKeyMappings.FIRE.m_90857_() && noSprintTicks == 0.0f && zoomTime < 0.5) {
                sprintBasicRotX = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.3f * times) / (customWeight + (double)4)), (double)sprintBasicRotX, (double)1.0), (double)0.0, (double)1.0);
                sprintBasicRotY = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.18f * times) / (customWeight + (double)4)), (double)sprintBasicRotY, (double)1.0), (double)0.0, (double)1.0);
                sprintBasicRotZ = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.3f * times) / (customWeight + (double)4)), (double)sprintBasicRotZ, (double)1.0), (double)0.0, (double)1.0);
                sprintBasicPosX = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.8f * times) / (customWeight + (double)4)), (double)sprintBasicPosX, (double)1.0), (double)0.0, (double)1.0);
                sprintBasicPosY = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.25f * times) / (customWeight + (double)4)), (double)sprintBasicPosY, (double)1.0), (double)0.0, (double)1.0);
                sprintBasicPosZ = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.8f * times) / (customWeight + (double)4)), (double)sprintBasicPosZ, (double)1.0), (double)0.0, (double)1.0);
            } else {
                sprintBasicRotX = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(1.4f * times) / customWeight), (double)sprintBasicRotX, (double)0.0), (double)0.0, (double)1.0);
                sprintBasicRotY = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.96f * times) / customWeight), (double)sprintBasicRotY, (double)0.0), (double)0.0, (double)1.0);
                sprintBasicRotZ = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(1.4f * times) / customWeight), (double)sprintBasicRotZ, (double)0.0), (double)0.0, (double)1.0);
                sprintBasicPosX = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.8f * times) / customWeight), (double)sprintBasicPosX, (double)0.0), (double)0.0, (double)1.0);
                sprintBasicPosY = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.8f * times) / customWeight), (double)sprintBasicPosY, (double)0.0), (double)0.0, (double)1.0);
                sprintBasicPosZ = RangesKt.coerceIn((double)Mth.m_14139_((double)((double)(0.8f * times) / customWeight), (double)sprintBasicPosZ, (double)0.0), (double)0.0, (double)1.0);
            }
        }
        if (this.isMoving()) {
            moveTime += 0.15 * animSpeed * (double)times * moveSpeed * (!(firePosTimer == 0.0) ? 0.4 : 1.0);
            sprintTime += 0.15 * animSpeed * (double)times * moveSpeed * (player2.m_20142_() ? sprintBasicPosX : 1.0) * (!(firePosTimer == 0.0) ? 0.4 : 1.0);
            moveFadeTime = Mth.m_14139_((double)(0.13 * (double)times), (double)moveFadeTime, (double)1.0);
        } else {
            moveFadeTime = Mth.m_14139_((double)(0.1 * (double)times), (double)moveFadeTime, (double)0.0);
        }
        if (((Player)entity).m_20142_() && !data.reloading() && firePosTimer == 0.0 && !ModKeyMappings.FIRE.m_90857_() && noSprintTicks == 0.0f) {
            sprintFadeTime = entity.m_20096_() ? Mth.m_14139_((double)(0.08 * (double)times), (double)sprintFadeTime, (double)1.0) : Mth.m_14139_((double)(0.15 * (double)times), (double)sprintFadeTime, (double)0.0);
            sprintPosX = (double)2 * Math.sin(Math.PI * sprintTime) * sprintFadeTime;
            sprintPosY = 1.0 * Math.sin(Math.PI * 2 * sprintTime) * sprintFadeTime;
        } else {
            sprintPosX = Mth.m_14139_((double)(0.1 * (double)times), (double)sprintPosX, (double)0.0);
            sprintPosY = Mth.m_14139_((double)(0.1 * (double)times), (double)sprintPosY, (double)0.0);
            sprintFadeTime = Mth.m_14139_((double)(0.1 * (double)times), (double)sprintFadeTime, (double)0.0);
        }
        movePosX = 0.2 * Math.sin(Math.PI * moveTime) * (1.0 - 0.4 * zoomTime) * moveFadeTime;
        movePosY = -0.135 * Math.sin(Math.PI * 2 * (moveTime - 0.25)) * (1.0 - 0.4 * zoomTime) * moveFadeTime;
        boolean left = MinecraftUtil.getMc().f_91066_.f_92086_.m_90857_();
        boolean right = MinecraftUtil.getMc().f_91066_.f_92088_.m_90857_();
        double pos = 0.0;
        if (left) {
            pos = -0.04;
        }
        if (right) {
            pos = 0.04;
        }
        if (left && right) {
            pos = 0.0;
        }
        movePosHorizon = Mth.m_14139_((double)(0.1 * (double)times), (double)movePosHorizon, (double)(pos * (1.0 - 1.0 * zoomTime)));
        double velocity = ((Player)entity).m_20184_().f_82480_ + 0.078;
        velocityY = RangesKt.coerceIn((double)(Mth.m_14139_((double)(0.23 * (double)times), (double)velocityY, (double)velocity) * (1.0 - 0.5 * zoomTime)), (double)-0.8, (double)0.8);
    }

    @JvmStatic
    public static final void gunRootMove(@NotNull AnimationProcessor<?> animationProcessor, float customX, float customY, float customZ, boolean useCustomAnim) {
        Intrinsics.checkNotNullParameter(animationProcessor, (String)"animationProcessor");
        CoreGeoBone root = animationProcessor.getBone("root");
        float walkPosX = (float)movePosX;
        float walkPosY = (float)(swayY + movePosY);
        float walkPosZ = 0.0f;
        float walkRotX = (float)swayX;
        float walkRotY = (float)((double)0.2f * movePosX);
        float walkRotZ = (float)((double)0.2f * movePosX);
        boolean i = !useCustomAnim;
        float basicSprintPosX = (float)(sprintBasicPosX * (1.5 + (double)customX)) * (float)i;
        double d = -2.35 + (double)customY;
        double d2 = 8;
        Double d3 = AnimationCurves.PARABOLA.apply(sprintBasicPosY);
        Intrinsics.checkNotNullExpressionValue((Object)d3, (String)"apply(...)");
        float basicSprintPosY = (float)(sprintBasicPosY * (d - d2 * ((Number)d3).doubleValue())) * (float)i;
        float basicSprintPosZ = (float)(sprintBasicPosZ * (-0.55 + (double)customZ)) * (float)i;
        float basicSprintRotX = (float)(sprintBasicRotX * (double)39 * (double)((float)Math.PI / 180)) * (float)i;
        float basicSprintRotY = (float)(sprintBasicRotY * 35.6 * (double)((float)Math.PI / 180)) * (float)i;
        float basicSprintRotZ = (float)(sprintBasicRotZ * 34.7 * (double)((float)Math.PI / 180)) * (float)i;
        float gunPosX = (float)((double)(walkPosX + basicSprintPosX) + sprintPosX * (double)i + (double)20 * drawTime + (double)9.3f * movePosHorizon) * (float)(1.0 - 0.5 * zoomTime);
        float gunPosY = (float)((double)(walkPosY + basicSprintPosY) + sprintPosY * (double)i - (double)40 * drawTime - (double)2.0f * velocityY) * (float)(1.0 - 0.5 * zoomTime);
        float gunPosZ = (walkPosZ + basicSprintPosZ) * (float)(1.0 - 1.0 * zoomTime);
        float gunRotX = (float)(((double)(walkRotX + basicSprintRotX) - (double)1.0471976f * drawTime - (double)0.15f * velocityY) * (1.0 - 0.5 * zoomTime) + (double)((float)Math.PI / 180) * turnRot[0]);
        float gunRotY = (float)(((double)(walkRotY + basicSprintRotY) + (double)0.2f * sprintBasicPosX * (double)i + (double)5.2359877f * drawTime) * (1.0 - 0.75 * zoomTime) + (double)((float)Math.PI / 180) * turnRot[1]);
        float gunRotZ = (float)(((double)(walkRotZ + basicSprintRotZ) + moveRotZ + (double)1.5707964f * drawTime + (double)2.7f * movePosHorizon) * (1.0 - 0.5 * zoomTime) + (double)((float)Math.PI / 180) * turnRot[2]);
        root.setPosX(gunPosX);
        root.setPosY(gunPosY);
        root.setPosZ(gunPosZ);
        root.setRotX(gunRotX);
        root.setRotY(gunRotY);
        root.setRotZ(gunRotZ);
    }

    private final void handleWeaponZoom(LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player == null) {
            return;
        }
        Player player2 = player;
        ItemStack stack = player2.m_21205_();
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        float times = (float)5 * this.getDelta();
        double weight = ((Number)data.get(GunProp.WEIGHT)).doubleValue();
        double speed = 7.0 / (weight + (double)2);
        Entity vehicle = player2.m_20202_();
        if (!(!zoom || vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player2) || MinecraftUtil.getNotInGame() || !(drawTime < 0.01) || isEditing || data.reloading() && !data.get(GunProp.ZOOM_RELOAD).booleanValue())) {
            if (fireCooldown <= 10.0) {
                zoomTime = RangesKt.coerceIn((double)(zoomTime + 0.03 * speed * (double)times), (double)0.0, (double)1.0);
            }
        } else {
            zoomTime = RangesKt.coerceIn((double)(zoomTime - 0.04 * speed * (double)times), (double)0.0, (double)1.0);
        }
        if (zoomPos > 0.8) {
            noSprintTicks = 5.0f;
        }
        zoomPos = ((Number)AnimationCurves.EASE_IN_OUT_QUINT.apply(zoomTime)).doubleValue();
        zoomPosZ = ((Number)AnimationCurves.PARABOLA.apply(zoomTime)).doubleValue();
    }

    private final void handleWeaponFire(ViewportEvent.ComputeCameraAngles event, LivingEntity entity) {
        float times = (float)((double)1.65f * customAnimSpeed * (double)RangesKt.coerceAtMost((float)MinecraftUtil.getMc().m_91297_(), (float)0.48f));
        ItemStack stack = entity.m_21205_();
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        double amplitude = 25000.0 * ((Number)data.get(GunProp.RECOIL_Y)).doubleValue() * ((Number)data.get(GunProp.RECOIL_X)).doubleValue();
        if (fireRecoilTime > 0.0) {
            firePosTimer = 0.001;
            fireRotTimer = fireRotTimer > 0.0 ? 0.12 : 0.001;
            fireRecoilTime -= (double)((float)7 * times);
            fireSpread += 0.1 * (double)times;
            firePosZ += (0.8 * firePosZ + 0.4) * ((double)4 * Math.random() + 0.85) * (double)times;
            recoilForce += 0.5;
        }
        fireSpread = RangesKt.coerceIn((double)(fireSpread - 0.1 * (Math.pow(fireSpread, 2) * (double)times)), (double)0.0, (double)2.0);
        firePosZ = RangesKt.coerceIn((double)(firePosZ - 0.7 * (Math.pow(firePosZ, 2) * (double)times)), (double)0.0, (double)2.5);
        firePosZ *= 0.99;
        if (0.0 < firePosTimer) {
            firePosTimer += 0.16 * (double)times;
        }
        if (0.0 < fireRotTimer) {
            fireRotTimer += 0.24 * (double)times;
        }
        if (firePosTimer >= 2.0) {
            firePosTimer = 0.0;
        }
        if (fireRotTimer >= 3.0) {
            fireRotTimer = 0.0;
        }
        double d = boltMove = firePosTimer > 0.0 && firePosTimer <= 0.5 ? 1.2 * (double)Mth.m_14031_((float)((float)Math.PI * 2 * (float)firePosTimer)) : 0.0;
        if (boltMove > 1.0) {
            boltMove = 1.0;
        }
        if (entity instanceof Player && ((Player)entity).m_5833_()) {
            return;
        }
        double shake = (double)MathTool.decayingOscillation(0.6f, 2.0f, 2.0f, (float)firePosTimer) * (1.0 + amplitude) * (double)((float)(((Number)DisplayConfig.WEAPON_SCREEN_SHAKE.get()).doubleValue() / 100.0));
        if (recoilY > 0.0) {
            shake = -shake;
        }
        ClientEventHandler.cameraRot[2] = lerpShake = Mth.m_14139_((double)(event.getPartialTick() * 0.5), (double)lerpShake, (double)shake);
    }

    /*
     * Unable to fully structure code
     */
    @JvmStatic
    public static final void handleShootAnimation(@NotNull CoreGeoBone bone, float x, float y, float z, float rotX, float rotY, float rotZ, float zoomMultiply, float customSpeed) {
        Intrinsics.checkNotNullParameter((Object)bone, (String)"bone");
        v0 = MinecraftUtil.getLocalPlayer();
        if (v0 == null) {
            return;
        }
        player = v0;
        stack = player.m_21205_();
        var13_11 = stack.m_41720_();
        v1 = var13_11 instanceof GunItem != false ? (GunItem)var13_11 : null;
        if (v1 == null) {
            return;
        }
        item = v1;
        ClientEventHandler.customAnimSpeed = customSpeed;
        Intrinsics.checkNotNull((Object)stack);
        data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        barrelType = data.attachment.get(AttachmentType.BARREL);
        gripType = data.attachment.get(AttachmentType.GRIP);
        scopeType = data.attachment.get(AttachmentType.SCOPE);
        switch (barrelType) {
            case 1: {
                v2 = 0.75f;
                break;
            }
            case 2: {
                v2 = 0.95f;
                break;
            }
            default: {
                v2 = 1.0f;
            }
        }
        recoil = v2;
        switch (gripType) {
            case 1: {
                v3 = 0.85f;
                break;
            }
            case 2: {
                v3 = 0.95f;
                break;
            }
            default: {
                v3 = 1.0f;
            }
        }
        gripRecoilX = v3;
        switch (gripType) {
            case 1: {
                v4 = 0.95f;
                break;
            }
            case 2: {
                v4 = 0.85f;
                break;
            }
            default: {
                v4 = 1.0f;
            }
        }
        gripRecoilY = v4;
        switch (scopeType) {
            case 2: {
                v5 = 1.25f - (float)(ClientEventHandler.zoomTime * (double)0.8f);
                break;
            }
            case 3: {
                v5 = 1.25f - (float)ClientEventHandler.zoomTime;
                break;
            }
            default: {
                v5 = zoomRecoil = 1.25f;
            }
        }
        if (!player.m_6144_() || !(player.m_20206_() >= 1.0f)) ** GOTO lbl-1000
        if (!ClientEventHandler.isProne((Player)player)) {
            v6 = 0.85f;
        } else lbl-1000:
        // 2 sources

        {
            v6 = ClientEventHandler.isProne((Player)player) ? (data.attachment.get(AttachmentType.GRIP) == 3 || item.hasBipod(data) ? 0.5f : 0.75f) : 1.0f;
        }
        pose = v6;
        zoomMultiply = zoomMultiply;
        zoomMultiply = RangesKt.coerceIn((float)zoomMultiply, (float)0.0f, (float)1.0f);
        zoom = (float)((double)true - (double)zoomMultiply * ClientEventHandler.zoomTime) * pose;
        bone.setPosX(zoom * x * (float)(ClientEventHandler.recoilHorizon * ((double)0.5f * ClientEventHandler.firePosZ)));
        bone.setPosY(zoom * y * (float)((double)ClientEventHandler.getBoneMoveY((float)ClientEventHandler.firePosTimer) * 0.25 * ((double)true - 0.25 * ClientEventHandler.zoomTime)));
        bone.setPosZ(zoom * z * (float)((double)ClientEventHandler.getBoneMoveZ((float)ClientEventHandler.firePosTimer) * 0.05 + (double)1.1f * ClientEventHandler.firePosZ) * (float)((double)true - 0.5 * ClientEventHandler.zoomTime));
        bone.setRotX(zoom * rotX * (float)((double)(-ClientEventHandler.getBoneRotX((float)ClientEventHandler.fireRotTimer) * 0.017453292f * 0.5f) + (double)0.01f * ClientEventHandler.firePosZ) * gripRecoilX * recoil * (float)((double)true - 0.85 * ClientEventHandler.zoomTime) * zoomRecoil);
        bone.setRotY((float)((double)((float)3 * zoom * rotY * ClientEventHandler.getBoneRotY((float)ClientEventHandler.fireRotTimer) * 0.017453292f) * ClientEventHandler.recoilHorizon * (double)gripRecoilY * (double)recoil * ((double)true - 0.3 * ClientEventHandler.zoomTime) * (double)zoomRecoil));
        bone.setRotZ((float)((double)((float)2 * zoom * rotZ * ClientEventHandler.getBoneRotZ((float)ClientEventHandler.fireRotTimer) * 0.017453292f) * ClientEventHandler.recoilHorizon * (double)gripRecoilY * (double)recoil * ((double)true - 0.5 * ClientEventHandler.zoomTime) * (double)zoomRecoil));
    }

    @JvmStatic
    public static final float getBoneRotX(float t) {
        return t <= 0.25f ? Mth.m_14179_((float)(t / 0.25f), (float)0.0f, (float)-5.82024f) : (t <= 0.5f ? Mth.m_14179_((float)((t - 0.25f) / 0.25f), (float)-5.82024f, (float)-6.38564f) : (t <= 0.75f ? Mth.m_14179_((float)((t - 0.5f) / 0.25f), (float)-6.38564f, (float)-6.0138f) : (t <= 1.0f ? Mth.m_14179_((float)((t - 0.75f) / 0.25f), (float)-6.0138f, (float)-3.22698f) : (t <= 1.3333f ? Mth.m_14179_((float)((t - 1.0f) / 0.3333f), (float)-3.22698f, (float)-0.42425f) : (t <= 1.75f ? Mth.m_14179_((float)((t - 1.3333f) / 0.4167f), (float)-0.42425f, (float)0.23068f) : (t <= 2.0833f ? Mth.m_14179_((float)((t - 1.75f) / 0.3333001f), (float)0.23068f, (float)-0.09988f) : (t <= 2.4167f ? Mth.m_14179_((float)((t - 2.0833f) / 0.33339977f), (float)-0.09988f, (float)0.04509f) : Mth.m_14179_((float)((t - 2.4167f) / 0.5833001f), (float)0.04509f, (float)0.0f))))))));
    }

    @JvmStatic
    public static final float getBoneRotY(float t) {
        return t <= 0.25f ? Mth.m_14179_((float)(t / 0.25f), (float)0.0f, (float)1.33042f) : (t <= 0.5f ? Mth.m_14179_((float)((t - 0.25f) / 0.25f), (float)1.33042f, (float)-0.61289f) : (t <= 0.75f ? Mth.m_14179_((float)((t - 0.5f) / 0.25f), (float)-0.61289f, (float)-0.64862f) : (t <= 1.0f ? Mth.m_14179_((float)((t - 0.75f) / 0.25f), (float)-0.64862f, (float)-0.95049f) : (t <= 1.3333f ? Mth.m_14179_((float)((t - 1.0f) / 0.3333f), (float)-0.95049f, (float)0.27786f) : (t <= 1.75f ? Mth.m_14179_((float)((t - 1.3333f) / 0.4167f), (float)0.27786f, (float)-0.21405f) : (t <= 2.0833f ? Mth.m_14179_((float)((t - 1.75f) / 0.3333001f), (float)-0.21405f, (float)0.076f) : (t <= 2.4167f ? Mth.m_14179_((float)((t - 2.0833f) / 0.33339977f), (float)0.076f, (float)0.01634f) : Mth.m_14179_((float)((t - 2.4167f) / 0.5833001f), (float)0.01634f, (float)0.0f))))))));
    }

    @JvmStatic
    public static final float getBoneRotZ(float t) {
        return t <= 0.25f ? Mth.m_14179_((float)(t / 0.25f), (float)0.0f, (float)5.79388f) : (t <= 0.5f ? Mth.m_14179_((float)((t - 0.25f) / 0.25f), (float)5.79388f, (float)-1.91761f) : (t <= 0.75f ? Mth.m_14179_((float)((t - 0.5f) / 0.25f), (float)-1.91761f, (float)-3.1926f) : (t <= 1.0f ? Mth.m_14179_((float)((t - 0.75f) / 0.25f), (float)-3.1926f, (float)1.89646f) : (t <= 1.3333f ? Mth.m_14179_((float)((t - 1.0f) / 0.3333f), (float)1.89646f, (float)0.43549f) : (t <= 1.75f ? Mth.m_14179_((float)((t - 1.3333f) / 0.4167f), (float)0.43549f, (float)-0.46178f) : (t <= 2.0833f ? Mth.m_14179_((float)((t - 1.75f) / 0.3333001f), (float)-0.46178f, (float)0.12379f) : (t <= 2.4167f ? Mth.m_14179_((float)((t - 2.0833f) / 0.33339977f), (float)0.12379f, (float)-0.04605f) : Mth.m_14179_((float)((t - 2.4167f) / 0.5833001f), (float)-0.04605f, (float)0.0f))))))));
    }

    @JvmStatic
    public static final float getBoneMoveY(float t) {
        return t <= 0.1667f ? Mth.m_14179_((float)(t / 0.1667f), (float)0.0f, (float)0.25313f) : (t <= 0.3333f ? Mth.m_14179_((float)((t - 0.1667f) / 0.16659999f), (float)0.25313f, (float)0.69563f) : (t <= 0.5f ? Mth.m_14179_((float)((t - 0.3333f) / 0.1667f), (float)0.69563f, (float)0.54937f) : (t <= 0.6667f ? Mth.m_14179_((float)((t - 0.5f) / 0.1667f), (float)0.54937f, (float)0.05688f) : (t <= 0.8333f ? Mth.m_14179_((float)((t - 0.6667f) / 0.16659999f), (float)0.05688f, (float)-0.17f) : (t <= 1.0f ? Mth.m_14179_((float)((t - 0.8333f) / 0.1667f), (float)-0.17f, (float)-0.28f) : (t <= 1.1667f ? Mth.m_14179_((float)((t - 1.0f) / 0.1667f), (float)-0.28f, (float)-0.065f) : (t <= 1.3333f ? Mth.m_14179_((float)((t - 1.1667f) / 0.16659999f), (float)-0.065f, (float)0.05f) : (t <= 1.5833f ? Mth.m_14179_((float)((t - 1.3333f) / 0.25f), (float)0.05f, (float)0.03f) : Mth.m_14179_((float)((t - 1.5833f) / 0.4167f), (float)0.03f, (float)0.0f)))))))));
    }

    @JvmStatic
    public static final float getBoneMoveZ(float t) {
        return t <= 0.1667f ? Mth.m_14179_((float)(t / 0.1667f), (float)0.0f, (float)5.205f) : (t <= 0.3333f ? Mth.m_14179_((float)((t - 0.1667f) / 0.16659999f), (float)5.205f, (float)2.775f) : (t <= 0.4167f ? Mth.m_14179_((float)((t - 0.3333f) / 0.08340001f), (float)2.775f, (float)0.66f) : (t <= 0.5833f ? Mth.m_14179_((float)((t - 0.4167f) / 0.16659999f), (float)0.66f, (float)-0.005f) : (t <= 0.75f ? Mth.m_14179_((float)((t - 0.5833f) / 0.1667f), (float)-0.005f, (float)-0.485f) : (t <= 0.9167f ? Mth.m_14179_((float)((t - 0.75f) / 0.1667f), (float)-0.485f, (float)-0.095f) : (t <= 1.1667f ? Mth.m_14179_((float)((t - 0.9167f) / 0.25f), (float)-0.095f, (float)0.06f) : (t <= 1.3333f ? Mth.m_14179_((float)((t - 1.1667f) / 0.16659999f), (float)0.06f, (float)0.1f) : (t <= 1.5833f ? Mth.m_14179_((float)((t - 1.3333f) / 0.25f), (float)0.1f, (float)-0.03f) : Mth.m_14179_((float)((t - 1.5833f) / 0.4167f), (float)-0.03f, (float)0.0f)))))))));
    }

    private final void handleWeaponShell() {
        if (MinecraftUtil.getLocalPlayer() == null) {
            return;
        }
        float times = RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        if (shellIndex >= 5) {
            shellIndex = 0;
            ClientEventHandler.shellIndexTime[0] = 0.001;
        }
        for (int i = 0; i < 5; ++i) {
            if (shellIndexTime[i] > 0.0) {
                ClientEventHandler.shellIndexTime[i] = RangesKt.coerceAtMost((double)(shellIndexTime[i] + (double)((float)8 * times)), (double)50.0);
            }
            if (!(shellIndexTime[i] == 50.0)) continue;
            ClientEventHandler.shellIndexTime[i] = 0.0;
        }
    }

    private final void handleGunRecoil() {
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        ItemStack stack = player.m_21205_();
        Item item = stack.m_41720_();
        GunItem gunItem = item instanceof GunItem ? (GunItem)item : null;
        if (gunItem == null) {
            return;
        }
        GunItem item2 = gunItem;
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        float times = RangesKt.coerceAtMost((float)this.getDelta(), (float)1.6f);
        int barrelType = data.attachment.get(AttachmentType.BARREL);
        int gripType = data.attachment.get(AttachmentType.GRIP);
        double recoil = switch (barrelType) {
            case 1 -> 1.5;
            case 2 -> 2.2;
            default -> 2.4;
        };
        double gripRecoilX = switch (gripType) {
            case 1 -> 1.25;
            case 2 -> 0.25;
            default -> 1.5;
        };
        double gripRecoilY = switch (gripType) {
            case 1 -> 0.7;
            case 2 -> 1.75;
            default -> 2.0;
        };
        double customWeight = ((Number)data.get(GunProp.WEIGHT)).doubleValue();
        double gunRecoilX = ((Number)data.get(GunProp.RECOIL_X)).doubleValue();
        recoilHorizon = Mth.m_14139_((double)(0.2 * (double)times), (double)recoilHorizon, (double)0.0) + recoilY;
        recoilY = 0.0;
        float pose = player.m_6144_() && player.m_20206_() >= 1.0f && !ClientEventHandler.isProne((Player)player) ? 0.7f : (ClientEventHandler.isProne((Player)player) ? (data.attachment.get(AttachmentType.GRIP) == 3 || item2.hasBipod(data) ? 0.1f : 0.5f) : 1.0f);
        float newYaw = player.m_146908_() - (float)(0.6 * recoilHorizon * (double)pose * (double)times * (0.5 + fireSpread) * recoil * ((double)4 / (customWeight + (double)4)) * gripRecoilX);
        player.m_146922_(newYaw);
        player.f_19859_ = player.m_146908_();
        if (firePosTimer > 0.0) {
            float rotateX = (float)((double)((float)70 * pose) * gunRecoilX * Math.sin(firePosTimer * Math.PI * (double)2) * (2.2 - firePosTimer) * recoil * ((double)4 / (customWeight + (double)4)) * gripRecoilY + (double)2 * recoilForce * recoilForce * gunRecoilX * (double)pose * recoil * ((double)4 / (customWeight + (double)4))) * times;
            if (rotateX < 0.0f) {
                rotateX *= 1.8f;
            }
            player.m_146926_(player.m_146909_() - rotateX);
            player.f_19860_ = player.m_146909_();
        }
    }

    private final void handleShockCamera(ViewportEvent.ComputeCameraAngles event, LivingEntity entity) {
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player == null) {
            return;
        }
        Player player2 = player;
        if (player2.m_5833_()) {
            return;
        }
        if (entity.m_21023_((MobEffect)ModMobEffects.SHOCK.get()) && MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.FIRST_PERSON) {
            float shakeStrength = (float)((Number)DisplayConfig.SHOCK_SCREEN_SHAKE.get()).intValue() / 100.0f;
            if (shakeStrength <= 0.0f) {
                return;
            }
            event.setYaw(MinecraftUtil.getMc().f_91063_.m_109153_().m_90590_() + (float)Mth.m_216263_((RandomSource)RandomSource.m_216327_(), (double)-3.0, (double)3.0) * shakeStrength);
            event.setPitch(MinecraftUtil.getMc().f_91063_.m_109153_().m_90589_() + (float)Mth.m_216263_((RandomSource)RandomSource.m_216327_(), (double)-3.0, (double)3.0) * shakeStrength);
        }
    }

    @JvmStatic
    public static final void handleReloadShake(double boneRotX, double boneRotY, double boneRotZ) {
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (player.m_5833_()) {
            return;
        }
        float shakeStrength = (float)((Number)DisplayConfig.WEAPON_SCREEN_SHAKE.get()).intValue() / 100.0f;
        if (shakeStrength <= 0.0f) {
            return;
        }
        ClientEventHandler.cameraRot[0] = -boneRotX * (double)shakeStrength;
        ClientEventHandler.cameraRot[1] = -boneRotY * (double)shakeStrength;
        ClientEventHandler.cameraRot[2] = -boneRotZ * (double)shakeStrength;
    }

    private final void handlePlayerCamera(ViewportEvent.ComputeCameraAngles event) {
        float yaw = event.getYaw();
        float pitch = event.getPitch();
        float roll = event.getRoll();
        float times = RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        LocalPlayer player = MinecraftUtil.getLocalPlayer();
        if (GLFW.glfwGetKey((long)MinecraftUtil.getMc().m_91268_().m_85439_(), (int)262) == 1) {
            cameraLocation = RangesKt.coerceIn((double)(cameraLocation - 0.05 * (double)this.getDelta()), (double)-0.6, (double)0.6);
        }
        if (GLFW.glfwGetKey((long)MinecraftUtil.getMc().m_91268_().m_85439_(), (int)263) == 1) {
            cameraLocation = RangesKt.coerceIn((double)(cameraLocation + 0.05 * (double)this.getDelta()), (double)-0.6, (double)0.6);
        }
        if (player == null) {
            return;
        }
        Entity lookingEntity = SeekTool.seekEntity((Entity)player, 520.0, 5.0);
        double range = lookingEntity != null ? (double)RangesKt.coerceAtLeast((float)player.m_20270_(lookingEntity), (float)0.01f) : RangesKt.coerceAtLeast((double)player.m_20182_().m_82554_(Vec3.m_82528_((Vec3i)((Vec3i)player.m_9236_().m_45547_(new ClipContext(player.m_146892_(), player.m_146892_().m_82549_(player.m_20154_().m_82490_(520.0)), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, (Entity)player)).m_82425_()))), (double)0.01);
        double angle = !((lookDistance = Mth.m_14139_((double)(0.2 * (double)times), (double)lookDistance, (double)range)) == 0.0) && !(cameraLocation == 0.0) ? Math.atan(Math.abs(cameraLocation) / (lookDistance + 2.9)) * (double)57.295776f : 0.0;
        boolean r = true;
        if (MinecraftUtil.getMc().f_91066_.m_92176_() != CameraType.FIRST_PERSON) {
            r = false;
        }
        event.setPitch((float)((double)pitch + cameraRot[0] + ((Boolean)DisplayConfig.CAMERA_ROTATE.get() != false ? 0.2 : 0.0) * turnRot[0] * (double)r + (double)3 * velocityY));
        if (MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.THIRD_PERSON_BACK) {
            event.setYaw((float)((double)yaw + cameraRot[1] + ((Boolean)DisplayConfig.CAMERA_ROTATE.get() != false ? 0.8 : 0.0) * turnRot[1] * (double)r - angle * zoomPos));
        } else {
            event.setYaw((float)((double)yaw + cameraRot[1] + ((Boolean)DisplayConfig.CAMERA_ROTATE.get() != false ? 0.8 : 0.0) * turnRot[1] * (double)r));
        }
        cameraRoll = (float)((double)roll + cameraRot[2] + ((Boolean)DisplayConfig.CAMERA_ROTATE.get() != false ? 0.35 : 0.0) * turnRot[2] * (double)r);
    }

    private final void handleBowPullAnimation(LivingEntity entity, ItemStack stack) {
        float times = (float)4 * RangesKt.coerceAtMost((float)this.getDelta(), (float)0.8f);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        if (holdingFireKey && data.hasEnoughAmmoToShoot((Entity)entity) && !bowPull && stack.m_150930_((Item)ModItems.BOCEK.get())) {
            entity.m_5496_((SoundEvent)ModSounds.BOCEK_PULL_1P.get(), 1.0f, 1.0f);
            bowPull = true;
        }
        if (bowPull) {
            bowPullTimer = RangesKt.coerceAtMost((double)(bowPullTimer + 0.024 * (double)times), (double)1.4);
            bowPower = RangesKt.coerceAtMost((double)(bowPower + 0.018 * (double)times), (double)1.0);
        } else {
            bowPullTimer = RangesKt.coerceAtLeast((double)(bowPullTimer - 0.021 * (double)times), (double)0.0);
            bowPower = RangesKt.coerceAtLeast((double)(bowPower - 0.04 * (double)times), (double)0.0);
        }
        bowPullPos = 0.5 * Math.cos(Math.PI * Math.pow(Math.pow(RangesKt.coerceIn((double)bowPullTimer, (double)0.0, (double)1.0), 2) - 1.0, 2)) + 0.5;
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public final void captureFov(@NotNull ViewportEvent.ComputeFov event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (event.usedConfiguredFov()) {
            fov = event.getFOV();
        }
    }

    /*
     * Unable to fully structure code
     */
    @SubscribeEvent
    public final void onFovUpdate(@NotNull ViewportEvent.ComputeFov event) {
        block12: {
            block15: {
                block13: {
                    block17: {
                        block16: {
                            block14: {
                                Intrinsics.checkNotNullParameter((Object)event, (String)"event");
                                v0 = MinecraftUtil.getLocalPlayer();
                                if (v0 == null) {
                                    return;
                                }
                                player = v0;
                                times = RangesKt.coerceAtMost((float)this.getDelta(), (float)1.6f);
                                vehicle = player.m_20202_();
                                if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player) && ClientEventHandler.zoomVehicle) {
                                    event.setFOV(event.getFOV() / ((VehicleEntity)vehicle).getDefaultZoom((Entity)player));
                                    ClientEventHandler.currentFov = event.getFOV();
                                    return;
                                }
                                stack = player.m_21205_();
                                factor = player.m_6117_() != false && player.m_21211_().m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get()) != false && MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.FIRST_PERSON ? 4.0 + ClientEventHandler.artilleryIndicatorCustomZoom : 1.0;
                                ClientEventHandler.artilleryIndicatorZoom = Mth.m_14139_((double)(0.3 * (double)times), (double)ClientEventHandler.artilleryIndicatorZoom, (double)factor);
                                event.setFOV(event.getFOV() / ClientEventHandler.artilleryIndicatorZoom);
                                if (!(stack.m_41720_() instanceof GunItem)) break block12;
                                if (!event.usedConfiguredFov()) {
                                    ClientEventHandler.lastX = player.m_146909_();
                                    ClientEventHandler.lastY = player.m_146908_();
                                    return;
                                }
                                p = stack.m_150930_((Item)ModItems.BOCEK.get()) != false ? ClientEventHandler.bowPullPos * ClientEventHandler.zoomTime : ClientEventHandler.zoomPos;
                                Intrinsics.checkNotNull((Object)stack);
                                data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
                                ClientEventHandler.customZoom = Mth.m_14139_((double)(0.6 * (double)times), (double)ClientEventHandler.customZoom, (double)(data.zoom() + (ClientEventHandler.breath != false ? 0.75 : 0.0)));
                                if (MinecraftUtil.getMc().f_91066_.m_92176_().m_90612_()) {
                                    event.setFOV(event.getFOV() / ((double)true + p * (ClientEventHandler.customZoom - (double)true)));
                                } else if (MinecraftUtil.getMc().f_91066_.m_92176_() == CameraType.THIRD_PERSON_BACK) {
                                    event.setFOV(event.getFOV() / ((double)true + p * 0.01));
                                }
                                ClientEventHandler.currentFov = event.getFOV();
                                if (!ClientEventHandler.zoom || MinecraftUtil.getNotInGame() || !(ClientEventHandler.drawTime < 0.01) || ClientEventHandler.isEditing) break block13;
                                if (!player.m_6144_()) break block14;
                                ClientEventHandler.lockedEntity = null;
                                break block15;
                            }
                            intelligentChipLevel = data.perk.getLevel(ModPerks.INSTANCE.getINTELLIGENT_CHIP());
                            seekRange = 32.0 + 8.0 * (double)(intelligentChipLevel - 1);
                            if (intelligentChipLevel <= 0) break block15;
                            if (ClientEventHandler.lockedEntity == null) break block16;
                            v1 = ClientEventHandler.lockedEntity;
                            Intrinsics.checkNotNull((Object)v1);
                            if (v1.m_6084_()) break block17;
                        }
                        v2 = data.perk;
                        v3 = ModPerks.INSTANCE.getPHASE_PENETRATING_BULLET().get();
                        Intrinsics.checkNotNullExpressionValue((Object)v3, (String)"get(...)");
                        if (v2.has((Perk)v3)) ** GOTO lbl-1000
                        v4 = data.perk;
                        v5 = ModPerks.INSTANCE.getBEAST_BULLET().get();
                        Intrinsics.checkNotNullExpressionValue((Object)v5, (String)"get(...)");
                        if (v4.has((Perk)v5)) lbl-1000:
                        // 2 sources

                        {
                            v6 = SeekTool.seekEntityThroughWall((Entity)player, seekRange, (double)16 / ClientEventHandler.customZoom);
                        } else {
                            v6 = ClientEventHandler.lockedEntity = SeekTool.seekLivingEntity((Entity)player, seekRange, (double)16 / ClientEventHandler.customZoom);
                        }
                    }
                    if (ClientEventHandler.lockedEntity != null) {
                        v7 = ClientEventHandler.lockedEntity;
                        Intrinsics.checkNotNull((Object)v7);
                        if (v7.m_6084_()) {
                            v8 = ClientEventHandler.lockedEntity;
                            Intrinsics.checkNotNull((Object)v8);
                            targetVec = v8.m_20299_((float)event.getPartialTick());
                            playerVec = player.m_20299_((float)event.getPartialTick());
                            hasGravity = data.perk.getLevel(ModPerks.INSTANCE.getMICRO_MISSILE()) <= 0;
                            velocity = stack.m_150930_((Item)ModItems.BOCEK.get()) != false ? ClientEventHandler.zoomTime * (double)24 : ((Number)data.get(GunProp.VELOCITY)).doubleValue();
                            Intrinsics.checkNotNull((Object)playerVec);
                            Intrinsics.checkNotNull((Object)targetVec);
                            v9 = ClientEventHandler.lockedEntity;
                            Intrinsics.checkNotNull((Object)v9);
                            v10 = v9.m_20184_().m_82490_(0.5);
                            Intrinsics.checkNotNullExpressionValue((Object)v10, (String)"scale(...)");
                            toVec = RangeTool.calculateFiringSolution(playerVec, targetVec, v10, velocity, hasGravity != false ? ((Number)data.get(GunProp.GRAVITY)).doubleValue() : 0.0);
                            this.look((Player)player, toVec);
                            v11 = ClientEventHandler.lockedEntity;
                            Intrinsics.checkNotNull((Object)v11);
                            if ((double)player.m_20270_(v11) > seekRange) {
                                ClientEventHandler.lockedEntity = null;
                            }
                        }
                    }
                    break block15;
                }
                ClientEventHandler.lockedEntity = null;
            }
            ClientEventHandler.lastX = player.m_146909_();
            ClientEventHandler.lastY = player.m_146908_();
        }
        if (stack.m_150930_((Item)ModItems.MONITOR.get()) && stack.m_41784_().m_128471_("Using") && stack.m_41784_().m_128471_("Linked")) {
            ClientEventHandler.droneFovLerp = Mth.m_14139_((double)(0.1 * (double)this.getDelta()), (double)ClientEventHandler.droneFovLerp, (double)ClientEventHandler.droneFov);
            event.setFOV(event.getFOV() / ClientEventHandler.droneFovLerp);
            ClientEventHandler.currentFov = event.getFOV();
        }
    }

    public final void look(@NotNull Player player, @NotNull Vec3 target) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        Intrinsics.checkNotNullParameter((Object)target, (String)"target");
        double d0 = target.f_82479_;
        double d1 = target.f_82480_;
        double d2 = target.f_82481_;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float fromX = lastX;
        float fromY = Mth.m_14177_((float)lastY);
        float toX = (float)Mth.m_14175_((double)(-(Mth.m_14136_((double)d1, (double)d3) * 57.2957763671875)));
        float toY = (float)Mth.m_14175_((double)(Mth.m_14136_((double)d2, (double)d0) * 57.2957763671875 - (double)90.0f));
        float diffY = Mth.m_14177_((float)(toY - fromY));
        float finalY = Mth.m_14177_((float)(fromY + diffY * 0.2f));
        player.m_146926_(Mth.m_14177_((float)Mth.m_14179_((float)0.2f, (float)fromX, (float)toX)));
        player.m_146922_(Mth.m_14177_((float)finalY));
    }

    @SubscribeEvent
    public final void setPlayerInvisible(@NotNull RenderPlayerEvent.Pre event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Player otherPlayer = event.getEntity();
        Entity vehicle = otherPlayer.m_20202_();
        if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).hidePassenger((Entity)otherPlayer)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public final void handleRenderCrossHair(@NotNull RenderGuiOverlayEvent.Pre event) {
        Entity vehicle;
        ItemStack stack;
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!Intrinsics.areEqual((Object)event.getOverlay(), (Object)VanillaGuiOverlay.CROSSHAIR.type())) {
            return;
        }
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        if (((Boolean)MiscConfig.HIDE_COMBAT_HUD.get()).booleanValue()) {
            stack = player.m_21205_();
            if (stack.m_41720_() instanceof GunItem) {
                event.setCanceled(true);
                return;
            }
            vehicle = player.m_20202_();
            if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).hasWeapon(((VehicleEntity)vehicle).getSeatIndex((Entity)player))) {
                event.setCanceled(true);
                return;
            }
        }
        if (!MinecraftUtil.getMc().f_91066_.m_92176_().m_90612_()) {
            return;
        }
        if (player.m_6117_() && player.m_21211_().m_150930_((Item)ModItems.ARTILLERY_INDICATOR.get())) {
            event.setCanceled(true);
        }
        if ((stack = player.m_21205_()).m_41720_() instanceof GunItem) {
            event.setCanceled(true);
        }
        if ((vehicle = player.m_20202_()) instanceof VehicleEntity && ((VehicleEntity)vehicle).hasWeapon(((VehicleEntity)vehicle).getSeatIndex((Entity)player))) {
            event.setCanceled(true);
        }
        if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player)) {
            event.setCanceled(true);
        }
        if (stack.m_150930_((Item)ModItems.MONITOR.get()) && stack.m_41784_().m_128471_("Using") && stack.m_41784_().m_128471_("Linked")) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public final void handleAvoidRenderingHotbar(@NotNull RenderGuiOverlayEvent.Pre event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!Intrinsics.areEqual((Object)event.getOverlay(), (Object)VanillaGuiOverlay.HOTBAR.type())) {
            return;
        }
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity && ((VehicleEntity)vehicle).banHand((LivingEntity)player)) {
            event.setCanceled(true);
        }
    }

    public final void resetGunStatus() {
        drawTime = 1.0;
        for (int i = 0; i < 5; ++i) {
            ClientEventHandler.shellIndexTime[i] = 0.0;
        }
        clientTimer.stop();
        zoom = false;
        holdingFireKeyTicks = 0;
        holdingFireKeyTicks0 = 0.0f;
        ClickEventHandler.switchZoom = false;
        burstFireAmount = 0;
        bowPullTimer = 0.0;
        bowPower = 0.0;
        noSprintTicks = 10.0f;
        seekingTime = 0;
        lockOn = false;
        lockingEntity = null;
        seekingEntity = null;
        lockingPos = null;
        isEditing = false;
        zoomTime = 0.0;
    }

    public final void resetLungeMineStatus() {
        lungeDraw = 30;
        lungeSprint = 0;
        lungeAttack = 0;
        usingLunge = false;
    }

    private final void handleWeaponDraw(LivingEntity entity) {
        float times = this.getDelta();
        ItemStack stack = entity.m_21205_();
        Intrinsics.checkNotNull((Object)stack);
        GunData data = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
        double weight = ((Number)data.get(GunProp.WEIGHT)).doubleValue();
        double speed = (double)20 / (weight + (double)5);
        drawTime = RangesKt.coerceAtLeast((double)(drawTime - RangesKt.coerceAtLeast((double)(0.2 * speed * (double)times * drawTime), (double)8.0E-4)), (double)0.0);
    }

    @JvmStatic
    public static final void handleShells(float x, float y, CoreGeoBone ... shells) {
        Intrinsics.checkNotNullParameter((Object)shells, (String)"shells");
        int n = shells.length;
        for (int i = 0; i < n; ++i) {
            int i2 = i;
            CoreGeoBone element = shells[i];
            if (i2 >= 5) break;
            element.setPosX((float)((double)(-x) * shellIndexTime[i2] * (((double)150 - shellIndexTime[i2]) / (double)150)));
            element.setPosY((float)((double)y * randomShell[0] * shellIndexTime[i2] - 0.025 * Math.pow(shellIndexTime[i2], 2)));
            element.setRotX((float)(randomShell[1] * shellIndexTime[i2]));
            element.setRotY((float)(randomShell[2] * shellIndexTime[i2]));
        }
    }

    public final void aimAtVillager(@NotNull Player player) {
        Intrinsics.checkNotNullParameter((Object)player, (String)"player");
        if (aimVillagerCountdown > 0) {
            return;
        }
        if (zoom) {
            Entity entity = OverlayTraceHandler.playerReachEntity;
            AbstractVillager abstractVillager = entity instanceof AbstractVillager ? (AbstractVillager)entity : null;
            if (abstractVillager == null) {
                return;
            }
            AbstractVillager entity2 = abstractVillager;
            List<Entity> entities = SeekTool.seekLivingEntities((Entity)entity2, 16.0, 120.0);
            for (Entity e : entities) {
                if (!Intrinsics.areEqual((Object)e, (Object)player)) continue;
                MinecraftUtil.sendPacketToServer(new AimVillagerMessage(entity2.m_19879_()));
                aimVillagerCountdown = 80;
            }
        }
    }

    @JvmStatic
    public static final boolean canOpenEditScreen(@NotNull ItemStack stack, @Nullable InteractionHand hand) {
        Intrinsics.checkNotNullParameter((Object)stack, (String)"stack");
        return burstFireAmount == 0 && stack.m_41720_() instanceof GunItem && hand == InteractionHand.MAIN_HAND;
    }

    @JvmStatic
    public static final void onOpenEditScreen() {
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer player = localPlayer;
        isEditing = true;
        holdingFireKey = false;
        player.m_5496_((SoundEvent)ModSounds.EDIT_MODE.get(), 1.0f, 1.0f);
    }

    @JvmStatic
    public static final void onCloseEditScreen() {
        isEditing = false;
    }

    @JvmStatic
    public static final void editModelShake() {
        velocityY = 0.2;
    }

    @JvmStatic
    public static final void stopSoundEvent(@NotNull ResourceLocation location, @NotNull SoundSource source) {
        Intrinsics.checkNotNullParameter((Object)location, (String)"location");
        Intrinsics.checkNotNullParameter((Object)source, (String)"source");
        MinecraftUtil.getMc().m_91106_().m_120386_(location, source);
    }

    @JvmStatic
    public static final void stopVehicleSeekSound(@Nullable Player player) {
        if (player == null) {
            return;
        }
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity) {
            GunData gunData = ((VehicleEntity)vehicle).getGunData((Entity)player);
            if (gunData == null) {
                return;
            }
            GunData gunData2 = gunData;
            ResourceLocation location = gunData2.get(GunProp.SOUND_INFO).getLocking().m_11660_();
            Intrinsics.checkNotNull((Object)location);
            ClientEventHandler.stopSoundEvent(location, SoundSource.PLAYERS);
        }
    }

    @JvmStatic
    public static final void stopWeaponSeekSound(@Nullable Player player) {
        if (player == null) {
            return;
        }
        ItemStack stack = player.m_21205_();
        if (stack.m_41720_() instanceof GunItem) {
            Intrinsics.checkNotNull((Object)stack);
            GunData gunData = GunData.Companion.from$default(GunData.Companion, stack, null, 2, null);
            ResourceLocation location = gunData.get(GunProp.SOUND_INFO).getLocking().m_11660_();
            Intrinsics.checkNotNull((Object)location);
            ClientEventHandler.stopSoundEvent(location, SoundSource.PLAYERS);
        }
    }

    @JvmStatic
    public static final void stopVehicleReloadSound(@Nullable Player player) {
        if (player == null) {
            return;
        }
        Entity vehicle = player.m_20202_();
        if (vehicle instanceof VehicleEntity) {
            GunData gunData = ((VehicleEntity)vehicle).getGunData((Entity)player);
            if (gunData == null) {
                return;
            }
            GunData gunData2 = gunData;
            ResourceLocation location = gunData2.get(GunProp.SOUND_INFO).vehicleReload.m_11660_();
            Intrinsics.checkNotNull((Object)location);
            ClientEventHandler.stopSoundEvent(location, SoundSource.PLAYERS);
        }
    }

    @SubscribeEvent
    public final void onRenderNameTag(@NotNull RenderNameTagEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Entity entity = event.getEntity();
        Player player = entity instanceof Player ? (Player)entity : null;
        if (player == null) {
            return;
        }
        Player entity2 = player;
        LocalPlayer localPlayer = MinecraftUtil.getLocalPlayer();
        if (localPlayer == null) {
            return;
        }
        LocalPlayer self = localPlayer;
        if (Intrinsics.areEqual((Object)self, (Object)entity2)) {
            return;
        }
        if (!(self.m_20202_() instanceof VehicleEntity)) {
            return;
        }
        if (self.m_20365_((Entity)entity2)) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public final void onPlayerLoggedIn(@NotNull PlayerEvent.PlayerLoggedInEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (!((Boolean)DisplayConfig.ENABLE_VERSION_CHECK_WARNING.get()).booleanValue()) {
            return;
        }
        Player player = event.getEntity();
        if (player == null) {
            return;
        }
        Player player2 = player;
        if (ModVersionEventHandler.currentVersion == null || ModVersionEventHandler.previousVersion == null) {
            return;
        }
        Object[] objectArray = new Object[]{Component.m_237113_((String)("" + ModVersionEventHandler.previousVersion)).m_130940_(ChatFormatting.YELLOW), Component.m_237113_((String)("" + ModVersionEventHandler.currentVersion)).m_130940_(ChatFormatting.YELLOW)};
        player2.m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.vehicle_reset_kit_1", (Object[])objectArray).m_130940_(ChatFormatting.RED), false);
        objectArray = new Object[]{Component.m_237113_((String)"[").m_7220_(((Item)ModItems.VEHICLE_RESET_KIT.get()).m_7968_().m_41786_()).m_130946_("]").m_130940_(ChatFormatting.GREEN)};
        player2.m_5661_((Component)Component.m_237110_((String)"tips.superbwarfare.vehicle_reset_kit_2", (Object[])objectArray), false);
        player2.m_5661_((Component)Component.m_237115_((String)"tips.superbwarfare.vehicle_reset_kit_3").m_130940_(ChatFormatting.AQUA).m_130940_(ChatFormatting.UNDERLINE), false);
    }

    @SubscribeEvent
    public final void onFogColor(@NotNull ViewportEvent.ComputeFogColor event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        if (activeThermalImaging) {
            event.setRed(0.1f);
            event.setGreen(0.1f);
            event.setBlue(0.1f);
        }
    }

    @SubscribeEvent
    public final void onClientVehicleFire(@NotNull ClientVehicleFireEvent event) {
        Intrinsics.checkNotNullParameter((Object)((Object)event), (String)"event");
        Entity shooter = event.getShooter();
        VehicleEntity vehicle = event.getVehicle();
        int index = event.getIndex();
        VehicleLightingHandler.onVehicleFire(event);
        VehicleAnimationInstance<VehicleEntity> vehicleAnimationInstance = vehicle.getAnimationInstance();
        if (vehicleAnimationInstance == null) {
            return;
        }
        VehicleAnimationInstance<VehicleEntity> ani = vehicleAnimationInstance;
        String string = event.getWeaponName();
        if (string == null && (string = vehicle.getGunName(vehicle.getSeatIndex(shooter))) == null) {
            return;
        }
        String name = string;
        ani.fire(FormatToolKt.camelToSnake(name), index);
    }

    private static final Boolean hasThermalImagingGoggles$lambda$0(ICuriosItemHandler it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.findFirstCurio((Item)ModItems.THERMAL_IMAGING_GOGGLES.get()).isPresent();
    }

    private static final Boolean hasThermalImagingGoggles$lambda$1(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        Intrinsics.checkNotNullParameter((Object)p0, (String)"p0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final Boolean hasThermalImagingGoggles$lambda$2() {
        return false;
    }

    private static final Boolean playGunClientSounds$lambda$7(IEnergyStorage it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getEnergyStored() > 0;
    }

    private static final Boolean playGunClientSounds$lambda$8(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        Intrinsics.checkNotNullParameter((Object)p0, (String)"p0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final Boolean playGunClientSounds$lambda$9() {
        return false;
    }

    private static final Boolean playGunClientSounds$lambda$10(IEnergyStorage it) {
        Intrinsics.checkNotNullParameter((Object)it, (String)"it");
        return it.getEnergyStored() >= 3000;
    }

    private static final Boolean playGunClientSounds$lambda$11(Function1 $tmp0, Object p0) {
        Intrinsics.checkNotNullParameter((Object)$tmp0, (String)"$tmp0");
        Intrinsics.checkNotNullParameter((Object)p0, (String)"p0");
        return (Boolean)$tmp0.invoke(p0);
    }

    private static final Boolean playGunClientSounds$lambda$12() {
        return false;
    }

    static {
        double[] dArray = new double[]{0.0, 0.0, 0.0};
        turnRot = dArray;
        dArray = new double[]{0.0, 0.0, 0.0};
        cameraRot = dArray;
        customAnimSpeed = 1.0;
        droneFov = 1.0;
        droneFovLerp = 1.0;
        cameraLocation = 0.6;
        drawTime = 1.0;
        dArray = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        shellIndexTime = dArray;
        dArray = new double[]{0.0, 0.0, 0.0};
        randomShell = dArray;
        artilleryIndicatorZoom = 1.0;
        clientTimer = new MillisTimer();
        clientTimerVehicle = new MillisTimer();
        shouldPlayDischargeSound = true;
        dArray = new double[]{0.0, 0.0, 0.0};
        shakePos = dArray;
        loiterLastForwardTapTick = -20;
        unloadPassengersLastTapTick = -20;
        disconnectTowingLastTapTick = -20;
        tdmSavedData = new TDMSavedData();
        fov = 70.0;
        Vec3 vec3 = Vec3.f_82478_;
        Intrinsics.checkNotNullExpressionValue((Object)vec3, (String)"ZERO");
        bombHitPosO = vec3;
        Vec3 vec32 = Vec3.f_82478_;
        Intrinsics.checkNotNullExpressionValue((Object)vec32, (String)"ZERO");
        bombHitPos = vec32;
    }

    @Metadata(mv={2, 0, 0}, k=3, xi=48)
    public final class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[Ammo.values().length];
            try {
                nArray[Ammo.SHOTGUN.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Ammo.SNIPER.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[Ammo.HEAVY.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}
