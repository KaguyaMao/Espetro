package org.espetro.client.splash;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

/**
 * 主菜单之前播放的一段启动视频（帧序列 + 音频）。
 *
 * <p>时序由 {@link #tick()} 驱动（不在 render 里切屏）：按真实时间推进帧，
 * 解码跟不上就丢帧；播完淡出后切回 {@link TitleScreen}。</p>
 */
public final class SplashVideoScreen extends Screen {

    private static final ResourceLocation TEXTURE_ID = new ResourceLocation("espetro", "splash_frame");
    private static final long MIN_VISIBLE_MS = 1200L;
    private static final long FADE_OUT_MS = 400L;
    private static final float VOLUME = 0.8F;

    private final Screen parent;

    private SplashVideoAssets assets;
    private DynamicTexture texture;
    private NativeImage pending;

    private long startedAt;
    private long pausedAt;
    private boolean started;
    private boolean finishing;
    private long finishingAt;
    private boolean finished;
    private int shownIndex = -1;
    private long loggedBuffered;

    private SoundInstance audio;
    private final SplashWavAudio wavAudio = new SplashWavAudio();
    private static boolean PROBED;
    private boolean audioStarted;
    private boolean audioGaveUp;
    private long audioGiveUpAt;
    private boolean uploadedOnce;

    public SplashVideoScreen(Screen parent) {
        super(Component.literal("THE NET"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        if (this.assets == null) {
            this.assets = SplashVideoAssets.open();
            this.startedAt = Util.getMillis();
            if (this.assets != null) {
                this.assets.prewarm();
            }
        }
        if (this.assets == null && !this.finishing) {
            beginFinish(Util.getMillis());
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);
        if (this.assets == null) {
            return;
        }
        long now = Util.getMillis();
        long elapsed = this.started ? Math.max(0L, now - this.startedAt) : 0L;

        int wanted = this.started
            ? (int) Math.min(this.assets.frameCount() - 1L, Math.max(0L, elapsed * this.assets.fps() / 1000L))
            : 0;

        if (this.pending == null && wanted != this.shownIndex) {
            this.pending = this.assets.take(wanted);
            if (this.pending == null && this.started && now - this.loggedBuffered > 1000L) {
                this.loggedBuffered = now;
                org.espetro.Espetro.LOGGER.debug("[启动视频] 第 {} 帧尚未解码（缓存 {}，最近错误 {}）",
                    wanted, this.assets.buffered(), this.assets.lastError());
            }
        }

        if (this.pending != null) {
            try {
                // 注意：DynamicTexture(NativeImage) 会接管传入图片的所有权，
                // 若之后 close 掉 pending，纹理会变成 "Image is not allocated"。
                // 因此这里始终自建纹理，只把 pending 当作数据源拷贝。
                if (this.texture == null) {
                    this.texture = new DynamicTexture(this.pending.getWidth(), this.pending.getHeight(), false);
                    Minecraft.getInstance().getTextureManager().register(TEXTURE_ID, this.texture);
                }
                this.texture.getPixels().copyFrom(this.pending);
                this.texture.upload();
                this.shownIndex = wanted;
                if (!this.uploadedOnce) {
                    this.uploadedOnce = true;
                    org.espetro.Espetro.LOGGER.info("[启动视频] 首帧已上传: {}x{}", this.pending.getWidth(), this.pending.getHeight());
                }
            } catch (Throwable error) {
                org.espetro.Espetro.LOGGER.warn("[启动视频] 帧上传失败: {}", error.toString());
            } finally {
                this.pending.close();
                this.pending = null;
            }
        }

        int videoWidth = 1280;
        int videoHeight = 720;
        float scale = Math.min((float) this.width / videoWidth, (float) this.height / videoHeight);
        int drawWidth = Math.max(1, Math.round(videoWidth * scale));
        int drawHeight = Math.max(1, Math.round(videoHeight * scale));
        int x = (this.width - drawWidth) / 2;
        int y = (this.height - drawHeight) / 2;

        if (this.texture != null) {
            guiGraphics.blit(TEXTURE_ID, x, y, drawWidth, drawHeight,
                0.0F, 0.0F, videoWidth, videoHeight, videoWidth, videoHeight);
        }

        if (this.finishing) {
            long fadeElapsed = now - this.finishingAt;
            int alpha = (int) Math.min(255L, Math.max(0L, fadeElapsed) * 255L / FADE_OUT_MS);
            if (alpha > 0) {
                guiGraphics.fill(0, 0, this.width, this.height, alpha << 24);
            }
        }
    }

    @Override
    public void tick() {
        if (this.finished) {
            return;
        }
        long now = Util.getMillis();
        if (!this.started) {
            this.started = true;
            this.startedAt = now;
        }
        if (!this.audioStarted) {
            startAudio();
        }

        boolean active = Minecraft.getInstance().isWindowActive();
        if (!active) {
            if (this.pausedAt == 0L) {
                this.pausedAt = now;
            }
        } else if (this.pausedAt != 0L) {
            this.startedAt += now - this.pausedAt;
            this.pausedAt = 0L;
        }
        if (!active) {
            return;
        }

        long elapsed = now - this.startedAt;
        if (!this.finishing) {
            if (this.assets == null) {
                beginFinish(now);
            } else if (elapsed >= this.assets.durationMs() && elapsed >= MIN_VISIBLE_MS) {
                beginFinish(now);
            }
        } else if (now - this.finishingAt >= FADE_OUT_MS) {
            finish();
        }
    }

    private void beginFinish(long now) {
        if (this.finishing) {
            return;
        }
        this.finishing = true;
        this.finishingAt = now;
        stopAudio();
    }

    private void stopAudio() {
        if (this.audio != null) {
            try {
                Minecraft.getInstance().getSoundManager().stop(this.audio);
            } catch (Throwable ignored) {
            }
            this.audio = null;
        }
        this.wavAudio.stop();
    }

    /** 诊断：确认资源包能否看到 sounds.json 与 ogg，以及 SoundManager 是否收录了该事件。 */
    private static void probeSoundResources() {
        if (PROBED) {
            return;
        }
        PROBED = true;
        Minecraft minecraft = Minecraft.getInstance();
        try {
            boolean json = minecraft.getResourceManager()
                .getResource(new ResourceLocation("espetro", "sounds.json")).isPresent();
            boolean ogg = minecraft.getResourceManager()
                .getResource(new ResourceLocation("espetro", "sounds/splash/audio.ogg")).isPresent();
            boolean evt = minecraft.getSoundManager()
                .getSoundEvent(new ResourceLocation("espetro", "splash_audio")) != null;
            java.util.Map<ResourceLocation, ?> listed = minecraft.getResourceManager()
                .listResources("sounds.json", id -> true);
            java.util.List<String> mine = new java.util.ArrayList<>();
            for (ResourceLocation id : listed.keySet()) {
                if (id.getNamespace().equals("espetro")) {
                    mine.add(id.toString());
                }
            }
            org.espetro.Espetro.LOGGER.info(
                "[启动视频] 资源探针: sounds.json={}, splash/audio.ogg={}, soundEvent={}, 共列举={}, espetro项={}, "
                    + "A(字符串)={}, B(全名)={}, C(stream)={}, 注册表={}",
                json, ogg, evt, listed.size(), mine,
                probeEvent("splash_a"), probeEvent("splash_b"), probeEvent("splash_c"),
                net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT
                    .containsKey(new ResourceLocation("espetro", "splash_audio")));
        } catch (Throwable error) {
            org.espetro.Espetro.LOGGER.warn("[启动视频] 资源探针失败: {}", error.toString());
        }
    }

    private static boolean probeEvent(String path) {
        try {
            return Minecraft.getInstance().getSoundManager()
                .getSoundEvent(new ResourceLocation("espetro", path)) != null;
        } catch (Throwable ignored) {
            return false;
        }
    }

    private void startAudio() {
        if (this.audioStarted || this.audioGaveUp) {
            return;
        }
        long now = Util.getMillis();
        // 让资源重载（含 FancyMenu 的音频处理器重载）先安顿下来
        if (now - this.startedAt < 250L) {
            return;
        }
        SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        try {
            probeSoundResources();
            SoundEvent event = org.espetro.Espetro.SPLASH_AUDIO.get();
            boolean registered = soundManager.getSoundEvent(event.getLocation()) != null;
            if (!registered) {
                // sounds.json 条目没进注册表（该客户端上会稳定复现），直接走 OpenAL 兜底
                if (this.wavAudio.play(VOLUME)) {
                    this.audioStarted = true;
                    org.espetro.Espetro.LOGGER.info(
                        "[启动视频] 注册表缺失，改走直连 OpenAL 播放 WAV（成功）");
                    return;
                }
            }
            this.audio = new SimpleSoundInstance(event.getLocation(), SoundSource.MASTER, VOLUME, 1.0F,
                SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE,
                0.0D, 0.0D, 0.0D, true);
            soundManager.play(this.audio);
            this.audioStarted = true;
            org.espetro.Espetro.LOGGER.info("[启动视频] 音频开始播放: {} (注册事件={}, 活跃={})",
                event.getLocation(), registered, soundManager.isActive(this.audio));
        } catch (Throwable error) {
            if (this.audioGiveUpAt == 0L) {
                this.audioGiveUpAt = now + 3000L;
            } else if (now > this.audioGiveUpAt) {
                this.audioGaveUp = true;
            }
            org.espetro.Espetro.LOGGER.warn("[启动视频] 音频播放失败: {}", error.toString());
            this.audio = null;
        }
    }

    private void finish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        stopAudio();
        SplashVideoController.markPlayed();
        Minecraft minecraft = Minecraft.getInstance();
        Screen target = this.parent;
        if (target != null) {
            try {
                minecraft.setScreen(target);
                return;
            } catch (Throwable error) {
                org.espetro.Espetro.LOGGER.warn("[启动视频] 回到原屏幕失败（{}），改用主菜单", error.toString());
            }
        }
        try {
            minecraft.setScreen(new TitleScreen());
        } catch (Throwable error) {
            org.espetro.Espetro.LOGGER.error("[启动视频] 无法切回主菜单: {}", error.toString());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 || keyCode == 32) {
            beginFinish(Util.getMillis());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        beginFinish(Util.getMillis());
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        stopAudio();
        if (this.texture != null) {
            try {
                Minecraft.getInstance().getTextureManager().release(TEXTURE_ID);
            } catch (Throwable ignored) {
            }
            this.texture = null;
        }
        if (this.assets != null) {
            this.assets.close();
            this.assets = null;
        }
        if (this.pending != null) {
            this.pending.close();
            this.pending = null;
        }
        super.removed();
    }
}
