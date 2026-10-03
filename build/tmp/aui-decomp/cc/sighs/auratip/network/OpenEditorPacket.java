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

import cc.sighs.auratip.editor.client.EditorClient;
import cc.sighs.oelib.network.api.INetworkContext;
import cc.sighs.oelib.network.api.INetworkPacket;
import cc.sighs.oelib.network.api.NetworkPacket;
import cc.sighs.oelib.network.api.Side;

@NetworkPacket(modId="auratip", id="open_editor", side=Side.CLIENT)
public record OpenEditorPacket(String mode) implements INetworkPacket<OpenEditorPacket>
{
    public void handle(INetworkContext context) {
        context.enqueueWork(() -> EditorClient.open(this.mode));
    }
}

