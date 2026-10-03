package org.espetro.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

import javax.annotation.Nullable;

/**
 * 临时保证目标区块处于已加载（FULL）状态。
 *
 * <p>背景：战场地图激活时会用 {@code TicketType.PORTAL} 预载「关键区块」（含双方主基地
 * 3×3 区块），但预载完成后立刻释放了票；而主基地弹药箱 / 载具补给站的放置被推迟到
 * 「玩家进入战场」之后，此时若玩家还在等待区/选点阶段，主基地区块早已按常规卸载，
 * 放置代码的 {@code level.hasChunkAt(pos)} 检查就会失败并跳过（且不再重试）。</p>
 *
 * <p>用法：放置前 {@link #acquire}，放置完成后 {@link #release}（可传 null）。</p>
 */
public final class ChunkTickets {

    /** 临时票半径：1 = 3×3 区块，与地图关键区块预载保持一致。 */
    private static final int TEMP_RADIUS = 1;

    private ChunkTickets() {
    }

    /**
     * 若目标位置区块尚未加载，则临时加票并同步加载到 FULL。
     *
     * @return 需要由调用方在放置完成后释放的区块坐标；区块本来就已加载时返回 {@code null}
     *         （此时无需释放）
     */
    @Nullable
    public static ChunkPos acquire(ServerLevel level, BlockPos pos) {
        if (level == null || pos == null || level.hasChunkAt(pos)) {
            return null;
        }
        ChunkPos chunk = new ChunkPos(pos);
        level.getChunkSource().addRegionTicket(
            TicketType.PORTAL, chunk, TEMP_RADIUS, chunk.getWorldPosition());
        try {
            level.getChunk(chunk.x, chunk.z);
            if (level.hasChunkAt(pos)) {
                return chunk; // 调用方负责 release
            }
        } catch (Throwable ignored) {
            // 落到下面的失败分支，统一释放票
        }
        release(level, chunk);
        return null;
    }

    /** 释放 {@link #acquire} 临时加的票；{@code chunk} 为 null 时什么都不做。 */
    public static void release(ServerLevel level, @Nullable ChunkPos chunk) {
        if (level == null || chunk == null) {
            return;
        }
        level.getChunkSource().removeRegionTicket(
            TicketType.PORTAL, chunk, TEMP_RADIUS, chunk.getWorldPosition());
    }
}
