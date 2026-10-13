package org.espetro.team;

import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;

/**
 * 通过反射调用 ESPoints 的火炮覆盖区同步接口。
 *
 * <p>依赖方向是 ESPoints → Espetro，所以这里只能反射调用；
 * ESPoints 缺失或版本较旧时静默跳过（不影响炮击本身）。</p>
 */
public final class ArtilleryZoneBridge {

    private static final String TARGET_CLASS = "com.example.espoints.network.SyncArtilleryZonesMessage";

    /** 一次覆盖的持续时长（毫秒）：与火炮脚本 2 发校射 + 6 轮覆盖大致对齐。 */
    public static final long ZONE_DURATION_MS = 70_000L;

    /** 155 火炮的覆盖半径（格）：与 KubeJS 脚本里的 impactRadius 保持一致。 */
    public static final double COVERAGE_RADIUS = 45.0D;

    private static volatile boolean unavailable;

    private ArtilleryZoneBridge() {
    }

    /**
     * 广播一块火炮覆盖区（仅该队伍可见）。
     *
     * @param kind "circle"（定点炮击）或 "line"（徐进弹幕）
     */
    public static void showZone(String team, String kind, double x, double z,
                                double endX, double endZ, double radius,
                                String icon, String dimension, long durationMs) {
        if (unavailable || team == null || team.isBlank() || !ModList.get().isLoaded("espoints")) {
            return;
        }
        try {
            Class<?> target = Class.forName(TARGET_CLASS);
            target.getMethod("showZone",
                    String.class, String.class, double.class, double.class,
                    double.class, double.class, double.class, String.class,
                    String.class, long.class)
                .invoke(null, team, kind, x, z, endX, endZ, radius, icon, dimension, durationMs);
        } catch (ClassNotFoundException | NoSuchMethodException legacy) {
            unavailable = true;   // 旧版 ESPoints 没有该接口：跳过即可
        } catch (ReflectiveOperationException error) {
            Espetro.LOGGER.warn("广播火炮覆盖区失败", error);
        }
    }
}
