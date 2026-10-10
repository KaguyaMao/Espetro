/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.dev.devtools;

import com.sighs.apricityui.dev.devtools.DevToolsDom;
import com.sighs.apricityui.dev.devtools.DevToolsTranslations;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.ui.DialogWindow;
import java.util.function.Consumer;

final class DevToolsSaveDialog {
    private DialogWindow dialog;

    DevToolsSaveDialog() {
    }

    void open(Document document, String path, Consumer<SaveOptions> onConfirm) {
        this.close();
        if (document == null || document.body == null) {
            return;
        }
        this.dialog = DialogWindow.open(document, new DialogWindow.Options(DevToolsTranslations.translate("devtools.apricityui.save_html", new Object[0]), 440.0, 0.0, false, "dialog-overlay show", "dialog", "dialog-header", "dialog-title", "dialog-close", "dialog-body", "save-dialog-title-icon"), () -> {
            this.dialog = null;
        });
        Element content = this.dialog.content();
        Element message = DevToolsDom.text(document, "DIV", "save-dialog-message", DevToolsTranslations.translate("devtools.apricityui.save_confirm", new Object[0]));
        Element file = DevToolsDom.text(document, "DIV", "save-dialog-path", path);
        content.append(message);
        content.append(file);
        Element scope = DevToolsDom.text(document, "DIV", "save-dialog-scope", DevToolsTranslations.translate("devtools.apricityui.save_scope_description", new Object[0]));
        content.append(scope);
        Element domOption = DevToolsDom.element(document, "LABEL", "save-dialog-option");
        Element domCheckbox = DevToolsDom.element(document, "INPUT", "save-dialog-checkbox save-dialog-dom-checkbox");
        domCheckbox.setAttribute("type", "checkbox");
        Element domCheckmark = DevToolsDom.element(document, "SPAN", "save-dialog-checkmark");
        Element domCopy = DevToolsDom.element(document, "SPAN", "save-dialog-option-copy");
        domCopy.append(DevToolsDom.text(document, "SPAN", "save-dialog-option-title", DevToolsTranslations.translate("devtools.apricityui.save_dom_tree", new Object[0])));
        domCopy.append(DevToolsDom.text(document, "SPAN", "save-dialog-option-description", DevToolsTranslations.translate("devtools.apricityui.save_dom_tree.description", new Object[0])));
        domOption.append(domCheckbox);
        domOption.append(domCheckmark);
        domOption.append(domCopy);
        domOption.addEventListener("click", event -> {
            if (event.target == domCheckbox) {
                return;
            }
            domCheckbox.setChecked(!domCheckbox.isChecked());
            event.preventDefault();
            DevToolsDom.markDirty(document);
        });
        content.append(domOption);
        Element reminder = DevToolsDom.element(document, "LABEL", "save-dialog-reminder");
        Element checkbox = DevToolsDom.element(document, "INPUT", "save-dialog-checkbox");
        checkbox.setAttribute("type", "checkbox");
        Element checkmark = DevToolsDom.element(document, "SPAN", "save-dialog-checkmark");
        reminder.append(checkbox);
        reminder.append(checkmark);
        reminder.append(DevToolsDom.text(document, "SPAN", "save-dialog-reminder-text", DevToolsTranslations.translate("devtools.apricityui.do_not_ask_again", new Object[0])));
        reminder.addEventListener("click", event -> {
            if (event.target == checkbox) {
                return;
            }
            checkbox.setChecked(!checkbox.isChecked());
            event.preventDefault();
            DevToolsDom.markDirty(document);
        });
        content.append(reminder);
        Element footer = DevToolsDom.element(document, "DIV", "dialog-footer");
        Element cancel = DevToolsSaveDialog.button(document, DevToolsTranslations.translate("devtools.apricityui.cancel", new Object[0]), "dialog-btn dialog-btn-cancel");
        Element save = DevToolsSaveDialog.button(document, DevToolsTranslations.translate("devtools.apricityui.save", new Object[0]), "dialog-btn dialog-btn-confirm");
        cancel.addEventListener("click", event -> this.close());
        save.addEventListener("click", event -> {
            boolean skipNextTime = checkbox.isChecked();
            boolean saveDomTree = domCheckbox.isChecked();
            this.close();
            if (onConfirm != null) {
                onConfirm.accept(new SaveOptions(skipNextTime, saveDomTree));
            }
        });
        footer.append(cancel);
        footer.append(save);
        this.dialog.window().append(footer);
        DevToolsDom.markDirty(document);
    }

    void close() {
        DialogWindow current = this.dialog;
        this.dialog = null;
        if (current != null && current.isOpen()) {
            current.close();
        }
    }

    private static Element button(Document document, String label, String className) {
        Element button = DevToolsDom.element(document, "BUTTON", className);
        button.append(DevToolsDom.text(document, "SPAN", "dialog-btn-label", label));
        return button;
    }

    record SaveOptions(boolean skipConfirmation, boolean saveDomTree) {
    }
}

