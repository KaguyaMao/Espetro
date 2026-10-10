/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.client;

import com.example.espoints.network.SyncPlayerIdentityMessage;
import com.example.espoints.network.SyncPlayerPositionsMessage;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ClientPlayerIdentityState {
    private static final ClientPlayerIdentityState INSTANCE = new ClientPlayerIdentityState();
    private long session = Long.MIN_VALUE;
    private long revision;
    private Map<Integer, SyncPlayerIdentityMessage.Identity> identities = Map.of();

    private ClientPlayerIdentityState() {
    }

    public static ClientPlayerIdentityState get() {
        return INSTANCE;
    }

    public synchronized boolean replace(long incomingSession, List<SyncPlayerIdentityMessage.Identity> incoming) {
        if (incomingSession < this.session) {
            return false;
        }
        HashMap<Integer, SyncPlayerIdentityMessage.Identity> next = new HashMap<Integer, SyncPlayerIdentityMessage.Identity>();
        for (SyncPlayerIdentityMessage.Identity identity : incoming) {
            if (next.put(identity.shortId(), identity) == null) continue;
            return false;
        }
        this.session = incomingSession;
        this.identities = Map.copyOf(next);
        ++this.revision;
        return true;
    }

    public synchronized Map<UUID, SyncPlayerPositionsMessage.PlayerPosition> resolve(long frameSession, Map<Integer, SyncPlayerPositionsMessage.PlayerPosition> frame) {
        if (frameSession != this.session) {
            return null;
        }
        LinkedHashMap<UUID, SyncPlayerPositionsMessage.PlayerPosition> result = new LinkedHashMap<UUID, SyncPlayerPositionsMessage.PlayerPosition>();
        for (Map.Entry<Integer, SyncPlayerPositionsMessage.PlayerPosition> entry : frame.entrySet()) {
            SyncPlayerIdentityMessage.Identity identity = this.identities.get(entry.getKey());
            if (identity == null) continue;
            SyncPlayerPositionsMessage.PlayerPosition sample = entry.getValue();
            result.put(identity.uuid(), new SyncPlayerPositionsMessage.PlayerPosition(sample.getX(), sample.getY(), sample.getZ(), identity.name(), identity.team(), sample.getYaw(), identity.squadId(), identity.squadLeader(), identity.commander()));
        }
        return result;
    }

    public synchronized void clear() {
        this.session = Long.MIN_VALUE;
        this.identities = Map.of();
        ++this.revision;
    }

    public synchronized long revision() {
        return this.revision;
    }
}

