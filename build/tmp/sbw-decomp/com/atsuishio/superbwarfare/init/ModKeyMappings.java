/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  kotlin.Metadata
 *  kotlin.jvm.JvmField
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.client.settings.KeyModifier
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  org.jetbrains.annotations.NotNull
 */
package com.atsuishio.superbwarfare.init;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
@Metadata(mv={2, 0, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b&\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J6\u0010,\u001a\u00020\b2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020/2\b\b\u0002\u00100\u001a\u0002012\b\b\u0002\u00102\u001a\u0002032\b\b\u0002\u00104\u001a\u000205H\u0002J\u0010\u00106\u001a\u0002072\u0006\u00108\u001a\u000209H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0017\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0019\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001b\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001d\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001f\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010%\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010&\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010'\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010(\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010*\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010+\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2={"Lcom/atsuishio/superbwarfare/init/ModKeyMappings;", "", "<init>", "()V", "CATEGORY", "", "KEYS", "", "Lnet/minecraft/client/KeyMapping;", "MOVE_FORWARD", "MOVE_BACKWARD", "MOVE_LEFT", "MOVE_RIGHT", "MOVE_SPACE", "MOVE_SHIFT", "MOVE_CTRL", "RELOAD", "FIRE_MODE", "SENSITIVITY_INCREASE", "SENSITIVITY_REDUCE", "INTERACT", "DISMOUNT", "BREATH", "CHANGE_SEAT", "CONFIG", "EDIT_MODE", "CHANGE_AMMO_FORWARD", "CHANGE_AMMO_BACKWARD", "CHANGE_FIRE_MODE_FORWARD", "CHANGE_FIRE_MODE_BACKWARD", "UNLOAD", "UNLOAD_PASSENGERS", "DISCONNECT_TOWING", "FIRE", "HOLD_ZOOM", "SWITCH_ZOOM", "RELEASE_DECOY", "FREE_CAMERA", "MELEE", "VEHICLE_SEEK", "MARK", "ACTIVE_THERMAL_IMAGING", "LOITER_CONFIG", "TOGGLE_TACTICAL_MAP", "registerKey", "name", "code", "", "conflictContext", "Lnet/minecraftforge/client/settings/KeyConflictContext;", "modifier", "Lnet/minecraftforge/client/settings/KeyModifier;", "type", "Lcom/mojang/blaze3d/platform/InputConstants$Type;", "registerKeyMappings", "", "event", "Lnet/minecraftforge/client/event/RegisterKeyMappingsEvent;", "superbwarfare"})
@SourceDebugExtension(value={"SMAP\nModKeyMappings.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ModKeyMappings.kt\ncom/atsuishio/superbwarfare/init/ModKeyMappings\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,151:1\n1863#2,2:152\n*S KotlinDebug\n*F\n+ 1 ModKeyMappings.kt\ncom/atsuishio/superbwarfare/init/ModKeyMappings\n*L\n149#1:152,2\n*E\n"})
public final class ModKeyMappings {
    @NotNull
    public static final ModKeyMappings INSTANCE = new ModKeyMappings();
    @NotNull
    public static final String CATEGORY = "key.categories.superbwarfare";
    @NotNull
    private static final List<KeyMapping> KEYS = new ArrayList();
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_FORWARD = ModKeyMappings.registerKey$default(INSTANCE, "move_forward", 87, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_BACKWARD = ModKeyMappings.registerKey$default(INSTANCE, "move_backward", 83, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_LEFT = ModKeyMappings.registerKey$default(INSTANCE, "move_left", 65, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_RIGHT = ModKeyMappings.registerKey$default(INSTANCE, "move_right", 68, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_SPACE = ModKeyMappings.registerKey$default(INSTANCE, "move_space", 32, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_SHIFT = ModKeyMappings.registerKey$default(INSTANCE, "move_shift", 340, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MOVE_CTRL = ModKeyMappings.registerKey$default(INSTANCE, "move_ctrl", 341, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping RELOAD = ModKeyMappings.registerKey$default(INSTANCE, "reload", 82, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping FIRE_MODE = ModKeyMappings.registerKey$default(INSTANCE, "fire_mode", 78, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping SENSITIVITY_INCREASE = ModKeyMappings.registerKey$default(INSTANCE, "sensitivity_increase", 266, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping SENSITIVITY_REDUCE = ModKeyMappings.registerKey$default(INSTANCE, "sensitivity_reduce", 267, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping INTERACT = ModKeyMappings.registerKey$default(INSTANCE, "interact", 88, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping DISMOUNT = ModKeyMappings.registerKey$default(INSTANCE, "dismount", 342, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping BREATH = ModKeyMappings.registerKey$default(INSTANCE, "breath", 341, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CHANGE_SEAT = ModKeyMappings.registerKey$default(INSTANCE, "change_seat", 340, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CONFIG = ModKeyMappings.registerKey$default(INSTANCE, "config", 79, KeyConflictContext.IN_GAME, KeyModifier.ALT, null, 16, null);
    @JvmField
    @NotNull
    public static final KeyMapping EDIT_MODE = ModKeyMappings.registerKey$default(INSTANCE, "edit_mode", 72, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CHANGE_AMMO_FORWARD = ModKeyMappings.registerKey$default(INSTANCE, "change_ammo_forward", 263, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CHANGE_AMMO_BACKWARD = ModKeyMappings.registerKey$default(INSTANCE, "change_ammo_backward", 262, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CHANGE_FIRE_MODE_FORWARD = ModKeyMappings.registerKey$default(INSTANCE, "change_fire_mode_forward", 265, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping CHANGE_FIRE_MODE_BACKWARD = ModKeyMappings.registerKey$default(INSTANCE, "change_fire_mode_backward", 264, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping UNLOAD = ModKeyMappings.registerKey$default(INSTANCE, "unload", InputConstants.f_84822_.m_84873_(), null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping UNLOAD_PASSENGERS = ModKeyMappings.registerKey$default(INSTANCE, "unload_passengers", 85, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping DISCONNECT_TOWING = ModKeyMappings.registerKey$default(INSTANCE, "disconnect_towing", 89, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping FIRE = ModKeyMappings.registerKey$default(INSTANCE, "fire", 0, null, null, InputConstants.Type.MOUSE, 12, null);
    @JvmField
    @NotNull
    public static final KeyMapping HOLD_ZOOM = ModKeyMappings.registerKey$default(INSTANCE, "hold_zoom", 1, null, null, InputConstants.Type.MOUSE, 12, null);
    @JvmField
    @NotNull
    public static final KeyMapping SWITCH_ZOOM = ModKeyMappings.registerKey$default(INSTANCE, "switch_zoom", -1, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping RELEASE_DECOY = ModKeyMappings.registerKey$default(INSTANCE, "release_decoy", 86, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping FREE_CAMERA = ModKeyMappings.registerKey$default(INSTANCE, "free_camera", 67, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MELEE = ModKeyMappings.registerKey$default(INSTANCE, "melee", 86, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping VEHICLE_SEEK = ModKeyMappings.registerKey$default(INSTANCE, "vehicle_seek", 88, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping MARK = ModKeyMappings.registerKey$default(INSTANCE, "mark", 2, null, null, InputConstants.Type.MOUSE, 12, null);
    @JvmField
    @NotNull
    public static final KeyMapping ACTIVE_THERMAL_IMAGING = ModKeyMappings.registerKey$default(INSTANCE, "active_thermal_imaging", 75, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping LOITER_CONFIG = ModKeyMappings.registerKey$default(INSTANCE, "loiter_config", 74, null, null, null, 28, null);
    @JvmField
    @NotNull
    public static final KeyMapping TOGGLE_TACTICAL_MAP = ModKeyMappings.registerKey$default(INSTANCE, "toggle_tactical_map", 77, null, null, null, 28, null);

    private ModKeyMappings() {
    }

    private final KeyMapping registerKey(String name, int code, KeyConflictContext conflictContext, KeyModifier modifier, InputConstants.Type type) {
        KeyMapping key = new KeyMapping("key.superbwarfare." + name, (IKeyConflictContext)conflictContext, modifier, type, code, CATEGORY);
        KEYS.add(key);
        return key;
    }

    static /* synthetic */ KeyMapping registerKey$default(ModKeyMappings modKeyMappings, String string, int n, KeyConflictContext keyConflictContext, KeyModifier keyModifier, InputConstants.Type type, int n2, Object object) {
        if ((n2 & 4) != 0) {
            keyConflictContext = KeyConflictContext.IN_GAME;
        }
        if ((n2 & 8) != 0) {
            keyModifier = KeyModifier.NONE;
        }
        if ((n2 & 0x10) != 0) {
            type = InputConstants.Type.KEYSYM;
        }
        return modKeyMappings.registerKey(string, n, keyConflictContext, keyModifier, type);
    }

    @SubscribeEvent
    public final void registerKeyMappings(@NotNull RegisterKeyMappingsEvent event) {
        Intrinsics.checkNotNullParameter((Object)event, (String)"event");
        Iterable $this$forEach$iv = KEYS;
        boolean $i$f$forEach = false;
        for (Object element$iv : $this$forEach$iv) {
            KeyMapping it = (KeyMapping)element$iv;
            boolean bl = false;
            event.register(it);
        }
    }
}
