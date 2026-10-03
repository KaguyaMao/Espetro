/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.ReloadableResourceManager
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class RegisterClientReloadListenersEvent
extends Event
implements IModBusEvent {
    private final ReloadableResourceManager resourceManager;

    @ApiStatus.Internal
    public RegisterClientReloadListenersEvent(ReloadableResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public void registerReloadListener(PreparableReloadListener reloadListener) {
        this.resourceManager.m_7217_(reloadListener);
    }
}

