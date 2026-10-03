/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class RegisterTextureAtlasSpriteLoadersEvent
extends Event
implements IModBusEvent {
    private final Map<ResourceLocation, ITextureAtlasSpriteLoader> loaders;

    @ApiStatus.Internal
    public RegisterTextureAtlasSpriteLoadersEvent(Map<ResourceLocation, ITextureAtlasSpriteLoader> loaders) {
        this.loaders = loaders;
    }

    public void register(String name, ITextureAtlasSpriteLoader loader) {
        ResourceLocation key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
        Preconditions.checkArgument((!this.loaders.containsKey(key) ? 1 : 0) != 0, (Object)("Sprite loader already registered: " + String.valueOf(key)));
        this.loaders.put(key, loader);
    }
}

