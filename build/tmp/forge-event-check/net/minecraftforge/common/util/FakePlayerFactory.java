/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraftforge.common.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.util.FakePlayer;

public class FakePlayerFactory {
    private static final GameProfile MINECRAFT = new GameProfile(UUID.fromString("41C82C87-7AfB-4024-BA57-13D2C99CAE77"), "[Minecraft]");
    private static final Map<FakePlayerKey, FakePlayer> fakePlayers = Maps.newHashMap();

    public static FakePlayer getMinecraft(ServerLevel level) {
        return FakePlayerFactory.get(level, MINECRAFT);
    }

    public static FakePlayer get(ServerLevel level, GameProfile username) {
        FakePlayerKey key = new FakePlayerKey(level, username);
        return fakePlayers.computeIfAbsent(key, k -> new FakePlayer(k.level(), k.username()));
    }

    public static void unloadLevel(ServerLevel level) {
        fakePlayers.entrySet().removeIf(entry -> ((FakePlayer)((Object)((Object)entry.getValue()))).m_9236_() == level);
    }

    private record FakePlayerKey(ServerLevel level, GameProfile username) {
    }
}

