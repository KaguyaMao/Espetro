/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Builder
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Entry
 *  com.atsuishio.superbwarfare.data.loot.WreckageLootData$Pool
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$PathProvider
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.common.data.ExistingFileHelper
 *  net.minecraftforge.registries.ForgeRegistries
 */
package com.redabysslucia.dragonrise_reforge.datagen.base;

import com.atsuishio.superbwarfare.data.loot.WreckageLootData;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class SbwWreckageLootProvider
implements DataProvider {
    protected final PackOutput output;
    protected final ExistingFileHelper existingFileHelper;
    protected final List<WreckageLootData> lootData = new ArrayList<WreckageLootData>();

    public SbwWreckageLootProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public abstract void generate();

    public void add(EntityType<? extends VehicleEntity> type, WreckageLootData.Builder builder) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(type);
        if (id != null) {
            this.lootData.add(builder.build(id));
        }
    }

    private JsonObject serialize(WreckageLootData data) {
        JsonObject root = new JsonObject();
        root.addProperty("ID", data.getId().toString());
        JsonArray pools = new JsonArray();
        for (WreckageLootData.Pool pool : data.getPools()) {
            JsonObject poolObj = new JsonObject();
            JsonArray entries = new JsonArray();
            for (WreckageLootData.Entry entry : pool.getEntries()) {
                JsonObject entryObj = new JsonObject();
                entryObj.addProperty("Name", entry.getName());
                entryObj.addProperty("Count", (Number)entry.getCount());
                entryObj.addProperty("Chance", (Number)entry.getChance());
                entries.add((JsonElement)entryObj);
            }
            poolObj.add("Entries", (JsonElement)entries);
            poolObj.addProperty("Rolls", (Number)pool.getRolls());
            poolObj.addProperty("Source", pool.getSource());
            poolObj.addProperty("Type", pool.getType().name().toLowerCase());
            pools.add((JsonElement)poolObj);
        }
        root.add("Pools", (JsonElement)pools);
        return root;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        this.generate();
        ArrayList<CompletableFuture> list = new ArrayList<CompletableFuture>();
        PackOutput.PathProvider pathProvider = this.output.m_245269_(PackOutput.Target.DATA_PACK, "sbw/loot");
        for (WreckageLootData data : this.lootData) {
            ResourceLocation id = data.getId();
            if (this.existingFileHelper.exists(id, PackType.SERVER_DATA, ".json", "sbw/loot")) {
                throw new IllegalArgumentException("Duplicate wreckage loot data: " + id);
            }
            Path path = pathProvider.m_245731_(id);
            list.add(DataProvider.m_253162_((CachedOutput)pOutput, (JsonElement)this.serialize(data), (Path)path));
        }
        return CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
    }

    public String m_6055_() {
        return "DragonRise Wreckage Loot";
    }
}

