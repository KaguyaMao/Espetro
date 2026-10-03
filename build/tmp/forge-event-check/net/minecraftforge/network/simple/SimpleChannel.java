/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  net.minecraft.client.Minecraft
 *  net.minecraft.network.Connection
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  org.apache.commons.lang3.tuple.Pair
 */
package net.minecraftforge.network.simple;

import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkInstance;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.IndexedMessageCodec;
import org.apache.commons.lang3.tuple.Pair;

public class SimpleChannel {
    private final NetworkInstance instance;
    private final IndexedMessageCodec indexedCodec;
    private final Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeConsumer;
    private List<Function<Boolean, ? extends List<? extends Pair<String, ?>>>> loginPackets;
    private Map<Class<?>, Boolean> packetsNeedResponse;

    public SimpleChannel(NetworkInstance instance) {
        this(instance, Optional.empty());
    }

    private SimpleChannel(NetworkInstance instance, Optional<Consumer<NetworkEvent.ChannelRegistrationChangeEvent>> registryChangeNotify) {
        this.instance = instance;
        this.indexedCodec = new IndexedMessageCodec(instance);
        this.loginPackets = new ArrayList();
        this.packetsNeedResponse = new HashMap();
        instance.addListener(this::networkEventListener);
        instance.addGatherListener(this::networkLoginGather);
        this.registryChangeConsumer = registryChangeNotify;
    }

    public SimpleChannel(NetworkInstance instance, Consumer<NetworkEvent.ChannelRegistrationChangeEvent> registryChangeNotify) {
        this(instance, Optional.of(registryChangeNotify));
    }

    private void networkLoginGather(NetworkEvent.GatherLoginPayloadsEvent gatherEvent) {
        this.loginPackets.forEach(packetGenerator -> ((List)packetGenerator.apply(gatherEvent.isLocal())).forEach(p -> {
            FriendlyByteBuf pb = new FriendlyByteBuf(Unpooled.buffer());
            this.indexedCodec.build(p.getRight(), pb);
            gatherEvent.add(pb, this.instance.getChannelName(), (String)p.getLeft(), this.packetsNeedResponse.getOrDefault(p.getRight().getClass(), true));
        }));
    }

    private void networkEventListener(NetworkEvent networkEvent) {
        if (networkEvent instanceof NetworkEvent.ChannelRegistrationChangeEvent) {
            this.registryChangeConsumer.ifPresent(l -> l.accept((NetworkEvent.ChannelRegistrationChangeEvent)networkEvent));
        } else {
            this.indexedCodec.consume(networkEvent.getPayload(), networkEvent.getLoginIndex(), networkEvent.getSource());
        }
    }

    public <MSG> int encodeMessage(MSG message, FriendlyByteBuf target) {
        return this.indexedCodec.build(message, target);
    }

    public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return this.registerMessage(index, messageType, encoder, decoder, messageConsumer, Optional.empty());
    }

    public <MSG> IndexedMessageCodec.MessageHandler<MSG> registerMessage(int index, Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer, Optional<NetworkDirection> networkDirection) {
        return this.indexedCodec.addCodecIndex(index, messageType, encoder, decoder, messageConsumer, networkDirection);
    }

    private <MSG> Pair<FriendlyByteBuf, Integer> toBuffer(MSG msg) {
        FriendlyByteBuf bufIn = new FriendlyByteBuf(Unpooled.buffer());
        int index = this.encodeMessage(msg, bufIn);
        return Pair.of((Object)bufIn, (Object)index);
    }

    public <MSG> void sendToServer(MSG message) {
        this.sendTo(message, Minecraft.m_91087_().m_91403_().m_104910_(), NetworkDirection.PLAY_TO_SERVER);
    }

    public <MSG> void sendTo(MSG message, Connection manager, NetworkDirection direction) {
        manager.m_129512_(this.toVanillaPacket(message, direction));
    }

    public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        target.send(this.toVanillaPacket(message, target.getDirection()));
    }

    public <MSG> Packet<?> toVanillaPacket(MSG message, NetworkDirection direction) {
        return direction.buildPacket(this.toBuffer(message), this.instance.getChannelName()).getThis();
    }

    public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context) {
        context.getPacketDispatcher().sendPacket(this.instance.getChannelName(), (FriendlyByteBuf)this.toBuffer(msgToReply).getLeft());
    }

    public boolean isRemotePresent(Connection manager) {
        return this.instance.isRemotePresent(manager);
    }

    public <M> MessageBuilder<M> messageBuilder(Class<M> type, int id) {
        return MessageBuilder.forType(this, type, id, null);
    }

    public <M> MessageBuilder<M> messageBuilder(Class<M> type, int id, NetworkDirection direction) {
        return MessageBuilder.forType(this, type, id, direction);
    }

    public static class MessageBuilder<MSG> {
        private SimpleChannel channel;
        private Class<MSG> type;
        private int id;
        private BiConsumer<MSG, FriendlyByteBuf> encoder;
        private Function<FriendlyByteBuf, MSG> decoder;
        private BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer;
        private Function<MSG, Integer> loginIndexGetter;
        private BiConsumer<MSG, Integer> loginIndexSetter;
        private Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators;
        private Optional<NetworkDirection> networkDirection;
        private boolean needsResponse = true;

        private static <MSG> MessageBuilder<MSG> forType(SimpleChannel channel, Class<MSG> type, int id, NetworkDirection networkDirection) {
            MessageBuilder<MSG> builder = new MessageBuilder<MSG>();
            builder.channel = channel;
            builder.id = id;
            builder.type = type;
            builder.networkDirection = Optional.ofNullable(networkDirection);
            return builder;
        }

        public MessageBuilder<MSG> encoder(BiConsumer<MSG, FriendlyByteBuf> encoder) {
            this.encoder = encoder;
            return this;
        }

        public MessageBuilder<MSG> decoder(Function<FriendlyByteBuf, MSG> decoder) {
            this.decoder = decoder;
            return this;
        }

        public MessageBuilder<MSG> loginIndex(Function<MSG, Integer> loginIndexGetter, BiConsumer<MSG, Integer> loginIndexSetter) {
            this.loginIndexGetter = loginIndexGetter;
            this.loginIndexSetter = loginIndexSetter;
            return this;
        }

        public MessageBuilder<MSG> buildLoginPacketList(Function<Boolean, List<Pair<String, MSG>>> loginPacketGenerators) {
            this.loginPacketGenerators = loginPacketGenerators;
            return this;
        }

        public MessageBuilder<MSG> markAsLoginPacket() {
            this.loginPacketGenerators = isLocal -> {
                try {
                    return Collections.singletonList(Pair.of((Object)this.type.getName(), this.type.newInstance()));
                }
                catch (IllegalAccessException | InstantiationException e) {
                    throw new RuntimeException("Inaccessible no-arg constructor for message " + this.type.getName(), e);
                }
            };
            return this;
        }

        public MessageBuilder<MSG> noResponse() {
            this.needsResponse = false;
            return this;
        }

        public MessageBuilder<MSG> consumerNetworkThread(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
            this.consumer = consumer;
            return this;
        }

        public MessageBuilder<MSG> consumerMainThread(BiConsumer<MSG, Supplier<NetworkEvent.Context>> consumer) {
            this.consumer = (msg, context) -> {
                NetworkEvent.Context ctx = (NetworkEvent.Context)context.get();
                ctx.enqueueWork(() -> MessageBuilder.lambda$consumerMainThread$1(consumer, msg, (Supplier)context));
                ctx.setPacketHandled(true);
            };
            return this;
        }

        public MessageBuilder<MSG> consumerNetworkThread(ToBooleanBiFunction<MSG, Supplier<NetworkEvent.Context>> handler) {
            this.consumer = (msg, ctx) -> {
                boolean handled = handler.applyAsBool((Object)msg, (Supplier<NetworkEvent.Context>)ctx);
                ((NetworkEvent.Context)ctx.get()).setPacketHandled(handled);
            };
            return this;
        }

        public void add() {
            IndexedMessageCodec.MessageHandler<MSG> message = this.channel.registerMessage(this.id, this.type, this.encoder, this.decoder, this.consumer, this.networkDirection);
            if (this.loginIndexSetter != null) {
                message.setLoginIndexSetter(this.loginIndexSetter);
            }
            if (this.loginIndexGetter != null) {
                if (!IntSupplier.class.isAssignableFrom(this.type)) {
                    throw new IllegalArgumentException("Login packet type that does not supply an index as an IntSupplier");
                }
                message.setLoginIndexGetter(this.loginIndexGetter);
            }
            if (this.loginPacketGenerators != null) {
                this.channel.loginPackets.add(this.loginPacketGenerators);
            }
            this.channel.packetsNeedResponse.put(this.type, this.needsResponse);
        }

        private static /* synthetic */ void lambda$consumerMainThread$1(BiConsumer consumer, Object msg, Supplier context) {
            consumer.accept(msg, context);
        }

        public static interface ToBooleanBiFunction<T, U> {
            public boolean applyAsBool(T var1, U var2);
        }
    }
}

