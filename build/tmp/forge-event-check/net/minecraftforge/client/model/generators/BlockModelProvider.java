/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.PackOutput
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client.model.generators;

import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public abstract class BlockModelProvider
extends ModelProvider<BlockModelBuilder> {
    public BlockModelProvider(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
        super(output, modid, "block", BlockModelBuilder::new, existingFileHelper);
    }

    @NotNull
    public String m_6055_() {
        return "Block Models: " + this.modid;
    }
}

