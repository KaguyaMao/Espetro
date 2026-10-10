/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelException
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.ChannelPipeline
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  net.minecraft.network.Connection
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.RateKickingConnection
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.server.network.LegacyQueryHandler
 *  net.minecraft.server.network.ServerConnectionListener
 *  net.minecraft.server.network.ServerHandshakePacketListenerImpl
 */
package net.minecraft.server.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.RateKickingConnection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.network.LegacyQueryHandler;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;

class ServerConnectionListener.1
extends ChannelInitializer<Channel> {
    ServerConnectionListener.1() {
    }

    protected void initChannel(Channel p_9729_) {
        try {
            p_9729_.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        ChannelPipeline channelpipeline = p_9729_.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(ServerConnectionListener.READ_TIMEOUT)).addLast("legacy_query", (ChannelHandler)new LegacyQueryHandler(ServerConnectionListener.this));
        Connection.m_264299_((ChannelPipeline)channelpipeline, (PacketFlow)PacketFlow.SERVERBOUND);
        int i = ServerConnectionListener.this.f_9702_.m_7032_();
        RateKickingConnection connection = i > 0 ? new RateKickingConnection(i) : new Connection(PacketFlow.SERVERBOUND);
        ServerConnectionListener.this.f_9704_.add(connection);
        channelpipeline.addLast("packet_handler", (ChannelHandler)connection);
        connection.m_129505_((PacketListener)new ServerHandshakePacketListenerImpl(ServerConnectionListener.this.f_9702_, (Connection)connection));
    }
}
