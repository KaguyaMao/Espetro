/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public class ComputeFovModifierEvent
extends Event {
    private final Player player;
    private final float fovModifier;
    private float newFovModifier;

    @ApiStatus.Internal
    public ComputeFovModifierEvent(Player player, float fovModifier) {
        this.player = player;
        this.fovModifier = fovModifier;
        this.setNewFovModifier((float)Mth.m_14139_((double)((Double)Minecraft.m_91087_().f_91066_.m_231925_().m_231551_()), (double)1.0, (double)fovModifier));
    }

    public Player getPlayer() {
        return this.player;
    }

    public float getFovModifier() {
        return this.fovModifier;
    }

    public float getNewFovModifier() {
        return this.newFovModifier;
    }

    public void setNewFovModifier(float newFovModifier) {
        this.newFovModifier = newFovModifier;
    }
}

