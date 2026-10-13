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

    private static volatile long armedAtMs;

    private CommanderSkillTargetRequest() {
    }

    /** 本地主动点击技能时调用。 */
    public static void arm() {
        armedAtMs = System.currentTimeMillis();
    }

    /** 消费一次标记：只有近期内本地点击过才返回 true。 */
    public static boolean consumeArmed() {
        long armedAt = armedAtMs;
        armedAtMs = 0L;
        return armedAt > 0L && System.currentTimeMillis() - armedAt <= WINDOW_MS;
    }
}
