/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.element.Body;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.HTML;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public final class ClientRuntimeSelfTest {
    private static final String ENABLE_PROPERTY = "apricityui.clientSelfTest";
    private static final String EXIT_PROPERTY = "apricityui.clientSelfTest.exitOnFinish";
    private static final String RESULT_PROPERTY = "apricityui.clientSelfTest.resultFile";
    private static final String DOCUMENT_PATH_PROPERTY = "apricityui.clientSelfTest.documentPath";
    private static final String MAX_FIRST_CREATE_MILLIS_PROPERTY = "apricityui.clientSelfTest.maxFirstCreateMillis";
    private static final String LIFECYCLE_DOC_PATH = "tests/lifecycle-event-test.html";
    private static final String RUNTIME_DOC_PATH = "tests/client-runtime-self-test.html";
    private static final long START_DELAY_TICKS = 10L;
    private static final long ASSERT_TIMEOUT_TICKS = 120L;
    private static State state = State.IDLE;
    private static long tickCounter = 0L;
    private static long startTick = -1L;
    private static Document lifecycleDocument;
    private static Document runtimeDocument;
    private static Document firstCreateDocument;
    private static String firstCreatePath;
    private static long firstCreateNanos;

    private ClientRuntimeSelfTest() {
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!Boolean.getBoolean(ENABLE_PROPERTY)) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null) {
            return;
        }
        ++tickCounter;
        switch (state) {
            case IDLE: {
                ClientRuntimeSelfTest.maybeStart();
                break;
            }
            case WAITING: {
                ClientRuntimeSelfTest.maybeAssert();
                break;
            }
        }
    }

    private static void maybeStart() {
        if (tickCounter < 10L) {
            return;
        }
        if (HTML.getTemple(LIFECYCLE_DOC_PATH) == null || HTML.getTemple(RUNTIME_DOC_PATH) == null) {
            return;
        }
        String requestedPath = System.getProperty(DOCUMENT_PATH_PROPERTY, "").trim();
        if (!requestedPath.isEmpty() && HTML.getTemple(requestedPath) == null) {
            return;
        }
        Document.remove(LIFECYCLE_DOC_PATH);
        Document.remove(RUNTIME_DOC_PATH);
        if (!requestedPath.isEmpty()) {
            firstCreatePath = requestedPath;
            Document.remove(firstCreatePath);
            long startNs = System.nanoTime();
            firstCreateDocument = Document.create(firstCreatePath);
            firstCreateNanos = System.nanoTime() - startNs;
            ApricityUI.LOGGER.info("[AUI SelfTest] first-create path={} cost={}us documents={} elements={}", new Object[]{firstCreatePath, firstCreateNanos / 1000L, Document.get(firstCreatePath).size(), firstCreateDocument == null ? 0 : firstCreateDocument.getElements().size()});
        }
        lifecycleDocument = Document.create(LIFECYCLE_DOC_PATH);
        runtimeDocument = Document.create(RUNTIME_DOC_PATH);
        startTick = tickCounter;
        state = State.WAITING;
        ApricityUI.LOGGER.info("[AUI SelfTest] started client runtime self-test");
    }

    private static void maybeAssert() {
        Minecraft minecraft;
        if (tickCounter - startTick < 120L) {
            return;
        }
        ArrayList<String> failures = new ArrayList<String>();
        try {
            ClientRuntimeSelfTest.validateLifecycleDocument(failures);
        }
        catch (Throwable failure) {
            failures.add("lifecycle assertion threw " + failure.getClass().getSimpleName() + ": " + ClientRuntimeSelfTest.safe(failure.getMessage()));
        }
        try {
            ClientRuntimeSelfTest.validateRuntimeDocument(failures);
        }
        catch (Throwable failure) {
            failures.add("runtime assertion threw " + failure.getClass().getSimpleName() + ": " + ClientRuntimeSelfTest.safe(failure.getMessage()));
        }
        try {
            ClientRuntimeSelfTest.validateFirstCreate(failures);
        }
        catch (Throwable failure) {
            failures.add("first-create assertion threw " + failure.getClass().getSimpleName() + ": " + ClientRuntimeSelfTest.safe(failure.getMessage()));
        }
        if (failures.isEmpty()) {
            ApricityUI.LOGGER.info("[AUI SelfTest] PASS client runtime self-test");
        } else {
            ApricityUI.LOGGER.error("[AUI SelfTest] FAIL client runtime self-test: {}", (Object)String.join((CharSequence)" | ", failures));
        }
        ClientRuntimeSelfTest.writeResult(failures);
        Document.remove(LIFECYCLE_DOC_PATH);
        Document.remove(RUNTIME_DOC_PATH);
        if (!firstCreatePath.isEmpty()) {
            Document.remove(firstCreatePath);
        }
        state = State.DONE;
        if (Boolean.getBoolean(EXIT_PROPERTY) && (minecraft = Minecraft.m_91087_()) != null) {
            minecraft.m_91395_();
        }
    }

    private static void validateLifecycleDocument(List<String> failures) {
        String text;
        if (lifecycleDocument == null || ClientRuntimeSelfTest.lifecycleDocument.body == null) {
            failures.add("lifecycle document was not created");
            return;
        }
        Element snapshot = lifecycleDocument.querySelector("#snapshot");
        Element log = lifecycleDocument.querySelector("#log");
        if (snapshot == null) {
            failures.add("lifecycle snapshot node missing");
        } else {
            text = snapshot.getTextContent();
            if (text == null || !text.contains("load | readyState=complete")) {
                failures.add("lifecycle snapshot unexpected: " + ClientRuntimeSelfTest.safe(text));
            }
        }
        if (log == null) {
            failures.add("lifecycle log node missing");
        } else {
            text = log.getTextContent();
            String lateResult = ClientRuntimeSelfTest.lifecycleDocument.body.getAttribute("data-late-listener-result");
            if (!"no".equals(lateResult)) {
                failures.add("lifecycle late-listener behavior unexpected: result=" + ClientRuntimeSelfTest.safe(lateResult) + " log=" + ClientRuntimeSelfTest.safe(text));
            }
        }
    }

    private static void validateRuntimeDocument(List<String> failures) {
        if (runtimeDocument == null || ClientRuntimeSelfTest.runtimeDocument.body == null) {
            failures.add("runtime document was not created");
            return;
        }
        Body body = ClientRuntimeSelfTest.runtimeDocument.body;
        ClientRuntimeSelfTest.expectAttr(body, "data-initial-ready-state", "interactive", failures, "initial readyState");
        ClientRuntimeSelfTest.expectAttr(body, "data-domcontentloaded-ready-state", "interactive", failures, "DOMContentLoaded readyState");
        ClientRuntimeSelfTest.expectAttr(body, "data-load-ready-state", "complete", failures, "load readyState");
        ClientRuntimeSelfTest.expectAttr(body, "data-timeout", "done", failures, "setTimeout");
        ClientRuntimeSelfTest.expectAttr(body, "data-late-load-replay", "no", failures, "late load replay");
        ClientRuntimeSelfTest.expectAttr(body, "data-urlsearchparams", "1,2", failures, "URLSearchParams");
        String actualFormData = body.getAttribute("data-formdata");
        if (!"alpha=1&beta=x&beta=y".equals(actualFormData)) {
            failures.add("FormData expected=alpha=1&beta=x&beta=y actual=" + ClientRuntimeSelfTest.safe(actualFormData) + " select.multiple=" + ClientRuntimeSelfTest.safe(body.getAttribute("data-form-select-multiple")) + " options=" + ClientRuntimeSelfTest.safe(body.getAttribute("data-form-select-options")) + " selectedFlags=" + ClientRuntimeSelfTest.safe(body.getAttribute("data-form-select-selected-flags")));
        }
        ClientRuntimeSelfTest.expectAttr(body, "data-form-field-name", "alpha", failures, "form field name");
        ClientRuntimeSelfTest.expectAttr(body, "data-form-select-multiple", "true", failures, "select.multiple");
        ClientRuntimeSelfTest.expectAttr(body, "data-load-handler-entered", "yes", failures, "load handler");
        ClientRuntimeSelfTest.expectAttr(body, "data-location-type", "object", failures, "document.location typeof");
        String pathname = body.getAttribute("data-location-pathname");
        if (pathname == null || !pathname.endsWith(RUNTIME_DOC_PATH)) {
            failures.add("location.pathname unexpected: " + ClientRuntimeSelfTest.safe(pathname) + " href=" + ClientRuntimeSelfTest.safe(body.getAttribute("data-location-href")));
        }
    }

    private static void validateFirstCreate(List<String> failures) {
        long maxMillis;
        long elapsedMillis;
        if (firstCreatePath.isEmpty()) {
            return;
        }
        if (firstCreateDocument == null || ClientRuntimeSelfTest.firstCreateDocument.body == null) {
            failures.add("first-create document was not created path=" + firstCreatePath);
            return;
        }
        int documentCount = Document.get(firstCreatePath).size();
        if (documentCount != 1) {
            failures.add("first-create expected one document path=" + firstCreatePath + " actual=" + documentCount);
        }
        if ((elapsedMillis = firstCreateNanos / 1000000L) > (maxMillis = Long.getLong(MAX_FIRST_CREATE_MILLIS_PROPERTY, 250L).longValue())) {
            failures.add("first-create exceeded budget path=" + firstCreatePath + " actual=" + elapsedMillis + "ms max=" + maxMillis + "ms");
        }
    }

    private static void expectAttr(Element body, String name, String expected, List<String> failures, String label) {
        String actual = body.getAttribute(name);
        if (!expected.equals(actual)) {
            failures.add(label + " expected=" + expected + " actual=" + ClientRuntimeSelfTest.safe(actual));
        }
    }

    private static String safe(String value) {
        return value == null ? "<null>" : value;
    }

    private static void writeResult(List<String> failures) {
        String rawPath = System.getProperty(RESULT_PROPERTY);
        if (rawPath == null || rawPath.isBlank()) {
            return;
        }
        try {
            Path result = Path.of(rawPath, new String[0]).toAbsolutePath().normalize();
            Path parent = result.getParent();
            if (parent != null) {
                Files.createDirectories(parent, new FileAttribute[0]);
            }
            StringBuilder output = new StringBuilder(failures.isEmpty() ? "PASS\n" : "FAIL\n");
            if (!firstCreatePath.isEmpty() && firstCreateNanos >= 0L) {
                output.append("first-create path=").append(firstCreatePath).append(" cost=").append(firstCreateNanos / 1000L).append("us").append(" documents=").append(Document.get(firstCreatePath).size()).append('\n');
            }
            for (String failure : failures) {
                output.append(failure).append('\n');
            }
            Files.writeString(result, (CharSequence)output.toString(), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception writeFailure) {
            ApricityUI.LOGGER.error("[AUI SelfTest] could not write result file", (Throwable)writeFailure);
        }
    }

    static {
        firstCreatePath = "";
        firstCreateNanos = -1L;
    }

    private static enum State {
        IDLE,
        WAITING,
        DONE;

    }
}

