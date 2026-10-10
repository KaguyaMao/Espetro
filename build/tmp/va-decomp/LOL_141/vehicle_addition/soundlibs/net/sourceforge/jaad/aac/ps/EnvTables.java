/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Huffman;

class EnvTables {
    final Huffman.Table f;
    final Huffman.Table t;

    EnvTables(Huffman.Table f, Huffman.Table t) {
        this.f = f;
        this.t = t;
    }

    EnvTables(int[][] f, int[][] t) {
        this(Huffman.table(f), Huffman.table(t));
    }

    Huffman.Table table(boolean dt) {
        return dt ? this.t : this.f;
    }
}

