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
 *  net.minecraft.network.protocol.PacketFlow
 */
package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;

/*
 * Exception performing whole class analysis ignored.
 */
class Connection.1
extends ChannelInitializer<Channel> {
    Connection.1() {
    }

    protected void initChannel(Channel p_129552_) {
        try {
            p_129552_.config().setOption(ChannelOption.TCP_NODELAY, (Object)true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        ChannelPipeline channelpipeline = p_129552_.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30));
        Connection.m_264299_((ChannelPipeline)channelpipeline, (PacketFlow)PacketFlow.CLIENTBOUND);
        channelpipeline.addLast("packet_handler", (ChannelHandler)Connection.this);
    }
}
