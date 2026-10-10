/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TNotifier
extends Thread {
    public static TNotifier notifier = null;
    private List<NotifyEntry> m_entries = new ArrayList<NotifyEntry>();

    public TNotifier() {
        super("Tritonus Notifier");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addEntry(EventObject event, Collection<LineListener> listeners) {
        List<NotifyEntry> list = this.m_entries;
        synchronized (list) {
            this.m_entries.add(new NotifyEntry(event, listeners));
            this.m_entries.notifyAll();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        while (true) {
            NotifyEntry entry = null;
            List<NotifyEntry> list = this.m_entries;
            synchronized (list) {
                while (this.m_entries.size() == 0) {
                    try {
                        this.m_entries.wait();
                    }
                    catch (InterruptedException e) {
                        if (!TDebug.TraceAllExceptions) continue;
                        TDebug.out(e);
                    }
                }
                entry = this.m_entries.remove(0);
            }
            entry.deliver();
        }
    }

    static {
        notifier = new TNotifier();
        notifier.setDaemon(true);
        notifier.start();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class NotifyEntry {
        private EventObject m_event;
        private List<LineListener> m_listeners;

        public NotifyEntry(EventObject event, Collection<LineListener> listeners) {
            this.m_event = event;
            this.m_listeners = new ArrayList<LineListener>(listeners);
        }

        public void deliver() {
            for (LineListener listener : this.m_listeners) {
                listener.update((LineEvent)this.m_event);
            }
        }
    }
}

