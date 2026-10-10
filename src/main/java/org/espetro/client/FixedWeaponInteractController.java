package org.espetro.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.EntityHitResult;
import org.espetro.network.NetworkManager;
import org.lwjgl.glfw.GLFW;

/**
 * 看向己方固定武器时按 <b>F</b>：用所在 FOB 的弹药兑换一次弹药。
 *
 * <p>与「乘车时按 F 开载具轮盘」互斥：只要玩家正在乘坐任何东西，就把 F 让给载具系统。
 * 单档位设计 —— 每按一次兑换一次，可连续按。</p>
 */
public final class FixedWeaponInteractController {

    private static boolean keyWasDown;

    private FixedWeaponInteractController() {
    }

    public static void tick(Minecraft mc) {
        if (mc == null || mc.player == null || mc.level == null) {
            keyWasDown = false;
            return;
        }
        boolean down = isInteractKeyDown(mc);
        boolean pressed = down && !keyWasDown;
        keyWasDown = down;
        // 按住 F 期间保持轮盘开启，松手关闭（兑换不再自动关闭轮盘）
        org.espetro.client.gui.FixedWeaponWheelController.holdTick(down);
        if (!pressed || mc.screen != null) {
            return;
        }
        if (mc.player.isPassenger()) {
            return; // 乘车/乘坐固定武器时 F 交给载具轮盘
        }
        if (!(mc.hitResult instanceof EntityHitResult hit)) {
            return;
        }
        // 只发"打开轮盘"请求：服务端确认是己方固定武器才回轮盘数据，否则静默
        NetworkManager.sendFixedWeaponOpen(hit.getEntity().getUUID());
    }

    /** 与载具轮盘保持一致：优先 SBW 的交互键，取不到就直读 F。 */
    private static boolean isInteractKeyDown(Minecraft mc) {
        try {
            Class<?> keys = Class.forName("com.atsuishio.superbwarfare.init.ModKeyMappings");
            Object mapping = keys.getField("INTERACT").get(null);
            if (mapping instanceof net.minecraft.client.KeyMapping keyMapping) {
                return keyMapping.isDown();
            }
        } catch (Throwable ignored) {
            // 退化为直读 F
        }
        try {
            return GLFW.glfwGetKey(mc.getWindow().getWindow(), GLFW.GLFW_KEY_F) == GLFW.GLFW_PRESS;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
