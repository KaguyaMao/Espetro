/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.network.NetworkManager;
import org.espetro.team.CommanderSkillManager;

public class CommanderSkillScreen
extends EspetroMenuScreen {
    private Map<String, Integer> cooldowns = new HashMap<String, Integer>();
    private final Map<String, Long> cooldownEndsAtMillis = new HashMap<String, Long>();
    private List<CommanderSkillManager.SkillView> skills = new ArrayList<CommanderSkillManager.SkillView>();
    private boolean isCommander;
    private int lastCooldownSignature = Integer.MIN_VALUE;
    private final Map<String, SkillRow> skillRows = new HashMap<String, SkillRow>();

    public CommanderSkillScreen(boolean isCommander, Map<String, Integer> cooldowns) {
        this(isCommander, cooldowns, List.of());
    }

    public CommanderSkillScreen(boolean isCommander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        super(Component.m_237113_("\u6307\u6325\u5b98\u6280\u80fd"));
        this.isCommander = isCommander;
        this.setSkills(skills);
        this.setCooldowns(cooldowns);
    }

    public static void open(boolean isCommander, Map<String, Integer> cooldowns) {
        CommanderSkillScreen.open(isCommander, cooldowns, List.of());
    }

    public static void open(boolean isCommander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        Minecraft mc = Minecraft.m_91087_();
        mc.m_91152_(new CommanderSkillScreen(isCommander, cooldowns, skills));
    }

    public void updateData(boolean isCommander, Map<String, Integer> cooldowns) {
        this.updateData(isCommander, cooldowns, List.of());
    }

    public void updateData(boolean isCommander, Map<String, Integer> cooldowns, List<CommanderSkillManager.SkillView> skills) {
        ArrayList<CommanderSkillManager.SkillView> nextSkills = skills != null ? new ArrayList<CommanderSkillManager.SkillView>(skills) : new ArrayList();
        boolean layoutChanged = this.isCommander != isCommander || !this.skills.equals(nextSkills);
        this.isCommander = isCommander;
        this.setSkills(nextSkills);
        this.setCooldowns(cooldowns);
        if (this.root != null) {
            if (layoutChanged) {
                this.rebuildMenuRoot();
            } else {
                this.refreshSkillRows();
            }
        }
    }

    private void setSkills(List<CommanderSkillManager.SkillView> skills) {
        this.skills = skills != null ? new ArrayList<CommanderSkillManager.SkillView>(skills) : new ArrayList();
    }

    private void setCooldowns(Map<String, Integer> cooldowns) {
        this.cooldowns = cooldowns != null ? new HashMap<String, Integer>(cooldowns) : new HashMap();
        this.cooldownEndsAtMillis.clear();
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Integer> entry : this.cooldowns.entrySet()) {
            int seconds = entry.getValue() != null ? entry.getValue() : 0;
            if (seconds <= 0) continue;
            this.cooldownEndsAtMillis.put(entry.getKey(), now + (long)seconds * 1000L);
        }
        this.lastCooldownSignature = Integer.MIN_VALUE;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.skillRows.clear();
        int panelW = Math.max(300, Math.min(420, this.f_96543_ - 20));
        int panelH = Math.max(180, Math.min(320, this.f_96544_ - 24));
        int panelX = (this.f_96543_ - panelW) / 2;
        int panelY = Math.max(8, (this.f_96544_ - panelH) / 2);
        root.addChild(EspetroAuiWidgets.panel(panelX, panelY, panelW, panelH, 1645598, -10788256));
        root.addChild(EspetroAuiWidgets.text(panelX + 10, panelY + 8, "\u00a76\u00a7l\u6218\u672f\u6280\u80fd", -14490));
        root.addChild(EspetroAuiWidgets.button(panelX + panelW - 50, panelY + 7, 40, 14, "\u5173\u95ed", () -> Minecraft.m_91087_().m_91152_(null)));
        root.addChild(EspetroAuiWidgets.rect(panelX + 10, panelY + 26, panelW - 20, 1, 0x25FFFFFF));
        if (this.skills.isEmpty()) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 50, panelW, "\u00a77\u5f53\u524d\u89d2\u8272\u65e0\u53ef\u7528\u6280\u80fd", -2828064));
            this.lastCooldownSignature = this.getCooldownSignature();
            return;
        }
        int contentY = panelY + 34;
        int contentX = panelX + 10;
        int contentW = panelW - 20;
        for (CommanderSkillManager.SkillView skill : this.skills) {
            contentY = this.buildSkillCard(root, contentX, contentY, contentW, skill);
            contentY += 8;
        }
        this.lastCooldownSignature = this.getCooldownSignature();
    }

    private int buildSkillCard(GuiElement root, int x, int y, int width, CommanderSkillManager.SkillView skill) {
        int cardH = 62;
        int cooldownSec = this.getRemainingCooldownSeconds(skill.id());
        boolean onCooldown = cooldownSec > 0;
        int cardColor = onCooldown ? 0x55363636 : 0x60404040;
        int borderColor = onCooldown ? -10788256 : -2140310398;
        EspetroAuiWidgets.Panel panel = EspetroAuiWidgets.panel(x, y, width, cardH, cardColor, borderColor);
        root.addChild(panel);
        root.addChild(EspetroAuiWidgets.text(x + 8, y + 6, "\u00a7e" + skill.displayName(), -14490));
        root.addChild(EspetroAuiWidgets.text(x + 8, y + 20, "\u00a77" + skill.description(), -2828064));
        String stats = skill.stats() == null ? "" : skill.stats();
        root.addChild(EspetroAuiWidgets.text(x + 8, y + 34, stats, -5327681));
        int btnW = 56;
        int btnH = 16;
        int btnX = x + width - btnW - 8;
        int btnY = y + (cardH - btnH) / 2;
        EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(btnX, btnY, btnW, btnH, (String)(onCooldown ? cooldownSec + "\u79d2" : "\u53d1\u52a8"), () -> this.activateSkill(skill.id())).setEnabled(!onCooldown).setTextColor(onCooldown ? -5327681 : -9054838);
        root.addChild(button);
        this.skillRows.put(skill.id(), new SkillRow(panel, button));
        return y + cardH;
    }

    private int getRemainingCooldownSeconds(String skillId) {
        Long endsAt = this.cooldownEndsAtMillis.get(skillId);
        if (endsAt == null) {
            return 0;
        }
        long remainingMillis = endsAt - System.currentTimeMillis();
        return remainingMillis <= 0L ? 0 : (int)Math.ceil((double)remainingMillis / 1000.0);
    }

    private int getCooldownSignature() {
        int signature = 1;
        for (CommanderSkillManager.SkillView skill : this.skills) {
            signature = 31 * signature + this.getRemainingCooldownSeconds(skill.id());
        }
        return signature;
    }

    private void refreshSkillRows() {
        for (CommanderSkillManager.SkillView skill : this.skills) {
            SkillRow row = this.skillRows.get(skill.id());
            if (row == null) continue;
            int cooldownSec = this.getRemainingCooldownSeconds(skill.id());
            boolean onCooldown = cooldownSec > 0;
            row.panel().setColor(onCooldown ? 0x55363636 : 0x60404040).setBorderColor(onCooldown ? -10788256 : -2140310398);
            row.button().setLabel((String)(onCooldown ? cooldownSec + "\u79d2" : "\u53d1\u52a8")).setEnabled(!onCooldown).setTextColor(onCooldown ? -5327681 : -9054838);
        }
        this.lastCooldownSignature = this.getCooldownSignature();
    }

    private void activateSkill(String skillId) {
        NetworkManager.sendCommanderSkillActivate(skillId);
        Minecraft.m_91087_().m_91152_(null);
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        if (this.root == null) {
            return;
        }
        int cooldownSignature = this.getCooldownSignature();
        if (cooldownSignature != this.lastCooldownSignature) {
            this.refreshSkillRows();
        }
    }

    @Override
    public boolean m_6913_() {
        return true;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private record SkillRow(EspetroAuiWidgets.Panel panel, EspetroAuiWidgets.ActionButton button) {
    }
}

