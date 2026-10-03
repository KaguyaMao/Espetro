/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.SharedConstants
 *  net.minecraft.gametest.framework.GameTest
 *  net.minecraft.gametest.framework.GameTestRegistry
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoader
 *  net.minecraftforge.fml.loading.FMLLoader
 *  net.minecraftforge.forgespi.language.ModFileScanData
 *  net.minecraftforge.forgespi.language.ModFileScanData$AnnotationData
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.objectweb.asm.Type
 */
package net.minecraftforge.gametest;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.SharedConstants;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

public class ForgeGameTestHooks {
    private static boolean registeredGametests = false;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Type GAME_TEST_HOLDER = Type.getType(GameTestHolder.class);

    public static boolean isGametestEnabled() {
        return !FMLLoader.isProduction() && (SharedConstants.f_136183_ || ForgeGameTestHooks.isGametestServer() || Boolean.getBoolean("forge.enableGameTest"));
    }

    public static boolean isGametestServer() {
        return !FMLLoader.isProduction() && Boolean.getBoolean("forge.gameTestServer");
    }

    public static void registerGametests() {
        if (!registeredGametests && ForgeGameTestHooks.isGametestEnabled() && ModLoader.isLoadingStateValid()) {
            Set<String> enabledNamespaces = ForgeGameTestHooks.getEnabledNamespaces();
            LOGGER.info("Enabled Gametest Namespaces: {}", enabledNamespaces);
            HashSet<Method> gameTestMethods = new HashSet<Method>();
            RegisterGameTestsEvent event = new RegisterGameTestsEvent(gameTestMethods);
            ModLoader.get().postEvent((Event)event);
            ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a -> GAME_TEST_HOLDER.equals((Object)a.annotationType())).forEach(a -> ForgeGameTestHooks.addGameTestMethods(a, gameTestMethods));
            for (Method gameTestMethod : gameTestMethods) {
                GameTestRegistry.register((Method)gameTestMethod, enabledNamespaces);
            }
            registeredGametests = true;
        }
    }

    private static Set<String> getEnabledNamespaces() {
        String enabledNamespacesStr = System.getProperty("forge.enabledGameTestNamespaces");
        if (enabledNamespacesStr == null) {
            return Set.of();
        }
        return Arrays.stream(enabledNamespacesStr.split(",")).filter(s -> !s.isBlank()).collect(Collectors.toUnmodifiableSet());
    }

    private static void addGameTestMethods(ModFileScanData.AnnotationData annotationData, Set<Method> gameTestMethods) {
        try {
            Class<?> clazz = Class.forName(annotationData.clazz().getClassName(), true, ForgeGameTestHooks.class.getClassLoader());
            gameTestMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getTemplateNamespace(Method method) {
        GameTest gameTest = method.getAnnotation(GameTest.class);
        if (gameTest != null && !gameTest.templateNamespace().isEmpty()) {
            return gameTest.templateNamespace();
        }
        GameTestHolder gameTestHolder = method.getDeclaringClass().getAnnotation(GameTestHolder.class);
        if (gameTestHolder != null) {
            return gameTestHolder.value();
        }
        return "minecraft";
    }

    public static boolean prefixGameTestTemplate(Method method) {
        PrefixGameTestTemplate annotation = method.getAnnotation(PrefixGameTestTemplate.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(PrefixGameTestTemplate.class);
        }
        return annotation == null || annotation.value();
    }
}

