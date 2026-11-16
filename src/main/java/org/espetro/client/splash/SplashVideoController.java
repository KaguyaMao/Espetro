package org.espetro.client.splash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;

/**
 * 启动视频的触发时机：加载完成后、主菜单第一次弹出时拦截一次。
 *
 * <p>只认 {@link TitleScreen}（FancyMenu 的自定义主菜单也是它的子类），
 * 因此不会抢占 DrippyLoadingScreen / FancyMenu 的加载覆盖层——那些屏幕有自己的
 * 收尾逻辑，被顶掉后会停在黑屏进不了主菜单。</p>
 */
public final class SplashVideoController {

    private static boolean triggered;
    private static boolean played;

    private SplashVideoController() {
    }

    public static void markPlayed() {
        played = true;
    }

    /** 是否已经播放过（加载期叠加或主菜单屏二者之一）。 */
    public static boolean wasPlayedByScreen() {
        return played;
    }

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        tryPlay(event.getScreen());
    }

    /** Early decoding may fail after TitleScreen was initialized underneath the loading overlay. */
    static void tryFallback(Minecraft minecraft) {
        tryPlay(minecraft.screen);
    }

    private static void tryPlay(Screen target) {
        if (played || triggered || SplashEarlyRenderer.get().isRunning()) {
            return;
        }
        if (!(target instanceof TitleScreen)) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null) {
            return;
        }
        if (!SplashVideoAssets.available()) {
            played = true;
            return;
        }
        triggered = true;
        // 放到本 tick 之后切屏，避免在 Screen 初始化过程中重入
        minecraft.execute(() -> {
            try {
                minecraft.setScreen(new SplashVideoScreen(target));
            } catch (Throwable error) {
                played = true;
                org.espetro.Espetro.LOGGER.warn("[启动视频] 启动失败，已跳过: {}", error.toString());
            }
        });
    }
}
