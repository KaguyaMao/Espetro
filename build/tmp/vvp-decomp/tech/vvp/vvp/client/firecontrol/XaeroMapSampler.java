/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.BlockPos$MutableBlockPos
 *  net.minecraft.util.Mth
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.level.material.MapColor
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.system.MemoryUtil
 *  xaero.map.MapProcessor
 *  xaero.map.WorldMapSession
 *  xaero.map.file.MapSaveLoad
 *  xaero.map.region.LeveledRegion
 *  xaero.map.region.MapRegion
 *  xaero.map.region.MapTileChunk
 *  xaero.map.region.texture.LeafRegionTexture
 *  xaero.map.region.texture.RegionTexture
 */
package tech.vvp.vvp.client.firecontrol;

import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;
import tech.vvp.vvp.client.firecontrol.XaeroCompat;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.file.MapSaveLoad;
import xaero.map.region.LeveledRegion;
import xaero.map.region.MapRegion;
import xaero.map.region.MapTileChunk;
import xaero.map.region.texture.LeafRegionTexture;
import xaero.map.region.texture.RegionTexture;

public final class XaeroMapSampler {
    private static final int LEAF_SHIFT = 9;
    private static final int TEX = 64;
    private static MapProcessor cachedProcessor;
    private static ClientLevel cachedLevel;
    private static MapProcessor frameProcessor;
    private static int frameCaveLayer;
    private static int lastChunkX;
    private static int lastChunkZ;
    private static MapTileChunk lastChunk;
    private static final HashMap<Integer, int[]> glTexArrayCache;
    private static int glDownloadsThisFrame;
    private static final int MAX_GL_DOWNLOADS_PER_FRAME = 3;

    private XaeroMapSampler() {
    }

    public static boolean isXaeroAvailable() {
        if (!XaeroCompat.isWorldMapLoaded()) {
            return false;
        }
        Minecraft mc = Minecraft.m_91087_();
        WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session == null && mc.f_91074_ != null) {
            session = WorldMapSession.getForPlayer((LocalPlayer)mc.f_91074_);
        }
        return session != null && session.isUsable() && session.getMapProcessor() != null;
    }

    public static void beginFrame(ClientLevel level) {
        frameProcessor = XaeroMapSampler.resolveProcessor(level);
        frameCaveLayer = frameProcessor != null ? frameProcessor.getCurrentCaveLayer() : 0;
        glDownloadsThisFrame = 0;
        lastChunkX = Integer.MIN_VALUE;
        lastChunkZ = Integer.MIN_VALUE;
        lastChunk = null;
    }

    public static int sampleColorFast(int blockX, int blockZ) {
        if (frameProcessor == null) {
            return 0;
        }
        return XaeroMapSampler.sampleXaeroFast(blockX, blockZ);
    }

    public static int sampleColor(ClientLevel level, int blockX, int blockZ) {
        if (!XaeroCompat.isWorldMapLoaded()) {
            return XaeroMapSampler.sampleMcSurface(level, blockX, blockZ);
        }
        XaeroMapSampler.beginFrame(level);
        return XaeroMapSampler.sampleColorFast(blockX, blockZ);
    }

    public static void prepareView(ClientLevel level, double centerX, double centerZ, double viewRadiusBlocks) {
        MapProcessor processor = XaeroMapSampler.resolveProcessor(level);
        if (processor == null) {
            return;
        }
        int caveLayer = processor.getCurrentCaveLayer();
        processor.updateCaveStart();
        int leafRadius = Math.min(6, Mth.m_14167_((float)((float)(viewRadiusBlocks / 512.0))) + 2);
        int centerLeafX = Mth.m_14107_((double)centerX) >> 9;
        int centerLeafZ = Mth.m_14107_((double)centerZ) >> 9;
        MapSaveLoad saveLoad = processor.getMapSaveLoad();
        for (int dx = -leafRadius; dx <= leafRadius; ++dx) {
            for (int dz = -leafRadius; dz <= leafRadius; ++dz) {
                int leafX = centerLeafX + dx;
                int leafZ = centerLeafZ + dz;
                MapRegion region = processor.getLeafMapRegion(caveLayer, leafX, leafZ, true);
                if (region == null) continue;
                region.registerVisit();
                byte ls = region.getLoadState();
                if (saveLoad == null || !saveLoad.saveExists(region) || ls != 0 && ls != 4) continue;
                saveLoad.requestLoad(region, "vvp_fdc", false);
            }
        }
    }

    private static MapProcessor resolveProcessor(ClientLevel level) {
        ClientLevel mapWorld;
        Minecraft mc = Minecraft.m_91087_();
        WorldMapSession session = WorldMapSession.getCurrentSession();
        if (session == null && mc.f_91074_ != null) {
            session = WorldMapSession.getForPlayer((LocalPlayer)mc.f_91074_);
        }
        if (session == null || !session.isUsable()) {
            cachedProcessor = null;
            return null;
        }
        MapProcessor processor = session.getMapProcessor();
        if (processor == null) {
            cachedProcessor = null;
            return null;
        }
        if (cachedProcessor != processor || cachedLevel != level) {
            processor.checkForWorldUpdate();
            processor.updateVisitedDimension(level);
            cachedProcessor = processor;
            cachedLevel = level;
        }
        if ((mapWorld = processor.getWorld()) != null && mapWorld != level && !mapWorld.m_46472_().equals((Object)level.m_46472_())) {
            return null;
        }
        return processor;
    }

    private static int sampleXaeroFast(int blockX, int blockZ) {
        int c;
        MapTileChunk chunk;
        int chunkX = blockX >> 6;
        int chunkZ = blockZ >> 6;
        if (chunkX == lastChunkX && chunkZ == lastChunkZ) {
            chunk = lastChunk;
        } else {
            chunk = frameProcessor.getMapChunk(frameCaveLayer, chunkX, chunkZ);
            lastChunkX = chunkX;
            lastChunkZ = chunkZ;
            lastChunk = chunk;
        }
        if (chunk != null && (c = XaeroMapSampler.sampleFromChunk(chunk, blockX, blockZ)) != 0) {
            return c;
        }
        for (int lvl = 1; lvl <= 3; ++lvl) {
            int c2 = XaeroMapSampler.sampleBranch(blockX, blockZ, lvl);
            if (c2 == 0) continue;
            return c2;
        }
        return 0;
    }

    private static int sampleFromChunk(MapTileChunk chunk, int blockX, int blockZ) {
        int c;
        ByteBuffer buf;
        LeafRegionTexture tex = chunk.getLeafTexture();
        if (tex == null) {
            return 0;
        }
        if (tex.hasSourceData() && (buf = tex.getDirectColorBuffer()) != null && buf.capacity() >= 16384 && (c = XaeroMapSampler.readPixelFromBuffer(buf, XaeroMapSampler.tilePixelX(blockX), XaeroMapSampler.tilePixelZ(blockZ))) != 0) {
            return c;
        }
        int glTex = tex.getGlColorTexture();
        if (glTex != -1) {
            return XaeroMapSampler.readPixelFromGlTexture(tex, glTex, XaeroMapSampler.tilePixelX(blockX), XaeroMapSampler.tilePixelZ(blockZ));
        }
        return 0;
    }

    private static int sampleBranch(int blockX, int blockZ, int lvl) {
        int c;
        RegionTexture tex;
        LeveledRegion region;
        int regionShift = 9 + lvl;
        int regionX = blockX >> regionShift;
        int regionZ = blockZ >> regionShift;
        try {
            region = frameProcessor.getLeveledRegion(frameCaveLayer, regionX, regionZ, lvl);
        }
        catch (Exception ignored) {
            return 0;
        }
        if (region == null) {
            return 0;
        }
        int pixelScale = 1 << lvl;
        int texSideBlocks = 64 * pixelScale;
        int blockInRegionX = blockX - (regionX << regionShift);
        int blockInRegionZ = blockZ - (regionZ << regionShift);
        int texX = blockInRegionX / texSideBlocks;
        int texZ = blockInRegionZ / texSideBlocks;
        if (texX < 0 || texX >= 8 || texZ < 0 || texZ >= 8) {
            return 0;
        }
        try {
            tex = region.getTexture(texX, texZ);
        }
        catch (NullPointerException ignored) {
            return 0;
        }
        if (tex == null) {
            return 0;
        }
        int pixelX = (blockInRegionX - texX * texSideBlocks) / pixelScale;
        int pixelZ = (blockInRegionZ - texZ * texSideBlocks) / pixelScale;
        ByteBuffer buf = tex.getDirectColorBuffer();
        if (buf != null && buf.capacity() >= 16384 && (c = XaeroMapSampler.readPixelFromBuffer(buf, pixelX, pixelZ)) != 0) {
            return c;
        }
        int glTex = tex.getGlColorTexture();
        if (glTex != -1) {
            return XaeroMapSampler.readPixelFromGlTexture(tex, glTex, pixelX, pixelZ);
        }
        return 0;
    }

    private static int readPixelFromBuffer(ByteBuffer buf, int px, int pz) {
        if (px < 0 || px >= 64 || pz < 0 || pz >= 64) {
            return 0;
        }
        int idx = (pz * 64 + px) * 4;
        if (idx + 3 >= buf.capacity()) {
            return 0;
        }
        int packed = buf.getInt(idx);
        int blue = packed >> 24 & 0xFF;
        int green = packed >> 16 & 0xFF;
        int red = packed >> 8 & 0xFF;
        int alpha = packed & 0xFF;
        if (alpha == 0 && red == 0 && green == 0 && blue == 0) {
            return 0;
        }
        if (alpha == 0) {
            alpha = 255;
        }
        return XaeroMapSampler.argb(red, green, blue, alpha);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int readPixelFromGlTexture(RegionTexture<?> tex, int glTexId, int px, int pz) {
        int[] result;
        int[] cached = glTexArrayCache.get(glTexId);
        if (cached != null) {
            int idx = pz * 64 + px;
            return idx >= 0 && idx < cached.length ? cached[idx] : 0;
        }
        if (glDownloadsThisFrame >= 3) {
            return 0;
        }
        try {
            int prevTex = GL11.glGetInteger((int)32873);
            GlStateManager._bindTexture((int)glTexId);
            IntBuffer pixelBuf = MemoryUtil.memAllocInt((int)4096);
            try {
                GL11.glGetTexImage((int)3553, (int)0, (int)6408, (int)5121, (IntBuffer)pixelBuf);
                int[] pixels = new int[4096];
                for (int i = 0; i < 4096; ++i) {
                    int rgba = pixelBuf.get(i);
                    int r = rgba & 0xFF;
                    int g = rgba >> 8 & 0xFF;
                    int b = rgba >> 16 & 0xFF;
                    int a = rgba >> 24 & 0xFF;
                    if (a == 0 && r == 0 && g == 0 && b == 0) continue;
                    if (a == 0) {
                        a = 255;
                    }
                    pixels[i] = XaeroMapSampler.argb(r, g, b, a);
                }
                glTexArrayCache.put(glTexId, pixels);
            }
            finally {
                MemoryUtil.memFree((Buffer)pixelBuf);
            }
            GlStateManager._bindTexture((int)prevTex);
            ++glDownloadsThisFrame;
        }
        catch (Exception ignored) {
            return 0;
        }
        if (glTexArrayCache.size() > 512) {
            glTexArrayCache.clear();
        }
        if ((result = glTexArrayCache.get(glTexId)) != null) {
            int idx = pz * 64 + px;
            return idx >= 0 && idx < result.length ? result[idx] : 0;
        }
        return 0;
    }

    public static void clearGlCache() {
        glTexArrayCache.clear();
    }

    private static int tilePixelX(int blockX) {
        return (blockX >> 4 & 3) * 16 + (blockX & 0xF);
    }

    private static int tilePixelZ(int blockZ) {
        return (blockZ >> 4 & 3) * 16 + (blockZ & 0xF);
    }

    private static int sampleMcSurface(ClientLevel level, int blockX, int blockZ) {
        if (!level.m_7232_(blockX >> 4, blockZ >> 4)) {
            return 0;
        }
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(blockX, 0, blockZ);
        int y = level.m_6924_(Heightmap.Types.WORLD_SURFACE, blockX, blockZ);
        pos.m_122178_(blockX, y, blockZ);
        BlockState state = level.m_8055_((BlockPos)pos);
        while (y > level.m_141937_() && (state == null || state.m_60795_())) {
            pos.m_142448_(--y);
            state = level.m_8055_((BlockPos)pos);
        }
        if (state == null) {
            return 0;
        }
        MapColor mapColor = state.m_284242_((BlockGetter)level, (BlockPos)pos);
        if (mapColor == null || mapColor == MapColor.f_283808_) {
            return 0;
        }
        int rgb = mapColor.f_283871_;
        if (rgb == 0) {
            return 0;
        }
        return XaeroMapSampler.argb(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF, 255);
    }

    static int argb(int r, int g, int b, int a) {
        return a << 24 | b << 16 | g << 8 | r;
    }

    static int color(int r, int g, int b, int a) {
        return XaeroMapSampler.argb(r, g, b, a);
    }

    static {
        lastChunkX = Integer.MIN_VALUE;
        lastChunkZ = Integer.MIN_VALUE;
        lastChunk = null;
        glTexArrayCache = new HashMap(256);
        glDownloadsThisFrame = 0;
    }
}

