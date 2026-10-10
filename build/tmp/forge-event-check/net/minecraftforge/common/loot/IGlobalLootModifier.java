/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.util.ExtraCodecs
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.loot;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Function;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.LootModifierManager;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public interface IGlobalLootModifier {
    public static final Codec<IGlobalLootModifier> DIRECT_CODEC = ExtraCodecs.m_184415_(() -> ForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS.get().getCodec()).dispatch(IGlobalLootModifier::codec, Function.identity());
    public static final Codec<LootItemCondition[]> LOOT_CONDITIONS_CODEC = Codec.PASSTHROUGH.flatXmap(d -> {
        try {
            LootItemCondition[] conditions = (LootItemCondition[])LootModifierManager.GSON_INSTANCE.fromJson(IGlobalLootModifier.getJson(d), LootItemCondition[].class);
            return DataResult.success((Object)conditions);
        }
        catch (JsonSyntaxException e) {
            LootModifierManager.LOGGER.warn("Unable to decode loot conditions", (Throwable)e);
            return DataResult.error(e::getMessage);
        }
    }, conditions -> {
        try {
            JsonElement element = LootModifierManager.GSON_INSTANCE.toJsonTree(conditions);
            return DataResult.success((Object)new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)element));
        }
        catch (JsonSyntaxException e) {
            LootModifierManager.LOGGER.warn("Unable to encode loot conditions", (Throwable)e);
            return DataResult.error(e::getMessage);
        }
    });

    public static <U> JsonElement getJson(Dynamic<?> dynamic) {
        Dynamic<?> typed = dynamic;
        return typed.getValue() instanceof JsonElement ? (JsonElement)typed.getValue() : (JsonElement)typed.getOps().convertTo((DynamicOps)JsonOps.INSTANCE, typed.getValue());
    }

    @NotNull
    public ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> var1, LootContext var2);

    public Codec<? extends IGlobalLootModifier> codec();
}

