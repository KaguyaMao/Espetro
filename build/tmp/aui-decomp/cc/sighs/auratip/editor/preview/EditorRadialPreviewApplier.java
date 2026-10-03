/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.editor.preview;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.client.render.RadialMenuOverlay;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.action.Action;
import com.google.gson.JsonElement;
import com.mojang.blaze3d.platform.Window;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class EditorRadialPreviewApplier {
    private static final ResourceLocation PREVIEW_ID = new ResourceLocation("auratip", "editor_preview_menu");

    private EditorRadialPreviewApplier() {
    }

    public static RadialMenuData defaultMenu() {
        RadialMenuData.MenuSettings settings = new RadialMenuData.MenuSettings(55, 100, 1.0f, Optional.empty(), Optional.of("#CC101010"), Optional.empty(), Optional.empty());
        RadialMenuData.Slot slot = new RadialMenuData.Slot("Preview", new TextureIcon(new ResourceLocation("minecraft", "textures/item/paper.png"), 1.0f), new Action.RunCommand("/say AuraTip Editor Preview"), Optional.of(Component.m_237113_((String)"Preview")), Optional.of("#77FFFFFF"), false, Optional.of("#40FFD54F"));
        return new RadialMenuData(PREVIEW_ID, settings, List.of(slot));
    }

    public static void applyDefaultPreview() {
        Minecraft mc = Minecraft.m_91087_();
        mc.execute(() -> {
            if (mc.f_91074_ == null || mc.f_91073_ == null) {
                return;
            }
            Window window = mc.m_91268_();
            RadialMenuOverlay.INSTANCE.open(EditorRadialPreviewApplier.defaultMenu(), window.m_85445_(), window.m_85446_(), mc);
        });
    }

    public static void closePreview() {
        Minecraft.m_91087_().execute(() -> RadialMenuOverlay.INSTANCE.close());
    }

    public static void applyMenuJson(JsonElement menuJson) {
        if (menuJson == null) {
            return;
        }
        DataResult parsed = RadialMenuData.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)menuJson);
        RadialMenuData menu = parsed.resultOrPartial(msg -> AuraTip.LOGGER.warn("Editor radial menu parse error: {}", msg)).orElse(null);
        if (menu == null) {
            return;
        }
        RadialMenuData preview = new RadialMenuData(PREVIEW_ID, menu.menuSettings(), menu.slots());
        Minecraft mc = Minecraft.m_91087_();
        mc.execute(() -> {
            if (mc.f_91074_ == null || mc.f_91073_ == null) {
                return;
            }
            Window window = mc.m_91268_();
            if (RadialMenuOverlay.INSTANCE.isActive() && RadialMenuOverlay.INSTANCE.replace(preview)) {
                return;
            }
            RadialMenuOverlay.INSTANCE.open(preview, window.m_85445_(), window.m_85446_(), mc);
        });
    }
}

