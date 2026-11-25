package org.espetro.client.gui;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import org.espetro.network.RoundReport;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Local mock report using the real settlement screen and real mouse handlers. */
final class UiDevPreviews {
    private static boolean openedAtStartup;

    private UiDevPreviews() {}

    static LiteralArgumentBuilder<CommandSourceStack> command() {
        return Commands.literal("dev")
            .executes(context -> openLater(-1))
            .then(Commands.literal("list").executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("round_end, round_details, round_scoreboard"), false);
                return 1;
            }))
            .then(Commands.literal("open")
                .then(Commands.literal("round_end").executes(context -> openLater(0)))
                .then(Commands.literal("round_details").executes(context -> openLater(1)))
                .then(Commands.literal("round_scoreboard").executes(context -> openLater(2))));
    }

    private static int openLater(int page) {
        var mc = Minecraft.getInstance();
        mc.tell(() -> open(null, page));
        return 1;
    }

    static void onTitleKey(ScreenEvent.KeyPressed.Pre event) {
        if (event.getKeyCode() == GLFW.GLFW_KEY_F8 && event.getScreen() instanceof TitleScreen) {
            event.setCanceled(true);
            open(event.getScreen(), -1);
        }
    }

    static void onTitleOpened(ScreenEvent.Init.Post event) {
        String id = System.getProperty("espetro.uiDev", "");
        if (openedAtStartup || id.isBlank() || !(event.getScreen() instanceof TitleScreen)) return;
        openedAtStartup = true;
        var mc = Minecraft.getInstance();
        mc.tell(() -> {
            if (mc.screen != event.getScreen()) return;
            int page = switch (id) {
                case "round_end" -> 0;
                case "round_details" -> 1;
                case "round_scoreboard" -> 2;
                default -> -1;
            };
            open(event.getScreen(), page);
        });
    }

    private static void open(Screen parent, int page) {
        var mc = Minecraft.getInstance();
        var catalogue = new Catalogue(parent);
        mc.setScreen(page < 0 ? catalogue : sample(catalogue, page));
    }

    private static RoundEndScreen sample(Screen parent, int page) {
        var tickets = new ArrayList<RoundReport.Ticket>();
        var players = new ArrayList<RoundReport.Player>();
        var vehicle = new RoundReport.Reason(RoundReport.Cause.VEHICLE, "步兵战车被击毁", "ifv|",
            "2号小队 · 装甲支援", List.of("niko_4", "Morrow", "Boone"));
        int red = 600, blue = 600;
        for (int minute = 1; minute <= 52; minute++) {
            int tick = minute * 1200;
            RoundReport.Reason reason = RoundReport.Reason.of(RoundReport.Cause.DEATH, "队员阵亡");
            if (minute == 11) {
                tickets.add(new RoundReport.Ticket(tick, true, red, red + 25,
                    RoundReport.Reason.of(RoundReport.Cause.CAPTURE, "占领据点")));
                red += 25;
            }
            int nextRed = Math.max(186, red - (minute == 29 ? 35 : 8));
            int nextBlue = Math.max(0, blue - (minute == 38 ? 39 : 11));
            tickets.add(new RoundReport.Ticket(tick, true, red, nextRed, minute == 29 ? vehicle : reason));
            tickets.add(new RoundReport.Ticket(tick + (minute == 29 ? 45 : 0), false, blue, nextBlue, minute == 38
                ? RoundReport.Reason.of(RoundReport.Cause.FOB, "前线电台被摧毁") : reason));
            red = nextRed;
            blue = nextBlue;
        }
        String[] names = {"ly01_lin", "Morrow", "Boone", "Frost", "niko_4", "Trail",
            "CharlieLead", "Iris", "Harbor", "Slate", "Cobalt", "Rook"};
        String[] icons = {"leader", "medic", "automatic_rifleman", "light_at", "lead_crewman", "crewman",
            "leader", "rifleman", "grenadier", "medic", "leader", "machine_gunner"};
        String[] roles = {"队长", "医护兵", "自动步枪兵", "反装甲兵", "车组长", "车组成员",
            "队长", "步枪兵", "掷弹兵", "医护兵", "队长", "机枪手"};
        for (int i = 0; i < names.length; i++) {
            int j = i % 6;
            players.add(new RoundReport.Player(UUID.nameUUIDFromBytes(names[i].getBytes(java.nio.charset.StandardCharsets.UTF_8)),
                names[i], i < 6, j >= 4 ? 2 : 1, j >= 4 ? "装甲支援" : "前线步兵",
                (byte) (j == 2 || j == 5 ? 1 : j == 3 ? 2 : 0), j == 0 || j == 4,
                j != 1, roles[i], icons[i], "", icons[i], 19 - i, 3 + i % 4));
        }
        return new RoundEndScreen("ATTACK", RoundReport.DISPLAY_SECONDS, "进攻方", "防守方", red, blue, 3,
            false, new RoundReport(52 * 1200, 600, 600, tickets, players)).preview(parent, page);
    }

    private static final class Catalogue extends Screen {
        private final Screen parent;
        Catalogue(Screen parent) {
            super(Component.literal("Espetro UI 预览"));
            this.parent = parent;
        }
        @Override protected void init() {
            String[] names = {"结算 · 摘要", "结算 · 详情", "结算 · 计分板"};
            for (int i = 0; i < names.length; i++) {
                int page = i;
                addRenderableWidget(Button.builder(Component.literal(names[i]), b -> minecraft.setScreen(sample(this, page)))
                    .bounds(width / 2 - 64, height / 2 - 48 + i * 24, 128, 20).build());
            }
        }
        @Override public void render(net.minecraft.client.gui.GuiGraphics g, int x, int y, float tick) {
            renderBackground(g);
            g.drawCenteredString(font, title, width / 2, Math.max(5, height / 2 - 68), UiTheme.TEXT);
            super.render(g, x, y, tick);
        }
        @Override public void onClose() { minecraft.setScreen(parent); }
    }
}
