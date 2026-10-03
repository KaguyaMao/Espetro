/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Transformation
 *  net.minecraft.client.resources.model.ModelState
 */
package net.minecraftforge.client.model;

import com.mojang.math.Transformation;
import net.minecraft.client.resources.model.ModelState;

public final class SimpleModelState
implements ModelState {
    private final Transformation transformation;
    private final boolean uvLocked;

    public SimpleModelState(Transformation transformation, boolean uvLocked) {
        this.transformation = transformation;
        this.uvLocked = uvLocked;
    }

    public SimpleModelState(Transformation transformation) {
        this(transformation, false);
    }

    public Transformation m_6189_() {
        return this.transformation;
    }

    public boolean m_7538_() {
        return this.uvLocked;
    }
}

