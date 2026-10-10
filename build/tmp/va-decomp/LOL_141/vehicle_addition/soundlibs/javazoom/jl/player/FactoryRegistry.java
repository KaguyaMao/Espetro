/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDeviceFactory;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.JavaSoundAudioDeviceFactory;
import java.util.Enumeration;
import java.util.Hashtable;

public class FactoryRegistry
extends AudioDeviceFactory {
    private static FactoryRegistry instance = null;
    protected Hashtable factories = new Hashtable();

    public static synchronized FactoryRegistry systemRegistry() {
        if (instance == null) {
            instance = new FactoryRegistry();
            instance.registerDefaultFactories();
        }
        return instance;
    }

    public void addFactory(AudioDeviceFactory factory) {
        this.factories.put(factory.getClass(), factory);
    }

    public void removeFactoryType(Class cls) {
        this.factories.remove(cls);
    }

    public void removeFactory(AudioDeviceFactory factory) {
        this.factories.remove(factory.getClass());
    }

    public AudioDevice createAudioDevice() throws JavaLayerException {
        AudioDevice device = null;
        AudioDeviceFactory[] factories = this.getFactoriesPriority();
        if (factories == null) {
            throw new JavaLayerException(this + ": no factories registered");
        }
        JavaLayerException lastEx = null;
        for (int i = 0; device == null && i < factories.length; ++i) {
            try {
                device = factories[i].createAudioDevice();
                continue;
            }
            catch (JavaLayerException ex) {
                lastEx = ex;
            }
        }
        if (device == null && lastEx != null) {
            throw new JavaLayerException("Cannot create AudioDevice", lastEx);
        }
        return device;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected AudioDeviceFactory[] getFactoriesPriority() {
        AudioDeviceFactory[] fa = null;
        Hashtable hashtable = this.factories;
        synchronized (hashtable) {
            int size = this.factories.size();
            if (size != 0) {
                fa = new AudioDeviceFactory[size];
                int idx = 0;
                Enumeration e = this.factories.elements();
                while (e.hasMoreElements()) {
                    AudioDeviceFactory factory = (AudioDeviceFactory)e.nextElement();
                    fa[idx++] = factory;
                }
            }
        }
        return fa;
    }

    protected void registerDefaultFactories() {
        this.addFactory(new JavaSoundAudioDeviceFactory());
    }
}

