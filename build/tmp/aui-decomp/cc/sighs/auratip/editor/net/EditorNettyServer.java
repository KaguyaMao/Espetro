/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.bootstrap.ServerBootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelOption
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.SocketChannel
 *  io.netty.channel.socket.nio.NioServerSocketChannel
 */
package cc.sighs.auratip.editor.net;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.editor.net.EditorHttpHandler;
import cc.sighs.auratip.editor.net.EditorHttpRequestDecoder;
import cc.sighs.auratip.editor.net.EditorWsHub;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

public final class EditorNettyServer {
    private final AtomicBoolean running = new AtomicBoolean(false);
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private int port;
    private final EditorWsHub wsHub = new EditorWsHub();

    public boolean isRunning() {
        return this.running.get();
    }

    public int getPort() {
        return this.port;
    }

    public void start() {
        if (!this.running.compareAndSet(false, true)) {
            return;
        }
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(1);
        try {
            ServerBootstrap bootstrap = ((ServerBootstrap)new ServerBootstrap().group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).childOption(ChannelOption.TCP_NODELAY, (Object)true).childHandler((ChannelHandler)new ChannelInitializer<SocketChannel>(){

                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ChannelHandler[]{new EditorHttpRequestDecoder()}).addLast(new ChannelHandler[]{new EditorHttpHandler(EditorNettyServer.this.wsHub)});
                }
            });
            ChannelFuture bind = bootstrap.bind((SocketAddress)new InetSocketAddress("127.0.0.1", 0)).syncUninterruptibly();
            this.serverChannel = bind.channel();
            this.port = ((InetSocketAddress)this.serverChannel.localAddress()).getPort();
            AuraTip.LOGGER.info("EditorNettyServer started on 127.0.0.1:{}", (Object)this.port);
        }
        catch (Exception e) {
            AuraTip.LOGGER.error("Failed to start EditorNettyServer", (Throwable)e);
            this.stop();
        }
    }

    public void stop() {
        if (!this.running.compareAndSet(true, false)) {
            return;
        }
        try {
            if (this.serverChannel != null) {
                this.serverChannel.close().syncUninterruptibly();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.wsHub.closeAll();
        try {
            if (this.workerGroup != null) {
                this.workerGroup.shutdownGracefully().syncUninterruptibly();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            if (this.bossGroup != null) {
                this.bossGroup.shutdownGracefully().syncUninterruptibly();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.bossGroup = null;
        this.workerGroup = null;
        this.serverChannel = null;
        this.port = 0;
        AuraTip.LOGGER.info("EditorNettyServer stopped");
    }
}

