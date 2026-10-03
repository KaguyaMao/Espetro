package org.espetro.vehicle;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.espetro.Espetro;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 载具原版操作白名单。
 *
 * <p>配置里列出的载具类型 <b>不受到本模组的任何交互类修改</b>：读条上车/下车/换座、
 * 座位权限、敌对阵营禁乘、小队归属准入、残骸加速消失、载具内标点座位规则、
 * 换职业/退队踢下座位等一律豁免，完全使用 Superb Warfare（SBW）及其载具包原生交互。</p>
 *
 * <p>仍然保留（不豁免）：主基地无敌保护、阵营归属与部署记账（{@code espetro_vehicle}
 * tag、{@code VehicleManager.activeVehicleData}、兵损扣减、自动刷新、回合清理、{@code /veh} 认领）。</p>
 *
 * <p>判定按 <b>实体类型注册名</b>（如 {@code fcp:bmp2}、{@code superbwarfare:ztz_99a}），
 * 不带实例/UUID 概念；支持整命名空间通配 {@code "fcp:*"}。
 * 命中判定只做 Set 查找，可在 {@code hurt}/{@code baseTick} 等热路径使用。</p>
 *
 * <p>本类同时被服务端与客户端引用，因此只依赖 {@link BuiltInRegistries}/{@link EntityType}
 * 等两侧通用的类，绝不引用客户端专用类。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class VehicleNativeWhitelist {

    /** 精确实体类型注册名（全小写），如 {@code fcp:bmp2}。 */
    private static volatile Set<String> exactIds = Set.of();

    /** 命名空间通配（全小写），如 {@code fcp}（来自配置 {@code "fcp:*"}）。 */
    private static volatile Set<String> namespaces = Set.of();

    /** 总开关快照。 */
    private static volatile boolean enabled = false;

    /** 是否已从配置构建过一次（懒初始化 + 配置加载/重载刷新）。 */
    private static volatile boolean built = false;

    private VehicleNativeWhitelist() {
    }

    /** 总开关；未构建过时按需从配置构建一次。 */
    public static boolean isEnabled() {
        ensureBuilt();
        return enabled;
    }

    /** 实体是否属于原版操作白名单；{@code null} → false。 */
    public static boolean isNative(@Nullable Entity e) {
        return e != null && isNative(e.getType());
    }

    /**
     * 实体类型是否属于原版操作白名单。
     * <p>先用 {@link #isEnabled()} 短路，再按注册名命中"精确 id"或"命名空间通配"。</p>
     */
    public static boolean isNative(@Nullable EntityType<?> type) {
        if (type == null) {
            return false;
        }
        if (!isEnabled()) {
            return false;
        }
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        if (key == null) {
            return false;
        }
        // 热路径：volatile 只读一次，后续全部为不可变 Set 查找（无字符串分配）。
        Set<String> exact = exactIds;
        String id = key.toString();
        if (exact.contains(id)) {
            return true;
        }
        // 注册名理论上恒为小写；兜底处理大小写异常的注册来源。
        if (!exact.isEmpty() && hasUpperCase(id)
            && exact.contains(id.toLowerCase(Locale.ROOT))) {
            return true;
        }
        Set<String> ns = namespaces;
        if (ns.isEmpty()) {
            return false;
        }
        String namespace = key.getNamespace();
        if (ns.contains(namespace)) {
            return true;
        }
        return hasUpperCase(namespace) && ns.contains(namespace.toLowerCase(Locale.ROOT));
    }

    /** 缺少 String#chars 短路时的低成本检查，避免热路径无谓的 toLowerCase 分配。 */
    private static boolean hasUpperCase(String value) {
        for (int i = 0; i < value.length(); i++) {
            if (Character.isUpperCase(value.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从配置重建白名单缓存。配置缺省/异常一律兜底为空集，绝不抛异常。
     * <p>由配置加载/重载事件与懒初始化调用。</p>
     */
    public static void rebuild() {
        boolean newEnabled = true;
        Set<String> exact = new LinkedHashSet<>();
        Set<String> wildcards = new LinkedHashSet<>();
        Set<String> wildcardDisplay = new LinkedHashSet<>();
        try {
            newEnabled = VehicleInteractionConfig.NATIVE_WHITELIST_ENABLED.get();
        } catch (Throwable t) {
            newEnabled = false;
            Espetro.LOGGER.warn("[载具白名单] 读取 nativeWhitelistEnabled 失败，本次按未启用处理: {}", t.toString());
        }
        try {
            // 逗号分隔字符串（Forge 的列表类型在读取时会被静默重置为默认，故不用列表）
            String raw = VehicleInteractionConfig.NATIVE_VEHICLES.get();
            if (raw != null) {
                for (String part : raw.split(",")) {
                    String entry = part;
                    String id = entry.trim().toLowerCase(Locale.ROOT);
                    if (id.isEmpty()) {
                        continue;
                    }
                    if (id.endsWith(":*")) {
                        String namespace = id.substring(0, id.length() - 2).trim();
                        if (!namespace.isEmpty()) {
                            wildcards.add(namespace);
                            wildcardDisplay.add(namespace + ":*");
                        }
                    } else {
                        exact.add(id);
                    }
                }
            }
        } catch (Throwable t) {
            exact.clear();
            wildcards.clear();
            wildcardDisplay.clear();
            Espetro.LOGGER.warn("[载具白名单] 读取 nativeVehicles 失败，本次按空集处理: {}", t.toString());
        }

        exactIds = Set.copyOf(exact);
        namespaces = Set.copyOf(wildcards);
        enabled = newEnabled;
        built = true;
        Espetro.LOGGER.info("[载具白名单] enabled={}, 精确 {} 项, 命名空间 {} 个: {} / {}",
            newEnabled, exact.size(), wildcards.size(), exact, wildcardDisplay);
    }

    private static void ensureBuilt() {
        if (!built) {
            synchronized (VehicleNativeWhitelist.class) {
                if (!built) {
                    rebuild();
                }
            }
        }
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        rebuild();
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        rebuild();
    }
}
