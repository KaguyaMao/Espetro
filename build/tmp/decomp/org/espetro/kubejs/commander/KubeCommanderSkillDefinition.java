/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.kubejs.commander;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.espetro.kubejs.commander.SkillUserRole;

public final class KubeCommanderSkillDefinition {
    private final String id;
    private final String displayName;
    private final String description;
    private final String stats;
    private final String icon;
    private final String trigger;
    private final int cooldownSeconds;
    private final boolean enabled;
    private final Set<SkillUserRole> allowedRoles;

    public KubeCommanderSkillDefinition(String id, String displayName, String description, String stats, String icon, String trigger, int cooldownSeconds, boolean enabled) {
        this(id, displayName, description, stats, icon, trigger, cooldownSeconds, enabled, SkillUserRole.defaultRoles());
    }

    public KubeCommanderSkillDefinition(String id, String displayName, String description, String stats, String icon, String trigger, int cooldownSeconds, boolean enabled, Set<SkillUserRole> allowedRoles) {
        this.id = KubeCommanderSkillDefinition.sanitizeId(id);
        this.displayName = displayName == null || displayName.isBlank() ? this.id : displayName;
        this.description = description == null ? "" : description;
        this.stats = stats == null ? "" : stats;
        this.icon = icon == null ? "" : icon.trim();
        this.trigger = trigger == null || trigger.isBlank() ? "activate" : trigger.trim().toLowerCase(Locale.ROOT);
        this.cooldownSeconds = Math.max(0, cooldownSeconds);
        this.enabled = enabled;
        LinkedHashSet<SkillUserRole> roles = allowedRoles == null || allowedRoles.isEmpty() ? SkillUserRole.defaultRoles() : new LinkedHashSet<SkillUserRole>(allowedRoles);
        this.allowedRoles = Collections.unmodifiableSet(roles);
    }

    public String id() {
        return this.id;
    }

    public String getId() {
        return this.id;
    }

    public String displayName() {
        return this.displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String description() {
        return this.description;
    }

    public String getDescription() {
        return this.description;
    }

    public String stats() {
        return this.stats;
    }

    public String getStats() {
        return this.stats;
    }

    public String icon() {
        return this.icon;
    }

    public String getIcon() {
        return this.icon;
    }

    public String trigger() {
        return this.trigger;
    }

    public String getTrigger() {
        return this.trigger;
    }

    public int cooldownSeconds() {
        return this.cooldownSeconds;
    }

    public int getCooldownSeconds() {
        return this.cooldownSeconds;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Set<SkillUserRole> allowedRoles() {
        return this.allowedRoles;
    }

    public Set<SkillUserRole> getAllowedRoles() {
        return this.allowedRoles;
    }

    public boolean allows(SkillUserRole role) {
        return role != null && this.allowedRoles.contains((Object)role);
    }

    public boolean allowsCommander() {
        return this.allows(SkillUserRole.COMMANDER);
    }

    public boolean allowsSquadLeader() {
        return this.allows(SkillUserRole.SQUAD_LEADER);
    }

    public String allowedRolesWire() {
        return this.allowedRoles.stream().map(SkillUserRole::wireName).collect(Collectors.joining(","));
    }

    public boolean isTargetMapTrigger() {
        return "artillery_target".equals(this.trigger) || "target_map".equals(this.trigger);
    }

    public boolean isActivateTrigger() {
        return !this.isTargetMapTrigger();
    }

    private static String sanitizeId(String id) {
        return id == null ? "" : id.trim();
    }
}

