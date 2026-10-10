/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.NetworkManager$PacketContext
 *  dev.architectury.networking.simple.BaseS2CMessage
 *  dev.architectury.networking.simple.MessageType
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.FriendlyByteBuf
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.net.KubeJSNet;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

public class PaintMessage
extends BaseS2CMessage {
    private final CompoundTag tag;

    public PaintMessage(CompoundTag c) {
        this.tag = c;
    }

    PaintMessage(FriendlyByteBuf buffer) {
        this.tag = NBTUtils.read((FriendlyByteBuf)buffer);
    }

    public MessageType getType() {
        return KubeJSNet.PAINT;
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.m_130079_(this.tag);
    }

    public void handle(NetworkManager.PacketContext context) {
        KubeJS.PROXY.paint(this.tag);
    }
}

