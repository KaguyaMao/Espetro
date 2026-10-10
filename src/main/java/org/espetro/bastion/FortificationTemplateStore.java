package org.espetro.bastion;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 管理员自建工事模板的磁盘目录。
 *
 * <p>模板以标准 Minecraft Structure NBT（gzip）存放在
 * {@code config/espetro/fortification_templates/<name>.nbt}，与
 * {@code config/espetro/fortifications.json} 同级，便于随 config 备份。
 * 引用方式仍是 {@code espetro:fortifications/<name>}：编译器先查本目录，
 * 找不到再回退到数据包/内置模板。</p>
 */
public final class FortificationTemplateStore {

    /** 结构模板引用前缀。 */
    static final String PREFIX = "fortifications/";
    /** 模板名允许的字符（禁止路径分隔符，防目录穿越）。 */
    private static final Pattern NAME = Pattern.compile("[a-z0-9_]{1,48}");
    private static final String SUFFIX = ".nbt";
    private static final String META_SUFFIX = ".meta.json";

    private FortificationTemplateStore() {
    }

    public static Path directory() {
        return FMLPaths.CONFIGDIR.get().resolve("espetro/fortification_templates");
    }

    /** 名称是否合法（小写字母/数字/下划线，1~48 字符）。 */
    public static boolean isValidName(@Nullable String name) {
        return name != null && NAME.matcher(name).matches();
    }

    /** 把 {@code espetro:fortifications/<name>} 解析为文件路径；非本命名约定返回 null。 */
    @Nullable
    public static Path pathFor(@Nullable ResourceLocation id) {
        if (id == null || !"espetro".equals(id.getNamespace())) return null;
        String path = id.getPath();
        if (!path.startsWith(PREFIX)) return null;
        String name = path.substring(PREFIX.length());
        if (!isValidName(name)) return null;
        return directory().resolve(name + SUFFIX);
    }

    public static Path pathForName(String name) {
        return directory().resolve(name + SUFFIX);
    }

    public static Path metaPathForName(String name) {
        return directory().resolve(name + META_SUFFIX);
    }

    /** 目录内已有模板名（不含后缀），按字母排序。 */
    public static List<String> listNames() {
        Path dir = directory();
        if (!Files.isDirectory(dir)) return List.of();
        List<String> names = new ArrayList<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(Files::isRegularFile)
                .map(p -> p.getFileName().toString())
                .filter(n -> n.endsWith(SUFFIX) && !n.endsWith(META_SUFFIX))
                .map(n -> n.substring(0, n.length() - SUFFIX.length()))
                .filter(FortificationTemplateStore::isValidName)
                .sorted()
                .forEach(names::add);
        } catch (IOException ignored) {
            return List.of();
        }
        return List.copyOf(names);
    }

    /** 把现有文件改名备份（带时间戳），返回备份文件名，无文件时返回 null。 */
    @Nullable
    public static String backup(String name, String stamp) throws IOException {
        Path file = pathForName(name);
        if (!Files.isRegularFile(file)) return null;
        Path target = file.resolveSibling(file.getFileName() + ".bak-" + stamp);
        Files.move(file, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        return target.getFileName().toString();
    }

    public static boolean delete(String name) throws IOException {
        boolean removed = Files.deleteIfExists(pathForName(name));
        Files.deleteIfExists(metaPathForName(name));
        return removed;
    }

    public static String normalizedName(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }
}
