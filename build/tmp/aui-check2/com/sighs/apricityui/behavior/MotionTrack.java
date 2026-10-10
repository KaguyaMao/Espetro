/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.behavior;

import com.sighs.apricityui.dom.RenderElement;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Layout;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.StyleFrameCache;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class MotionTrack {
    private static final int FLAG_TRANSITION = 1;
    private static final int FLAG_ANIMATION_SPEC = 2;
    private static final String[] LAYOUT_PROPS = new String[]{"width", "height", "minWidth", "minHeight", "maxWidth", "maxHeight", "boxSizing", "position", "top", "right", "bottom", "left", "margin", "marginTop", "marginBottom", "marginLeft", "marginRight", "padding", "paddingTop", "paddingBottom", "paddingLeft", "paddingRight", "border", "borderTop", "borderBottom", "borderLeft", "borderRight"};
    private static final String[] VISUAL_BOX_PROPS = new String[]{"borderRadius", "boxShadow", "backgroundColor", "backgroundImage", "backgroundRepeat", "backgroundSize", "backgroundPosition", "borderImage", "borderImageSource", "borderImageSlice", "borderImageWidth", "borderImageOutset", "borderImageRepeat"};
    private static final String[] BACKGROUND_PROPS = new String[]{"backgroundColor", "backgroundImage", "backgroundRepeat", "backgroundSize", "backgroundPosition"};
    private final Document owner;
    private final ConcurrentHashMap<Element, Integer> flags = new ConcurrentHashMap();
    private final ConcurrentHashMap<Element, MotionStyles> lastMotionStyles = new ConcurrentHashMap();
    private final Set<Element> hitTestRoots = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<Element> layoutRoots = Collections.newSetFromMap(new IdentityHashMap());
    private final Set<Element> geometryRoots = Collections.newSetFromMap(new IdentityHashMap());
    private boolean visualChanges = false;

    public MotionTrack(Document owner) {
        this.owner = owner;
    }

    public void clear() {
        this.flags.clear();
        this.lastMotionStyles.clear();
        this.hitTestRoots.clear();
        this.layoutRoots.clear();
        this.geometryRoots.clear();
        this.visualChanges = false;
    }

    public void removeElement(Element element) {
        if (element == null) {
            return;
        }
        this.flags.keySet().removeIf(e -> element.uuid.equals(e.uuid));
        this.lastMotionStyles.keySet().removeIf(e -> element.uuid.equals(e.uuid));
        this.hitTestRoots.remove(element);
        this.layoutRoots.remove(element);
        this.geometryRoots.remove(element);
    }

    public void setTransitionActive(Element element, boolean active) {
        this.setFlag(element, 1, active);
    }

    public void setHasAnimationSpec(Element element, boolean hasSpec) {
        this.setFlag(element, 2, hasSpec);
    }

    public boolean stepRender() {
        this.hitTestRoots.clear();
        this.layoutRoots.clear();
        this.geometryRoots.clear();
        this.visualChanges = false;
        if (!StyleFrameCache.isActive()) {
            return false;
        }
        if (this.flags.isEmpty()) {
            return false;
        }
        boolean requiresGeometryCommit = false;
        for (Map.Entry<Element, Integer> entry : this.flags.entrySet()) {
            Style inheritedBefore;
            boolean hasAnimationSpec;
            Element element = entry.getKey();
            if (element == null || element.document != this.owner) {
                this.flags.remove(element);
                continue;
            }
            int value = entry.getValue() == null ? 0 : entry.getValue();
            boolean hasTransition = (value & 1) != 0;
            boolean bl = hasAnimationSpec = (value & 2) != 0;
            if (!hasTransition && !hasAnimationSpec) {
                this.flags.remove(element);
                this.lastMotionStyles.remove(element);
                continue;
            }
            Style base = element.getRawComputedStyle();
            if (hasAnimationSpec && !Animation.hasAnimationSpec(base)) {
                this.setHasAnimationSpec(element, false);
                hasAnimationSpec = false;
            }
            if (!hasTransition && !hasAnimationSpec) continue;
            this.visualChanges = true;
            MotionStyles motionStyles = this.lastMotionStyles.computeIfAbsent(element, ignored -> new MotionStyles());
            Style previousMotionStyle = motionStyles.initialized ? motionStyles.last : null;
            Style animated = motionStyles.work;
            Style previousBuffer = motionStyles.last;
            animated.copyFrom(base);
            boolean completedLayoutTransition = false;
            boolean completedTransformTransition = false;
            boolean completedRectTransition = false;
            if (hasTransition) {
                completedLayoutTransition = Transition.affectsLayout(element);
                completedTransformTransition = Transition.affectsTransform(element);
                completedRectTransition = Transition.affectsRect(element);
                boolean stillActive = Transition.updateStyle(element, animated);
                if (!stillActive) {
                    requiresGeometryCommit |= this.invalidateCompletedTransitionCaches(element, completedLayoutTransition, completedTransformTransition, completedRectTransition);
                    this.setTransitionActive(element, false);
                }
            }
            if (hasAnimationSpec) {
                Animation.updateStyle(element, animated);
            }
            requiresGeometryCommit |= this.invalidateMotionCaches(element, previousMotionStyle == null ? base : previousMotionStyle, animated);
            StyleFrameCache.put(element, animated);
            motionStyles.last = animated;
            motionStyles.work = previousBuffer;
            motionStyles.initialized = true;
            Style style = inheritedBefore = previousMotionStyle == null ? base : previousMotionStyle;
            if (!Objects.equals(inheritedBefore.color, animated.color)) {
                this.refreshInheritedColorSubtree(element);
            }
            if (this.flags.containsKey(element)) continue;
            this.lastMotionStyles.remove(element);
        }
        return requiresGeometryCommit;
    }

    public boolean hasVisualChanges() {
        return this.visualChanges;
    }

    private void refreshInheritedColorSubtree(Element root) {
        if (root == null) {
            return;
        }
        ArrayDeque<Element> pending = new ArrayDeque<Element>(root.children);
        while (!pending.isEmpty()) {
            Element element = pending.removeFirst();
            if (element.document != this.owner) continue;
            element.recomputeStyleSelf();
            pending.addAll(element.children);
        }
    }

    public Set<Element> drainHitTestRoots() {
        return MotionTrack.drainRoots(this.hitTestRoots);
    }

    public Set<Element> drainLayoutRoots() {
        return MotionTrack.drainRoots(this.layoutRoots);
    }

    public Set<Element> drainGeometryRoots() {
        return MotionTrack.drainRoots(this.geometryRoots);
    }

    private static Set<Element> drainRoots(Set<Element> roots) {
        if (roots.isEmpty()) {
            return Set.of();
        }
        Set<Element> result = Collections.newSetFromMap(new IdentityHashMap());
        result.addAll(roots);
        roots.clear();
        return result;
    }

    private boolean invalidateMotionCaches(Element element, Style base, Style animated) {
        RenderElement renderer = element.getRenderer();
        boolean requiresGeometryCommit = false;
        if (MotionTrack.differsAny(base, animated, LAYOUT_PROPS)) {
            this.invalidateLayoutMotion(element, base, animated);
            requiresGeometryCommit = true;
        } else if (MotionTrack.differsAny(base, animated, VISUAL_BOX_PROPS)) {
            renderer.clearVisualBoxCache();
            renderer.invalidateStyleVersion();
        }
        if (MotionTrack.differsAny(base, animated, Style.getTextProp())) {
            renderer.text.clear();
            renderer.wrappedText.clear();
            element.forEachRoute(routeElement -> routeElement.getRenderer().invalidateTextVersion());
        }
        if (!Objects.equals(base.transform, animated.transform)) {
            renderer.invalidateTransformVersion();
            renderer.transform.clear();
            this.geometryRoots.add(element);
            this.hitTestRoots.add(element);
            requiresGeometryCommit = true;
        }
        if (!Objects.equals(base.filter, animated.filter) || !Objects.equals(base.opacity, animated.opacity)) {
            renderer.filter.clear();
        }
        if (!Objects.equals(base.backdropFilter, animated.backdropFilter)) {
            renderer.backdropFilter.clear();
        }
        if (MotionTrack.differsAny(base, animated, BACKGROUND_PROPS)) {
            renderer.background.clear();
            renderer.invalidateStyleVersion();
        }
        return requiresGeometryCommit;
    }

    private void invalidateLayoutMotion(Element element, Style base, Style animated) {
        boolean affectsNormalFlow;
        RenderElement renderer = element.getRenderer();
        this.layoutRoots.add(element);
        boolean bl = affectsNormalFlow = Layout.isInFlow(base) || Layout.isInFlow(animated);
        if (affectsNormalFlow) {
            element.forEachRoute(e -> {
                RenderElement routeRenderer = e.getRenderer();
                routeRenderer.invalidateLayoutVersion();
                routeRenderer.size.clear();
                routeRenderer.box.clear();
            });
            if (element.parentElement != null) {
                element.parentElement.children.forEach(sibling -> sibling.getRenderer().position.clear());
                this.hitTestRoots.add(element.parentElement);
            } else {
                this.hitTestRoots.add(element);
            }
        } else {
            renderer.invalidateLayoutVersion();
            renderer.size.clear();
            renderer.box.clear();
            this.hitTestRoots.add(element);
        }
        renderer.invalidateLayoutSubtree();
    }

    private static boolean differsAny(Style base, Style animated, String[] props) {
        for (String prop : props) {
            if (Objects.equals(base.get(prop), animated.get(prop))) continue;
            return true;
        }
        return false;
    }

    private boolean invalidateCompletedTransitionCaches(Element element, boolean affectsLayout, boolean affectsTransform, boolean affectsRect) {
        RenderElement renderer = element.getRenderer();
        boolean requiresGeometryCommit = false;
        if (affectsLayout) {
            Style style = element.getRawComputedStyle();
            this.invalidateLayoutMotion(element, style, style);
            requiresGeometryCommit = true;
        } else if (affectsRect) {
            renderer.clearVisualBoxCache();
            renderer.background.clear();
            renderer.invalidateStyleVersion();
        }
        if (affectsTransform) {
            renderer.invalidateTransformVersion();
            this.geometryRoots.add(element);
            this.hitTestRoots.add(element);
            requiresGeometryCommit = true;
        }
        renderer.transform.clear();
        renderer.filter.clear();
        renderer.backdropFilter.clear();
        renderer.background.clear();
        renderer.text.clear();
        renderer.wrappedText.clear();
        return requiresGeometryCommit;
    }

    private static boolean differsAny(Style base, Style animated, Iterable<String> props) {
        for (String prop : props) {
            if (Objects.equals(base.get(prop), animated.get(prop))) continue;
            return true;
        }
        return false;
    }

    private void setFlag(Element element, int flag, boolean enabled) {
        if (element == null || element.document != this.owner) {
            return;
        }
        this.flags.compute(element, (e, old) -> {
            int value;
            int n = value = old == null ? 0 : old;
            value = enabled ? (value |= flag) : (value &= ~flag);
            return value == 0 ? null : Integer.valueOf(value);
        });
        if (!this.flags.containsKey(element)) {
            this.lastMotionStyles.remove(element);
        }
    }

    private static final class MotionStyles {
        Style last = new Style();
        Style work = new Style();
        boolean initialized;

        private MotionStyles() {
        }
    }
}

