package org.espetro.client.gui;

import net.minecraft.resources.ResourceLocation;
import org.esradial.client.RadialMenuClientApi;
import org.esradial.client.RadialMenuData;

/**
 * 各轮盘控制器共用的 EsRadial 小工具。
 * <p>
 * 每个控制器以自己的 owner 字符串打开菜单；判断"当前显示的是不是我的菜单"统一用 {@link #isShowing}。
 */
public final class EspetroRadials {

    private EspetroRadials() {
    }

    /** 当前活动轮盘是否正显示 {@code menuId}。 */
    public static boolean isShowing(ResourceLocation menuId) {
        return RadialMenuClientApi.activeMenuId().filter(menuId::equals).isPresent();
    }

    /** 以 {@code owner} 身份打开点击式轮盘（松开按键不关闭）。 */
    public static boolean openClick(String owner, RadialMenuData data) {
        return RadialMenuClientApi.open(data, RadialMenuClientApi.OpenOptions.click(owner));
    }
}
