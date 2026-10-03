/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.rhino.mod.wrapper.UUIDWrapper;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlayerSelector {
    public static PlayerSelector of(Object o) {
        if (o instanceof ServerPlayer) {
            ServerPlayer sp = (ServerPlayer)o;
            return PlayerSelector.identity(sp);
        }
        if (o instanceof UUID) {
            UUID uuid = (UUID)o;
            return PlayerSelector.uuid(uuid);
        }
        String name = Objects.toString(o, "").trim().toLowerCase();
        if (name.isEmpty()) {
            return PlayerSelector.identity(null);
        }
        UUID uuid = UUIDWrapper.fromString((Object)name);
        if (uuid != null) {
            return PlayerSelector.uuid(uuid);
        }
        return PlayerSelector.name(name).or(PlayerSelector.fuzzyName(name));
    }

    @Nullable
    public ServerPlayer getPlayer(MinecraftServer var1);

    public static PlayerSelector identity(ServerPlayer player) {
        return server -> player;
    }

    public static PlayerSelector uuid(UUID uuid) {
        return server -> server.m_6846_().m_11259_(uuid);
    }

    public static PlayerSelector name(String name) {
        return server -> server.m_6846_().m_11255_(name);
    }

    public static PlayerSelector fuzzyName(String name) {
        return server -> {
            for (ServerPlayer p : server.m_6846_().m_11314_()) {
                if (!p.m_6302_().toLowerCase(Locale.ROOT).contains(name)) continue;
                return p;
            }
            return null;
        };
    }

    default public PlayerSelector or(PlayerSelector fallback) {
        return server -> {
            ServerPlayer p = this.getPlayer(server);
            return p == null ? fallback.getPlayer(server) : p;
        };
    }
}

