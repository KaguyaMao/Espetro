/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.flag.FeatureFlagSet
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.CreativeModeTab$ItemDisplayParameters
 *  net.minecraft.world.item.CreativeModeTab$Output
 *  net.minecraft.world.item.CreativeModeTab$TabVisibility
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.event.IModBusEvent
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.event;

import java.util.function.Supplier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

public final class BuildCreativeModeTabContentsEvent
extends Event
implements IModBusEvent,
CreativeModeTab.Output {
    private final CreativeModeTab tab;
    private final CreativeModeTab.ItemDisplayParameters parameters;
    private final MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries;
    private final ResourceKey<CreativeModeTab> tabKey;

    @ApiStatus.Internal
    public BuildCreativeModeTabContentsEvent(CreativeModeTab tab, ResourceKey<CreativeModeTab> tabKey, CreativeModeTab.ItemDisplayParameters parameters, MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries) {
        this.tab = tab;
        this.tabKey = tabKey;
        this.parameters = parameters;
        this.entries = entries;
    }

    public CreativeModeTab getTab() {
        return this.tab;
    }

    public ResourceKey<CreativeModeTab> getTabKey() {
        return this.tabKey;
    }

    public FeatureFlagSet getFlags() {
        return this.parameters.f_268709_();
    }

    public CreativeModeTab.ItemDisplayParameters getParameters() {
        return this.parameters;
    }

    public boolean hasPermissions() {
        return this.parameters.f_268429_();
    }

    public MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> getEntries() {
        return this.entries;
    }

    public void m_246267_(ItemStack stack, CreativeModeTab.TabVisibility visibility) {
        this.getEntries().put(stack, visibility);
    }

    public void accept(Supplier<? extends ItemLike> item, CreativeModeTab.TabVisibility visibility) {
        this.m_245282_(item.get(), visibility);
    }

    public void accept(Supplier<? extends ItemLike> item) {
        this.m_246326_(item.get());
    }
}

