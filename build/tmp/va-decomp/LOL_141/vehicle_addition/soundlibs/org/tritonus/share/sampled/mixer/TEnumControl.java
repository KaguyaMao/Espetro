/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TCompoundControl;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControlController;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControllable;
import javax.sound.sampled.EnumControl;

public class TEnumControl
extends EnumControl
implements TControllable {
    private TControlController m_controller;

    public TEnumControl(EnumControl.Type type, Object[] aValues, Object value) {
        super(type, aValues, value);
        if (TDebug.TraceControl) {
            TDebug.out("TEnumControl.<init>: begin");
        }
        this.m_controller = new TControlController();
        if (TDebug.TraceControl) {
            TDebug.out("TEnumControl.<init>: end");
        }
    }

    public void setParentControl(TCompoundControl compoundControl) {
        this.m_controller.setParentControl(compoundControl);
    }

    public TCompoundControl getParentControl() {
        return this.m_controller.getParentControl();
    }

    public void commit() {
        this.m_controller.commit();
    }
}

