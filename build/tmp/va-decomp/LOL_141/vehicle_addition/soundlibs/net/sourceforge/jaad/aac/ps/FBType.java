/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter12;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter2;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter4;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filter8;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSTables;
import java.util.Comparator;

public class FBType {
    public static final Comparator<FBType> CMP = Comparator.nullsLast(Comparator.comparingInt(t -> t.nr_par_bands));
    public static final FBType T34 = new FBType(PSTables.group_border34, PSTables.map_group2bk34, 50, 32, 34, 5, PSTables.Phi_Fract_SubQmf34, PSTables.Q_Fract_allpass_SubQmf34, new Filter[]{Filter12.f, Filter8.f34, Filter4.f, Filter4.f, Filter4.f});
    public static final FBType T20 = new FBType(PSTables.group_border20, PSTables.map_group2bk20, 22, 10, 20, 3, PSTables.Phi_Fract_SubQmf20, PSTables.Q_Fract_allpass_SubQmf20, new Filter[]{Filter8.f20, Filter2.f, Filter2.f});
    final String name;
    final int num_groups;
    final int num_hybrid_groups;
    final int nr_par_bands;
    final int decay_cutoff;
    final int[] group_border;
    final int[] map_group2bk;
    final float[][] phiFract;
    final float[][][] qFractAllpassSubQmf;
    final Filter[] filters;

    public static FBType max(FBType a, FBType b) {
        return CMP.compare(a, b) < 0 ? a : b;
    }

    public FBType max(FBType other) {
        return FBType.max(this, other);
    }

    public FBType(int[] group_border, int[] map_group2bk, int num_groups, int num_hybrid_groups, int nr_par_bands, int decay_cutoff, float[][] phiFract, float[][][] qFractAllpassSubQmf, Filter[] filters) {
        this.name = "FBType" + nr_par_bands;
        this.num_groups = num_groups;
        this.num_hybrid_groups = num_hybrid_groups;
        this.nr_par_bands = nr_par_bands;
        this.decay_cutoff = decay_cutoff;
        this.group_border = group_border;
        this.map_group2bk = map_group2bk;
        this.phiFract = phiFract;
        this.qFractAllpassSubQmf = qFractAllpassSubQmf;
        this.filters = filters;
    }

    int bk(int gr) {
        return this.map_group2bk[gr] & 0xFFFFEFFF;
    }

    int maxsb(int gr) {
        return gr < this.num_hybrid_groups ? this.group_border[gr] + 1 : this.group_border[gr + 1];
    }

    boolean bkm(int gr) {
        return (this.map_group2bk[gr] & 0xFFFFEFFF) != 0;
    }

    public String toString() {
        return this.name;
    }
}

