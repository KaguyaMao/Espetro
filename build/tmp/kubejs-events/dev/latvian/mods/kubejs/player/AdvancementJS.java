/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.player;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class AdvancementJS {
    public final Advancement advancement;

    public AdvancementJS(Advancement a) {
        this.advancement = a;
    }

    public boolean equals(Object o) {
        return o == this || o instanceof AdvancementJS && this.advancement.equals((Object)((AdvancementJS)o).advancement);
    }

    public int hashCode() {
        return this.advancement.hashCode();
    }

    public String toString() {
        return this.getId().toString();
    }

    public ResourceLocation id() {
        return this.getId();
    }

    public ResourceLocation getId() {
        return this.advancement.m_138327_();
    }

    @Nullable
    public AdvancementJS getParent() {
        return this.advancement.m_138319_() == null ? null : new AdvancementJS(this.advancement.m_138319_());
    }

    public Set<AdvancementJS> getChildren() {
        LinkedHashSet<AdvancementJS> set = new LinkedHashSet<AdvancementJS>();
        for (Advancement a : this.advancement.m_138322_()) {
            set.add(new AdvancementJS(a));
        }
        return set;
    }

    public void addChild(AdvancementJS a) {
        this.advancement.m_138317_(a.advancement);
    }

    public Component getDisplayText() {
        return this.advancement.m_138330_();
    }

    public boolean hasDisplay() {
        return this.advancement.m_138320_() != null;
    }

    public Component getTitle() {
        return this.advancement.m_138320_() != null ? this.advancement.m_138320_().m_14977_() : Component.m_237119_();
    }

    public Component getDescription() {
        return this.advancement.m_138320_() != null ? this.advancement.m_138320_().m_14985_() : Component.m_237119_();
    }
}

