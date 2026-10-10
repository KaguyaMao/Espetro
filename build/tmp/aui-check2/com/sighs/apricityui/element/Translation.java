/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.network.chat.Component
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.Span;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.NormalFlow;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Text;
import net.minecraft.network.chat.Component;

@ElementRegister(value="TRANSLATION")
public class Translation
extends Span {
    public static final String TAG_NAME = "TRANSLATION";

    public Translation(Document document) {
        super(document);
        this.tagName = TAG_NAME;
    }

    public String getTranslatedText() {
        return Component.m_237115_((String)super.getTextContent()).getString();
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        if (NormalFlow.isInlineTextPaintedByAncestor(this)) {
            return;
        }
        Rect rectRenderer = Rect.of(this);
        switch (phase) {
            case SHADOW: {
                rectRenderer.drawShadow(poseStack);
                break;
            }
            case BODY: {
                rectRenderer.drawBody(poseStack);
                this.drawStaticText(poseStack, rectRenderer, Text.of(this));
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }
}

