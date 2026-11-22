package org.espetro.client.gui;

import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.HcrTacticalMapBridge;
import org.espetro.client.ClientEquipZones;
import org.espetro.network.ClientRequests;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.Fireteam;
import org.espetro.team.GamePhase;
import org.lwjgl.glfw.GLFW;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 统一部署界面使用的通用控件与绘制工具：按钮、纯文本、分类图标、分隔线、
 * 指挥官技能按钮，以及按比例缩放的文字绘制。从 {@link UnifiedDeployScreen} 拆出。
 */
public final class DeployWidgets {

    private DeployWidgets() {
    }

    // ==================== 自定义按钮 ====================

    /** 职业分类图标：黄色渲染（用于每排左侧）。 */
    static class CategoryIcon extends GuiElement {
        private final ResourceLocation texture;

        CategoryIcon(ResourceLocation texture, int x, int y, int size) {
            super(x, y, size, size);
            this.texture = texture;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!isVisible() || texture == null) {
                return;
            }
            int size = getWidth();
            graphics.setColor(1.0f, 0.85f, 0.25f, 1.0f);      // 黄色
            graphics.blit(texture, x + getX(), y + getY(), size, size, 0.0f, 0.0f, 128, 128, 128, 128);
            graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
    }

    /** 一条细横线（用于职业排之间）：默认 1px，可指定更粗的线（需求 7）。 */
    static class Divider extends GuiElement {
        final int color;
        private final int thickness;

        Divider(int x, int y, int width, int color) {
            this(x, y, width, color, 1);
        }

        Divider(int x, int y, int width, int color, int thickness) {
            super(x, y, Math.max(1, width), Math.max(1, thickness));
            this.color = color;
            this.thickness = Math.max(1, thickness);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!isVisible()) {
                return;
            }
            graphics.fill(x + getX(), y + getY(),
                x + getX() + getWidth(), y + getY() + thickness, color);
        }
    }

    static class EspButton extends GuiElement {
        final Runnable action;
        String label;
        boolean enabled = true;
        boolean hovered = false;
        int normalColor = UnifiedDeployScreen.BTN_BG_PLAIN_BLACK;
        int hoverColor = UnifiedDeployScreen.BTN_BG_HOVER;
        private int disabledColor = UnifiedDeployScreen.BTN_BG_DISABLED;
        private int disabledBorderColor = 0x60383848;
        private int disabledTextColor = 0x666666;
        private int textColor = UnifiedDeployScreen.BTN_TEXT;
        ResourceLocation icon;
        private int iconTextureWidth = 128;
        private int iconTextureHeight = 128;
        private int iconSize = 10;
        private String rightLabel = "";
        private float textScale = UnifiedDeployScreen.UI_TEXT_SCALE;
        private boolean centeredText = true;
        /** 非 0 表示已选中，画成该底色。 */
        private int selectedColor = 0;
        /** 禁用态仍可点击以弹出原因说明（如选职拒绝）。 */
        private Runnable disabledAction;

        EspButton(int x, int y, int w, int h, String label, Runnable action) {
            super(x, y, w, h);
            this.label = label;
            this.action = action;
        }

        void setEnabled(boolean e) { enabled = e; }
        void setLabel(String l) {
            String next = l == null ? "" : l;
            if (java.util.Objects.equals(label, next)) {
                return;
            }
            label = next;
        }
        boolean isEnabled() { return enabled; }
        void setDisabledAction(Runnable r) { disabledAction = r; }
        void setIcon(ResourceLocation icon, int textureWidth, int textureHeight) {
            this.icon = icon;
            this.iconTextureWidth = textureWidth;
            this.iconTextureHeight = textureHeight;
        }
        void setIconSize(int size) { iconSize = Math.max(1, size); }
        void setRightLabel(String value) { rightLabel = value == null ? "" : value; }
        void setTextScale(float scale) { textScale = Math.max(0.5f, Math.min(1.0f, scale)); }
        void setCenteredText(boolean centered) { centeredText = centered; }

        /**
         * 需求 6：黑色半透明平底按钮 —— 原来"有边框、没底色"的按钮统一改用它。
         * 边框已全局不再绘制，这里只负责铺上统一的黑半透底。
         */
        /** 完全透明：常态不铺底色、无边框，仅悬停高亮 / 选中黄底。 */
        void setTransparentIdle() {
            normalColor = 0x00000000;
            hoverColor = 0x66FFFFFF;
        }

        void setPlainBlack() { normalColor = UnifiedDeployScreen.BTN_BG_PLAIN_BLACK; }

        void setSelected(boolean selected, int color) { selectedColor = selected ? color : 0; }
        /** 禁用态样式；border 参数在需求 6（全局去边框）后不再绘制，保留以兼容现有调用点。 */
        void setDisabledStyle(int background, int border, int text) {
            disabledColor = background;
            disabledBorderColor = border;
            disabledTextColor = text;
        }

        @Override
        public boolean onMouseClick(int mx, int my, int button) {
            if (button != 0 || !isVisible() || !hasFocus()) return false;
            if (!enabled) {
                if (disabledAction != null) {
                    disabledAction.run();
                    return true;
                }
                return false;
            }
            if (action != null) action.run();
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!isVisible()) return;
            hovered = hasFocus();

            int bx = x + getX(), by = y + getY(), bw = getWidth(), bh = getHeight();

            // 选中 > 禁用 > 悬停 > 常态；需求 6：所有按钮都不再绘制边框（描边）
            boolean selected = selectedColor != 0;
            if (selected) {
                graphics.fill(bx, by, bx + bw, by + bh, selectedColor);
            } else if (!enabled) {
                if ((disabledColor >>> 24) != 0) {
                    graphics.fill(bx, by, bx + bw, by + bh, disabledColor);
                }
            } else if (hovered) {
                // 悬停：先铺常态底色再叠高亮，避免半透明白把黑半透底顶掉导致文字发虚
                if ((normalColor >>> 24) != 0) {
                    graphics.fill(bx, by, bx + bw, by + bh, normalColor);
                }
                graphics.fill(bx, by, bx + bw, by + bh, hoverColor);
            } else {
                graphics.fill(bx, by, bx + bw, by + bh, normalColor);
            }

            int textCol = enabled ? textColor : disabledTextColor;
            int contentLeft = bx + 3;
            if (icon != null) {
                int drawnIconSize = Math.min(iconSize, bh - 3);
                // 需求 3-4：禁用态不再铺底色，改为把图标压暗，仍能区分可用 / 不可用
                if (!enabled) {
                    graphics.setColor(0.45f, 0.45f, 0.45f, 1.0f);
                }
                graphics.blit(icon, bx + 2, by + (bh - drawnIconSize) / 2,
                    drawnIconSize, drawnIconSize, 0.0f, 0.0f,
                    iconTextureWidth, iconTextureHeight, iconTextureWidth, iconTextureHeight);
                if (!enabled) {
                    graphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
                contentLeft = bx + drawnIconSize + 4;
            }
            int rightLabelWidth = rightLabel.isEmpty() ? 0 : Math.round(
                Minecraft.getInstance().font.width(
                    EspetroAuiWidgets.stripFormatting(rightLabel)) * textScale);
            int rightLabelX = bx + bw - 3 - rightLabelWidth;
            int availableTextWidth = Math.max(8,
                rightLabelX - (rightLabel.isEmpty() ? 0 : 4) - contentLeft);
            int logicalTextWidth = Math.max(8, (int) (availableTextWidth / textScale));
            String drawnLabel = EspetroAuiWidgets.trimToWidth(label, logicalTextWidth);
            int textWidth = Math.round(Minecraft.getInstance().font.width(
                EspetroAuiWidgets.stripFormatting(drawnLabel)) * textScale);
            int textX = centeredText
                ? contentLeft + Math.max(0, (availableTextWidth - textWidth) / 2)
                : contentLeft;
            int textHeight = Math.max(1, Math.round(Minecraft.getInstance().font.lineHeight * textScale));
            drawScaledString(graphics, drawnLabel, textX,
                by + Math.max(0, (bh - textHeight) / 2), textCol, textScale);
            if (!rightLabel.isEmpty()) {
                drawScaledString(graphics, rightLabel, rightLabelX,
                    by + Math.max(0, (bh - textHeight) / 2), textCol, textScale);
            }
        }
    }

    static void drawScaledString(GuiGraphics graphics, String text, int x, int y,
                                         int color, float scale) {
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 1.0f);
        graphics.drawString(Minecraft.getInstance().font, Component.literal(text),
            Math.round(x / scale), Math.round(y / scale), color, false);
        graphics.pose().popPose();
    }

    // ==================== 自定义文本元素（绕过 GuiText，直接用 drawString) ====================

    /** 直接用 Minecraft Font 渲染的文本元素，亮度和按钮文字一致 */
    static class PlainText extends GuiElement {
        private String text;
        int color;

        PlainText(int x, int y, String text, int color) {
            super(x, y,
                Math.max(1, Math.round(Minecraft.getInstance().font.width(
                    EspetroAuiWidgets.stripFormatting(text)) * UnifiedDeployScreen.UI_TEXT_SCALE)),
                Math.max(1, Math.round(Minecraft.getInstance().font.lineHeight * UnifiedDeployScreen.UI_TEXT_SCALE)));
            this.text = text;
            this.color = color;
        }

        void setColor(int c) { color = c; }
        void setText(String value) {
            String next = value == null ? "" : value;
            if (java.util.Objects.equals(text, next)) {
                return;
            }
            text = next;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!isVisible()) return;
            drawScaledString(graphics, text, x + getX(), y + getY(), color, UnifiedDeployScreen.UI_TEXT_SCALE);
        }
    }

    /** 技能按钮：自绘（半透明灰底 + 白边 + 居中图标/名称/冷却）。 */
    static final class SkillButton extends GuiElement {
        private static final int ICON_SIZE = 16;
        private final String skillId;
        private final String skillName;
        private final ResourceLocation skillIcon;
        private int cooldownSeconds;
        /** 防抖：同一次按住/拖动/连点只算一次，避免误触连发火炮。 */
        private long lastActivateMs;

        SkillButton(int x, int y, int width, int height, CommanderSkillManager.SkillView skill) {
            super(x, y, width, height);
            this.skillId = skill.id();
            this.skillName = skill.displayName();
            this.skillIcon = TacticalRadialController.skillIcon(skill);
            refresh();
        }

        /** 每秒调用：取全局锚点推算出的剩余秒数（重建菜单也不会重新计时）。 */
        void refresh() {
            cooldownSeconds = isDebugNoCooldown()
                ? 0 : TacticalRadialController.skillCooldownSeconds(skillId);
        }

        /** 部署阶段所有技能不可用（服务端同样会拒绝）。 */
        private static boolean isDeployPhase() {
            return ClientGameState.getCurrentPhase() == GamePhase.DEPLOYING;
        }

        /** 调试模式（/gamerule espetroSkillNoCooldown true）：忽略冷却。 */
        private static boolean isDebugNoCooldown() {
            net.minecraft.client.multiplayer.ClientLevel level = Minecraft.getInstance().level;
            return level != null && org.espetro.debug.EspetroDebugRules
                .skillNoCooldown(level.getGameRules());
        }

        private boolean isUsable() {
            return (cooldownSeconds <= 0 || isDebugNoCooldown())
                && !CommanderSkillTargetRequest.isPending(skillId)
                && !isDeployPhase();
        }

        @Override
        public boolean onMouseClick(int mouseX, int mouseY, int button) {
            // 关键：GuiElement 不会替叶子元素做范围判定（它只转发给子元素），
            // 所以必须自己判断命中，否则这个按钮会吞掉整个界面的左键点击。
            if (mouseX < getX() || mouseX >= getX() + getWidth()
                || mouseY < getY() || mouseY >= getY() + getHeight()) {
                return false;
            }
            if (button != 0 || !isUsable()) {
                return false;
            }
            long now = System.currentTimeMillis();
            if (now - lastActivateMs < 600L) {
                return true;   // 600ms 内的重复触发直接吞掉
            }
            lastActivateMs = now;
            // 不再关闭 J 菜单：需要选点的技能由服务端回包进入火炮选点模式，
            // 指挥官直接在这张地图上操作即可。
            CommanderSkillTargetRequest.arm(skillId);
            ClientRequests.sendCommanderSkillActivate(skillId);
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            if (!isVisible()) {
                return;
            }
            int px = x + getX();
            int py = y + getY();
            int w = getWidth();
            int h = getHeight();
            boolean deployLocked = isDeployPhase();
            boolean usable = isUsable();
            boolean pending = CommanderSkillTargetRequest.isPending(skillId);
            boolean hover = usable && mouseX >= px && mouseX < px + w
                && mouseY >= py && mouseY < py + h;

            // 待用（已点击、尚未使用）整体变黄；冷却中压暗；可用为半透明灰底
            // 需求 6：同属按钮且原本就有底色 → 只去掉四周描边，底色保留
            int fill;
            if (pending) {
                fill = hover ? 0xCC6E5A12 : 0xBB5C4A0E;
            } else if (usable) {
                fill = hover ? 0xAA3C4247 : 0x99202427;
            } else {
                fill = 0x881A1D1F;
            }
            graphics.fill(px, py, px + w, py + h, fill);

            var font = Minecraft.getInstance().font;
            int centerX = px + w / 2;

            // ① 技能图标（居中）
            if (skillIcon != null) {
                float iconAlpha = usable ? 1f : 0.45f;
                graphics.setColor(1f, 1f, 1f, iconAlpha);
                graphics.blit(skillIcon, centerX - ICON_SIZE / 2, py + 4, 0, 0,
                    ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
                graphics.setColor(1f, 1f, 1f, 1f);
            }

            // ② 技能名称（居中；列窄时自动缩小以完整显示）
            int nameColor = pending ? 0xFFFFE066 : usable ? 0xFFFFFFFF : 0xFF8B9099;
            int nameWidth = font.width(skillName);
            float nameScale = nameWidth + 6 <= w ? 1.0f : Math.max(0.5f, (w - 6f) / nameWidth);
            graphics.pose().pushPose();
            graphics.pose().translate(centerX, py + 22, 0.0D);
            graphics.pose().scale(nameScale, nameScale, 1.0f);
            graphics.drawCenteredString(font, skillName, 0, 0, nameColor);
            graphics.pose().popPose();

            // ③ 冷却 / 可用（居中，字体更小）
            String cooldownText = deployLocked ? "不可用"
                : pending ? "选点中"
                : usable ? "可用" : DeploySnapshots.formatCooldown(cooldownSeconds);
            int cooldownColor = deployLocked ? 0xFF8B9099
                : usable ? 0xFF7FD98F : 0xFFB9BEC6;
            graphics.pose().pushPose();
            graphics.pose().translate(centerX, py + 37, 0.0D);
            graphics.pose().scale(0.75f, 0.75f, 1.0f);
            graphics.drawCenteredString(font, cooldownText, 0, 0, cooldownColor);
            graphics.pose().popPose();

            super.draw(graphics, x, y, width, height, mouseX, mouseY, partialTick);
        }

        /** 冷却文案统一走外层 UnifiedDeployScreen#formatCooldown（m:ss）。 */
        static String formatCooldown(int seconds) {
            return DeploySnapshots.formatCooldown(seconds);
        }
    }

    static void resetGuiRenderState(GuiGraphics graphics) {
        graphics.flush();
        graphics.setColor(1f, 1f, 1f, 1f);
        com.mojang.blaze3d.systems.RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        com.mojang.blaze3d.systems.RenderSystem.enableBlend();
        com.mojang.blaze3d.systems.RenderSystem.defaultBlendFunc();
        com.mojang.blaze3d.systems.RenderSystem.disableDepthTest();
        com.mojang.blaze3d.systems.RenderSystem.depthMask(true);
        com.mojang.blaze3d.systems.RenderSystem.disableCull();
    }
}
