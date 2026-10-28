package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadialActionPacket;
import org.espetro.team.CommanderSkillManager;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SquadRadialMenuScreen extends EspetroMenuScreen {

    private static final ResourceLocation RADIO =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/radio_deploy.png");
    private static final ResourceLocation HAB =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/hab_deploy.png");
    private static final ResourceLocation RALLY =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/rally_deploy.png");
    private static final ResourceLocation CONSTRUCTION =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/deposit_supply.png");
    private static final ResourceLocation AMMO =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/fob_status.png");
    private static final ResourceLocation VEHICLE =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/squad/vehicle_deploy.png");
    private static final ResourceLocation COMMAND =
        ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/commander_skills/command.png");

    private final List<Option> options = new ArrayList<>();
    private int selectedIndex = -1;
    private EspetroAuiWidgets.Text centerLabel;

    /** 圆盘中心托底半径：盖住中心提示文字，又不压到最内圈选项。 */
    private static final int HUB_RADIUS = 34;

    public SquadRadialMenuScreen(boolean commander, Map<String, Integer> cooldowns,
                                 List<CommanderSkillManager.SkillView> skills) {
        super(Component.literal("战术交互"));
        addTacticalOption("部署 Radio", RADIO, 128, RadialActionPacket.Action.DEPLOY_RADIO);
        addTacticalOption("部署兵站", HAB, 128, RadialActionPacket.Action.DEPLOY_HAB);
        addTacticalOption("部署 Rally", RALLY, 128, RadialActionPacket.Action.DEPLOY_RALLY);
        addTacticalOption("查看 Radio 状态", AMMO, 128, RadialActionPacket.Action.FOB_STATUS);
        if (commander) {
            addTacticalOption("载具信息", VEHICLE, 128, RadialActionPacket.Action.DEPLOY_VEHICLE);
        }

        if (commander && skills != null) {
            for (CommanderSkillManager.SkillView skill : skills) {
                int cooldown = cooldowns == null ? 0 : cooldowns.getOrDefault(skill.id(), 0);
                ResourceLocation icon = skill.icon() == null
                    ? null : ResourceLocation.tryParse(skill.icon());
                if (icon == null) icon = COMMAND;
                String label = cooldown > 0
                    ? skill.displayName() + " (" + cooldown + "s)"
                    : skill.displayName();
                options.add(new Option(label, icon, 128, cooldown <= 0,
                    () -> {
                    org.espetro.client.gui.CommanderSkillTargetRequest.arm();
                    NetworkManager.sendCommanderSkillActivate(skill.id());
                }));
            }
        }
    }

    private void addTacticalOption(String label, ResourceLocation icon, int textureWidth,
                                   RadialActionPacket.Action action) {
        options.add(new Option(label, icon, textureWidth, true,
            () -> NetworkManager.sendRadialAction(action)));
    }

    @Override
    public void tick() {
        super.tick();
        if (Espetro.KEY_RADIAL instanceof net.minecraft.client.KeyMapping key && !key.isDown()) {
            if (selectedIndex >= 0 && selectedIndex < options.size()) {
                Option selected = options.get(selectedIndex);
                if (selected.enabled) selected.action.run();
            }
            onClose();
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = wheelRadius();
        double step = Math.PI * 2.0 / Math.max(1, options.size());
        for (int i = 0; i < options.size(); i++) {
            double angle = -Math.PI / 2.0 + step * i;
            int optionX = centerX + (int) Math.round(Math.cos(angle) * radius);
            int optionY = centerY + (int) Math.round(Math.sin(angle) * radius);
            root.addChild(new RadialOption(i, optionX, optionY, options.get(i)));
        }
        centerLabel = EspetroAuiWidgets.centeredText(centerX - 90, centerY - 5, 180,
            "移动鼠标选择", UiTheme.TEXT);
        centerLabel.setTextScale(UiTheme.TYPE_HEADING);
        centerLabel.setShadow(true);
        root.addChild(centerLabel);
    }

    private int wheelRadius() {
        return options.size() > 6 ? 94 : 72;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = width / 2;
        int centerY = height / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        selectedIndex = select(dx, dy);

        // 世界压暗一层（令牌派生的半透明深底，仍能透视战场）。
        graphics.fill(0, 0, width, height, UiTheme.withAlpha(UiTheme.BACKDROP_TOP, 0x70));
        // 圆盘导轨 + 中心托底：轮盘"盘面"由圆环与圆盘构成，而不是直角面板。
        UiPaint.ring(graphics, centerX, centerY, wheelRadius(),
            UiTheme.withAlpha(UiTheme.ACCENT, 0x18));
        UiPaint.circle(graphics, centerX, centerY, HUB_RADIUS,
            UiTheme.withAlpha(UiTheme.SURFACE_0, 0xE6));
        UiPaint.ring(graphics, centerX, centerY, HUB_RADIUS, UiTheme.HAIRLINE_STRONG);
        if (centerLabel != null) {
            centerLabel.setText(selectedIndex < 0 ? "移动鼠标选择"
                : options.get(selectedIndex).label);
            centerLabel.setColor(UiTheme.TEXT);
        }
    }

    private final class RadialOption extends GuiElement {
        private final int index;
        private final int centerX;
        private final int centerY;
        private final Option option;

        private RadialOption(int index, int centerX, int centerY, Option option) {
            super(centerX - 30, centerY - 30, 60, 76);
            this.index = index;
            this.centerX = centerX;
            this.centerY = centerY;
            this.option = option;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            boolean active = selectedIndex == index;
            int half = active ? 23 : 20;
            int ringRadius = half + 3;
            // 未解锁=红底、选中=强调色底、常态=表面层；描边同语义。
            int plate = !option.enabled ? UiTheme.NEGATIVE_SOFT
                : active ? UiTheme.ACCENT_SOFT : UiTheme.SURFACE_2;
            int border = !option.enabled ? UiTheme.alpha(UiTheme.NEGATIVE, 0.6f)
                : active ? UiTheme.ACCENT : UiTheme.HAIRLINE_STRONG;
            UiPaint.circle(graphics, centerX, centerY, ringRadius, plate);
            UiPaint.ring(graphics, centerX, centerY, ringRadius, border);
            if (active) {
                // 选中态：一圈更亮的柔光外环。
                UiPaint.ring(graphics, centerX, centerY, ringRadius + 1, UiTheme.ACCENT_LINE);
                UiPaint.ring(graphics, centerX, centerY, ringRadius + 2,
                    UiTheme.alpha(UiTheme.ACCENT, 0.25f));
            }
            UiPaint.circle(graphics, centerX, centerY, half, UiTheme.ICON_PLATE);
            RenderSystem.setShaderColor(1f, 1f, 1f,
                option.enabled ? (active ? 1f : 0.8f) : 0.45f);
            graphics.blit(option.icon, centerX - half, centerY - half, half * 2, half * 2,
                0f, 0f, 128, 128, option.textureWidth, 128);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            int labelColor = !option.enabled ? UiTheme.NEGATIVE
                : active ? UiTheme.TEXT : UiTheme.TEXT_SECOND;
            int labelWidth = font.width(option.label);
            graphics.drawString(font, option.label, centerX - labelWidth / 2,
                centerY + half + 6, labelColor, true);
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }

    private int select(double dx, double dy) {
        if (options.isEmpty() || dx * dx + dy * dy < 25 * 25) return -1;
        double normalized = Math.atan2(dy, dx) + Math.PI / 2.0;
        if (normalized < 0) normalized += Math.PI * 2.0;
        double step = Math.PI * 2.0 / options.size();
        return (int) Math.round(normalized / step) % options.size();
    }

    private record Option(String label, ResourceLocation icon, int textureWidth,
                          boolean enabled, Runnable action) {}

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
