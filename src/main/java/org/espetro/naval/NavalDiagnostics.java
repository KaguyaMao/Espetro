package org.espetro.naval;

import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

/**
 * 两栖配置自检：不激活对局，只读 EsConfig/amphibious.json 并试加载每艘舰船的结构模板。
 *
 * <p>用于在开图前确认「舰船结构能被读出来、尺寸/方块数/标记数量正常」，
 * 避免等到激活时才发现结构是 0×0×0。</p>
 */
public final class NavalDiagnostics {

    private NavalDiagnostics() {
    }

    public static List<String> describe(MinecraftServer server) {
        List<String> lines = new ArrayList<>();
        for (org.espetro.mapconfig.ActiveMapConfig map
            : org.espetro.mapconfig.ExternalConfigBootstrap.getAllMaps()) {
            AmphibiousConfig config = map.amphibious;
            if (config == null) {
                continue;
            }
            lines.add("§6[两栖] §f" + map.displayName + " §7{" + map.mapFolder + "}§7 enabled="
                + config.enabled + " ships=" + config.ships.size()
                + (config.assaultFaction == null ? "" : " assault_faction=" + config.assaultFaction));
            for (String error : config.errors) {
                lines.add("  §c配置错误: " + error);
            }
            for (AmphibiousConfig.Ship ship : config.ships) {
                try {
                    NavalStructureService.Loaded loaded = NavalStructureService.load(server, ship);
                    lines.add("  §a" + ship.id + " §7[" + ship.role + " " + ship.source + "] §f"
                        + ship.structure + " §7size=" + loaded.size.toShortString()
                        + " blocks=" + loaded.blockCount
                        + " markers=" + loaded.markers.size()
                        + " anchor=" + ship.x + "," + ship.y + "," + ship.z);
                } catch (Exception e) {
                    lines.add("  §c" + ship.id + " 加载失败: " + e.getMessage());
                }
            }
        }
        if (lines.isEmpty()) {
            lines.add("§7没有任何地图声明两栖配置（EsConfig/amphibious.json）");
        }
        return lines;
    }
}
