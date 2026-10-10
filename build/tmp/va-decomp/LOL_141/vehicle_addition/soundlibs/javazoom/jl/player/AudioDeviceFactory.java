/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;

public abstract class AudioDeviceFactory {
    public abstract AudioDevice createAudioDevice() throws JavaLayerException;

    protected AudioDevice instantiate(ClassLoader loader, String name) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        AudioDevice dev = null;
        Class<?> cls = null;
        cls = loader == null ? Class.forName(name) : loader.loadClass(name);
        Object o = cls.newInstance();
        dev = (AudioDevice)o;
        return dev;
    }
}

