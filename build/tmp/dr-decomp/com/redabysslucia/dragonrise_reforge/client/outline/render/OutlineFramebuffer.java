/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  org.lwjgl.opengl.GL30
 */
package com.redabysslucia.dragonrise_reforge.client.outline.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL30;

public class OutlineFramebuffer {
    public int width;
    public int height;
    private int fboId;
    private int colorTexture;
    private int depthRenderbuffer = 0;
    private int depthTexture = 0;
    private final boolean useDepthTexture;

    public OutlineFramebuffer(int width, int height) {
        this(width, height, false);
    }

    public OutlineFramebuffer(int width, int height, boolean useDepthTexture) {
        this.width = width;
        this.height = height;
        this.useDepthTexture = useDepthTexture;
        this.create();
    }

    private void create() {
        RenderSystem.assertOnRenderThreadOrInit();
        this.fboId = GlStateManager.glGenFramebuffers();
        this.colorTexture = GL30.glGenTextures();
        GlStateManager._bindTexture((int)this.colorTexture);
        GL30.glTexImage2D((int)3553, (int)0, (int)32856, (int)this.width, (int)this.height, (int)0, (int)6408, (int)5121, (long)0L);
        GL30.glTexParameteri((int)3553, (int)10241, (int)9729);
        GL30.glTexParameteri((int)3553, (int)10240, (int)9729);
        GL30.glTexParameteri((int)3553, (int)10242, (int)33071);
        GL30.glTexParameteri((int)3553, (int)10243, (int)33071);
        GlStateManager._glBindFramebuffer((int)36160, (int)this.fboId);
        GL30.glFramebufferTexture2D((int)36160, (int)36064, (int)3553, (int)this.colorTexture, (int)0);
        this.depthRenderbuffer = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer((int)36161, (int)this.depthRenderbuffer);
        GL30.glRenderbufferStorage((int)36161, (int)35056, (int)this.width, (int)this.height);
        GL30.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)this.depthRenderbuffer);
        int status = GL30.glCheckFramebufferStatus((int)36160);
        if (status != 36053) {
            throw new RuntimeException("Framebuffer is not complete: " + status);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
    }

    public void attachExternalDepth(int type, int id) {
        GlStateManager._glBindFramebuffer((int)36160, (int)this.fboId);
        GL30.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)0);
        GL30.glFramebufferTexture2D((int)36160, (int)33306, (int)3553, (int)0, (int)0);
        GL30.glFramebufferRenderbuffer((int)36160, (int)36096, (int)36161, (int)0);
        GL30.glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)0, (int)0);
        if (type == 36161) {
            GL30.glFramebufferRenderbuffer((int)36160, (int)36096, (int)36161, (int)id);
        } else if (type == 5890) {
            GL30.glFramebufferTexture2D((int)36160, (int)36096, (int)3553, (int)id, (int)0);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
    }

    public void restoreInternalDepth() {
        GlStateManager._glBindFramebuffer((int)36160, (int)this.fboId);
        if (this.depthRenderbuffer != 0) {
            GL30.glFramebufferRenderbuffer((int)36160, (int)33306, (int)36161, (int)this.depthRenderbuffer);
        }
        GlStateManager._glBindFramebuffer((int)36160, (int)0);
    }

    public void bind() {
        RenderSystem.assertOnRenderThreadOrInit();
        GlStateManager._glBindFramebuffer((int)36160, (int)this.fboId);
        GlStateManager._viewport((int)0, (int)0, (int)this.width, (int)this.height);
    }

    public void resize(int newWidth, int newHeight) {
        if (newWidth == this.width && newHeight == this.height) {
            return;
        }
        this.destroy();
        this.width = newWidth;
        this.height = newHeight;
        this.create();
    }

    public void destroy() {
        RenderSystem.assertOnRenderThreadOrInit();
        if (this.fboId != 0) {
            GlStateManager._glDeleteFramebuffers((int)this.fboId);
        }
        if (this.colorTexture != 0) {
            GlStateManager._deleteTexture((int)this.colorTexture);
        }
        if (this.depthRenderbuffer != 0) {
            GL30.glDeleteRenderbuffers((int)this.depthRenderbuffer);
        }
        if (this.depthTexture != 0) {
            GL30.glDeleteTextures((int)this.depthTexture);
        }
        this.fboId = 0;
        this.colorTexture = 0;
        this.depthRenderbuffer = 0;
        this.depthTexture = 0;
    }

    public int getColorTexture() {
        return this.colorTexture;
    }

    public int getDepthTexture() {
        return this.depthTexture;
    }

    public int getFboId() {
        return this.fboId;
    }
}

