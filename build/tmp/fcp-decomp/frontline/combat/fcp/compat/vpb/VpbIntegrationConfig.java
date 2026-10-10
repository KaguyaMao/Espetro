/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.tools.ParticleTool$ParticleType
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.slf4j.Logger
 */
package frontline.combat.fcp.compat.vpb;

import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import frontline.combat.fcp.compat.vpb.WarheadStats;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

public final class VpbIntegrationConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "fcp-vpb-integration.json";
    private static volatile VpbIntegrationConfig INSTANCE = new VpbIntegrationConfig();
    public final Map<ResourceLocation, WarheadStats> projectileWarheads;
    public final Set<ResourceLocation> muzzleSmokeProjectiles;
    public final boolean debugLogging;

    private VpbIntegrationConfig() {
        this(new HashMap<ResourceLocation, WarheadStats>(), new HashSet<ResourceLocation>(), false);
    }

    private VpbIntegrationConfig(Map<ResourceLocation, WarheadStats> projectileWarheads, Set<ResourceLocation> muzzleSmokeProjectiles, boolean debugLogging) {
        this.projectileWarheads = Collections.unmodifiableMap(projectileWarheads);
        this.muzzleSmokeProjectiles = Collections.unmodifiableSet(muzzleSmokeProjectiles);
        this.debugLogging = debugLogging;
    }

    public static VpbIntegrationConfig get() {
        return INSTANCE;
    }

    public static synchronized void load() {
        Path path = FMLPaths.CONFIGDIR.get().resolve(FILE_NAME);
        try {
            String json;
            JsonObject root;
            if (!Files.exists(path, new LinkOption[0])) {
                VpbIntegrationConfig.writeDefault(path);
                LOGGER.info("[FCP/VPB] Created default integration config at {}", (Object)path);
            }
            if ((root = (JsonObject)GSON.fromJson(json = Files.readString(path, StandardCharsets.UTF_8), JsonObject.class)) == null) {
                throw new IOException("config root is not a JSON object");
            }
            boolean debug = root.has("debugLogging") && root.get("debugLogging").getAsBoolean();
            HashMap<ResourceLocation, WarheadStats> warheads = new HashMap<ResourceLocation, WarheadStats>();
            if (root.has("projectileWarheads") && root.get("projectileWarheads").isJsonObject()) {
                JsonObject obj = root.getAsJsonObject("projectileWarheads");
                for (Map.Entry e : obj.entrySet()) {
                    WarheadStats stats;
                    ResourceLocation id;
                    if (((String)e.getKey()).startsWith("_") || (id = VpbIntegrationConfig.parseId((String)e.getKey())) == null || !((JsonElement)e.getValue()).isJsonObject()) continue;
                    JsonObject s = ((JsonElement)e.getValue()).getAsJsonObject();
                    float directDamage = s.has("directDamage") ? s.get("directDamage").getAsFloat() : 0.0f;
                    float explosionDamage = s.has("explosionDamage") ? s.get("explosionDamage").getAsFloat() : 0.0f;
                    float explosionRadius = s.has("explosionRadius") ? s.get("explosionRadius").getAsFloat() : 0.0f;
                    int fireTime = s.has("fireTime") ? s.get("fireTime").getAsInt() : 0;
                    boolean destroy = !s.has("destroyBlocks") || s.get("destroyBlocks").getAsBoolean();
                    ParticleTool.ParticleType particle = null;
                    if (s.has("explosionParticle") && !s.get("explosionParticle").getAsString().isBlank()) {
                        String raw = s.get("explosionParticle").getAsString().trim().toUpperCase(Locale.ROOT);
                        try {
                            particle = ParticleTool.ParticleType.valueOf((String)raw);
                        }
                        catch (IllegalArgumentException badType) {
                            LOGGER.warn("[FCP/VPB] '{}' has invalid explosionParticle '{}' - using radius-based tier. Valid: MINI, SMALL, MEDIUM, LARGE, HUGE, GIANT", (Object)id, (Object)raw);
                        }
                    }
                    if (!(stats = new WarheadStats(directDamage, explosionDamage, explosionRadius, particle, fireTime, destroy)).hasDirectHit() && !stats.hasExplosion()) {
                        LOGGER.warn("[FCP/VPB] Skipping '{}': needs directDamage > 0 and/or (explosionDamage > 0 and explosionRadius > 0)", (Object)id);
                        continue;
                    }
                    warheads.put(id, stats);
                }
            }
            HashSet<ResourceLocation> smokeProjectiles = new HashSet<ResourceLocation>();
            if (root.has("muzzleSmokeProjectiles") && root.get("muzzleSmokeProjectiles").isJsonArray()) {
                JsonArray arr = root.getAsJsonArray("muzzleSmokeProjectiles");
                for (JsonElement el : arr) {
                    ResourceLocation id = VpbIntegrationConfig.parseId(el.getAsString());
                    if (id == null) continue;
                    smokeProjectiles.add(id);
                }
            }
            INSTANCE = new VpbIntegrationConfig(warheads, smokeProjectiles, debug);
            LOGGER.info("[FCP/VPB] Loaded {} warhead mapping(s) and {} muzzle-smoke projectile(s).", (Object)warheads.size(), (Object)smokeProjectiles.size());
        }
        catch (Exception ex) {
            LOGGER.warn("[FCP/VPB] Failed to read {} - integration disabled until fixed: {}", (Object)FILE_NAME, (Object)ex.toString());
            INSTANCE = new VpbIntegrationConfig();
        }
    }

    private static ResourceLocation parseId(String raw) {
        if (raw == null) {
            return null;
        }
        ResourceLocation id = ResourceLocation.m_135820_((String)raw.trim());
        if (id == null) {
            LOGGER.warn("[FCP/VPB] Ignoring invalid id '{}'", (Object)raw);
        }
        return id;
    }

    private static void writeDefault(Path path) throws IOException {
        JsonObject root = new JsonObject();
        root.addProperty("comment", "FCP  Point Blank integration. Keys are registry ids. projectileWarheads maps a VPB projectile ENTITY id to the SBW warhead it is replaced by on impact: directDamage is a flat hit on the struck entity (runs through SBW vehicle DamageModifiers/armor), explosionDamage/explosionRadius are the AoE blast. AP = direct only; HE = explosion only; HEAT = both. muzzleSmokeProjectiles lists VPB projectile ENTITY ids that emit the SBW-RPG muzzle + downrange smoke when fired. Enable debugLogging to print unmapped pointblank ids to the log so you can discover the exact strings for your installed content packs.");
        root.addProperty("debugLogging", Boolean.valueOf(false));
        JsonObject warheads = new JsonObject();
        JsonObject example = new JsonObject();
        example.addProperty("directDamage", (Number)200.0);
        example.addProperty("explosionDamage", (Number)90.0);
        example.addProperty("explosionRadius", (Number)6.0);
        example.addProperty("explosionParticle", "MEDIUM");
        example.addProperty("fireTime", (Number)0);
        example.addProperty("destroyBlocks", Boolean.valueOf(true));
        warheads.add("pointblank:example_rocket", (JsonElement)example);
        root.add("projectileWarheads", (JsonElement)warheads);
        JsonArray smokeProjectiles = new JsonArray();
        smokeProjectiles.add("pointblank:example_rocket");
        root.add("muzzleSmokeProjectiles", (JsonElement)smokeProjectiles);
        Files.createDirectories(path.getParent(), new FileAttribute[0]);
        Files.writeString(path, (CharSequence)GSON.toJson((JsonElement)root), StandardCharsets.UTF_8, new OpenOption[0]);
    }
}

