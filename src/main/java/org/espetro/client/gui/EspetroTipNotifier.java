package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.esradial.client.toast.EsToasts;
import org.esradial.client.toast.ToastSpec;
import org.espetro.Espetro;

/**
 * 右侧短提示（EsRadial {@link EsToasts}）。
 * <p>
 * 停靠右侧居中、自屏幕右缘滑入；打开部署等主 GUI 时 EsToasts 会自动绘制在 Screen 之上，
 * 因此拒绝原因始终可见。提示渲染失败时降级为聊天栏消息，保证不崩溃。
 */
public final class EspetroTipNotifier {

    private static final long DEBOUNCE_MS = 1500L;
    /** 停留时长（tick，20 tick ≈ 1s）。 */
    private static final int TIP_DURATION_TICKS = 50; // ≈ 2.5s

    private static String lastKey = "";
    private static long lastShownAt;

    private EspetroTipNotifier() {
    }

    public static void showDenial(String title, String body) {
        show(title, body, true, "denial:" + title + ":" + body);
    }

    public static void showInfo(String title, String body) {
        show(title, body, false, "info:" + title + ":" + body);
    }

    public static void showRaw(String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        // 去掉格式码用于正文
        String stripped = message.replaceAll("(?i)§[0-9A-FK-OR]", "");
        show("无法操作", stripped, true, "raw:" + message);
    }

    private static void show(String title, String body, boolean error, String debounceKey) {
        long now = System.currentTimeMillis();
        if (debounceKey.equals(lastKey) && now - lastShownAt < DEBOUNCE_MS) {
            return;
        }
        lastKey = debounceKey;
        lastShownAt = now;

        String safeTitle = title == null ? "" : title;
        String safeBody = body == null ? "" : body;
        try {
            Component titleText = Component.literal(safeTitle);
            Component bodyText = Component.literal(safeBody);
            ToastSpec spec = error ? ToastSpec.error(titleText, bodyText) : ToastSpec.info(titleText, bodyText);
            EsToasts.show(spec.withHoldTicks(TIP_DURATION_TICKS));
        } catch (Throwable t) {
            Espetro.LOGGER.debug("EsRadial 提示失败，降级聊天: {}", t.toString());
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String line = "§" + (error ? "c" : "e") + safeTitle + "§r\n§f" + safeBody;
                mc.player.displayClientMessage(Component.literal(line), false);
            }
        }
    }
}
