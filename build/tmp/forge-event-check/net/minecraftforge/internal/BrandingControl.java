/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.ResourceManagerReloadListener
 *  net.minecraftforge.fml.ModList
 */
package net.minecraftforge.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.versions.forge.ForgeVersion;
import net.minecraftforge.versions.mcp.MCPVersion;

public class BrandingControl {
    private static List<String> brandings;
    private static List<String> brandingsNoMC;
    private static List<String> overCopyrightBrandings;

    private static void computeBranding() {
        if (brandings == null) {
            ImmutableList.Builder brd = ImmutableList.builder();
            brd.add((Object)("Forge " + ForgeVersion.getVersion()));
            brd.add((Object)("Minecraft " + MCPVersion.getMCVersion()));
            brd.add((Object)("MCP " + MCPVersion.getMCPVersion()));
            int tModCount = ModList.get().size();
            brd.add((Object)ForgeI18n.parseMessage("fml.menu.loadingmods", tModCount));
            brandings = brd.build();
            brandingsNoMC = brandings.subList(1, brandings.size());
        }
    }

    private static List<String> getBrandings(boolean includeMC, boolean reverse) {
        BrandingControl.computeBranding();
        if (includeMC) {
            return reverse ? Lists.reverse(brandings) : brandings;
        }
        return reverse ? Lists.reverse(brandingsNoMC) : brandingsNoMC;
    }

    private static void computeOverCopyrightBrandings() {
        if (overCopyrightBrandings == null) {
            ImmutableList.Builder brd = ImmutableList.builder();
            if (ForgeHooksClient.forgeStatusLine != null) {
                brd.add((Object)ForgeHooksClient.forgeStatusLine);
            }
            overCopyrightBrandings = brd.build();
        }
    }

    public static void forEachLine(boolean includeMC, boolean reverse, BiConsumer<Integer, String> lineConsumer) {
        List<String> brandings = BrandingControl.getBrandings(includeMC, reverse);
        IntStream.range(0, brandings.size()).boxed().forEachOrdered(idx -> lineConsumer.accept((Integer)idx, (String)brandings.get((int)idx)));
    }

    public static void forEachAboveCopyrightLine(BiConsumer<Integer, String> lineConsumer) {
        BrandingControl.computeOverCopyrightBrandings();
        IntStream.range(0, overCopyrightBrandings.size()).boxed().forEachOrdered(idx -> lineConsumer.accept((Integer)idx, overCopyrightBrandings.get((int)idx)));
    }

    public static String getClientBranding() {
        return "forge";
    }

    public static String getServerBranding() {
        return "forge";
    }

    public static ResourceManagerReloadListener resourceManagerReloadListener() {
        return BrandingControl::onResourceManagerReload;
    }

    private static void onResourceManagerReload(ResourceManager resourceManager) {
        brandings = null;
        brandingsNoMC = null;
    }
}

