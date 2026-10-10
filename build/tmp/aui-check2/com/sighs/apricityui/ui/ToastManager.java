/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.ui;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.task.FrameTaskScheduler;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ToastManager {
    private static final String DOC_PATH = "devtools/toast.html";
    private static final String LIST_ID = "aui-toast-list";
    private static final long EXIT_DURATION_NS = 180000000L;
    private static final AtomicLong SEQ = new AtomicLong(1L);
    private static final Map<String, ToastRef> ACTIVE = new ConcurrentHashMap<String, ToastRef>();
    private static final String ITEM_BASE_STYLE = "pointer-events:auto;display:flex;align-items:flex-start;gap:10px;width:100%;padding:10px 12px;border:2px solid #1a1a1a;border-left:6px solid #8b5cf6;background-color:#ffffff;color:#1a1a1a;box-shadow:4px 4px 0 #1a1a1a;font-family:'Microsoft YaHei',sans-serif;font-size:12px;font-weight:600;line-height:18px;letter-spacing:0.7px;text-transform:uppercase;overflow:hidden;";
    private static final String MARKER_STYLE = "flex:0 0 18px;width:18px;height:18px;background-color:#8b5cf6;color:#ffffff;font-size:12px;font-weight:700;line-height:18px;text-align:center;";
    private static final String CONTENT_STYLE = "display:flex;flex:1;min-width:0;flex-direction:column;gap:2px;";
    private static final String LABEL_STYLE = "color:#6d28d9;font-size:10px;font-weight:700;line-height:12px;letter-spacing:1px;";
    private static final String MESSAGE_STYLE = "color:#1a1a1a;font-size:12px;font-weight:600;line-height:18px;letter-spacing:0.4px;";
    private static final String CLOSE_STYLE = "flex:0 0 14px;width:14px;color:#999999;font-size:14px;font-weight:700;line-height:14px;text-align:center;";

    private ToastManager() {
    }

    public static String show(String message) {
        return ToastManager.show(message, ToastOptions.defaults());
    }

    public static String show(String message, int durationMs) {
        return ToastManager.show(message, ToastOptions.defaults().withDurationMs(durationMs));
    }

    public static String show(String message, ToastOptions options) {
        return ToastManager.show(message, null, options);
    }

    public static String showTranslation(String translationKey) {
        return ToastManager.show(null, translationKey, ToastOptions.defaults());
    }

    private static String show(String message, String translationKey, ToastOptions options) {
        Element attachedItem;
        String content = message == null || message.isBlank() ? " " : message.trim();
        ToastOptions safe = options == null ? ToastOptions.defaults() : options.normalize();
        Overlay overlay = ToastManager.ensureOverlay();
        if (overlay == null || overlay.list() == null) {
            return "";
        }
        String id = "toast-" + SEQ.getAndIncrement();
        Element item = Element.init(overlay.document().createElement("div"));
        item.setAttribute("id", id);
        item.setClassName("aui-toast");
        item.setAttribute("style", ToastManager.buildItemStyle(safe));
        item.append(ToastManager.createPart(overlay.document(), "span", "!", ToastManager.buildMarkerStyle(safe)));
        Element contentBox = Element.init(overlay.document().createElement("div"));
        contentBox.setAttribute("style", CONTENT_STYLE);
        contentBox.append(ToastManager.createPart(overlay.document(), "span", "AUI // NOTICE", LABEL_STYLE));
        Element messagePart = translationKey == null || translationKey.isBlank() ? ToastManager.createPart(overlay.document(), "span", content, MESSAGE_STYLE) : ToastManager.createTranslationMessagePart(overlay.document(), translationKey);
        contentBox.append(messagePart);
        item.append(contentBox);
        if (safe.dismissOnClick()) {
            Element close = ToastManager.createPart(overlay.document(), "span", "x", CLOSE_STYLE);
            close.addEventListener("click", event -> ToastManager.dismiss(id));
            item.append(close);
        }
        if (safe.dismissOnClick()) {
            item.addEventListener("click", event -> ToastManager.dismiss(id));
        }
        if ((attachedItem = overlay.list().insertBefore(item, overlay.list().getFirstElementChild())) == null) {
            return "";
        }
        overlay.document().markDirty(overlay.document().body, 7);
        long expiresAtNs = safe.durationMs() <= 0 ? Long.MAX_VALUE : System.nanoTime() + (long)safe.durationMs() * 1000000L;
        ACTIVE.put(id, new ToastRef(attachedItem, expiresAtNs));
        FrameTaskScheduler.scheduleAfterFrames(1, deadlineNs -> {
            ToastRef ref = ACTIVE.get(id);
            if (ref != null && !ref.leaving) {
                ref.item.setClassName("aui-toast aui-toast-visible");
                ToastManager.markDirty(ref.item.getOwnerDocument());
            }
            return true;
        });
        return id;
    }

    public static void tick() {
        if (ACTIVE.isEmpty()) {
            return;
        }
        long now = System.nanoTime();
        for (Map.Entry<String, ToastRef> entry : ACTIVE.entrySet()) {
            ToastRef ref = entry.getValue();
            if (!ref.leaving && now >= ref.expiresAtNs) {
                ToastManager.beginDismiss(ref, now);
                continue;
            }
            if (!ref.leaving || now < ref.removeAtNs) continue;
            ToastManager.remove(entry.getKey(), ref);
        }
    }

    public static void dismiss(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        ToastRef ref = ACTIVE.get(id);
        if (ref == null) {
            return;
        }
        ToastManager.beginDismiss(ref, System.nanoTime());
    }

    public static void clear() {
        for (Map.Entry<String, ToastRef> entry : ACTIVE.entrySet()) {
            ToastManager.remove(entry.getKey(), entry.getValue());
        }
        ACTIVE.clear();
    }

    private static void beginDismiss(ToastRef ref, long now) {
        if (ref == null || ref.leaving) {
            return;
        }
        ref.leaving = true;
        ref.removeAtNs = now + 180000000L;
        ref.item.setClassName("aui-toast aui-toast-leaving");
        ToastManager.markDirty(ref.item.getOwnerDocument());
    }

    private static void remove(String id, ToastRef ref) {
        if (ref == null || !ACTIVE.remove(id, ref)) {
            return;
        }
        Element item = ref.item;
        if (item == null) {
            return;
        }
        Document owner = item.getOwnerDocument();
        item.remove();
        ToastManager.markDirty(owner);
    }

    private static Overlay ensureOverlay() {
        Document document = null;
        ArrayList<Document> docs = Document.get(DOC_PATH);
        if (!docs.isEmpty()) {
            document = docs.get(0);
        } else {
            document = Document.create(DOC_PATH);
            if (document != null) {
                document.setReloadPersistent(true);
            }
        }
        if (document == null) {
            return null;
        }
        document.setReloadPersistent(true);
        Element list = document.getElementById(LIST_ID);
        if (list == null) {
            list = document.querySelector("#aui-toast-list");
        }
        if (list == null) {
            return null;
        }
        return new Overlay(document, list);
    }

    private static String buildItemStyle(ToastOptions options) {
        StringBuilder style = new StringBuilder(ITEM_BASE_STYLE);
        if (options.backgroundColor() != null && !options.backgroundColor().isBlank()) {
            style.append("background-color:").append(options.backgroundColor().trim()).append(';');
        }
        if (options.textColor() != null && !options.textColor().isBlank()) {
            style.append("color:").append(options.textColor().trim()).append(';');
        }
        if (options.borderColor() != null && !options.borderColor().isBlank()) {
            style.append("border:1px solid ").append(options.borderColor().trim()).append(';');
        }
        if (options.customStyle() != null && !options.customStyle().isBlank()) {
            String patch = options.customStyle().trim();
            style.append(patch);
            if (!patch.endsWith(";")) {
                style.append(';');
            }
        }
        return style.toString();
    }

    private static String buildMarkerStyle(ToastOptions options) {
        String color = options.borderColor() == null || options.borderColor().isBlank() ? "#8b5cf6" : options.borderColor().trim();
        return "flex:0 0 18px;width:18px;height:18px;background-color:#8b5cf6;color:#ffffff;font-size:12px;font-weight:700;line-height:18px;text-align:center;background-color:" + color + ";";
    }

    private static Element createPart(Document document, String tagName, String text, String style) {
        Element element = Element.init(document.createElement(tagName));
        element.innerText = text;
        element.setAttribute("style", style);
        return element;
    }

    public static Element createTranslationMessagePart(Document document, String translationKey) {
        Element messagePart = ToastManager.createPart(document, "span", " ", MESSAGE_STYLE);
        Element translation = Element.init(document.createElement("TRANSLATION"));
        translation.setTextContent(translationKey == null ? "" : translationKey);
        messagePart.appendChild(translation);
        return messagePart;
    }

    private static void markDirty(Document document) {
        if (document != null && document.body != null) {
            document.markDirty(document.body, 7);
        }
    }

    public record ToastOptions(int durationMs, boolean dismissOnClick, String backgroundColor, String textColor, String borderColor, String customStyle) {
        public static ToastOptions defaults() {
            return new ToastOptions(2600, true, "", "", "", "");
        }

        public ToastOptions withDurationMs(int durationMs) {
            return new ToastOptions(durationMs, this.dismissOnClick, this.backgroundColor, this.textColor, this.borderColor, this.customStyle);
        }

        public ToastOptions normalize() {
            int safeDuration = Math.max(0, this.durationMs);
            return new ToastOptions(safeDuration, this.dismissOnClick, this.backgroundColor, this.textColor, this.borderColor, this.customStyle);
        }
    }

    private record Overlay(Document document, Element list) {
    }

    private static final class ToastRef {
        private final Element item;
        private final long expiresAtNs;
        private boolean leaving;
        private long removeAtNs;

        private ToastRef(Element item, long expiresAtNs) {
            this.item = item;
            this.expiresAtNs = expiresAtNs;
        }
    }
}

