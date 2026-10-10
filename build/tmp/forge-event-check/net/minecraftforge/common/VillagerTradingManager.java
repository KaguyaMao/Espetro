/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.core.NonNullList
 *  net.minecraft.world.entity.npc.VillagerProfession
 *  net.minecraft.world.entity.npc.VillagerTrades
 *  net.minecraft.world.entity.npc.VillagerTrades$ItemListing
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.common;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.event.village.WandererTradesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

public class VillagerTradingManager {
    private static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>> VANILLA_TRADES = new HashMap<VillagerProfession, Int2ObjectMap<VillagerTrades.ItemListing[]>>();
    private static final Int2ObjectMap<VillagerTrades.ItemListing[]> WANDERER_TRADES = new Int2ObjectOpenHashMap();

    static void loadTrades(ServerAboutToStartEvent e) {
        VillagerTradingManager.postWandererEvent();
        VillagerTradingManager.postVillagerEvents();
    }

    private static void postWandererEvent() {
        NonNullList generic = NonNullList.m_122779_();
        NonNullList rare = NonNullList.m_122779_();
        Arrays.stream((VillagerTrades.ItemListing[])WANDERER_TRADES.get(1)).forEach(((List)generic)::add);
        Arrays.stream((VillagerTrades.ItemListing[])WANDERER_TRADES.get(2)).forEach(((List)rare)::add);
        MinecraftForge.EVENT_BUS.post((Event)new WandererTradesEvent((List<VillagerTrades.ItemListing>)generic, (List<VillagerTrades.ItemListing>)rare));
        VillagerTrades.f_35628_.put(1, (Object)generic.toArray(new VillagerTrades.ItemListing[0]));
        VillagerTrades.f_35628_.put(2, (Object)rare.toArray(new VillagerTrades.ItemListing[0]));
    }

    private static void postVillagerEvents() {
        for (VillagerProfession prof : ForgeRegistries.VILLAGER_PROFESSIONS) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> trades = VANILLA_TRADES.getOrDefault(prof, (Int2ObjectMap<VillagerTrades.ItemListing[]>)new Int2ObjectOpenHashMap());
            Int2ObjectOpenHashMap mutableTrades = new Int2ObjectOpenHashMap();
            for (int i = 1; i < 6; ++i) {
                mutableTrades.put(i, (Object)NonNullList.m_122779_());
            }
            trades.int2ObjectEntrySet().forEach(arg_0 -> VillagerTradingManager.lambda$postVillagerEvents$3((Int2ObjectMap)mutableTrades, arg_0));
            MinecraftForge.EVENT_BUS.post((Event)new VillagerTradesEvent((Int2ObjectMap<List<VillagerTrades.ItemListing>>)mutableTrades, prof));
            Int2ObjectOpenHashMap newTrades = new Int2ObjectOpenHashMap();
            mutableTrades.int2ObjectEntrySet().forEach(arg_0 -> VillagerTradingManager.lambda$postVillagerEvents$4((Int2ObjectMap)newTrades, arg_0));
            VillagerTrades.f_35627_.put(prof, newTrades);
        }
    }

    private static /* synthetic */ void lambda$postVillagerEvents$4(Int2ObjectMap newTrades, Int2ObjectMap.Entry e) {
        newTrades.put(e.getIntKey(), (Object)((List)e.getValue()).toArray(new VillagerTrades.ItemListing[0]));
    }

    private static /* synthetic */ void lambda$postVillagerEvents$3(Int2ObjectMap mutableTrades, Int2ObjectMap.Entry e) {
        Arrays.stream((VillagerTrades.ItemListing[])e.getValue()).forEach(((List)mutableTrades.get(e.getIntKey()))::add);
    }

    private static /* synthetic */ void lambda$static$0(Int2ObjectMap copy, Int2ObjectMap.Entry ent) {
        copy.put(ent.getIntKey(), (Object)Arrays.copyOf((VillagerTrades.ItemListing[])ent.getValue(), ((VillagerTrades.ItemListing[])ent.getValue()).length));
    }

    static {
        VillagerTrades.f_35627_.entrySet().forEach(e -> {
            Int2ObjectOpenHashMap copy = new Int2ObjectOpenHashMap();
            ((Int2ObjectMap)e.getValue()).int2ObjectEntrySet().forEach(arg_0 -> VillagerTradingManager.lambda$static$0((Int2ObjectMap)copy, arg_0));
            VANILLA_TRADES.put((VillagerProfession)e.getKey(), (Int2ObjectMap<VillagerTrades.ItemListing[]>)copy);
        });
        VillagerTrades.f_35628_.int2ObjectEntrySet().forEach(e -> WANDERER_TRADES.put(e.getIntKey(), (Object)Arrays.copyOf((VillagerTrades.ItemListing[])e.getValue(), ((VillagerTrades.ItemListing[])e.getValue()).length)));
    }
}

