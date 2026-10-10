/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.RegistryAccess
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.event;

import net.minecraft.core.RegistryAccess;
import net.minecraftforge.eventbus.api.Event;

public class TagsUpdatedEvent
extends Event {
    private final RegistryAccess registryAccess;
    private final UpdateCause updateCause;
    private final boolean integratedServer;

    public TagsUpdatedEvent(RegistryAccess registryAccess, boolean fromClientPacket, boolean isIntegratedServerConnection) {
        this.registryAccess = registryAccess;
        this.updateCause = fromClientPacket ? UpdateCause.CLIENT_PACKET_RECEIVED : UpdateCause.SERVER_DATA_LOAD;
        this.integratedServer = isIntegratedServerConnection;
    }

    public RegistryAccess getRegistryAccess() {
        return this.registryAccess;
    }

    public UpdateCause getUpdateCause() {
        return this.updateCause;
    }

    public boolean shouldUpdateStaticData() {
        return this.updateCause == UpdateCause.SERVER_DATA_LOAD || !this.integratedServer;
    }

    public static enum UpdateCause {
        SERVER_DATA_LOAD,
        CLIENT_PACKET_RECEIVED;

    }
}

