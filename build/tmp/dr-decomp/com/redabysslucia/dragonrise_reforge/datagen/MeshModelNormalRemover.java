/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraftforge.common.data.ExistingFileHelper
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MeshModelNormalRemover
implements DataProvider {
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;

    public MeshModelNormalRemover(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                ArrayList<Path> dirs = new ArrayList<Path>();
                Path bedrock = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/models/bedrock/vehicle");
                Path geo = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/geo");
                if (Files.exists(bedrock, new LinkOption[0])) {
                    dirs.add(bedrock);
                }
                if (Files.exists(geo, new LinkOption[0])) {
                    dirs.add(geo);
                }
                int cleaned = 0;
                for (Path dir : dirs) {
                    DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.geo.json");
                    try {
                        for (Path file : stream) {
                            String content = Files.readString(file);
                            String modified = MeshModelNormalRemover.removeNormals(content);
                            if (modified.equals(content)) continue;
                            Files.writeString(file, (CharSequence)modified, new OpenOption[0]);
                            ++cleaned;
                            System.out.println("MeshModelNormalRemover: removed normals from " + dir.getFileName() + "/" + file.getFileName());
                        }
                    }
                    finally {
                        if (stream == null) continue;
                        stream.close();
                    }
                }
                System.out.println("MeshModelNormalRemover: cleaned " + cleaned + " model file(s)");
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to remove normals from mesh models", e);
            }
        });
    }

    static String removeNormals(String content) {
        int bracket;
        int colon;
        int i;
        StringBuilder sb = new StringBuilder(content.length());
        int searchFrom = 0;
        while ((i = content.indexOf("\"normals\"", searchFrom)) >= 0 && (colon = content.indexOf(58, i)) >= 0 && (bracket = content.indexOf(91, colon)) >= 0) {
            int t;
            int j;
            int depth = 0;
            for (j = bracket; j < content.length(); ++j) {
                char c = content.charAt(j);
                if (c == '[') {
                    ++depth;
                    continue;
                }
                if (c == ']' && --depth == 0) break;
            }
            if (depth != 0 || j >= content.length()) break;
            int end = j + 1;
            int delStart = i;
            int delEnd = end;
            for (t = i - 1; t >= 0 && Character.isWhitespace(content.charAt(t)); --t) {
            }
            if (t >= 0 && content.charAt(t) == ',') {
                delStart = t;
            } else {
                for (t = end; t < content.length() && Character.isWhitespace(content.charAt(t)); ++t) {
                }
                if (t < content.length() && content.charAt(t) == ',') {
                    delEnd = t + 1;
                }
            }
            sb.append(content, searchFrom, delStart);
            searchFrom = delEnd;
        }
        sb.append(content, searchFrom, content.length());
        return sb.toString();
    }

    public String m_6055_() {
        return "DragonRise Mesh Model Normal Remover";
    }
}

