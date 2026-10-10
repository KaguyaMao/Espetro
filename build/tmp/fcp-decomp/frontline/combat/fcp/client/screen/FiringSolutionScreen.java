/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.phys.BlockHitResult
 *  net.minecraft.world.phys.HitResult
 */
package frontline.combat.fcp.client.screen;

import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import frontline.combat.fcp.entity.vehicle.DelayedMortarVehicleBase;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicleBase;
import frontline.combat.fcp.firecontrol.FireControlComputation;
import frontline.combat.fcp.firecontrol.FireControlSolution;
import frontline.combat.fcp.firecontrol.FireControlStatus;
import frontline.combat.fcp.firecontrol.IndirectFireBallistics;
import frontline.combat.fcp.firecontrol.TrajectoryMode;
import frontline.combat.fcp.integration.EsWeatherFireControlBridge;
import frontline.combat.fcp.network.FCPNetwork;
import frontline.combat.fcp.network.message.SetFireControlMessage;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FiringSolutionScreen
extends Screen {
    private static final int PANEL_MAX_WIDTH = 380;
    private static final int PANEL_MAX_HEIGHT = 220;
    private static final int PANEL_MARGIN = 8;
    private static final int BACKGROUND = -234090478;
    private static final int SURFACE = -15262432;
    private static final int SURFACE_ALT = -14669780;
    private static final int BORDER = -11971494;
    private static final int TEXT = -1577746;
    private static final int MUTED = -6773334;
    private static final int WARNING = -14249;
    private static final int ERROR = -39330;
    private static final int READY = -9972847;
    private final IndirectFireVehicleBase vehicle;
    private final Player player;
    private EditBox targetX;
    private EditBox targetY;
    private EditBox targetZ;
    private EditBox radius;
    private Button targetTab;
    private Button rangeTab;
    private Button lowMode;
    private Button highMode;
    private Button apply;
    private Button clear;
    private Button close;
    private TrajectoryMode trajectoryMode;
    private FireControlComputation preview = FireControlComputation.failure(FireControlStatus.INACTIVE);
    private boolean showRangeTable;
    private boolean compact;
    private boolean weatherJammed;
    private boolean wasWeatherJammed;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;

    public FiringSolutionScreen(IndirectFireVehicleBase vehicle, Player player) {
        super((Component)Component.m_237115_((String)"screen.fcp.fire_control.title"));
        this.vehicle = vehicle;
        this.player = player;
        this.trajectoryMode = vehicle.isFireControlActive() ? vehicle.getFireControlTrajectory() : (vehicle instanceof DelayedMortarVehicleBase ? TrajectoryMode.HIGH : TrajectoryMode.LOW);
    }

    protected void m_7856_() {
        this.panelWidth = Math.min(380, this.f_96543_ - 16);
        this.panelHeight = Math.min(220, this.f_96544_ - 16);
        this.panelX = (this.f_96543_ - this.panelWidth) / 2;
        this.panelY = (this.f_96544_ - this.panelHeight) / 2;
        this.compact = this.panelWidth < 350 || this.panelHeight < 200;
        int tabY = this.panelY + 24;
        this.targetTab = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.fcp.fire_control.tab.solution"), button -> this.setRangeTable(false)).m_252987_(this.panelX + 10, tabY, 86, 16).m_253136_());
        this.rangeTab = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.fcp.fire_control.tab.range_table"), button -> this.setRangeTable(true)).m_252987_(this.panelX + 100, tabY, 86, 16).m_253136_());
        if (this.compact) {
            this.createCompactInputs();
        } else {
            this.createWideInputs();
        }
        int actionY = this.panelY + this.panelHeight - 22;
        this.apply = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.fcp.fire_control.apply"), button -> this.applySolution()).m_252987_(this.panelX + 10, actionY, 70, 16).m_253136_());
        this.clear = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.fcp.fire_control.clear"), button -> FCPNetwork.FCP_HANDLER.sendToServer((Object)SetFireControlMessage.clear(this.vehicle.m_19879_()))).m_252987_(this.panelX + 84, actionY, 70, 16).m_253136_());
        this.close = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237115_((String)"screen.fcp.fire_control.close"), button -> this.m_7379_()).m_252987_(this.panelX + this.panelWidth - 80, actionY, 70, 16).m_253136_());
        this.prefillInputs();
        this.updateModeLabels();
        this.updatePreview();
        this.refreshWeatherJamState();
    }

    private void createWideInputs() {
        int fieldX = this.panelX + 68;
        int fieldY = this.panelY + 52;
        this.targetX = this.createCoordinateBox(fieldX, fieldY, 88);
        this.targetY = this.createCoordinateBox(fieldX, fieldY + 22, 88);
        this.targetZ = this.createCoordinateBox(fieldX, fieldY + 44, 88);
        this.radius = this.createRadiusBox(fieldX, fieldY + 66, 44);
        this.lowMode = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), button -> this.setTrajectoryMode(TrajectoryMode.LOW)).m_252987_(this.panelX + 12, this.panelY + 146, 70, 16).m_253136_());
        this.highMode = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), button -> this.setTrajectoryMode(TrajectoryMode.HIGH)).m_252987_(this.panelX + 86, this.panelY + 146, 70, 16).m_253136_());
    }

    private void createCompactInputs() {
        int top = this.panelY + 51;
        int left = this.panelX + 28;
        int right = this.panelX + this.panelWidth / 2 + 12;
        this.targetX = this.createCoordinateBox(left, top, 86);
        this.targetY = this.createCoordinateBox(right, top, 86);
        this.targetZ = this.createCoordinateBox(left, top + 25, 86);
        this.radius = this.createRadiusBox(right, top + 25, 44);
        this.lowMode = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), button -> this.setTrajectoryMode(TrajectoryMode.LOW)).m_252987_(this.panelX + 10, this.panelY + 101, 68, 16).m_253136_());
        this.highMode = (Button)this.m_142416_((GuiEventListener)Button.m_253074_((Component)Component.m_237119_(), button -> this.setTrajectoryMode(TrajectoryMode.HIGH)).m_252987_(this.panelX + 82, this.panelY + 101, 68, 16).m_253136_());
    }

    private EditBox createCoordinateBox(int x, int y, int width) {
        EditBox box = new EditBox(this.f_96547_, x, y, width, 16, (Component)Component.m_237119_());
        box.m_94199_(10);
        box.m_94153_(value -> value.matches("-?\\d*"));
        box.m_94182_(false);
        box.m_94202_(-1577746);
        box.m_94151_(value -> this.updatePreview());
        return (EditBox)this.m_142416_((GuiEventListener)box);
    }

    private EditBox createRadiusBox(int x, int y, int width) {
        EditBox box = new EditBox(this.f_96547_, x, y, width, 16, (Component)Component.m_237119_());
        box.m_94199_(2);
        box.m_94153_(value -> value.matches("\\d*"));
        box.m_94182_(false);
        box.m_94202_(-1577746);
        box.m_94151_(value -> this.updatePreview());
        return (EditBox)this.m_142416_((GuiEventListener)box);
    }

    private void prefillInputs() {
        FireControlComputation preferred;
        BlockPos target = this.vehicle.isFireControlActive() ? this.vehicle.getFireControlTarget() : this.getCrosshairTarget();
        this.targetX.m_94144_(Integer.toString(target.m_123341_()));
        this.targetY.m_94144_(Integer.toString(target.m_123342_()));
        this.targetZ.m_94144_(Integer.toString(target.m_123343_()));
        this.radius.m_94144_(Integer.toString(this.vehicle.isFireControlActive() ? this.vehicle.getFireControlRadius() : 0));
        if (!this.vehicle.isFireControlActive() && !(preferred = IndirectFireBallistics.solve((VehicleEntity)this.vehicle, this.vehicle.getTurretControllerIndex(), target, this.trajectoryMode)).isSuccess()) {
            TrajectoryMode alternate;
            TrajectoryMode trajectoryMode = alternate = this.trajectoryMode == TrajectoryMode.LOW ? TrajectoryMode.HIGH : TrajectoryMode.LOW;
            if (IndirectFireBallistics.solve((VehicleEntity)this.vehicle, this.vehicle.getTurretControllerIndex(), target, alternate).isSuccess()) {
                this.trajectoryMode = alternate;
            }
        }
    }

    private BlockPos getCrosshairTarget() {
        BlockPos blockPos;
        HitResult hit = this.player.m_19907_(512.0, 1.0f, false);
        if (hit instanceof BlockHitResult) {
            BlockHitResult blockHit = (BlockHitResult)hit;
            blockPos = blockHit.m_82425_();
        } else {
            blockPos = this.player.m_20183_();
        }
        return blockPos;
    }

    private void setTrajectoryMode(TrajectoryMode mode) {
        this.trajectoryMode = mode;
        this.updateModeLabels();
        this.updatePreview();
    }

    private void updateModeLabels() {
        if (this.lowMode == null || this.highMode == null) {
            return;
        }
        this.lowMode.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.trajectory.low").m_130940_(this.trajectoryMode == TrajectoryMode.LOW ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        this.highMode.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.trajectory.high").m_130940_(this.trajectoryMode == TrajectoryMode.HIGH ? ChatFormatting.GREEN : ChatFormatting.GRAY));
    }

    private void setRangeTable(boolean show) {
        this.showRangeTable = this.weatherJammed ? true : show;
        this.updateWidgetVisibility();
    }

    private void updateWidgetVisibility() {
        boolean inputsVisible;
        if (this.targetX == null) {
            return;
        }
        this.targetX.f_93624_ = inputsVisible = !this.showRangeTable && !this.weatherJammed;
        this.targetY.f_93624_ = inputsVisible;
        this.targetZ.f_93624_ = inputsVisible;
        this.radius.f_93624_ = inputsVisible;
        this.lowMode.f_93624_ = inputsVisible;
        this.highMode.f_93624_ = inputsVisible;
        this.apply.f_93624_ = inputsVisible;
        this.clear.f_93624_ = inputsVisible && !this.weatherJammed;
        this.apply.f_93623_ = inputsVisible && this.preview.isSuccess();
        this.targetTab.f_93623_ = this.showRangeTable && !this.weatherJammed;
        this.rangeTab.f_93623_ = !this.showRangeTable && !this.weatherJammed;
        this.targetTab.f_93624_ = !this.weatherJammed;
        boolean bl = this.rangeTab.f_93624_ = !this.weatherJammed;
        if (this.weatherJammed) {
            this.apply.f_93623_ = false;
            this.clear.f_93623_ = false;
        }
    }

    private void refreshWeatherJamState() {
        this.weatherJammed = EsWeatherFireControlBridge.isDisrupted((Entity)this.vehicle);
        if (this.weatherJammed) {
            this.showRangeTable = true;
            this.scrambleInputFields();
            if (this.targetTab != null) {
                this.targetTab.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(7)));
            }
            if (this.rangeTab != null) {
                this.rangeTab.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(9)));
            }
        } else if (this.wasWeatherJammed) {
            if (this.targetTab != null) {
                this.targetTab.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.tab.solution"));
            }
            if (this.rangeTab != null) {
                this.rangeTab.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.tab.range_table"));
            }
            this.updateModeLabels();
            if (this.apply != null) {
                this.apply.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.apply"));
            }
            if (this.clear != null) {
                this.clear.m_93666_((Component)Component.m_237115_((String)"screen.fcp.fire_control.clear"));
            }
        }
        this.wasWeatherJammed = this.weatherJammed;
        this.updateWidgetVisibility();
    }

    private void scrambleInputFields() {
        if (this.targetX == null) {
            return;
        }
        if (this.vehicle.m_9236_().m_46467_() % 4L == 0L) {
            this.targetX.m_94144_(FiringSolutionScreen.garble(6));
            this.targetY.m_94144_(FiringSolutionScreen.garble(5));
            this.targetZ.m_94144_(FiringSolutionScreen.garble(6));
            this.radius.m_94144_(FiringSolutionScreen.garble(2));
            if (this.apply != null) {
                this.apply.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(5)));
            }
            if (this.clear != null) {
                this.clear.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(5)));
            }
            if (this.lowMode != null) {
                this.lowMode.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(4)));
            }
            if (this.highMode != null) {
                this.highMode.m_93666_((Component)Component.m_237113_((String)FiringSolutionScreen.garble(4)));
            }
        }
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

    private void updatePreview() {
        if (this.targetX == null || this.targetY == null || this.targetZ == null || this.radius == null) {
            return;
        }
        try {
            int x = Integer.parseInt(this.targetX.m_94155_());
            int y = Integer.parseInt(this.targetY.m_94155_());
            int z = Integer.parseInt(this.targetZ.m_94155_());
            int hitRadius = Integer.parseInt(this.radius.m_94155_());
            if (hitRadius < 0 || hitRadius > 99) {
                this.preview = FireControlComputation.failure(FireControlStatus.INVALID_INPUT);
            } else {
                BlockPos target = new BlockPos(x, y, z);
                this.preview = y < this.vehicle.m_9236_().m_141937_() || y >= this.vehicle.m_9236_().m_151558_() || !this.vehicle.m_9236_().m_6857_().m_61937_(target) ? FireControlComputation.failure(FireControlStatus.INVALID_INPUT) : IndirectFireBallistics.solve((VehicleEntity)this.vehicle, this.vehicle.getTurretControllerIndex(), target, this.trajectoryMode);
            }
        }
        catch (NumberFormatException ignored) {
            this.preview = FireControlComputation.failure(FireControlStatus.INVALID_INPUT);
        }
        if (this.apply != null) {
            this.apply.f_93623_ = this.preview.isSuccess();
            this.clear.f_93623_ = this.vehicle.isFireControlActive();
        }
    }

    private void applySolution() {
        if (this.weatherJammed || EsWeatherFireControlBridge.isDisrupted((Entity)this.vehicle)) {
            this.player.m_5661_((Component)Component.m_237115_((String)"message.fcp.fire_control.weather_jammed"), true);
            return;
        }
        if (!this.preview.isSuccess()) {
            return;
        }
        try {
            BlockPos target = new BlockPos(Integer.parseInt(this.targetX.m_94155_()), Integer.parseInt(this.targetY.m_94155_()), Integer.parseInt(this.targetZ.m_94155_()));
            int hitRadius = Integer.parseInt(this.radius.m_94155_());
            FCPNetwork.FCP_HANDLER.sendToServer((Object)SetFireControlMessage.apply(this.vehicle.m_19879_(), target, hitRadius, this.trajectoryMode));
        }
        catch (NumberFormatException ignored) {
            this.preview = FireControlComputation.failure(FireControlStatus.INVALID_INPUT);
        }
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
        }
    }

    public void m_88315_(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.m_280273_(graphics);
        int accent = 0xFF000000 | this.vehicle.getHudColor();
        graphics.m_280509_(this.panelX + 2, this.panelY + 2, this.panelX + this.panelWidth + 2, this.panelY + this.panelHeight + 2, -1728053248);
        graphics.m_280509_(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + this.panelHeight, -234090478);
        graphics.m_280509_(this.panelX, this.panelY, this.panelX + this.panelWidth, this.panelY + 2, this.weatherJammed ? -39330 : accent);
        graphics.m_280509_(this.panelX, this.panelY + 20, this.panelX + this.panelWidth, this.panelY + 21, -11971494);
        MutableComponent titleText = this.weatherJammed ? Component.m_237115_((String)"screen.fcp.fire_control.title_jammed") : this.f_96539_;
        graphics.m_280614_(this.f_96547_, (Component)titleText, this.panelX + 10, this.panelY + 7, this.weatherJammed ? -39330 : -1577746, false);
        if (this.weatherJammed) {
            String noise = FiringSolutionScreen.garble(Math.min(18, this.panelWidth / 8));
            graphics.m_280056_(this.f_96547_, noise, this.panelX + this.panelWidth - 10 - this.f_96547_.m_92895_(noise), this.panelY + 7, -6773334, false);
        } else {
            String vehicleName = this.f_96547_.m_92834_(this.vehicle.m_5446_().getString(), this.panelWidth / 2);
            graphics.m_280056_(this.f_96547_, vehicleName, this.panelX + this.panelWidth - 10 - this.f_96547_.m_92895_(vehicleName), this.panelY + 7, -6773334, false);
        }
        if (this.weatherJammed || this.showRangeTable) {
            this.renderRangeTable(graphics, this.weatherJammed ? -14249 : accent);
            if (this.weatherJammed) {
                graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.jammed_hint"), this.panelX + 12, this.panelY + this.panelHeight - 38, -14249, false);
            }
        } else if (this.compact) {
            this.renderCompactSolution(graphics, accent);
        } else {
            this.renderWideSolution(graphics, accent);
        }
        super.m_88315_(graphics, mouseX, mouseY, partialTick);
    }

    private void renderWideSolution(GuiGraphics graphics, int accent) {
        int labelX = this.panelX + 12;
        int fieldY = this.panelY + 52;
        this.drawInputRow(graphics, (Component)Component.m_237115_((String)"screen.fcp.fire_control.target_x"), labelX, fieldY, this.targetX);
        this.drawInputRow(graphics, (Component)Component.m_237115_((String)"screen.fcp.fire_control.target_y"), labelX, fieldY + 22, this.targetY);
        this.drawInputRow(graphics, (Component)Component.m_237115_((String)"screen.fcp.fire_control.target_z"), labelX, fieldY + 44, this.targetZ);
        this.drawInputRow(graphics, (Component)Component.m_237115_((String)"screen.fcp.fire_control.radius"), labelX, fieldY + 66, this.radius);
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.trajectory"), labelX, this.panelY + 135, -6773334, false);
        int divider = this.panelX + 170;
        graphics.m_280509_(divider, this.panelY + 45, divider + 1, this.panelY + this.panelHeight - 30, -11971494);
        this.renderSolutionReadout(graphics, divider + 12, this.panelY + 50, accent, true);
    }

    private void renderCompactSolution(GuiGraphics graphics, int accent) {
        graphics.m_280056_(this.f_96547_, "X", this.panelX + 12, this.panelY + 55, -6773334, false);
        graphics.m_280056_(this.f_96547_, "Y", this.panelX + this.panelWidth / 2 - 4, this.panelY + 55, -6773334, false);
        graphics.m_280056_(this.f_96547_, "Z", this.panelX + 12, this.panelY + 80, -6773334, false);
        graphics.m_280056_(this.f_96547_, "R", this.panelX + this.panelWidth / 2 - 4, this.panelY + 80, -6773334, false);
        this.drawInputSurface(graphics, this.targetX);
        this.drawInputSurface(graphics, this.targetY);
        this.drawInputSurface(graphics, this.targetZ);
        this.drawInputSurface(graphics, this.radius);
        this.renderSolutionReadout(graphics, this.panelX + this.panelWidth / 2 + 12, this.panelY + 104, accent, false);
    }

    private void drawInputRow(GuiGraphics graphics, Component label, int x, int y, EditBox box) {
        graphics.m_280614_(this.f_96547_, label, x, y + 4, -6773334, false);
        this.drawInputSurface(graphics, box);
    }

    private void drawInputSurface(GuiGraphics graphics, EditBox box) {
        graphics.m_280509_(box.m_252754_() - 2, box.m_252907_() - 1, box.m_252754_() + box.m_5711_() + 2, box.m_252907_() + box.m_93694_() + 1, -14669780);
        graphics.m_280509_(box.m_252754_() - 2, box.m_252907_() + box.m_93694_(), box.m_252754_() + box.m_5711_() + 2, box.m_252907_() + box.m_93694_() + 1, box.m_93696_() ? -9972847 : -11971494);
    }

    private void renderSolutionReadout(GuiGraphics graphics, int x, int y, int accent, boolean drawPlot) {
        FireControlStatus status = this.displayStatus();
        int statusColor = FiringSolutionScreen.statusColor(status, accent);
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.status"), x, y, -6773334, false);
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)status.translationKey()), x + 48, y, statusColor, false);
        if (!this.preview.isSuccess()) {
            graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)this.preview.status().translationKey()), x, y + 18, -39330, false);
            return;
        }
        FireControlSolution solution = this.preview.solution();
        if (!drawPlot) {
            this.drawValue(graphics, x, y + 17, "screen.fcp.fire_control.range_value", FiringSolutionScreen.format1(solution.range()) + " m");
            this.drawValue(graphics, x, y + 27, "screen.fcp.fire_control.elevation_value", FiringSolutionScreen.format1(solution.pitch()) + "\u00b0");
            return;
        }
        this.drawValue(graphics, x, y + 18, "screen.fcp.fire_control.range_value", FiringSolutionScreen.format1(solution.range()) + " m");
        this.drawValue(graphics, x, y + 32, "screen.fcp.fire_control.bearing_value", FiringSolutionScreen.format1(solution.yaw()) + "\u00b0");
        this.drawValue(graphics, x, y + 46, "screen.fcp.fire_control.elevation_value", FiringSolutionScreen.format1(solution.pitch()) + "\u00b0");
        this.drawValue(graphics, x, y + 60, "screen.fcp.fire_control.flight_time_value", FiringSolutionScreen.format1(solution.flightTime() / 20.0) + " s");
        if (drawPlot) {
            this.drawTargetPlot(graphics, x + 123, y + 107, 27, accent);
        }
    }

    private FireControlStatus displayStatus() {
        if (this.vehicle.isFireControlActive() && this.parsedTarget().equals((Object)this.vehicle.getFireControlTarget()) && this.parsedRadius() == this.vehicle.getFireControlRadius() && this.trajectoryMode == this.vehicle.getFireControlTrajectory()) {
            return this.vehicle.getFireControlStatus();
        }
        return this.preview.isSuccess() ? FireControlStatus.ALIGNING : this.preview.status();
    }

    private BlockPos parsedTarget() {
        try {
            return new BlockPos(Integer.parseInt(this.targetX.m_94155_()), Integer.parseInt(this.targetY.m_94155_()), Integer.parseInt(this.targetZ.m_94155_()));
        }
        catch (NumberFormatException ignored) {
            return BlockPos.f_121853_;
        }
    }

    private int parsedRadius() {
        try {
            return Integer.parseInt(this.radius.m_94155_());
        }
        catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private void drawValue(GuiGraphics graphics, int x, int y, String key, String value) {
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)key), x, y, -6773334, false);
        graphics.m_280056_(this.f_96547_, value, x + 62, y, -1577746, false);
    }

    private void drawTargetPlot(GuiGraphics graphics, int centerX, int centerY, int plotRadius, int accent) {
        graphics.m_280509_(centerX - plotRadius - 3, centerY - plotRadius - 3, centerX + plotRadius + 4, centerY + plotRadius + 4, -15262432);
        for (int i = 0; i < 32; ++i) {
            double angle = (double)i * Math.PI * 2.0 / 32.0;
            int x = centerX + (int)Math.round(Math.cos(angle) * (double)plotRadius);
            int y = centerY + (int)Math.round(Math.sin(angle) * (double)plotRadius);
            graphics.m_280509_(x, y, x + 1, y + 1, accent);
        }
        graphics.m_280509_(centerX - 4, centerY, centerX + 5, centerY + 1, -1577746);
        graphics.m_280509_(centerX, centerY - 4, centerX + 1, centerY + 5, -1577746);
        graphics.m_280653_(this.f_96547_, (Component)Component.m_237110_((String)"screen.fcp.fire_control.radius_short", (Object[])new Object[]{this.radius.m_94155_()}), centerX, centerY + plotRadius + 6, -6773334);
    }

    private void renderRangeTable(GuiGraphics graphics, int accent) {
        int startY = this.panelY + 48;
        int left = this.panelX + 18;
        int col2 = this.panelX + this.panelWidth / 2 - 15;
        int col3 = this.panelX + this.panelWidth - 86;
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.table.elevation"), left, startY, accent, false);
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.table.range"), col2, startY, accent, false);
        graphics.m_280614_(this.f_96547_, (Component)Component.m_237115_((String)"screen.fcp.fire_control.table.time"), col3, startY, accent, false);
        graphics.m_280509_(left, startY + 11, this.panelX + this.panelWidth - 18, startY + 12, -11971494);
        int rows = this.compact ? 6 : 8;
        double minPitch = this.vehicle.getTurretMinPitch();
        double maxPitch = this.vehicle.getTurretMaxPitch();
        double velocity = this.vehicle.getProjectileVelocity(this.vehicle.getTurretControllerIndex());
        double gravity = this.vehicle.getProjectileGravity(this.vehicle.getTurretControllerIndex());
        double muzzleY = this.vehicle.getShootPos((int)this.vehicle.getTurretControllerIndex(), (float)1.0f).f_82480_;
        double targetY = this.parsedTarget().equals((Object)BlockPos.f_121853_) ? this.vehicle.m_20186_() : this.parsedTarget().m_252807_().f_82480_;
        int rowHeight = this.compact ? 11 : 15;
        for (int i = 0; i < rows; ++i) {
            double pitch = minPitch + (maxPitch - minPitch) * (double)i / ((double)rows - 1.0);
            double range = IndirectFireBallistics.rangeAtPitch(velocity, gravity, muzzleY, targetY, pitch);
            double horizontalSpeed = velocity * Math.cos(Math.toRadians(pitch));
            double time = horizontalSpeed > 1.0E-8 ? range / horizontalSpeed / 20.0 : 0.0;
            int rowY = startY + 17 + i * rowHeight;
            if ((i & 1) == 0) {
                graphics.m_280509_(left - 4, rowY - 2, this.panelX + this.panelWidth - 18, rowY + 9, -15262432);
            }
            graphics.m_280056_(this.f_96547_, FiringSolutionScreen.format1(pitch) + "\u00b0", left, rowY, -1577746, false);
            graphics.m_280056_(this.f_96547_, FiringSolutionScreen.format0(range) + " m", col2, rowY, -1577746, false);
            graphics.m_280056_(this.f_96547_, FiringSolutionScreen.format1(time) + " s", col3, rowY, -1577746, false);
        }
    }

    private static int statusColor(FireControlStatus status, int accent) {
        return switch (status) {
            default -> throw new IncompatibleClassChangeError();
            case FireControlStatus.READY -> -9972847;
            case FireControlStatus.MOVING -> -14249;
            case FireControlStatus.ALIGNING -> accent;
            case FireControlStatus.INACTIVE -> -6773334;
            case FireControlStatus.OUT_OF_RANGE, FireControlStatus.PITCH_LIMIT, FireControlStatus.YAW_LIMIT, FireControlStatus.INVALID_INPUT, FireControlStatus.INVALID_WEAPON, FireControlStatus.WRECKED -> -39330;
        };
    }

    private static String format0(double value) {
        return String.format(Locale.ROOT, "%.0f", value);
    }

    private static String format1(double value) {
        return String.format(Locale.ROOT, "%.1f", value);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if ((keyCode == 257 || keyCode == 335) && this.apply.f_93623_ && this.apply.f_93624_) {
            this.applySolution();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    public boolean m_7043_() {
        return false;
    }
}

