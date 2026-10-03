/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.dev.DevToolsLogBridge;
import com.sighs.apricityui.dev.devtools.DevToolsController;
import com.sighs.apricityui.dev.devtools.DevToolsDom;
import com.sighs.apricityui.dom.DocumentFragment;
import com.sighs.apricityui.dom.TextNode;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.render.Operation;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DevToolsConsole {
    private static final int MAX_LOG_ENTRIES = 2000;
    private static final int MAX_EXTERNAL_LOGS_PER_TICK = 128;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private static final Pattern SELECT_PATTERN = Pattern.compile("^select\\((\\d+)\\)$", 2);
    private static final Pattern QUERY_PATTERN = Pattern.compile("^(\\$\\$?|querySelectorAll)\\((.+)\\)$", 2);
    private static final Pattern ARITHMETIC_PATTERN = Pattern.compile("^(\\d+)\\s*([+\\-*/])\\s*(\\d+)$");
    private final DevToolsController controller;
    private final ArrayList<LogEntry> logs = new ArrayList();
    private final ArrayList<String> history = new ArrayList();
    private Filter filter = Filter.ALL;
    private int historyIndex = -1;
    private boolean wrap;
    private Document boundDocument;
    private boolean initialized;
    private int renderedLogCount = -1;
    private Filter renderedFilter;
    private String renderedSearch = "";
    private boolean logWindowTruncated;
    private int pendingAppendedLogCount;
    private int pendingRemovedLogCount;

    DevToolsConsole(DevToolsController controller) {
        this.controller = controller;
    }

    void bind() {
        Document document = this.controller.toolDocument();
        if (document == null || document.body == null) {
            return;
        }
        if (this.boundDocument != document) {
            this.bindFilters(document);
            this.bindSearch(document);
            this.bindActions(document);
            this.bindInput(document);
            this.bindHints(document);
            this.boundDocument = document;
            this.renderedLogCount = -1;
            this.renderedFilter = null;
            this.renderedSearch = "";
            this.logWindowTruncated = false;
            this.pendingAppendedLogCount = 0;
            this.pendingRemovedLogCount = 0;
        }
        if (!this.initialized) {
            this.initialized = true;
            this.addLog("system", "PRISM//INSPECTOR v1.0.0 \u00b7 Connected to page", "system", null);
            this.addLog("info", "Page loaded \u00b7 " + DevToolsConsole.countNodes(this.controller.targetDocument()) + " nodes parsed", "lifecycle", null);
            this.addLog("info", "DOM ready \u00b7 CSS computed", "lifecycle", null);
            this.addLog("log", "Welcome! Type help to see available commands.", "system", null);
            this.addLog("warn", "Deprecated API usage detected at header.site-header", "compat", "  at checkCompat (audit.js:128)\n  at onPageLoad (lifecycle.js:42)");
            this.addLog("info", "3 stylesheets loaded \u00b7 0 blocking", "network", null);
        }
        if (this.controller.isConsoleMode()) {
            this.drainExternalLogs();
            this.render();
        }
    }

    void drainExternalLogs() {
        List<DevToolsLogBridge.ConsoleLog> externalLogs = DevToolsLogBridge.drain(128);
        if (externalLogs.isEmpty()) {
            return;
        }
        for (DevToolsLogBridge.ConsoleLog external : externalLogs) {
            this.appendLog(external.level(), external.text(), external.source(), external.stack(), external.time());
        }
        this.render();
    }

    private void bindFilters(Document document) {
        for (Element button : document.querySelectorAll(".console-filter")) {
            button.addEventListener("click", event -> this.setFilter(Filter.parse(button.getAttribute("data-level"))));
        }
    }

    private void bindSearch(Document document) {
        Element search = document.querySelector("#consoleSearch");
        if (search == null) {
            return;
        }
        search.addEventListener("input", event -> this.render());
        search.addEventListener("change", event -> this.render());
    }

    private void bindActions(Document document) {
        Element clearButton;
        Element wrapButton = document.querySelector("#consoleWrapBtn");
        if (wrapButton != null) {
            wrapButton.addEventListener("click", event -> this.toggleWrap());
        }
        if ((clearButton = document.querySelector("#consoleClearBtn")) != null) {
            clearButton.addEventListener("click", event -> this.clear());
        }
    }

    private void bindInput(Document document) {
        Element input = document.querySelector("#consoleInput");
        if (input == null) {
            return;
        }
        input.addEventListener("keydown", this::handleInputKey);
    }

    private void bindHints(Document document) {
        for (Element hint : document.querySelectorAll(".hint-chip")) {
            hint.addEventListener("click", event -> {
                Element input = document.querySelector("#consoleInput");
                if (input == null) {
                    return;
                }
                input.setValue(hint.getAttribute("data-hint"));
                input.focus();
            });
        }
    }

    private void handleInputKey(Event event) {
        if (!(event instanceof KeyEvent)) {
            return;
        }
        KeyEvent keyEvent = (KeyEvent)event;
        Document document = this.controller.toolDocument();
        if (document == null) {
            return;
        }
        Element input = document.querySelector("#consoleInput");
        if (input == null) {
            return;
        }
        if ("Enter".equals(keyEvent.key)) {
            String command = DevToolsDom.value(input).trim();
            if (!command.isEmpty()) {
                this.history.remove(command);
                this.history.add(0, command);
                this.historyIndex = -1;
                this.execute(command);
                input.setValue("");
            }
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        if ("ArrowUp".equals(keyEvent.key)) {
            if (this.historyIndex < this.history.size() - 1) {
                ++this.historyIndex;
            }
            input.setValue(this.historyIndex >= 0 ? this.history.get(this.historyIndex) : "");
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        if ("ArrowDown".equals(keyEvent.key)) {
            if (this.historyIndex > 0) {
                --this.historyIndex;
                input.setValue(this.history.get(this.historyIndex));
            } else {
                this.historyIndex = -1;
                input.setValue("");
            }
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        if ("KeyL".equals(keyEvent.code) && keyEvent.controlKey) {
            this.clear();
            event.preventDefault();
            event.stopPropagation();
        }
    }

    private void setFilter(Filter next) {
        this.filter = next == null ? Filter.ALL : next;
        Document document = this.controller.toolDocument();
        if (document == null) {
            return;
        }
        for (Element button : document.querySelectorAll(".console-filter")) {
            Object classes = "console-filter";
            String level = button.getAttribute("data-level");
            if ("info".equals(level) || "warn".equals(level) || "error".equals(level)) {
                classes = (String)classes + " " + level;
            }
            if (this.filter.id.equals(level)) {
                classes = (String)classes + " active";
            }
            button.setAttribute("class", (String)classes);
        }
        Element label = document.querySelector("#consoleStatusFilter");
        if (label != null) {
            label.setTextContent("FILTER: " + this.filter.id.toUpperCase(Locale.ROOT));
        }
        this.render();
    }

    private void toggleWrap() {
        Element container;
        this.wrap = !this.wrap;
        Document document = this.controller.toolDocument();
        Element element = container = document == null ? null : document.querySelector("#consoleLogs");
        if (container != null) {
            container.setAttribute("style", "white-space:" + (this.wrap ? "pre-wrap" : "pre") + ";");
            DevToolsDom.markDirty(document);
        }
        this.controller.showToast(this.wrap ? "Word wrap ON" : "Word wrap OFF");
    }

    private void clear() {
        this.logs.clear();
        this.logWindowTruncated = false;
        this.pendingAppendedLogCount = 0;
        this.pendingRemovedLogCount = 0;
        this.addLog("system", "Console cleared.", "system", null);
    }

    private void execute(String command) {
        this.addLog("command", command, "input", null);
        String trimmed = command.trim();
        String[] parts = trimmed.split("\\s+", 2);
        String name = parts[0].toLowerCase(Locale.ROOT);
        try {
            switch (name) {
                case "help": {
                    this.addLog("info", DevToolsConsole.helpText(), "system", null);
                    break;
                }
                case "clear": 
                case "cls": {
                    this.clear();
                    break;
                }
                case "select": {
                    this.select(trimmed);
                    break;
                }
                case "inspect": {
                    this.controller.togglePickModeFromConsole();
                    this.addLog("info", this.controller.isPickMode() ? "Inspect mode enabled" : "Inspect mode disabled", "command", null);
                    break;
                }
                case "$": 
                case "$$": 
                case "queryselectorall": {
                    this.query(trimmed);
                    break;
                }
                case "copy": {
                    this.copy(trimmed);
                    break;
                }
                case "dir": {
                    this.dir(trimmed);
                    break;
                }
                case "table": {
                    this.addLog("info", "Table view (simulated)", "command", null);
                    break;
                }
                case "keys": {
                    this.keys(trimmed);
                    break;
                }
                case "count": {
                    this.addLog("info", "Total nodes: " + DevToolsConsole.countNodes(this.controller.targetDocument()), "stats", null);
                    break;
                }
                case "tree": {
                    this.addLog("result", DevToolsConsole.treeText(this.controller.targetDocument()), "tree", null);
                    break;
                }
                case "echo": {
                    this.addLog("log", DevToolsConsole.argument(trimmed), "echo", null);
                    break;
                }
                case "warn": {
                    this.addLog("warn", DevToolsConsole.argument(trimmed), "warn", null);
                    break;
                }
                case "error": {
                    this.addLog("error", DevToolsConsole.argument(trimmed), "error", "  at executeCommand (console.js:42)\n  at handleConsoleKey (console.js:18)");
                    break;
                }
                default: {
                    this.evaluate(trimmed);
                    break;
                }
            }
        }
        catch (RuntimeException exception) {
            this.addLog("error", exception.getMessage() == null ? exception.toString() : exception.getMessage(), "error", null);
        }
    }

    private void select(String command) {
        Matcher matcher = SELECT_PATTERN.matcher(command);
        if (!matcher.matches()) {
            this.addLog("error", "Usage: select(<nodeId>)", "command", null);
            return;
        }
        int index = Integer.parseInt(matcher.group(1));
        Element selected = DevToolsConsole.elementAt(this.controller.targetDocument(), index);
        if (selected == null) {
            this.addLog("error", "Node #" + index + " not found", "command", null);
            return;
        }
        this.controller.selectElement(selected);
        this.addLog("success", "Selected <" + selected.tagName.toLowerCase(Locale.ROOT) + "> #" + index, "command", null);
    }

    private void query(String command) {
        Document target;
        Matcher matcher = QUERY_PATTERN.matcher(command);
        if (!matcher.matches()) {
            this.addLog("error", "Usage: $(css) or $$(css)", "command", null);
            return;
        }
        String selector = matcher.group(2).trim();
        if (selector.startsWith("\"") && selector.endsWith("\"") || selector.startsWith("'") && selector.endsWith("'")) {
            selector = selector.substring(1, selector.length() - 1);
        }
        if ((target = this.controller.targetDocument()) == null) {
            this.addLog("warn", "No matching document", "query", null);
            return;
        }
        List<Element> matches = target.querySelectorAll(selector);
        if ("$".equals(matcher.group(1))) {
            this.addLog(matches.isEmpty() ? "warn" : "result", (String)(matches.isEmpty() ? "No matching element" : "<" + matches.get((int)0).tagName.toLowerCase(Locale.ROOT) + ">"), "query", null);
            return;
        }
        StringBuilder result = new StringBuilder("Found ").append(matches.size()).append(" element(s):");
        for (Element match : matches) {
            result.append('\n').append("<").append(match.tagName.toLowerCase(Locale.ROOT)).append("> #").append(DevToolsConsole.shortIndex(target, match));
        }
        this.addLog("result", result.toString(), "query", null);
    }

    private void copy(String command) {
        if (!command.matches("(?i)^copy\\(.+\\)$")) {
            this.addLog("error", "Usage: copy(value)", "command", null);
            return;
        }
        Operation.setClipboardText(command.substring(command.indexOf(40) + 1, command.length() - 1));
        this.addLog("success", "Copied to clipboard", "command", null);
    }

    private void dir(String command) {
        if (!command.matches("(?i)^dir\\(.+\\)$")) {
            this.addLog("error", "Usage: dir(object)", "command", null);
            return;
        }
        String expression = command.substring(command.indexOf(40) + 1, command.length() - 1).trim();
        if ("window".equals(expression) || "document".equals(expression)) {
            this.addLog("result", "{" + expression + ": true, nodeCount: " + DevToolsConsole.countNodes(this.controller.targetDocument()) + "}", "dir", null);
        } else {
            this.addLog("result", "{ expression: \"" + expression + "\" }", "dir", null);
        }
    }

    private void keys(String command) {
        if (!command.matches("(?i)^keys\\(.+\\)$")) {
            this.addLog("error", "Usage: keys(object)", "command", null);
            return;
        }
        this.addLog("result", "[\"tag\", \"id\", \"class\", \"children\"]", "keys", null);
    }

    private void evaluate(String command) {
        Matcher matcher = ARITHMETIC_PATTERN.matcher(command);
        if (matcher.matches()) {
            double left = Double.parseDouble(matcher.group(1));
            double right = Double.parseDouble(matcher.group(3));
            double result = switch (matcher.group(2)) {
                case "+" -> left + right;
                case "-" -> left - right;
                case "*" -> left * right;
                case "/" -> {
                    if (right == 0.0) {
                        yield Double.NaN;
                    }
                    yield left / right;
                }
                default -> Double.NaN;
            };
            this.addLog("result", DevToolsConsole.formatNumber(result), "eval", null);
            return;
        }
        if (command.startsWith("\"") && command.endsWith("\"") || command.startsWith("'") && command.endsWith("'")) {
            this.addLog("result", command.substring(1, command.length() - 1), "eval", null);
            return;
        }
        if (command.matches("\\d+")) {
            this.addLog("result", command, "eval", null);
            return;
        }
        if ("true".equals(command) || "false".equals(command) || "null".equals(command) || "undefined".equals(command)) {
            this.addLog("result", command, "eval", null);
            return;
        }
        this.addLog("error", "Uncaught ReferenceError: " + command + " is not defined", "eval", "  at <anonymous>:1:1");
    }

    private void addLog(String level, String text, String source, String stack) {
        this.appendLog(level, text, source, stack, LocalTime.now().format(TIME_FORMAT));
        this.render();
    }

    private void appendLog(String level, String text, String source, String stack, String time) {
        this.logs.add(new LogEntry(level == null || level.isBlank() ? "log" : level, text == null ? "" : text, source == null || source.isBlank() ? "page" : source, stack, time == null || time.isBlank() ? LocalTime.now().format(TIME_FORMAT) : time));
        ++this.pendingAppendedLogCount;
        int overflow = this.logs.size() - 2000;
        if (overflow > 0) {
            this.logs.subList(0, overflow).clear();
            this.logWindowTruncated = true;
            this.pendingRemovedLogCount += overflow;
        }
    }

    private void render() {
        boolean canTrimAndAppend;
        Document document = this.controller.toolDocument();
        if (document == null) {
            return;
        }
        Element container = document.querySelector("#consoleLogs");
        if (container == null) {
            return;
        }
        String search = DevToolsDom.value(document.querySelector("#consoleSearch")).toLowerCase(Locale.ROOT);
        int appendedStart = Math.max(0, this.logs.size() - this.pendingAppendedLogCount);
        boolean sameView = this.renderedLogCount >= 0 && this.renderedFilter == this.filter && this.renderedSearch.equals(search) && this.renderedLogCount <= this.logs.size();
        boolean canAppend = sameView && !this.logWindowTruncated && appendedStart == this.renderedLogCount;
        boolean bl = canTrimAndAppend = sameView && this.logWindowTruncated && this.filter == Filter.ALL && search.isBlank() && this.pendingRemovedLogCount > 0 && this.pendingRemovedLogCount <= this.renderedLogCount && appendedStart == this.renderedLogCount - this.pendingRemovedLogCount && container.getChildElementCount() >= this.pendingRemovedLogCount;
        if (canAppend) {
            int visible = this.appendRenderedLogs(document, container, appendedStart, this.logs.size(), true, search);
            if (visible > 0) {
                this.removeEmptyState(container);
            }
            this.finishIncrementalRender(document, container, search);
            return;
        }
        if (canTrimAndAppend) {
            Element first;
            for (int i = 0; i < this.pendingRemovedLogCount && (first = container.getFirstElementChild()) != null; ++i) {
                first.remove();
            }
            this.appendRenderedLogs(document, container, appendedStart, this.logs.size(), false, search);
            this.finishIncrementalRender(document, container, search);
            return;
        }
        DevToolsDom.clear(container);
        DocumentFragment fragment = document.createDocumentFragment();
        int visible = this.appendRenderedLogs(document, fragment, 0, this.logs.size(), true, search);
        if (visible == 0) {
            String empty = this.logs.isEmpty() ? "NO LOGS YET \u00b7 TYPE \"help\" TO GET STARTED" : "NO MATCHING ENTRIES";
            fragment.appendChild(this.emptyState(document, empty));
        }
        container.appendChild(fragment);
        this.updateCounts(document);
        this.renderedLogCount = this.logs.size();
        this.renderedFilter = this.filter;
        this.renderedSearch = search;
        this.logWindowTruncated = false;
        this.pendingAppendedLogCount = 0;
        this.pendingRemovedLogCount = 0;
        container.scrollTop = container.scrollHeight;
        DevToolsDom.markDirty(document);
    }

    private int appendRenderedLogs(Document document, Node parent, int start, int end, boolean applyView, String search) {
        DocumentFragment fragment = document.createDocumentFragment();
        int visible = 0;
        for (int i = start; i < end; ++i) {
            LogEntry log = this.logs.get(i);
            if (applyView && (!this.matchesFilter(log) || !search.isBlank() && !log.text.toLowerCase(Locale.ROOT).contains(search))) continue;
            fragment.appendChild(this.renderLog(document, log));
            ++visible;
        }
        if (visible > 0) {
            parent.appendChild(fragment);
        }
        return visible;
    }

    private Element emptyState(Document document, String text) {
        Element state = DevToolsDom.text(document, "DIV", "console-empty-state", text);
        state.setAttribute("style", "padding:40px 20px;text-align:center;color:var(--gray);font-size:11px;letter-spacing:1px;");
        return state;
    }

    private void removeEmptyState(Element container) {
        Element state = container.querySelector(".console-empty-state");
        if (state != null) {
            state.remove();
        }
    }

    private void finishIncrementalRender(Document document, Element container, String search) {
        this.updateCounts(document);
        this.renderedLogCount = this.logs.size();
        this.renderedFilter = this.filter;
        this.renderedSearch = search;
        this.logWindowTruncated = false;
        this.pendingAppendedLogCount = 0;
        this.pendingRemovedLogCount = 0;
        container.scrollTop = container.scrollHeight;
        DevToolsDom.markDirty(document);
    }

    private Element renderLog(Document document, LogEntry log) {
        Element entry = DevToolsDom.element(document, "DIV", "log-entry " + log.level);
        entry.append(DevToolsDom.text(document, "DIV", "log-icon", DevToolsConsole.icon(log.level)));
        Element body = DevToolsDom.element(document, "DIV", "log-body");
        Element meta = DevToolsDom.element(document, "DIV", "log-meta");
        meta.append(DevToolsDom.text(document, "SPAN", "log-time", log.time));
        meta.append(DevToolsDom.text(document, "SPAN", "log-source", log.source));
        body.append(meta);
        body.append(DevToolsDom.text(document, "DIV", "log-text", log.text));
        if (log.stack != null && !log.stack.isBlank()) {
            body.append(DevToolsDom.text(document, "DIV", "log-stack", log.stack));
        }
        entry.append(body);
        return entry;
    }

    private void updateCounts(Document document) {
        int info = 0;
        int warn = 0;
        int error = 0;
        for (LogEntry log : this.logs) {
            switch (log.level) {
                case "warn": {
                    ++warn;
                    break;
                }
                case "error": {
                    ++error;
                    break;
                }
                case "info": 
                case "log": 
                case "success": 
                case "system": 
                case "command": 
                case "result": {
                    ++info;
                    break;
                }
            }
        }
        DevToolsConsole.setText(document, "#count-all", Integer.toString(this.logs.size()));
        DevToolsConsole.setText(document, "#count-info", Integer.toString(info));
        DevToolsConsole.setText(document, "#count-warn", Integer.toString(warn));
        DevToolsConsole.setText(document, "#count-error", Integer.toString(error));
        DevToolsConsole.setText(document, "#consoleStatusCount", this.logs.size() + " ENTRIES");
    }

    private boolean matchesFilter(LogEntry log) {
        return switch (this.filter) {
            default -> throw new IncompatibleClassChangeError();
            case Filter.ALL -> true;
            case Filter.WARN -> "warn".equals(log.level);
            case Filter.ERROR -> "error".equals(log.level);
            case Filter.INFO -> "info".equals(log.level) || "log".equals(log.level) || "success".equals(log.level) || "system".equals(log.level) || "command".equals(log.level) || "result".equals(log.level);
        };
    }

    private static String icon(String level) {
        return switch (level) {
            case "info" -> "\u2139";
            case "warn" -> "\u26a0";
            case "error" -> "\u2715";
            case "success" -> "\u2713";
            case "command" -> "\u203a";
            case "result" -> "\u2190";
            case "system" -> "\u25c6";
            default -> "\u2022";
        };
    }

    private static String helpText() {
        return "Available commands:\n  help          Show this help\n  clear         Clear console\n  select(<id>)  Select element by ID\n  inspect       Enter inspect mode\n  $$(<css>)     Query elements\n  $(<css>)      Query first element\n  copy(<val>)   Copy to clipboard\n  dir(<obj>)    Display object\n  table(<arr>)  Display as table\n  keys(<obj>)   Object keys\n  count()       Count nodes\n  tree          Show DOM tree\n  echo <text>   Print text";
    }

    private static String argument(String command) {
        int space = command.indexOf(32);
        return space < 0 ? "" : command.substring(space + 1).trim();
    }

    private static String formatNumber(double value) {
        if (Double.isFinite(value) && value == Math.rint(value)) {
            return Long.toString((long)value);
        }
        return Double.toString(value);
    }

    private static void setText(Document document, String selector, String value) {
        Element element = document.querySelector(selector);
        if (element != null) {
            element.setTextContent(value);
        }
    }

    private static Element elementAt(Document document, int index) {
        if (document == null || document.documentElement == null || index < 1) {
            return null;
        }
        int[] current = new int[]{0};
        return DevToolsConsole.elementAt(document.documentElement, index, current);
    }

    private static Element elementAt(Element element, int index, int[] current) {
        if (element == null) {
            return null;
        }
        current[0] = current[0] + 1;
        if (current[0] == index) {
            return element;
        }
        for (Node child : element.getChildNodes()) {
            Element childElement;
            Element found;
            if (!(child instanceof Element) || (found = DevToolsConsole.elementAt(childElement = (Element)child, index, current)) == null) continue;
            return found;
        }
        return null;
    }

    private static int shortIndex(Document document, Element target) {
        int[] current = new int[]{0};
        return DevToolsConsole.shortIndex(document == null ? null : document.documentElement, target, current);
    }

    private static int shortIndex(Element element, Element target, int[] current) {
        if (element == null) {
            return -1;
        }
        if (element == target) {
            current[0] = current[0] + 1;
            return current[0];
        }
        current[0] = current[0] + 1;
        for (Node child : element.getChildNodes()) {
            Element childElement;
            int found;
            if (!(child instanceof Element) || (found = DevToolsConsole.shortIndex(childElement = (Element)child, target, current)) < 0) continue;
            return found;
        }
        return -1;
    }

    private static int countNodes(Document document) {
        return document == null || document.documentElement == null ? 0 : DevToolsConsole.countNodes(document.documentElement);
    }

    private static int countNodes(Node node) {
        if (node == null) {
            return 0;
        }
        int count = 1;
        for (Node child : node.getChildNodes()) {
            TextNode text;
            if (child instanceof TextNode && ((text = (TextNode)child).getTextContent() == null || text.getTextContent().isBlank())) continue;
            count += DevToolsConsole.countNodes(child);
        }
        return count;
    }

    private static String treeText(Document document) {
        if (document == null || document.documentElement == null) {
            return "No debuggable document";
        }
        StringBuilder text = new StringBuilder();
        DevToolsConsole.appendTree(text, document.documentElement, 0);
        return text.toString();
    }

    private static void appendTree(StringBuilder text, Element element, int depth) {
        text.append("  ".repeat(Math.max(0, depth))).append('<').append(element.tagName.toLowerCase(Locale.ROOT)).append('>').append('\n');
        for (Node child : element.getChildNodes()) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            DevToolsConsole.appendTree(text, childElement, depth + 1);
        }
    }

    private static enum Filter {
        ALL("all"),
        INFO("info"),
        WARN("warn"),
        ERROR("error");

        final String id;

        private Filter(String id) {
            this.id = id;
        }

        static Filter parse(String value) {
            for (Filter filter : Filter.values()) {
                if (!filter.id.equalsIgnoreCase(value)) continue;
                return filter;
            }
            return ALL;
        }
    }

    private record LogEntry(String level, String text, String source, String stack, String time) {
    }
}

