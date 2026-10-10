/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.networking.simple.MessageType
 *  dev.architectury.networking.simple.SimpleNetworkManager
 */
package dev.latvian.mods.kubejs.net;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import dev.latvian.mods.kubejs.net.AddStageMessage;
import dev.latvian.mods.kubejs.net.DisplayClientErrorsMessage;
import dev.latvian.mods.kubejs.net.DisplayServerErrorsMessage;
import dev.latvian.mods.kubejs.net.FirstClickMessage;
import dev.latvian.mods.kubejs.net.NotificationMessage;
import dev.latvian.mods.kubejs.net.PaintMessage;
import dev.latvian.mods.kubejs.net.ReloadStartupScriptsMessage;
import dev.latvian.mods.kubejs.net.RemoveStageMessage;
import dev.latvian.mods.kubejs.net.SendDataFromClientMessage;
import dev.latvian.mods.kubejs.net.SendDataFromServerMessage;
import dev.latvian.mods.kubejs.net.SyncStagesMessage;

public interface KubeJSNet {
    public static final SimpleNetworkManager NET = SimpleNetworkManager.create((String)"kubejs");
    public static final MessageType SEND_DATA_FROM_CLIENT = NET.registerC2S("send_data_from_client", SendDataFromClientMessage::new);
    public static final MessageType SEND_DATA_FROM_SERVER = NET.registerS2C("send_data_from_server", SendDataFromServerMessage::new);
    public static final MessageType PAINT = NET.registerS2C("paint", PaintMessage::new);
    public static final MessageType ADD_STAGE = NET.registerS2C("add_stage", AddStageMessage::new);
    public static final MessageType REMOVE_STAGE = NET.registerS2C("remove_stage", RemoveStageMessage::new);
    public static final MessageType SYNC_STAGES = NET.registerS2C("sync_stages", SyncStagesMessage::new);
    public static final MessageType FIRST_CLICK = NET.registerC2S("first_click", FirstClickMessage::new);
    public static final MessageType NOTIFICATION = NET.registerS2C("toast", NotificationMessage::new);
    public static final MessageType RELOAD_STARTUP_SCRIPTS = NET.registerS2C("reload_startup_scripts", ReloadStartupScriptsMessage::new);
    public static final MessageType DISPLAY_SERVER_ERRORS = NET.registerS2C("display_server_errors", DisplayServerErrorsMessage::new);
    public static final MessageType DISPLAY_CLIENT_ERRORS = NET.registerS2C("display_client_errors", DisplayClientErrorsMessage::new);

    public static void init() {
    }
}

