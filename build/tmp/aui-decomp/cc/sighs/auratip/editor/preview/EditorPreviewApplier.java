/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.editor.preview;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.client.render.TipOverlay;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.util.ComponentSerialization;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class EditorPreviewApplier {
    private static final ResourceLocation PREVIEW_ID = new ResourceLocation("auratip", "editor_preview");

    private EditorPreviewApplier() {
    }

    public static void applyDefaultPreview() {
        TipOverlay.INSTANCE.show(EditorPreviewApplier.defaultTip(), Map.of("player", Component.m_237113_((String)"Player")));
    }

    public static void closePreview() {
        Minecraft.m_91087_().execute(() -> TipOverlay.INSTANCE.closeImmediately());
    }

    public static void applyTipJson(JsonElement tipJson) {
        if (tipJson == null) {
            return;
        }
        DataResult parsed = TipData.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)tipJson);
        TipData tip = parsed.resultOrPartial(msg -> AuraTip.LOGGER.warn("Editor tip parse error: {}", msg)).orElse(null);
        if (tip == null) {
            return;
        }
        TipData preview = EditorPreviewApplier.normalizeForPreview(tip);
        Minecraft.m_91087_().execute(() -> TipOverlay.INSTANCE.show(preview, Map.of()));
    }

    public static TipData defaultTip() {
        TipData.Trigger trigger = new TipData.Trigger(new ResourceLocation("auratip", "editor"), TipData.Trigger.Mode.REPEATABLE, 0);
        TipData.VisualSettings.Background background = new TipData.VisualSettings.Background(TipData.VisualSettings.BackgroundType.GRADIENT, List.of("#FFE0F7FF", "#FFB3E5FC"), 8, true, Optional.empty(), Optional.empty());
        TipData.VisualSettings visual = new TipData.VisualSettings(new ResourceLocation("auratip", "fade_and_slide"), background, Optional.empty(), 280, 180, new TipData.Position("CENTER", 0, 0, false), 1.0f, Optional.empty(), Optional.empty(), new ResourceLocation("auratip", "none"), 1.0f, false, 4, 1.0f, TipData.AnimationParams.EMPTY, TipData.LayoutConfig.DEFAULT);
        TipData.Behavior behavior = new TipData.Behavior(-1, true, Optional.empty(), true, true, true);
        TipData.Page page = new TipData.Page(0, Optional.of(new ComponentSerialization.TextElement((Component)Component.m_237113_((String)"AuraTip Editor Preview"), 0.85f, 0, Optional.empty())), Optional.empty(), Optional.of(new ComponentSerialization.TextElement((Component)Component.m_237113_((String)"Edit in browser, preview renders here.\nPress ESC to exit editor mode."), 0.7f, 1, Optional.empty())), Optional.empty(), Optional.empty());
        return new TipData(PREVIEW_ID, trigger, visual, behavior, List.of(page));
    }

    private static TipData normalizeForPreview(TipData tip) {
        TipData.Behavior beh = tip.behavior();
        TipData.Behavior previewBehavior = new TipData.Behavior(-1, beh.pauseTimerOnHover(), beh.closableByKey(), beh.allowPaging(), beh.showCloseButton(), beh.showPageIndicator());
        return new TipData(PREVIEW_ID, tip.trigger(), tip.visualSettings(), previewBehavior, tip.pages());
    }
}

