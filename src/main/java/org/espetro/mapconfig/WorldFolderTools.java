package org.espetro.mapconfig;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * EsWorld 下地图目录的枚举与改名工具。
 *
 * <p>存在非 UTF-8 文件名（例如从 GBK 系统迁移过来的中文目录名）时，Minecraft/面板都无法正确
 * 传回原名，因此提供「按序号改名」的入口：先枚举，再用序号指定目标目录。</p>
 */
public final class WorldFolderTools {

    /** 允许的新目录名（限制为 ASCII，避免再次产生编码问题）。 */
    private static final Pattern SAFE_NAME = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private WorldFolderTools() {
    }

    public record Entry(int index, Path path, String display, boolean nameValid) {
    }

    public static List<Entry> list(Path root) throws IOException {
        List<Path> dirs = new ArrayList<>();
        if (Files.isDirectory(root)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                for (Path path : stream) {
                    if (Files.isDirectory(path)) {
                        dirs.add(path);
                    }
                }
            }
        }
        dirs.sort(Comparator.comparing(path -> String.valueOf(path.getFileName())));
        List<Entry> entries = new ArrayList<>(dirs.size());
        int index = 0;
        for (Path dir : dirs) {
            String name;
            boolean valid = true;
            try {
                name = dir.getFileName().toString();
                valid = !name.isEmpty() && name.indexOf('\uFFFD') < 0;
            } catch (RuntimeException ex) {
                name = "<无法解码>";
                valid = false;
            }
            if (!valid) {
                name = "<名称不是合法 UTF-8: " + name.length() + " 个替换字符>";
            }
            entries.add(new Entry(index++, dir, name, valid));
        }
        return entries;
    }

    public static boolean isValidNewName(String name) {
        return name != null && SAFE_NAME.matcher(name).matches();
    }

    /**
     * 把 {@code index} 对应的目录改名为 {@code newName}。
     *
     * @return 成功时返回新的完整路径
     */
    public static Path rename(Path root, int index, String newName) throws IOException {
        List<Entry> entries = list(root);
        if (index < 0 || index >= entries.size()) {
            throw new IllegalArgumentException("目录序号越界：可选 0.." + (entries.size() - 1));
        }
        if (!isValidNewName(newName)) {
            throw new IllegalArgumentException("新名称只能包含字母、数字、下划线与短横线（1-64 字符）");
        }
        Path source = entries.get(index).path();
        Path target = root.resolve(newName);
        if (Files.exists(target)) {
            throw new IOException("目标已存在：" + newName);
        }
        Files.move(source, target);
        return target;
    }
}
