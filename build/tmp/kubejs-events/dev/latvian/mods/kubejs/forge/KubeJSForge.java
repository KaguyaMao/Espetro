/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.forge.EventBuses
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab$TabVisibility
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.common.ForgeMod
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.player.PlayerDestroyItemEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.IExtensionPoint$DisplayTest
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
 *  net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.RegisterEvent
 */
package dev.latvian.mods.kubejs.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.latvian.mods.kubejs.CommonProperties;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.bindings.event.StartupEvents;
import dev.latvian.mods.kubejs.bindings.event.WorldgenEvents;
import dev.latvian.mods.kubejs.entity.forge.LivingEntityDropsEventJS;
import dev.latvian.mods.kubejs.forge.ForgeKubeJSEvents;
import dev.latvian.mods.kubejs.forge.KubeJSForgeClient;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabCallback;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabEvent;
import dev.latvian.mods.kubejs.item.creativetab.KubeJSCreativeTabs;
import dev.latvian.mods.kubejs.item.forge.ItemDestroyedEventJS;
import dev.latvian.mods.kubejs.platform.forge.IngredientForgeHelper;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

@Mod(value="kubejs")
public class KubeJSForge {
    public KubeJSForge() throws Throwable {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus((String)"kubejs", (IEventBus)bus);
        bus.addListener(EventPriority.LOW, KubeJSForge::loadComplete);
        bus.addListener(EventPriority.LOW, KubeJSForge::initRegistries);
        bus.addListener(EventPriority.LOW, KubeJSForge::commonSetup);
        bus.addListener(EventPriority.LOW, KubeJSForge::creativeTab);
        KubeJS.instance = new KubeJS();
        KubeJS.instance.setup();
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> "OHNOES\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31\ud83d\ude31", (a, b) -> true));
        MinecraftForge.EVENT_BUS.addListener(KubeJSForge::itemDestroyed);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.LOWEST, KubeJSForge::livingDrops);
        if (!CommonProperties.get().serverOnly) {
            ForgeMod.enableMilkFluid();
            IngredientForgeHelper.register();
            KubeJSCreativeTabs.init();
        }
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> new KubeJSForgeClient());
    }

    private static void initRegistries(RegisterEvent event) {
        RegistryInfo<?> info = RegistryInfo.of(event.getRegistryKey());
        info.registerObjects((id, supplier) -> event.register((ResourceKey)UtilsJS.cast(info.key), id, supplier));
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        WorldgenEvents.post();
    }

    private static void creativeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceLocation tabId = event.getTabKey().m_135782_();
        if (StartupEvents.MODIFY_CREATIVE_TAB.hasListeners(tabId)) {
            StartupEvents.MODIFY_CREATIVE_TAB.post((ScriptTypeHolder)ScriptType.STARTUP, (Object)tabId, new CreativeTabEvent(event.getTab(), event.hasPermissions(), new CreativeTabCallbackForge(event)));
        }
    }

    private static void loadComplete(FMLLoadCompleteEvent event) {
        KubeJS.instance.loadComplete();
    }

    private static void itemDestroyed(PlayerDestroyItemEvent event) {
        if (ForgeKubeJSEvents.ITEM_DESTROYED.hasListeners()) {
            ForgeKubeJSEvents.ITEM_DESTROYED.post((ScriptTypeHolder)event.getEntity(), (Object)event.getOriginal().m_41720_(), new ItemDestroyedEventJS(event));
        }
    }

    private static void livingDrops(LivingDropsEvent event) {
        if (ForgeKubeJSEvents.ENTITY_DROPS.hasListeners()) {
            LivingEntityDropsEventJS e = new LivingEntityDropsEventJS(event);
            if (ForgeKubeJSEvents.ENTITY_DROPS.post((ScriptTypeHolder)event.getEntity(), (Object)e.getEntity().m_6095_(), e).interruptFalse()) {
                event.setCanceled(true);
            } else if (e.eventDrops != null) {
                event.getDrops().clear();
                event.getDrops().addAll(e.eventDrops);
            }
        }
    }

    private record CreativeTabCallbackForge(BuildCreativeModeTabContentsEvent event) implements CreativeTabCallback
    {
        @Override
        public void addAfter(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
            for (ItemStack item : items) {
                this.event.getEntries().putAfter((Object)order, (Object)item, (Object)visibility);
            }
        }

        @Override
        public void addBefore(ItemStack order, ItemStack[] items, CreativeModeTab.TabVisibility visibility) {
            for (ItemStack item : items) {
                this.event.getEntries().putBefore((Object)order, (Object)item, (Object)visibility);
            }
        }

        @Override
        public void remove(Ingredient filter, boolean removeDisplay, boolean removeSearch) {
            ArrayList<AbstractMap.SimpleEntry<ItemStack, CreativeModeTab.TabVisibility>> entries = new ArrayList<AbstractMap.SimpleEntry<ItemStack, CreativeModeTab.TabVisibility>>();
            for (Map.Entry entry : this.event.getEntries()) {
                if (!filter.test((ItemStack)entry.getKey())) continue;
                CreativeModeTab.TabVisibility visibility = (CreativeModeTab.TabVisibility)entry.getValue();
                if (removeDisplay && removeSearch) {
                    visibility = null;
                }
                if (removeDisplay && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
                    visibility = CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY;
                }
                if (removeSearch && visibility == CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS) {
                    visibility = CreativeModeTab.TabVisibility.PARENT_TAB_ONLY;
                }
                entries.add(new AbstractMap.SimpleEntry<ItemStack, CreativeModeTab.TabVisibility>((ItemStack)entry.getKey(), visibility));
            }
            for (Map.Entry entry : entries) {
                if (entry.getValue() == null) {
                    this.event.getEntries().remove((Object)((ItemStack)entry.getKey()));
                    continue;
                }
                this.event.getEntries().put((Object)((ItemStack)entry.getKey()), (Object)((CreativeModeTab.TabVisibility)entry.getValue()));
            }
        }
    }
}

