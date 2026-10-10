/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Function
 *  dev.latvian.mods.rhino.Scriptable
 *  javax.annotation.Nullable
 */
package org.espetro.kubejs.commander;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.kubejs.commander.CommanderSkillBuilder;
import org.espetro.kubejs.commander.KubeCommanderSkillDefinition;
import org.espetro.kubejs.commander.KubeCommanderSkillEvent;
import org.espetro.team.CommanderSkillManager;

public final class EspetroCommanderSkills {
    public static final String DEFAULT_ARTILLERY_SKILL_ID = "artillery_155";
    private static final Map<String, KubeCommanderSkillDefinition> DEFINITIONS = new LinkedHashMap<String, KubeCommanderSkillDefinition>();
    private static final Map<String, Function> SERVER_HANDLERS = new LinkedHashMap<String, Function>();

    private EspetroCommanderSkills() {
    }

    public static CommanderSkillBuilder create(String id) {
        return new CommanderSkillBuilder(id);
    }

    public static CommanderSkillBuilder skill(String id) {
        return EspetroCommanderSkills.create(id);
    }

    public static KubeCommanderSkillDefinition register(KubeCommanderSkillDefinition definition) {
        if (definition == null || definition.id().isBlank() || !definition.enabled()) {
            return definition;
        }
        if (definition.id().length() > 128) {
            Espetro.LOGGER.warn("\u5ffd\u7565 ID \u8fc7\u957f\u7684 KubeJS \u6307\u6325\u5b98\u6280\u80fd: {}", (Object)definition.id());
            return definition;
        }
        DEFINITIONS.put(definition.id(), definition);
        Espetro.LOGGER.info("\u5df2\u6ce8\u518c KubeJS \u6280\u80fd: {} ({}) usableBy=[{}]", new Object[]{definition.id(), definition.displayName(), definition.allowedRolesWire()});
        return definition;
    }

    public static void registerDefaults() {
        EspetroCommanderSkills.registerBuiltInDefaults();
    }

    public static void clearDefinitions() {
        DEFINITIONS.clear();
    }

    public static void clearHandlers() {
        SERVER_HANDLERS.clear();
    }

    public static boolean on(String skillId, Function callback) {
        if (skillId == null || skillId.isBlank() || callback == null) {
            return false;
        }
        SERVER_HANDLERS.put(skillId.trim(), callback);
        Espetro.LOGGER.info("\u5df2\u6ce8\u518c KubeJS \u6307\u6325\u5b98\u6280\u80fd\u56de\u8c03: {}", (Object)skillId.trim());
        return true;
    }

    public static boolean has(String skillId) {
        return EspetroCommanderSkills.getDefinition(skillId) != null;
    }

    @Nullable
    public static KubeCommanderSkillDefinition getDefinition(String skillId) {
        if (skillId == null) {
            return null;
        }
        return DEFINITIONS.get(skillId.trim());
    }

    public static List<KubeCommanderSkillDefinition> getDefinitions() {
        return DEFINITIONS.values().stream().sorted(Comparator.comparing(KubeCommanderSkillDefinition::id)).toList();
    }

    public static String[] getSkillIds() {
        return (String[])EspetroCommanderSkills.getDefinitions().stream().map(KubeCommanderSkillDefinition::id).toArray(String[]::new);
    }

    public static boolean execute(KubeCommanderSkillDefinition definition, KubeCommanderSkillEvent event) {
        if (definition == null || event == null) {
            return false;
        }
        Function callback = SERVER_HANDLERS.get(definition.id());
        if (callback == null) {
            event.tell("\u00a7c\u6307\u6325\u5b98\u6280\u80fd " + definition.displayName() + " \u6ca1\u6709 KubeJS server_scripts \u56de\u8c03\u3002");
            Espetro.LOGGER.warn("KubeJS \u6307\u6325\u5b98\u6280\u80fd {} \u6ca1\u6709 server_scripts \u56de\u8c03", (Object)definition.id());
            return false;
        }
        try {
            Context cx = Context.enter();
            cx.setApplicationClassLoader(EspetroCommanderSkills.class.getClassLoader());
            Scriptable scope = callback.getParentScope();
            Object eventObject = Context.javaToJS((Context)cx, (Object)event, (Scriptable)scope);
            Object result = callback.call(cx, scope, scope, new Object[]{eventObject});
            return !Boolean.FALSE.equals(result);
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u6267\u884c KubeJS \u6307\u6325\u5b98\u6280\u80fd\u5931\u8d25: {}", (Object)definition.id(), (Object)e);
            event.tell("\u00a7c\u6307\u6325\u5b98\u6280\u80fd KubeJS \u56de\u8c03\u6267\u884c\u5931\u8d25: " + e.getMessage());
            return false;
        }
    }

    public static boolean execute(ServerPlayer commander, String skillId) {
        return CommanderSkillManager.getInstance().activateSkill(commander, skillId);
    }

    public static boolean activate(ServerPlayer commander, String skillId) {
        return EspetroCommanderSkills.execute(commander, skillId);
    }

    public static boolean openTacticalMap(ServerPlayer commander, String skillId) {
        return CommanderSkillManager.getInstance().beginArtilleryTargetSelection(commander, skillId);
    }

    public static boolean openTargetMap(ServerPlayer commander, String skillId) {
        return EspetroCommanderSkills.openTacticalMap(commander, skillId);
    }

    public static boolean openArtilleryMap(ServerPlayer commander) {
        return CommanderSkillManager.getInstance().beginArtilleryTargetSelection(commander);
    }

    public static boolean isOnCooldown(ServerPlayer commander, String skillId) {
        return commander != null && EspetroCommanderSkills.isOnCooldown(commander.m_20148_(), skillId);
    }

    public static boolean isOnCooldown(UUID commanderId, String skillId) {
        return commanderId != null && CommanderSkillManager.getInstance().isOnCooldown(commanderId, skillId);
    }

    public static int getCooldownSeconds(ServerPlayer commander, String skillId) {
        return commander == null ? 0 : EspetroCommanderSkills.getCooldownSeconds(commander.m_20148_(), skillId);
    }

    public static int getCooldownSeconds(UUID commanderId, String skillId) {
        return commanderId == null ? 0 : CommanderSkillManager.getInstance().getRemainingCooldownSeconds(commanderId, skillId);
    }

    public static Map<String, Integer> getCooldowns(ServerPlayer commander) {
        return commander == null ? Map.of() : CommanderSkillManager.getInstance().getCooldownData(commander.m_20148_());
    }

    public static CommanderSkillManager.SkillStatus getStatus(ServerPlayer commander, String skillId) {
        return CommanderSkillManager.getInstance().getSkillStatus(commander, skillId);
    }

    public static boolean canUse(ServerPlayer commander, String skillId) {
        return EspetroCommanderSkills.getStatus(commander, skillId).canUse();
    }

    public static KubeCommanderSkillEvent event(KubeCommanderSkillDefinition definition, ServerPlayer commander, String team) {
        return new KubeCommanderSkillEvent(definition, commander, team);
    }

    public static KubeCommanderSkillEvent targetEvent(KubeCommanderSkillDefinition definition, CommanderSkillManager.ArtillerySupportRequest request, ServerPlayer commander, ServerLevel level, BlockPos blockPos) {
        return new KubeCommanderSkillEvent(definition, request, commander, level, blockPos);
    }

    public static List<CommanderSkillManager.SkillView> getSkillViews() {
        ArrayList<CommanderSkillManager.SkillView> views = new ArrayList<CommanderSkillManager.SkillView>();
        for (KubeCommanderSkillDefinition definition : EspetroCommanderSkills.getDefinitions()) {
            views.add(new CommanderSkillManager.SkillView(definition.id(), definition.displayName(), definition.description(), (String)(definition.stats().isBlank() ? "\u00a78KubeJS | \u51b7\u5374: " + definition.cooldownSeconds() + "\u79d2" : definition.stats()), definition.icon()));
        }
        return views;
    }

    private static void registerBuiltInDefaults() {
        EspetroCommanderSkills.create("drone_detection").displayName("\u65e0\u4eba\u673a\u4fa6\u6d4b").description("\u77ed\u65f6\u95f4\u9ad8\u4eae\u6307\u6325\u5b98\u9644\u8fd1\u654c\u65b9\u73a9\u5bb6").stats("\u00a78\u9ad8\u4eae\u534a\u5f84: 100\u683c | \u6301\u7eed: 10\u79d2 | \u51b7\u5374: 60\u79d2").icon("espetro:textures/gui/commander_skills/drone_detection.png").activate().cooldownSeconds(60).register();
        EspetroCommanderSkills.create(DEFAULT_ARTILLERY_SKILL_ID).displayName("155\u706b\u70ae\u652f\u63f4").description("\u6253\u5f00 ESPoints \u6218\u672f\u5730\u56fe\u9009\u62e9\u70ae\u51fb\u5750\u6807\uff0c\u518d\u4ea4\u7ed9 KubeJS \u6267\u884c\u706b\u529b\u6548\u679c").stats("\u00a78ESPoints\u5730\u56fe\u9009\u70b9 | KubeJS\u4e24\u6279\u5b9e\u4f53\u70ae\u51fb | \u51b7\u5374: 180\u79d2").icon("espetro:textures/gui/commander_skills/artillery_155.png").targetMap().cooldownSeconds(180).register();
    }
}

