package org.espetro.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 网络包处理的公共样板。
 *
 * <p>每个包的 {@code handle} 只需一行：</p>
 * <pre>{@code
 * // 客户端 -> 服务端：在主线程执行，发送者为空时直接丢弃
 * public void handle(Supplier<NetworkEvent.Context> ctx) {
 *     PacketContexts.handleOnServer(ctx, player -> { ... });
 * }
 *
 * // 服务端 -> 客户端：转发到 ClientPacketHandlers 的同名静态方法
 * public void handle(Supplier<NetworkEvent.Context> ctx) {
 *     PacketContexts.handleOnClient(ctx, "handleFoo", FooPacket.class, this);
 * }
 * }</pre>
 *
 * <p>客户端分发保留原先的反射方式，避免在独立服务端上加载客户端类。
 * 方法句柄会缓存，不会每个包都重新反射查找。</p>
 */
public final class PacketContexts {
    private static final String CLIENT_HANDLERS = "org.espetro.client.ClientPacketHandlers";
    private static final Map<String, MethodHandle> CLIENT_HANDLER_CACHE = new ConcurrentHashMap<>();

    private PacketContexts() {
    }

    /** 在服务端主线程执行 {@code handler}；发送者为空（非玩家来源）时忽略。 */
    public static void handleOnServer(Supplier<NetworkEvent.Context> ctx, Consumer<ServerPlayer> handler) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender != null) {
                handler.accept(sender);
            }
        });
        context.setPacketHandled(true);
    }

    /** 在客户端主线程调用 {@code ClientPacketHandlers.<handlerMethod>(packet)}。 */
    public static <P> void handleOnClient(Supplier<NetworkEvent.Context> ctx, String handlerMethod,
                                          Class<P> packetType, P packet) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> invokeClientHandler(handlerMethod, packetType, packet));
        context.setPacketHandled(true);
    }

    static <P> void invokeClientHandler(String handlerMethod, Class<P> packetType, P packet) {
        MethodHandle handle;
        try {
            handle = CLIENT_HANDLER_CACHE.computeIfAbsent(handlerMethod + '(' + packetType.getName() + ')',
                key -> lookup(handlerMethod, packetType));
        } catch (ClientHandlersUnavailable unavailable) {
            // 独立服务端上不存在客户端类，属预期情况
            Espetro.LOGGER.debug("跳过客户端包处理 {}：{}", handlerMethod, unavailable.getMessage());
            return;
        } catch (RuntimeException lookupFailed) {
            Espetro.LOGGER.error("找不到客户端包处理方法 {}({})", handlerMethod, packetType.getSimpleName(), lookupFailed);
            return;
        }
        try {
            handle.invoke(packet);
        } catch (Throwable error) {
            Espetro.LOGGER.error("处理 {} 失败", packetType.getSimpleName(), error);
        }
    }

    private static MethodHandle lookup(String handlerMethod, Class<?> packetType) {
        Class<?> handlers;
        try {
            handlers = Class.forName(CLIENT_HANDLERS);
        } catch (ClassNotFoundException | LinkageError notOnClient) {
            throw new ClientHandlersUnavailable(notOnClient.toString());
        }
        try {
            return MethodHandles.publicLookup().findStatic(handlers, handlerMethod,
                MethodType.methodType(void.class, packetType));
        } catch (ReflectiveOperationException missing) {
            throw new IllegalStateException(missing);
        }
    }

    private static final class ClientHandlersUnavailable extends RuntimeException {
        ClientHandlersUnavailable(String message) {
            super(message, null, false, false);
        }
    }
}
