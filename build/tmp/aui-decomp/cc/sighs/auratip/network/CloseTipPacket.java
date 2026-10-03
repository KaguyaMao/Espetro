/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.network.api.INetworkContext
 *  cc.sighs.oelib.network.api.INetworkPacket
 *  cc.sighs.oelib.network.api.NetworkPacket
 *  cc.sighs.oelib.network.api.Side
 */
package cc.sighs.auratip.network;

import cc.sighs.auratip.client.TipClient;
import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;

@NetworkPacket(modId="auratip", id="close_tip", side=Side.CLIENT)
public record CloseTipPacket() implements INetworkPacket<CloseTipPacket>
{
    public void handle(INetworkContext context) {
        context.enqueueWork(TipClient::closeCurrentTip);
    }
}

