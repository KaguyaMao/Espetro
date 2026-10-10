/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.kubejs.commander;

import java.util.LinkedHashSet;
import java.util.Set;
import org.espetro.Espetro;
import org.espetro.kubejs.commander.EspetroCommanderSkills;
import org.espetro.kubejs.commander.KubeCommanderSkillDefinition;
import org.espetro.kubejs.commander.SkillUserRole;

public final class CommanderSkillBuilder {
    private final String id;
    private String displayName;
    private String description = "";
    private String stats = "";
    private String icon = "";
    private String trigger = "activate";
    private int cooldownSeconds = 60;
    private boolean enabled = true;
    private final Set<SkillUserRole> allowedRoles = new LinkedHashSet<SkillUserRole>();

    public CommanderSkillBuilder(String id) {
        this.displayName = this.id = id == null ? "" : id.trim();
    }

    public CommanderSkillBuilder displayName(String value) {
        this.displayName = value;
        return this;
    }

    public CommanderSkillBuilder name(String value) {
        return this.displayName(value);
    }

    public CommanderSkillBuilder description(String value) {
        this.description = value == null ? "" : value;
        return this;
    }

    public CommanderSkillBuilder stats(String value) {
        this.stats = value == null ? "" : value;
        return this;
    }

    public CommanderSkillBuilder icon(String value) {
        this.icon = value == null ? "" : value.trim();
        return this;
    }

    public CommanderSkillBuilder trigger(String value) {
        this.trigger = value == null || value.isBlank() ? "activate" : value.trim().toLowerCase();
        return this;
    }

    public CommanderSkillBuilder activate() {
        return this.trigger("activate");
    }

    public CommanderSkillBuilder targetMap() {
        return this.trigger("target_map");
    }

    public CommanderSkillBuilder artilleryTarget() {
        return this.trigger("artillery_target");
    }

    public CommanderSkillBuilder cooldownSeconds(int value) {
        this.cooldownSeconds = Math.max(0, value);
        return this;
    }

    public CommanderSkillBuilder cooldown(int value) {
        return this.cooldownSeconds(value);
    }

    public CommanderSkillBuilder enabled(boolean value) {
        this.enabled = value;
        return this;
    }

    public CommanderSkillBuilder disabled() {
        this.enabled = false;
        return this;
    }

    public CommanderSkillBuilder usableBy(Object ... roles) {
        Set<SkillUserRole> parsed = SkillUserRole.parseMany(roles);
        if (parsed.isEmpty()) {
            Espetro.LOGGER.warn("\u6280\u80fd {} usableBy \u65e0\u6709\u6548\u89d2\u8272\uff0c\u5c06\u56de\u9000\u4e3a\u4ec5\u6307\u6325\u5b98", (Object)this.id);
            return this;
        }
        this.allowedRoles.clear();
        this.allowedRoles.addAll(parsed);
        return this;
    }

    public CommanderSkillBuilder roles(Object ... roles) {
        return this.usableBy(roles);
    }

    public CommanderSkillBuilder allowCommander() {
        this.allowedRoles.add(SkillUserRole.COMMANDER);
        return this;
    }

    public CommanderSkillBuilder allowSquadLeader() {
        this.allowedRoles.add(SkillUserRole.SQUAD_LEADER);
        return this;
    }

    public KubeCommanderSkillDefinition build() {
        LinkedHashSet<SkillUserRole> roles = this.allowedRoles.isEmpty() ? SkillUserRole.defaultRoles() : new LinkedHashSet<SkillUserRole>(this.allowedRoles);
        return new KubeCommanderSkillDefinition(this.id, this.displayName, this.description, this.stats, this.icon, this.trigger, this.cooldownSeconds, this.enabled, roles);
    }

    public KubeCommanderSkillDefinition register() {
        KubeCommanderSkillDefinition definition = this.build();
        EspetroCommanderSkills.register(definition);
        return definition;
    }
}

