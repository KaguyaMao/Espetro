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
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;

@ElementRegister(value="DIV")
public class Div
extends Element {
    public static final String TAG_NAME = "DIV";

    public Div(Document document) {
        super(document, TAG_NAME);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        super.drawPhase(poseStack, phase);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.children.size() + ")";
    }
}

