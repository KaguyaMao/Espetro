/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.sbw;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import org.espetro.client.vehicle.SeatSwitchGate;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Minecraft.class})
public abstract class MinecraftSeatMixin {
    @Shadow
    public LocalPlayer f_91074_;
    @Shadow
    public Options f_91066_;
    @Unique
    private boolean[] espetro$suppressedHotbar;

    @Inject(method={"handleKeybinds"}, at={@At(value="HEAD")}, require=0)
    private void espetro$suppressSeatHotbar(CallbackInfo ci) {
        this.espetro$suppressedHotbar = null;
        if (this.f_91074_ == null || this.f_91066_ == null) {
            return;
        }
        if (!SbwVehicleSeatResolver.isSupportedVehicle(this.f_91074_.m_20202_())) {
            return;
        }
        if (SeatSwitchGate.isArmed()) {
            return;
        }
        if (!this.f_91066_.f_92090_.m_90857_()) {
            return;
        }
        KeyMapping[] hotbar = this.f_91066_.f_92056_;
        boolean[] wasDown = new boolean[hotbar.length];
        boolean any = false;
        for (int i = 0; i < hotbar.length; ++i) {
            if (hotbar[i] == null || !hotbar[i].m_90857_()) continue;
            wasDown[i] = true;
            any = true;
            hotbar[i].m_7249_(false);
        }
        if (any) {
            this.espetro$suppressedHotbar = wasDown;
        }
    }

    @Inject(method={"handleKeybinds"}, at={@At(value="RETURN")}, require=0)
    private void espetro$restoreSeatHotbar(CallbackInfo ci) {
        if (this.espetro$suppressedHotbar == null || this.f_91066_ == null) {
            return;
        }
        KeyMapping[] hotbar = this.f_91066_.f_92056_;
        for (int i = 0; i < hotbar.length && i < this.espetro$suppressedHotbar.length; ++i) {
            if (!this.espetro$suppressedHotbar[i] || hotbar[i] == null) continue;
            hotbar[i].m_7249_(true);
        }
        this.espetro$suppressedHotbar = null;
    }
}

