/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class TextureStitchEvent
extends Event
implements IModBusEvent {
    private final TextureAtlas atlas;

    @ApiStatus.Internal
    public TextureStitchEvent(TextureAtlas atlas) {
        this.atlas = atlas;
    }

    public TextureAtlas getAtlas() {
        return this.atlas;
    }

    public static class Post
    extends TextureStitchEvent {
        @ApiStatus.Internal
        public Post(TextureAtlas map) {
            super(map);
        }
    }
}

