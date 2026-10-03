/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.style.ComputedStyleResolver;
import com.sighs.apricityui.style.InlineStyleDeclaration;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.ShorthandParser;
import com.sighs.apricityui.style.VarResolver;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Style
extends AbstractMap<String, String>
implements Cloneable {
    public static final Style DEFAULT = new Style();
    private static final Set<String> UNSUPPORTED_PROPERTIES = ConcurrentHashMap.newKeySet();
    static final Set<String> INHERITED_PROPERTIES = Set.of("color", "selection-color", "font-size", "font-family", "font-weight", "font-style", "line-height", "direction", "letter-spacing", "text-align", "text-indent", "text-transform", "white-space", "cursor", "visibility", "accent-color", "text-stroke");
    public String width = "unset";
    public String height = "unset";
    public String aspectRatio = "auto";
    public String minWidth = "unset";
    public String minHeight = "unset";
    public String maxWidth = "unset";
    public String maxHeight = "unset";
    public String boxSizing = "content-box";
    public String overflow = "visible";
    public String overflowX = "unset";
    public String overflowY = "unset";
    public String opacity = "1.0";
    public String boxShadow = "unset";
    public String zIndex = "auto";
    public String display = "block";
    public String content = "unset";
    public String gridTemplateColumns = "unset";
    public String gridTemplateRows = "unset";
    public String gap = "0px";
    public String rowGap = "unset";
    public String columnGap = "unset";
    public String justifyItems = "stretch";
    public String justifySelf = "unset";
    public String alignSelf = "unset";
    public String gridRow = "auto";
    public String gridColumn = "auto";
    public String backgroundColor = "unset";
    public String backgroundImage = "unset";
    public String backgroundRepeat = "unset";
    public String backgroundSize = "unset";
    public String backgroundPosition = "unset";
    public String objectFit = "fill";
    public String objectPosition = "50% 50%";
    public String appearance = "auto";
    public String resize = "none";
    public String margin = "unset";
    public String marginTop = "unset";
    public String marginBottom = "unset";
    public String marginLeft = "unset";
    public String marginRight = "unset";
    public String padding = "unset";
    public String paddingTop = "unset";
    public String paddingBottom = "unset";
    public String paddingLeft = "unset";
    public String paddingRight = "unset";
    public String border = "unset";
    public String borderTop = "unset";
    public String borderBottom = "unset";
    public String borderLeft = "unset";
    public String borderRight = "unset";
    public String borderWidth = "unset";
    public String borderColor = "unset";
    public String borderRadius = "unset";
    public String borderImage = "unset";
    public String borderImageSource = "unset";
    public String borderImageSlice = "unset";
    public String borderImageWidth = "unset";
    public String borderImageOutset = "unset";
    public String borderImageRepeat = "unset";
    public String color = "unset";
    public String selectionColor = "unset";
    public String accentColor = "unset";
    public String fontSize = "unset";
    public String fontFamily = "unset";
    public String fontWeight = "unset";
    public String fontStyle = "unset";
    public String textStroke = "unset";
    public String textDecoration = "unset";
    public String lineHeight = "unset";
    public String direction = "unset";
    public String letterSpacing = "unset";
    public String textAlign = "unset";
    public String verticalAlign = "unset";
    public String textIndent = "unset";
    public String textTransform = "unset";
    public String whiteSpace = "unset";
    public String textOverflow = "clip";
    public String lineClamp = "none";
    public String flexDirection = "row";
    public String flexWrap = "nowrap";
    public String alignContent = "stretch";
    public String justifyContent = "flex-start";
    public String alignItems = "stretch";
    public String flex = "unset";
    public String flexGrow = "0";
    public String flexShrink = "1";
    public String flexBasis = "auto";
    public String order = "0";
    public String top = "unset";
    public String bottom = "unset";
    public String left = "unset";
    public String right = "unset";
    public String position = "static";
    public String cursor = "auto";
    public String userSelect = "unset";
    public String pointerEvents = "auto";
    public String visibility = "unset";
    public String transition = "none";
    public String transform = "none";
    public String transformOrigin = "50% 50%";
    public String rotate = "none";
    public String clipPath = "none";
    public String filter = "none";
    public String backdropFilter = "none";
    public String animation = "unset";
    public String animationName = "unset";
    public String animationDuration = "unset";
    public String animationDelay = "unset";
    public String animationIterationCount = "unset";
    public String animationDirection = "unset";
    public String animationFillMode = "unset";
    public String animationTimingFunction = "unset";
    public String animationPlayState = "unset";
    private Map<String, String> customProperties = new HashMap<String, String>();
    private transient Element inlineOwner;
    private static final Map<String, Field> FIELD_CACHE = new HashMap<String, Field>();
    private static final Map<String, String> STYLE_NAME = new HashMap<String, String>();
    static final Field[] STYLE_FIELDS;
    static final String[] STYLE_FIELD_CSS_NAMES;
    private static final Set<String> TEXT_PROPS;

    public static void warmUpMetadata() {
        if (STYLE_FIELDS.length != STYLE_FIELD_CSS_NAMES.length) {
            throw new IllegalStateException("Style metadata is inconsistent");
        }
    }

    public static String[] getSupportedPropertyNames() {
        return (String[])STYLE_FIELD_CSS_NAMES.clone();
    }

    public static Style createInlineDeclarationStyle() {
        return Style.createInlineDeclarationStyle(null);
    }

    public static Style createInlineDeclarationStyle(Element owner) {
        Style style = new Style();
        for (Field field : STYLE_FIELDS) {
            try {
                field.set(style, "");
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        style.customProperties.clear();
        style.inlineOwner = owner;
        return style;
    }

    public String getPropertyValue(String name) {
        if (this.inlineOwner != null) {
            return this.inlineOwner.getInlineStylePropertyValue(name);
        }
        String value = this.get(name);
        return value == null || "unset".equalsIgnoreCase(value) ? "" : value;
    }

    public String getPropertyPriority(String name) {
        return this.inlineOwner == null ? "" : this.inlineOwner.getInlineStylePropertyPriority(name);
    }

    public void setProperty(String name, String value) {
        this.setProperty(name, value, "");
    }

    public void setProperty(String name, String value, String priority) {
        if (this.inlineOwner != null) {
            this.inlineOwner.setInlineStyleProperty(name, value, priority);
        } else {
            this.update(name, value == null || value.isBlank() ? "unset" : value);
        }
    }

    public String removeProperty(String name) {
        if (this.inlineOwner != null) {
            return this.inlineOwner.removeInlineStyleProperty(name);
        }
        String previous = this.getPropertyValue(name);
        this.update(name, "unset");
        return previous;
    }

    public String getCssText() {
        return this.inlineOwner == null ? this.toCss() : this.inlineOwner.getInlineStyleCssText();
    }

    public void setCssText(String value) {
        if (this.inlineOwner != null) {
            this.inlineOwner.setInlineStyleCssText(value);
        }
    }

    public int getLength() {
        return this.inlineOwner == null ? this.entrySet().size() : this.inlineOwner.getInlineStylePropertyNames().length;
    }

    public String item(int index) {
        if (index < 0) {
            return "";
        }
        if (this.inlineOwner != null) {
            String[] names = this.inlineOwner.getInlineStylePropertyNames();
            return index < names.length ? names[index] : "";
        }
        return this.entrySet().stream().skip(index).map(Map.Entry::getKey).findFirst().orElse("");
    }

    @Override
    public String get(Object key) {
        if (key == null) {
            return "";
        }
        String name = String.valueOf(key);
        if (this.inlineOwner != null && "cssText".equals(name)) {
            return this.getCssText();
        }
        if (this.inlineOwner != null && "length".equals(name)) {
            return String.valueOf(this.getLength());
        }
        if (this.inlineOwner != null && name.chars().allMatch(Character::isDigit)) {
            try {
                return this.item(Integer.parseInt(name));
            }
            catch (NumberFormatException ignored) {
                return "";
            }
        }
        return this.inlineOwner == null ? this.get(name) : this.getPropertyValue(name);
    }

    @Override
    public String put(String key, String value) {
        String previous = this.get(key);
        if (this.inlineOwner != null && "cssText".equals(key)) {
            this.setCssText(value);
        } else {
            this.setProperty(key, value);
        }
        return previous;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key == null) {
            return false;
        }
        String name = String.valueOf(key);
        if (this.inlineOwner != null && ("cssText".equals(name) || "length".equals(name))) {
            return true;
        }
        if (this.inlineOwner != null && name.chars().allMatch(Character::isDigit)) {
            try {
                int index = Integer.parseInt(name);
                return index >= 0 && index < this.getLength();
            }
            catch (NumberFormatException ignored) {
                return false;
            }
        }
        return this.inlineOwner != null ? !this.inlineOwner.getInlineStylePropertyValue(name).isEmpty() : super.containsKey(key);
    }

    @Override
    public String remove(Object key) {
        return key == null ? "" : this.removeProperty(String.valueOf(key));
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        LinkedHashSet<Map.Entry<String, String>> entries = new LinkedHashSet<Map.Entry<String, String>>();
        if (this.inlineOwner != null) {
            for (String name2 : this.inlineOwner.getInlineStylePropertyNames()) {
                entries.add(new AbstractMap.SimpleImmutableEntry<String, String>(name2, this.inlineOwner.getInlineStylePropertyValue(name2)));
            }
            return entries;
        }
        for (String name3 : STYLE_FIELD_CSS_NAMES) {
            String value2 = this.get(name3);
            if (value2 == null || value2.isEmpty() || "unset".equalsIgnoreCase(value2)) continue;
            entries.add(new AbstractMap.SimpleImmutableEntry<String, String>(name3, value2));
        }
        this.customProperties.forEach((name, value) -> entries.add(new AbstractMap.SimpleImmutableEntry<String, String>((String)name, (String)value)));
        return entries;
    }

    public Map<String, String> changesComparedTo(Style previous) {
        LinkedHashMap<String, String> changes = new LinkedHashMap<String, String>();
        if (previous == null) {
            return changes;
        }
        for (int i = 0; i < STYLE_FIELDS.length; ++i) {
            try {
                String current = (String)STYLE_FIELDS[i].get(this);
                String old = (String)STYLE_FIELDS[i].get(previous);
                if (Objects.equals(current, old)) continue;
                changes.put(STYLE_FIELD_CSS_NAMES[i], current == null ? "" : current);
                continue;
            }
            catch (IllegalAccessException current) {
                // empty catch block
            }
        }
        for (String name2 : previous.customProperties.keySet()) {
            if (this.customProperties.containsKey(name2)) continue;
            changes.put(name2, "");
        }
        this.customProperties.forEach((name, value) -> {
            if (!Objects.equals(value, previous.customProperties.get(name))) {
                changes.put((String)name, (String)value);
            }
        });
        return changes;
    }

    public void merge(String styleString) {
        if (styleString == null || styleString.isBlank()) {
            return;
        }
        InlineStyleDeclaration.parse(styleString).forEach((property, value) -> this.update((String)property, InlineStyleDeclaration.valueWithoutPriority(value)));
    }

    public void mergeCascade(Map<String, CSS.Declaration> stylesheet, String inlineStyle) {
        this.applyStylesheet(stylesheet, false);
        this.applyInline(inlineStyle, false);
        this.applyStylesheet(stylesheet, true);
        this.applyInline(inlineStyle, true);
    }

    private void applyStylesheet(Map<String, CSS.Declaration> stylesheet, boolean important) {
        if (stylesheet == null) {
            return;
        }
        for (Map.Entry<String, CSS.Declaration> entry : stylesheet.entrySet()) {
            CSS.Declaration declaration = entry.getValue();
            if (declaration == null || declaration.important() != important) continue;
            this.update(entry.getKey(), declaration.value());
        }
    }

    private void applyInline(String inlineStyle, boolean important) {
        if (inlineStyle == null || inlineStyle.isBlank()) {
            return;
        }
        for (Map.Entry<String, String> entry : InlineStyleDeclaration.parse(inlineStyle).entrySet()) {
            String value = entry.getValue();
            if (value == null || value.isBlank() || "important".equals(InlineStyleDeclaration.priorityOf(value)) != important) continue;
            this.update(entry.getKey(), InlineStyleDeclaration.valueWithoutPriority(value));
        }
    }

    public void update(String name, String value) {
        String styleName;
        if (name == null || name.isBlank()) {
            return;
        }
        if (value == null) {
            value = "";
        }
        if (value.startsWith(" ")) {
            value = value.replaceFirst(" ", "");
        }
        if (name.startsWith("--")) {
            this.customProperties.put(VarResolver.normalizeCustomPropertyName(name), value);
            return;
        }
        if ("-webkit-appearance".equalsIgnoreCase(name)) {
            name = "appearance";
        }
        if ("background".equals(styleName = Style.transformStyleName(name))) {
            ShorthandParser.applyBackground(this, value);
            return;
        }
        if ("flex".equals(styleName)) {
            ShorthandParser.applyFlex(this, value);
            return;
        }
        if ("gap".equals(styleName)) {
            ShorthandParser.applyGap(this, value);
            return;
        }
        if ("inset".equals(styleName)) {
            ShorthandParser.applyInset(this, value);
            return;
        }
        if ("margin".equals(styleName)) {
            ShorthandParser.applyBox(this, "margin", value);
            return;
        }
        if ("padding".equals(styleName)) {
            ShorthandParser.applyBox(this, "padding", value);
            return;
        }
        if ("border".equals(styleName)) {
            ShorthandParser.applyBorder(this, value);
            return;
        }
        if ("borderWidth".equals(styleName)) {
            ShorthandParser.applyBorderWidth(this, value);
            return;
        }
        if ("borderColor".equals(styleName)) {
            ShorthandParser.applyBorderColor(this, value);
            return;
        }
        if (styleName.startsWith("border") && styleName.endsWith("Width")) {
            ShorthandParser.applyBorderSidePart(this, styleName, value, true);
            return;
        }
        if (styleName.startsWith("border") && styleName.endsWith("Color")) {
            ShorthandParser.applyBorderSidePart(this, styleName, value, false);
            return;
        }
        if ("animation".equals(styleName)) {
            ShorthandParser.applyAnimation(this, value);
            return;
        }
        if ("rotate".equals(styleName)) {
            ShorthandParser.applyRotate(this, value);
            return;
        }
        if ("overflow".equals(styleName)) {
            this.overflow = value = Interaction.normalizeOverflow(value);
            this.overflowX = value;
            this.overflowY = value;
            return;
        }
        if ("overflowX".equals(styleName) || "overflowY".equals(styleName)) {
            value = Interaction.normalizeOverflow(value);
        }
        if ("visibility".equals(styleName)) {
            value = Interaction.normalizeVisibility(value);
        }
        try {
            Field field = FIELD_CACHE.get(styleName);
            if (field == null) {
                field = this.getClass().getDeclaredField(styleName);
                FIELD_CACHE.put(styleName, field);
            }
            field.set(this, value);
        }
        catch (NoSuchFieldException exception) {
            if (UNSUPPORTED_PROPERTIES.add(styleName)) {
                ApricityUI.LOGGER.warn("[AUI CSS] unsupported property ignored property={} value={}", (Object)name, (Object)value);
            }
        }
        catch (IllegalAccessException exception) {
            ApricityUI.LOGGER.error("[AUI CSS] failed to apply property={} value={}", new Object[]{name, value, exception});
        }
    }

    public void applyUserAgentDefaults(Element element) {
        this.display = Style.defaultDisplayFor(element);
        if (element != null && "PRE".equalsIgnoreCase(element.tagName)) {
            this.whiteSpace = "pre";
        }
        if (element != null && "TEXTAREA".equalsIgnoreCase(element.tagName)) {
            this.whiteSpace = "pre-wrap";
        }
        if (element != null && "BUTTON".equalsIgnoreCase(element.tagName)) {
            this.textAlign = "center";
        }
        if (element != null && "SELECT".equalsIgnoreCase(element.tagName)) {
            this.boxSizing = "border-box";
            this.whiteSpace = "nowrap";
            this.overflow = "hidden";
            this.overflowX = "hidden";
            this.overflowY = "hidden";
        }
    }

    public String getFieldValue(String styleName) {
        try {
            Object value;
            Field field = FIELD_CACHE.get(styleName);
            if (field == null) {
                field = this.getClass().getDeclaredField(styleName);
                FIELD_CACHE.put(styleName, field);
            }
            return (value = field.get(this)) == null ? "unset" : value.toString();
        }
        catch (IllegalAccessException | NoSuchFieldException ignored) {
            return "unset";
        }
    }

    public void setFieldValue(String styleName, String value) {
        Field field = FIELD_CACHE.get(styleName);
        if (field == null) {
            return;
        }
        try {
            field.set(this, value);
        }
        catch (IllegalAccessException illegalAccessException) {
            // empty catch block
        }
    }

    public String get(String name) {
        String styleName;
        Field field;
        if (name == null || name.isBlank()) {
            return null;
        }
        if (name.startsWith("--")) {
            return this.customProperties.get(VarResolver.normalizeCustomPropertyName(name));
        }
        if ("-webkit-appearance".equalsIgnoreCase(name)) {
            name = "appearance";
        }
        if ((field = FIELD_CACHE.get(styleName = Style.transformStyleName(name))) == null) {
            return null;
        }
        try {
            return (String)field.get(this);
        }
        catch (IllegalAccessException illegalAccessException) {
            return null;
        }
    }

    public String getCustomProperty(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        return this.customProperties.get(VarResolver.normalizeCustomPropertyName(name));
    }

    public boolean affectsDescendantComputedStyleComparedTo(Style previous) {
        if (previous == null) {
            return true;
        }
        if (!this.customProperties.equals(previous.customProperties)) {
            return true;
        }
        for (String cssName : INHERITED_PROPERTIES) {
            if (Objects.equals(this.get(cssName), previous.get(cssName))) continue;
            return true;
        }
        return false;
    }

    public void resolveVarReferences(Element context) {
        VarResolver.resolveReferences(this, context);
    }

    public void finalizeComputedValues(Element context) {
        ComputedStyleResolver.finalize(this, context);
    }

    private static String defaultDisplayFor(Element element) {
        if (element != null && element.isPseudoElement()) {
            return "inline";
        }
        if (element == null || element.tagName == null) {
            return "block";
        }
        String tag = element.tagName.trim().toUpperCase(Locale.ROOT);
        if ("INPUT".equals(tag) && "hidden".equalsIgnoreCase(element.getAttribute("type"))) {
            return "none";
        }
        return switch (tag) {
            case "A", "ABBR", "B", "BDI", "BDO", "CITE", "CODE", "DATA", "DEL", "DFN", "EM", "I", "INS", "KBD", "LABEL", "MARK", "Q", "S", "SAMP", "SMALL", "SPAN", "STRONG", "SUB", "SUP", "TIME", "U", "VAR", "WBR", "IMG", "INPUT", "SELECT", "TEXTAREA", "CANVAS", "SVG", "TEXTURE", "BUTTON", "TRANSLATION" -> "inline";
            case "HEAD", "SCRIPT", "STYLE", "TITLE", "META", "LINK", "OPTION", "OPTGROUP" -> "none";
            default -> "block";
        };
    }

    public static String transformStyleName(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        String cache = STYLE_NAME.get(input);
        if (cache != null) {
            return cache;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpperCase = false;
        for (int i = 0; i < input.length(); ++i) {
            char currentChar = input.charAt(i);
            if (currentChar == '-') {
                nextUpperCase = true;
                continue;
            }
            if (nextUpperCase) {
                result.append(Character.toUpperCase(currentChar));
                nextUpperCase = false;
                continue;
            }
            result.append(currentChar);
        }
        STYLE_NAME.put(input, result.toString());
        return result.toString();
    }

    private static String camelToKebab(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append('-').append(Character.toLowerCase(c));
                continue;
            }
            result.append(c);
        }
        return result.toString();
    }

    public String toCss() {
        StringBuilder css = new StringBuilder();
        for (int i = 0; i < STYLE_FIELDS.length; ++i) {
            Field field = STYLE_FIELDS[i];
            try {
                Object value2 = field.get(this);
                Object defaultValue = field.get(DEFAULT);
                if (value2 == null || value2.toString().equals(defaultValue == null ? null : defaultValue.toString())) continue;
                css.append(STYLE_FIELD_CSS_NAMES[i]).append(": ").append(value2).append(";");
                continue;
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        this.customProperties.forEach((name, value) -> css.append((String)name).append(": ").append((String)value).append(";"));
        return css.toString();
    }

    public static Set<String> getTextProp() {
        return TEXT_PROPS;
    }

    public void copyFrom(Style other) {
        if (other == null || other == this) {
            return;
        }
        for (Field field : STYLE_FIELDS) {
            try {
                field.set(this, field.get(other));
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        this.customProperties.clear();
        this.customProperties.putAll(other.customProperties);
    }

    @Override
    public Style clone() {
        try {
            Style style = (Style)super.clone();
            style.customProperties = new HashMap<String, String>(this.customProperties);
            style.inlineOwner = null;
            return style;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < STYLE_FIELDS.length; ++i) {
            Field field = STYLE_FIELDS[i];
            try {
                Object value2 = field.get(this);
                if (value2 == null || "unset".equals(value2)) continue;
                sb.append(STYLE_FIELD_CSS_NAMES[i]).append(":").append(value2).append(";");
                continue;
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
        this.customProperties.forEach((name, value) -> sb.append((String)name).append(":").append((String)value).append(";"));
        return sb.toString();
    }

    static {
        TEXT_PROPS = Set.of("color", "font-size", "font-family", "font-weight", "font-style", "text-stroke", "text-decoration", "line-height", "direction", "letter-spacing", "text-align", "vertical-align", "text-indent", "text-transform", "white-space", "text-overflow", "line-clamp");
        ArrayList<Field> fields = new ArrayList<Field>();
        ArrayList<String> cssNames = new ArrayList<String>();
        for (Field field : Style.class.getDeclaredFields()) {
            if (field.getType() != String.class || Modifier.isStatic(field.getModifiers())) continue;
            field.setAccessible(true);
            String fieldName = field.getName();
            String cssName = Style.camelToKebab(fieldName);
            FIELD_CACHE.put(fieldName, field);
            if (!fieldName.equals(cssName)) {
                FIELD_CACHE.put(cssName, field);
            }
            fields.add(field);
            cssNames.add(cssName);
        }
        STYLE_FIELDS = fields.toArray(new Field[0]);
        STYLE_FIELD_CSS_NAMES = cssNames.toArray(new String[0]);
    }

    public record TextStroke(double width, int color) {
        public static final TextStroke NONE = new TextStroke(0.0, 0);
    }
}

