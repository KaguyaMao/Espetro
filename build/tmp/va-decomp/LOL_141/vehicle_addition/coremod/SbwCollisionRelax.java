/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.tools.OBB
 *  kotlin.Pair
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction$Axis
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.VoxelShape
 *  org.joml.Vector3d
 */
package LOL_141.vehicle_addition.coremod;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.OBB;
import java.lang.reflect.Field;
import kotlin.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3d;

public final class SbwCollisionRelax {
    public static final double COLLISION_EMBED_ALLOWED = 0.6;
    private static final double CLOSE_THRESHOLD = 1.5;
    private static final double[][] SAMPLE_OFFSETS = new double[][]{{-1.0, -1.0}, {-1.0, 1.0}, {1.0, -1.0}, {1.0, 1.0}, {0.0, 0.0}};
    private static Field obbExtentsF;

    private SbwCollisionRelax() {
    }

    private static Vector3d extentsOf(Object obb) {
        try {
            if (obbExtentsF == null) {
                obbExtentsF = obb.getClass().getDeclaredField("extents");
                obbExtentsF.setAccessible(true);
            }
            return (Vector3d)obbExtentsF.get(obb);
        }
        catch (Throwable t) {
            return null;
        }
    }

    public static Pair<Double, Double> checkBottomSupportRatio(VehicleEntity vehicle, OBB obb) {
        try {
            Level level = vehicle.m_9236_();
            Vector3d[] axes = obb.getAxes();
            Vector3d center = obb.center;
            Vector3d ext = SbwCollisionRelax.extentsOf(obb);
            if (ext == null) {
                return new Pair((Object)1.0, (Object)0.0);
            }
            double ex = ext.x;
            double ey = ext.y;
            double ez = ext.z;
            int onSurfaceCount = 0;
            double maxPenetration = 0.0;
            for (double[] off : SAMPLE_OFFSETS) {
                double blockTopY;
                double dist;
                double lx = off[0] * ex;
                double lz = off[1] * ez;
                double wx = center.x + axes[0].x * lx + axes[1].x * -ey + axes[2].x * lz;
                double wy = center.y + axes[0].y * lx + axes[1].y * -ey + axes[2].y * lz;
                double wz = center.z + axes[0].z * lx + axes[1].z * -ey + axes[2].z * lz;
                BlockPos blockPos = BlockPos.m_274561_((double)wx, (double)wy, (double)wz);
                BlockPos blockPosBelow = BlockPos.m_274561_((double)wx, (double)(wy - 0.02), (double)wz);
                BlockState state = level.m_8055_(blockPosBelow);
                VoxelShape shape = state.m_60812_((BlockGetter)level, blockPosBelow);
                BlockPos shapeBlockPos = blockPosBelow;
                if (shape.m_83281_()) {
                    state = level.m_8055_(blockPos);
                    shape = state.m_60812_((BlockGetter)level, blockPos);
                    shapeBlockPos = blockPos;
                }
                if (shape.m_83281_() || !(Math.abs(dist = wy - (blockTopY = (double)shapeBlockPos.m_123342_() + shape.m_83297_(Direction.Axis.Y))) <= 1.5)) continue;
                ++onSurfaceCount;
                if (!(dist < 0.0)) continue;
                maxPenetration = Math.max(maxPenetration, -dist);
            }
            double ratio = (double)onSurfaceCount / (double)SAMPLE_OFFSETS.length;
            double correction = Math.max(0.0, maxPenetration - 0.6);
            return new Pair((Object)ratio, (Object)correction);
        }
        catch (Throwable t) {
            return new Pair((Object)1.0, (Object)0.0);
        }
    }
}

