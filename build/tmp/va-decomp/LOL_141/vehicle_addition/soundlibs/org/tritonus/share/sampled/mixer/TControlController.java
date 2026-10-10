/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TCompoundControl;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControllable;

public class TControlController
implements TControllable {
    private TCompoundControl m_parentControl;

    public void setParentControl(TCompoundControl compoundControl) {
        this.m_parentControl = compoundControl;
    }

    public TCompoundControl getParentControl() {
        return this.m_parentControl;
    }

    public void commit() {
        if (TDebug.TraceControl) {
            TDebug.out("TControlController.commit(): called [" + this.getClass().getName() + "]");
        }
        if (this.getParentControl() != null) {
            this.getParentControl().commit();
        }
    }
}

