package org.espetro.client.vehicle;

import net.minecraft.resources.ResourceLocation;
import org.espetro.client.gui.UiTheme;

/** Client-side vehicle interaction progress for the AuraTip wheel center. */
public final class VehicleInteractionState {

    private static final ResourceLocation ICON =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/vehicle/radial_seat.png");

    private static VehicleInteractionKind kind = VehicleInteractionKind.NONE;
    private static float progress = -1f;
    private static float previousProgress;

    private VehicleInteractionState() {
    }

    public static void set(VehicleInteractionKind newKind, float p) {
        if (newKind == null || newKind == VehicleInteractionKind.NONE) {
            clear();
            return;
        }
        float next = clamp(p);
        if (kind != newKind || next != progress) previousProgress = kind == newKind && next >= progress ? progress : 0;
        kind = newKind;
        progress = next;
    }

    public static void setMount(float p) {
        set(VehicleInteractionKind.MOUNT, p);
    }

    public static void setSeatSwitch(float p) {
        set(VehicleInteractionKind.SEAT_SWITCH, p);
    }

    public static void setDismount(float p) {
        set(VehicleInteractionKind.DISMOUNT, p);
    }

    public static void clear() {
        kind = VehicleInteractionKind.NONE;
        progress = -1f;
    }

    public static VehicleInteractionKind kind() {
        return kind;
    }

    public static float progress() {
        return kind == VehicleInteractionKind.NONE ? -1f : progress;
    }

    public static float progress(float partialTick) {
        return kind == VehicleInteractionKind.NONE ? -1f
            : net.minecraft.util.Mth.lerp(clamp(partialTick), previousProgress, progress);
    }

    public static ResourceLocation icon() {
        return ICON;
    }

    public static String label() {
        return switch (kind) {
            case MOUNT -> "上车";
            case SEAT_SWITCH -> "换座";
            case DISMOUNT -> "下车";
            default -> "";
        };
    }

    public static int color() {
        if (progress < 0f) {
            return UiTheme.TEXT_SECOND;
        }
        if (progress > 0.75f) {
            return UiTheme.POSITIVE;
        }
        if (progress > 0.5f) {
            return UiTheme.WARNING;
        }
        return UiTheme.NEGATIVE;
    }

    private static float clamp(float p) {
        if (p < 0f) {
            return 0f;
        }
        return Math.min(1f, p);
    }
}
