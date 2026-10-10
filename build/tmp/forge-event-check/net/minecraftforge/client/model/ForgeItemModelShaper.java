/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.client.renderer.ItemModelShaper
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.client.resources.model.ModelManager
 *  net.minecraft.client.resources.model.ModelResourceLocation
 *  net.minecraft.core.Holder$Reference
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ForgeItemModelShaper
extends ItemModelShaper {
    private final Map<Holder.Reference<Item>, ModelResourceLocation> locations = Maps.newHashMap();
    private final Map<Holder.Reference<Item>, BakedModel> models = Maps.newHashMap();

    public ForgeItemModelShaper(ModelManager manager) {
        super(manager);
    }

    @Nullable
    public BakedModel m_109394_(Item item) {
        return this.models.get(ForgeRegistries.ITEMS.getDelegateOrThrow(item));
    }

    public void m_109396_(Item item, ModelResourceLocation location) {
        Holder.Reference<Item> key = ForgeRegistries.ITEMS.getDelegateOrThrow(item);
        this.locations.put(key, location);
        this.models.put(key, this.m_109393_().m_119422_(location));
    }

    public void m_109403_() {
        ModelManager manager = this.m_109393_();
        for (Map.Entry<Holder.Reference<Item>, ModelResourceLocation> e : this.locations.entrySet()) {
            this.models.put(e.getKey(), manager.m_119422_(e.getValue()));
        }
    }

    public ModelResourceLocation getLocation(@NotNull ItemStack stack) {
        ModelResourceLocation location = this.locations.get(ForgeRegistries.ITEMS.getDelegateOrThrow(stack.m_41720_()));
        return location == null ? ModelBakery.f_119230_ : location;
    }
}

