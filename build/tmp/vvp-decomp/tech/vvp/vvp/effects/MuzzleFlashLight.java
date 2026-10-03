/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.core.Vec3i
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.LightBlock
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.block.state.properties.Property
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.effects;

import com.atsuishio.superbwarfare.Mod;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.effects.MuzzleEffectPreset;

public final class MuzzleFlashLight {
    private static final Map<BlockPos, Integer> CLEANUP_GENERATION = new ConcurrentHashMap<BlockPos, Integer>();

    private MuzzleFlashLight() {
    }

    public static void spawn(ServerLevel level, Vec3 muzzlePos, Vec3 forward, MuzzleEffectPreset preset) {
        int extraAlongBarrel;
        int durationTicks;
        int peakLevel;
        switch (preset) {
            case TANK: {
                peakLevel = 15;
                durationTicks = 4;
                extraAlongBarrel = 2;
                break;
            }
            case AUTOCANNON: {
                peakLevel = 14;
                durationTicks = 2;
                extraAlongBarrel = 1;
                break;
            }
            case MISSILE: {
                peakLevel = 13;
                durationTicks = 2;
                extraAlongBarrel = 0;
                break;
            }
            case MACHINE_GUN: {
                peakLevel = 11;
                durationTicks = 1;
                extraAlongBarrel = 0;
                break;
            }
            default: {
                return;
            }
        }
        Vec3 direction = forward.m_82556_() > 1.0E-6 ? forward.m_82541_() : new Vec3(0.0, 0.0, 1.0);
        ArrayList<BlockPos> placed = new ArrayList<BlockPos>();
        MuzzleFlashLight.placeAtMuzzle(level, muzzlePos, direction, peakLevel, placed, 2);
        for (int i = 1; i <= extraAlongBarrel; ++i) {
            Vec3 alongBarrel = muzzlePos.m_82549_(direction.m_82490_(0.28 * (double)i));
            MuzzleFlashLight.placeAtMuzzle(level, alongBarrel, direction, Math.max(9, peakLevel - i * 2), placed, 2);
        }
        MuzzleFlashLight.scheduleLights(level, placed, peakLevel, durationTicks);
    }

    public static void spawnBackblast(ServerLevel level, Vec3 rearPos, Vec3 backDir) {
        Vec3 direction = backDir.m_82556_() > 1.0E-6 ? backDir.m_82541_() : new Vec3(0.0, 0.0, -1.0);
        ArrayList<BlockPos> placed = new ArrayList<BlockPos>();
        MuzzleFlashLight.placeAtMuzzle(level, rearPos, direction, 15, placed, 4);
        MuzzleFlashLight.placeAtMuzzle(level, rearPos.m_82549_(direction.m_82490_(0.7)), direction, 14, placed, 4);
        MuzzleFlashLight.placeAtMuzzle(level, rearPos.m_82549_(direction.m_82490_(1.4)), direction, 13, placed, 4);
        MuzzleFlashLight.placeAtMuzzle(level, rearPos.m_82549_(direction.m_82490_(2.2)), direction, 12, placed, 3);
        MuzzleFlashLight.placeAtMuzzle(level, rearPos.m_82520_(0.0, -0.45, 0.0), direction, 12, placed, 5);
        MuzzleFlashLight.placeAtMuzzle(level, rearPos.m_82549_(direction.m_82490_(1.0)).m_82520_(0.0, -0.25, 0.0), direction, 11, placed, 4);
        MuzzleFlashLight.scheduleLights(level, placed, 15, 12);
    }

    private static void scheduleLights(ServerLevel level, List<BlockPos> placed, int peakLevel, int durationTicks) {
        if (placed.isEmpty()) {
            return;
        }
        List<BlockPos> lights = List.copyOf(placed);
        for (BlockPos lightPos : lights) {
            int generation;
            int cleanupGeneration = generation = CLEANUP_GENERATION.merge(lightPos, 1, Integer::sum).intValue();
            int fadePeak = peakLevel;
            for (int tick = 0; tick < durationTicks; ++tick) {
                int tickIndex = tick;
                Mod.queueServerWork((int)tick, () -> {
                    if (CLEANUP_GENERATION.getOrDefault(lightPos, 0) != cleanupGeneration) {
                        return;
                    }
                    int levelValue = Math.max(4, fadePeak - tickIndex * (fadePeak / Math.max(1, durationTicks)));
                    if (level.m_8055_(lightPos).m_60713_(Blocks.f_152480_)) {
                        level.m_7731_(lightPos, (BlockState)Blocks.f_152480_.m_49966_().m_61124_((Property)LightBlock.f_153657_, (Comparable)Integer.valueOf(levelValue)), 3);
                    }
                });
            }
            Mod.queueServerWork((int)durationTicks, () -> {
                if (CLEANUP_GENERATION.getOrDefault(lightPos, 0) != cleanupGeneration) {
                    return;
                }
                CLEANUP_GENERATION.remove(lightPos, cleanupGeneration);
                if (level.m_8055_(lightPos).m_60713_(Blocks.f_152480_)) {
                    level.m_7731_(lightPos, Blocks.f_50016_.m_49966_(), 3);
                }
            });
        }
    }

    private static void placeAtMuzzle(ServerLevel level, Vec3 muzzlePos, Vec3 forward, int lightLevel, List<BlockPos> placed, int searchRadius) {
        double step;
        Vec3 direction = forward.m_82541_();
        ArrayList<Vec3> candidates = new ArrayList<Vec3>();
        for (step = 0.0; step <= 1.2; step += 0.1) {
            candidates.add(muzzlePos.m_82549_(direction.m_82490_(step)));
        }
        for (step = 0.08; step <= 0.55; step += 0.08) {
            candidates.add(muzzlePos.m_82546_(direction.m_82490_(step)));
        }
        BlockPos center = BlockPos.m_274446_((Position)muzzlePos);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int radius = 0; radius <= searchRadius; ++radius) {
            for (int dx = -radius; dx <= radius; ++dx) {
                for (int dy = -radius; dy <= radius; ++dy) {
                    for (int dz = -radius; dz <= radius; ++dz) {
                        if (dx == 0 && dy == 0 && dz == 0 && radius > 0) continue;
                        mutable.m_122178_(center.m_123341_() + dx, center.m_123342_() + dy, center.m_123343_() + dz);
                        candidates.add(Vec3.m_82512_((Vec3i)mutable));
                    }
                }
            }
        }
        candidates.sort(Comparator.comparingDouble(candidate -> candidate.m_82557_(muzzlePos)));
        for (Vec3 candidate2 : candidates) {
            BlockPos blockPos = BlockPos.m_274446_((Position)candidate2);
            if (placed.contains(blockPos) || !MuzzleFlashLight.tryPlace(level, blockPos, lightLevel)) continue;
            placed.add(blockPos);
            return;
        }
    }

    private static boolean tryPlace(ServerLevel level, BlockPos pos, int lightLevel) {
        if (!level.m_46749_(pos)) {
            return false;
        }
        BlockState state = level.m_8055_(pos);
        if (!state.m_60795_() && !state.m_60713_(Blocks.f_152480_)) {
            return false;
        }
        int appliedLevel = lightLevel;
        if (state.m_60713_(Blocks.f_152480_)) {
            appliedLevel = Math.max(lightLevel, (Integer)state.m_61143_((Property)LightBlock.f_153657_));
        }
        level.m_7731_(pos, (BlockState)Blocks.f_152480_.m_49966_().m_61124_((Property)LightBlock.f_153657_, (Comparable)Integer.valueOf(appliedLevel)), 3);
        return true;
    }
}

