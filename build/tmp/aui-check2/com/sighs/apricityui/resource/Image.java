/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 */
package com.sighs.apricityui.resource;

import com.mojang.blaze3d.platform.NativeImage;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.resource.async.image.DecodedImage;
import com.sighs.apricityui.spi.AuiRenderService;
import com.sighs.apricityui.spi.AuiResourceService;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.TextureKey;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Image {
    public static ITexture loadTexture(String cacheKey, InputStream is) {
        if (is == null) {
            ApricityUI.LOGGER.warn("[AUI Image] resource stream is missing path={}", (Object)cacheKey);
            return null;
        }
        try {
            byte[] bytes = is.readAllBytes();
            DecodedImage decodedImage = Image.decode(cacheKey, bytes);
            return Image.uploadDecoded(cacheKey, decodedImage);
        }
        catch (IOException e) {
            ApricityUI.LOGGER.error("[AUI Image] failed to read image bytes path={}", (Object)cacheKey, (Object)e);
            return null;
        }
    }

    public static DecodedImage decode(String cacheKey, byte[] data) {
        DecodedImage decoded;
        String format;
        String fileName;
        if (data == null || data.length == 0) {
            ApricityUI.LOGGER.warn("[AUI Image] image data is empty path={}", (Object)cacheKey);
            return null;
        }
        String string = fileName = cacheKey == null ? "" : cacheKey.toLowerCase();
        if (fileName.endsWith(".gif")) {
            format = "gif";
            decoded = Image.loadGifTexture(data);
        } else if (fileName.endsWith(".cur")) {
            format = "cur";
            decoded = Image.loadCurTexture(data);
        } else if (fileName.endsWith(".ani")) {
            format = "ani";
            decoded = Image.loadAniTexture(data);
        } else {
            format = "static";
            decoded = Image.loadStaticTexture(data);
        }
        if (decoded == null) {
            ApricityUI.LOGGER.warn("[AUI Image] image decode returned no result path={} format={} bytes={}", new Object[]{cacheKey, format, data.length});
        }
        return decoded;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ITexture uploadDecoded(String cacheKey, DecodedImage decodedImage) {
        if (decodedImage == null) {
            ApricityUI.LOGGER.warn("[AUI Image] cannot upload an undecoded image path={}", (Object)cacheKey);
            return null;
        }
        try {
            if (decodedImage.isAnimated()) {
                ArrayList<AnimatedTexture.Frame> frames = new ArrayList<AnimatedTexture.Frame>();
                int[] delays = decodedImage.getFrameDelaysMs();
                try {
                    int index = 0;
                    for (NativeImage frameImage : decodedImage.getFrames()) {
                        TextureInfo info = Image.uploadImage(cacheKey + "_frame_" + index, frameImage);
                        int delay = index < delays.length ? delays[index] : 100;
                        frames.add(new AnimatedTexture.Frame(info.key, info.texture, Math.max(delay, 20)));
                        ++index;
                    }
                }
                catch (Exception e) {
                    new AnimatedTexture(frames, decodedImage.getWidth(), decodedImage.getHeight(), decodedImage.getHotspotX(), decodedImage.getHotspotY()).destroy();
                    throw e;
                }
                AnimatedTexture animatedTexture = new AnimatedTexture(frames, decodedImage.getWidth(), decodedImage.getHeight(), decodedImage.getHotspotX(), decodedImage.getHotspotY());
                return animatedTexture;
            }
            NativeImage imageData = decodedImage.getStaticImage();
            if (imageData == null) {
                ITexture delays = null;
                return delays;
            }
            TextureInfo info = Image.uploadImage(cacheKey, imageData);
            StaticTexture staticTexture = new StaticTexture(info.texture, info.key, info.width, info.height, decodedImage.getHotspotX(), decodedImage.getHotspotY());
            return staticTexture;
        }
        catch (Exception e) {
            ApricityUI.LOGGER.error("[AUI Image] texture upload failed path={}", (Object)cacheKey, (Object)e);
            ITexture iTexture = null;
            return iTexture;
        }
        finally {
            decodedImage.close();
        }
    }

    private static DecodedImage loadStaticTexture(byte[] data) {
        DecodedImage decodedImage;
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        try {
            NativeImage image = NativeImage.m_85058_((InputStream)bis);
            decodedImage = DecodedImage.ofStatic(image);
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((InputStream)bis).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                ApricityUI.LOGGER.error("[AUI Image] static image decode failed bytes={}", (Object)data.length, (Object)e);
                return null;
            }
        }
        ((InputStream)bis).close();
        return decodedImage;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive exception aggregation
     */
    private static DecodedImage loadGifTexture(byte[] data) {
        ArrayList<NativeImage> frames = new ArrayList<NativeImage>();
        ArrayList<Integer> delays = new ArrayList<Integer>();
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);){
            DecodedImage decodedImage;
            block23: {
                Iterator<ImageReader> readers;
                ImageInputStream stream;
                block20: {
                    DecodedImage decodedImage2;
                    block21: {
                        stream = ImageIO.createImageInputStream(bis);
                        readers = ImageIO.getImageReadersBySuffix("gif");
                        if (readers.hasNext()) break block20;
                        ApricityUI.LOGGER.error("[AUI Image] no GIF ImageIO reader is installed");
                        decodedImage2 = null;
                        if (stream == null) break block21;
                        stream.close();
                    }
                    return decodedImage2;
                }
                try {
                    ImageReader reader = readers.next();
                    try {
                        reader.setInput(stream);
                        int count = reader.getNumImages(true);
                        for (int i = 0; i < count; ++i) {
                            BufferedImage frameImage = reader.read(i);
                            NativeImage nativeImage = Image.convertToNative(frameImage);
                            frames.add(nativeImage);
                            delays.add(Image.getFrameDelay(reader, i));
                        }
                    }
                    finally {
                        reader.dispose();
                    }
                    DecodedImage decodedImage3 = DecodedImage.ofAnimated(frames, delays);
                    if (decodedImage3 == null) {
                        frames.forEach(NativeImage::close);
                    }
                    decodedImage = decodedImage3;
                    if (stream == null) break block23;
                }
                catch (Throwable throwable) {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                stream.close();
            }
            return decodedImage;
        }
        catch (Exception e) {
            frames.forEach(NativeImage::close);
            ApricityUI.LOGGER.error("[AUI Image] GIF decode failed bytes={}", (Object)data.length, (Object)e);
            return null;
        }
    }

    private static DecodedImage loadCurTexture(byte[] data) {
        CursorFrame frame = Image.decodeCursorContainer(data);
        if (frame == null || frame.image == null) {
            return null;
        }
        return DecodedImage.ofStatic(frame.image, frame.hotspotX, frame.hotspotY);
    }

    private static DecodedImage loadAniTexture(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.remaining() < 12) {
            return null;
        }
        if (buffer.getInt() != Image.fourCC("RIFF")) {
            return null;
        }
        buffer.getInt();
        if (buffer.getInt() != Image.fourCC("ACON")) {
            return null;
        }
        ArrayList<byte[]> iconChunks = new ArrayList<byte[]>();
        ArrayList<Integer> sequence = new ArrayList<Integer>();
        ArrayList<Integer> rates = new ArrayList<Integer>();
        int defaultRateJiffies = 6;
        while (buffer.remaining() >= 8) {
            int listType;
            int chunkId = buffer.getInt();
            int chunkSize = buffer.getInt();
            if (chunkSize < 0 || chunkSize > buffer.remaining()) break;
            int chunkEnd = buffer.position() + chunkSize;
            if (chunkId == Image.fourCC("anih")) {
                if (chunkSize >= 36) {
                    buffer.getInt();
                    buffer.getInt();
                    buffer.getInt();
                    buffer.getInt();
                    buffer.getInt();
                    buffer.getInt();
                    buffer.getInt();
                    defaultRateJiffies = Math.max(1, buffer.getInt());
                    buffer.getInt();
                }
            } else if (chunkId == Image.fourCC("rate")) {
                while (buffer.position() + 4 <= chunkEnd) {
                    rates.add(buffer.getInt());
                }
            } else if (chunkId == Image.fourCC("seq ")) {
                while (buffer.position() + 4 <= chunkEnd) {
                    sequence.add(buffer.getInt());
                }
            } else if (chunkId == Image.fourCC("LIST") && chunkSize >= 4 && (listType = buffer.getInt()) == Image.fourCC("fram")) {
                while (buffer.position() + 8 <= chunkEnd) {
                    int subChunkId = buffer.getInt();
                    int subChunkSize = buffer.getInt();
                    if (subChunkSize >= 0 && buffer.position() + subChunkSize <= chunkEnd) {
                        if (subChunkId == Image.fourCC("icon")) {
                            byte[] iconData = new byte[subChunkSize];
                            buffer.get(iconData);
                            iconChunks.add(iconData);
                        } else {
                            buffer.position(buffer.position() + subChunkSize);
                        }
                        if ((subChunkSize & 1) == 0 || buffer.position() >= chunkEnd) continue;
                        buffer.get();
                        continue;
                    }
                    break;
                }
            }
            buffer.position(chunkEnd);
            if ((chunkSize & 1) == 0 || !buffer.hasRemaining()) continue;
            buffer.get();
        }
        if (iconChunks.isEmpty()) {
            return null;
        }
        ArrayList<NativeImage> frames = new ArrayList<NativeImage>();
        ArrayList<Integer> delays = new ArrayList<Integer>();
        int hotspotX = 0;
        int hotspotY = 0;
        try {
            int steps = sequence.isEmpty() ? iconChunks.size() : sequence.size();
            for (int i = 0; i < steps; ++i) {
                CursorFrame frame;
                int iconIndex;
                int n = iconIndex = sequence.isEmpty() ? i : (Integer)sequence.get(i);
                if (iconIndex < 0 || iconIndex >= iconChunks.size() || (frame = Image.decodeCursorContainer((byte[])iconChunks.get(iconIndex))) == null || frame.image == null) continue;
                if (frames.isEmpty()) {
                    hotspotX = frame.hotspotX;
                    hotspotY = frame.hotspotY;
                }
                frames.add(frame.image);
                int rate = i < rates.size() ? (Integer)rates.get(i) : defaultRateJiffies;
                delays.add(Math.max(20, Math.max(1, rate) * 1000 / 60));
            }
            DecodedImage decodedImage = DecodedImage.ofAnimated(frames, delays, hotspotX, hotspotY);
            if (decodedImage == null) {
                frames.forEach(NativeImage::close);
            }
            return decodedImage;
        }
        catch (Exception e) {
            frames.forEach(NativeImage::close);
            ApricityUI.LOGGER.error("[AUI Image] ANI decode failed bytes={}", (Object)data.length, (Object)e);
            return null;
        }
    }

    private static NativeImage convertToNative(BufferedImage bi) {
        int w = bi.getWidth();
        int h = bi.getHeight();
        NativeImage nativeImage = new NativeImage(w, h, false);
        for (int y = 0; y < h; ++y) {
            for (int x = 0; x < w; ++x) {
                int argb = bi.getRGB(x, y);
                int a = argb >> 24 & 0xFF;
                int r = argb >> 16 & 0xFF;
                int g = argb >> 8 & 0xFF;
                int b = argb & 0xFF;
                int abgr = a << 24 | b << 16 | g << 8 | r;
                AuiServices.render().setImagePixel(nativeImage, x, y, abgr);
            }
        }
        return nativeImage;
    }

    private static CursorFrame decodeCursorContainer(byte[] data) {
        if (data == null || data.length < 22) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int reserved = Short.toUnsignedInt(buffer.getShort());
        int type = Short.toUnsignedInt(buffer.getShort());
        int count = Short.toUnsignedInt(buffer.getShort());
        if (reserved != 0 || type != 1 && type != 2 || count <= 0) {
            return null;
        }
        CursorDirEntry selected = null;
        for (int i = 0; i < count; ++i) {
            if (buffer.remaining() < 16) {
                return null;
            }
            int widthRaw = Byte.toUnsignedInt(buffer.get());
            int heightRaw = Byte.toUnsignedInt(buffer.get());
            buffer.get();
            buffer.get();
            int hotspotX = Short.toUnsignedInt(buffer.getShort());
            int hotspotY = Short.toUnsignedInt(buffer.getShort());
            int bytesInRes = buffer.getInt();
            int imageOffset = buffer.getInt();
            int width = widthRaw == 0 ? 256 : widthRaw;
            int height = heightRaw == 0 ? 256 : heightRaw;
            CursorDirEntry entry = new CursorDirEntry(width, height, hotspotX, hotspotY, bytesInRes, imageOffset);
            if (selected != null && entry.width * entry.height <= selected.width * selected.height) continue;
            selected = entry;
        }
        if (selected == null) {
            return null;
        }
        int end = selected.imageOffset + selected.bytesInRes;
        if (selected.imageOffset < 0 || end > data.length || selected.imageOffset >= end) {
            return null;
        }
        byte[] imageData = new byte[selected.bytesInRes];
        System.arraycopy(data, selected.imageOffset, imageData, 0, selected.bytesInRes);
        NativeImage image = Image.decodeCursorImageData(imageData, selected.width, selected.height);
        if (image == null) {
            return null;
        }
        return new CursorFrame(image, selected.hotspotX, selected.hotspotY);
    }

    private static NativeImage decodeCursorImageData(byte[] imageData, int entryWidth, int entryHeight) {
        int rowStride;
        int height;
        if (imageData.length >= 8 && (imageData[0] & 0xFF) == 137 && imageData[1] == 80 && imageData[2] == 78 && imageData[3] == 71) {
            NativeImage nativeImage;
            ByteArrayInputStream input = new ByteArrayInputStream(imageData);
            try {
                nativeImage = NativeImage.m_85058_((InputStream)input);
            }
            catch (Throwable throwable) {
                try {
                    try {
                        ((InputStream)input).close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    ApricityUI.LOGGER.error("[AUI Image] cursor PNG frame decode failed bytes={} entry={}x{}", new Object[]{imageData.length, entryWidth, entryHeight, e});
                    return null;
                }
            }
            ((InputStream)input).close();
            return nativeImage;
        }
        ByteBuffer dib = ByteBuffer.wrap(imageData).order(ByteOrder.LITTLE_ENDIAN);
        if (dib.remaining() < 40) {
            return null;
        }
        int headerSize = dib.getInt();
        if (headerSize < 40 || imageData.length < headerSize) {
            return null;
        }
        int width = dib.getInt();
        int storedHeight = dib.getInt();
        dib.getShort();
        int bitCount = Short.toUnsignedInt(dib.getShort());
        int compression = dib.getInt();
        dib.getInt();
        dib.getInt();
        dib.getInt();
        dib.getInt();
        dib.getInt();
        if (width <= 0) {
            width = entryWidth;
        }
        int n = height = storedHeight > 0 ? storedHeight / 2 : entryHeight;
        if (height <= 0) {
            height = entryHeight;
        }
        int xorOffset = headerSize;
        if (bitCount <= 8) {
            int colorCount = 1 << bitCount;
            xorOffset += colorCount * 4;
        }
        if (xorOffset >= imageData.length) {
            return null;
        }
        boolean hasAlpha = false;
        if (compression == 0 && bitCount == 32) {
            rowStride = width * 4;
            hasAlpha = Image.scanForAlpha(imageData, xorOffset, width, height, rowStride);
        } else if (compression == 0 && bitCount == 24) {
            rowStride = Image.align4(width * 3);
        } else if (compression == 0 && bitCount == 8) {
            rowStride = Image.align4(width);
        } else {
            return null;
        }
        int andOffset = xorOffset + rowStride * height;
        int andStride = Image.align4((width + 7) / 8);
        NativeImage image = new NativeImage(width, height, false);
        for (int y = 0; y < height; ++y) {
            int srcY = height - 1 - y;
            int xorRow = xorOffset + srcY * rowStride;
            int andRow = andOffset + srcY * andStride;
            for (int x = 0; x < width; ++x) {
                int r;
                int g;
                int b;
                int a = 255;
                if (bitCount == 32) {
                    pixelOffset = xorRow + x * 4;
                    if (pixelOffset + 3 >= imageData.length) {
                        image.close();
                        return null;
                    }
                    b = imageData[pixelOffset] & 0xFF;
                    g = imageData[pixelOffset + 1] & 0xFF;
                    r = imageData[pixelOffset + 2] & 0xFF;
                    a = imageData[pixelOffset + 3] & 0xFF;
                    if (!hasAlpha && Image.isMaskTransparent(imageData, andRow, x)) {
                        a = 0;
                    }
                } else if (bitCount == 24) {
                    pixelOffset = xorRow + x * 3;
                    if (pixelOffset + 2 >= imageData.length) {
                        image.close();
                        return null;
                    }
                    b = imageData[pixelOffset] & 0xFF;
                    g = imageData[pixelOffset + 1] & 0xFF;
                    r = imageData[pixelOffset + 2] & 0xFF;
                    if (Image.isMaskTransparent(imageData, andRow, x)) {
                        a = 0;
                    }
                } else {
                    pixelOffset = xorRow + x;
                    if (pixelOffset >= imageData.length) {
                        image.close();
                        return null;
                    }
                    int paletteOffset = headerSize + (imageData[pixelOffset] & 0xFF) * 4;
                    if (paletteOffset + 3 >= imageData.length) {
                        image.close();
                        return null;
                    }
                    b = imageData[paletteOffset] & 0xFF;
                    g = imageData[paletteOffset + 1] & 0xFF;
                    r = imageData[paletteOffset + 2] & 0xFF;
                    if (Image.isMaskTransparent(imageData, andRow, x)) {
                        a = 0;
                    }
                }
                int abgr = a << 24 | b << 16 | g << 8 | r;
                AuiServices.render().setImagePixel(image, x, y, abgr);
            }
        }
        return image;
    }

    private static boolean scanForAlpha(byte[] imageData, int xorOffset, int width, int height, int rowStride) {
        for (int y = 0; y < height; ++y) {
            int row = xorOffset + y * rowStride;
            for (int x = 0; x < width; ++x) {
                int pixelOffset = row + x * 4 + 3;
                if (pixelOffset >= imageData.length) {
                    return false;
                }
                if ((imageData[pixelOffset] & 0xFF) == 0) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean isMaskTransparent(byte[] imageData, int andRow, int x) {
        int byteOffset = andRow + x / 8;
        if (byteOffset < 0 || byteOffset >= imageData.length) {
            return false;
        }
        int bit = 7 - x % 8;
        return (imageData[byteOffset] >> bit & 1) != 0;
    }

    private static int align4(int value) {
        return value + 3 & 0xFFFFFFFC;
    }

    private static int fourCC(String value) {
        return value.charAt(0) | value.charAt(1) << 8 | value.charAt(2) << 16 | value.charAt(3) << 24;
    }

    private static int getFrameDelay(ImageReader reader, int imageIndex) throws IOException {
        int delayTime = 100;
        IIOMetadata metadata = reader.getImageMetadata(imageIndex);
        String metaFormatName = metadata.getNativeMetadataFormatName();
        Node root = metadata.getAsTree(metaFormatName);
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            NamedNodeMap attributes;
            Node delayNode;
            Node node = children.item(i);
            if (!node.getNodeName().equalsIgnoreCase("GraphicControlExtension") || (delayNode = (attributes = node.getAttributes()).getNamedItem("delayTime")) == null) continue;
            delayTime = Integer.parseInt(delayNode.getNodeValue()) * 10;
        }
        return Math.max(delayTime, 20);
    }

    private static TextureInfo uploadImage(String cacheKey, NativeImage image) {
        AuiRenderService render = AuiServices.render();
        AuiResourceService resources = AuiServices.resources();
        Object texture = render.createDynamicTexture(cacheKey, image, false);
        render.uploadTextureRegion(texture, image, 0, 0, image.m_84982_(), image.m_85084_(), false);
        TextureKey key = resources.locationOf(cacheKey);
        render.registerTexture(texture, resources.textureLocation(key));
        return new TextureInfo(texture, cacheKey, image.m_84982_(), image.m_85084_());
    }

    public static interface ITexture {
        public String getKey();

        public int getWidth();

        public int getHeight();

        default public int getHotspotX() {
            return 0;
        }

        default public int getHotspotY() {
            return 0;
        }

        public void destroy();
    }

    private record TextureInfo(Object texture, String key, int width, int height) {
    }

    public static class AnimatedTexture
    implements ITexture {
        private final List<Frame> frames;
        private final int width;
        private final int height;
        private final int totalDuration;
        private final int hotspotX;
        private final int hotspotY;

        public AnimatedTexture(List<Frame> frames, int width, int height, int hotspotX, int hotspotY) {
            this.frames = frames;
            this.width = width;
            this.height = height;
            this.totalDuration = frames.stream().mapToInt(Frame::durationMs).sum();
            this.hotspotX = hotspotX;
            this.hotspotY = hotspotY;
        }

        @Override
        public String getKey() {
            if (this.frames.isEmpty()) {
                return null;
            }
            if (this.totalDuration == 0) {
                return this.frames.get((int)0).key;
            }
            long now = System.currentTimeMillis();
            long cycleTime = now % (long)this.totalDuration;
            int currentTimer = 0;
            for (Frame frame : this.frames) {
                if (cycleTime >= (long)(currentTimer += frame.durationMs)) continue;
                return frame.key;
            }
            return this.frames.get((int)0).key;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getHotspotX() {
            return this.hotspotX;
        }

        @Override
        public int getHotspotY() {
            return this.hotspotY;
        }

        @Override
        public void destroy() {
            for (Frame frame : this.frames) {
                Object texture = frame.texture();
                AuiServices.render().recordRenderCall(() -> AuiServices.render().closeTexture(texture));
            }
        }

        public record Frame(String key, Object texture, int durationMs) {
        }
    }

    public static class StaticTexture
    implements ITexture {
        private final Object texture;
        private final String key;
        private final int width;
        private final int height;
        private final int hotspotX;
        private final int hotspotY;

        public StaticTexture(Object texture, String key, int width, int height, int hotspotX, int hotspotY) {
            this.texture = texture;
            this.key = key;
            this.width = width;
            this.height = height;
            this.hotspotX = hotspotX;
            this.hotspotY = hotspotY;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }

        @Override
        public int getHotspotX() {
            return this.hotspotX;
        }

        @Override
        public int getHotspotY() {
            return this.hotspotY;
        }

        @Override
        public void destroy() {
            Object texture = this.texture;
            AuiServices.render().recordRenderCall(() -> AuiServices.render().closeTexture(texture));
        }
    }

    private record CursorFrame(NativeImage image, int hotspotX, int hotspotY) {
    }

    private record CursorDirEntry(int width, int height, int hotspotX, int hotspotY, int bytesInRes, int imageOffset) {
    }
}

