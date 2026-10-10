/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.TextureKey;

@ElementRegister(value="TEXTURE")
public class Texture
extends Element {
    public static final String TAG_NAME = "TEXTURE";
    private String observedSrc = "";
    private TextureKey textureLocation;

    public Texture(Document document) {
        super(document, TAG_NAME);
    }

    public TextureKey getTextureLocation() {
        this.syncSource();
        return this.textureLocation;
    }

    public String getCurrentSrc() {
        TextureKey location = this.getTextureLocation();
        return location == null ? "" : location.toString();
    }

    @Override
    protected void onInitFromDom(Element origin) {
        this.syncSource();
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
        if ("src".equals(name)) {
            this.syncSource();
        }
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        if ("src".equals(name)) {
            this.syncSource();
        }
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        Rect rectRenderer = Rect.of(this);
        switch (phase) {
            case SHADOW: {
                rectRenderer.drawShadow(poseStack);
                break;
            }
            case BODY: {
                rectRenderer.drawBody(poseStack);
                this.drawTexture(poseStack, rectRenderer);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }

    private void drawTexture(PoseStack poseStack, Rect rectRenderer) {
        TextureKey location = this.getTextureLocation();
        if (location == null) {
            return;
        }
        Position position = rectRenderer.getBodyRectPosition();
        Size size = rectRenderer.getBodyRectSize();
        if (size.width() <= 0.0 || size.height() <= 0.0) {
            return;
        }
        ImageDrawer.draw(poseStack, location, (float)position.x, (float)position.y, (float)size.width(), (float)size.height(), "true".equals(this.getAttribute("blur")));
    }

    private void syncSource() {
        String src = this.getAttribute("src");
        String string = src = src == null ? "" : src.trim();
        if (src.equals(this.observedSrc)) {
            return;
        }
        this.observedSrc = src;
        this.textureLocation = AuiServices.resources().tryParseTextureKey(src);
    }
}

