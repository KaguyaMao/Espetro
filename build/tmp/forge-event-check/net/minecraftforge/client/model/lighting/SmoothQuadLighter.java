/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.color.block.BlockColors
 *  net.minecraft.client.renderer.LevelRenderer
 *  net.minecraft.client.renderer.LightTexture
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.core.Vec3i
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.shapes.Shapes
 *  net.minecraft.world.phys.shapes.VoxelShape
 */
package net.minecraftforge.client.model.lighting;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.lighting.QuadLighter;

public class SmoothQuadLighter
extends QuadLighter {
    private static final Direction[] SIDES = Direction.values();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private final boolean[][][] t = new boolean[3][3][3];
    private final int[][][] s = new int[3][3][3];
    private final int[][][] b = new int[3][3][3];
    private final float[][][][] skyLight = new float[3][2][2][2];
    private final float[][][][] blockLight = new float[3][2][2][2];
    private final float[][][] ao = new float[3][3][3];

    public SmoothQuadLighter(BlockColors colors) {
        super(colors);
    }

    @Override
    protected void computeLightingAt(BlockAndTintGetter level, BlockPos origin, BlockState state) {
        int z;
        int y;
        for (int x = 0; x <= 2; ++x) {
            for (y = 0; y <= 2; ++y) {
                for (z = 0; z <= 2; ++z) {
                    this.pos.m_122154_((Vec3i)origin, x - 1, y - 1, z - 1);
                    BlockState neighborState = level.m_8055_((BlockPos)this.pos);
                    this.t[x][y][z] = neighborState.m_60739_((BlockGetter)level, (BlockPos)this.pos) < 15;
                    int brightness = LevelRenderer.m_109537_((BlockAndTintGetter)level, (BlockState)neighborState, (BlockPos)this.pos);
                    this.s[x][y][z] = LightTexture.m_109894_((int)brightness);
                    this.b[x][y][z] = LightTexture.m_109883_((int)brightness);
                    this.ao[x][y][z] = neighborState.m_60792_((BlockGetter)level, (BlockPos)this.pos);
                }
            }
        }
        Direction[] x = SIDES;
        y = x.length;
        for (z = 0; z < y; ++z) {
            BlockState otherStateShape;
            Direction side = x[z];
            this.pos.m_122159_((Vec3i)origin, side);
            BlockState neighborState = level.m_8055_((BlockPos)this.pos);
            BlockState thisStateShape = state.m_60815_() && state.m_60787_() ? state : Blocks.f_50016_.m_49966_();
            BlockState blockState = otherStateShape = neighborState.m_60815_() && neighborState.m_60787_() ? neighborState : Blocks.f_50016_.m_49966_();
            if (neighborState.m_60739_((BlockGetter)level, (BlockPos)this.pos) != 15 && !Shapes.m_83145_((VoxelShape)thisStateShape.m_60655_((BlockGetter)level, origin, side), (VoxelShape)otherStateShape.m_60655_((BlockGetter)level, (BlockPos)this.pos, side.m_122424_()))) continue;
            int x2 = side.m_122429_() + 1;
            int y2 = side.m_122430_() + 1;
            int z2 = side.m_122431_() + 1;
            this.s[x2][y2][z2] = Math.max(this.s[1][1][1] - 1, this.s[x2][y2][z2]);
            this.b[x2][y2][z2] = Math.max(this.b[1][1][1] - 1, this.b[x2][y2][z2]);
        }
        for (int x3 = 0; x3 < 2; ++x3) {
            for (y = 0; y < 2; ++y) {
                for (z = 0; z < 2; ++z) {
                    int x1 = x3 * 2;
                    int y1 = y * 2;
                    int z1 = z * 2;
                    int sxyz = this.s[x1][y1][z1];
                    int bxyz = this.b[x1][y1][z1];
                    boolean txyz = this.t[x1][y1][z1];
                    int sxz = this.s[x1][1][z1];
                    int sxy = this.s[x1][y1][1];
                    int syz = this.s[1][y1][z1];
                    int bxz = this.b[x1][1][z1];
                    int bxy = this.b[x1][y1][1];
                    int byz = this.b[1][y1][z1];
                    boolean txz = this.t[x1][1][z1];
                    boolean txy = this.t[x1][y1][1];
                    boolean tyz = this.t[1][y1][z1];
                    int sx = this.s[x1][1][1];
                    int sy = this.s[1][y1][1];
                    int sz = this.s[1][1][z1];
                    int bx = this.b[x1][1][1];
                    int by = this.b[1][y1][1];
                    int bz = this.b[1][1][z1];
                    boolean tx = this.t[x1][1][1];
                    boolean ty = this.t[1][y1][1];
                    boolean tz = this.t[1][1][z1];
                    this.skyLight[0][x3][y][z] = this.combine(sx, sxz, sxy, txz || txy ? sxyz : sx, tx, txz, txy, txz || txy ? txyz : tx);
                    this.blockLight[0][x3][y][z] = this.combine(bx, bxz, bxy, txz || txy ? bxyz : bx, tx, txz, txy, txz || txy ? txyz : tx);
                    this.skyLight[1][x3][y][z] = this.combine(sy, sxy, syz, txy || tyz ? sxyz : sy, ty, txy, tyz, txy || tyz ? txyz : ty);
                    this.blockLight[1][x3][y][z] = this.combine(by, bxy, byz, txy || tyz ? bxyz : by, ty, txy, tyz, txy || tyz ? txyz : ty);
                    this.skyLight[2][x3][y][z] = this.combine(sz, syz, sxz, tyz || txz ? sxyz : sz, tz, tyz, txz, tyz || txz ? txyz : tz);
                    this.blockLight[2][x3][y][z] = this.combine(bz, byz, bxz, tyz || txz ? bxyz : bz, tz, tyz, txz, tyz || txz ? txyz : tz);
                }
            }
        }
    }

    @Override
    protected float calculateBrightness(float[] position) {
        int sz;
        float x = position[0];
        float y = position[1];
        float z = position[2];
        int sx = x < 0.0f ? 1 : 2;
        int sy = y < 0.0f ? 1 : 2;
        int n = sz = z < 0.0f ? 1 : 2;
        if (x < 0.0f) {
            x += 1.0f;
        }
        if (y < 0.0f) {
            y += 1.0f;
        }
        if (z < 0.0f) {
            z += 1.0f;
        }
        float a = 0.0f;
        a += this.ao[sx - 1][sy - 1][sz - 1] * (1.0f - x) * (1.0f - y) * (1.0f - z);
        a += this.ao[sx - 1][sy - 1][sz - 0] * (1.0f - x) * (1.0f - y) * (0.0f + z);
        a += this.ao[sx - 1][sy - 0][sz - 1] * (1.0f - x) * (0.0f + y) * (1.0f - z);
        a += this.ao[sx - 1][sy - 0][sz - 0] * (1.0f - x) * (0.0f + y) * (0.0f + z);
        a += this.ao[sx - 0][sy - 1][sz - 1] * (0.0f + x) * (1.0f - y) * (1.0f - z);
        a += this.ao[sx - 0][sy - 1][sz - 0] * (0.0f + x) * (1.0f - y) * (0.0f + z);
        a += this.ao[sx - 0][sy - 0][sz - 1] * (0.0f + x) * (0.0f + y) * (1.0f - z);
        a += this.ao[sx - 0][sy - 0][sz - 0] * (0.0f + x) * (0.0f + y) * (0.0f + z);
        a = Mth.m_14036_((float)a, (float)0.0f, (float)1.0f);
        return a;
    }

    @Override
    protected int calculateLightmap(float[] position, byte[] normal) {
        int block = (int)(this.calcLightmap(this.blockLight, position[0], position[1], position[2]) * 240.0f);
        int sky = (int)(this.calcLightmap(this.skyLight, position[0], position[1], position[2]) * 240.0f);
        return block | sky << 16;
    }

    private float combine(int c, int s1, int s2, int s3, boolean t0, boolean t1, boolean t2, boolean t3) {
        if (c == 0 && !t0) {
            c = Math.max(0, Math.max(s1, s2) - 1);
        }
        if (s1 == 0 && !t1) {
            s1 = Math.max(0, c - 1);
        }
        if (s2 == 0 && !t2) {
            s2 = Math.max(0, c - 1);
        }
        if (s3 == 0 && !t3) {
            s3 = Math.max(0, Math.max(s1, s2) - 1);
        }
        return (float)(c + s1 + s2 + s3) / 60.0f;
    }

    protected float calcLightmap(float[][][][] light, float x, float y, float z) {
        float l2;
        if ((l2 = (x *= 2.0f) * x + (y *= 2.0f) * y + (z *= 2.0f) * z) > 5.98f) {
            float s = (float)Math.sqrt(5.98f / l2);
            x *= s;
            y *= s;
            z *= s;
        }
        float ax = x > 0.0f ? x : -x;
        float ay = y > 0.0f ? y : -y;
        float az = z > 0.0f ? z : -z;
        float e1 = 1.0001f;
        if (ax > 1.9999f && ay <= e1 && az <= e1) {
            x = x < 0.0f ? -1.9999f : 1.9999f;
        } else if (ay > 1.9999f && az <= e1 && ax <= e1) {
            y = y < 0.0f ? -1.9999f : 1.9999f;
        } else if (az > 1.9999f && ax <= e1 && ay <= e1) {
            z = z < 0.0f ? -1.9999f : 1.9999f;
        }
        ax = x > 0.0f ? x : -x;
        ay = y > 0.0f ? y : -y;
        float f = az = z > 0.0f ? z : -z;
        if (ax <= e1 && ay + az > 2.9999f) {
            s = 2.9999f / (ay + az);
            y *= s;
            z *= s;
        } else if (ay <= e1 && az + ax > 2.9999f) {
            s = 2.9999f / (az + ax);
            z *= s;
            x *= s;
        } else if (az <= e1 && ax + ay > 2.9999f) {
            s = 2.9999f / (ax + ay);
            x *= s;
            y *= s;
        } else if (ax + ay + az > 3.9999f) {
            s = 3.9999f / (ax + ay + az);
            x *= s;
            y *= s;
            z *= s;
        }
        float l = 0.0f;
        float s = 0.0f;
        for (int ix = 0; ix <= 1; ++ix) {
            for (int iy = 0; iy <= 1; ++iy) {
                for (int iz = 0; iz <= 1; ++iz) {
                    float vx = x * (float)(1 - ix * 2);
                    float vy = y * (float)(1 - iy * 2);
                    float vz = z * (float)(1 - iz * 2);
                    float s3 = vx + vy + vz + 4.0f;
                    float sx = vy + vz + 3.0f;
                    float sy = vz + vx + 3.0f;
                    float sz = vx + vy + 3.0f;
                    float bx = (2.0f * vx + vy + vz + 6.0f) / (s3 * sy * sz * (vx + 2.0f));
                    s += bx;
                    l += bx * light[0][ix][iy][iz];
                    float by = (2.0f * vy + vz + vx + 6.0f) / (s3 * sz * sx * (vy + 2.0f));
                    s += by;
                    l += by * light[1][ix][iy][iz];
                    float bz = (2.0f * vz + vx + vy + 6.0f) / (s3 * sx * sy * (vz + 2.0f));
                    s += bz;
                    l += bz * light[2][ix][iy][iz];
                }
            }
        }
        l /= s;
        l = Mth.m_14036_((float)l, (float)0.0f, (float)1.0f);
        return l;
    }
}

