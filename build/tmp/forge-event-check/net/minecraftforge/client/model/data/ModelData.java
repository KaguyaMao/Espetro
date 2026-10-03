/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.jetbrains.annotations.Contract
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.data;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

public final class ModelData {
    public static final ModelData EMPTY = ModelData.builder().build();
    private final Map<ModelProperty<?>, Object> properties;

    private ModelData(Map<ModelProperty<?>, Object> properties) {
        this.properties = properties;
    }

    public Set<ModelProperty<?>> getProperties() {
        return this.properties.keySet();
    }

    public boolean has(ModelProperty<?> property) {
        return this.properties.containsKey(property);
    }

    @Nullable
    public <T> T get(ModelProperty<T> property) {
        return (T)this.properties.get(property);
    }

    public Builder derive() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder(null);
    }

    public static final class Builder {
        private final Map<ModelProperty<?>, Object> properties = new IdentityHashMap();

        private Builder(@Nullable ModelData parent) {
            if (parent != null) {
                this.properties.putAll(parent.properties);
            }
        }

        @Contract(value="_, _ -> this")
        public <T> Builder with(ModelProperty<T> property, T value) {
            Preconditions.checkState((boolean)property.test(value), (Object)"The provided value is invalid for this property.");
            this.properties.put(property, value);
            return this;
        }

        @Contract(value="-> new")
        public ModelData build() {
            return new ModelData(Collections.unmodifiableMap(this.properties));
        }
    }
}

