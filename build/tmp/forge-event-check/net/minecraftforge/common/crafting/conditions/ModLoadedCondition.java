/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraftforge.fml.ModList
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.ModList;

public class ModLoadedCondition
implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "mod_loaded");
    private final String modid;

    public ModLoadedCondition(String modid) {
        this.modid = modid;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return ModList.get().isLoaded(this.modid);
    }

    public String toString() {
        return "mod_loaded(\"" + this.modid + "\")";
    }

    public static class Serializer
    implements IConditionSerializer<ModLoadedCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ModLoadedCondition value) {
            json.addProperty("modid", value.modid);
        }

        @Override
        public ModLoadedCondition read(JsonObject json) {
            return new ModLoadedCondition(GsonHelper.m_13906_((JsonObject)json, (String)"modid"));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

