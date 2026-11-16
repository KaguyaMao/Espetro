package org.espetro.naval;

import org.espetro.mapconfig.VehSpawnSnapshot;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 把舰上的载具点合并进本轮 {@link VehSpawnSnapshot}（只改内存，不落盘）。
 *
 * <p>规则：</p>
 * <ul>
 *   <li>覆盖表里出现的类型：attack 侧整体替换为舰上的点（顺序即编制槽位顺序）；</li>
 *   <li>defend 侧若覆盖表给了就用它，否则复制 attack 点补齐（现有校验要求两侧齐全）；</li>
 *   <li>覆盖表没提到的类型保持地图原值。</li>
 * </ul>
 */
public final class VehSpawnMerge {

    private VehSpawnMerge() {
    }

    public static VehSpawnSnapshot merge(VehSpawnSnapshot base,
                                         Map<String, List<AmphibiousConfig.PointEntry>> override) {
        if (base == null || override == null || override.isEmpty()) {
            return base;
        }
        Map<String, List<VehSpawnSnapshot.SpawnPoint>> merged = new LinkedHashMap<>();
        for (Map.Entry<String, List<VehSpawnSnapshot.SpawnPoint>> entry
            : base.spawnPointsByType.entrySet()) {
            merged.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        Set<String> types = new LinkedHashSet<>(base.vehicleTypes);
        List<String> errors = new ArrayList<>(base.errors);

        for (Map.Entry<String, List<AmphibiousConfig.PointEntry>> entry : override.entrySet()) {
            String type = entry.getKey().toLowerCase(Locale.ROOT);
            List<VehSpawnSnapshot.SpawnPoint> basePoints = merged.getOrDefault(type, List.of());
            List<VehSpawnSnapshot.SpawnPoint> points = new ArrayList<>();
            int index = 0;
            for (AmphibiousConfig.PointEntry point : entry.getValue()) {
                VehSpawnSnapshot.Pose attack = pose(point.attack);
                VehSpawnSnapshot.Pose defend = pose(point.defend);
                if (attack == null) {
                    if (defend == null) {
                        index++;
                        continue;
                    }
                    attack = defend;
                }
                if (defend == null) {
                    // 守方点位必须保留地图原值；复制攻方会导致两队载具刷在同一处。
                    defend = baseDefend(basePoints, index);
                }
                if (defend == null) {
                    index++;
                    continue;
                }
                points.add(new VehSpawnSnapshot.SpawnPoint(point.id, attack, defend));
                index++;
            }
            if (points.isEmpty()) {
                continue;
            }
            merged.put(type, points);
            types.add(type);
        }
        return new VehSpawnSnapshot(new ArrayList<>(types), merged, errors);
    }

    /** 取地图原守方点位：同序号优先，越界取最后一个有点位的。 */
    private static VehSpawnSnapshot.Pose baseDefend(List<VehSpawnSnapshot.SpawnPoint> basePoints, int index) {
        if (basePoints.isEmpty()) {
            return null;
        }
        VehSpawnSnapshot.SpawnPoint chosen = basePoints.get(Math.min(index, basePoints.size() - 1));
        if (chosen.defend() != null) {
            return chosen.defend();
        }
        for (int i = basePoints.size() - 1; i >= 0; i--) {
            if (basePoints.get(i).defend() != null) {
                return basePoints.get(i).defend();
            }
        }
        return null;
    }

    private static VehSpawnSnapshot.Pose pose(AmphibiousConfig.Point point) {
        if (point == null) {
            return null;
        }
        return new VehSpawnSnapshot.Pose(point.x(), point.y(), point.z(), point.yaw());
    }
}
