package org.espetro.vehicle;

import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;

import javax.annotation.Nullable;

/**
 * 载具内换职白名单（完全数据驱动，不绑定任何阵营前缀）。
 *
 * <p>编制 JSON 的 {@code vehicles.&lt;type&gt;} 用 {@code fightveh} / {@code supplyveh} 标注载具类别：</p>
 * <ul>
 *   <li>显式 {@code fightveh: true} 的步兵战斗载具允许更换完整职业列表；</li>
 *   <li>{@code supplyveh: true} 的补给载具、两项都显式 {@code false} 的载具，
 *       以及未声明任何分类标签的旧载具，只能换成编制中的载具组员（驾驶员）与载具队长。</li>
 * </ul>
 *
 * <p>“载具组员 / 载具队长”由职业自身的 {@code vehicle_crew: true} 标签识别，
 * 旧配置回退到图标 {@code crewman}（载具组员/驾驶员）与 {@code lead_crewman}（载具队长），
 * 因此新增或改名职业后行为自动跟随，不需要改代码。</p>
 */
public final class VehicleClassChangePolicy {

    /** 在受限载具内选择了白名单之外的职业时的提示。 */
    public static final String DENIED_MESSAGE = "§c该载具只能更换驾驶员/载具队长职业。";

    /** 旧配置的图标回退：crewman = 载具组员/驾驶员，lead_crewman = 载具队长。 */
    private static final String ICON_CREW = "crewman";
    private static final String ICON_CREW_LEADER = "lead_crewman";

    private VehicleClassChangePolicy() {
    }

    /**
     * 职业是否属于载具组员系列（驾驶员/载具队长）。
     *
     * @param configured 编制 JSON 的 {@code vehicle_crew}，null 表示旧配置需按图标回退
     * @param icon       编制 JSON 的 {@code icon} 短名
     */
    public static boolean resolvesVehicleCrew(@Nullable Boolean configured, @Nullable String icon) {
        if (configured != null) {
            return configured;
        }
        return isVehicleCrewIcon(icon);
    }

    /** 仅按图标判定（客户端只有 {@code ClassEntry.icon}，没有 {@code vehicle_crew} 字段）。 */
    public static boolean isVehicleCrewIcon(@Nullable String icon) {
        if (icon == null) {
            return false;
        }
        String slug = icon.trim();
        return ICON_CREW.equalsIgnoreCase(slug) || ICON_CREW_LEADER.equalsIgnoreCase(slug);
    }

    /** 职业定义是否属于载具组员系列。 */
    public static boolean isVehicleCrewClass(@Nullable FactionDataLoader.ClassKitData kit) {
        return kit != null && resolvesVehicleCrew(kit.vehicleCrew, kit.icon);
    }

    /**
     * 该载具是否允许完整职业列表。
     *
     * <p>只有编制 JSON 里显式写了 {@code fightveh: true} 的步兵战斗载具可以；
     * 兼容回退（两项都缺失）来的“无标签载具”与补给载具一样受限。</p>
     */
    public static boolean allowsFullClassChange(@Nullable VehicleConfig.VehicleTypeConfig config) {
        return config != null && config.explicitFightVehicle;
    }

    /**
     * 指定职业在该载具内是否可选。
     *
     * <p>编制未加载或职业 id 未知时返回 true，把判定交回原有职业校验链路，
     * 避免在这里重复实现“无效职业”的报错。</p>
     */
    public static boolean isClassAllowed(@Nullable VehicleConfig.VehicleTypeConfig config,
                                         @Nullable String classId) {
        if (allowsFullClassChange(config)) {
            return true;
        }
        if (classId == null || classId.isBlank()) {
            return false;
        }
        FactionDataLoader.ClassKitData kit =
            FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        return kit == null || isVehicleCrewClass(kit);
    }
}
