/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.vehicle;

import net.minecraft.resources.ResourceLocation;
import org.espetro.client.vehicle.VehicleInteractionKind;

public final class VehicleInteractionState {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/vehicle/mount.png");
    private static VehicleInteractionKind kind = VehicleInteractionKind.NONE;
    private static float progress = -1.0f;

    private VehicleInteractionState() {
    }

    public static void set(VehicleInteractionKind newKind, float p) {
        if (newKind == null || newKind == VehicleInteractionKind.NONE) {
            VehicleInteractionState.clear();
            return;
        }
        kind = newKind;
        progress = VehicleInteractionState.clamp(p);
    }

    public static void setMount(float p) {
        VehicleInteractionState.set(VehicleInteractionKind.MOUNT, p);
    }

    public static void setSeatSwitch(float p) {
        VehicleInteractionState.set(VehicleInteractionKind.SEAT_SWITCH, p);
    }

    public static void setDismount(float p) {
        VehicleInteractionState.set(VehicleInteractionKind.DISMOUNT, p);
    }

    public static void clear() {
        kind = VehicleInteractionKind.NONE;
        progress = -1.0f;
    }

    public static VehicleInteractionKind kind() {
        return kind;
    }

    public static float progress() {
        return kind == VehicleInteractionKind.NONE ? -1.0f : progress;
    }

    public static ResourceLocation icon() {
        return ICON;
    }

    public static String label() {
        return switch (kind) {
            case VehicleInteractionKind.MOUNT -> "\u4e0a\u8f66";
            case VehicleInteractionKind.SEAT_SWITCH -> "\u6362\u5ea7";
            case VehicleInteractionKind.DISMOUNT -> "\u4e0b\u8f66";
            default -> "";
        };
    }

    public static int color() {
        if (progress < 0.0f) {
            return -7829368;
        }
        if (progress > 0.75f) {
            return -12268476;
        }
        if (progress > 0.5f) {
            return -3364352;
        }
        return -3390396;
    }

    private static float clamp(float p) {
        if (p < 0.0f) {
            return 0.0f;
        }
        return Math.min(1.0f, p);
    }
}

