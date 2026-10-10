/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.Vec3
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Quaterniond
 *  org.joml.Quaterniondc
 *  org.joml.Vector3d
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package LOL_141.vehicle_addition.compat;

import LOL_141.vehicle_addition.compat.SamplePointCache;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GroundTerrainCompat {
    private static final double EMBED_TOLERANCE = 0.25;
    private static final double SEARCH_UP = 1.5;
    private static final double SEARCH_DOWN = 8.0;
    private static final int SAMPLE_RADIUS = 1;
    private static final float MAX_TILT = 45.0f;
    private static final float TARGET_SMOOTHING = 0.3f;
    private static final double MAX_DESCENT_PER_TICK = 0.3;
    private static final float HANG_GRAVITY = 0.04f;
    private static final float HANG_MAX_SPEED = 0.15f;
    private static final double HANG_GATE = 0.5;
    private static final float MAX_ROT_SPEED = 1.2f;
    private static final float ROT_DAMPING = 0.9f;
    private static final float ROT_ACCEL = 0.15f;
    private static final double FALL_OUTSET = 0.15;
    private static final double FALL_RETURN = 0.35;
    private static final long LOG_INTERVAL = 100L;
    private static final Logger LOGGER = LoggerFactory.getLogger(GroundTerrainCompat.class);

    private GroundTerrainCompat() {
    }

    /*
     * Enabled aggressive block sorting
     */
    public static void applyAutoTerrainCompact(Level level, Vec3 vehiclePos, Vec3 obbCenter, Vec3 obbExtents, Quaterniond obbRot, float rotateRate, AngularState state, PoseAccess pose) {
        double safeDescent;
        int touchCount;
        double minRawHeightY;
        double meanRawH;
        double meanH;
        double meanLz;
        double meanLx;
        int supportCount;
        ArrayList<Integer> supportIdx;
        int pitCount;
        double[] rawHeightY;
        double[] heightY;
        double[] sampleLz;
        double[] sampleLx;
        double speedMS;
        block35: {
            if (level == null || obbExtents == null || state == null || obbRot == null) {
                return;
            }
            Vec3 motion = pose.deltaMovement();
            speedMS = 0.0;
            if (motion != null) {
                speedMS = motion.m_82553_() * 20.0;
            }
            int count = 5;
            sampleLx = new double[5];
            sampleLz = new double[5];
            double[] sampleLy = new double[5];
            double[] sampleWorldX = new double[5];
            double[] sampleWorldY = new double[5];
            double[] sampleWorldZ = new double[5];
            double ex = obbExtents.f_82479_;
            double ey = obbExtents.f_82480_;
            double ez = obbExtents.f_82481_;
            double[] cornerX = new double[]{-ex, -ex, ex, ex, 0.0};
            double[] cornerZ = new double[]{-ez, ez, -ez, ez, 0.0};
            double sinYaw = Math.sin(Math.toRadians(pose.yRot()));
            double cosYaw = Math.cos(Math.toRadians(pose.yRot()));
            for (int i = 0; i < 5; ++i) {
                double lx = cornerX[i];
                double lz = cornerZ[i];
                double wx = vehiclePos.f_82479_ + (lx * cosYaw - lz * sinYaw);
                double wz = vehiclePos.f_82481_ + (lx * sinYaw + lz * cosYaw);
                sampleLx[i] = lx;
                sampleLz[i] = lz;
                sampleLy[i] = -ey;
                sampleWorldX[i] = wx;
                sampleWorldY[i] = vehiclePos.f_82480_ - ey;
                sampleWorldZ[i] = wz;
            }
            heightY = new double[5];
            rawHeightY = new double[5];
            boolean[] isPit = new boolean[5];
            for (int i = 0; i < 5; ++i) {
                Double top = GroundTerrainCompat.sampleTerrainTop(level, sampleWorldX[i], sampleWorldY[i], sampleWorldZ[i], 1.5, 8.0, 1);
                if (top == null) {
                    isPit[i] = true;
                    heightY[i] = 8.0;
                    rawHeightY[i] = 8.0;
                    continue;
                }
                double rawPre = sampleWorldY[i] - top;
                isPit[i] = false;
                rawHeightY[i] = rawPre;
                double h = rawPre;
                h = h >= -0.25 && h <= 0.25 ? 0.0 : (h > 0.0 ? (h -= 0.25) : (h += 0.25));
                heightY[i] = GroundTerrainCompat.clamp(h, -8.0, 8.0);
            }
            ArrayList<double[]> embeddedContacts = new ArrayList<double[]>();
            for (int ci = 0; ci < 4; ++ci) {
                Vector3d local = new Vector3d(cornerX[ci], -ey, cornerZ[ci]);
                obbRot.transform(local);
                double cwx = obbCenter.f_82479_ + local.x;
                double cwy = obbCenter.f_82480_ + local.y;
                double cwz = obbCenter.f_82481_ + local.z;
                if (!level.m_8055_(BlockPos.m_274561_((double)cwx, (double)cwy, (double)cwz)).m_280296_()) continue;
                embeddedContacts.add(new double[]{cwx, cwy, cwz});
            }
            pitCount = 0;
            for (boolean pit : isPit) {
                if (!pit) continue;
                ++pitCount;
            }
            boolean centerEnabled = true;
            for (int ci = 0; ci < 4; ++ci) {
                if (isPit[ci] || rawHeightY[ci] > 0.25) continue;
                centerEnabled = false;
                break;
            }
            supportIdx = new ArrayList<Integer>();
            double sumLx = 0.0;
            double sumLz = 0.0;
            double sumH = 0.0;
            double sumRawH = 0.0;
            for (int i = 0; i < 5; ++i) {
                if (isPit[i] || i == 4 && !centerEnabled) continue;
                supportIdx.add(i);
                sumLx += sampleLx[i];
                sumLz += sampleLz[i];
                sumH += heightY[i];
                sumRawH += rawHeightY[i];
            }
            supportCount = supportIdx.size();
            if (level.f_46443_ && pose.debugEntity() != null) {
                ArrayList<Vec3> points = new ArrayList<Vec3>(5);
                ArrayList<Boolean> pits = new ArrayList<Boolean>(5);
                for (int i = 0; i < 5; ++i) {
                    points.add(new Vec3(sampleWorldX[i], sampleWorldY[i], sampleWorldZ[i]));
                    pits.add(isPit[i]);
                }
                ArrayList<Vec3> embeddedPts = new ArrayList<Vec3>(embeddedContacts.size());
                for (double[] e : embeddedContacts) {
                    embeddedPts.add(new Vec3(e[0], e[1], e[2]));
                }
                SamplePointCache.put(pose.debugEntity(), points, pits, supportIdx, embeddedPts);
            }
            if (supportCount == 0) {
                float fallV = state.fallV;
                if (fallV > -0.02f) {
                    fallV = -0.02f;
                }
                if ((fallV -= 0.04f) < -0.15f) {
                    fallV = -0.15f;
                }
                state.fallV = fallV;
                pose.addVerticalVelocity(fallV);
                if (level.m_46467_() % 5L == 0L) {
                    LOGGER.info("[VT] t={} side={} NOUPP supp=0 pits={} fallV={} xRot={} roll={} spd={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", pitCount, String.format("%.2f", Float.valueOf(fallV)), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll())), String.format("%.2f", speedMS)});
                }
                return;
            }
            meanLx = sumLx / (double)supportCount;
            meanLz = sumLz / (double)supportCount;
            meanH = sumH / (double)supportCount;
            meanRawH = sumRawH / (double)supportCount;
            minRawHeightY = Double.MAX_VALUE;
            Iterator iterator = supportIdx.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                if (!(rawHeightY[i] < minRawHeightY)) continue;
                minRawHeightY = rawHeightY[i];
            }
            if (minRawHeightY > 0.5) {
                float fallV = state.fallV;
                if (fallV > -0.02f) {
                    fallV = -0.02f;
                }
                if ((fallV -= 0.04f) < -0.15f) {
                    fallV = -0.15f;
                }
                state.fallV = fallV;
                pose.addVerticalVelocity(fallV);
                if (level.m_46467_() % 5L == 0L) {
                    LOGGER.info("[VT] t={} side={} AIRBORNE minRawH={} xRot={} roll={} spd={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", String.format("%.2f", minRawHeightY), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll())), String.format("%.2f", speedMS)});
                }
                return;
            }
            ArrayList<double[]> touchXZ = new ArrayList<double[]>();
            ArrayList<double[]> touchWorld = new ArrayList<double[]>();
            Iterator iterator2 = supportIdx.iterator();
            while (iterator2.hasNext()) {
                int i = (Integer)iterator2.next();
                if (!(rawHeightY[i] <= 0.25)) continue;
                touchXZ.add(new double[]{sampleWorldX[i], sampleWorldZ[i]});
                touchWorld.add(new double[]{sampleWorldX[i], sampleWorldY[i] - rawHeightY[i], sampleWorldZ[i]});
            }
            for (double[] e : embeddedContacts) {
                touchXZ.add(new double[]{e[0], e[2]});
                touchWorld.add(new double[]{e[0], e[1], e[2]});
            }
            touchCount = touchXZ.size();
            double comX = obbCenter.f_82479_;
            double comZ = obbCenter.f_82481_;
            if (touchCount >= 3) {
                List<double[]> hull = GroundTerrainCompat.convexHull(touchXZ);
                if (hull.size() >= 3 && GroundTerrainCompat.isPointInPolygon(new double[]{comX, comZ}, hull)) {
                    state.rotV = 0.0f;
                    break block35;
                } else {
                    GroundTerrainCompat.rotateAroundSupportEdge(level, state, pose, hull, touchWorld, comX, comZ, vehiclePos, speedMS);
                    return;
                }
            }
            if (touchCount == 2) {
                GroundTerrainCompat.rotateAroundTwoPoints(level, state, pose, touchWorld, comX, comZ, vehiclePos, speedMS);
                return;
            }
            if (touchCount == 1) {
                GroundTerrainCompat.rotateAroundSinglePoint(level, state, pose, touchWorld, comX, comZ, vehiclePos, speedMS);
                return;
            }
            state.rotV = 0.0f;
        }
        double descent = 0.0;
        if (meanRawH > 0.12 && minRawHeightY < Double.MAX_VALUE && (safeDescent = minRawHeightY + 0.25) > 0.02) {
            descent = Math.min(safeDescent, 0.3);
            pose.addVerticalVelocity(-descent);
        }
        float fitTargetX = 0.0f;
        float fitTargetZ = 0.0f;
        if (touchCount >= 3) {
            double sumXH = 0.0;
            double sumZH = 0.0;
            double sumX2 = 0.0;
            double sumZ2 = 0.0;
            Iterator iterator = supportIdx.iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                if (rawHeightY[i] > 0.25) continue;
                double dx = sampleLx[i] - meanLx;
                double dz = sampleLz[i] - meanLz;
                double dh = heightY[i] - meanH;
                sumXH += dx * dh;
                sumX2 += dx * dx;
                sumZH += dz * dh;
                sumZ2 += dz * dz;
            }
            if (sumX2 > 1.0E-6 || sumZ2 > 1.0E-6) {
                double slopeX = sumX2 > 0.0 ? GroundTerrainCompat.clamp(sumXH / sumX2, -3.0, 3.0) : 0.0;
                double slopeZ = sumZ2 > 0.0 ? GroundTerrainCompat.clamp(sumZH / sumZ2, -3.0, 3.0) : 0.0;
                fitTargetX = Mth.m_14036_((float)((float)Math.toDegrees(Math.atan2(slopeZ, 1.0))), (float)-45.0f, (float)45.0f);
                fitTargetZ = -Mth.m_14036_((float)((float)Math.toDegrees(Math.atan2(slopeX, 1.0))), (float)-45.0f, (float)45.0f);
            }
        }
        state.targetX += GroundTerrainCompat.wrapDegrees(fitTargetX - state.targetX) * 0.3f;
        state.targetZ += GroundTerrainCompat.wrapDegrees(fitTargetZ - state.targetZ) * 0.3f;
        pose.setXRot(state.targetX);
        pose.setZRot(state.targetZ);
        float dirX = Math.signum(fitTargetX - state.targetX);
        if (state.prevDirX != 0.0f && dirX != 0.0f && dirX != state.prevDirX) {
            ++state.oscCount;
        }
        state.prevDirX = dirX;
        state.rawFitX = fitTargetX;
        state.rawFitZ = fitTargetZ;
        if (level.m_46467_() % 5L == 0L) {
            LOGGER.info("[VT] t={} side={} xRot={} rawX={} tgtX={} osc={} roll={} rawZ={} tgtZ={} supp={} pits={} meanRawH={} desc={} spd={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(fitTargetX)), String.format("%.1f", Float.valueOf(state.targetX)), state.oscCount, String.format("%.1f", Float.valueOf(pose.roll())), String.format("%.1f", Float.valueOf(fitTargetZ)), String.format("%.1f", Float.valueOf(state.targetZ)), supportCount, pitCount, String.format("%.2f", meanRawH), String.format("%.2f", descent), String.format("%.2f", speedMS)});
        }
        if (level.m_46467_() % 100L == 0L) {
            LOGGER.info("[GroundTerrain] side={} fit support={} pits={}/{} meanRawH={} xRot={} roll={} oscCount={}", new Object[]{level.f_46443_ ? "C" : "S", supportCount, pitCount, 5, String.format("%.2f", meanRawH), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll())), state.oscCount});
        }
    }

    private static void rotateAroundSupportEdge(Level level, AngularState state, PoseAccess pose, List<double[]> hull, List<double[]> touchWorld, double comX, double comZ, Vec3 vehiclePos, double speedMS) {
        float dir;
        double minDist = Double.MAX_VALUE;
        double[] edgeA = null;
        double[] edgeB = null;
        for (int i = 0; i < hull.size(); ++i) {
            double[] b;
            double[] a = hull.get(i);
            double d = GroundTerrainCompat.pointToSegmentDist(comX, comZ, a[0], a[1], (b = hull.get((i + 1) % hull.size()))[0], b[1]);
            if (!(d < minDist)) continue;
            minDist = d;
            edgeA = a;
            edgeB = b;
        }
        if (edgeA == null || edgeB == null) {
            return;
        }
        double[] wa = null;
        double[] wb = null;
        double bestA = Double.MAX_VALUE;
        double bestB = Double.MAX_VALUE;
        for (double[] w : touchWorld) {
            double da = Math.hypot(w[0] - edgeA[0], w[2] - edgeA[1]);
            double db = Math.hypot(w[0] - edgeB[0], w[2] - edgeB[1]);
            if (da < bestA) {
                bestA = da;
                wa = w;
            }
            if (!(db < bestB)) continue;
            bestB = db;
            wb = w;
        }
        if (wa == null || wb == null) {
            return;
        }
        void cross = (edgeB[0] - edgeA[0]) * (comZ - edgeA[1]) - (edgeB[1] - edgeA[1]) * (comX - edgeA[0]);
        float f = dir = cross > 0.0 ? 1.0f : -1.0f;
        if (state.rotV != 0.0f && GroundTerrainCompat.isPointInPolygon(new double[]{comX, comZ}, hull)) {
            state.rotV = 0.0f;
            return;
        }
        float accel = (float)Math.max(minDist, 0.15) * 0.15f;
        state.rotV = Mth.m_14036_((float)(state.rotV + dir * accel), (float)-1.2f, (float)1.2f);
        state.rotV *= 0.9f;
        double len = Math.hypot(edgeB[0] - edgeA[0], edgeB[1] - edgeA[1]);
        double angleDepth = Math.toDegrees(Math.atan2(0.5, Math.max(len, 1.0)));
        double curAngle = Math.abs(pose.xRot());
        if (curAngle >= angleDepth && state.rotV * dir > 0.0f) {
            state.rotV = 0.0f;
            return;
        }
        Vec3 axisStart = new Vec3(wa[0], wa[1], wa[2]);
        Vec3 axisEnd = new Vec3(wb[0], wb[1], wb[2]);
        GroundTerrainCompat.rotateVehicleAroundAxis(pose, axisStart, axisEnd, state.rotV, vehiclePos);
        if (level.m_46467_() % 5L == 0L) {
            LOGGER.info("[VT] t={} side={} ROTEDGE dist={} rotV={} xRot={} roll={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", String.format("%.2f", minDist), String.format("%.2f", Float.valueOf(state.rotV)), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll()))});
        }
    }

    private static void rotateAroundTwoPoints(Level level, AngularState state, PoseAccess pose, List<double[]> touchWorld, double comX, double comZ, Vec3 vehiclePos, double speedMS) {
        double[] wa = touchWorld.get(0);
        double[] wb = touchWorld.get(1);
        double cross = (wb[0] - wa[0]) * (comZ - wa[2]) - (wb[2] - wa[2]) * (comX - wa[0]);
        float dir = cross > 0.0 ? 1.0f : -1.0f;
        state.rotV = Mth.m_14036_((float)(state.rotV + dir * 0.1f), (float)-1.2f, (float)1.2f);
        state.rotV *= 0.9f;
        Vec3 axisStart = new Vec3(wa[0], wa[1], wa[2]);
        Vec3 axisEnd = new Vec3(wb[0], wb[1], wb[2]);
        GroundTerrainCompat.rotateVehicleAroundAxis(pose, axisStart, axisEnd, state.rotV, vehiclePos);
        if (level.m_46467_() % 5L == 0L) {
            LOGGER.info("[VT] t={} side={} ROT2 rotV={} xRot={} roll={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", String.format("%.2f", Float.valueOf(state.rotV)), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll()))});
        }
    }

    private static void rotateAroundSinglePoint(Level level, AngularState state, PoseAccess pose, List<double[]> touchWorld, double comX, double comZ, Vec3 vehiclePos, double speedMS) {
        double dz;
        double[] w = touchWorld.get(0);
        double dx = comX - w[0];
        double dist = Math.hypot(dx, dz = comZ - w[2]);
        if (dist < 0.05) {
            return;
        }
        double ax = dz / dist;
        double az = -dx / dist;
        state.rotV = Mth.m_14036_((float)(state.rotV + 0.1f), (float)-1.2f, (float)1.2f);
        state.rotV *= 0.9f;
        Vec3 axisStart = new Vec3(w[0], w[1], w[2]);
        Vec3 axisEnd = new Vec3(w[0] + ax, w[1], w[2] + az);
        GroundTerrainCompat.rotateVehicleAroundAxis(pose, axisStart, axisEnd, state.rotV, vehiclePos);
        if (level.m_46467_() % 5L == 0L) {
            LOGGER.info("[VT] t={} side={} ROT1 dist={} rotV={} xRot={} roll={}", new Object[]{level.m_46467_(), level.f_46443_ ? "C" : "S", String.format("%.2f", dist), String.format("%.2f", Float.valueOf(state.rotV)), String.format("%.1f", Float.valueOf(pose.xRot())), String.format("%.1f", Float.valueOf(pose.roll()))});
        }
    }

    private static void rotateVehicleAroundAxis(PoseAccess pose, Vec3 axisStart, Vec3 axisEnd, float degrees, Vec3 currentPos) {
        if (Math.abs(degrees) < 0.01f) {
            return;
        }
        Vec3 axis = axisEnd.m_82546_(axisStart);
        double axisLen = axis.m_82553_();
        if (axisLen < 1.0E-6) {
            return;
        }
        axis = axis.m_82490_(1.0 / axisLen);
        double rad = Math.toRadians(degrees);
        Quaterniond q = new Quaterniond().fromAxisAngleRad(axis.f_82479_, axis.f_82480_, axis.f_82481_, rad);
        Vector3d rel = new Vector3d(currentPos.f_82479_ - axisStart.f_82479_, currentPos.f_82480_ - axisStart.f_82480_, currentPos.f_82481_ - axisStart.f_82481_);
        q.transform(rel);
        Vec3 newPos = new Vec3(rel.x + axisStart.f_82479_, rel.y + axisStart.f_82480_, rel.z + axisStart.f_82481_);
        pose.setPosition(newPos);
        Quaterniond rot = new Quaterniond();
        rot.rotationYXZ(Math.toRadians(-pose.yRot()), Math.toRadians(pose.xRot()), Math.toRadians(pose.roll()));
        Quaterniond step = new Quaterniond().fromAxisAngleRad(axis.f_82479_, axis.f_82480_, axis.f_82481_, rad);
        step.mul((Quaterniondc)rot);
        Vector3d euler = new Vector3d();
        step.getEulerAnglesYXZ(euler);
        if (Double.isNaN(euler.x) || Double.isNaN(euler.y) || Double.isNaN(euler.z)) {
            return;
        }
        pose.setYRot((float)Math.toDegrees(-euler.y));
        pose.setXRot((float)Math.toDegrees(euler.x));
        pose.setZRot((float)Math.toDegrees(euler.z));
    }

    private static List<double[]> convexHull(List<double[]> pts) {
        ArrayList<double[]> sorted = new ArrayList<double[]>(pts);
        sorted.sort((a, b) -> a[0] != b[0] ? Double.compare(a[0], b[0]) : Double.compare(a[1], b[1]));
        if (sorted.size() <= 2) {
            return sorted;
        }
        ArrayList<double[]> lower = new ArrayList<double[]>();
        for (double[] p : sorted) {
            while (lower.size() >= 2 && GroundTerrainCompat.cross((double[])lower.get(lower.size() - 2), (double[])lower.get(lower.size() - 1), p) <= 0.0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }
        ArrayList<double[]> upper = new ArrayList<double[]>();
        for (int i = sorted.size() - 1; i >= 0; --i) {
            double[] p = (double[])sorted.get(i);
            while (upper.size() >= 2 && GroundTerrainCompat.cross((double[])upper.get(upper.size() - 2), (double[])upper.get(upper.size() - 1), p) <= 0.0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);
        return lower;
    }

    private static double cross(double[] o, double[] a, double[] b) {
        return (a[0] - o[0]) * (b[1] - o[1]) - (a[1] - o[1]) * (b[0] - o[0]);
    }

    private static boolean isPointInPolygon(double[] p, List<double[]> poly) {
        boolean inside = false;
        int n = poly.size();
        int i = 0;
        int j = n - 1;
        while (i < n) {
            double[] b;
            double[] a = poly.get(i);
            if (a[1] > p[1] != (b = poly.get(j))[1] > p[1] && p[0] < (b[0] - a[0]) * (p[1] - a[1]) / (b[1] - a[1]) + a[0]) {
                inside = !inside;
            }
            j = i++;
        }
        return inside;
    }

    private static double pointToSegmentDist(double px, double pz, double ax, double az, double bx, double bz) {
        double dx = bx - ax;
        double dz = bz - az;
        double lenSq = dx * dx + dz * dz;
        if (lenSq < 1.0E-9) {
            return Math.sqrt((px - ax) * (px - ax) + (pz - az) * (pz - az));
        }
        double t = ((px - ax) * dx + (pz - az) * dz) / lenSq;
        t = GroundTerrainCompat.clamp(t, 0.0, 1.0);
        double cx = ax + t * dx;
        double cz = az + t * dz;
        return Math.sqrt((px - cx) * (px - cx) + (pz - cz) * (pz - cz));
    }

    private static Double sampleTerrainTop(Level level, double wx, double wy, double wz, double searchUp, double searchDown, int radius) {
        int bx = Mth.m_14107_((double)wx);
        int bz = Mth.m_14107_((double)wz);
        int topBlock = Mth.m_14107_((double)(wy + searchUp));
        int botBlock = Mth.m_14107_((double)(wy - searchDown));
        double ceil = wy + searchUp + 1.0E-6;
        double[] best = new double[]{Double.NaN};
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int ox = -radius; ox <= radius; ++ox) {
            for (int oz = -radius; oz <= radius; ++oz) {
                int cx = bx + ox;
                int cz = bz + oz;
                int fOx = ox;
                int fOz = oz;
                for (int by = topBlock; by >= botBlock; --by) {
                    VoxelShape shape;
                    pos.m_122178_(cx, by, cz);
                    BlockState state = level.m_8055_((BlockPos)pos);
                    if (state.m_60795_() || (shape = state.m_60812_((BlockGetter)level, (BlockPos)pos)).m_83281_()) continue;
                    int curBy = by;
                    shape.m_83286_((minX, minY, minZ, maxX, maxY, maxZ) -> {
                        double boxTop;
                        if ((double)fOx >= minX - 1.0E-6 && (double)fOx <= maxX + 1.0E-6 && (double)fOz >= minZ - 1.0E-6 && (double)fOz <= maxZ + 1.0E-6 && (boxTop = (double)curBy + maxY) <= ceil && (Double.isNaN(best[0]) || boxTop > best[0])) {
                            best[0] = boxTop;
                        }
                    });
                }
            }
        }
        return Double.isNaN(best[0]) ? null : Double.valueOf(best[0]);
    }

    private static float wrapDegrees(float value) {
        return Mth.m_14177_((float)value);
    }

    private static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    public static interface PoseAccess {
        public float xRot();

        public float yRot();

        public float roll();

        public void setXRot(float var1);

        public void setYRot(float var1);

        public void setZRot(float var1);

        public void adjustY(double var1);

        public void setPosition(Vec3 var1);

        default public void addVerticalVelocity(double dy) {
            Vec3 m = this.deltaMovement();
            if (m != null) {
                this.deltaMovement(m.f_82479_, m.f_82480_ + dy, m.f_82481_);
            }
        }

        default public void deltaMovement(double x, double y, double z) {
        }

        default public Vec3 deltaMovement() {
            return null;
        }

        default public Entity debugEntity() {
            return null;
        }
    }

    public static final class AngularState {
        public float fallV;
        public float rotV;
        public float targetX;
        public float targetZ;
        public float rawFitX;
        public float rawFitZ;
        public int oscCount;
        public float prevDirX;
    }
}

