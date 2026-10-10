/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants
 *  com.mojang.blaze3d.platform.InputConstants$Key
 *  net.minecraft.client.KeyMapping
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import org.jetbrains.annotations.NotNull;

public interface IForgeKeyMapping {
    private KeyMapping self() {
        return (KeyMapping)this;
    }

    @NotNull
    public InputConstants.Key getKey();

    default public boolean isActiveAndMatches(InputConstants.Key keyCode) {
        return keyCode != InputConstants.f_84822_ && keyCode.equals((Object)this.getKey()) && this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
    }

    default public void setToDefault() {
        this.setKeyModifierAndCode(this.getDefaultKeyModifier(), this.self().m_90861_());
    }

    public void setKeyConflictContext(IKeyConflictContext var1);

    public IKeyConflictContext getKeyConflictContext();

    public KeyModifier getDefaultKeyModifier();

    public KeyModifier getKeyModifier();

    public void setKeyModifierAndCode(KeyModifier var1, InputConstants.Key var2);

    default public boolean isConflictContextAndModifierActive() {
        return this.getKeyConflictContext().isActive() && this.getKeyModifier().isActive(this.getKeyConflictContext());
    }

    default public boolean hasKeyModifierConflict(KeyMapping other) {
        return !(!this.getKeyConflictContext().conflicts(other.getKeyConflictContext()) && !other.getKeyConflictContext().conflicts(this.getKeyConflictContext()) || !this.getKeyModifier().matches(other.getKey()) && !other.getKeyModifier().matches(this.getKey()));
    }
}

