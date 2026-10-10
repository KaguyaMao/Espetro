/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.EnvTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Huffman;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PDData;

public class PDMode
extends EnvMode {
    final EnvTables tables;
    private static final PDMode[] IPD_MODES = PDMode.modes(Huffman.f_huff_ipd, Huffman.t_huff_ipd);
    private static final PDMode[] OPD_MODES = PDMode.modes(Huffman.f_huff_opd, Huffman.t_huff_opd);

    public PDMode(int id, int nr_par, EnvTables tables) {
        super(id, nr_par);
        this.tables = tables;
    }

    @Override
    EnvTables tables() {
        return this.tables;
    }

    @Override
    int stride() {
        return 1;
    }

    @Override
    int clip(int idx) {
        return idx & 7;
    }

    public static PDData IpdData() {
        return new PDData(IPD_MODES);
    }

    public static PDData OpdData() {
        return new PDData(OPD_MODES);
    }

    private static PDMode[] modes(int[][] f, int[][] t) {
        EnvTables tables = new EnvTables(f, t);
        return new PDMode[]{new PDMode(0, 5, tables), new PDMode(1, 11, tables), new PDMode(2, 17, tables), new PDMode(3, 5, tables), new PDMode(4, 11, tables), new PDMode(5, 17, tables)};
    }
}

