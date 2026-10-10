/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.advancements.AdvancementRewards
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 */
package net.minecraftforge.common.extensions;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;

public interface IForgeAdvancementBuilder {
    private Advancement.Builder self() {
        return (Advancement.Builder)this;
    }

    default public Advancement save(Consumer<Advancement> saver, ResourceLocation id, ExistingFileHelper fileHelper) {
        boolean canBuild = this.self().m_138392_(advancementId -> {
            if (fileHelper.exists((ResourceLocation)advancementId, PackType.SERVER_DATA, ".json", "advancements")) {
                return new Advancement(advancementId, null, null, AdvancementRewards.f_9978_, (Map)Maps.newHashMap(), new String[0][0], false);
            }
            return null;
        });
        if (!canBuild) {
            throw new IllegalStateException("Tried to build Advancement without valid Parent!");
        }
        Advancement advancement = this.self().m_138403_(id);
        saver.accept(advancement);
        return advancement;
    }
}

