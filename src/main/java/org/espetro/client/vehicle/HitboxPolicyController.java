package org.espetro.client.vehicle;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.espetro.network.HitboxPolicyPacket;

/**
 * 客户端碰撞体积（F3+B）显示策略。
 * <p>服务端通过 {@code /espetro hitbox off} 下发策略后，客户端在此强制关闭
 * 碰撞体积渲染，并在每 tick 持续压制——即使玩家再次按 F3+B 也会被立即关闭。
 * <p><b>策略为"允许"时客户端不做任何干预</b>：既不打开也不关闭，完全交给玩家自己的
 * F3+B 开关（早期版本会在收到允许策略时主动 {@code setRenderHitBoxes(true)}，
 * 导致一进游戏就强制显示碰撞体积，现已移除该行为）。
 */
public final class HitboxPolicyController {

    /** 服务端是否允许显示碰撞体积；默认 true = 不干预。 */
    private static boolean renderHitBoxes = true;
    private static boolean registered;

    private HitboxPolicyController() {
    }

    /** 收到服务端策略包（网络线程 → 主线程） */
    public static void onPolicy(HitboxPolicyPacket packet) {
        renderHitBoxes = packet != null && packet.getRenderHitBoxes();
        if (!renderHitBoxes) {
            // 仅在"禁止"时强制关闭；允许时保持玩家自己的 F3+B 状态不变
            forceOff();
        }
        register();
    }

    public static boolean isHitBoxForcedOff() {
        return !renderHitBoxes;
    }

    private static void forceOff() {
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getEntityRenderDispatcher() != null) {
            mc.getEntityRenderDispatcher().setRenderHitBoxes(false);
        }
    }

    private static void register() {
        if (registered) {
            return;
        }
        registered = true;
        MinecraftForge.EVENT_BUS.register(HitboxPolicyController.class);
    }

    /** 每 tick 强制保持服务端策略：禁用时即使 F3+B 打开也会被立即关掉。 */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (renderHitBoxes) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getEntityRenderDispatcher() != null
            && mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            mc.getEntityRenderDispatcher().setRenderHitBoxes(false);
        }
    }
}
