package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.espetro.network.NetworkManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.client.aui.GuiElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommanderSkillScreen extends EspetroMenuScreen {

    private Map<String, Integer> cooldowns = new HashMap<>();
    private final Map<String, Long> cooldownEndsAtMillis = new HashMap<>();
    private List<CommanderSkillManager.SkillView> skills = new ArrayList<>();
    private boolean isCommander;
    private int lastCooldownSignature = Integer.MIN_VALUE;
    private final Map<String, SkillRow> skillRows = new HashMap<>();

    public CommanderSkillScreen(boolean isCommander, Map<String, Integer> cooldowns) {
        this(isCommander, cooldowns, List.of());
    }

    public CommanderSkillScreen(boolean isCommander, Map<String, Integer> cooldowns,
                                List<CommanderSkillManager.SkillView> skills) {
        super(Component.literal("指挥官技能"));
        this.isCommander = isCommander;
        setSkills(skills);
        setCooldowns(cooldowns);
    }

    public static void open(boolean isCommander, Map<String, Integer> cooldowns) {
        open(isCommander, cooldowns, List.of());
    }

    public static void open(boolean isCommander, Map<String, Integer> cooldowns,
                            List<CommanderSkillManager.SkillView> skills) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new CommanderSkillScreen(isCommander, cooldowns, skills));
    }

    public void updateData(boolean isCommander, Map<String, Integer> cooldowns) {
        updateData(isCommander, cooldowns, List.of());
    }

    public void updateData(boolean isCommander, Map<String, Integer> cooldowns,
                           List<CommanderSkillManager.SkillView> skills) {
        List<CommanderSkillManager.SkillView> nextSkills = skills != null
            ? new ArrayList<>(skills) : new ArrayList<>();
        boolean layoutChanged = this.isCommander != isCommander || !this.skills.equals(nextSkills);
        this.isCommander = isCommander;
        setSkills(nextSkills);
        setCooldowns(cooldowns);
        if (root != null) {
            if (layoutChanged) {
                rebuildMenuRoot();
            } else {
                refreshSkillRows();
            }
        }
    }

    private void setSkills(List<CommanderSkillManager.SkillView> skills) {
        this.skills = skills != null ? new ArrayList<>(skills) : new ArrayList<>();
    }

    private void setCooldowns(Map<String, Integer> cooldowns) {
        this.cooldowns = cooldowns != null ? new HashMap<>(cooldowns) : new HashMap<>();
        this.cooldownEndsAtMillis.clear();

        long now = System.currentTimeMillis();
        for (Map.Entry<String, Integer> entry : this.cooldowns.entrySet()) {
            int seconds = entry.getValue() != null ? entry.getValue() : 0;
            if (seconds > 0) {
                this.cooldownEndsAtMillis.put(entry.getKey(), now + seconds * 1000L);
            }
        }
        this.lastCooldownSignature = Integer.MIN_VALUE;
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        skillRows.clear();
        int panelW = Math.max(300, Math.min(420, this.width - 20));
        int panelH = Math.max(180, Math.min(320, this.height - 24));
        int panelX = (this.width - panelW) / 2;
        int panelY = Math.max(8, (this.height - panelH) / 2);

        root.addChild(EspetroAuiWidgets.card(panelX, panelY, panelW, panelH));

        EspetroAuiWidgets.Text title = EspetroAuiWidgets.text(panelX + 10, panelY + 6,
            "战术技能", UiTheme.GOLD);
        title.setTextScale(UiTheme.TYPE_TITLE).setShadow(true);
        root.addChild(title);

        root.addChild(EspetroAuiWidgets.button(panelX + panelW - 50, panelY + 7, 40, 14,
            "关闭", () -> Minecraft.getInstance().setScreen(null)));

        root.addChild(EspetroAuiWidgets.divider(panelX + 10, panelY + 25, panelW - 20));

        // isCommander 在同步包中表示「有技能入口」；列表已由服务端按 usableBy 过滤
        if (skills.isEmpty()) {
            root.addChild(EspetroAuiWidgets.centeredText(panelX, panelY + 50, panelW,
                "当前角色无可用技能", UiTheme.TEXT_THIRD));
            lastCooldownSignature = getCooldownSignature();
            return;
        }

        int contentY = panelY + 34;
        int contentX = panelX + 10;
        int contentW = panelW - 20;

        for (CommanderSkillManager.SkillView skill : skills) {
            contentY = buildSkillCard(root, contentX, contentY, contentW, skill);
            contentY += 8;
        }

        lastCooldownSignature = getCooldownSignature();
    }

    private int buildSkillCard(GuiElement root, int x, int y, int width,
                               CommanderSkillManager.SkillView skill) {
        int cardH = 62;
        int cooldownSec = getRemainingCooldownSeconds(skill.id());
        boolean onCooldown = cooldownSec > 0;

        EspetroAuiWidgets.Panel panel = EspetroAuiWidgets.cardAccent(
            x, y, width, cardH, onCooldown ? UiTheme.TEXT_THIRD : UiTheme.POSITIVE);
        root.addChild(panel);

        EspetroAuiWidgets.Text name = EspetroAuiWidgets.text(x + 8, y + 6,
            skill.displayName(), UiTheme.GOLD);
        name.setShadow(true);
        root.addChild(name);

        root.addChild(EspetroAuiWidgets.text(x + 8, y + 20,
            skill.description(), UiTheme.TEXT_SECOND));

        String stats = skill.stats() == null ? "" : skill.stats();
        root.addChild(EspetroAuiWidgets.text(x + 8, y + 34,
            stats, UiTheme.TEXT_THIRD));

        int btnW = 56;
        int btnH = 16;
        int btnX = x + width - btnW - 8;
        int btnY = y + (cardH - btnH) / 2;

        EspetroAuiWidgets.ActionButton button = EspetroAuiWidgets.button(
            btnX, btnY, btnW, btnH,
            onCooldown ? cooldownSec + "秒" : "发动",
            () -> activateSkill(skill.id()))
            .setEnabled(!onCooldown)
            .setTextColor(onCooldown ? UiTheme.TEXT_DISABLED : UiTheme.POSITIVE);
        root.addChild(button);
        skillRows.put(skill.id(), new SkillRow(panel, button));

        return y + cardH;
    }

    private int getRemainingCooldownSeconds(String skillId) {
        Long endsAt = cooldownEndsAtMillis.get(skillId);
        if (endsAt == null) {
            return 0;
        }

        long remainingMillis = endsAt - System.currentTimeMillis();
        return remainingMillis <= 0 ? 0 : (int) Math.ceil(remainingMillis / 1000.0);
    }

    private int getCooldownSignature() {
        int signature = 1;
        for (CommanderSkillManager.SkillView skill : skills) {
            signature = 31 * signature + getRemainingCooldownSeconds(skill.id());
        }
        return signature;
    }

    private void refreshSkillRows() {
        for (CommanderSkillManager.SkillView skill : skills) {
            SkillRow row = skillRows.get(skill.id());
            if (row == null) {
                continue;
            }
            int cooldownSec = getRemainingCooldownSeconds(skill.id());
            boolean onCooldown = cooldownSec > 0;
            row.panel().setAccentColor(onCooldown ? UiTheme.TEXT_THIRD : UiTheme.POSITIVE);
            row.button()
                .setLabel(onCooldown ? cooldownSec + "秒" : "发动")
                .setEnabled(!onCooldown)
                .setTextColor(onCooldown ? UiTheme.TEXT_DISABLED : UiTheme.POSITIVE);
        }
        lastCooldownSignature = getCooldownSignature();
    }

    private record SkillRow(EspetroAuiWidgets.Panel panel,
                            EspetroAuiWidgets.ActionButton button) {
    }

    private void activateSkill(String skillId) {
        CommanderSkillTargetRequest.arm();
        NetworkManager.sendCommanderSkillActivate(skillId);
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void tick() {
        super.tick();
        if (root == null) {
            return;
        }

        int cooldownSignature = getCooldownSignature();
        if (cooldownSignature != lastCooldownSignature) {
            refreshSkillRows();
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
