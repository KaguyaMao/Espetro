/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.sighs.apricityui.init;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.behavior.DocumentSelection;
import com.sighs.apricityui.behavior.FocusRing;
import com.sighs.apricityui.behavior.MotionTrack;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.richtext.RangeBridge;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.behavior.richtext.TreeWalkerBridge;
import com.sighs.apricityui.canvas.CanvasPath2D;
import com.sighs.apricityui.canvas.DOMMatrix;
import com.sighs.apricityui.dom.CommentNode;
import com.sighs.apricityui.dom.DocumentFragment;
import com.sighs.apricityui.dom.DocumentRegistry;
import com.sighs.apricityui.dom.ElementTree;
import com.sighs.apricityui.dom.MutationObserverManager;
import com.sighs.apricityui.dom.RenderElement;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.element.Body;
import com.sighs.apricityui.element.Head;
import com.sighs.apricityui.element.Html;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.HTML;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.render.RenderQueue;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.style.StyleAsyncHandler;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.StyleFrameCache;
import com.sighs.apricityui.style.StyleScope;
import com.sighs.apricityui.util.BrowserLocation;
import com.sighs.apricityui.viewport.ApricityViewport;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public class Document {
    private static final String MOUSE_EVENTS_META_NAME = "aui-mouse-events";
    private static final long SLOW_REFRESH_LOG_THRESHOLD_NS = 50000000L;
    private static final double CLICK_PRESS_SLOP_PX = 4.0;
    private static final double TEXT_DRAG_SLOP_PX = 4.0;
    private final ElementTree tree = new ElementTree(this);
    private final RenderQueue render = new RenderQueue(this);
    private final String path;
    public final Map<String, Map<String, CSS.Declaration>> CSSCache = new LinkedHashMap<String, Map<String, CSS.Declaration>>();
    public final List<CSS.DebugRule> CSSDebugRules = new ArrayList<CSS.DebugRule>();
    public final List<String> JSCache = new ArrayList<String>();
    public Html documentElement;
    public Head head;
    public Body body;
    private final UUID uuid = UUID.randomUUID();
    public final boolean inWorld;
    private volatile boolean reloadPersistent = false;
    private volatile boolean interceptMouseEvents;
    private volatile boolean manuallyRendered = false;
    private volatile long refreshGeneration = 0L;
    private volatile long timedLayoutGeneration = -1L;
    private volatile LifecycleState lifecycleState = LifecycleState.LOADING;
    private volatile String readyState;
    private volatile FontMode fontMode;
    private volatile Element lastClickTarget;
    private volatile int lastClickButton;
    private volatile long lastClickTimeNs;
    private volatile int clickCount;
    private volatile double pressX;
    private volatile double pressY;
    private volatile double viewportScaleX;
    private volatile double viewportScaleY;
    private volatile double viewportOffsetX;
    private volatile double viewportOffsetY;
    private volatile long viewportVersion;
    private final ApricityViewport.State viewportState;
    private volatile ApricityViewport viewport;
    private final MutationObserverManager mutationManager;
    private final StyleScope style;
    private final MotionTrack motion;
    private final FocusRing focus;
    private final DocumentSelection documentSelection;
    private final RichTextSelection richTextSelection;
    private final IdentityHashMap<Element, SelectionCacheEntry> selectionCache;
    private List<Element> selectionUnitsCache;
    private final Set<Element> activeScrollElements;
    private final Set<Element> mutableInlineStyleElements;
    private final TextDragState textDrag;

    public Document(String path, boolean inWorld) {
        this.readyState = LifecycleState.LOADING.readyStateValue;
        this.fontMode = FontMode.WEB_SCALED;
        this.lastClickTarget = null;
        this.lastClickButton = -1;
        this.lastClickTimeNs = 0L;
        this.clickCount = 0;
        this.pressX = 0.0;
        this.pressY = 0.0;
        this.viewportScaleX = 1.0;
        this.viewportScaleY = 1.0;
        this.viewportOffsetX = 0.0;
        this.viewportOffsetY = 0.0;
        this.viewportVersion = 1L;
        this.viewport = new ApricityViewport(1, 1, 1.0f, 1.0);
        this.mutationManager = new MutationObserverManager(this);
        this.style = new StyleScope(this);
        this.motion = new MotionTrack(this);
        this.focus = new FocusRing(this);
        this.documentSelection = new DocumentSelection(this);
        this.richTextSelection = new RichTextSelection(this);
        this.selectionCache = new IdentityHashMap();
        this.selectionUnitsCache = null;
        this.activeScrollElements = ConcurrentHashMap.newKeySet();
        this.mutableInlineStyleElements = Collections.newSetFromMap(new WeakHashMap());
        this.textDrag = new TextDragState();
        this.path = path;
        this.inWorld = inWorld;
        this.viewportState = ApricityViewport.spec(path).createState(path);
        this.interceptMouseEvents = Document.parseMouseEventInterception(HTML.findMetaContent(path, MOUSE_EVENTS_META_NAME));
    }

    public boolean interceptsMouseEvents() {
        return this.interceptMouseEvents;
    }

    public boolean interceptsMouseEventsAt(Position screenPosition) {
        return this.interceptMouseEvents && this.hitTest(this.screenToDocumentPosition(screenPosition)) != null;
    }

    private static boolean parseMouseEventInterception(String raw) {
        if (raw == null || raw.isBlank()) {
            return false;
        }
        return switch (raw.trim().toLowerCase(Locale.ROOT)) {
            case "intercept", "block", "true", "yes", "on", "1" -> true;
            default -> false;
        };
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public FontMode getFontMode() {
        return this.fontMode;
    }

    public void setFontMode(FontMode fontMode) {
        this.fontMode = fontMode == null ? FontMode.WEB_SCALED : fontMode;
    }

    public void setViewportTransform(double scaleX, double scaleY, double offsetX, double offsetY) {
        this.viewportScaleX = scaleX > 0.0 && Double.isFinite(scaleX) ? scaleX : 1.0;
        this.viewportScaleY = scaleY > 0.0 && Double.isFinite(scaleY) ? scaleY : 1.0;
        this.viewportOffsetX = Double.isFinite(offsetX) ? offsetX : 0.0;
        this.viewportOffsetY = Double.isFinite(offsetY) ? offsetY : 0.0;
    }

    public Position screenToDocumentPosition(Position screenPosition) {
        if (screenPosition == null) {
            return Position.ZERO;
        }
        return new Position((screenPosition.x - this.viewportOffsetX) / this.viewportScaleX, (screenPosition.y - this.viewportOffsetY) / this.viewportScaleY);
    }

    public Position documentToScreenPosition(Position documentPosition) {
        if (documentPosition == null) {
            return Position.ZERO;
        }
        return new Position(documentPosition.x * this.viewportScaleX + this.viewportOffsetX, documentPosition.y * this.viewportScaleY + this.viewportOffsetY);
    }

    public double getViewportScaleX() {
        return this.viewportScaleX;
    }

    public double getViewportScaleY() {
        return this.viewportScaleY;
    }

    public ApricityViewport getViewport() {
        return this.viewport;
    }

    public boolean isManuallyRendered() {
        return this.manuallyRendered;
    }

    public void setManuallyRendered(boolean manuallyRendered) {
        this.manuallyRendered = manuallyRendered;
    }

    public long getViewportVersion() {
        return this.viewportVersion;
    }

    public void applyViewport(boolean relayout) {
        ApricityViewport previous = this.viewport;
        try {
            Minecraft minecraft = Minecraft.m_91087_();
            if (minecraft == null) {
                Size fallback = Size.getHeadlessWindowSize();
                this.viewport = this.viewportState.resolveHeadless((int)Math.round(fallback.width()), (int)Math.round(fallback.height()));
            } else {
                this.viewport = this.viewportState.resolve(minecraft.m_91268_());
            }
        }
        catch (NoClassDefFoundError unavailableClientRuntime) {
            if (!Document.isUnavailableClientRuntime(unavailableClientRuntime)) {
                throw unavailableClientRuntime;
            }
            Size fallback = Size.getHeadlessWindowSize();
            this.viewport = this.viewportState.resolveHeadless((int)Math.round(fallback.width()), (int)Math.round(fallback.height()));
        }
        this.setViewportTransform(this.viewport.renderScale(), this.viewport.renderScale(), 0.0, 0.0);
        if (!this.viewport.equals(previous)) {
            ++this.viewportVersion;
            StyleAsyncHandler.INSTANCE.handleViewportChange(this);
        }
        if (relayout) {
            this.markDirty(7);
        }
    }

    private static boolean isUnavailableClientRuntime(NoClassDefFoundError error) {
        String missing = error.getMessage();
        if (missing == null) {
            return false;
        }
        String className = missing.replace('.', '/');
        return className.startsWith("net/minecraft/client/") || className.equals("com/mojang/blaze3d/platform/Window");
    }

    public boolean handleViewportZoom(boolean zoomIn) {
        boolean changed;
        if (!this.viewportState.canUserScale()) {
            return false;
        }
        boolean bl = changed = zoomIn ? this.viewportState.zoomIn() : this.viewportState.zoomOut();
        if (!changed) {
            return false;
        }
        DocumentRegistry.applyViewportForPath(this.path, true);
        ApricityUI.LOGGER.info("[AUI Viewport] zoom path={} zoom={} viewport={}x{}", new Object[]{this.path, String.format(Locale.ROOT, "%.2f", this.viewport.zoom()), this.viewport.layoutWidth(), this.viewport.layoutHeight()});
        return true;
    }

    public boolean resetViewportZoom() {
        if (!this.viewportState.canUserScale()) {
            return false;
        }
        if (!this.viewportState.resetZoom()) {
            return false;
        }
        DocumentRegistry.applyViewportForPath(this.path, true);
        return true;
    }

    public boolean setViewportZoom(double zoom) {
        boolean changed = this.viewportState.setZoom(zoom);
        if (!changed) {
            return false;
        }
        if (this.inWorld) {
            this.applyViewport(true);
        } else {
            DocumentRegistry.applyViewportForPath(this.path, true);
        }
        ApricityUI.LOGGER.info("[AUI Viewport] editor zoom path={} zoom={} viewport={}x{}", new Object[]{this.path, String.format(Locale.ROOT, "%.2f", this.viewport.zoom()), this.viewport.layoutWidth(), this.viewport.layoutHeight()});
        return true;
    }

    public void refreshStyles() {
        String rawHtml = HTML.getTemple(this.path);
        if (rawHtml == null) {
            return;
        }
        CSS.Extractor cssExtractor = new CSS.Extractor(this.path);
        cssExtractor.handle(rawHtml);
        cssExtractor.pushToDocument(this);
        this.reapplyStylesFromCache();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void refresh() {
        long refreshStartNs = System.nanoTime();
        this.beginRefreshLifecycle();
        ApricityViewport.spec(this.path).createState(this.path);
        this.interceptMouseEvents = Document.parseMouseEventInterception(HTML.findMetaContent(this.path, MOUSE_EVENTS_META_NAME));
        this.applyViewport(false);
        ContextScope contextScope = Document.withContext(this);
        long resetEndNs = System.nanoTime();
        String stage = "reset";
        try {
            HTML.DocumentRoot root;
            FontMode sourceFontMode;
            block11: {
                Size.clearRootFontOverride();
                this.setFontMode(FontMode.WEB_SCALED);
                this.CSSCache.clear();
                this.CSSDebugRules.clear();
                this.JSCache.clear();
                this.mutableInlineStyleElements.clear();
                this.tree.clear();
                this.render.reset();
                this.motion.clear();
                this.invalidateSelectorIndex();
                sourceFontMode = FontMode.parse(HTML.findMetaContent(this.path, "aui-font-mode"));
                stage = "html/css/js extraction";
                root = HTML.create(this, this.path);
                if (root != null && root.body() != null) break block11;
                ApricityUI.LOGGER.error("[AUI Document] refresh produced no body path={} stage={}", (Object)this.path, (Object)stage);
                return;
            }
            try {
                if (this.body != null) {
                    root.body().setEventListeners(this.body.EventListener);
                }
                this.documentElement = root.documentElement();
                this.head = root.head();
                this.body = root.body();
                FontMode headFontMode = this.resolveFontModeFromHead(this.head);
                this.setFontMode(headFontMode == FontMode.WEB_SCALED ? sourceFontMode : headFontMode);
                this.rebuildElementIndexFromBody();
                long extractionEndNs = System.nanoTime();
                stage = "initial style calculation";
                if (this.documentElement != null) {
                    Size.setRootFontOverride(this.resolveRootFontSize());
                    this.clearRenderCaches(this.documentElement);
                    this.style.recomputeSubtree(this.documentElement);
                }
                long initialStyleEndNs = System.nanoTime();
                stage = "document expanders";
                AuiServices.expander().apply(this);
                long expandersEndNs = System.nanoTime();
                stage = "final style calculation";
                this.clearRenderCaches(this.documentElement);
                this.style.recomputeSubtree(this.documentElement);
                long finalStyleEndNs = System.nanoTime();
                this.tree.getElements().forEach(Element::clearDirtyFlags);
                this.render.reset();
                this.render.rebuildPaintList();
                ImageAsyncHandler.prefetchImages(this);
                this.enterInteractive();
                long paintPrefetchEndNs = System.nanoTime();
                stage = "global javascript";
                String globalJS = Loader.readGlobalJS();
                if (globalJS != null && !globalJS.isBlank()) {
                    AuiServices.script().evalGlobal(globalJS, this.uuid.toString());
                }
                stage = "document javascript";
                for (String js : this.JSCache) {
                    AuiServices.script().eval(js, null, this.path + "#script");
                }
                long scriptsEndNs = System.nanoTime();
                stage = "lifecycle events";
                this.fireLifecycleEvent("DOMContentLoaded", false);
                this.enterComplete();
                this.fireLifecycleEvent("load", false);
                long lifecycleEndNs = System.nanoTime();
                ApricityUI.LOGGER.info("[AUI Document] refresh complete path={} elements={} cssRules={} scripts={}", new Object[]{this.path, this.tree.getElements().size(), this.CSSCache.size(), this.JSCache.size()});
                this.logSlowRefreshTiming(refreshStartNs, resetEndNs, extractionEndNs, initialStyleEndNs, expandersEndNs, finalStyleEndNs, paintPrefetchEndNs, scriptsEndNs, lifecycleEndNs);
            }
            catch (Exception exception) {
                ApricityUI.LOGGER.error("[AUI Document] refresh failed path={} stage={}", new Object[]{this.path, stage, exception});
            }
        }
        finally {
            contextScope.close();
        }
    }

    private void logSlowRefreshTiming(long refreshStartNs, long resetEndNs, long extractionEndNs, long initialStyleEndNs, long expandersEndNs, long finalStyleEndNs, long paintPrefetchEndNs, long scriptsEndNs, long lifecycleEndNs) {
        long totalNs = lifecycleEndNs - refreshStartNs;
        if (totalNs < 50000000L) {
            return;
        }
        ApricityUI.LOGGER.info("[AUI Document] refresh timing path={} total={}ms resetViewport={}ms extraction={}ms initialStyle={}ms expanders={}ms finalStyle={}ms paintPrefetch={}ms scripts={}ms lifecycle={}ms", new Object[]{this.path, totalNs / 1000000L, (resetEndNs - refreshStartNs) / 1000000L, (extractionEndNs - resetEndNs) / 1000000L, (initialStyleEndNs - extractionEndNs) / 1000000L, (expandersEndNs - initialStyleEndNs) / 1000000L, (finalStyleEndNs - expandersEndNs) / 1000000L, (paintPrefetchEndNs - finalStyleEndNs) / 1000000L, (scriptsEndNs - paintPrefetchEndNs) / 1000000L, (lifecycleEndNs - scriptsEndNs) / 1000000L});
    }

    private double resolveRootFontSize() {
        Double parsed;
        CSS.Declaration declaredCss;
        double defaultFontSize = this.fontMode.defaultFontSize();
        if (this.documentElement == null) {
            return defaultFontSize;
        }
        this.documentElement.getComputedStyle();
        String declared = this.documentElement.getInlineStylePropertyValue("font-size");
        if (declared == null || declared.isBlank() || declared.equals("unset")) {
            declaredCss = (CSS.Declaration)this.documentElement.cssCache.get("font-size");
            String string = declared = declaredCss == null ? null : declaredCss.value();
        }
        if (declared == null || declared.equals("unset")) {
            declaredCss = (CSS.Declaration)this.documentElement.cssCache.get("fontSize");
            declared = declaredCss == null ? null : declaredCss.value();
        }
        return (parsed = Size.tryResolveLength(declared, defaultFontSize, defaultFontSize)) == null || parsed <= 0.0 ? defaultFontSize : parsed;
    }

    private FontMode resolveFontModeFromHead(Head head) {
        if (head == null) {
            return FontMode.WEB_SCALED;
        }
        ArrayDeque<Node> stack = new ArrayDeque<Node>(head.childNodes);
        while (!stack.isEmpty()) {
            Node node = (Node)stack.pop();
            if (!(node instanceof Element)) continue;
            Element element = (Element)node;
            if ("META".equals(element.tagName) && "aui-font-mode".equalsIgnoreCase(element.getAttribute("name"))) {
                return FontMode.parse(element.getAttribute("content"));
            }
            ArrayList children = element.childNodes;
            for (int i = children.size() - 1; i >= 0; --i) {
                stack.push((Node)children.get(i));
            }
        }
        return FontMode.WEB_SCALED;
    }

    private void clearRenderCaches(Element root) {
        if (root == null) {
            return;
        }
        ArrayDeque<Element> stack = new ArrayDeque<Element>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Element current = (Element)stack.pop();
            if (current == null) continue;
            RenderElement renderer = current.getRenderer();
            renderer.text.clear();
            renderer.wrappedText.clear();
            renderer.size.clear();
            renderer.box.clear();
            renderer.position.clear();
            ArrayList<Element> children = current.children;
            for (int i = children.size() - 1; i >= 0; --i) {
                Element child = (Element)children.get(i);
                if (child == null) continue;
                stack.push(child);
            }
        }
    }

    private void beginRefreshLifecycle() {
        ++this.refreshGeneration;
        this.lifecycleState = LifecycleState.LOADING;
        this.readyState = this.lifecycleState.readyStateValue;
        this.clearMutationObservers();
        this.bumpSelectionCache();
    }

    private void enterInteractive() {
        if (this.lifecycleState == LifecycleState.DISPOSED) {
            return;
        }
        this.lifecycleState = LifecycleState.INTERACTIVE;
        this.readyState = this.lifecycleState.readyStateValue;
    }

    private void enterComplete() {
        if (this.lifecycleState == LifecycleState.DISPOSED) {
            return;
        }
        this.lifecycleState = LifecycleState.COMPLETE;
        this.readyState = this.lifecycleState.readyStateValue;
    }

    public void disposeLifecycle() {
        if (this.lifecycleState == LifecycleState.DISPOSED) {
            return;
        }
        this.lifecycleState = LifecycleState.DISPOSED;
        this.clearMutationObservers();
        this.bumpSelectionCache();
        this.focus.clearFocus();
        this.focus.setPressedElement(null);
        this.focus.setPreviousCursorElement(null);
        this.documentSelection.clear();
        this.textDrag.clear();
        this.lastClickTarget = null;
        this.lastClickButton = -1;
        this.lastClickTimeNs = 0L;
        this.clickCount = 0;
        this.pressX = 0.0;
        this.pressY = 0.0;
    }

    private void fireLifecycleEvent(String type, boolean bubbles) {
        if (this.body == null || type == null || type.isBlank() || !this.isActive()) {
            return;
        }
        Event event = new Event(this.body, type, null, false);
        event.bubbles = bubbles;
        event.setTrusted(true);
        Event.triggerSingle(event);
    }

    private void rebuildElementIndexFromBody() {
        this.tree.rebuildFromRoot(this.documentElement);
    }

    public ArrayList<RenderNode> getPaintList() {
        return this.render.getPaintList();
    }

    public void markHitTestDirtyAll() {
        this.render.markHitTestDirty();
    }

    public Element hitTest(Position documentPosition) {
        if (!this.isActive()) {
            return null;
        }
        try (ContextScope ignored = Document.withContext(this);){
            if (this.render.hasPendingWork()) {
                this.render.commit();
            }
            Element element = this.render.hitTest(documentPosition);
            return element;
        }
    }

    public void updateElement(Element element) {
        this.tree.updateElement(element);
    }

    public Set<Element> getDirtyElements() {
        return this.render.getDirtyElements();
    }

    public void requestStyleRecalc(Element element) {
        if (element == null) {
            return;
        }
        if (element.document != this) {
            return;
        }
        this.style.requestRecalc(element);
    }

    public void requestPseudoStyleRecalc(Element element, String pseudoName) {
        if (element == null) {
            return;
        }
        if (element.document != this) {
            return;
        }
        this.style.requestPseudoRecalc(element, pseudoName);
    }

    public void flushPendingStyleUpdates() {
        this.style.flushPendingUpdates();
    }

    public void tickFrame() {
        if (!this.isActive()) {
            return;
        }
        try (ContextScope ignored = Document.withContext(this);){
            StyleFrameCache.begin();
            try {
                this.commitStyleRecalc();
                this.tickElements();
                this.commitStyleRecalc();
                this.flushMutationObservers();
                this.commitRenderState();
            }
            finally {
                StyleFrameCache.end();
            }
        }
    }

    public void commitStyleRecalc() {
        if (!this.isActive()) {
            return;
        }
        this.commitMutableInlineStyles();
        this.style.flushPendingUpdates();
    }

    public boolean commitPendingStyleRecalcForRender() {
        if (!this.isActive()) {
            return false;
        }
        this.commitMutableInlineStyles();
        return this.style.flushPendingUpdates();
    }

    void trackMutableInlineStyle(Element element) {
        if (element != null) {
            this.mutableInlineStyleElements.add(element);
        }
    }

    private void commitMutableInlineStyles() {
        for (Element element : new ArrayList<Element>(this.mutableInlineStyleElements)) {
            element.commitPendingInlineStyleMutations();
        }
    }

    public boolean stepMotionRender() {
        boolean requiresGeometryCommit = this.motion.stepRender();
        if (this.motion.hasVisualChanges()) {
            this.render.markVisualDirty();
        }
        return requiresGeometryCommit;
    }

    public void commitMotionHitTest() {
        this.render.updateHitTestSubtrees(this.motion.drainHitTestRoots());
    }

    public Set<Element> drainMotionLayoutRoots() {
        return this.motion.drainLayoutRoots();
    }

    public Set<Element> drainMotionGeometryRoots() {
        return this.motion.drainGeometryRoots();
    }

    public boolean stepScrollRender() {
        if (!this.isActive()) {
            return false;
        }
        if (this.activeScrollElements.isEmpty()) {
            return false;
        }
        boolean changed = false;
        for (Element element : new ArrayList<Element>(this.activeScrollElements)) {
            if (element == null || !element.isConnected()) {
                this.activeScrollElements.remove(element);
                continue;
            }
            boolean elementChanged = element.stepScrollRender();
            if (elementChanged) {
                element.getRenderer().invalidateScrollVersion();
                this.render.markHitTestDirty(element);
                changed = true;
            }
            if (element.needsScrollRenderStep()) continue;
            this.activeScrollElements.remove(element);
        }
        if (changed) {
            this.render.markVisualDirty();
        }
        return changed;
    }

    void registerActiveScroll(Element element) {
        if (element == null || !element.isConnected()) {
            return;
        }
        this.activeScrollElements.add(element);
    }

    public void tickElements() {
        if (!this.isActive()) {
            return;
        }
        this.render.tickElements();
    }

    public void commitRenderState() {
        if (!this.isActive()) {
            return;
        }
        this.render.commit();
    }

    public boolean commitRenderStateForMotion() {
        if (!this.isActive()) {
            return false;
        }
        return this.render.commit(false);
    }

    public boolean hasPendingRenderState() {
        return this.render.hasPendingWork();
    }

    public long getVisualVersion() {
        return this.render.getVisualVersion();
    }

    public boolean hasPendingVisualWork() {
        return this.render.hasPendingVisualWork();
    }

    public int getGlobalDirtyMask() {
        return this.render.getGlobalDirtyMask();
    }

    public void markDirty(int mask) {
        this.render.markDirty(mask);
    }

    public void markDirty(Element element, int mask) {
        this.render.markDirty(element, mask);
    }

    public void registerStylesheet(String css, String contextPath, int orderStart) {
        if (css == null || css.isBlank()) {
            return;
        }
        Size viewport = new Size(this.getViewport().layoutWidth(), this.getViewport().layoutHeight());
        CSS.readCSS(css, this.CSSCache, this.CSSDebugRules, contextPath, orderStart, viewport);
        this.rebuildSelectorIndex();
        this.reapplyStylesFromCache();
    }

    public void registerUaStylesheet(String css, String contextPath) {
        if (css == null || css.isBlank()) {
            return;
        }
        LinkedHashMap<String, Map<String, CSS.Declaration>> uaRules = new LinkedHashMap<String, Map<String, CSS.Declaration>>();
        Size viewport = new Size(this.getViewport().layoutWidth(), this.getViewport().layoutHeight());
        CSS.readCSS(css, uaRules, this.CSSDebugRules, contextPath, -1000, viewport);
        LinkedHashMap<String, Map<String, CSS.Declaration>> merged = new LinkedHashMap<String, Map<String, CSS.Declaration>>();
        merged.putAll(uaRules);
        merged.putAll(this.CSSCache);
        this.CSSCache.clear();
        this.CSSCache.putAll(merged);
        this.rebuildSelectorIndex();
        this.reapplyStylesFromCache();
    }

    public void reapplyStylesFromCache() {
        if (this.body == null) {
            return;
        }
        this.body.invalidateStyle();
        this.markDirty(this.body, 5);
    }

    public void invalidateFontMetrics() {
        if (this.documentElement == null) {
            return;
        }
        this.documentElement.getRenderer().invalidateLayoutSubtree();
        this.markDirty(this.documentElement, 5);
    }

    public void invalidateSelectorIndex() {
        this.style.invalidateSelectorIndex();
    }

    public void rebuildSelectorIndex() {
        this.style.rebuildSelectorIndex();
    }

    public Selector.Index getSelectorIndex() {
        return this.style.getSelectorIndex();
    }

    public ElementTree getTree() {
        return this.tree;
    }

    public boolean is(String path) {
        return this.path.equals(path);
    }

    public boolean is(UUID uuid) {
        return this.uuid.equals(uuid);
    }

    public String getPath() {
        return this.path;
    }

    public boolean isReloadPersistent() {
        return this.reloadPersistent;
    }

    public void setReloadPersistent(boolean reloadPersistent) {
        this.reloadPersistent = reloadPersistent;
    }

    public long getRefreshGeneration() {
        return this.refreshGeneration;
    }

    public boolean markFirstLayoutCommitForTiming() {
        long generation = this.refreshGeneration;
        if (this.timedLayoutGeneration == generation) {
            return false;
        }
        this.timedLayoutGeneration = generation;
        return true;
    }

    public boolean isDisposed() {
        return this.lifecycleState == LifecycleState.DISPOSED;
    }

    public boolean isActive() {
        return this.lifecycleState != LifecycleState.DISPOSED;
    }

    public boolean isCurrentGeneration(long generation) {
        return this.isActive() && this.refreshGeneration == generation;
    }

    public Element createHTML(String html) {
        return HTML.createElement(this, html);
    }

    public Element createElement(String tagName) {
        return new Element(this, tagName);
    }

    public TextNode createTextNode(String text) {
        return new TextNode(this, text);
    }

    public CommentNode createComment(String text) {
        return new CommentNode(this, text);
    }

    public DocumentFragment createDocumentFragment() {
        return new DocumentFragment(this);
    }

    public void createRelation(Node child, Node parent, boolean head) {
        this.tree.createRelation(child, parent, head);
    }

    public Node createRelationAndReturn(Node child, Node parent, boolean head) {
        this.tree.createRelation(child, parent, head);
        return child;
    }

    public List<Element> querySelectorAll(String selector) {
        return Selector.querySelectorAll(this.documentElement, selector);
    }

    public Element querySelector(String selector) {
        return Selector.querySelector(this.documentElement, selector);
    }

    public void recordID(Element element) {
        this.tree.recordId(element);
    }

    public void removeID(String id, Element element) {
        this.tree.removeId(id, element);
    }

    public Element getElementById(String id) {
        return this.tree.getElementById(id);
    }

    public Element getDocumentElement() {
        return this.documentElement;
    }

    public Element getHead() {
        return this.head;
    }

    public String getURL() {
        return this.path;
    }

    public String getDocumentURI() {
        return this.path;
    }

    public String getBaseURI() {
        return this.path;
    }

    public BrowserLocation getLocation() {
        return new BrowserLocation(this.path);
    }

    public String getReadyState() {
        return this.readyState;
    }

    public boolean hasFocus() {
        return this.getFocusedElement() != null;
    }

    public void blur() {
        this.clearFocus();
    }

    public Node appendChild(Node element) {
        if (this.body == null) {
            return null;
        }
        return this.body.appendChild(element);
    }

    public void scrollTo(double x, double y) {
        if (this.body == null) {
            return;
        }
        this.body.scrollTo(x, y);
    }

    public void scrollBy(double x, double y) {
        if (this.body == null) {
            return;
        }
        this.body.scrollBy(x, y);
    }

    public Node prepend(Node element) {
        if (this.body == null || element == null) {
            return null;
        }
        this.body.insertBefore(element, this.body.getFirstChild());
        return element;
    }

    public void addEventListener(String type, Consumer<Event> listener) {
        if (this.body == null) {
            return;
        }
        this.body.addEventListener(type, listener);
    }

    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        if (this.body == null) {
            return;
        }
        this.body.addEventListener(type, listener, useCapture);
    }

    public void addEventListener(String type, Consumer<Event> listener, boolean useCapture, boolean once) {
        if (this.body == null) {
            return;
        }
        this.body.addEventListener(type, listener, useCapture, once);
    }

    public void removeEventListener(String type, Consumer<Event> listener) {
        this.removeEventListener(type, listener, false);
    }

    public void removeEventListener(String type, Consumer<Event> listener, boolean useCapture) {
        if (this.body == null) {
            return;
        }
        this.body.removeEventListener(type, listener, useCapture);
    }

    public boolean dispatchEvent(Object event) {
        if (!(event instanceof Event)) {
            return false;
        }
        Event targetEvent = (Event)event;
        if (this.body == null) {
            return false;
        }
        if (targetEvent.target == null) {
            targetEvent.target = this.body;
        }
        if (targetEvent.currentTarget == null) {
            targetEvent.currentTarget = this.body;
        }
        Event.tiggerEvent(targetEvent);
        return !targetEvent.defaultPrevented;
    }

    public List<Element> getElementsByClassName(String className) {
        String normalized;
        if (this.body == null) {
            return List.of();
        }
        String string = normalized = className == null ? "" : className.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        String selector = "." + String.join((CharSequence)".", normalized.split("\\s+"));
        return Selector.querySelectorAll(this.body, selector);
    }

    public List<Element> getElementsByTagName(String tagName) {
        String normalized;
        if (this.body == null) {
            return List.of();
        }
        String string = normalized = tagName == null ? "" : tagName.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        return Selector.querySelectorAll(this.body, normalized);
    }

    public List<Element> getElementsByName(String name) {
        String normalized;
        if (this.body == null) {
            return List.of();
        }
        String string = normalized = name == null ? "" : name.trim();
        if (normalized.isEmpty()) {
            return List.of();
        }
        return Selector.querySelectorAll(this.body, "[name=\"" + normalized + "\"]");
    }

    public static void refreshAll() {
        DocumentRegistry.refreshAll();
    }

    public static Document create(String path) {
        return DocumentRegistry.create(path);
    }

    public static Document createInWorld(String path) {
        return DocumentRegistry.createInWorld(path);
    }

    public static ArrayList<Document> get(String path) {
        return DocumentRegistry.get(path);
    }

    public static Document getByUUID(String uuid) {
        return DocumentRegistry.getByUUID(uuid);
    }

    public static List<Document> getAll() {
        return DocumentRegistry.getAll();
    }

    public static Document getContextDocument() {
        return DocumentRegistry.getContext();
    }

    public static void runWithContext(Document document, Runnable runnable) {
        if (runnable == null) {
            return;
        }
        try (ContextScope ignored = Document.withContext(document);){
            runnable.run();
        }
    }

    public static ContextScope withContext(Document document) {
        Document previous = DocumentRegistry.getContext();
        DocumentRegistry.setContext(document);
        return new ContextScope(previous);
    }

    public ArrayList<Element> getElements() {
        return this.tree.getElements();
    }

    public ArrayList<Node> getNodes() {
        return this.tree.getNodes();
    }

    public static void remove(String path) {
        DocumentRegistry.remove(path);
    }

    public static void remove(UUID uuid) {
        DocumentRegistry.remove(uuid);
    }

    public void remove() {
        DocumentRegistry.remove(this.uuid);
    }

    public void removeNode(Node node) {
        this.tree.removeNode(node);
        if (node instanceof Element) {
            Element element = (Element)node;
            this.motion.removeElement(element);
        }
    }

    public void removeElement(Element element) {
        this.removeNode(element);
    }

    public MutationObserver createMutationObserver(Consumer<Object> callback) {
        return this.mutationManager.create(callback);
    }

    public CanvasPath2D createPath2D() {
        return new CanvasPath2D();
    }

    public CanvasPath2D createPath2D(Object source) {
        if (source instanceof CanvasPath2D) {
            CanvasPath2D path = (CanvasPath2D)source;
            return new CanvasPath2D(path);
        }
        if (source instanceof String) {
            String text = (String)source;
            return new CanvasPath2D(text);
        }
        return new CanvasPath2D();
    }

    public DOMMatrix createDOMMatrix() {
        return new DOMMatrix();
    }

    public DOMMatrix createDOMMatrix(Object source) {
        return new DOMMatrix(source);
    }

    public void queueMutation(MutationRecord record) {
        this.mutationManager.queue(record);
    }

    public void flushMutationObservers() {
        this.mutationManager.flush();
    }

    public void setTransitionActive(Element element, boolean active) {
        this.motion.setTransitionActive(element, active);
    }

    public void setHasAnimationSpec(Element element, boolean hasSpec) {
        this.motion.setHasAnimationSpec(element, hasSpec);
    }

    public Element getPreviousCursorElement() {
        return this.focus.getPreviousCursorElement();
    }

    public void setPreviousCursorElement(Element element) {
        this.focus.setPreviousCursorElement(element);
    }

    public Element getPressedElement() {
        return this.focus.getPressedElement();
    }

    public void setPressedElement(Element element) {
        this.focus.setPressedElement(element);
    }

    public int advanceClickSequence(Element target, int button, double x, double y, long nowNs, long thresholdNs) {
        boolean withinDistance;
        boolean sameTarget = target != null && target == this.lastClickTarget;
        boolean sameButton = this.lastClickButton == button;
        boolean withinWindow = this.lastClickTimeNs != 0L && nowNs - this.lastClickTimeNs <= thresholdNs;
        boolean bl = withinDistance = this.lastClickTimeNs != 0L && Math.abs(x - this.pressX) <= 4.0 && Math.abs(y - this.pressY) <= 4.0;
        if (!(sameTarget && sameButton && withinWindow && withinDistance)) {
            this.clickCount = 0;
        }
        ++this.clickCount;
        this.lastClickTarget = target;
        this.lastClickButton = button;
        this.lastClickTimeNs = nowNs;
        this.pressX = x;
        this.pressY = y;
        return this.clickCount;
    }

    public int getClickCount() {
        return this.clickCount;
    }

    public boolean registerClickAndCheckDoubleClick(Element target, int button, long nowNs, long thresholdNs) {
        if (target == null || target != this.lastClickTarget || button != this.lastClickButton) {
            return false;
        }
        return this.clickCount >= 2;
    }

    public Element getActiveElement() {
        Element focused = this.focus.getFocusedElement();
        if (focused != null) {
            return focused;
        }
        return this.body;
    }

    public Element getFocusedElement() {
        return this.focus.getFocusedElement();
    }

    public void setFocusedElement(Element element) {
        this.focus.setFocusedElement(element);
    }

    public boolean hasAnyTextSelection() {
        return this.focus.hasAnyTextSelection();
    }

    public void clearAllTextSelections() {
        this.focus.clearAllTextSelections();
    }

    public void clearAllTextSelectionsExcept(Element keep) {
        this.focus.clearAllTextSelectionsExcept(keep);
    }

    public DocumentSelection getDocumentSelection() {
        return this.documentSelection;
    }

    public RichTextSelection getRichTextSelection() {
        return this.richTextSelection;
    }

    public RangeBridge createRange() {
        return new RangeBridge();
    }

    public TreeWalkerBridge createTreeWalker(Element root, int whatToShow) {
        return new TreeWalkerBridge(root, whatToShow);
    }

    public int[] resolveUnitSelectionRange(Element unit) {
        if (unit == null) {
            return null;
        }
        if (this.documentSelection.isActive()) {
            return this.documentSelection.localRangeForUnit(unit);
        }
        if (this.richTextSelection.isActive()) {
            return this.richTextSelection.localRangeForUnit(unit);
        }
        return null;
    }

    public boolean hasAnyActiveSelection() {
        return this.documentSelection.isActive() || this.richTextSelection.isActive();
    }

    public void clearRichTextSelection() {
        this.richTextSelection.clear();
    }

    public void dispatchSelectionChange() {
        if (this.body == null) {
            return;
        }
        Event event = new Event(this.body, "selectionchange", true);
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
    }

    public void bumpSelectionCache() {
        this.selectionCache.clear();
        this.selectionUnitsCache = null;
    }

    public String getCachedFlattened(Element element) {
        if (element == null) {
            return "";
        }
        SelectionCacheEntry entry = this.selectionCache.get(element);
        if (entry != null && entry.flattened != null) {
            return entry.flattened;
        }
        String value = SelectionUnits.computeFlattenedSelectableText(element);
        if (entry == null) {
            entry = new SelectionCacheEntry();
            this.selectionCache.put(element, entry);
        }
        entry.flattened = value;
        return value;
    }

    public SelectionUnits.RawText getCachedRaw(Element element) {
        if (element == null) {
            return null;
        }
        SelectionCacheEntry entry = this.selectionCache.get(element);
        if (entry != null && entry.raw != null) {
            return entry.raw;
        }
        SelectionUnits.RawText value = SelectionUnits.computeRawTextOf(element);
        if (entry == null) {
            entry = new SelectionCacheEntry();
            this.selectionCache.put(element, entry);
        }
        entry.raw = value;
        return value;
    }

    public boolean getCachedPaintsRuns(Element element) {
        if (element == null) {
            return false;
        }
        SelectionCacheEntry entry = this.selectionCache.get(element);
        if (entry != null && entry.paintsRuns != null) {
            return entry.paintsRuns;
        }
        boolean value = SelectionUnits.computePaintsTextViaRuns(element);
        if (entry == null) {
            entry = new SelectionCacheEntry();
            this.selectionCache.put(element, entry);
        }
        entry.paintsRuns = value;
        return value;
    }

    public List<Element> getCachedUnits() {
        if (this.selectionUnitsCache == null) {
            this.selectionUnitsCache = SelectionUnits.computeUnits(this);
        }
        return this.selectionUnitsCache;
    }

    public boolean hasDocumentSelection() {
        return this.documentSelection.isActive();
    }

    public void clearDocumentSelection() {
        this.documentSelection.clear();
    }

    public boolean selectAllDocumentText() {
        return this.documentSelection.selectAll(this);
    }

    public String getDocumentSelectedText() {
        return this.documentSelection.getSelectedText(this);
    }

    public boolean isTextDragPending() {
        return this.textDrag.text != null;
    }

    public boolean isTextDragging() {
        return this.textDrag.dragged;
    }

    public String getDraggedText() {
        return this.textDrag.text;
    }

    public void beginTextDrag(String text, double x, double y) {
        if (text == null || text.isEmpty()) {
            this.textDrag.clear();
            return;
        }
        this.textDrag.text = text;
        this.textDrag.startX = x;
        this.textDrag.startY = y;
        this.textDrag.dragged = false;
    }

    public void updateTextDrag(double x, double y) {
        if (this.textDrag.text == null || this.textDrag.dragged) {
            return;
        }
        if (Math.abs(x - this.textDrag.startX) > 4.0 || Math.abs(y - this.textDrag.startY) > 4.0) {
            this.textDrag.dragged = true;
            this.documentSelection.setSelecting(false);
        }
    }

    public void endTextDrag() {
        this.textDrag.clear();
    }

    public void clearFocus() {
        this.focus.clearFocus();
    }

    private void clearMutationObservers() {
        this.mutationManager.clearAll();
    }

    private static enum LifecycleState {
        LOADING("loading"),
        INTERACTIVE("interactive"),
        COMPLETE("complete"),
        DISPOSED("complete");

        private final String readyStateValue;

        private LifecycleState(String readyStateValue) {
            this.readyStateValue = readyStateValue;
        }
    }

    public static enum FontMode {
        MC("mc", 9.0, 9.0),
        WEB("web", 16.0, 9.0),
        WEB_SCALED("web-scaled", 16.0, 16.0);

        private final String value;
        private final double defaultFontSize;
        private final double defaultFontScaleBase;

        private FontMode(String value, double defaultFontSize, double defaultFontScaleBase) {
            this.value = value;
            this.defaultFontSize = defaultFontSize;
            this.defaultFontScaleBase = defaultFontScaleBase;
        }

        public String value() {
            return this.value;
        }

        public double defaultFontSize() {
            return this.defaultFontSize;
        }

        public double defaultFontScaleBase() {
            return this.defaultFontScaleBase;
        }

        public static FontMode parse(String raw) {
            if (raw == null) {
                return WEB_SCALED;
            }
            String normalized = raw.trim().toLowerCase(Locale.ROOT);
            for (FontMode mode : FontMode.values()) {
                if (!mode.value.equals(normalized)) continue;
                return mode;
            }
            return WEB_SCALED;
        }
    }

    private static final class TextDragState {
        private String text = null;
        private double startX = 0.0;
        private double startY = 0.0;
        private boolean dragged = false;

        private TextDragState() {
        }

        private void clear() {
            this.text = null;
            this.startX = 0.0;
            this.startY = 0.0;
            this.dragged = false;
        }
    }

    public static final class ContextScope
    implements AutoCloseable {
        private final Document previous;
        private boolean closed = false;

        private ContextScope(Document previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            DocumentRegistry.setContext(this.previous);
        }
    }

    public static final class MutationObserver {
        private final Document owner;
        private final Consumer<Object> callback;
        private final long ownerGeneration;
        private final CopyOnWriteArrayList<ObservedTarget> observed = new CopyOnWriteArrayList();
        private final ArrayList<MutationRecord> pending = new ArrayList();
        public volatile boolean disconnected = false;

        public MutationObserver(Document owner, Consumer<Object> callback) {
            this.owner = owner;
            this.callback = callback;
            this.ownerGeneration = owner == null ? -1L : owner.getRefreshGeneration();
        }

        public void observe(Node target, boolean childList, boolean attributes, boolean characterData, boolean subtree, boolean attributeOldValue, boolean characterDataOldValue, String attributeFilterCsv) {
            if (target == null || this.disconnected) {
                return;
            }
            this.observed.removeIf(entry -> entry.target == target);
            this.observed.add(new ObservedTarget(target, childList, attributes, characterData, subtree, attributeOldValue, characterDataOldValue, MutationObserver.parseAttributeFilter(attributeFilterCsv)));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void disconnect() {
            this.disconnected = true;
            this.observed.clear();
            ArrayList<MutationRecord> arrayList = this.pending;
            synchronized (arrayList) {
                this.pending.clear();
            }
            this.owner.mutationManager.remove(this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public List<MutationRecord> takeRecords() {
            ArrayList<MutationRecord> arrayList = this.pending;
            synchronized (arrayList) {
                ArrayList<MutationRecord> snapshot = new ArrayList<MutationRecord>(this.pending);
                this.pending.clear();
                return snapshot;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void enqueue(MutationRecord record) {
            if (this.disconnected || record == null || this.owner == null || !this.owner.isCurrentGeneration(this.ownerGeneration) || !this.matches(record)) {
                return;
            }
            ArrayList<MutationRecord> arrayList = this.pending;
            synchronized (arrayList) {
                this.pending.add(this.adapt(record));
            }
        }

        public void flush() {
            if (this.disconnected || this.callback == null || this.owner == null || !this.owner.isCurrentGeneration(this.ownerGeneration)) {
                return;
            }
            List<MutationRecord> snapshot = this.takeRecords();
            if (snapshot.isEmpty()) {
                return;
            }
            this.callback.accept(snapshot);
        }

        private boolean matches(MutationRecord record) {
            for (ObservedTarget entry : this.observed) {
                if (entry == null || entry.target == null || !entry.accepts(record)) continue;
                if (record.target == entry.target) {
                    return true;
                }
                if (!entry.subtree || !entry.target.contains(record.target)) continue;
                return true;
            }
            return false;
        }

        private MutationRecord adapt(MutationRecord record) {
            boolean targetMatch;
            if ("attributes".equals(record.type) && !record.attributeName.isBlank()) {
                for (ObservedTarget entry : this.observed) {
                    if (entry == null || entry.target == null || !entry.accepts(record) || !(targetMatch = record.target == entry.target || entry.subtree && entry.target.contains(record.target))) continue;
                    String oldValue = entry.attributeOldValue ? record.oldValue : null;
                    return MutationRecord.attributes(record.target, record.attributeName, oldValue);
                }
            }
            if ("characterData".equals(record.type)) {
                for (ObservedTarget entry : this.observed) {
                    if (entry == null || entry.target == null || !entry.accepts(record)) continue;
                    boolean bl = targetMatch = record.target == entry.target || entry.subtree && entry.target.contains(record.target);
                    if (!targetMatch) continue;
                    return MutationRecord.characterData(record.target, entry.characterDataOldValue ? record.oldValue : null);
                }
            }
            return record;
        }

        private static Set<String> parseAttributeFilter(String csv) {
            if (csv == null || csv.isBlank()) {
                return Collections.emptySet();
            }
            LinkedHashSet<String> values = new LinkedHashSet<String>();
            for (String part : csv.split(",")) {
                String normalized;
                if (part == null || (normalized = part.trim()).isEmpty()) continue;
                values.add(normalized);
            }
            return values.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(values);
        }
    }

    public static final class MutationRecord {
        public final String type;
        public final Node target;
        public final List<Node> addedNodes;
        public final List<Node> removedNodes;
        public final Node previousSibling;
        public final Node nextSibling;
        public final String attributeName;
        public final String oldValue;

        private MutationRecord(String type, Node target, List<Node> addedNodes, List<Node> removedNodes, Node previousSibling, Node nextSibling, String attributeName, String oldValue) {
            this.type = type == null ? "" : type;
            this.target = target;
            this.addedNodes = addedNodes == null ? List.of() : Collections.unmodifiableList(new ArrayList<Node>(addedNodes));
            this.removedNodes = removedNodes == null ? List.of() : Collections.unmodifiableList(new ArrayList<Node>(removedNodes));
            this.previousSibling = previousSibling;
            this.nextSibling = nextSibling;
            this.attributeName = attributeName == null ? "" : attributeName;
            this.oldValue = oldValue;
        }

        public static MutationRecord childList(Node target, List<Node> addedNodes, List<Node> removedNodes, Node previousSibling, Node nextSibling) {
            return new MutationRecord("childList", target, addedNodes, removedNodes, previousSibling, nextSibling, null, null);
        }

        public static MutationRecord attributes(Node target, String attributeName, String oldValue) {
            return new MutationRecord("attributes", target, List.of(), List.of(), null, null, attributeName, oldValue);
        }

        public static MutationRecord characterData(Node target, String oldValue) {
            return new MutationRecord("characterData", target, List.of(), List.of(), null, null, null, oldValue);
        }
    }

    private static final class SelectionCacheEntry {
        String flattened = null;
        SelectionUnits.RawText raw = null;
        Boolean paintsRuns = null;

        private SelectionCacheEntry() {
        }
    }

    private record ObservedTarget(Node target, boolean childList, boolean attributes, boolean characterData, boolean subtree, boolean attributeOldValue, boolean characterDataOldValue, Set<String> attributeFilter) {
        private boolean accepts(MutationRecord record) {
            if (record == null) {
                return false;
            }
            if ("childList".equals(record.type)) {
                return this.childList;
            }
            if ("attributes".equals(record.type)) {
                if (!this.attributes) {
                    return false;
                }
                return this.attributeFilter == null || this.attributeFilter.isEmpty() || this.attributeFilter.contains(record.attributeName);
            }
            if ("characterData".equals(record.type)) {
                return this.characterData;
            }
            return false;
        }
    }
}

