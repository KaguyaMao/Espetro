/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.loader.Loader;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class ResourceUsageIndex {
    private static final Map<String, Set<String>> TEMPLATE_CSS = new ConcurrentHashMap<String, Set<String>>();
    private static final Map<String, Set<String>> TEMPLATE_JS = new ConcurrentHashMap<String, Set<String>>();
    private static final Map<String, Set<String>> CSS_IMPORTS = new ConcurrentHashMap<String, Set<String>>();

    private ResourceUsageIndex() {
    }

    public static void recordCss(String templatePath, List<String> hrefs) {
        ResourceUsageIndex.record(TEMPLATE_CSS, templatePath, hrefs);
    }

    public static void recordJs(String templatePath, List<String> srcs) {
        ResourceUsageIndex.record(TEMPLATE_JS, templatePath, srcs);
    }

    private static void record(Map<String, Set<String>> index, String templatePath, List<String> rawPaths) {
        if (templatePath == null || templatePath.isBlank()) {
            return;
        }
        HashSet<String> resolved = new HashSet<String>();
        if (rawPaths != null) {
            for (String raw : rawPaths) {
                String path = ResourceUsageIndex.resolveLocal(templatePath, raw);
                if (path == null) continue;
                resolved.add(path);
            }
        }
        if (resolved.isEmpty()) {
            index.remove(templatePath);
        } else {
            index.put(templatePath, resolved);
        }
    }

    public static void recordImport(String parent, String imported) {
        if (parent == null || parent.isBlank() || imported == null || imported.isBlank()) {
            return;
        }
        if (Loader.isRemotePath(parent) || Loader.isRemotePath(imported)) {
            return;
        }
        CSS_IMPORTS.computeIfAbsent(parent, key -> ConcurrentHashMap.newKeySet()).add(imported);
    }

    public static Set<String> affectedTemplates(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            return Set.of();
        }
        HashSet<String> affectedCss = new HashSet<String>();
        ArrayDeque<String> queue = new ArrayDeque<String>();
        queue.add(resourcePath);
        while (!queue.isEmpty()) {
            String current = (String)queue.poll();
            if (!affectedCss.add(current)) continue;
            for (Map.Entry<String, Set<String>> entry : CSS_IMPORTS.entrySet()) {
                if (!entry.getValue().contains(current)) continue;
                queue.add(entry.getKey());
            }
        }
        HashSet<String> templates = new HashSet<String>();
        block2: for (Map.Entry<String, Set<String>> entry : TEMPLATE_CSS.entrySet()) {
            for (String css : entry.getValue()) {
                if (!affectedCss.contains(css)) continue;
                templates.add(entry.getKey());
                continue block2;
            }
        }
        for (Map.Entry<String, Set<String>> entry : TEMPLATE_JS.entrySet()) {
            if (!entry.getValue().contains(resourcePath)) continue;
            templates.add(entry.getKey());
        }
        return templates;
    }

    private static String resolveLocal(String contextPath, String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String resolved = Loader.resolve(contextPath, raw);
        if (resolved.isBlank() || Loader.isRemotePath(resolved)) {
            return null;
        }
        return resolved;
    }
}

