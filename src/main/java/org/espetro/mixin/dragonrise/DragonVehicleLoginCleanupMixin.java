package org.espetro.mixin.dragonrise;

import net.minecraftforge.event.entity.player.PlayerEvent;
import org.espetro.Espetro;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 关闭 dragonrise 整合 jar 内嵌自研 mod {@code com.rhythm.dragon_vehicle_deployer}
 * 的「玩家登录即按 NBT {@code LastDriver} 删车」行为（载具在乘员阵亡后凭空消失的主嫌之一）。
 *
 * <p>被拦的目标方法（用 {@code javap -p} 在
 * {@code dragonrise_reforge-1.5.0-beta-20261015.jar} 上核实）：
 * <pre>
 *   public static void onPlayerLoggedIn(net.minecraftforge.event.entity.player.PlayerEvent$PlayerLoggedInEvent);
 *   descriptor: (Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V
 * </pre>
 * 该类的字节码里只有这一个方法（外加 {@code <init>}/{@code <clinit>}），方法体内**唯一**的副作用是
 * 末段的 {@code LOGGER.info("Removing vehicle ... on login")} 与
 * {@code Entity.discard()}（SRG {@code m_146870_}）；前面的懒加载只写类内两个静态字段。
 * 因此 {@code ci.cancel()} 在 {@code HEAD} 取消**不会**丢掉任何其它功能，
 * 只是让「登录时按 LastDriver 遍历全维度实体并删车」整段失效。
 *
 * <p>{@code targets} 用字符串：该类只存在于第三方整合 jar 里，不在 Espetro 的编译依赖中。
 * {@code remap = false}：类名与方法名都不是被混淆的 vanilla 成员（Forge 事件描述符为字面量），
 * 与方法体内的 vanilla 调用无关 —— 方法体由 ForgeGradle 的 reobf 统一重映射（见既有 mixin 习惯）。
 * {@code require = 0}：目标类/方法缺失或版本变化时静默跳过，绝不让服务器启动失败。
 *
 * <p>{@code @Pseudo}：目标类只存在于第三方整合 jar 里，未装 dragonrise 的实例必须能安静地不注入
 * （Espetro 的 mixin 插件只对 {@code .sbw.} 前缀做存在性门控，本包名永远注册）。
 */
@Pseudo
@Mixin(targets = "com.rhythm.dragon_vehicle_deployer.event.PlayerLoginHandler", remap = false)
public abstract class DragonVehicleLoginCleanupMixin {

    /** 该监听器每次玩家登录都会执行，日志只打一次，避免刷屏。 */
    private static boolean espetro$loginVehicleGuardLogged;

    @Inject(
        method = "onPlayerLoggedIn(Lnet/minecraftforge/event/entity/player/PlayerEvent$PlayerLoggedInEvent;)V",
        at = @At("HEAD"), cancellable = true, remap = false, require = 0)
    private static void espetro$cancelVehicleRemovalOnLogin(PlayerEvent.PlayerLoggedInEvent event, CallbackInfo ci) {
        // 先取消，再打日志：即使日志本身出问题，也绝不会把玩家登录时「删车」的行为放回去。
        ci.cancel();
        if (espetro$loginVehicleGuardLogged) {
            return;
        }
        espetro$loginVehicleGuardLogged = true;
        String player = "?";
        try {
            if (event != null && event.getEntity() != null) {
                player = event.getEntity().getName().getString();
            }
        } catch (Throwable ignored) {
            // 日志取名字失败不影响拦截
        }
        Espetro.LOGGER.info(
            "[载具保护] 已拦截 dragon_vehicle_deployer 的「登录按 LastDriver 删车」：玩家 {} 登录不再删除任何载具"
                + "（PlayerLoginHandler#onPlayerLoggedIn 已取消；每个 JVM 只打印一次）",
            player);
    }
}
