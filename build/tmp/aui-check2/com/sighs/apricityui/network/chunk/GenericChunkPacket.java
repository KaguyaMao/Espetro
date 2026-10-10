/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.network.chunk;

import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.network.api.Side;
import com.sighs.apricityui.network.chunk.GenericChunkAssembler;
import java.util.UUID;
import net.minecraft.resources.ResourceLocation;

@NetworkPacket(modId="apricityui", id="generic_chunk", side=Side.BOTH)
public record GenericChunkPacket(UUID sessionId, int totalSize, short chunkIndex, short totalChunks, ResourceLocation originalTypeId, byte[] chunkData) implements INetworkPacket<GenericChunkPacket>
{
    @Override
    public void handle(INetworkContext context) {
        GenericChunkAssembler.receiveChunk(this.sessionId, this.totalSize, this.chunkIndex, this.totalChunks, this.originalTypeId, this.chunkData, context);
    }
}

