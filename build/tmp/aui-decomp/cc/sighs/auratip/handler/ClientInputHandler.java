/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.event.Subscribe
 *  cc.sighs.oelib.event.events.InputEvent$Key
 *  cc.sighs.oelib.event.events.InputEvent$MouseButton$Pre
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  com.mojang.blaze3d.platform.Window
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.handler;

import cc.sighs.auratip.api.client.TipClientApi;
import cc.sighs.auratip.api.tip.TipBuilder;
import cc.sighs.auratip.client.RadialMenuClient;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.client.render.TipOverlay;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.dev.DevEnvironment;
import cc.sighs.auratip.dev.DevJavaApiSamples;
import cc.sighs.auratip.handler.ClientKeyMappings;
import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.InputEvent;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ClientInputHandler {
    @Subscribe
    public static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() != 1) {
            return;
        }
        int key = event.getKey();
        if (TipOverlay.INSTANCE.isActive() && TipOverlay.INSTANCE.keyPressed(key)) {
            return;
        }
        if (RadialMenuOverlay.INSTANCE.isActive()) {
            RadialMenuOverlay.INSTANCE.keyPressed(key);
        }
    }

    @Subscribe
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        if (event.getAction() != 1 && event.getAction() != 0) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        Window window = minecraft.m_91268_();
        int guiWidth = window.m_85445_();
        int guiHeight = window.m_85446_();
        double guiX = minecraft.f_91067_.m_91589_() * (double)guiWidth / (double)window.m_85443_();
        double guiY = minecraft.f_91067_.m_91594_() * (double)guiHeight / (double)window.m_85444_();
        if (event.getAction() == 1 && TipOverlay.INSTANCE.isActive() && TipOverlay.INSTANCE.mouseClicked(guiX, guiY, event.getButton())) {
            event.setCanceled(true);
            return;
        }
        if (RadialMenuOverlay.INSTANCE.isActive()) {
            boolean handled;
            boolean bl = handled = event.getAction() == 1 ? RadialMenuOverlay.INSTANCE.mouseClicked(guiX, guiY, event.getButton()) : RadialMenuOverlay.INSTANCE.mouseReleased(guiX, guiY, event.getButton());
            if (handled) {
                event.setCanceled(true);
            }
        }
    }

    @Subscribe
    public static void onRadialMenuKey(InputEvent.Key event) {
        if (event.getAction() != 1) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ != null) {
            return;
        }
        InputConstants.Key key = InputConstants.m_84827_((int)event.getKey(), (int)event.getScanCode());
        if (DevEnvironment.isDev()) {
            if (ClientKeyMappings.DEV_TRIGGER_SHOWTIP.isActiveAndMatches(key)) {
                if (mc.f_91074_ != null) {
                    mc.f_91074_.f_108617_.m_246623_("showtip");
                }
                return;
            }
            if (ClientKeyMappings.DEV_ENQUEUE_CLIENT_TIP.isActiveAndMatches(key)) {
                if (mc.f_91074_ != null) {
                    TipData tip = new TipBuilder(new ResourceLocation("auratip", "dev_client_enqueue")).triggerRepeatable(new ResourceLocation("auratip", "unused_trigger"), 0).visual(v -> v.animationStyle(new ResourceLocation("auratip", "fade_transition")).hoverAnimationStyle(new ResourceLocation("auratip", "none")).size(220, 55).positionPreset("TOP_LEFT")).behavior(b -> b.duration(160)).page(0, p -> p.title((Component)Component.m_237113_((String)"TipClientApi.enqueue"), 0.8f, 0).content((Component)Component.m_237113_((String)"\u8fd9\u662f\u5ba2\u6237\u7aef\u672c\u5730\u5165\u961f\uff0c\u4e0d\u8d70\u670d\u52a1\u5668\u89e6\u53d1\u89c4\u5219\u3002\n\u73a9\u5bb6: ${player}"), 0.65f, 1)).build();
                    TipClientApi.enqueue(List.of(tip), Map.of("player", mc.f_91074_.m_5446_()));
                }
                return;
            }
            if (ClientKeyMappings.DEV_OPEN_DATAPACK_MENU.isActiveAndMatches(key)) {
                RadialMenuClient.openMenu(DevJavaApiSamples.DATAPACK_MENU);
                return;
            }
            if (ClientKeyMappings.DEV_OPEN_JAVA_MENU.isActiveAndMatches(key)) {
                RadialMenuClient.openMenu(DevJavaApiSamples.JAVA_MENU);
                return;
            }
            if (ClientKeyMappings.DEV_OPEN_KJS_MENU.isActiveAndMatches(key)) {
                RadialMenuClient.openMenu(new ResourceLocation("kubejs", "demo_menu"));
            }
        }
    }
}

