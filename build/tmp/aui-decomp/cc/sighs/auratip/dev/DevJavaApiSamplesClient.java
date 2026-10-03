/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.dev;

import cc.sighs.auratip.api.action.Actions;
import cc.sighs.auratip.api.radiamenu.RadialMenuBuilder;
import cc.sighs.auratip.api.radiamenu.RadialMenuRegistry;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.dev.DevJavaApiSamples;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class DevJavaApiSamplesClient {
    private static final String OWNER = "auratip_dev";

    private DevJavaApiSamplesClient() {
    }

    public static void initClient() {
        DevJavaApiSamplesClient.registerDevScriptActionHandler();
        DevJavaApiSamplesClient.registerRuntimeMenu();
    }

    private static void registerRuntimeMenu() {
        RadialMenuData menu = new RadialMenuBuilder(DevJavaApiSamples.JAVA_MENU).radii(55, 100).ringColors(List.of("#1A1A0E2A", "#D95C2B8F")).persistentSlot("ShowTip (/showtip)", new TextureIcon(new ResourceLocation("minecraft", "textures/item/paper.png"), 1.0f), Actions.runCommand("/showtip"), (Component)Component.m_237113_((String)"/showtip"), "#77FFFFFF", "#50FFD54F").slot("KJS Action (open inventory)", new TextureIcon(new ResourceLocation("minecraft", "textures/item/apple.png"), 1.0f), Actions.script(new ResourceLocation("kubejs", "open_gui"), Map.of("screen", "inventory_screen")), (Component)Component.m_237113_((String)"kubejs:open_gui"), "#77FFFFFF").slot("Java Script Action", new TextureIcon(new ResourceLocation("minecraft", "textures/item/diamond.png"), 1.0f), Actions.script(DevJavaApiSamples.JAVA_SCRIPT_ACTION, Map.of("message", "Hello from Java runtime menu")), (Component)Component.m_237113_((String)"auratip:dev_action"), "#77FFFFFF").build();
        RadialMenuRegistry.setMenus(OWNER, List.of(menu));
    }

    private static void registerDevScriptActionHandler() {
        Actions.register(DevJavaApiSamples.JAVA_SCRIPT_ACTION, params -> {
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ == null) {
                return;
            }
            String msg = params.getString("message", "");
            if (msg.isBlank()) {
                msg = "(empty message)";
            }
            mc.f_91074_.m_5661_((Component)Component.m_237113_((String)("[AuraTip Dev] " + msg)), false);
        });
    }
}

