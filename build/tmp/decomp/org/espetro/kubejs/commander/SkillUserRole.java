/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.kubejs.commander;

import java.lang.reflect.Array;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public enum SkillUserRole {
    COMMANDER,
    SQUAD_LEADER;


    public static Set<SkillUserRole> defaultRoles() {
        LinkedHashSet<SkillUserRole> roles = new LinkedHashSet<SkillUserRole>();
        roles.add(COMMANDER);
        return roles;
    }

    public static SkillUserRole parseOne(String raw) {
        String key;
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return switch (key = raw.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_')) {
            case "commander", "cmd", "command", "\u6307\u6325\u5b98" -> COMMANDER;
            case "squad_leader", "squadleader", "leader", "sl", "\u961f\u957f", "\u5c0f\u961f\u957f" -> SQUAD_LEADER;
            default -> null;
        };
    }

    public static Set<SkillUserRole> parseMany(Object ... tokens) {
        LinkedHashSet<SkillUserRole> roles = new LinkedHashSet<SkillUserRole>();
        if (tokens == null) {
            return roles;
        }
        for (Object token : tokens) {
            SkillUserRole role;
            if (token == null) continue;
            if (token instanceof Iterable) {
                Iterable it = (Iterable)token;
                for (Object nested : it) {
                    role = SkillUserRole.parseOne(String.valueOf(nested));
                    if (role == null) continue;
                    roles.add(role);
                }
                continue;
            }
            if (token.getClass().isArray()) {
                int len = Array.getLength(token);
                for (int i = 0; i < len; ++i) {
                    Object nested;
                    nested = Array.get(token, i);
                    role = SkillUserRole.parseOne(String.valueOf(nested));
                    if (role == null) continue;
                    roles.add(role);
                }
                continue;
            }
            SkillUserRole role2 = SkillUserRole.parseOne(String.valueOf(token));
            if (role2 == null) continue;
            roles.add(role2);
        }
        return roles;
    }

    public String wireName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

