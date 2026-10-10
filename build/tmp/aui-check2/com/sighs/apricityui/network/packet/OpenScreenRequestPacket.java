/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.network.packet;

import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.network.api.Side;
import com.sighs.apricityui.spi.AuiServices;
import java.util.List;

@NetworkPacket(modId="apricityui", id="open_screen", side=Side.SERVER)
public record OpenScreenRequestPacket(String templatePath, List<ContainerDeclaration> containers) implements INetworkPacket<OpenScreenRequestPacket>
{
    public OpenScreenRequestPacket(String templatePath) {
        this(templatePath, List.of());
    }

    public OpenScreenRequestPacket {
        templatePath = templatePath == null ? "" : templatePath;
        containers = containers == null ? List.of() : List.copyOf(containers);
    }

    @Override
    public void handle(INetworkContext context) {
        if (context.sender() != null) {
            AuiServices.network().openScreen(context.sender(), this.templatePath, this.containers);
        }
    }
}

