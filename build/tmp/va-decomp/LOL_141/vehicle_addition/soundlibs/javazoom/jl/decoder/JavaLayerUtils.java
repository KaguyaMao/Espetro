/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerHook;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;

public class JavaLayerUtils {
    private static JavaLayerHook hook = null;

    public static Object deserialize(InputStream in, Class cls) throws IOException {
        if (cls == null) {
            throw new NullPointerException("cls");
        }
        Object obj = JavaLayerUtils.deserialize(in, cls);
        if (!cls.isInstance(obj)) {
            throw new InvalidObjectException("type of deserialized instance not of required class.");
        }
        return obj;
    }

    public static Object deserialize(InputStream in) throws IOException {
        Object obj;
        if (in == null) {
            throw new NullPointerException("in");
        }
        ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            obj = objIn.readObject();
        }
        catch (ClassNotFoundException ex) {
            throw new InvalidClassException(ex.toString());
        }
        return obj;
    }

    public static Object deserializeArray(InputStream in, Class elemType, int length) throws IOException {
        int arrayLength;
        if (elemType == null) {
            throw new NullPointerException("elemType");
        }
        if (length < -1) {
            throw new IllegalArgumentException("length");
        }
        Object obj = JavaLayerUtils.deserialize(in);
        Class<?> cls = obj.getClass();
        if (!cls.isArray()) {
            throw new InvalidObjectException("object is not an array");
        }
        Class<?> arrayElemType = cls.getComponentType();
        if (arrayElemType != elemType) {
            throw new InvalidObjectException("unexpected array component type");
        }
        if (length != -1 && (arrayLength = Array.getLength(obj)) != length) {
            throw new InvalidObjectException("array length mismatch");
        }
        return obj;
    }

    public static Object deserializeArrayResource(String name, Class elemType, int length) throws IOException {
        InputStream str = JavaLayerUtils.getResourceAsStream(name);
        if (str == null) {
            throw new IOException("unable to load resource '" + name + "'");
        }
        Object obj = JavaLayerUtils.deserializeArray(str, elemType, length);
        return obj;
    }

    public static void serialize(OutputStream out, Object obj) throws IOException {
        if (out == null) {
            throw new NullPointerException("out");
        }
        if (obj == null) {
            throw new NullPointerException("obj");
        }
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        objOut.writeObject(obj);
    }

    public static synchronized void setHook(JavaLayerHook hook0) {
        hook = hook0;
    }

    public static synchronized JavaLayerHook getHook() {
        return hook;
    }

    public static synchronized InputStream getResourceAsStream(String name) {
        InputStream is = null;
        if (hook != null) {
            is = hook.getResourceAsStream(name);
        } else {
            Class<JavaLayerUtils> cls = JavaLayerUtils.class;
            is = cls.getResourceAsStream(name);
        }
        return is;
    }
}

