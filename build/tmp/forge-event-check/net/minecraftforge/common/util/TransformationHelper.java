/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.math.Axis
 *  com.mojang.math.Transformation
 *  net.minecraft.util.Mth
 *  net.minecraft.util.StringRepresentable
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  org.joml.Vector4f
 */
package net.minecraftforge.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Axis;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public final class TransformationHelper {
    private static final double THRESHOLD = 0.9995;

    public static Quaternionf quatFromXYZ(Vector3f xyz, boolean degrees) {
        return TransformationHelper.quatFromXYZ(xyz.x, xyz.y, xyz.z, degrees);
    }

    public static Quaternionf quatFromXYZ(float[] xyz, boolean degrees) {
        return TransformationHelper.quatFromXYZ(xyz[0], xyz[1], xyz[2], degrees);
    }

    public static Quaternionf quatFromXYZ(float x, float y, float z, boolean degrees) {
        float conversionFactor = degrees ? (float)Math.PI / 180 : 1.0f;
        return new Quaternionf().rotationXYZ(x * conversionFactor, y * conversionFactor, z * conversionFactor);
    }

    public static Quaternionf makeQuaternion(float[] values) {
        return new Quaternionf(values[0], values[1], values[2], values[3]);
    }

    public static Vector3f lerp(Vector3f from, Vector3f to, float progress) {
        Vector3f res = new Vector3f((Vector3fc)from);
        res.lerp((Vector3fc)to, progress);
        return res;
    }

    public static Quaternionf slerp(Quaternionfc v0, Quaternionfc v1, float t) {
        float dot = v0.x() * v1.x() + v0.y() * v1.y() + v0.z() * v1.z() + v0.w() * v1.w();
        if (dot < 0.0f) {
            v1 = new Quaternionf(-v1.x(), -v1.y(), -v1.z(), -v1.w());
            dot = -dot;
        }
        if ((double)dot > 0.9995) {
            float x = Mth.m_14179_((float)t, (float)v0.x(), (float)v1.x());
            float y = Mth.m_14179_((float)t, (float)v0.y(), (float)v1.y());
            float z = Mth.m_14179_((float)t, (float)v0.z(), (float)v1.z());
            float w = Mth.m_14179_((float)t, (float)v0.w(), (float)v1.w());
            return new Quaternionf(x, y, z, w);
        }
        float angle01 = (float)Math.acos(dot);
        float angle0t = angle01 * t;
        float sin0t = Mth.m_14031_((float)angle0t);
        float sin01 = Mth.m_14031_((float)angle01);
        float sin1t = Mth.m_14031_((float)(angle01 - angle0t));
        float s1 = sin0t / sin01;
        float s0 = sin1t / sin01;
        return new Quaternionf(s0 * v0.x() + s1 * v1.x(), s0 * v0.y() + s1 * v1.y(), s0 * v0.z() + s1 * v1.z(), s0 * v0.w() + s1 * v1.w());
    }

    public static Transformation slerp(Transformation one, Transformation that, float progress) {
        return new Transformation(TransformationHelper.lerp(one.m_252829_(), that.m_252829_(), progress), TransformationHelper.slerp((Quaternionfc)one.m_253244_(), (Quaternionfc)that.m_253244_(), progress), TransformationHelper.lerp(one.m_252900_(), that.m_252900_(), progress), TransformationHelper.slerp((Quaternionfc)one.m_252848_(), (Quaternionfc)that.m_252848_(), progress));
    }

    public static boolean epsilonEquals(Vector4f v1, Vector4f v2, float epsilon) {
        return Mth.m_14154_((float)(v1.x() - v2.x())) < epsilon && Mth.m_14154_((float)(v1.y() - v2.y())) < epsilon && Mth.m_14154_((float)(v1.z() - v2.z())) < epsilon && Mth.m_14154_((float)(v1.w() - v2.w())) < epsilon;
    }

    public static enum TransformOrigin implements StringRepresentable
    {
        CENTER(new Vector3f(0.5f, 0.5f, 0.5f), "center"),
        CORNER(new Vector3f(), "corner"),
        OPPOSING_CORNER(new Vector3f(1.0f, 1.0f, 1.0f), "opposing-corner");

        private final Vector3f vec;
        private final String name;

        private TransformOrigin(Vector3f vec, String name) {
            this.vec = vec;
            this.name = name;
        }

        public Vector3f getVector() {
            return this.vec;
        }

        @NotNull
        public String m_7912_() {
            return this.name;
        }

        @Nullable
        public static TransformOrigin fromString(String originName) {
            if (CENTER.m_7912_().equals(originName)) {
                return CENTER;
            }
            if (CORNER.m_7912_().equals(originName)) {
                return CORNER;
            }
            if (OPPOSING_CORNER.m_7912_().equals(originName)) {
                return OPPOSING_CORNER;
            }
            return null;
        }
    }

    public static class Deserializer
    implements JsonDeserializer<Transformation> {
        public Transformation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                String transform = json.getAsString();
                if (transform.equals("identity")) {
                    return Transformation.m_121093_();
                }
                throw new JsonParseException("TRSR: unknown default string: " + transform);
            }
            if (json.isJsonArray()) {
                return new Transformation(Deserializer.parseMatrix(json));
            }
            if (!json.isJsonObject()) {
                throw new JsonParseException("TRSR: expected array or object, got: " + String.valueOf(json));
            }
            JsonObject obj = json.getAsJsonObject();
            if (obj.has("matrix")) {
                Transformation ret = new Transformation(Deserializer.parseMatrix(obj.get("matrix")));
                if (obj.entrySet().size() > 1) {
                    throw new JsonParseException("TRSR: can't combine matrix and other keys");
                }
                return ret;
            }
            Vector3f translation = null;
            Quaternionf leftRot = null;
            Vector3f scale = null;
            Quaternionf rightRot = null;
            Vector3f origin = TransformOrigin.OPPOSING_CORNER.getVector();
            HashSet elements = new HashSet(obj.keySet());
            if (obj.has("translation")) {
                translation = new Vector3f(Deserializer.parseFloatArray(obj.get("translation"), 3, "Translation"));
                elements.remove("translation");
            }
            if (obj.has("rotation")) {
                leftRot = Deserializer.parseRotation(obj.get("rotation"));
                elements.remove("rotation");
            } else if (obj.has("left_rotation")) {
                leftRot = Deserializer.parseRotation(obj.get("left_rotation"));
                elements.remove("left_rotation");
            }
            if (obj.has("scale")) {
                if (!obj.get("scale").isJsonArray()) {
                    try {
                        float s = obj.get("scale").getAsNumber().floatValue();
                        scale = new Vector3f(s, s, s);
                    }
                    catch (ClassCastException ex) {
                        throw new JsonParseException("TRSR scale: expected number or array, got: " + String.valueOf(obj.get("scale")));
                    }
                } else {
                    scale = new Vector3f(Deserializer.parseFloatArray(obj.get("scale"), 3, "Scale"));
                }
                elements.remove("scale");
            }
            if (obj.has("right_rotation")) {
                rightRot = Deserializer.parseRotation(obj.get("right_rotation"));
                elements.remove("right_rotation");
            } else if (obj.has("post-rotation")) {
                rightRot = Deserializer.parseRotation(obj.get("post-rotation"));
                elements.remove("post-rotation");
            }
            if (obj.has("origin")) {
                origin = Deserializer.parseOrigin(obj);
                elements.remove("origin");
            }
            if (!elements.isEmpty()) {
                throw new JsonParseException("TRSR: can either have single 'matrix' key, or a combination of 'translation', 'rotation' OR 'left_rotation', 'scale', 'post-rotation' (legacy) OR 'right_rotation', 'origin'. Found: " + String.join((CharSequence)", ", elements));
            }
            Transformation matrix = new Transformation(translation, leftRot, scale, rightRot);
            return matrix.applyOrigin(new Vector3f((Vector3fc)origin));
        }

        private static Vector3f parseOrigin(JsonObject obj) {
            Vector3f origin = null;
            JsonElement originElement = obj.get("origin");
            if (originElement.isJsonArray()) {
                origin = new Vector3f(Deserializer.parseFloatArray(originElement, 3, "Origin"));
            } else if (originElement.isJsonPrimitive()) {
                String originString = originElement.getAsString();
                TransformOrigin originEnum = TransformOrigin.fromString(originString);
                if (originEnum == null) {
                    throw new JsonParseException("Origin: expected one of 'center', 'corner', 'opposing-corner'");
                }
                origin = originEnum.getVector();
            } else {
                throw new JsonParseException("Origin: expected an array or one of 'center', 'corner', 'opposing-corner'");
            }
            return origin;
        }

        public static Matrix4f parseMatrix(JsonElement e) {
            if (!e.isJsonArray()) {
                throw new JsonParseException("Matrix: expected an array, got: " + String.valueOf(e));
            }
            JsonArray m = e.getAsJsonArray();
            if (m.size() != 3) {
                throw new JsonParseException("Matrix: expected an array of length 3, got: " + m.size());
            }
            Matrix4f matrix = new Matrix4f();
            for (int rowIdx = 0; rowIdx < 3; ++rowIdx) {
                if (!m.get(rowIdx).isJsonArray()) {
                    throw new JsonParseException("Matrix row: expected an array, got: " + String.valueOf(m.get(rowIdx)));
                }
                JsonArray r = m.get(rowIdx).getAsJsonArray();
                if (r.size() != 4) {
                    throw new JsonParseException("Matrix row: expected an array of length 4, got: " + r.size());
                }
                for (int columnIdx = 0; columnIdx < 4; ++columnIdx) {
                    try {
                        matrix.set(columnIdx, rowIdx, r.get(columnIdx).getAsNumber().floatValue());
                        continue;
                    }
                    catch (ClassCastException ex) {
                        throw new JsonParseException("Matrix element: expected number, got: " + String.valueOf(r.get(columnIdx)));
                    }
                }
            }
            matrix.determineProperties();
            return matrix;
        }

        public static float[] parseFloatArray(JsonElement e, int length, String prefix) {
            if (!e.isJsonArray()) {
                throw new JsonParseException(prefix + ": expected an array, got: " + String.valueOf(e));
            }
            JsonArray t = e.getAsJsonArray();
            if (t.size() != length) {
                throw new JsonParseException(prefix + ": expected an array of length " + length + ", got: " + t.size());
            }
            float[] ret = new float[length];
            for (int i = 0; i < length; ++i) {
                try {
                    ret[i] = t.get(i).getAsNumber().floatValue();
                    continue;
                }
                catch (ClassCastException ex) {
                    throw new JsonParseException(prefix + " element: expected number, got: " + String.valueOf(t.get(i)));
                }
            }
            return ret;
        }

        public static Quaternionf parseAxisRotation(JsonElement e) {
            Quaternionf ret;
            block7: {
                if (!e.isJsonObject()) {
                    throw new JsonParseException("Axis rotation: object expected, got: " + String.valueOf(e));
                }
                JsonObject obj = e.getAsJsonObject();
                if (obj.entrySet().size() != 1) {
                    throw new JsonParseException("Axis rotation: expected single axis object, got: " + String.valueOf(e));
                }
                Map.Entry entry = (Map.Entry)obj.entrySet().iterator().next();
                try {
                    if (((String)entry.getKey()).equals("x")) {
                        ret = Axis.f_252529_.m_252977_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                        break block7;
                    }
                    if (((String)entry.getKey()).equals("y")) {
                        ret = Axis.f_252436_.m_252977_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                        break block7;
                    }
                    if (((String)entry.getKey()).equals("z")) {
                        ret = Axis.f_252403_.m_252977_(((JsonElement)entry.getValue()).getAsNumber().floatValue());
                        break block7;
                    }
                    throw new JsonParseException("Axis rotation: expected single axis key, got: " + (String)entry.getKey());
                }
                catch (ClassCastException ex) {
                    throw new JsonParseException("Axis rotation value: expected number, got: " + String.valueOf(entry.getValue()));
                }
            }
            return ret;
        }

        public static Quaternionf parseRotation(JsonElement e) {
            if (e.isJsonArray()) {
                if (e.getAsJsonArray().get(0).isJsonObject()) {
                    Quaternionf ret = new Quaternionf();
                    for (JsonElement a : e.getAsJsonArray()) {
                        ret.mul((Quaternionfc)Deserializer.parseAxisRotation(a));
                    }
                    return ret;
                }
                if (e.isJsonArray()) {
                    JsonArray array = e.getAsJsonArray();
                    if (array.size() == 3) {
                        return TransformationHelper.quatFromXYZ(Deserializer.parseFloatArray(e, 3, "Rotation"), true);
                    }
                    return TransformationHelper.makeQuaternion(Deserializer.parseFloatArray(e, 4, "Rotation"));
                }
                throw new JsonParseException("Rotation: expected array or object, got: " + String.valueOf(e));
            }
            if (e.isJsonObject()) {
                return Deserializer.parseAxisRotation(e);
            }
            throw new JsonParseException("Rotation: expected array or object, got: " + String.valueOf(e));
        }
    }
}

