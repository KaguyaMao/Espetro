/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.network.api.INetworkContext
 *  cc.sighs.oelib.network.api.INetworkPacket
 *  cc.sighs.oelib.network.api.NetworkPacket
 *  cc.sighs.oelib.network.api.Side
 *  net.minecraft.network.chat.Component
 */
package cc.sighs.auratip.network;

import cc.sighs.auratip.client.TipClient;
import cc.sighs.auratip.data.TipData;
import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;
import java.util.List;
import java.util.Map;
import net.minecraft.network.chat.Component;

@NetworkPacket(modId="auratip", id="show_tips", side=Side.CLIENT, chunkThreshold=8192)
public record ShowTipsPacket(List<TipEntry> tips, Map<String, Component> variables) implements INetworkPacket<ShowTipsPacket>
{
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> {
            List<TipData> resolvedTips = this.tips == null ? List.of() : this.tips.stream().map(TipEntry::tip).toList();
            TipClient.enqueueTips(resolvedTips, this.variables);
        });
    }

    public record TipEntry(TipData tip) {
    }
}

