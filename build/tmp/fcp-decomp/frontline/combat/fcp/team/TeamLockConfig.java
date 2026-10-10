/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.slf4j.Logger
 */
package frontline.combat.fcp.team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

public final class TeamLockConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "fcp-team-lock.json";
    private static volatile TeamLockConfig INSTANCE = new TeamLockConfig();
    public final boolean enforce;
    public final boolean autoClaimOnEnter;
    public final boolean blockUnclaimed;
    public final boolean opBypass;
    public final boolean applyToNonFcpVehicles;
    public final int messageCooldownTicks;
    public final Set<String> interactionBypassItems;

    private TeamLockConfig() {
        this(true, true, false, true, true, 40, Collections.emptySet());
    }

    private TeamLockConfig(boolean enforce, boolean autoClaimOnEnter, boolean blockUnclaimed, boolean opBypass, boolean applyToNonFcpVehicles, int messageCooldownTicks, Set<String> interactionBypassItems) {
        this.enforce = enforce;
        this.autoClaimOnEnter = autoClaimOnEnter;
        this.blockUnclaimed = blockUnclaimed;
        this.opBypass = opBypass;
        this.applyToNonFcpVehicles = applyToNonFcpVehicles;
        this.messageCooldownTicks = messageCooldownTicks;
        this.interactionBypassItems = Collections.unmodifiableSet(interactionBypassItems);
    }

    public static TeamLockConfig get() {
        return INSTANCE;
    }

    public static synchronized void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        try {
            String json;
            JsonObject root;
            if (!Files.exists(path, new LinkOption[0])) {
                TeamLockConfig.writeDefault(path);
                LOGGER.info("[FCP/Team] Created default team-lock config at {}", (Object)path);
            }
            if ((root = (JsonObject)GSON.fromJson(json = Files.readString(path, StandardCharsets.UTF_8), JsonObject.class)) == null) {
                throw new IOException("config root is not a JSON object");
            }
            TeamLockConfig def = new TeamLockConfig();
            INSTANCE = new TeamLockConfig(TeamLockConfig.bool(root, "enforce", def.enforce), TeamLockConfig.bool(root, "autoClaimOnEnter", def.autoClaimOnEnter), TeamLockConfig.bool(root, "blockUnclaimed", def.blockUnclaimed), TeamLockConfig.bool(root, "opBypass", def.opBypass), TeamLockConfig.bool(root, "applyToNonFcpVehicles", def.applyToNonFcpVehicles), root.has("messageCooldownTicks") ? root.get("messageCooldownTicks").getAsInt() : def.messageCooldownTicks, TeamLockConfig.stringSet(root, "interactionBypassItems"));
            LOGGER.info("[FCP/Team] Loaded team-lock config (enforce={}, autoClaim={}, blockUnclaimed={}, opBypass={}, allVehicles={}, bypassItems={}).", new Object[]{TeamLockConfig.INSTANCE.enforce, TeamLockConfig.INSTANCE.autoClaimOnEnter, TeamLockConfig.INSTANCE.blockUnclaimed, TeamLockConfig.INSTANCE.opBypass, TeamLockConfig.INSTANCE.applyToNonFcpVehicles, TeamLockConfig.INSTANCE.interactionBypassItems.size()});
        }
        catch (Exception e) {
            LOGGER.error("[FCP/Team] Failed to load {}, falling back to defaults.", (Object)FILE_NAME, (Object)e);
            INSTANCE = new TeamLockConfig();
        }
    }

    private static boolean bool(JsonObject o, String key, boolean def) {
        return o.has(key) ? o.get(key).getAsBoolean() : def;
    }

    private static Set<String> stringSet(JsonObject o, String key) {
        HashSet<String> out = new HashSet<String>();
        if (o.has(key) && o.get(key).isJsonArray()) {
            for (JsonElement el : o.getAsJsonArray(key)) {
                if (!el.isJsonPrimitive()) continue;
                out.add(el.getAsString());
            }
        }
        return out;
    }

    private static void writeDefault(Path path) throws IOException {
        TeamLockConfig d = new TeamLockConfig();
        JsonObject root = new JsonObject();
        root.addProperty("_comment", "Frontline Combat Pack - persistent per-vehicle team lock. Teams are vanilla scoreboard teams. Blocks ALL right-click interaction (enter, inventory, repaint, crowbar, name tag) with enemy vehicles; left-click attacks and gunfire are never affected.");
        root.addProperty("enforce", Boolean.valueOf(d.enforce));
        root.addProperty("autoClaimOnEnter", Boolean.valueOf(d.autoClaimOnEnter));
        root.addProperty("blockUnclaimed", Boolean.valueOf(d.blockUnclaimed));
        root.addProperty("opBypass", Boolean.valueOf(d.opBypass));
        root.addProperty("applyToNonFcpVehicles", Boolean.valueOf(d.applyToNonFcpVehicles));
        root.addProperty("messageCooldownTicks", (Number)d.messageCooldownTicks);
        root.addProperty("_interactionBypassItems_comment", "Item IDs allowed to interact with enemy vehicles anyway (e.g. keep C4 working): [\"superbwarfare:c4_bomb\", \"superbwarfare:detonator\"]. Empty = block everything.");
        root.add("interactionBypassItems", (JsonElement)new JsonArray());
        Files.createDirectories(path.getParent(), new FileAttribute[0]);
        Files.writeString(path, (CharSequence)GSON.toJson((JsonElement)root), StandardCharsets.UTF_8, new OpenOption[0]);
    }
}

