/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.Products$P1
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Instance
 *  com.mojang.serialization.codecs.RecordCodecBuilder$Mu
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.storage.loot.LootContext
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemConditions
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.loot;

import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.function.Predicate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.jetbrains.annotations.NotNull;

public abstract class LootModifier
implements IGlobalLootModifier {
    protected final LootItemCondition[] conditions;
    private final Predicate<LootContext> combinedConditions;

    protected static <T extends LootModifier> Products.P1<RecordCodecBuilder.Mu<T>, LootItemCondition[]> codecStart(RecordCodecBuilder.Instance<T> instance) {
        return instance.group((App)LOOT_CONDITIONS_CODEC.fieldOf("conditions").forGetter(lm -> lm.conditions));
    }

    protected LootModifier(LootItemCondition[] conditionsIn) {
        this.conditions = conditionsIn;
        this.combinedConditions = LootItemConditions.m_81834_((Predicate[])conditionsIn);
    }

    @Override
    @NotNull
    public final ObjectArrayList<ItemStack> apply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        return this.combinedConditions.test(context) ? this.doApply(generatedLoot, context) : generatedLoot;
    }

    @NotNull
    protected abstract ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> var1, LootContext var2);
}

