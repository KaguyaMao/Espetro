/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TNotifier;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TMixer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TLine
implements Line {
    private static final Control[] EMPTY_CONTROL_ARRAY = new Control[0];
    private Line.Info m_info;
    private boolean m_bOpen;
    private List<Control> m_controls;
    private Set<LineListener> m_lineListeners;
    private TMixer m_mixer;

    protected TLine(TMixer mixer, Line.Info info) {
        this.setLineInfo(info);
        this.setOpen(false);
        this.m_controls = new ArrayList<Control>();
        this.m_lineListeners = new HashSet<LineListener>();
        this.m_mixer = mixer;
    }

    protected TLine(TMixer mixer, Line.Info info, Collection<Control> controls) {
        this(mixer, info);
        this.m_controls.addAll(controls);
    }

    protected TMixer getMixer() {
        return this.m_mixer;
    }

    @Override
    public Line.Info getLineInfo() {
        return this.m_info;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void setLineInfo(Line.Info info) {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.setLineInfo(): setting: " + info);
        }
        TLine tLine = this;
        synchronized (tLine) {
            this.m_info = info;
        }
    }

    @Override
    public void open() throws LineUnavailableException {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.open(): called");
        }
        if (!this.isOpen()) {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.open(): opening");
            }
            this.openImpl();
            if (this.getMixer() != null) {
                this.getMixer().registerOpenLine(this);
            }
            this.setOpen(true);
        } else if (TDebug.TraceLine) {
            TDebug.out("TLine.open(): already open");
        }
    }

    protected void openImpl() throws LineUnavailableException {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.openImpl(): called");
        }
    }

    @Override
    public void close() {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.close(): called");
        }
        if (this.isOpen()) {
            if (TDebug.TraceLine) {
                TDebug.out("TLine.close(): closing");
            }
            if (this.getMixer() != null) {
                this.getMixer().unregisterOpenLine(this);
            }
            this.closeImpl();
            this.setOpen(false);
        } else if (TDebug.TraceLine) {
            TDebug.out("TLine.close(): not open");
        }
    }

    protected void closeImpl() {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.closeImpl(): called");
        }
    }

    @Override
    public boolean isOpen() {
        return this.m_bOpen;
    }

    protected void setOpen(boolean bOpen) {
        if (TDebug.TraceLine) {
            TDebug.out("TLine.setOpen(): called, value: " + bOpen);
        }
        boolean bOldValue = this.isOpen();
        this.m_bOpen = bOpen;
        if (bOldValue != this.isOpen()) {
            if (this.isOpen()) {
                if (TDebug.TraceLine) {
                    TDebug.out("TLine.setOpen(): opened");
                }
                this.notifyLineEvent(LineEvent.Type.OPEN);
            } else {
                if (TDebug.TraceLine) {
                    TDebug.out("TLine.setOpen(): closed");
                }
                this.notifyLineEvent(LineEvent.Type.CLOSE);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addControl(Control control) {
        List<Control> list = this.m_controls;
        synchronized (list) {
            this.m_controls.add(control);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeControl(Control control) {
        List<Control> list = this.m_controls;
        synchronized (list) {
            this.m_controls.remove(control);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Control[] getControls() {
        List<Control> list = this.m_controls;
        synchronized (list) {
            return this.m_controls.toArray(EMPTY_CONTROL_ARRAY);
        }
    }

    @Override
    public Control getControl(Control.Type controlType) {
        List<Control> list = this.m_controls;
        synchronized (list) {
            for (Control control : this.m_controls) {
                if (!control.getType().equals(controlType)) continue;
                return control;
            }
            throw new IllegalArgumentException("no control of type " + controlType);
        }
    }

    @Override
    public boolean isControlSupported(Control.Type controlType) {
        try {
            return this.getControl(controlType) != null;
        }
        catch (IllegalArgumentException e) {
            if (TDebug.TraceAllExceptions) {
                TDebug.out(e);
            }
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addLineListener(LineListener listener) {
        Set<LineListener> set = this.m_lineListeners;
        synchronized (set) {
            this.m_lineListeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLineListener(LineListener listener) {
        Set<LineListener> set = this.m_lineListeners;
        synchronized (set) {
            this.m_lineListeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Set<LineListener> getLineListeners() {
        Set<LineListener> set = this.m_lineListeners;
        synchronized (set) {
            return new HashSet<LineListener>(this.m_lineListeners);
        }
    }

    protected void notifyLineEvent(LineEvent.Type type) {
        this.notifyLineEvent(new LineEvent(this, type, -1L));
    }

    protected void notifyLineEvent(LineEvent event) {
        TNotifier.notifier.addEntry(event, this.getLineListeners());
    }
}

