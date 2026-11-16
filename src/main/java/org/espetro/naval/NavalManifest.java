package org.espetro.naval;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 多卷舰船结构清单：{@code config/espetro/naval_structures/<name>/manifest.json}。
 *
 * <p>一艘船 = 一份清单 + N 个 {@code part_*.nbt}。每卷有自己的局部偏移（相对整船最小角），
 * 贴图时 {@code anchor + rotate(offset)} 即该卷落点。</p>
 */
public final class NavalManifest {

    public static final int CURRENT_FORMAT = 2;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public int format = CURRENT_FORMAT;
    public String name;
    public String id;
    public int[] size = new int[]{0, 0, 0};
    public int[] anchor = new int[]{0, 0, 0};
    public int[] pivot = new int[]{0, 0, 0};
    public int draft = 0;
    public String author = "";
    public String exported_at = "";
    public long total_blocks = 0L;
    public List<Part> parts = new ArrayList<>();

    /** 单卷。 */
    public static final class Part {
        public String file = "";
        public int[] offset = new int[]{0, 0, 0};
        public int[] size = new int[]{0, 0, 0};
        public int blocks = 0;
        public List<String> markers = new ArrayList<>();
    }

    public static NavalManifest read(Path file) throws IOException {
        String json = Files.readString(file, StandardCharsets.UTF_8);
        NavalManifest manifest;
        try {
            manifest = GSON.fromJson(json, NavalManifest.class);
        } catch (JsonSyntaxException e) {
            throw new IOException("manifest.json 解析失败: " + e.getMessage());
        }
        if (manifest == null) {
            throw new IOException("manifest.json 为空");
        }
        if (manifest.parts == null || manifest.parts.isEmpty()) {
            throw new IOException("manifest.json 没有卷（parts 为空）");
        }
        return manifest;
    }

    public void write(Path file) throws IOException {
        Files.writeString(file, GSON.toJson(this), StandardCharsets.UTF_8);
    }

    public int sizeX() {
        return size.length > 0 ? size[0] : 0;
    }

    public int sizeY() {
        return size.length > 1 ? size[1] : 0;
    }

    public int sizeZ() {
        return size.length > 2 ? size[2] : 0;
    }

    public static Part part(String file, int[] offset, int[] size, int blocks) {
        Part part = new Part();
        part.file = file;
        part.offset = offset;
        part.size = size;
        part.blocks = blocks;
        return part;
    }
}
