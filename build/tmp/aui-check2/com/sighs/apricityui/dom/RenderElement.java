/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.dom;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Filter;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.style.Transform;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.joml.Matrix4f;

public class RenderElement {
    private final Element element;
    public Cache<Element[]> route = new Cache<Element[]>(){

        @Override
        public void expandClear() {
            RenderElement.this.clearCommittedLayout();
            RenderElement.this.element.children.forEach(e -> e.getRenderer().route.clear());
        }
    };
    public Cache<List<Transform>> transform = new Cache<List<Transform>>(){

        @Override
        public void expandClear() {
            RenderElement.this.clearCommittedWorldTransform();
            RenderElement.this.element.children.forEach(e -> e.getRenderer().transform.clear());
        }
    };
    public Cache<Float> opacity = new Cache<Float>(){

        @Override
        public void expandClear() {
            RenderElement.this.element.children.forEach(e -> e.getRenderer().opacity.clear());
        }
    };
    public Cache<Style> computedStyle = new Cache();
    public Cache<Text> text = new Cache<Text>(){

        @Override
        public void expandClear() {
            RenderElement.this.element.children.forEach(e -> e.getRenderer().text.clear());
        }
    };
    public Cache<Text.WrappedTextCache> wrappedText = new Cache<Text.WrappedTextCache>(){

        @Override
        public void expandClear() {
            RenderElement.this.element.children.forEach(e -> e.getRenderer().wrappedText.clear());
        }
    };
    public Cache<Size> size = new Cache<Size>(){
        private long dependency = Long.MIN_VALUE;

        @Override
        public Size get() {
            if (this.value == null) {
                return null;
            }
            if (this.dependency != RenderElement.this.usedSizeDependency()) {
                this.value = null;
                return null;
            }
            return (Size)this.value;
        }

        @Override
        public void set(Size value) {
            this.value = value;
            this.dependency = RenderElement.this.usedSizeDependency();
        }

        @Override
        public void expandClear() {
            RenderElement.this.clearCommittedLayout();
        }
    };
    public Cache<Box> box = new Cache<Box>(){

        @Override
        public void expandClear() {
            RenderElement.this.clearCommittedLayout();
        }
    };
    public Cache<Position> position = new Cache<Position>(){

        @Override
        public void expandClear() {
            RenderElement.this.clearCommittedLayout();
            RenderElement.this.element.children.forEach(e -> e.getRenderer().position.clear());
        }
    };
    public Cache<Background> background = new Cache();
    public Cache<String> cursor = new Cache<String>(){

        @Override
        public void expandClear() {
            RenderElement.this.element.children.forEach(e -> e.getRenderer().cursor.clear());
        }
    };
    public Cache<Filter.FilterState> filter = new Cache();
    public Cache<Filter.FilterState> backdropFilter = new Cache();
    private Rect committedRect = null;
    private Matrix4f committedWorldTransform = null;
    private long styleVersion = 1L;
    private long textVersion = 1L;
    private long layoutVersion = 1L;
    private long scrollVersion = 1L;
    private long transformVersion = 1L;
    private long committedRectDependency = Long.MIN_VALUE;
    private long committedTransformDependency = Long.MIN_VALUE;
    private static final Set<String> LAYOUT_PROPS = Set.of("width", "height", "boxSizing", "margin", "marginTop", "marginBottom", "marginLeft", "marginRight", "flexDirection", "flexWrap", "alignContent", "justifyContent", "alignItems", "order", "gridTemplateColumns", "gridTemplateRows", "gap", "rowGap", "columnGap", "justifyItems", "gridRow", "gridColumn", "justifySelf", "alignSelf", "position", "top", "bottom", "left", "right", "display");
    private static final Set<String> PADDING_AND_BORDER_PROPS = Set.of("padding", "paddingTop", "paddingBottom", "paddingLeft", "paddingRight", "border", "borderTop", "borderBottom", "borderLeft", "borderRight");
    private static final Set<String> VISUAL_BOX_PROPS = Set.of("color", "visibility", "opacity", "borderRadius", "boxShadow", "backgroundColor", "backgroundImage", "backgroundRepeat", "backgroundSize", "backgroundPosition", "borderImage", "borderImageSource", "borderImageSlice", "borderImageWidth", "borderImageOutset", "borderImageRepeat");
    private static final Set<String> BACKGROUND_PROPS = Set.of("backgroundColor", "backgroundImage", "backgroundRepeat", "backgroundSize", "backgroundPosition");
    private static final Set<String> CURSOR_PROPS = Set.of("cursor");
    private static final Set<String> HIT_TEST_PROPS = Set.of("visibility", "pointerEvents");
    private static final Set<String> TEXT_LAYOUT_PROPS = Set.of("fontSize", "lineHeight", "fontFamily", "fontWeight", "fontStyle", "textStroke", "direction", "letterSpacing", "textAlign", "verticalAlign", "textIndent", "whiteSpace", "textOverflow", "lineClamp");
    private static final Set<String> STRUCTURAL_PROPS = Set.of("clipPath", "filter", "backdropFilter", "overflow", "overflowX", "overflowY");

    public RenderElement(Element element) {
        this.element = element;
    }

    public Rect getCommittedRect() {
        return this.committedRect;
    }

    public Rect getCommittedRectIfValid() {
        return this.hasCommittedRect(this.rectDependency(this.element.document)) ? this.committedRect : null;
    }

    public Matrix4f getCommittedWorldTransform() {
        return this.committedWorldTransform;
    }

    public Matrix4f getCommittedWorldTransformIfValid() {
        return this.hasCommittedWorldTransform(this.transformDependency(this.element.document)) ? this.committedWorldTransform : null;
    }

    public boolean hasCommittedRect(long dependency) {
        return this.committedRect != null && this.committedRectDependency == dependency;
    }

    public boolean hasCommittedWorldTransform(long dependency) {
        return this.committedWorldTransform != null && this.committedTransformDependency == dependency;
    }

    public void commitRect(Rect rect, long dependency) {
        this.committedRect = rect;
        this.committedRectDependency = dependency;
    }

    public void commitWorldTransform(Matrix4f worldTransform, long dependency) {
        this.committedWorldTransform = worldTransform;
        this.committedTransformDependency = dependency;
    }

    public void invalidateLayoutVersion() {
        ++this.layoutVersion;
    }

    public long layoutDependency() {
        return this.usedSizeDependency();
    }

    public long textDependency() {
        long value = 17L;
        for (Element routeElement : this.element.getRouteArray()) {
            value = RenderElement.mix(value, routeElement.getRenderer().textVersion);
        }
        return value;
    }

    private long usedSizeDependency() {
        long value = 17L;
        if (this.element.document != null) {
            value = RenderElement.mix(value, this.element.document.getViewportVersion());
        }
        Element[] route = this.element.getRouteArray();
        for (int i = 0; i < route.length; ++i) {
            Size ancestorSize;
            RenderElement renderer = route[i].getRenderer();
            value = RenderElement.mix(value, renderer.layoutVersion);
            if (i == 0 || (ancestorSize = (Size)renderer.size.value) == null) continue;
            value = RenderElement.mix(value, Double.doubleToLongBits(ancestorSize.width()));
            value = RenderElement.mix(value, Double.doubleToLongBits(ancestorSize.height()));
        }
        return value;
    }

    public void invalidateLayoutSubtree() {
        ArrayDeque<Element> stack = new ArrayDeque<Element>();
        stack.push(this.element);
        while (!stack.isEmpty()) {
            Element current = (Element)stack.pop();
            RenderElement renderer = current.getRenderer();
            ++renderer.layoutVersion;
            renderer.size.value = null;
            renderer.box.value = null;
            renderer.position.value = null;
            renderer.text.value = null;
            renderer.wrappedText.value = null;
            renderer.transform.value = null;
            renderer.clearCommittedLayout();
            for (Element child : current.getExistingLayoutChildren()) {
                stack.push(child);
            }
        }
    }

    public void invalidateStyleVersion() {
        ++this.styleVersion;
    }

    public void invalidateTextVersion() {
        ++this.textVersion;
    }

    public void invalidateScrollVersion() {
        ++this.scrollVersion;
    }

    public void invalidateTransformVersion() {
        ++this.transformVersion;
    }

    public long rectDependency(Document document) {
        return this.dependency(document, false);
    }

    public long transformDependency(Document document) {
        return this.dependency(document, true);
    }

    private long dependency(Document document, boolean includeTransform) {
        long value = 17L;
        if (document != null) {
            value = RenderElement.mix(value, document.getViewportVersion());
        }
        for (Element routeElement : this.element.getRouteArray()) {
            RenderElement renderer = routeElement.getRenderer();
            if (!includeTransform && routeElement == this.element) {
                value = RenderElement.mix(value, renderer.styleVersion);
            }
            value = RenderElement.mix(value, renderer.layoutVersion);
            value = RenderElement.mix(value, renderer.scrollVersion);
            if (!includeTransform) continue;
            value = RenderElement.mix(value, renderer.transformVersion);
        }
        return value;
    }

    private static long mix(long value, long version) {
        return value * -7046029288634856825L ^ version;
    }

    public void clearCommittedLayout() {
        this.committedRect = null;
        this.committedWorldTransform = null;
        this.committedRectDependency = Long.MIN_VALUE;
        this.committedTransformDependency = Long.MIN_VALUE;
    }

    public void clearCommittedWorldTransform() {
        this.committedWorldTransform = null;
        this.committedTransformDependency = Long.MIN_VALUE;
    }

    public void clearVisualBoxCache() {
        this.box.value = null;
    }

    public void clearCommittedLayoutSubtree() {
        this.clearCommittedLayout();
        for (Element child : this.element.children) {
            child.getRenderer().clearCommittedLayoutSubtree();
        }
    }

    public void clearCommittedWorldTransformSubtree() {
        this.clearCommittedWorldTransform();
        for (Element child : this.element.children) {
            child.getRenderer().clearCommittedWorldTransformSubtree();
        }
    }

    public static void observeStyle(Element element, Style origin, Style current) {
        boolean textChanged;
        boolean currentFilterEnabled;
        int dirtyMask = 0;
        Predicate<Set> check = set -> {
            for (String s : set) {
                String oVal = origin.get(s);
                String cVal = current.get(s);
                if (oVal == null && cVal == null || oVal != null && oVal.equals(cVal)) continue;
                return true;
            }
            return false;
        };
        RenderElement renderer = element.getRenderer();
        for (String prop : STRUCTURAL_PROPS) {
            boolean has;
            String oVal = origin.get(prop);
            String cVal = current.get(prop);
            boolean had = oVal != null && !oVal.equals("none") && !oVal.isEmpty();
            boolean bl = has = cVal != null && !cVal.equals("none") && !cVal.isEmpty();
            if (prop.equals("overflow") || prop.equals("overflowX") || prop.equals("overflowY")) {
                had = Interaction.clipsOverflow(origin);
                has = Interaction.clipsOverflow(current);
            }
            if (had == has) continue;
            dirtyMask |= 2;
            break;
        }
        boolean originFilterEnabled = !Filter.isDisabled(origin.filter, origin.opacity);
        boolean bl = currentFilterEnabled = !Filter.isDisabled(current.filter, current.opacity);
        if (originFilterEnabled != currentFilterEnabled) {
            dirtyMask |= 2;
        }
        if (!current.transform.equals(origin.transform) || !current.transformOrigin.equals(origin.transformOrigin)) {
            renderer.transform.clear();
            renderer.invalidateTransformVersion();
            dirtyMask |= 0x11;
            if (Transform.createsStackingContext(origin.transform) != Transform.createsStackingContext(current.transform) || Math.abs(Transform.getTranslateZ(origin.transform) - Transform.getTranslateZ(current.transform)) > 1.0E-4) {
                dirtyMask |= 2;
            }
        }
        if (!origin.opacity.equals(current.opacity)) {
            renderer.opacity.clear();
            renderer.filter.clear();
            dirtyMask |= 1;
        }
        if (!origin.filter.equals(current.filter)) {
            renderer.filter.clear();
            dirtyMask |= 1;
        }
        if (!origin.backdropFilter.equals(current.backdropFilter)) {
            renderer.backdropFilter.clear();
            dirtyMask |= 1;
        }
        if (textChanged = check.test(Style.getTextProp())) {
            renderer.text.clear();
            renderer.wrappedText.clear();
            element.forEachRoute(routeElement -> routeElement.getRenderer().invalidateTextVersion());
            dirtyMask |= 1;
            if (check.test(TEXT_LAYOUT_PROPS)) {
                element.forEachRoute(routeElement -> {
                    RenderElement routeRenderer = routeElement.getRenderer();
                    routeRenderer.invalidateLayoutVersion();
                    routeRenderer.size.clear();
                });
                renderer.box.clear();
                if (element.parentElement != null) {
                    element.parentElement.getRenderer().size.clear();
                    element.parentElement.children.forEach(sibling -> sibling.getRenderer().position.clear());
                } else {
                    renderer.position.clear();
                }
                dirtyMask |= 4;
            }
        }
        boolean paddingOrBorderChanged = check.test(PADDING_AND_BORDER_PROPS);
        boolean layoutChanged = check.test(LAYOUT_PROPS);
        if (paddingOrBorderChanged) {
            element.forEachRoute(e -> e.getRenderer().size.clear());
            element.forEachRoute(e -> e.getRenderer().box.clear());
            if (element.parentElement != null) {
                element.parentElement.getRenderer().size.clear();
                element.parentElement.children.forEach(sibling -> sibling.getRenderer().position.clear());
            } else {
                renderer.position.clear();
            }
            dirtyMask |= 4;
        }
        if (layoutChanged) {
            element.forEachRoute(e -> e.getRenderer().size.clear());
            renderer.box.clear();
            if (element.parentElement != null) {
                element.parentElement.getRenderer().size.clear();
                element.parentElement.children.forEach(sibling -> sibling.getRenderer().position.clear());
            } else {
                renderer.position.clear();
            }
            dirtyMask |= 4;
        }
        if (paddingOrBorderChanged || layoutChanged) {
            renderer.invalidateLayoutSubtree();
        }
        if (!origin.display.equals(current.display)) {
            dirtyMask |= 2;
        }
        if (!origin.zIndex.equals(current.zIndex)) {
            dirtyMask |= 2;
        }
        if (check.test(BACKGROUND_PROPS)) {
            renderer.background.clear();
            renderer.invalidateStyleVersion();
            dirtyMask |= 1;
        }
        if (check.test(VISUAL_BOX_PROPS)) {
            renderer.clearVisualBoxCache();
            renderer.invalidateStyleVersion();
            dirtyMask |= 1;
        }
        if (check.test(CURSOR_PROPS)) {
            renderer.cursor.clear();
        }
        if (check.test(HIT_TEST_PROPS)) {
            dirtyMask |= 8;
        }
        if (!origin.animation.equals(current.animation)) {
            Animation.stop(element);
            renderer.transform.clear();
            renderer.invalidateTransformVersion();
            renderer.filter.clear();
            dirtyMask |= 1;
        }
        if (!origin.zIndex.equals(current.zIndex)) {
            dirtyMask |= 2;
        }
        if (dirtyMask != 0 && element.document != null) {
            element.document.markDirty(element, dirtyMask);
        }
    }

    public static class Cache<T> {
        T value = null;

        public T get() {
            return this.value;
        }

        public void set(T value) {
            this.value = value;
        }

        public void clear() {
            this.value = null;
            this.expandClear();
        }

        public void expandClear() {
        }
    }
}

