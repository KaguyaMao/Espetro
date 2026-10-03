/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.level.chunk.ChunkStatus
 */
package net.minecraftforge.server.command;

import java.util.ArrayDeque;
import java.util.Queue;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraftforge.common.WorldWorkerManager;

public class ChunkGenWorker
implements WorldWorkerManager.IWorker {
    private final CommandSourceStack listener;
    protected final BlockPos start;
    protected final int total;
    private final ServerLevel dim;
    private final Queue<BlockPos> queue;
    private final int notificationFrequency;
    private int lastNotification = 0;
    private long lastNotifcationTime = 0L;
    private int genned = 0;
    private Boolean keepingLoaded;

    public ChunkGenWorker(CommandSourceStack listener, BlockPos start, int total, ServerLevel dim, int interval) {
        this.listener = listener;
        this.start = start;
        this.total = total;
        this.dim = dim;
        this.queue = this.buildQueue();
        this.notificationFrequency = interval != -1 ? interval : Math.max(total / 20, 100);
        this.lastNotifcationTime = System.currentTimeMillis();
    }

    protected Queue<BlockPos> buildQueue() {
        ArrayDeque<BlockPos> ret = new ArrayDeque<BlockPos>();
        ret.add(this.start);
        int radius = 1;
        while (ret.size() < this.total) {
            int q;
            for (q = -radius + 1; q <= radius && ret.size() < this.total; ++q) {
                ret.add(this.start.m_7918_(radius, 0, q));
            }
            for (q = radius - 1; q >= -radius && ret.size() < this.total; --q) {
                ret.add(this.start.m_7918_(q, 0, radius));
            }
            for (q = radius - 1; q >= -radius && ret.size() < this.total; --q) {
                ret.add(this.start.m_7918_(-radius, 0, q));
            }
            for (q = -radius + 1; q <= radius && ret.size() < this.total; ++q) {
                ret.add(this.start.m_7918_(q, 0, -radius));
            }
            ++radius;
        }
        return ret;
    }

    public MutableComponent getStartMessage(CommandSourceStack sender) {
        return Component.m_237110_((String)"commands.forge.gen.start", (Object[])new Object[]{this.total, this.start.m_123341_(), this.start.m_123343_(), this.dim});
    }

    @Override
    public boolean hasWork() {
        return this.queue.size() > 0;
    }

    @Override
    public boolean doWork() {
        BlockPos next = this.queue.poll();
        if (next != null) {
            ChunkAccess chunk;
            int z;
            int x;
            if (++this.lastNotification >= this.notificationFrequency || this.lastNotifcationTime < System.currentTimeMillis() - 60000L) {
                this.listener.m_288197_(() -> Component.m_237110_((String)"commands.forge.gen.progress", (Object[])new Object[]{this.total - this.queue.size(), this.total}), true);
                this.lastNotification = 0;
                this.lastNotifcationTime = System.currentTimeMillis();
            }
            if (!this.dim.m_7232_(x = next.m_123341_(), z = next.m_123343_()) && !(chunk = this.dim.m_6522_(x, z, ChunkStatus.f_62314_, true)).m_6415_().m_62427_(ChunkStatus.f_62326_)) {
                chunk = this.dim.m_46819_(x, z, ChunkStatus.f_62326_);
                ++this.genned;
            }
        }
        if (this.queue.size() == 0) {
            this.listener.m_288197_(() -> Component.m_237110_((String)"commands.forge.gen.complete", (Object[])new Object[]{this.genned, this.total, this.dim.m_46472_().m_135782_()}), true);
            return false;
        }
        return true;
    }
}

