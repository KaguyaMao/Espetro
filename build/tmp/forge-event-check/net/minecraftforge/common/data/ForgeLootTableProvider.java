/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.critereon.ItemPredicate
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.loot.LootTableProvider
 *  net.minecraft.data.loot.LootTableProvider$SubProviderEntry
 *  net.minecraft.data.loot.LootTableSubProvider
 *  net.minecraft.data.loot.packs.VanillaLootTableProvider
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.storage.loot.LootPool
 *  net.minecraft.world.level.storage.loot.LootTable
 *  net.minecraft.world.level.storage.loot.LootTable$Builder
 *  net.minecraft.world.level.storage.loot.ValidationContext
 *  net.minecraft.world.level.storage.loot.entries.CompositeEntryBase
 *  net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer
 *  net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition
 *  net.minecraft.world.level.storage.loot.predicates.LootItemCondition$Builder
 *  net.minecraft.world.level.storage.loot.predicates.MatchTool
 *  net.minecraftforge.fml.util.ObfuscationReflectionHelper
 */
package net.minecraftforge.common.data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.data.loot.packs.VanillaLootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.CompositeEntryBase;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.predicates.CompositeLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.InvertedLootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.common.loot.CanToolPerformAction;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

public final class ForgeLootTableProvider
extends LootTableProvider {
    public ForgeLootTableProvider(PackOutput packOutput) {
        super(packOutput, Set.of(), VanillaLootTableProvider.m_247452_((PackOutput)packOutput).getTables());
    }

    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext validationcontext) {
    }

    public List<LootTableProvider.SubProviderEntry> getTables() {
        return super.getTables().stream().map(entry -> new LootTableProvider.SubProviderEntry(() -> this.replaceAndFilterChangesOnly((LootTableSubProvider)entry.f_243941_().get()), entry.f_244144_())).collect(Collectors.toList());
    }

    private LootTableSubProvider replaceAndFilterChangesOnly(LootTableSubProvider subProvider) {
        return newConsumer -> subProvider.m_245126_((resourceLocation, builder) -> {
            if (this.findAndReplaceInLootTableBuilder((LootTable.Builder)builder, Items.f_42574_, ToolActions.SHEARS_DIG)) {
                newConsumer.accept(resourceLocation, builder);
            }
        });
    }

    private boolean findAndReplaceInLootTableBuilder(LootTable.Builder builder, Item from, ToolAction toolAction) {
        List lootPools = (List)ObfuscationReflectionHelper.getPrivateValue(LootTable.Builder.class, (Object)builder, (String)"f_79156_");
        boolean found = false;
        if (lootPools == null) {
            throw new IllegalStateException(LootTable.Builder.class.getName() + " is missing field f_79156_");
        }
        for (LootPool lootPool : lootPools) {
            if (!this.findAndReplaceInLootPool(lootPool, from, toolAction)) continue;
            found = true;
        }
        return found;
    }

    private boolean findAndReplaceInLootPool(LootPool lootPool, Item from, ToolAction toolAction) {
        LootPoolEntryContainer[] lootEntries = (LootPoolEntryContainer[])ObfuscationReflectionHelper.getPrivateValue(LootPool.class, (Object)lootPool, (String)"f_79023_");
        LootItemCondition[] lootConditions = (LootItemCondition[])ObfuscationReflectionHelper.getPrivateValue(LootPool.class, (Object)lootPool, (String)"f_79024_");
        boolean found = false;
        if (lootEntries == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_79023_");
        }
        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (this.findAndReplaceInLootEntry(lootEntry, from, toolAction)) {
                found = true;
            }
            if (!(lootEntry instanceof CompositeEntryBase) || !this.findAndReplaceInParentedLootEntry((CompositeEntryBase)lootEntry, from, toolAction)) continue;
            found = true;
        }
        if (lootConditions == null) {
            throw new IllegalStateException(LootPool.class.getName() + " is missing field f_79024_");
        }
        for (int i = 0; i < lootConditions.length; ++i) {
            CompositeLootItemCondition compositeLootItemCondition;
            LootItemCondition lootCondition = lootConditions[i];
            if (lootCondition instanceof MatchTool && this.checkMatchTool((MatchTool)lootCondition, from)) {
                lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).m_6409_();
                found = true;
                continue;
            }
            if (!(lootCondition instanceof InvertedLootItemCondition)) continue;
            LootItemCondition invLootCondition = (LootItemCondition)ObfuscationReflectionHelper.getPrivateValue(InvertedLootItemCondition.class, (Object)((InvertedLootItemCondition)lootCondition), (String)"f_81681_");
            if (invLootCondition instanceof MatchTool && this.checkMatchTool((MatchTool)invLootCondition, from)) {
                lootConditions[i] = InvertedLootItemCondition.m_81694_((LootItemCondition.Builder)CanToolPerformAction.canToolPerformAction(toolAction)).m_6409_();
                found = true;
                continue;
            }
            if (!(invLootCondition instanceof CompositeLootItemCondition) || !this.findAndReplaceInComposite(compositeLootItemCondition = (CompositeLootItemCondition)invLootCondition, from, toolAction)) continue;
            found = true;
        }
        return found;
    }

    private boolean findAndReplaceInParentedLootEntry(CompositeEntryBase entry, Item from, ToolAction toolAction) {
        LootPoolEntryContainer[] lootEntries = (LootPoolEntryContainer[])ObfuscationReflectionHelper.getPrivateValue(CompositeEntryBase.class, (Object)entry, (String)"f_79428_");
        boolean found = false;
        if (lootEntries == null) {
            throw new IllegalStateException(CompositeEntryBase.class.getName() + " is missing field f_79428_");
        }
        for (LootPoolEntryContainer lootEntry : lootEntries) {
            if (!this.findAndReplaceInLootEntry(lootEntry, from, toolAction)) continue;
            found = true;
        }
        return found;
    }

    private boolean findAndReplaceInLootEntry(LootPoolEntryContainer entry, Item from, ToolAction toolAction) {
        LootItemCondition[] lootConditions = (LootItemCondition[])ObfuscationReflectionHelper.getPrivateValue(LootPoolEntryContainer.class, (Object)entry, (String)"f_79636_");
        boolean found = false;
        if (lootConditions == null) {
            throw new IllegalStateException(LootPoolEntryContainer.class.getName() + " is missing field f_79636_");
        }
        for (int i = 0; i < lootConditions.length; ++i) {
            CompositeLootItemCondition composite;
            LootItemCondition lootItemCondition = lootConditions[i];
            if (lootItemCondition instanceof CompositeLootItemCondition && this.findAndReplaceInComposite(composite = (CompositeLootItemCondition)lootItemCondition, from, toolAction)) {
                found = true;
                continue;
            }
            if (!(lootConditions[i] instanceof MatchTool) || !this.checkMatchTool((MatchTool)lootConditions[i], from)) continue;
            lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).m_6409_();
            found = true;
        }
        return found;
    }

    private boolean findAndReplaceInComposite(CompositeLootItemCondition alternative, Item from, ToolAction toolAction) {
        LootItemCondition[] lootConditions = (LootItemCondition[])ObfuscationReflectionHelper.getPrivateValue(CompositeLootItemCondition.class, (Object)alternative, (String)"f_285609_");
        boolean found = false;
        if (lootConditions == null) {
            throw new IllegalStateException(CompositeLootItemCondition.class.getName() + " is missing field f_285609_");
        }
        for (int i = 0; i < lootConditions.length; ++i) {
            if (!(lootConditions[i] instanceof MatchTool) || !this.checkMatchTool((MatchTool)lootConditions[i], from)) continue;
            lootConditions[i] = CanToolPerformAction.canToolPerformAction(toolAction).m_6409_();
            found = true;
        }
        return found;
    }

    private boolean checkMatchTool(MatchTool lootCondition, Item expected) {
        ItemPredicate predicate = (ItemPredicate)ObfuscationReflectionHelper.getPrivateValue(MatchTool.class, (Object)lootCondition, (String)"f_81993_");
        Set items = (Set)ObfuscationReflectionHelper.getPrivateValue(ItemPredicate.class, (Object)predicate, (String)"f_151427_");
        return items != null && items.contains(expected);
    }
}

