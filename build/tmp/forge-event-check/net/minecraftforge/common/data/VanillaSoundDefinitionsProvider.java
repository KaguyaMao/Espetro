/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.PackOutput
 */
package net.minecraftforge.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

public class VanillaSoundDefinitionsProvider
extends SoundDefinitionsProvider {
    public VanillaSoundDefinitionsProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, "minecraft", helper);
    }

    @Override
    public void registerSounds() {
        this.add(ForgeMod.BUCKET_EMPTY_MILK.getId(), VanillaSoundDefinitionsProvider.definition().subtitle("subtitles.item.bucket.empty").with(VanillaSoundDefinitionsProvider.sound("item/bucket/empty1"), VanillaSoundDefinitionsProvider.sound("item/bucket/empty1").pitch(0.9), VanillaSoundDefinitionsProvider.sound("item/bucket/empty2"), VanillaSoundDefinitionsProvider.sound("item/bucket/empty3")));
        this.add(ForgeMod.BUCKET_FILL_MILK.getId(), VanillaSoundDefinitionsProvider.definition().subtitle("subtitles.item.bucket.fill").with(VanillaSoundDefinitionsProvider.sound("item/bucket/fill1"), VanillaSoundDefinitionsProvider.sound("item/bucket/fill2"), VanillaSoundDefinitionsProvider.sound("item/bucket/fill3")));
    }
}

