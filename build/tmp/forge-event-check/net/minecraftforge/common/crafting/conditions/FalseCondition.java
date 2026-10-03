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

public final class FalseCondition
implements ICondition {
    public static final FalseCondition INSTANCE = new FalseCondition();
    private static final ResourceLocation NAME = new ResourceLocation("forge", "false");

    private FalseCondition() {
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext condition) {
        return false;
    }

    public String toString() {
        return "false";
    }

    public static class Serializer
    implements IConditionSerializer<FalseCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, FalseCondition value) {
        }

        @Override
        public FalseCondition read(JsonObject json) {
            return INSTANCE;
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

