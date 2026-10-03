/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.CSS;
import com.sighs.apricityui.parser.CssString;
import com.sighs.apricityui.util.AuiLog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Selector {
    private static final Map<String, List<CompiledSelector>> SELECTOR_CACHE = new ConcurrentHashMap<String, List<CompiledSelector>>();
    private static final Set<String> SELECTOR_DIAGNOSTICS = ConcurrentHashMap.newKeySet();
    private static final Set<String> SUPPORTED_PSEUDOS = Set.of("root", "first-child", "last-child", "only-child", "nth-child", "nth-last-child", "first-of-type", "last-of-type", "only-of-type", "nth-of-type", "nth-last-of-type", "hover", "active", "focus", "focus-visible", "focus-within", "disabled", "enabled", "required", "optional", "valid", "invalid", "in-range", "out-of-range", "read-only", "read-write", "placeholder-shown", "empty", "checked", "not", "is", "where");

    public static void clearCompiledCache() {
        SELECTOR_CACHE.clear();
    }

    public static void warmUp(Iterable<String> selectors) {
        if (selectors == null) {
            return;
        }
        for (String selector : selectors) {
            if (selector == null || selector.isBlank()) continue;
            SELECTOR_CACHE.computeIfAbsent(selector, Selector::parseGroup);
        }
    }

    static int compiledCacheSize() {
        return SELECTOR_CACHE.size();
    }

    public static HashMap<String, CSS.Declaration> matchCSS(Element element) {
        if (element == null || element.document == null) {
            return new HashMap<String, CSS.Declaration>();
        }
        return element.document.getSelectorIndex().match(element);
    }

    private static boolean isFocusWithin(Element element) {
        Element focused;
        Element element2 = focused = element == null || element.document == null ? null : element.document.getFocusedElement();
        while (focused != null) {
            if (focused == element) {
                return true;
            }
            focused = focused.parentElement;
        }
        return false;
    }

    public static HashMap<String, CSS.Declaration> matchPseudoElementCSS(Element element, PseudoElement pseudoElement) {
        if (element == null || element.document == null || pseudoElement == null) {
            return new HashMap<String, CSS.Declaration>();
        }
        return element.document.getSelectorIndex().matchPseudoElement(element, pseudoElement);
    }

    public static boolean matches(Element element, String selectorStr) {
        if (element == null || selectorStr == null || selectorStr.isBlank()) {
            return false;
        }
        List groups = SELECTOR_CACHE.computeIfAbsent(selectorStr, Selector::parseGroup);
        for (CompiledSelector selector : groups) {
            if (selector.pseudoElement != null || !Selector.isMatch(element, selector)) continue;
            return true;
        }
        return false;
    }

    private static boolean isMatch(Element element, CompiledSelector selector) {
        List<Component> comps = selector.components;
        List<Combinator> combs = selector.combinators;
        int compIdx = comps.size() - 1;
        Element current = element;
        if (!comps.get(compIdx).matches(current)) {
            return false;
        }
        for (int i = combs.size() - 1; i >= 0; --i) {
            Combinator comb = combs.get(i);
            Component target = comps.get(--compIdx);
            if (comb == Combinator.CHILD) {
                current = current.parentElement;
                if (target.matches(current)) continue;
                return false;
            }
            if (comb == Combinator.ADJACENT_SIBLING) {
                if (target.matches(current = Selector.previousElementSibling(current))) continue;
                return false;
            }
            if (comb == Combinator.GENERAL_SIBLING) {
                if ((current = Selector.previousMatchingElementSibling(current, target)) != null) continue;
                return false;
            }
            if (comb != Combinator.DESCENDANT) continue;
            boolean found = false;
            while ((current = current.parentElement) != null) {
                if (!target.matches(current)) continue;
                found = true;
                break;
            }
            if (found) continue;
            return false;
        }
        return true;
    }

    private static Element previousElementSibling(Element element) {
        if (element == null || element.parentElement == null) {
            return null;
        }
        ArrayList<Element> siblings = element.parentElement.children;
        int index = siblings.indexOf(element);
        if (index <= 0) {
            return null;
        }
        return (Element)siblings.get(index - 1);
    }

    private static Element previousMatchingElementSibling(Element element, Component target) {
        if (element == null || target == null || element.parentElement == null) {
            return null;
        }
        ArrayList<Element> siblings = element.parentElement.children;
        int index = siblings.indexOf(element);
        for (int i = index - 1; i >= 0; --i) {
            Element candidate = (Element)siblings.get(i);
            if (!target.matches(candidate)) continue;
            return candidate;
        }
        return null;
    }

    private static List<CompiledSelector> parseGroup(String fullSelector) {
        ArrayList<CompiledSelector> results = new ArrayList<CompiledSelector>();
        for (String p : Selector.splitSelectorList(fullSelector)) {
            if (p.isBlank()) continue;
            results.add(Selector.parseSelector(p.trim()));
        }
        return results;
    }

    public static List<String> splitSelectorList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return CssString.splitTopLevel(value, ',');
    }

    private static CompiledSelector parseSelector(String selector) {
        List<String> tokens = Selector.splitSelectorTokens(selector);
        ArrayList<Component> components = new ArrayList<Component>();
        ArrayList<Combinator> combinators = new ArrayList<Combinator>();
        SpecificityParts specificity = SpecificityParts.ZERO;
        PseudoElement pseudoElement = null;
        block14: for (String token : tokens) {
            int tags;
            ParsedAtom parsedAtom;
            switch (token = token.trim()) {
                case "": {
                    continue block14;
                }
                case ">": {
                    combinators.add(Combinator.CHILD);
                    continue block14;
                }
                case "+": {
                    combinators.add(Combinator.ADJACENT_SIBLING);
                    continue block14;
                }
                case "~": {
                    combinators.add(Combinator.GENERAL_SIBLING);
                    continue block14;
                }
                case " ": {
                    combinators.add(Combinator.DESCENDANT);
                    continue block14;
                }
            }
            if (components.size() > combinators.size()) {
                combinators.add(Combinator.DESCENDANT);
            }
            if ((parsedAtom = Selector.parseAtom(token)).pseudoElement() != null) {
                pseudoElement = parsedAtom.pseudoElement();
            }
            Component comp = parsedAtom.component();
            components.add(comp);
            int ids = comp.id == null ? 0 : 1;
            int classes = (comp.classes == null ? 0 : comp.classes.size()) + (comp.attributeSelectors == null ? 0 : comp.attributeSelectors.size());
            int n = tags = comp.tag == null || comp.tag.equals("*") ? 0 : 1;
            if (comp.pseudos != null) {
                for (Pseudo pseudo : comp.pseudos) {
                    SpecificityParts pseudoSpecificity = pseudo.specificity();
                    ids += pseudoSpecificity.ids;
                    classes += pseudoSpecificity.classes;
                    tags += pseudoSpecificity.tags;
                }
            }
            if (parsedAtom.pseudoElement() != null) {
                ++tags;
            }
            specificity = specificity.plus(new SpecificityParts(ids, classes, tags));
        }
        if (components.isEmpty()) {
            Selector.logSelectorDiagnostic("empty", selector, "selector has no component");
        }
        if (combinators.size() >= components.size() && !components.isEmpty()) {
            Selector.logSelectorDiagnostic("combinator", selector, "selector has too many combinators");
        }
        return new CompiledSelector(components, combinators, pseudoElement, specificity.ids, specificity.classes, specificity.tags);
    }

    private static List<String> splitSelectorTokens(String selector) {
        ArrayList<String> tokens = new ArrayList<String>();
        StringBuilder atom = new StringBuilder();
        int brackets = 0;
        int parentheses = 0;
        char quote = '\u0000';
        for (int i = 0; i < selector.length(); ++i) {
            char ch = selector.charAt(i);
            if (quote != '\u0000') {
                atom.append(ch);
                if (ch != quote || i != 0 && selector.charAt(i - 1) == '\\') continue;
                quote = '\u0000';
                continue;
            }
            if (ch == '\'' || ch == '\"') {
                quote = ch;
                atom.append(ch);
                continue;
            }
            if (ch == '[') {
                ++brackets;
                atom.append(ch);
                continue;
            }
            if (ch == ']') {
                brackets = Math.max(0, brackets - 1);
                atom.append(ch);
                continue;
            }
            if (ch == '(') {
                ++parentheses;
                atom.append(ch);
                continue;
            }
            if (ch == ')') {
                parentheses = Math.max(0, parentheses - 1);
                atom.append(ch);
                continue;
            }
            if (brackets == 0 && parentheses == 0 && Character.isWhitespace(ch)) {
                if (atom.isEmpty()) continue;
                tokens.add(atom.toString());
                atom.setLength(0);
                continue;
            }
            if (brackets == 0 && parentheses == 0 && (ch == '>' || ch == '+' || ch == '~')) {
                if (!atom.isEmpty()) {
                    tokens.add(atom.toString());
                    atom.setLength(0);
                }
                tokens.add(String.valueOf(ch));
                continue;
            }
            atom.append(ch);
        }
        if (!atom.isEmpty()) {
            tokens.add(atom.toString());
        }
        return tokens;
    }

    private static ParsedAtom parseAtom(String atom) {
        String rest;
        String tag = null;
        String id = null;
        HashSet<String> classes = new HashSet<String>();
        ArrayList<AttributeSelector> attributeSelectors = new ArrayList<AttributeSelector>();
        ArrayList<Pseudo> pseudos = new ArrayList<Pseudo>();
        PseudoElement pseudoElement = null;
        int firstSpecial = -1;
        for (char c : new char[]{'#', '.', '[', ':'}) {
            int idx = atom.indexOf(c);
            if (idx == -1 || firstSpecial != -1 && idx >= firstSpecial) continue;
            firstSpecial = idx;
        }
        if (firstSpecial == -1) {
            tag = atom.isBlank() ? null : atom;
            rest = "";
        } else {
            String maybeTag = atom.substring(0, firstSpecial).trim();
            tag = maybeTag.isEmpty() ? null : maybeTag;
            rest = atom.substring(firstSpecial);
        }
        Pattern token = Pattern.compile("(#(?<id>(?:\\\\.|[\\w-])+))|(\\.(?<cls>(?:\\\\.|[\\w-])+))|(\\[(?<attrName>[\\w-]+)(?:\\s*(?<attrOperator>~=|\\|=|\\^=|\\$=|\\*=|=)\\s*(?<attrValue>\"[^\"]*\"|'[^']*'|[^]]+))?])|(?<pseudoColon>::?)(?<pseudoName>[\\w-]+)(?:\\((?<pseudoExpr>[^)]*)\\))?");
        Matcher m = token.matcher(rest);
        int cursor = 0;
        while (m.find()) {
            if (m.start() > cursor && !rest.substring(cursor, m.start()).isBlank()) {
                Selector.logSelectorDiagnostic("fragment", atom, "unrecognized selector fragment=" + AuiLog.compact(rest.substring(cursor, m.start())));
            }
            cursor = m.end();
            String gid = m.group("id");
            if (gid != null) {
                id = Selector.unescapeCssIdentifier(gid);
                continue;
            }
            String gcls = m.group("cls");
            if (gcls != null) {
                classes.add(Selector.unescapeCssIdentifier(gcls));
                continue;
            }
            String attrName = m.group("attrName");
            if (attrName != null) {
                String v = m.group("attrValue");
                String operator = m.group("attrOperator");
                if (v != null && ((v = v.trim()).startsWith("\"") && v.endsWith("\"") || v.startsWith("'") && v.endsWith("'"))) {
                    v = v.substring(1, v.length() - 1);
                }
                boolean insensitive = false;
                if (operator != null && v != null && v.matches("(?s).*\\s+[iI]$")) {
                    insensitive = true;
                    v = v.substring(0, v.length() - 1).trim();
                } else if (operator != null && v != null && v.matches("(?s).*\\s+[sS]$")) {
                    v = v.substring(0, v.length() - 1).trim();
                }
                attributeSelectors.add(new AttributeSelector(attrName, operator, v, insensitive));
                continue;
            }
            String pseudoName = m.group("pseudoName");
            if (pseudoName == null) continue;
            String normalized = pseudoName.toLowerCase(Locale.ROOT);
            if ("before".equals(normalized) || "after".equals(normalized)) {
                pseudoElement = "before".equals(normalized) ? PseudoElement.BEFORE : PseudoElement.AFTER;
                continue;
            }
            if (!Selector.isSupportedPseudo(normalized)) {
                Selector.logSelectorDiagnostic("pseudo", atom, "unsupported pseudo-class=" + normalized);
            }
            pseudos.add(new Pseudo(normalized, m.group("pseudoExpr")));
        }
        if (cursor < rest.length() && !rest.substring(cursor).isBlank()) {
            Selector.logSelectorDiagnostic("fragment", atom, "trailing selector fragment=" + AuiLog.compact(rest.substring(cursor)));
        }
        return new ParsedAtom(new Component(tag, id, classes.isEmpty() ? null : classes, attributeSelectors.isEmpty() ? null : attributeSelectors, pseudos.isEmpty() ? null : pseudos), pseudoElement);
    }

    private static boolean isSupportedPseudo(String name) {
        return SUPPORTED_PSEUDOS.contains(name);
    }

    private static void logSelectorDiagnostic(String kind, String selector, String detail) {
        String key = kind + "|" + selector + "|" + detail;
        if (!SELECTOR_DIAGNOSTICS.add(key)) {
            return;
        }
        ApricityUI.LOGGER.warn("[AUI CSS] selector diagnostic kind={} selector={} detail={}", new Object[]{kind, AuiLog.compact(selector), detail});
    }

    private static String unescapeCssIdentifier(String value) {
        if (value == null || value.indexOf(92) < 0) {
            return value;
        }
        StringBuilder builder = new StringBuilder(value.length());
        boolean escape = false;
        for (int i = 0; i < value.length(); ++i) {
            char ch = value.charAt(i);
            if (escape) {
                builder.append(ch);
                escape = false;
                continue;
            }
            if (ch == '\\') {
                escape = true;
                continue;
            }
            builder.append(ch);
        }
        if (escape) {
            builder.append('\\');
        }
        return builder.toString();
    }

    public static List<Element> querySelectorAll(Element root, String selectorStr) {
        ArrayList<Element> results = new ArrayList<Element>();
        List groups = SELECTOR_CACHE.computeIfAbsent(selectorStr, Selector::parseGroup);
        Selector.searchElements(root, groups, results);
        return results;
    }

    private static void searchElements(Element current, List<CompiledSelector> selectors, List<Element> results) {
        if (current == null) {
            return;
        }
        for (CompiledSelector sel : selectors) {
            if (!Selector.isMatch(current, sel)) continue;
            results.add(current);
            break;
        }
        for (Element child : current.children) {
            Selector.searchElements(child, selectors, results);
        }
    }

    public static Element querySelector(Element root, String selectorStr) {
        List groups = SELECTOR_CACHE.computeIfAbsent(selectorStr, Selector::parseGroup);
        return Selector.findFirstMatch(root, groups);
    }

    private static Element findFirstMatch(Element current, List<CompiledSelector> selectors) {
        if (current == null) {
            return null;
        }
        for (CompiledSelector sel : selectors) {
            if (!Selector.isMatch(current, sel)) continue;
            return current;
        }
        for (Element child : current.children) {
            Element found = Selector.findFirstMatch(child, selectors);
            if (found == null) continue;
            return found;
        }
        return null;
    }

    public static List<DebugStyleBlock> getDebugStyles(Element element) {
        record DebugMatch(CSS.DebugRule rule, Specificity specificity) {
        }
        ArrayList<DebugMatch> matches = new ArrayList<DebugMatch>();
        for (CSS.DebugRule debugRule : element.document.CSSDebugRules) {
            String selectorStr = debugRule.selector();
            int finalOrder = debugRule.order();
            List groups = SELECTOR_CACHE.computeIfAbsent(selectorStr, Selector::parseGroup);
            Specificity matchedSpecificity = null;
            for (CompiledSelector compiledSelector : groups) {
                if (!Selector.isMatch(element, compiledSelector)) continue;
                Specificity specificity = compiledSelector.specificity(finalOrder);
                if (matchedSpecificity != null && specificity.compareTo(matchedSpecificity) <= 0) continue;
                matchedSpecificity = specificity;
            }
            if (matchedSpecificity == null) continue;
            matches.add(new DebugMatch(debugRule, matchedSpecificity));
        }
        matches.sort((a, b) -> b.specificity.compareTo(a.specificity));
        record Winner(int ruleOrder, Specificity specificity, boolean important) {
        }
        HashMap<String, Winner> winners = new HashMap<String, Winner>();
        for (DebugMatch match : matches) {
            for (Map.Entry<String, CSS.Declaration> entry : match.rule.properties().entrySet()) {
                String property = Selector.cascadePropertyKey(entry.getKey());
                CSS.Declaration declaration = entry.getValue();
                Winner winner = (Winner)winners.get(property);
                if (winner != null && (!declaration.important() || winner.important()) && (declaration.important() != winner.important() || match.specificity.compareTo(winner.specificity()) <= 0)) continue;
                winners.put(property, new Winner(match.rule.order(), match.specificity, declaration.important()));
            }
        }
        Map<String, Boolean> map = Selector.inlinePropertyPriorities(element.getAttribute("style"));
        ArrayList<DebugStyleBlock> result = new ArrayList<DebugStyleBlock>();
        for (DebugMatch match : matches) {
            LinkedHashMap<String, DebugDeclaration> declarations = new LinkedHashMap<String, DebugDeclaration>();
            for (Map.Entry entry : match.rule.properties().entrySet()) {
                String property = (String)entry.getKey();
                CSS.Declaration declaration = (CSS.Declaration)entry.getValue();
                Winner winner = (Winner)winners.get(Selector.cascadePropertyKey(property));
                Boolean inlineImportant = map.get(Selector.cascadePropertyKey(property));
                boolean inlineWins = inlineImportant != null && (inlineImportant != false || !declaration.important());
                boolean overridden = inlineWins || winner == null || winner.ruleOrder() != match.rule.order();
                declarations.put(property, new DebugDeclaration(declaration.value(), declaration.important(), overridden));
            }
            result.add(new DebugStyleBlock(match.rule.sourcePath(), match.rule.selector(), match.rule.order(), declarations));
        }
        return result;
    }

    private static Map<String, Boolean> inlinePropertyPriorities(String style) {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        if (style == null || style.isBlank()) {
            return result;
        }
        for (String declaration : style.split(";")) {
            String property;
            int colon = declaration.indexOf(58);
            if (colon <= 0 || (property = Selector.cascadePropertyKey(declaration.substring(0, colon))).isBlank()) continue;
            result.put(property, declaration.substring(colon + 1).trim().toLowerCase(Locale.ROOT).endsWith("!important"));
        }
        return result;
    }

    private static String cascadePropertyKey(String raw) {
        String property = raw == null ? "" : raw.trim();
        return property.startsWith("--") ? property : property.toLowerCase(Locale.ROOT);
    }

    public static final class Index {
        private final Map<String, List<IndexedRule>> byId = new HashMap<String, List<IndexedRule>>();
        private final Map<String, List<IndexedRule>> byClass = new HashMap<String, List<IndexedRule>>();
        private final Map<String, List<IndexedRule>> byTag = new HashMap<String, List<IndexedRule>>();
        private final Map<String, List<IndexedRule>> byPseudo = new HashMap<String, List<IndexedRule>>();
        private final Map<String, List<IndexedRule>> byAttr = new HashMap<String, List<IndexedRule>>();
        private final Set<String> pseudosAffectingDescendants = new HashSet<String>();
        private final List<IndexedRule> always = new ArrayList<IndexedRule>();
        private final ArrayList<IndexedRule> scratchCandidates = new ArrayList();
        private final IdentityHashMap<IndexedRule, Boolean> scratchSeen = new IdentityHashMap();

        private Index() {
        }

        public static Index build(Map<String, Map<String, CSS.Declaration>> cssCache) {
            Index index = new Index();
            if (cssCache == null || cssCache.isEmpty()) {
                return index;
            }
            int order = 0;
            for (Map.Entry<String, Map<String, CSS.Declaration>> entry : cssCache.entrySet()) {
                String selectorStr = entry.getKey();
                Map<String, CSS.Declaration> styles = entry.getValue();
                if (selectorStr == null || selectorStr.isBlank() || styles == null) {
                    ++order;
                    continue;
                }
                List groups = SELECTOR_CACHE.computeIfAbsent(selectorStr, Selector::parseGroup);
                for (CompiledSelector sel : groups) {
                    Specificity specificity = sel.specificity(order);
                    IndexedRule rule = new IndexedRule(selectorStr, sel, specificity, styles);
                    index.addRule(rule);
                }
                ++order;
            }
            return index;
        }

        private void addRule(IndexedRule rule) {
            List<Component> components = rule.selector.components;
            this.recordAncestorPseudoDependencies(rule.selector);
            Component last = components.get(components.size() - 1);
            if (last.id != null) {
                this.byId.computeIfAbsent(last.id, ignored -> new ArrayList()).add(rule);
                return;
            }
            if (last.classes != null && !last.classes.isEmpty()) {
                for (String cls : last.classes) {
                    if (cls == null || cls.isBlank()) continue;
                    this.byClass.computeIfAbsent(cls, ignored -> new ArrayList()).add(rule);
                }
                return;
            }
            if (last.tag != null && !last.tag.isBlank() && !last.tag.equals("*")) {
                this.byTag.computeIfAbsent(last.tag.toLowerCase(Locale.ROOT), ignored -> new ArrayList()).add(rule);
                return;
            }
            if (last.pseudos != null && !last.pseudos.isEmpty()) {
                for (Pseudo p : last.pseudos) {
                    if (p == null || p.name == null || p.name.isBlank()) continue;
                    this.byPseudo.computeIfAbsent(p.name, ignored -> new ArrayList()).add(rule);
                }
                return;
            }
            if (last.attributeSelectors != null && !last.attributeSelectors.isEmpty()) {
                for (AttributeSelector attribute : last.attributeSelectors) {
                    if (attribute.name == null || attribute.name.isBlank()) continue;
                    this.byAttr.computeIfAbsent(attribute.name, ignored -> new ArrayList()).add(rule);
                }
                return;
            }
            this.always.add(rule);
        }

        private void recordAncestorPseudoDependencies(CompiledSelector selector) {
            if (selector == null || selector.components == null || selector.components.size() <= 1) {
                return;
            }
            for (int i = 0; i < selector.components.size() - 1; ++i) {
                Component component = selector.components.get(i);
                if (component == null || component.pseudos == null || component.pseudos.isEmpty()) continue;
                for (Pseudo pseudo : component.pseudos) {
                    if (pseudo == null || pseudo.name == null || pseudo.name.isBlank()) continue;
                    this.pseudosAffectingDescendants.add(pseudo.name);
                }
            }
        }

        public boolean pseudoCanAffectDescendants(String pseudoName) {
            return pseudoName != null && this.pseudosAffectingDescendants.contains(pseudoName);
        }

        public HashMap<String, CSS.Declaration> match(Element element) {
            String tag;
            Set<String> classes;
            this.scratchCandidates.clear();
            this.scratchSeen.clear();
            if (element == null || element.document == null) {
                return new HashMap<String, CSS.Declaration>();
            }
            this.addCandidates(this.always);
            String id = element.id;
            if (id != null && !id.isBlank()) {
                this.addCandidates(this.byId.get(id));
            }
            if ((classes = element.getClassNames()) != null && !classes.isEmpty()) {
                for (String string : classes) {
                    this.addCandidates(this.byClass.get(string));
                }
            }
            if ((tag = element.tagName) != null && !tag.isBlank()) {
                this.addCandidates(this.byTag.get(tag.toLowerCase(Locale.ROOT)));
            }
            for (String string : SUPPORTED_PSEUDOS) {
                if (!Pseudo.mayMatch(string, element)) continue;
                this.addCandidates(this.byPseudo.get(string));
            }
            HashMap<String, String> hashMap = element.getAttributes();
            if (hashMap != null && !hashMap.isEmpty()) {
                for (String string : hashMap.keySet()) {
                    this.addCandidates(this.byAttr.get(string));
                }
            }
            ArrayList<MatchedRule> arrayList = new ArrayList<MatchedRule>();
            for (IndexedRule rule : this.scratchCandidates) {
                if (rule == null || rule.selector.pseudoElement != null || !Selector.isMatch(element, rule.selector)) continue;
                arrayList.add(new MatchedRule(rule.specificity, rule.styles));
            }
            arrayList.sort(Comparator.comparing(m -> m.specificity));
            LinkedHashMap<String, CSS.Declaration> linkedHashMap = new LinkedHashMap<String, CSS.Declaration>();
            ArrayList<MatchedRule> importantRules = new ArrayList<MatchedRule>();
            for (MatchedRule rule : arrayList) {
                boolean hasImportant = false;
                for (Map.Entry<String, CSS.Declaration> entry : rule.styles.entrySet()) {
                    CSS.Declaration declaration = entry.getValue();
                    if (declaration.important()) {
                        hasImportant = true;
                        continue;
                    }
                    linkedHashMap.put(entry.getKey(), declaration);
                }
                if (!hasImportant) continue;
                importantRules.add(rule);
            }
            for (MatchedRule rule : importantRules) {
                for (Map.Entry<String, CSS.Declaration> e : rule.styles.entrySet()) {
                    CSS.Declaration declaration = e.getValue();
                    if (!declaration.important()) continue;
                    linkedHashMap.put(e.getKey(), declaration);
                }
            }
            return linkedHashMap;
        }

        public HashMap<String, CSS.Declaration> matchPseudoElement(Element element, PseudoElement pseudoElement) {
            String tag;
            Set<String> classes;
            this.scratchCandidates.clear();
            this.scratchSeen.clear();
            if (element == null || element.document == null || pseudoElement == null) {
                return new HashMap<String, CSS.Declaration>();
            }
            this.addCandidates(this.always);
            String id = element.id;
            if (id != null && !id.isBlank()) {
                this.addCandidates(this.byId.get(id));
            }
            if ((classes = element.getClassNames()) != null && !classes.isEmpty()) {
                for (String cls : classes) {
                    this.addCandidates(this.byClass.get(cls));
                }
            }
            if ((tag = element.tagName) != null && !tag.isBlank()) {
                this.addCandidates(this.byTag.get(tag.toLowerCase(Locale.ROOT)));
            }
            ArrayList<MatchedRule> matched = new ArrayList<MatchedRule>();
            for (IndexedRule rule : this.scratchCandidates) {
                if (rule == null || rule.selector.pseudoElement != pseudoElement || !Selector.isMatch(element, rule.selector)) continue;
                matched.add(new MatchedRule(rule.specificity, rule.styles));
            }
            matched.sort(Comparator.comparing(m -> m.specificity));
            LinkedHashMap<String, CSS.Declaration> finalStyles = new LinkedHashMap<String, CSS.Declaration>();
            ArrayList<MatchedRule> importantRules = new ArrayList<MatchedRule>();
            for (MatchedRule rule : matched) {
                boolean hasImportant = false;
                for (Map.Entry<String, CSS.Declaration> entry : rule.styles.entrySet()) {
                    CSS.Declaration declaration = entry.getValue();
                    if (declaration.important()) {
                        hasImportant = true;
                        continue;
                    }
                    finalStyles.put(entry.getKey(), declaration);
                }
                if (!hasImportant) continue;
                importantRules.add(rule);
            }
            for (MatchedRule rule : importantRules) {
                for (Map.Entry<String, CSS.Declaration> e : rule.styles.entrySet()) {
                    CSS.Declaration declaration = e.getValue();
                    if (!declaration.important()) continue;
                    finalStyles.put(e.getKey(), declaration);
                }
            }
            return finalStyles;
        }

        private void addCandidates(List<IndexedRule> rules) {
            if (rules == null || rules.isEmpty()) {
                return;
            }
            for (IndexedRule rule : rules) {
                if (rule == null || this.scratchSeen.put(rule, Boolean.TRUE) != null) continue;
                this.scratchCandidates.add(rule);
            }
        }

        private record IndexedRule(String selectorStr, CompiledSelector selector, Specificity specificity, Map<String, CSS.Declaration> styles) {
        }
    }

    public static enum PseudoElement {
        BEFORE,
        AFTER;

    }

    private record CompiledSelector(List<Component> components, List<Combinator> combinators, PseudoElement pseudoElement, int ids, int classesAndPseudos, int tags) {
        public Specificity specificity(int order) {
            return new Specificity(this.ids, this.classesAndPseudos, this.tags, order);
        }
    }

    private record Component(String tag, String id, Set<String> classes, List<AttributeSelector> attributeSelectors, List<Pseudo> pseudos) {
        public boolean matches(Element e) {
            if (e == null) {
                return false;
            }
            if (this.tag != null && !this.tag.equals("*") && !this.tag.equalsIgnoreCase(e.tagName)) {
                return false;
            }
            if (this.id != null && !this.id.equals(e.getAttribute("id"))) {
                return false;
            }
            if (this.classes != null && !e.getClassNames().containsAll(this.classes)) {
                return false;
            }
            if (this.attributeSelectors != null) {
                for (AttributeSelector attributeSelector : this.attributeSelectors) {
                    if (attributeSelector.matches(e)) continue;
                    return false;
                }
            }
            if (this.pseudos != null) {
                for (Pseudo p : this.pseudos) {
                    if (p.matches(e)) continue;
                    return false;
                }
            }
            return true;
        }
    }

    private static enum Combinator {
        DESCENDANT,
        CHILD,
        ADJACENT_SIBLING,
        GENERAL_SIBLING;

    }

    private record SpecificityParts(int ids, int classes, int tags) implements Comparable<SpecificityParts>
    {
        private static final SpecificityParts ZERO = new SpecificityParts(0, 0, 0);

        public SpecificityParts plus(SpecificityParts other) {
            return new SpecificityParts(this.ids + other.ids, this.classes + other.classes, this.tags + other.tags);
        }

        @Override
        public int compareTo(SpecificityParts other) {
            if (this.ids != other.ids) {
                return Integer.compare(this.ids, other.ids);
            }
            if (this.classes != other.classes) {
                return Integer.compare(this.classes, other.classes);
            }
            return Integer.compare(this.tags, other.tags);
        }
    }

    private record ParsedAtom(Component component, PseudoElement pseudoElement) {
    }

    private record Pseudo(String name, String expression) {
        public static boolean mayMatch(String name, Element e) {
            if (e == null) {
                return false;
            }
            return switch (name) {
                case "hover" -> e.isHover;
                case "active" -> e.isActive;
                case "focus", "focus-visible" -> e.isFocus;
                case "focus-within" -> Selector.isFocusWithin(e);
                case "disabled" -> e.isDisabled();
                case "enabled" -> {
                    if (!e.isDisabled()) {
                        yield true;
                    }
                    yield false;
                }
                case "required" -> e.hasAttribute("required");
                case "optional" -> {
                    if (!e.hasAttribute("required")) {
                        yield true;
                    }
                    yield false;
                }
                case "valid" -> e.isValid();
                case "invalid" -> {
                    if (e.isWillValidate() && !e.isValid()) {
                        yield true;
                    }
                    yield false;
                }
                case "in-range" -> {
                    if (e.isWillValidate() && !e.getValidity().rangeUnderflow && !e.getValidity().rangeOverflow) {
                        yield true;
                    }
                    yield false;
                }
                case "out-of-range" -> {
                    if (e.isWillValidate() && (e.getValidity().rangeUnderflow || e.getValidity().rangeOverflow)) {
                        yield true;
                    }
                    yield false;
                }
                case "read-only" -> e.hasAttribute("readonly");
                case "read-write" -> {
                    if (!e.hasAttribute("readonly") && e.isWillValidate()) {
                        yield true;
                    }
                    yield false;
                }
                case "placeholder-shown" -> {
                    if (e.hasAttribute("placeholder") && e.getValue().isEmpty()) {
                        yield true;
                    }
                    yield false;
                }
                case "empty" -> e.children.isEmpty();
                case "checked" -> {
                    if (e.getAttributes().containsKey("checked") || "OPTION".equalsIgnoreCase(e.tagName) || e.getAttributes().containsKey("selected")) {
                        yield true;
                    }
                    yield false;
                }
                case "root" -> {
                    if (e.parentElement == null) {
                        yield true;
                    }
                    yield false;
                }
                case "first-child", "last-child", "nth-child", "nth-last-child", "only-child", "first-of-type", "last-of-type", "only-of-type", "nth-of-type", "nth-last-of-type" -> {
                    if (e.parentElement != null) {
                        yield true;
                    }
                    yield false;
                }
                case "not", "is", "where" -> true;
                default -> false;
            };
        }

        public boolean matches(Element e) {
            if (e == null) {
                return false;
            }
            return switch (this.name) {
                case "root" -> {
                    if (e.parentElement == null) {
                        yield true;
                    }
                    yield false;
                }
                case "first-child" -> this.isSiblingIndex(e, 0);
                case "last-child" -> this.isSiblingIndex(e, -1);
                case "only-child" -> {
                    if (e.parentElement != null && e.parentElement.children.size() == 1) {
                        yield true;
                    }
                    yield false;
                }
                case "nth-child" -> this.matchNth(e, this.expression, false, false);
                case "nth-last-child" -> this.matchNth(e, this.expression, true, false);
                case "first-of-type" -> this.isTypeSiblingIndex(e, 0);
                case "last-of-type" -> this.isTypeSiblingIndex(e, -1);
                case "only-of-type" -> {
                    if (this.countSameTypeSiblings(e) == 1) {
                        yield true;
                    }
                    yield false;
                }
                case "nth-of-type" -> this.matchNth(e, this.expression, false, true);
                case "nth-last-of-type" -> this.matchNth(e, this.expression, true, true);
                case "hover" -> e.isHover;
                case "active" -> e.isActive;
                case "focus" -> e.isFocus;
                case "focus-visible" -> e.isFocus;
                case "focus-within" -> Selector.isFocusWithin(e);
                case "disabled" -> e.isDisabled();
                case "enabled" -> {
                    if (!e.isDisabled()) {
                        yield true;
                    }
                    yield false;
                }
                case "required" -> e.hasAttribute("required");
                case "optional" -> {
                    if (!e.hasAttribute("required")) {
                        yield true;
                    }
                    yield false;
                }
                case "valid" -> e.isValid();
                case "invalid" -> {
                    if (e.isWillValidate() && !e.isValid()) {
                        yield true;
                    }
                    yield false;
                }
                case "in-range" -> {
                    if (e.isWillValidate() && !e.getValidity().rangeUnderflow && !e.getValidity().rangeOverflow) {
                        yield true;
                    }
                    yield false;
                }
                case "out-of-range" -> {
                    if (e.isWillValidate() && (e.getValidity().rangeUnderflow || e.getValidity().rangeOverflow)) {
                        yield true;
                    }
                    yield false;
                }
                case "read-only" -> e.hasAttribute("readonly");
                case "read-write" -> {
                    if (!e.hasAttribute("readonly") && e.isWillValidate()) {
                        yield true;
                    }
                    yield false;
                }
                case "placeholder-shown" -> {
                    if (e.hasAttribute("placeholder") && e.getValue().isEmpty()) {
                        yield true;
                    }
                    yield false;
                }
                case "empty" -> e.children.isEmpty();
                case "checked" -> this.isChecked(e);
                case "not" -> {
                    if (!this.matchesAny(e, this.expression)) {
                        yield true;
                    }
                    yield false;
                }
                case "is", "where" -> this.matchesAny(e, this.expression);
                default -> false;
            };
        }

        private boolean matchesAny(Element element, String selectorList) {
            if (selectorList == null || selectorList.isBlank()) {
                return false;
            }
            for (CompiledSelector selector : Selector.parseGroup(selectorList)) {
                if (selector.pseudoElement != null || !Selector.isMatch(element, selector)) continue;
                return true;
            }
            return false;
        }

        public SpecificityParts specificity() {
            if ("where".equals(this.name)) {
                return SpecificityParts.ZERO;
            }
            if ("is".equals(this.name) || "not".equals(this.name)) {
                SpecificityParts result = SpecificityParts.ZERO;
                if (this.expression == null || this.expression.isBlank()) {
                    return result;
                }
                for (CompiledSelector selector : Selector.parseGroup(this.expression)) {
                    SpecificityParts candidate = new SpecificityParts(selector.ids, selector.classesAndPseudos, selector.tags);
                    if (candidate.compareTo(result) <= 0) continue;
                    result = candidate;
                }
                return result;
            }
            return new SpecificityParts(0, 1, 0);
        }

        private boolean isChecked(Element e) {
            String type;
            if ("INPUT".equalsIgnoreCase(e.tagName) && ("checkbox".equalsIgnoreCase(type = e.getAttribute("type")) || "radio".equalsIgnoreCase(type))) {
                return e.isChecked();
            }
            if (e.getAttributes().containsKey("checked")) {
                String v = e.getAttribute("checked");
                if (v == null || v.isBlank()) {
                    return true;
                }
                return !"false".equalsIgnoreCase(v) && !"0".equals(v);
            }
            if ("OPTION".equalsIgnoreCase(e.tagName)) {
                if (e.getAttributes().containsKey("selected")) {
                    return true;
                }
                Element parent = e.parentElement;
                if (parent != null && "SELECT".equalsIgnoreCase(parent.tagName)) {
                    String pv = parent.getAttribute("value");
                    String ov = e.getAttribute("value");
                    return pv != null && pv.equals(ov);
                }
            }
            return false;
        }

        private boolean isSiblingIndex(Element e, int target) {
            if (e.parentElement == null) {
                return false;
            }
            ArrayList<Element> siblings = e.parentElement.children;
            int idx = siblings.indexOf(e);
            return target == -1 ? idx == siblings.size() - 1 : idx == target;
        }

        private boolean isTypeSiblingIndex(Element e, int target) {
            if (e.parentElement == null) {
                return false;
            }
            List<Element> siblings = this.sameTypeSiblings(e);
            int index = siblings.indexOf(e);
            return target == -1 ? index == siblings.size() - 1 : index == target;
        }

        private int countSameTypeSiblings(Element e) {
            return e.parentElement == null ? 0 : this.sameTypeSiblings(e).size();
        }

        private List<Element> sameTypeSiblings(Element e) {
            ArrayList<Element> result = new ArrayList<Element>();
            if (e.parentElement == null) {
                return result;
            }
            for (Element sibling : e.parentElement.children) {
                if (!sibling.tagName.equalsIgnoreCase(e.tagName)) continue;
                result.add(sibling);
            }
            return result;
        }

        private boolean matchNth(Element e, String expr, boolean fromEnd, boolean ofType) {
            String normalized;
            if (e.parentElement == null) {
                return false;
            }
            List<Element> siblings = ofType ? this.sameTypeSiblings(e) : e.parentElement.children;
            int index = siblings.indexOf(e);
            if (index < 0) {
                return false;
            }
            int pos = fromEnd ? siblings.size() - index : index + 1;
            String string = normalized = expr == null ? "" : expr.replaceAll("\\s+", "").toLowerCase(Locale.ROOT);
            if ("odd".equals(normalized)) {
                return pos % 2 != 0;
            }
            if ("even".equals(normalized)) {
                return pos % 2 == 0;
            }
            try {
                return Integer.parseInt(normalized) == pos;
            }
            catch (NumberFormatException ignored) {
                int b;
                Matcher matcher = Pattern.compile("([+-]?\\d*)n(?:([+-]\\d+))?").matcher(normalized);
                if (!matcher.matches()) {
                    return false;
                }
                String coefficient = matcher.group(1);
                int a = coefficient.isEmpty() || "+".equals(coefficient) ? 1 : ("-".equals(coefficient) ? -1 : Integer.parseInt(coefficient));
                int n = b = matcher.group(2) == null ? 0 : Integer.parseInt(matcher.group(2));
                if (a == 0) {
                    return pos == b;
                }
                int delta = pos - b;
                return a > 0 ? delta >= 0 && delta % a == 0 : delta <= 0 && delta % a == 0;
            }
        }
    }

    private record AttributeSelector(String name, String operator, String expected, boolean caseInsensitive) {
        public boolean matches(Element element) {
            String target;
            String actual = null;
            boolean present = false;
            for (Map.Entry<String, String> entry : element.getAttributes().entrySet()) {
                if (!entry.getKey().equalsIgnoreCase(this.name)) continue;
                present = true;
                actual = entry.getValue();
                break;
            }
            if (!present) {
                return false;
            }
            if (this.operator == null) {
                return true;
            }
            String value = actual == null ? "" : actual;
            String string = target = this.expected == null ? "" : this.expected;
            if (this.caseInsensitive) {
                value = value.toLowerCase(Locale.ROOT);
                target = target.toLowerCase(Locale.ROOT);
            }
            return switch (this.operator) {
                case "=" -> value.equals(target);
                case "~=" -> Arrays.stream(value.trim().split("\\s+")).anyMatch(target::equals);
                case "|=" -> {
                    if (value.equals(target) || value.startsWith(target + "-")) {
                        yield true;
                    }
                    yield false;
                }
                case "^=" -> value.startsWith(target);
                case "$=" -> value.endsWith(target);
                case "*=" -> value.contains(target);
                default -> false;
            };
        }
    }

    public record Specificity(int ids, int classes, int tags, int order) implements Comparable<Specificity>
    {
        @Override
        public int compareTo(Specificity o) {
            if (this.ids != o.ids) {
                return Integer.compare(this.ids, o.ids);
            }
            if (this.classes != o.classes) {
                return Integer.compare(this.classes, o.classes);
            }
            if (this.tags != o.tags) {
                return Integer.compare(this.tags, o.tags);
            }
            return Integer.compare(this.order, o.order);
        }
    }

    public record DebugDeclaration(String value, boolean important, boolean overridden) {
        public String displayValue() {
            return this.value + (this.important ? " !important" : "");
        }
    }

    public record DebugStyleBlock(String sourcePath, String selector, int ruleOrder, Map<String, DebugDeclaration> declarations) {
        public Map<String, String> styles() {
            LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
            this.declarations.forEach((property, declaration) -> result.put((String)property, declaration.displayValue()));
            return result;
        }
    }

    private record MatchedRule(Specificity specificity, Map<String, CSS.Declaration> styles) {
    }
}

