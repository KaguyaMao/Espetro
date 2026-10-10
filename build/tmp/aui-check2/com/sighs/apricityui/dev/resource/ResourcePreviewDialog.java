/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.dev.resource;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.dev.resource.ResourceFontAsset;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.render.AABB;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.ui.DialogWindow;
import com.sighs.apricityui.ui.ToastManager;
import java.util.Locale;

public final class ResourcePreviewDialog {
    private static final double MIN_WIDTH = 360.0;
    private static final double MIN_HEIGHT = 240.0;
    private static ResourcePreviewDialog active;
    private Document owner;
    private Document preview;
    private Element viewport;
    private Element imageView;
    private Element fontTextArea;
    private DialogWindow dialog;
    private String sourcePath = "";
    private String fontFamily = "";
    private boolean imagePreview;
    private boolean fontPreview;

    public void open(Document owner, Loader.StaticResourceEntry entry) {
        if (owner == null || owner.body == null || entry == null) {
            return;
        }
        String path = ResourcePreviewDialog.safe(entry.path());
        if (path.isBlank()) {
            return;
        }
        this.close();
        this.owner = owner;
        this.sourcePath = path;
        this.imagePreview = ResourcePreviewDialog.isImage(entry);
        this.fontPreview = ResourceFontAsset.isFont(entry);
        if (this.fontPreview) {
            this.fontFamily = ResourceFontAsset.familyName(entry);
            if (!ResourceFontAsset.ensureLoaded(entry)) {
                ToastManager.show("Font preview unavailable");
                this.close();
                return;
            }
        }
        Document document = this.preview = this.imagePreview || this.fontPreview ? null : Document.create(path);
        if (!this.imagePreview && !this.fontPreview && this.preview == null) {
            ToastManager.show("Preview unavailable");
            return;
        }
        if (this.preview != null) {
            this.preview.setReloadPersistent(false);
            this.preview.setManuallyRendered(true);
        }
        this.createWindow();
        active = this;
    }

    public void close() {
        if (active == this) {
            active = null;
        }
        DialogWindow current = this.dialog;
        this.dialog = null;
        if (current != null) {
            current.close();
        }
        if (this.preview != null && !this.preview.isDisposed()) {
            this.preview.remove();
        }
        this.owner = null;
        this.preview = null;
        this.viewport = null;
        this.imageView = null;
        this.fontTextArea = null;
        this.sourcePath = "";
        this.fontFamily = "";
        this.imagePreview = false;
        this.fontPreview = false;
    }

    public boolean isOpen() {
        return this.dialog != null && this.dialog.isOpen() && (this.imagePreview || this.fontPreview || this.preview != null && !this.preview.isDisposed());
    }

    public static void draw(PoseStack poseStack) {
        ResourcePreviewDialog.sweepClosed();
        if (active != null && ResourcePreviewDialog.active.owner != null && !ResourcePreviewDialog.active.owner.inWorld) {
            active.drawPreview(poseStack);
        }
    }

    public static void draw(PoseStack poseStack, Document ownerDocument) {
        ResourcePreviewDialog.sweepClosed();
        if (active != null && ResourcePreviewDialog.active.owner == ownerDocument && ownerDocument != null && !ownerDocument.inWorld) {
            active.drawPreview(poseStack);
        }
    }

    public static void drawInWorld(PoseStack poseStack, Document owner) {
        ResourcePreviewDialog.sweepClosed();
        if (active != null && ResourcePreviewDialog.active.owner == owner && owner != null && owner.inWorld) {
            active.drawPreview(poseStack);
        }
    }

    private static void sweepClosed() {
        if (active != null && !active.isOpen()) {
            active.close();
        }
    }

    private void createWindow() {
        this.openFrameworkWindow();
    }

    private void openFrameworkWindow() {
        double screenWidth = this.owner.getViewport().layoutWidth();
        double screenHeight = this.owner.getViewport().layoutHeight();
        double width = Math.min(Math.max(360.0, screenWidth * 0.8), screenWidth - 24.0);
        double height = Math.min(Math.max(240.0, screenHeight * 0.8), screenHeight - 24.0);
        DialogWindow.Options options = new DialogWindow.Options(this.sourcePath.toUpperCase(), width, height, true, "dialog-overlay show resource-preview-overlay", "dialog resource-preview-window", "dialog-header resource-preview-header", "dialog-title resource-preview-title", "dialog-close resource-preview-close", "dialog-body resource-preview-body", "dialog-title-icon", true);
        this.dialog = DialogWindow.open(this.owner, options, this::close);
        Element body = this.dialog.content();
        body.setAttribute("style", "position:relative;flex:1;min-height:0;display:flex;");
        this.viewport = this.element("DIV", "resource-preview-viewport");
        this.viewport.setAttribute("style", "position:relative;flex:1;min-height:0;background:#fff;border:1px solid var(--gray-light);overflow:hidden;");
        if (this.imagePreview) {
            this.imageView = this.element("IMG", "resource-preview-image");
            this.imageView.setAttribute("src", "/" + this.sourcePath);
            this.imageView.setAttribute("alt", this.sourcePath);
            this.imageView.setAttribute("style", "position:absolute;inset:0;width:100%;height:100%;object-fit:contain;display:block;");
            this.viewport.append(this.imageView);
        } else if (this.fontPreview) {
            this.fontTextArea = this.element("TEXTAREA", "resource-preview-font-sample");
            this.fontTextArea.setValue("\u4e2d\u6587\u5b57\u4f53\u9884\u89c8\nThe quick brown fox jumps over the lazy dog.");
            this.fontTextArea.setAttribute("spellcheck", "false");
            this.fontTextArea.setAttribute("style", "position:absolute;inset:0;width:100%;height:100%;box-sizing:border-box;resize:none;border:0;outline:none;padding:32px;background:#fff;color:#1a1a1a;font-family:'" + this.fontFamily + "',sans-serif;font-size:42px;line-height:1.5;font-weight:400;letter-spacing:0;white-space:pre-wrap;overflow:auto;");
            this.viewport.append(this.fontTextArea);
        }
        body.append(this.viewport);
        this.viewport.addEventListener("mousedown", this::forward);
        this.viewport.addEventListener("mouseup", this::forward);
        this.viewport.addEventListener("mousemove", this::forward);
        this.viewport.addEventListener("wheel", this::forward);
        this.markDirty();
    }

    private void forward(Event event) {
        MouseEvent mouse;
        block5: {
            block4: {
                if (!(event instanceof MouseEvent)) break block4;
                mouse = (MouseEvent)event;
                if (this.preview != null && !this.preview.isDisposed()) break block5;
            }
            return;
        }
        MouseEvent forwarded = mouse;
        if (this.owner != null && !this.owner.inWorld) {
            Position screenPosition = this.owner.documentToScreenPosition(new Position(mouse.clientX, mouse.clientY));
            forwarded = mouse.clone();
            forwarded.clientX = screenPosition.x;
            forwarded.clientY = screenPosition.y;
            forwarded.pageX = screenPosition.x;
            forwarded.pageY = screenPosition.y;
            forwarded.movementX = mouse.movementX * this.owner.getViewportScaleX();
            forwarded.movementY = mouse.movementY * this.owner.getViewportScaleY();
            forwarded.deltaX = mouse.deltaX * this.owner.getViewportScaleX();
            forwarded.deltaY = mouse.deltaY * this.owner.getViewportScaleY();
            forwarded.scrollDelta = mouse.scrollDelta * this.owner.getViewportScaleY();
        }
        MouseEvent.tiggerEvent(forwarded, this.preview);
        event.stopPropagation();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawPreview(PoseStack poseStack) {
        if (!this.isOpen() || this.viewport == null) {
            return;
        }
        if (this.imagePreview || this.fontPreview) {
            if (this.imageView != null) {
                this.imageView.tick();
            }
            return;
        }
        this.applyImageSource();
        this.preview.tickElements();
        AABB rect = Rect.of(this.viewport).getVisualBounds();
        if (!rect.isValid()) {
            return;
        }
        double hostScale = this.owner.inWorld ? 1.0 : (double)this.owner.getViewport().renderScale();
        float x = (float)((double)rect.x() * hostScale);
        float y = (float)((double)rect.y() * hostScale);
        float w = (float)((double)rect.width() * hostScale);
        float h = (float)((double)rect.height() * hostScale);
        double scaleX = w / (float)Math.max(1, this.preview.getViewport().layoutWidth());
        double scaleY = h / (float)Math.max(1, this.preview.getViewport().layoutHeight());
        if (!Double.isFinite(scaleX) || !Double.isFinite(scaleY) || scaleX <= 0.0 || scaleY <= 0.0) {
            return;
        }
        double contentWidth = w;
        double contentHeight = h;
        double contentX = x;
        double contentY = y;
        this.preview.setViewportTransform(scaleX, scaleY, contentX, contentY);
        poseStack.m_85836_();
        if (this.owner.inWorld) {
            float[] clipRadii = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
            Mask.pushMask(poseStack, (float)contentX, (float)contentY, (float)contentWidth, (float)contentHeight, clipRadii);
            try {
                poseStack.m_85837_(contentX, contentY, 0.0);
                poseStack.m_85841_((float)scaleX, (float)scaleY, 1.0f);
                Base.drawDocument(poseStack, this.preview);
            }
            finally {
                Mask.popMask(poseStack, (float)contentX, (float)contentY, (float)contentWidth, (float)contentHeight, clipRadii);
                poseStack.m_85849_();
            }
            return;
        }
        Mask.pushSurfaceClip(this.preview.getViewport().layoutWidth(), this.preview.getViewport().layoutHeight(), contentX, contentY, scaleX, scaleY);
        try {
            poseStack.m_85837_(contentX, contentY, 0.0);
            poseStack.m_85841_((float)scaleX, (float)scaleY, 1.0f);
            Base.drawEmbeddedDocument(poseStack, this.preview, this.owner);
        }
        finally {
            Mask.popSurfaceClip();
            poseStack.m_85849_();
        }
    }

    private Element element(String tag, String className) {
        Element element = Element.init(this.owner.createElement(tag));
        element.setAttribute("class", className);
        return element;
    }

    private void markDirty() {
        if (this.owner != null && this.owner.body != null) {
            this.owner.markDirty(this.owner.body, 7);
        }
    }

    private void applyImageSource() {
        if (!this.imagePreview || this.preview == null || this.preview.isDisposed()) {
            return;
        }
        Element image = this.preview.querySelector("#previewImage");
        if (image == null || ("/" + this.sourcePath).equals(image.getAttribute("src"))) {
            return;
        }
        image.setAttribute("src", "/" + this.sourcePath);
        this.preview.markDirty(7);
    }

    private static boolean isImage(Loader.StaticResourceEntry entry) {
        String ext = ResourcePreviewDialog.safe(entry.extension()).toLowerCase(Locale.ROOT);
        return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("bmp") || ext.equals("gif") || ext.equals("webp");
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}

