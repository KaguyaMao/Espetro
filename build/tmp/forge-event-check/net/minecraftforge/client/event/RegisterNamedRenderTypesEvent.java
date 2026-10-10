/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public class RegisterNamedRenderTypesEvent
extends Event
implements IModBusEvent {
    private final Map<ResourceLocation, RenderTypeGroup> renderTypes;

    @ApiStatus.Internal
    public RegisterNamedRenderTypesEvent(Map<ResourceLocation, RenderTypeGroup> renderTypes) {
        this.renderTypes = renderTypes;
    }

    public void register(String name, RenderType blockRenderType, RenderType entityRenderType) {
        this.register(name, blockRenderType, entityRenderType, entityRenderType);
    }

    public void register(String name, RenderType blockRenderType, RenderType entityRenderType, RenderType fabulousEntityRenderType) {
        ResourceLocation key = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name);
        Preconditions.checkArgument((!this.renderTypes.containsKey(key) ? 1 : 0) != 0, (Object)("Render type already registered: " + String.valueOf(key)));
        Preconditions.checkArgument((blockRenderType.m_110508_() == DefaultVertexFormat.f_85811_ ? 1 : 0) != 0, (Object)"The block render type must use the BLOCK vertex format.");
        Preconditions.checkArgument((blockRenderType.getChunkLayerId() >= 0 ? 1 : 0) != 0, (Object)"Only chunk render types can be used for block rendering. Query RenderType#chunkBufferLayers() for a list.");
        Preconditions.checkArgument((entityRenderType.m_110508_() == DefaultVertexFormat.f_85812_ ? 1 : 0) != 0, (Object)"The entity render type must use the NEW_ENTITY vertex format.");
        Preconditions.checkArgument((fabulousEntityRenderType.m_110508_() == DefaultVertexFormat.f_85812_ ? 1 : 0) != 0, (Object)"The fabulous entity render type must use the NEW_ENTITY vertex format.");
        this.renderTypes.put(key, new RenderTypeGroup(blockRenderType, entityRenderType, fabulousEntityRenderType));
    }
}

