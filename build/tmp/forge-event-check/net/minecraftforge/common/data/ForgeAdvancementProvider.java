/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.core.HolderLookup$Provider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.advancements.AdvancementProvider
 *  net.minecraft.data.advancements.AdvancementSubProvider
 */
package net.minecraftforge.common.data;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ForgeAdvancementProvider
extends AdvancementProvider {
    public ForgeAdvancementProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper existingFileHelper, List<AdvancementGenerator> subProviders) {
        super(output, registries, subProviders.stream().map(generator -> generator.toSubProvider(existingFileHelper)).toList());
    }

    public static interface AdvancementGenerator {
        public void generate(HolderLookup.Provider var1, Consumer<Advancement> var2, ExistingFileHelper var3);

        default public AdvancementSubProvider toSubProvider(ExistingFileHelper existingFileHelper) {
            return (registries, saver) -> this.generate(registries, saver, existingFileHelper);
        }
    }
}

