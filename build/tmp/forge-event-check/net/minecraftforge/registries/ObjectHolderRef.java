/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ResourceLocationException
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.registries;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

class ObjectHolderRef
implements Consumer<Predicate<ResourceLocation>> {
    private static final Logger LOGGER = LogManager.getLogger();
    private final Field field;
    private final ResourceLocation injectedObject;
    private final ForgeRegistry<?> registry;

    @Nullable
    static ObjectHolderRef create(ResourceLocation registryName, Field field, String injectedObject, boolean extractFromExistingValues) {
        ResourceLocation injectedObjectName;
        ForgeRegistry<Object> registry = RegistryManager.ACTIVE.getRegistry(registryName);
        if (registry == null) {
            return null;
        }
        if (extractFromExistingValues) {
            try {
                Object existing = field.get(null);
                if (existing == null || existing == registry.getDefault()) {
                    return null;
                }
                injectedObjectName = registry.getKey(existing);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            injectedObjectName = new ResourceLocation(injectedObject);
        }
        catch (ResourceLocationException e) {
            throw new IllegalArgumentException("Invalid @ObjectHolder annotation on \"" + field.toString() + "\"", e);
        }
        if (injectedObjectName == null) {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "The ObjectHolder annotation cannot apply to a field that does not map to a registry. Ensure the registry was created during NewRegistryEvent. (found : %s at %s.%s)", field.getType().getName(), field.getDeclaringClass().getName(), field.getName()));
        }
        field.setAccessible(true);
        if (Modifier.isFinal(field.getModifiers())) {
            throw new RuntimeException("@ObjectHolder on final field, our transformer did not run? " + field.getDeclaringClass().getName() + "/" + field.getName());
        }
        return new ObjectHolderRef(registry, field, injectedObjectName);
    }

    private ObjectHolderRef(ForgeRegistry<?> registry, Field field, ResourceLocation injectedObject) {
        this.registry = registry;
        this.field = field;
        this.injectedObject = injectedObject;
    }

    @Override
    public void accept(Predicate<ResourceLocation> filter) {
        if (this.registry == null || !filter.test(this.registry.getRegistryName())) {
            return;
        }
        Object thing = this.registry.containsKey(this.injectedObject) ? this.registry.getValue(this.injectedObject) : null;
        if (thing == null) {
            LOGGER.debug("Unable to lookup {} for {}. This means the object wasn't registered. It's likely just mod options.", (Object)this.injectedObject, (Object)this.field);
            return;
        }
        try {
            this.field.set(null, thing);
        }
        catch (IllegalArgumentException | ReflectiveOperationException e) {
            LOGGER.warn("Unable to set {} with value {} ({})", (Object)this.field, thing, (Object)this.injectedObject, (Object)e);
        }
    }

    public int hashCode() {
        return this.field.hashCode();
    }

    public boolean equals(Object other) {
        if (!(other instanceof ObjectHolderRef)) {
            return false;
        }
        ObjectHolderRef o = (ObjectHolderRef)other;
        return this.field.equals(o.field);
    }
}

