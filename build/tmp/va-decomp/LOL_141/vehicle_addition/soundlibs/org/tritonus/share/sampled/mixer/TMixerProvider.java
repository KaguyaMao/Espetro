/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.spi.MixerProvider;

public abstract class TMixerProvider
extends MixerProvider {
    private static final Mixer.Info[] EMPTY_MIXER_INFO_ARRAY = new Mixer.Info[0];
    private static Map<Class, MixerProviderStruct> sm_mixerProviderStructs = new HashMap<Class, MixerProviderStruct>();
    private boolean m_bDisabled = false;

    public TMixerProvider() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.<init>(): begin");
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.<init>(): end");
        }
    }

    protected void staticInit() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MixerProviderStruct getMixerProviderStruct() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerProviderStruct(): begin");
        }
        Class<?> cls = this.getClass();
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerProviderStruct(): called from " + cls);
        }
        Class<TMixerProvider> clazz = TMixerProvider.class;
        synchronized (TMixerProvider.class) {
            MixerProviderStruct struct = sm_mixerProviderStructs.get(cls);
            if (struct == null) {
                if (TDebug.TraceMixerProvider) {
                    TDebug.out("TMixerProvider.getMixerProviderStruct(): creating new MixerProviderStruct for " + cls);
                }
                struct = new MixerProviderStruct();
                sm_mixerProviderStructs.put(cls, struct);
            }
            if (TDebug.TraceMixerProvider) {
                TDebug.out("TMixerProvider.getMixerProviderStruct(): end");
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return struct;
        }
    }

    protected void disable() {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("disabling " + this.getClass().getName());
        }
        this.m_bDisabled = true;
    }

    protected boolean isDisabled() {
        return this.m_bDisabled;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addMixer(Mixer mixer) {
        MixerProviderStruct struct;
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.addMixer(): begin");
        }
        MixerProviderStruct mixerProviderStruct = struct = this.getMixerProviderStruct();
        synchronized (mixerProviderStruct) {
            struct.m_mixers.add(mixer);
            if (struct.m_defaultMixer == null) {
                struct.m_defaultMixer = mixer;
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.addMixer(): end");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeMixer(Mixer mixer) {
        MixerProviderStruct struct;
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.removeMixer(): begin");
        }
        MixerProviderStruct mixerProviderStruct = struct = this.getMixerProviderStruct();
        synchronized (mixerProviderStruct) {
            struct.m_mixers.remove(mixer);
            if (struct.m_defaultMixer == mixer) {
                struct.m_defaultMixer = null;
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.removeMixer(): end");
        }
    }

    public boolean isMixerSupported(Mixer.Info info) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.isMixerSupported(): begin");
        }
        boolean bIsSupported = false;
        Mixer.Info[] infos = this.getMixerInfo();
        for (int i = 0; i < infos.length; ++i) {
            if (!infos[i].equals(info)) continue;
            bIsSupported = true;
            break;
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.isMixerSupported(): end");
        }
        return bIsSupported;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Mixer getMixer(Mixer.Info info) {
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixer(): begin");
        }
        MixerProviderStruct struct = this.getMixerProviderStruct();
        Mixer mixerResult = null;
        MixerProviderStruct mixerProviderStruct = struct;
        synchronized (mixerProviderStruct) {
            if (info == null) {
                mixerResult = struct.m_defaultMixer;
            } else {
                for (Mixer mixer : struct.m_mixers) {
                    if (!mixer.getMixerInfo().equals(info)) continue;
                    mixerResult = mixer;
                    break;
                }
            }
        }
        if (mixerResult == null) {
            throw new IllegalArgumentException("no mixer available for " + info);
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixer(): end");
        }
        return mixerResult;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Mixer.Info[] getMixerInfo() {
        MixerProviderStruct struct;
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerInfo(): begin");
        }
        HashSet<Mixer.Info> mixerInfos = new HashSet<Mixer.Info>();
        MixerProviderStruct mixerProviderStruct = struct = this.getMixerProviderStruct();
        synchronized (mixerProviderStruct) {
            for (Mixer mixer : struct.m_mixers) {
                mixerInfos.add(mixer.getMixerInfo());
            }
        }
        if (TDebug.TraceMixerProvider) {
            TDebug.out("TMixerProvider.getMixerInfo(): end");
        }
        return mixerInfos.toArray(EMPTY_MIXER_INFO_ARRAY);
    }

    private class MixerProviderStruct {
        public List<Mixer> m_mixers = new ArrayList<Mixer>();
        public Mixer m_defaultMixer = null;
    }
}

