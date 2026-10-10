/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControlController;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControllable;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;

public class TCompoundControl
extends CompoundControl
implements TControllable {
    private TControlController m_controller;

    public TCompoundControl(CompoundControl.Type type, Control[] aMemberControls) {
        super(type, aMemberControls);
        if (TDebug.TraceControl) {
            TDebug.out("TCompoundControl.<init>: begin");
        }
        this.m_controller = new TControlController();
        if (TDebug.TraceControl) {
            TDebug.out("TCompoundControl.<init>: end");
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

