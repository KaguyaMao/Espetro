/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.FactoryRegistry;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.Player;
import java.applet.Applet;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;

public class PlayerApplet
extends Applet
implements Runnable {
    public static final String AUDIO_PARAMETER = "audioURL";
    private Player player = null;
    private Thread playerThread = null;
    private String fileName = null;

    protected AudioDevice getAudioDevice() throws JavaLayerException {
        return FactoryRegistry.systemRegistry().createAudioDevice();
    }

    protected InputStream getAudioStream() {
        InputStream in = null;
        try {
            URL url = this.getAudioURL();
            if (url != null) {
                in = url.openStream();
            }
        }
        catch (IOException ex) {
            System.err.println(ex);
        }
        return in;
    }

    protected String getAudioFileName() {
        String urlString = this.fileName;
        if (urlString == null) {
            urlString = this.getParameter(AUDIO_PARAMETER);
        }
        return urlString;
    }

    protected URL getAudioURL() {
        String urlString = this.getAudioFileName();
        URL url = null;
        if (urlString != null) {
            try {
                url = new URL(this.getDocumentBase(), urlString);
            }
            catch (Exception ex) {
                System.err.println(ex);
            }
        }
        return url;
    }

    public void setFileName(String name) {
        this.fileName = name;
    }

    public String getFileName() {
        return this.fileName;
    }

    protected void stopPlayer() throws JavaLayerException {
        if (this.player != null) {
            this.player.close();
            this.player = null;
            this.playerThread = null;
        }
    }

    protected void play(InputStream in, AudioDevice dev) throws JavaLayerException {
        this.stopPlayer();
        if (in != null && dev != null) {
            this.player = new Player(in, dev);
            this.playerThread = this.createPlayerThread();
            this.playerThread.start();
        }
    }

    protected Thread createPlayerThread() {
        return new Thread((Runnable)this, "Audio player thread");
    }

    public void init() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void start() {
        String name = this.getAudioFileName();
        try {
            InputStream in = this.getAudioStream();
            AudioDevice dev = this.getAudioDevice();
            this.play(in, dev);
        }
        catch (JavaLayerException ex) {
            PrintStream printStream = System.err;
            synchronized (printStream) {
                System.err.println("Unable to play " + name);
                ex.printStackTrace(System.err);
            }
        }
    }

    public void stop() {
        try {
            this.stopPlayer();
        }
        catch (JavaLayerException ex) {
            System.err.println(ex);
        }
    }

    public void destroy() {
    }

    public void run() {
        if (this.player != null) {
            try {
                this.player.play();
            }
            catch (JavaLayerException ex) {
                System.err.println("Problem playing audio: " + ex);
            }
        }
    }
}

