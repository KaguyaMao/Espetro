/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.DataManager
 *  dev.latvian.mods.kubejs.typings.Info
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.compat.kubejs.tip;

import cc.sighs.auratip.api.client.TipClientApi;
import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.api.tip.TipServer;
import cc.sighs.auratip.compat.kubejs.tip.TipVariables;
import cc.sighs.auratip.data.TipData;
import cc.sighs.oelib.data.DataManager;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class TipTriggers {
    @Info(value="Manually trigger a Tip trigger type for a player, using the current TipVariables snapshot. The type can omit namespace (defaults to kubejs).")
    public static void trigger(String type, ServerPlayer player) {
        TipServer.trigger(TipTriggers.normalizeType(type), player, TipVariables.snapshot());
    }

    @Info(value="Manually trigger a Tip trigger type for a player, using an explicit variables map. The type can omit namespace (defaults to kubejs).")
    public static void trigger(String type, ServerPlayer player, @Nullable Map<String, ?> variables) {
        TipServer.trigger(TipTriggers.normalizeType(type), player, variables);
    }

    @Info(value="Trigger a specific tip by its id (applies ONCE/cooldown rules). Uses the current TipVariables snapshot.")
    public static void triggerById(String tipId, ServerPlayer player) {
        TipServer.triggerById(TipTriggers.normalizeTipId(tipId), player, TipVariables.snapshot());
    }

    @Info(value="Trigger a specific tip by its id (applies ONCE/cooldown rules), using an explicit variables map.")
    public static void triggerById(String tipId, ServerPlayer player, @Nullable Map<String, ?> variables) {
        TipServer.triggerById(TipTriggers.normalizeTipId(tipId), player, variables);
    }

    @Info(value="Show tips immediately to a player (bypasses trigger filtering/cooldown), using the current TipVariables snapshot.")
    public static void show(ServerPlayer player, List<TipData> tips) {
        TipServer.show(player, tips, TipVariables.snapshot());
    }

    @Info(value="Show tips immediately to a player (bypasses trigger filtering/cooldown), using an explicit variables map.")
    public static void show(ServerPlayer player, List<TipData> tips, @Nullable Map<String, ?> variables) {
        TipServer.show(player, tips, variables);
    }

    @Info(value="Show a single tip immediately to a player, using the current TipVariables snapshot.")
    public static void show(ServerPlayer player, TipData tip) {
        TipServer.show(player, tip, TipVariables.snapshot());
    }

    @Info(value="Show a single tip immediately to a player, using an explicit variables map.")
    public static void show(ServerPlayer player, TipData tip, @Nullable Map<String, ?> variables) {
        TipServer.show(player, tip, variables);
    }

    @Info(value="Find a tip by id and show it immediately (bypasses trigger filtering/cooldown). Uses the current TipVariables snapshot.")
    public static void showById(String tipId, ServerPlayer player) {
        TipData tip = TipTriggers.findTipById(TipTriggers.normalizeTipId(tipId));
        if (tip == null) {
            return;
        }
        TipServer.show(player, tip, TipVariables.snapshot());
    }

    @Info(value="Find a tip by id and show it immediately (bypasses trigger filtering/cooldown), using an explicit variables map.")
    public static void showById(String tipId, ServerPlayer player, @Nullable Map<String, ?> variables) {
        TipData tip = TipTriggers.findTipById(TipTriggers.normalizeTipId(tipId));
        if (tip == null) {
            return;
        }
        TipServer.show(player, tip, variables);
    }

    @Info(value="Client-only: enqueue tips to be shown locally (bypasses all server-side trigger rules). Uses the current TipVariables snapshot.")
    public static void enqueue(List<TipData> tips) {
        TipTriggers.enqueue(tips, TipVariables.snapshot());
    }

    @Info(value="Client-only: enqueue tips to be shown locally (bypasses all server-side trigger rules), using an explicit variables map.")
    public static void enqueue(List<TipData> tips, @Nullable Map<String, ?> variables) {
        if (tips == null || tips.isEmpty()) {
            return;
        }
        TipClientApi.enqueue(tips, variables);
    }

    @Info(value="Closes the currently displayed tip on the client, if any. Client-side only; if a queue is active, the next tip will be shown.")
    public static void close() {
        TipClientApi.close();
    }

    @Info(value="Closes the currently displayed tip for the given player, if any. Server-side; sends a packet to the client.")
    public static void close(ServerPlayer player) {
        TipServer.close(player);
    }

    private static ResourceLocation normalizeType(String type) {
        if (type == null || type.isEmpty()) {
            return new ResourceLocation("kubejs", "trigger");
        }
        if (type.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", type);
        }
        return new ResourceLocation(type);
    }

    private static ResourceLocation normalizeTipId(String id) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation("kubejs", "tip");
        }
        if (id.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", id);
        }
        return new ResourceLocation(id);
    }

    @Nullable
    private static TipData findTipById(ResourceLocation tipId) {
        List<TipData> runtimeTips;
        if (tipId == null) {
            return null;
        }
        TipData found = null;
        List dataTips = DataManager.getDataList(TipData.class);
        if (dataTips != null && !dataTips.isEmpty()) {
            for (TipData tip : dataTips) {
                if (tip == null || tip.id() == null || !tipId.equals((Object)tip.id())) continue;
                if (found != null) {
                    throw new IllegalStateException("Duplicate TipData id '" + String.valueOf(tipId) + "' detected in datapacks.");
                }
                found = tip;
            }
        }
        if ((runtimeTips = TipRegistry.getTips()) != null && !runtimeTips.isEmpty()) {
            for (TipData tip : runtimeTips) {
                if (tip == null || tip.id() == null || !tipId.equals((Object)tip.id())) continue;
                if (found != null) {
                    throw new IllegalStateException("Duplicate TipData id '" + String.valueOf(tipId) + "' detected between datapacks and runtime tips.");
                }
                found = tip;
            }
        }
        return found;
    }
}

