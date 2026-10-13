package org.espetro.client.gui;

/**
 * 指挥官技能「选点」的本地点击标记。
 *
 * <p>背景：服务端在部署阶段会对指挥官主动下发"进入火炮选点"回包，
 * 导致客户端一进部署阶段就只能在地图上放炮、其它交互全被选点模式挡住。
 * 这里要求：只有玩家**本地主动点击**过技能（J 菜单按钮 / 技能轮盘 / 技能面板）
 * 之后，才允许进入选点模式；其它来源的请求一律忽略。</p>
 */
public final class CommanderSkillTargetRequest {

    /** 本地点击后允许进入选点模式的时间窗（毫秒）。 */
    private static final long WINDOW_MS = 10_000L;

    /** 待用（已点击技能但尚未真正使用）的技能：skillId → 点击时间。 */
    private static final java.util.Map<String, Long> PENDING = new java.util.concurrent.ConcurrentHashMap<>();

    /** 待用状态最长保持时间：超时视为放弃（与选点模式超时一致）。 */
    private static final long PENDING_TIMEOUT_MS = 30_000L;

    private static volatile long armedAtMs;

    private CommanderSkillTargetRequest() {
    }

    /** 本地主动点击技能时调用。 */
    public static void arm() {
        armedAtMs = System.currentTimeMillis();
    }

    /** 本地点击某个技能：进入待用状态（按钮变黄）。 */
    public static void arm(String skillId) {
        arm();
        if (skillId != null && !skillId.isBlank()) {
            PENDING.put(skillId, System.currentTimeMillis());
        }
    }

    /** 该技能是否处于待用状态（已点击、尚未使用）。 */
    public static boolean isPending(String skillId) {
        Long at = skillId == null ? null : PENDING.get(skillId);
        if (at == null) {
            return false;
        }
        if (System.currentTimeMillis() - at > PENDING_TIMEOUT_MS) {
            PENDING.remove(skillId);
            return false;
        }
        return true;
    }

    /** 技能已真正使用（服务端开始冷却）或取消时清除待用状态。 */
    public static void clearPending(String skillId) {
        if (skillId != null) {
            PENDING.remove(skillId);
        }
    }

    /** 消费一次标记：只有近期内本地点击过才返回 true。 */
    public static boolean consumeArmed() {
        long armedAt = armedAtMs;
        armedAtMs = 0L;
        return armedAt > 0L && System.currentTimeMillis() - armedAt <= WINDOW_MS;
    }
}
