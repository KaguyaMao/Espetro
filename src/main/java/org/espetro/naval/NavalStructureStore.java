package org.espetro.naval;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 舰船结构模板目录。两种形态并存：
 * <ul>
 *   <li>多卷：{@code config/espetro/naval_structures/<name>/manifest.json} + {@code part_*.nbt}</li>
 *   <li>单文件：{@code config/espetro/naval_structures/<name>.nbt}</li>
 * </ul>
 *
 * <p>引用 id 形如 {@code espetro:naval/<name>}：先查本目录（多卷优先），找不到再回退到
 * 数据包/内置模板（{@code data/espetro/structures/naval/<name>.nbt}）。</p>
 */
public final class NavalStructureStore {

    public static final String PREFIX = "naval/";
    public static final String MANIFEST = "manifest.json";
    private static final String SUFFIX = ".nbt";
    private static final String META_SUFFIX = ".meta.json";
    private static final int MAX_NAME = 48;

    private NavalStructureStore() {
    }

    public static Path directory() {
        return FMLPaths.CONFIGDIR.get().resolve("espetro/naval_structures");
    }

    public static boolean isValidName(@Nullable String name) {
        if (name == null || name.isEmpty() || name.length() > MAX_NAME) {
            return false;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            boolean ok = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_';
            if (!ok) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    private static String nameOf(@Nullable ResourceLocation id) {
        if (id == null || !"espetro".equals(id.getNamespace())) {
            return null;
        }
        String path = id.getPath();
        if (!path.startsWith(PREFIX)) {
            return null;
        }
        String name = path.substring(PREFIX.length());
        return isValidName(name) ? name : null;
    }

    /** {@code espetro:naval/<name>} → 外部单文件路径（多卷时返回其目录下的 manifest 所在目录名不存在的话仍返回单文件路径）。 */
    @Nullable
    public static Path pathFor(@Nullable ResourceLocation id) {
        String name = nameOf(id);
        return name == null ? null : pathForName(name);
    }

    /** {@code espetro:naval/<name>} → 多卷目录；不是多卷形态时返回 null。 */
    @Nullable
    public static Path dirFor(@Nullable ResourceLocation id) {
        String name = nameOf(id);
        if (name == null) {
            return null;
        }
        Path dir = dirForName(name);
        return Files.isRegularFile(dir.resolve(MANIFEST)) ? dir : null;
    }

    public static Path pathForName(String name) {
        return directory().resolve(name + SUFFIX);
    }

    public static Path dirForName(String name) {
        return directory().resolve(name);
    }

    public static Path manifestForName(String name) {
        return dirForName(name).resolve(MANIFEST);
    }

    public static Path metaPathForName(String name) {
        return directory().resolve(name + META_SUFFIX);
    }

    public static boolean hasParts(String name) {
        return Files.isRegularFile(manifestForName(name));
    }

    /** 列目录：多卷（有 manifest 的子目录）+ 单文件，两种都算。 */
    public static List<String> listNames() {
        Path dir = directory();
        if (!Files.isDirectory(dir)) {
            return List.of();
        }
        List<String> names = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            for (Path p : (Iterable<Path>) stream::iterator) {
                String file = p.getFileName().toString();
                if (Files.isDirectory(p)) {
                    if (Files.isRegularFile(p.resolve(MANIFEST)) && isValidName(file)) {
                        names.add(file);
                    }
                    continue;
                }
                if (file.endsWith(SUFFIX) && !file.endsWith(META_SUFFIX)) {
                    String name = file.substring(0, file.length() - SUFFIX.length());
                    if (isValidName(name)) {
                        names.add(name);
                    }
                }
            }
        } catch (Exception ignored) {
            return names;
        }
        names.sort(String::compareTo);
        return names;
    }
}
