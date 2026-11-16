package org.espetro.client.splash;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplashWavAudioTest {
    @Test
    void cannotTouchOpenAlBeforeMinecraftSoundEngineIsReady() {
        assertFalse(SplashWavAudio.isAlReady());
        SplashWavAudio audio = new SplashWavAudio();
        assertFalse(audio.play(0.8f));
        assertFalse(audio.play(0.8f, 2f));
        assertFalse(audio.isPlaying());
        audio.stop();
    }
}
