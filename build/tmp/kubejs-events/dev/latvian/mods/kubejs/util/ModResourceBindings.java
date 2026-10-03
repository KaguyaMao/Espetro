/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Mod
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import dev.architectury.platform.Mod;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypePredicate;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.jetbrains.annotations.Nullable;

public class ModResourceBindings {
    private final Map<String, Collection<BindingProvider>> bindings = new HashMap<String, Collection<BindingProvider>>();

    public void addBindings(BindingsEvent event) {
        for (Map.Entry<String, Collection<BindingProvider>> modBindings : this.bindings.entrySet()) {
            String modName = modBindings.getKey();
            Collection<BindingProvider> providers = modBindings.getValue();
            ArrayList<String> addedBindings = new ArrayList<String>();
            for (BindingProvider provider : providers) {
                String name = provider.name();
                if (!provider.test(event.getType())) continue;
                try {
                    event.add(name, provider.generate());
                    addedBindings.add(name);
                }
                catch (Exception e) {
                    KubeJS.LOGGER.error("Error adding binding for script type {} from mod '{}': {}", new Object[]{event.getType(), modName, name, e});
                }
            }
            KubeJS.LOGGER.info("Added bindings for script type {} from mod '{}': {}", new Object[]{event.getType(), modName, addedBindings});
        }
    }

    public void readBindings(Mod mod) throws IOException {
        Optional resource = mod.findResource(new String[]{"kubejs.bindings.txt"});
        if (resource.isEmpty()) {
            return;
        }
        try (Stream<String> lines = Files.lines((Path)resource.get());){
            List<BindingProvider> providers = lines.map(s -> s.split("#", 2)[0].trim()).filter(line -> !line.isBlank()).map(line -> this.createProvider(mod, (String)line)).filter(Objects::nonNull).toList();
            this.bindings.put(mod.getModId(), providers);
        }
    }

    @Nullable
    private BindingProvider createProvider(Mod mod, String line) {
        String[] split = line.split("\\s+");
        if (split.length < 3) {
            KubeJS.LOGGER.error("Invalid binding for '{}' in line: {}", (Object)mod.getModId(), (Object)line);
            return null;
        }
        ScriptTypePredicate scriptTypeFilter = this.typePredicateOf(split[0]);
        String name = split[1];
        String className = split[2];
        ClassBindingProvider classProvider = new ClassBindingProvider(name, scriptTypeFilter, className);
        if (split.length == 3) {
            return classProvider;
        }
        String methodFieldOrConstructor = split[3];
        if (methodFieldOrConstructor.equals("<init>")) {
            return new InstanceBindingProvider(classProvider);
        }
        return new InvokeBindingProvider(classProvider, methodFieldOrConstructor);
    }

    private ScriptTypePredicate typePredicateOf(String typeString) {
        String lower;
        return switch (lower = typeString.toLowerCase()) {
            case "*", "all" -> ScriptTypePredicate.ALL;
            case "common" -> ScriptTypePredicate.COMMON;
            case "startup_or_client" -> ScriptTypePredicate.STARTUP_OR_CLIENT;
            case "startup_or_server" -> ScriptTypePredicate.STARTUP_OR_SERVER;
            default -> {
                for (ScriptType type : ScriptType.VALUES) {
                    if (!type.name.equals(lower)) continue;
                    yield type;
                }
                throw new IllegalArgumentException("Unknown script type predicate: " + typeString);
            }
        };
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    static interface BindingProvider
    extends ScriptTypePredicate {
        public String name();

        public Object generate();
    }

    record ClassBindingProvider(String name, ScriptTypePredicate filter, String className) implements BindingProvider
    {
        @Override
        public Object generate() {
            try {
                return this.getClass().getClassLoader().loadClass(this.className);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean test(ScriptType scriptType) {
            return this.filter.test(scriptType);
        }
    }

    record InstanceBindingProvider(ClassBindingProvider parent) implements BindingProvider
    {
        @Override
        public String name() {
            return this.parent().name();
        }

        @Override
        public Object generate() {
            Class clazz = (Class)this.parent().generate();
            try {
                Constructor constructor = clazz.getConstructor(new Class[0]);
                return constructor.newInstance(new Object[0]);
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new IllegalStateException("[Bindings] Failed to find default constructor in class '" + clazz.getName() + "'");
            }
        }

        @Override
        public boolean test(ScriptType scriptType) {
            return this.parent().test(scriptType);
        }
    }

    record InvokeBindingProvider(ClassBindingProvider parent, String methodOrField) implements BindingProvider
    {
        @Override
        public String name() {
            return this.parent().name();
        }

        @Override
        public Object generate() {
            Class clazz = (Class)this.parent().generate();
            Object f = this.byField(clazz);
            if (f != null) {
                return f;
            }
            Object m = this.byMethod(clazz);
            if (m != null) {
                return m;
            }
            throw new IllegalStateException("[Bindings] Failed to find static field or method '" + this.methodOrField + "' in class '" + clazz.getName() + "'");
        }

        @Override
        public boolean test(ScriptType scriptType) {
            return this.parent().test(scriptType);
        }

        @Nullable
        private Object byField(Class<?> clazz) {
            try {
                Field field = clazz.getField(this.methodOrField);
                if (Modifier.isStatic(field.getModifiers())) {
                    return field.get(null);
                }
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException("[Bindings] Failed to get static field '" + this.methodOrField + "' in class '" + clazz.getName() + "'", e);
            }
            catch (NoSuchFieldException noSuchFieldException) {
                // empty catch block
            }
            return null;
        }

        @Nullable
        private Object byMethod(Class<?> clazz) {
            try {
                Method method = clazz.getMethod(this.methodOrField, new Class[0]);
                if (Modifier.isStatic(method.getModifiers())) {
                    return method.invoke(null, new Object[0]);
                }
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("[Bindings] Failed to invoke static method '" + this.methodOrField + "' in class '" + clazz.getName() + "'", e);
            }
            catch (NoSuchMethodException noSuchMethodException) {
                // empty catch block
            }
            return null;
        }
    }
}

