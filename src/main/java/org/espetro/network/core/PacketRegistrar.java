package org.espetro.network.core;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 按方向注册数据包。
 * <p>
 * 方向会被 Forge 校验：声明为 {@link #toClient} 的包若由客户端发往服务端会被直接丢弃，
 * 反之亦然，避免客户端伪造服务端同步包（或服务端误收客户端请求）。只有确实双向使用的包
 * 才用 {@link #bidirectional}。
 * <p>
 * 包 ID 按调用顺序分配，调整注册顺序或增删包时必须同步提升协议版本。
 */
public final class PacketRegistrar {

    private final SimpleChannel channel;
    private int nextId;

    public PacketRegistrar(SimpleChannel channel) {
        this.channel = channel;
    }

    /** 服务端 → 客户端。 */
    public <T> PacketRegistrar toClient(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder,
                                        Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        return register(type, encoder, decoder, handler, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    /** 客户端 → 服务端。 */
    public <T> PacketRegistrar toServer(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder,
                                        Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        return register(type, encoder, decoder, handler, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    /** 请求与响应共用同一个包类型（例如客户端请求、服务端回传同类型同步包）。 */
    public <T> PacketRegistrar bidirectional(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder,
                                             Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler) {
        return register(type, encoder, decoder, handler, Optional.empty());
    }

    /** 已注册的包数量。 */
    public int registeredCount() {
        return nextId;
    }

    private <T> PacketRegistrar register(Class<T> type, BiConsumer<T, FriendlyByteBuf> encoder,
                                         Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> handler,
                                         Optional<NetworkDirection> direction) {
        channel.registerMessage(nextId++, type, encoder, decoder, handler, direction);
        return this;
    }
}
