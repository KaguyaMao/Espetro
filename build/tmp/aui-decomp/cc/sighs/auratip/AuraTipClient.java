/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip;

import cc.sighs.auratip.dev.DevEnvironment;
import cc.sighs.auratip.dev.DevJavaApiSamplesClient;
import cc.sighs.auratip.handler.AuraShaders;
import cc.sighs.auratip.handler.ClientKeyMappings;

public class AuraTipClient {
    public static void init() {
        AuraShaders.register();
        ClientKeyMappings.register();
        if (DevEnvironment.isDev()) {
            DevJavaApiSamplesClient.initClient();
        }
    }
}

