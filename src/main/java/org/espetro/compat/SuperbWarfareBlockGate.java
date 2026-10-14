package org.espetro.compat;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.espetro.Espetro;

/**
 * 卓越前线（Superb Warfare）爆炸的方块破坏白名单。
 *
 * <p>需求：卓越前线的任何攻击只允许破坏/击穿 <b>玻璃类、活板门类、门类</b> 方块。
 * SBW 的爆炸是 {@code CustomExplosion extends Explosion}，因此能在
 * {@link ExplosionEvent.Detonate} 里把不允许的方块从破坏列表中剔除，
 * 既保留爆炸对实体与允许方块的正常效果，又不碰其它建筑。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class SuperbWarfareBlockGate {

    private static final String SBW_PACKAGE = "com.atsuishio.superbwarfare";

    private SuperbWarfareBlockGate() {
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getExplosion() == null
            || !event.getExplosion().getClass().getName().startsWith(SBW_PACKAGE)) {
            return;
        }
        var level = event.getLevel();
        event.getAffectedBlocks().removeIf((pos) -> !isAllowed(level.getBlockState(pos)));
    }

    /**
     * 过滤一批即将被卓越前线爆炸破坏的方块，只保留允许的（玻璃类/活板门类/门类）。
     *
     * <p>SBW 的 {@code CustomExplosion} 自己销毁方块、且不把方块列表交给
     * {@code ExplosionEvent.Detonate}，所以真正的拦截点在
     * {@code CustomExplosionBlockGateMixin} 里，本方法供其调用。</p>
     */
    public static java.util.Collection<net.minecraft.core.BlockPos> filterBlocks(
            net.minecraft.world.level.Level level, java.util.Collection<net.minecraft.core.BlockPos> blocks) {
        if (level == null || blocks == null || blocks.isEmpty()) {
            return blocks;
        }
        java.util.List<net.minecraft.core.BlockPos> kept = new java.util.ArrayList<>(blocks.size());
        for (net.minecraft.core.BlockPos pos : blocks) {
            if (pos != null && isAllowed(level.getBlockState(pos))) {
                kept.add(pos);
            }
        }
        return kept;
    }

    /** 该坐标的方块是否允许被卓越前线的爆炸破坏（供 Mixin 调用）。 */
    public static boolean isExplosionBreakable(net.minecraft.world.level.Level level,
                                               net.minecraft.core.BlockPos pos) {
        return level != null && pos != null && isAllowed(level.getBlockState(pos));
    }

    /** 该方块是否允许被卓越前线的爆炸破坏。 */
    private static boolean isAllowed(BlockState state) {
        if (state.isAir()) {
            return false;
        }
        // 门类 / 活板门类
        if (state.is(BlockTags.DOORS) || state.is(BlockTags.TRAPDOORS)) {
            return true;
        }
        // 玻璃类：原版不可渗透方块 + Forge 的玻璃/玻璃板标签
        if (state.is(BlockTags.IMPERMEABLE)
            || state.is(Tags.Blocks.GLASS)
            || state.is(Tags.Blocks.GLASS_PANES)) {
            return true;
        }
        // 模组玻璃兜底：注册名里含 glass 的方块
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return id != null && id.getPath().contains("glass");
    }
}
