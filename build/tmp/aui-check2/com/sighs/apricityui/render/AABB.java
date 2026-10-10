/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

public record AABB(float x, float y, float width, float height) {
    public float maxX() {
        return this.x + this.width;
    }

    public float maxY() {
        return this.y + this.height;
    }

    public AABB intersection(AABB other) {
        float newX = Math.max(this.x, other.x);
        float newY = Math.max(this.y, other.y);
        float newMaxX = Math.min(this.maxX(), other.maxX());
        float newMaxY = Math.min(this.maxY(), other.maxY());
        return new AABB(newX, newY, Math.max(0.0f, newMaxX - newX), Math.max(0.0f, newMaxY - newY));
    }

    public boolean intersects(AABB other) {
        return this.x < other.maxX() && this.maxX() > other.x && this.y < other.maxY() && this.maxY() > other.y;
    }

    public boolean isValid() {
        return this.width > 0.0f && this.height > 0.0f;
    }
}

