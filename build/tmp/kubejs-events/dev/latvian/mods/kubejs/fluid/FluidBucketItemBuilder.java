/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  dev.architectury.core.item.ArchitecturyBucketItem
 *  net.minecraft.world.item.BucketItem
 */
package dev.latvian.mods.kubejs.fluid;

import com.google.gson.JsonElement;
import dev.architectury.core.item.ArchitecturyBucketItem;
import dev.latvian.mods.kubejs.fluid.FluidBuilder;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import java.util.function.Supplier;
import net.minecraft.world.item.BucketItem;

public class FluidBucketItemBuilder
extends ItemBuilder {
    public final FluidBuilder fluidBuilder;

    public FluidBucketItemBuilder(FluidBuilder b) {
        super(b.newID("", "_bucket"));
        this.fluidBuilder = b;
        this.maxStackSize(1);
    }

    @Override
    public BucketItem createObject() {
        return new ArchitecturyBucketItem((Supplier)this.fluidBuilder, this.createItemProperties());
    }

    @Override
    public void generateAssetJsons(AssetJsonGenerator generator) {
        if (this.modelJson != null) {
            generator.json(AssetJsonGenerator.asItemModelLocation(this.id), (JsonElement)this.modelJson);
            return;
        }
        generator.itemModel(this.id, m -> {
            if (!this.parentModel.isEmpty()) {
                m.parent(this.parentModel);
            } else {
                m.parent("kubejs:item/generated_bucket");
            }
            if (this.textureJson.size() > 0) {
                m.textures(this.textureJson);
            }
        });
    }
}

