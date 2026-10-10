/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.dev;

import cc.sighs.auratip.api.tip.TipBuilder;
import cc.sighs.auratip.api.tip.TipRegistry;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.dev.DevTestCommand;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class DevJavaApiSamples {
    private static final String OWNER = "auratip_dev";
    public static final ResourceLocation DATAPACK_TIP = new ResourceLocation("auratip", "showtip_demo_intro");
    public static final ResourceLocation DATAPACK_MENU = new ResourceLocation("auratip", "example_menu");
    public static final ResourceLocation TRIGGER_SHOWTIP = new ResourceLocation("auratip", "showtip_command");
    public static final ResourceLocation TRIGGER_FIRST_JOIN = new ResourceLocation("auratip", "first_join_world");
    public static final ResourceLocation JAVA_TIP_SHOWTIP = new ResourceLocation("auratip", "dev_java_showtip");
    public static final ResourceLocation JAVA_TIP_FIRST_JOIN = new ResourceLocation("auratip", "dev_java_first_join");
    public static final ResourceLocation JAVA_TIP_BY_ID = new ResourceLocation("auratip", "dev_java_by_id");
    public static final ResourceLocation JAVA_MENU = new ResourceLocation("auratip", "dev_java_menu");
    public static final ResourceLocation JAVA_SCRIPT_ACTION = new ResourceLocation("auratip", "dev_action");

    private DevJavaApiSamples() {
    }

    public static void initCommon() {
        DevJavaApiSamples.registerRuntimeTips();
        DevTestCommand.register();
    }

    private static void registerRuntimeTips() {
        TipData a = new TipBuilder(JAVA_TIP_SHOWTIP).triggerRepeatable(TRIGGER_SHOWTIP, 0).visual(v -> v.animationStyle(new ResourceLocation("auratip", "slide_in_left")).hoverAnimationStyle(new ResourceLocation("auratip", "hover_float")).size(190, 60).positionAbsolute(12, 220)).behavior(b -> b.duration(160).pauseOnHover(true)).page(0, p -> p.title((Component)Component.m_237113_((String)"Java Runtime Tip"), 0.85f, 0).content((Component)Component.m_237113_((String)"\u7531 Java \u6ce8\u518c\uff0c/showtip \u4f1a\u89e6\u53d1\u6211\u3002\n\u73a9\u5bb6: ${player}"), 0.7f, 1)).build();
        TipData b2 = new TipBuilder(JAVA_TIP_FIRST_JOIN).triggerRepeatable(TRIGGER_FIRST_JOIN, 0).visual(v -> v.animationStyle(new ResourceLocation("auratip", "fade_and_slide")).hoverAnimationStyle(new ResourceLocation("auratip", "none")).size(180, 48).positionPreset("TOP_RIGHT")).behavior(beh -> beh.duration(120).pauseOnHover(false)).page(0, p -> p.title((Component)Component.m_237113_((String)"\u767b\u5f55\u89e6\u53d1"), 0.8f, 0).content((Component)Component.m_237113_((String)"\u8fd9\u662f Java runtime tip\uff08\u89e6\u53d1: auratip:first_join_world\uff09"), 0.65f, 1)).build();
        TipData c = new TipBuilder(JAVA_TIP_BY_ID).triggerRepeatable(new ResourceLocation("auratip", "unused_trigger"), 0).visual(v -> v.animationStyle(new ResourceLocation("auratip", "slide_in_right")).hoverAnimationStyle(new ResourceLocation("auratip", "hover_shake")).size(200, 55).positionPreset("CENTER")).behavior(beh -> beh.duration(120)).page(0, p -> p.title((Component)Component.m_237113_((String)"triggerById \u6f14\u793a"), 0.8f, 0).content((Component)Component.m_237113_((String)"\u7528 /auratipdev tip trigger_id auratip:dev_java_by_id \u70b9\u540d\u89e6\u53d1"), 0.65f, 1)).build();
        TipRegistry.setTips(OWNER, List.of(a, b2, c));
    }
}

