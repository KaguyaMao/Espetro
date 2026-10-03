/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package com.sighs.apricityui.dev.debug;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sighs.apricityui.dev.debug.DebugDom;
import com.sighs.apricityui.dev.debug.DebugInput;
import com.sighs.apricityui.dev.debug.DebugProtocolException;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

final class DebugProtocolSession {
    private final Map<String, String> attachedTargets = new HashMap<String, String>();

    DebugProtocolSession() {
    }

    JsonElement handle(JsonObject request) {
        if (!(request != null && "2.0".equals(DebugProtocolSession.optionalString(request, "jsonrpc")) && request.has("method") && request.get("method").isJsonPrimitive())) {
            throw new DebugProtocolException(-32600, "Invalid JSON-RPC request");
        }
        if (request.has("id") && !DebugProtocolSession.validRequestId(request.get("id"))) {
            throw new DebugProtocolException(-32600, "id must be a string, number, or null");
        }
        String method = request.get("method").getAsString();
        JsonObject params = DebugProtocolSession.parameters(request);
        return switch (method) {
            case "System.info" -> this.systemInfo();
            case "Target.list" -> this.listTargets();
            case "Target.attach" -> this.attach(params);
            case "Target.detach" -> this.detach(params);
            case "DOM.query" -> DebugDom.query(this.requireDocument(params), DebugProtocolSession.requiredString(params, "selector"));
            case "DOM.queryAll" -> DebugDom.queryAll(this.requireDocument(params), DebugProtocolSession.requiredString(params, "selector"));
            case "DOM.snapshot" -> this.snapshot(params);
            case "DOM.getAttributes" -> DebugDom.attributes(this.requireElement(params));
            case "DOM.getText" -> DebugDom.text(this.requireNode(params));
            case "DOM.getComputedStyle" -> DebugDom.computedStyle(this.requireElement(params));
            case "DOM.getBoxModel" -> this.boxModel(params);
            case "DOM.hover" -> this.input(params, InputAction.HOVER);
            case "DOM.click" -> this.input(params, InputAction.CLICK);
            case "DOM.fill" -> this.input(params, InputAction.FILL);
            default -> throw new DebugProtocolException(-32601, "Unknown method: " + method);
        };
    }

    void close() {
        this.attachedTargets.clear();
    }

    private JsonObject systemInfo() {
        JsonObject result = new JsonObject();
        result.addProperty("name", "Apricity Debug Protocol");
        result.addProperty("protocolVersion", (Number)1);
        result.addProperty("endpoint", "ws://127.0.0.1:25321/apricity");
        JsonArray capabilities = new JsonArray();
        capabilities.add("target");
        capabilities.add("dom");
        capabilities.add("input");
        result.add("capabilities", (JsonElement)capabilities);
        return result;
    }

    private JsonObject listTargets() {
        JsonArray targets = new JsonArray();
        for (Document document : Document.getAll()) {
            if (document == null || document.isDisposed()) continue;
            JsonObject target = new JsonObject();
            target.addProperty("targetId", document.getUuid().toString());
            target.addProperty("path", document.getPath());
            target.addProperty("active", Boolean.valueOf(document.isActive()));
            target.addProperty("inWorld", Boolean.valueOf(document.inWorld));
            target.addProperty("refreshGeneration", (Number)document.getRefreshGeneration());
            targets.add((JsonElement)target);
        }
        JsonObject result = new JsonObject();
        result.add("targets", (JsonElement)targets);
        return result;
    }

    private JsonObject attach(JsonObject params) {
        String targetId = DebugProtocolSession.requiredString(params, "targetId");
        Document document = Document.getByUUID(targetId);
        if (document == null || !document.isActive()) {
            throw new DebugProtocolException(-32001, "Target is closed");
        }
        String sessionId = UUID.randomUUID().toString();
        this.attachedTargets.put(sessionId, targetId);
        JsonObject result = new JsonObject();
        result.addProperty("sessionId", sessionId);
        result.addProperty("targetId", targetId);
        result.addProperty("path", document.getPath());
        return result;
    }

    private JsonObject detach(JsonObject params) {
        String sessionId = DebugProtocolSession.requiredString(params, "sessionId");
        boolean detached = this.attachedTargets.remove(sessionId) != null;
        JsonObject result = new JsonObject();
        result.addProperty("detached", Boolean.valueOf(detached));
        return result;
    }

    private JsonObject snapshot(JsonObject params) {
        Document document = this.requireDocument(params);
        int maxDepth = DebugProtocolSession.boundedInt(params, "maxDepth", 32, 0, 128);
        int maxNodes = DebugProtocolSession.boundedInt(params, "maxNodes", 5000, 1, 20000);
        return DebugDom.snapshot(document, maxDepth, maxNodes);
    }

    private JsonObject boxModel(JsonObject params) {
        Document document = this.requireDocument(params);
        return DebugDom.boxModel(document, this.requireElement(document, params));
    }

    private JsonObject input(JsonObject params, InputAction action) {
        Document document = this.requireDocument(params);
        Element element = this.requireElement(document, params);
        return switch (action) {
            default -> throw new IncompatibleClassChangeError();
            case InputAction.HOVER -> DebugInput.hover(document, element);
            case InputAction.CLICK -> DebugInput.click(document, element);
            case InputAction.FILL -> DebugInput.fill(element, DebugProtocolSession.requiredString(params, "value", true));
        };
    }

    private Element requireElement(JsonObject params) {
        Document document = this.requireDocument(params);
        return this.requireElement(document, params);
    }

    private Node requireNode(JsonObject params) {
        Document document = this.requireDocument(params);
        return DebugDom.requireNode(document, DebugProtocolSession.requiredString(params, "nodeId"));
    }

    private Element requireElement(Document document, JsonObject params) {
        return DebugDom.requireElement(document, DebugProtocolSession.requiredString(params, "nodeId"));
    }

    private Document requireDocument(JsonObject params) {
        String sessionId = DebugProtocolSession.requiredString(params, "sessionId");
        String targetId = this.attachedTargets.get(sessionId);
        if (targetId == null) {
            throw new DebugProtocolException(-32001, "Session is detached");
        }
        Document document = Document.getByUUID(targetId);
        if (document == null || !document.isActive()) {
            this.attachedTargets.remove(sessionId);
            throw new DebugProtocolException(-32001, "Target is closed");
        }
        return document;
    }

    private static JsonObject parameters(JsonObject request) {
        if (!request.has("params")) {
            return new JsonObject();
        }
        JsonElement params = request.get("params");
        if (!params.isJsonObject()) {
            throw new DebugProtocolException(-32602, "params must be an object");
        }
        return params.getAsJsonObject();
    }

    private static String requiredString(JsonObject params, String name) {
        return DebugProtocolSession.requiredString(params, name, false);
    }

    private static String requiredString(JsonObject params, String name, boolean allowEmpty) {
        if (!(params.has(name) && params.get(name).isJsonPrimitive() && params.get(name).getAsJsonPrimitive().isString())) {
            throw new DebugProtocolException(-32602, name + " must be a string");
        }
        String value = params.get(name).getAsString();
        if (!allowEmpty && value.isBlank()) {
            throw new DebugProtocolException(-32602, name + " must not be empty");
        }
        return value;
    }

    private static String optionalString(JsonObject object, String name) {
        if (!object.has(name) || !object.get(name).isJsonPrimitive()) {
            return null;
        }
        return object.get(name).getAsString();
    }

    private static int boundedInt(JsonObject params, String name, int fallback, int min, int max) {
        if (!params.has(name)) {
            return fallback;
        }
        try {
            if (!params.get(name).isJsonPrimitive() || !params.get(name).getAsJsonPrimitive().isNumber()) {
                throw new NumberFormatException();
            }
            int value = params.get(name).getAsInt();
            if (value < min || value > max) {
                throw new NumberFormatException();
            }
            return value;
        }
        catch (RuntimeException exception) {
            throw new DebugProtocolException(-32602, name + " must be between " + min + " and " + max);
        }
    }

    private static boolean validRequestId(JsonElement id) {
        if (id == null || id.isJsonNull()) {
            return true;
        }
        if (!id.isJsonPrimitive()) {
            return false;
        }
        return id.getAsJsonPrimitive().isString() || id.getAsJsonPrimitive().isNumber();
    }

    private static enum InputAction {
        HOVER,
        CLICK,
        FILL;

    }
}

