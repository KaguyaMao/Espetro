/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;

public class CustomBuilderObject
extends BuilderBase {
    private final Supplier<Object> object;
    private final RegistryInfo<?> registry;

    public CustomBuilderObject(ResourceLocation i, Supplier<Object> object, RegistryInfo<?> registry) {
        super(i);
        this.object = object;
        this.registry = registry;
        this.translationKey = this.getTranslationKeyGroup() + "." + this.id.m_135827_() + "." + this.id.m_135815_();
    }

    @Override
    public String getTranslationKeyGroup() {
        if (this.getRegistryType() == null) {
            return "If you see this something broke. Please file a bug report.";
        }
        return super.getTranslationKeyGroup();
    }

    @Override
    public RegistryInfo<?> getRegistryType() {
        return this.registry;
    }

    public Object createObject() {
        return this.object.get();
    }
}

