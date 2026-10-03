/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.List;
import java.util.Locale;

public class DOMMatrix {
    public double a = 1.0;
    public double b = 0.0;
    public double c = 0.0;
    public double d = 1.0;
    public double e = 0.0;
    public double f = 0.0;

    public DOMMatrix() {
    }

    public DOMMatrix(Object init) {
        this.setFrom(init);
    }

    public DOMMatrix translateSelf(double tx, double ty) {
        AffineTransform transform = this.toAffineTransform();
        transform.translate(tx, ty);
        this.setFrom(transform);
        return this;
    }

    public DOMMatrix scaleSelf(double sx) {
        return this.scaleSelf(sx, sx);
    }

    public DOMMatrix scaleSelf(double sx, double sy) {
        AffineTransform transform = this.toAffineTransform();
        transform.scale(sx, sy);
        this.setFrom(transform);
        return this;
    }

    public DOMMatrix rotateSelf(double degrees) {
        AffineTransform transform = this.toAffineTransform();
        transform.rotate(Math.toRadians(degrees));
        this.setFrom(transform);
        return this;
    }

    public DOMMatrix multiplySelf(Object other) {
        AffineTransform transform = this.toAffineTransform();
        transform.concatenate(DOMMatrix.from(other));
        this.setFrom(transform);
        return this;
    }

    public DOMMatrix invertSelf() {
        try {
            this.setFrom(this.toAffineTransform().createInverse());
        }
        catch (NoninvertibleTransformException noninvertibleTransformException) {
            // empty catch block
        }
        return this;
    }

    public AffineTransform toAffineTransform() {
        return new AffineTransform(this.a, this.b, this.c, this.d, this.e, this.f);
    }

    public void setFrom(Object init) {
        AffineTransform transform = DOMMatrix.from(init);
        this.a = transform.getScaleX();
        this.b = transform.getShearY();
        this.c = transform.getShearX();
        this.d = transform.getScaleY();
        this.e = transform.getTranslateX();
        this.f = transform.getTranslateY();
    }

    public static DOMMatrix fromAffineTransform(AffineTransform transform) {
        return new DOMMatrix(transform == null ? null : transform);
    }

    public static AffineTransform from(Object init) {
        if (init == null) {
            return new AffineTransform();
        }
        if (init instanceof DOMMatrix) {
            DOMMatrix matrix = (DOMMatrix)init;
            return matrix.toAffineTransform();
        }
        if (init instanceof AffineTransform) {
            AffineTransform transform = (AffineTransform)init;
            return new AffineTransform(transform);
        }
        if (init instanceof double[]) {
            double[] values = (double[])init;
            return DOMMatrix.fromNumbers(values);
        }
        if (init instanceof float[]) {
            float[] values = (float[])init;
            double[] converted = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                converted[i] = values[i];
            }
            return DOMMatrix.fromNumbers(converted);
        }
        if (init instanceof int[]) {
            int[] values = (int[])init;
            double[] converted = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                converted[i] = values[i];
            }
            return DOMMatrix.fromNumbers(converted);
        }
        if (init instanceof Object[]) {
            Object[] values = (Object[])init;
            return DOMMatrix.fromObjectArray(values);
        }
        if (init instanceof List) {
            List values = (List)init;
            return DOMMatrix.fromList(values);
        }
        if (init instanceof String) {
            String text = (String)init;
            return DOMMatrix.fromString(text);
        }
        return new AffineTransform();
    }

    private static AffineTransform fromNumbers(double[] values) {
        if (values.length >= 6) {
            return new AffineTransform(values[0], values[1], values[2], values[3], values[4], values[5]);
        }
        return new AffineTransform();
    }

    private static AffineTransform fromObjectArray(Object[] values) {
        if (values.length < 6) {
            return new AffineTransform();
        }
        double[] converted = new double[6];
        for (int i = 0; i < 6; ++i) {
            Object object = values[i];
            if (!(object instanceof Number)) {
                return new AffineTransform();
            }
            Number number = (Number)object;
            converted[i] = number.doubleValue();
        }
        return DOMMatrix.fromNumbers(converted);
    }

    private static AffineTransform fromList(List<?> values) {
        if (values.size() < 6) {
            return new AffineTransform();
        }
        double[] converted = new double[6];
        for (int i = 0; i < 6; ++i) {
            Object value = values.get(i);
            if (!(value instanceof Number)) {
                return new AffineTransform();
            }
            Number number = (Number)value;
            converted[i] = number.doubleValue();
        }
        return DOMMatrix.fromNumbers(converted);
    }

    private static AffineTransform fromString(String raw) {
        String inner;
        String[] parts;
        if (raw == null || raw.isBlank()) {
            return new AffineTransform();
        }
        String text = raw.trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        if (normalized.startsWith("matrix(") && normalized.endsWith(")") && (parts = (inner = text.substring(text.indexOf(40) + 1, text.length() - 1)).split("[,\\s]+")).length >= 6) {
            double[] values = new double[6];
            try {
                for (int i = 0; i < 6; ++i) {
                    values[i] = Double.parseDouble(parts[i]);
                }
                return DOMMatrix.fromNumbers(values);
            }
            catch (NumberFormatException ignored) {
                return new AffineTransform();
            }
        }
        return new AffineTransform();
    }
}

