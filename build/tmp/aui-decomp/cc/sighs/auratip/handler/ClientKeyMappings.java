/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.registry.extra.KeyMappingRegister
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 */
package cc.sighs.auratip.handler;

import cc.sighs.auratip.dev.DevEnvironment;
import cc.sighs.oelib.registry.extra.KeyMappingRegister;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

public class ClientKeyMappings {
    private static final String CATEGORY = "key.categories.auratip";
    public static final KeyMapping CLOSE_TIP = new KeyMapping("key.auratip.close_tip", InputConstants.Type.KEYSYM, 261, "key.categories.auratip");
    public static final KeyMapping DEV_OPEN_DATAPACK_MENU = new KeyMapping("key.auratip.dev.open_datapack_menu", InputConstants.Type.KEYSYM, 295, "key.categories.auratip");
    public static final KeyMapping DEV_OPEN_JAVA_MENU = new KeyMapping("key.auratip.dev.open_java_menu", InputConstants.Type.KEYSYM, 296, "key.categories.auratip");
    public static final KeyMapping DEV_OPEN_KJS_MENU = new KeyMapping("key.auratip.dev.open_kjs_menu", InputConstants.Type.KEYSYM, 297, "key.categories.auratip");
    public static final KeyMapping DEV_ENQUEUE_CLIENT_TIP = new KeyMapping("key.auratip.dev.enqueue_client_tip", InputConstants.Type.KEYSYM, 298, "key.categories.auratip");
    public static final KeyMapping DEV_TRIGGER_SHOWTIP = new KeyMapping("key.auratip.dev.trigger_showtip", InputConstants.Type.KEYSYM, 299, "key.categories.auratip");

    public static void register() {
        KeyMappingRegister.register((KeyMapping)CLOSE_TIP);
        if (DevEnvironment.isDev()) {
            KeyMappingRegister.register((KeyMapping)DEV_OPEN_DATAPACK_MENU);
            KeyMappingRegister.register((KeyMapping)DEV_OPEN_JAVA_MENU);
            KeyMappingRegister.register((KeyMapping)DEV_OPEN_KJS_MENU);
            KeyMappingRegister.register((KeyMapping)DEV_ENQUEUE_CLIENT_TIP);
            KeyMappingRegister.register((KeyMapping)DEV_TRIGGER_SHOWTIP);
        }
    }
}

