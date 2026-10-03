/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public final class TrueCondition
implements ICondition {
    public static final TrueCondition INSTANCE = new TrueCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "true");

    private TrueCondition() {
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return true;
    }

    public String toString() {
        return "true";
    }

    public static class Serializer
    implements IConditionSerializer<TrueCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TrueCondition value) {
        }

        @Override
        public TrueCondition read(JsonObject json) {
            return INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

