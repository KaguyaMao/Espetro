/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseS2CMessage
 *  dev.architectury.networking.simple.MessageType
 *  net.minecraft.network.FriendlyByteBuf
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.kubejs.script.ConsoleLine;
import dev.latvian.mods.kubejs.script.ScriptType;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public class DisplayServerErrorsMessage
extends BaseS2CMessage {
    private final ScriptType type;
    private final List<ConsoleLine> errors;
    private final List<ConsoleLine> warnings;

    public DisplayServerErrorsMessage(ScriptType type, List<ConsoleLine> errors, List<ConsoleLine> warnings) {
        this.type = type;
        this.errors = errors;
        this.warnings = warnings;
    }

    DisplayServerErrorsMessage(FriendlyByteBuf buf) {
        this.type = ScriptType.values()[buf.readByte()];
        this.errors = buf.m_236845_(ConsoleLine::new);
        this.warnings = buf.m_236845_(ConsoleLine::new);
    }

    public MessageType getType() {
        return KubeJSNet.DISPLAY_SERVER_ERRORS;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.type.ordinal());
        buf.m_236828_(this.errors, ConsoleLine::writeToNet);
        buf.m_236828_(this.warnings, ConsoleLine::writeToNet);
    }

    public void handle(NetworkManager.PacketContext context) {
        KubeJS.PROXY.openErrors(this.type, this.errors, this.warnings);
    }
}

