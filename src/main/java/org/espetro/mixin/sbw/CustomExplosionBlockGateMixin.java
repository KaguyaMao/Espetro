package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.tools.CustomExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.compat.SuperbWarfareBlockGate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

/**
 * 卓越前线自研爆炸（{@link CustomExplosion}）的方块破坏白名单：只允许破坏玻璃类 / 门类 / 活板门类。
 *
 * <p>为什么不用 Forge 的 {@code ExplosionEvent.Detonate}：SBW 的 {@link CustomExplosion}
 * 在自己重写的 {@code explode()} 里<b>逐层</b>收集方块并立刻调用 {@code processBlockList(...)}
 * 销毁（{@code blockstate.onBlockExploded(...)}），之后才发起 {@code onExplosionDetonate}，
 * 而且传进去的是<b>附近实体</b>列表（方块列表 {@code toBlow} 在每一层末尾都被清空）。
 * 事件里拿到的方块列表始终为空，过滤无效 —— 只能在真正的破坏入口拦截。</p>
 *
 * <p>两个钩子的目标都用生产环境 SBW 0.8.9.1 的字节码核对过：</p>
 * <ul>
 *   <li>{@code private final void processBlockList(java.util.Collection<? extends BlockPos>)}</li>
 *   <li>{@code invokevirtual net/minecraft/world/level/block/state/BlockState.onBlockExploded:
 *       (Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V}
 *       （Forge 的 {@code IForgeBlockState#onBlockExploded} 默认方法，1.20.1 里就长这样）</li>
 * </ul>
 * 两者互相独立互为保险：任一因目标解析失败而未注入，另一个仍然拦住非白名单方块。
 * 两个钩子都只做“剔除/拦下不允许的方块”，<b>不会</b>影响玻璃/门/活板门的破坏。
 */
@Mixin(value = CustomExplosion.class, remap = false)
public abstract class CustomExplosionBlockGateMixin {

    /** CustomExplosion 自身的私有字段（javap 确认：private final Level level）。 */
    @Shadow
    @Final
    private Level level;

    /** 钩子①：过滤这一层即将被销毁的方块（只放行玻璃类/活板门类/门类）。 */
    @ModifyVariable(method = "processBlockList",
        at = @At("HEAD"), argsOnly = true, index = 1, remap = false, require = 0)
    private Collection<BlockPos> espetro$filterExplosionBlocks(Collection<BlockPos> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return blocks;
        }
        return SuperbWarfareBlockGate.filterBlocks(this.level, blocks);
    }

    /** 钩子②：真正执行破坏的那一次调用，再兜一次底。 */
    @Redirect(method = "processBlockList", remap = false, require = 0,
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;onBlockExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
    private void espetro$guardExplodedBlock(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        if (SuperbWarfareBlockGate.allowExplodedBlock(level, pos)) {
            state.onBlockExploded(level, pos, explosion);
        }
    }
}
