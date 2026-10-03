/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TCompoundControl;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControlController;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TControllable;
import javax.sound.sampled.BooleanControl;

public class TBooleanControl
extends BooleanControl
implements TControllable {
    private TControlController m_controller;

    public TBooleanControl(BooleanControl.Type type, boolean bInitialValue) {
        this(type, bInitialValue, null);
    }

    public TBooleanControl(BooleanControl.Type type, boolean bInitialValue, TCompoundControl parentControl) {
        super(type, bInitialValue);
        if (TDebug.TraceControl) {
            TDebug.out("TBooleanControl.<init>: begin");
        }
        this.m_controller = new TControlController();
        if (TDebug.TraceControl) {
            TDebug.out("TBooleanControl.<init>: end");
        }
    }

    public TBooleanControl(BooleanControl.Type type, boolean bInitialValue, String strTrueStateLabel, String strFalseStateLabel) {
        this(type, bInitialValue, strTrueStateLabel, strFalseStateLabel, null);
    }

    public TBooleanControl(BooleanControl.Type type, boolean bInitialValue, String strTrueStateLabel, String strFalseStateLabel, TCompoundControl parentControl) {
        super(type, bInitialValue, strTrueStateLabel, strFalseStateLabel);
        if (TDebug.TraceControl) {
            TDebug.out("TBooleanControl.<init>: begin");
        }
        this.m_controller = new TControlController();
        if (TDebug.TraceControl) {
            TDebug.out("TBooleanControl.<init>: end");
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

