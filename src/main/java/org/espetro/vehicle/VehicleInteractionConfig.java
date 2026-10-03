package org.espetro.vehicle;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/** Hold-to-mount / dismount / seat-switch timings (common config). */
public final class VehicleInteractionConfig {

    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue MOUNT_DELAY_TICKS;
    public static final ForgeConfigSpec.IntValue DISMOUNT_DELAY_TICKS;
    public static final ForgeConfigSpec.IntValue SEAT_SWITCH_DELAY_TICKS;
    public static final ForgeConfigSpec.DoubleValue MOUNT_MAX_DISTANCE;
    /** 载具原版操作白名单总开关。 */
    public static final ForgeConfigSpec.BooleanValue NATIVE_WHITELIST_ENABLED;
    /** 载具原版操作白名单：实体类型注册名列表，支持整命名空间通配。 */
    public static final ForgeConfigSpec.ConfigValue<String> NATIVE_VEHICLES;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        b.push("vehicle");
        MOUNT_DELAY_TICKS = b.comment("Ticks holding INTERACT on wheel center to mount (0 = instant)")
            .defineInRange("mountDelayTicks", 60, 0, 20 * 60);
        DISMOUNT_DELAY_TICKS = b.comment("Ticks holding INTERACT to dismount (0 = native SBW)")
            .defineInRange("dismountDelayTicks", 60, 0, 20 * 60);
        SEAT_SWITCH_DELAY_TICKS = b.comment("Ticks holding Shift before a seat change is allowed")
            .defineInRange("seatSwitchDelayTicks", 100, 0, 20 * 60);
        MOUNT_MAX_DISTANCE = b.comment("Max distance from vehicle while mounting")
            .defineInRange("mountMaxDistance", 5.0, 1.0, 16.0);
        NATIVE_WHITELIST_ENABLED = b.comment(
                "载具原版操作白名单总开关。",
                "开启后，nativeVehicles 中列出的载具不受本模组任何交互类修改：",
                "读条上车/下车/换座、座位权限与载具组员座位、敌对阵营禁乘、小队归属准入、",
                "残骸加速消失、载具内标点座位规则、换职业/退队踢下座位全部豁免，",
                "完全使用 Superb Warfare（SBW）及其载具包的原生交互方式。",
                "仍然保留：主基地无敌保护、阵营归属与部署记账（兵损/自动刷新/回合清理/veh 认领）。")
            .define("nativeWhitelistEnabled", true);
        NATIVE_VEHICLES = b.comment(
                "载具原版操作白名单：元素是实体类型注册名（entity type registry name），",
                "如 fcp:bmp2、superbwarfare:ztz_99a；不带实例/UUID 概念。",
                "支持整命名空间通配：\"fcp:*\" 表示 fcp 命名空间下的全部实体类型。",
                "示例: [\"fcp:bmp2\", \"superbwarfare:ztz_99a\"]")
            // 注意：默认值必须是可变列表（new ArrayList<>()）。若用不可变的 List.of()，
            // Forge 在读取配置文件时会静默把该项重置为默认，白名单将永远读成空列表（实测）。
            .define("nativeVehicles", "superbwarfare:drone,superbwarfare:tow,superbwarfare:container,dragonrise_reforge:hj8,dragonrise_reforge:9m133,dragonrise_reforge:m2,dragonrise_reforge:qjz89,dragonrise_reforge:dshk");
        b.pop();
        SPEC = b.build();
    }

    private VehicleInteractionConfig() {
    }

    public static int mountDelayTicks() {
        return MOUNT_DELAY_TICKS.get();
    }

    public static int dismountDelayTicks() {
        return DISMOUNT_DELAY_TICKS.get();
    }

    public static int seatSwitchDelayTicks() {
        return SEAT_SWITCH_DELAY_TICKS.get();
    }

    public static double mountMaxDistance() {
        return MOUNT_MAX_DISTANCE.get();
    }
}
