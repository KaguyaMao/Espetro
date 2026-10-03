/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.Item
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client.model.generators;

import java.util.Objects;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

public abstract class ItemModelProvider
extends ModelProvider<ItemModelBuilder> {
    public ItemModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, "item", ItemModelBuilder::new, existingFileHelper);
    }

    public ItemModelBuilder basicItem(Item item) {
        return this.basicItem(Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)));
    }

    public ItemModelBuilder basicItem(ResourceLocation item) {
        return (ItemModelBuilder)((ItemModelBuilder)((ItemModelBuilder)this.getBuilder(item.toString())).parent(new ModelFile.UncheckedModelFile("item/generated"))).texture("layer0", new ResourceLocation(item.m_135827_(), "item/" + item.m_135815_()));
    }

    @NotNull
    public String m_6055_() {
        return "Item Models: " + this.modid;
    }
}

