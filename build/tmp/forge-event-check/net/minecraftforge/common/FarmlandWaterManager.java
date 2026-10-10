/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.MapMaker
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.ChunkPos
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelReader
 *  net.minecraft.world.level.chunk.ChunkAccess
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.common;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.common.ticket.ChunkTicketManager;
import net.minecraftforge.common.ticket.SimpleTicket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FarmlandWaterManager {
    private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("forge.debugFarmlandWaterManager", "false"));
    private static final Map<LevelReader, Map<ChunkPos, ChunkTicketManager<Vec3>>> customWaterHandler = new WeakHashMap<LevelReader, Map<ChunkPos, ChunkTicketManager<Vec3>>>();
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends SimpleTicket<Vec3>> T addCustomTicket(Level level, T ticket, ChunkPos masterChunk, ChunkPos ... additionalChunks) {
        Preconditions.checkArgument((!level.f_46443_ ? 1 : 0) != 0, (Object)"Water region is only determined server-side");
        Map ticketMap = customWaterHandler.computeIfAbsent((LevelReader)level, id -> new MapMaker().weakValues().makeMap());
        ChunkTicketManager[] additionalTickets = new ChunkTicketManager[additionalChunks.length];
        for (int i = 0; i < additionalChunks.length; ++i) {
            additionalTickets[i] = ticketMap.computeIfAbsent(additionalChunks[i], ChunkTicketManager::new);
        }
        ticket.setManager(ticketMap.computeIfAbsent(masterChunk, ChunkTicketManager::new), additionalTickets);
        ticket.validate();
        return ticket;
    }

    public static AABBTicket addAABBTicket(Level level, AABB aabb) {
        if (DEBUG) {
            LOGGER.info("FarmlandWaterManager: New AABBTicket, aabb={}", (Object)aabb);
        }
        ChunkPos leftUp = new ChunkPos((int)aabb.f_82288_ >> 4, (int)aabb.f_82290_ >> 4);
        ChunkPos rightDown = new ChunkPos((int)aabb.f_82291_ >> 4, (int)aabb.f_82293_ >> 4);
        HashSet<ChunkPos> posSet = new HashSet<ChunkPos>();
        for (int x = leftUp.f_45578_; x <= rightDown.f_45578_; ++x) {
            for (int z = leftUp.f_45579_; z <= rightDown.f_45579_; ++z) {
                posSet.add(new ChunkPos(x, z));
            }
        }
        ChunkPos masterPos = null;
        double masterDistance = Double.MAX_VALUE;
        for (ChunkPos pos : posSet) {
            double distToCenter = FarmlandWaterManager.getDistanceSq(pos, aabb.m_82399_());
            if (!(distToCenter < masterDistance)) continue;
            if (DEBUG) {
                LOGGER.info("FarmlandWaterManager: New better pos then {}: {}, prev dist {}, new dist {}", (Object)masterPos, (Object)pos, (Object)masterDistance, (Object)distToCenter);
            }
            masterPos = pos;
            masterDistance = distToCenter;
        }
        posSet.remove(masterPos);
        if (DEBUG) {
            LOGGER.info("FarmlandWaterManager: {} center pos, {} dummy posses. Dist to center {}", masterPos, (Object)posSet.toArray(new ChunkPos[0]), (Object)masterDistance);
        }
        return FarmlandWaterManager.addCustomTicket(level, new AABBTicket(aabb), masterPos, posSet.toArray(new ChunkPos[0]));
    }

    private static double getDistanceSq(ChunkPos pos, Vec3 vec3d) {
        double d0 = pos.f_45578_ * 16 + 8;
        double d1 = pos.f_45579_ * 16 + 8;
        double d2 = d0 - vec3d.f_82479_;
        double d3 = d1 - vec3d.f_82481_;
        return d2 * d2 + d3 * d3;
    }

    public static boolean hasBlockWaterTicket(LevelReader level, BlockPos pos) {
        ChunkTicketManager<Vec3> ticketManager = FarmlandWaterManager.getTicketManager(new ChunkPos(pos.m_123341_() >> 4, pos.m_123343_() >> 4), level);
        if (ticketManager != null) {
            Vec3 posAsVec3d = new Vec3((double)pos.m_123341_() + 0.5, (double)pos.m_123342_() + 0.5, (double)pos.m_123343_() + 0.5);
            for (SimpleTicket<Vec3> ticket : ticketManager.getTickets()) {
                if (!ticket.matches(posAsVec3d)) continue;
                return true;
            }
        }
        return false;
    }

    static void removeTickets(ChunkAccess chunk) {
        ChunkTicketManager<Vec3> ticketManager = FarmlandWaterManager.getTicketManager(chunk.m_7697_(), (LevelReader)chunk.getWorldForge());
        if (ticketManager != null) {
            if (DEBUG) {
                LOGGER.info("FarmlandWaterManager: got tickets {} at {} before", (Object)ticketManager.getTickets().size(), (Object)ticketManager.pos);
            }
            ticketManager.getTickets().removeIf(next -> next.unload(ticketManager));
            if (DEBUG) {
                LOGGER.info("FarmlandWaterManager: got tickets {} at {} after", (Object)ticketManager.getTickets().size(), (Object)ticketManager.pos);
            }
        }
    }

    private static ChunkTicketManager<Vec3> getTicketManager(ChunkPos pos, LevelReader level) {
        Preconditions.checkArgument((!level.m_5776_() ? 1 : 0) != 0, (Object)"Water region is only determined server-side");
        Map<ChunkPos, ChunkTicketManager<Vec3>> ticketMap = customWaterHandler.get(level);
        if (ticketMap == null) {
            return null;
        }
        return ticketMap.get(pos);
    }
}

