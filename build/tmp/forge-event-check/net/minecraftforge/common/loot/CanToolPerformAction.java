/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSerializationContext
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.Serializer
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParam
 *  net.minecraft.world.level.storage.loot.parameters.LootContextParams
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.ToolAction;
import org.jetbrains.annotations.NotNull;

public class CanToolPerformAction
implements LootItemCondition {
    public static final LootItemConditionType LOOT_CONDITION_TYPE = new LootItemConditionType((net.minecraft.world.level.storage.loot.Serializer)new Serializer());
    final ToolAction action;

    public CanToolPerformAction(ToolAction action) {
        this.action = action;
    }

    @NotNull
    public LootItemConditionType m_7940_() {
        return LOOT_CONDITION_TYPE;
    }

    @NotNull
    public Set<LootContextParam<?>> m_6231_() {
        return ImmutableSet.of((Object)LootContextParams.f_81463_);
    }

    public boolean test(LootContext lootContext) {
        ItemStack itemstack = (ItemStack)lootContext.m_78953_(LootContextParams.f_81463_);
        return itemstack != null && itemstack.canPerformAction(this.action);
    }

    public static LootItemCondition.Builder canToolPerformAction(ToolAction action) {
        return () -> new CanToolPerformAction(action);
    }

    public static class Serializer
    implements net.minecraft.world.level.storage.loot.Serializer<CanToolPerformAction> {
        public void serialize(JsonObject json, CanToolPerformAction itemCondition, @NotNull JsonSerializationContext context) {
            json.addProperty("action", itemCondition.action.name());
        }

        @NotNull
        public CanToolPerformAction deserialize(JsonObject json, @NotNull JsonDeserializationContext context) {
            return new CanToolPerformAction(ToolAction.get(json.get("action").getAsString()));
        }
    }
}

