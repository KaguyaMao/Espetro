/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  net.minecraft.server.packs.repository.Pack
 *  net.minecraft.server.packs.repository.PackRepository
 *  net.minecraft.server.packs.repository.RepositorySource
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.forgespi.language.IModFileInfo
 *  net.minecraftforge.forgespi.language.IModInfo
 *  net.minecraftforge.forgespi.locating.IModFile
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.resource;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.resource.PathPackResources;
import org.jetbrains.annotations.NotNull;

public class ResourcePackLoader {
    private static Map<IModFile, PathPackResources> modResourcePacks;

    public static Optional<PathPackResources> getPackFor(String modId) {
        return Optional.ofNullable(ModList.get().getModFileById(modId)).map(IModFileInfo::getFile).map(mf -> modResourcePacks.get(mf));
    }

    @Deprecated
    public static void loadResourcePacks(PackRepository resourcePacks, BiFunction<Map<IModFile, ? extends PathPackResources>, BiConsumer<? super PathPackResources, Pack>, ? extends RepositorySource> packFinder) {
        ResourcePackLoader.loadResourcePacks(resourcePacks, (Map<IModFile, ? extends PathPackResources> map) -> (RepositorySource)packFinder.apply((Map<IModFile, ? extends PathPackResources>)map, (rp, p) -> {}));
    }

    public static void loadResourcePacks(PackRepository resourcePacks, Function<Map<IModFile, ? extends PathPackResources>, ? extends RepositorySource> packFinder) {
        modResourcePacks = ModList.get().getModFiles().stream().filter(mf -> mf.requiredLanguageLoaders().stream().noneMatch(ls -> ls.languageName().equals("minecraft"))).map(mf -> Pair.of((Object)mf, (Object)((Object)ResourcePackLoader.createPackForMod(mf)))).collect(Collectors.toMap(p -> ((IModFileInfo)p.getFirst()).getFile(), Pair::getSecond, (u, v) -> {
            throw new IllegalStateException(String.format(Locale.ENGLISH, "Duplicate key %s", u));
        }, LinkedHashMap::new));
        resourcePacks.addPackFinder(packFinder.apply(modResourcePacks));
    }

    @NotNull
    public static PathPackResources createPackForMod(final IModFileInfo mf) {
        return new PathPackResources(mf.getFile().getFileName(), true, mf.getFile().getFilePath()){
            private final IModFile modFile;
            {
                super(packId, isBuiltin, source);
                this.modFile = mf.getFile();
            }

            @Override
            @NotNull
            protected Path resolve(String ... paths) {
                return this.modFile.findResource(paths);
            }
        };
    }

    public static List<String> getPackNames() {
        return ModList.get().applyForEachModFile(mf -> "mod:" + ((IModInfo)mf.getModInfos().get(0)).getModId()).filter(n -> !n.equals("mod:minecraft")).collect(Collectors.toList());
    }

    public static <V> Comparator<Map.Entry<String, V>> getSorter() {
        ArrayList<String> order = new ArrayList<String>();
        order.add("vanilla");
        order.add("mod_resources");
        ModList.get().getModFiles().stream().filter(mf -> mf.requiredLanguageLoaders().stream().noneMatch(ls -> ls.languageName().equals("minecraft"))).map(e -> ((IModInfo)e.getMods().get(0)).getModId()).map(e -> "mod:" + e).forEach(order::add);
        Object2IntOpenHashMap order_f = new Object2IntOpenHashMap(order.size());
        for (int x = 0; x < order.size(); ++x) {
            order_f.put((Object)((String)order.get(x)), x);
        }
        return (arg_0, arg_1) -> ResourcePackLoader.lambda$getSorter$14((Object2IntMap)order_f, arg_0, arg_1);
    }

    private static /* synthetic */ int lambda$getSorter$14(Object2IntMap order_f, Map.Entry e1, Map.Entry e2) {
        int i2;
        String s1 = (String)e1.getKey();
        String s2 = (String)e2.getKey();
        int i1 = order_f.getOrDefault((Object)s1, -1);
        if (i1 == (i2 = order_f.getOrDefault((Object)s2, -1)) && i1 == -1) {
            return s1.compareTo(s2);
        }
        if (i1 == -1) {
            return 1;
        }
        if (i2 == -1) {
            return -1;
        }
        return i2 - i1;
    }
}

