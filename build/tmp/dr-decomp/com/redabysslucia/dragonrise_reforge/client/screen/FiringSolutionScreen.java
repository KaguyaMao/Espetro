/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Position
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.Vec3
 */
package com.redabysslucia.dragonrise_reforge.client.screen;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.redabysslucia.dragonrise_reforge.entities.vehicle.IndirectFireVehicleBase;
import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlComputation;
import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlSolution;
import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlStatus;
import com.redabysslucia.dragonrise_reforge.firecontrol.IndirectFireBallistics;
import com.redabysslucia.dragonrise_reforge.firecontrol.TrajectoryMode;
import com.redabysslucia.dragonrise_reforge.integration.EsWeatherFireControlBridge;
import com.redabysslucia.dragonrise_reforge.network.ModNetwork;
import com.redabysslucia.dragonrise_reforge.network.message.SetFireControlMessage;
import com.redabysslucia.dragonrise_reforge.network.message.ToggleTakeoverMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FiringSolutionScreen
extends Screen {
    private final IndirectFireVehicleBase vehicle;
    private final Player player;
    private EditBox targetXField;
    private EditBox targetYField;
    private EditBox targetZField;
    private Button applyButton;
    private Button clearButton;
    private Button closeButton;
    private Button takeoverToggle;
    private FireControlComputation preview;
    private TrajectoryMode trajectoryMode = TrajectoryMode.LOW;
    private boolean weatherJammed;
    private boolean wasWeatherJammed;
    private static final int PANEL_WIDTH = 210;
    private static final int PANEL_HEIGHT = 200;
    private static final double DEFAULT_TARGET_RANGE = 120.0;
    private static final int RANGE_TABLE_ROWS = 6;

    public FiringSolutionScreen(VehicleEntity vehicle, Player player) {
        super((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.title"));
        this.vehicle = (IndirectFireVehicleBase)vehicle;
        this.player = player;
    }

    protected void m_7856_() {
        super.m_7856_();
        int panelX = (this.f_96543_ - 210) / 2;
        int panelY = (this.f_96544_ - 200) / 2;
        Font font = this.f_96541_.f_91062_;
        int labelWidth = 8;
        int fieldWidth = 50;
        int fieldY = panelY + 40;
        int fieldHeight = 12;
        int xCol1 = panelX + 10 + labelWidth;
        this.targetXField = new EditBox(font, xCol1, fieldY, fieldWidth, fieldHeight, (Component)Component.m_237113_((String)"X"));
        this.targetYField = new EditBox(font, xCol1 + 60, fieldY, fieldWidth, fieldHeight, (Component)Component.m_237113_((String)"Y"));
        this.targetZField = new EditBox(font, xCol1 + 120, fieldY, fieldWidth, fieldHeight, (Component)Component.m_237113_((String)"Z"));
        BlockPos defaultTarget = this.defaultTargetPos();
        this.targetXField.m_94144_(String.valueOf(defaultTarget.m_123341_()));
        this.targetYField.m_94144_(String.valueOf(defaultTarget.m_123342_()));
        this.targetZField.m_94144_(String.valueOf(defaultTarget.m_123343_()));
        this.targetXField.m_94153_(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        this.targetYField.m_94153_(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        this.targetZField.m_94153_(s -> s.isEmpty() || s.equals("-") || s.matches("-?\\d{0,7}"));
        this.m_142416_((GuiEventListener)this.targetXField);
        this.m_142416_((GuiEventListener)this.targetYField);
        this.m_142416_((GuiEventListener)this.targetZField);
        int buttonY = panelY + 200 - 48;
        this.applyButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.apply"), button -> this.applySolution()).m_252987_(panelX + 10, buttonY, 55, 14).m_253136_());
        this.clearButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.clear"), button -> this.clearSolution()).m_252987_(panelX + 70, buttonY, 55, 14).m_253136_());
        this.closeButton = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.close"), button -> this.m_7379_()).m_252987_(panelX + 130, buttonY, 55, 14).m_253136_());
        int takeoverY = panelY + 200 - 28;
        this.takeoverToggle = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237113_((String)""), button -> this.toggleTakeover()).m_252987_(panelX + 10, takeoverY, 190, 14).m_253136_());
        this.updateTakeoverLabel();
        this.updatePreview();
        this.refreshWeatherJamState();
    }

    private BlockPos defaultTargetPos() {
        BlockPos saved;
        if (this.vehicle.isFireControlActive() && !(saved = this.vehicle.getFireControlTarget()).equals((Object)BlockPos.f_121853_)) {
            return saved;
        }
        Vec3 origin = this.vehicle.m_20182_();
        int seat = this.vehicle.getTurretControllerIndex();
        if (seat >= 0) {
            try {
                Vec3 muzzle = this.vehicle.getShootPos(seat, 1.0f);
                if (muzzle != null) {
                    origin = new Vec3(muzzle.f_82479_, this.vehicle.m_20186_(), muzzle.f_82481_);
                }
            }
            catch (Exception muzzle) {
                // empty catch block
            }
        }
        Vec3 forward = this.vehicle.m_20156_();
        double horiz = Math.sqrt(forward.f_82479_ * forward.f_82479_ + forward.f_82481_ * forward.f_82481_);
        if (horiz < 1.0E-4) {
            float yRot = this.vehicle.m_146908_() * ((float)Math.PI / 180);
            forward = new Vec3(-Math.sin(yRot), 0.0, Math.cos(yRot));
            horiz = 1.0;
        }
        double nx = forward.f_82479_ / horiz;
        double nz = forward.f_82481_ / horiz;
        double tx = origin.f_82479_ + nx * 120.0;
        double tz = origin.f_82481_ + nz * 120.0;
        return BlockPos.m_274561_((double)tx, (double)this.vehicle.m_20186_(), (double)tz);
    }

    private void applySolution() {
        if (this.weatherJammed || EsWeatherFireControlBridge.isDisrupted((Entity)this.vehicle)) {
            this.player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.fire_control.weather_jammed").m_130940_(ChatFormatting.RED), true);
            return;
        }
        if (!this.vehicle.isMainCannonSelected()) {
            this.player.m_5661_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.weapon_warning").m_130940_(ChatFormatting.RED), true);
            return;
        }
        if (this.preview == null || !this.preview.isSuccess()) {
            return;
        }
        try {
            int x = Integer.parseInt(this.targetXField.m_94155_());
            int y = Integer.parseInt(this.targetYField.m_94155_());
            int z = Integer.parseInt(this.targetZField.m_94155_());
            BlockPos target = new BlockPos(x, y, z);
            ModNetwork.PACKET_HANDLER.sendToServer((Object)SetFireControlMessage.apply(this.vehicle.m_19879_(), target, 0, this.trajectoryMode, true));
        }
        catch (NumberFormatException ignored) {
            this.player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.fire_control.invalid_input").m_130940_(ChatFormatting.RED), true);
        }
    }

    private void clearSolution() {
        ModNetwork.PACKET_HANDLER.sendToServer((Object)SetFireControlMessage.clear(this.vehicle.m_19879_()));
    }

    private void toggleTakeover() {
        if (!this.vehicle.isFireControlActive()) {
            this.player.m_5661_((Component)Component.m_237115_((String)"message.dragonrise_reforge.fire_control.takeover_requires_active").m_130940_(ChatFormatting.YELLOW), true);
            return;
        }
        boolean newState = !this.vehicle.isFireControlTakeoverEnabled();
        ModNetwork.PACKET_HANDLER.sendToServer((Object)new ToggleTakeoverMessage(this.vehicle.m_19879_(), newState));
        this.updateTakeoverLabel();
    }

    private void updateTakeoverLabel() {
        if (this.takeoverToggle == null) {
            return;
        }
        boolean enabled = this.vehicle.isFireControlActive() && this.vehicle.isFireControlTakeoverEnabled();
        this.takeoverToggle.m_93666_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.takeover").m_130946_(": ").m_7220_((Component)Component.m_237115_((String)(enabled ? "screen.dragonrise_reforge.fire_control.takeover.on" : "screen.dragonrise_reforge.fire_control.takeover.off")).m_130940_(enabled ? ChatFormatting.GREEN : ChatFormatting.GRAY)));
    }

    private void updatePreview() {
        try {
            int x = Integer.parseInt(this.targetXField.m_94155_());
            int y = Integer.parseInt(this.targetYField.m_94155_());
            int z = Integer.parseInt(this.targetZField.m_94155_());
            BlockPos target = new BlockPos(x, y, z);
            this.preview = IndirectFireBallistics.solve((VehicleEntity)this.vehicle, this.vehicle.getTurretControllerIndex(), target, this.trajectoryMode);
        }
        catch (NumberFormatException e) {
            this.preview = FireControlComputation.failure(FireControlStatus.INVALID_INPUT);
        }
        if (this.applyButton != null) {
            boolean mainCannonReady = this.vehicle.isMainCannonSelected();
            this.applyButton.f_93623_ = !this.weatherJammed && this.preview != null && this.preview.isSuccess() && mainCannonReady;
        }
    }

    private void refreshWeatherJamState() {
        boolean inputsVisible;
        this.weatherJammed = EsWeatherFireControlBridge.isDisrupted((Entity)this.vehicle);
        if (this.targetXField == null) {
            return;
        }
        this.targetXField.f_93624_ = inputsVisible = !this.weatherJammed;
        this.targetYField.f_93624_ = inputsVisible;
        this.targetZField.f_93624_ = inputsVisible;
        if (this.applyButton != null) {
            this.applyButton.f_93624_ = inputsVisible;
            boolean bl = this.applyButton.f_93623_ = inputsVisible && this.preview != null && this.preview.isSuccess() && this.vehicle.isMainCannonSelected();
        }
        if (this.clearButton != null) {
            this.clearButton.f_93624_ = inputsVisible;
            this.clearButton.f_93623_ = inputsVisible;
        }
        if (this.takeoverToggle != null) {
            this.takeoverToggle.f_93624_ = inputsVisible;
            this.takeoverToggle.f_93623_ = inputsVisible;
        }
        if (this.weatherJammed) {
            if (this.vehicle.m_9236_().m_46467_() % 4L == 0L) {
                this.targetXField.m_94144_(FiringSolutionScreen.garble(6));
                this.targetYField.m_94144_(FiringSolutionScreen.garble(5));
                this.targetZField.m_94144_(FiringSolutionScreen.garble(6));
                if (this.applyButton != null) {
                    this.applyButton.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(4)));
                }
                if (this.clearButton != null) {
                    this.clearButton.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(4)));
                }
                if (this.takeoverToggle != null) {
                    this.takeoverToggle.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(10)));
                }
            }
        } else if (this.wasWeatherJammed) {
            if (this.applyButton != null) {
                this.applyButton.m_93666_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.apply"));
            }
            if (this.clearButton != null) {
                this.clearButton.m_93666_((Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.clear"));
            }
            this.updateTakeoverLabel();
        }
        this.wasWeatherJammed = this.weatherJammed;
    }

    private static String garble(int length) {
        RandomSource random = RandomSource.m_216335_((long)System.nanoTime());
        String alphabet = "#$%&@?!*<>/\\|+=~^";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            builder.append("#$%&@?!*<>/\\|+=~^".charAt(random.m_188503_("#$%&@?!*<>/\\|+=~^".length())));
        }
        return builder.toString();
    }

    public void m_86600_() {
        super.m_86600_();
        if (this.player.m_20202_() != this.vehicle) {
            this.m_7379_();
            return;
        }
        this.refreshWeatherJamState();
        if (!this.weatherJammed) {
            this.updatePreview();
            this.updateTakeoverLabel();
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        int panelX = (this.f_96543_ - 210) / 2;
        int panelY = (this.f_96544_ - 200) / 2;
        graphics.m_280509_(panelX, panelY, panelX + 210, panelY + 200, -872415232);
        int border = this.weatherJammed ? -43691 : -14592;
        graphics.m_280509_(panelX, panelY, panelX + 210, panelY + 1, border);
        graphics.m_280509_(panelX, panelY + 200 - 1, panelX + 210, panelY + 200, border);
        graphics.m_280509_(panelX, panelY, panelX + 1, panelY + 200, border);
        graphics.m_280509_(panelX + 210 - 1, panelY, panelX + 210, panelY + 200, border);
        Font font = this.f_96541_.f_91062_;
        int textX = panelX + 10;
        if (this.weatherJammed) {
            graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.title_jammed"), textX, panelY + 8, -43691, false);
            graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.jammed_hint"), textX, panelY + 24, -21931, false);
            this.renderJammedRangeTable(graphics, font, textX, panelY + 42);
            super.m_88315_(graphics, mouseX, mouseY, partialTick);
            return;
        }
        graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.title"), textX, panelY + 8, -14592, false);
        int labelY = panelY + 40;
        graphics.m_280056_(font, "X:", textX, labelY + 2, -1, false);
        graphics.m_280056_(font, "Y:", textX + 60, labelY + 2, -1, false);
        graphics.m_280056_(font, "Z:", textX + 120, labelY + 2, -1, false);
        int resultY = panelY + 70;
        if (this.preview != null && this.preview.isSuccess()) {
            FireControlSolution sol = this.preview.solution();
            BlockPos impact = BlockPos.m_274446_((Position)sol.target());
            graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.impact", (Object[])new Object[]{impact.m_123341_(), impact.m_123342_(), impact.m_123343_()}), textX, resultY, -16711834, false);
            graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.range", (Object[])new Object[]{String.format("%.1f", sol.range())}), textX, resultY + 12, -16711834, false);
            graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.pitch", (Object[])new Object[]{String.format("%.2f", sol.pitch())}), textX, resultY + 24, -16711834, false);
            graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.yaw", (Object[])new Object[]{String.format("%.2f", sol.yaw())}), textX, resultY + 36, -16711834, false);
            graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.flight_time", (Object[])new Object[]{String.format("%.1f", sol.flightTime())}), textX, resultY + 48, -16711834, false);
        } else if (this.preview != null) {
            MutableComponent statusText = Component.m_237115_((String)this.preview.status().translationKey()).m_130940_(ChatFormatting.RED);
            graphics.m_280614_(font, (Component)statusText, textX, resultY, -43691, false);
            if (this.preview.status() == FireControlStatus.PITCH_LIMIT && this.preview.hasRequestedPitch()) {
                graphics.m_280614_(font, (Component)Component.m_237110_((String)"screen.dragonrise_reforge.fire_control.pitch_detail", (Object[])new Object[]{String.format("%.1f", this.preview.requestedPitch()), String.format("%.0f", Float.valueOf(this.vehicle.getTurretMinPitch())), String.format("%.0f", Float.valueOf(this.vehicle.getTurretMaxPitch()))}), textX, resultY + 12, -21931, false);
            }
        }
        int statusY = panelY + 200 - 70;
        FireControlStatus status = this.vehicle.getFireControlStatus();
        ChatFormatting statusColor = status == FireControlStatus.READY ? ChatFormatting.GREEN : (status == FireControlStatus.INACTIVE ? ChatFormatting.GRAY : ChatFormatting.YELLOW);
        graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.status").m_130946_(": ").m_7220_((Component)Component.m_237115_((String)status.translationKey()).m_130940_(statusColor)), textX, statusY, -1, false);
        if (!this.vehicle.isMainCannonSelected()) {
            graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.weapon_warning").m_130940_(ChatFormatting.RED), textX, statusY + 12, -43691, false);
        }
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
    }

    private void renderJammedRangeTable(GuiGraphics graphics, Font font, int textX, int startY) {
        graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.table.elevation"), textX, startY, -14592, false);
        graphics.m_280614_(font, (Component)Component.m_237115_((String)"screen.dragonrise_reforge.fire_control.table.range"), textX + 70, startY, -14592, false);
        int seat = this.vehicle.getTurretControllerIndex();
        double elevMin = Math.max(1.0, (double)this.vehicle.getTurretMinPitch());
        double elevMax = Math.max(elevMin, (double)this.vehicle.getTurretMaxPitch());
        double velocity = seat >= 0 ? (double)this.vehicle.getProjectileVelocity(seat) : 0.0;
        double gravity = seat >= 0 ? (double)this.vehicle.getProjectileGravity(seat) : 0.0;
        double vehicleY = this.vehicle.m_20186_();
        int rows = 6;
        for (int i = 0; i < rows; ++i) {
            double elev = rows <= 1 ? elevMax : elevMin + (elevMax - elevMin) * (double)i / ((double)rows - 1.0);
            double range = IndirectFireBallistics.rangeAtPitch(velocity, gravity, vehicleY, vehicleY, elev);
            int rowY = startY + 14 + i * 12;
            graphics.m_280056_(font, String.format("%.0f\u00b0", elev), textX, rowY, -1, false);
            graphics.m_280056_(font, String.format("%.0f m", range), textX + 70, rowY, -16711834, false);
        }
    }

    public boolean m_7043_() {
        return false;
    }
}

