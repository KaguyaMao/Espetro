/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.api.tip;

import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.data.trigger.TipTriggerManager;
import cc.sighs.auratip.network.CloseTipPacket;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class TipServer {
    private TipServer() {
    }

    public static void trigger(ResourceLocation type, ServerPlayer player) {
        TipTriggerManager.trigger(type, player, Map.of());
    }

    public static void trigger(ResourceLocation type, ServerPlayer player, @Nullable Map<String, ?> variables) {
        TipTriggerManager.trigger(type, player, variables);
    }

    public static void triggerById(ResourceLocation tipId, ServerPlayer player, @Nullable Map<String, ?> variables) {
        TipTriggerManager.triggerById(tipId, player, variables);
    }

    public static void show(ServerPlayer player, List<TipData> tips, @Nullable Map<String, ?> variables) {
        TipTriggerManager.showDirect(player, tips, variables);
    }

    public static void show(ServerPlayer player, TipData tip, @Nullable Map<String, ?> variables) {
        if (tip == null) {
            return;
        }
        TipServer.show(player, List.of(tip), variables);
    }

    public static void close(ServerPlayer player) {
        new CloseTipPacket().sendTo(player);
    }
}

