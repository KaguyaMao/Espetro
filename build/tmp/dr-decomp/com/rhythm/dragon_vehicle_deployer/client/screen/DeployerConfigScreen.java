/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.rhythm.dragon_vehicle_deployer.client.screen;

import com.rhythm.dragon_vehicle_deployer.menu.DeployerConfigMenu;
import com.rhythm.dragon_vehicle_deployer.network.DeployerSettingsPacket;
import com.rhythm.dragon_vehicle_deployer.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class DeployerConfigScreen
extends AbstractContainerScreen<DeployerConfigMenu> {
    private int localInterval = -1;
    private int localAutoSpawn = -1;
    private int localIdleTimeout = -1;

    public DeployerConfigScreen(DeployerConfigMenu menu, Inventory inv, Component title) {
        super((AbstractContainerMenu)menu, inv, title);
        this.f_97726_ = 250;
        this.f_97727_ = 200;
    }

    private int getDisplayInterval() {
        return this.localInterval >= 0 ? this.localInterval : ((DeployerConfigMenu)this.f_97732_).getSpawnIntervalSeconds();
    }

    private boolean getDisplayAutoSpawn() {
        return this.localAutoSpawn >= 0 ? this.localAutoSpawn == 1 : ((DeployerConfigMenu)this.f_97732_).isAutoSpawnEnabled();
    }

    private int getDisplayIdleTimeout() {
        return this.localIdleTimeout >= 0 ? this.localIdleTimeout : ((DeployerConfigMenu)this.f_97732_).getIdleClearTimeoutSeconds();
    }

    protected void m_7856_() {
        super.m_7856_();
        int cx = this.f_97735_ + this.f_97726_ / 2;
        int btnW = 32;
        int btnH = 20;
        int gap = 6;
        int row1Y = this.f_97736_ + 35;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"-60"), b -> {
            this.localInterval = Math.max(5, this.getDisplayInterval() - 60);
        }).m_252987_(cx - 80 - btnW, row1Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"-5"), b -> {
            this.localInterval = Math.max(5, this.getDisplayInterval() - 5);
        }).m_252987_(cx - 80 + btnW + gap, row1Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+5"), b -> {
            this.localInterval = Math.min(3600, this.getDisplayInterval() + 5);
        }).m_252987_(cx + 80 - btnW - btnW - gap, row1Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+60"), b -> {
            this.localInterval = Math.min(3600, this.getDisplayInterval() + 60);
        }).m_252987_(cx + 80, row1Y, btnW, btnH).m_253136_());
        int row2Y = row1Y + 40;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"gui.dragonrise_reforge.toggle"), b -> {
            this.localAutoSpawn = this.getDisplayAutoSpawn() ? 0 : 1;
        }).m_252987_(cx - 40, row2Y, 80, btnH).m_253136_());
        int row3Y = row2Y + 40;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"-60"), b -> {
            this.localIdleTimeout = Math.max(0, this.getDisplayIdleTimeout() - 60);
        }).m_252987_(cx - 80 - btnW, row3Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"-5"), b -> {
            this.localIdleTimeout = Math.max(0, this.getDisplayIdleTimeout() - 5);
        }).m_252987_(cx - 80 + btnW + gap, row3Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+5"), b -> {
            this.localIdleTimeout = Math.min(36000, this.getDisplayIdleTimeout() + 5);
        }).m_252987_(cx + 80 - btnW - btnW - gap, row3Y, btnW, btnH).m_253136_());
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)"+60"), b -> {
            this.localIdleTimeout = Math.min(36000, this.getDisplayIdleTimeout() + 60);
        }).m_252987_(cx + 80, row3Y, btnW, btnH).m_253136_());
        int row4Y = row3Y + 30;
        this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"gui.dragonrise_reforge.confirm"), b -> {
            ModNetwork.CHANNEL.sendToServer((Object)new DeployerSettingsPacket(((DeployerConfigMenu)this.f_97732_).getPos(), this.getDisplayInterval(), this.getDisplayAutoSpawn(), this.getDisplayIdleTimeout()));
            this.m_7379_();
        }).m_252987_(cx - 50, row4Y, 100, btnH).m_253136_());
    }

    protected void m_7286_(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.m_280509_(this.f_97735_, this.f_97736_, this.f_97735_ + this.f_97726_, this.f_97736_ + this.f_97727_, -870178270);
        graphics.m_280509_(this.f_97735_, this.f_97736_, this.f_97735_ + this.f_97726_, this.f_97736_ + 1, -7829368);
        graphics.m_280509_(this.f_97735_, this.f_97736_ + this.f_97727_ - 1, this.f_97735_ + this.f_97726_, this.f_97736_ + this.f_97727_, -7829368);
        graphics.m_280509_(this.f_97735_, this.f_97736_, this.f_97735_ + 1, this.f_97736_ + this.f_97727_, -7829368);
        graphics.m_280509_(this.f_97735_ + this.f_97726_ - 1, this.f_97736_, this.f_97735_ + this.f_97726_, this.f_97736_ + this.f_97727_, -7829368);
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
        int cx = this.f_97735_ + this.f_97726_ / 2;
        graphics.m_280653_(this.f_96547_, (Component)Component.m_237115_((String)"container.dragonrise_reforge.deployer_config"), cx, this.f_97736_ + 8, 0xFFFFFF);
        int row1LabelY = this.f_97736_ + 25;
        int row1Y = this.f_97736_ + 35;
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"gui.dragonrise_reforge.spawn_interval"), this.f_97735_ + 8, row1LabelY, 0xAAAAAA, false);
        graphics.m_280137_(this.f_96547_, this.getDisplayInterval() + "s", cx, row1Y + 6, 0x55FF55);
        int row2LabelY = row1Y + 30;
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"gui.dragonrise_reforge.auto_spawn"), this.f_97735_ + 8, row2LabelY, 0xAAAAAA, false);
        boolean autoOn = this.getDisplayAutoSpawn();
        String statusText = autoOn ? Component.m_237115_((String)"gui.dragonrise_reforge.enabled").getString() : Component.m_237115_((String)"gui.dragonrise_reforge.disabled").getString();
        int statusColor = autoOn ? 0x55FF55 : 0xFF5555;
        int statusX = this.f_97735_ + 8 + this.f_96547_.m_92852_((FormattedText)Component.m_237115_((String)"gui.dragonrise_reforge.auto_spawn")) + 6;
        graphics.m_280056_(this.f_96547_, statusText, statusX, row2LabelY, statusColor, false);
        int row3LabelY = row2LabelY + 40;
        int row3BtnY = row3LabelY + 10;
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"gui.dragonrise_reforge.idle_clear_timeout"), this.f_97735_ + 8, row3LabelY, 0xAAAAAA, false);
        int idleVal = this.getDisplayIdleTimeout();
        if (idleVal == 0) {
            graphics.m_280137_(this.f_96547_, Component.m_237115_((String)"gui.dragonrise_reforge.disabled").getString(), cx, row3BtnY + 6, 0xFF5555);
        } else {
            graphics.m_280137_(this.f_96547_, idleVal + "s", cx, row3BtnY + 6, 0x55FF55);
        }
        this.m_280072_(graphics, mouseX, mouseY);
    }

    protected void m_280003_(GuiGraphics graphics, int mouseX, int mouseY) {
    }
}

