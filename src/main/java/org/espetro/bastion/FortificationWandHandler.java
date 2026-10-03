package org.espetro.bastion;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 工事选定棒的服务端交互：左键=角A，右键=角B，潜行+右键=锚点，潜行+左键=清空。
 *
 * <p>**仅管理员**：手持棒子且 {@code hasPermissions(2)} 才生效；否则棒子等同于普通木棍，
 * 不修改选区也不取消原版行为。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class FortificationWandHandler {

    /** 每 10 tick 同步一次选区到客户端（渲染 + HUD）。 */
    private static final int SYNC_INTERVAL_TICKS = 10;
    private static final long MESSAGE_THROTTLE_MS = 3_000L;

    private static final Map<UUID, Long> LAST_DENIAL = new HashMap<>();

    private FortificationWandHandler() {
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        handle(event, true);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        handle(event, false);
    }

    private static void handle(PlayerInteractEvent event, boolean attack) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (!holdsWand(player)) return;
        try {
            if (!FortificationAuthoringManager.hasPermission(player)) {
                deny(player);
                return;
            }
            event.setCanceled(true);
            if (event instanceof PlayerInteractEvent.RightClickBlock right) {
                right.setUseBlock(net.minecraftforge.eventbus.api.Event.Result.DENY);
                right.setUseItem(net.minecraftforge.eventbus.api.Event.Result.DENY);
            }
            BlockPos pos = event.getPos();
            FortificationAuthoringManager manager = FortificationAuthoringManager.getInstance();
            String message;
            if (player.isShiftKeyDown()) {
                if (attack) {
                    manager.clear(player);
                    manager.syncTo(player);
                    player.displayClientMessage(Component.literal("§e选区已清空。"), false);
                    return;
                }
                message = manager.setAnchor(player, pos);
            } else {
                message = manager.setCorner(player, attack, pos);
            }
            manager.syncTo(player);
            player.displayClientMessage(Component.literal(message), true);
        } catch (Exception e) {
            // 交互事件发生在服务端 tick 内：异常会直接崩服，必须吞掉并记日志。
            Espetro.LOGGER.error("[工事编辑器] 选定棒交互失败（已忽略，不会崩服）", e);
        }
    }

    private static boolean holdsWand(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        return !main.isEmpty() && main.getItem() instanceof FortificationWandItem;
    }

    private static void deny(ServerPlayer player) {
        long now = System.currentTimeMillis();
        Long previous = LAST_DENIAL.put(player.getUUID(), now);
        if (previous != null && now - previous < MESSAGE_THROTTLE_MS) return;
        player.displayClientMessage(Component.literal("§c选定棒仅管理员可用。"), true);
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            FortificationAuthoringManager.getInstance().forget(player.getUUID());
            LAST_DENIAL.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        var server = Espetro.getServer();
        if (server == null || server.getTickCount() % SYNC_INTERVAL_TICKS != 0) return;
        FortificationAuthoringManager manager = FortificationAuthoringManager.getInstance();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (!holdsWand(player) || !FortificationAuthoringManager.hasPermission(player)) continue;
            manager.syncTo(player);   // syncTo 内部自带 try/catch，不会让 tick 抛异常
        }
    }
}
