/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.espetro.client.aui.AuiRadial;
import org.espetro.client.aui.AuiRadialSlot;
import org.espetro.client.gui.EspetroTipNotifier;
import org.espetro.client.gui.RadioRadialController;
import org.espetro.client.gui.VehicleWheelController;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.CloseResupplySessionPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.ResupplyCatalogPacket;
import org.espetro.network.ResupplyEntryDeltaPacket;
import org.espetro.network.SelectResupplyEntryPacket;

public final class ResupplyRadialController {
    private static final String MENU = "resupply/items";
    private static final int PAGE_SIZE = 5;
    private static final String AVAILABLE = "#FFFFD54F";
    private static final String UNAVAILABLE = "#FFFF4D4D";
    private static final String HOVER = "#FFFFFFFF";
    private static boolean initialized;
    private static UUID token;
    private static long catalogRevision;
    private static long stateRevision;
    private static long nextActionSeq;
    private static ResupplySourceRef source;
    private static int balance;
    private static int page;
    private static List<ResupplyCatalogPacket.Entry> entries;
    private static boolean menuWasActive;
    private static long rebuildCount;

    private ResupplyRadialController() {
    }

    public static void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
    }

    private static void select(int index) {
        ResupplyCatalogPacket.Entry entry = ResupplyRadialController.find(index);
        if (entry == null || token == null || source == null) {
            return;
        }
        if (!entry.selectable()) {
            EspetroTipNotifier.showDenial("\u65e0\u6cd5\u8865\u7ed9", entry.reason().isBlank() ? "\u8be5\u9879\u76ee\u5f53\u524d\u4e0d\u53ef\u7528\u3002" : entry.reason());
            return;
        }
        NetworkManager.NET.sendToServer((Object)new SelectResupplyEntryPacket(token, catalogRevision, nextActionSeq++, index, source));
    }

    private static void navigate(String action) {
        switch (action) {
            case "previous": {
                if (page > 0) {
                    --page;
                }
                ResupplyRadialController.replacePage();
                break;
            }
            case "next": {
                if (page + 1 < ResupplyRadialController.pageCount()) {
                    ++page;
                }
                ResupplyRadialController.replacePage();
                break;
            }
            case "back": {
                ResupplyRadialController.returnToRoot();
                break;
            }
        }
    }

    public static void onCatalog(ResupplyCatalogPacket packet) {
        ResupplyRadialController.initialize();
        token = packet.token();
        catalogRevision = packet.catalogRevision();
        stateRevision = packet.stateRevision();
        source = packet.source();
        balance = packet.balance();
        entries = List.copyOf(packet.entries());
        nextActionSeq = 1L;
        page = 0;
        ResupplyRadialController.replacePage();
        menuWasActive = true;
    }

    public static void onDelta(ResupplyEntryDeltaPacket packet) {
        if (token == null || !token.equals(packet.token())) {
            return;
        }
        if (packet.close()) {
            EspetroTipNotifier.showDenial("\u8865\u7ed9\u4f1a\u8bdd\u5df2\u5173\u95ed", packet.message());
            if (AuiRadial.isOpen()) {
                AuiRadial.hide();
            }
            ResupplyRadialController.clear(false);
            return;
        }
        if (packet.stateRevision() < stateRevision) {
            return;
        }
        boolean changed = packet.stateRevision() != stateRevision || balance != packet.balance();
        stateRevision = packet.stateRevision();
        balance = packet.balance();
        if (packet.entry() != null) {
            ArrayList<ResupplyCatalogPacket.Entry> updated = new ArrayList<ResupplyCatalogPacket.Entry>(entries);
            int index = packet.entry().index();
            if (index >= 0 && index < updated.size() && !ResupplyRadialController.sameView(updated.get(index), packet.entry())) {
                updated.set(index, packet.entry());
                entries = List.copyOf(updated);
                changed = true;
            }
        }
        if (!packet.success() && !packet.message().isBlank()) {
            EspetroTipNotifier.showDenial("\u65e0\u6cd5\u8865\u7ed9", packet.message());
        }
        if (changed) {
            ResupplyRadialController.replacePage();
        }
    }

    public static void tick() {
        boolean active;
        if (token == null) {
            return;
        }
        boolean bl = active = AuiRadial.isOpen() && AuiRadial.isPage(MENU);
        if (menuWasActive && !active) {
            ResupplyRadialController.clear(true);
        }
        menuWasActive = active;
    }

    public static boolean isActive() {
        return token != null && AuiRadial.isOpen() && AuiRadial.isPage(MENU);
    }

    public static long rebuildCount() {
        return rebuildCount;
    }

    private static void replacePage() {
        if (token == null) {
            return;
        }
        List<AuiRadialSlot> slots = ResupplyRadialController.buildPage();
        if (AuiRadial.isOpen()) {
            AuiRadial.replace(slots, MENU);
        } else {
            AuiRadial.show(slots, MENU);
        }
        ++rebuildCount;
        menuWasActive = true;
    }

    private static boolean sameView(ResupplyCatalogPacket.Entry left, ResupplyCatalogPacket.Entry right) {
        if (left == right) {
            return true;
        }
        if (left == null || right == null) {
            return false;
        }
        return left.index() == right.index() && left.configuredId().equals(right.configuredId()) && left.count() == right.count() && left.max() == right.max() && left.ammoCost() == right.ammoCost() && left.current() == right.current() && left.selectable() == right.selectable() && left.reason().equals(right.reason()) && ItemStack.m_150942_(left.icon(), right.icon()) && left.icon().m_41613_() == right.icon().m_41613_();
    }

    private static List<AuiRadialSlot> buildPage() {
        int pages = ResupplyRadialController.pageCount();
        if (page >= pages) {
            page = Math.max(0, pages - 1);
        }
        ArrayList<AuiRadialSlot> slots = new ArrayList<AuiRadialSlot>();
        int first = page * 5;
        int end = Math.min(entries.size(), first + 5);
        for (int i = first; i < end; ++i) {
            ResupplyCatalogPacket.Entry entry = ResupplyRadialController.effective(entries.get(i));
            ItemStack iconStack = entry.icon().m_41619_() ? new ItemStack(Items.f_42127_) : entry.icon().m_41777_();
            iconStack.m_41764_(1);
            String itemName = entry.icon().m_41619_() ? entry.configuredId() : entry.icon().m_41786_().getString();
            String label = itemName + " \u00a7f+" + entry.count() + " \u00a77" + entry.current() + "/" + entry.max() + " \u00a7b[" + entry.ammoCost() + "]";
            if (!entry.selectable() && !entry.reason().isBlank()) {
                label = label + " \u00a7c" + entry.reason();
            }
            int index = entry.index();
            boolean selectable = entry.selectable();
            slots.add(AuiRadialSlot.item("espetro.resupply.entry." + entry.index(), Component.m_237113_(label), iconStack, selectable ? AVAILABLE : UNAVAILABLE, selectable, () -> ResupplyRadialController.select(index)));
        }
        slots.add(AuiRadialSlot.glyph("espetro.resupply.back", Component.m_237113_("\u8fd4\u56de  \u00a7b\u4f59\u989d " + balance), "\u21a9", "#FF44484D", () -> ResupplyRadialController.navigate("back")));
        if (page > 0) {
            slots.add(AuiRadialSlot.glyph("espetro.resupply.previous", Component.m_237113_("\u4e0a\u4e00\u9875 " + page + "/" + pages), "\u2039", "#FF506070", () -> ResupplyRadialController.navigate("previous")));
        }
        if (page + 1 < pages) {
            slots.add(AuiRadialSlot.glyph("espetro.resupply.next", Component.m_237113_("\u4e0b\u4e00\u9875 " + (page + 2) + "/" + pages), "\u203a", "#FF506070", () -> ResupplyRadialController.navigate("next")));
        }
        return slots;
    }

    private static ResupplyCatalogPacket.Entry effective(ResupplyCatalogPacket.Entry entry) {
        if (entry.selectable() && balance < entry.ammoCost()) {
            return new ResupplyCatalogPacket.Entry(entry.index(), entry.icon(), entry.configuredId(), entry.count(), entry.max(), entry.ammoCost(), entry.current(), false, "\u6765\u6e90\u5f39\u836f\u4e0d\u8db3");
        }
        return entry;
    }

    static int pageCount(int entryCount, int pageSize) {
        int size = Math.max(1, pageSize);
        return Math.max(1, (Math.max(0, entryCount) + size - 1) / size);
    }

    static List<String> pageSlotIds(int entryCount, int pageIndex, int balanceValue) {
        int pages = ResupplyRadialController.pageCount(entryCount, 5);
        int safePage = Math.min(Math.max(0, pageIndex), pages - 1);
        int first = safePage * 5;
        int end = Math.min(entryCount, first + 5);
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = first; i < end; ++i) {
            ids.add("espetro.resupply.entry." + i);
        }
        ids.add("espetro.resupply.back");
        if (safePage > 0) {
            ids.add("espetro.resupply.previous");
        }
        if (safePage + 1 < pages) {
            ids.add("espetro.resupply.next");
        }
        return ids;
    }

    private static int pageCount() {
        return ResupplyRadialController.pageCount(entries.size(), 5);
    }

    private static ResupplyCatalogPacket.Entry find(int index) {
        return index >= 0 && index < entries.size() ? ResupplyRadialController.effective(entries.get(index)) : null;
    }

    private static void returnToRoot() {
        ResupplySourceRef previousSource = source;
        ResupplyRadialController.clear(true);
        if (previousSource == null) {
            return;
        }
        if (previousSource.kind() == ResupplySourceRef.Kind.RADIO) {
            RadioRadialController.replaceRoot();
        } else {
            VehicleWheelController.replaceRoot();
        }
    }

    private static void clear(boolean notifyServer) {
        UUID oldToken = token;
        token = null;
        source = null;
        entries = List.of();
        page = 0;
        menuWasActive = false;
        if (notifyServer && oldToken != null) {
            NetworkManager.NET.sendToServer((Object)new CloseResupplySessionPacket(oldToken));
        }
    }

    static {
        nextActionSeq = 1L;
        entries = List.of();
    }
}

