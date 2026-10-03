/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  com.google.gson.annotations.SerializedName
 */
package org.espetro.logistics;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.server.MinecraftServer;

public final class LogisticsConfig {
    private static final Gson GSON = new Gson();
    private static LogisticsSettings settings = new LogisticsSettings();

    private LogisticsConfig() {
    }

    public static LogisticsSettings get() {
        return settings;
    }

    public static void load(MinecraftServer server) {
    }

    public static void applyExternalJson(String rawJson) {
        LogisticsSettings loaded = new LogisticsSettings();
        JsonObject root = (JsonObject)GSON.fromJson(rawJson, JsonObject.class);
        if (root != null && root.has("logistics")) {
            loaded = (LogisticsSettings)GSON.fromJson(root.get("logistics"), LogisticsSettings.class);
        }
        loaded.normalize();
        settings = loaded;
    }

    public static class LogisticsSettings {
        @SerializedName(value="max_construction")
        public int maxConstruction = 20000;
        @SerializedName(value="max_ammunition")
        public int maxAmmunition = 20000;
        @SerializedName(value="pickup_cooldown_seconds")
        public int pickupCooldownSeconds = 5;
        @SerializedName(value="deposit_radius")
        public double depositRadius = 8.0;
        @SerializedName(value="radio_build_radius")
        public double radioBuildRadius = 150.0;
        @SerializedName(value="radio_exclusion_radius")
        public double radioExclusionRadius = 400.0;
        @SerializedName(value="radio_teammate_radius")
        public double radioTeammateRadius = 30.0;
        @SerializedName(value="radio_teammate_count")
        public int radioTeammateCount = 0;
        @SerializedName(value="require_teammate")
        public boolean requireTeammate = false;
        public RadioPlacementSettings radio;
        @SerializedName(value="hab_construction_cost")
        public int habConstructionCost = 500;
        @SerializedName(value="ammo_crate_construction_cost")
        public int ammoCrateConstructionCost = 100;
        @SerializedName(value="default_resupply_ammo_cost")
        public int defaultResupplyAmmoCost = 50;
        @SerializedName(value="hab_activation_seconds")
        public int habActivationSeconds = 0;
        @SerializedName(value="hab_reactivation_seconds")
        public int habReactivationSeconds = 30;
        @SerializedName(value="hab_disable_radio_health")
        public int habDisableRadioHealth = 75;
        public List<SupplySource> sources = new ArrayList<SupplySource>();

        private void normalize() {
            this.maxConstruction = Math.max(0, this.maxConstruction);
            this.maxAmmunition = Math.max(0, this.maxAmmunition);
            this.pickupCooldownSeconds = Math.max(0, this.pickupCooldownSeconds);
            this.depositRadius = Math.max(1.0, this.depositRadius);
            this.radioBuildRadius = Math.max(1.0, this.radioBuildRadius);
            this.radioExclusionRadius = Math.max(0.0, this.radioExclusionRadius);
            this.radioTeammateRadius = Math.max(0.0, this.radioTeammateRadius);
            int n = this.radioTeammateCount = this.requireTeammate ? Math.max(0, this.radioTeammateCount) : 0;
            if (this.radio == null) {
                this.radio = RadioPlacementSettings.fromLegacy(this);
            }
            this.radio.normalize();
            this.radioBuildRadius = this.radio.buildRadius;
            this.radioExclusionRadius = this.radio.exclusionRadius;
            this.radioTeammateRadius = this.radio.teammateRadius;
            this.radioTeammateCount = this.radio.teammateCount;
            this.requireTeammate = this.radio.teammateCount > 0;
            this.habConstructionCost = Math.max(0, this.habConstructionCost);
            this.ammoCrateConstructionCost = Math.max(0, this.ammoCrateConstructionCost);
            this.defaultResupplyAmmoCost = Math.max(0, this.defaultResupplyAmmoCost);
            this.habActivationSeconds = Math.max(0, this.habActivationSeconds);
            this.habReactivationSeconds = Math.max(0, this.habReactivationSeconds);
            if (this.sources == null) {
                this.sources = new ArrayList<SupplySource>();
            }
            this.sources.removeIf(Objects::isNull);
            for (SupplySource source : this.sources) {
                source.normalize();
            }
        }

        public RadioPlacementSettings getRadio() {
            if (this.radio == null) {
                this.radio = RadioPlacementSettings.fromLegacy(this);
                this.radio.normalize();
            }
            return this.radio;
        }
    }

    public static class SupplyItem {
        public String id;
        public String nbt;
        public int count = 1;
        @SerializedName(value="points_per_item")
        public int pointsPerItem = 1;
        @SerializedName(value="supply_id")
        public String supplyId;

        private void normalize() {
            this.count = Math.max(1, this.count);
            this.pointsPerItem = Math.max(1, this.pointsPerItem);
        }
    }

    public static class SourceLocation {
        public String dimension;
        public int[] position;
        public double radius = 0.5;
    }

    public static class SupplySource {
        public String id = "default";
        public String team;
        public List<String> blocks = new ArrayList<String>();
        @SerializedName(value="source_ids")
        public List<String> sourceIds = new ArrayList<String>();
        @SerializedName(value="block_entity_nbt")
        public String blockEntityNbt;
        public List<SourceLocation> locations = new ArrayList<SourceLocation>();
        public List<SupplyItem> construction = new ArrayList<SupplyItem>();
        public List<SupplyItem> ammunition = new ArrayList<SupplyItem>();

        private void normalize() {
            if (this.id == null || this.id.isBlank()) {
                this.id = "default";
            }
            if (this.blocks == null) {
                this.blocks = new ArrayList<String>();
            }
            if (this.sourceIds == null) {
                this.sourceIds = new ArrayList<String>();
            }
            if (this.locations == null) {
                this.locations = new ArrayList<SourceLocation>();
            }
            if (this.construction == null) {
                this.construction = new ArrayList<SupplyItem>();
            }
            if (this.ammunition == null) {
                this.ammunition = new ArrayList<SupplyItem>();
            }
            this.locations.removeIf(Objects::isNull);
            this.construction.removeIf(Objects::isNull);
            this.ammunition.removeIf(Objects::isNull);
            this.construction.forEach(SupplyItem::normalize);
            this.ammunition.forEach(SupplyItem::normalize);
        }
    }

    public static class RadioPlacementSettings {
        @SerializedName(value="allowed_phases")
        public List<String> allowedPhases = new ArrayList<String>(List.of("BATTLE"));
        @SerializedName(value="require_commander")
        public boolean requireCommander = false;
        @SerializedName(value="allow_squad_leader")
        public boolean allowSquadLeader = true;
        @SerializedName(value="cooldown_seconds")
        public int cooldownSeconds = -1;
        @SerializedName(value="required_planks")
        public int requiredPlanks = 0;
        @SerializedName(value="creative_bypasses_planks")
        public boolean creativeBypassesPlanks = true;
        @SerializedName(value="max_active_per_team")
        public int maxActivePerTeam = -1;
        @SerializedName(value="build_radius")
        public double buildRadius = 150.0;
        @SerializedName(value="require_target_block")
        public boolean requireTargetBlock = false;
        @SerializedName(value="exclusion_radius")
        public double exclusionRadius = 400.0;
        @SerializedName(value="teammate_count")
        public int teammateCount = 0;
        @SerializedName(value="teammate_radius")
        public double teammateRadius = 30.0;

        private static RadioPlacementSettings fromLegacy(LogisticsSettings legacy) {
            RadioPlacementSettings result = new RadioPlacementSettings();
            result.buildRadius = legacy.radioBuildRadius;
            result.exclusionRadius = legacy.radioExclusionRadius;
            result.teammateRadius = legacy.radioTeammateRadius;
            result.teammateCount = legacy.radioTeammateCount;
            return result;
        }

        private void normalize() {
            if (this.allowedPhases == null) {
                this.allowedPhases = new ArrayList<String>(List.of("BATTLE"));
            } else {
                this.allowedPhases.removeIf(phase -> phase == null || phase.isBlank());
                this.allowedPhases.replaceAll(phase -> phase.trim().toUpperCase(Locale.ROOT));
            }
            this.cooldownSeconds = Math.max(-1, this.cooldownSeconds);
            this.requiredPlanks = Math.max(-1, this.requiredPlanks);
            this.maxActivePerTeam = Math.max(-1, this.maxActivePerTeam);
            this.buildRadius = Math.max(0.0, this.buildRadius);
            this.exclusionRadius = Math.max(0.0, this.exclusionRadius);
            this.teammateCount = Math.max(0, this.teammateCount);
            this.teammateRadius = Math.max(0.0, this.teammateRadius);
        }

        public boolean allowsPhase(String phaseName) {
            return phaseName != null && this.allowedPhases.contains(phaseName.toUpperCase(Locale.ROOT));
        }
    }
}

