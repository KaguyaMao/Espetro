package org.espetro.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.espetro.Espetro;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 卓越前线（Superb Warfare）爆炸/弹药破坏方块的唯一白名单。
 *
 * <p>需求：卓越前线的任何攻击只允许破坏/击穿 <b>玻璃类、玻璃板、染色玻璃、活板门类、门类</b>方块，
 * 其它方块（混凝土、木材、沙袋、工事……）一律不能被卓越前线的爆炸或弹药破坏。</p>
 *
 * <p>卓越前线一共有三条会造成方块破坏的路径，本类为它们提供同一个白名单：</p>
 * <ol>
 *   <li><b>自研爆炸</b>{@code com.atsuishio.superbwarfare.tools.CustomExplosion}
 *       （重写了 {@code explode()}，逐层收集方块后直接调用
 *       {@code BlockState.onBlockExploded(Level, BlockPos, Explosion)} 销毁，<b>不会</b>把方块列表交给
 *       {@code ExplosionEvent.Detonate}）→ 由 {@code CustomExplosionBlockGateMixin} 的两个钩子拦截。</li>
 *   <li><b>钻地/预破坏</b>：炮弹/火箭/航弹/C4 等类里直接调用的
 *       {@code ServerLevel/Level.destroyBlock(BlockPos, boolean)}
 *       （受 SBW 的 {@code extra_explosion_effect} 开关控制）→ 由
 *       {@code SbwLevelDestroyBlockGateMixin} 拦截，只放行白名单方块。</li>
 *   <li><b>原生爆炸</b>：SBW 少数地方直接调用 {@code Level.explode(...)}（爆炸类是原版
 *       {@code net.minecraft.world.level.Explosion}，不是 CustomExplosion）→ 由本类的
 *       {@link #onExplosionDetonate} 兜底过滤。</li>
 * </ol>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class SuperbWarfareBlockGate {

    private static final String SBW_PACKAGE = "com.atsuishio.superbwarfare";

    /** 每个钩子只打印一次日志，避免刷屏。 */
    private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();

    private static final StackWalker WALKER =
        StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);

    /** 判定“当前破坏是不是卓越前线发起的”时，需要跳过的 MC / Forge / 本模组 / JDK 栈帧前缀。 */
    private static final String[] INTERNAL_PREFIXES = {
        "net.minecraft.", "net.minecraftforge.", "org.espetro.", "com.mojang.",
        "java.", "javax.", "jdk.", "sun.", "cpw.mods.", "org.spongepowered.",
        "it.unimi.", "kotlin.", "org.slf4j.", "com.electronwill."
    };

    private SuperbWarfareBlockGate() {
    }

    // ------------------------------------------------------------------
    // 路径 ③：兜底过滤原生爆炸（SBW 自己 level.explode(...) 出来的 Explosion）
    // ------------------------------------------------------------------

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();
        if (explosion == null || !isSuperbWarfareExplosion(explosion)) {
            return;
        }
        var level = event.getLevel();
        List<BlockPos> affected = event.getAffectedBlocks();
        if (affected == null || affected.isEmpty()) {
            return; // CustomExplosion 的方块表在事件前已被自身清空，走 mixin
        }
        int before = affected.size();
        affected.removeIf((pos) -> !isAllowed(level.getBlockState(pos)));
        int removed = before - affected.size();
        if (removed > 0) {
            logOnce("detonate", "[SBW] 原生爆炸白名单已生效：剔除 " + removed + " 个非玻璃/门/活板门方块（保留 " + affected.size() + " 个）");
        }
    }

    /** 该爆炸是否由卓越前线造成（自身爆炸类，或直接/间接来源是卓越前线的实体/载具）。 */
    private static boolean isSuperbWarfareExplosion(Explosion explosion) {
        if (explosion.getClass().getName().startsWith(SBW_PACKAGE)) {
            return true;
        }
        return isSbwClass(explosion.getDirectSourceEntity()) || isSbwClass(explosion.getIndirectSourceEntity());
    }

    private static boolean isSbwClass(Entity entity) {
        return entity != null && entity.getClass().getName().startsWith(SBW_PACKAGE);
    }

    // ------------------------------------------------------------------
    // 路径 ①：CustomExplosion 的两个钩子（由 CustomExplosionBlockGateMixin 调用）
    // ------------------------------------------------------------------

    /**
     * 过滤一批即将被卓越前线自研爆炸销毁的方块，只保留允许的（玻璃类/活板门类/门类）。
     *
     * <p>SBW 的 {@code CustomExplosion} 自己销毁方块、且不把方块列表交给
     * {@code ExplosionEvent.Detonate}，所以真正的拦截点在
     * {@code CustomExplosionBlockGateMixin} 里，本方法供其调用。</p>
     */
    public static Collection<BlockPos> filterBlocks(Level level, Collection<BlockPos> blocks) {
        if (level == null || blocks == null || blocks.isEmpty()) {
            return blocks;
        }
        List<BlockPos> kept = new ArrayList<>(blocks.size());
        for (BlockPos pos : blocks) {
            if (pos != null && isAllowed(level.getBlockState(pos))) {
                kept.add(pos);
            }
        }
        int removed = blocks.size() - kept.size();
        if (removed > 0) {
            logOnce("processBlockList", "[SBW] 爆炸白名单(入参过滤)已生效：剔除 " + removed
                + " 个非玻璃/门/活板门方块（保留 " + kept.size() + " 个）");
        }
        return kept;
    }

    /** 该坐标的方块是否允许被卓越前线的爆炸破坏（供 Mixin 调用）。 */
    public static boolean isExplosionBreakable(Level level, BlockPos pos) {
        return level != null && pos != null && isAllowed(level.getBlockState(pos));
    }

    /** 真正执行破坏的那一次调用（hooks ②），命中的方块被记一次日志便于开服后核对。 */
    public static boolean allowExplodedBlock(Level level, BlockPos pos) {
        boolean allowed = isExplosionBreakable(level, pos);
        if (!allowed) {
            logOnce("onBlockExploded", "[SBW] 爆炸白名单(破坏兜底)已生效：拦下 "
                + describe(level, pos) + " 不被爆炸破坏");
        }
        return allowed;
    }

    // ------------------------------------------------------------------
    // 路径 ②：钻地/预破坏 level.destroyBlock(...)（由 SbwLevelDestroyBlockGateMixin 调用）
    // ------------------------------------------------------------------

    /**
     * 判断一次 {@code Level#destroyBlock(BlockPos, boolean, Entity, int)} 是否要被白名单拦下。
     *
     * <p>只有“调用栈上第一个非 MC/Forge/本模组的帧”属于卓越前线时才介入；失败一律放行
     * （宁可漏拦，也不能影响原版或其它模组的破坏流程）。</p>
     *
     * @return true 表示应当取消这次方块破坏
     */
    public static boolean shouldGateDestroyBlock(Level level, BlockPos pos) {
        try {
            if (level == null || pos == null) {
                return false;
            }
            BlockState state = level.getBlockState(pos);
            if (isAllowed(state)) {
                return false;
            }
            if (!isSuperbWarfareCaller()) {
                return false;
            }
            logOnce("level#destroyBlock", "[SBW] 方块破坏白名单(钻地/预破坏兜底)已生效：拦下 "
                + describe(level, pos) + " 被卓越前线直接破坏");
            return true;
        } catch (Throwable t) {
            return false; // 失败开放：任何异常都不干预破坏流程
        }
    }

    /** 调用栈上第一个非 MC/Forge/本模组/JDK 的帧是否来自卓越前线。 */
    private static boolean isSuperbWarfareCaller() {
        return WALKER.walk(frames -> frames
            .limit(8)
            .map(StackWalker.StackFrame::getDeclaringClass)
            .filter(c -> !isInternalFrame(c))
            .findFirst()
            .map(c -> c.getName().startsWith(SBW_PACKAGE))
            .orElse(false));
    }

    private static boolean isInternalFrame(Class<?> type) {
        String name = type.getName();
        for (String prefix : INTERNAL_PREFIXES) {
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------
    // 白名单本体
    // ------------------------------------------------------------------

    /** 该方块是否允许被卓越前线的爆炸/弹药破坏。 */
    private static boolean isAllowed(BlockState state) {
        if (state == null || state.isAir()) {
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

    // ------------------------------------------------------------------
    // 日志
    // ------------------------------------------------------------------

    private static void logOnce(String key, String message) {
        if (LOGGED.add(key)) {
            Espetro.LOGGER.info(message + "（此后同类命中不再重复打印）");
        }
    }

    private static String describe(Level level, BlockPos pos) {
        try {
            BlockState state = level.getBlockState(pos);
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            return (id == null ? state.toString() : id.toString()) + " @ " + pos.toShortString();
        } catch (Throwable t) {
            return String.valueOf(pos);
        }
    }
}
