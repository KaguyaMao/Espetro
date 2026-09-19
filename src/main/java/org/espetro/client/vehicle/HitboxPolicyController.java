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
 */
public final class HitboxPolicyController {

    private static boolean renderHitBoxes = true;
    private static boolean registered;

    private HitboxPolicyController() {
    }

    /** 收到服务端策略包（网络线程 → 主线程） */
    public static void onPolicy(HitboxPolicyPacket packet) {
        renderHitBoxes = packet != null && packet.getRenderHitBoxes();
        Minecraft mc = Minecraft.getInstance();
        if (mc != null && mc.getEntityRenderDispatcher() != null) {
            mc.getEntityRenderDispatcher().setRenderHitBoxes(renderHitBoxes);
        }
        register();
    }

    public static boolean isHitBoxForcedOff() {
        return !renderHitBoxes;
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
