/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.DataManager
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 */
package cc.sighs.auratip.data.trigger;

import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.network.ShowTipsPacket;
import cc.sighs.auratip.util.ResolveUtil;
import cc.sighs.oelib.data.DataManager;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class TipTriggerManager {
    private static final String SHOWN_TIPS_TAG = "auratip_shown_tips";

    private TipTriggerManager() {
    }

    public static void trigger(ResourceLocation type, ServerPlayer player, Map<String, ?> variables) {
        if (type == null) {
            return;
        }
        List<TipData> toShow = TipTriggerManager.collectTipsToShow(player, tip -> {
            TipData.Trigger trigger = tip.trigger();
            if (trigger == null) {
                return false;
            }
            ResourceLocation triggerType = trigger.type();
            return type.equals((Object)triggerType);
        });
        if (toShow.isEmpty()) {
            return;
        }
        List<ShowTipsPacket.TipEntry> payloadTips = toShow.stream().map(ShowTipsPacket.TipEntry::new).toList();
        new ShowTipsPacket(payloadTips, ResolveUtil.toComponentMap(variables)).sendTo(player);
    }

    public static void triggerById(ResourceLocation tipId, ServerPlayer player, Map<String, ?> variables) {
        if (tipId == null) {
            return;
        }
        List<TipData> toShow = TipTriggerManager.collectTipsToShow(player, tip -> tip != null && tipId.equals((Object)tip.id()));
        if (toShow.isEmpty()) {
            return;
        }
        List<ShowTipsPacket.TipEntry> payloadTips = toShow.stream().map(ShowTipsPacket.TipEntry::new).toList();
        new ShowTipsPacket(payloadTips, ResolveUtil.toComponentMap(variables)).sendTo(player);
    }

    private static List<TipData> collectTipsToShow(ServerPlayer player, Predicate<TipData> filter) {
        TipData previous;
        boolean hasRuntimeTips;
        List dataTips = DataManager.getDataList(TipData.class);
        List<TipData> runtimeTips = TipRegistry.getTips();
        boolean hasDataTips = dataTips != null && !dataTips.isEmpty();
        boolean bl = hasRuntimeTips = runtimeTips != null && !runtimeTips.isEmpty();
        if (!hasDataTips && !hasRuntimeTips) {
            return List.of();
        }
        LinkedHashMap<ResourceLocation, TipData> byId = new LinkedHashMap<ResourceLocation, TipData>();
        if (hasDataTips) {
            for (TipData tip : dataTips) {
                if (tip == null || tip.id() == null || (previous = byId.putIfAbsent(tip.id(), tip)) == null) continue;
                throw new IllegalStateException("Duplicate TipData id '" + String.valueOf(tip.id()) + "' detected in datapacks. Tip ids must be globally unique.");
            }
        }
        if (hasRuntimeTips) {
            for (TipData tip : runtimeTips) {
                if (tip == null || tip.id() == null || (previous = byId.putIfAbsent(tip.id(), tip)) == null) continue;
                throw new IllegalStateException("Duplicate TipData id '" + String.valueOf(tip.id()) + "' detected between datapacks and runtime tips. Tip ids must be globally unique.");
            }
        }
        ArrayList tips = new ArrayList(byId.values());
        CompoundTag persistent = player.getPersistentData();
        CompoundTag shown = persistent.m_128469_(SHOWN_TIPS_TAG);
        long now = player.m_9236_().m_46467_();
        ArrayList<TipData> toShow = new ArrayList<TipData>();
        boolean dirty = false;
        for (TipData tip : tips) {
            long lastShown;
            boolean once;
            TipData.Trigger trigger;
            if (!filter.test(tip) || (trigger = tip.trigger()) == null) continue;
            String id = tip.id().toString();
            TipData.Trigger.Mode mode = trigger.mode();
            boolean bl2 = once = mode == TipData.Trigger.Mode.ONCE;
            if (once && shown.m_128471_(id)) continue;
            int cooldown = trigger.cooldown();
            if (!once && cooldown > 0 && now - (lastShown = shown.m_128454_(id + "_last")) < (long)cooldown) continue;
            toShow.add(tip);
            dirty = true;
            if (once) {
                shown.m_128379_(id, true);
                continue;
            }
            if (cooldown <= 0) continue;
            shown.m_128356_(id + "_last", now);
        }
        if (dirty) {
            persistent.m_128365_(SHOWN_TIPS_TAG, (Tag)shown);
        }
        return toShow;
    }

    public static void showDirect(ServerPlayer player, List<TipData> tips, Map<String, ?> variables) {
        if (player == null || tips == null || tips.isEmpty()) {
            return;
        }
        List<ShowTipsPacket.TipEntry> payloadTips = tips.stream().map(ShowTipsPacket.TipEntry::new).toList();
        new ShowTipsPacket(payloadTips, ResolveUtil.toComponentMap(variables)).sendTo(player);
    }
}

