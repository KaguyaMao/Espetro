/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.item.ArmorMaterial
 */
package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.item.MutableArmorTier;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.function.Consumer;
import net.minecraft.world.item.ArmorMaterial;

@Info(value="Invoked when the game is starting up and the armor tier registry is being built.\n")
public class ItemArmorTierRegistryEventJS
extends StartupEventJS {
    @Info(value="Adds a new armor tier with a parent tier specified by string.")
    public void add(String id, String parent, Consumer<MutableArmorTier> tier) {
        ArmorMaterial material = ItemBuilder.toArmorMaterial(parent);
        String fullId = KubeJS.appendModId(id);
        MutableArmorTier t = new MutableArmorTier(fullId, material);
        tier.accept(t);
        ItemBuilder.ARMOR_TIERS.put(fullId, t);
    }

    @Info(value="Adds a new armor tier.")
    public void add(String id, Consumer<MutableArmorTier> tier) {
        this.add(id, "iron", tier);
    }
}

