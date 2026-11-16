package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.aui.GuiElement;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 编制指定展示页：地图已指定某一方编制时，展示 5 秒（不投票）。
 * <p>版式与地图揭晓页一致：主标题 + 副标题“已自动选择唯一可用编制”+ 编制图 + 编制名 + 倒计时。</p>
 */
public class FactionLockRevealScreen extends EspetroMenuScreen {

    private static final int IMG_MAX_W = 320;
    private static final int IMG_MAX_H = 180;

    private static final Map<String, ResolvedTexture> TEXTURE_CACHE = new HashMap<>();

    private record ResolvedTexture(ResourceLocation location, int width, int height) {
    }

    private final String team;
    private final String factionName;
    private final ResourceLocation texture;
    private final int texW;
    private final int texH;
    private int ticksRemaining;
    private VotePhase phaseHeader;

    public FactionLockRevealScreen(String team, String factionName,
                                   String factionSelectionImage, int durationSeconds) {
        super(Component.literal("编制指定"));
        this.team = normalizeTeam(team);
        this.factionName = factionName == null || factionName.isBlank() ? "未知编制" : factionName;
        this.ticksRemaining = Math.max(1, durationSeconds) * 20;

        ResolvedTexture resolved = resolveTexture(factionSelectionImage);
        this.texture = resolved != null ? resolved.location()
            : ResourceLocation.fromNamespaceAndPath("espetro", "textures/gui/attack_faction.png");
        this.texW = resolved != null ? resolved.width() : 128;
        this.texH = resolved != null ? resolved.height() : 128;
    }

    private static String normalizeTeam(String team) {
        return "DEFEND".equalsIgnoreCase(team) ? "DEFEND" : "ATTACK";
    }

    private static ResolvedTexture resolveTexture(String configuredPath) {
        if (configuredPath == null || configuredPath.isBlank()) {
            return null;
        }
        String key = configuredPath.trim();
        ResolvedTexture cached = TEXTURE_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        ResourceLocation resourceLocation = ResourceLocation.tryParse(key);
        ResolvedTexture resource = resolveResourceTexture(resourceLocation);
        if (resource != null) {
            TEXTURE_CACHE.put(key, resource);
            return resource;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return null;
        }
        Path imagePath = FactionSelectionImageResolver.resolveClientFile(
            mc.gameDirectory.toPath(), key);
        if (imagePath == null) {
            return null;
        }
        try (InputStream in = Files.newInputStream(imagePath)) {
            NativeImage image = NativeImage.read(in);
            int width = image.getWidth();
            int height = image.getHeight();
            DynamicTexture texture = new DynamicTexture(image);
            ResourceLocation dynamicLocation = ResourceLocation.fromNamespaceAndPath(
                "espetro", "dynamic/faction_lock_" + Integer.toHexString(key.hashCode()));
            mc.getTextureManager().register(dynamicLocation, texture);
            ResolvedTexture resolved = new ResolvedTexture(dynamicLocation, width, height);
            TEXTURE_CACHE.put(key, resolved);
            return resolved;
        } catch (Exception ignored) {
            return null;
        }
    }

    private static ResolvedTexture resolveResourceTexture(ResourceLocation location) {
        if (location == null) {
            return null;
        }
        String cacheKey = "resource:" + location;
        ResolvedTexture cached = TEXTURE_CACHE.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        try {
            var resource = Minecraft.getInstance().getResourceManager().getResource(location);
            if (resource.isEmpty()) {
                return null;
            }
            try (InputStream in = resource.get().open(); NativeImage image = NativeImage.read(in)) {
                ResolvedTexture resolved = new ResolvedTexture(
                    location, image.getWidth(), image.getHeight());
                TEXTURE_CACHE.put(cacheKey, resolved);
                return resolved;
            }
        } catch (Exception ignored) {
            return null;
        }
    }

    private String teamLabel() {
        return "DEFEND".equals(team) ? "守方" : "攻方";
    }

    private String detailText() {
        return getSecondsRemaining() + " 秒后继续投票流程";
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        phaseHeader = VotePhase.add(root, width, height, teamLabel() + "编制已指定",
            "已自动选择唯一可用编制", this::detailText,
            () -> "ATTACK".equals(team) ? VotePhase.Stage.ATTACK : VotePhase.Stage.DEFEND);
        VotePhase.Grid layout = VotePhase.grid(width, height, 1, 1, 26);
        int cardW = layout.cardWidth();
        int cardH = layout.cardHeight();
        int panelX = layout.x(0);
        int panelY = layout.y(0);
        int imageH = Math.max(1, cardH - 26);

        int accent = "DEFEND".equals(team) ? UiTheme.DEFEND : UiTheme.ATTACK;
        root.addChild(EspetroAuiWidgets.cardAccent(panelX, panelY, cardW, cardH, accent));

        AspectFit.Size fitted = AspectFit.within(texW, texH, Math.max(1, cardW - 8), imageH);
        int imgX = panelX + (cardW - fitted.width()) / 2;
        int imgY = panelY + (imageH - fitted.height()) / 2 + 2;
        root.addChild(new FactionImageElement(imgX, imgY, fitted.width(), fitted.height(),
            texW, texH, texture));

        EspetroAuiWidgets.Text label = EspetroAuiWidgets.centeredText(panelX, panelY + imageH + 6,
            cardW, factionName, accent);
        label.setTextScale(UiTheme.TYPE_HEADING).setShadow(true);
        root.addChild(label);
    }

    /** 完整绘制纹理，按比例适配，不裁剪不拉伸。 */
    private static final class FactionImageElement extends GuiElement {
        private final ResourceLocation texture;
        private final int texW;
        private final int texH;
        private final int dispW;
        private final int dispH;

        FactionImageElement(int x, int y, int dispW, int dispH,
                            int texW, int texH, ResourceLocation texture) {
            super(x, y, dispW, dispH);
            this.dispW = dispW;
            this.dispH = dispH;
            this.texW = texW;
            this.texH = texH;
            this.texture = texture;
        }

        @Override
        public void draw(GuiGraphics graphics, int refX, int refY, int screenWidth,
                         int screenHeight, int mouseX, int mouseY, float opacity) {
            if (!isVisible() || texture == null) {
                return;
            }
            int bx = refX + getX();
            int by = refY + getY();
            graphics.blit(texture, bx, by, dispW, dispH, 0, 0, texW, texH, texW, texH);

        }
    }

    private int getSecondsRemaining() {
        return Math.max(0, (ticksRemaining + 19) / 20);
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(
            graphics, this.width, this.height, ClientGameState.getCurrentMapFolder());
    }

    @Override
    public void tick() {
        super.tick();
        ticksRemaining--;
        if (ticksRemaining % 20 == 0 && phaseHeader != null) {
            phaseHeader.setDetail(detailText());
        }
        if (ticksRemaining <= 0 && Minecraft.getInstance().screen == this && !tutorialPreviewMode) {
            org.espetro.client.aui.AuiScreen.closeWithFade(this);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
