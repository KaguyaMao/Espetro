package org.espetro.mixin.sbw;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.espetro.compat.SuperbWarfareBlockGate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 卓越前线「钻地 / 预破坏」路径的白名单闸门。
 *
 * <p>背景：SBW 的炮弹、火箭弹、航弹、C4、反坦克地雷等在爆炸前会沿弹道直接调用
 * {@code level.destroyBlock(...)} 把沿途方块挖掉（受 SBW 的 {@code extra_explosion_effect}
 * 开关控制）。这条路走的是 {@code ServerLevel/Level.destroyBlock(BlockPos, boolean)} →
 * {@code destroyBlock(BlockPos, boolean, Entity)} → {@code destroyBlock(BlockPos, boolean, Entity, int)}
 * 的委托链，<b>完全绕开</b> {@code CustomExplosion}，因此
 * {@code CustomExplosionBlockGateMixin} 拦不到它。</p>
 *
 * <p>生产环境字节码核对（SBW 0.8.9.1，所有调用点都是
 * {@code invokevirtual Level/ServerLevel.m_46961_:(Lnet/minecraft/core/BlockPos;Z)Z}）：
 * CannonShellEntity、MediumRocketEntity、SmallCannonShellEntity、AerialBombEntity（2 处）、
 * FastThrowableProjectile.destroyBlock、Tm62Entity、MelonBombEntity（2 处）、C4Entity.explode、
 * AnnihilatorEntity.laserLength。它们最终都会落到这里注入的 4 参实现上。</p>
 *
 * <p>只有“调用栈上第一个非 MC/Forge/本模组/JDK 的帧”属于 {@code com.atsuishio.superbwarfare}
 * 时才介入（因此载具撞击破坏、其它模组与本模组自己的破坏不受影响），且判定失败一律放行。
 * {@code require = 0}：目标方法解析失败时静默跳过，不会因为 SBW 版本变化而崩服。</p>
 */
@Mixin(Level.class)
public abstract class SbwLevelDestroyBlockGateMixin {

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;ZLnet/minecraft/world/entity/Entity;I)Z",
        at = @At("HEAD"), cancellable = true, require = 0)
    private void espetro$gateSbwDestroyBlock(BlockPos pos, boolean drop, Entity entity, int recursionLeft,
                                             CallbackInfoReturnable<Boolean> cir) {
        if (SuperbWarfareBlockGate.shouldGateDestroyBlock((Level) (Object) this, pos)) {
            cir.setReturnValue(false);
        }
    }
}
