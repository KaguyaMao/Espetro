/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadialActionPacket;
import org.espetro.team.CommanderSkillManager;

public class SquadRadialMenuScreen
extends EspetroMenuScreen {
    private static final ResourceLocation RADIO = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/radio_deploy.png");
    private static final ResourceLocation HAB = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/hab_deploy.png");
    private static final ResourceLocation RALLY = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/rally_deploy.png");
    private static final ResourceLocation CONSTRUCTION = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/deposit_supply.png");
    private static final ResourceLocation AMMO = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/fob_status.png");
    private static final ResourceLocation VEHICLE = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/vehicle_deploy.png");
    private static final ResourceLocation COMMAND = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/commander_skills/command.png");
    private final List<Option> options = new ArrayList<Option>();
    private int selectedIndex = -1;
    private EspetroAuiWidgets.Text centerLabel;

    public SquadRadialMenuScreen(boolean commander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        super(Component.m_237113_("\u6218\u672f\u4ea4\u4e92"));
        this.addTacticalOption("\u90e8\u7f72 Radio", RADIO, 128, RadialActionPacket.Action.DEPLOY_RADIO);
        this.addTacticalOption("\u90e8\u7f72\u5175\u7ad9", HAB, 128, RadialActionPacket.Action.DEPLOY_HAB);
        this.addTacticalOption("\u90e8\u7f72 Rally", RALLY, 128, RadialActionPacket.Action.DEPLOY_RALLY);
        this.addTacticalOption("\u67e5\u770b Radio \u72b6\u6001", AMMO, 128, RadialActionPacket.Action.FOB_STATUS);
        if (commander) {
            this.addTacticalOption("\u8f7d\u5177\u4fe1\u606f", VEHICLE, 128, RadialActionPacket.Action.DEPLOY_VEHICLE);
        }
        if (commander && skills != null) {
            for (CommanderSkillManager.SkillView skill : skills) {
                ResourceLocation icon;
                int cooldown = cooldowns == null ? 0 : cooldowns.getOrDefault(skill.id(), 0);
                ResourceLocation resourceLocation = icon = skill.icon() == null ? null : ResourceLocation.m_135820_(skill.icon());
                if (icon == null) {
                    icon = COMMAND;
                }
                String label = cooldown > 0 ? skill.displayName() + " \u00a77(" + cooldown + "s)" : skill.displayName();
                this.options.add(new Option(label, icon, 128, cooldown <= 0, () -> NetworkManager.sendCommanderSkillActivate(skill.id())));
            }
        }
    }

    private void addTacticalOption(String label, ResourceLocation icon, int textureWidth, RadialActionPacket.Action action) {
        this.options.add(new Option(label, icon, textureWidth, true, () -> NetworkManager.sendRadialAction(action)));
    }

    @Override
    public void m_86600_() {
        KeyMapping key;
        super.m_86600_();
        Object object = Espetro.KEY_RADIAL;
        if (object instanceof KeyMapping && !(key = (KeyMapping)object).m_90857_()) {
            if (this.selectedIndex >= 0 && this.selectedIndex < this.options.size()) {
                Option selected = this.options.get(this.selectedIndex);
                if (selected.enabled) {
                    selected.action.run();
                }
            }
            this.m_7379_();
        }
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        int centerX = this.f_96543_ / 2;
        int centerY = this.f_96544_ / 2;
        int radius = this.options.size() > 6 ? 94 : 72;
        double step = Math.PI * 2 / (double)Math.max(1, this.options.size());
        for (int i = 0; i < this.options.size(); ++i) {
            double angle = -1.5707963267948966 + step * (double)i;
            int optionX = centerX + (int)Math.round(Math.cos(angle) * (double)radius);
            int optionY = centerY + (int)Math.round(Math.sin(angle) * (double)radius);
            root.addChild(new RadialOption(i, optionX, optionY, this.options.get(i)));
        }
        this.centerLabel = EspetroAuiWidgets.centeredText(centerX - 90, centerY - 5, 180, "\u79fb\u52a8\u9f20\u6807\u9009\u62e9", -1);
        root.addChild(this.centerLabel);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = this.f_96543_ / 2;
        int centerY = this.f_96544_ / 2;
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        this.selectedIndex = this.select(dx, dy);
        graphics.m_280509_(0, 0, this.f_96543_, this.f_96544_, 0x66000000);
        if (this.centerLabel != null) {
            this.centerLabel.setText(this.selectedIndex < 0 ? "\u79fb\u52a8\u9f20\u6807\u9009\u62e9" : this.options.get((int)this.selectedIndex).label);
        }
    }

    private int select(double dx, double dy) {
        if (this.options.isEmpty() || dx * dx + dy * dy < 625.0) {
            return -1;
        }
        double normalized = Math.atan2(dy, dx) + 1.5707963267948966;
        if (normalized < 0.0) {
            normalized += Math.PI * 2;
        }
        double step = Math.PI * 2 / (double)this.options.size();
        return (int)Math.round(normalized / step) % this.options.size();
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private record Option(String label, ResourceLocation icon, int textureWidth, boolean enabled, Runnable action) {
    }

    private final class RadialOption
    extends GuiElement {
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
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            int half;
            boolean active = SquadRadialMenuScreen.this.selectedIndex == this.index;
            int n = half = active ? 23 : 20;
            graphics.m_280509_(this.centerX - half - 3, this.centerY - half - 3, this.centerX + half + 3, this.centerY + half + 3, !this.option.enabled ? -801496028 : (active ? -531931829 : -803726819));
            graphics.m_280637_(this.centerX - half - 3, this.centerY - half - 3, (half + 3) * 2, (half + 3) * 2, active ? -1 : -8946816);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.option.enabled ? (active ? 1.0f : 0.8f) : 0.45f);
            graphics.m_280411_(this.option.icon, this.centerX - half, this.centerY - half, half * 2, half * 2, 0.0f, 0.0f, 128, 128, this.option.textureWidth, 128);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            graphics.m_280137_(SquadRadialMenuScreen.this.f_96547_, this.option.label, this.centerX, this.centerY + half + 6, !this.option.enabled ? -39322 : (active ? -1 : -4209721));
            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }
    }
}

