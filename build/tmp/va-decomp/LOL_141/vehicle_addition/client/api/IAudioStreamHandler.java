/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.client.api;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public interface IAudioStreamHandler {
    public boolean canHandle(URL var1);

    public AudioInputStream handle(URL var1) throws UnsupportedAudioFileException, IOException;

    default public AudioInputStream handle(URL url, long startOffset) throws UnsupportedAudioFileException, IOException {
        return this.handle(url);
    }

    default public int getPriority() {
        return 0;
    }
}

