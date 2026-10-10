/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 */
package cc.sighs.auratip.editor.net;

import io.netty.channel.Channel;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class EditorWsHub {
    private final Set<Channel> channels = ConcurrentHashMap.newKeySet();

    EditorWsHub() {
    }

    public void add(Channel ch) {
        if (ch != null) {
            this.channels.add(ch);
        }
    }

    public void remove(Channel ch) {
        if (ch != null) {
            this.channels.remove(ch);
        }
    }

    public void closeAll() {
        for (Channel ch : this.channels) {
            try {
                ch.close();
            }
            catch (Exception exception) {}
        }
        this.channels.clear();
    }
}

