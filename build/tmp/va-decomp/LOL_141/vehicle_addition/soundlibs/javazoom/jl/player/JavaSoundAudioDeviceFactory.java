/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDeviceFactory;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.JavaSoundAudioDevice;

public class JavaSoundAudioDeviceFactory
extends AudioDeviceFactory {
    private boolean tested = false;
    private static final String DEVICE_CLASS_NAME = "LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.JavaSoundAudioDevice";

    public synchronized AudioDevice createAudioDevice() throws JavaLayerException {
        if (!this.tested) {
            this.testAudioDevice();
            this.tested = true;
        }
        try {
            return this.createAudioDeviceImpl();
        }
        catch (Exception ex) {
            throw new JavaLayerException("unable to create JavaSound device: " + ex);
        }
        catch (LinkageError ex) {
            throw new JavaLayerException("unable to create JavaSound device: " + ex);
        }
    }

    protected JavaSoundAudioDevice createAudioDeviceImpl() throws JavaLayerException {
        ClassLoader loader = this.getClass().getClassLoader();
        try {
            JavaSoundAudioDevice dev = (JavaSoundAudioDevice)this.instantiate(loader, DEVICE_CLASS_NAME);
            return dev;
        }
        catch (Exception ex) {
            throw new JavaLayerException("Cannot create JavaSound device", ex);
        }
        catch (LinkageError ex) {
            throw new JavaLayerException("Cannot create JavaSound device", ex);
        }
    }

    public void testAudioDevice() throws JavaLayerException {
        JavaSoundAudioDevice dev = this.createAudioDeviceImpl();
        dev.test();
    }
}

