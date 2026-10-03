/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.dimension;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.annotation.Nullable;

final class BattlefieldNamespaceReset {
    static final String NAMESPACE = "espetro";
    static final String TRASH = ".espetro-reset-trash";
    static final String TOMBSTONE_PREFIX = "startup-";

    private BattlefieldNamespaceReset() {
    }

    static ResetResult reset(Path worldRoot, long generation) {
        return BattlefieldNamespaceReset.reset(worldRoot, generation, NioMover.INSTANCE, BattlefieldNamespaceReset::deleteTree);
    }

    static ResetResult reset(Path worldRoot, long generation, Mover mover, Deleter deleter) {
        ArrayList<String> warnings = new ArrayList<String>();
        try {
            if (worldRoot == null) {
                throw new IllegalStateException("worldRoot \u4e3a\u7a7a");
            }
            Path lexicalWorld = worldRoot.toAbsolutePath().normalize();
            if (!Files.isDirectory(lexicalWorld, LinkOption.NOFOLLOW_LINKS) || Files.isSymbolicLink(lexicalWorld)) {
                throw new IllegalStateException("worldRoot \u4e0d\u662f\u5b89\u5168\u7684\u771f\u5b9e\u76ee\u5f55: " + lexicalWorld);
            }
            Path realWorld = lexicalWorld.toRealPath(new LinkOption[0]);
            Path dimensions = lexicalWorld.resolve("dimensions").normalize();
            Path namespace = dimensions.resolve(NAMESPACE).normalize();
            Path trash = lexicalWorld.resolve(TRASH).normalize();
            BattlefieldNamespaceReset.requireChild(lexicalWorld, dimensions, "dimensionsRoot");
            BattlefieldNamespaceReset.requireChild(dimensions, namespace, "namespaceRoot");
            BattlefieldNamespaceReset.requireChild(lexicalWorld, trash, "trashRoot");
            if (namespace.equals(lexicalWorld) || namespace.equals(dimensions) || namespace.getParent() == null) {
                throw new IllegalStateException("\u62d2\u7edd\u5371\u9669 namespace \u76ee\u6807: " + namespace);
            }
            if (Files.exists(dimensions, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(dimensions) || !Files.isDirectory(dimensions, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IllegalStateException("dimensionsRoot \u5fc5\u987b\u662f\u975e\u7b26\u53f7\u94fe\u63a5\u76ee\u5f55: " + dimensions);
                }
                Path realDimensions = dimensions.toRealPath(new LinkOption[0]);
                if (!realDimensions.startsWith(realWorld) || realDimensions.equals(realWorld)) {
                    throw new IllegalStateException("dimensionsRoot \u8d8a\u51fa\u5f53\u524d\u5b58\u6863: " + realDimensions);
                }
            }
            if (Files.exists(trash, LinkOption.NOFOLLOW_LINKS) && (Files.isSymbolicLink(trash) || !Files.isDirectory(trash, LinkOption.NOFOLLOW_LINKS))) {
                throw new IllegalStateException("trashRoot \u5fc5\u987b\u662f\u975e\u7b26\u53f7\u94fe\u63a5\u76ee\u5f55: " + trash);
            }
            if (!Files.exists(namespace, LinkOption.NOFOLLOW_LINKS)) {
                BattlefieldNamespaceReset.cleanupExistingTrash(lexicalWorld, trash, deleter, warnings);
                return ResetResult.ready(null, warnings);
            }
            if (Files.exists(trash, LinkOption.NOFOLLOW_LINKS)) {
                if (Files.isSymbolicLink(trash) || !Files.isDirectory(trash, LinkOption.NOFOLLOW_LINKS)) {
                    throw new IllegalStateException("trashRoot \u5fc5\u987b\u662f\u975e\u7b26\u53f7\u94fe\u63a5\u76ee\u5f55: " + trash);
                }
            } else {
                Files.createDirectory(trash, new FileAttribute[0]);
            }
            if (Files.isSymbolicLink(trash)) {
                throw new IllegalStateException("trashRoot \u521b\u5efa\u540e\u53d8\u6210\u7b26\u53f7\u94fe\u63a5: " + trash);
            }
            Path realTrash = trash.toRealPath(new LinkOption[0]);
            if (!realTrash.startsWith(realWorld) || realTrash.equals(realWorld)) {
                throw new IllegalStateException("trashRoot \u8d8a\u51fa\u5f53\u524d\u5b58\u6863: " + realTrash);
            }
            Path tombstone = trash.resolve(TOMBSTONE_PREFIX + generation + "-" + UUID.randomUUID()).normalize();
            BattlefieldNamespaceReset.requireChild(trash, tombstone, "tombstone");
            try {
                mover.atomic(namespace, tombstone);
            }
            catch (AtomicMoveNotSupportedException unsupported) {
                mover.regular(namespace, tombstone);
            }
            if (Files.exists(namespace, LinkOption.NOFOLLOW_LINKS) || !Files.exists(tombstone, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("namespace \u9694\u79bb\u540e\u72b6\u6001\u4e0d\u4e00\u81f4");
            }
            try {
                deleter.delete(tombstone);
            }
            catch (Exception e) {
                warnings.add("live namespace \u5df2\u9694\u79bb\uff0c\u4f46 tombstone \u5220\u9664\u5931\u8d25: " + e.getMessage());
            }
            BattlefieldNamespaceReset.cleanupExistingTrash(lexicalWorld, trash, deleter, warnings);
            return ResetResult.ready(tombstone, warnings);
        }
        catch (Exception e) {
            return ResetResult.failed(e.getMessage() == null ? e.toString() : e.getMessage(), warnings);
        }
    }

    private static void cleanupExistingTrash(Path world, Path trash, Deleter deleter, List<String> warnings) {
        try {
            if (!Files.exists(trash, LinkOption.NOFOLLOW_LINKS)) {
                return;
            }
            if (Files.isSymbolicLink(trash) || !Files.isDirectory(trash, LinkOption.NOFOLLOW_LINKS)) {
                throw new IOException("\u4e0d\u5b89\u5168\u7684 trashRoot: " + trash);
            }
            try (Stream<Path> entries = Files.list(trash);){
                for (Path entry : entries.toList()) {
                    String name = entry.getFileName().toString();
                    if (!name.startsWith(TOMBSTONE_PREFIX)) continue;
                    Path normalized = entry.toAbsolutePath().normalize();
                    BattlefieldNamespaceReset.requireChild(trash.toAbsolutePath().normalize(), normalized, "old tombstone");
                    try {
                        deleter.delete(normalized);
                    }
                    catch (Exception e) {
                        warnings.add("\u65e7 tombstone \u5220\u9664\u5931\u8d25 " + name + ": " + e.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            warnings.add("\u65e7 tombstone \u6e05\u7406\u8df3\u8fc7: " + e.getMessage());
        }
    }

    private static void requireChild(Path parent, Path child, String label) {
        Path normalizedParent = parent.toAbsolutePath().normalize();
        Path normalizedChild = child.toAbsolutePath().normalize();
        if (!normalizedChild.startsWith(normalizedParent) || normalizedChild.equals(normalizedParent)) {
            throw new IllegalStateException(label + " \u4e0d\u5728\u9884\u671f\u7236\u76ee\u5f55\u5185: " + normalizedChild);
        }
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root, LinkOption.NOFOLLOW_LINKS)) {
            return;
        }
        Files.walkFileTree(root, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, @Nullable IOException error) throws IOException {
                if (error != null) {
                    throw error;
                }
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static enum NioMover implements Mover
    {
        INSTANCE;


        @Override
        public void atomic(Path source, Path target) throws IOException {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
        }

        @Override
        public void regular(Path source, Path target) throws IOException {
            Files.move(source, target, new CopyOption[0]);
        }
    }

    @FunctionalInterface
    static interface Deleter {
        public void delete(Path var1) throws IOException;
    }

    static interface Mover {
        public void atomic(Path var1, Path var2) throws IOException;

        public void regular(Path var1, Path var2) throws IOException;
    }

    record ResetResult(boolean isolated, @Nullable Path tombstone, List<String> warnings, @Nullable String error) {
        static ResetResult ready(@Nullable Path tombstone, List<String> warnings) {
            return new ResetResult(true, tombstone, List.copyOf(warnings), null);
        }

        static ResetResult failed(String error, List<String> warnings) {
            return new ResetResult(false, null, List.copyOf(warnings), error);
        }
    }
}

