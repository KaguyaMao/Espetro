/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Vector3f
 */
package com.sighs.apricityui.dev;

import com.sighs.apricityui.dev.resource.ResourceCreateDialog;
import com.sighs.apricityui.dev.resource.ResourceFontAsset;
import com.sighs.apricityui.dev.resource.ResourceMetaDialog;
import com.sighs.apricityui.dev.resource.ResourcePath;
import com.sighs.apricityui.dev.resource.ResourcePreviewDialog;
import com.sighs.apricityui.dev.resource.ResourceReferenceDialog;
import com.sighs.apricityui.dom.DocumentFragment;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.ui.ContextMenu;
import com.sighs.apricityui.ui.ToastManager;
import com.sighs.apricityui.world.WorldWindow;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public final class ResourceManager {
    private static final String PATH = "devtools/resource.html";
    private static final String INTERNAL_IMAGE_PREVIEW_PATH = "devtools/resource-preview-image.html";
    private static final String ROOT_PATH = "";
    private static final int TREE_ANIMATION_LIMIT = 32;
    private static final String FOLDER_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"4\" y=\"12\" width=\"32\" height=\"22\" fill=\"#8b5cf6\"/><rect x=\"4\" y=\"8\" width=\"14\" height=\"6\" fill=\"#6d28d9\"/><rect x=\"4\" y=\"14\" width=\"32\" height=\"2\" fill=\"#6d28d9\"/></svg>";
    private static final String FILE_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"6\" y=\"4\" width=\"28\" height=\"32\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><rect x=\"10\" y=\"12\" width=\"20\" height=\"2\" fill=\"#8b5cf6\"/><rect x=\"10\" y=\"18\" width=\"16\" height=\"2\" fill=\"#8b5cf6\"/><rect x=\"10\" y=\"24\" width=\"20\" height=\"2\" fill=\"#8b5cf6\"/><rect x=\"10\" y=\"30\" width=\"12\" height=\"2\" fill=\"#8b5cf6\"/></svg>";
    private static final String IMAGE_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"6\" y=\"4\" width=\"28\" height=\"32\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><rect x=\"10\" y=\"12\" width=\"20\" height=\"14\" fill=\"#8b5cf6\" opacity=\"0.2\"/><circle cx=\"16\" cy=\"18\" r=\"3\" fill=\"#8b5cf6\"/><path d=\"M10 24l6-6 4 4 6-8 4 6v4H10z\" fill=\"#8b5cf6\"/></svg>";
    private static final String LOCK_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"10\" y=\"18\" width=\"20\" height=\"16\" fill=\"#8b5cf6\"/><path d=\"M14 18v-5a6 6 0 0 1 12 0v5\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><circle cx=\"20\" cy=\"26\" r=\"2\" fill=\"#fff\"/></svg>";
    private static final String ARCHIVE_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"6\" y=\"4\" width=\"28\" height=\"32\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><rect x=\"18\" y=\"6\" width=\"4\" height=\"4\" fill=\"#8b5cf6\"/><rect x=\"18\" y=\"14\" width=\"4\" height=\"4\" fill=\"#8b5cf6\"/><rect x=\"18\" y=\"22\" width=\"4\" height=\"4\" fill=\"#8b5cf6\"/></svg>";
    private static final String CONFIG_ICON = "<svg viewBox=\"0 0 40 40\" fill=\"none\"><rect x=\"6\" y=\"4\" width=\"28\" height=\"32\" fill=\"none\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><circle cx=\"20\" cy=\"20\" r=\"6\" fill=\"none\" stroke=\"#8b5cf6\" stroke-width=\"2\"/><rect x=\"18\" y=\"10\" width=\"4\" height=\"4\" fill=\"#8b5cf6\"/><rect x=\"18\" y=\"26\" width=\"4\" height=\"4\" fill=\"#8b5cf6\"/></svg>";
    private static Document toolDocument;
    private static FolderNode root;
    private static String currentPath;
    private static SelectedItem selectedItem;
    private static final List<String> history;
    private static int historyIndex;
    private static final Set<String> expandedPaths;
    private static final Map<String, TreeBranch> treeBranches;
    private static final ResourceCreateDialog createDialog;
    private static final ResourcePreviewDialog previewDialog;
    private static final ResourceMetaDialog metaDialog;
    private static final ResourceReferenceDialog referenceDialog;
    private static WorldWindow worldWindow;

    private ResourceManager() {
    }

    public static boolean isOpen() {
        return toolDocument != null && !toolDocument.isDisposed();
    }

    public static void reconcileConfiguredMode() {
        boolean isWorldWindow;
        if (!ResourceManager.isOpen()) {
            return;
        }
        boolean wantsWorldWindow = AuiServices.config().resourceManagerWorldWindow();
        boolean bl = isWorldWindow = worldWindow != null && toolDocument == ResourceManager.worldWindow.document;
        if (wantsWorldWindow == isWorldWindow) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (wantsWorldWindow && (minecraft == null || minecraft.f_91073_ == null || minecraft.f_91080_ != null || minecraft.f_91074_ == null)) {
            return;
        }
        ResourceManager.close();
        ResourceManager.open();
    }

    public static void toggle() {
        if (ResourceManager.isOpen()) {
            ResourceManager.close();
        } else {
            ResourceManager.open();
        }
    }

    public static void open() {
        if (ResourceManager.shouldOpenWorldWindow()) {
            ResourceManager.openWorldWindow();
            return;
        }
        if (worldWindow != null) {
            ResourceManager.closeWorldWindow();
        }
        if (!ResourceManager.isOpen()) {
            ArrayList<Document> existing = Document.get(PATH);
            Document document = toolDocument = existing.isEmpty() ? Document.create(PATH) : (Document)existing.get(existing.size() - 1);
        }
        if (toolDocument == null) {
            return;
        }
        toolDocument.setReloadPersistent(true);
        ResourceManager.refresh();
    }

    private static boolean shouldOpenWorldWindow() {
        Minecraft minecraft = Minecraft.m_91087_();
        return AuiServices.config().resourceManagerWorldWindow() && minecraft != null && minecraft.f_91073_ != null && minecraft.f_91080_ == null && minecraft.f_91074_ != null;
    }

    private static void openWorldWindow() {
        if (toolDocument != null && !toolDocument.isDisposed() && !ResourceManager.toolDocument.inWorld) {
            toolDocument.remove();
            toolDocument = null;
        }
        if (worldWindow == null || ResourceManager.worldWindow.document == null || ResourceManager.worldWindow.document.isDisposed()) {
            Vec3 cameraPosition = AuiServices.client().getCameraPosition();
            Vector3f lookVector = AuiServices.client().getCameraLookVector();
            Vec3 look = new Vec3((double)lookVector.x, (double)lookVector.y, (double)lookVector.z).m_82541_();
            Vec3 position = cameraPosition.m_82549_(look.m_82490_(3.0));
            Vec3 toCamera = cameraPosition.m_82546_(position);
            double horizontal = Math.sqrt(toCamera.f_82479_ * toCamera.f_82479_ + toCamera.f_82481_ * toCamera.f_82481_);
            float yaw = (float)(Math.toDegrees(Math.atan2(toCamera.f_82481_, toCamera.f_82479_)) + 90.0);
            float pitch = (float)(-Math.toDegrees(Math.atan2(toCamera.f_82480_, horizontal)));
            worldWindow = new WorldWindow(PATH, position, 16, yaw, pitch);
            WorldWindow.addWindow(worldWindow);
        }
        if ((toolDocument = ResourceManager.worldWindow.document) != null) {
            toolDocument.setReloadPersistent(true);
            ResourceManager.refresh();
        }
    }

    private static void closeWorldWindow() {
        if (worldWindow != null) {
            WorldWindow.removeWindow(worldWindow);
            worldWindow = null;
        }
        if (toolDocument != null && toolDocument.isDisposed()) {
            toolDocument = null;
        }
    }

    public static void close() {
        ContextMenu.closeActive();
        createDialog.close();
        previewDialog.close();
        metaDialog.close();
        referenceDialog.close();
        if (worldWindow != null) {
            ResourceManager.closeWorldWindow();
        }
        if (toolDocument != null && !toolDocument.isDisposed()) {
            toolDocument.remove();
        }
        toolDocument = null;
        ResourceManager.resetNavigation();
    }

    public static void refresh() {
        if (!ResourceManager.isOpen()) {
            ArrayList<Document> existing = Document.get(PATH);
            if (existing.isEmpty()) {
                return;
            }
            toolDocument = (Document)existing.get(existing.size() - 1);
            toolDocument.setReloadPersistent(true);
        }
        ResourceManager.render(ClientLoader.listFinalStaticResources());
    }

    private static void render(List<Loader.StaticResourceEntry> entries) {
        if (toolDocument == null || ResourceManager.toolDocument.body == null) {
            return;
        }
        root = ResourceManager.buildTree(entries);
        if (ResourceManager.findFolder(currentPath) == null) {
            currentPath = ROOT_PATH;
            selectedItem = null;
            ResourceManager.resetHistory(ROOT_PATH);
        } else if (selectedItem != null && !selectedItem.existsIn(root)) {
            selectedItem = null;
        }
        ResourceManager.bindShellActions();
        ResourceManager.renderNavigation();
        ResourceManager.renderTree();
        ResourceManager.renderFiles(true);
        ResourceManager.renderDetail();
        ResourceManager.markDirty();
    }

    private static void bindShellActions() {
        ResourceManager.bindOnce("#backButton", event -> ResourceManager.goBack());
        ResourceManager.bindOnce("#upButton", event -> ResourceManager.goUp());
        ResourceManager.bindOnce("#newButton", event -> createDialog.open(toolDocument, currentPath, ClientLoader::reload));
        ResourceManager.bindOnce(".content", "contextmenu", ResourceManager::showEmptyContextMenu);
    }

    private static void bindOnce(String selector, Consumer<Event> listener) {
        ResourceManager.bindOnce(selector, "click", listener);
    }

    private static void bindOnce(String selector, String eventType, Consumer<Event> listener) {
        Object marker;
        Element element = toolDocument.querySelector(selector);
        String normalizedEvent = ResourceManager.safe(eventType).isBlank() ? "click" : eventType.trim().toLowerCase(Locale.ROOT);
        Object object = marker = "click".equals(normalizedEvent) ? "data-java-bound" : "data-java-bound-" + normalizedEvent;
        if (element == null || "1".equals(element.getAttribute((String)marker))) {
            return;
        }
        element.setAttribute((String)marker, "1");
        element.addEventListener(normalizedEvent, listener);
    }

    private static void goBack() {
        if (historyIndex <= 0) {
            return;
        }
        ResourceManager.navigate(history.get(--historyIndex), false);
    }

    private static void goUp() {
        if (currentPath.isBlank()) {
            return;
        }
        ResourceManager.navigate(ResourceManager.parentPath(currentPath), true);
    }

    private static void navigate(String path, boolean recordHistory) {
        String normalized = ResourceManager.normalizePath(path);
        if (ResourceManager.findFolder(normalized) == null) {
            return;
        }
        if (recordHistory && !normalized.equals(currentPath)) {
            while (history.size() > historyIndex + 1) {
                history.remove(history.size() - 1);
            }
            history.add(normalized);
            historyIndex = history.size() - 1;
        }
        currentPath = normalized;
        selectedItem = null;
        ResourceManager.expandAncestorsLocally(normalized);
        ResourceManager.renderNavigation();
        ResourceManager.refreshSelectionClasses();
        ResourceManager.renderFiles(true);
        ResourceManager.renderDetail();
        ResourceManager.markNavigationDirty();
    }

    private static void select(SelectedItem item) {
        selectedItem = item;
        ResourceManager.refreshSelectionClasses();
        ResourceManager.renderDetail();
        ResourceManager.markSelectionDirty();
    }

    private static void refreshSelectionClasses() {
        if (toolDocument == null) {
            return;
        }
        for (Element element : toolDocument.querySelectorAll(".file-card")) {
            ResourceManager.updateSelectedClass(element, selectedItem != null && ResourceManager.selectedItem.path.equals(ResourceManager.normalizePath(element.getAttribute("data-path"))));
        }
        for (Element element : toolDocument.querySelectorAll(".tree-item")) {
            String path = ResourceManager.normalizePath(element.getAttribute("data-path"));
            boolean selected = selectedItem != null ? ResourceManager.selectedItem.path.equals(path) : currentPath.equals(path);
            ResourceManager.updateSelectedClass(element, selected);
        }
    }

    private static void updateSelectedClass(Element element, boolean selected) {
        String updated;
        if (element == null) {
            return;
        }
        String classes = ResourceManager.safe(element.getAttribute("class"));
        ArrayList<String> tokens = new ArrayList<String>();
        for (String token : classes.split("\\s+")) {
            if (token.isBlank() || "selected".equals(token)) continue;
            tokens.add(token);
        }
        if (selected) {
            tokens.add("selected");
        }
        if (!(updated = String.join((CharSequence)" ", tokens)).equals(classes)) {
            element.setAttribute("class", updated);
        }
    }

    private static void markSelectionDirty() {
        if (toolDocument == null) {
            return;
        }
        int repaint = 3;
        for (Element element : toolDocument.querySelectorAll(".file-card")) {
            toolDocument.markDirty(element, repaint);
        }
        for (Element element : toolDocument.querySelectorAll(".tree-item")) {
            toolDocument.markDirty(element, repaint);
        }
        Element detail = toolDocument.querySelector("#detailContent");
        if (detail != null) {
            toolDocument.markDirty(detail, 7);
        }
    }

    private static void markNavigationDirty() {
        if (toolDocument == null) {
            return;
        }
        int mask = 7;
        for (String selector : List.of("#navPath", "#fileGrid", "#contentTitle", "#contentCount", "#detailContent")) {
            Element element = toolDocument.querySelector(selector);
            if (element == null) continue;
            toolDocument.markDirty(element, mask);
        }
        ResourceManager.markSelectionDirty();
    }

    private static void selectFromTree(Loader.StaticResourceEntry entry) {
        if (entry == null) {
            return;
        }
        String parent = ResourceManager.parentPath(entry.path());
        if (!parent.equals(currentPath)) {
            while (history.size() > historyIndex + 1) {
                history.remove(history.size() - 1);
            }
            history.add(parent);
            historyIndex = history.size() - 1;
            currentPath = parent;
            ResourceManager.expandAncestorsLocally(parent);
        }
        selectedItem = SelectedItem.file(entry);
        ResourceManager.renderNavigation();
        ResourceManager.refreshSelectionClasses();
        ResourceManager.renderFiles(false);
        ResourceManager.renderDetail();
        ResourceManager.markNavigationDirty();
    }

    private static void renderNavigation() {
        Element nav = toolDocument.querySelector("#navPath");
        if (nav == null) {
            return;
        }
        nav.clearChildren();
        Element rootLink = ResourceManager.textElement("SPAN", "ROOT");
        rootLink.addEventListener("click", event -> ResourceManager.navigate(ROOT_PATH, true));
        nav.append(rootLink);
        if (currentPath.isBlank()) {
            return;
        }
        StringBuilder path = new StringBuilder();
        String[] parts = currentPath.split("/");
        for (int i = 0; i < parts.length; ++i) {
            String part = parts[i];
            if (part.isBlank()) continue;
            if (!path.isEmpty()) {
                path.append('/');
            }
            path.append(part);
            String targetPath = path.toString();
            Element separator = ResourceManager.textElement("SPAN", "\u25b8");
            separator.setAttribute("class", "nav-sep");
            nav.append(separator);
            Element link = ResourceManager.textElement("SPAN", part.toUpperCase(Locale.ROOT));
            if (i == parts.length - 1) {
                link.setAttribute("class", "current");
            }
            link.addEventListener("click", event -> ResourceManager.navigate(targetPath, true));
            nav.append(link);
        }
    }

    private static void renderTree() {
        Element container = toolDocument.querySelector("#treeContainer");
        if (container == null) {
            return;
        }
        treeBranches.clear();
        container.clearChildren();
        DocumentFragment fragment = toolDocument.createDocumentFragment();
        ResourceManager.appendTreeChildren(fragment, root, 0, true);
        container.appendChild(fragment);
    }

    private static void appendTreeChildren(Node parent, FolderNode folder, int depth, boolean animate) {
        int index = 0;
        for (FolderNode child : folder.sortedFolders()) {
            boolean expanded = expandedPaths.contains(child.path);
            boolean hasChildren = !child.folders.isEmpty() || !child.files.isEmpty();
            Element item = ResourceManager.createElement("DIV", ResourceManager.treeItemClass(child.path.equals(currentPath), animate));
            item.setAttribute("style", "padding-left:" + (24 + depth * 16) + "px;animation-delay:" + (double)index * 0.04 + "s;");
            item.setAttribute("data-path", child.path);
            item.setAttribute("data-tree-folder", child.path);
            item.addEventListener("click", event -> ResourceManager.navigate(child.path, true));
            item.addEventListener("contextmenu", event -> ResourceManager.showContextMenu(event, SelectedItem.folder(child)));
            Element toggle = ResourceManager.textElement("DIV", "\u25be");
            toggle.setAttribute("class", hasChildren ? (expanded ? "tree-toggle" : "tree-toggle collapsed") : "tree-toggle empty");
            toggle.addEventListener("click", event -> {
                event.stopPropagation();
                ResourceManager.toggleTreeFolder(child, depth, hasChildren);
            });
            item.append(toggle);
            item.append(ResourceManager.iconElement("tree-icon", FOLDER_ICON));
            item.append(ResourceManager.textElement("SPAN", child.name.toUpperCase(Locale.ROOT)));
            parent.appendChild(item);
            if (hasChildren) {
                Element wrapper = ResourceManager.createElement("DIV", expanded ? "tree-children-wrapper expanded" : "tree-children-wrapper");
                wrapper.setAttribute("data-tree-children", child.path);
                Element inner = ResourceManager.createElement("DIV", "tree-children-inner");
                inner.setAttribute("data-tree-children-inner", child.path);
                if (expanded) {
                    ResourceManager.appendTreeChildren(inner, child, depth + 1, animate);
                }
                wrapper.append(inner);
                treeBranches.put(child.path, new TreeBranch(item, toggle, wrapper, inner));
                parent.appendChild(wrapper);
            }
            ++index;
        }
        for (Loader.StaticResourceEntry entry : folder.sortedFiles()) {
            Element item = ResourceManager.createElement("DIV", ResourceManager.treeItemClass(selectedItem != null && selectedItem.matches(entry), animate));
            item.setAttribute("style", "padding-left:" + (24 + depth * 16) + "px;animation-delay:" + (double)index * 0.04 + "s;");
            item.setAttribute("data-path", ResourceManager.safe(entry.path()));
            item.setAttribute("data-resource-key", ResourceManager.resourceKey(entry));
            item.addEventListener("click", event -> ResourceManager.selectFromTree(entry));
            item.addEventListener("contextmenu", event -> ResourceManager.showContextMenu(event, SelectedItem.file(entry)));
            item.append(ResourceManager.textElement("DIV", "\u25be", "tree-toggle empty"));
            item.append(ResourceManager.iconElement("tree-icon", ResourceManager.iconFor(entry)));
            item.append(ResourceManager.textElement("SPAN", ResourceManager.fileName(entry.path()).toUpperCase(Locale.ROOT)));
            parent.appendChild(item);
            ++index;
        }
    }

    private static String treeItemClass(boolean selected, boolean animate) {
        return "tree-item" + (selected ? " selected" : ROOT_PATH) + (animate ? " anim-in" : ROOT_PATH);
    }

    private static void toggleTreeFolder(FolderNode folder, int depth, boolean hasChildren) {
        if (folder == null || !hasChildren) {
            return;
        }
        if (expandedPaths.contains(folder.path)) {
            expandedPaths.remove(folder.path);
        } else {
            expandedPaths.add(folder.path);
        }
        ResourceManager.updateTreeExpansion(folder, depth, true);
    }

    private static void updateTreeExpansion(FolderNode folder, int depth, boolean hasChildren) {
        if (toolDocument == null || folder == null || !hasChildren) {
            return;
        }
        boolean expanded = expandedPaths.contains(folder.path);
        TreeBranch branch = treeBranches.get(folder.path);
        if (branch == null || !branch.isConnected()) {
            return;
        }
        Element item = branch.item();
        Element wrapper = branch.wrapper();
        Element inner = branch.inner();
        Element toggle = branch.toggle();
        if (toggle != null) {
            toggle.setAttribute("class", expanded ? "tree-toggle" : "tree-toggle collapsed");
        }
        wrapper.setAttribute("class", expanded ? "tree-children-wrapper expanded" : "tree-children-wrapper");
        ResourceManager.removeDescendantTreeBranches(folder.path);
        inner.clearChildren();
        if (expanded) {
            DocumentFragment fragment = toolDocument.createDocumentFragment();
            ResourceManager.appendTreeChildren(fragment, folder, depth + 1, ResourceManager.countTreeEntries(folder) <= 32);
            inner.appendChild(fragment);
        }
        toolDocument.markDirty(item, 7);
        toolDocument.markDirty(wrapper, 7);
    }

    private static int countTreeEntries(FolderNode folder) {
        if (folder == null) {
            return 0;
        }
        int total = folder.files.size();
        for (FolderNode child : folder.folders.values()) {
            if ((total += 1 + ResourceManager.countTreeEntries(child)) <= 32) continue;
            return total;
        }
        return total;
    }

    private static void removeDescendantTreeBranches(String path) {
        String prefix = ResourceManager.safe(path) + "/";
        treeBranches.keySet().removeIf(candidate -> candidate.startsWith(prefix));
    }

    private static void renderFiles(boolean animate) {
        Element card;
        Element grid = toolDocument.querySelector("#fileGrid");
        Element title = toolDocument.querySelector("#contentTitle");
        Element count = toolDocument.querySelector("#contentCount");
        if (grid == null || title == null || count == null) {
            return;
        }
        FolderNode folder = ResourceManager.findFolder(currentPath);
        grid.clearChildren();
        if (folder == null) {
            return;
        }
        List<FolderNode> folders = folder.sortedFolders();
        List<Loader.StaticResourceEntry> files = folder.sortedFiles();
        int total = folders.size() + files.size();
        title.setTextContent(folder.name.toUpperCase(Locale.ROOT));
        count.setTextContent(total + (total == 1 ? " ITEM" : " ITEMS"));
        if (total == 0) {
            Element empty = ResourceManager.textElement("DIV", "EMPTY");
            empty.setAttribute("style", "color:var(--gray);text-align:center;padding:40px;");
            grid.append(empty);
            return;
        }
        int index = 0;
        for (FolderNode child : folders) {
            SelectedItem folderItem = SelectedItem.folder(child);
            card = ResourceManager.fileCard(child.name, "--", FOLDER_ICON, folderItem, animate, index++);
            card.setAttribute("data-path", child.path);
            card.addEventListener("dblclick", event -> ResourceManager.navigate(child.path, true));
            grid.append(card);
        }
        for (Loader.StaticResourceEntry entry : files) {
            SelectedItem fileItem = SelectedItem.file(entry);
            card = ResourceManager.fileCard(ResourceManager.fileName(entry.path()), ResourceManager.formatSize(entry.sizeBytes()), ResourceManager.iconFor(entry), entry, fileItem, animate, index++);
            card.setAttribute("data-path", ResourceManager.safe(entry.path()));
            card.setAttribute("data-resource-key", ResourceManager.resourceKey(entry));
            if (ResourceManager.isPreviewable(entry) && !PATH.equals(ResourceManager.safe(entry.path()))) {
                card.addEventListener("dblclick", event -> ResourceManager.openPreview(entry));
            }
            grid.append(card);
        }
    }

    private static Element fileCard(String name, String meta, String icon, SelectedItem item, boolean animate, int index) {
        return ResourceManager.fileCard(name, meta, icon, null, item, animate, index);
    }

    private static Element fileCard(String name, String meta, String icon, Loader.StaticResourceEntry entry, SelectedItem item, boolean animate, int index) {
        boolean selected = selectedItem != null && ResourceManager.selectedItem.key.equals(item.key);
        String classes = "file-card" + (selected ? " selected" : ROOT_PATH) + (animate ? " entering" : ROOT_PATH);
        Element card = ResourceManager.createElement("DIV", classes);
        if (animate) {
            card.setAttribute("style", "animation-delay:" + (double)index * 0.05 + "s;");
        }
        card.addEventListener("click", event -> ResourceManager.select(item));
        card.addEventListener("contextmenu", event -> ResourceManager.showContextMenu(event, item));
        card.append(ResourceManager.fileIcon(entry, icon));
        card.append(ResourceManager.textElement("DIV", name.toUpperCase(Locale.ROOT), "file-name"));
        card.append(ResourceManager.textElement("DIV", meta, "file-meta"));
        return card;
    }

    private static Element fileIcon(Loader.StaticResourceEntry entry, String fallbackIcon) {
        Element icon = ResourceManager.createElement("DIV", "file-icon");
        if (ResourceManager.isImagePreviewable(entry)) {
            Element thumbnail = ResourceManager.createElement("IMG", "file-thumbnail");
            thumbnail.setAttribute("src", "/" + ResourceManager.safe(entry.path()));
            thumbnail.setAttribute("alt", ResourceManager.fileName(entry.path()));
            thumbnail.setAttribute("style", "width:48px;height:48px;object-fit:contain;");
            icon.append(thumbnail);
        } else if (ResourceFontAsset.isFont(entry)) {
            ResourceFontAsset.ensureLoaded(entry);
            Element glyph = ResourceManager.textElement("DIV", "Aa", "file-font-glyph");
            glyph.setAttribute("style", "font-family:'" + ResourceFontAsset.familyName(entry) + "',sans-serif;");
            icon.append(glyph);
        } else {
            icon.setInnerHTML(fallbackIcon);
        }
        return icon;
    }

    private static void renderDetail() {
        Element panel = toolDocument.querySelector("#detailPanel");
        Element content = toolDocument.querySelector("#detailContent");
        if (panel == null || content == null) {
            return;
        }
        content.clearChildren();
        if (selectedItem == null) {
            panel.setAttribute("class", "detail-panel");
            content.append(ResourceManager.textElement("DIV", "SELECT FILE TO VIEW DETAILS", "detail-empty"));
            return;
        }
        panel.setAttribute("class", "detail-panel active");
        Element detail = ResourceManager.createElement("DIV", "detail-content");
        Element icon = ResourceManager.createElement("DIV", "detail-icon");
        Loader.StaticResourceEntry entry = ResourceManager.selectedItem.entry;
        icon.setInnerHTML(ResourceManager.selectedItem.folder != null ? FOLDER_ICON : ResourceManager.iconFor(entry));
        detail.append(icon);
        detail.append(ResourceManager.textElement("DIV", ResourceManager.selectedItem.name.toUpperCase(Locale.ROOT), "detail-name"));
        if (ResourceManager.selectedItem.folder != null) {
            detail.append(ResourceManager.detailRow("TYPE", "FOLDER"));
            detail.append(ResourceManager.detailRow("SIZE", "--"));
            detail.append(ResourceManager.detailRow("LAYER", "--"));
            detail.append(ResourceManager.detailRow("PATH", ResourceManager.displayPath(ResourceManager.selectedItem.path)));
        } else {
            String type = ResourceManager.safe(entry.extension()).isBlank() ? "FILE" : entry.extension().toUpperCase(Locale.ROOT);
            detail.append(ResourceManager.detailRow("TYPE", type));
            detail.append(ResourceManager.detailRow("SIZE", ResourceManager.formatSize(entry.sizeBytes())));
            detail.append(ResourceManager.detailRow("LAYER", ResourceManager.layerLabel(entry.layer())));
            detail.append(ResourceManager.detailRow("PATH", ResourceManager.safe(entry.path())));
        }
        content.append(detail);
    }

    private static Element detailRow(String label, String value) {
        Element row = ResourceManager.createElement("DIV", "detail-row");
        row.append(ResourceManager.textElement("SPAN", label, "detail-label"));
        row.append(ResourceManager.textElement("SPAN", value, "detail-value"));
        return row;
    }

    private static void showContextMenu(Event event, SelectedItem item) {
        if (item == null || toolDocument == null) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        ResourceManager.select(item);
        ArrayList<ContextMenu.Item> items = new ArrayList<ContextMenu.Item>();
        items.add(ContextMenu.Item.header(item.name));
        if (item.folder != null) {
            items.add(ContextMenu.Item.action("OPEN", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M2 3h4l1.5 1.5H12v7H2V3zm1 1v6h8V5H7l-1.5-1.5H3z\"/></svg>", () -> ResourceManager.navigate(item.path, true)));
            items.add(ContextMenu.Item.separator());
            items.add(ContextMenu.Item.action("NEW FILE HERE", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M3 1h5l3 3v8H3V1zm5 1v3h3M7 6v2H5v1h2v2h1V9h2V8H8V6H7z\"/></svg>", () -> createDialog.open(toolDocument, item.path, ClientLoader::reload)));
        } else if (item.entry != null) {
            boolean previewable = ResourceManager.isPreviewable(item.entry) && !PATH.equals(ResourceManager.safe(item.entry.path()));
            ContextMenu.Item preview = ContextMenu.Item.action("PREVIEW", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M2 3h4l1.5 1.5H12v7H2V3zm1 1v6h8V5H7l-1.5-1.5H3z\"/></svg>", "DBL-CLK", () -> ResourceManager.openPreview(item.entry));
            items.add(previewable ? preview : preview.disabled());
            if (ResourceReferenceDialog.supports(item.entry)) {
                items.add(ContextMenu.Item.action("REFERENCE", "<svg viewBox=\"0 0 14 14\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"><path d=\"M5.5 8.5l3-3\"/><path d=\"M4.5 10.5H3a2.5 2.5 0 010-5h2M9.5 3.5H11a2.5 2.5 0 010 5H9\"/></svg>", () -> referenceDialog.open(toolDocument, item.entry)));
            }
            if ("html".equalsIgnoreCase(ResourceManager.safe(item.entry.extension()))) {
                Path localPath = ResourceManager.resolveLocalPath(item.entry);
                ContextMenu.Item editMeta = ContextMenu.Item.action("EDIT META", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M10 1l3 3-8 8H2v-3l8-8zm-1.5 3L4 8.5V10h1.5L10 5.5 8.5 4z\"/></svg>", () -> ResourceManager.openMetaEditor(item.entry));
                items.add(localPath != null && Files.isRegularFile(localPath, new LinkOption[0]) ? editMeta : editMeta.disabled());
            }
        }
        items.add(ContextMenu.Item.separator());
        items.add(ContextMenu.Item.action("COPY PATH", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><rect x=\"4\" y=\"4\" width=\"8\" height=\"8\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M2 10V3h6v1H3v6H2z\"/></svg>", "CTRL+C", () -> {
            Operation.setClipboardText(item.path);
            ToastManager.show("Path copied");
        }));
        if (item.entry != null) {
            Loader.StaticResourceEntry entry = item.entry;
            String source = ResourceManager.resolveSourceForCopy(entry);
            if (!source.isBlank() || ResourceManager.resolveLocalPath(entry) != null) {
                items.add(ContextMenu.Item.separator());
            }
            if (!source.isBlank()) {
                items.add(ContextMenu.Item.action("COPY SOURCE", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><rect x=\"4\" y=\"4\" width=\"8\" height=\"8\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M2 10V3h6v1H3v6H2z\"/></svg>", () -> {
                    Operation.setClipboardText(source);
                    ToastManager.show("Source copied");
                }));
            }
            if (ResourceManager.resolveLocalPath(entry) != null) {
                items.add(ContextMenu.Item.action("OPEN FOLDER", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M2 3h4l1.5 1.5H12v7H2V3zm1 1v6h8V5H7l-1.5-1.5H3z\"/></svg>", () -> ResourceManager.browseLocalFile(entry)));
            }
        }
        items.add(ContextMenu.Item.separator());
        items.add(ContextMenu.Item.action("PROPERTIES", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><circle cx=\"7\" cy=\"7\" r=\"5\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M7 4v3.5M7 9v.5\" stroke=\"currentColor\" stroke-width=\"1.5\" stroke-linecap=\"round\"/></svg>", "ALT+ENTER", () -> ResourceManager.select(item)));
        ContextMenu.show(toolDocument, ResourceManager.mousePosition(event), items);
    }

    private static void showEmptyContextMenu(Event event) {
        if (toolDocument == null) {
            return;
        }
        event.preventDefault();
        event.stopPropagation();
        FolderNode folder = ResourceManager.findFolder(currentPath);
        String title = folder == null ? "DIRECTORY" : folder.name;
        ContextMenu.Item goUpItem = ContextMenu.Item.action("GO UP", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M2 9l5-5 5 5H2z\"/></svg>", ResourceManager::goUp);
        List<ContextMenu.Item> items = List.of(ContextMenu.Item.header(title), ContextMenu.Item.action("NEW FILE", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M3 1h5l3 3v8H3V1zm5 1v3h3M7 6v2H5v1h2v2h1V9h2V8H8V6H7z\"/></svg>", () -> createDialog.open(toolDocument, currentPath, ClientLoader::reload)), ContextMenu.Item.separator(), currentPath.isBlank() ? goUpItem.disabled() : goUpItem, ContextMenu.Item.action("REFRESH", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M12 7a5 5 0 1 1-1.5-3.5L12 2v3.5H8.5l1.5-1.5A3.5 3.5 0 1 0 10.5 7H12z\"/></svg>", "F5", ResourceManager::refresh));
        ContextMenu.show(toolDocument, ResourceManager.mousePosition(event), items);
    }

    private static Position mousePosition(Event event) {
        Position position;
        if (event instanceof MouseEvent) {
            MouseEvent mouse = (MouseEvent)event;
            position = new Position(mouse.clientX, mouse.clientY);
        } else {
            position = Operation.getMousePositionDirectly();
        }
        return position;
    }

    private static void openPreview(Loader.StaticResourceEntry entry) {
        if (entry == null || toolDocument == null) {
            return;
        }
        previewDialog.open(toolDocument, entry);
    }

    private static void openMetaEditor(Loader.StaticResourceEntry entry) {
        if (entry == null || toolDocument == null) {
            return;
        }
        Path localPath = ResourceManager.resolveLocalPath(entry);
        if (localPath == null || !Files.isRegularFile(localPath, new LinkOption[0])) {
            ToastManager.show("HTML source is read-only");
            return;
        }
        metaDialog.open(toolDocument, entry.path(), localPath, ClientLoader::reload);
    }

    private static void browseLocalFile(Loader.StaticResourceEntry entry) {
        Path openTarget;
        Path localPath = ResourceManager.resolveLocalPath(entry);
        if (localPath == null || !Files.exists(localPath, new LinkOption[0])) {
            ToastManager.show("No local file source");
            return;
        }
        Path path = openTarget = Files.isDirectory(localPath, new LinkOption[0]) ? localPath : localPath.getParent();
        if (openTarget == null) {
            openTarget = localPath;
        }
        try {
            AuiServices.client().openFile(openTarget.toFile());
            ToastManager.show("Opened local folder");
        }
        catch (Exception ignored) {
            ToastManager.show("Failed to open folder");
        }
    }

    private static Path resolveLocalPath(Loader.StaticResourceEntry entry) {
        if (entry == null || entry.layer() == Loader.ResourceLayer.RESOURCE_PACK) {
            return null;
        }
        String sourceRoot = ResourceManager.safe(entry.sourceRoot());
        if (sourceRoot.isBlank()) {
            return null;
        }
        Path rootPath = Path.of(sourceRoot, new String[0]).toAbsolutePath().normalize();
        if (!Files.exists(rootPath, new LinkOption[0])) {
            return null;
        }
        Path resolved = rootPath;
        for (String part : ResourceManager.normalizePath(entry.path()).split("/")) {
            if (part.isBlank()) continue;
            resolved = resolved.resolve(part);
        }
        return (resolved = resolved.normalize()).startsWith(rootPath) ? resolved : null;
    }

    private static FolderNode buildTree(List<Loader.StaticResourceEntry> entries) {
        FolderNode treeRoot = new FolderNode("ROOT", ROOT_PATH);
        if (entries == null) {
            return treeRoot;
        }
        for (Loader.StaticResourceEntry entry : entries) {
            String path;
            if (entry == null || (path = ResourceManager.normalizePath(entry.path())).isBlank() || INTERNAL_IMAGE_PREVIEW_PATH.equals(path)) continue;
            String[] parts = path.split("/");
            FolderNode cursor = treeRoot;
            StringBuilder folderPath = new StringBuilder();
            for (int i = 0; i < parts.length - 1; ++i) {
                String name = parts[i];
                if (name.isBlank()) continue;
                if (!folderPath.isEmpty()) {
                    folderPath.append('/');
                }
                folderPath.append(name);
                String nextPath = folderPath.toString();
                cursor = cursor.folders.computeIfAbsent(name, ignored -> new FolderNode(name, nextPath));
            }
            cursor.files.add(entry);
        }
        return treeRoot;
    }

    private static FolderNode findFolder(String path) {
        String normalized = ResourceManager.normalizePath(path);
        if (normalized.isBlank()) {
            return root;
        }
        FolderNode cursor = root;
        for (String part : normalized.split("/")) {
            cursor = cursor.folders.get(part);
            if (cursor != null) continue;
            return null;
        }
        return cursor;
    }

    private static void expandAncestors(String path) {
        StringBuilder cursor = new StringBuilder();
        for (String part : ResourceManager.normalizePath(path).split("/")) {
            if (part.isBlank()) continue;
            if (!cursor.isEmpty()) {
                cursor.append('/');
            }
            cursor.append(part);
            expandedPaths.add(cursor.toString());
        }
    }

    private static void expandAncestorsLocally(String path) {
        LinkedHashSet<String> previous = new LinkedHashSet<String>(expandedPaths);
        ResourceManager.expandAncestors(path);
        for (String expandedPath : expandedPaths) {
            FolderNode folder;
            if (previous.contains(expandedPath) || (folder = ResourceManager.findFolder(expandedPath)) == null) continue;
            boolean hasChildren = !folder.folders.isEmpty() || !folder.files.isEmpty();
            ResourceManager.updateTreeExpansion(folder, ResourceManager.treeDepth(expandedPath), hasChildren);
        }
    }

    private static int treeDepth(String path) {
        String normalized = ResourceManager.normalizePath(path);
        return normalized.isBlank() ? 0 : Math.max(0, normalized.split("/").length - 1);
    }

    private static String iconFor(Loader.StaticResourceEntry entry) {
        if (entry == null) {
            return FILE_ICON;
        }
        String extension = ResourceManager.safe(entry.extension()).toLowerCase(Locale.ROOT);
        if (ResourceManager.isImagePreviewable(entry)) {
            return IMAGE_ICON;
        }
        if (extension.equals("lock")) {
            return LOCK_ICON;
        }
        if (extension.equals("zip") || extension.equals("jar") || extension.equals("rar") || extension.equals("7z")) {
            return ARCHIVE_ICON;
        }
        if (extension.equals("json") || extension.equals("toml") || extension.equals("properties") || extension.equals("cfg") || extension.equals("conf")) {
            return CONFIG_ICON;
        }
        return FILE_ICON;
    }

    private static boolean isImagePreviewable(Loader.StaticResourceEntry entry) {
        String extension = entry == null ? ROOT_PATH : ResourceManager.safe(entry.extension()).toLowerCase(Locale.ROOT);
        return extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("bmp") || extension.equals("gif") || extension.equals("webp");
    }

    private static boolean isHtmlPreviewable(Loader.StaticResourceEntry entry) {
        String extension = entry == null ? ROOT_PATH : ResourceManager.safe(entry.extension()).toLowerCase(Locale.ROOT);
        return extension.equals("html") || extension.equals("htm");
    }

    private static boolean isPreviewable(Loader.StaticResourceEntry entry) {
        return ResourceManager.isHtmlPreviewable(entry) || ResourceManager.isImagePreviewable(entry) || ResourceFontAsset.isFont(entry);
    }

    private static String resolveSourceForCopy(Loader.StaticResourceEntry entry) {
        if (entry == null) {
            return ROOT_PATH;
        }
        return ResourceManager.safe(entry.sourceDetail()).isBlank() ? ResourceManager.safe(entry.sourceRoot()) : ResourceManager.safe(entry.sourceDetail());
    }

    private static String layerLabel(Loader.ResourceLayer layer) {
        if (layer == null) {
            return "--";
        }
        return switch (layer) {
            default -> throw new IncompatibleClassChangeError();
            case Loader.ResourceLayer.RESOURCE_PACK -> "PACK";
            case Loader.ResourceLayer.LOCAL_FOLDER -> "LOCAL";
            case Loader.ResourceLayer.DEV_FOLDER -> "DEV";
        };
    }

    private static String formatSize(long bytes) {
        return ResourcePath.formatSize(bytes);
    }

    private static Element createElement(String tagName, String className) {
        Element element = Element.init(toolDocument.createElement(tagName));
        if (className != null && !className.isBlank()) {
            element.setAttribute("class", className);
        }
        return element;
    }

    private static Element textElement(String tagName, String text) {
        return ResourceManager.textElement(tagName, text, ROOT_PATH);
    }

    private static Element textElement(String tagName, String text, String className) {
        Element element = ResourceManager.createElement(tagName, className);
        element.innerText = ResourceManager.safe(text);
        return element;
    }

    private static Element iconElement(String className, String icon) {
        Element element = ResourceManager.createElement("DIV", className);
        element.setInnerHTML(icon);
        return element;
    }

    private static String resourceKey(Loader.StaticResourceEntry entry) {
        if (entry == null) {
            return ROOT_PATH;
        }
        return ResourceManager.safe(entry.path()) + "|" + (entry.layer() == null ? ROOT_PATH : entry.layer().name());
    }

    private static String normalizePath(String path) {
        return ResourcePath.normalize(path);
    }

    private static String parentPath(String path) {
        return ResourcePath.parent(path);
    }

    private static String fileName(String path) {
        return ResourcePath.fileName(path);
    }

    private static String displayPath(String path) {
        String normalized = ResourceManager.normalizePath(path);
        return normalized.isBlank() ? "/" : normalized;
    }

    private static String safe(String value) {
        return ResourcePath.safe(value);
    }

    private static void markDirty() {
        if (toolDocument == null || ResourceManager.toolDocument.body == null) {
            return;
        }
        toolDocument.markDirty(ResourceManager.toolDocument.body, 7);
    }

    private static void resetNavigation() {
        root = new FolderNode("ROOT", ROOT_PATH);
        currentPath = ROOT_PATH;
        selectedItem = null;
        expandedPaths.clear();
        ResourceManager.resetHistory(ROOT_PATH);
    }

    private static void resetHistory(String path) {
        history.clear();
        history.add(path);
        historyIndex = 0;
    }

    static {
        root = new FolderNode("ROOT", ROOT_PATH);
        currentPath = ROOT_PATH;
        history = new ArrayList<String>(List.of(ROOT_PATH));
        expandedPaths = new LinkedHashSet<String>();
        treeBranches = new LinkedHashMap<String, TreeBranch>();
        createDialog = new ResourceCreateDialog();
        previewDialog = new ResourcePreviewDialog();
        metaDialog = new ResourceMetaDialog();
        referenceDialog = new ResourceReferenceDialog();
    }

    private static final class FolderNode {
        private final String name;
        private final String path;
        private final Map<String, FolderNode> folders = new LinkedHashMap<String, FolderNode>();
        private final List<Loader.StaticResourceEntry> files = new ArrayList<Loader.StaticResourceEntry>();

        private FolderNode(String name, String path) {
            this.name = ResourceManager.safe(name);
            this.path = ResourceManager.normalizePath(path);
        }

        private List<FolderNode> sortedFolders() {
            return this.folders.values().stream().sorted(Comparator.comparing(folder -> folder.name.toLowerCase(Locale.ROOT))).toList();
        }

        private List<Loader.StaticResourceEntry> sortedFiles() {
            return this.files.stream().sorted(Comparator.comparing(entry -> ResourceManager.fileName(entry.path()).toLowerCase(Locale.ROOT))).toList();
        }
    }

    private static final class SelectedItem {
        private final String key;
        private final String name;
        private final String path;
        private final FolderNode folder;
        private final Loader.StaticResourceEntry entry;

        private SelectedItem(String key, String name, String path, FolderNode folder, Loader.StaticResourceEntry entry) {
            this.key = key;
            this.name = name;
            this.path = ResourceManager.normalizePath(path);
            this.folder = folder;
            this.entry = entry;
        }

        private static SelectedItem folder(FolderNode folder) {
            return new SelectedItem("folder|" + folder.path, folder.name, folder.path, folder, null);
        }

        private static SelectedItem file(Loader.StaticResourceEntry entry) {
            return new SelectedItem("file|" + ResourceManager.resourceKey(entry), ResourceManager.fileName(entry.path()), entry.path(), null, entry);
        }

        private boolean matches(Loader.StaticResourceEntry other) {
            return this.entry != null && ResourceManager.resourceKey(this.entry).equals(ResourceManager.resourceKey(other));
        }

        private boolean existsIn(FolderNode tree) {
            if (this.folder != null) {
                return ResourceManager.findFolder(this.path) != null;
            }
            if (this.entry == null) {
                return false;
            }
            return SelectedItem.findEntry(tree, ResourceManager.resourceKey(this.entry));
        }

        private static boolean findEntry(FolderNode folder, String key) {
            for (Loader.StaticResourceEntry entry : folder.files) {
                if (!ResourceManager.resourceKey(entry).equals(key)) continue;
                return true;
            }
            for (FolderNode child : folder.folders.values()) {
                if (!SelectedItem.findEntry(child, key)) continue;
                return true;
            }
            return false;
        }
    }

    private record TreeBranch(Element item, Element toggle, Element wrapper, Element inner) {
        private boolean isConnected() {
            return this.item != null && this.wrapper != null && this.inner != null && this.item.isConnected() && this.wrapper.isConnected() && this.inner.isConnected();
        }
    }
}

