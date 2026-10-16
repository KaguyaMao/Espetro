package org.espetro.mixin.dragonrise;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.espetro.Espetro;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * dragonrise 整合 jar 内嵌自研 mod {@code com.rhythm.dragon_vehicle_deployer} 的
 * 「部署器闲置回收」不再删掉玩家开走的车。
 *
 * <p>被拦的目标（{@code javap -p -c} 在 {@code dragonrise_reforge-1.5.0-beta-20261015.jar} 上核实）：
 * <pre>
 *   private void trackOccupancy(net.minecraft.world.level.Level);   // 部署器自己的方法，名字不混淆
 *   ... 154: aload 4
 *       156: invokevirtual net/minecraft/world/entity/Entity.m_146870_:()V   // = Entity#discard()
 * </pre>
 * 即 {@code entity.discard()} 在整个方法里只出现一次；这就是「载具被乘坐过 → 空置满
 * idleClearTimeoutSeconds → 直接 discard 并立刻补刷一辆」的那次调用（源码
 * {@code VehicleDeployerBlockEntity.java:225}）。
 *
 * <p>运行时改法与源码改动（GScode 里把距离判定并进第 224 行的 if）略有差别：这里**只**把
 * {@code discard} 换成条件调用 —— 车仍在部署器旁（&lt;8 格）时照旧真删（保留防卡死的回收补刷），
 * 车已被开走时跳过删除；随后原方法自己的 226–231 行照常执行（解除绑定 + 置
 * {@code idleClearTriggered}），于是下一 tick 在部署器处补刷一辆新车。结果是：玩家的车一定留下，
 * 部署器也照常出车。（源码那版是「远处什么都不做」，会把部署器一直绑在那辆远车上；这处差异是
 * 有意的，见报告。）
 *
 * <p>距离基准：方块实体的 {@code getBlockPos()} 取 {@code Vec3.atCenterOf(...)} 即方块中心；
 * 阈值 64.0 是**平方距离**（=8 格）。依据：本部署器的出车点写死在
 * {@code deploy()} 里为「{@code pos + (0.5, 1.5, 0.5) ± 0.1}」，正是「方块中心 +(0,1,0) 抖 ±0.1」，
 * 新车静止时距中心约 1.0 格；8 格余量足以容纳挤压/滑动/轻微挪车，而真正被开走的车（&gt;8 格）永不被删。
 *
 * <p><b>remap 取舍（已实测）</b>：{@code method = "trackOccupancy"} 是 mod 自有方法，必须字面量匹配
 * → {@code remap = false}。{@code @At} 的 target 是 **vanilla** 方法 {@code Entity#discard()}，
 * 生产环境（Forge SRG）叫 {@code m_146870_}。理论上可以靠 refmap 把它从 {@code discard} 映射过去，
 * 但本工程的 Mixin 注解处理器**对 {@code targets = "外部类" } 的 mixin 完全不产出任何 refmap 条目**
 * （实测：同一个 {@code @At} 目标写在可解析的 {@code @Mixin(Level.class)} 上会生成
 * {@code "Lnet/minecraft/world/entity/Entity;discard()V": "Lnet/minecraft/world/entity/Entity;m_146870_()V"}，
 * 写在 {@code @Mixin(targets="com.rhythm.../VehicleDeployerBlockEntity")} 上则一条都没有），
 * 所以这里改用**两次独立注入、各自写死一个字面量**：
 * <ul>
 *   <li>SRG（{@code m_146870_}）—— 生产环境命中，esp. 线上整合包/服务器；</li>
 *   <li>官方名（{@code discard}）—— userdev/开发环境（官方映射）命中。</li>
 * </ul>
 * 两个环境的字节码里只会有其中一个名字，因此同一时刻只有一个 {@code @Redirect} 会匹配，
 * 不会出现「同一指令被重定向两次」的冲突；{@code require = 0} 保证任一未命中都只是静默跳过。
 *
 * <p>处理器是**非静态**方法，与目标方法 {@code trackOccupancy}（实例方法）的静态性一致，
 * 于是 {@code this} 就是被注入的那个 {@code VehicleDeployerBlockEntity}，可直接用
 * {@link BlockEntity} 强转读 {@code getBlockPos()}（方法体由 reobf 统一重映射，不需要
 * {@code @Shadow}，也就没有「shadow 解析失败 → 硬错误」的风险）。
 *
 * <p>{@code @Pseudo}：目标类来自第三方整合 jar，可能压根不存在（未装 dragonrise 的实例/测试环境）。
 * Espetro 的 mixin 插件只对 {@code .sbw.} 前缀做存在性门控，本包名（{@code .dragonrise.}）永远注册，
 * 因此必须 {@code @Pseudo} 才能在缺类时安静地不注入。处理器内部对取坐标/距离运算做
 * {@code catch (Throwable)} 兜底，判定失败时**不删车**（宁可让部署器多出一辆车，也不误删玩家的车）。
 */
@Pseudo
@Mixin(targets = "com.rhythm.dragon_vehicle_deployer.block.entity.VehicleDeployerBlockEntity", remap = false)
public abstract class DragonVehicleDeployerIdleClearMixin {

    /** 真正「跳过删除」时打印，且每个 JVM 只打一行（避免任何形式的刷屏）。 */
    private static boolean espetro$idleSkipLoggedOnce;

    /** 生产环境（Forge SRG）：{@code Entity#discard()} = {@code m_146870_}。 */
    @Redirect(
        method = "trackOccupancy", remap = false, require = 0,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;m_146870_()V"))
    private void espetro$guardIdleClearSrg(Entity entity) {
        espetro$guardIdleClear(entity);
    }

    /** 开发环境（userdev / 官方映射）：{@code Entity#discard()} 字面量。 */
    @Redirect(
        method = "trackOccupancy", remap = false, require = 0,
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;discard()V"))
    private void espetro$guardIdleClearNamed(Entity entity) {
        espetro$guardIdleClear(entity);
    }

    /** 两个环境的共同实现：只有「仍停在部署器旁（&lt;8 格）」的闲置载具才真的被回收销毁。 */
    private void espetro$guardIdleClear(Entity entity) {
        if (entity == null) {
            return;
        }
        boolean stillAtDeployer = false;
        double distanceSqr = -1.0;
        try {
            BlockEntity deployer = (BlockEntity) (Object) this;
            Vec3 center = Vec3.atCenterOf(deployer.getBlockPos());
            distanceSqr = entity.position().distanceToSqr(center);
            // 64.0 = 8 格的平方；出车点距方块中心约 1.0 格，见类注释。
            stillAtDeployer = distanceSqr < 64.0;
        } catch (Throwable t) {
            // 任何意外（含链接错误）都不删车：保守方向是「保住玩家的载具」。
            stillAtDeployer = false;
        }
        if (stillAtDeployer) {
            entity.discard();
            return;
        }
        // 车已被开走：跳过这次删除。原方法接下来的 226–231 行仍会执行（解绑 + 标记补刷），
        // 所以部署器不会卡死；而被跳过删除的载具永远留在世界上。
        if (!espetro$idleSkipLoggedOnce) {
            espetro$idleSkipLoggedOnce = true;
            BlockPos pos = null;
            try {
                pos = ((BlockEntity) (Object) this).getBlockPos();
            } catch (Throwable ignored) {
                // 日志用坐标取不到就留空
            }
            Espetro.LOGGER.info(
                "[载具保护] 部署器闲置回收已跳过删除：载具 {} 距部署器 {} {} 格（阈值 8 格），"
                    + "该载具保留在世界上，部署器照常补刷新车（此后不再重复打印）",
                entity.getUUID(), pos, distanceSqr < 0 ? "?" : String.format("%.2f", Math.sqrt(distanceSqr)));
        }
    }
}
