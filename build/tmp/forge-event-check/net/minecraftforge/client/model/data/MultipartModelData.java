/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.resources.model.BakedModel
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.data;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

public class MultipartModelData {
    public static final ModelProperty<MultipartModelData> PROPERTY = new ModelProperty();
    private final Map<BakedModel, ModelData> partData;

    private MultipartModelData(Map<BakedModel, ModelData> partData) {
        this.partData = partData;
    }

    @Nullable
    public ModelData get(BakedModel model) {
        return this.partData.get(model);
    }

    public static ModelData resolve(ModelData modelData, BakedModel model) {
        MultipartModelData multipartData = modelData.get(PROPERTY);
        if (multipartData == null) {
            return modelData;
        }
        ModelData partData = multipartData.get(model);
        return partData != null ? partData : modelData;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<BakedModel, ModelData> partData = new IdentityHashMap<BakedModel, ModelData>();

        public Builder with(BakedModel model, ModelData data) {
            this.partData.put(model, data);
            return this;
        }

        public MultipartModelData build() {
            return new MultipartModelData(this.partData);
        }
    }
}

