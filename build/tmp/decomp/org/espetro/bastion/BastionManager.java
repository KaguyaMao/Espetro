/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  javax.annotation.Nullable
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.bastion;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.api.event.BastionLifecycleEvent;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionEventHandler;
import org.espetro.bastion.BastionItems;
import org.espetro.bastion.HabChannelManager;
import org.espetro.bastion.RadioCoveragePolicy;
import org.espetro.bastion.StructureKind;
import org.espetro.config.GameConfig;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamPackManager;
import org.espetro.team.TroopCountManager;
import org.espetro.team.VoteManager;

public class BastionManager {
    public static final int MAX_BASTIONS = 4;
    private static final int TELEPORT_PORTAL_TICKET_RADIUS = 1;
    @Deprecated
    public static final int MAX_BASTIONS_PER_TEAM = 4;
    private static BastionManager INSTANCE;
    private final Map<UUID, BastionData> bastions = new HashMap<UUID, BastionData>();
    private final Map<UUID, BlockPos> bastionRecordPositions = new HashMap<UUID, BlockPos>();
    private final Map<UUID, UUID> bastionIdsByArmorStand = new HashMap<UUID, UUID>();
    private final Map<UUID, UUID> waitingPlayers = new HashMap<UUID, UUID>();
    private final Map<HabChunkKey, CompletableFuture<Boolean>> pendingHabChunkLoads = new HashMap<HabChunkKey, CompletableFuture<Boolean>>();
    private final Set<UUID> pendingHabTeleports = new HashSet<UUID>();
    private final Set<UUID> deathWaitingPlayers = new HashSet<UUID>();
    private final Map<UUID, DeployPoint> playerDeployPoints = new HashMap<UUID, DeployPoint>();
    private final Map<UUID, Long> bastionCooldowns = new HashMap<UUID, Long>();
    private final Map<UUID, Vec3> playerLockPositions = new HashMap<UUID, Vec3>();
    private final Map<UUID, Long> resupplyCooldowns = new HashMap<UUID, Long>();
    private final Map<UUID, DerivedTacticalState> derivedTacticalStates = new HashMap<UUID, DerivedTacticalState>();
    private long lastDerivedTacticalTick = -4611686018427387904L;
    public static final long RESUPPLY_COOLDOWN_MS = 300000L;
    private int cooldownSeconds = 800;
    private int requiredPlanks = 640;
    private int armorStandHealth = 5;
    private int destroyTroopPenalty = 20;
    private final Map<BlockPos, UUID> radioBlockPositions = new HashMap<BlockPos, UUID>();
    private final Map<UUID, long[]> habProxyCache = new HashMap<UUID, long[]>();
    private static final int HAB_PROXY_CACHE_TICKS = 30;

    private BastionManager() {
        INSTANCE = this;
        this.loadConfig();
    }

    private void loadConfig() {
    }

    public void reloadConfig() {
    }

    public void applyExternalJson(String rawJson) {
        this.cooldownSeconds = 800;
        this.requiredPlanks = 640;
        this.armorStandHealth = 5;
        this.destroyTroopPenalty = 20;
        JsonObject json = (JsonObject)new Gson().fromJson(rawJson, JsonObject.class);
        if (json == null || !json.has("bastion")) {
            return;
        }
        JsonObject bastion = json.getAsJsonObject("bastion");
        if (bastion.has("cooldown_seconds")) {
            this.cooldownSeconds = Math.max(0, bastion.get("cooldown_seconds").getAsInt());
        }
        if (bastion.has("required_planks")) {
            this.requiredPlanks = Math.max(0, bastion.get("required_planks").getAsInt());
        }
        if (bastion.has("armor_stand_health")) {
            this.armorStandHealth = Math.max(1, bastion.get("armor_stand_health").getAsInt());
        }
        if (bastion.has("destroy_troop_penalty")) {
            this.destroyTroopPenalty = Math.max(0, bastion.get("destroy_troop_penalty").getAsInt());
        }
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getRequiredPlanks() {
        return this.requiredPlanks;
    }

    public int getEffectiveRadioCooldownSeconds() {
        int configured = LogisticsConfig.get().getRadio().cooldownSeconds;
        return configured >= 0 ? configured : this.cooldownSeconds;
    }

    public int getEffectiveRadioRequiredConstruction() {
        return 0;
    }

    public int getHabConstructionCost() {
        return Math.max(0, LogisticsConfig.get().habConstructionCost);
    }

    public int getArmorStandHealth() {
        return this.armorStandHealth;
    }

    public int getDestroyTroopPenalty() {
        return this.destroyTroopPenalty;
    }

    public static BastionManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BastionManager();
        }
        return INSTANCE;
    }

    public void tickDerivedTacticalState(MinecraftServer server) {
        if (server == null) {
            return;
        }
        long tick = server.m_129921_();
        if (tick - this.lastDerivedTacticalTick < 20L) {
            return;
        }
        this.lastDerivedTacticalTick = tick;
        long now = System.currentTimeMillis();
        HashMap<UUID, DerivedTacticalState> current = new HashMap<UUID, DerivedTacticalState>();
        for (BastionData bastion : this.bastions.values()) {
            if (bastion == null || !bastion.isActive()) continue;
            boolean activationReady = bastion.getHabAvailableAt() <= now;
            boolean suppressionWindowActive = bastion.getHabDisabledUntil() > now;
            boolean radioCovered = !bastion.isHab() || this.isCoveredByFriendlyRadio(bastion);
            boolean proximitySuppressed = (bastion.isHab() || bastion.isLegacyCombined()) && bastion.isHabBuilt() && this.isHabProxied(bastion);
            boolean habOperational = this.isHabOperational(bastion);
            current.put(bastion.getBastionId(), new DerivedTacticalState(habOperational, activationReady, suppressionWindowActive, proximitySuppressed, radioCovered));
        }
        if (!current.equals(this.derivedTacticalStates)) {
            this.derivedTacticalStates.clear();
            this.derivedTacticalStates.putAll(current);
            EspetroAPI.markTacticalMapStateDirty();
        }
    }

    public BastionData createBastion(ServerLevel level, BlockPos pos, String team, String name) {
        return this.createStructure(level, pos, team, name, StructureKind.RADIO);
    }

    public BastionData createRadio(ServerLevel level, BlockPos pos, String team, String name) {
        return this.createStructure(level, pos, team, name, StructureKind.RADIO);
    }

    public BastionData createHab(ServerLevel level, BlockPos pos, String team, String name) {
        return this.createStructure(level, pos, team, name, StructureKind.HAB);
    }

    public BastionData createStructure(ServerLevel level, BlockPos pos, String team, String name, StructureKind kind) {
        StructureKind structureKind;
        StructureKind structureKind2 = structureKind = kind == null ? StructureKind.RADIO : kind;
        if (structureKind == StructureKind.RADIO && !this.hasBastionCapacity(team)) {
            Espetro.LOGGER.warn("\u961f\u4f0d {} \u7684\u751f\u6548 Radio \u6570\u91cf\u5df2\u8fbe\u5230\u4e0a\u9650 {}\uff0c\u62d2\u7edd\u521b\u5efa: {} ({})", new Object[]{team, this.getBastionLimitPerTeam(), name, pos});
            return null;
        }
        if (structureKind == StructureKind.RADIO && this.wouldRadioCoverageOverlap(level, pos, team)) {
            Espetro.LOGGER.warn("Radio \u4f5c\u7528\u8303\u56f4\u4e0e\u73b0\u6709 Radio \u91cd\u53e0\uff0c\u62d2\u7edd\u521b\u5efa: {} ({})", (Object)name, (Object)pos);
            return null;
        }
        BastionData bastion = new BastionData(team, name, pos, level, structureKind);
        bastion.setArmorStandPosition(pos.m_7494_());
        if (structureKind == StructureKind.HAB) {
            bastion.setHabBuilt(true);
            long activationMs = Math.max(0L, (long)LogisticsConfig.get().habActivationSeconds * 1000L);
            bastion.setHabAvailableAt(activationMs == 0L ? 0L : System.currentTimeMillis() + activationMs);
        }
        if (!this.registerBastionRecord(bastion)) {
            Espetro.LOGGER.warn("\u7ed3\u6784\u8bb0\u5f55\u5931\u8d25\uff0c\u62d2\u7edd\u521b\u5efa: {} ({})", (Object)name, (Object)pos);
            return null;
        }
        bastion.setCoreHealth(this.armorStandHealth);
        if (structureKind == StructureKind.RADIO) {
            bastion.setArmorStandPosition(pos);
            this.radioBlockPositions.put(pos.m_7949_(), bastion.getBastionId());
        } else {
            ArmorStand armorStand = this.createCoreArmorStand(level, pos.m_7494_(), team, name);
            if (armorStand == null) {
                this.releaseBastionRecord(bastion);
                Espetro.LOGGER.error("\u65e0\u6cd5\u521b\u5efa\u76d4\u7532\u67b6\u5b9e\u4f53");
                return null;
            }
            level.m_7967_(armorStand);
            bastion.setArmorStandId(armorStand.m_20148_());
            this.registerCoreEntity(bastion);
            bastion.setArmorStandPosition(armorStand.m_20183_());
        }
        this.updateBastionArmorStandPosition(bastion, bastion.getArmorStandPosition());
        bastion.setActive(true);
        this.bastions.put(bastion.getBastionId(), bastion);
        EspetroAPI.markTacticalMapStateDirty();
        this.recomputeHabCoverage();
        Espetro.LOGGER.info("\u521b\u5efa{}: {} (\u961f\u4f0d: {}, \u7f16\u53f7: {}, \u6838\u5fc3\u4f4d\u7f6e: {})", new Object[]{structureKind, name, team, bastion.getBastionNumber(), bastion.getArmorStandPosition()});
        MinecraftForge.EVENT_BUS.post((Event)new BastionLifecycleEvent.Built(bastion));
        return bastion;
    }

    @Nullable
    public BastionData findRadioByBlockPos(BlockPos pos) {
        UUID id = this.radioBlockPositions.get(pos);
        if (id != null) {
            BastionData data = this.bastions.get(id);
            if (data != null && data.isRadio()) {
                return data;
            }
            this.radioBlockPositions.remove(pos);
        }
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isRadio() || !bastion.isActive() || !bastion.getPosition().equals(pos)) continue;
            this.radioBlockPositions.put(pos.m_7949_(), bastion.getBastionId());
            return bastion;
        }
        return null;
    }

    public void releaseRadioBlockRecord(BastionData bastion) {
        if (bastion != null) {
            this.radioBlockPositions.remove(bastion.getPosition());
        }
    }

    public void retrieveRadio(BastionData bastion, UUID retrieverId) {
        if (bastion == null || !bastion.isRadio()) {
            return;
        }
        this.releaseRadioBlockRecord(bastion);
        this.releaseBastionRecord(bastion);
        bastion.setActive(false);
        this.bastions.remove(bastion.getBastionId());
        if (retrieverId != null) {
            this.bastionCooldowns.remove(retrieverId);
        }
        Espetro.LOGGER.info("Radio {} \u88ab {} \u6536\u8d77", (Object)bastion.getName(), (Object)retrieverId);
        this.recomputeHabCoverage();
    }

    public void recomputeHabCoverage() {
        double buildRadius = LogisticsConfig.get().radioBuildRadius;
        double radiusSq = buildRadius * buildRadius;
        ArrayList<BastionData> radios = new ArrayList<BastionData>();
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isActive() || !bastion.isRadio()) continue;
            radios.add(bastion);
        }
        for (BastionData hab : this.bastions.values()) {
            if (!hab.isHab() || !hab.isActive()) continue;
            boolean covered = false;
            for (BastionData radio : radios) {
                if (radio.getLevel() != hab.getLevel() || !Objects.equals(radio.getTeam(), hab.getTeam()) || !(radio.getPosition().m_123331_(hab.getPosition()) <= radiusSq)) continue;
                covered = true;
                break;
            }
            if (covered == hab.isHabCoveredCache()) continue;
            hab.setHabCoveredCache(covered);
            Espetro.broadcastToTeam(hab.getTeam(), covered ? "\u00a7a[\u5175\u7ad9] \u00a7e" + hab.getName() + " \u00a7a\u5df2\u6062\u590d Radio \u8986\u76d6\uff0c\u53ef\u4ee5\u590d\u6d3b\u3002" : "\u00a7c[\u5175\u7ad9] \u00a7e" + hab.getName() + " \u00a7c\u5931\u53bb Radio \u8986\u76d6\uff0c\u65e0\u6cd5\u590d\u6d3b\uff01");
        }
    }

    public List<BastionData> getTeamBastions(String team) {
        ArrayList<BastionData> result = new ArrayList<BastionData>(4);
        if (team == null) {
            return result;
        }
        for (BastionData bastion : this.bastions.values()) {
            if (bastion == null || !bastion.isActive() || !team.equals(bastion.getTeam()) || bastion.isRadio() && !bastion.isLegacyCombined() || !bastion.isHabBuilt() && !bastion.isLegacyCombined()) continue;
            if (!this.bastionRecordPositions.containsKey(bastion.getBastionId())) {
                this.registerBastionRecord(bastion);
            }
            if (this.getRecordedArmorStandPosition(bastion) == null) continue;
            result.add(bastion);
        }
        result.sort(Comparator.comparing(BastionData::getName));
        return result;
    }

    public List<BastionData> getTeamOperationalBastions(String team) {
        ArrayList<BastionData> result = new ArrayList<BastionData>();
        for (BastionData bastion : this.getTeamBastions(team)) {
            if (!this.isHabOperational(bastion)) continue;
            result.add(bastion);
        }
        return result;
    }

    public List<BastionData> getAllBastions() {
        return new ArrayList<BastionData>(this.bastions.values());
    }

    @Nullable
    public BastionData findNearestBastion(ServerLevel level, BlockPos pos, @Nullable String team, double radius) {
        return this.findNearestRadio(level, pos, team, radius);
    }

    @Nullable
    public BastionData findNearestRadio(ServerLevel level, BlockPos pos, @Nullable String team, double radius) {
        BastionData nearest = null;
        double bestDistance = radius * radius;
        for (BastionData bastion : this.bastions.values()) {
            double distance;
            if (!bastion.isActive() || !bastion.isRadio() || bastion.getLevel() != level || team != null && !team.equals(bastion.getTeam()) || !((distance = bastion.getPosition().m_123331_(pos)) <= bestDistance)) continue;
            nearest = bastion;
            bestDistance = distance;
        }
        return nearest;
    }

    public boolean wouldRadioCoverageOverlap(ServerLevel level, BlockPos pos, String team) {
        if (level == null || pos == null) {
            return true;
        }
        LogisticsConfig.LogisticsSettings settings = LogisticsConfig.get();
        double separation = RadioCoveragePolicy.minimumCenterDistance(settings.radioBuildRadius, settings.radioExclusionRadius);
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isActive() || !bastion.isRadio() || bastion.getLevel() != level || !RadioCoveragePolicy.blocksPlacement(bastion.getTeam(), team, bastion.getPosition().m_123331_(pos), separation)) continue;
            return true;
        }
        return false;
    }

    public double getMinimumRadioCenterDistance() {
        LogisticsConfig.LogisticsSettings settings = LogisticsConfig.get();
        return RadioCoveragePolicy.minimumCenterDistance(settings.radioBuildRadius, settings.radioExclusionRadius);
    }

    public List<BastionData> findCoveringRadios(ServerLevel level, BlockPos pos, String team) {
        double buildRadius = LogisticsConfig.get().radioBuildRadius;
        double radiusSq = buildRadius * buildRadius;
        ArrayList<BastionData> covering = new ArrayList<BastionData>();
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isActive() || !bastion.isRadio() || bastion.getLevel() != level || !Objects.equals(team, bastion.getTeam()) || !(bastion.getPosition().m_123331_(pos) <= radiusSq)) continue;
            covering.add(bastion);
        }
        covering.sort(Comparator.comparingInt(BastionData::getConstructionSupplies).thenComparing(b -> b.getBastionId().toString()));
        return covering;
    }

    public boolean isInsideFriendlyRadioBuildRadius(ServerLevel level, BlockPos pos, String team) {
        return !this.findCoveringRadios(level, pos, team).isEmpty();
    }

    public boolean tryDebitConstructionFromCoveringRadios(ServerLevel level, BlockPos pos, String team, int amount) {
        if (amount <= 0) {
            return true;
        }
        List<BastionData> covering = this.findCoveringRadios(level, pos, team);
        if (covering.isEmpty()) {
            return false;
        }
        int total = 0;
        for (BastionData radio : covering) {
            if ((total += radio.getConstructionSupplies()) >= amount) break;
        }
        if (total < amount) {
            return false;
        }
        int remaining = amount;
        for (BastionData radio : covering) {
            if (remaining <= 0) break;
            int take = Math.min(remaining, radio.getConstructionSupplies());
            if (take <= 0 || !radio.consumeConstructionSupplies(take)) continue;
            remaining -= take;
        }
        return remaining <= 0;
    }

    public int sumConstructionInCoveringRadios(ServerLevel level, BlockPos pos, String team) {
        int total = 0;
        for (BastionData radio : this.findCoveringRadios(level, pos, team)) {
            total += radio.getConstructionSupplies();
        }
        return total;
    }

    public void advanceFobConstruction(BastionData bastion) {
    }

    public boolean tryConsumeFobAmmunition(BastionData bastion, int amount) {
        return bastion != null && bastion.isActive() && bastion.isRadio() && bastion.consumeAmmunitionSupplies(Math.max(0, amount));
    }

    public String getFobStatus(BastionData bastion) {
        if (bastion == null) {
            return "\u65e0\u6548";
        }
        if (bastion.isRadio() && !bastion.isLegacyCombined()) {
            return bastion.isAmmoCrateBuilt() ? "Radio \u5f39\u836f\u5e93\u5b58\u53ef\u7528\uff08\u5f39\u836f\u7bb1\u5df2\u5efa\uff09" : "Radio \u5e93\u5b58 " + bastion.getConstructionSupplies() + "/" + bastion.getAmmunitionSupplies() + "\uff08\u5f39\u836f\u7bb1\u9700\u624b\u52a8\u5efa\u9020\uff09";
        }
        if (!bastion.isActive()) {
            return "HAB \u5df2\u5931\u6548";
        }
        if (!bastion.isHabBuilt()) {
            return "HAB \u5f85\u5efa\u9020";
        }
        long now = System.currentTimeMillis();
        if (bastion.getHabAvailableAt() > now) {
            return "HAB \u542f\u7528\u4e2d " + (bastion.getHabAvailableAt() - now + 999L) / 1000L + "s";
        }
        if (bastion.getHabDisabledUntil() > now) {
            return "HAB \u88ab\u538b\u5236 " + (bastion.getHabDisabledUntil() - now + 999L) / 1000L + "s";
        }
        if (bastion.isHab() && !this.isCoveredByFriendlyRadio(bastion)) {
            return "HAB \u65e0 Radio \u8986\u76d6";
        }
        if (this.getRecordedArmorStandPosition(bastion) == null) {
            return "HAB \u5750\u6807\u7f3a\u5931";
        }
        return "HAB \u53ef\u90e8\u7f72";
    }

    public static boolean isDeployReadyStatus(String status) {
        return status != null && status.equals("HAB \u53ef\u90e8\u7f72");
    }

    public boolean isCoveredByFriendlyRadio(BastionData hab) {
        if (hab == null || !hab.isActive()) {
            return false;
        }
        if (hab.isRadio() && hab.isLegacyCombined()) {
            return true;
        }
        if (!hab.isHab()) {
            return false;
        }
        return hab.isHabCoveredCache();
    }

    @Nullable
    public BastionData findBastionByArmorStand(UUID armorStandId) {
        UUID bastionId = this.bastionIdsByArmorStand.get(armorStandId);
        if (bastionId != null) {
            BastionData bastion = this.bastions.get(bastionId);
            if (bastion != null) {
                return bastion;
            }
            this.bastionIdsByArmorStand.remove(armorStandId);
        }
        for (BastionData bastion : this.bastions.values()) {
            if (bastion.getArmorStandId() == null || !bastion.getArmorStandId().equals(armorStandId)) continue;
            this.registerCoreEntity(bastion);
            return bastion;
        }
        return null;
    }

    @Nullable
    public BastionData getBastion(UUID bastionId) {
        return this.bastions.get(bastionId);
    }

    public void setBastionActive(BastionData bastion, boolean active) {
        if (!active) {
            this.releaseBastionRecord(bastion);
            bastion.setActive(false);
            return;
        }
        if (!bastion.isActive() && bastion.isRadio() && !this.hasBastionCapacity(bastion.getTeam())) {
            bastion.setActive(false);
            Espetro.LOGGER.warn("Radio {} \u65e0\u6cd5\u91cd\u65b0\u6fc0\u6d3b\uff1a\u961f\u4f0d {} \u7684\u751f\u6548 Radio \u6570\u91cf\u5df2\u8fbe\u4e0a\u9650", (Object)bastion.getName(), (Object)bastion.getTeam());
            return;
        }
        if (!this.registerBastionRecord(bastion)) {
            bastion.setActive(false);
            Espetro.LOGGER.warn("\u5175\u7ad9 {} \u65e0\u6cd5\u91cd\u65b0\u6fc0\u6d3b\uff1a\u8bb0\u5f55\u5750\u6807\u5931\u8d25", (Object)bastion.getName());
            return;
        }
        bastion.setActive(active);
    }

    public void onCoreArmorStandDestroyed(BastionData bastion, @Nullable Entity attacker) {
        this.destroyBastion(bastion, attacker, false);
    }

    public void destroyBastion(BastionData bastion, @Nullable Entity attacker) {
        this.destroyBastion(bastion, attacker, true, null);
    }

    public void destroyBastionWithManpower(BastionData bastion, @Nullable Entity attacker, boolean deductManpower) {
        this.destroyBastion(bastion, attacker, true, deductManpower);
    }

    private void destroyBastion(BastionData bastion, @Nullable Entity attacker, boolean removeLoadedCoreEntity) {
        this.destroyBastion(bastion, attacker, removeLoadedCoreEntity, null, false);
    }

    private void destroyBastion(BastionData bastion, @Nullable Entity attacker, boolean removeLoadedCoreEntity, @Nullable Boolean manpowerOverride) {
        this.destroyBastion(bastion, attacker, removeLoadedCoreEntity, manpowerOverride, false);
    }

    private void destroyBastion(BastionData bastion, @Nullable Entity attacker, boolean removeLoadedCoreEntity, @Nullable Boolean manpowerOverride, boolean silent) {
        int penalty;
        if (bastion == null || !bastion.isActive()) {
            return;
        }
        String bastionName = bastion.getName();
        String bastionTeam = bastion.getTeam();
        boolean radio = bastion.isRadio();
        boolean deductManpower = !silent && (manpowerOverride != null ? manpowerOverride != false : radio);
        int n = penalty = deductManpower ? this.getDestroyTroopPenalty() : 0;
        if (removeLoadedCoreEntity) {
            this.removeCoreEntityIfLoaded(bastion, true);
            if (radio) {
                this.removeRadioBlockIfLoaded(bastion);
            }
        }
        if (radio) {
            this.releaseRadioBlockRecord(bastion);
        }
        this.setBastionActive(bastion, false);
        if (radio && !silent) {
            this.recomputeHabCoverage();
        }
        if (!silent) {
            String attackerName;
            String string = attackerName = attacker == null ? "unknown" : attacker.m_7755_().getString();
            if (deductManpower) {
                TroopCountManager troopManager = TroopCountManager.getInstance();
                if ("ATTACK".equals(bastionTeam)) {
                    troopManager.modifyAttackTroops(-penalty);
                } else {
                    troopManager.modifyDefendTroops(-penalty);
                }
                Espetro.LOGGER.info("Radio {} \u88ab\u6467\u6bc1\uff01\u653b\u51fb\u8005={} \u6263\u5175\u529b={}", new Object[]{bastionName, attackerName, penalty});
                Espetro.broadcastToTeam(bastionTeam, "\u00a7c[Radio] \u00a7e" + bastionName + " \u00a7c\u5df2\u88ab\u6467\u6bc1\uff01- " + penalty + " \u5175\u529b");
            } else if (radio) {
                Espetro.LOGGER.info("Radio {} \u88ab\u62c6\u9664\uff08\u4e0d\u6263\u5175\u529b\uff09\u653b\u51fb\u8005={}", (Object)bastionName, (Object)attackerName);
                Espetro.broadcastToTeam(bastionTeam, "\u00a7e[Radio] \u00a76" + bastionName + " \u00a7e\u5df2\u62c6\u9664\uff08\u4e0d\u6263\u5175\u529b\uff09\u3002");
            } else {
                Espetro.LOGGER.info("\u5175\u7ad9 HAB {} \u88ab\u6467\u6bc1\uff01\u653b\u51fb\u8005={}\uff08\u4e0d\u6263\u5175\u529b\uff09", (Object)bastionName, (Object)attackerName);
                Espetro.broadcastToTeam(bastionTeam, "\u00a7c[\u5175\u7ad9] \u00a7e" + bastionName + " \u00a7c\u5df2\u88ab\u6467\u6bc1\uff01\u65e0\u6cd5\u518d\u4ece\u6b64\u70b9\u590d\u6d3b\uff08\u4e0d\u6263\u5175\u529b\uff09\u3002");
            }
            ServerPlayer commander = this.findCommanderForTeam(bastionTeam);
            if (commander != null) {
                commander.m_213846_(Component.m_237113_(deductManpower ? "\u00a7c\u4f60\u7684 Radio \u00a7e" + bastionName + " \u00a7c\u5df2\u88ab\u6467\u6bc1\uff01" : (radio ? "\u00a7e\u4f60\u7684 Radio \u00a76" + bastionName + " \u00a7e\u5df2\u62c6\u9664\u3002" : "\u00a7c\u4f60\u7684\u5175\u7ad9 \u00a7e" + bastionName + " \u00a7c\u5df2\u88ab\u6467\u6bc1\uff01")));
            }
        }
        MinecraftForge.EVENT_BUS.post((Event)new BastionLifecycleEvent.Destroyed(bastion, attacker, deductManpower, penalty));
    }

    public int destroyAllBastionsForMatchEnd() {
        HabChannelManager.getInstance().reset();
        ArrayList<BastionData> snapshot = new ArrayList<BastionData>(this.bastions.values());
        int destroyed = 0;
        for (BastionData bastion : snapshot) {
            if (bastion == null || !bastion.isActive()) continue;
            this.destroyBastion(bastion, null, true, false, true);
            ++destroyed;
        }
        this.reset(false);
        if (destroyed > 0) {
            Espetro.LOGGER.info("\u6218\u5c40\u7ed3\u675f/\u56de\u57ce\uff1a\u5df2\u6467\u6bc1\u5168\u90e8\u5175\u7ad9 {} \u4e2a\uff08\u4e0d\u6263\u5175\u529b\uff0c\u672a\u5f3a\u52a0\u8f7d\u533a\u5757\uff09", (Object)destroyed);
        }
        return destroyed;
    }

    private void removeRadioBlockIfLoaded(BastionData bastion) {
        if (bastion == null || BastionItems.RADIO_BLOCK == null) {
            return;
        }
        ServerLevel level = bastion.getLevel();
        BlockPos pos = bastion.getPosition();
        if (level == null || pos == null || !this.isChunkLoaded(level, pos)) {
            return;
        }
        if (level.m_8055_(pos).m_60713_(BastionItems.RADIO_BLOCK)) {
            level.m_7731_(pos, Blocks.f_50016_.m_49966_(), 3);
        }
    }

    public boolean teleportPlayerToHab(ServerPlayer player, BastionData bastion) {
        ArmorStand stand;
        Entity entity;
        ArmorStand stand2;
        Entity entity2;
        if (player == null || bastion == null) {
            return false;
        }
        ServerLevel level = bastion.getLevel();
        if (level == null) {
            return false;
        }
        UUID standId = bastion.getArmorStandId();
        if (standId != null && (entity2 = level.m_8791_(standId)) instanceof ArmorStand && (stand2 = (ArmorStand)entity2).m_6084_()) {
            player.m_8999_(level, stand2.m_20185_(), stand2.m_20186_(), stand2.m_20189_(), stand2.m_146908_(), stand2.m_146909_());
            this.updateBastionArmorStandPosition(bastion, stand2.m_20183_());
            return true;
        }
        BlockPos targetPos = this.getRecordedArmorStandPosition(bastion);
        if (targetPos == null) {
            return false;
        }
        if (!this.isChunkLoaded(level, targetPos)) {
            return false;
        }
        if (standId != null && (entity = level.m_8791_(standId)) instanceof ArmorStand && (stand = (ArmorStand)entity).m_6084_()) {
            player.m_8999_(level, stand.m_20185_(), stand.m_20186_(), stand.m_20189_(), stand.m_146908_(), stand.m_146909_());
            this.updateBastionArmorStandPosition(bastion, stand.m_20183_());
            return true;
        }
        player.m_8999_(level, (double)targetPos.m_123341_() + 0.5, targetPos.m_123342_(), (double)targetPos.m_123343_() + 0.5, player.m_146908_(), player.m_146909_());
        return true;
    }

    public boolean teleportPlayerToHabAsync(ServerPlayer player, BastionData bastion, Consumer<Boolean> completion) {
        if (player == null || bastion == null || completion == null) {
            return false;
        }
        if (!this.pendingHabTeleports.add(player.m_20148_())) {
            return false;
        }
        ServerLevel level = bastion.getLevel();
        BlockPos targetPos = this.getRecordedArmorStandPosition(bastion);
        if (level == null || targetPos == null) {
            this.pendingHabTeleports.remove(player.m_20148_());
            completion.accept(false);
            return true;
        }
        if (this.isChunkLoaded(level, targetPos)) {
            level.m_7654_().execute(() -> {
                this.pendingHabTeleports.remove(player.m_20148_());
                boolean valid = this.isWaitingForBastion(player.m_20148_()) && bastion.isActive() && this.isHabOperational(bastion, false);
                completion.accept(valid && this.teleportPlayerToHab(player, bastion));
            });
            return true;
        }
        ChunkPos chunk = new ChunkPos(targetPos);
        HabChunkKey key = new HabChunkKey(level, chunk);
        CompletableFuture future = this.pendingHabChunkLoads.computeIfAbsent(key, ignored -> {
            level.m_7726_().m_8387_(TicketType.f_9447_, chunk, 1, targetPos);
            CompletableFuture<Boolean> created = ((CompletableFuture)level.m_7726_().m_8431_(chunk.f_45578_, chunk.f_45579_, ChunkStatus.f_62326_, true).thenApply(result -> result != null && result.left().isPresent())).completeOnTimeout(false, 10L, TimeUnit.SECONDS);
            created.whenComplete((success, error) -> level.m_7654_().execute(() -> {
                this.pendingHabChunkLoads.remove(key, created);
                level.m_7726_().m_8438_(TicketType.f_9447_, chunk, 1, targetPos);
            }));
            return created;
        });
        future.whenComplete((loaded, error) -> level.m_7654_().execute(() -> {
            this.pendingHabTeleports.remove(player.m_20148_());
            boolean valid = error == null && Boolean.TRUE.equals(loaded) && player.f_8906_ != null && this.isWaitingForBastion(player.m_20148_()) && bastion.isActive() && this.isHabOperational(bastion, false);
            completion.accept(valid && this.teleportPlayerToHab(player, bastion));
        }));
        return true;
    }

    public boolean isHabTeleportPending(UUID playerId) {
        return this.pendingHabTeleports.contains(playerId);
    }

    public boolean selectBastion(ServerLevel level, UUID playerId, UUID bastionId) {
        BastionData bastion = this.bastions.get(bastionId);
        if (bastion == null || !bastion.isActive()) {
            return false;
        }
        if (!this.waitingPlayers.containsKey(playerId)) {
            return false;
        }
        this.clearWaiting(playerId);
        return true;
    }

    public void onPlayerDeath(ServerLevel level, UUID playerId) {
        this.waitingPlayers.put(playerId, UUID.randomUUID());
        this.deathWaitingPlayers.add(playerId);
    }

    public boolean isWaitingForBastion(UUID playerId) {
        return this.waitingPlayers.containsKey(playerId);
    }

    public boolean isDeathWaiting(UUID playerId) {
        return this.deathWaitingPlayers.contains(playerId);
    }

    public void clearWaiting(UUID playerId) {
        this.waitingPlayers.remove(playerId);
        this.deathWaitingPlayers.remove(playerId);
        this.pendingHabTeleports.remove(playerId);
        this.unlockPlayerPosition(playerId);
    }

    public void lockPlayerPosition(UUID playerId, Vec3 pos) {
        this.playerLockPositions.put(playerId, pos);
    }

    public void unlockPlayerPosition(UUID playerId) {
        this.playerLockPositions.remove(playerId);
    }

    @Nullable
    public Vec3 getPlayerLockPosition(UUID playerId) {
        return this.playerLockPositions.get(playerId);
    }

    public void removeInvalidBastions() {
        Iterator<BastionData> iterator = this.bastions.values().iterator();
        while (iterator.hasNext()) {
            ArmorStand stand;
            Entity entity;
            BastionData bastion = iterator.next();
            if (!bastion.isActive()) {
                iterator.remove();
                this.unregisterCoreEntity(bastion);
                continue;
            }
            if (bastion.getArmorStandId() == null || bastion.getLevel() == null || !bastion.isChunkLoaded() || !((entity = bastion.getLevel().m_8791_(bastion.getArmorStandId())) instanceof ArmorStand) || !(stand = (ArmorStand)entity).m_6084_()) continue;
            this.updateBastionArmorStandPosition(bastion, stand.m_20183_());
        }
        this.destroyRadiosMissingCoreInArea(null, null, Double.POSITIVE_INFINITY, null);
    }

    public int destroyRadiosMissingCoreInArea(@Nullable ServerLevel level, @Nullable Vec3 center, double radius, @Nullable Entity attacker) {
        double radiusSq = radius * radius;
        ArrayList<BastionData> missing = new ArrayList<BastionData>();
        for (BastionData bastion : this.bastions.values()) {
            if (bastion == null || !bastion.isActive() || !bastion.isRadio() || level != null && bastion.getLevel() != level) continue;
            BlockPos pos = bastion.getPosition();
            ServerLevel bastionLevel = bastion.getLevel();
            if (pos == null || bastionLevel == null || !bastion.isChunkLoaded() || center != null && pos.m_203193_(center) > radiusSq || BastionItems.RADIO_BLOCK != null && bastionLevel.m_8055_(pos).m_60713_(BastionItems.RADIO_BLOCK)) continue;
            missing.add(bastion);
        }
        for (BastionData bastion : missing) {
            this.destroyBastionWithManpower(bastion, attacker, true);
        }
        return missing.size();
    }

    public void activatePlayerBastionSelection(UUID playerId) {
        this.waitingPlayers.put(playerId, UUID.randomUUID());
        this.deathWaitingPlayers.remove(playerId);
    }

    public void reset() {
        this.reset(true);
    }

    public void clearRuntimeState() {
        this.reset(false);
    }

    private void reset(boolean removeLoadedEntities) {
        BastionEventHandler.clearRadioDismantleAttempts();
        for (BastionData bastion : this.bastions.values()) {
            if (removeLoadedEntities) {
                this.removeCoreEntityIfLoaded(bastion, false);
            }
            this.releaseBastionRecord(bastion);
        }
        this.bastions.clear();
        this.bastionIdsByArmorStand.clear();
        this.radioBlockPositions.clear();
        this.waitingPlayers.clear();
        this.pendingHabChunkLoads.clear();
        this.pendingHabTeleports.clear();
        this.deathWaitingPlayers.clear();
        this.playerDeployPoints.clear();
        this.playerLockPositions.clear();
        this.bastionCooldowns.clear();
        this.resupplyCooldowns.clear();
        this.habProxyCache.clear();
        this.derivedTacticalStates.clear();
        this.lastDerivedTacticalTick = -4611686018427387904L;
        this.clearBastionRecords();
        EspetroAPI.markTacticalMapStateDirty();
    }

    private void removeCoreEntityIfLoaded(BastionData bastion, boolean kill) {
        if (bastion == null || bastion.getArmorStandId() == null) {
            return;
        }
        BlockPos entityPos = bastion.getArmorStandPosition();
        if (entityPos == null) {
            BlockPos blockPos = entityPos = bastion.getPosition() != null ? bastion.getPosition().m_7494_() : null;
        }
        if (entityPos == null) {
            return;
        }
        ServerLevel level = bastion.getLevel();
        if (level == null || !this.isChunkLoaded(level, entityPos)) {
            return;
        }
        Entity entity = level.m_8791_(bastion.getArmorStandId());
        if (entity == null) {
            return;
        }
        entity.m_146870_();
    }

    public int getActiveBastionCount() {
        int count = 0;
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isActive() || !bastion.isRadio()) continue;
            ++count;
        }
        return count;
    }

    public int getActiveBastionCount(String team) {
        int count = 0;
        for (BastionData bastion : this.bastions.values()) {
            if (!bastion.isActive() || !bastion.isRadio() || !Objects.equals(team, bastion.getTeam())) continue;
            ++count;
        }
        return count;
    }

    @Deprecated
    public boolean hasBastionCapacity() {
        return this.getActiveBastionCount() < 4;
    }

    public boolean hasBastionCapacity(String team) {
        return this.getActiveBastionCount(team) < this.getBastionLimitPerTeam();
    }

    public int getBastionLimitPerTeam() {
        int configured = LogisticsConfig.get().getRadio().maxActivePerTeam;
        return configured >= 0 ? configured : 4;
    }

    @Nullable
    public BlockPos getRecordedArmorStandPosition(BastionData bastion) {
        BlockPos recordPos = this.bastionRecordPositions.get(bastion.getBastionId());
        if (recordPos != null) {
            return recordPos;
        }
        BlockPos recorded = bastion.getArmorStandPosition();
        return recorded != null ? recorded : bastion.getPosition().m_7494_();
    }

    public void updateBastionArmorStandPosition(BastionData bastion, BlockPos pos) {
        bastion.setArmorStandPosition(pos);
        this.bastionRecordPositions.put(bastion.getBastionId(), pos);
    }

    private boolean isBastionUsable(BastionData bastion) {
        BlockPos armorStandPos;
        if (!bastion.isActive()) {
            return false;
        }
        if (bastion.isRadio() && !bastion.isLegacyCombined()) {
            return false;
        }
        if (!this.bastionRecordPositions.containsKey(bastion.getBastionId()) && !this.registerBastionRecord(bastion)) {
            return false;
        }
        if (bastion.checkArmorStand() && (armorStandPos = bastion.getArmorStandPosition()) != null) {
            this.updateBastionArmorStandPosition(bastion, armorStandPos);
        }
        return this.getRecordedArmorStandPosition(bastion) != null && this.isHabOperational(bastion);
    }

    public boolean isHabOperational(BastionData bastion) {
        return this.isHabOperational(bastion, false);
    }

    public boolean isHabOperational(BastionData bastion, boolean applySuppression) {
        if (bastion == null || !bastion.isActive()) {
            return false;
        }
        if (bastion.isRadio() && !bastion.isLegacyCombined()) {
            return false;
        }
        if (!bastion.isHabBuilt()) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (bastion.getHabAvailableAt() > now) {
            return false;
        }
        if (bastion.isHab() && !this.isCoveredByFriendlyRadio(bastion)) {
            return false;
        }
        if (!bastion.isHab()) {
            float maximum = Math.max(1.0f, (float)this.armorStandHealth);
            float healthPercent = bastion.getCoreHealth() * 100.0f / maximum;
            if (healthPercent <= (float)LogisticsConfig.get().habDisableRadioHealth) {
                if (applySuppression) {
                    bastion.setHabDisabledUntil(now + (long)LogisticsConfig.get().habReactivationSeconds * 1000L);
                }
                return false;
            }
        }
        if (this.isHabProxied(bastion)) {
            if (applySuppression) {
                bastion.setHabDisabledUntil(now + (long)LogisticsConfig.get().habReactivationSeconds * 1000L);
            }
            return false;
        }
        return bastion.getHabDisabledUntil() <= now;
    }

    private boolean isHabProxied(BastionData bastion) {
        ServerLevel level = bastion.getLevel();
        if (level == null) {
            return false;
        }
        long gameTime = level.m_46467_();
        UUID id = bastion.getBastionId();
        long[] cached = this.habProxyCache.get(id);
        if (cached != null && gameTime - cached[0] <= 30L) {
            return cached[1] != 0L;
        }
        BlockPos center = bastion.getPosition();
        int[] radii = new int[]{20, 30, 40, 50, 60, 70, 80, 90};
        long maxR2 = (long)radii[radii.length - 1] * (long)radii[radii.length - 1];
        int[] counts = new int[radii.length];
        String habTeam = bastion.getTeam();
        boolean proxied = false;
        for (ServerPlayer player : level.m_6907_()) {
            double distSq;
            if (!player.m_6084_() || player.m_5833_() || Objects.equals(habTeam, Espetro.getPlayerTeam(player)) || (distSq = player.m_20183_().m_123331_(center)) > (double)maxR2) continue;
            for (int index = 0; index < radii.length; ++index) {
                long r = radii[index];
                if (!(distSq <= (double)(r * r))) continue;
                int n = index;
                counts[n] = counts[n] + 1;
                if (counts[index] < index + 2) continue;
                proxied = true;
                break;
            }
            if (!proxied) continue;
            break;
        }
        this.habProxyCache.put(id, new long[]{gameTime, proxied ? 1L : 0L});
        return proxied;
    }

    private boolean isChunkLoaded(ServerLevel level, BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return level.m_7726_().m_5563_(chunkPos.f_45578_, chunkPos.f_45579_);
    }

    @Nullable
    private ArmorStand createCoreArmorStand(ServerLevel level, BlockPos corePos, String team, String name) {
        ArmorStand armorStand = EntityType.f_20529_.m_20615_(level);
        if (armorStand == null) {
            return null;
        }
        armorStand.m_6034_((double)corePos.m_123341_() + 0.5, corePos.m_123342_(), (double)corePos.m_123343_() + 0.5);
        armorStand.m_6593_(Component.m_237113_(name));
        armorStand.m_20340_(false);
        this.syncCoreArmorStand(armorStand);
        armorStand.m_21153_(this.armorStandHealth);
        ItemStack helmet = new ItemStack(Items.f_42407_);
        CompoundTag displayTag = new CompoundTag();
        displayTag.m_128405_("color", "ATTACK".equals(team) ? 0xAA0000 : 170);
        CompoundTag tag = new CompoundTag();
        tag.m_128365_("display", displayTag);
        helmet.m_41751_(tag);
        armorStand.m_8061_(EquipmentSlot.HEAD, helmet);
        return armorStand;
    }

    void syncCoreArmorStand(ArmorStand armorStand) {
        AttributeInstance maxHealth = armorStand.m_21051_(Attributes.f_22276_);
        if (maxHealth != null && maxHealth.m_22115_() != (double)this.armorStandHealth) {
            maxHealth.m_22100_(this.armorStandHealth);
        }
        armorStand.m_20331_(false);
        armorStand.m_20225_(true);
        armorStand.m_20049_("bastion_armor_stand");
    }

    @Nullable
    private ServerPlayer findCommanderForTeam(String team) {
        UUID commanderId;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return null;
        }
        VoteManager voteManager = VoteManager.getInstance();
        UUID uUID = commanderId = "ATTACK".equals(team) ? voteManager.getAttackCommander() : voteManager.getDefendCommander();
        if (commanderId != null) {
            return server.m_6846_().m_11259_(commanderId);
        }
        return null;
    }

    private boolean registerBastionRecord(BastionData bastion) {
        BlockPos armorStandPos = bastion.getArmorStandPosition();
        if (armorStandPos == null) {
            armorStandPos = bastion.getPosition().m_7494_();
            bastion.setArmorStandPosition(armorStandPos);
        }
        this.bastionRecordPositions.put(bastion.getBastionId(), armorStandPos);
        bastion.setBastionNumber(this.findAvailableBastionNumber(bastion));
        return true;
    }

    private void releaseBastionRecord(BastionData bastion) {
        this.unregisterCoreEntity(bastion);
        this.bastionRecordPositions.remove(bastion.getBastionId());
        Espetro.LOGGER.debug("\u91ca\u653e\u5175\u7ad9\u8bb0\u5f55: {}", (Object)bastion.getName());
        bastion.setBastionNumber(-1);
        bastion.clearArmorStandPosition();
        bastion.setArmorStandId(null);
    }

    private void clearBastionRecords() {
        this.bastionRecordPositions.clear();
    }

    private void registerCoreEntity(BastionData bastion) {
        UUID armorStandId = bastion.getArmorStandId();
        if (armorStandId != null) {
            this.bastionIdsByArmorStand.put(armorStandId, bastion.getBastionId());
        }
    }

    private void unregisterCoreEntity(BastionData bastion) {
        UUID armorStandId = bastion.getArmorStandId();
        if (armorStandId != null) {
            this.bastionIdsByArmorStand.remove(armorStandId);
        }
    }

    private int findAvailableBastionNumber(BastionData bastion) {
        boolean[] used = new boolean[5];
        for (BastionData other : this.bastions.values()) {
            int number;
            if (other.getBastionId().equals(bastion.getBastionId()) || !other.isActive() || !Objects.equals(other.getTeam(), bastion.getTeam()) || (number = other.getBastionNumber()) < 1 || number > 4) continue;
            used[number] = true;
        }
        int savedNumber = bastion.getBastionNumber();
        if (savedNumber >= 1 && savedNumber <= 4 && !used[savedNumber]) {
            return savedNumber;
        }
        for (int number = 1; number <= 4; ++number) {
            if (used[number]) continue;
            return number;
        }
        return 4;
    }

    public int getBastionCooldownRemaining(UUID playerId) {
        return this.getBastionCooldownRemaining(playerId, this.cooldownSeconds);
    }

    public int getBastionCooldownRemaining(UUID playerId, int effectiveCooldownSeconds) {
        Long lastUse = this.bastionCooldowns.get(playerId);
        if (lastUse == null) {
            return 0;
        }
        long elapsed = System.currentTimeMillis() - lastUse;
        int remaining = (int)(((long)Math.max(0, effectiveCooldownSeconds) * 1000L - elapsed) / 1000L);
        return Math.max(0, remaining);
    }

    public void setBastionCooldown(UUID playerId) {
        this.bastionCooldowns.put(playerId, System.currentTimeMillis());
    }

    @Nullable
    public String canBuildBastion(UUID playerId) {
        return this.canBuildBastion(playerId, this.cooldownSeconds);
    }

    @Nullable
    public String canBuildBastion(UUID playerId, int effectiveCooldownSeconds) {
        int remaining = this.getBastionCooldownRemaining(playerId, effectiveCooldownSeconds);
        if (remaining > 0) {
            return "\u00a7c\u5175\u7ad9\u5efa\u9020\u51b7\u5374\u4e2d\uff01\u8bf7\u7b49\u5f85 " + remaining + " \u79d2\u540e\u518d\u8bd5\u3002";
        }
        return null;
    }

    @Nullable
    public String tryResupply(UUID playerId) {
        return null;
    }

    public void recordResupply(UUID playerId) {
    }

    public int getResupplyCooldownRemaining(UUID playerId) {
        return 0;
    }

    @Nullable
    public BastionData findBastionByShulkerPos(BlockPos pos) {
        for (BastionData b : this.bastions.values()) {
            if (!b.isActive() || !pos.equals(b.getShulkerPos())) continue;
            return b;
        }
        return null;
    }

    public void savePlayerDeployPoint(ServerPlayer player) {
        BlockPos bedPos = player.m_8961_();
        ServerLevel level = player.m_284548_();
        BlockPos spawnPos = level.m_220360_();
        BlockPos deployPos = bedPos != null ? bedPos : spawnPos;
        this.playerDeployPoints.put(player.m_20148_(), new DeployPoint(deployPos, level));
        EspetroAPI.markTacticalMapStateDirty();
    }

    public void savePlayerDeployPoint(ServerPlayer player, BlockPos pos, ServerLevel level) {
        this.playerDeployPoints.put(player.m_20148_(), new DeployPoint(pos, level));
        EspetroAPI.markTacticalMapStateDirty();
    }

    @Nullable
    public DeployPoint getPlayerDeployPoint(UUID playerId) {
        return this.playerDeployPoints.get(playerId);
    }

    public List<PlayerDeployPointSnapshot> getPlayerDeployPointSnapshots() {
        return this.playerDeployPoints.entrySet().stream().filter(entry -> entry.getValue() != null && ((DeployPoint)entry.getValue()).pos != null && ((DeployPoint)entry.getValue()).level != null).map(entry -> new PlayerDeployPointSnapshot((UUID)entry.getKey(), ((DeployPoint)entry.getValue()).level.m_46472_().m_135782_().toString(), ((DeployPoint)entry.getValue()).pos.m_123341_(), ((DeployPoint)entry.getValue()).pos.m_123342_(), ((DeployPoint)entry.getValue()).pos.m_123343_())).sorted(Comparator.comparing(snapshot -> snapshot.playerId().toString())).toList();
    }

    public boolean respawnAtDeployPoint(ServerLevel level, ServerPlayer player) {
        DeployPoint deployPoint = this.playerDeployPoints.get(player.m_20148_());
        if (deployPoint == null) {
            return false;
        }
        TeamPackManager.getInstance().cancelPendingRespawn(player.m_20148_());
        player.m_8999_(deployPoint.level, (double)deployPoint.pos.m_123341_() + 0.5, (double)deployPoint.pos.m_123342_() + 0.1, (double)deployPoint.pos.m_123343_() + 0.5, 0.0f, 0.0f);
        this.clearWaiting(player.m_20148_());
        player.m_143403_(GameType.SURVIVAL);
        player.m_21219_();
        int invincibilityTicks = GameConfig.getRespawnInvincibilityTicks();
        player.m_7292_(new MobEffectInstance(MobEffects.f_19606_, invincibilityTicks, 127, false, false, false));
        GameStateManager.getInstance().applyBattlefieldMiningRestriction(player);
        player.m_213846_(Component.m_237113_("\u00a7a\u5df2\u5728\u539f\u90e8\u7f72\u70b9\u590d\u6d3b\uff01"));
        return true;
    }

    private record DerivedTacticalState(boolean habOperational, boolean activationReady, boolean suppressionWindowActive, boolean proximitySuppressed, boolean radioCovered) {
    }

    private record HabChunkKey(ServerLevel level, ChunkPos chunk) {
    }

    public static class DeployPoint {
        public final BlockPos pos;
        public final ServerLevel level;

        public DeployPoint(BlockPos pos, ServerLevel level) {
            this.pos = pos;
            this.level = level;
        }
    }

    public record PlayerDeployPointSnapshot(UUID playerId, String dimension, int x, int y, int z) {
    }
}

