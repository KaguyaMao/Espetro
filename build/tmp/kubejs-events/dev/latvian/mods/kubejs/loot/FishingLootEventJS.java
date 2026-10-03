/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.loot.LootBuilder;
import dev.latvian.mods.kubejs.loot.LootEventJS;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;

public class FishingLootEventJS
extends LootEventJS {
    public FishingLootEventJS(Map<ResourceLocation, JsonElement> c) {
        super(c);
    }

    @Override
    public String getType() {
        return "minecraft:fishing";
    }

    @Override
    public String getDirectory() {
        return "gameplay/fishing";
    }

    public void addFishing(ResourceLocation id, Consumer<LootBuilder> b) {
        LootBuilder builder = this.createLootBuilder(null, b);
        JsonObject json = builder.toJson();
        this.addJson(builder.customId == null ? id : builder.customId, json);
    }
}

