/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Queues
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.logging.LogUtils
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.DefaultEventLoopGroup
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.SimpleChannelInboundHandler
 *  io.netty.channel.epoll.Epoll
 *  io.netty.channel.epoll.EpollEventLoopGroup
 *  io.netty.channel.epoll.EpollSocketChannel
 *  io.netty.channel.local.LocalChannel
 *  io.netty.channel.local.LocalServerChannel
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.handler.timeout.TimeoutException
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.GenericFutureListener
 *  javax.annotation.Nullable
 *  net.minecraft.Util
 *  net.minecraft.network.CipherDecoder
 *  net.minecraft.network.CipherEncoder
 *  net.minecraft.network.CompressionDecoder
 *  net.minecraft.network.CompressionEncoder
 *  net.minecraft.network.Connection$PacketHolder
 *  net.minecraft.network.ConnectionProtocol
 *  net.minecraft.network.PacketBundlePacker
 *  net.minecraft.network.PacketBundleUnpacker
 *  net.minecraft.network.PacketDecoder
 *  net.minecraft.network.PacketEncoder
 *  net.minecraft.network.PacketListener
 *  net.minecraft.network.PacketSendListener
 *  net.minecraft.network.SkipPacketException
 *  net.minecraft.network.TickablePacketListener
 *  net.minecraft.network.Varint21FrameDecoder
 *  net.minecraft.network.Varint21LengthFieldPrepender
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.network.protocol.BundlerInfo
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.network.protocol.PacketFlow
 *  net.minecraft.network.protocol.game.ClientboundDisconnectPacket
 *  net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket
 *  net.minecraft.server.RunningOnDifferentThreadException
 *  net.minecraft.util.LazyLoadedValue
 *  net.minecraft.util.Mth
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.logging.LogUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.RejectedExecutionException;
import javax.annotation.Nullable;
import javax.crypto.Cipher;
import net.minecraft.Util;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketBundlePacker;
import net.minecraft.network.PacketBundleUnpacker;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.TickablePacketListener;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class Connection
extends SimpleChannelInboundHandler<Packet<?>> {
    private static final float f_178299_ = 0.75f;
    private static final Logger f_129465_ = LogUtils.getLogger();
    public static final Marker f_129459_ = MarkerFactory.getMarker((String)"NETWORK");
    public static final Marker f_129460_ = (Marker)Util.m_137469_((Object)MarkerFactory.getMarker((String)"NETWORK_PACKETS"), p_202569_ -> p_202569_.add(f_129459_));
    public static final Marker f_202554_ = (Marker)Util.m_137469_((Object)MarkerFactory.getMarker((String)"PACKET_RECEIVED"), p_202562_ -> p_202562_.add(f_129460_));
    public static final Marker f_202555_ = (Marker)Util.m_137469_((Object)MarkerFactory.getMarker((String)"PACKET_SENT"), p_202557_ -> p_202557_.add(f_129460_));
    public static final AttributeKey<ConnectionProtocol> f_129461_ = AttributeKey.valueOf((String)"protocol");
    public static final LazyLoadedValue<NioEventLoopGroup> f_129462_ = new LazyLoadedValue(() -> new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<EpollEventLoopGroup> f_129463_ = new LazyLoadedValue(() -> new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build()));
    public static final LazyLoadedValue<DefaultEventLoopGroup> f_129464_ = new LazyLoadedValue(() -> new DefaultEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build()));
    private final PacketFlow f_129466_;
    private final Queue<PacketHolder> f_129467_ = Queues.newConcurrentLinkedQueue();
    private Channel f_129468_;
    private SocketAddress f_129469_;
    private PacketListener f_129470_;
    private Component f_129471_;
    private boolean f_129472_;
    private boolean f_129473_;
    private int f_129474_;
    private int f_129475_;
    private float f_129476_;
    private float f_129477_;
    private int f_129478_;
    private boolean f_129479_;
    @Nullable
    private volatile Component f_290021_;

    public Connection(PacketFlow p_129482_) {
        this.f_129466_ = p_129482_;
    }

    public void channelActive(ChannelHandlerContext p_129525_) throws Exception {
        super.channelActive(p_129525_);
        this.f_129468_ = p_129525_.channel();
        this.f_129469_ = this.f_129468_.remoteAddress();
        try {
            this.m_129498_(ConnectionProtocol.HANDSHAKING);
        }
        catch (Throwable $$1) {
            f_129465_.error(LogUtils.FATAL_MARKER, "Failed to change protocol to handshake", $$1);
        }
        if (this.f_290021_ != null) {
            this.m_129507_(this.f_290021_);
        }
    }

    public void m_129498_(ConnectionProtocol p_129499_) {
        this.f_129468_.attr(f_129461_).set((Object)p_129499_);
        this.f_129468_.attr(BundlerInfo.f_263730_).set((Object)p_129499_);
        this.f_129468_.config().setAutoRead(true);
        f_129465_.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext p_129527_) {
        this.m_129507_((Component)Component.m_237115_((String)"disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext p_129533_, Throwable p_129534_) {
        if (p_129534_ instanceof SkipPacketException) {
            f_129465_.debug("Skipping packet due to errors", p_129534_.getCause());
            return;
        }
        boolean $$2 = !this.f_129479_;
        this.f_129479_ = true;
        if (!this.f_129468_.isOpen()) {
            return;
        }
        if (p_129534_ instanceof TimeoutException) {
            f_129465_.debug("Timeout", p_129534_);
            this.m_129507_((Component)Component.m_237115_((String)"disconnect.timeout"));
        } else {
            MutableComponent $$3 = Component.m_237110_((String)"disconnect.genericReason", (Object[])new Object[]{"Internal Exception: " + p_129534_});
            if ($$2) {
                f_129465_.debug("Failed to sent packet", p_129534_);
                ConnectionProtocol $$4 = this.m_178315_();
                ClientboundLoginDisconnectPacket $$5 = $$4 == ConnectionProtocol.LOGIN ? new ClientboundLoginDisconnectPacket((Component)$$3) : new ClientboundDisconnectPacket((Component)$$3);
                this.m_243124_((Packet<?>)$$5, PacketSendListener.m_243092_(() -> this.m_202558_((Component)$$3)));
                this.m_129540_();
            } else {
                f_129465_.debug("Double fault", p_129534_);
                this.m_129507_((Component)$$3);
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext p_129487_, Packet<?> p_129488_) {
        if (this.f_129468_.isOpen()) {
            try {
                Connection.m_129517_(p_129488_, this.f_129470_);
            }
            catch (RunningOnDifferentThreadException runningOnDifferentThreadException) {
            }
            catch (RejectedExecutionException $$2) {
                this.m_129507_((Component)Component.m_237115_((String)"multiplayer.disconnect.server_shutdown"));
            }
            catch (ClassCastException $$3) {
                f_129465_.error("Received {} that couldn't be processed", p_129488_.getClass(), (Object)$$3);
                this.m_129507_((Component)Component.m_237115_((String)"multiplayer.disconnect.invalid_packet"));
            }
            ++this.f_129474_;
        }
    }

    private static <T extends PacketListener> void m_129517_(Packet<T> p_129518_, PacketListener p_129519_) {
        p_129518_.m_5797_(p_129519_);
    }

    public void m_129505_(PacketListener p_129506_) {
        Validate.notNull((Object)p_129506_, (String)"packetListener", (Object[])new Object[0]);
        this.f_129470_ = p_129506_;
    }

    public void m_129512_(Packet<?> p_129513_) {
        this.m_243124_(p_129513_, null);
    }

    public void m_243124_(Packet<?> p_243248_, @Nullable PacketSendListener p_243316_) {
        if (this.m_129536_()) {
            this.m_129544_();
            this.m_129520_(p_243248_, p_243316_);
        } else {
            this.f_129467_.add(new PacketHolder(p_243248_, p_243316_));
        }
    }

    private void m_129520_(Packet<?> p_129521_, @Nullable PacketSendListener p_243246_) {
        ConnectionProtocol $$2 = ConnectionProtocol.m_129592_(p_129521_);
        ConnectionProtocol $$3 = this.m_178315_();
        ++this.f_129475_;
        if ($$3 != $$2) {
            if ($$2 == null) {
                throw new IllegalStateException("Encountered packet without set protocol: " + p_129521_);
            }
            f_129465_.debug("Disabled auto read");
            this.f_129468_.config().setAutoRead(false);
        }
        if (this.f_129468_.eventLoop().inEventLoop()) {
            this.m_243087_(p_129521_, p_243246_, $$2, $$3);
        } else {
            this.f_129468_.eventLoop().execute(() -> this.m_243087_(p_129521_, p_243246_, $$2, $$3));
        }
    }

    private void m_243087_(Packet<?> p_243260_, @Nullable PacketSendListener p_243290_, ConnectionProtocol p_243203_, ConnectionProtocol p_243307_) {
        if (p_243203_ != p_243307_) {
            this.m_129498_(p_243203_);
        }
        ChannelFuture $$4 = this.f_129468_.writeAndFlush(p_243260_);
        if (p_243290_ != null) {
            $$4.addListener(p_243167_ -> {
                if (p_243167_.isSuccess()) {
                    p_243290_.m_243096_();
                } else {
                    Packet $$2 = p_243290_.m_243103_();
                    if ($$2 != null) {
                        ChannelFuture $$3 = this.f_129468_.writeAndFlush((Object)$$2);
                        $$3.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                    }
                }
            });
        }
        $$4.addListener((GenericFutureListener)ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    private ConnectionProtocol m_178315_() {
        return (ConnectionProtocol)this.f_129468_.attr(f_129461_).get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void m_129544_() {
        if (this.f_129468_ == null || !this.f_129468_.isOpen()) {
            return;
        }
        Queue<PacketHolder> queue = this.f_129467_;
        synchronized (queue) {
            PacketHolder $$0;
            while (($$0 = this.f_129467_.poll()) != null) {
                this.m_129520_($$0.f_129558_, $$0.f_129559_);
            }
        }
    }

    public void m_129483_() {
        this.m_129544_();
        PacketListener packetListener = this.f_129470_;
        if (packetListener instanceof TickablePacketListener) {
            TickablePacketListener $$0 = (TickablePacketListener)packetListener;
            $$0.m_9933_();
        }
        if (!this.m_129536_() && !this.f_129473_) {
            this.m_129541_();
        }
        if (this.f_129468_ != null) {
            this.f_129468_.flush();
        }
        if (this.f_129478_++ % 20 == 0) {
            this.m_7073_();
        }
    }

    protected void m_7073_() {
        this.f_129477_ = Mth.m_14179_((float)0.75f, (float)this.f_129475_, (float)this.f_129477_);
        this.f_129476_ = Mth.m_14179_((float)0.75f, (float)this.f_129474_, (float)this.f_129476_);
        this.f_129475_ = 0;
        this.f_129474_ = 0;
    }

    public SocketAddress m_129523_() {
        return this.f_129469_;
    }

    public void m_129507_(Component p_129508_) {
        if (this.f_129468_ == null) {
            this.f_290021_ = p_129508_;
        }
        if (this.m_129536_()) {
            this.f_129468_.close().awaitUninterruptibly();
            this.f_129471_ = p_129508_;
        }
    }

    public boolean m_129531_() {
        return this.f_129468_ instanceof LocalChannel || this.f_129468_ instanceof LocalServerChannel;
    }

    public PacketFlow m_178313_() {
        return this.f_129466_;
    }

    public PacketFlow m_178314_() {
        return this.f_129466_.m_178539_();
    }

    public static Connection m_178300_(InetSocketAddress p_178301_, boolean p_178302_) {
        Connection $$2 = new Connection(PacketFlow.CLIENTBOUND);
        ChannelFuture $$3 = Connection.m_290025_(p_178301_, p_178302_, $$2);
        $$3.syncUninterruptibly();
        return $$2;
    }

    public static ChannelFuture m_290025_(InetSocketAddress p_290034_, boolean p_290035_, Connection p_290031_) {
        LazyLoadedValue<NioEventLoopGroup> $$6;
        Class<NioSocketChannel> $$5;
        if (Epoll.isAvailable() && p_290035_) {
            Class<EpollSocketChannel> $$3 = EpollSocketChannel.class;
            LazyLoadedValue<EpollEventLoopGroup> $$4 = f_129463_;
        } else {
            $$5 = NioSocketChannel.class;
            $$6 = f_129462_;
        }
        return ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)$$6.m_13971_())).handler((ChannelHandler)new /* Unavailable Anonymous Inner Class!! */)).channel($$5)).connect(p_290034_.getAddress(), p_290034_.getPort());
    }

    public static void m_264299_(ChannelPipeline p_265436_, PacketFlow p_265104_) {
        PacketFlow $$2 = p_265104_.m_178539_();
        p_265436_.addLast("splitter", (ChannelHandler)new Varint21FrameDecoder()).addLast("decoder", (ChannelHandler)new PacketDecoder(p_265104_)).addLast("prepender", (ChannelHandler)new Varint21LengthFieldPrepender()).addLast("encoder", (ChannelHandler)new PacketEncoder($$2)).addLast("unbundler", (ChannelHandler)new PacketBundleUnpacker($$2)).addLast("bundler", (ChannelHandler)new PacketBundlePacker(p_265104_));
    }

    public static Connection m_129493_(SocketAddress p_129494_) {
        Connection $$1 = new Connection(PacketFlow.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)f_129464_.m_13971_())).handler((ChannelHandler)new /* Unavailable Anonymous Inner Class!! */)).channel(LocalChannel.class)).connect(p_129494_).syncUninterruptibly();
        return $$1;
    }

    public void m_129495_(Cipher p_129496_, Cipher p_129497_) {
        this.f_129472_ = true;
        this.f_129468_.pipeline().addBefore("splitter", "decrypt", (ChannelHandler)new CipherDecoder(p_129496_));
        this.f_129468_.pipeline().addBefore("prepender", "encrypt", (ChannelHandler)new CipherEncoder(p_129497_));
    }

    public boolean m_129535_() {
        return this.f_129472_;
    }

    public boolean m_129536_() {
        return this.f_129468_ != null && this.f_129468_.isOpen();
    }

    public boolean m_129537_() {
        return this.f_129468_ == null;
    }

    public PacketListener m_129538_() {
        return this.f_129470_;
    }

    @Nullable
    public Component m_129539_() {
        return this.f_129471_;
    }

    public void m_129540_() {
        if (this.f_129468_ != null) {
            this.f_129468_.config().setAutoRead(false);
        }
    }

    public void m_129484_(int p_129485_, boolean p_182682_) {
        if (p_129485_ >= 0) {
            if (this.f_129468_.pipeline().get("decompress") instanceof CompressionDecoder) {
                ((CompressionDecoder)this.f_129468_.pipeline().get("decompress")).m_182677_(p_129485_, p_182682_);
            } else {
                this.f_129468_.pipeline().addBefore("decoder", "decompress", (ChannelHandler)new CompressionDecoder(p_129485_, p_182682_));
            }
            if (this.f_129468_.pipeline().get("compress") instanceof CompressionEncoder) {
                ((CompressionEncoder)this.f_129468_.pipeline().get("compress")).m_129449_(p_129485_);
            } else {
                this.f_129468_.pipeline().addBefore("encoder", "compress", (ChannelHandler)new CompressionEncoder(p_129485_));
            }
        } else {
            if (this.f_129468_.pipeline().get("decompress") instanceof CompressionDecoder) {
                this.f_129468_.pipeline().remove("decompress");
            }
            if (this.f_129468_.pipeline().get("compress") instanceof CompressionEncoder) {
                this.f_129468_.pipeline().remove("compress");
            }
        }
    }

    public void m_129541_() {
        if (this.f_129468_ == null || this.f_129468_.isOpen()) {
            return;
        }
        if (this.f_129473_) {
            f_129465_.warn("handleDisconnection() called twice");
        } else {
            this.f_129473_ = true;
            if (this.m_129539_() != null) {
                this.m_129538_().m_7026_(this.m_129539_());
            } else if (this.m_129538_() != null) {
                this.m_129538_().m_7026_((Component)Component.m_237115_((String)"multiplayer.disconnect.generic"));
            }
        }
    }

    public float m_129542_() {
        return this.f_129476_;
    }

    public float m_129543_() {
        return this.f_129477_;
    }

    protected /* synthetic */ void channelRead0(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
        this.channelRead0(channelHandlerContext, (Packet)object);
    }

    private /* synthetic */ void m_202558_(Component p_202559_) {
        this.m_129507_(p_202559_);
    }
}
