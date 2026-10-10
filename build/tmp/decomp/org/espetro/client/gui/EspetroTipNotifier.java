/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 */
package org.espetro.client.gui;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;

public final class EspetroTipNotifier {
    private static final long DEBOUNCE_MS = 1500L;
    private static final int TIP_DURATION_TICKS = 50;
    private static final int TIP_WIDTH = 248;
    private static final int TIP_HEIGHT = 88;
    private static final String POSITION_PRESET = "RIGHT_CENTER";
    private static final ResourceLocation SLIDE_FROM_RIGHT = ResourceLocation.fromNamespaceAndPath((String)"auratip", (String)"slide_in_left");
    private static String lastKey = "";
    private static long lastShownAt;

    private EspetroTipNotifier() {
    }

    public static void showDenial(String title, String body) {
        EspetroTipNotifier.show(title, body, true, "denial:" + title + ":" + body);
    }

    public static void showInfo(String title, String body) {
        EspetroTipNotifier.show(title, body, false, "info:" + title + ":" + body);
    }

    public static void showRaw(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        String plain = message.replace('\u00a7', '&');
        String stripped = message.replaceAll("(?i)\u00a7[0-9A-FK-OR]", "");
        EspetroTipNotifier.show("\u65e0\u6cd5\u64cd\u4f5c", stripped, true, "raw:" + plain);
    }

    private static void show(String title, String body, boolean error, String debounceKey) {
        long now = System.currentTimeMillis();
        if (debounceKey.equals(lastKey) && now - lastShownAt < 1500L) {
            return;
        }
        lastKey = debounceKey;
        lastShownAt = now;
        if (EspetroTipNotifier.tryAuraTip(title, body, error)) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            String line = "\u00a7" + (error ? "c" : "e") + title + "\u00a7r\n\u00a7f" + body;
            mc.f_91074_.m_5661_(Component.m_237113_(line), false);
        }
    }

    private static boolean tryAuraTip(String title, String body, boolean error) {
        if (!ModList.get().isLoaded("auratip")) {
            return false;
        }
        try {
            Class<?> tipClientApi = Class.forName("cc.sighs.auratip.api.client.TipClientApi");
            Class<?> tipBuilderClz = Class.forName("cc.sighs.auratip.api.tip.TipBuilder");
            Class<?> bgTypeClz = Class.forName("cc.sighs.auratip.data.TipData$VisualSettings$BackgroundType");
            Object builder = tipBuilderClz.getConstructor(ResourceLocation.class).newInstance(ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("denial_" + (System.nanoTime() & 0xFFFFL))));
            Consumer<Object> visualConsumer = visual -> {
                try {
                    visual.getClass().getMethod("size", Integer.TYPE, Integer.TYPE).invoke(visual, 248, 88);
                    visual.getClass().getMethod("positionPreset", String.class).invoke(visual, POSITION_PRESET);
                    try {
                        visual.getClass().getMethod("animationStyle", ResourceLocation.class).invoke(visual, SLIDE_FROM_RIGHT);
                    }
                    catch (ReflectiveOperationException reflectiveOperationException) {
                        // empty catch block
                    }
                    try {
                        visual.getClass().getMethod("animationSpeed", Float.TYPE).invoke(visual, Float.valueOf(1.15f));
                    }
                    catch (ReflectiveOperationException reflectiveOperationException) {
                        // empty catch block
                    }
                    try {
                        visual.getClass().getMethod("animParam", String.class, Object.class).invoke(visual, "extra_padding", Float.valueOf(48.0f));
                    }
                    catch (ReflectiveOperationException ignored) {
                        try {
                            visual.getClass().getMethod("animParams", Map.class).invoke(visual, Map.of("extra_padding", Float.valueOf(48.0f)));
                        }
                        catch (ReflectiveOperationException reflectiveOperationException) {
                            // empty catch block
                        }
                    }
                    visual.getClass().getMethod("themeColor", String.class).invoke(visual, error ? "#FFE05A5A" : "#FFE0B85A");
                    visual.getClass().getMethod("stripeWidth", Integer.TYPE).invoke(visual, 3);
                    Enum solid = Enum.valueOf(bgTypeClz.asSubclass(Enum.class), "SOLID");
                    visual.getClass().getMethod("background", bgTypeClz, List.class, Integer.TYPE).invoke(visual, solid, List.of("#EE111416"), 0);
                    visual.getClass().getMethod("backgroundRounded", Boolean.TYPE).invoke(visual, false);
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
            tipBuilderClz.getMethod("visual", Consumer.class).invoke(builder, visualConsumer);
            Consumer<Object> behaviorConsumer = behavior -> {
                try {
                    behavior.getClass().getMethod("duration", Integer.TYPE).invoke(behavior, 50);
                    try {
                        behavior.getClass().getMethod("pauseOnHover", Boolean.TYPE).invoke(behavior, true);
                    }
                    catch (ReflectiveOperationException reflectiveOperationException) {}
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
            tipBuilderClz.getMethod("behavior", Consumer.class).invoke(builder, behaviorConsumer);
            Consumer<Object> pageConsumer = page -> {
                try {
                    page.getClass().getMethod("title", Component.class).invoke(page, Component.m_237113_(title == null ? "" : title));
                    page.getClass().getMethod("content", Component.class).invoke(page, Component.m_237113_(body == null ? "" : body));
                }
                catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            };
            tipBuilderClz.getMethod("page", Integer.TYPE, Consumer.class).invoke(builder, 0, pageConsumer);
            Object tip = tipBuilderClz.getMethod("build", new Class[0]).invoke(builder, new Object[0]);
            try {
                tipClientApi.getMethod("close", new Class[0]).invoke(null, new Object[0]);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
            tipClientApi.getMethod("enqueue", List.class, Map.class).invoke(null, List.of(tip), Map.of());
            return true;
        }
        catch (Throwable t) {
            Espetro.LOGGER.debug("AuraTip \u63d0\u793a\u5931\u8d25\uff0c\u964d\u7ea7\u804a\u5929: {}", (Object)t.toString());
            return false;
        }
    }
}

