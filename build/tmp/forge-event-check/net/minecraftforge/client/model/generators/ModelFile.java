/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.generators;

import com.google.common.base.Preconditions;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public abstract class ModelFile {
    protected ResourceLocation location;

    protected ModelFile(ResourceLocation location) {
        this.location = location;
    }

    protected abstract boolean exists();

    public ResourceLocation getLocation() {
        this.assertExistence();
        return this.location;
    }

    public void assertExistence() {
        Preconditions.checkState((boolean)this.exists(), (String)"Model at %s does not exist", (Object)this.location);
    }

    public ResourceLocation getUncheckedLocation() {
        return this.location;
    }

    public static class ExistingModelFile
    extends ModelFile {
        private final ExistingFileHelper existingHelper;

        public ExistingModelFile(ResourceLocation location, ExistingFileHelper existingHelper) {
            super(location);
            this.existingHelper = existingHelper;
        }

        @Override
        protected boolean exists() {
            if (this.getUncheckedLocation().m_135815_().contains(".")) {
                return this.existingHelper.exists(this.getUncheckedLocation(), ModelProvider.MODEL_WITH_EXTENSION);
            }
            return this.existingHelper.exists(this.getUncheckedLocation(), ModelProvider.MODEL);
        }
    }

    public static class UncheckedModelFile
    extends ModelFile {
        public UncheckedModelFile(String location) {
            this(new ResourceLocation(location));
        }

        public UncheckedModelFile(ResourceLocation location) {
            super(location);
        }

        @Override
        protected boolean exists() {
            return true;
        }
    }
}

