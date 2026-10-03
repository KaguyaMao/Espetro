/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.forgespi.language.ModFileScanData
 *  net.minecraftforge.forgespi.language.ModFileScanData$AnnotationData
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.Nullable
 *  org.objectweb.asm.Type
 */
package net.minecraftforge.registries;

import com.google.common.collect.Maps;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.ObjectHolderRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Type;

public class ObjectHolderRegistry {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<Consumer<Predicate<ResourceLocation>>> objectHolders = new HashSet<Consumer<Predicate<ResourceLocation>>>();
    private static final Type OBJECT_HOLDER = Type.getType(ObjectHolder.class);
    private static final Type MOD = Type.getType(Mod.class);
    private static final List<VanillaObjectHolderData> VANILLA_OBJECT_HOLDERS = List.of(new VanillaObjectHolderData("net.minecraft.world.level.block.Blocks", "block", "net.minecraft.world.level.block.Block"), new VanillaObjectHolderData("net.minecraft.world.item.Items", "item", "net.minecraft.world.item.Item"), new VanillaObjectHolderData("net.minecraft.world.item.enchantment.Enchantments", "enchantment", "net.minecraft.world.item.enchantment.Enchantment"), new VanillaObjectHolderData("net.minecraft.world.effect.MobEffects", "mob_effect", "net.minecraft.world.effect.MobEffect"), new VanillaObjectHolderData("net.minecraft.core.particles.ParticleTypes", "particle_type", "net.minecraft.core.particles.ParticleType"), new VanillaObjectHolderData("net.minecraft.sounds.SoundEvents", "sound_event", "net.minecraft.sounds.SoundEvent"));

    public static synchronized void addHandler(Consumer<Predicate<ResourceLocation>> ref) {
        objectHolders.add(ref);
    }

    public static synchronized boolean removeHandler(Consumer<Predicate<ResourceLocation>> ref) {
        return objectHolders.remove(ref);
    }

    public static void findObjectHolders() {
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Processing ObjectHolder annotations");
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a -> OBJECT_HOLDER.equals((Object)a.annotationType()) || MOD.equals((Object)a.annotationType())).toList();
        if (annotations.stream().noneMatch(a -> OBJECT_HOLDER.equals((Object)a.annotationType()))) {
            return;
        }
        HashMap classModIds = Maps.newHashMap();
        HashMap classCache = Maps.newHashMap();
        annotations.stream().filter(a -> MOD.equals((Object)a.annotationType())).forEach(data -> classModIds.put(data.clazz(), (String)data.annotationData().get("value")));
        VANILLA_OBJECT_HOLDERS.forEach(data -> {
            try {
                Class<?> holderClass = Class.forName(data.holderClass(), true, ObjectHolderRegistry.class.getClassLoader());
                Class<?> registryClass = Class.forName(data.registryType(), true, ObjectHolderRegistry.class.getClassLoader());
                Type holderType = Type.getType(holderClass);
                classCache.put(holderType, holderClass);
                ObjectHolderRegistry.scanTarget(classModIds, classCache, holderType, null, registryClass, data.registryName(), "minecraft", true, true);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException("Vanilla class not found, should not be possible", e);
            }
        });
        annotations.stream().filter(a -> OBJECT_HOLDER.equals((Object)a.annotationType())).filter(a -> a.targetType() == ElementType.FIELD).forEach(data -> ObjectHolderRegistry.scanTarget(classModIds, classCache, data.clazz(), data.memberName(), null, (String)data.annotationData().get("registryName"), (String)data.annotationData().get("value"), false, false));
        LOGGER.debug(ForgeRegistry.REGISTRIES, "Found {} ObjectHolder annotations", (Object)objectHolders.size());
    }

    private static void scanTarget(Map<Type, String> classModIds, Map<Type, Class<?>> classCache, Type type, @Nullable String annotationTarget, @Nullable Class<?> registryClass, String registryName, String value, boolean isClass, boolean extractFromValue) {
        Class<?> clazz;
        if (classCache.containsKey(type)) {
            clazz = classCache.get(type);
        } else {
            try {
                clazz = Class.forName(type.getClassName(), extractFromValue, ObjectHolderRegistry.class.getClassLoader());
                classCache.put(type, clazz);
            }
            catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        if (isClass) {
            ObjectHolderRegistry.scanClassForFields(classModIds, type, new ResourceLocation(registryName), registryClass, (String)value, clazz, extractFromValue);
        } else {
            if (((String)value).indexOf(58) == -1) {
                String prefix = classModIds.get(type);
                if (prefix == null) {
                    LOGGER.warn(ForgeRegistry.REGISTRIES, "Found an unqualified ObjectHolder annotation ({}) without a modid context at {}.{}, ignoring", value, (Object)type, (Object)annotationTarget);
                    throw new IllegalStateException("Unqualified reference to ObjectHolder");
                }
                value = prefix + ":" + (String)value;
            }
            try {
                Field f = clazz.getDeclaredField(annotationTarget);
                ObjectHolderRef ref = ObjectHolderRef.create(new ResourceLocation(registryName), f, (String)value, extractFromValue);
                if (ref != null) {
                    ObjectHolderRegistry.addHandler(ref);
                }
            }
            catch (NoSuchFieldException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void scanClassForFields(Map<Type, String> classModIds, Type targetClass, ResourceLocation registryName, Class<?> registryClass, String value, Class<?> clazz, boolean extractFromExistingValues) {
        classModIds.put(targetClass, value);
        int flags = 4105;
        for (Field f : clazz.getFields()) {
            ObjectHolderRef ref;
            if ((f.getModifiers() & 0x1009) != 4105 || f.isAnnotationPresent(ObjectHolder.class) || !registryClass.isAssignableFrom(f.getType()) || (ref = ObjectHolderRef.create(registryName, f, value + ":" + f.getName().toLowerCase(Locale.ENGLISH), extractFromExistingValues)) == null) continue;
            ObjectHolderRegistry.addHandler(ref);
        }
    }

    private static ResourceLocation getRegistryName(Map<Type, ResourceLocation> classRegistryNames, @Nullable String registryName, Type targetClass, Object declaration) {
        if (registryName != null) {
            return new ResourceLocation(registryName);
        }
        if (classRegistryNames.containsKey(targetClass)) {
            return classRegistryNames.get(targetClass);
        }
        throw new IllegalStateException("No registry name was declared for " + String.valueOf(declaration));
    }

    public static void applyObjectHolders() {
        try {
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Applying holder lookups");
            ObjectHolderRegistry.applyObjectHolders(key -> true);
            LOGGER.debug(ForgeRegistry.REGISTRIES, "Holder lookups applied");
        }
        catch (RuntimeException e) {
            LOGGER.error("", (Throwable)e);
        }
    }

    public static void applyObjectHolders(Predicate<ResourceLocation> filter) {
        RuntimeException aggregate = new RuntimeException("Failed to apply some object holders, see suppressed exceptions for details");
        objectHolders.forEach(objectHolder -> {
            try {
                objectHolder.accept(filter);
            }
            catch (Exception e) {
                aggregate.addSuppressed(e);
            }
        });
        if (aggregate.getSuppressed().length > 0) {
            throw aggregate;
        }
    }

    private record VanillaObjectHolderData(String holderClass, String registryName, String registryType) {
    }
}

