/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package cc.sighs.auratip.api.client;

import cc.sighs.auratip.client.TipClient;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.util.ResolveUtil;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public final class TipClientApi {
    private TipClientApi() {
    }

    public static void enqueue(List<TipData> tips, @Nullable Map<String, ?> variables) {
        TipClient.enqueueTips(tips, ResolveUtil.toComponentMap(variables));
    }

    public static void close() {
        TipClient.closeCurrentTip();
    }
}

