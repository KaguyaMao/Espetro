/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraftforge.fml.IExtensionPoint
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.IModInfo
 */
package net.minecraftforge.client;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModInfo;

public class ConfigScreenHandler {
    public static Optional<BiFunction<Minecraft, Screen, Screen>> getScreenFactoryFor(IModInfo selectedMod) {
        return ModList.get().getModContainerById(selectedMod.getModId()).flatMap(mc -> mc.getCustomExtension(ConfigScreenFactory.class).map(ConfigScreenFactory::screenFunction));
    }

    public record ConfigScreenFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) implements IExtensionPoint<ConfigScreenFactory>
    {
        public ConfigScreenFactory(Function<Screen, Screen> screenFunction) {
            this((Minecraft mcClient, Screen modsScreen) -> (Screen)screenFunction.apply((Screen)modsScreen));
        }
    }
}

