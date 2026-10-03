/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 */
package cc.sighs.auratip.client;

import cc.sighs.auratip.client.render.TipOverlay;
import cc.sighs.auratip.data.TipData;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;

public class TipClient {
    private static final Deque<QueuedTip> QUEUE = new ArrayDeque<QueuedTip>();

    public static void enqueueTips(List<TipData> tips, Map<String, Component> variables) {
        if (tips == null || tips.isEmpty()) {
            return;
        }
        HashMap<String, Component> vars = variables == null ? Map.of() : new HashMap<String, Component>(variables);
        for (TipData tip : tips) {
            QUEUE.addLast(new QueuedTip(tip, vars));
        }
        TipClient.showNextIfIdle();
    }

    public static void onTipClosed() {
        TipClient.showNextIfIdle();
    }

    public static void closeCurrentTip() {
        if (TipOverlay.INSTANCE.isActive()) {
            TipOverlay.INSTANCE.requestClose();
        }
    }

    static void showNextIfIdle() {
        if (TipOverlay.INSTANCE.isActive()) {
            return;
        }
        QueuedTip next = QUEUE.pollFirst();
        if (next != null) {
            TipOverlay.INSTANCE.show(next.tip, next.variables);
        }
    }

    private record QueuedTip(TipData tip, Map<String, Component> variables) {
    }
}

