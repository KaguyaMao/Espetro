/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.espetro.Espetro;
import org.espetro.client.ClientEquipZones;
import org.espetro.client.HcrTacticalMapBridge;
import org.espetro.client.aui.AuiScreen;
import org.espetro.client.aui.GuiElement;
import org.espetro.client.aui.GuiRect;
import org.espetro.client.gui.ClassPreviewRenderer;
import org.espetro.client.gui.ClassSelectionGui;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientGovernanceState;
import org.espetro.client.gui.CurrentMapBackgroundRenderer;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.EspetroMenuScreen;
import org.espetro.client.gui.EspetroTipNotifier;
import org.espetro.client.gui.MatchScoreboardScreen;
import org.espetro.client.gui.RoleIconResources;
import org.espetro.client.gui.ScrollableList;
import org.espetro.client.gui.SquadScreen;
import org.espetro.client.gui.TroopCountOverlay;
import org.espetro.network.EquipZoneSyncPacket;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.team.Fireteam;
import org.espetro.team.GamePhase;

public class UnifiedDeployScreen
extends EspetroMenuScreen {
    private static final int BTN_H = 12;
    private static final int CLASS_BTN_H = 22;
    private static final int CLASS_ICON_SIZE = 17;
    private static final int TITLE_H = 35;
    private static final int STATUS_BAR_H = 13;
    private static final int MAP_FOOTER_H = 20;
    private static final int SECTION_TITLE_H = 10;
    private static final int INNER_PADDING = 3;
    private static final int SCROLLBAR_RESERVED_W = 6;
    private static final int SQUAD_ROW_H = 11;
    private static final int SQUAD_MEMBER_ROW_H = 9;
    private static final int SQUAD_ACTION_ROW_H = 10;
    private static final int SQUAD_ACCENT_BAR_W = 2;
    private static final int FIRETEAM_CONTEXT_W = 118;
    private static final int FIRETEAM_CONTEXT_ROW_H = 12;
    private static final int VARIANT_POPUP_W = 180;
    private static final int VARIANT_HEADER_H = 18;
    private static final int VARIANT_ROW_H = 28;
    private static final int VARIANT_MAX_VISIBLE = 6;
    private static final int SQUAD_ROW_GAP = 1;
    private static final float UI_TEXT_SCALE = 0.72f;
    private static final float TOOLTIP_TEXT_SCALE = 0.68f;
    private static final float SQUAD_TEXT_SCALE = 0.68f;
    private static final float SQUAD_MEMBER_TEXT_SCALE = 0.64f;
    private static final int BTN_BG_NORMAL = -15000032;
    private static final int BTN_BG_HOVER = -12365499;
    private static final int BTN_BG_DISABLED = -15197411;
    private static final int CLASS_BG_UNAVAILABLE = -11919324;
    private static final int CLASS_BG_SELECTED = -9803232;
    private static final int CLASS_BORDER_UNAVAILABLE = -7718334;
    private static final int BTN_BORDER = -10919842;
    private static final int BTN_TEXT = 0xFFFFFF;
    private static final int CHROME_BG = -16777216;
    private static final int PANEL_LEFT_BG = -16777216;
    private static final int PANEL_CENTER_BG = -16777216;
    private static final int PANEL_MAP_FOOTER_BG = -16777216;
    private static final int SCREEN_SHADE = -16777216;
    private static final int STATIC_DIVIDER = -12959939;
    private static final Pattern COORDINATE_PATTERN = Pattern.compile("[-+]?\\d+(?:\\.\\d+)?");
    private static final ResourceLocation HAB_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/hab.png");
    private static final ResourceLocation RALLY_ICON = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"textures/gui/squad/rally.png");
    private final String factionId;
    private final String factionName;
    private final String factionDescription;
    private final String factionIcon;
    private final List<UnifiedDeployScreenPacket.ClassInfo> classes;
    private final Map<String, Integer> classCounts;
    private final Map<String, Map<String, Integer>> variantCounts = new HashMap<String, Map<String, Integer>>();
    private final boolean hasDeployPoint;
    private final String deployPointPos;
    private final List<UnifiedDeployScreenPacket.BastionItem> bastions;
    private final boolean isCommander;
    private final List<UnifiedDeployScreenPacket.SquadInfo> squads;
    private final List<UnifiedDeployScreenPacket.SquadCategoryInfo> squadCategories;
    private int mySquadId;
    private int deployTimeRemaining;
    private long deployTimerAnchorMs;
    private int deployTimerAnchorSeconds;
    private final String team;
    private boolean waitingForDeploySelection;
    private long outpostRedeployCooldownEndsAt;
    private long classSwitchCooldownEndsAt;
    private String selectedClassId;
    private int lastDisplayedClassSwitchCooldown = -1;
    private boolean lastClassSelectionLocationAllowed;
    private GovernanceStatePacket governanceState = ClientGovernanceState.get();
    private long governanceReceivedAtMs = ClientGovernanceState.getReceivedAtMs();
    private final Map<String, EspButton> governanceVoteButtons = new HashMap<String, EspButton>();
    private final EnumSet<Section> dirtySections = EnumSet.noneOf(Section.class);
    private UUID fireteamContextTarget;
    private int fireteamContextX;
    private int fireteamContextY;
    private final List<FireteamContextEntry> fireteamContextEntries = new ArrayList<FireteamContextEntry>();
    private GuiElement fireteamContextRoot;
    private final List<EspButton> classButtons = new ArrayList<EspButton>();
    private final Map<EspButton, Integer> classButtonToClassIndex = new HashMap<EspButton, Integer>();
    private final List<EspButton> deployButtons = new ArrayList<EspButton>();
    private final Map<EspButton, String> deployButtonPositions = new HashMap<EspButton, String>();
    private final Map<EspButton, String> deployButtonCommands = new HashMap<EspButton, String>();
    private final Map<EspButton, String> deployButtonBaseLabels = new HashMap<EspButton, String>();
    private final Map<EspButton, Long> deployButtonNextWaveAt = new HashMap<EspButton, Long>();
    private final Map<EspButton, String> deployButtonNameCores = new HashMap<EspButton, String>();
    private final Map<EspButton, Integer> deployButtonWaveSeconds = new HashMap<EspButton, Integer>();
    private final Map<EspButton, Long> deployButtonHabAvailableAt = new HashMap<EspButton, Long>();
    private final Map<EspButton, Integer> deployButtonHabActivationSeconds = new HashMap<EspButton, Integer>();
    private EspButton outpostRedeployButton;
    private EspButton confirmDeployButton;
    private PlainText classTitleText;
    private PlainText statusText;
    private PlainText statusTimerText;
    private PlainText troopCountText;
    private PlainText phaseTitleText;
    private PlainText governanceTimerText;
    private String pendingDeployPosition;
    private String pendingDeployCommand;
    private final Set<Integer> expandedSquadIds = new HashSet<Integer>();
    private int variantPopupClassIndex = -1;
    private int variantPopupX;
    private int variantPopupY;
    private int variantPopupH;
    private int variantPopupScroll;
    private int lastMouseX;
    private int lastMouseY;
    private final ClassPreviewRenderer previewRenderer = new ClassPreviewRenderer();
    private int activePreviewClassIndex = -1;
    private String activePreviewVariantId = null;
    private long previewGraceDeadlineMs = 0L;
    private static final long PREVIEW_GRACE_MS = 350L;
    private ScrollableList classScrollList;
    private ScrollableList deployScrollList;
    private ScrollableList squadScrollList;
    private GuiElement squadSectionRoot;
    private GuiElement classSectionRoot;
    private GuiElement deploySectionRoot;
    private GuiElement mapControlsRoot;
    private GuiElement statusSectionRoot;
    private int leftX;
    private int leftY;
    private int leftW;
    private int leftH;
    private int centerX;
    private int centerY;
    private int centerW;
    private int centerH;
    private int squadAreaX;
    private int squadAreaY;
    private int squadAreaW;
    private int squadAreaH;
    private int classAreaX;
    private int classAreaY;
    private int classAreaW;
    private int classAreaH;
    private int deployAreaX;
    private int deployAreaY;
    private int deployAreaW;
    private int deployAreaH;
    private int mapX;
    private int mapY;
    private int mapW;
    private int mapH;
    private static final int ICON_BTN = 15;
    private static final int ICON_GRID_GAP = 2;
    private static final int ROW_LABEL_W = 10;

    public UnifiedDeployScreen(UnifiedDeployScreenPacket data) {
        super(Component.m_237113_("\u90e8\u7f72\u9762\u677f"));
        this.factionId = data.getFactionId();
        this.factionName = data.getFactionName();
        this.factionDescription = data.getFactionDescription();
        this.factionIcon = data.getFactionIcon();
        this.classes = new ArrayList<UnifiedDeployScreenPacket.ClassInfo>(data.getClasses());
        this.classCounts = new HashMap<String, Integer>(data.getClassCounts());
        this.replaceVariantCounts(data.getVariantCounts());
        this.hasDeployPoint = data.hasDeployPoint();
        this.deployPointPos = data.getDeployPointPos();
        this.bastions = new ArrayList<UnifiedDeployScreenPacket.BastionItem>(data.getBastions() == null ? List.of() : data.getBastions());
        this.bastions.sort(Comparator.comparing(b -> b.type == null ? "" : b.type).thenComparing(b -> b.id == null ? "" : b.id.toString()));
        this.isCommander = data.isCommander();
        this.squads = new ArrayList<UnifiedDeployScreenPacket.SquadInfo>(data.getSquads());
        this.squadCategories = new ArrayList<UnifiedDeployScreenPacket.SquadCategoryInfo>(data.getSquadCategories());
        this.mySquadId = data.getMySquadId();
        this.team = data.getTeam();
        this.selectedClassId = UnifiedDeployScreen.normalizeSelectedClassId(data.getSelectedClassId());
        this.waitingForDeploySelection = data.isWaitingForDeploySelection();
        this.outpostRedeployCooldownEndsAt = System.currentTimeMillis() + (long)data.getOutpostRedeployCooldownRemaining() * 1000L;
        this.classSwitchCooldownEndsAt = System.currentTimeMillis() + (long)data.getClassSwitchCooldownRemaining() * 1000L;
        this.anchorDeployTimer(data.getDeployTimeRemaining());
    }

    private void anchorDeployTimer(int seconds) {
        this.deployTimeRemaining = seconds;
        this.deployTimerAnchorSeconds = seconds;
        this.deployTimerAnchorMs = System.currentTimeMillis();
    }

    public void updateBastions(List<UnifiedDeployScreenPacket.BastionItem> nextBastions) {
        boolean structureChanged;
        ArrayList<UnifiedDeployScreenPacket.BastionItem> next = nextBastions == null ? new ArrayList<UnifiedDeployScreenPacket.BastionItem>() : new ArrayList<UnifiedDeployScreenPacket.BastionItem>(nextBastions);
        next.sort(Comparator.comparing(b -> b.type == null ? "" : b.type).thenComparing(b -> b.id == null ? "" : b.id.toString()));
        boolean bl = structureChanged = this.bastions.size() != next.size();
        if (!structureChanged) {
            for (int i = 0; i < this.bastions.size(); ++i) {
                UnifiedDeployScreenPacket.BastionItem a = this.bastions.get(i);
                UnifiedDeployScreenPacket.BastionItem b2 = (UnifiedDeployScreenPacket.BastionItem)next.get(i);
                if (Objects.equals(a.id, b2.id) && Objects.equals(a.type, b2.type) && Objects.equals(a.name, b2.name) && Objects.equals(a.pos, b2.pos) && Objects.equals(a.status, b2.status)) continue;
                structureChanged = true;
                break;
            }
        }
        this.bastions.clear();
        this.bastions.addAll(next);
        if (structureChanged) {
            this.invalidateSections(Section.DEPLOY, Section.STATUS);
            return;
        }
        HashMap<UUID, Long> waveById = new HashMap<UUID, Long>();
        HashMap<UUID, Integer> totalById = new HashMap<UUID, Integer>();
        HashMap<UUID, Long> habAtById = new HashMap<UUID, Long>();
        HashMap<UUID, Integer> habTotalById = new HashMap<UUID, Integer>();
        for (UnifiedDeployScreenPacket.BastionItem item : this.bastions) {
            waveById.put(item.id, item.nextWaveAtEpochMs);
            totalById.put(item.id, item.waveSeconds);
            habAtById.put(item.id, item.habAvailableAtEpochMs);
            habTotalById.put(item.id, item.habActivationTotalSeconds);
        }
        for (EspButton button : this.deployButtons) {
            String command = this.deployButtonCommands.get(button);
            if (command == null || !command.startsWith("bastion select ")) continue;
            String idText = command.substring("bastion select ".length()).trim();
            try {
                Long habAt;
                Integer total;
                UUID id = UUID.fromString(idText);
                Long wave = (Long)waveById.get(id);
                if (wave != null) {
                    if (wave > 0L) {
                        this.deployButtonNextWaveAt.put(button, wave);
                    } else {
                        this.deployButtonNextWaveAt.remove(button);
                    }
                }
                if ((total = (Integer)totalById.get(id)) != null && total > 0) {
                    this.deployButtonWaveSeconds.put(button, total);
                }
                if ((habAt = (Long)habAtById.get(id)) != null && habAt > 0L) {
                    this.deployButtonHabAvailableAt.put(button, habAt);
                } else {
                    this.deployButtonHabAvailableAt.remove(button);
                }
                Integer habTotal = (Integer)habTotalById.get(id);
                if (habTotal != null && habTotal > 0) {
                    this.deployButtonHabActivationSeconds.put(button, habTotal);
                    continue;
                }
                this.deployButtonHabActivationSeconds.remove(button);
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
        this.refreshRallyWaveLabels();
        this.refreshHabActivationLabels();
        this.refreshConfirmDeployButton();
    }

    public void updateClassCounts(Map<String, Integer> counts) {
        this.updateClassCounts(counts, null, null);
    }

    public void updateClassCounts(Map<String, Integer> counts, Map<String, Map<String, Integer>> updatedVariantCounts) {
        this.updateClassCounts(counts, updatedVariantCounts, null);
    }

    public void updateClassCounts(Map<String, Integer> counts, Map<String, Map<String, Integer>> updatedVariantCounts, Map<String, Integer> updatedSquadCounts) {
        boolean countsChanged = counts != null && !counts.equals(this.classCounts);
        boolean variantsChanged = updatedVariantCounts != null && !UnifiedDeployScreen.variantCountsEqual(this.variantCounts, updatedVariantCounts);
        boolean squadCountsChanged = false;
        if (updatedSquadCounts != null) {
            for (UnifiedDeployScreenPacket.ClassInfo cls : this.classes) {
                int next = Math.max(0, updatedSquadCounts.getOrDefault(cls.classId, 0));
                if (cls.squadCurrentCount == next) continue;
                cls.squadCurrentCount = next;
                squadCountsChanged = true;
            }
        }
        if (!(countsChanged || variantsChanged || squadCountsChanged)) {
            return;
        }
        if (countsChanged) {
            this.classCounts.clear();
            this.classCounts.putAll(counts);
        }
        if (variantsChanged) {
            this.replaceVariantCounts(updatedVariantCounts);
        }
        this.refreshClassButtons();
    }

    private void replaceVariantCounts(Map<String, Map<String, Integer>> counts) {
        this.variantCounts.clear();
        if (counts == null) {
            return;
        }
        for (Map.Entry<String, Map<String, Integer>> entry : counts.entrySet()) {
            this.variantCounts.put(entry.getKey(), new HashMap<String, Integer>(entry.getValue()));
        }
    }

    private static boolean variantCountsEqual(Map<String, Map<String, Integer>> a, Map<String, Map<String, Integer>> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        for (Map.Entry<String, Map<String, Integer>> e : a.entrySet()) {
            Map<String, Integer> other = b.get(e.getKey());
            if (other != null && other.equals(e.getValue())) continue;
            return false;
        }
        return true;
    }

    public void updateClasses(List<UnifiedDeployScreenPacket.ClassInfo> nextClasses, Map<String, Integer> counts, Map<String, Map<String, Integer>> updatedVariantCounts) {
        boolean countsChanged = counts != null && !counts.equals(this.classCounts);
        boolean variantsChanged = updatedVariantCounts != null && !UnifiedDeployScreen.variantCountsEqual(this.variantCounts, updatedVariantCounts);
        boolean classDataChanged = false;
        boolean classGridChanged = false;
        if (nextClasses != null) {
            classGridChanged = !UnifiedDeployScreen.classGridEquals(this.classes, nextClasses);
            classDataChanged = !UnifiedDeployScreen.classDisplayStateEquals(this.classes, nextClasses);
            this.classes.clear();
            this.classes.addAll(nextClasses);
        }
        if (countsChanged) {
            this.classCounts.clear();
            this.classCounts.putAll(counts);
        }
        if (variantsChanged) {
            this.replaceVariantCounts(updatedVariantCounts);
        }
        if (!(classGridChanged || classDataChanged || countsChanged || variantsChanged)) {
            return;
        }
        if (classGridChanged) {
            this.closeVariantPopup();
            this.activePreviewClassIndex = -1;
            this.activePreviewVariantId = null;
            this.previewGraceDeadlineMs = 0L;
            this.invalidateSections(Section.CLASS);
            return;
        }
        this.refreshClassButtons();
    }

    public void updateSelectedClass(String updatedSelectedClassId) {
        String next = UnifiedDeployScreen.normalizeSelectedClassId(updatedSelectedClassId);
        if (Objects.equals(this.selectedClassId, next)) {
            return;
        }
        this.selectedClassId = next;
        this.refreshDeployButtonStates();
        this.refreshClassButtons();
    }

    private static String normalizeSelectedClassId(String classId) {
        return classId == null || classId.isBlank() ? null : classId;
    }

    private static boolean classGridEquals(List<UnifiedDeployScreenPacket.ClassInfo> a, List<UnifiedDeployScreenPacket.ClassInfo> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            UnifiedDeployScreenPacket.ClassInfo x = a.get(i);
            UnifiedDeployScreenPacket.ClassInfo y = b.get(i);
            if (!Objects.equals(x.classId, y.classId) || x.variants.size() != y.variants.size()) {
                return false;
            }
            for (int v = 0; v < x.variants.size(); ++v) {
                if (Objects.equals(x.variants.get((int)v).variantId, y.variants.get((int)v).variantId)) continue;
                return false;
            }
        }
        return true;
    }

    private static boolean classDisplayStateEquals(List<UnifiedDeployScreenPacket.ClassInfo> a, List<UnifiedDeployScreenPacket.ClassInfo> b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        for (int i = 0; i < a.size(); ++i) {
            UnifiedDeployScreenPacket.ClassInfo y;
            UnifiedDeployScreenPacket.ClassInfo x = a.get(i);
            if (x == (y = b.get(i))) continue;
            if (x == null || y == null) {
                return false;
            }
            if (!(x.maxPlayers == y.maxPlayers && x.strictCount == y.strictCount && x.teamCount == y.teamCount && x.maxPerSquad == y.maxPerSquad && x.squadCurrentCount == y.squadCurrentCount && Objects.equals(x.classId, y.classId) && Objects.equals(x.name, y.name) && Objects.equals(x.icon, y.icon) && Objects.equals(x.iconImage, y.iconImage) && x.variants.size() == y.variants.size())) {
                return false;
            }
            for (int v = 0; v < x.variants.size(); ++v) {
                UnifiedDeployScreenPacket.VariantInfo vx = x.variants.get(v);
                UnifiedDeployScreenPacket.VariantInfo vy = y.variants.get(v);
                if (Objects.equals(vx.variantId, vy.variantId) && vx.maxPlayers == vy.maxPlayers && vx.currentCount == vy.currentCount && Objects.equals(vx.name, vy.name)) continue;
                return false;
            }
        }
        return true;
    }

    public void updateTimeRemaining(int seconds) {
        boolean statusLayoutChanged = this.deployTimeRemaining < 0 != seconds < 0;
        this.anchorDeployTimer(seconds);
        this.refreshTitleTimer();
        if (statusLayoutChanged) {
            this.invalidateSections(Section.STATUS);
        }
    }

    public void updateBattleTimer() {
        this.refreshTitleTimer();
    }

    public void updateDeploymentState(boolean waitingForSelection, int redeployCooldownRemaining) {
        this.waitingForDeploySelection = waitingForSelection;
        this.outpostRedeployCooldownEndsAt = System.currentTimeMillis() + (long)Math.max(0, redeployCooldownRemaining) * 1000L;
        this.refreshDeployButtonStates();
    }

    public void updateClassSwitchCooldown(int remainingSeconds) {
        this.classSwitchCooldownEndsAt = System.currentTimeMillis() + (long)Math.max(0, remainingSeconds) * 1000L;
        this.refreshClassSwitchCooldown();
    }

    public boolean isWaitingForDeploySelection() {
        return this.waitingForDeploySelection;
    }

    public void updateSquads(List<UnifiedDeployScreenPacket.SquadInfo> updatedSquads, int updatedMySquadId) {
        List<Object> nextSquads;
        List<Object> list = nextSquads = updatedSquads == null ? List.of() : updatedSquads;
        if (this.mySquadId == updatedMySquadId && this.squads.equals(nextSquads)) {
            return;
        }
        this.squads.clear();
        this.squads.addAll(nextSquads);
        HashSet<Integer> availableSquadIds = new HashSet<Integer>();
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            availableSquadIds.add(squad.id);
        }
        this.expandedSquadIds.retainAll(availableSquadIds);
        this.mySquadId = updatedMySquadId;
        this.closeFireteamContextMenu();
        this.invalidateSections(Section.SQUAD);
        if (this.statusText != null) {
            this.statusText.setText(this.buildStatusText());
        }
        this.refreshClassButtons();
    }

    public void updateGovernance(GovernanceStatePacket packet) {
        GovernanceStatePacket.TeamState previous = this.activeGovernance();
        if (packet != null) {
            ClientGovernanceState.update(packet);
        }
        this.governanceState = ClientGovernanceState.get();
        this.governanceReceivedAtMs = ClientGovernanceState.getReceivedAtMs();
        GovernanceStatePacket.TeamState current = this.activeGovernance();
        if (!UnifiedDeployScreen.sameGovernanceLayout(previous, current)) {
            this.invalidateSections(Section.MAP_CONTROLS);
        } else {
            this.refreshGovernanceLabels(current);
        }
    }

    private void refreshGovernanceLabels(GovernanceStatePacket.TeamState current) {
        if (current == null) {
            return;
        }
        if (this.governanceTimerText != null) {
            this.governanceTimerText.setText("\u00a7e\u5269\u4f59 " + ClientGovernanceState.secondsLeft(current) + "s");
        }
        for (Map.Entry<String, EspButton> e : this.governanceVoteButtons.entrySet()) {
            try {
                UUID candidate = UUID.fromString(e.getKey());
                e.getValue().setLabel(UnifiedDeployScreen.buildVoteButtonLabel(current, candidate, UnifiedDeployScreen.votePrefixFor(current, candidate)));
            }
            catch (IllegalArgumentException illegalArgumentException) {}
        }
    }

    private static String votePrefixFor(GovernanceStatePacket.TeamState state, UUID candidate) {
        if (state == null || candidate == null) {
            return "\u5019\u9009\u4eba";
        }
        if ("IMPEACHMENT_VOTE".equals(state.state)) {
            if (candidate.equals(state.commander)) {
                return "\u539f\u6307\u6325\u5b98";
            }
            if (candidate.equals(state.challenger)) {
                return "\u6311\u6218\u8005";
            }
        }
        return "\u5fd7\u613f\u8005";
    }

    private static String buildVoteButtonLabel(GovernanceStatePacket.TeamState state, UUID candidate, String prefix) {
        String name = MatchScoreboardScreen.nameFor(candidate);
        int votes = ClientGovernanceState.voteCount(state, candidate);
        boolean mine = ClientGovernanceState.isMyVote(state, candidate);
        String mark = mine ? "\u00a7a\u2713 " : "\u00a7f";
        return mark + prefix + "\u00a7f\uff1a\u00a7e" + name + "  \u00a7b[" + votes + "\u7968]";
    }

    private static boolean sameGovernanceLayout(GovernanceStatePacket.TeamState a, GovernanceStatePacket.TeamState b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return Objects.equals(a.state, b.state) && Objects.equals(a.commander, b.commander) && Objects.equals(a.challenger, b.challenger) && Objects.equals(a.volunteers, b.volunteers);
    }

    private static void drawScaledString(GuiGraphics graphics, String text, int x, int y, int color, float scale) {
        graphics.m_280168_().m_85836_();
        graphics.m_280168_().m_85841_(scale, scale, 1.0f);
        graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(text), Math.round((float)x / scale), Math.round((float)y / scale), color, false);
        graphics.m_280168_().m_85849_();
    }

    @Override
    protected void buildMenuRoot(GuiElement root) {
        this.root = root;
        this.dirtySections.clear();
        this.populateGui();
    }

    private void populateGui() {
        this.outpostRedeployButton = null;
        this.confirmDeployButton = null;
        this.classTitleText = null;
        this.statusText = null;
        this.statusTimerText = null;
        this.troopCountText = null;
        this.phaseTitleText = null;
        this.governanceTimerText = null;
        this.governanceVoteButtons.clear();
        this.governanceState = ClientGovernanceState.get();
        this.governanceReceivedAtMs = ClientGovernanceState.getReceivedAtMs();
        this.computeRegions();
        this.buildTitleBar();
        this.root.addChild(new GuiRect(this.leftX, this.leftY, this.leftW, this.leftH, -16777216));
        this.root.addChild(new GuiRect(this.centerX, this.centerY, this.centerW, this.centerH, -16777216));
        this.root.addChild(new GuiRect(this.mapX, this.mapY + Math.max(0, this.mapH - 20), this.mapW, 20, -16777216));
        this.squadSectionRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.classSectionRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.deploySectionRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.mapControlsRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.statusSectionRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.root.addChild(this.squadSectionRoot);
        this.buildSquadSection(this.squadSectionRoot);
        this.root.addChild(this.classSectionRoot);
        this.buildClassSection(this.classSectionRoot);
        this.root.addChild(this.deploySectionRoot);
        this.buildDeploySection(this.deploySectionRoot);
        this.root.addChild(this.mapControlsRoot);
        this.buildMapPanel(this.mapControlsRoot);
        this.root.addChild(this.statusSectionRoot);
        this.buildStatusBar(this.statusSectionRoot);
        this.fireteamContextRoot = new GuiElement(0, 0, this.f_96543_, this.f_96544_);
        this.fireteamContextRoot.setVisible(false);
        this.root.addChild(this.fireteamContextRoot);
    }

    private void invalidateSections(Section ... sections) {
        if (this.root == null) {
            return;
        }
        this.dirtySections.addAll(Arrays.asList(sections));
    }

    private void flushDirtySections() {
        if (this.dirtySections.isEmpty()) {
            return;
        }
        EnumSet<Section> pending = EnumSet.copyOf(this.dirtySections);
        this.dirtySections.clear();
        AuiScreen.runWithDocument(null, () -> {
            if (pending.contains((Object)Section.SQUAD)) {
                this.rebuildSquadSection();
            }
            if (pending.contains((Object)Section.CLASS)) {
                this.rebuildClassSection();
            }
            if (pending.contains((Object)Section.DEPLOY)) {
                this.rebuildDeploySection();
            }
            if (pending.contains((Object)Section.MAP_CONTROLS)) {
                this.rebuildMapControls();
            }
            if (pending.contains((Object)Section.STATUS)) {
                this.rebuildStatusSection();
            }
        });
    }

    private void rebuildSquadSection() {
        if (this.squadSectionRoot == null) {
            return;
        }
        double scrollOffset = this.squadScrollList == null ? 0.0 : this.squadScrollList.getOffset();
        this.squadSectionRoot.clearChildren();
        this.buildSquadSection(this.squadSectionRoot);
        if (this.squadScrollList != null) {
            this.squadScrollList.setOffset(scrollOffset);
        }
    }

    private void rebuildClassSection() {
        if (this.classSectionRoot == null) {
            return;
        }
        double scrollOffset = this.classScrollList == null ? 0.0 : this.classScrollList.getOffset();
        this.classSectionRoot.clearChildren();
        this.buildClassSection(this.classSectionRoot);
        if (this.classScrollList != null) {
            this.classScrollList.setOffset(scrollOffset);
        }
    }

    private void rebuildDeploySection() {
        if (this.deploySectionRoot == null) {
            return;
        }
        double scrollOffset = this.deployScrollList == null ? 0.0 : this.deployScrollList.getOffset();
        this.deploySectionRoot.clearChildren();
        this.buildDeploySection(this.deploySectionRoot);
        if (this.deployScrollList != null) {
            this.deployScrollList.setOffset(scrollOffset);
        }
        if (this.pendingDeployCommand != null && this.findDeployButton(this.pendingDeployPosition, this.pendingDeployCommand) == null) {
            this.clearPendingDeploySelection();
        } else {
            this.refreshConfirmDeployButton();
        }
    }

    private void rebuildMapControls() {
        if (this.mapControlsRoot == null) {
            return;
        }
        this.governanceTimerText = null;
        this.governanceVoteButtons.clear();
        this.mapControlsRoot.clearChildren();
        this.buildMapPanel(this.mapControlsRoot);
    }

    private void rebuildStatusSection() {
        if (this.statusSectionRoot == null) {
            return;
        }
        this.statusText = null;
        this.outpostRedeployButton = null;
        this.statusSectionRoot.clearChildren();
        this.buildStatusBar(this.statusSectionRoot);
    }

    private void computeRegions() {
        int usableH = this.f_96544_ - 35 - 13 - 6;
        this.leftX = 4;
        this.leftY = 37;
        this.leftW = Math.max(88, Math.min(190, (int)((float)this.f_96543_ * 0.22f)));
        this.leftH = usableH;
        this.centerX = this.leftX + this.leftW + 4;
        this.centerY = this.leftY;
        this.centerW = Math.max(132, Math.min(260, (int)((float)this.f_96543_ * 0.31f)));
        this.centerH = usableH;
        this.mapX = this.centerX + this.centerW + 4;
        this.mapY = this.leftY;
        this.mapW = this.f_96543_ - this.mapX - 4;
        this.mapH = usableH;
        this.squadAreaX = this.leftX + 3;
        this.squadAreaY = this.leftY + 3;
        this.squadAreaW = this.leftW - 6;
        this.squadAreaH = usableH - 6;
        int halfH = usableH / 2;
        this.classAreaX = this.centerX + 3;
        this.classAreaY = this.centerY + 3;
        this.classAreaW = this.centerW - 6;
        this.classAreaH = halfH - 6;
        this.deployAreaX = this.centerX + 3;
        this.deployAreaY = this.centerY + halfH + 2;
        this.deployAreaW = this.centerW - 6;
        this.deployAreaH = usableH - halfH - 6 - 4;
    }

    private void buildTitleBar() {
        String teamColor = EspetroAuiWidgets.teamPrefix(this.team);
        this.phaseTitleText = new PlainText(5, 3, "\u00a76\u00a7l\u90e8\u7f72\u9636\u6bb5 \u00a77| " + teamColor + "\u00a7l" + this.factionIcon + " " + this.factionName, 0xFFFFFF);
        this.root.addChild(this.phaseTitleText);
        String teamName = EspetroAuiWidgets.teamName(this.team);
        String sub = "\u00a7f" + teamName;
        if (this.factionDescription != null && !this.factionDescription.isEmpty()) {
            sub = sub + " \u00a7f\u00b7 " + this.factionDescription;
        }
        PlainText subtitle = new PlainText(5, 15, EspetroAuiWidgets.trimToWidth(sub, Math.max(80, this.f_96543_ - 16)), 0xFFFFFF);
        this.root.addChild(subtitle);
        PlainText hint = new PlainText(5, 25, EspetroAuiWidgets.trimToWidth("\u00a77\u9009\u62e9\u73ed\u7ec4\u3001\u804c\u4e1a\u4e0e\u90e8\u7f72\u70b9\uff0c\u786e\u8ba4\u540e\u7b49\u5f85\u90e8\u7f72", Math.max(80, this.f_96543_ - 16)), 0xFFFFFF);
        this.root.addChild(hint);
        this.statusTimerText = new PlainText(this.f_96543_ - 6, 4, "", -11654);
        this.root.addChild(this.statusTimerText);
        this.troopCountText = new PlainText(this.f_96543_ - 6, 16, "", -1);
        this.root.addChild(this.troopCountText);
        this.refreshTitleTimer();
        this.updateTroopLabel(TroopCountOverlay.getDisplayLine());
    }

    public void updateTroopLabel(String line) {
        if (this.troopCountText == null) {
            return;
        }
        String text = line == null ? "" : line;
        this.troopCountText.setText(text);
        if (text.isEmpty()) {
            this.troopCountText.setX(this.f_96543_ - 6);
            return;
        }
        int w = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(text) * 0.72f);
        this.troopCountText.setX(this.f_96543_ - w - 6);
        this.troopCountText.setY(16);
    }

    private int teamAccentColor() {
        return "ATTACK".equals(this.team) ? -2925744 : -11106873;
    }

    private void buildSquadSection(GuiElement sectionRoot) {
        int sx = this.squadAreaX;
        int sy = this.squadAreaY;
        int areaW = this.squadAreaW;
        int areaH = this.squadAreaH;
        sectionRoot.addChild(new PlainText(sx, sy, "\u00a76\u00a7l\u73ed\u7ec4", -14490));
        int manageH = 11;
        int listY = sy + 10 + 1;
        int listH = areaH - 10 - manageH - 5;
        this.squadScrollList = new ScrollableList(sx, listY, areaW, Math.max(11, listH)).setScrollStep(12).setAlwaysShowScrollbar(false);
        sectionRoot.addChild(this.squadScrollList);
        int rowW = areaW - 6;
        int rowY = 0;
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            String disclosure;
            boolean mine = squad.id == this.mySquadId;
            boolean unavailable = squad.isLocked || squad.memberCount >= squad.maxMembers;
            boolean expanded = this.expandedSquadIds.contains(squad.id);
            String string = disclosure = expanded ? "\u00a7f\u25bc" : "\u00a7f\u25b6";
            String state = mine ? "\u00a7a\u25cf" : (squad.isLocked ? "\u00a7c\u25a0" : "\u00a77\u25cb");
            String label = disclosure + " " + state + " \u00a7f" + squad.displayId + ". " + squad.name + " \u00a77" + squad.memberCount + "/" + squad.maxMembers;
            EspButton button = new EspButton(0, rowY, rowW, 11, label, () -> this.toggleSquadExpanded(squad.id));
            button.setTextScale(0.68f);
            button.setCenteredText(false);
            String category = squad.categoryDisplayName;
            if (!(category == null || category.isBlank() || "none".equalsIgnoreCase(squad.categoryId) || "\u65e0".equals(category))) {
                button.setRightLabel("\u00a7e" + category);
            }
            if (mine) {
                button.normalColor = -13350599;
                button.hoverColor = -12823230;
            }
            this.squadScrollList.addChild(button);
            rowY += 12;
            if (!expanded) continue;
            if (squad.members.isEmpty()) {
                this.squadScrollList.addChild(new SquadMemberRow(3, rowY, rowW - 3, "\u00a77\u6682\u65e0\u6210\u5458\u8d44\u6599", -10919842, -10919842, null, squad.id));
                rowY += 10;
            } else {
                byte lastFt = -1;
                for (UnifiedDeployScreenPacket.SquadMemberInfo member : squad.members) {
                    String role;
                    if (member.fireteam != lastFt) {
                        lastFt = member.fireteam;
                        Fireteam ft = Fireteam.fromIndex(member.fireteam);
                        this.squadScrollList.addChild(new PlainText(5, rowY + 1, "\u00a78\u706b\u529b\u7ec4 " + ft.label() + (member.fireteamLeader ? "" : ""), ft.color()));
                        rowY += 9;
                    }
                    String marker = member.commander ? "\u00a76\u25c6" : (member.leader ? "\u00a7d\u25c6" : (member.fireteamLeader ? "\u00a7b\u25b8" : "\u00a77\u00b7"));
                    String string2 = role = member.className.isBlank() ? "" : " \u00a78| \u00a7b" + member.className;
                    int roleAccent = member.commander ? -14490 : (member.leader ? -2847489 : -9984001);
                    Fireteam ft = Fireteam.fromIndex(member.fireteam);
                    this.squadScrollList.addChild(new SquadMemberRow(3, rowY, rowW - 3, marker + " \u00a7f" + member.playerName + role, ft.color(), roleAccent, member.uuid, squad.id));
                    rowY += 10;
                }
            }
            if (mine || unavailable) continue;
            EspButton join = new EspButton(3, rowY, rowW - 3, 10, "\u00a7a+ \u52a0\u5165\u73ed\u7ec4", () -> NetworkManager.joinSquad(squad.id));
            join.setTextScale(0.64f);
            join.normalColor = -14338773;
            join.hoverColor = -12823230;
            this.squadScrollList.addChild(join);
            rowY += 11;
        }
        if (this.squads.isEmpty()) {
            this.squadScrollList.addChild(new PlainText(2, 3, "\u00a77\u6682\u65e0\u73ed\u7ec4", -5525325));
        }
        EspButton manage = new EspButton(sx, sy + areaH - manageH, areaW, manageH, "\u00a7e\u7ba1\u7406\u73ed\u7ec4", this::openSquadManagement);
        manage.setTextScale(0.68f);
        sectionRoot.addChild(manage);
    }

    private void toggleSquadExpanded(int squadId) {
        if (!this.expandedSquadIds.add(squadId)) {
            this.expandedSquadIds.remove(squadId);
        }
        this.invalidateSections(Section.SQUAD);
    }

    private void openSquadManagement() {
        block2: {
            try {
                Minecraft.m_91087_().m_91152_(new SquadScreen(new ArrayList<UnifiedDeployScreenPacket.SquadInfo>(this.squads), this.mySquadId, this.team, new ArrayList<UnifiedDeployScreenPacket.SquadCategoryInfo>(this.squadCategories), this));
            }
            catch (Throwable t) {
                Espetro.LOGGER.error("\u6253\u5f00\u73ed\u7ec4\u7ba1\u7406\u754c\u9762\u5931\u8d25", t);
                LocalPlayer player = Minecraft.m_91087_().f_91074_;
                if (player == null) break block2;
                player.m_5661_(Component.m_237113_("\u00a7c\u65e0\u6cd5\u6253\u5f00\u73ed\u7ec4\u7ba1\u7406\u754c\u9762\uff0c\u8bf7\u67e5\u770b\u65e5\u5fd7\u3002"), false);
            }
        }
    }

    private void buildClassSection(GuiElement sectionRoot) {
        int sx = this.classAreaX;
        int sy = this.classAreaY;
        int areaW = this.classAreaW;
        int areaH = this.classAreaH;
        this.classTitleText = new PlainText(sx, sy, this.buildClassTitle(), -22016);
        sectionRoot.addChild(this.classTitleText);
        this.lastDisplayedClassSwitchCooldown = this.getClassSwitchCooldownRemaining();
        this.lastClassSelectionLocationAllowed = this.isClassSelectionLocationAllowed();
        int listY = sy + 10 + 1;
        int listH = areaH - 10 - 2;
        this.classScrollList = new ScrollableList(sx, listY, areaW, listH).setScrollStep(17).setAlwaysShowScrollbar(true);
        sectionRoot.addChild(this.classScrollList);
        this.classButtons.clear();
        this.classButtonToClassIndex.clear();
        int contentW = areaW - 6;
        int cols = Math.max(1, (contentW - 10) / 17);
        LinkedHashMap rows = new LinkedHashMap();
        for (int r = 1; r <= 5; ++r) {
            rows.put(r, new ArrayList());
        }
        ArrayList<UnifiedDeployScreenPacket.ClassInfo> otherClasses = new ArrayList<UnifiedDeployScreenPacket.ClassInfo>();
        for (UnifiedDeployScreenPacket.ClassInfo cls : this.classes) {
            if (cls.row >= 1 && cls.row <= 5) {
                ((List)rows.get(cls.row)).add(cls);
                continue;
            }
            otherClasses.add(cls);
        }
        int gy = 0;
        for (int r = 1; r <= 5; ++r) {
            List rowList = (List)rows.get(r);
            if (rowList.isEmpty()) continue;
            for (int i = 0; i < rowList.size(); ++i) {
                int col = i % cols;
                int bx = 10 + col * 17;
                int by = gy + i / cols * 17;
                this.addClassIconButton((UnifiedDeployScreenPacket.ClassInfo)rowList.get(i), rowList, bx, by, 15);
            }
            gy += (rowList.size() + cols - 1) / cols * 17 + 2;
        }
        for (UnifiedDeployScreenPacket.ClassInfo cls : otherClasses) {
            int count = this.classCounts.getOrDefault(cls.classId, cls.currentCount);
            boolean disabled = this.isClassButtonDisabled(cls);
            boolean emphasizeRed = this.isClassEmphasizeRed(cls, disabled);
            String label = "\u00a7f" + cls.name;
            String right = this.buildClassCountRightLabel(cls, emphasizeRed);
            EspButton btn = new EspButton(0, gy, contentW, 17, label, () -> this.selectClass(this.classes.indexOf(cls)));
            btn.setIcon(RoleIconResources.resolve(cls.iconImage, cls.icon), 128, 128);
            btn.setIconSize(15);
            btn.setRightLabel(right);
            btn.setCenteredText(false);
            btn.setEnabled(!disabled);
            btn.setDisabledAction(() -> this.selectClass(this.classes.indexOf(cls)));
            if (disabled) {
                boolean coolingDown = this.getClassSwitchCooldownRemaining() > 0;
                btn.setDisabledStyle(coolingDown ? -15197411 : -11919324, coolingDown ? 1614297160 : -7718334, coolingDown ? -8947849 : -25958);
            }
            this.classScrollList.addChild(btn);
            this.classButtons.add(btn);
            this.classButtonToClassIndex.put(btn, this.classes.indexOf(cls));
            gy += 19;
        }
    }

    private void addClassIconButton(UnifiedDeployScreenPacket.ClassInfo cls, List<UnifiedDeployScreenPacket.ClassInfo> rowList, int bx, int by, int size) {
        int clsIdx = this.classes.indexOf(cls);
        boolean disabled = this.isClassButtonDisabled(cls);
        boolean emphasizeRed = this.isClassEmphasizeRed(cls, disabled);
        EspButton btn = new EspButton(bx, by, size, size, "", () -> this.selectClass(clsIdx));
        btn.setIcon(RoleIconResources.resolve(cls.iconImage, cls.icon), 128, 128);
        btn.setIconSize(size - 2);
        btn.setCenteredText(false);
        btn.setEnabled(!disabled);
        btn.setDisabledAction(() -> this.selectClass(clsIdx));
        if (disabled) {
            boolean coolingDown = this.getClassSwitchCooldownRemaining() > 0;
            btn.setDisabledStyle(coolingDown ? -15197411 : -11919324, coolingDown ? 1614297160 : -7718334, coolingDown ? -8947849 : -25958);
        }
        this.classScrollList.addChild(btn);
        this.classButtons.add(btn);
        this.classButtonToClassIndex.put(btn, clsIdx);
    }

    private void buildDeploySection(GuiElement sectionRoot) {
        int sx = this.deployAreaX;
        int sy = this.deployAreaY;
        int areaW = this.deployAreaW;
        int areaH = this.deployAreaH;
        sectionRoot.addChild(new PlainText(sx, sy, this.buildDeployTitle(), -22016));
        int listY = sy + 10 + 2;
        int confirmH = 12;
        int listH = areaH - 10 - confirmH - 6;
        this.deployScrollList = new ScrollableList(sx, listY, areaW, listH).setScrollStep(13).setAlwaysShowScrollbar(true);
        sectionRoot.addChild(this.deployScrollList);
        this.deployButtons.clear();
        this.deployButtonPositions.clear();
        this.deployButtonCommands.clear();
        this.deployButtonBaseLabels.clear();
        this.deployButtonNextWaveAt.clear();
        this.deployButtonNameCores.clear();
        this.deployButtonWaveSeconds.clear();
        this.deployButtonHabAvailableAt.clear();
        this.deployButtonHabActivationSeconds.clear();
        int btnW = areaW - 6 - 4;
        int btnSpacing = 1;
        int row = 0;
        if (this.hasDeployPoint) {
            String deployLabel = "\u00a7e\u25c6 \u539f\u90e8\u7f72\u70b9 \u00a77(" + this.deployPointPos + ")";
            String deployCommand = "bastion deploy";
            EspButton btn = new EspButton(2, row * (12 + btnSpacing), btnW, 12, deployLabel, () -> this.selectDeploymentPoint(this.deployPointPos, deployCommand));
            btn.setEnabled(this.waitingForDeploySelection);
            this.registerDeployButton(btn, this.deployPointPos, deployCommand, deployLabel, 0L, deployLabel, 0);
            ++row;
        }
        for (UnifiedDeployScreenPacket.BastionItem b : this.bastions) {
            UUID bid = b.id;
            if (b.isOutpost()) {
                String deployCmd = "outpost deploy " + (b.getOutpostIndex() + 1);
                String deployLabel = "\u00a7d\u25c6 " + b.name + (String)(b.status.isBlank() ? "" : " \u00a77[" + b.status + "]");
                EspButton selectButton = new EspButton(2, row * (12 + btnSpacing), btnW, 12, deployLabel, () -> this.selectDeploymentPoint(b.pos, deployCmd));
                selectButton.setEnabled(this.waitingForDeploySelection);
                this.registerDeployButton(selectButton, b.pos, deployCmd, deployLabel, 0L, deployLabel, 0);
            } else {
                String habActivationPart;
                String cmd = "bastion select " + bid;
                String markerColor = b.isRally() ? "\u00a7a" : "\u00a79";
                String marker = b.isRally() ? "\u2691 " : "\u25a0 ";
                String nameCore = markerColor + marker + b.name;
                long waveAt = b.isRally() ? b.nextWaveAtEpochMs : 0L;
                String string = habActivationPart = b.isRally() ? "" : this.formatHabStatus(b.habAvailableAtEpochMs, b.habActivationTotalSeconds);
                String statusPart = b.isRally() ? this.formatWaveStatus(waveAt, b.waveSeconds) : (habActivationPart.isEmpty() ? (b.status.isBlank() ? "" : " \u00a77[" + b.status + "]") : habActivationPart);
                String deployLabel = nameCore + statusPart;
                boolean habReady = b.isRally() ? true : (b.habAvailableAtEpochMs > 0L ? b.habAvailableAtEpochMs <= System.currentTimeMillis() : "HAB \u53ef\u90e8\u7f72".equals(b.status));
                EspButton btn = new EspButton(2, row * (12 + btnSpacing), btnW, 12, deployLabel, () -> this.selectDeploymentPoint(b.pos, cmd));
                btn.setIcon(b.isRally() ? RALLY_ICON : HAB_ICON, b.isRally() ? 256 : 128, 128);
                btn.setEnabled(this.waitingForDeploySelection && habReady);
                if (this.waitingForDeploySelection && !habReady) {
                    String habCountdown;
                    String string2 = habCountdown = b.habAvailableAtEpochMs > 0L && !habReady ? "HAB \u542f\u7528\u4e2d " + Math.max(1L, (b.habAvailableAtEpochMs - System.currentTimeMillis() + 999L) / 1000L) + "s" : "";
                    String reason = !habCountdown.isEmpty() ? habCountdown : (b.status == null || b.status.isBlank() ? "\u8be5\u5175\u7ad9\u5f53\u524d\u4e0d\u53ef\u7528" : b.status);
                    btn.setDisabledAction(() -> EspetroTipNotifier.showDenial("\u65e0\u6cd5\u90e8\u7f72\u5230\u8be5\u5175\u7ad9", reason));
                } else {
                    btn.setDisabledAction(null);
                }
                this.registerDeployButton(btn, b.pos, cmd, deployLabel, waveAt, nameCore, b.waveSeconds);
                if (b.habAvailableAtEpochMs > 0L) {
                    this.deployButtonHabAvailableAt.put(btn, b.habAvailableAtEpochMs);
                    this.deployButtonHabActivationSeconds.put(btn, b.habActivationTotalSeconds);
                }
            }
            ++row;
        }
        this.confirmDeployButton = new EspButton(sx, sy + areaH - confirmH, areaW, confirmH, "\u00a77\u9009\u62e9\u90e8\u7f72\u70b9", this::confirmDeploymentPoint);
        this.confirmDeployButton.setEnabled(false);
        sectionRoot.addChild(this.confirmDeployButton);
    }

    private void registerDeployButton(EspButton button, String positionText, String command, String baseLabel, long nextWaveAtEpochMs, String nameCore, int waveSeconds) {
        this.deployScrollList.addChild(button);
        this.deployButtons.add(button);
        this.deployButtonPositions.put(button, positionText);
        this.deployButtonCommands.put(button, command);
        this.deployButtonBaseLabels.put(button, baseLabel);
        this.deployButtonNameCores.put(button, nameCore);
        if (nextWaveAtEpochMs > 0L) {
            this.deployButtonNextWaveAt.put(button, nextWaveAtEpochMs);
        } else {
            this.deployButtonNextWaveAt.remove(button);
        }
        if (waveSeconds > 0) {
            this.deployButtonWaveSeconds.put(button, waveSeconds);
        } else {
            this.deployButtonWaveSeconds.remove(button);
        }
        button.setLabel(this.buildDeployButtonLabel(this.buildLiveDeployBaseLabel(button, baseLabel), positionText, command));
    }

    private String formatWaveStatus(long nextWaveAtEpochMs, int waveSeconds) {
        if (nextWaveAtEpochMs <= 0L) {
            return "";
        }
        long remaining = Math.max(0L, (nextWaveAtEpochMs - System.currentTimeMillis() + 999L) / 1000L);
        if (remaining <= 0L) {
            return " \u00a77[\u5c31\u7eea]";
        }
        int total = waveSeconds > 0 ? waveSeconds : (int)remaining;
        return " \u00a77[\u51b7\u5374 " + remaining + "/" + total + "s]";
    }

    private String formatHabStatus(long habAvailableAtEpochMs, int totalSeconds) {
        if (habAvailableAtEpochMs <= 0L) {
            return "";
        }
        long remaining = Math.max(0L, (habAvailableAtEpochMs - System.currentTimeMillis() + 999L) / 1000L);
        if (remaining <= 0L) {
            return "";
        }
        return " \u00a77[\u542f\u7528\u4e2d " + remaining + "s]";
    }

    private String buildLiveDeployBaseLabel(EspButton button, String fallbackBase) {
        Long waveAt = this.deployButtonNextWaveAt.get(button);
        String nameCore = this.deployButtonNameCores.get(button);
        if (waveAt == null || waveAt <= 0L || nameCore == null) {
            return fallbackBase;
        }
        int total = this.deployButtonWaveSeconds.getOrDefault(button, 0);
        return nameCore + this.formatWaveStatus(waveAt, total);
    }

    private void refreshRallyWaveLabels() {
        for (EspButton button : this.deployButtons) {
            Long waveAt = this.deployButtonNextWaveAt.get(button);
            if (waveAt == null || waveAt <= 0L) continue;
            String base = this.buildLiveDeployBaseLabel(button, this.deployButtonBaseLabels.get(button));
            button.setLabel(this.buildDeployButtonLabel(base, this.deployButtonPositions.get(button), this.deployButtonCommands.get(button)));
            this.deployButtonBaseLabels.put(button, base);
        }
    }

    private void refreshHabActivationLabels() {
        long now = System.currentTimeMillis();
        for (Map.Entry<EspButton, Long> entry : this.deployButtonHabAvailableAt.entrySet()) {
            EspButton button = entry.getKey();
            long habAt = entry.getValue();
            int total = this.deployButtonHabActivationSeconds.getOrDefault(button, 0);
            String nameCore = this.deployButtonNameCores.get(button);
            if (nameCore == null) continue;
            String habPart = this.formatHabStatus(habAt, total);
            Object base = nameCore + (habPart.isEmpty() ? "" : habPart);
            if (habAt > 0L && habAt <= now) {
                base = nameCore;
            }
            button.setLabel(this.buildDeployButtonLabel((String)base, this.deployButtonPositions.get(button), this.deployButtonCommands.get(button)));
            this.deployButtonBaseLabels.put(button, (String)base);
            if (habAt <= 0L || habAt > now || !this.waitingForDeploySelection) continue;
            button.setEnabled(true);
        }
    }

    private int getRedeployCooldownRemaining() {
        long remainingMillis = this.outpostRedeployCooldownEndsAt - System.currentTimeMillis();
        return remainingMillis <= 0L ? 0 : (int)((remainingMillis + 999L) / 1000L);
    }

    private void selectDeploymentPoint(String positionText, String command) {
        if (!this.waitingForDeploySelection) {
            return;
        }
        if (!this.hasLocalPlayerSelectedClass()) {
            EspetroTipNotifier.showDenial("\u672a\u9009\u62e9\u804c\u4e1a", "\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\uff0c\u518d\u9009\u62e9\u90e8\u7f72\u70b9\u3002");
            return;
        }
        this.pendingDeployPosition = positionText;
        this.pendingDeployCommand = command;
        this.updateSelectedDeploymentPoint(positionText);
        this.refreshDeployButtonLabels();
        this.refreshConfirmDeployButton();
    }

    private void confirmDeploymentPoint() {
        if (!this.waitingForDeploySelection || this.pendingDeployCommand == null) {
            return;
        }
        if (!this.hasLocalPlayerSelectedClass()) {
            EspetroTipNotifier.showDenial("\u672a\u9009\u62e9\u804c\u4e1a", "\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\uff0c\u518d\u786e\u8ba4\u90e8\u7f72\u3002");
            return;
        }
        double[] coordinates = UnifiedDeployScreen.parseDeploymentCoordinates(this.pendingDeployPosition);
        if (coordinates != null) {
            HcrTacticalMapBridge.setSelectedDeploymentPoint(coordinates[0], coordinates[1]);
        } else {
            HcrTacticalMapBridge.clearSelectedDeploymentPoint();
        }
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (player != null) {
            player.f_108617_.m_246623_(this.pendingDeployCommand);
            boolean rallyQueued = this.isPendingRallyOnCooldown();
            if (!rallyQueued) {
                this.waitingForDeploySelection = false;
                this.clearPendingDeploySelection();
                this.refreshDeployButtonStates();
                Minecraft.m_91087_().m_91152_(null);
            } else {
                this.refreshConfirmDeployButton();
            }
        }
    }

    private EspButton findDeployButton(String positionText, String command) {
        for (EspButton button : this.deployButtons) {
            if (!Objects.equals(this.deployButtonPositions.get(button), positionText) || !Objects.equals(this.deployButtonCommands.get(button), command)) continue;
            return button;
        }
        return null;
    }

    private boolean isPendingRallyOnCooldown() {
        if (this.pendingDeployCommand == null || !this.pendingDeployCommand.startsWith("bastion select ")) {
            return false;
        }
        EspButton button = this.findDeployButton(this.pendingDeployPosition, this.pendingDeployCommand);
        if (button == null) {
            return false;
        }
        Long waveAt = this.deployButtonNextWaveAt.get(button);
        if (waveAt == null || waveAt <= 0L) {
            return false;
        }
        return waveAt > System.currentTimeMillis();
    }

    private void refreshConfirmDeployButton() {
        int total;
        if (this.confirmDeployButton == null) {
            return;
        }
        if (!this.waitingForDeploySelection || this.pendingDeployCommand == null) {
            this.confirmDeployButton.setLabel("\u00a77\u9009\u62e9\u90e8\u7f72\u70b9");
            this.confirmDeployButton.setEnabled(false);
            return;
        }
        EspButton button = this.findDeployButton(this.pendingDeployPosition, this.pendingDeployCommand);
        Long waveAt = button == null ? null : this.deployButtonNextWaveAt.get(button);
        int n = total = button == null ? 0 : this.deployButtonWaveSeconds.getOrDefault(button, 0);
        if (waveAt != null && waveAt > System.currentTimeMillis()) {
            long remaining = Math.max(1L, (waveAt - System.currentTimeMillis() + 999L) / 1000L);
            int m = total > 0 ? total : (int)remaining;
            this.confirmDeployButton.setLabel("\u00a7e\u5f53\u524d\u961f\u5305\u90e8\u7f72\u51b7\u5374\u65f6\u95f4[" + remaining + "/" + m + "]");
            this.confirmDeployButton.setEnabled(true);
            return;
        }
        this.confirmDeployButton.setLabel("\u00a7a\u90e8\u7f72");
        this.confirmDeployButton.setEnabled(true);
    }

    private void updateSelectedDeploymentPoint(String positionText) {
        double[] coordinates = UnifiedDeployScreen.parseDeploymentCoordinates(positionText);
        if (coordinates != null) {
            HcrTacticalMapBridge.setSelectedDeploymentPoint(coordinates[0], coordinates[1]);
        } else {
            HcrTacticalMapBridge.clearSelectedDeploymentPoint();
        }
    }

    private boolean isPendingDeploySelection(String positionText, String command) {
        return Objects.equals(this.pendingDeployPosition, positionText) && Objects.equals(this.pendingDeployCommand, command);
    }

    private void clearPendingDeploySelection() {
        this.pendingDeployPosition = null;
        this.pendingDeployCommand = null;
        HcrTacticalMapBridge.clearSelectedDeploymentPoint();
        if (this.confirmDeployButton != null) {
            this.confirmDeployButton.setLabel("\u00a77\u9009\u62e9\u90e8\u7f72\u70b9");
            this.confirmDeployButton.setEnabled(false);
        }
    }

    private static double[] parseDeploymentCoordinates(String positionText) {
        double[] dArray;
        if (positionText == null || positionText.isBlank()) {
            return null;
        }
        Matcher matcher = COORDINATE_PATTERN.matcher(positionText);
        double[] values = new double[3];
        int count = 0;
        while (matcher.find() && count < values.length) {
            try {
                values[count++] = Double.parseDouble(matcher.group());
            }
            catch (NumberFormatException ignored) {
                return null;
            }
        }
        if (count >= 3) {
            double[] dArray2 = new double[2];
            dArray2[0] = values[0];
            dArray = dArray2;
            dArray2[1] = values[2];
        } else {
            dArray = null;
        }
        return dArray;
    }

    private String buildRedeployLabel() {
        int remaining = this.getRedeployCooldownRemaining();
        return remaining > 0 ? "\u00a77\u91cd\u65b0\u90e8\u7f72 " + remaining + "s" : "\u00a7c\u91cd\u65b0\u90e8\u7f72";
    }

    @Override
    public void m_86600_() {
        super.m_86600_();
        this.flushDirtySections();
        if (!this.onceEverySecond()) {
            return;
        }
        this.tickLocalDeployTimer();
        if (this.outpostRedeployButton != null) {
            this.outpostRedeployButton.setLabel(this.buildRedeployLabel());
            this.outpostRedeployButton.setEnabled(!this.waitingForDeploySelection && this.getRedeployCooldownRemaining() == 0);
        }
        this.refreshRallyWaveLabels();
        this.refreshHabActivationLabels();
        this.refreshConfirmDeployButton();
        this.refreshClassSwitchCooldown();
        this.refreshClassSelectionLocation();
        GovernanceStatePacket.TeamState governance = this.activeGovernance();
        if (this.governanceTimerText != null && governance != null) {
            this.governanceTimerText.setText("\u00a7e\u5269\u4f59 " + ClientGovernanceState.secondsLeft(governance) + "s");
        }
    }

    private void tickLocalDeployTimer() {
        int elapsed;
        int display;
        boolean changed = false;
        if (this.deployTimerAnchorSeconds >= 0 && (display = Math.max(0, this.deployTimerAnchorSeconds - (elapsed = (int)((System.currentTimeMillis() - this.deployTimerAnchorMs) / 1000L)))) != this.deployTimeRemaining) {
            this.deployTimeRemaining = display;
            changed = true;
        }
        if (changed || ClientGameState.getCurrentPhase() == GamePhase.BATTLE) {
            this.refreshTitleTimer();
        }
    }

    @Override
    public void m_7861_() {
        this.clearPendingDeploySelection();
        this.previewRenderer.clear();
        super.m_7861_();
    }

    private void buildMapPanel(GuiElement sectionRoot) {
        block15: {
            int rowY;
            GovernanceStatePacket.TeamState state;
            block16: {
                block14: {
                    boolean battle;
                    this.governanceVoteButtons.clear();
                    int bx = this.mapX + 5;
                    int by = this.mapY + this.mapH - 12 - 4;
                    EspButton score = new EspButton(bx, by, 64, 12, "\u00a76\u73a9\u5bb6\u5206\u6570\u677f", () -> Minecraft.m_91087_().m_91152_(new MatchScoreboardScreen(this)));
                    score.setTextScale(0.72f);
                    sectionRoot.addChild(score);
                    state = this.activeGovernance();
                    boolean bl = battle = ClientGameState.getCurrentPhase() == GamePhase.BATTLE;
                    if (battle && (state == null || "IDLE".equals(state.state))) {
                        EspButton impeach = new EspButton(bx + 68, by, 56, 12, "\u00a7c\u53d1\u8d77\u5f39\u52be", () -> NetworkManager.sendGovernanceAction(GovernanceActionPacket.Action.START_IMPEACHMENT, null));
                        impeach.setTextScale(0.72f);
                        sectionRoot.addChild(impeach);
                        EspButton vehicleInfo = new EspButton(bx + 128, by, 64, 12, "\u00a7b\u8f7d\u5177\u4fe1\u606f", () -> NetworkManager.requestVehicleInfo());
                        vehicleInfo.setTextScale(0.72f);
                        sectionRoot.addChild(vehicleInfo);
                        return;
                    }
                    if (state == null || "IDLE".equals(state.state)) {
                        return;
                    }
                    int governanceH = Math.max(20, this.mapH - 20 - 6);
                    sectionRoot.addChild(new GuiRect(this.mapX + 3, this.mapY + 3, this.mapW - 6, governanceH, -535291880));
                    String stateTitle = switch (state.state) {
                        case "IMPEACHMENT_VOTE" -> "\u5f39\u52be\u6295\u7968";
                        case "VACANCY_VOLUNTEER" -> "\u6307\u6325\u5b98\u7a7a\u7f3a";
                        case "VACANCY_VOTE" -> "\u7a7a\u7f3a\u516c\u6295";
                        default -> state.state;
                    };
                    sectionRoot.addChild(new PlainText(this.mapX + 10, this.mapY + 30, "\u00a76\u00a7l\u6307\u6325\u5b98\u6cbb\u7406\uff1a" + stateTitle, -14490));
                    this.governanceTimerText = new PlainText(this.mapX + 10, this.mapY + 43, "\u00a7e\u5269\u4f59 " + ClientGovernanceState.secondsLeft(state) + "s", -11654);
                    sectionRoot.addChild(this.governanceTimerText);
                    rowY = this.mapY + 60;
                    if (!"IMPEACHMENT_VOTE".equals(state.state)) break block14;
                    this.addGovernanceVoteButton(sectionRoot, state, state.commander, "\u539f\u6307\u6325\u5b98", rowY, GovernanceActionPacket.Action.VOTE_IMPEACHMENT);
                    this.addGovernanceVoteButton(sectionRoot, state, state.challenger, "\u6311\u6218\u8005", rowY + 18, GovernanceActionPacket.Action.VOTE_IMPEACHMENT);
                    break block15;
                }
                if (!"VACANCY_VOLUNTEER".equals(state.state)) break block16;
                EspButton volunteer = new EspButton(this.mapX + 10, rowY, Math.max(80, this.mapW - 20), 12, "\u00a7a\u5fd7\u613f\u8865\u4f4d", () -> NetworkManager.sendGovernanceAction(GovernanceActionPacket.Action.VOLUNTEER_VACANCY, null));
                volunteer.setTextScale(0.72f);
                sectionRoot.addChild(volunteer);
                if (state.volunteers.isEmpty()) break block15;
                String names = state.volunteers.stream().map(MatchScoreboardScreen::nameFor).reduce((a, b) -> a + ", " + b).orElse("");
                sectionRoot.addChild(new PlainText(this.mapX + 10, rowY + 18, "\u00a77\u5df2\u5fd7\u613f: " + names, -5197648));
                break block15;
            }
            if ("VACANCY_VOTE".equals(state.state)) {
                int y = rowY;
                for (UUID volunteer : state.volunteers) {
                    this.addGovernanceVoteButton(sectionRoot, state, volunteer, "\u5fd7\u613f\u8005", y, GovernanceActionPacket.Action.VOTE_VACANCY);
                    if ((y += 18) <= this.mapY + this.mapH - 20 - 18) continue;
                    break;
                }
            }
        }
    }

    private void addGovernanceVoteButton(GuiElement sectionRoot, GovernanceStatePacket.TeamState state, UUID candidate, String prefix, int y, GovernanceActionPacket.Action action) {
        if (candidate == null) {
            return;
        }
        EspButton button = new EspButton(this.mapX + 10, y, Math.max(80, this.mapW - 20), 12, UnifiedDeployScreen.buildVoteButtonLabel(state, candidate, prefix), () -> NetworkManager.sendGovernanceAction(action, candidate));
        button.setTextScale(0.72f);
        sectionRoot.addChild(button);
        this.governanceVoteButtons.put(candidate.toString(), button);
    }

    private GovernanceStatePacket.TeamState activeGovernance() {
        GovernanceStatePacket.TeamState cached = ClientGovernanceState.forTeam(this.team);
        if (cached != null) {
            return cached;
        }
        for (GovernanceStatePacket.TeamState state : this.governanceState.teams) {
            if (!this.team.equals(state.team)) continue;
            return state;
        }
        return null;
    }

    private void buildStatusBar(GuiElement sectionRoot) {
        int barY = this.f_96544_ - 13;
        sectionRoot.addChild(new GuiRect(0, barY, this.f_96543_, 13, -16777216));
        this.statusText = new PlainText(5, barY + 3, this.buildStatusText(), 0xFFFFFF);
        sectionRoot.addChild(this.statusText);
        boolean hasOutpost = this.bastions.stream().anyMatch(UnifiedDeployScreenPacket.BastionItem::isOutpost);
        if (this.deployTimeRemaining >= 0 && "DEFEND".equals(this.team) && hasOutpost) {
            int redeployW = 66;
            this.outpostRedeployButton = new EspButton(this.f_96543_ - redeployW - 4, barY + 1, redeployW, 11, this.buildRedeployLabel(), () -> {
                LocalPlayer p = Minecraft.m_91087_().f_91074_;
                if (p != null) {
                    p.f_108617_.m_246623_("outpost redeploy");
                }
            });
            this.outpostRedeployButton.setEnabled(!this.waitingForDeploySelection && this.getRedeployCooldownRemaining() == 0);
            sectionRoot.addChild(this.outpostRedeployButton);
        }
    }

    private String buildDeployTitle() {
        return "\u00a76\u00a7l\u90e8\u7f72\u70b9";
    }

    private String formatTime(int seconds) {
        int safeSeconds = Math.max(0, seconds);
        return safeSeconds > 60 ? safeSeconds / 60 + ":" + String.format("%02d", safeSeconds % 60) : safeSeconds + "s";
    }

    private void refreshTitleTimer() {
        if (this.statusTimerText == null) {
            return;
        }
        String teamColor = EspetroAuiWidgets.teamPrefix(this.team);
        GamePhase phase = ClientGameState.getCurrentPhase();
        String phaseName = switch (phase) {
            default -> throw new IncompatibleClassChangeError();
            case GamePhase.WAITING_FOR_PLAYERS -> "\u00a76\u00a7l\u7b49\u5f85";
            case GamePhase.LOBBY -> "\u00a76\u00a7l\u4e3b\u57ce\u7b49\u5f85";
            case GamePhase.MAP_VOTE -> "\u00a76\u00a7l\u5730\u56fe\u6295\u7968";
            case GamePhase.MAP_LOADING -> "\u00a76\u00a7l\u5730\u56fe\u52a0\u8f7d";
            case GamePhase.TEAM_SELECT -> "\u00a76\u00a7l\u9009\u8fb9";
            case GamePhase.ATTACK_COMMANDER_VOTE, GamePhase.DEFEND_COMMANDER_VOTE -> "\u00a76\u00a7l\u6307\u6325\u5b98\u6295\u7968";
            case GamePhase.ATTACK_FACTION_SELECT, GamePhase.DEFEND_FACTION_SELECT -> "\u00a76\u00a7l\u7f16\u5236\u9009\u62e9";
            case GamePhase.FACTION_REVEAL -> "\u00a76\u00a7l\u7f16\u5236\u63ed\u793a";
            case GamePhase.DEPLOYING -> "\u00a76\u00a7l\u90e8\u7f72\u9636\u6bb5";
            case GamePhase.BATTLE -> "\u00a76\u00a7l\u6218\u6597\u9636\u6bb5";
            case GamePhase.ROUND_END -> "\u00a76\u00a7l\u7ed3\u7b97";
            case GamePhase.CLEANUP -> "\u00a76\u00a7l\u6e05\u7406";
        };
        String objectiveMode = ClientGameState.getObjectiveMode();
        String modePart = objectiveMode != null && (phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE) ? " \u00a78\u00b7 \u00a7e" + objectiveMode : "";
        String factionPart = "\u00a77| " + teamColor + "\u00a7l" + this.factionIcon + " " + this.factionName;
        String title = EspetroAuiWidgets.trimToWidth(phaseName + modePart + " " + factionPart, Math.max(80, this.f_96543_ - 96));
        this.phaseTitleText.setText(title);
        int battleRemaining = ClientGameState.getBattleTimeRemaining();
        String timer = phase == GamePhase.BATTLE && battleRemaining >= 0 ? this.formatTime(battleRemaining) : (this.deployTimeRemaining >= 0 ? this.formatTime(this.deployTimeRemaining) : "");
        this.statusTimerText.setText(timer);
        int timerWidth = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(timer) * 0.72f);
        this.statusTimerText.setX(this.f_96543_ - timerWidth - 6);
        if (this.troopCountText != null) {
            this.updateTroopLabel(TroopCountOverlay.getDisplayLine());
        }
    }

    private String buildStatusText() {
        String teamColor = EspetroAuiWidgets.teamPrefix(this.team);
        String teamName = EspetroAuiWidgets.teamName(this.team);
        Object squadStr = "";
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            if (squad.id != this.mySquadId) continue;
            squadStr = " | \u00a7a" + squad.name;
            break;
        }
        return teamColor + teamName + (String)squadStr + " \u00a7f| " + this.factionName;
    }

    private boolean hasLocalPlayerSelectedClass() {
        return this.selectedClassId != null;
    }

    private void refreshDeployButtonStates() {
        if (!this.waitingForDeploySelection) {
            this.clearPendingDeploySelection();
        }
        boolean hasClass = this.hasLocalPlayerSelectedClass();
        for (EspButton button : this.deployButtons) {
            String command = this.deployButtonCommands.get(button);
            boolean ready = this.isDeployButtonSelectable(command);
            button.setEnabled(this.waitingForDeploySelection && ready && hasClass);
            if (this.waitingForDeploySelection && ready && !hasClass) {
                button.setDisabledAction(() -> EspetroTipNotifier.showDenial("\u672a\u9009\u62e9\u804c\u4e1a", "\u8bf7\u5148\u9009\u62e9\u804c\u4e1a\uff0c\u518d\u9009\u62e9\u90e8\u7f72\u70b9\u3002"));
                continue;
            }
            button.setDisabledAction(null);
        }
        this.refreshDeployButtonLabels();
        if (this.outpostRedeployButton != null) {
            this.outpostRedeployButton.setLabel(this.buildRedeployLabel());
            this.outpostRedeployButton.setEnabled(!this.waitingForDeploySelection && this.getRedeployCooldownRemaining() == 0);
        }
    }

    private boolean isDeployButtonSelectable(String command) {
        if (command == null || !command.startsWith("bastion select ")) {
            return true;
        }
        String idText = command.substring("bastion select ".length()).trim();
        try {
            UUID id = UUID.fromString(idText);
            for (UnifiedDeployScreenPacket.BastionItem item : this.bastions) {
                if (item == null || item.id == null || !item.id.equals(id)) continue;
                if (item.isRally()) {
                    return true;
                }
                if (item.habAvailableAtEpochMs > 0L) {
                    return item.habAvailableAtEpochMs <= System.currentTimeMillis();
                }
                return "HAB \u53ef\u90e8\u7f72".equals(item.status);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        return true;
    }

    private void refreshDeployButtonLabels() {
        for (EspButton button : this.deployButtons) {
            String baseLabel = this.deployButtonBaseLabels.get(button);
            if (baseLabel == null) continue;
            String liveBase = this.buildLiveDeployBaseLabel(button, baseLabel);
            if (this.deployButtonNextWaveAt.containsKey(button)) {
                this.deployButtonBaseLabels.put(button, liveBase);
            }
            button.setLabel(this.buildDeployButtonLabel(liveBase, this.deployButtonPositions.get(button), this.deployButtonCommands.get(button)));
        }
    }

    private String buildDeployButtonLabel(String baseLabel, String positionText, String command) {
        if (!this.isPendingDeploySelection(positionText, command)) {
            return baseLabel;
        }
        String suffix = " \u00a7a\u25b6";
        int coordinateStart = baseLabel.indexOf(" \u00a77(");
        if (coordinateStart >= 0) {
            return baseLabel.substring(0, coordinateStart) + suffix + baseLabel.substring(coordinateStart);
        }
        return baseLabel + suffix;
    }

    @Override
    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        CurrentMapBackgroundRenderer.render(graphics, this.f_96543_, this.f_96544_, ClientGameState.getCurrentMapFolder());
        this.computeRegions();
        graphics.m_280509_(this.leftX, this.leftY, this.leftX + this.leftW, this.leftY + this.leftH, -16777216);
        graphics.m_280509_(this.centerX, this.centerY, this.centerX + this.centerW, this.centerY + this.centerH, -16777216);
        UnifiedDeployScreenPacket.LoadoutPreview activePreview = this.getActivePreview();
        if (activePreview != null) {
            this.renderClassPreview(graphics, activePreview, mouseX, mouseY);
        } else if (this.activeGovernance() == null || "IDLE".equals(this.activeGovernance().state)) {
            this.renderTacticalMap(graphics, partialTick);
        } else {
            int viewportH = Math.max(1, this.mapH - 20);
            graphics.m_280509_(this.mapX, this.mapY, this.mapX + this.mapW, this.mapY + viewportH, -16777216);
        }
        UnifiedDeployScreen.resetGuiRenderState(graphics);
        graphics.m_280509_(this.leftX, this.leftY, this.leftX + this.leftW, this.leftY + this.leftH, -16777216);
        graphics.m_280509_(this.centerX, this.centerY, this.centerX + this.centerW, this.centerY + this.centerH, -16777216);
        graphics.m_280509_(0, 0, this.f_96543_, 35, -16777216);
        graphics.m_280509_(0, 0, 3, 35, this.teamAccentColor());
        int barY = this.f_96544_ - 13;
        if (barY > 35) {
            graphics.m_280509_(0, barY, this.f_96543_, this.f_96544_, -16777216);
        }
        this.renderStaticDividers(graphics);
    }

    private void renderStaticDividers(GuiGraphics graphics) {
        graphics.m_280509_(4, 35, Math.max(4, this.f_96543_ - 4), 36, -12959939);
        int dividerBottom = Math.max(37, this.f_96544_ - 13 - 2);
        graphics.m_280509_(this.leftX + this.leftW, 37, this.leftX + this.leftW + 1, dividerBottom, -12959939);
        graphics.m_280509_(this.centerX + this.centerW, 37, this.centerX + this.centerW + 1, dividerBottom, -12959939);
    }

    private static void resetGuiRenderState(GuiGraphics graphics) {
        graphics.m_280262_();
        graphics.m_280246_(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();
    }

    @Override
    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.computeRegions();
        this.updatePreviewTarget(mouseX, mouseY);
        if (this.activeGovernance() != null && !"IDLE".equals(this.activeGovernance().state)) {
            graphics.m_280637_(this.mapX, this.mapY, this.mapW, this.mapH, -2141494688);
        }
        this.renderClassTooltip(graphics, mouseX, mouseY);
        this.renderVariantPopup(graphics, mouseX, mouseY);
    }

    private void renderTacticalMap(GuiGraphics graphics, float partialTick) {
        int viewportH = this.mapViewportHeight();
        this.renderMapViewportBackground(graphics, viewportH);
        graphics.m_280262_();
        HcrTacticalMapBridge.renderEmbeddedMap(graphics, this.mapX, this.mapY, this.mapW, viewportH, partialTick);
        UnifiedDeployScreen.resetGuiRenderState(graphics);
        this.renderMapViewportBorder(graphics, viewportH);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderClassPreview(GuiGraphics graphics, UnifiedDeployScreenPacket.LoadoutPreview preview, int mouseX, int mouseY) {
        this.previewRenderer.update(preview);
        if (!this.previewRenderer.isReady()) {
            this.renderTacticalMap(graphics, 0.0f);
            return;
        }
        int viewportH = this.mapViewportHeight();
        this.renderMapViewportBackground(graphics, viewportH);
        int centerX = this.mapX + this.mapW / 2;
        int centerY = this.mapY + viewportH * 11 / 16;
        int scale = Math.max(40, Math.min(this.mapW, viewportH) / 3);
        float mouseDeltaX = centerX - mouseX;
        float mouseDeltaY = centerY - mouseY;
        graphics.m_280588_(this.mapX, this.mapY, this.mapX + this.mapW, this.mapY + viewportH);
        try {
            this.previewRenderer.render(graphics, centerX, centerY, scale, mouseDeltaX, mouseDeltaY);
        }
        finally {
            graphics.m_280618_();
        }
        UnifiedDeployScreen.resetGuiRenderState(graphics);
        this.renderMapViewportBorder(graphics, viewportH);
    }

    private int mapViewportHeight() {
        return Math.max(1, this.mapH - 20);
    }

    private void renderMapViewportBackground(GuiGraphics graphics, int viewportH) {
        graphics.m_280509_(this.mapX, this.mapY, this.mapX + this.mapW, this.mapY + viewportH, -16777216);
    }

    private void renderMapViewportBorder(GuiGraphics graphics, int viewportH) {
        graphics.m_280637_(this.mapX, this.mapY, this.mapW, viewportH, -10788256);
    }

    private void updatePreviewTarget(int mouseX, int mouseY) {
        String newVariantId;
        int newClassIndex;
        block6: {
            block5: {
                int variantIndex;
                newClassIndex = -1;
                newVariantId = null;
                if (!this.hasVariantPopup()) break block5;
                UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(this.variantPopupClassIndex);
                int row = this.computeVariantPopupHoveredRow(mouseX, mouseY);
                if (row >= 0 && (variantIndex = this.variantPopupScroll + row) < cls.variants.size()) {
                    newClassIndex = this.variantPopupClassIndex;
                    newVariantId = cls.variants.get((int)variantIndex).variantId;
                }
                if (newClassIndex >= 0) break block6;
                newClassIndex = this.variantPopupClassIndex;
                newVariantId = this.resolveDefaultVariantId(cls);
                break block6;
            }
            for (EspButton btn : this.classButtons) {
                if (!btn.hovered) continue;
                Integer idx = this.classButtonToClassIndex.get(btn);
                if (idx == null || idx < 0 || idx >= this.classes.size()) break;
                newClassIndex = idx;
                newVariantId = this.resolveDefaultVariantId(this.classes.get(idx));
                break;
            }
        }
        if (newClassIndex >= 0) {
            this.activePreviewClassIndex = newClassIndex;
            this.activePreviewVariantId = newVariantId;
            this.previewGraceDeadlineMs = 0L;
        } else if (this.activePreviewClassIndex >= 0 && this.previewGraceDeadlineMs == 0L) {
            this.previewGraceDeadlineMs = System.currentTimeMillis() + 350L;
        }
    }

    private int computeVariantPopupHoveredRow(int mouseX, int mouseY) {
        if (!UnifiedDeployScreen.inside(mouseX, mouseY, this.variantPopupX, this.variantPopupY, 180, this.variantPopupH)) {
            return -1;
        }
        int closeX = this.variantPopupX + 180 - 16;
        int closeY = this.variantPopupY + 2;
        if (UnifiedDeployScreen.inside(mouseX, mouseY, closeX, closeY, 13, 13)) {
            return -1;
        }
        if (mouseY < this.variantPopupY + 18) {
            return -1;
        }
        int row = (mouseY - (this.variantPopupY + 18)) / 28;
        int visible = Math.min(6, this.classes.get((int)this.variantPopupClassIndex).variants.size());
        if (row < 0 || row >= visible) {
            return -1;
        }
        return row;
    }

    private String resolveDefaultVariantId(UnifiedDeployScreenPacket.ClassInfo cls) {
        if (cls.variants.isEmpty()) {
            return null;
        }
        for (UnifiedDeployScreenPacket.VariantInfo v : cls.variants) {
            if (!"default".equals(v.variantId)) continue;
            return v.variantId;
        }
        return cls.variants.get((int)0).variantId;
    }

    private UnifiedDeployScreenPacket.LoadoutPreview getActivePreview() {
        if (this.activePreviewClassIndex < 0 || this.activePreviewClassIndex >= this.classes.size()) {
            return null;
        }
        if (this.previewGraceDeadlineMs != 0L && System.currentTimeMillis() >= this.previewGraceDeadlineMs) {
            this.activePreviewClassIndex = -1;
            this.activePreviewVariantId = null;
            this.previewGraceDeadlineMs = 0L;
            return null;
        }
        UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(this.activePreviewClassIndex);
        if (cls.variants.isEmpty()) {
            return UnifiedDeployScreenPacket.LoadoutPreview.empty();
        }
        for (UnifiedDeployScreenPacket.VariantInfo v : cls.variants) {
            if (!Objects.equals(v.variantId, this.activePreviewVariantId)) continue;
            return v.preview;
        }
        String fallbackId = this.resolveDefaultVariantId(cls);
        for (UnifiedDeployScreenPacket.VariantInfo v : cls.variants) {
            if (!Objects.equals(v.variantId, fallbackId)) continue;
            return v.preview;
        }
        return cls.variants.get((int)0).preview;
    }

    private void renderClassTooltip(GuiGraphics graphics, int mx, int my) {
        if (this.variantPopupClassIndex >= 0) {
            return;
        }
        for (EspButton btn : this.classButtons) {
            String denial;
            Integer clsIdx;
            if (!btn.hovered || (clsIdx = this.classButtonToClassIndex.get(btn)) == null || clsIdx < 0 || clsIdx >= this.classes.size()) continue;
            UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(clsIdx);
            boolean enabled = btn.isEnabled();
            ArrayList<Object> lines = new ArrayList<Object>();
            lines.add((enabled ? "\u00a76\u00a7l" : "\u00a7c") + cls.name);
            if (enabled) {
                if (cls.role != null && !cls.role.isBlank()) {
                    lines.add("\u00a7f" + cls.role);
                }
                if (cls.description != null && !cls.description.isBlank()) {
                    lines.add("\u00a77" + cls.description);
                }
                Object bonuses = "";
                if (cls.healthBonus != 0) {
                    bonuses = (String)bonuses + "\u00a7c\u2764 +" + cls.healthBonus;
                }
                if (cls.speedBonus != 0.0f) {
                    bonuses = (String)bonuses + (((String)bonuses).isEmpty() ? "" : "  ") + "\u00a7b\u26a1 +" + String.format("%.1f", Float.valueOf(cls.speedBonus));
                }
                if (!((String)bonuses).isEmpty()) {
                    lines.add(bonuses);
                }
            }
            int c = this.classCounts.getOrDefault(cls.classId, cls.currentCount);
            if (!this.inSquad()) {
                if (cls.teamCount) {
                    lines.add("\u00a7c\u9700\u5148\u52a0\u5165\u73ed\u7ec4\u5c0f\u961f");
                } else {
                    lines.add((c >= cls.maxPlayers ? "\u00a7c" : "\u00a7a") + c + "/" + cls.maxPlayers + " \u00a77\u00b7\u5175\u529b" + cls.troopValue);
                }
                lines.add("\u00a77\u5165\u961f\u540e\u53ef\u9009\u804c\u4e1a");
            } else {
                int squadCap;
                int squadCur = Math.max(0, cls.squadCurrentCount);
                lines.add((squadCur >= (squadCap = this.getSquadDisplayCap(cls)) ? "\u00a7c" : "\u00a7a") + squadCur + "/" + squadCap + " \u00a77\u00b7\u5175\u529b" + cls.troopValue);
            }
            if (!enabled && !(denial = this.resolveClassDenialMessage(cls)).isEmpty()) {
                String clean = denial.replaceAll("(?i)\u00a7[0-9A-FK-OR]", "");
                lines.add("\u00a7c" + clean);
            }
            Objects.requireNonNull(this.f_96547_);
            int lineH = Math.max(6, Math.round(9.0f * 0.68f) + 1);
            int pw = 132;
            int ph = 5 + lines.size() * lineH;
            int px = mx + 8;
            int py = my - ph / 2;
            if (px + pw > this.f_96543_) {
                px = mx - pw - 8;
            }
            if (py < 3) {
                py = 3;
            }
            if (py + ph > this.f_96544_ - 13) {
                py = this.f_96544_ - 13 - ph - 2;
            }
            graphics.m_280509_(px, py, px + pw, py + ph, -586084062);
            graphics.m_280637_(px, py, pw, ph, -11184777);
            int logicalWidth = Math.max(8, (int)((float)(pw - 7) / 0.68f));
            int ty = py + 3;
            for (String string : lines) {
                UnifiedDeployScreen.drawScaledString(graphics, EspetroAuiWidgets.trimToWidth(string, logicalWidth), px + 4, ty, 0xFFFFFF, 0.68f);
                ty += lineH;
            }
        }
    }

    private void renderVariantPopup(GuiGraphics graphics, int mouseX, int mouseY) {
        int variantIndex;
        if (!this.hasVariantPopup()) {
            return;
        }
        UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(this.variantPopupClassIndex);
        List<UnifiedDeployScreenPacket.VariantInfo> variants = cls.variants;
        int visible = Math.min(6, variants.size());
        graphics.m_280509_(this.variantPopupX, this.variantPopupY, this.variantPopupX + 180, this.variantPopupY + this.variantPopupH, -267316200);
        graphics.m_280637_(this.variantPopupX, this.variantPopupY, 180, this.variantPopupH, -1525668);
        graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a76\u00a7l" + cls.name + " \u00a77\u88c5\u5907\u53d8\u4f53"), this.variantPopupX + 6, this.variantPopupY + 5, 0xFFFFFF, false);
        int closeX = this.variantPopupX + 180 - 16;
        int closeY = this.variantPopupY + 2;
        boolean closeHovered = UnifiedDeployScreen.inside(mouseX, mouseY, closeX, closeY, 13, 13);
        graphics.m_280509_(closeX, closeY, closeX + 13, closeY + 13, closeHovered ? -3122598 : -11193035);
        graphics.m_280653_(this.f_96547_, Component.m_237113_("\u00a7fX"), closeX + 6, closeY + 2, 0xFFFFFF);
        for (int row = 0; row < visible && (variantIndex = this.variantPopupScroll + row) < variants.size(); ++row) {
            UnifiedDeployScreenPacket.VariantInfo variant = variants.get(variantIndex);
            int rowX = this.variantPopupX + 3;
            int rowY = this.variantPopupY + 18 + row * 28;
            int rowW = 174;
            int count = this.variantCounts.getOrDefault(cls.classId, Collections.emptyMap()).getOrDefault(variant.variantId, variant.currentCount);
            boolean parentFull = this.isClassButtonDisabled(cls);
            boolean full = parentFull || cls.strictCount && count >= variant.maxPlayers;
            boolean hovered = UnifiedDeployScreen.inside(mouseX, mouseY, rowX, rowY, rowW, 27);
            int fill = full ? -802480091 : (hovered ? -532459195 : -803529184);
            graphics.m_280509_(rowX, rowY, rowX + rowW, rowY + 28 - 1, fill);
            graphics.m_280637_(rowX, rowY, rowW, 27, hovered && !full ? -4732488 : -2141626274);
            String countText = cls.strictCount ? (full ? "\u00a7c" : "\u00a7a") + "[" + count + "/" + variant.maxPlayers + "]" : "\u00a7a" + count + "\u4eba";
            graphics.m_280614_(this.f_96547_, Component.m_237113_((full ? "\u00a78" : "\u00a7f") + variant.name), rowX + 5, rowY + 4, 0xFFFFFF, false);
            int countW = this.f_96547_.m_92895_(EspetroAuiWidgets.stripFormatting(countText));
            graphics.m_280614_(this.f_96547_, Component.m_237113_(countText), rowX + rowW - countW - 5, rowY + 4, 0xFFFFFF, false);
            if (variant.description == null || variant.description.isBlank()) continue;
            graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a77" + EspetroAuiWidgets.trimToWidth(variant.description, rowW - 10)), rowX + 5, rowY + 16, -5327681, false);
        }
        if (variants.size() > 6) {
            graphics.m_280614_(this.f_96547_, Component.m_237113_("\u00a78\u6eda\u8f6e\u6d4f\u89c8"), this.variantPopupX + 180 - 49, this.variantPopupY + this.variantPopupH - 10, -5327681, false);
        }
    }

    @Override
    public boolean m_6375_(double mx, double my, int button) {
        if (this.tutorialPreviewMode) {
            return super.m_6375_(mx, my, button);
        }
        this.lastMouseX = (int)mx;
        this.lastMouseY = (int)my;
        if (this.hasVariantPopup()) {
            if (button == 0 && this.handleVariantPopupClick((int)mx, (int)my)) {
                return true;
            }
            this.closeVariantPopup();
            return true;
        }
        return super.m_6375_(mx, my, button);
    }

    @Override
    public boolean m_6050_(double mx, double my, double delta) {
        if (this.hasVariantPopup() && UnifiedDeployScreen.inside((int)mx, (int)my, this.variantPopupX, this.variantPopupY, 180, this.variantPopupH)) {
            int maxScroll = Math.max(0, this.classes.get((int)this.variantPopupClassIndex).variants.size() - 6);
            this.variantPopupScroll = Math.max(0, Math.min(maxScroll, this.variantPopupScroll + (delta < 0.0 ? 1 : -1)));
            return true;
        }
        return super.m_6050_(mx, my, delta);
    }

    @Override
    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256 && this.hasFireteamContextMenu()) {
            this.closeFireteamContextMenu();
            return true;
        }
        if (keyCode == 256 && this.hasVariantPopup()) {
            this.closeVariantPopup();
            return true;
        }
        if (keyCode == 67) {
            HcrTacticalMapBridge.increaseRenderRange();
            return true;
        }
        if (keyCode == 66) {
            HcrTacticalMapBridge.decreaseRenderRange();
            return true;
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private boolean inSquad() {
        return this.mySquadId >= 0;
    }

    private int mySquadSize() {
        if (!this.inSquad()) {
            return 0;
        }
        for (UnifiedDeployScreenPacket.SquadInfo squad : this.squads) {
            if (squad.id != this.mySquadId) continue;
            return squad.members.size();
        }
        return 0;
    }

    private String buildClassCountRightLabel(UnifiedDeployScreenPacket.ClassInfo cls, boolean disabled) {
        String color;
        String string = color = disabled ? "\u00a7c" : "\u00a7a";
        if (!this.inSquad()) {
            return "";
        }
        int squadCur = Math.max(0, cls.squadCurrentCount);
        int squadCap = this.getSquadDisplayCap(cls);
        return color + "[" + squadCur + "/" + squadCap + "]";
    }

    private String resolveClassDenialMessage(UnifiedDeployScreenPacket.ClassInfo cls) {
        if (!this.isClassSelectionLocationAllowed()) {
            return "\u53ea\u80fd\u5728\u9009\u62e9\u90e8\u7f72\u70b9\u65f6\u3001\u539f\u90e8\u7f72\u70b9\u9644\u8fd1\u6216\u5df1\u65b9 Radio \u8f6e\u76d8\u4e2d\u9009\u62e9\u804c\u4e1a\u3002";
        }
        if (this.getClassSwitchCooldownRemaining() > 0) {
            return "\u804c\u4e1a\u5207\u6362\u51b7\u5374\u4e2d\uff0c\u8fd8\u9700\u7b49\u5f85 " + this.getClassSwitchCooldownRemaining() + " \u79d2\u3002";
        }
        if (!this.inSquad()) {
            return "\u8bf7\u5148\u52a0\u5165\u73ed\u7ec4\u5c0f\u961f\u540e\u518d\u9009\u62e9\u804c\u4e1a\u3002";
        }
        if (cls.teammatesNeed > 0 && this.mySquadSize() < cls.teammatesNeed) {
            return "\u5c0f\u961f\u8fbe\u5230 " + cls.teammatesNeed + " \u4eba\u540e\u624d\u80fd\u9009\u62e9\u8be5\u804c\u4e1a\u3002";
        }
        if (cls.unlockMinSquad > 0 && this.mySquadSize() < cls.unlockMinSquad) {
            return "\u5c0f\u961f\u8fbe\u5230 " + cls.unlockMinSquad + " \u4eba\u540e\u624d\u80fd\u89e3\u9501\u8be5\u804c\u4e1a\u3002";
        }
        int squadCur = Math.max(0, cls.squadCurrentCount);
        if (cls.unlockPerN > 0) {
            int available = this.mySquadSize() / cls.unlockPerN;
            if (available <= 0) {
                return "\u5c0f\u961f\u9700\u6ee1 " + cls.unlockPerN + " \u4eba\u624d\u80fd\u89e3\u9501 1 \u4e2a\u8be5\u804c\u4e1a\u540d\u989d\u3002";
            }
            if (squadCur >= available) {
                return "\u8be5\u804c\u4e1a\u540d\u989d\u5df2\u7528\u5b8c\uff08\u6bcf " + cls.unlockPerN + " \u4eba\u89e3\u9501 1 \u4e2a\uff0c\u5f53\u524d " + available + " \u4e2a\uff09\u3002";
            }
        }
        if (cls.teamCount) {
            if (squadCur >= cls.maxPlayers) {
                return "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCur + "/" + cls.maxPlayers + "\uff09\u3002";
            }
            return "";
        }
        int teamCur = this.classCounts.getOrDefault(cls.classId, cls.currentCount);
        if (teamCur >= cls.maxPlayers) {
            return "\u8be5\u804c\u4e1a\u5168\u961f\u4eba\u6570\u5df2\u6ee1\uff08" + teamCur + "/" + cls.maxPlayers + "\uff09\uff0c\u5c0f\u961f\u663e\u793a\u672a\u6ee1\u4e5f\u4e0d\u80fd\u518d\u9009\u3002";
        }
        if (cls.maxPerSquad > 0 && squadCur >= cls.maxPerSquad) {
            return "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCur + "/" + cls.maxPerSquad + "\uff09\u3002";
        }
        return "";
    }

    private int getSquadDisplayCap(UnifiedDeployScreenPacket.ClassInfo cls) {
        if (cls.teamCount) {
            return Math.max(1, cls.maxPlayers);
        }
        if (cls.maxPerSquad > 0) {
            return cls.maxPerSquad;
        }
        return Math.max(1, cls.maxPlayers);
    }

    private boolean isClassButtonDisabled(UnifiedDeployScreenPacket.ClassInfo cls) {
        int available;
        if (!this.isClassSelectionLocationAllowed()) {
            return true;
        }
        if (this.getClassSwitchCooldownRemaining() > 0) {
            return true;
        }
        if (!this.inSquad()) {
            return true;
        }
        if (cls.teammatesNeed > 0 && this.mySquadSize() < cls.teammatesNeed) {
            return true;
        }
        if (cls.unlockMinSquad > 0 && this.mySquadSize() < cls.unlockMinSquad) {
            return true;
        }
        int squadCur = Math.max(0, cls.squadCurrentCount);
        if (cls.unlockPerN > 0 && ((available = this.mySquadSize() / cls.unlockPerN) <= 0 || squadCur >= available)) {
            return true;
        }
        if (cls.teamCount) {
            return squadCur >= cls.maxPlayers;
        }
        int teamCount = this.classCounts.getOrDefault(cls.classId, cls.currentCount);
        if (teamCount >= cls.maxPlayers) {
            return true;
        }
        return cls.maxPerSquad > 0 && squadCur >= cls.maxPerSquad;
    }

    private boolean isClassEmphasizeRed(UnifiedDeployScreenPacket.ClassInfo cls, boolean disabled) {
        int available;
        if (!this.isClassSelectionLocationAllowed()) {
            return true;
        }
        if (this.getClassSwitchCooldownRemaining() > 0) {
            return false;
        }
        if (!disabled) {
            return false;
        }
        if (!this.inSquad()) {
            return true;
        }
        if (cls.teammatesNeed > 0 && this.mySquadSize() < cls.teammatesNeed) {
            return true;
        }
        if (cls.unlockMinSquad > 0 && this.mySquadSize() < cls.unlockMinSquad) {
            return true;
        }
        int squadCur = Math.max(0, cls.squadCurrentCount);
        if (cls.unlockPerN > 0 && ((available = this.mySquadSize() / cls.unlockPerN) <= 0 || squadCur >= available)) {
            return true;
        }
        if (cls.teamCount) {
            return squadCur >= cls.maxPlayers;
        }
        int teamCount = this.classCounts.getOrDefault(cls.classId, cls.currentCount);
        if (teamCount >= cls.maxPlayers) {
            return true;
        }
        return cls.maxPerSquad > 0 && squadCur >= cls.maxPerSquad;
    }

    private boolean isClassSelectionLocationAllowed() {
        if (this.waitingForDeploySelection) {
            return true;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return false;
        }
        for (EquipZoneSyncPacket.Zone zone : ClientEquipZones.getZones()) {
            double range;
            double dz;
            double dy;
            double dx;
            if (!"spawn".equals(zone.type()) || !((dx = mc.f_91074_.m_20185_() - zone.x()) * dx + (dy = mc.f_91074_.m_20186_() - zone.y()) * dy + (dz = mc.f_91074_.m_20189_() - zone.z()) * dz < (range = Math.max(0.1, zone.range())) * range)) continue;
            return true;
        }
        return false;
    }

    private void refreshClassButtons() {
        boolean coolingDown = this.getClassSwitchCooldownRemaining() > 0;
        for (EspButton btn : this.classButtons) {
            boolean isCompactBtn;
            Integer clsIdx = this.classButtonToClassIndex.get(btn);
            if (clsIdx == null || clsIdx < 0 || clsIdx >= this.classes.size()) continue;
            UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(clsIdx);
            boolean disabled = this.isClassButtonDisabled(cls);
            boolean emphasizeRed = this.isClassEmphasizeRed(cls, disabled);
            boolean isSelected = this.selectedClassId != null && this.selectedClassId.equals(cls.classId);
            boolean bl = isCompactBtn = btn.getWidth() > 19;
            if (isCompactBtn) {
                btn.setLabel("\u00a7f" + cls.name);
                btn.setRightLabel(this.buildClassCountRightLabel(cls, emphasizeRed));
            } else {
                btn.setLabel("");
                btn.setRightLabel(null);
            }
            btn.setIcon(RoleIconResources.resolve(cls.iconImage, cls.icon), 128, 128);
            btn.setEnabled(!disabled);
            int classIndex = clsIdx;
            btn.setDisabledAction(() -> this.selectClass(classIndex));
            if (disabled) {
                if (isSelected) {
                    btn.setDisabledStyle(-9803232, -1525668, -1);
                    continue;
                }
                btn.setDisabledStyle(coolingDown ? -15197411 : -11919324, coolingDown ? 1614297160 : -7718334, coolingDown ? -8947849 : -25958);
                continue;
            }
            btn.setDisabledAction(null);
            btn.normalColor = isSelected ? -9803232 : -15000032;
            btn.hoverColor = -12365499;
        }
    }

    private int getClassSwitchCooldownRemaining() {
        long remainingMs = this.classSwitchCooldownEndsAt - System.currentTimeMillis();
        return remainingMs <= 0L ? 0 : (int)((remainingMs + 999L) / 1000L);
    }

    private String buildClassTitle() {
        int remaining = this.getClassSwitchCooldownRemaining();
        return remaining > 0 ? "\u00a76\u804c\u4e1a\u9009\u62e9 \u00a77| \u00a7e" + remaining + "\u79d2\u540e\u53ef\u66f4\u6362" : "\u00a76\u804c\u4e1a\u9009\u62e9";
    }

    private void refreshClassSwitchCooldown() {
        int remaining = this.getClassSwitchCooldownRemaining();
        if (remaining == this.lastDisplayedClassSwitchCooldown) {
            return;
        }
        this.lastDisplayedClassSwitchCooldown = remaining;
        if (this.classTitleText != null) {
            this.classTitleText.setText(this.buildClassTitle());
        }
        if (remaining > 0) {
            this.closeVariantPopup();
        }
        this.refreshClassButtons();
    }

    private void refreshClassSelectionLocation() {
        boolean allowed = this.isClassSelectionLocationAllowed();
        if (allowed == this.lastClassSelectionLocationAllowed) {
            return;
        }
        this.lastClassSelectionLocationAllowed = allowed;
        if (!allowed) {
            this.closeVariantPopup();
        }
        this.refreshClassButtons();
    }

    private void selectClass(int index) {
        if (index >= 0 && index < this.classes.size()) {
            UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(index);
            if (this.isClassButtonDisabled(cls)) {
                String reason = this.resolveClassDenialMessage(cls);
                if (reason.isEmpty()) {
                    reason = "\u5f53\u524d\u65e0\u6cd5\u9009\u62e9\u8be5\u804c\u4e1a\u3002";
                }
                EspetroTipNotifier.showDenial("\u65e0\u6cd5\u9009\u62e9\u804c\u4e1a", reason);
                return;
            }
            if (cls.variants.size() == 1) {
                ClassSelectionGui.selectClass(this.factionId, cls.classId, cls.variants.get((int)0).variantId);
            } else if (cls.variants.size() > 1) {
                this.openVariantPopup(index, this.lastMouseX, this.lastMouseY);
            }
        }
    }

    private void openVariantPopup(int classIndex, int mouseX, int mouseY) {
        this.variantPopupClassIndex = classIndex;
        this.variantPopupScroll = 0;
        int count = this.classes.get((int)classIndex).variants.size();
        int visible = Math.min(6, count);
        int footer = count > 6 ? 10 : 3;
        this.variantPopupH = 18 + visible * 28 + footer;
        this.variantPopupX = mouseX + 9;
        if (this.variantPopupX + 180 > this.f_96543_ - 3) {
            this.variantPopupX = mouseX - 180 - 9;
        }
        this.variantPopupX = Math.max(3, Math.min(this.f_96543_ - 180 - 3, this.variantPopupX));
        this.variantPopupY = Math.max(3, Math.min(this.f_96544_ - 13 - this.variantPopupH - 2, mouseY + 7));
    }

    private boolean handleVariantPopupClick(int mouseX, int mouseY) {
        int variantIndex;
        if (this.getClassSwitchCooldownRemaining() > 0) {
            this.closeVariantPopup();
            return true;
        }
        int closeX = this.variantPopupX + 180 - 16;
        int closeY = this.variantPopupY + 2;
        if (UnifiedDeployScreen.inside(mouseX, mouseY, closeX, closeY, 13, 13)) {
            this.closeVariantPopup();
            return true;
        }
        if (!UnifiedDeployScreen.inside(mouseX, mouseY, this.variantPopupX, this.variantPopupY, 180, this.variantPopupH)) {
            this.closeVariantPopup();
            return true;
        }
        UnifiedDeployScreenPacket.ClassInfo cls = this.classes.get(this.variantPopupClassIndex);
        int row = (mouseY - (this.variantPopupY + 18)) / 28;
        if (mouseY >= this.variantPopupY + 18 && row >= 0 && row < 6 && (variantIndex = this.variantPopupScroll + row) < cls.variants.size()) {
            UnifiedDeployScreenPacket.VariantInfo variant = cls.variants.get(variantIndex);
            int count = this.variantCounts.getOrDefault(cls.classId, Collections.emptyMap()).getOrDefault(variant.variantId, variant.currentCount);
            if (!cls.strictCount || count < variant.maxPlayers) {
                ClassSelectionGui.selectClass(this.factionId, cls.classId, variant.variantId);
                this.closeVariantPopup();
            }
        }
        return true;
    }

    private boolean hasVariantPopup() {
        return this.variantPopupClassIndex >= 0 && this.variantPopupClassIndex < this.classes.size();
    }

    private void closeVariantPopup() {
        this.variantPopupClassIndex = -1;
        this.variantPopupScroll = 0;
    }

    private boolean hasFireteamContextMenu() {
        return this.fireteamContextTarget != null && this.fireteamContextRoot != null && this.fireteamContextRoot.isVisible() && !this.fireteamContextEntries.isEmpty();
    }

    private void closeFireteamContextMenu() {
        this.fireteamContextTarget = null;
        this.fireteamContextEntries.clear();
        if (this.fireteamContextRoot != null) {
            this.fireteamContextRoot.clearChildren();
            this.fireteamContextRoot.setVisible(false);
        }
    }

    private void openFireteamContextMenu(UUID targetUuid, int squadId, int mouseX, int mouseY) {
        this.closeFireteamContextMenu();
        if (targetUuid == null) {
            return;
        }
        UnifiedDeployScreenPacket.SquadInfo squad = null;
        for (UnifiedDeployScreenPacket.SquadInfo s : this.squads) {
            if (s.id != squadId) continue;
            squad = s;
            break;
        }
        if (squad == null) {
            return;
        }
        UnifiedDeployScreenPacket.SquadMemberInfo target = null;
        UnifiedDeployScreenPacket.SquadMemberInfo self = null;
        UUID localId = Minecraft.m_91087_().f_91074_ != null ? Minecraft.m_91087_().f_91074_.m_20148_() : null;
        for (UnifiedDeployScreenPacket.SquadMemberInfo m : squad.members) {
            if (m.uuid.equals(targetUuid)) {
                target = m;
            }
            if (localId == null || !m.uuid.equals(localId)) continue;
            self = m;
        }
        if (target == null || self == null) {
            return;
        }
        if (squad.id != this.mySquadId) {
            return;
        }
        boolean selfIsSquadLeader = self.leader;
        boolean selfIsFtLeader = self.fireteamLeader;
        boolean sameFireteam = self.fireteam == target.fireteam;
        UUID targetId = target.uuid;
        byte targetFt = target.fireteam;
        boolean isSelf = targetId.equals(self.uuid);
        ArrayList<FireteamContextEntry> entries = new ArrayList<FireteamContextEntry>();
        if (selfIsSquadLeader && !isSelf) {
            entries.add(new FireteamContextEntry("\u8f6c\u79fb\u961f\u957f\uff1f", true, () -> {
                NetworkManager.transferSquadLeader(targetId);
                this.closeFireteamContextMenu();
            }));
            for (Fireteam ft : Fireteam.values()) {
                boolean already = targetFt == ft.toNetwork();
                Fireteam assignFt = ft;
                entries.add(new FireteamContextEntry("\u5c06\u8be5\u961f\u5458\u79fb\u81f3" + ft.label() + "\u7ec4", !already, already ? null : () -> {
                    NetworkManager.assignFireteam(targetId, assignFt);
                    this.closeFireteamContextMenu();
                }));
            }
            for (Fireteam ft : new Fireteam[]{Fireteam.B, Fireteam.C}) {
                boolean alreadyLeader = targetFt == ft.toNetwork() && target.fireteamLeader;
                Fireteam appointFt = ft;
                entries.add(new FireteamContextEntry("\u6307\u8ba4\u4e3a" + ft.label() + "\u7ec4\u957f", !alreadyLeader, alreadyLeader ? null : () -> {
                    NetworkManager.appointFireteamLeader(targetId, appointFt);
                    this.closeFireteamContextMenu();
                }));
            }
        }
        if (!selfIsSquadLeader && selfIsFtLeader && sameFireteam && !isSelf) {
            entries.add(new FireteamContextEntry("\u8f6c\u79fb\u7ec4\u957f\uff1f", true, () -> {
                NetworkManager.transferFireteamLeader(targetId);
                this.closeFireteamContextMenu();
            }));
        }
        if (entries.isEmpty()) {
            return;
        }
        this.fireteamContextTarget = targetUuid;
        this.fireteamContextEntries.clear();
        this.fireteamContextEntries.addAll(entries);
        int menuH = 6 + entries.size() * 12;
        this.fireteamContextX = Math.max(3, Math.min(this.f_96543_ - 118 - 3, mouseX + 6));
        this.fireteamContextY = Math.max(3, Math.min(this.f_96544_ - menuH - 3, mouseY + 4));
        if (this.fireteamContextRoot == null) {
            return;
        }
        this.fireteamContextRoot.clearChildren();
        this.fireteamContextRoot.setVisible(true);
        this.fireteamContextRoot.addChild(new GuiElement(0, 0, this.f_96543_, this.f_96544_){

            @Override
            public boolean onMouseClick(int mx, int my, int button) {
                UnifiedDeployScreen.this.closeFireteamContextMenu();
                return true;
            }
        });
        this.fireteamContextRoot.addChild(new GuiRect(this.fireteamContextX, this.fireteamContextY, 118, menuH, -267316200));
        int y = this.fireteamContextY + 3;
        for (FireteamContextEntry entry : this.fireteamContextEntries) {
            EspButton button = new EspButton(this.fireteamContextX + 2, y, 114, 11, entry.label, entry.action);
            button.setEnabled(entry.enabled);
            button.setTextScale(0.64f);
            button.setCenteredText(false);
            this.fireteamContextRoot.addChild(button);
            y += 12;
        }
    }

    private static boolean inside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x < left + width && y >= top && y < top + height;
    }

    @Override
    public boolean m_6913_() {
        return !this.isDeploymentSelectionRequired();
    }

    @Override
    public void m_7379_() {
        if (!this.isDeploymentSelectionRequired()) {
            super.m_7379_();
        }
    }

    private boolean isDeploymentSelectionRequired() {
        return this.waitingForDeploySelection;
    }

    @Override
    public boolean m_7043_() {
        return false;
    }

    private static enum Section {
        SQUAD,
        CLASS,
        DEPLOY,
        MAP_CONTROLS,
        STATUS;

    }

    private static class EspButton
    extends GuiElement {
        private final Runnable action;
        private String label;
        private boolean enabled = true;
        private boolean hovered = false;
        private int normalColor = -15000032;
        private int hoverColor = -12365499;
        private int disabledColor = -15197411;
        private int disabledBorderColor = 1614297160;
        private int disabledTextColor = 0x666666;
        private int textColor = 0xFFFFFF;
        private ResourceLocation icon;
        private int iconTextureWidth = 128;
        private int iconTextureHeight = 128;
        private int iconSize = 10;
        private String rightLabel = "";
        private float textScale = 0.72f;
        private boolean centeredText = true;
        private Runnable disabledAction;

        EspButton(int x, int y, int w, int h, String label, Runnable action) {
            super(x, y, w, h);
            this.label = label;
            this.action = action;
        }

        void setEnabled(boolean e) {
            this.enabled = e;
        }

        void setLabel(String l) {
            String next;
            String string = next = l == null ? "" : l;
            if (Objects.equals(this.label, next)) {
                return;
            }
            this.label = next;
        }

        boolean isEnabled() {
            return this.enabled;
        }

        void setDisabledAction(Runnable r) {
            this.disabledAction = r;
        }

        void setIcon(ResourceLocation icon, int textureWidth, int textureHeight) {
            this.icon = icon;
            this.iconTextureWidth = textureWidth;
            this.iconTextureHeight = textureHeight;
        }

        void setIconSize(int size) {
            this.iconSize = Math.max(1, size);
        }

        void setRightLabel(String value) {
            this.rightLabel = value == null ? "" : value;
        }

        void setTextScale(float scale) {
            this.textScale = Math.max(0.5f, Math.min(1.0f, scale));
        }

        void setCenteredText(boolean centered) {
            this.centeredText = centered;
        }

        void setDisabledStyle(int background, int border, int text) {
            this.disabledColor = background;
            this.disabledBorderColor = border;
            this.disabledTextColor = text;
        }

        @Override
        public boolean onMouseClick(int mx, int my, int button) {
            if (button != 0 || !this.isVisible() || !this.hasFocus()) {
                return false;
            }
            if (!this.enabled) {
                if (this.disabledAction != null) {
                    this.disabledAction.run();
                    return true;
                }
                return false;
            }
            if (this.action != null) {
                this.action.run();
            }
            return true;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!this.isVisible()) {
                return;
            }
            this.hovered = this.hasFocus();
            int bx = x + this.getX();
            int by = y + this.getY();
            int bw = this.getWidth();
            int bh = this.getHeight();
            int bgCol = !this.enabled ? this.disabledColor : (this.hovered ? this.hoverColor : this.normalColor);
            graphics.m_280509_(bx, by, bx + bw, by + bh, bgCol);
            int borderCol = !this.enabled ? this.disabledBorderColor : (this.hovered ? -6710853 : -10919842);
            graphics.m_280637_(bx, by, bw, bh, borderCol);
            int textCol = this.enabled ? this.textColor : this.disabledTextColor;
            int contentLeft = bx + 3;
            if (this.icon != null) {
                int drawnIconSize = Math.min(this.iconSize, bh - 3);
                graphics.m_280411_(this.icon, bx + 2, by + (bh - drawnIconSize) / 2, drawnIconSize, drawnIconSize, 0.0f, 0.0f, this.iconTextureWidth, this.iconTextureHeight, this.iconTextureWidth, this.iconTextureHeight);
                contentLeft = bx + drawnIconSize + 4;
            }
            int rightLabelWidth = this.rightLabel.isEmpty() ? 0 : Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(this.rightLabel)) * this.textScale);
            int rightLabelX = bx + bw - 3 - rightLabelWidth;
            int availableTextWidth = Math.max(8, rightLabelX - (this.rightLabel.isEmpty() ? 0 : 4) - contentLeft);
            int logicalTextWidth = Math.max(8, (int)((float)availableTextWidth / this.textScale));
            String drawnLabel = EspetroAuiWidgets.trimToWidth(this.label, logicalTextWidth);
            int textWidth = Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(drawnLabel)) * this.textScale);
            int textX = this.centeredText ? contentLeft + Math.max(0, (availableTextWidth - textWidth) / 2) : contentLeft;
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            int textHeight = Math.max(1, Math.round(9.0f * this.textScale));
            UnifiedDeployScreen.drawScaledString(graphics, drawnLabel, textX, by + Math.max(0, (bh - textHeight) / 2), textCol, this.textScale);
            if (!this.rightLabel.isEmpty()) {
                UnifiedDeployScreen.drawScaledString(graphics, this.rightLabel, rightLabelX, by + Math.max(0, (bh - textHeight) / 2), textCol, this.textScale);
            }
        }
    }

    private static class PlainText
    extends GuiElement {
        private String text;
        private int color;

        PlainText(int x, int y, String text, int color) {
            int n = Math.max(1, Math.round((float)Minecraft.m_91087_().f_91062_.m_92895_(EspetroAuiWidgets.stripFormatting(text)) * 0.72f));
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            super(x, y, n, Math.max(1, Math.round(9.0f * 0.72f)));
            this.text = text;
            this.color = color;
        }

        void setColor(int c) {
            this.color = c;
        }

        void setText(String value) {
            String next;
            String string = next = value == null ? "" : value;
            if (Objects.equals(this.text, next)) {
                return;
            }
            this.text = next;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!this.isVisible()) {
                return;
            }
            UnifiedDeployScreen.drawScaledString(graphics, this.text, x + this.getX(), y + this.getY(), this.color, 0.72f);
        }
    }

    private class SquadMemberRow
    extends GuiElement {
        private final String label;
        private final int fireteamColor;
        private final int roleAccentColor;
        private final UUID memberUuid;
        private final int squadId;

        SquadMemberRow(int x, int y, int width, String label, int fireteamColor, int roleAccentColor, UUID memberUuid, int squadId) {
            super(x, y, width, 9);
            this.label = label;
            this.fireteamColor = fireteamColor;
            this.roleAccentColor = roleAccentColor;
            this.memberUuid = memberUuid;
            this.squadId = squadId;
        }

        @Override
        public boolean onMouseClick(int mx, int my, int button) {
            if (!this.isVisible() || !this.hasFocus() || this.memberUuid == null) {
                return false;
            }
            if (button == 1) {
                UnifiedDeployScreen.this.openFireteamContextMenu(this.memberUuid, this.squadId, UnifiedDeployScreen.this.lastMouseX, UnifiedDeployScreen.this.lastMouseY);
                return true;
            }
            return false;
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int w, int h, int mx, int my, float tick) {
            if (!this.isVisible()) {
                return;
            }
            int bx = x + this.getX();
            int by = y + this.getY();
            int bar = 2;
            graphics.m_280509_(bx, by, bx + this.getWidth(), by + this.getHeight(), this.hasFocus() ? -264222909 : -535554537);
            graphics.m_280509_(bx, by, bx + bar, by + this.getHeight(), this.fireteamColor);
            graphics.m_280509_(bx + bar, by, bx + bar * 2, by + this.getHeight(), this.roleAccentColor);
            int textX = bx + bar * 2 + 3;
            int availableWidth = Math.max(8, this.getWidth() - (bar * 2 + 5));
            String drawnLabel = EspetroAuiWidgets.trimToWidth(this.label, (int)((float)availableWidth / 0.64f));
            Objects.requireNonNull(Minecraft.m_91087_().f_91062_);
            int textHeight = Math.max(1, Math.round(9.0f * 0.64f));
            UnifiedDeployScreen.drawScaledString(graphics, drawnLabel, textX, by + Math.max(0, (this.getHeight() - textHeight) / 2), 0xFFFFFF, 0.64f);
        }
    }

    private record FireteamContextEntry(String label, boolean enabled, Runnable action) {
    }
}

