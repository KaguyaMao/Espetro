/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 */
package com.sighs.apricityui.resource.async.image;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class DecodedImage
implements AutoCloseable {
    private final NativeImage staticImage;
    private final List<NativeImage> frames;
    private final int[] frameDelaysMs;
    private final int width;
    private final int height;
    private final int hotspotX;
    private final int hotspotY;

    private DecodedImage(NativeImage staticImage, List<NativeImage> frames, int[] frameDelaysMs, int width, int height, int hotspotX, int hotspotY) {
        this.staticImage = staticImage;
        this.frames = frames;
        this.frameDelaysMs = frameDelaysMs;
        this.width = width;
        this.height = height;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    public static DecodedImage ofStatic(NativeImage image) {
        if (image == null) {
            return null;
        }
        return new DecodedImage(image, List.of(), new int[0], image.m_84982_(), image.m_85084_(), 0, 0);
    }

    public static DecodedImage ofStatic(NativeImage image, int hotspotX, int hotspotY) {
        if (image == null) {
            return null;
        }
        return new DecodedImage(image, List.of(), new int[0], image.m_84982_(), image.m_85084_(), hotspotX, hotspotY);
    }

    public static DecodedImage ofAnimated(List<NativeImage> images, List<Integer> delaysMs) {
        return DecodedImage.ofAnimated(images, delaysMs, 0, 0);
    }

    public static DecodedImage ofAnimated(List<NativeImage> images, List<Integer> delaysMs, int hotspotX, int hotspotY) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        ArrayList<NativeImage> copied = new ArrayList<NativeImage>(images.size());
        for (NativeImage frame : images) {
            if (frame == null) continue;
            copied.add(frame);
        }
        if (copied.isEmpty()) {
            return null;
        }
        int[] delays = new int[copied.size()];
        for (int i = 0; i < copied.size(); ++i) {
            int delay = 100;
            if (delaysMs != null && i < delaysMs.size() && delaysMs.get(i) != null) {
                delay = delaysMs.get(i);
            }
            delays[i] = Math.max(20, delay);
        }
        NativeImage first = (NativeImage)copied.get(0);
        return new DecodedImage(null, Collections.unmodifiableList(copied), delays, first.m_84982_(), first.m_85084_(), hotspotX, hotspotY);
    }

    public boolean isAnimated() {
        return !this.frames.isEmpty();
    }

    public NativeImage getStaticImage() {
        return this.staticImage;
    }

    public List<NativeImage> getFrames() {
        return this.frames;
    }

    public int[] getFrameDelaysMs() {
        return this.frameDelaysMs;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getHotspotX() {
        return this.hotspotX;
    }

    public int getHotspotY() {
        return this.hotspotY;
    }

    @Override
    public void close() {
        if (this.staticImage != null) {
            this.staticImage.close();
        }
        for (NativeImage frame : this.frames) {
            frame.close();
        }
    }
}

