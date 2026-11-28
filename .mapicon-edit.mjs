// 一次性文本替换脚本：为载具编制 JSON 增加 map_icon 数据驱动地图图标支持。
// 每个补丁断言「恰好 1 次匹配」，否则整体中止（不写入任何文件）。
import { readFileSync, writeFileSync, copyFileSync, mkdirSync } from 'node:fs';

const BACKUP_DIR = 'D:\\minecraft\\mcsm-tools';
const SUFFIX = '.bak-mapicon';

const files = {
  faction: 'D:\\minecraft\\modp\\Espetro\\src\\main\\java\\org\\espetro\\team\\FactionDataLoader.java',
  config: 'D:\\minecraft\\modp\\Espetro\\src\\main\\java\\org\\espetro\\vehicle\\VehicleConfig.java',
  manager: 'D:\\minecraft\\modp\\Espetro\\src\\main\\java\\org\\espetro\\vehicle\\VehicleManager.java',
  hud: 'D:\\minecraft\\modp\\EsPoints\\src\\main\\java\\com\\example\\espoints\\hud\\TacticalMapHUD.java',
  icons: 'D:\\minecraft\\modp\\EsPoints\\src\\main\\java\\com\\example\\espoints\\tactical\\FriendlyVehicleIcons.java',
  sync: 'D:\\minecraft\\modp\\EsPoints\\src\\main\\java\\com\\example\\espoints\\tactical\\FriendlyVehicleMapSync.java',
  message: 'D:\\minecraft\\modp\\EsPoints\\src\\main\\java\\com\\example\\espoints\\network\\SyncFriendlyVehiclesMessage.java',
  network: 'D:\\minecraft\\modp\\EsPoints\\src\\main\\java\\com\\example\\espoints\\network\\NetworkHandler.java',
  docs: 'D:\\minecraft\\modp\\Espetro\\docs\\CONFIGURATION.md',
};

const sources = new Map();
/** 原文件换行风格：替换按 LF 匹配，写回时还原。 */
const lineEndings = new Map();
for (const [key, path] of Object.entries(files)) {
  const raw = readFileSync(path, 'utf8');
  lineEndings.set(key, raw.includes('\r\n') ? '\r\n' : '\n');
  sources.set(key, raw.replace(/\r\n/g, '\n'));
}

const edited = new Set();

function patch(key, find, replace, label) {
  const text = sources.get(key);
  const first = text.indexOf(find);
  if (first < 0) throw new Error(`[${label}] 未找到匹配片段（${key}）`);
  const second = text.indexOf(find, first + 1);
  if (second >= 0) throw new Error(`[${label}] 匹配多于 1 次（${key}）`);
  sources.set(key, text.slice(0, first) + replace + text.slice(first + find.length));
  edited.add(key);
  const count = sources.get(key).split(replace).length - 1;
  if (count < 1) throw new Error(`[${label}] 替换未生效（${key}）`);
}

/* ---------------- Espetro: FactionDataLoader.VehicleData ---------------- */
patch('faction',
  `        @SerializedName(value = "initial_deploy_delay_seconds",
            alternate = {"initialDeployDelaySeconds"})
        public InitialDeployDelayData initialDeployDelay;
    }`,
  `        @SerializedName(value = "initial_deploy_delay_seconds",
            alternate = {"initialDeployDelaySeconds"})
        public InitialDeployDelayData initialDeployDelay;
        /**
         * 可选：该车型在战术地图上使用的图标文件名，例如 {@code "map_trackedheavyifv.png"}。
         * 只写文件名（相对 {@code espoints:textures/gui/mark/friendly/}），不要写路径。
         * 缺失/为空 = 沿用 EsPoints 的车型关键字兜底图标。
         */
        @SerializedName(value = "map_icon", alternate = {"mapIcon", "icon", "mapicon"})
        public String mapIcon;
    }`,
  'FactionDataLoader.mapIcon');

/* ---------------- Espetro: VehicleConfig.VehicleTypeConfig ---------------- */
patch('config',
  `        /** 部署时写入载具集装箱的物品（"<itemId>[<snbt>] <count>"），slot 未单独指定时使用。 */
        public List<String> items = new ArrayList<>();`,
  `        /**
         * 战术地图图标文件名（如 {@code "map_trackedheavyifv.png"}），来自编制 JSON 的
         * {@code vehicles.<type>.map_icon}；null/空 = 沿用 EsPoints 的车型关键字兜底图标。
         * 只是同步给客户端的展示数据，不参与服务端逻辑。
         */
        @Nullable
        public String mapIcon;
        /** 部署时写入载具集装箱的物品（"<itemId>[<snbt>] <count>"），slot 未单独指定时使用。 */
        public List<String> items = new ArrayList<>();`,
  'VehicleConfig.mapIcon field');

patch('config',
  `                cfg.troopValue = Math.max(0, data.troopValue);
                cfg.nbt = firstNonBlank(data.nbt, null);`,
  `                cfg.troopValue = Math.max(0, data.troopValue);
                cfg.mapIcon = firstNonBlank(data.mapIcon, null);
                cfg.nbt = firstNonBlank(data.nbt, null);`,
  'VehicleConfig.applyActiveMap copy');

patch('config',
  `        cfg.troopValue = Math.max(0, vd.troopValue);
        cfg.vehicleCrewSeats = vd.vehicleCrewSeats;`,
  `        cfg.troopValue = Math.max(0, vd.troopValue);
        cfg.mapIcon = firstNonBlank(vd.mapIcon, null);
        cfg.vehicleCrewSeats = vd.vehicleCrewSeats;`,
  'VehicleConfig.buildVehicleConfig copy');

/* ---------------- Espetro: VehicleManager snapshot ---------------- */
patch('manager',
  `    public record TacticalVehicleSnapshot(UUID id, String type, String name, String team,
            double x, double z, float yaw, int passengers, int squadId, boolean loaded) { }`,
  `    public record TacticalVehicleSnapshot(UUID id, String type, String name, String team,
            double x, double z, float yaw, int passengers, int squadId, boolean loaded,
            @Nullable String mapIcon) {
        /** 兼容不含地图图标字段的旧调用点。 */
        public TacticalVehicleSnapshot(UUID id, String type, String name, String team,
                double x, double z, float yaw, int passengers, int squadId, boolean loaded) {
            this(id, type, name, team, x, z, yaw, passengers, squadId, loaded, null);
        }
    }`,
  'VehicleManager.TacticalVehicleSnapshot record');

patch('manager',
  `                vehicle == null ? 0 : vehicle.getYRot(), passengers,
                vehicle == null ? -1 : VehicleSquadOwnership.getSquadId(vehicle), vehicle != null));`,
  `                vehicle == null ? 0 : vehicle.getYRot(), passengers,
                vehicle == null ? -1 : VehicleSquadOwnership.getSquadId(vehicle), vehicle != null,
                mapIconFor(data)));`,
  'VehicleManager.snapshot fill');

patch('manager',
  `    /**
     * 返回 Espetro 追踪载具的所属阵营；不会加载区块或遍历实体。
     */`,
  `    /**
     * 取该载具类型在编制里配置的地图图标文件名；配置缺失或已清空时返回 null，
     * 由 EsPoints 客户端回退到车型关键字图标表。载具实体未加载时同样可用。
     */
    @Nullable
    private static String mapIconFor(ActiveVehicleData data) {
        if (data.vehicleType() == null) return null;
        var config = VehicleConfig.getVehicleConfig(data.factionId(), data.vehicleType());
        if (config == null || config.mapIcon == null) return null;
        String icon = config.mapIcon.trim();
        return icon.isEmpty() ? null : icon;
    }

    /**
     * 返回 Espetro 追踪载具的所属阵营；不会加载区块或遍历实体。
     */`,
  'VehicleManager.mapIconFor helper');

/* ---------------- EsPoints: TacticalMapHUD ---------------- */
patch('hud',
  `                    com.example.espoints.tactical.FriendlyVehicleIcons.texture(vehicle.type()),`,
  `                    com.example.espoints.tactical.FriendlyVehicleIcons.texture(vehicle.type(), vehicle.mapIcon()),`,
  'TacticalMapHUD icon call');

/* ---------------- EsPoints: FriendlyVehicleIcons ---------------- */
patch('icons',
  `import net.minecraft.resources.ResourceLocation;
import java.util.Locale;

public final class FriendlyVehicleIcons {
    private FriendlyVehicleIcons() { }`,
  `import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class FriendlyVehicleIcons {
    /** 编制 JSON 的 map_icon 只允许这种纯文件名，避免路径穿越与非法字符。 */
    private static final Pattern SAFE_ICON_NAME = Pattern.compile("[a-z0-9_]+\\\\.png");
    /** 贴图目录：只允许从这里挑图标。 */
    private static final String ICON_DIR = "textures/gui/mark/friendly/";
    /** 编制里配置过的自定义图标名缓存（避免逐帧检查资源存在性）。 */
    private static final Map<String, Boolean> CUSTOM_ICON_CACHE = new java.util.concurrent.ConcurrentHashMap<>();
    /** 资源管理器不可用（启动早期/测试环境）时置位，之后直接放行，交给纹理管理器处理。 */
    private static volatile boolean resourceLookupUnavailable;
    private FriendlyVehicleIcons() { }

    /**
     * 编制 JSON {@code map_icon} 给的图标优先；缺省、非法或文件不存在时回退到车型关键字表。
     */
    public static ResourceLocation texture(String type, String mapIcon) {
        String fallback = fileFor(type);
        if (mapIcon == null) return textureFor(fallback);
        String name = mapIcon.trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty()) return textureFor(fallback);
        if (!SAFE_ICON_NAME.matcher(name).matches()) return textureFor(fallback);
        return iconExists(name) ? textureFor(name) : textureFor(fallback);
    }

    private static ResourceLocation textureFor(String fileName) {
        return ResourceLocation.fromNamespaceAndPath("espoints", ICON_DIR + fileName);
    }

    /**
     * 数据驱动图标的存在性检查。第一次遇到某个名字时查一次资源包；不存在就永久回退，
     * 避免纹理管理器画出紫黑格。资源管理器不可用（启动早期/测试环境）时按可用处理。
     */
    private static boolean iconExists(String name) {
        Boolean cached = CUSTOM_ICON_CACHE.get(name);
        if (cached != null) return cached;
        if (resourceLookupUnavailable) return true;
        Boolean resolved = resolveIconPresence(name);
        if (resolved == null) {
            resourceLookupUnavailable = true;
            return true;
        }
        CUSTOM_ICON_CACHE.putIfAbsent(name, resolved);
        return resolved;
    }

    private static Boolean resolveIconPresence(String name) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) return null;
        try {
            return minecraft.getResourceManager().getResource(
                ResourceLocation.fromNamespaceAndPath("espoints", ICON_DIR + name)).isPresent();
        } catch (RuntimeException e) {
            return null;
        }
    }`,
  'FriendlyVehicleIcons.header');

patch('icons',
  `    public static ResourceLocation texture(String type) {
        return ResourceLocation.fromNamespaceAndPath("espoints", "textures/gui/mark/friendly/" + fileFor(type));
    }`,
  `    /** 兼容旧调用点：只按车型关键字表选图标。 */
    public static ResourceLocation texture(String type) {
        return textureFor(fileFor(type));
    }`,
  'FriendlyVehicleIcons.texture(String)');

/* ---------------- EsPoints: FriendlyVehicleMapSync ---------------- */
patch('sync',
  `                .map(v -> new SyncFriendlyVehiclesMessage.Vehicle(v.id(), bounded(v.type()), bounded(v.name()), v.x(), v.z(),
                    v.yaw(), v.passengers(), v.squadId(), v.loaded())).toList());`,
  `                .map(v -> new SyncFriendlyVehiclesMessage.Vehicle(v.id(), bounded(v.type()), bounded(v.name()), v.x(), v.z(),
                    v.yaw(), v.passengers(), v.squadId(), v.loaded(),
                    boundedIcon(v.mapIcon()))).toList());`,
  'FriendlyVehicleMapSync.map');

patch('sync',
  `    private static String bounded(String text) { return text == null ? "" : text.substring(0, Math.min(128, text.length())); }`,
  `    private static String bounded(String text) { return text == null ? "" : text.substring(0, Math.min(128, text.length())); }
    /** 编制里的地图图标文件名：允许 null（回退车型关键字表），过长直接丢弃。 */
    private static String boundedIcon(String icon) {
        if (icon == null) return null;
        String trimmed = icon.trim();
        return trimmed.length() > SyncFriendlyVehiclesMessage.MAX_ICON_LENGTH ? null : trimmed;
    }`,
  'FriendlyVehicleMapSync.boundedIcon');

/* ---------------- EsPoints: SyncFriendlyVehiclesMessage ---------------- */
patch('message',
  `    public static final int MAX_VEHICLES = 256;
    public record Vehicle(UUID id, String type, String name, double x, double z, float yaw,
                          int passengers, int squadId, boolean loaded) {
        public Vehicle {
            if (id == null || type == null || name == null || type.length() > 128 || name.length() > 128
                || !Double.isFinite(x) || !Double.isFinite(z) || !Float.isFinite(yaw)
                || passengers < 0 || passengers > 256 || squadId < -1) throw new IllegalArgumentException("Invalid vehicle");
        }
    }`,
  `    public static final int MAX_VEHICLES = 256;
    /** 编制 JSON map_icon 的文件名长度上限（正常都远小于此值）。 */
    public static final int MAX_ICON_LENGTH = 64;
    public record Vehicle(UUID id, String type, String name, double x, double z, float yaw,
                          int passengers, int squadId, boolean loaded, String mapIcon) {
        public Vehicle {
            if (id == null || type == null || name == null || type.length() > 128 || name.length() > 128
                || !Double.isFinite(x) || !Double.isFinite(z) || !Float.isFinite(yaw)
                || passengers < 0 || passengers > 256 || squadId < -1
                || (mapIcon != null && mapIcon.length() > MAX_ICON_LENGTH))
                throw new IllegalArgumentException("Invalid vehicle");
        }
        /** 兼容不携带地图图标的旧调用点（map_icon 缺失 = 客户端回退车型关键字表）。 */
        public Vehicle(UUID id, String type, String name, double x, double z, float yaw,
                       int passengers, int squadId, boolean loaded) {
            this(id, type, name, x, z, yaw, passengers, squadId, loaded, null);
        }
    }`,
  'SyncFriendlyVehiclesMessage.Vehicle');

patch('message',
  `            buf.writeVarInt(v.passengers()); buf.writeVarInt(v.squadId()); buf.writeBoolean(v.loaded());
        }`,
  `            buf.writeVarInt(v.passengers()); buf.writeVarInt(v.squadId()); buf.writeBoolean(v.loaded());
            boolean hasMapIcon = v.mapIcon() != null && !v.mapIcon().isEmpty();
            buf.writeBoolean(hasMapIcon);
            if (hasMapIcon) buf.writeUtf(v.mapIcon(), MAX_ICON_LENGTH);
        }`,
  'SyncFriendlyVehiclesMessage.encode');

patch('message',
  `            var v = new Vehicle(buf.readUUID(), buf.readUtf(128), buf.readUtf(128), buf.readDouble(),
                buf.readDouble(), buf.readFloat(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean());`,
  `            var v = new Vehicle(buf.readUUID(), buf.readUtf(128), buf.readUtf(128), buf.readDouble(),
                buf.readDouble(), buf.readFloat(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(),
                buf.readBoolean() ? buf.readUtf(MAX_ICON_LENGTH) : null);`,
  'SyncFriendlyVehiclesMessage.decode');

/* ---------------- EsPoints: PROTOCOL_VERSION ---------------- */
patch('network',
  `    public static final String PROTOCOL_VERSION = "18";`,
  `    public static final String PROTOCOL_VERSION = "19";`,
  'NetworkHandler.PROTOCOL_VERSION');

/* ---------------- Espetro docs ---------------- */
patch('docs',
  `| \`initial_deploy_delay_seconds.defend\` | 0 | 此编制作为防守方时，从布防阶段开始到系统自动生成首批载具的等待秒数 |`,
  `| \`initial_deploy_delay_seconds.defend\` | 0 | 此编制作为防守方时，从布防阶段开始到系统自动生成首批载具的等待秒数 |
| \`map_icon\` | 空（按车型关键字兜底） | 该车型在战术地图上使用的图标**文件名**，例如 \`"map_trackedheavyifv.png"\` |`,
  'CONFIGURATION.md field row');

patch('docs',
  `编制文件不再保存部署坐标。部署位置完全由获胜地图的 \`VehSpawn.json\` 决定。`,
  `#### 载具地图图标 \`map_icon\`

\`vehicles.<vehicle_type>\` 可选的 \`map_icon\` 字段用于指定该车型在己方战术地图上显示的图标：

- 只写**文件名**，不要写路径或命名空间：\`"map_icon": "map_trackedheavyifv.png"\`。
- 客户端只会从 \`espoints:textures/gui/mark/friendly/\` 目录取图，合法格式为 \`[a-z0-9_]+\\.png\`（小写字母、数字、下划线）。
- 可选文件名以 EsPoints 的 \`assets/espoints/textures/gui/mark/friendly/\` 目录为准，例如
  \`map_tank.png\`、\`map_ifv.png\`、\`map_trackedifv.png\`、\`map_trackedheavyifv.png\`、\`map_apc.png\`、
  \`map_trackedapc.png\`、\`map_jeep.png\`、\`map_jeep_antitank.png\`、\`map_jeep_turret.png\`、
  \`map_truck_logistics.png\`、\`map_truck_transport.png\`、\`map_truck_antiair.png\`、
  \`map_transporthelo.png\`、\`map_attackhelo.png\`、\`map_uav.png\`、\`map_antiair.png\`、\`map_boat.png\`、
  \`map_motorcycle.png\`、\`map_genericinfantry.png\` 以及 \`t_map_*.png\` 系列。
- \`map_icon\` 缺失、为空、格式非法，或对应贴图不存在时，客户端自动回退到旧的关键字图标表（坦克/步战/APC/卡车/直升机……），不会显示紫黑格，也不会崩。
- 服务端只在\`编制 JSON → 载具配置 → 战术地图同步包\`之间透传这个文件名，不做任何贴图校验；改图标只影响客户端显示，不影响载具实体与平衡。

\`map_icon\` 是随 EsPoints 协议版本 19 一起新增的同步字段：只升级 Espetro 不升级 EsPoints（或反之）会被握手直接拒绝。

编制文件不再保存部署坐标。部署位置完全由获胜地图的 \`VehSpawn.json\` 决定。`,
  'CONFIGURATION.md section');

/* ---------------- 备份 + 写入 ---------------- */
mkdirSync(BACKUP_DIR, { recursive: true });
const written = [];
for (const key of edited) {
  const path = files[key];
  const backup = BACKUP_DIR + '\\' + path.split('\\').pop() + SUFFIX;
  copyFileSync(path, backup);
  const text = sources.get(key);
  const eol = lineEndings.get(key);
  writeFileSync(path, eol === '\r\n' ? text.replace(/\n/g, '\r\n') : text, 'utf8');
  written.push(`${path}  (backup: ${backup})`);
}
console.log('已修改文件:');
for (const line of written) console.log('  ' + line);
