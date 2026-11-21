package org.espetro.team;

import javax.annotation.Nullable;

import java.util.Locale;

/**
 * 进攻方 / 防守方。
 *
 * <p>全模组统一的阵营标识。字符串形式（{@link #id()}）与历史上使用的
 * {@code "ATTACK"} / {@code "DEFEND"} 完全一致，网络包、存档和配置文件无需迁移。</p>
 *
 * <p>解析字符串时按来源可信度选择：</p>
 * <ul>
 *   <li>{@link #of(String)}：严格，仅接受 {@code ATTACK} / {@code DEFEND}（忽略大小写和首尾空白）。</li>
 *   <li>{@link #parse(String)}：在严格基础上额外接受计分板队伍名 {@code ESPETRO_ATTACK} / {@code ESPETRO_DEFEND}。</li>
 *   <li>{@link #guess(String)}：宽松，按关键字匹配（attack / defend / 进攻 / 攻方 / 防守 / 守方），用于玩家或脚本输入。</li>
 * </ul>
 */
public enum Side {
    ATTACK,
    DEFEND;

    /** 编译期常量，可用于 {@code switch} 的 {@code case} 和注解。 */
    public static final String ATTACK_ID = "ATTACK";
    /** 编译期常量，可用于 {@code switch} 的 {@code case} 和注解。 */
    public static final String DEFEND_ID = "DEFEND";

    private static final String SCOREBOARD_PREFIX = "ESPETRO_";

    /** 与历史字符串一致的标识：{@code "ATTACK"} 或 {@code "DEFEND"}。 */
    public String id() {
        return name();
    }

    public Side opposite() {
        return this == ATTACK ? DEFEND : ATTACK;
    }

    /** 严格解析：仅接受 {@code ATTACK} / {@code DEFEND}，忽略大小写和首尾空白；其他返回 {@code null}。 */
    @Nullable
    public static Side of(@Nullable String team) {
        if (team == null) return null;
        String normalized = team.trim().toUpperCase(Locale.ROOT);
        if (ATTACK_ID.equals(normalized)) return ATTACK;
        if (DEFEND_ID.equals(normalized)) return DEFEND;
        return null;
    }

    /** 同 {@link #of(String)}，另外接受计分板队伍名 {@code ESPETRO_ATTACK} / {@code ESPETRO_DEFEND}。 */
    @Nullable
    public static Side parse(@Nullable String team) {
        if (team == null) return null;
        String normalized = team.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith(SCOREBOARD_PREFIX)) {
            normalized = normalized.substring(SCOREBOARD_PREFIX.length());
        }
        return of(normalized);
    }

    /** 宽松解析：包含 attack / 进攻 / 攻方 视为进攻方，包含 defend / 防守 / 守方 视为防守方；进攻优先。 */
    @Nullable
    public static Side guess(@Nullable String team) {
        if (team == null || team.isBlank()) return null;
        String lower = team.toLowerCase(Locale.ROOT);
        if (lower.contains("attack") || team.contains("进攻") || team.contains("攻方")) return ATTACK;
        if (lower.contains("defend") || team.contains("防守") || team.contains("守方")) return DEFEND;
        return null;
    }

    /** {@link #of(String)} 的字符串版本：返回规范化后的 {@code "ATTACK"} / {@code "DEFEND"}，无法识别时返回 {@code null}。 */
    @Nullable
    public static String normalizeId(@Nullable String team) {
        Side side = of(team);
        return side == null ? null : side.id();
    }

    /**
     * 对方阵营的标识。保持历史写法 {@code "ATTACK".equals(team) ? "DEFEND" : "ATTACK"} 的语义：
     * 只有精确等于 {@code "ATTACK"} 时返回 {@code "DEFEND"}，其余（包括 {@code null}）都返回 {@code "ATTACK"}。
     */
    public static String oppositeId(@Nullable String team) {
        return ATTACK_ID.equals(team) ? DEFEND_ID : ATTACK_ID;
    }
}
