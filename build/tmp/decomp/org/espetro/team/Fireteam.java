/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

public enum Fireteam {
    A(0, "A", -13703466),
    B(1, "B", -5214977),
    C(2, "C", -11690497);

    public static final int COUNT = 3;
    private final int index;
    private final String label;
    private final int mapColor;

    private Fireteam(int index, String label, int mapColor) {
        this.index = index;
        this.label = label;
        this.mapColor = mapColor;
    }

    public int index() {
        return this.index;
    }

    public String label() {
        return this.label;
    }

    public int color() {
        return this.mapColor;
    }

    public static Fireteam fromIndex(int index) {
        Fireteam[] values = Fireteam.values();
        if (index < 0 || index >= values.length) {
            return A;
        }
        return values[index];
    }

    public static Fireteam fromNetwork(byte b) {
        return Fireteam.fromIndex(b);
    }

    public byte toNetwork() {
        return (byte)this.index;
    }
}

