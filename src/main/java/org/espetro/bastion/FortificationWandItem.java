package org.espetro.bastion;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 工事建筑选定棒（管理员专用，原版木棍贴图）。
 *
 * <p>所有点击逻辑在 {@link FortificationWandHandler}（服务端权威，permission 2 门禁）；
 * 本类只负责物品本体与 tooltip。</p>
 */
public class FortificationWandItem extends Item {

    public static final String ITEM_ID = "fortification_wand";

    public FortificationWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("§7左键 = 角 A    右键 = 角 B"));
        tooltip.add(Component.literal("§7潜行+右键 = 锚点（放置时的旋转中心）"));
        tooltip.add(Component.literal("§7潜行+左键 = 清空选区"));
        tooltip.add(Component.literal("§8/espetro fort info | save <名字> | reload"));
        tooltip.add(Component.literal("§8仅管理员可用"));
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
