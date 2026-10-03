/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 */
package com.example.espoints.capturepoint;

import com.example.espoints.capturepoint.CaptureProgressIntegrator;
import com.example.espoints.capturepoint.CaptureState;
import com.example.espoints.capturepoint.DisplayState;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class CapturePoint {
    private final String name;
    private final BlockPos pos1;
    private final BlockPos pos2;
    private final int batch;
    private CaptureState state;
    private DisplayState displayState;
    private String captorName;
    private int progress;
    private double preciseProgress;
    private long capturedStateStartTime;
    private final int minX;
    private final int minY;
    private final int minZ;
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    public CapturePoint(String name, BlockPos pos1, BlockPos pos2) {
        this(name, pos1, pos2, 1);
    }

    public CapturePoint(String name, BlockPos pos1, BlockPos pos2, int batch) {
        this.name = name;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.batch = batch;
        this.minX = Math.min(pos1.m_123341_(), pos2.m_123341_());
        this.maxX = Math.max(pos1.m_123341_(), pos2.m_123341_());
        this.minY = Math.min(pos1.m_123342_(), pos2.m_123342_());
        this.maxY = Math.max(pos1.m_123342_(), pos2.m_123342_());
        this.minZ = Math.min(pos1.m_123343_(), pos2.m_123343_());
        this.maxZ = Math.max(pos1.m_123343_(), pos2.m_123343_());
        this.state = CaptureState.NEUTRAL;
        this.displayState = DisplayState.NEUTRAL;
        this.captorName = "";
        this.progress = 0;
        this.preciseProgress = 0.0;
        this.capturedStateStartTime = 0L;
        ModLogger.debug("\u521b\u5efa\u636e\u70b9: " + name + " (\u6279\u6b21 " + batch + ") \u4ece (" + pos1.m_123341_() + "," + pos1.m_123342_() + "," + pos1.m_123343_() + ") \u5230 (" + pos2.m_123341_() + "," + pos2.m_123342_() + "," + pos2.m_123343_() + ")");
    }

    public boolean isPositionInside(BlockPos pos) {
        int x = pos.m_123341_();
        int y = pos.m_123342_();
        int z = pos.m_123343_();
        return x >= this.minX && x <= this.maxX && y >= this.minY && y <= this.maxY && z >= this.minZ && z <= this.maxZ;
    }

    public void updateStatus(List<? extends Player> playersInPoint) {
        this.updateStatus(playersInPoint, 40);
    }

    public void updateStatus(List<? extends Player> playersInPoint, int elapsedTicks) {
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("elapsedTicks must not be negative");
        }
        try {
            ModLogger.debug("\u66f4\u65b0\u636e\u70b9 " + this.name + " \u72b6\u6001\uff0c\u5f53\u524d\u72b6\u6001: " + String.valueOf((Object)this.state) + ", \u73a9\u5bb6\u6570\u91cf: " + playersInPoint.size());
            switch (this.state) {
                case NEUTRAL: {
                    this.handleNeutralState(playersInPoint, elapsedTicks);
                    break;
                }
                case CAPTURING_FLAG: {
                    this.handleCapturingFlagState(playersInPoint, elapsedTicks);
                    break;
                }
                case CONTESTED: {
                    this.handleContestedState(playersInPoint, elapsedTicks);
                    break;
                }
                case CAPTURING_CONTESTED: {
                    this.handleCapturingContestedState(playersInPoint, elapsedTicks);
                    break;
                }
                case CAPTURING_DOWN: {
                    this.handleCapturingDownState(playersInPoint, elapsedTicks);
                    break;
                }
                case CAPTURED: {
                    this.handleCapturedState(playersInPoint, elapsedTicks);
                }
            }
            this.updateDisplayState(playersInPoint);
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u66f4\u65b0\u540e\u72b6\u6001: " + String.valueOf((Object)this.state) + ", \u663e\u793a\u72b6\u6001: " + String.valueOf((Object)this.getDisplayState()) + ", \u8fdb\u5ea6: " + this.progress + ", \u5360\u9886\u8005: " + this.captorName);
        }
        catch (Exception e) {
            ModLogger.error("\u66f4\u65b0\u636e\u70b9\u72b6\u6001\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
        }
    }

    private void handleNeutralState(List<? extends Player> playersInPoint, int elapsedTicks) {
        Map<String, Integer> teamGroups = this.getTeamGroups(playersInPoint);
        if (teamGroups.isEmpty()) {
            this.resetToNeutral("\u636e\u70b9 " + this.name + " \u4fdd\u6301\u4e2d\u7acb\u72b6\u6001\uff0c\u65e0\u9635\u8425\u73a9\u5bb6\u5728\u70b9\u5185");
            return;
        }
        this.pushNeutralProgressByMajority(teamGroups, elapsedTicks);
    }

    private void handleCapturingFlagState(List<? extends Player> playersInPoint, int elapsedTicks) {
        Map<String, Integer> teamGroups = this.getTeamGroups(playersInPoint);
        if (teamGroups.isEmpty()) {
            this.resetToNeutral("\u636e\u70b9 " + this.name + " \u73a9\u5bb6\u79bb\u5f00\uff0c\u56de\u5230\u4e2d\u7acb\u72b6\u6001");
            return;
        }
        this.pushNeutralProgressByMajority(teamGroups, elapsedTicks);
    }

    private void handleContestedState(List<? extends Player> playersInPoint, int elapsedTicks) {
        Map<String, Integer> teamGroups = this.getTeamGroups(playersInPoint);
        if (teamGroups.isEmpty()) {
            this.state = CaptureState.CAPTURED;
            this.capturedStateStartTime = 0L;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u73a9\u5bb6\u79bb\u5f00\uff0c\u56de\u5230\u5df2\u5360\u9886\u72b6\u6001");
            return;
        }
        this.pushCapturedProgressByMajority(teamGroups, elapsedTicks);
    }

    private void handleCapturingContestedState(List<? extends Player> playersInPoint, int elapsedTicks) {
        Map<String, Integer> teamGroups = this.getTeamGroups(playersInPoint);
        if (teamGroups.isEmpty()) {
            this.resetToNeutral("\u636e\u70b9 " + this.name + " \u73a9\u5bb6\u5168\u90e8\u79bb\u5f00\uff0c\u56de\u5230\u4e2d\u7acb\u72b6\u6001");
            return;
        }
        this.pushNeutralProgressByMajority(teamGroups, elapsedTicks);
    }

    private void handleCapturingDownState(List<? extends Player> playersInPoint, int elapsedTicks) {
        Map<String, Integer> teamGroups = this.getTeamGroups(playersInPoint);
        if (teamGroups.isEmpty()) {
            if (this.preciseProgress <= 0.0) {
                this.resetToNeutral("\u636e\u70b9 " + this.name + " \u73a9\u5bb6\u79bb\u5f00\uff0c\u8fdb\u5ea6\u5df2\u8017\u5c3d\uff0c\u56de\u5230\u4e2d\u7acb\u72b6\u6001");
            } else {
                this.state = CaptureState.CAPTURED;
                this.capturedStateStartTime = 0L;
                ModLogger.debug("\u636e\u70b9 " + this.name + " \u73a9\u5bb6\u79bb\u5f00\uff0c\u8fdb\u5ea6\u4ecd\u9ad8\uff0c\u56de\u5230\u5df2\u5360\u9886\u72b6\u6001");
            }
            return;
        }
        this.pushCapturedProgressByMajority(teamGroups, elapsedTicks);
    }

    private void handleCapturedState(List<? extends Player> playersInPoint, int elapsedTicks) {
        long currentTime;
        long duration;
        Map<String, Integer> teamGroups;
        if (this.capturedStateStartTime == 0L) {
            this.capturedStateStartTime = System.currentTimeMillis();
        }
        if ((teamGroups = this.getTeamGroups(playersInPoint)).isEmpty()) {
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u65e0\u73a9\u5bb6\u5728\u573a\uff0c\u4fdd\u6301\u5df2\u5360\u9886\u72b6\u6001");
        } else {
            this.pushCapturedProgressByMajority(teamGroups, elapsedTicks);
        }
        if (this.state == CaptureState.CAPTURED && this.progress < 100 && (duration = (currentTime = System.currentTimeMillis()) - this.capturedStateStartTime) >= 5000L) {
            this.setPreciseProgress(100.0);
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u5df2\u5360\u9886\u72b6\u6001\u6301\u7eed5\u79d2\uff0c\u8fdb\u5ea6\u81ea\u52a8\u6062\u590d\u5230100%");
        }
    }

    private void pushNeutralProgressByMajority(Map<String, Integer> teamGroups, int elapsedTicks) {
        String majorityTeam = this.getMajorityTeam(teamGroups);
        if (majorityTeam == null) {
            this.state = CaptureState.CAPTURING_CONTESTED;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u53cc\u65b9\u4eba\u6570\u76f8\u540c\uff0c\u5347\u65d7\u8fdb\u5ea6\u6682\u505c\uff0c\u961f\u4f0d\u6570\u91cf: " + teamGroups.size());
            return;
        }
        if (this.captorName == null || this.captorName.isEmpty()) {
            this.captorName = majorityTeam;
        }
        if (EspetroTeamBridge.isSameTeam(majorityTeam, this.captorName)) {
            this.changeProgress(1, elapsedTicks);
            if (this.preciseProgress >= 100.0) {
                this.captureForTeam(majorityTeam);
            } else {
                this.state = CaptureState.CAPTURING_FLAG;
                ModLogger.debug("\u636e\u70b9 " + this.name + " \u591a\u6570\u65b9 " + majorityTeam + " \u63a8\u8fdb\u5347\u65d7\uff0c\u8fdb\u5ea6: " + this.progress + "\uff0c\u4eba\u6570: " + String.valueOf(teamGroups.get(majorityTeam)));
            }
            return;
        }
        this.changeProgress(-1, elapsedTicks);
        if (this.preciseProgress <= 0.0) {
            this.captorName = majorityTeam;
            this.state = CaptureState.CAPTURING_FLAG;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u539f\u5347\u65d7\u8fdb\u5ea6\u88ab\u538b\u5236\u6e05\u7a7a\uff0c\u591a\u6570\u65b9\u5207\u6362\u4e3a " + majorityTeam);
        } else {
            this.state = CaptureState.CAPTURING_CONTESTED;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u591a\u6570\u65b9 " + majorityTeam + " \u6b63\u5728\u538b\u5236 " + this.captorName + " \u7684\u5347\u65d7\u8fdb\u5ea6\uff0c\u8fdb\u5ea6: " + this.progress);
        }
    }

    private void pushCapturedProgressByMajority(Map<String, Integer> teamGroups, int elapsedTicks) {
        if (this.captorName == null || this.captorName.isEmpty()) {
            this.pushNeutralProgressByMajority(teamGroups, elapsedTicks);
            return;
        }
        String majorityTeam = this.getMajorityTeam(teamGroups);
        if (majorityTeam == null) {
            this.state = CaptureState.CONTESTED;
            this.capturedStateStartTime = 0L;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u53cc\u65b9\u4eba\u6570\u76f8\u540c\uff0c\u964d\u65d7/\u6062\u590d\u8fdb\u5ea6\u6682\u505c\uff0c\u961f\u4f0d\u6570\u91cf: " + teamGroups.size());
            return;
        }
        if (EspetroTeamBridge.isSameTeam(majorityTeam, this.captorName)) {
            this.changeProgress(1, elapsedTicks);
            this.state = CaptureState.CAPTURED;
            if (this.preciseProgress >= 100.0) {
                this.capturedStateStartTime = 0L;
            }
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u5360\u9886\u65b9 " + this.captorName + " \u4eba\u6570\u5360\u4f18\uff0c\u6062\u590d\u8fdb\u5ea6: " + this.progress);
            return;
        }
        this.changeProgress(-1, elapsedTicks);
        this.capturedStateStartTime = 0L;
        if (this.preciseProgress <= 0.0) {
            this.resetToNeutral("\u636e\u70b9 " + this.name + " \u88ab\u591a\u6570\u65b9 " + majorityTeam + " \u964d\u65d7\u5b8c\u6210\uff0c\u56de\u5230\u4e2d\u7acb\u72b6\u6001");
        } else {
            this.state = CaptureState.CAPTURING_DOWN;
            ModLogger.debug("\u636e\u70b9 " + this.name + " \u591a\u6570\u65b9 " + majorityTeam + " \u6b63\u5728\u964d\u65d7\uff0c\u8fdb\u5ea6: " + this.progress + "\uff0c\u5360\u9886\u65b9: " + this.captorName);
        }
    }

    private String getMajorityTeam(Map<String, Integer> teamGroups) {
        String majorityTeam = null;
        int majorityCount = 0;
        boolean tied = false;
        for (Map.Entry<String, Integer> entry : teamGroups.entrySet()) {
            int count = entry.getValue();
            if (count > majorityCount) {
                majorityTeam = entry.getKey();
                majorityCount = count;
                tied = false;
                continue;
            }
            if (count != majorityCount) continue;
            tied = true;
        }
        return majorityCount > 0 && !tied ? majorityTeam : null;
    }

    private void captureForTeam(String teamName) {
        this.captorName = teamName;
        this.setPreciseProgress(100.0);
        this.state = CaptureState.CAPTURED;
        this.capturedStateStartTime = 0L;
        ModLogger.info("\u636e\u70b9 " + this.name + " \u88ab\u5360\u9886\uff0c\u5360\u9886\u8005: " + this.captorName);
    }

    private void resetToNeutral(String reason) {
        this.state = CaptureState.NEUTRAL;
        this.setPreciseProgress(0.0);
        this.captorName = "";
        this.capturedStateStartTime = 0L;
        ModLogger.debug(reason);
    }

    private void changeProgress(int direction, int elapsedTicks) {
        this.setPreciseProgress(CaptureProgressIntegrator.advance(this.preciseProgress, direction, elapsedTicks));
    }

    private void setPreciseProgress(double value) {
        this.preciseProgress = CaptureProgressIntegrator.clamp(value);
        this.progress = CaptureProgressIntegrator.display(this.preciseProgress);
    }

    private void updateDisplayState(List<? extends Player> playersInPoint) {
        switch (this.state) {
            case NEUTRAL: {
                this.displayState = DisplayState.NEUTRAL;
                this.captorName = "";
                break;
            }
            case CAPTURING_FLAG: {
                if (playersInPoint.isEmpty()) break;
                this.displayState = DisplayState.CAPTURING_FLAG_SINGLE;
                break;
            }
            case CONTESTED: {
                this.displayState = DisplayState.CONTESTED_MULTI;
                break;
            }
            case CAPTURING_CONTESTED: {
                this.displayState = DisplayState.CAPTURING_CONTESTED_MULTI;
                break;
            }
            case CAPTURING_DOWN: {
                this.displayState = DisplayState.CAPTURING_DOWN;
                break;
            }
            case CAPTURED: {
                this.displayState = DisplayState.CAPTURED;
            }
        }
    }

    private Map<String, Integer> getTeamGroups(List<? extends Player> playersInPoint) {
        HashMap<String, Integer> teamGroups = new HashMap<String, Integer>();
        for (Player player : playersInPoint) {
            ServerPlayer serverPlayer;
            String teamName;
            if (!(player instanceof ServerPlayer) || (teamName = EspetroTeamBridge.getServerPlayerTeam(serverPlayer = (ServerPlayer)player)) == null) continue;
            teamGroups.merge(teamName, 1, Integer::sum);
        }
        return teamGroups;
    }

    public String getName() {
        return this.name;
    }

    public BlockPos getPos1() {
        return this.pos1;
    }

    public BlockPos getPos2() {
        return this.pos2;
    }

    public CaptureState getState() {
        return this.state;
    }

    public void setState(CaptureState state) {
        this.state = state;
    }

    public DisplayState getDisplayState() {
        return this.displayState;
    }

    public void setDisplayState(DisplayState displayState) {
        this.displayState = displayState;
    }

    public String getCaptorName() {
        return this.captorName;
    }

    public void setCaptorName(String captorName) {
        String canonicalTeam = EspetroTeamBridge.canonicalizeTeamName(captorName);
        this.captorName = canonicalTeam != null ? canonicalTeam : captorName;
    }

    public int getProgress() {
        return this.progress;
    }

    public void setProgress(int progress) {
        this.setPreciseProgress(progress);
    }

    public String getInfoString() {
        return this.name + " (\u6279\u6b21 " + this.batch + ") - " + String.valueOf((Object)this.state) + " - " + this.progress + "% - \u5360\u9886\u8005: " + this.captorName;
    }

    public int getBatch() {
        return this.batch;
    }

    public SerializableCapturePoint toSerializable() {
        return new SerializableCapturePoint(this.name, this.pos1, this.pos2, this.batch, this.state, this.displayState, this.captorName, this.progress);
    }

    public void restoreFromSerializable(SerializableCapturePoint serializable) {
        this.state = serializable.state;
        this.displayState = serializable.displayState;
        this.captorName = serializable.captorName;
        this.setPreciseProgress(serializable.progress);
    }

    public static class SerializableCapturePoint {
        public final String name;
        public final BlockPos pos1;
        public final BlockPos pos2;
        public final int batch;
        public final CaptureState state;
        public final DisplayState displayState;
        public final String captorName;
        public final int progress;

        public SerializableCapturePoint(String name, BlockPos pos1, BlockPos pos2, int batch, CaptureState state, DisplayState displayState, String captorName, int progress) {
            this.name = name;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.batch = batch;
            this.state = state;
            this.displayState = displayState;
            this.captorName = captorName;
            this.progress = progress;
        }

        public static SerializableCapturePoint fromNetwork(FriendlyByteBuf buf) {
            try {
                String name = buf.m_130136_(32);
                BlockPos pos1 = buf.m_130135_();
                BlockPos pos2 = buf.m_130135_();
                int batch = buf.m_130242_();
                short stateId = buf.readUnsignedByte();
                short displayStateId = buf.readUnsignedByte();
                CaptureState[] states = CaptureState.values();
                DisplayState[] displayStates = DisplayState.values();
                if (stateId >= states.length || displayStateId >= displayStates.length) {
                    throw new IllegalArgumentException("\u672a\u77e5\u7684\u636e\u70b9\u72b6\u6001\u7f16\u53f7");
                }
                CaptureState state = states[stateId];
                DisplayState displayState = displayStates[displayStateId];
                String captorName = buf.m_130136_(32);
                int progress = buf.m_130242_();
                SerializableCapturePoint.validateNetworkFields(name, pos1, pos2, batch, captorName, progress);
                return new SerializableCapturePoint(name, pos1, pos2, batch, state, displayState, captorName, progress);
            }
            catch (Exception e) {
                ModLogger.error("\u4ece\u7f51\u7edc\u6570\u636e\u5305\u8bfb\u53d6\u636e\u70b9\u4fe1\u606f\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
                throw new IllegalArgumentException("\u65e0\u6cd5\u89e3\u7801\u636e\u70b9\u4fe1\u606f", e);
            }
        }

        public void toNetwork(FriendlyByteBuf buf) {
            SerializableCapturePoint.validateNetworkFields(this.name, this.pos1, this.pos2, this.batch, this.captorName, this.progress);
            if (this.state == null || this.displayState == null) {
                throw new IllegalArgumentException("\u636e\u70b9\u72b6\u6001\u4e0d\u80fd\u4e3a\u7a7a");
            }
            buf.m_130072_(this.name, 32);
            buf.m_130064_(this.pos1);
            buf.m_130064_(this.pos2);
            buf.m_130130_(this.batch);
            buf.writeByte(this.state.ordinal());
            buf.writeByte(this.displayState.ordinal());
            buf.m_130072_(this.captorName, 32);
            buf.m_130130_(this.progress);
        }

        private static void validateNetworkFields(String name, BlockPos pos1, BlockPos pos2, int batch, String captorName, int progress) {
            if (name == null || name.isBlank() || name.length() > 32 || captorName == null || captorName.length() > 32 || pos1 == null || pos2 == null || batch < 1 || batch > 64 || progress < 0 || progress > 100) {
                throw new IllegalArgumentException("\u636e\u70b9\u7f51\u7edc\u5b57\u6bb5\u8d85\u51fa\u534f\u8bae\u9650\u5236");
            }
        }
    }
}

