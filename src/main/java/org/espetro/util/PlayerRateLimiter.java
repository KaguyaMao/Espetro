package org.espetro.util;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * 按玩家限流，常用于“拒绝提示”这类连点会刷屏的消息。
 *
 * <p>语义与原先各处手写的 {@code Map<UUID, Long>} 限流一致：每次调用 {@link #tryAcquire(UUID)}
 * 都会记录本次时间；只有距<strong>上一次调用</strong>（不论上次是否放行）超过间隔才放行。
 * 也就是说玩家持续连点时只在第一次提示，停手超过间隔后再点才会再次提示。</p>
 *
 * <p>线程安全。玩家下线时应调用 {@link #forget(UUID)} 释放记录。</p>
 */
public final class PlayerRateLimiter {
    private final long intervalMillis;
    private final LongSupplier clock;
    private final Map<UUID, Long> lastCall = new ConcurrentHashMap<>();

    public PlayerRateLimiter(long intervalMillis) {
        this(intervalMillis, System::currentTimeMillis);
    }

    /** 可注入时钟，便于测试。 */
    public PlayerRateLimiter(long intervalMillis, LongSupplier clock) {
        if (intervalMillis < 0) throw new IllegalArgumentException("intervalMillis < 0");
        this.intervalMillis = intervalMillis;
        this.clock = clock;
    }

    /** 记录一次调用；首次调用或距上次调用已超过间隔时返回 {@code true}。 */
    public boolean tryAcquire(UUID playerId) {
        long now = clock.getAsLong();
        Long previous = lastCall.put(playerId, now);
        return previous == null || now - previous >= intervalMillis;
    }

    public void forget(@Nullable UUID playerId) {
        if (playerId != null) lastCall.remove(playerId);
    }

    public void clear() {
        lastCall.clear();
    }

    int trackedCount() {
        return lastCall.size();
    }
}
