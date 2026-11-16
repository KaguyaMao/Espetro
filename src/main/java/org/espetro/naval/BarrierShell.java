package org.espetro.naval;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * 舰船屏障几何生成。
 *
 * <p>P0 只实现 {@code box} 模式：沿舰体外接立方体的四壁 + 可选顶盖生成一圈
 * {@code minecraft:barrier}。放置/移除复用
 * {@code GameStateManager} 的攻方等待屏障队列，因此分帧与回滚逻辑一致。</p>
 */
public final class BarrierShell {

    private BarrierShell() {
    }

    /**
     * 生成包围盒外壳。
     *
     * @param min    包围盒最小角
     * @param max    包围盒最大角
     * @param height 从 {@code min.y} 起算的墙高
     * @param cap    是否加顶盖
     */
    public static List<BlockPos> box(BlockPos min, BlockPos max, int height, boolean cap) {
        List<BlockPos> out = new ArrayList<>();
        int minX = min.getX();
        int maxX = max.getX();
        int minZ = min.getZ();
        int maxZ = max.getZ();
        int baseY = min.getY();
        int top = Math.max(1, height);

        for (int y = baseY; y < baseY + top; y++) {
            for (int x = minX; x <= maxX; x++) {
                out.add(new BlockPos(x, y, minZ));
                out.add(new BlockPos(x, y, maxZ));
            }
            for (int z = minZ + 1; z < maxZ; z++) {
                out.add(new BlockPos(minX, y, z));
                out.add(new BlockPos(maxX, y, z));
            }
        }
        if (cap) {
            int capY = baseY + top;
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    out.add(new BlockPos(x, capY, z));
                }
            }
        }
        return out;
    }
}
