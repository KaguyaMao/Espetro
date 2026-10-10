package org.espetro.client.vehicle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import org.espetro.mixin.client.ClientLevelEntityRetryAccessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 修复 MC 客户端在“区块尚未加载完成”时收到实体 spawn 包会直接丢弃实体的问题。
 * <p>开局批量生成载具/补给站时，客户端区块仍在加载，实体被 {@code ClientLevel.addEntity}
 * 丢弃后服务端不会重发 spawn 包，导致实体在客户端永久缺失（走近也不出现，重生后才恢复）。
 * 这里将被丢弃的实体暂存，等待所在区块加载完成后自动补加。
 */
public final class ClientEntityRetryQueue {

    private static final int MAX_RETRY_TICKS = 20 * 20; // 最多重试 20 秒
    private static final List<Stashed> STASH = new ArrayList<>();
    private static int retryCounter;

    private record Stashed(Entity entity, long firstTick) {
    }

    private ClientEntityRetryQueue() {
    }

    /** 由 ClientLevelEntityRetryMixin 调用：实体被 addEntity 丢弃时暂存。 */
    public static void stash(Entity entity) {
        if (entity == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        STASH.add(new Stashed(entity, mc.level.getGameTime()));
    }

    /** 每 tick 调用：尝试补加暂存实体；区块加载完成且实体未消失则重新加入世界。 */
    public static void retry(Minecraft mc) {
        if (STASH.isEmpty()) {
            return;
        }
        ClientLevel level = mc.level;
        if (level == null) {
            STASH.clear();
            return;
        }
        long now = level.getGameTime();
        retryCounter++;
        if ((retryCounter & 1) != 0) {
            return; // 每 2 tick 重试一次，避免每帧轮询
        }
        Iterator<Stashed> it = STASH.iterator();
        while (it.hasNext()) {
            Stashed stashed = it.next();
            Entity entity = stashed.entity();
            if (entity.isRemoved() || now - stashed.firstTick() > MAX_RETRY_TICKS) {
                it.remove();
                continue;
            }
            int i = Mth.floor(entity.getX() / 16.0D);
            int j = Mth.floor(entity.getZ() / 16.0D);
            ChunkAccess chunk = level.getChunk(i, j, ChunkStatus.FULL, false);
            boolean loaded = chunk instanceof LevelChunk
                && chunk.getStatus().isOrAfter(ChunkStatus.FULL);
            if (!loaded) {
                continue; // 区块仍未 FULL 加载，继续等待
            }
            it.remove();
            try {
                // 走原版 spawn 包路径补加（与 ClientLevelEntityRetryMixin 的 HEAD 检查一致，
                // 此时区块已就绪不会再被取消）。addEntity 在 ClientLevel 中是 private，
                // 通过 @Invoker 访问器调用。
                ((ClientLevelEntityRetryAccessor) level)
                    .espetro$callAddEntity(entity.getId(), entity);
                // 补加完成：暂存期间服务端可能已移动实体或更新数据（增量包因实体未注册
                // 被丢弃），主动请求服务端重发绝对位置与数据，避免载具错位/无法交互。
                org.espetro.network.NetworkManager.NET.sendToServer(
                    new org.espetro.network.EntityResyncRequestPacket(entity.getId()));
            } catch (RuntimeException ignored) {
                // 补加失败则放弃，避免异常打断客户端
            }
        }
    }
}
