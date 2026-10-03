/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.example.espoints.client;

import com.example.espoints.util.ModLogger;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="espoints", value={Dist.CLIENT})
public class AudioManager {
    private static AudioManager INSTANCE;
    private static final String AUDIO_FOLDER_NAME = "fightBGM";
    private static final String[] SUPPORTED_FORMATS;
    private boolean isAudioPlaying = false;
    private Clip audioClip;
    private Random random = new Random();
    private boolean lastPlayRequest = false;

    private AudioManager() {
        ModLogger.info("AudioManager\u5df2\u521d\u59cb\u5316");
    }

    public static AudioManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AudioManager();
        }
        return INSTANCE;
    }

    public void handleLowReinforcementAudio(boolean playAudio) {
        ModLogger.info("\u6536\u5230\u97f3\u9891\u64ad\u653e\u8bf7\u6c42: " + playAudio + ", \u5f53\u524d\u64ad\u653e\u72b6\u6001: " + this.isAudioPlaying);
        boolean isOperationRunning = this.isOperationModeRunning();
        ModLogger.info("\u5f53\u524d\u884c\u52a8\u72b6\u6001: " + isOperationRunning);
        if (isOperationRunning) {
            this.lastPlayRequest = playAudio;
            if (playAudio) {
                if (this.isGameWorldLoaded()) {
                    this.startAudio();
                } else {
                    ModLogger.info("\u6e38\u620f\u4e16\u754c\u672a\u52a0\u8f7d\uff0c\u8df3\u8fc7\u97f3\u9891\u64ad\u653e");
                }
            } else {
                this.stopAudio();
            }
        } else {
            ModLogger.info("\u884c\u52a8\u672a\u8fd0\u884c\uff0c\u81ea\u52a8\u505c\u6b62\u97f3\u9891");
            this.lastPlayRequest = false;
            this.stopAudio();
        }
    }

    private boolean isOperationModeRunning() {
        try {
            Class<?> capturePointManagerClass = Class.forName("com.example.espoints.capturepoint.CapturePointManager");
            Method getInstanceMethod = capturePointManagerClass.getMethod("getInstance", new Class[0]);
            Object instance = getInstanceMethod.invoke(null, new Object[0]);
            Field operationModeRunningField = capturePointManagerClass.getDeclaredField("operationModeRunning");
            operationModeRunningField.setAccessible(true);
            return operationModeRunningField.getBoolean(instance);
        }
        catch (Exception e) {
            ModLogger.error("\u83b7\u53d6\u884c\u52a8\u72b6\u6001\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    private boolean isGameWorldLoaded() {
        Minecraft minecraft = Minecraft.m_91087_();
        return minecraft.f_91073_ != null && minecraft.f_91074_ != null;
    }

    private void startAudio() {
        if (this.isAudioPlaying) {
            ModLogger.info("\u97f3\u9891\u5df2\u7ecf\u5728\u64ad\u653e\uff0c\u8df3\u8fc7\u542f\u52a8");
            return;
        }
        try {
            if (!this.isGameWorldLoaded()) {
                ModLogger.info("\u6e38\u620f\u4e16\u754c\u672a\u52a0\u8f7d\uff0c\u65e0\u6cd5\u64ad\u653e\u97f3\u9891");
                return;
            }
            File audioFolder = this.getAudioFolder();
            ModLogger.info("\u97f3\u9891\u6587\u4ef6\u5939\u8def\u5f84: " + audioFolder.getAbsolutePath());
            ModLogger.info("\u97f3\u9891\u6587\u4ef6\u5939\u662f\u5426\u5b58\u5728: " + audioFolder.exists());
            List<File> audioFiles = this.getAudioFiles(audioFolder);
            ModLogger.info("\u627e\u5230\u97f3\u9891\u6587\u4ef6\u6570\u91cf: " + audioFiles.size());
            if (audioFiles.isEmpty()) {
                ModLogger.error("\u97f3\u9891\u6587\u4ef6\u5939\u4e2d\u6ca1\u6709\u627e\u5230\u652f\u6301\u7684\u97f3\u9891\u6587\u4ef6\uff01");
                ModLogger.error("\u652f\u6301\u7684\u683c\u5f0f: " + String.join((CharSequence)", ", SUPPORTED_FORMATS));
                ModLogger.error("\u97f3\u9891\u6587\u4ef6\u5939\u8def\u5f84: " + audioFolder.getAbsolutePath());
                return;
            }
            File selectedAudio = audioFiles.get(this.random.nextInt(audioFiles.size()));
            ModLogger.info("\u968f\u673a\u9009\u62e9\u7684\u97f3\u9891\u6587\u4ef6: " + selectedAudio.getName());
            ModLogger.info("\u97f3\u9891\u6587\u4ef6\u8def\u5f84: " + selectedAudio.getAbsolutePath());
            ModLogger.info("\u97f3\u9891\u6587\u4ef6\u5927\u5c0f: " + selectedAudio.length() + " \u5b57\u8282");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(selectedAudio);
            ModLogger.info("\u6210\u529f\u521b\u5efa\u97f3\u9891\u8f93\u5165\u6d41");
            this.audioClip = AudioSystem.getClip();
            this.audioClip.open(audioStream);
            ModLogger.info("\u6210\u529f\u6253\u5f00\u97f3\u9891\u526a\u8f91");
            this.audioClip.start();
            this.audioClip.loop(-1);
            this.isAudioPlaying = true;
            ModLogger.info("\u80cc\u6c34\u4e00\u6218\u97f3\u9891\u5f00\u59cb\u64ad\u653e");
            ModLogger.info("\u97f3\u9891\u683c\u5f0f: " + String.valueOf(audioStream.getFormat()));
        }
        catch (Exception e) {
            ModLogger.error("\u97f3\u9891\u64ad\u653e\u5f02\u5e38: " + e.getMessage());
            ModLogger.error("\u5b8c\u6574\u9519\u8bef\u4fe1\u606f:");
            e.printStackTrace();
        }
    }

    private void stopAudio() {
        ModLogger.info("\u5c1d\u8bd5\u505c\u6b62\u97f3\u9891\u64ad\u653e\uff0c\u5f53\u524d\u72b6\u6001: " + this.isAudioPlaying);
        if (!this.isAudioPlaying || this.audioClip == null) {
            ModLogger.info("\u97f3\u9891\u5df2\u7ecf\u505c\u6b62\u6216\u672a\u521d\u59cb\u5316\uff0c\u8df3\u8fc7\u505c\u6b62\u64cd\u4f5c");
            return;
        }
        try {
            this.audioClip.stop();
            this.audioClip.close();
            this.isAudioPlaying = false;
            this.audioClip = null;
            ModLogger.info("\u80cc\u6c34\u4e00\u6218\u97f3\u9891\u5df2\u505c\u6b62");
        }
        catch (Exception e) {
            ModLogger.error("\u505c\u6b62\u97f3\u9891\u64ad\u653e\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        AudioManager manager = AudioManager.getInstance();
        Minecraft minecraft = Minecraft.m_91087_();
        boolean isOperationRunning = manager.isOperationModeRunning();
        if (manager.isAudioPlaying && (minecraft.f_91073_ == null || minecraft.f_91074_ == null)) {
            ModLogger.info("\u68c0\u6d4b\u5230\u6e38\u620f\u4e16\u754c\u672a\u52a0\u8f7d\uff0c\u81ea\u52a8\u505c\u6b62\u97f3\u9891");
            manager.stopAudio();
        } else if (manager.isAudioPlaying && !isOperationRunning) {
            ModLogger.info("\u68c0\u6d4b\u5230\u884c\u52a8\u5df2\u7ed3\u675f\uff0c\u81ea\u52a8\u505c\u6b62\u97f3\u9891");
            manager.stopAudio();
            manager.lastPlayRequest = false;
        } else if (!manager.isAudioPlaying && manager.lastPlayRequest && minecraft.f_91073_ != null && minecraft.f_91074_ != null && isOperationRunning) {
            ModLogger.info("\u68c0\u6d4b\u5230\u6e38\u620f\u4e16\u754c\u5df2\u52a0\u8f7d\u4e14\u6709\u64ad\u653e\u8bf7\u6c42\uff0c\u5c1d\u8bd5\u64ad\u653e\u97f3\u9891");
            manager.startAudio();
        }
    }

    private File getAudioFolder() {
        File minecraftDir = Minecraft.m_91087_().f_91069_;
        Path audioFolderPath = Paths.get(minecraftDir.getAbsolutePath(), AUDIO_FOLDER_NAME);
        File audioFolder = audioFolderPath.toFile();
        if (!audioFolder.exists()) {
            boolean created = audioFolder.mkdirs();
            if (created) {
                ModLogger.info("\u97f3\u9891\u6587\u4ef6\u5939\u5df2\u521b\u5efa: " + String.valueOf(audioFolderPath));
            } else {
                ModLogger.error("\u65e0\u6cd5\u521b\u5efa\u97f3\u9891\u6587\u4ef6\u5939: " + String.valueOf(audioFolderPath));
            }
        }
        return audioFolder;
    }

    private List<File> getAudioFiles(File audioFolder) {
        ArrayList<File> audioFiles = new ArrayList<File>();
        if (!audioFolder.exists() || !audioFolder.isDirectory()) {
            return audioFiles;
        }
        File[] files = audioFolder.listFiles();
        if (files == null) {
            ModLogger.error("\u65e0\u6cd5\u8bfb\u53d6\u97f3\u9891\u6587\u4ef6\u5939\u5185\u5bb9\uff01");
            return audioFiles;
        }
        block0: for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().toLowerCase();
            for (String format : SUPPORTED_FORMATS) {
                if (!fileName.endsWith("." + format)) continue;
                audioFiles.add(file);
                ModLogger.info("\u627e\u5230\u652f\u6301\u7684\u97f3\u9891\u6587\u4ef6: " + file.getName());
                continue block0;
            }
        }
        return audioFiles;
    }

    public static Path getAudioFilePath() {
        File minecraftDir = Minecraft.m_91087_().f_91069_;
        Path audioFolderPath = Paths.get(minecraftDir.getAbsolutePath(), AUDIO_FOLDER_NAME);
        File audioFolder = audioFolderPath.toFile();
        if (!audioFolder.exists()) {
            boolean created = audioFolder.mkdirs();
            if (created) {
                ModLogger.info("\u97f3\u9891\u6587\u4ef6\u5939\u5df2\u521b\u5efa: " + String.valueOf(audioFolderPath));
            } else {
                ModLogger.error("\u65e0\u6cd5\u521b\u5efa\u97f3\u9891\u6587\u4ef6\u5939: " + String.valueOf(audioFolderPath));
            }
        }
        return audioFolderPath.resolve("lastStandBGM.wav");
    }

    public static boolean isAudioFileExists() {
        File audioFolder = new File(Minecraft.m_91087_().f_91069_, AUDIO_FOLDER_NAME);
        List<File> audioFiles = AudioManager.getAudioFilesStatic(audioFolder);
        return !audioFiles.isEmpty();
    }

    private static List<File> getAudioFilesStatic(File audioFolder) {
        ArrayList<File> audioFiles = new ArrayList<File>();
        if (!audioFolder.exists() || !audioFolder.isDirectory()) {
            return audioFiles;
        }
        File[] files = audioFolder.listFiles();
        if (files == null) {
            return audioFiles;
        }
        block0: for (File file : files) {
            if (!file.isFile()) continue;
            String fileName = file.getName().toLowerCase();
            for (String format : SUPPORTED_FORMATS) {
                if (!fileName.endsWith("." + format)) continue;
                audioFiles.add(file);
                continue block0;
            }
        }
        return audioFiles;
    }

    public void cleanup() {
        ModLogger.info("\u6e05\u7406AudioManager\u8d44\u6e90");
        if (this.isAudioPlaying) {
            this.stopAudio();
        }
    }

    static {
        SUPPORTED_FORMATS = new String[]{"wav", "mp3", "ogg", "flac"};
    }
}

