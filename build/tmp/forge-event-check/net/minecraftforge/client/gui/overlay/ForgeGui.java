/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.DebugScreenOverlay
 *  net.minecraft.client.multiplayer.ClientPacketListener
 *  net.minecraft.client.renderer.GameRenderer
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.resources.language.I18n
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringUtil
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.PlayerRideableJumping
 *  net.minecraft.world.entity.ai.attributes.AttributeInstance
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.food.FoodData
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.scores.Objective
 *  net.minecraftforge.eventbus.api.Event
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.client.gui.overlay;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.scores.Objective;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeGui
extends Gui {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int WHITE = 0xFFFFFF;
    public static double rayTraceDistance = 20.0;
    public int leftHeight = 39;
    public int rightHeight = 39;
    private Font font = null;
    private final ForgeDebugScreenOverlay debugOverlay;

    public ForgeGui(Minecraft mc) {
        super(mc, mc.m_91291_());
        this.debugOverlay = new ForgeDebugScreenOverlay(mc);
    }

    public Minecraft getMinecraft() {
        return this.f_92986_;
    }

    public void setupOverlayRenderState(boolean blend, boolean depthTest) {
        if (blend) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
        } else {
            RenderSystem.disableBlend();
        }
        if (depthTest) {
            RenderSystem.enableDepthTest();
        } else {
            RenderSystem.disableDepthTest();
        }
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShader(GameRenderer::m_172817_);
    }

    public void m_280421_(GuiGraphics guiGraphics, float partialTick) {
        this.f_92977_ = this.f_92986_.m_91268_().m_85445_();
        this.f_92978_ = this.f_92986_.m_91268_().m_85446_();
        this.rightHeight = 39;
        this.leftHeight = 39;
        if (MinecraftForge.EVENT_BUS.post((Event)new RenderGuiEvent.Pre(this.f_92986_.m_91268_(), guiGraphics, partialTick))) {
            return;
        }
        this.font = this.f_92986_.f_91062_;
        this.f_92985_.m_188584_((long)this.f_92989_ * 312871L);
        GuiOverlayManager.getOverlays().forEach(entry -> {
            try {
                IGuiOverlay overlay = entry.overlay();
                if (this.pre((NamedGuiOverlay)entry, guiGraphics)) {
                    return;
                }
                overlay.render(this, guiGraphics, partialTick, this.f_92977_, this.f_92978_);
                this.post((NamedGuiOverlay)entry, guiGraphics);
            }
            catch (Exception e) {
                LOGGER.error("Error rendering overlay '{}'", (Object)entry.id(), (Object)e);
            }
        });
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        MinecraftForge.EVENT_BUS.post((Event)new RenderGuiEvent.Post(this.f_92986_.m_91268_(), guiGraphics, partialTick));
    }

    public boolean shouldDrawSurvivalElements() {
        return this.f_92986_.f_91072_.m_105205_() && this.f_92986_.m_91288_() instanceof Player;
    }

    protected void renderSubtitles(GuiGraphics guiGraphics) {
        this.f_92996_.m_280227_(guiGraphics);
    }

    protected void renderBossHealth(GuiGraphics guiGraphics) {
        RenderSystem.defaultBlendFunc();
        this.f_92986_.m_91307_().m_6180_("bossHealth");
        this.f_92999_.m_280652_(guiGraphics);
        this.f_92986_.m_91307_().m_7238_();
    }

    void renderSpyglassOverlay(GuiGraphics guiGraphics) {
        float deltaFrame = this.f_92986_.m_91297_();
        this.f_168664_ = Mth.m_14179_((float)(0.5f * deltaFrame), (float)this.f_168664_, (float)1.125f);
        if (this.f_92986_.f_91066_.m_92176_().m_90612_()) {
            if (this.f_92986_.f_91074_.m_150108_()) {
                this.m_280278_(guiGraphics, this.f_168664_);
            } else {
                this.f_168664_ = 0.5f;
            }
        }
    }

    void renderHelmet(float partialTick, GuiGraphics guiGraphics) {
        ItemStack itemstack = this.f_92986_.f_91074_.m_150109_().m_36052_(3);
        if (this.f_92986_.f_91066_.m_92176_().m_90612_() && !itemstack.m_41619_()) {
            Item item = itemstack.m_41720_();
            if (item == Blocks.f_50143_.m_5456_()) {
                this.m_280155_(guiGraphics, f_92983_, 1.0f);
            } else {
                IClientItemExtensions.of(item).renderHelmetOverlay(itemstack, (Player)this.f_92986_.f_91074_, this.f_92977_, this.f_92978_, partialTick);
            }
        }
    }

    void renderFrostbite(GuiGraphics guiGraphics) {
        if (this.f_92986_.f_91074_.m_146888_() > 0) {
            this.m_280155_(guiGraphics, f_168666_, this.f_92986_.f_91074_.m_146889_());
        }
    }

    protected void renderArmor(GuiGraphics guiGraphics, int width, int height) {
        this.f_92986_.m_91307_().m_6180_("armor");
        RenderSystem.enableBlend();
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        int level = this.f_92986_.f_91074_.m_21230_();
        for (int i = 1; level > 0 && i < 20; i += 2) {
            if (i < level) {
                guiGraphics.m_280218_(f_279580_, left, top, 34, 9, 9, 9);
            } else if (i == level) {
                guiGraphics.m_280218_(f_279580_, left, top, 25, 9, 9, 9);
            } else if (i > level) {
                guiGraphics.m_280218_(f_279580_, left, top, 16, 9, 9, 9);
            }
            left += 8;
        }
        this.leftHeight += 10;
        RenderSystem.disableBlend();
        this.f_92986_.m_91307_().m_7238_();
    }

    protected void m_280379_(GuiGraphics guiGraphics, float alpha) {
        if (alpha > 0.0f) {
            super.m_280379_(guiGraphics, alpha);
        }
    }

    protected void renderAir(int width, int height, GuiGraphics guiGraphics) {
        this.f_92986_.m_91307_().m_6180_("air");
        Player player = (Player)this.f_92986_.m_91288_();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - this.rightHeight;
        int air = player.m_20146_();
        if (player.isEyeInFluidType(ForgeMod.WATER_TYPE.get()) || air < 300) {
            int full = Mth.m_14165_((double)((double)(air - 2) * 10.0 / 300.0));
            int partial = Mth.m_14165_((double)((double)air * 10.0 / 300.0)) - full;
            for (int i = 0; i < full + partial; ++i) {
                guiGraphics.m_280218_(f_279580_, left - i * 8 - 9, top, i < full ? 16 : 25, 18, 9, 9);
            }
            this.rightHeight += 10;
        }
        RenderSystem.disableBlend();
        this.f_92986_.m_91307_().m_7238_();
    }

    public void renderHealth(int width, int height, GuiGraphics guiGraphics) {
        boolean highlight;
        this.f_92986_.m_91307_().m_6180_("health");
        RenderSystem.enableBlend();
        Player player = (Player)this.f_92986_.m_91288_();
        int health = Mth.m_14167_((float)player.m_21223_());
        boolean bl = highlight = this.f_92976_ > (long)this.f_92989_ && (this.f_92976_ - (long)this.f_92989_) / 3L % 2L == 1L;
        if (health < this.f_92973_ && player.f_19802_ > 0) {
            this.f_92975_ = Util.m_137550_();
            this.f_92976_ = this.f_92989_ + 20;
        } else if (health > this.f_92973_ && player.f_19802_ > 0) {
            this.f_92975_ = Util.m_137550_();
            this.f_92976_ = this.f_92989_ + 10;
        }
        if (Util.m_137550_() - this.f_92975_ > 1000L) {
            this.f_92973_ = health;
            this.f_92974_ = health;
            this.f_92975_ = Util.m_137550_();
        }
        this.f_92973_ = health;
        int healthLast = this.f_92974_;
        AttributeInstance attrMaxHealth = player.m_21051_(Attributes.f_22276_);
        float healthMax = Math.max((float)attrMaxHealth.m_22135_(), (float)Math.max(healthLast, health));
        int absorb = Mth.m_14167_((float)player.m_6103_());
        int healthRows = Mth.m_14167_((float)((healthMax + (float)absorb) / 2.0f / 10.0f));
        int rowHeight = Math.max(10 - (healthRows - 2), 3);
        this.f_92985_.m_188584_((long)(this.f_92989_ * 312871));
        int left = width / 2 - 91;
        int top = height - this.leftHeight;
        this.leftHeight += healthRows * rowHeight;
        if (rowHeight != 10) {
            this.leftHeight += 10 - rowHeight;
        }
        int regen = -1;
        if (player.m_21023_(MobEffects.f_19605_)) {
            regen = this.f_92989_ % Mth.m_14167_((float)(healthMax + 5.0f));
        }
        this.m_168688_(guiGraphics, player, left, top, rowHeight, regen, healthMax, health, healthLast, absorb, highlight);
        RenderSystem.disableBlend();
        this.f_92986_.m_91307_().m_7238_();
    }

    public void renderFood(int width, int height, GuiGraphics guiGraphics) {
        this.f_92986_.m_91307_().m_6180_("food");
        Player player = (Player)this.f_92986_.m_91288_();
        RenderSystem.enableBlend();
        int left = width / 2 + 91;
        int top = height - this.rightHeight;
        this.rightHeight += 10;
        boolean unused = false;
        FoodData stats = this.f_92986_.f_91074_.m_36324_();
        int level = stats.m_38702_();
        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;
            int icon = 16;
            int background = 0;
            if (this.f_92986_.f_91074_.m_21023_(MobEffects.f_19612_)) {
                icon += 36;
                background = 13;
            }
            if (unused) {
                background = 1;
            }
            if (player.m_36324_().m_38722_() <= 0.0f && this.f_92989_ % (level * 3 + 1) == 0) {
                y = top + (this.f_92985_.m_188503_(3) - 1);
            }
            guiGraphics.m_280218_(f_279580_, x, y, 16 + background * 9, 27, 9, 9);
            if (idx < level) {
                guiGraphics.m_280218_(f_279580_, x, y, icon + 36, 27, 9, 9);
                continue;
            }
            if (idx != level) continue;
            guiGraphics.m_280218_(f_279580_, x, y, icon + 45, 27, 9, 9);
        }
        RenderSystem.disableBlend();
        this.f_92986_.m_91307_().m_7238_();
    }

    protected void renderSleepFade(int width, int height, GuiGraphics guiGraphics) {
        if (this.f_92986_.f_91074_.m_36318_() > 0) {
            this.f_92986_.m_91307_().m_6180_("sleep");
            int sleepTime = this.f_92986_.f_91074_.m_36318_();
            float opacity = (float)sleepTime / 100.0f;
            if (opacity > 1.0f) {
                opacity = 1.0f - (float)(sleepTime - 100) / 10.0f;
            }
            int color = (int)(220.0f * opacity) << 24 | 0x101020;
            guiGraphics.m_285944_(RenderType.m_286086_(), 0, 0, width, height, color);
            this.f_92986_.m_91307_().m_7238_();
        }
    }

    protected void renderExperience(int x, GuiGraphics guiGraphics) {
        guiGraphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        if (this.f_92986_.f_91072_.m_105288_()) {
            super.m_280276_(guiGraphics, x);
        }
        RenderSystem.enableBlend();
        guiGraphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void m_280069_(PlayerRideableJumping playerRideableJumping, GuiGraphics guiGraphics, int x) {
        guiGraphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        super.m_280069_(playerRideableJumping, guiGraphics, x);
        RenderSystem.enableBlend();
        this.f_92986_.m_91307_().m_7238_();
        guiGraphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
    }

    protected void renderHUDText(int width, int height, GuiGraphics guiGraphics) {
        this.f_92986_.m_91307_().m_6180_("forgeHudText");
        RenderSystem.defaultBlendFunc();
        ArrayList<String> listL = new ArrayList<String>();
        ArrayList<String> listR = new ArrayList<String>();
        if (this.f_92986_.m_91402_()) {
            long time = this.f_92986_.f_91073_.m_46467_();
            if (time >= 120500L) {
                listR.add(I18n.m_118938_((String)"demo.demoExpired", (Object[])new Object[0]));
            } else {
                listR.add(I18n.m_118938_((String)"demo.remainingTime", (Object[])new Object[]{StringUtil.m_14404_((int)((int)(120500L - time)))}));
            }
        }
        if (this.f_92986_.f_91066_.f_92063_) {
            this.debugOverlay.update();
            listL.addAll(this.debugOverlay.getLeft());
            listR.addAll(this.debugOverlay.getRight());
        }
        CustomizeGuiOverlayEvent.DebugText event = new CustomizeGuiOverlayEvent.DebugText(this.f_92986_.m_91268_(), guiGraphics, this.f_92986_.m_91296_(), listL, listR);
        MinecraftForge.EVENT_BUS.post((Event)event);
        int top = 2;
        for (String msg : listL) {
            if (msg != null && !msg.isEmpty()) {
                int n = 2 + this.font.m_92895_(msg) + 1;
                Objects.requireNonNull(this.font);
                guiGraphics.m_280509_(1, top - 1, n, top + 9 - 1, -1873784752);
                guiGraphics.m_280056_(this.font, msg, 2, top, 0xE0E0E0, false);
            }
            Objects.requireNonNull(this.font);
            top += 9;
        }
        top = 2;
        for (String msg : listR) {
            if (msg != null && !msg.isEmpty()) {
                int w = this.font.m_92895_(msg);
                int left = width - 2 - w;
                Objects.requireNonNull(this.font);
                guiGraphics.m_280509_(left - 1, top - 1, left + w + 1, top + 9 - 1, -1873784752);
                guiGraphics.m_280056_(this.font, msg, left, top, 0xE0E0E0, false);
            }
            Objects.requireNonNull(this.font);
            top += 9;
        }
        this.f_92986_.m_91307_().m_7238_();
    }

    protected void renderFPSGraph(GuiGraphics guiGraphics) {
        if (this.f_92986_.f_91066_.f_92063_ && this.f_92986_.f_91066_.f_92065_) {
            this.debugOverlay.m_94056_(guiGraphics);
        }
    }

    public void m_93091_() {
        super.m_93091_();
        this.debugOverlay.m_94040_();
    }

    protected void renderRecordOverlay(int width, int height, float partialTick, GuiGraphics guiGraphics) {
        if (this.f_92991_ > 0) {
            this.f_92986_.m_91307_().m_6180_("overlayMessage");
            float hue = (float)this.f_92991_ - partialTick;
            int opacity = (int)(hue * 255.0f / 20.0f);
            if (opacity > 255) {
                opacity = 255;
            }
            if (opacity > 8) {
                int yShift = Math.max(this.leftHeight, this.rightHeight) + 9;
                guiGraphics.m_280168_().m_85836_();
                guiGraphics.m_280168_().m_85837_((double)width / 2.0, (double)(height - Math.max(yShift, 68)), 0.0);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                int color = this.f_92992_ ? Mth.m_14169_((float)(hue / 50.0f), (float)0.7f, (float)0.6f) & 0xFFFFFF : 0xFFFFFF;
                int messageWidth = this.font.m_92852_((FormattedText)this.f_92990_);
                this.m_93039_(guiGraphics, this.font, -4, messageWidth, 0xFFFFFF | opacity << 24);
                guiGraphics.m_280648_(this.font, this.f_92990_.m_7532_(), -messageWidth / 2, -4, color | opacity << 24);
                RenderSystem.disableBlend();
                guiGraphics.m_280168_().m_85849_();
            }
            this.f_92986_.m_91307_().m_7238_();
        }
    }

    protected void renderTitle(int width, int height, float partialTick, GuiGraphics guiGraphics) {
        if (this.f_93001_ != null && this.f_93000_ > 0) {
            this.f_92986_.m_91307_().m_6180_("titleAndSubtitle");
            float age = (float)this.f_93000_ - partialTick;
            int opacity = 255;
            if (this.f_93000_ > this.f_92972_ + this.f_92971_) {
                float f3 = (float)(this.f_92970_ + this.f_92971_ + this.f_92972_) - age;
                opacity = (int)(f3 * 255.0f / (float)this.f_92970_);
            }
            if (this.f_93000_ <= this.f_92972_) {
                opacity = (int)(age * 255.0f / (float)this.f_92972_);
            }
            if ((opacity = Mth.m_14045_((int)opacity, (int)0, (int)255)) > 8) {
                guiGraphics.m_280168_().m_85836_();
                guiGraphics.m_280168_().m_85837_((double)width / 2.0, (double)height / 2.0, 0.0);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                guiGraphics.m_280168_().m_85836_();
                guiGraphics.m_280168_().m_85841_(4.0f, 4.0f, 4.0f);
                int l = opacity << 24 & 0xFF000000;
                guiGraphics.m_280649_(this.font, this.f_93001_.m_7532_(), -this.m_93082_().m_92852_((FormattedText)this.f_93001_) / 2, -10, 0xFFFFFF | l, true);
                guiGraphics.m_280168_().m_85849_();
                if (this.f_93002_ != null) {
                    guiGraphics.m_280168_().m_85836_();
                    guiGraphics.m_280168_().m_85841_(2.0f, 2.0f, 2.0f);
                    guiGraphics.m_280649_(this.font, this.f_93002_.m_7532_(), -this.m_93082_().m_92852_((FormattedText)this.f_93002_) / 2, 5, 0xFFFFFF | l, true);
                    guiGraphics.m_280168_().m_85849_();
                }
                RenderSystem.disableBlend();
                guiGraphics.m_280168_().m_85849_();
            }
            this.f_92986_.m_91307_().m_7238_();
        }
    }

    protected void renderChat(int width, int height, GuiGraphics guiGraphics) {
        this.f_92986_.m_91307_().m_6180_("chat");
        Window window = this.f_92986_.m_91268_();
        CustomizeGuiOverlayEvent.Chat event = new CustomizeGuiOverlayEvent.Chat(window, guiGraphics, this.f_92986_.m_91296_(), 0, height - 40);
        MinecraftForge.EVENT_BUS.post((Event)event);
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_85837_((double)event.getPosX(), (double)(event.getPosY() - height + 40) / this.f_92988_.m_93815_(), 0.0);
        int mouseX = Mth.m_14107_((double)(this.f_92986_.f_91067_.m_91589_() * (double)window.m_85445_() / (double)window.m_85443_()));
        int mouseY = Mth.m_14107_((double)(this.f_92986_.f_91067_.m_91594_() * (double)window.m_85446_() / (double)window.m_85444_()));
        this.f_92988_.m_280165_(guiGraphics, this.f_92989_, mouseX, mouseY);
        guiGraphics.m_280168_().m_85849_();
        this.f_92986_.m_91307_().m_7238_();
    }

    protected void renderPlayerList(int width, int height, GuiGraphics guiGraphics) {
        Objective scoreobjective = this.f_92986_.f_91073_.m_6188_().m_83416_(0);
        ClientPacketListener handler = this.f_92986_.f_91074_.f_108617_;
        if (this.f_92986_.f_91066_.f_92099_.m_90857_() && (!this.f_92986_.m_91090_() || handler.m_105142_().size() > 1 || scoreobjective != null)) {
            this.f_92998_.m_94556_(true);
            this.f_92998_.m_280406_(guiGraphics, width, this.f_92986_.f_91073_.m_6188_(), scoreobjective);
        } else {
            this.f_92998_.m_94556_(false);
        }
    }

    protected void renderHealthMount(int width, int height, GuiGraphics guiGraphics) {
        Player player = (Player)this.f_92986_.m_91288_();
        Entity tmp = player.m_20202_();
        if (!(tmp instanceof LivingEntity)) {
            return;
        }
        boolean unused = false;
        int left_align = width / 2 + 91;
        this.f_92986_.m_91307_().m_6182_("mountHealth");
        RenderSystem.enableBlend();
        LivingEntity mount = (LivingEntity)tmp;
        int health = (int)Math.ceil(mount.m_21223_());
        float healthMax = mount.m_21233_();
        int hearts = (int)(healthMax + 0.5f) / 2;
        if (hearts > 30) {
            hearts = 30;
        }
        int MARGIN = 52;
        int BACKGROUND = 52 + (unused ? 1 : 0);
        int HALF = 97;
        int FULL = 88;
        int heart = 0;
        while (hearts > 0) {
            int top = height - this.rightHeight;
            int rowCount = Math.min(hearts, 10);
            hearts -= rowCount;
            for (int i = 0; i < rowCount; ++i) {
                int x = left_align - i * 8 - 9;
                guiGraphics.m_280218_(f_279580_, x, top, BACKGROUND, 9, 9, 9);
                if (i * 2 + 1 + heart < health) {
                    guiGraphics.m_280218_(f_279580_, x, top, 88, 9, 9, 9);
                    continue;
                }
                if (i * 2 + 1 + heart != health) continue;
                guiGraphics.m_280218_(f_279580_, x, top, 97, 9, 9, 9);
            }
            this.rightHeight += 10;
            heart += 20;
        }
        RenderSystem.disableBlend();
    }

    private boolean pre(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        return MinecraftForge.EVENT_BUS.post((Event)new RenderGuiOverlayEvent.Pre(this.f_92986_.m_91268_(), guiGraphics, this.f_92986_.m_91296_(), overlay));
    }

    private void post(NamedGuiOverlay overlay, GuiGraphics guiGraphics) {
        MinecraftForge.EVENT_BUS.post((Event)new RenderGuiOverlayEvent.Post(this.f_92986_.m_91268_(), guiGraphics, this.f_92986_.m_91296_(), overlay));
    }

    private static class ForgeDebugScreenOverlay
    extends DebugScreenOverlay {
        private final Minecraft mc;

        private ForgeDebugScreenOverlay(Minecraft mc) {
            super(mc);
            this.mc = mc;
        }

        public void update() {
            Entity entity = this.mc.m_91288_();
            this.f_94032_ = entity.m_19907_(rayTraceDistance, 0.0f, false);
            this.f_94033_ = entity.m_19907_(rayTraceDistance, 0.0f, true);
        }

        protected void m_280186_(GuiGraphics guiGraphics) {
            RenderSystem.disableDepthTest();
        }

        protected void m_280532_(GuiGraphics guiGraphics) {
        }

        private List<String> getLeft() {
            List ret = this.m_94075_();
            ret.add("");
            boolean flag = this.mc.m_91092_() != null;
            ret.add("Debug: Pie [shift]: " + (this.mc.f_91066_.f_92064_ ? "visible" : "hidden") + (flag ? " FPS + TPS" : " FPS") + " [alt]: " + (this.mc.f_91066_.f_92065_ ? "visible" : "hidden"));
            ret.add("For help: press F3 + Q");
            return ret;
        }

        private List<String> getRight() {
            return this.m_94078_();
        }
    }
}

