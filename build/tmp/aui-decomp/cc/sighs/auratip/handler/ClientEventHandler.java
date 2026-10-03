/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.event.Subscribe
 *  cc.sighs.oelib.event.events.TickEvent$ClientTickEvent
 *  cc.sighs.oelib.event.events.TickEvent$Phase
 *  net.minecraft.client.Minecraft
 */
package cc.sighs.auratip.handler;

import cc.sighs.auratip.client.TipClient;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.client.render.TipOverlay;
import cc.sighs.auratip.handler.ClientKeyMappings;
import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.TickEvent;
import net.minecraft.client.Minecraft;

public class ClientEventHandler {
    @Subscribe
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (ClientKeyMappings.CLOSE_TIP.m_90859_()) {
            TipClient.closeCurrentTip();
        }
        Minecraft mc = Minecraft.m_91087_();
        int width = mc.m_91268_().m_85445_();
        int height = mc.m_91268_().m_85446_();
        TipOverlay.INSTANCE.tick(width, height);
        RadialMenuOverlay.INSTANCE.tick();
    }
}

