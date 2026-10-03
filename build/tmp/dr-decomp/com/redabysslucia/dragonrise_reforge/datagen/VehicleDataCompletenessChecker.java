/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraftforge.common.data.ExistingFileHelper
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VehicleDataCompletenessChecker
implements DataProvider {
    private static final List<String> ALL_FIELDS = List.of("MaxHealth", "RepairCooldown", "RepairAmount", "SelfHurtPercent", "SelfHurtAmount", "MaxEnergy", "OBB", "Seats", "Radar", "UpStep", "TrackDistanceMultiply", "KeepChunkLoaded", "MouseSensitivity", "PassengerRenderScale", "AllowFreeCam", "HasDecoy", "SmokeDecoy", "ApplyDefaultDamageModifiers", "SendHitParticles", "DamageModifiers", "Mass", "TowForceFactor", "DecoyMagazineSize", "DecoyReloadTime", "DestroyInfo", "SeekInfo", "VehicleContainerType", "HasUpgradeSlots", "VehicleIcon", "ContainerIcon", "HUDColor", "LaserColor", "LaserScale", "Type", "EngineType", "EngineInfo", "EngineSound", "HornSound", "ThirdPersonCameraPos", "HasLowHealthWarning", "ForwardTowed", "RotateOffsetHeight", "Weapons", "CollisionLevel", "TurretPos", "TurretTurnSpeed", "TurretYawRange", "TurretPitchRange", "TurretControllerIndex", "TurretCustomPitch", "HudType", "BarrelPos", "PassengerWeaponStationPos", "PassengerWeaponStationBarrelPos", "PassengerWeaponStationTurnSpeed", "PassengerWeaponStationYawRange", "PassengerWeaponStationPitchRange", "PassengerWeaponStationControllerIndex", "UsePassengerCreativeAmmoBox", "Gravity", "TerrainCompat", "TerrainCompatRotateRate", "InertiaRotateRate");
    private static final List<String> CORE_FIELDS = List.of("EngineInfo", "Weapons");
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;
    private List<String> availableTypeIcons = new ArrayList<String>();
    private static final Map<String, String> OFFICIAL_REFS = new TreeMap<String, String>();
    private static final String WEAPON_EXAMPLES = "**\u5766\u514b\u70ae\u793a\u4f8b\uff08M1A2 cannon_ap\uff0c\u5b98\u65b9\uff09**\n```json\n\"Cannon\": {\n  \"Icon\": \"superbwarfare:textures/overlay/vehicle/weapon/icons/ap_shell.png\",\n  \"DefaultFireMode\": \"Semi\", \"AvailableFireModes\": \"Semi\",\n  \"Projectile\": \"superbwarfare:cannon_shell\", \"ShellType\": \"AP\",\n  \"Magazine\": 1, \"EmptyReloadTime\": 100, \"Velocity\": 20, \"ProjectileLife\": 50,\n  \"Damage\": 700, \"ExplosionDamage\": 80, \"ExplosionRadius\": 4, \"Spread\": 0.02,\n  \"DefaultZoom\": 3, \"Gravity\": 0.03, \"RecoilTime\": 42, \"ShootAnimationTime\": 20,\n  \"AmmoType\": [\"superbwarfare:large_shell_ap\", {\"Ammo\": \"superbwarfare:large_shell_he\", \"Override\": {\"ShellType\": \"HE\", \"Damage\": 250, \"ExplosionRadius\": 10}}],\n  \"ShootPos\": { \"Transform\": \"Barrel\", \"Positions\": [[0,0.13,4.7]], \"Directions\": [\"Barrel\"] },\n  \"Name\": \"weapon.superbwarfare.cannon_ap\"\n}\n```\n**\u673a\u70ae\u793a\u4f8b\uff08A-10 GAU-8 30mm\uff0c\u5b98\u65b9\uff09**\n```json\n\"Cannon\": {\n  \"DefaultFireMode\": \"Auto\", \"AvailableFireModes\": \"Auto\",\n  \"AmmoType\": \"superbwarfare:small_shell_ap\", \"Projectile\": \"superbwarfare:small_cannon_shell\",\n  \"RPM\": 1200, \"Velocity\": 24, \"ProjectileLife\": 60, \"Damage\": 35,\n  \"ExplosionDamage\": 10, \"ExplosionRadius\": 3, \"Spread\": 0.5, \"DefaultZoom\": 2,\n  \"HeatPerShoot\": 1.5, \"NaturalCooldown\": 0.5,\n  \"ShootPos\": { \"Transform\": \"Vehicle\", \"Positions\": [[0.1,1.36,5.9]], \"Directions\": [[0,-0.02,1]] },\n  \"Name\": \"weapon.superbwarfare.30mm_gau_8_a\"\n}\n```\n**\u673a\u67aa\u793a\u4f8b\uff08M1A2 7.62mm \u540c\u8f74\uff0c\u5b98\u65b9\uff09**\n```json\n\"MachineGun\": {\n  \"DefaultFireMode\": \"Auto\", \"AvailableFireModes\": \"Auto\",\n  \"AmmoType\": \"@RifleAmmo\", \"Projectile\": \"superbwarfare:projectile\",\n  \"RPM\": 600, \"Damage\": 9.5, \"BypassesArmor\": 0.3, \"Velocity\": 30, \"Spread\": 0.5,\n  \"DefaultZoom\": 3, \"HeatPerShoot\": 4, \"NaturalCooldown\": 1,\n  \"ShootPos\": { \"Transform\": \"Barrel\", \"Positions\": [[-0.139,0.245,0.982]], \"Directions\": [\"Barrel\"] },\n  \"Name\": \"weapon.superbwarfare.7_62mm_coax\"\n}\n```\n**\u5bfc\u5f39\u793a\u4f8b\uff08A-10 AGM-65 \u5c0f\u725b\uff0c\u5b98\u65b9\uff09**\n```json\n\"Missile\": {\n  \"DefaultFireMode\": \"Semi\", \"AvailableFireModes\": \"Semi\",\n  \"Magazine\": 4, \"EmptyReloadTime\": 200, \"Projectile\": \"superbwarfare:agm_65\",\n  \"Damage\": 1100, \"ExplosionDamage\": 180, \"ExplosionRadius\": 12,\n  \"AddShooterDeltaMovement\": true, \"Velocity\": 1.05, \"ProjectileLife\": 3600,\n  \"SeekWeaponInfo\": { \"SeekDirection\": \"ClientCamera\", \"SeekRange\": 1024, \"SeekAngle\": 20, \"SeekTime\": 10, \"MaxTargetHeight\": 32, \"MinTargetSize\": 0.9 },\n  \"AmmoType\": [\"superbwarfare:large_anti_ground_missile\"],\n  \"ShootPos\": { \"Transform\": \"Vehicle\", \"Positions\": [[4.97,1.19,0]], \"BoundUpWithAmmoAmount\": true },\n  \"Crosshair\": \"@AirCraftMissile\", \"Name\": \"weapon.superbwarfare.agm_65_missile\"\n}\n```\n**\u706b\u7bad\u793a\u4f8b\uff08A-10 70mm \u706b\u7bad\u5de2\uff0c\u5b98\u65b9\uff09**\n```json\n\"Rocket\": {\n  \"AmmoType\": \"superbwarfare:small_rocket\", \"Projectile\": \"superbwarfare:small_rocket\",\n  \"DefaultFireMode\": \"Auto\", \"AvailableFireModes\": \"Semi\",\n  \"Magazine\": 28, \"EmptyReloadTime\": 160, \"RPM\": 450, \"Spread\": 0.4,\n  \"Damage\": 80, \"ExplosionDamage\": 40, \"ExplosionRadius\": 5,\n  \"Gravity\": 0.001, \"ProjectileLife\": 60, \"Velocity\": 11,\n  \"ShootPos\": { \"Transform\": \"Vehicle\", \"Positions\": [[2.95,1.2,0.1]], \"Directions\": [[0,-0.03,1]] },\n  \"Name\": \"weapon.superbwarfare.70mm_rocket\"\n}\n```\n";

    public VehicleDataCompletenessChecker(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                Path dataDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/data/dragonrise_reforge/sbw/vehicles");
                Path modelDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/models/bedrock/vehicle");
                Path reportFile = Path.of(workingDir, new String[0]).resolve("datagen_vehicle_report.md");
                Path promptDir = Path.of(workingDir, new String[0]).resolve("datagen_ai_prompts");
                if (!Files.exists(dataDir, new LinkOption[0])) {
                    return;
                }
                Path typeIconDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/textures/gui/vehicle/type");
                this.availableTypeIcons = new ArrayList<String>();
                if (Files.exists(typeIconDir, new LinkOption[0])) {
                    try (DirectoryStream<Path> stream = Files.newDirectoryStream(typeIconDir, "*.png");){
                        for (Path p : stream) {
                            this.availableTypeIcons.add(p.getFileName().toString());
                        }
                    }
                    this.availableTypeIcons.sort(String::compareTo);
                }
                TreeMap missingByVehicle = new TreeMap();
                ArrayList<String> completeVehicles = new ArrayList<String>();
                ArrayList<String> skippedVehicles = new ArrayList<String>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(dataDir, "*.json");){
                    for (Path path : stream) {
                        JsonObject json;
                        String id = path.getFileName().toString().replace(".json", "");
                        Path modelFile = modelDir.resolve(id + ".geo.json");
                        if (!Files.exists(modelFile, new LinkOption[0]) || !this.hasBuildBone(Files.readString(modelFile))) {
                            skippedVehicles.add(id);
                            continue;
                        }
                        try {
                            json = JsonParser.parseString((String)Files.readString(path)).getAsJsonObject();
                        }
                        catch (Exception e) {
                            continue;
                        }
                        ArrayList<String> missing = new ArrayList<String>();
                        for (String field : ALL_FIELDS) {
                            if (json.has(field)) continue;
                            missing.add(field);
                        }
                        ArrayList<String> missingCore = new ArrayList<String>();
                        for (String field : CORE_FIELDS) {
                            if (json.has(field)) continue;
                            missingCore.add(field);
                        }
                        if (missingCore.isEmpty()) {
                            completeVehicles.add(id);
                            continue;
                        }
                        missingByVehicle.put(id, missing);
                    }
                }
                StringBuilder report = new StringBuilder();
                report.append("# \u8f7d\u5177\u6570\u636e\u5b8c\u6574\u6027\u62a5\u544a\n\n");
                report.append("\u751f\u6210\u65f6\u95f4\uff1a").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
                report.append("\u524d\u63d0\uff1a\u4ec5\u7edf\u8ba1**\u6a21\u578b\u542b Build \u9aa8\u9abc**\u7684\u8f7d\u5177\uff08\u9632\u6b62\u8bef\u62a5/\u8986\u76d6\u5df2\u5b8c\u6210\u8f7d\u5177\uff09\uff1b\u672c\u62a5\u544a\u53ea\u8bfb\uff0c\u4e0d\u4fee\u6539\u4efb\u4f55\u6570\u636e\u3002\n\n");
                report.append("\u5b57\u6bb5\u6e05\u5355\uff1asuperbwarfare `DefaultVehicleData` \u5171 ").append(ALL_FIELDS.size()).append(" \u4e2a\u5b57\u6bb5\uff0c\u5176\u4e2d\u6838\u5fc3\u5fc5\u586b ").append(CORE_FIELDS.size()).append(" \u4e2a\uff08MaxHealth/MaxEnergy/OBB/Seats/Weapons/Engine*/HudType/ContainerType/Icon\uff09\u3002\n\n");
                report.append("## \u4e00\u3001\u7f3a\u5c11\u6838\u5fc3\u5b57\u6bb5\u7684\u8f7d\u5177\uff08\u9700\u8981 AI/\u4eba\u5de5\u586b\u5145\uff09\n\n");
                if (missingByVehicle.isEmpty()) {
                    report.append("\uff08\u65e0\uff09\n\n");
                } else {
                    report.append("| \u8f7d\u5177 | \u7f3a\u5931\u5b57\u6bb5\u6570 | \u7f3a\u5931\u5b57\u6bb5 |\n|---|---|---|\n");
                    for (Map.Entry entry : missingByVehicle.entrySet()) {
                        report.append("| ").append((String)entry.getKey()).append(" | ").append(((List)entry.getValue()).size()).append(" | ").append(String.join((CharSequence)", ", (Iterable)entry.getValue())).append(" |\n");
                    }
                    report.append("\n");
                }
                report.append("## \u4e8c\u3001\u6838\u5fc3\u5b57\u6bb5\u9f50\u5168\u7684\u8f7d\u5177\n\n");
                if (completeVehicles.isEmpty()) {
                    report.append("\uff08\u65e0\uff09\n\n");
                } else {
                    report.append(String.join((CharSequence)", ", completeVehicles)).append("\n\n");
                }
                report.append("## \u4e09\u3001\u8df3\u8fc7\uff08\u6a21\u578b\u7f3a\u5931\u6216\u65e0 Build \u9aa8\u9abc\uff09\n\n");
                if (skippedVehicles.isEmpty()) {
                    report.append("\uff08\u65e0\uff09\n\n");
                } else {
                    report.append(String.join((CharSequence)", ", skippedVehicles)).append("\n");
                }
                Files.createDirectories(reportFile.getParent(), new FileAttribute[0]);
                Files.writeString(reportFile, (CharSequence)report.toString(), new OpenOption[0]);
                System.out.println("VehicleDataCompletenessChecker: report -> " + reportFile.getFileName());
                if (!missingByVehicle.isEmpty()) {
                    Files.createDirectories(promptDir, new FileAttribute[0]);
                }
                int prompts = 0;
                for (Map.Entry e : missingByVehicle.entrySet()) {
                    String id = (String)e.getKey();
                    Path vehicleJson = dataDir.resolve(id + ".json");
                    Path modelFile = modelDir.resolve(id + ".geo.json");
                    String prompt = this.buildPrompt(id, Files.readString(vehicleJson), Files.exists(modelFile, new LinkOption[0]) ? Files.readString(modelFile) : "", (List)e.getValue());
                    Files.writeString(promptDir.resolve(id + ".md"), (CharSequence)prompt, new OpenOption[0]);
                    ++prompts;
                }
                System.out.println("VehicleDataCompletenessChecker: generated " + prompts + " AI prompt(s)");
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to check vehicle data completeness", e);
            }
        });
    }

    private String buildPrompt(String id, String vehicleJson, String modelJson, List<String> missingFields) {
        String typeCategory = this.detectTypeCategory(vehicleJson, modelJson);
        StringBuilder sb = new StringBuilder();
        sb.append("# AI \u586b\u5145\u4efb\u52a1\uff1a\u8f7d\u5177 `").append(id).append("`\n\n");
        sb.append("\u8be5\u8f7d\u5177\u6a21\u578b\u542b Build \u9aa8\u9abc\uff0c\u5c5e\u4e8e\u53ef\u81ea\u52a8\u751f\u6210\u6570\u636e\u7684\u8f66\u8f86\u3002**\u8bf7\u91cd\u70b9\u8865\u5168 `EngineInfo`\uff08\u8f7d\u5177\u6027\u80fd\uff09\u4e0e `Weapons`\uff08\u6b66\u5668\uff09\u5b57\u6bb5**\uff0c\u5176\u4ed6\u5b57\u6bb5\u53ef\u5ffd\u7565\u3002");
        sb.append("**\u5df2\u6709\u5b57\u6bb5\u4fdd\u6301\u539f\u6837\uff0c\u4e0d\u5f97\u6539\u52a8**\u3002\u8f93\u51fa\u5b8c\u6574 JSON\u3002\n\n");
        sb.append("## \u8f7d\u5177\u7c7b\u578b\n\n");
        sb.append("`").append(typeCategory).append("`\n\n");
        sb.append("## \u672c\u6b21\u91cd\u70b9\u5173\u6ce8\uff08\u7f3a\u5931\uff09\n\n");
        sb.append("```\n");
        for (String f : missingFields) {
            if (!f.equals("EngineInfo") && !f.equals("Weapons")) continue;
            sb.append(f).append("\n");
        }
        sb.append("```\n\n");
        sb.append(this.buildTypeGuide(typeCategory)).append("\n\n");
        String officialRef = OFFICIAL_REFS.get(typeCategory);
        if (officialRef != null) {
            sb.append("## \u5b98\u65b9\u53c2\u8003\u8f7d\u5177\uff08\u540c\u7c7b\u578b\uff0c\u4f9b\u53c2\u7167\uff09\n\n").append(officialRef).append("\n\n");
        }
        sb.append("## \u73b0\u6709\u6570\u636e\uff08\u9aa8\u67b6\uff0c\u4fdd\u7559\u4e0d\u53d8\uff09\n\n```json\n").append(vehicleJson).append("\n```\n\n");
        sb.append("## \u6a21\u578b\u4fe1\u606f\uff08\u8f85\u52a9\u5224\u65ad\u6b66\u5668/\u5ea7\u4f4d/\u70ae\u5854\uff09\n\n```\n");
        if (modelJson != null && !modelJson.isEmpty()) {
            try {
                JsonObject geometry;
                JsonArray geometries;
                JsonObject geo = JsonParser.parseString((String)modelJson).getAsJsonObject();
                JsonArray jsonArray = geometries = geo.has("minecraft:geometry") ? geo.getAsJsonArray("minecraft:geometry") : null;
                if (geometries != null && geometries.size() > 0 && (geometry = geometries.get(0).getAsJsonObject()).has("bones")) {
                    ArrayList<String> weaponBones = new ArrayList<String>();
                    ArrayList<String> seatBones = new ArrayList<String>();
                    ArrayList<String> terrainBones = new ArrayList<String>();
                    int obbCount = 0;
                    for (JsonElement b : geometry.getAsJsonArray("bones")) {
                        String name;
                        String string = name = b.getAsJsonObject().has("name") ? b.getAsJsonObject().get("name").getAsString() : "";
                        if (name.matches("^(CannonPos|MachineGunPos|MissilePos).*")) {
                            weaponBones.add(name);
                        } else if (name.startsWith("SeatsPos")) {
                            seatBones.add(name);
                        } else if (name.startsWith("TerrainCompatPos")) {
                            terrainBones.add(name);
                        }
                        if (!name.toLowerCase().contains("obb")) continue;
                        ++obbCount;
                    }
                    sb.append("\u5c04\u51fb\u70b9\u9aa8\u9abc: ").append(weaponBones.isEmpty() ? "\uff08\u65e0\uff09" : String.join((CharSequence)", ", weaponBones)).append("\n");
                    sb.append("\u5ea7\u4f4d\u9aa8\u9abc: ").append(seatBones.isEmpty() ? "\uff08\u65e0\uff09" : String.join((CharSequence)", ", seatBones)).append("\n");
                    sb.append("\u5730\u5f62\u517c\u5bb9\u9aa8\u9abc: ").append((String)(terrainBones.isEmpty() ? "\uff08\u65e0\uff09 : " + String.join((CharSequence)", ", terrainBones) : String.join((CharSequence)", ", terrainBones))).append("\n");
                    sb.append("OBB \u9aa8\u9abc\u6570: ").append(obbCount).append("\n");
                }
            }
            catch (Exception ignored) {
                sb.append("\uff08\u6a21\u578b\u89e3\u6790\u5931\u8d25\uff09\n");
            }
        }
        sb.append("```\n\n");
        sb.append("## \u586b\u5199\u8981\u6c42\uff08\u91cd\u70b9\uff1aEngineInfo \u6027\u80fd + \u6b66\u5668\u5b57\u6bb5\uff09\n\n");
        sb.append("### 1. EngineInfo\uff08\u8f7d\u5177\u6027\u80fd\uff09\n\n");
        sb.append("\u6309\u4e0a\u9762\u300c\u7c7b\u578b\u4e13\u5c5e\u586b\u5199\u6307\u5357\u300d\u4e2d\u8be5\u7c7b\u578b\u7684 `EngineInfo` \u952e\u6e05\u5355\u586b\u5199\uff0c\u7ed9\u51fa**\u5408\u7406\u7684\u6027\u80fd\u53c2\u6570**\uff08\u901f\u5ea6\u3001\u8f6c\u5411\u3001\u80fd\u91cf\u6d88\u8017\u7b49\uff09\u3002\n");
        sb.append("\u53c2\u8003\uff08\u901f\u5ea6\u6362\u7b97\u89c1\u4e0b\u6587\uff09\uff1a`Increment`/`Decrement`\uff08\u52a0\u901f\u5ea6\uff09\u5efa\u8bae `0.01`\uff1b`MaxForwardSpeedRate` \u586b 1 = 80 km/h\u3001`MaxBackwardSpeedRate` \u586b 1 = 64 km/h\u3002\n\n");
        sb.append("### 2. Weapons\uff08\u6b66\u5668\u5b57\u6bb5\uff09\n\n");
        sb.append("- **\u6ce8\u610f**\uff1a`Weapons` \u975e\u5fc5\u586b\u2014\u2014**\u5982\u679c\u8be5\u8f7d\u5177\u4e0d\u5e94\u8be5\u6709\u6b66\u5668**\uff08\u7eaf\u8fd0\u8f93\u8f66/\u65e0\u6b66\u88c5\u8f66\u8f86/\u6a21\u578b\u6ca1\u6709 `CannonPos`/`MachineGunPos`/`MissilePos` \u5c04\u51fb\u70b9\u9aa8\u9abc\uff09\uff0c**\u7701\u7565 `Weapons` \u5b57\u6bb5**\u5373\u53ef\uff0c\u4e0d\u8981\u786c\u586b\u3002\n");
        sb.append("- **\u6b66\u5668\u952e\u540d\uff08key\uff09**\uff1a\u6839\u636e\u6a21\u578b\u5c04\u51fb\u70b9\u9aa8\u9abc\u547d\u540d\uff08\u89c1\u4e0a\u65b9\u6a21\u578b\u4fe1\u606f\uff09\uff0c\u5982 `CannonPos*` \u2192 `Cannon`\u3001`MachineGunPos*` \u2192 `MachineGun`\u3001`MissilePos*` \u2192 `Missile`\uff1b\u591a\u4e2a\u5c04\u51fb\u70b9\u53ef\u7528 `Cannon1`/`Cannon2`\u3002\n");
        sb.append("- **\u6bcf\u628a\u6b66\u5668\u7684\u5b57\u6bb5\u7ed3\u6784**\uff08superbwarfare \u6807\u51c6\uff09\uff1a\n");
        sb.append("```json\n\"<\u6b66\u5668\u540d>\": {\n  \"AmmoType\": \"<\u5f39\u836fID>\",\n  \"Projectile\": \"<\u629b\u5c04\u7269ID>\",\n  \"RPM\": \u5c04\u901f,\n  \"Velocity\": \u521d\u901f,\n  \"Damage\": \u4f24\u5bb3,\n  \"ExplosionDamage\": \u7206\u70b8\u4f24\u5bb3,\n  \"ExplosionRadius\": \u7206\u70b8\u534a\u5f84,\n  \"Magazine\": \u5f39\u5939\u5bb9\u91cf,\n  \"EmptyReloadTime\": \u88c5\u586btick,\n  \"Spread\": \u6563\u5e03,\n  \"DefaultZoom\": \u9ed8\u8ba4\u7f29\u653e,\n  \"ShootPos\": { \"Positions\": [[x,y,z]], \"Transform\": \"Vehicle\" },\n  \"SoundInfo\": {},\n  \"Name\": \"weapon.dragonrise_reforge.<\u540d\u5b57>\"\n}\n```\n");
        sb.append("- \u53ea\u7ed9\u51fa**\u6b66\u5668\u952e\u540d\u4e0e\u5b57\u6bb5\u7ed3\u6784**\u5373\u53ef\uff0c\u5f39\u836f/\u629b\u5c04\u7269 ID \u53ef\u586b\u5360\u4f4d\u6216\u53c2\u8003 superbwarfare \u539f\u7248 ID\uff08\u5982 `superbwarfare:xxx`\uff09\u3002\n\n");
        sb.append("### 3. \u6b66\u5668\u5b8c\u6574\u914d\u7f6e\u793a\u4f8b\uff08\u5b98\u65b9\u5b9e\u6d4b\uff0c\u586b\u673a\u67aa/\u673a\u70ae/\u5766\u514b\u70ae/\u5bfc\u5f39/\u706b\u7bad\u53c2\u7167\uff09\n\n");
        sb.append(WEAPON_EXAMPLES).append("\n");
        sb.append("### 4. Projectile \u629b\u5c04\u7269\u53c2\u8003\uff08ID + \u7279\u70b9 + \u9700\u7279\u522b\u586b\u5199\u7684\u5b57\u6bb5\uff09\n\n");
        sb.append("\u6839\u636e\u6b66\u5668\u7c7b\u578b\u4ece\u4e0b\u8868\u9009\u62e9 `Projectile` \u4e0e `AmmoType`\uff0c\u5e76\u6309\u7279\u70b9\u8865\u5bf9\u5e94\u5b57\u6bb5\uff1a\n\n");
        sb.append("- **\u666e\u901a\u70ae\u5f39**\uff1a`superbwarfare:small_cannon_shell`\uff08\u5c0f\u53e3\u5f84/\u673a\u70ae\uff09\u3001`superbwarfare:cannon_shell`\uff08\u4e2d\u53e3\u5f84\u5766\u514b\u70ae/\u673a\u70ae\uff09\n");
        sb.append("  - \u7279\u70b9\uff1a\u76f4\u7ebf\u9ad8\u901f\u5f39\u9053\uff0c`Velocity` 20~30\uff0c`Gravity` \u5c0f\n");
        sb.append("  - \u7279\u522b\u5b57\u6bb5\uff1a`Damage`\u3001`RPM`\uff08\u5c04\u901f\uff09\u3001`Magazine`\u3001`Spread`\u3001`ShootPos`\uff08\u70ae\u53e3\u4f4d\u7f6e\uff09\n");
        sb.append("- **\u9730\u5f39**\uff1a`superbwarfare:grapeshot`\n");
        sb.append("  - \u7279\u70b9\uff1a\u6563\u5c04\u9762\u6740\u4f24\n");
        sb.append("  - \u7279\u522b\u5b57\u6bb5\uff1a`Spread` \u5927\u3001`Velocity` \u8f83\u4f4e\n");
        sb.append("- **\u8feb\u51fb\u70ae\u5f39**\uff1a`superbwarfare:mortar_shell`\n");
        sb.append("  - \u7279\u70b9\uff1a\u9ad8\u629b\u7269\u7ebf\uff0c`Gravity` \u5927\u3001`Velocity` \u4f4e\u3001`ProjectileLife` \u957f\n");
        sb.append("  - \u7279\u522b\u5b57\u6bb5\uff1a`Gravity`\u3001`ProjectileLife`\u3001`ExplosionRadius`\uff08\u706b\u70ae\u7c7b\u53ef\u914d\u5408\u95f4\u63a5\u706b\u63a7\uff09\n");
        sb.append("- **\u706b\u7bad**\uff1a`superbwarfare:small_rocket`\uff08\u53ca medium_rocket \u7b49\uff09\n");
        sb.append("  - \u7279\u70b9\uff1a\u76f4\u7ebf+\u91cd\u529b\uff0c\u591a\u53d1\u9f50\u5c04\n");
        sb.append("  - \u7279\u522b\u5b57\u6bb5\uff1a`Spread` \u5927\u3001`Magazine`\uff08\u706b\u7bad\u5de2\u7ba1\u6570\uff09\u3001`RPM` \u4f4e\u3001`DefaultZoom`\n");
        sb.append("- **\u822a\u5f39\uff08\u6295\u653e/\u81ea\u7531\u843d\u4f53\uff09**\uff1a`superbwarfare:mk_82`/`mk_84`\uff08\u7f8e\u5236\uff09\u3001`sc_50`/`sc_250`\uff08\u5fb7\u5236\uff09\u3001`melon_bomb`\u3001`bor_57`\n");
        sb.append("  - \u7279\u70b9\uff1a\u81ea\u7531\u843d\u4f53\u6295\u653e\uff0c`Velocity` \u4f4e\u3001`Gravity` \u6709\u503c\n");
        sb.append("  - **\u7279\u522b\u5b57\u6bb5\uff08\u5fc5\u586b\uff09**\uff1a`AddShooterDeltaMovement: true`\u3001`ShootPos.Directions: [\"DeltaMovement\"]`\u3001`ShootDirectionForHud: \"Bomb\"`\u3001`ViewDirection: \"Bomb\"`\u3001`BoundUpWithAmmoAmount`\uff08\u6309\u5f39\u6570\u6302\u8f7d\uff09\n");
        sb.append("- **\u5236\u5bfc\u5bfc\u5f39**\uff1a`superbwarfare:wire_guide_missile`\uff08\u7ebf\u5bfc/TOW\uff09\u3001`agm_65`\uff08\u5c0f\u725b\u7a7a\u5730\uff09\u3001`fim_92_missile`\uff08\u6bd2\u523a\u9632\u7a7a\uff09\u3001`kh_39`\u3001`ru_3m14`/`ru_9m100`/`ru_9m336`\uff08\u4fc4\u5236\u5404\u578b\uff09\n");
        sb.append("  - \u7279\u70b9\uff1a\u9700\u5236\u5bfc/\u5bfb\u7684\uff0c`Velocity` \u4e2d\u901f\u3001\u53ef `SeekMissile`/`@Missile` \u6b66\u5668\u7c7b\u578b\n");
        sb.append("  - \u7279\u522b\u5b57\u6bb5\uff1a\u6b66\u5668 key \u7528 `Missile`/`SeekMissile`/`DriverAAMissile`\uff08\u9632\u7a7a\uff09\u7b49\u3001`DefaultZoom`\u3001`Magazine`\u3001`Spread` \u5c0f\n");
        sb.append("- **\u5176\u4ed6**\uff1a`superbwarfare:projectile`\uff08\u901a\u7528\u629b\u5c04\u7269\uff09\u3001`swarm_drone`\uff08\u8702\u7fa4\u65e0\u4eba\u673a\uff0c`@Missile` \u7c7b\uff09\n\n");
        sb.append("### 5. AmmoType \u5f39\u836f\u53c2\u8003\uff08\u4f9b\u9009\u62e9\uff09\n\n");
        sb.append("\u6839\u636e\u6b66\u5668\u4e0e Projectile \u7c7b\u578b\u9009\u62e9\u5bf9\u5e94 `AmmoType`\uff1a\n\n");
        sb.append("- **\u673a\u70ae/\u70ae\u5f39**\uff1a`superbwarfare:small_shell_ap`\uff08\u7a7f\u7532\uff09\u3001`superbwarfare:small_shell_he`\uff08\u9ad8\u7206\uff09\u3001`superbwarfare:small_shell_aa`\uff08\u5bf9\u7a7a/\u9632\u7a7a\u5f39\u94fe\uff09\uff1b\u5927\u53e3\u5f84\u7528 `superbwarfare:large_shell_ap`/`large_shell_he`\n");
        sb.append("- **\u69b4\u5f39\u53d1\u5c04\u5668**\uff1a`superbwarfare:grenade_40mm`\n");
        sb.append("- **\u822a\u5f39**\uff1a`superbwarfare:small_aerial_bomb` / `medium_aerial_bomb` / `large_aerial_bomb`\n");
        sb.append("- **\u706b\u7bad**\uff1a`superbwarfare:small_rocket`\u3001`superbwarfare:medium_rocket_ap`\uff08\u7a7f\u7532\u706b\u7bad\uff09\n");
        sb.append("- **\u5bfc\u5f39**\uff1a`superbwarfare:medium_anti_air_missile`\uff08\u9632\u7a7a\u5bfc\u5f39\uff09\u3001`superbwarfare:medium_anti_ground_missile`\uff08\u53cd\u5730\u5bfc\u5f39\uff09\u3001`superbwarfare:javelin_missile`\uff08\u6807\u67aa\uff09\u3001`superbwarfare:taser_electrode`\uff08\u7535\u51fb\u5f39\uff09\n");
        sb.append("- **\u8feb\u51fb\u70ae**\uff1a`superbwarfare:mortar_shell`\n");
        sb.append("- **\u901a\u7528\u5360\u4f4d**\uff1a`@HeavyAmmo`/`@RifleAmmo`/`@HandgunAmmo`/`@ShotgunAmmo`/`@SniperAmmo`\uff08\u673a\u67aa/\u6b65\u67aa\u7b49\u5f39\u836f\u7c7b\u578b\uff09\n");
        sb.append("- \u4f8b\uff1a\u5766\u514b\u4e3b\u70ae \u2192 `superbwarfare:small_shell_ap` \u6216 `large_shell_he`\uff1b\u5bf9\u7a7a\u673a\u70ae \u2192 `superbwarfare:small_shell_aa`\uff1b\u98de\u673a\u822a\u5f39 \u2192 `superbwarfare:large_aerial_bomb`\uff1b\u9632\u7a7a\u5bfc\u5f39 \u2192 `superbwarfare:medium_anti_air_missile`\n\n");
        sb.append("### 6. Velocity \u521d\u901f\u89c4\u8303\u5316\u53c2\u8003\n\n");
        sb.append("\u6309\u6b66\u5668/\u5f39\u79cd\u9009\u62e9\u6807\u51c6 `Velocity`\uff08\u521d\u901f\uff09\u503c\uff1a\n\n");
        sb.append("- **\u673a\u70ae/\u5c0f\u53e3\u5f84\u70ae\u5f39**\uff08small_cannon_shell\uff09\uff1a`20~30`\uff08\u9ad8\u521d\u901f\u3001\u76f4\u7ebf\u5f39\u9053\uff09\n");
        sb.append("- **\u5766\u514b\u70ae/\u4e2d\u53e3\u5f84\u70ae\u5f39**\uff08cannon_shell\uff09\uff1a`25~35`\n");
        sb.append("- **\u5927\u53e3\u5f84\u70ae\u5f39**\uff08large_shell\uff09\uff1a`15~25`\n");
        sb.append("- **\u8feb\u51fb\u70ae\u5f39**\uff08mortar_shell\uff09\uff1a`8~15`\uff08\u629b\u7269\u7ebf\uff09\n");
        sb.append("- **\u69b4\u5f39\u53d1\u5c04\u5668**\uff08gun_grenade\uff09\uff1a`4~10`\n");
        sb.append("- **\u706b\u7bad**\uff08small_rocket\uff09\uff1a`15~25`\n");
        sb.append("- **\u822a\u5f39**\uff08aerial_bomb/bor_57/mk_82 \u7b49\u6295\u653e\u5f39\uff09\uff1a`0.8~1.5`\uff08\u51e0\u4e4e\u65e0\u521d\u901f\uff0c\u9760\u91cd\u529b\u4e0b\u843d\uff09\n");
        sb.append("- **\u5236\u5bfc\u5bfc\u5f39**\uff08missile/wire_guide\uff09\uff1a`2~6`\uff08\u53d1\u5c04\u540e\u5236\u5bfc\u52a0\u901f\uff09\n");
        sb.append("- \u539f\u5219\uff1a\u76f4\u7ebf\u5f39\u9053\u521d\u901f\u9ad8\uff0820+\uff09\uff0c\u629b\u7269\u7ebf/\u6295\u653e\u521d\u901f\u4f4e\uff08<15\uff09\uff0c\u5bfc\u5f39\u521d\u901f\u4e2d\u7b49\u504f\u4f4e\uff082~6\uff09\n\n");
        sb.append("### 7. ProjectileLife \u629b\u5c04\u7269\u5b58\u6d3b\u65f6\u95f4\u53c2\u8003\uff08\u5b98\u65b9\u5305\u5b9e\u6d4b\uff09\n\n");
        sb.append("`ProjectileLife` \u662f\u629b\u5c04\u7269\u5b58\u6d3b tick \u6570\uff08\u8d85\u65f6\u81ea\u52a8\u9500\u6bc1\uff09\u3002\u4ee5\u4e0b\u4e3a superbwarfare \u5b98\u65b9\u8f7d\u5177 data \u7684\u5b9e\u6d4b\u8303\u56f4\uff0c\u8bf7\u6309\u6b66\u5668\u5f39\u9053\u7c7b\u578b\u5957\u7528\uff1a\n\n");
        sb.append("- **\u9ad8\u901f\u76f4\u5c04\u6b66\u5668**\uff08\u673a\u70ae/\u5766\u514b\u70ae\u76f4\u5c04\uff0cVelocity \u2265 25\uff09\uff1a`20~40`\uff08\u5982 cannon_shell Vel=35 \u2192 Life 20~40\uff09\n");
        sb.append("- **\u4e2d\u9ad8\u901f\u76f4\u5c04**\uff08\u673a\u70ae/\u5766\u514b\u70ae\uff0cVelocity 15~25\uff09\uff1a`40~60`\uff08\u5982 small_cannon_shell Vel=18 \u2192 Life 40~60\u3001Vel=23~25 \u2192 20\uff09\n");
        sb.append("- **\u66f2\u5c04/\u8fdc\u7a0b\u706b\u70ae**\uff08\u69b4\u5f39\u70ae/\u8feb\u51fb\u70ae/\u95f4\u5c04\uff0cVelocity \u4f4e\uff09\uff1a`800`\uff08\u5b98\u65b9 cannon_shell Vel=15~18 \u7528 800\uff0c\u9700\u957f\u5b58\u6d3b\u98de\u5230\u8fdc\u8ddd\u79bb\uff09\n");
        sb.append("- \u539f\u5219\uff1a\u76f4\u7ebf\u5feb\u5f39\u77ed\u547d\uff0820~60\uff09\uff0c\u66f2\u5c04\u6162\u5f39\u957f\u547d\uff08\u6570\u767e~800\uff09\uff1b**\u907f\u514d\u9ad8\u673a\u52a8\u5f39\u8d85\u957f\u5b58\u6d3b\u5361\u670d**\n\n");
        sb.append("### 8. Spread \u6563\u5e03\u53c2\u8003\uff08\u5b98\u65b9\u5305\u5b9e\u6d4b\uff09\n\n");
        sb.append("`Spread` \u4e3a\u6b66\u5668\u6563\u5e03\u503c\uff0c\u8d8a\u5c0f\u8d8a\u51c6\u3002superbwarfare \u5b98\u65b9\u8f7d\u5177 data \u5b9e\u6d4b\u8303\u56f4\uff1a\n\n");
        sb.append("- **\u5766\u514b/\u706b\u70ae\u4e3b\u70ae**\uff08cannon_ap \u9ad8\u7cbe\u5ea6\uff0c\u5982 M1A2/T-90A/ZTZ99A/\u69b4\u5f39\u70ae\uff09\uff1a`0.02~0.06`\n");
        sb.append("- **\u673a\u70ae/\u4e2d\u53e3\u5f84**\uff0820mm/25mm/30mm\u3001cannon_ap\uff09\uff1a`0.25~3`\n");
        sb.append("- **\u91cd\u673a\u67aa**\uff0850_cal \u7b49\uff09\uff1a`5`\n");
        sb.append("- **\u9762\u6740\u4f24/\u706b\u7bad/\u9730\u5f39**\uff1a`5~6`\n");
        sb.append("- **\u822a\u5f39\u6295\u653e**\uff08aerial_bomb/bor_57\uff09\uff1a`10`\n");
        sb.append("- **\u5236\u5bfc\u5bfc\u5f39**\uff08missile/\u7ebf\u5bfc\uff09\uff1a`0~0.02`\uff08\u9ad8\u7cbe\u5ea6\u5bfb\u7684\uff09\n");
        sb.append("- \u89c4\u5f8b\uff1a\u4e3b\u70ae/\u5bfc\u5f39\u7cbe\u5ea6\u6700\u9ad8\uff080.02~0.06\uff09\uff0c\u9762\u6740\u4f24/\u822a\u5f39\u6563\u5e03\u6700\u5927\uff085~10\uff09\n\n");
        sb.append("\u8f93\u51fa\uff1a\u76f4\u63a5\u7ed9\u51fa\u5b8c\u6574\u7684 `" + id + ".json` \u5185\u5bb9\uff08JSON \u4ee3\u7801\u5757\uff09\uff0c\u672a\u8981\u6c42\u586b\u5199\u7684\u5b57\u6bb5\u4fdd\u6301\u539f\u6837\u6216\u7701\u7565\u3002\n");
        return sb.toString();
    }

    private boolean hasBuildBone(String content) {
        try {
            JsonObject geoJson = JsonParser.parseString((String)content).getAsJsonObject();
            if (!geoJson.has("minecraft:geometry")) {
                return false;
            }
            JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
            for (JsonElement geomElement : geometries) {
                JsonObject geometry = geomElement.getAsJsonObject();
                if (!geometry.has("bones")) continue;
                for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                    JsonObject bone = boneElement.getAsJsonObject();
                    if (!bone.has("name") || !bone.get("name").getAsString().equals("Build")) continue;
                    return true;
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return false;
    }

    private String detectTypeCategory(String vehicleJson, String modelJson) {
        if (modelJson == null || modelJson.isEmpty()) {
            return "CAR";
        }
        try {
            JsonObject geoJson = JsonParser.parseString((String)modelJson).getAsJsonObject();
            if (!geoJson.has("minecraft:geometry")) {
                return "CAR";
            }
            JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
            for (JsonElement geomElement : geometries) {
                JsonObject geometry = geomElement.getAsJsonObject();
                if (!geometry.has("bones")) continue;
                for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                    JsonObject bone = boneElement.getAsJsonObject();
                    if (!bone.has("name")) continue;
                    switch (bone.get("name").getAsString().toUpperCase()) {
                        case "HELI": {
                            return "HELI";
                        }
                        case "SHIP": {
                            return "SHIP";
                        }
                        case "AIRSHIP": {
                            return "AIRSHIP";
                        }
                        case "TANK": {
                            return "TANK";
                        }
                        case "PLANE": {
                            return "PLANE";
                        }
                        case "CAR": {
                            return "CAR";
                        }
                    }
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "CAR";
    }

    private String buildTypeGuide(String category) {
        StringBuilder g = new StringBuilder();
        g.append("## \u7c7b\u578b\u4e13\u5c5e\u586b\u5199\u6307\u5357\uff08").append(category).append("\uff09\n\n");
        switch (category) {
            case "TANK": {
                g.append("- `MaxHealth`\uff1a**300-500**\uff08\u5766\u514b\uff09\n");
                g.append("- `HudType`\uff1a`@Land`\n");
                g.append("- `EngineType`\uff1a`Track`\uff08\u5c65\u5e26\uff09\uff1b`EngineSound` \u586b\u97f3\u6548 ID\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\uff1a`Buoyancy`, `EnergyCostRate`, `WheelRotSpeed`, `WheelDifferential`, `TrackRotSpeed`, `TrackDifferential`, `MaxForwardSpeedRate`, `MaxBackwardSpeedRate`, `Increment`, `Decrement`, `SteeringSpeed`, `EngineSoundVolume`\n");
                g.append("- \u5efa\u8bae\u8865\uff1a`TrackDistanceMultiply`\uff08\u5c65\u5e26\u52a8\u753b\u901f\u5ea6\uff09\u3001`InertiaRotateRate`\uff08\u8f6c\u5411\u60ef\u6027\uff09\n");
                g.append("- \u70ae\u5854\uff1a`TurretPos`/`BarrelPos`\uff08\u82e5\u6a21\u578b\u6709 turret/barrel \u9aa8\u9abc\uff09\u3001`TurretTurnSpeed`\uff08\u5982 `1.5 1.5`\uff09\u3001`TurretYawRange`\uff08\u5982 `-75 75`\uff09\u3001`TurretPitchRange`\uff08\u5982 `-9 20`\uff09\u3001`TurretControllerIndex`\n");
                g.append("- \u5730\u5f62\uff1a`TerrainCompat`\uff08\u5c65\u5e26\u63a5\u5730\u4f4d\u7f6e\u6570\u7ec4\uff09\n");
                g.append("- \u6b66\u5668\u5178\u578b\uff1a`Cannon`\uff08\u4e3b\u70ae\uff09+ `MachineGun`/`Coax`\uff08\u540c\u8f74\u673a\u67aa\uff09\n");
                g.append("- \u65e0\u9700\u5b57\u6bb5\uff1a`PitchSpeed`/`YawSpeed`/`RollSpeed`/`LiftSpeed`/`HasGear`\uff08\u90a3\u662f\u98de\u884c\u5668\u7528\u7684\uff09\n");
                break;
            }
            case "CAR": {
                g.append("- `MaxHealth`\uff1a**100-300**\uff08\u6c7d\u8f66\uff09\n");
                g.append("- `HudType`\uff1a`@Land`\n");
                g.append("- `EngineType`\uff1a`Wheel`\uff08\u8f6e\u5f0f\uff09\uff1b`EngineSound` \u586b\u97f3\u6548 ID\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\uff1a`Buoyancy`, `EnergyCostRate`, `WheelRotSpeed`, `WheelDifferential`, `MaxForwardSpeedRate`, `MaxBackwardSpeedRate`, `Increment`, `Decrement`, `SteeringSpeed`, `EngineSoundVolume`\n");
                g.append("- \u70ae\u5854\uff1a`TurretPos`/`BarrelPos`\uff08\u82e5\u6a21\u578b\u6709\uff09\u3001`TurretTurnSpeed`/`TurretYawRange`/`TurretPitchRange`\n");
                g.append("- \u5730\u5f62\uff1a`TerrainCompat`\n");
                g.append("- \u6b66\u5668\u5178\u578b\uff1a`Cannon`/`MachineGun`/`Missile`\uff08\u6309\u6a21\u578b\u5c04\u51fb\u70b9\u9aa8\u9abc\uff09\n");
                g.append("- \u65e0\u9700\u5b57\u6bb5\uff1a\u98de\u884c\u5668\u7684 `PitchSpeed`/`YawSpeed`/`RollSpeed`/`LiftSpeed`/`HasGear`\n");
                break;
            }
            case "PLANE": {
                g.append("- `MaxHealth`\uff1a**100-300**\uff08\u98de\u884c\u5668\uff09\n");
                g.append("- `HudType`\uff1a`@Aircraft`\n");
                g.append("- `EngineType`\uff1a`Aircraft`\uff1b`EngineSound` \u586b\u55b7\u6c14/\u87ba\u65cb\u6868\u97f3\u6548\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\uff1a`HasGear`, `EnergyCostRate`, `Increment`, `Decrement`, `PitchSpeed`, `YawSpeed`, `RollSpeed`, `LiftSpeed`, `SpeedRate`, `GearRotateAngle`, `EngineStartSound`, `EngineSoundVolume`\n");
                g.append("- \u5efa\u8bae\u8865\uff1a`HasDecoy: true`\uff08\u8bf1\u9975/\u70ed\u7130\u5f39\uff09\u3001`ThirdPersonCameraPos`\u3001`RotateOffsetHeight`\n");
                g.append("- \u6b66\u5668\u5178\u578b\uff1a`Cannon`\uff08\u673a\u70ae\uff09+ `Missile`\uff08\u7a7a\u7a7a/\u7a7a\u5730\uff09+ `Rocket` + `Bomb`\uff08\u6309\u6a21\u578b `CannonPos`/`MissilePos` \u9aa8\u9abc\uff09\n");
                g.append("- \u65e0\u9700\u5b57\u6bb5\uff1a\u9646\u5730\u8f66\u7684 `TerrainCompat` \u53ef\u4e0d\u586b\uff1b`TrackDistanceMultiply` \u4e0d\u9700\u8981\n");
                break;
            }
            case "HELI": {
                g.append("- `MaxHealth`\uff1a**100-300**\uff08\u98de\u884c\u5668\uff09\n");
                g.append("- `HudType`\uff1a`@Helicopter`\n");
                g.append("- `EngineType`\uff1a`Helicopter`\uff1b`EngineSound` \u586b\u65cb\u7ffc\u97f3\u6548\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\uff1a`EnergyCostRate`, `Increment`, `Decrement`, `PitchSpeed`, `YawSpeed`, `RollSpeed`, `LiftSpeed`, `Speed`, `EngineStartSound`, `EngineSoundVolume`\n");
                g.append("- \u5efa\u8bae\u8865\uff1a`HasDecoy: true`\u3001`ThirdPersonCameraPos`\u3001`RotateOffsetHeight`\n");
                g.append("- \u6b66\u5668\u5178\u578b\uff1a`Cannon`\uff08\u673a\u70ae\uff0c\u53ef\u65cb\u8f6c\uff09+ `Rocket` + `Missile`\uff08\u542b `@Missile` \u7b49\uff0c\u6309\u6a21\u578b\u9aa8\u9abc\uff09\n");
                g.append("- \u65e0\u9700\u5b57\u6bb5\uff1a`HasGear`/`SpeedRate`\uff08\u56fa\u5b9a\u7ffc\u7279\u6709\uff09\u3001`TrackDistanceMultiply`\n");
                break;
            }
            case "SHIP": {
                g.append("- `HudType`\uff1a`@Boat`\uff08\u5982\u65e0\u5219 `@Land`\uff09\n");
                g.append("- `EngineType`\uff1a`Boat`\uff1b`EngineSound` \u586b\u5f15\u64ce\u97f3\u6548\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\u6d6e\u529b\u76f8\u5173\uff1a`Buoyancy`, `EnergyCostRate`, `Increment`, `Decrement`, `SteeringSpeed`, `EngineSoundVolume` \u7b49\n");
                g.append("- \u5efa\u8bae\u8865\uff1a`waterMask` \u6c34\u9762\u906e\u7f69\uff1b`TerrainCompat` \u53ef\u7701\u7565\n");
                break;
            }
            case "AIRSHIP": {
                g.append("- `MaxHealth`\uff1a**100-300**\uff08\u98de\u884c\u5668\uff09\n");
                g.append("- \u98de\u8247\uff08\u53c2\u8003 superbwarfare \u539f\u7248 kirov \u57fa\u6d1b\u592b\uff09\uff1a\u6d6e\u7a7a\u91cd\u578b\u5e73\u53f0\uff0c\u6574\u4f53 `Gravity: 0`\n");
                g.append("- `HudType`\uff1a`@Kirov`\uff08\u539f\u7248\u98de\u8247\u4e13\u7528 HUD\uff09\n");
                g.append("- `EngineType`\uff1a`AirShip`\uff1b`EngineSound` \u586b\u98de\u8247\u5f15\u64ce\u97f3\u6548\n");
                g.append("- `EngineInfo` \u5e94\u5305\u542b\uff08kirov \u539f\u7248\u952e\uff09\uff1a`Buoyancy`, `EnergyCostRate`, `MaxForwardSpeedRate`, `MaxBackwardSpeedRate`, `MaxUpSpeedRate`, `MaxDownSpeedRate`, `Increment`, `Decrement`, `SteeringSpeed`, `EngineSoundVolume`, `FloatHeight`, `SprintMultiply`\n");
                g.append("- \u7279\u6709\u5b57\u6bb5\uff1a`FloatHeight`\uff08\u60ac\u6d6e\u9ad8\u5ea6\uff0ckirov \u4e3a 3.5\uff09\u3001`Buoyancy`\uff08\u6d6e\u529b\uff0ckirov \u4e3a 0.1\uff09\u3001`MaxUpSpeedRate`/`MaxDownSpeedRate`\uff08\u5782\u76f4\u722c\u5347/\u4e0b\u964d\u901f\u5ea6\uff09\n");
                g.append("- \u5efa\u8bae\u8865\uff1a`HasDecoy: true`\u3001`ThirdPersonCameraPos`\uff08\u5982 `[0, 11, 44]`\uff09\u3001`RotateOffsetHeight`\u3001`VehicleContainerType: Huge`\n");
                g.append("- \u6b66\u5668\u5178\u578b\uff1a`Bomb`\uff08\u822a\u5f39\u6295\u653e\u2014\u2014`AddShooterDeltaMovement: true`\u3001`ShootPos.Directions: [\"DeltaMovement\"]`\u3001`ShootDirectionForHud: \"Bomb\"`\u3001`Velocity: 1.0`\uff09\uff0c\u4e5f\u53ef\u5e26 `Missile`\n");
                g.append("- \u65e0\u9700\u5b57\u6bb5\uff1a\u9646\u5730\u8f66\u7684 `TerrainCompat`\u3001`TrackDistanceMultiply` \u4e0d\u9700\u8981\n");
                break;
            }
            default: {
                g.append("- \u9646\u5730\u8f7d\u5177\u9ed8\u8ba4\uff1a`HudType: @Land`\uff0c`EngineType` \u6309\u6a21\u578b\uff08`Track`/`Wheel`\uff09\n");
                g.append("- `EngineInfo` \u53c2\u8003\uff1a`EnergyCostRate`, `MaxForwardSpeedRate`, `MaxBackwardSpeedRate`, `Increment`, `Decrement`, `SteeringSpeed`, `EngineSoundVolume`\uff08\u5c65\u5e26\u518d\u52a0 `TrackRotSpeed`/`TrackDifferential`\uff0c\u8f6e\u5f0f\u52a0 `WheelRotSpeed`/`WheelDifferential`\uff09\n");
                g.append("- \u6709\u70ae\u5854\u8865 `TurretPos`/`BarrelPos`/`TurretTurnSpeed`/`TurretYawRange`/`TurretPitchRange`\n");
            }
        }
        g.append("\n## \u901f\u5ea6\u53c2\u8003\uff08\u6240\u6709\u8f7d\u5177\u7edf\u4e00\uff09\n\n");
        g.append("- `MaxEnergy`\uff1a**\u9ed8\u8ba4 100000**\uff08\u6240\u6709\u8f7d\u5177\uff09\n");
        g.append("- `Increment`/`Decrement`\uff1a\u524d\u8fdb/\u5012\u8f66**\u52a0\u901f\u5ea6**\uff0c**\u5efa\u8bae\u7edf\u4e00\u586b `0.01`**\n");
        g.append("- `MaxForwardSpeedRate`\uff08\u6700\u5927\u524d\u8fdb\u901f\u5ea6\uff09\uff1a\u586b `1` \u65f6\u6700\u5feb 80 km/h \u2192 \u6bcf `0.01` = 0.8 km/h\u3002\u6309\u76ee\u6807\u901f\u5ea6\u6362\u7b97\uff1a\u76ee\u6807km/h \u00f7 0.8 \u00d7 0.01\uff08\u4f8b\uff1a60 km/h \u2192 60 \u00f7 0.8 \u00d7 0.01 = 0.75\uff1b40 km/h \u2192 0.5\uff09\n");
        g.append("- `MaxBackwardSpeedRate`\uff08\u6700\u5927\u5012\u8f66\u901f\u5ea6\uff09\uff1a\u586b `1` \u65f6\u6700\u5feb 64 km/h \u2192 \u6bcf `0.01` = 0.64 km/h\u3002\u6309\u76ee\u6807\u901f\u5ea6\u6362\u7b97\uff1a\u76ee\u6807km/h \u00f7 0.64 \u00d7 0.01\uff08\u4f8b\uff1a32 km/h \u2192 32 \u00f7 0.64 \u00d7 0.01 = 0.5\uff1b16 km/h \u2192 0.25\uff09\n");
        g.append("\n## HudType \u53ef\u9009\u503c\uff08\u4f9b\u9009\u62e9\uff09\n\n");
        g.append("- `@Land`\uff1a\u9646\u5730\u8f7d\u5177\uff08\u5766\u514b/\u88c5\u7532\u8f66/\u6c7d\u8f66\uff09\n");
        g.append("- `@Aircraft`\uff1a\u56fa\u5b9a\u7ffc\u98de\u884c\u5668\n");
        g.append("- `@OldAircraft`\uff1a\u65e7\u5f0f/\u87ba\u65cb\u6868\u56fa\u5b9a\u7ffc\uff08\u5982\u4e8c\u6218\u87ba\u65cb\u6868\u673a\uff09\n");
        g.append("- `@Helicopter`\uff1a\u76f4\u5347\u673a\n");
        g.append("- `@Artillery`\uff1a\u81ea\u884c\u706b\u70ae/\u706b\u70ae\u7c7b\n");
        g.append("- `@Kirov`\uff1a\u98de\u8247\uff08\u539f\u7248\u57fa\u6d1b\u592b\u4e13\u7528 HUD\uff09\n\n");
        g.append("## Type \u53ef\u9009\u503c\uff08\u4f9b\u9009\u62e9\uff09\n\n");
        g.append("- `Tank`\uff1a\u5766\u514b\n");
        g.append("- `APC`\uff1a\u88c5\u7532\u8fd0\u5175\u8f66/\u6b65\u5175\u6218\u8f66\n");
        g.append("- `Car`\uff1a\u6c7d\u8f66/\u8f6e\u5f0f\u8f66\u8f86\n");
        g.append("- `AA`\uff1a\u9632\u7a7a\n");
        g.append("- `Artillery`\uff1a\u81ea\u884c\u706b\u70ae\n");
        g.append("- `Defense`\uff1a\u56fa\u5b9a\u9632\u5fa1\u8bbe\u65bd\n");
        g.append("- `Airplane`\uff1a\u56fa\u5b9a\u7ffc\u98de\u884c\u5668\n");
        g.append("- `Helicopter`\uff1a\u76f4\u5347\u673a\n");
        g.append("- `AirShip`\uff1a\u98de\u8247\n");
        g.append("- `Boat`\uff1a\u8230\u8239/\u6c34\u4e0a\u8f7d\u5177\n");
        g.append("- `Drone`\uff1a\u65e0\u4eba\u673a\n");
        g.append("- `Special`\uff1a\u7279\u6b8a\u8f7d\u5177\n\n");
        g.append("## EngineType \u53ef\u9009\u503c\uff08\u4f9b\u9009\u62e9\uff09\n\n");
        g.append("- `Track`\uff1a\u5c65\u5e26\uff08\u5766\u514b/\u5c65\u5e26\u88c5\u7532\u8f66\uff09\n");
        g.append("- `Wheel`\uff1a\u8f6e\u5f0f\uff08\u6c7d\u8f66/\u8f6e\u5f0f\u88c5\u7532\u8f66\uff09\n");
        g.append("- `Aircraft`\uff1a\u56fa\u5b9a\u7ffc\u55b7\u6c14/\u87ba\u65cb\u6868\u5f15\u64ce\n");
        g.append("- `Helicopter`\uff1a\u76f4\u5347\u673a\u65cb\u7ffc\u5f15\u64ce\n");
        g.append("- `AirShip`\uff1a\u98de\u8247\u5f15\u64ce\n");
        g.append("- `Ship`\uff1a\u8230\u8239\u5f15\u64ce\n");
        g.append("- `Empty`\uff1a\u65e0\u5f15\u64ce\uff08\u9759\u6001/\u9632\u5fa1\u8bbe\u65bd\uff09\n");
        g.append("- `Fixed`\uff1a\u56fa\u5b9a\u5f15\u64ce\uff08\u5176\u4ed6\u56fa\u5b9a\u7c7b\u578b\uff09\n");
        g.append("- `WheelChair`/`Tom6`\uff1a\u7279\u6b8a\uff08\u4e00\u822c\u4e0d\u7528\uff09\n\n");
        g.append("\n## ContainerIcon \u8f7d\u5177\u7c7b\u578b\u56fe\u6807\u9009\u62e9\n\n");
        g.append("\u6839\u636e\u8f7d\u5177\u7684\u56fd\u5bb6/\u9635\u8425\u4e0e\u7c7b\u522b\uff0c\u4ece\u4ee5\u4e0b\u56fe\u6807\u4e2d\u9009\u62e9 `ContainerIcon`\uff08\u503c\u4e3a\u5b8c\u6574\u8d44\u6e90\u8def\u5f84 `dragonrise_reforge:textures/gui/vehicle/type/<\u6587\u4ef6\u540d>`\uff09\uff1a\n\n");
        g.append("**\u56fd\u5bb6/\u9635\u8425\u524d\u7f00**\uff1a`cn`=\u4e2d\u56fd\u3001`us`=\u7f8e\u56fd\u3001`ru`=\u4fc4\u7f57\u65af\u3001`uk`=\u82f1\u56fd\u3001`jp`=\u65e5\u672c\u3001`fr`=\u6cd5\u56fd\u3001`gm`=\u5fb7\u56fd\u3001`se`=\u745e\u5178\u3001`ussr`=\u82cf\u8054\u3001`dr`=**\u4e8c\u6218\u5fb7\u56fd**\u3001`blue`=\u84dd\u65b9\u9635\u8425\u3001`red`=\u7ea2\u65b9\u9635\u8425\n");
        g.append("**\u7c7b\u522b\u540e\u7f00**\uff1a`land`=\u9646\u5730\u8f7d\u5177\u3001`aircraft`=\u98de\u884c\u5668\u3001`water`=\u6c34\u4e0a\u8f7d\u5177\n\n");
        g.append("**\u672c\u6a21\u7ec4\u53ef\u7528\u56fe\u6807**\uff1a\n");
        if (this.availableTypeIcons.isEmpty()) {
            g.append("\uff08\u672a\u626b\u63cf\u5230\u56fe\u6807\u6587\u4ef6\uff09\n");
        } else {
            g.append("```\n");
            for (String icon : this.availableTypeIcons) {
                g.append(icon).append("\n");
            }
            g.append("```\n");
        }
        g.append("**superbwarfare \u901a\u7528\u56fe\u6807**\uff08`superbwarfare:textures/gui/vehicle/type/<\u540d>.png`\uff09\uff1a`land`\uff08\u9646\u5730\uff09\u3001`aircraft`\uff08\u56fa\u5b9a\u7ffc\uff09\u3001`helicopter`\uff08\u76f4\u5347\u673a\uff09\u3001`water`\uff08\u6c34\u4e0a\uff09\u3001`airship`\uff08\u98de\u8247\uff09\u3001`defense`\uff08\u9632\u5fa1\uff09\u3001`civilian`\uff08\u6c11\u7528\uff09\u3001`otto`\n");
        g.append("- \u4f8b\uff1a\u4e2d\u56fd\u9646\u5730\u8f7d\u5177 \u2192 `dragonrise_reforge:textures/gui/vehicle/type/cn_land.png`\uff1b\u4e8c\u6218\u5fb7\u56fd\u9646\u5730 \u2192 `dragonrise_reforge:textures/gui/vehicle/type/dr_land.png`\uff1b\u7f8e\u56fd\u98de\u884c\u5668 \u2192 `dragonrise_reforge:textures/gui/vehicle/type/us_aircraft.png`\n\n");
        g.append("\n- \u901a\u7528\uff1a`VehicleIcon` \u586b `dragonrise_reforge:textures/vehicle_icon/").append("XXX").append("_icon.png`\uff1b`VehicleContainerType` \u6309\u8f7d\u5177\u5927\u5c0f\uff08`Empty`/`Mini`/`Small`/`Medium`/`Large`/`Huge`\uff09\n");
        return g.toString();
    }

    public String m_6055_() {
        return "DragonRise Vehicle Data Completeness Checker";
    }

    static {
        OFFICIAL_REFS.put("TANK", "**\u5b98\u65b9\u53c2\u8003\uff1aM1A2 \u827e\u5e03\u62c9\u59c6\u65af\uff08m_1a_2.json\uff09**\n```\n\"Type\": \"Tank\", \"HudType\": \"@Land\", \"EngineType\": \"Track\",\n\"MaxHealth\": 500, \"MaxEnergy\": 10000000, \"VehicleContainerType\": \"Medium\",\n\"EngineInfo\": { \"Buoyancy\": 0, \"EnergyCostRate\": 128, \"WheelRotSpeed\": 1.25, \"WheelDifferential\": 0.75,\n  \"TrackRotSpeed\": 1.9, \"TrackDifferential\": 0.6, \"MaxForwardSpeedRate\": 0.78, \"MaxBackwardSpeedRate\": 0.5,\n  \"Increment\": 0.02, \"Decrement\": 0.01, \"SteeringSpeed\": 0.1, \"EngineSoundVolume\": 0.6 },\n\"Weapons\": \u952e\u4e3a Cannon\uff08\u4e3b\u70ae\uff09\u3001MachineGun\uff08\u540c\u8f74\u673a\u67aa\uff09\u3001PassengerMachineGun\n```");
        OFFICIAL_REFS.put("CAR", "**\u5b98\u65b9\u53c2\u8003\uff1aLAV-25 \u8f6e\u5f0f\u88c5\u7532\u8f66\uff08lav_25.json\uff09**\n```\n\"Type\": \"APC\", \"HudType\": \"@Land\", \"EngineType\": \"Wheel\",\n\"MaxHealth\": 300, \"MaxEnergy\": 5000000, \"VehicleContainerType\": \"Medium\",\n\"EngineInfo\": { \"Buoyancy\": 0.052, \"EnergyCostRate\": 64, \"WheelRotSpeed\": 1.75, \"WheelDifferential\": 0.3,\n  \"MaxForwardSpeedRate\": 0.95, \"MaxBackwardSpeedRate\": 0.6, \"Increment\": 0.027, \"Decrement\": 0.017,\n  \"SteeringSpeed\": 0.065 },\n\"Weapons\": \u952e\u4e3a Cannon\uff0825mm\u673a\u70ae\uff09\u3001MachineGun\uff08\u540c\u8f74\u673a\u67aa\uff09\u3001Missile\n```");
        OFFICIAL_REFS.put("PLANE", "**\u5b98\u65b9\u53c2\u8003\uff1aA-10 \u653b\u51fb\u673a\uff08a_10a.json\uff09**\n```\n\"Type\": \"Airplane\", \"HudType\": \"@Aircraft\", \"EngineType\": \"Aircraft\",\n\"MaxHealth\": 350, \"MaxEnergy\": 10000000, \"VehicleContainerType\": \"Small\",\n\"EngineInfo\": { \"EnergyCostRate\": 256, \"Increment\": 1, \"Decrement\": 1, \"PitchSpeed\": 1, \"YawSpeed\": 1,\n  \"RollSpeed\": 1, \"LiftSpeed\": 1, \"SpeedRate\": 1, \"GearRotateAngle\": 85, \"EngineSoundVolume\": 0.8 },\n\"Weapons\": \u952e\u4e3a Cannon\uff08\u673a\u70ae\uff09\u3001Rocket\u3001Bomb\u3001Missile\n```");
        OFFICIAL_REFS.put("HELI", "**\u5b98\u65b9\u53c2\u8003\uff1a\u7c73-28 \u6b66\u88c5\u76f4\u5347\u673a\uff08mi_28.json\uff09**\n```\n\"Type\": \"Helicopter\", \"HudType\": \"@Helicopter\", \"EngineType\": \"Helicopter\",\n\"MaxHealth\": 350, \"MaxEnergy\": 10000000, \"VehicleContainerType\": \"Small\",\n\"EngineInfo\": { \"EnergyCostRate\": 320, \"Increment\": 0.8, \"Decrement\": 0.8, \"PitchSpeed\": 0.75,\n  \"YawSpeed\": 0.85, \"RollSpeed\": 0.6, \"LiftSpeed\": 1, \"Speed\": 0.97, \"EngineSoundVolume\": 2 },\n\"Weapons\": \u952e\u4e3a Cannon\uff08\u673a\u70ae\uff09\u3001Rocket\u3001@Missile\u3001SeekMissile\u3001DriverAAMissile\n```");
        OFFICIAL_REFS.put("SHIP", "**\u5b98\u65b9\u53c2\u8003\uff1a\u5feb\u8247\uff08speedboat.json\uff09**\n```\n\"Type\": \"Boat\", \"EngineType\": \"Ship\",\n\"MaxHealth\": 200, \"MaxEnergy\": 500000, \"VehicleContainerType\": \"Medium\",\n\"EngineInfo\": { \"Buoyancy\": 0.09, \"EnergyCostRate\": 48, \"MaxForwardSpeedRate\": 1.5,\n  \"MaxBackwardSpeedRate\": 1.0, \"Increment\": 0.035, \"Decrement\": 0.022, \"SteeringSpeed\": 0.15,\n  \"BodyRollRate\": 0.8 },\n\"Weapons\": \u952e\u4e3a MachineGun\n```");
        OFFICIAL_REFS.put("AIRSHIP", "**\u5b98\u65b9\u53c2\u8003\uff1a\u57fa\u6d1b\u592b\u98de\u8247\uff08kirov.json\uff09**\n```\n\"Type\": \"AirShip\", \"HudType\": \"@Kirov\", \"EngineType\": \"AirShip\", \"Gravity\": 0,\n\"MaxHealth\": 4000, \"MaxEnergy\": 40000000, \"VehicleContainerType\": \"Huge\",\n\"EngineInfo\": { \"Buoyancy\": 0.1, \"EnergyCostRate\": 128, \"MaxForwardSpeedRate\": 0.7,\n  \"MaxBackwardSpeedRate\": 0.6, \"MaxUpSpeedRate\": 0.3, \"MaxDownSpeedRate\": 0.3, \"Increment\": 0.4,\n  \"Decrement\": 0.3, \"SteeringSpeed\": 0.3, \"EngineSoundVolume\": 0.4, \"FloatHeight\": 3.5, \"SprintMultiply\": 3 },\n\"Weapons\": \u952e\u4e3a Bomb\uff08\u822a\u5f39\uff09\n```");
    }
}

