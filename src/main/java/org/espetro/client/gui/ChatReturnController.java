package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.client.aui.AuiScreen;

/**
 * 聊天后自动回到刚才的菜单。
 *
 * <p>本模组界面里按 T/斜杠 会打开原版聊天（用 {@link ChatScreen} 替换当前界面），
 * 关掉聊天后原版只会回到游戏画面。这里记住来源菜单，等聊天关闭（{@code mc.screen == null}）
 * 时把菜单放回去，避免玩家每次看聊天都要重新按一次 J。</p>
 *
 * <p>只有在"确实是从本模组菜单打开聊天"时才恢复：一旦玩家中途切到别的界面
 * （例如服务端推来的投票界面）或断线，就放弃恢复，绝不把玩家拽回旧界面。</p>
 */
@Mod.EventBusSubscriber(modid = Espetro.MOD_ID, value = Dist.CLIENT)
public final class ChatReturnController {

    private static AuiScreen pending;

    private ChatReturnController() {
    }

    /** 从某个菜单打开聊天前调用，记住它。 */
    public static void rememberMenu(AuiScreen screen) {
        pending = screen;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || pending == null) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            pending = null;   // 断线/退世界：放弃恢复
            return;
        }
        if (minecraft.screen == null) {
            AuiScreen restore = pending;
            pending = null;
            minecraft.setScreen(restore);   // 聊天已关闭 → 回到刚才的菜单
            return;
        }
        if (!(minecraft.screen instanceof ChatScreen)) {
            pending = null;   // 玩家切到了别的界面：不要抢回来
        }
    }
}
