package org.espetro.client.gui;

import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;

/**
 * 多个界面共用的 GUI 纹理。新增共用图标时在这里声明，避免各处重复拼路径。
 * 所有图标为 128×128 源纹理（阵营背景图除外）。
 */
public final class EspetroIcons {

    public static final ResourceLocation AMMO_SUPPLY = id("textures/gui/squad/ammo_supply.png");
    public static final ResourceLocation CONSTRUCTION_SUPPLY = id("textures/gui/squad/construction_supply.png");
    public static final ResourceLocation AMMO_CRATE = id("textures/gui/squad/ammo_crate.png");
    public static final ResourceLocation FOB_STATUS = id("textures/gui/squad/fob_status.png");
    public static final ResourceLocation RALLY_DEPLOY = id("textures/gui/squad/rally_deploy.png");
    public static final ResourceLocation VEHICLE_DEPLOY = id("textures/gui/squad/vehicle_deploy.png");
    public static final ResourceLocation SKILL_UNAVAILABLE = id("textures/gui/commander_skills/unavailable.png");
    public static final ResourceLocation ATTACK_FACTION = id("textures/gui/attack_faction.png");
    public static final ResourceLocation DEFEND_FACTION = id("textures/gui/defend_faction.png");

    /** 图标源纹理边长。 */
    public static final int ICON_TEXTURE_SIZE = 128;

    private EspetroIcons() {
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(Espetro.MOD_ID, path);
    }
}
