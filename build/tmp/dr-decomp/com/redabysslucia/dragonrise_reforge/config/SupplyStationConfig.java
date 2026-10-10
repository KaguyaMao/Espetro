/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 */
package com.redabysslucia.dragonrise_reforge.config;

import com.google.gson.annotations.SerializedName;
import java.util.HashMap;
import java.util.Map;

public class SupplyStationConfig {
    @SerializedName(value="Default")
    public ResupplyRule defaultRule = new ResupplyRule();
    @SerializedName(value="VehicleOverrides")
    public Map<String, ResupplyRule> vehicleOverrides = new HashMap<String, ResupplyRule>();

    public ResupplyRule getRuleForVehicle(String vehicleId) {
        ResupplyRule override = this.vehicleOverrides.get(vehicleId);
        if (override != null) {
            return override;
        }
        return this.defaultRule;
    }

    public String getEffectiveBonusItem(ResupplyRule vehicleRule) {
        if (vehicleRule.bonusItem != null && !vehicleRule.bonusItem.isEmpty()) {
            return vehicleRule.bonusItem;
        }
        if (vehicleRule != this.defaultRule && this.defaultRule.bonusItem != null && !this.defaultRule.bonusItem.isEmpty()) {
            return this.defaultRule.bonusItem;
        }
        return "";
    }

    public int getEffectiveBonusItemCount(ResupplyRule vehicleRule) {
        if (vehicleRule.bonusItem != null && !vehicleRule.bonusItem.isEmpty()) {
            return Math.max(1, vehicleRule.bonusItemCount);
        }
        if (vehicleRule != this.defaultRule && this.defaultRule.bonusItem != null && !this.defaultRule.bonusItem.isEmpty()) {
            return Math.max(1, this.defaultRule.bonusItemCount);
        }
        return 0;
    }

    public AmmoTypeRule getAmmoRule(ResupplyRule vehicleRule, String ammoKey) {
        AmmoTypeRule override = vehicleRule.ammoOverrides.get(ammoKey);
        if (override != null) {
            return override;
        }
        AmmoTypeRule defaultAmmoRule = new AmmoTypeRule();
        defaultAmmoRule.mode = vehicleRule.mode;
        defaultAmmoRule.fixedAmount = vehicleRule.fixedAmount;
        return defaultAmmoRule;
    }

    public boolean isMagazineMode(ResupplyRule rule, String ammoKey) {
        AmmoTypeRule ammoRule = this.getAmmoRule(rule, ammoKey);
        return "MAGAZINE".equalsIgnoreCase(ammoRule.mode);
    }

    public boolean isPackageBasedMode(ResupplyRule rule, String ammoKey) {
        AmmoTypeRule ammoRule = this.getAmmoRule(rule, ammoKey);
        return "PACKAGE".equalsIgnoreCase(ammoRule.mode);
    }

    public static class ResupplyRule {
        @SerializedName(value="Mode")
        public String mode = "MAGAZINE";
        @SerializedName(value="FixedAmount")
        public int fixedAmount = 100;
        @SerializedName(value="HealPercent")
        public float healPercent = 50.0f;
        @SerializedName(value="BonusItem")
        public String bonusItem = "";
        @SerializedName(value="BonusItemCount")
        public int bonusItemCount = 1;
        @SerializedName(value="AmmoOverrides")
        public Map<String, AmmoTypeRule> ammoOverrides = new HashMap<String, AmmoTypeRule>();
    }

    public static class AmmoTypeRule {
        @SerializedName(value="Mode")
        public String mode = "MAGAZINE";
        @SerializedName(value="FixedAmount")
        public int fixedAmount = 100;
        @SerializedName(value="CustomItem")
        public String customItem = "";
        @SerializedName(value="CustomItemCount")
        public int customItemCount = 1;
    }
}

