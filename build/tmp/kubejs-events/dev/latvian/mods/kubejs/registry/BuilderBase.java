/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.Util
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.registry;

import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class BuilderBase<T>
implements Supplier<T> {
    public final ResourceLocation id;
    protected T object;
    public String translationKey;
    public Component displayName;
    public boolean formattedDisplayName;
    public transient boolean dummyBuilder;
    public transient Set<ResourceLocation> defaultTags;

    public BuilderBase(ResourceLocation i) {
        this.id = i;
        this.object = null;
        this.translationKey = "";
        this.displayName = null;
        this.formattedDisplayName = false;
        this.dummyBuilder = false;
        this.defaultTags = new HashSet<ResourceLocation>();
    }

    public abstract RegistryInfo getRegistryType();

    public abstract T createObject();

    public T transformObject(T obj) {
        return obj;
    }

    @Override
    public final T get() {
        try {
            return this.object;
        }
        catch (Exception ex) {
            if (this.dummyBuilder) {
                throw new RuntimeException("Object '" + String.valueOf(this.id) + "' of registry '" + String.valueOf(this.getRegistryType().key.m_135782_()) + "' is from a dummy builder and doesn't have a value!");
            }
            throw new RuntimeException("Object '" + String.valueOf(this.id) + "' of registry '" + String.valueOf(this.getRegistryType().key.m_135782_()) + "' hasn't been registered yet!", ex);
        }
    }

    public void createAdditionalObjects() {
    }

    public String getTranslationKeyGroup() {
        return this.getRegistryType().languageKeyPrefix;
    }

    @Info(value="Sets the translation key for this object, e.g. `block.minecraft.stone`.\n")
    public BuilderBase<T> translationKey(String key) {
        this.translationKey = key;
        return this;
    }

    @Info(value="Sets the display name for this object, e.g. `Stone`.\n\nThis will be overridden by a lang file if it exists.\n")
    public BuilderBase<T> displayName(Component name) {
        this.displayName = name;
        return this;
    }

    @Info(value="Makes displayName() override language files.\n")
    public BuilderBase<T> formattedDisplayName() {
        this.formattedDisplayName = true;
        return this;
    }

    @Info(value="Combined method of formattedDisplayName().displayName(name).\n")
    public BuilderBase<T> formattedDisplayName(Component name) {
        return this.formattedDisplayName().displayName(name);
    }

    @Info(value="Adds a tag to this object, e.g. `minecraft:stone`.\n")
    public BuilderBase<T> tag(ResourceLocation tag) {
        this.defaultTags.add(tag);
        this.getRegistryType().hasDefaultTags = true;
        return this;
    }

    public ResourceLocation newID(String pre, String post) {
        if (pre.isEmpty() && post.isEmpty()) {
            return this.id;
        }
        return new ResourceLocation(this.id.m_135827_() + ":" + pre + this.id.m_135815_() + post);
    }

    public void generateDataJsons(DataJsonGenerator generator) {
    }

    public void generateAssetJsons(AssetJsonGenerator generator) {
    }

    public String getBuilderTranslationKey() {
        if (this.translationKey.isEmpty()) {
            return Util.m_137492_((String)this.getTranslationKeyGroup(), (ResourceLocation)this.id);
        }
        return this.translationKey;
    }

    public void generateLang(LangEventJS lang) {
        if (this.displayName != null) {
            lang.add(this.id.m_135827_(), this.getBuilderTranslationKey(), this.displayName.getString());
        } else {
            lang.add(this.id.m_135827_(), this.getBuilderTranslationKey(), UtilsJS.snakeCaseToTitleCase(this.id.m_135815_()));
        }
    }

    protected T createTransformedObject() {
        this.object = this.transformObject(this.createObject());
        return this.object;
    }

    public String toString() {
        String n = this.getClass().getName();
        int i = n.lastIndexOf(46);
        if (i != -1) {
            n = n.substring(i + 1);
        }
        return n + "[" + String.valueOf(this.id) + "]";
    }
}

