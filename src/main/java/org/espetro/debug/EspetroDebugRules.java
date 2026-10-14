package org.espetro.debug;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.GameRules;
import org.espetro.Espetro;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Espetro 调试用游戏规则。
 *
 * <p>通过 {@code /gamerule espetroSkillNoCooldown true} 开启"技能无冷却"调试模式。
 * Minecraft 不允许模组直接注册新的 gamerule，这里按<b>参数类型</b>反射查找
 * {@code GameRules.register(String, Category, Type)}，避免生产环境 SRG 名称差异。</p>
 */
public final class EspetroDebugRules {

    /** 技能无冷却规则名。 */
    public static final String SKILL_NO_COOLDOWN = "espetroSkillNoCooldown";

    private static GameRules.Key<GameRules.BooleanValue> skillNoCooldownKey;

    private EspetroDebugRules() {
    }

    /** 在模组构造阶段调用（必须早于任何世界加载）。 */
    @SuppressWarnings("unchecked")
    public static void register() {
        if (skillNoCooldownKey != null) {
            return;
        }
        try {
            Method register = null;
            for (Method candidate : GameRules.class.getDeclaredMethods()) {
                Class<?>[] types = candidate.getParameterTypes();
                if (types.length == 3 && types[0] == String.class
                    && types[1] == GameRules.Category.class
                    && types[2] == GameRules.Type.class) {
                    register = candidate;
                    break;
                }
            }
            if (register == null) {
                Espetro.LOGGER.warn("未找到 GameRules.register，调试规则 {} 不可用", SKILL_NO_COOLDOWN);
                return;
            }
            register.setAccessible(true);

            Object type = null;
            for (Method candidate : GameRules.BooleanValue.class.getDeclaredMethods()) {
                Class<?>[] types = candidate.getParameterTypes();
                // create(boolean) 返回的是 GameRules.Type<BooleanValue>，因此只看"静态 + 单个 boolean 参数"
                if (Modifier.isStatic(candidate.getModifiers())
                    && types.length == 1 && types[0] == boolean.class) {
                    type = candidate.invoke(null, false);
                    break;
                }
            }
            if (type == null) {
                Espetro.LOGGER.warn("未找到 BooleanValue 工厂方法，调试规则 {} 不可用", SKILL_NO_COOLDOWN);
                return;
            }

            skillNoCooldownKey = (GameRules.Key<GameRules.BooleanValue>) register.invoke(
                null, SKILL_NO_COOLDOWN, GameRules.Category.MISC, type);
            Espetro.LOGGER.info("已注册调试游戏规则: /gamerule {} true|false（true = 技能无冷却）",
                SKILL_NO_COOLDOWN);
        } catch (ReflectiveOperationException | RuntimeException error) {
            Espetro.LOGGER.warn("注册调试游戏规则 {} 失败", SKILL_NO_COOLDOWN, error);
        }
    }

    /** 当前战局是否开启技能无冷却调试模式。 */
    public static boolean skillNoCooldown() {
        return skillNoCooldown(Espetro.getServer());
    }

    public static boolean skillNoCooldown(MinecraftServer server) {
        if (server == null || skillNoCooldownKey == null) {
            return false;
        }
        try {
            return server.getGameRules().getBoolean(skillNoCooldownKey);
        } catch (RuntimeException error) {
            return false;
        }
    }
}
