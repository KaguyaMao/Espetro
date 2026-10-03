/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.resource;

import com.sighs.apricityui.dev.resource.ResourceFileWriter;
import com.sighs.apricityui.dev.resource.ResourceMetaDialog;
import com.sighs.apricityui.dev.resource.ResourcePath;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.ui.DialogWindow;
import com.sighs.apricityui.ui.ToastManager;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Locale;

public final class ResourceCreateDialog {
    private static final String LOCAL_FILE_ICON = "<svg viewBox=\"0 0 48 48\" fill=\"none\"><path d=\"M12 5h16l8 8v30H12z\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><path d=\"M28 5v10h8\" stroke=\"#8b5cf6\" stroke-width=\"2\"/><path d=\"M17 24h14M17 30h14M17 36h9\" stroke=\"#8b5cf6\" stroke-width=\"2\"/></svg>";
    private static final String CLIPBOARD_ICON = "<svg viewBox=\"0 0 48 48\" fill=\"none\"><rect x=\"11\" y=\"9\" width=\"26\" height=\"34\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><rect x=\"17\" y=\"4\" width=\"14\" height=\"9\" fill=\"#8b5cf6\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><path d=\"M17 23h14M17 30h14M17 37h9\" stroke=\"#8b5cf6\" stroke-width=\"2\"/></svg>";
    private static final String BLANK_TEMPLATE_ICON = "<svg viewBox=\"0 0 48 48\" fill=\"none\"><path d=\"M12 5h16l8 8v30H12z\" stroke=\"#1a1a1a\" stroke-width=\"2\"/><path d=\"M28 5v10h8\" stroke=\"#8b5cf6\" stroke-width=\"2\"/><path d=\"M17 24h14M17 30h14\" stroke=\"#8b5cf6\" stroke-width=\"2\"/><path d=\"M20 37h8\" stroke=\"#8b5cf6\" stroke-width=\"2\"/></svg>";
    private final ResourceMetaDialog templateMetaDialog = new ResourceMetaDialog();
    private String importedContent = "";
    private Element pathInput;
    private Element localFileInput;
    private Element localCard;
    private Element clipboardCard;
    private Element blankTemplateCard;
    private Element submitButton;
    private DialogWindow dialog;

    public void open(Document document, String currentPath, Runnable afterCreate) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        this.openFrameworkDialog(document, currentPath, afterCreate);
    }

    public void close() {
        this.templateMetaDialog.close();
        if (this.dialog != null) {
            this.dialog.close();
        }
        this.dialog = null;
        this.pathInput = null;
        this.localFileInput = null;
        this.localCard = null;
        this.clipboardCard = null;
        this.blankTemplateCard = null;
        this.submitButton = null;
        this.importedContent = "";
    }

    private void openFrameworkDialog(Document document, String currentPath, Runnable afterCreate) {
        this.dialog = DialogWindow.open(document, new DialogWindow.Options("NEW HTML", 720.0, 0.0, false, "dialog-overlay show", "dialog", "dialog-header", "dialog-title", "dialog-close", "dialog-body", "dialog-title-icon"), null);
        Element root = this.dialog.content();
        Element pathField = ResourceCreateDialog.element(document, "DIV", "dialog-field");
        pathField.append(ResourceCreateDialog.text(document, "LABEL", "SAVE PATH", "dialog-label"));
        this.pathInput = ResourceCreateDialog.element(document, "INPUT", "dialog-input");
        this.pathInput.setAttribute("type", "text");
        this.pathInput.setAttribute("placeholder", "example/original-file.html");
        String normalizedCurrentPath = ResourcePath.normalize(currentPath);
        this.pathInput.value = normalizedCurrentPath.isBlank() ? "/" : normalizedCurrentPath + "/";
        this.pathInput.addEventListener("input", event -> this.refreshSubmit(document));
        this.pathInput.addEventListener("change", event -> this.refreshSubmit(document));
        pathField.append(this.pathInput);
        root.append(pathField);
        Element importGrid = ResourceCreateDialog.element(document, "DIV", "resource-import-grid");
        this.localCard = ResourceCreateDialog.importCard(document, "LOCAL FILE", "OPEN FILE PICKER", LOCAL_FILE_ICON, "resource-import-local");
        this.localFileInput = ResourceCreateDialog.element(document, "INPUT", "resource-create-file-input");
        this.localFileInput.setAttribute("type", "file");
        this.localFileInput.setAttribute("accept", ".html,text/html");
        this.localFileInput.addEventListener("change", event -> this.importLocal(document, this.localFileInput.value));
        this.localCard.addEventListener("click", event -> this.localFileInput.click());
        this.clipboardCard = ResourceCreateDialog.importCard(document, "CLIPBOARD", "IMPORT HTML TEXT", CLIPBOARD_ICON, "resource-import-clipboard");
        this.clipboardCard.addEventListener("click", event -> this.importClipboard(document));
        this.blankTemplateCard = ResourceCreateDialog.importCard(document, "BLANK TEMPLATE", "CONFIGURE META", BLANK_TEMPLATE_ICON, "resource-import-template");
        this.blankTemplateCard.addEventListener("click", event -> this.openBlankTemplate(document));
        importGrid.append(this.localCard);
        importGrid.append(this.clipboardCard);
        importGrid.append(this.blankTemplateCard);
        root.append(importGrid);
        root.append(this.localFileInput);
        Element submitRow = ResourceCreateDialog.element(document, "DIV", "dialog-footer");
        this.submitButton = ResourceCreateDialog.element(document, "BUTTON", "dialog-btn dialog-btn-confirm");
        this.submitButton.append(ResourceCreateDialog.text(document, "SPAN", "CREATE", "dialog-btn-label"));
        this.submitButton.addEventListener("click", event -> this.submit(afterCreate));
        submitRow.append(this.submitButton);
        this.dialog.window().append(submitRow);
        this.refreshSubmit(document);
        ResourceCreateDialog.markDirty(document);
    }

    private void importLocal(Document document, String selectedPath) {
        ResourceFileWriter.ImportedFile imported;
        try {
            imported = selectedPath == null || selectedPath.isBlank() ? null : ResourceFileWriter.readHtmlFile(Path.of(selectedPath, new String[0]));
        }
        catch (Exception ignored) {
            imported = null;
        }
        if (imported == null) {
            ToastManager.show("Select an HTML file to import");
            return;
        }
        this.importedContent = imported.content();
        if (this.pathInput != null && (this.readInput().isBlank() || this.readInput().endsWith("/"))) {
            this.pathInput.value = this.readInput() + imported.name();
        }
        ResourceCreateDialog.updateCard(this.localCard, imported.name(), ResourceCreateDialog.abbreviate(imported.path().toString()));
        this.refreshSubmit(document);
    }

    private void importClipboard(Document document) {
        String content = Operation.getClipboardText();
        if (content.isBlank()) {
            ToastManager.show("Clipboard has no HTML content");
            return;
        }
        this.importedContent = content;
        ResourceCreateDialog.updateCard(this.clipboardCard, "CLIPBOARD READY", ResourcePath.formatSize(content.getBytes(StandardCharsets.UTF_8).length));
        this.refreshSubmit(document);
    }

    private void openBlankTemplate(Document document) {
        this.templateMetaDialog.openTemplate(document, metaMarkup -> {
            this.importedContent = ResourceCreateDialog.blankHtml(metaMarkup);
            ResourceCreateDialog.updateCard(this.blankTemplateCard, "BLANK TEMPLATE READY", "BROWSER META");
            this.refreshSubmit(document);
        });
    }

    private static String blankHtml(String metaMarkup) {
        String meta = metaMarkup == null ? "" : metaMarkup.trim();
        StringBuilder html = new StringBuilder("<!DOCTYPE html>\n<html>\n<head>");
        if (!meta.isBlank()) {
            for (String line : meta.split("\\R")) {
                html.append("\n    ").append(line);
            }
            html.append('\n');
        }
        return html.append("</head>\n<body></body>\n</html>\n").toString();
    }

    private void submit(Runnable afterCreate) {
        if (this.submitButton == null || !this.isReady()) {
            ToastManager.show("Choose content and a .html path");
            return;
        }
        ResourceFileWriter.WriteResult result = ResourceFileWriter.writeHtml(this.readInput(), this.importedContent);
        if (!result.success()) {
            ToastManager.show(result.message());
            return;
        }
        String createdPath = ResourcePath.normalize(this.readInput());
        this.close();
        ToastManager.show("Created " + createdPath);
        if (afterCreate != null) {
            afterCreate.run();
        }
    }

    private boolean isReady() {
        return !this.importedContent.isBlank() && !ResourceFileWriter.validateHtmlPath(this.readInput()).isBlank();
    }

    private String readInput() {
        return this.pathInput == null || this.pathInput.value == null ? "" : this.pathInput.value;
    }

    private void refreshSubmit(Document document) {
        if (this.submitButton == null) {
            return;
        }
        if (this.isReady()) {
            this.submitButton.removeAttribute("disabled");
        } else {
            this.submitButton.setAttribute("disabled", "disabled");
        }
        ResourceCreateDialog.markDirty(document);
    }

    private static Element importCard(Document document, String name, String meta, String icon, String className) {
        Element card = ResourceCreateDialog.element(document, "DIV", "file-card resource-import-card " + className);
        Element iconElement = ResourceCreateDialog.element(document, "DIV", "resource-import-icon");
        iconElement.setInnerHTML(icon);
        card.append(iconElement);
        card.append(ResourceCreateDialog.text(document, "DIV", name, "file-name"));
        card.append(ResourceCreateDialog.text(document, "DIV", meta, "file-meta"));
        return card;
    }

    private static void updateCard(Element card, String name, String meta) {
        String currentClass;
        if (card == null) {
            return;
        }
        Element nameElement = card.querySelector(".file-name");
        Element metaElement = card.querySelector(".file-meta");
        if (nameElement != null) {
            nameElement.setTextContent(name.toUpperCase(Locale.ROOT));
        }
        if (metaElement != null) {
            metaElement.setTextContent(meta);
        }
        card.setAttribute("class", ((currentClass = card.getAttribute("class")) == null ? "" : currentClass) + " imported");
    }

    private static String abbreviate(String value) {
        String safe = ResourcePath.safe(value);
        return safe.length() <= 42 ? safe : "..." + safe.substring(safe.length() - 39);
    }

    private static Element element(Document document, String tagName, String className) {
        Element element = Element.init(document.createElement(tagName));
        element.setAttribute("class", className);
        return element;
    }

    private static Element text(Document document, String tagName, String value, String className) {
        Element element = ResourceCreateDialog.element(document, tagName, className);
        element.setTextContent(value);
        return element;
    }

    private static void markDirty(Document document) {
        if (document != null && document.body != null) {
            document.markDirty(document.body, 7);
        }
    }
}

