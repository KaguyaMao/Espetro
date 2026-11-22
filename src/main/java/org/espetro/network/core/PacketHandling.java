package org.espetro.network.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 数据包处理的统一入口，取代各个包里复制的 enqueue / 反射 / getSender 样板。
 *
 * <h2>客户端处理</h2>
 * 公共代码不能直接加载 {@code org.espetro.client} 下的类（专用服务器上不存在客户端类）。
 * 这里沿用 Forge {@code DistExecutor} 的双层 lambda 约定：外层 {@link Supplier} 只在客户端求值，
 * 内层 {@link Runnable} 的方法引用直到执行时才解析，因此服务器永远不会加载客户端类。
 *
 * <pre>{@code
 * public void handle(Supplier<NetworkEvent.Context> ctx) {
 *     PacketHandling.handleOnClient(ctx, "BattleTimer",
 *         () -> () -> ClientPacketHandlers.handleBattleTimer(this));
 * }
 * }</pre>
 *
 * <h2>服务端处理</h2>
 * <pre>{@code
 * public void handle(Supplier<NetworkEvent.Context> ctx) {
 *     PacketHandling.handleOnServer(ctx, player -> SquadManager.getInstance().joinSquad(player, id));
 * }
 * }</pre>
 *
 * 客户端处理逻辑抛出的异常统一记录日志（含包名），不会中断客户端任务队列；
 * 服务端处理逻辑的异常交由原版主线程任务队列记录，与改造前一致。
 */
public final class PacketHandling {

    private PacketHandling() {
    }

    /** S→C：在客户端主线程执行 {@code clientWork}，并标记已处理。 */
    public static void handleOnClient(Supplier<NetworkEvent.Context> ctx, String label,
                                      Supplier<Runnable> clientWork) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> runOnClient(label, clientWork));
        context.setPacketHandled(true);
    }

    /**
     * 在当前线程执行仅客户端可用的逻辑；服务器上为空操作。
     * 用于双向包的客户端分支等已经处于主线程的场景。
     */
    public static void runOnClient(String label, Supplier<Runnable> clientWork) {
        if (FMLEnvironment.dist != Dist.CLIENT) {
            return;
        }
        try {
            clientWork.get().run();
        } catch (Throwable t) {
            Espetro.LOGGER.error("处理客户端数据包失败: {}", label, t);
        }
    }

    /** C→S：在服务端主线程以发送者执行 {@code serverWork}；发送者为空时忽略。 */
    public static void handleOnServer(Supplier<NetworkEvent.Context> ctx,
                                      Consumer<ServerPlayer> serverWork) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                serverWork.accept(player);
            }
        });
        context.setPacketHandled(true);
    }
}
