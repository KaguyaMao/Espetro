/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.spi.AuiServices;
import org.joml.Matrix4f;

public final class MeshBuilder {
    private final Object impl;

    MeshBuilder(Object impl) {
        this.impl = impl;
    }

    public static MeshBuilder of(Object impl) {
        return new MeshBuilder(impl);
    }

    public Object unwrap() {
        return this.impl;
    }

    public void vertex(Matrix4f mat, float x, float y, int color) {
        this.vertex(mat, x, y, 0.0f, color, 1.0f);
    }

    public void vertex(Matrix4f mat, float x, float y, int color, float alphaMultiplier) {
        this.vertex(mat, x, y, 0.0f, color, alphaMultiplier);
    }

    public void vertex(Matrix4f mat, float x, float y, float z, int color) {
        this.vertex(mat, x, y, z, color, 1.0f);
    }

    public void vertex(Matrix4f mat, float x, float y, float z, int color, float alphaMultiplier) {
        int a = (int)((float)(color >> 24 & 0xFF) * alphaMultiplier);
        int r = color >> 16 & 0xFF;
        int g = color >> 8 & 0xFF;
        int b = color & 0xFF;
        AuiServices.render().emitVertex(this.impl, mat, x, y, z, r, g, b, a);
    }

    public void vertexUV(Matrix4f mat, float x, float y, float z, float u, float v) {
        AuiServices.render().emitVertexUV(this.impl, mat, x, y, z, u, v);
    }

    public void submit() {
        AuiServices.render().submitMesh(this.impl);
    }
}

