/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.components.Button
 *  net.minecraft.client.gui.components.EditBox
 *  net.minecraft.client.gui.components.events.GuiEventListener
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.network.chat.Component
 */
package LOL_141.vehicle_addition.client.gui;

import LOL_141.vehicle_addition.api.netease.NetEaseResolver;
import LOL_141.vehicle_addition.api.netease.NetEaseSongInfo;
import LOL_141.vehicle_addition.radio.NetEaseSongManager;
import LOL_141.vehicle_addition.radio.RadioChannel;
import LOL_141.vehicle_addition.radio.RadioStationManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class RadioConfigScreen
extends Screen {
    private static final int ROW_HEIGHT = 16;
    private static final int VISIBLE_ROWS = 14;
    private static final int NAME_W = 100;
    private EditBox nameBox;
    private EditBox urlBox;
    private Button addButton;
    private final List<Button> deleteButtons = new ArrayList<Button>();
    private final int[] deleteStationIndex = new int[14];
    private int scrollOffset = 0;
    private int listLeft;
    private int listWidth;

    public RadioConfigScreen() {
        super((Component)Component.m_237115_((String)"screen.vehicle_addition.config.title"));
    }

    protected void m_7856_() {
        this.listWidth = Math.min(400, this.f_96543_ - 30);
        this.listLeft = Math.max(10, (this.f_96543_ - this.listWidth) / 2);
        int deleteCol = this.listLeft + this.listWidth - 42;
        int right = this.listLeft + this.listWidth;
        int addW = 60;
        int urlW = Math.max(60, right - 100 - addW - 12 - this.listLeft);
        this.nameBox = new EditBox(this.f_96547_, this.listLeft, 30, 100, 20, (Component)Component.m_237115_((String)"screen.vehicle_addition.name"));
        this.nameBox.m_94199_(64);
        this.urlBox = new EditBox(this.f_96547_, this.listLeft + 100 + 6, 30, urlW, 20, (Component)Component.m_237115_((String)"screen.vehicle_addition.url"));
        this.urlBox.m_94199_(4096);
        this.addButton = Button.m_253074_((Component)Component.m_237115_((String)"screen.vehicle_addition.add"), b -> this.addStation()).m_252987_(this.listLeft + 100 + 6 + urlW + 6, 30, addW, 20).m_253136_();
        Button saveButton = Button.m_253074_((Component)Component.m_237115_((String)"screen.vehicle_addition.save"), b -> this.saveAndClose()).m_252987_(this.listLeft, this.f_96544_ - 30, 80, 20).m_253136_();
        Button closeButton = Button.m_253074_((Component)Component.m_237115_((String)"screen.vehicle_addition.close"), b -> this.m_7379_()).m_252987_(this.listLeft + 86, this.f_96544_ - 30, 80, 20).m_253136_();
        this.m_142416_((GuiEventListener)this.nameBox);
        this.m_142416_((GuiEventListener)this.urlBox);
        this.m_142416_((GuiEventListener)this.addButton);
        this.m_142416_((GuiEventListener)saveButton);
        this.m_142416_((GuiEventListener)closeButton);
        this.deleteButtons.clear();
        for (int i = 0; i < 14; ++i) {
            int slot = i;
            Button del = Button.m_253074_((Component)Component.m_237113_((String)"[\u00d7]"), b -> this.deleteStation(slot)).m_252987_(deleteCol, 70 + i * 16, 36, 14).m_253136_();
            this.deleteButtons.add(del);
            this.m_142416_((GuiEventListener)del);
        }
        this.refreshList();
    }

    private void addStation() {
        String stationName;
        String name = this.nameBox.m_94155_().trim();
        String input = this.urlBox.m_94155_().trim();
        if (input.isEmpty()) {
            this.send("screen.vehicle_addition.url_empty", new Object[0]);
            return;
        }
        if (input.contains("music.163.com")) {
            this.importNetease(input);
            return;
        }
        if (!input.startsWith("http")) {
            this.send("screen.vehicle_addition.url_invalid", new Object[0]);
            return;
        }
        String string = stationName = name.isEmpty() ? RadioConfigScreen.defaultName(input) : name;
        if (RadioStationManager.addStation(stationName, input)) {
            this.send("screen.vehicle_addition.added", stationName);
        }
        this.nameBox.m_94144_("");
        this.urlBox.m_94144_("");
        this.refreshList();
    }

    private void importNetease(String link) {
        long id = NetEaseResolver.parseId(link);
        if (id <= 0L) {
            this.send("screen.vehicle_addition.netease.bad_url", new Object[0]);
            return;
        }
        boolean playlist = NetEaseResolver.isPlaylistUrl(link);
        this.addButton.f_93623_ = false;
        this.send("screen.vehicle_addition.netease.resolving", new Object[0]);
        CompletableFuture.runAsync(() -> {
            try {
                if (playlist) {
                    String playlistName = NetEaseResolver.resolvePlaylistName(id);
                    List<NetEaseSongInfo> songs = NetEaseResolver.resolvePlaylist(id);
                    Minecraft.m_91087_().execute(() -> {
                        this.addButton.f_93623_ = true;
                        if (songs.isEmpty()) {
                            this.send("message.vehicle_addition.playlist_empty", new Object[0]);
                            return;
                        }
                        int added = 0;
                        for (NetEaseSongInfo song : songs) {
                            if (!NetEaseSongManager.addSong(song)) continue;
                            ++added;
                        }
                        RadioStationManager.addStation(playlistName, "netease_playlist:" + id);
                        this.send("screen.vehicle_addition.netease.added_playlist", added);
                        if (added <= 1) {
                            this.send("message.vehicle_addition.playlist_few", new Object[0]);
                        }
                        this.urlBox.m_94144_("");
                        this.refreshList();
                    });
                } else {
                    NetEaseSongInfo song = NetEaseResolver.resolveSong(id);
                    Minecraft.m_91087_().execute(() -> {
                        this.addButton.f_93623_ = true;
                        if (song == null || song.vip()) {
                            this.send("screen.vehicle_addition.netease.vip_skip", new Object[0]);
                            return;
                        }
                        if (NetEaseSongManager.addSong(song)) {
                            this.send("screen.vehicle_addition.netease.added", song.displayName());
                            RadioStationManager.addStation("\u5355\u66f2: " + song.name(), "netease_song:" + song.id());
                        }
                        this.urlBox.m_94144_("");
                        this.refreshList();
                    });
                }
            }
            catch (Exception e) {
                Minecraft.m_91087_().execute(() -> {
                    this.addButton.f_93623_ = true;
                    this.send("screen.vehicle_addition.netease.resolve_error", new Object[0]);
                });
            }
        });
    }

    private static String defaultName(String url) {
        try {
            String host = new URL(url).getHost();
            return host == null || host.isEmpty() ? "Radio" : host;
        }
        catch (Exception e) {
            return "Radio";
        }
    }

    private void deleteStation(int slot) {
        int index = this.deleteStationIndex[slot];
        if (index < 0 || index >= RadioStationManager.getStations().size()) {
            return;
        }
        String name = RadioStationManager.getStations().get(index).name();
        RadioStationManager.removeStation(index);
        this.send("screen.vehicle_addition.removed", name);
        this.refreshList();
    }

    private void saveAndClose() {
        RadioStationManager.saveSilently();
        this.m_7379_();
    }

    private void refreshList() {
        List<RadioChannel> stations = RadioStationManager.getStations();
        int maxOffset = Math.max(0, stations.size() - 14);
        this.scrollOffset = Math.min(this.scrollOffset, maxOffset);
        for (int slot = 0; slot < 14; ++slot) {
            int index = slot + this.scrollOffset;
            if (index < stations.size()) {
                this.deleteStationIndex[slot] = index;
                this.deleteButtons.get((int)slot).f_93624_ = true;
                continue;
            }
            this.deleteStationIndex[slot] = -1;
            this.deleteButtons.get((int)slot).f_93624_ = false;
        }
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        List<RadioChannel> stations = RadioStationManager.getStations();
        int maxOffset = Math.max(0, stations.size() - 14);
        if (delta > 0.0) {
            this.scrollOffset = Math.max(0, this.scrollOffset - 1);
        } else if (delta < 0.0) {
            this.scrollOffset = Math.min(maxOffset, this.scrollOffset + 1);
        }
        this.refreshList();
        return true;
    }

    public void m_88315_(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
        int index;
        this.m_280273_(gui);
        gui.m_280430_(this.f_96547_, this.f_96539_, this.listLeft, 10, 0xFFFFFF);
        gui.m_280430_(this.f_96547_, (Component)Component.m_237115_((String)"screen.vehicle_addition.name"), this.listLeft, 20, 0xAAAAAA);
        gui.m_280430_(this.f_96547_, (Component)Component.m_237115_((String)"screen.vehicle_addition.url"), this.listLeft + 100 + 6, 20, 0xAAAAAA);
        gui.m_280430_(this.f_96547_, (Component)Component.m_237115_((String)"screen.vehicle_addition.list"), this.listLeft, 56, 0xAAAAAA);
        List<RadioChannel> stations = RadioStationManager.getStations();
        int textMaxWidth = this.listWidth - 50;
        for (int slot = 0; slot < 14 && (index = slot + this.scrollOffset) < stations.size(); ++slot) {
            RadioChannel channel = stations.get(index);
            int y = 70 + slot * 16;
            Object text = index + 1 + ". " + channel.name() + "  " + channel.url();
            text = this.f_96547_.m_92834_((String)text, textMaxWidth);
            gui.m_280488_(this.f_96547_, (String)text, this.listLeft, y, 0xE0E0E0);
        }
        if (stations.isEmpty()) {
            gui.m_280430_(this.f_96547_, (Component)Component.m_237115_((String)"screen.vehicle_addition.empty"), this.listLeft, 70, 0x888888);
        }
        super.m_88315_(gui, mouseX, mouseY, partialTick);
    }

    private void send(String key, Object ... args) {
        if (Minecraft.m_91087_().f_91074_ != null) {
            Minecraft.m_91087_().f_91074_.m_5661_((Component)Component.m_237110_((String)key, (Object[])args), false);
        }
    }
}

