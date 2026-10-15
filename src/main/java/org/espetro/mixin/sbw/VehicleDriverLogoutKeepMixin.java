package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.world.entity.Entity;
import org.espetro.Espetro;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 让「驾驶员离线时留在世界里」的载具不再被删除。
 *
 * <p>Superb Warfare / DragonRise / FCP 的载具都不会因为驾驶员离线或阵亡而主动自我删除：
 * SBW 源码里既没有 {@code PlayerLoggedOutEvent} 也没有 {@code LivingDeathEvent} 的载具销毁逻辑
 * （{@code VehicleConfig} 中也没有任何 despawn 开关）。真正删掉载具的是<b>原版</b>
 * {@code PlayerList#remove(ServerPlayer)}：当离线的玩家是某辆载具上唯一的玩家时，原版会
 * <pre>
 *   LOGGER.debug("Removing player mount");
 *   player.stopRiding();
 *   rootVehicle.getPassengersAndSelf().forEach(e -&gt; e.setRemoved(RemovalReason.UNLOADED_WITH_PLAYER));
 * </pre>
 * 而 {@code UNLOADED_WITH_PLAYER} 的 {@code shouldSave()} 为 false，因此载具被直接删除且不会写进区块存档。
 * SBW 自己也清楚这条路径（{@code VehicleEntity#remove} 里专门把 {@code UNLOADED_WITH_PLAYER}
 * 从「掉落库存」分支排除），这也是玩家「离线/死在车上后载具跟着消失」的唯一来源。
 *
 * <p>本 mixin 只拦这一条路径：{@code setRemoved} 的移除原因必须是
 * {@code UNLOADED_WITH_PLAYER}，且被移除的对象必须是 SBW {@link VehicleEntity}（含继承它的
 * DragonRise / FCP 载具）。载具被打爆（{@code KILLED}/{@code DISCARDED}）、撬棍回收、
 * 管理员清除、战局结束清理等其它生命周期逻辑完全不受影响。
 *
 * <p>移除点选在 {@code Entity#setRemoved}（原版方法，生产环境 SRG 名为 {@code m_142467_}）而不是
 * SBW 的 {@code VehicleEntity#remove}：离线路径调用的是 {@code setRemoved}，它<b>不会</b>经过
 * {@code remove} 覆写，所以注入 {@code remove} 抓不到这条路径。{@code setRemoved} 是 Minecraft
 * 的方法（即使被 mod 覆写，生产环境同样是 SRG 名），因此保持默认 {@code remap = true}。
 *
 * <p>{@code require = 0}：SBW/原版版本差异导致注入点消失时静默跳过，不会让服务器启动崩溃。
 */
@Mixin(Entity.class)
public abstract class VehicleDriverLogoutKeepMixin {

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true, require = 0)
    private void espetro$keepVehicleWhenDriverLogsOut(Entity.RemovalReason reason, CallbackInfo ci) {
        // 只处理「随玩家一起移除」：原版 PlayerList#remove 会用它删掉离线玩家的坐骑。
        // 其它原因（KILLED / DISCARDED / CHANGED_DIMENSION / UNLOADED_TO_CHUNK）一律放行。
        if (reason != Entity.RemovalReason.UNLOADED_WITH_PLAYER) {
            return;
        }
        if (!((Object) this instanceof VehicleEntity vehicle)) {
            return;
        }
        // 仅服务端拦截；客户端实体不参与存档删除。
        if (vehicle.level() == null || vehicle.level().isClientSide) {
            return;
        }
        ci.cancel();
        // 一次性确认命中：每次拦截都会记录一行，便于在日志中确认该路径确实被走到。
        Espetro.LOGGER.info(
            "载具 {} 的驾驶员离线：已阻止原版 Removing player mount 删除该载具（dimension={}, pos={}, reason={}），载具保留在世界上",
            vehicle.getUUID(), vehicle.level().dimension().location(), vehicle.position(), reason);
    }
}
