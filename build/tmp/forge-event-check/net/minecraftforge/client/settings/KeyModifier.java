/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.settings;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.jetbrains.annotations.Nullable;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum KeyModifier {
    CONTROL{

        @Override
        public boolean matches(InputConstants.Key key) {
            int keyCode = key.m_84873_();
            if (Minecraft.f_91002_) {
                return keyCode == 343 || keyCode == 347;
            }
            return keyCode == 341 || keyCode == 345;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.m_96637_();
        }

        @Override
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            String localizationFormatKey = Minecraft.f_91002_ ? "forge.controlsgui.control.mac" : "forge.controlsgui.control";
            return Component.m_237110_((String)localizationFormatKey, (Object[])new Object[]{defaultLogic.get()});
        }
    }
    ,
    SHIFT{

        @Override
        public boolean matches(InputConstants.Key key) {
            return key.m_84873_() == 340 || key.m_84873_() == 344;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.m_96638_();
        }

        @Override
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            return Component.m_237110_((String)"forge.controlsgui.shift", (Object[])new Object[]{defaultLogic.get()});
        }
    }
    ,
    ALT{

        @Override
        public boolean matches(InputConstants.Key key) {
            return key.m_84873_() == 342 || key.m_84873_() == 346;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            return Screen.m_96639_();
        }

        @Override
        public Component getCombinedName(InputConstants.Key keyCode, Supplier<Component> defaultLogic) {
            return Component.m_237110_((String)"forge.controlsgui.alt", (Object[])new Object[]{defaultLogic.get()});
        }
    }
    ,
    NONE{

        @Override
        public boolean matches(InputConstants.Key key) {
            return false;
        }

        @Override
        public boolean isActive(@Nullable IKeyConflictContext conflictContext) {
            if (conflictContext != null && !conflictContext.conflicts(KeyConflictContext.IN_GAME)) {
                for (KeyModifier keyModifier : VALUES) {
                    if (!keyModifier.isActive(conflictContext)) continue;
                    return false;
                }
            }
            return true;
        }

        @Override
        public Component getCombinedName(InputConstants.Key key, Supplier<Component> defaultLogic) {
            return defaultLogic.get();
        }
    };

    @Deprecated(forRemoval=true, since="1.20.2")
    public static final KeyModifier[] MODIFIER_VALUES;
    private static final KeyModifier[] VALUES;
    private static final List<KeyModifier> VALUES_LIST;
    private static final List<KeyModifier> ALL;

    @Deprecated(forRemoval=true, since="1.20.2")
    public static KeyModifier getActiveModifier() {
        for (KeyModifier keyModifier : VALUES) {
            if (!keyModifier.isActive(null)) continue;
            return keyModifier;
        }
        return NONE;
    }

    public static final List<KeyModifier> getValues(boolean includeNone) {
        return includeNone ? ALL : VALUES_LIST;
    }

    @Nullable
    public static KeyModifier getModifier(InputConstants.Key key) {
        for (KeyModifier modifier : VALUES) {
            if (!modifier.matches(key)) continue;
            return modifier;
        }
        return null;
    }

    public static boolean isKeyCodeModifier(InputConstants.Key key) {
        for (KeyModifier keyModifier : VALUES) {
            if (!keyModifier.matches(key)) continue;
            return true;
        }
        return false;
    }

    public static KeyModifier valueFromString(String stringValue) {
        try {
            return KeyModifier.valueOf(stringValue);
        }
        catch (IllegalArgumentException | NullPointerException ignored) {
            return NONE;
        }
    }

    public abstract boolean matches(InputConstants.Key var1);

    public abstract boolean isActive(@Nullable IKeyConflictContext var1);

    public abstract Component getCombinedName(InputConstants.Key var1, Supplier<Component> var2);

    static {
        MODIFIER_VALUES = new KeyModifier[]{SHIFT, CONTROL, ALT};
        VALUES = new KeyModifier[]{SHIFT, CONTROL, ALT};
        VALUES_LIST = List.of(SHIFT, CONTROL, ALT);
        ALL = List.of(SHIFT, CONTROL, ALT, NONE);
    }
}

