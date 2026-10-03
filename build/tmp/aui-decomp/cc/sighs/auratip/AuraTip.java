/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cc.sighs.oelib.data.DataRegistry
 *  cc.sighs.oelib.event.EventAutoRegistration
 *  cc.sighs.oelib.network.api.NetworkAutoRegistration
 *  com.mojang.logging.LogUtils
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.common.Mod
 *  org.slf4j.Logger
 */
package cc.sighs.auratip;

import cc.sighs.auratip.AuraTipClient;
import cc.sighs.auratip.api.radiamenu.icon.IRadialIcon;
import cc.sighs.auratip.api.radiamenu.icon.ItemIcon;
import cc.sighs.auratip.api.radiamenu.icon.TextureIcon;
import cc.sighs.auratip.command.AuraTipEditorCommand;
import cc.sighs.auratip.command.ShowTipCommand;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.dev.DevEnvironment;
import cc.sighs.auratip.dev.DevJavaApiSamples;
import cc.sighs.oelib.data.DataRegistry;
import cc.sighs.oelib.event.EventAutoRegistration;
import cc.sighs.oelib.network.api.NetworkAutoRegistration;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(value="auratip")
public class AuraTip {
    public static final String MOD_ID = "auratip";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AuraTip() {
        IRadialIcon.register(ItemIcon.TYPE, ItemIcon.class, ItemIcon.CODEC);
        IRadialIcon.register(TextureIcon.TYPE, TextureIcon.class, TextureIcon.CODEC);
        DataRegistry.register(TipData.class, TipData.CODEC);
        DataRegistry.register(RadialMenuData.class, RadialMenuData.CODEC);
        NetworkAutoRegistration.registerBasePackage((String)"cc.sighs.auratip.network");
        EventAutoRegistration.registerBasePackage((String)"cc.sighs.auratip.handler");
        ShowTipCommand.register();
        AuraTipEditorCommand.register();
        if (DevEnvironment.isDev()) {
            DevJavaApiSamples.initCommon();
        }
        DistExecutor.safeRunWhenOn((Dist)Dist.CLIENT, () -> AuraTipClient::init);
    }
}

