/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.Serializer
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
 */
package net.minecraftforge.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

public class LootTableIdCondition
implements LootItemCondition {
    public static final LootItemConditionType LOOT_TABLE_ID = new LootItemConditionType((net.minecraft.world.level.storage.loot.Serializer)new Serializer());
    public static final ResourceLocation UNKNOWN_LOOT_TABLE = new ResourceLocation("forge", "unknown_loot_table");
    private final ResourceLocation targetLootTableId;

    private LootTableIdCondition(ResourceLocation targetLootTableId) {
        this.targetLootTableId = targetLootTableId;
    }

    public LootItemConditionType m_7940_() {
        return LOOT_TABLE_ID;
    }

    public boolean test(LootContext lootContext) {
        return lootContext.getQueriedLootTableId().equals((Object)this.targetLootTableId);
    }

    public static Builder builder(ResourceLocation targetLootTableId) {
        return new Builder(targetLootTableId);
    }

    public static class Builder
    implements LootItemCondition.Builder {
        private final ResourceLocation targetLootTableId;

        public Builder(ResourceLocation targetLootTableId) {
            if (targetLootTableId == null) {
                throw new IllegalArgumentException("Target loot table must not be null");
            }
            this.targetLootTableId = targetLootTableId;
        }

        public LootItemCondition m_6409_() {
            return new LootTableIdCondition(this.targetLootTableId);
        }
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<LootTableIdCondition> {
        public void serialize(JsonObject object, LootTableIdCondition instance, JsonSerializationContext ctx) {
            object.addProperty("loot_table_id", instance.targetLootTableId.toString());
        }

        public LootTableIdCondition deserialize(JsonObject object, JsonDeserializationContext ctx) {
            return new LootTableIdCondition(new ResourceLocation(GsonHelper.m_13906_((JsonObject)object, (String)"loot_table_id")));
        }
    }
}

