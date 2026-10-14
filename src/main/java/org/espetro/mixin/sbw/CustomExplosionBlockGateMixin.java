package org.espetro.mixin.sbw;

import com.atsuishio.superbwarfare.tools.CustomExplosion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.espetro.Espetro;
import org.espetro.compat.SuperbWarfareBlockGate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Collection;

/**
 * 卓越前线爆炸的方块破坏白名单：只允许破坏玻璃类 / 活板门类 / 门类。
 *
 * <p>为什么不用 Forge 的 {@code ExplosionEvent.Detonate}：SBW 的 {@link CustomExplosion}
 * 在自己重写的 {@code explode()} 里<b>逐层</b>收集方块并立刻调用 {@code processBlockList(...)}
 * 销毁（{@code blockstate.onBlockExploded(...)}），之后才发起 {@code onExplosionDetonate}，
 * 而且传进去的是<b>附近实体</b>列表（方块列表 {@code toBlow} 在上一层末尾已被清空）。
 * 事件里拿到的方块列表始终为空，过滤无效 —— 只能在真正的破坏入口拦截。</p>
 *
 * <p>这里放两道独立钩子互为保险：即使其中一个因目标方法解析失败而未注入，另一个仍能拦住。</p>
 */
@Mixin(value = CustomExplosion.class, remap = false)
public abstract class CustomExplosionBlockGateMixin {

    /** CustomExplosion 自身的私有字段（javap 确认：private final Level level）。 */
    @Shadow
    @Final
    private Level level;

    private static boolean espetro$logged;

    private static void espetro$logOnce() {
        if (!espetro$logged) {
            espetro$logged = true;
            Espetro.LOGGER.info("[SBW] 爆炸方块白名单已生效（仅玻璃类/活板门类/门类可被破坏）");
        }
    }

    /** 钩子①：过滤即将被这一层销毁的方块。 */
    @ModifyVariable(method = "processBlockList",
        at = @At("HEAD"), argsOnly = true, remap = false, require = 0)
    private Collection<BlockPos> espetro$filterExplosionBlocks(Collection<BlockPos> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return blocks;
        }
        espetro$logOnce();
        return SuperbWarfareBlockGate.filterBlocks(this.level, blocks);
    }

    /** 钩子②：真正执行破坏的那一次调用，再兜一次底。 */
    @Redirect(method = "processBlockList", remap = false, require = 0,
        at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;onBlockExploded(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;)V"))
    private void espetro$guardExplodedBlock(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        espetro$logOnce();
        if (SuperbWarfareBlockGate.isExplosionBreakable(level, pos)) {
            state.onBlockExploded(level, pos, explosion);
        }
    }
}
