/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced.AdvancedPlayer;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced.PlaybackEvent;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced.PlaybackListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class jlap {
    public static void main(String[] args) {
        jlap test = new jlap();
        if (args.length != 1) {
            test.showUsage();
            System.exit(0);
        } else {
            try {
                test.play(args[0]);
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
                System.exit(0);
            }
        }
    }

    public void play(String filename) throws JavaLayerException, IOException {
        InfoListener lst = new InfoListener();
        jlap.playMp3(new File(filename), lst);
    }

    public void showUsage() {
        System.out.println("Usage: jla <filename>");
        System.out.println("");
        System.out.println(" e.g. : java javazoom.jl.player.advanced.jlap localfile.mp3");
    }

    public static AdvancedPlayer playMp3(File mp3, PlaybackListener listener) throws IOException, JavaLayerException {
        return jlap.playMp3(mp3, 0, Integer.MAX_VALUE, listener);
    }

    public static AdvancedPlayer playMp3(File mp3, int start, int end, PlaybackListener listener) throws IOException, JavaLayerException {
        return jlap.playMp3(new BufferedInputStream(new FileInputStream(mp3)), start, end, listener);
    }

    public static AdvancedPlayer playMp3(InputStream is, final int start, final int end, PlaybackListener listener) throws JavaLayerException {
        final AdvancedPlayer player = new AdvancedPlayer(is);
        player.setPlayBackListener(listener);
        new Thread(){

            public void run() {
                try {
                    player.play(start, end);
                }
                catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
        }.start();
        return player;
    }

    public class InfoListener
    extends PlaybackListener {
        public void playbackStarted(PlaybackEvent evt) {
            System.out.println("Play started from frame " + evt.getFrame());
        }

        public void playbackFinished(PlaybackEvent evt) {
            System.out.println("Play completed at frame " + evt.getFrame());
            System.exit(0);
        }
    }
}

