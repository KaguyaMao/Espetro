/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 */
package net.minecraftforge.client.model.geometry;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public interface IGeometryLoader<T extends IUnbakedGeometry<T>> {
    public T read(JsonObject var1, JsonDeserializationContext var2) throws JsonParseException;
}

