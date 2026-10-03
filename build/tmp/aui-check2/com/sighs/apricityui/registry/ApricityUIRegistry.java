/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.registry;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.spi.AuiServices;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class ApricityUIRegistry {
    public static List<Element> ELEMENTS = new ArrayList<Element>();

    public static void scanPackage(String basePackage) {
        AuiServices.client().addScanPackage(basePackage);
    }

    public static void scanPackages(String ... basePackages) {
        AuiServices.client().addScanPackages(basePackages);
    }

    public static void register() {
        AuiServices.client().scanAnnotationClasses(ElementRegister.class, data -> true, clazz -> {
            if (!Element.class.isAssignableFrom((Class<?>)clazz)) {
                ApricityUI.LOGGER.error("Class {} has @ElementRegister but is not a subclass of Element!", (Object)clazz.getName());
                return;
            }
            ElementRegister annotation = clazz.getAnnotation(ElementRegister.class);
            String value = annotation.value();
            Element.register(value, (document, s) -> {
                try {
                    Constructor constructor = clazz.getConstructor(Document.class);
                    constructor.setAccessible(true);
                    Element element = (Element)constructor.newInstance(document);
                    ELEMENTS.add(element);
                    return element;
                }
                catch (Throwable throwable) {
                    ApricityUI.LOGGER.error("Failed to load element {}", (Object)clazz.getName(), (Object)throwable);
                    return new Element((Document)document, value);
                }
            });
        }, () -> {});
    }
}

