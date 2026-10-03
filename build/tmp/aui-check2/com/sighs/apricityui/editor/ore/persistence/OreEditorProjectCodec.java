/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.sighs.apricityui.editor.ore.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreComponentNode;
import com.sighs.apricityui.editor.ore.model.OreContainerNode;
import com.sighs.apricityui.editor.ore.model.OreEditorProject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class OreEditorProjectCodec {
    public static final int FORMAT_VERSION = 1;

    public String write(OreEditorProject project) {
        JsonObject document = new JsonObject();
        document.addProperty("format", "ore-editor-project");
        document.addProperty("version", (Number)1);
        document.add("root", (JsonElement)this.writeNode(project == null ? new OreEditorProject().root() : project.root()));
        JsonObject theme = new JsonObject();
        if (project != null) {
            project.theme().overrides().forEach((arg_0, arg_1) -> ((JsonObject)theme).addProperty(arg_0, arg_1));
        }
        document.add("theme", (JsonElement)theme);
        JsonObject metadata = new JsonObject();
        metadata.addProperty("doctype", project == null ? "<!DOCTYPE html>" : project.documentMetadata().doctype());
        metadata.addProperty("head", project == null ? "" : project.documentMetadata().headContent());
        metadata.addProperty("bodyScripts", project == null ? "" : project.documentMetadata().bodyScriptContent());
        JsonObject htmlAttributes = new JsonObject();
        JsonObject bodyAttributes = new JsonObject();
        if (project != null) {
            project.documentMetadata().htmlAttributes().forEach((arg_0, arg_1) -> ((JsonObject)htmlAttributes).addProperty(arg_0, arg_1));
            project.documentMetadata().bodyAttributes().forEach((arg_0, arg_1) -> ((JsonObject)bodyAttributes).addProperty(arg_0, arg_1));
        }
        metadata.add("htmlAttributes", (JsonElement)htmlAttributes);
        metadata.add("bodyAttributes", (JsonElement)bodyAttributes);
        document.add("documentMetadata", (JsonElement)metadata);
        return document.toString();
    }

    public OreEditorProject read(String source) {
        JsonObject metadata;
        JsonObject document = JsonParser.parseString((String)(source == null ? "" : source)).getAsJsonObject();
        if (!"ore-editor-project".equals(OreEditorProjectCodec.string(document, "format"))) {
            throw new IllegalArgumentException("Not an Ore editor project");
        }
        if (document.get("version") == null || document.get("version").getAsInt() != 1) {
            throw new IllegalArgumentException("Unsupported Ore editor project version");
        }
        OreCanvasNode decoded = this.readNode(OreEditorProjectCodec.requiredObject(document, "root"), true);
        if (!(decoded instanceof OreContainerNode)) {
            throw new IllegalArgumentException("Project root must be a container");
        }
        OreContainerNode root = (OreContainerNode)decoded;
        OreEditorProject project = new OreEditorProject(root);
        JsonObject theme = OreEditorProjectCodec.object(document, "theme");
        if (theme != null) {
            for (Map.Entry entry : theme.entrySet()) {
                if (!((JsonElement)entry.getValue()).isJsonPrimitive()) continue;
                project.theme().set((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
        }
        if ((metadata = OreEditorProjectCodec.object(document, "documentMetadata")) != null) {
            JsonObject bodyAttributes;
            project.documentMetadata().setDoctype(OreEditorProjectCodec.string(metadata, "doctype"));
            project.documentMetadata().setHeadContent(OreEditorProjectCodec.string(metadata, "head"));
            project.documentMetadata().setBodyScriptContent(OreEditorProjectCodec.string(metadata, "bodyScripts"));
            JsonObject htmlAttributes = OreEditorProjectCodec.object(metadata, "htmlAttributes");
            if (htmlAttributes != null) {
                for (Map.Entry entry : htmlAttributes.entrySet()) {
                    if (!((JsonElement)entry.getValue()).isJsonPrimitive()) continue;
                    project.documentMetadata().setHtmlAttribute((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
                }
            }
            if ((bodyAttributes = OreEditorProjectCodec.object(metadata, "bodyAttributes")) != null) {
                for (Map.Entry entry : bodyAttributes.entrySet()) {
                    if (!((JsonElement)entry.getValue()).isJsonPrimitive()) continue;
                    project.documentMetadata().setBodyAttribute((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
                }
            }
        }
        return project;
    }

    private JsonObject writeNode(OreCanvasNode node) {
        JsonObject value = new JsonObject();
        value.addProperty("id", node.id().toString());
        value.addProperty("kind", node instanceof OreContainerNode ? "container" : "component");
        value.addProperty("locked", Boolean.valueOf(node.locked()));
        JsonObject style = new JsonObject();
        node.style().properties().forEach((arg_0, arg_1) -> ((JsonObject)style).addProperty(arg_0, arg_1));
        value.add("style", (JsonElement)style);
        JsonObject attributes = new JsonObject();
        node.attributes().forEach((arg_0, arg_1) -> ((JsonObject)attributes).addProperty(arg_0, arg_1));
        value.add("attributes", (JsonElement)attributes);
        if (node instanceof OreContainerNode) {
            OreContainerNode container = (OreContainerNode)node;
            value.addProperty("tag", container.tag());
            JsonObject flex = new JsonObject();
            flex.addProperty("direction", container.flex().direction());
            flex.addProperty("wrap", container.flex().wrap());
            flex.addProperty("justifyContent", container.flex().justifyContent());
            flex.addProperty("alignItems", container.flex().alignItems());
            flex.addProperty("alignContent", container.flex().alignContent());
            flex.addProperty("gap", container.flex().gap());
            flex.addProperty("rowGap", container.flex().rowGap());
            flex.addProperty("columnGap", container.flex().columnGap());
            value.add("flex", (JsonElement)flex);
            JsonArray children = new JsonArray();
            for (OreCanvasNode child : container.children()) {
                children.add((JsonElement)this.writeNode(child));
            }
            value.add("children", (JsonElement)children);
        } else if (node instanceof OreComponentNode) {
            OreComponentNode component = (OreComponentNode)node;
            value.addProperty("type", component.type());
            value.addProperty("content", component.content());
            value.addProperty("absolute", Boolean.valueOf(component.absolute()));
            value.addProperty("flowIndex", (Number)component.flowIndex());
            if (component.hasFlowStyleSnapshot()) {
                JsonObject flowStyles = new JsonObject();
                component.flowStyleSnapshot().forEach((arg_0, arg_1) -> ((JsonObject)flowStyles).addProperty(arg_0, arg_1));
                value.add("flowStyles", (JsonElement)flowStyles);
            }
            JsonObject states = new JsonObject();
            component.stateStyles().forEach((state, stateStyle) -> {
                JsonObject properties = new JsonObject();
                stateStyle.properties().forEach((arg_0, arg_1) -> ((JsonObject)properties).addProperty(arg_0, arg_1));
                states.add(state.name(), (JsonElement)properties);
            });
            value.add("states", (JsonElement)states);
        }
        return value;
    }

    private OreCanvasNode readNode(JsonObject value, boolean root) {
        OreComponentNode component;
        JsonObject style;
        JsonObject attributes;
        OreCanvasNode node;
        UUID id;
        try {
            id = UUID.fromString(OreEditorProjectCodec.string(value, "id"));
        }
        catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid node UUID", exception);
        }
        if ("container".equals(OreEditorProjectCodec.string(value, "kind"))) {
            JsonArray children;
            OreContainerNode container = new OreContainerNode(root, id);
            container.setTag(OreEditorProjectCodec.string(value, "tag"));
            JsonObject flex = OreEditorProjectCodec.object(value, "flex");
            if (flex != null) {
                container.flex().setDirection(OreEditorProjectCodec.string(flex, "direction"));
                container.flex().setWrap(OreEditorProjectCodec.string(flex, "wrap"));
                container.flex().setJustifyContent(OreEditorProjectCodec.string(flex, "justifyContent"));
                container.flex().setAlignItems(OreEditorProjectCodec.string(flex, "alignItems"));
                container.flex().setAlignContent(OreEditorProjectCodec.string(flex, "alignContent"));
                container.flex().setGap(OreEditorProjectCodec.string(flex, "gap"));
                container.flex().setRowGap(OreEditorProjectCodec.string(flex, "rowGap"));
                container.flex().setColumnGap(OreEditorProjectCodec.string(flex, "columnGap"));
            }
            if ((children = OreEditorProjectCodec.array(value, "children")) != null) {
                for (JsonElement child : children) {
                    if (!child.isJsonObject()) {
                        throw new IllegalArgumentException("Invalid child node");
                    }
                    container.add(this.readNode(child.getAsJsonObject(), false));
                }
            }
            node = container;
        } else if ("component".equals(OreEditorProjectCodec.string(value, "kind"))) {
            OreComponentNode component2 = new OreComponentNode(OreEditorProjectCodec.string(value, "type"), OreEditorProjectCodec.string(value, "content"), id);
            if (value.has("absolute") && value.get("absolute").getAsBoolean()) {
                component2.enterAbsolute(value.has("flowIndex") ? value.get("flowIndex").getAsInt() : 0);
            }
            node = component2;
        } else {
            throw new IllegalArgumentException("Unknown Ore node kind");
        }
        if (value.has("locked") && value.get("locked").isJsonPrimitive()) {
            node.setLocked(value.get("locked").getAsBoolean());
        }
        if ((attributes = OreEditorProjectCodec.object(value, "attributes")) != null) {
            for (Object entry : attributes.entrySet()) {
                if (!((JsonElement)entry.getValue()).isJsonPrimitive()) continue;
                node.setAttribute((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
        }
        if ((style = OreEditorProjectCodec.object(value, "style")) != null) {
            for (Map.Entry entry : style.entrySet()) {
                if (!((JsonElement)entry.getValue()).isJsonPrimitive()) continue;
                node.style().set((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
            }
        }
        if (node instanceof OreComponentNode && "absolute".equals((component = (OreComponentNode)node).style().get("position")) && !component.absolute()) {
            component.enterAbsolute(0);
        }
        if (node instanceof OreComponentNode) {
            JsonObject states;
            component = (OreComponentNode)node;
            JsonObject flowStyles = OreEditorProjectCodec.object(value, "flowStyles");
            if (flowStyles != null) {
                LinkedHashMap<String, String> snapshot = new LinkedHashMap<String, String>();
                for (Map.Entry entry : flowStyles.entrySet()) {
                    snapshot.put((String)entry.getKey(), ((JsonElement)entry.getValue()).isJsonPrimitive() ? ((JsonElement)entry.getValue()).getAsString() : null);
                }
                component.setFlowStyleSnapshot(snapshot);
            }
            if ((states = OreEditorProjectCodec.object(value, "states")) != null) {
                for (Map.Entry entry : states.entrySet()) {
                    try {
                        OreComponentNode.VisualState state = OreComponentNode.VisualState.valueOf((String)entry.getKey());
                        if (!((JsonElement)entry.getValue()).isJsonObject()) continue;
                        for (Map.Entry property : ((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
                            if (!((JsonElement)property.getValue()).isJsonPrimitive()) continue;
                            component.stateStyle(state).set((String)property.getKey(), ((JsonElement)property.getValue()).getAsString());
                        }
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                    }
                }
            }
        }
        return node;
    }

    private static JsonObject requiredObject(JsonObject object, String name) {
        JsonObject result = OreEditorProjectCodec.object(object, name);
        if (result == null) {
            throw new IllegalArgumentException("Missing " + name);
        }
        return result;
    }

    private static JsonObject object(JsonObject object, String name) {
        JsonElement value = object.get(name);
        return value != null && value.isJsonObject() ? value.getAsJsonObject() : null;
    }

    private static JsonArray array(JsonObject object, String name) {
        JsonElement value = object.get(name);
        return value != null && value.isJsonArray() ? value.getAsJsonArray() : null;
    }

    private static String string(JsonObject object, String name) {
        JsonElement value = object.get(name);
        return value == null || !value.isJsonPrimitive() ? "" : value.getAsString();
    }
}

