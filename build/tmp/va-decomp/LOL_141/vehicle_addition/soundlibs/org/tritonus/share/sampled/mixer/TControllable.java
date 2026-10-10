/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TCompoundControl;

public interface TControllable {
    public void setParentControl(TCompoundControl var1);

    public TCompoundControl getParentControl();

    public void commit();
}

