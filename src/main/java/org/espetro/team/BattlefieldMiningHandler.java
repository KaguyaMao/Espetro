package org.espetro.team;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;

/**
 * 战场挖掘限制：用「无法满足方块所需挖掘等级」代替挖掘疲劳。
 * <ul>
 *   <li>{@link PlayerEvent.HarvestCheck} — 一律 canHarvest=false：<b>能挖掉但无掉落、无经验</b>
 *       （原版 {@code ServerPlayerGameMode.destroyBlock} 里掉落只由 {@code playerDestroy} 产生，
 *       而它受 canHarvest 控制；方块移除 {@code removeBlock} 与之无关）</li>
 *   <li>{@link PlayerEvent.BreakSpeed} — 非白名单方块挖掘速度归零，避免裂纹进度</li>
 *   <li>{@link BlockEvent.BreakEvent} — 白名单允许破坏，其余服务端兜底取消</li>
 * </ul>
 * 白名单由方块标签 {@code #espetro:minable_in_battle} 控制（默认：门/活板门/栅栏门/玻璃），
 * 数据包或 kubejs 均可增删，{@code /reload} 生效。
 * Radio / 队包等特殊交互走各自事件，不依赖原版破坏流程。
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class BattlefieldMiningHandler {

    /** 战局中允许用原版生存方式挖掉的方块（默认：门 / 活板门 / 栅栏门 / 各种玻璃）。 */
    public static final TagKey<Block> MINABLE_IN_BATTLE = TagKey.create(Registries.BLOCK,
        ResourceLocation.fromNamespaceAndPath(Espetro.MOD_ID, "minable_in_battle"));

    private BattlefieldMiningHandler() {
    }

    /** 是否在战局可挖白名单里。 */
    private static boolean minableInBattle(BlockState state) {
        return state != null && state.is(MINABLE_IN_BATTLE);
    }

    /**
     * 将玩家视为挖掘等级不足：白名单方块同样置 false —— 挖得掉，但**不掉落物品、不给经验**。
     * （掉落判定链：canHarvestBlock → if (removed && canHarvest) playerDestroy）
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (GameStateManager.getInstance().shouldRestrictBattlefieldMining(event.getEntity())) {
            event.setCanHarvest(false);
        }
    }

    /** 非白名单方块挖掘速度归零（客户端裂纹与服务端进度一致无法推进）；白名单保留原版速度。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (!GameStateManager.getInstance().shouldRestrictBattlefieldMining(event.getEntity())) {
            return;
        }
        if (minableInBattle(event.getState())) {
            return;
        }
        event.setNewSpeed(0.0f);
    }

    /** 服务端兜底：白名单允许破坏（经验清零兜底），其余即便其它路径试图破坏也取消。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null
            || !GameStateManager.getInstance().shouldRestrictBattlefieldMining(player)) {
            return;
        }
        if (minableInBattle(event.getState())) {
            event.setExpToDrop(0);
            return;
        }
        event.setCanceled(true);
    }
}
