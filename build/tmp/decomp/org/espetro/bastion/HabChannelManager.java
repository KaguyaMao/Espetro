/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.bastion.FortificationManager;

public final class HabChannelManager {
    private static final HabChannelManager INSTANCE = new HabChannelManager();

    private HabChannelManager() {
    }

    public static HabChannelManager getInstance() {
        return INSTANCE;
    }

    public void start(ServerPlayer player, String team) {
        String error = FortificationManager.getInstance().beginPreview(player, "builtin_hab");
        player.m_213846_(Component.m_237113_(error == null ? "\u00a7e\u5de6\u952e\u786e\u8ba4\u5175\u7ad9\u65bd\u5de5\u8303\u56f4\uff0c\u53f3\u952e\u53d6\u6d88\u3002" : error));
    }

    public void cancel(UUID playerId, @Nullable String reason) {
    }

    public void reset() {
    }

    public void tick() {
    }
}

