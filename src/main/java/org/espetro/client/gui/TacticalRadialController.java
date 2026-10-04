package org.espetro.client.gui;

import org.esradial.client.Actions;
import org.esradial.client.RadialMenuClientApi;
import org.esradial.client.RadialMenuBuilder;
import org.esradial.client.RadialMenuRegistry;
import org.esradial.client.RadialMenuOverlay;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadialActionPacket;
import org.espetro.network.FortificationCatalogPacket;
import org.espetro.team.CommanderSkillManager;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hold-key state machine for Espetro's EsRadial tactical radial.
 *
 * <p>设计约束：
 * <ul>
 *   <li>Overlay 活跃期间绝不调用 {@code RadialMenuRegistry.setMenus}、close/open Overlay。</li>
 *   <li>菜单重建延迟到 Alt 已松开且 Overlay 已关闭后执行；每次关闭最多重建一次。</li>
 *   <li>技能同步由服务端在入服/指挥官变更/技能激活时主动推送，不依赖首次 Alt 长按。</li>
 *   <li>根菜单：指挥官直接显示「载具部署」；有可用技能时显示「技能」槽。</li>
 *   <li>冷却值更新仅影响下次打开时的菜单内容，不触发 Overlay 内重建。</li>
 *   <li>每次开始按住 Alt 会请求一次技能同步，避免「后成为队长」仍无入口。</li>
 * </ul>
 */
public final class TacticalRadialController {

    private static final String OWNER = "espetro";
    private static final int OPEN_DELAY_TICKS = 6;

    private static final ResourceLocation ROOT_MENU = id("tactical_root");
    private static final ResourceLocation BUILD_MENU = id("tactical_build");
    private static final ResourceLocation SKILLS_MENU = id("tactical_skills");
    private static final ResourceLocation OPEN_SUBMENU_ACTION = id("open_tactical_submenu");
    private static final ResourceLocation EXECUTE_ACTION = id("execute_tactical_action");
    private static final ResourceLocation SKILL_ACTIVATE_ACTION = id("skill_activate");
    private static final ResourceLocation BUILD_FORT_ACTION = id("build_fortification");
    /** 火力组长不允许建造的工事 id（电台：仍限指挥官/小队长）。 */
    private static final String FIRETEAM_FORBIDDEN_FORT_ID = "espetro:radio";

    private static final ResourceLocation RALLY = id("textures/gui/squad/rally_deploy.png");
    private static final ResourceLocation BUILD_ICON =
        id("textures/gui/commander_skills/vehicle_supply_station.png");
    private static final ResourceLocation AMMO_CRATE = id("textures/gui/squad/ammo_crate.png");
    private static final ResourceLocation VEHICLE = id("textures/gui/squad/vehicle_deploy.png");
    private static final ResourceLocation COMMAND_ICON = id("textures/gui/commander_skills/command.png");
    private static final ResourceLocation UNAVAILABLE_ICON = id("textures/gui/commander_skills/unavailable.png");

    private static boolean initialized;
    private static boolean keyWasDown;
    private static boolean ownsOverlay;
    private static boolean consumedUntilRelease;
    private static int heldTicks;

    // === 已确认的技能缓存（仅在客户端线程中读写） ===
    private static boolean cachedIsCommander;
    private static boolean hasSkillSnapshot;
    private static final Map<String, Integer> cachedCooldowns = new HashMap<>();
    private static final List<CommanderSkillManager.SkillView> cachedSkills = new ArrayList<>();
    private static final List<FortificationCatalogPacket.Entry> cachedFortifications = new ArrayList<>();
    /** 上次 rebuildMenus 时使用的签名；相同签名不重建 */
    private static String lastMenuSignature = "";

    // === 网络线程写入的待确认数据 ===
    private static volatile boolean skillsDirty;
    private static volatile boolean pendingIsCommander;
    private static volatile boolean pendingHasSnapshot;
    private static final Map<String, Integer> pendingCooldowns = new HashMap<>();
    private static final List<CommanderSkillManager.SkillView> pendingSkills = new ArrayList<>();
    private static volatile boolean fortificationsDirty;
    private static volatile List<FortificationCatalogPacket.Entry> pendingFortifications = List.of();

    /** 是否有待延迟执行的菜单重建 */
    private static boolean pendingRebuild;

    private TacticalRadialController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        Actions.register(OPEN_SUBMENU_ACTION, params -> {
            String menu = params.getString("menu", "");
            ResourceLocation target = switch (menu) {
                case "build" -> BUILD_MENU;
                case "skills" -> SKILLS_MENU;
                default -> null;
            };
            var data = target == null ? null : RadialMenuRegistry.getRuntimeMenu(target);
            if (data != null) RadialMenuClientApi.navigate(data);
        });
        Actions.register(EXECUTE_ACTION, params -> {
            try {
                RadialActionPacket.Action action = RadialActionPacket.Action.valueOf(
                    params.getString("action", ""));
                NetworkManager.sendRadialAction(action);
            } catch (IllegalArgumentException ignored) {
                return;
            }
            consumedUntilRelease = true;
            ownsOverlay = false;
        });
        Actions.register(BUILD_FORT_ACTION, params -> {
            String fortId = params.getString("fortId", "");
            if (!fortId.isEmpty()) {
                NetworkManager.sendBuildFortification(fortId);
            }
            consumedUntilRelease = true;
            ownsOverlay = false;
        });
        Actions.register(SKILL_ACTIVATE_ACTION, params -> {
            String skillId = params.getString("skillId", "");
            // 载具补给站已迁至建造工事，拦截旧技能
            if ("vehicle_supply_station".equals(skillId)) {
                return;
            }
            if (skillId.isEmpty()) {
                return;
            }
            NetworkManager.sendCommanderSkillActivate(skillId);
            consumedUntilRelease = true;
            ownsOverlay = false;
        });

        rebuildMenus();
    }

    // ==================== 技能同步 ====================

    /**
     * 由 ClientPacketHandlers 在收到 CommanderSkillSyncPacket 时调用（网络线程）。
     * 仅存储待确认数据并标记 dirty，不修改菜单，不触发 Overlay 操作。
     */
    public static void updateSkills(boolean isCommander, Map<String, Integer> cooldowns,
                                    List<CommanderSkillManager.SkillView> skills) {
        pendingIsCommander = isCommander;
        pendingHasSnapshot = true;
        synchronized (pendingCooldowns) {
            pendingCooldowns.clear();
            if (cooldowns != null) {
                pendingCooldowns.putAll(cooldowns);
            }
        }
        synchronized (pendingSkills) {
            pendingSkills.clear();
            if (skills != null) {
                pendingSkills.addAll(skills);
            }
        }
        skillsDirty = true;
    }

    public static void updateFortifications(List<FortificationCatalogPacket.Entry> entries) {
        pendingFortifications = entries == null ? List.of() : List.copyOf(entries);
        fortificationsDirty = true;
    }

    /**
     * 客户端线程中消费待确认的技能数据。
     * 仅在内容签名变化时标记 {@code pendingRebuild}，不直接重建。
     */
    private static void flushSkillUpdate() {
        if (!skillsDirty) {
            return;
        }
        skillsDirty = false;

        cachedIsCommander = pendingIsCommander;
        hasSkillSnapshot = pendingHasSnapshot;
        synchronized (pendingCooldowns) {
            cachedCooldowns.clear();
            cachedCooldowns.putAll(pendingCooldowns);
        }
        synchronized (pendingSkills) {
            cachedSkills.clear();
            cachedSkills.addAll(pendingSkills);
        }

        String newSig = computeSignature();
        if (!newSig.equals(lastMenuSignature)) {
            if (ownsOverlay || RadialMenuOverlay.INSTANCE.isActive()) {
                pendingRebuild = true;
            } else {
                rebuildMenus();
            }
        }
    }

    private static void flushFortificationUpdate() {
        if (!fortificationsDirty) return;
        fortificationsDirty = false;
        cachedFortifications.clear();
        cachedFortifications.addAll(pendingFortifications);
        String newSignature = computeSignature();
        if (!newSignature.equals(lastMenuSignature)) {
            if (ownsOverlay || RadialMenuOverlay.INSTANCE.isActive()) {
                pendingRebuild = true;
            } else {
                rebuildMenus();
            }
        }
    }

    /**
     * 计算当前菜单内容签名：isCommander + 技能 ID 列表 + 每个技能的冷却秒数。
     * 冷却值变化也会触发下次打开时的重建（但不会在 Overlay 活跃期间重建）。
     */
    private static String computeSignature() {
        StringBuilder sb = new StringBuilder();
        sb.append(cachedIsCommander ? '1' : '0');
        sb.append('|');
        for (CommanderSkillManager.SkillView skill : cachedSkills) {
            sb.append(skill.id()).append(':')
              .append(cachedCooldowns.getOrDefault(skill.id(), 0)).append(',');
        }
        sb.append('|');
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            sb.append(fort.id()).append(':').append(fort.icon()).append(':')
                .append(fort.constructionCost()).append(':')
                .append(fort.ammunitionCost()).append(',');
        }
        return sb.toString();
    }

    // ==================== 菜单构建 ====================

    private static void rebuildMenus() {
        lastMenuSignature = computeSignature();
        List<org.esradial.client.RadialMenuData> menus = new ArrayList<>();
        menus.add(rootMenu());
        menus.add(buildMenu());
        menus.add(skillsMenu());
        RadialMenuRegistry.setMenus(OWNER, menus);
    }

    // ==================== Tick ====================

    public static void tick(Minecraft minecraft, KeyMapping key) {
        if (!initialized || minecraft == null || key == null || minecraft.player == null) {
            closeOwnedOverlay();
            keyWasDown = false;
            reset(false);
            return;
        }

        // 客户端线程中消费网络线程的技能更新
        flushSkillUpdate();
        flushFortificationUpdate();

        boolean down = key.isDown();
        if (!down) {
            if (keyWasDown) {
                finishSelection();
            }
            keyWasDown = false;
            heldTicks = 0;
            consumedUntilRelease = false;
            // 轮盘关闭后，应用待重建的菜单（最多一次）
            tryApplyPendingRebuild();
            return;
        }

        // 刚按下 Alt：向服务端拉一次技能列表（队长身份可能在入服同步之后才获得）
        if (!keyWasDown) {
            NetworkManager.requestCommanderSkillSync();
            NetworkManager.requestFortificationCatalog();
        }
        keyWasDown = true;

        /*
         * 普通队员必须在 EsRadial.open() 之前被拦截。过去依赖服务端回传空目录，
         * 会先打开一个空轮盘再迅速关闭，EsRadial 已经接管的鼠标状态因此可能吞掉左键。
         */
        if (!ClientTacticalState.canLocalPlayerOpenTacticalRadial(
                minecraft.player.getName().getString())) {
            closeOwnedOverlay();
            heldTicks = 0;
            consumedUntilRelease = false;
            tryApplyPendingRebuild();
            return;
        }
        if (consumedUntilRelease) {
            return;
        }
        if (minecraft.screen != null) {
            closeOwnedOverlay();
            heldTicks = 0;
            tryApplyPendingRebuild();
            return;
        }

        if (ownsOverlay || RadialMenuOverlay.INSTANCE.isActive()) {
            return;
        }

        heldTicks++;
        if (heldTicks >= OPEN_DELAY_TICKS) {
            var data = RadialMenuRegistry.getRuntimeMenu(ROOT_MENU);
            ownsOverlay = data != null && RadialMenuClientApi.open(data,
                new RadialMenuClientApi.OpenOptions(OWNER, key::isDown, false, reason -> {
                    ownsOverlay = false;
                    consumedUntilRelease = true;
                }));
        }
    }

    /**
     * Overlay 已关闭时执行一次延迟重建。
     */
    private static void tryApplyPendingRebuild() {
        if (pendingRebuild && !ownsOverlay && !RadialMenuOverlay.INSTANCE.isActive()) {
            pendingRebuild = false;
            rebuildMenus();
        }
    }

    // ==================== Overlay 生命周期 ====================

    private static void finishSelection() {
        // Squad: releasing the opening key cancels; only a left click executes.
        closeOwnedOverlay();
        reset(true);
    }

    private static void closeOwnedOverlay() {
        if (ownsOverlay && RadialMenuOverlay.INSTANCE.isActive()) {
            RadialMenuClientApi.close(OWNER);
        }
        ownsOverlay = false;
    }

    private static void reset(boolean keepConsumed) {
        heldTicks = 0;
        ownsOverlay = false;
        if (!keepConsumed) {
            consumedUntilRelease = false;
        }
    }

    // ==================== 菜单数据 ====================

    private static org.esradial.client.RadialMenuData rootMenu() {
        var builder = base(ROOT_MENU);
        if (!cachedFortifications.isEmpty()) {
            builder = builder.slot("espetro.build", BUILD_ICON,
                Actions.script(OPEN_SUBMENU_ACTION, Map.of("menu", "build")),
                Component.literal("建造工事"), "#FFD5B25C");
        }
        // 载具部署不是工事建造：让指挥官在根轮盘直接看到入口，避免首次
        // 冷却已经结束却误以为没有可部署载具。
        if (cachedIsCommander) {
            builder = builder.slot("espetro.vehicle", VEHICLE,
                action(RadialActionPacket.Action.DEPLOY_VEHICLE),
                Component.literal("载具信息"), "#FFB0A070");
        }
        // 指挥官或同步到了可用技能（含小队长 usableBy）时显示入口
        if (cachedIsCommander || (hasSkillSnapshot && !cachedSkills.isEmpty())) {
            builder = builder.slot("espetro.skills", COMMAND_ICON,
                Actions.script(OPEN_SUBMENU_ACTION, Map.of("menu", "skills")),
                Component.translatable("radial.espetro.skills"), "#FFD5A25C");
        }
        return builder.build();
    }

    /**
     * 建造工事二级菜单：Rally 仍是部署点，其余工事只来自 JSON 目录。
     * Radio / 兵站已在 fortifications.json 中，不能再硬编码一份。
     */
    private static org.esradial.client.RadialMenuData buildMenu() {
        // 只是火力组长（非指挥官、非小队长）时：不给 Rally（队包）和电台，其余工事照常。
        boolean fireteamOnly = isFireteamLeaderOnly();
        var builder = base(BUILD_MENU);
        if (!fireteamOnly) {
            builder = builder.slot("espetro.rally", RALLY, action(RadialActionPacket.Action.DEPLOY_RALLY),
                Component.translatable("radial.espetro.rally"), "#FF7DAE82");
        }
        for (FortificationCatalogPacket.Entry fort : cachedFortifications) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }
            if (fireteamOnly && FIRETEAM_FORBIDDEN_FORT_ID.equals(fort.id())) {
                continue;
            }
            ResourceLocation icon = ResourceLocation.tryParse(fort.icon());
            if (icon == null) icon = UNAVAILABLE_ICON;
            StringBuilder label = new StringBuilder(fort.displayName());
            if (fort.constructionCost() > 0 || fort.ammunitionCost() > 0) {
                label.append(" §7(");
                if (fort.constructionCost() > 0) label.append("建材 ").append(fort.constructionCost());
                if (fort.constructionCost() > 0 && fort.ammunitionCost() > 0) label.append(" / ");
                if (fort.ammunitionCost() > 0) label.append("弹药 ").append(fort.ammunitionCost());
                label.append(')');
            }
            builder = builder.slot("espetro.fort." + fort.id(), icon,
                Actions.script(BUILD_FORT_ACTION, Map.of("fortId", fort.id())),
                Component.literal(label.toString()), "#FFB0A070");
        }
        return builder.build();
    }

    private static org.esradial.client.RadialMenuData skillsMenu() {
        var builder = base(SKILLS_MENU);

        if (!hasSkillSnapshot) {
            builder = builder.slot("espetro.skills_loading", UNAVAILABLE_ICON,
                Actions.script(EXECUTE_ACTION, Map.of("action", "FOB_STATUS")),
                Component.literal("加载中…"), "#FF4A3030").disabledLast(Component.literal("等待服务端技能列表"));
            return builder.build();
        }
        // 服务端已按 usableBy 过滤；列表空 = 当前角色无可用技能
        if (cachedSkills.isEmpty()) {
            builder = builder.slot("espetro.no_skills", UNAVAILABLE_ICON,
                Actions.script(EXECUTE_ACTION, Map.of("action", "FOB_STATUS")),
                Component.literal("无可用技能"), "#FF4A3030").disabledLast(Component.literal("当前身份没有可用技能"));
            return builder.build();
        }

        for (CommanderSkillManager.SkillView skill : cachedSkills) {
            // 载具补给站已迁出指挥官技能
            if ("vehicle_supply_station".equals(skill.id())) {
                continue;
            }
            int cooldown = cachedCooldowns.getOrDefault(skill.id(), 0);
            boolean onCooldown = cooldown > 0;
            String color = onCooldown ? "#FF4A3030" : "#FFD5B25C";
            String label = onCooldown
                ? skill.displayName() + " §7(" + cooldown + "s)"
                : skill.displayName();
            ResourceLocation icon = resolveSkillIcon(skill);
            builder = builder.slot("espetro.skill." + skill.id(), icon,
                Actions.script(SKILL_ACTIVATE_ACTION, Map.of("skillId", skill.id())),
                Component.literal(label), color);
            if (onCooldown) builder.disabledLast(Component.literal("冷却剩余 " + cooldown + " 秒"));
        }
        return builder.build();
    }

    /**
     * 解析技能图标资源位置。无效或缺失时回退 command.png。
     */
    private static ResourceLocation resolveSkillIcon(CommanderSkillManager.SkillView skill) {
        String raw = skill.icon();
        if (raw == null || raw.isBlank()) {
            return COMMAND_ICON;
        }
        ResourceLocation loc = ResourceLocation.tryParse(raw.trim());
        return loc != null ? loc : COMMAND_ICON;
    }

    /** Rally plus one slot per catalog fort; radio/HAB must not be hard-coded again. */
    static List<String> buildMenuSlotIds(List<FortificationCatalogPacket.Entry> forts) {
        List<String> ids = new ArrayList<>();
        boolean fireteamOnly = isFireteamLeaderOnly();
        if (!fireteamOnly) {
            ids.add("espetro.rally");
        }
        if (forts == null) {
            return ids;
        }
        for (FortificationCatalogPacket.Entry fort : forts) {
            if (fort == null || fort.id() == null || fort.id().isBlank()) {
                continue;
            }
            if (fireteamOnly && FIRETEAM_FORBIDDEN_FORT_ID.equals(fort.id())) {
                continue;
            }
            ids.add("espetro.fort." + fort.id());
        }
        return ids;
    }

    /**
     * 当前本地玩家是否"只是火力组长"——非指挥官、非小队长，仅火力组长。
     * 这类玩家可以开轮盘建工事，但不能建电台，也不能放 Rally/队包。
     */
    private static boolean isFireteamLeaderOnly() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            return false;
        }
        String name = mc.player.getName().getString();
        if (ClientTacticalState.isCommander(name) || ClientTacticalState.isLocalSquadLeader(name)) {
            return false;
        }
        return ClientTacticalState.isLocalFireteamLeader(name);
    }

    private static RadialMenuBuilder base(ResourceLocation menuId) {
        return new RadialMenuBuilder(menuId)
            .title(Component.literal(menuId.equals(ROOT_MENU) ? "指挥菜单"
                : menuId.equals(BUILD_MENU) ? "建造工事" : "指挥技能"))
            .radii(44, 96)
            .squadLayout()
            .animationSpeed(1.25f)
            .ringColors(List.of("#B824292B", "#C832383A"));
    }

    private static Runnable action(
            RadialActionPacket.Action action) {
        return Actions.script(EXECUTE_ACTION, Map.of("action", action.name()));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("espetro", path);
    }
}
