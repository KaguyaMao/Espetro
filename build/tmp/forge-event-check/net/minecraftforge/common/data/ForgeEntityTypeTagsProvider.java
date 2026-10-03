/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.tags.EntityTypeTagsProvider
 *  net.minecraft.world.entity.EntityType
 */
package net.minecraftforge.common.data;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ForgeEntityTypeTagsProvider
extends EntityTypeTagsProvider {
    public ForgeEntityTypeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, "forge", existingFileHelper);
    }

    public void m_6577_(HolderLookup.Provider lookupProvider) {
        this.m_206424_(Tags.EntityTypes.BOSSES).m_255179_((Object[])new EntityType[]{EntityType.f_20565_, EntityType.f_20496_});
    }

    public String m_6055_() {
        return "Forge EntityType Tags";
    }
}

