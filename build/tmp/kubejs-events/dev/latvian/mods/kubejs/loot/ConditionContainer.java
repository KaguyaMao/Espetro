/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.level.storage.loot.LootContext$EntityTarget
 */
package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.Map;
import net.minecraft.world.level.storage.loot.LootContext;

public interface ConditionContainer {
    public ConditionContainer addCondition(JsonObject var1);

    default public ConditionContainer randomChance(double chance) {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:random_chance");
        json.addProperty("chance", (Number)chance);
        return this.addCondition(json);
    }

    default public ConditionContainer randomChanceWithLooting(double chance, double multiplier) {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:random_chance_with_looting");
        json.addProperty("chance", (Number)chance);
        json.addProperty("looting_multiplier", (Number)multiplier);
        return this.addCondition(json);
    }

    default public ConditionContainer survivesExplosion() {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:survives_explosion");
        return this.addCondition(json);
    }

    default public ConditionContainer entityProperties(LootContext.EntityTarget entity, JsonObject properties) {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:entity_properties");
        json.addProperty("entity", entity.f_78994_);
        json.add("predicate", (JsonElement)properties);
        return this.addCondition(json);
    }

    default public ConditionContainer killedByPlayer() {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:killed_by_player");
        return this.addCondition(json);
    }

    default public ConditionContainer entityScores(LootContext.EntityTarget entity, Map<String, Object> scores) {
        JsonObject json = new JsonObject();
        json.addProperty("condition", "minecraft:entity_scores");
        json.addProperty("entity", entity.f_78994_);
        JsonObject s = new JsonObject();
        for (Map.Entry<String, Object> entry : scores.entrySet()) {
            s.add(entry.getKey(), UtilsJS.numberProviderJson(UtilsJS.numberProviderOf(entry.getValue())));
        }
        json.add("scores", (JsonElement)s);
        return this.addCondition(json);
    }
}

