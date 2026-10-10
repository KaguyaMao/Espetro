/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Huffman;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ICMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;

public class IIDMode
extends ICMode {
    private static final IIDTables DEFAULT_TABLES = new IIDTables(Huffman.f_huff_iid_def, Huffman.t_huff_iid_def, 7, PSTables.sf_iid_normal, PSTables.cos_betas_normal, PSTables.sin_betas_normal, PSTables.sin_gammas_normal, PSTables.cos_gammas_normal, PSTables.sincos_alphas_B_normal);
    private static final IIDTables FINE_TABLES = new IIDTables(Huffman.f_huff_iid_fine, Huffman.t_huff_iid_fine, 15, PSTables.sf_iid_fine, PSTables.cos_betas_fine, PSTables.sin_betas_fine, PSTables.sin_gammas_fine, PSTables.cos_gammas_fine, PSTables.sincos_alphas_B_fine);
    final IIDTables tables;
    private static final IIDMode[] IID_MODES = new IIDMode[]{new IIDMode(0, 10, DEFAULT_TABLES), new IIDMode(1, 20, DEFAULT_TABLES), new IIDMode(2, 34, DEFAULT_TABLES), new IIDMode(3, 10, FINE_TABLES), new IIDMode(4, 20, FINE_TABLES), new IIDMode(5, 34, FINE_TABLES)};

    public IIDMode(int id, int nr_par, IIDTables tables) {
        super(id, nr_par);
        this.tables = tables;
    }

    @Override
    IIDTables tables() {
        return this.tables;
    }

    @Override
    int clip(int idx) {
        int clip = this.tables.num_steps;
        return Utils.clip(idx, -clip, clip);
    }

    static IIDMode mode(int id) {
        return IID_MODES[id];
    }

    public static IIDData data() {
        return new IIDData();
    }
}

