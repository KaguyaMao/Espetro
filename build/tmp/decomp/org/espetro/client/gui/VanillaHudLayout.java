/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RenderGuiOverlayEvent$Pre
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions
 *  net.minecraftforge.client.extensions.common.IClientItemExtensions$FontContext
 *  net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 */
package org.espetro.client.gui;

import net.minecraft.ChatFormatting;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import org.espetro.client.gui.HudRenderState;
import org.espetro.client.gui.VanillaHudNameLayout;

public final class VanillaHudLayout {
    private static final ResourceLocation WIDGETS_LOCATION = ResourceLocation.withDefaultNamespace((String)"textures/gui/widgets.png");
    private static final ResourceLocation GUI_ICONS_LOCATION = ResourceLocation.withDefaultNamespace((String)"textures/gui/icons.png");
    private static final float HOTBAR_SCALE = 0.82f;
    private static final int HOTBAR_RIGHT_MARGIN = 0;
    private static final int SLOT_SIZE = 22;
    private static final int SLOT_STEP = 20;
    private static final int HOTBAR_SLOTS = 9;
    private static final int OFFHAND_GAP = 6;
    private static final int HOTBAR_REVEAL_TICKS = 40;
    private static final int HOTBAR_DURABILITY_PERCENTAGE = 10;
    private static final int HOTBAR_DURABILITY_TOTAL = 20;
    private static final double HOTBAR_ANIMATION_SPEED = 2.0;
    private static final double HOTBAR_HIDE_DISTANCE = 26.0;
    private static final int HEALTH_LEFT = 12;
    private static final int HEALTH_BOTTOM = 16;
    private static final int HEALTH_HEIGHT = 8;
    private static final int HEALTH_MIN_WIDTH = 96;
    private static final int HEALTH_MAX_WIDTH = 170;
    private static boolean hotbarStateReady;
    private static int previousSelectedSlot;
    private static ItemStack previousMainHand;
    private static int hotbarVisibleTicks;
    private static double hotbarOffset;
    private static double hotbarOffsetDelta;
    private static double hotbarAlpha;
    private static double hotbarAlphaDelta;
    private static int itemNameTimer;
    private static ItemStack itemNameStack;

    private VanillaHudLayout() {
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91073_ == null) {
            VanillaHudLayout.resetHotbarState();
            return;
        }
        if (!hotbarStateReady) {
            hotbarStateReady = true;
            previousSelectedSlot = player.m_150109_().f_35977_;
            previousMainHand = player.m_21205_().m_41777_();
        }
        ItemStack mainHand = player.m_21205_();
        if (player.m_150109_().f_35977_ != previousSelectedSlot || !ItemStack.m_41728_(mainHand, previousMainHand)) {
            VanillaHudLayout.revealHotbar();
            previousSelectedSlot = player.m_150109_().f_35977_;
            previousMainHand = mainHand.m_41777_();
        }
        if (VanillaHudLayout.shouldRevealHotbarForLowDurability(mainHand) || VanillaHudLayout.shouldRevealHotbarForLowDurability(player.m_21206_())) {
            VanillaHudLayout.revealHotbar();
        }
        VanillaHudLayout.tickItemName(mc, mainHand);
        VanillaHudLayout.tickHotbarAnimation();
    }

    private static void tickItemName(Minecraft mc, ItemStack selected) {
        if (selected.m_41619_()) {
            itemNameTimer = 0;
        } else if (!itemNameStack.m_41619_() && selected.m_41720_() == itemNameStack.m_41720_() && selected.m_41786_().equals(itemNameStack.m_41786_()) && selected.getHighlightTip(selected.m_41786_()).equals(itemNameStack.getHighlightTip(itemNameStack.m_41786_()))) {
            if (itemNameTimer > 0) {
                --itemNameTimer;
            }
        } else {
            itemNameTimer = (int)(40.0 * mc.f_91066_.m_264038_().m_231551_());
        }
        itemNameStack = selected.m_41777_();
    }

    public static void onRenderOverlayPre(RenderGuiOverlayEvent.Pre event) {
        ResourceLocation overlayId = event.getOverlay().id();
        Minecraft mc = Minecraft.m_91087_();
        if (VanillaGuiOverlay.HOTBAR.id().equals(overlayId)) {
            if (VanillaHudLayout.renderRightHotbar(event.getGuiGraphics(), mc, event.getPartialTick(), event.getWindow().m_85445_(), event.getWindow().m_85446_())) {
                event.setCanceled(true);
            }
            return;
        }
        if (VanillaGuiOverlay.ITEM_NAME.id().equals(overlayId)) {
            if (!mc.f_91066_.f_92062_ && mc.f_91072_ != null && mc.f_91072_.m_105295_() != GameType.SPECTATOR) {
                event.setCanceled(true);
            }
            return;
        }
        if (VanillaGuiOverlay.PLAYER_HEALTH.id().equals(overlayId)) {
            if (VanillaHudLayout.shouldReplaceSurvivalBars(mc)) {
                event.setCanceled(true);
                VanillaHudLayout.renderHealthLine(event.getGuiGraphics(), mc, event.getWindow().m_85445_(), event.getWindow().m_85446_());
            }
            return;
        }
        if (VanillaGuiOverlay.ARMOR_LEVEL.id().equals(overlayId)) {
            event.setCanceled(true);
            return;
        }
        if (VanillaGuiOverlay.FOOD_LEVEL.id().equals(overlayId)) {
            if (VanillaHudLayout.shouldReplaceSurvivalBars(mc)) {
                event.setCanceled(true);
            }
            return;
        }
        if (VanillaGuiOverlay.EXPERIENCE_BAR.id().equals(overlayId)) {
            event.setCanceled(true);
        }
    }

    private static void revealHotbar() {
        hotbarVisibleTicks = 40;
    }

    private static void resetHotbarState() {
        hotbarStateReady = false;
        previousSelectedSlot = -1;
        previousMainHand = ItemStack.f_41583_;
        hotbarVisibleTicks = 0;
        hotbarOffset = 1.0;
        hotbarOffsetDelta = 0.0;
        hotbarAlpha = 0.0;
        hotbarAlphaDelta = 0.0;
        itemNameTimer = 0;
        itemNameStack = ItemStack.f_41583_;
    }

    private static boolean shouldRevealHotbarForLowDurability(ItemStack stack) {
        if (!stack.m_41763_()) {
            return false;
        }
        int maxDamage = stack.m_41776_();
        int damage = stack.m_41773_();
        int remaining = maxDamage - damage;
        return (double)damage >= 0.9 * (double)maxDamage && remaining < 20;
    }

    private static void tickHotbarAnimation() {
        if (hotbarVisibleTicks == 0) {
            if (!VanillaHudLayout.isHotbarFullyHidden()) {
                VanillaHudLayout.moveHotbarOut();
            }
            if (hotbarOffset == 1.0) {
                hotbarOffsetDelta = 0.0;
            }
            if (hotbarAlpha == 0.0) {
                hotbarAlphaDelta = 0.0;
            }
        } else if (!VanillaHudLayout.isHotbarFullyRevealed()) {
            VanillaHudLayout.moveHotbarIn();
        } else {
            if (hotbarOffset == 0.0) {
                hotbarOffsetDelta = 0.0;
            }
            if (hotbarAlpha == 1.0) {
                hotbarAlphaDelta = 0.0;
            }
        }
        if (hotbarVisibleTicks > 0) {
            --hotbarVisibleTicks;
        }
    }

    private static boolean isHotbarFullyHidden() {
        return hotbarOffset == 1.0 && hotbarAlpha == 0.0;
    }

    private static boolean isHotbarFullyRevealed() {
        return hotbarOffset == 0.0 && hotbarAlpha == 1.0;
    }

    private static void moveHotbarIn() {
        hotbarOffset = Math.max(0.0, hotbarOffset + hotbarOffsetDelta);
        hotbarAlpha = Math.min(1.0, hotbarAlpha + hotbarAlphaDelta);
        double offsetSpeed = Math.sqrt(0.01 + hotbarOffset) * 0.1 * 2.0;
        double alphaSpeed = 0.1;
        hotbarOffsetDelta = hotbarOffset - offsetSpeed <= 0.0 ? -hotbarOffset : -offsetSpeed;
        hotbarAlphaDelta = hotbarAlpha + alphaSpeed >= 1.0 ? 1.0 - hotbarAlpha : alphaSpeed;
    }

    private static void moveHotbarOut() {
        hotbarOffset = Math.min(1.0, hotbarOffset + hotbarOffsetDelta);
        hotbarAlpha = Math.max(0.0, hotbarAlpha + hotbarAlphaDelta);
        double offsetSpeed = Math.sqrt(0.01 + hotbarOffset) * 0.1 * 2.0;
        double alphaSpeed = 0.1;
        hotbarOffsetDelta = hotbarOffset + offsetSpeed >= 1.0 ? 1.0 - hotbarOffset : offsetSpeed;
        hotbarAlphaDelta = hotbarAlpha - alphaSpeed <= 0.0 ? -hotbarAlpha : -alphaSpeed;
    }

    private static float getHotbarAlpha(float partialTick) {
        return Mth.m_14036_((float)(hotbarAlpha + (double)partialTick * hotbarAlphaDelta), 0.0f, 1.0f);
    }

    private static float getHotbarOffset(float partialTick) {
        return Mth.m_14036_((float)(hotbarOffset + (double)partialTick * hotbarOffsetDelta), 0.0f, 1.0f);
    }

    private static boolean renderRightHotbar(GuiGraphics graphics, Minecraft mc, float partialTick, int screenWidth, int screenHeight) {
        return VanillaHudLayout.drawRightHotbar(graphics, mc, partialTick, screenWidth, screenHeight);
    }

    private static boolean drawRightHotbar(GuiGraphics graphics, Minecraft mc, float partialTick, int screenWidth, int screenHeight) {
        LocalPlayer player = mc.f_91074_;
        if (player == null || mc.f_91072_ == null || mc.f_91066_.f_92062_) {
            return false;
        }
        if (mc.f_91072_.m_105295_() == GameType.SPECTATOR) {
            return false;
        }
        float alpha = VanillaHudLayout.getHotbarAlpha(partialTick);
        if (alpha <= 0.0f && VanillaHudLayout.getHotbarOffset(partialTick) >= 1.0f) {
            return true;
        }
        ItemStack offhand = player.m_21206_();
        int totalHeight = 182;
        if (!offhand.m_41619_()) {
            totalHeight += 28;
        }
        float localScreenWidth = (float)screenWidth / 0.82f;
        float localScreenHeight = (float)screenHeight / 0.82f;
        int baseX = Mth.m_14143_(localScreenWidth - 0.0f - 22.0f);
        int baseY = Mth.m_14143_(Math.max(9.756098f, (localScreenHeight - (float)totalHeight) / 2.0f));
        HudRenderState.begin(graphics);
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(0.82f, 0.82f, 1.0f);
        graphics.m_280168_().m_85837_((double)VanillaHudLayout.getHotbarOffset(partialTick) * 26.0, 0.0, 0.0);
        graphics.m_280246_(1.0f, 1.0f, 1.0f, alpha);
        for (int slot = 0; slot < 9; ++slot) {
            int slotY = baseY + slot * 20;
            graphics.m_280218_(WIDGETS_LOCATION, baseX, slotY, 0, 0, 22, 22);
        }
        int selectedY = baseY + player.m_150109_().f_35977_ * 20;
        graphics.m_280218_(WIDGETS_LOCATION, baseX - 1, selectedY - 1, 0, 22, 24, 22);
        int seed = 1;
        for (int slot = 0; slot < 9; ++slot) {
            int slotY = baseY + slot * 20;
            VanillaHudLayout.renderSlot(graphics, mc, player, player.m_150109_().f_35974_.get(slot), baseX + 3, slotY + 3, partialTick, seed++);
        }
        if (!offhand.m_41619_()) {
            int offhandY = baseY + 22 + 160 + 6;
            graphics.m_280218_(WIDGETS_LOCATION, baseX, offhandY, 0, 0, 22, 22);
            VanillaHudLayout.renderSlot(graphics, mc, player, offhand, baseX + 3, offhandY + 3, partialTick, seed);
        }
        VanillaHudLayout.renderAttackIndicator(graphics, mc, player, baseX - 22, selectedY + 2);
        VanillaHudLayout.renderSelectedItemNameAtSlot(graphics, mc, baseX, selectedY, alpha);
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        graphics.m_280168_().m_85849_();
        HudRenderState.restore(graphics);
        return true;
    }

    private static void renderSlot(GuiGraphics graphics, Minecraft mc, Player player, ItemStack stack, int x, int y, float partialTick, int seed) {
        if (stack.m_41619_()) {
            return;
        }
        float popTime = (float)stack.m_41612_() - partialTick;
        if (popTime > 0.0f) {
            float scale = 1.0f + popTime / 5.0f;
            graphics.m_280168_().m_85836_();
            graphics.m_280168_().m_252880_((float)x + 8.0f, (float)y + 12.0f, 0.0f);
            graphics.m_280168_().m_85841_(1.0f / scale, (scale + 1.0f) / 2.0f, 1.0f);
            graphics.m_280168_().m_252880_(-((float)x + 8.0f), -((float)y + 12.0f), 0.0f);
        }
        graphics.m_280638_(player, stack, x, y, seed);
        if (popTime > 0.0f) {
            graphics.m_280168_().m_85849_();
        }
        graphics.m_280370_(mc.f_91062_, stack, x, y);
    }

    private static void renderAttackIndicator(GuiGraphics graphics, Minecraft mc, LocalPlayer player, int x, int y) {
        if (mc.f_91066_.m_232120_().m_231551_() != AttackIndicatorStatus.HOTBAR) {
            return;
        }
        float attackStrength = player.m_36403_(0.0f);
        if (attackStrength >= 1.0f) {
            return;
        }
        int filled = (int)(attackStrength * 19.0f);
        graphics.m_280218_(GUI_ICONS_LOCATION, x, y, 0, 94, 18, 18);
        graphics.m_280218_(GUI_ICONS_LOCATION, x, y + 18 - filled, 18, 112 - filled, 18, filled);
    }

    private static void renderSelectedItemNameAtSlot(GuiGraphics graphics, Minecraft mc, int slotX, int slotY, float hotbarAlpha) {
        if (itemNameTimer <= 0 || itemNameStack.m_41619_()) {
            return;
        }
        MutableComponent styled = Component.m_237119_().m_7220_(itemNameStack.m_41786_()).m_130938_(itemNameStack.m_41791_().getStyleModifier());
        if (itemNameStack.m_41788_()) {
            styled.m_130940_(ChatFormatting.ITALIC);
        }
        Component tip = itemNameStack.getHighlightTip(styled);
        int fade = VanillaHudNameLayout.nameFade(itemNameTimer, hotbarAlpha);
        if (fade <= 0) {
            return;
        }
        Font font = IClientItemExtensions.of((ItemStack)itemNameStack).getFont(itemNameStack, IClientItemExtensions.FontContext.SELECTED_ITEM_NAME);
        if (font == null) {
            font = mc.f_91062_;
        }
        int textWidth = font.m_92852_(tip);
        int textX = VanillaHudNameLayout.nameX(slotX, textWidth);
        int textY = VanillaHudNameLayout.nameY(slotY);
        graphics.m_280509_(textX - 2, textY - 2, textX + textWidth + 2, textY + 9 + 2, VanillaHudNameLayout.nameBackgroundColor(fade));
        graphics.m_280614_(font, tip, textX, textY, 0xFFFFFF | fade << 24, true);
    }

    private static void renderHealthLine(GuiGraphics graphics, Minecraft mc, int screenWidth, int screenHeight) {
        VanillaHudLayout.drawHealthLine(graphics, mc, screenWidth, screenHeight);
    }

    private static void drawHealthLine(GuiGraphics graphics, Minecraft mc, int screenWidth, int screenHeight) {
        Entity entity;
        if (!VanillaHudLayout.shouldReplaceSurvivalBars(mc) || !((entity = mc.m_91288_()) instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        float maxHealth = Math.max(1.0f, player.m_21233_());
        float health = Mth.m_14036_(player.m_21223_(), 0.0f, maxHealth);
        if (health >= maxHealth) {
            return;
        }
        int barWidth = Mth.m_14045_(screenWidth / 5, 96, 170);
        int x = 12;
        int healthY = screenHeight - 16 - 8;
        int healthFilled = Math.round((float)barWidth * (health / maxHealth));
        if (health > 0.0f) {
            healthFilled = Math.max(1, healthFilled);
        }
        if (healthFilled > 0) {
            graphics.m_280509_(x, healthY, x + healthFilled, healthY + 8, -1887180);
            graphics.m_280509_(x, healthY, x + healthFilled, healthY + 2, -38037);
        }
        HudRenderState.restore(graphics);
    }

    private static boolean shouldReplaceSurvivalBars(Minecraft mc) {
        return !mc.f_91066_.f_92062_ && mc.f_91072_ != null && mc.f_91072_.m_105205_() && mc.m_91288_() instanceof Player;
    }

    static {
        previousSelectedSlot = -1;
        previousMainHand = ItemStack.f_41583_;
        hotbarOffset = 1.0;
        itemNameStack = ItemStack.f_41583_;
    }
}

