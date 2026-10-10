package org.espetro.vehicle;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.espetro.Espetro;

/**
 * 禁止玩家打开「载具 / 武器的物品栏 GUI」（可用配置开关，管理员豁免）。
 *
 * <p>开关：{@code config/espetro-common.toml} 的 {@code [vehicle]} 段：</p>
 * <ul>
 *   <li>{@code disableVehicleInventoryGui}（默认 {@code true}）= 是否禁用物品栏 GUI；</li>
 *   <li>{@code inventoryGuiBypassPermissionLevel}（默认 {@code 2}）= 权限等级 ≥ 此值的玩家不受限制
 *       （原版 OP 为 2，即管理员豁免；设为 {@code 0} 表示所有人豁免，等于关闭）。</li>
 * </ul>
 *
 * <p>只拦物品栏界面本身：上车/下车/换座、撬棍回收、命名牌、C4、F 键的补给与固定武器兑换轮盘都不受影响。</p>
 *
 * <p>两道防线：</p>
 * <ol>
 *   <li>右键 SBW 的 container 系方块（{@code superbwarfare:container} / {@code small_container} /
 *       {@code lucky_container}）直接拒绝，避免 GUI 闪一下；</li>
 *   <li>兜底：任何来自 {@code com.atsuishio.superbwarfare} 的物品栏菜单一旦打开就立刻关闭
 *       （覆盖载具自带物品箱等非方块入口）。</li>
 * </ol>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID)
public final class VehicleInventoryGate {

    private VehicleInventoryGate() {
    }

    /** 配置总开关；配置读不到时按“启用”处理（保持禁用行为）。 */
    public static boolean isEnabled() {
        try {
            return VehicleInteractionConfig.DISABLE_INVENTORY_GUI.get();
        } catch (Throwable t) {
            return true;
        }
    }

    /** 该玩家是否豁免（管理员 / OP，按配置的权限等级）。 */
    public static boolean bypasses(ServerPlayer player) {
        if (player == null) {
            return false;
        }
        try {
            int level = VehicleInteractionConfig.INVENTORY_GUI_BYPASS_PERMISSION_LEVEL.get();
            return level <= 0 || player.hasPermissions(level);
        } catch (Throwable t) {
            return player.hasPermissions(2);
        }
    }

    /** 右键 SBW 容器方块 → 直接拒绝（管理员豁免）。 */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onContainerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!isEnabled() || bypasses(player)) {
            return;
        }
        if (!isSuperbWarfareContainer(event.getLevel().getBlockState(event.getPos()).getBlock())) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
        player.displayClientMessage(Component.literal("§c载具/武器物品栏已禁用（管理员不受限制）。"), true);
    }

    /** 兜底：SBW 的物品栏菜单一打开就关掉（管理员豁免）。 */
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (!isEnabled() || bypasses(player)) {
            return;
        }
        AbstractContainerMenu menu = event.getContainer();
        if (menu == null) {
            return;
        }
        if (!menu.getClass().getName().startsWith("com.atsuishio.superbwarfare")) {
            return;
        }
        player.closeContainer();
        player.displayClientMessage(Component.literal("§c载具/武器物品栏已禁用（管理员不受限制）。"), true);
    }

    private static boolean isSuperbWarfareContainer(net.minecraft.world.level.block.Block block) {
        if (block == null) {
            return false;
        }
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
        if (id == null) {
            return false;
        }
        return "superbwarfare".equals(id.getNamespace()) && id.getPath().contains("container");
    }
}
