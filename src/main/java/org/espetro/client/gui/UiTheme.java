package org.espetro.client.gui;

/**
 * END-02 / Endfield-inspired industrial UI tokens.
 * Two matte palettes share one geometry and typography system. Color fields are
 * deliberately live values: never cache them in static final aliases elsewhere.
 * Theme changes run on the client thread; primitives never branch on a theme.
 * Previous palette: docs/archive/endfield-dark-v1/.
 */
public final class UiTheme {
    private UiTheme() { }

    public enum Mode { LIGHT, DARK }

    private static Mode mode = Mode.DARK;
    private static long revision;
    private static final java.util.List<Runnable> CHANGE_LISTENERS = new java.util.ArrayList<>();

    public static Mode mode() { return mode; }
    public static long revision() { return revision; }

    /** Refresh legacy aliases without introducing renderer dependencies. */
    public static void addChangeListener(Runnable listener) {
        CHANGE_LISTENERS.add(java.util.Objects.requireNonNull(listener));
    }

    /** Apply on the client thread; commands and persistence live outside tokens. */
    public static void setMode(Mode next) {
        java.util.Objects.requireNonNull(next, "mode");
        if (next == mode) return;
        mode = next;
        applyPalette();
        revision++;
        for (Runnable listener : java.util.List.copyOf(CHANGE_LISTENERS)) listener.run();
    }

    // Fixed colors for textures, world markers, and contrast on colored fills.
    public static final int TRANSPARENT = 0x00000000;
    public static final int DARK_INK = 0xFF151719;
    public static final int LIGHT_INK = 0xFFF5F5EE;
    public static final int WORLD_SCRIM = 0xCC101410;
    public static final int WORLD_TEXT = 0xFFF5F5EE;
    public static final int WORLD_INK = 0xFF101410;
    public static final int WORLD_MUTED = 0xFFBFC6B7;
    public static final int WORLD_POSITIVE = 0xFF7BE39D;
    public static final int WORLD_NEGATIVE = 0xFFFF766B;
    public static final int WORLD_WARNING = 0xFFFFD45C;
    public static final int TEXTURE_WHITE = 0xFFFFFFFF;
    public static final int ICON_PLATE = 0xFF252B23;

    /** ARGB form used by EsRadial's CSS color API. */
    public static String hex(int argb) {
        return String.format(java.util.Locale.ROOT, "#%08X", argb);
    }

    /** Signal yellow is a graphic color; use its readable ink variant for text. */
    public static int readableText(int color) {
        return color == ACCENT || color == ACCENT_DIM || color == ACCENT_LINE ? GOLD : color;
    }

    // Live semantic colors, read during drawing or widget construction.
    public static int BACKDROP_TOP = 0xFF111410;
    public static int BACKDROP_BOTTOM = 0xFF111410;
    public static int SURFACE_0 = 0xFF191D18;
    public static int CARD_TOP = 0xFF22271F;
    public static int CARD_BOTTOM = 0xFF22271F;
    public static int SURFACE_2 = 0xFF2D3329;
    public static int SURFACE_3 = 0xFF3B4235;
    public static int SUNKEN = 0xFF10140F;
    public static int SOLID_PAGE = 0xFF171B15;
    public static int COLUMN_SCRIM = 0xF0191D17;
    public static int GLASS_PANEL = 0xB41B2019;
    public static int GLASS_PANEL_SOFT = 0x9E252B21;
    public static int FROST_SCRIM = 0x7A0D110C;
    public static int SHADOW_1 = 0x66000000;
    public static int SHADOW_2 = 0x47000000;
    public static int SHADOW_3 = 0x2E000000;
    public static int HAIRLINE = 0x22FFFFFF;
    public static int HAIRLINE_STRONG = 0x3DFFFFFF;
    public static int SHEEN = 0x00000000;
    public static int HAIRLINE_DARK = 0x66000000;
    public static int GRID = 0x10FFFFFF;
    public static int GRID_MAJOR = 0x1CFFFFFF;
    public static int TICK = 0xFF6E757E;
    public static int TEXT = 0xFFF0F2E8;
    public static int TEXT_SECOND = 0xFFC0C6B8;
    public static int TEXT_THIRD = 0xFF9CA591;
    public static int TEXT_DISABLED = 0xFF6E7864;
    public static int TEXT_ON_ACCENT = 0xFF0A0C0E;
    public static int INK_FILL = 0xFFF0F2E8;
    public static int ON_INK = 0xFF171A16;
    public static int ON_INK_SECOND = 0xFF4B5046;
    public static int ACCENT = 0xFFFFE500;
    public static int ACCENT_DIM = 0xFFE2CB00;
    public static int ACCENT_ON_PAPER = 0xFFFFE500;
    public static int ACCENT_SOFT = 0x30FFE500;
    public static int ACCENT_LINE = 0xFFFFE500;
    public static int ACCENT_GLOW = 0x33FFE500;
    public static int GOLD = 0xFFFFE579;
    public static int GOLD_SOFT = 0x2EFFE579;
    public static int GOLD_LINE = 0x66FFE579;
    public static int ATTACK = 0xFFFF6B5E;
    public static int DEFEND = 0xFF5C8CFF;
    public static int POSITIVE = 0xFF57D38C;
    public static int WARNING = 0xFFFFC24B;
    public static int NEGATIVE = 0xFFFF5C5C;
    public static int PURPLE = 0xFFC08CFF;
    public static int SQUAD = 0xFF67A7FF;
    public static int ATTACK_SOFT = 0x26FF6B5E;
    public static int DEFEND_SOFT = 0x265C8CFF;
    public static int POSITIVE_SOFT = 0x2657D38C;
    public static int WARNING_SOFT = 0x26FFC24B;
    public static int NEGATIVE_SOFT = 0x26FF5C5C;

    private static void applyPalette() {
        boolean light = mode == Mode.LIGHT;
        BACKDROP_TOP = light ? 0xFFF2F2EC : 0xFF111410;
        BACKDROP_BOTTOM = light ? 0xFFF2F2EC : 0xFF111410;
        SURFACE_0 = light ? 0xFFF0F0E9 : 0xFF191D18;
        CARD_TOP = light ? 0xFFFAFAF4 : 0xFF22271F;
        CARD_BOTTOM = light ? 0xFFFAFAF4 : 0xFF22271F;
        SURFACE_2 = light ? 0xFFE8E9DF : 0xFF2D3329;
        SURFACE_3 = light ? 0xFFDCDDD2 : 0xFF3B4235;
        SUNKEN = light ? 0xFFE0E1D8 : 0xFF10140F;
        SOLID_PAGE = light ? 0xFFECEDE5 : 0xFF171B15;
        COLUMN_SCRIM = light ? 0xF0F2F2EA : 0xF0191D17;
        GLASS_PANEL = light ? 0xC6F4F4ED : 0xB41B2019;
        GLASS_PANEL_SOFT = light ? 0xB2E7E8DE : 0x9E252B21;
        FROST_SCRIM = light ? 0x96EEEFE6 : 0x7A0D110C;
        SHADOW_1 = light ? 0x23131517 : 0x66000000;
        SHADOW_2 = light ? 0x18131517 : 0x47000000;
        SHADOW_3 = light ? 0x10131517 : 0x2E000000;
        HAIRLINE = light ? 0x30202420 : 0x22FFFFFF;
        HAIRLINE_STRONG = light ? 0x66202420 : 0x3DFFFFFF;
        SHEEN = light ? 0x00000000 : 0x00000000;
        HAIRLINE_DARK = light ? 0x30202420 : 0x66000000;
        GRID = light ? 0x08202420 : 0x10FFFFFF;
        GRID_MAJOR = light ? 0x10202420 : 0x1CFFFFFF;
        TICK = light ? 0xFF85887B : 0xFF6E757E;
        TEXT = light ? 0xFF171A16 : 0xFFF0F2E8;
        TEXT_SECOND = light ? 0xFF4B5046 : 0xFFC0C6B8;
        TEXT_THIRD = light ? 0xFF62675C : 0xFF9CA591;
        TEXT_DISABLED = light ? 0xFF858A7F : 0xFF6E7864;
        TEXT_ON_ACCENT = light ? 0xFF151719 : 0xFF0A0C0E;
        INK_FILL = light ? 0xFF20251F : 0xFFF0F2E8;
        ON_INK = light ? 0xFFF5F5EE : 0xFF171A16;
        ON_INK_SECOND = light ? 0xFFC4C8BA : 0xFF4B5046;
        ACCENT = light ? 0xFFFFE500 : 0xFFFFE500;
        ACCENT_DIM = light ? 0xFFDFCA00 : 0xFFE2CB00;
        ACCENT_ON_PAPER = light ? 0xFF6B5C00 : 0xFFFFE500;
        ACCENT_SOFT = light ? 0x38FFE500 : 0x30FFE500;
        ACCENT_LINE = light ? 0xFFAA9300 : 0xFFFFE500;
        ACCENT_GLOW = light ? 0x33FFE500 : 0x33FFE500;
        GOLD = light ? 0xFF6B5C00 : 0xFFFFE579;
        GOLD_SOFT = light ? 0x226B5C00 : 0x2EFFE579;
        GOLD_LINE = light ? 0x666B5C00 : 0x66FFE579;
        ATTACK = light ? 0xFFB23E33 : 0xFFFF6B5E;
        DEFEND = light ? 0xFF2E58A6 : 0xFF5C8CFF;
        POSITIVE = light ? 0xFF256B42 : 0xFF57D38C;
        WARNING = light ? 0xFF865500 : 0xFFFFC24B;
        NEGATIVE = light ? 0xFFB22E32 : 0xFFFF5C5C;
        PURPLE = light ? 0xFF7750A0 : 0xFFC08CFF;
        SQUAD = light ? 0xFF3268A1 : 0xFF67A7FF;
        ATTACK_SOFT = light ? 0x1FB23E33 : 0x26FF6B5E;
        DEFEND_SOFT = light ? 0x1F2E58A6 : 0x265C8CFF;
        POSITIVE_SOFT = light ? 0x1F256B42 : 0x2657D38C;
        WARNING_SOFT = light ? 0x1F865500 : 0x26FFC24B;
        NEGATIVE_SOFT = light ? 0x1FB22E32 : 0x26FF5C5C;
    }

    // ------------------------------------------------------------------
    // 几何（Geometry）：4px 网格，全直角
    // ------------------------------------------------------------------

    /** 卡片圆角：0（直角）。保留常量是为了调用方语义清晰。 */
    public static final int RADIUS_CARD = 0;
    /** 按钮、标签圆角：0。 */
    public static final int RADIUS_CONTROL = 0;
    /** 内嵌小块圆角：0。 */
    public static final int RADIUS_INNER = 0;

    /** 卡片切角尺寸（45° 切一刀，0 表示不切）。 */
    public static final int CHAMFER_CARD = 0;
    /** 控件切角尺寸。 */
    public static final int CHAMFER_CONTROL = 0;
    /** 强调条厚度。 */
    public static final int ACCENT_BAR = 3;
    /** 角标长度（卡四角的 L 形短线）。 */
    public static final int TICK_LENGTH = 6;

    /** 卡片内边距。 */
    public static final int PAD_CARD = 12;
    /** 元素间距（小/中/大）。 */
    public static final int GAP_XS = 4;
    public static final int GAP_S = 6;
    public static final int GAP_M = 10;
    public static final int GAP_L = 16;

    /** 标准控件高度。 */
    public static final int CONTROL_HEIGHT = 20;
    /** 紧凑控件高度（标签、内联按钮）。 */
    public static final int CONTROL_HEIGHT_S = 16;

    /** 发丝线厚度。 */
    public static final int HAIRLINE_WIDTH = 1;

    // ------------------------------------------------------------------
    // 排版（Typography）
    // ------------------------------------------------------------------

    /** 微标签：小号 + 字距，用于分组标题、字段名、编号。 */
    public static final float TYPE_MICRO = 0.8f;
    /** 正文。 */
    public static final float TYPE_BODY = 1.0f;
    /** 小标题。 */
    public static final float TYPE_HEADING = 1.1f;
    /** 页面标题。 */
    public static final float TYPE_TITLE = 1.25f;
    /** 大数值（比分、人口、弹药）。 */
    public static final float TYPE_NUMERAL = 1.5f;

    /** 微标签字距（像素，仅在缩放下叠加）。 */
    public static final int LETTERSPACING_MICRO = 1;
    /** 正文轻微字距。 */
    public static final int LETTERSPACING_BODY = 0;

    // ------------------------------------------------------------------
    // 动效（Motion）：编辑风要"快"，过渡短促
    // ------------------------------------------------------------------

    /** 悬停过渡时长。 */
    public static final long MOTION_HOVER_MS = 90L;
    /** 选中/切换过渡时长。 */
    public static final long MOTION_SELECT_MS = 120L;
    /** 面板入场时长。 */
    public static final long MOTION_ENTER_MS = 160L;

    // ------------------------------------------------------------------
    // 工具方法
    // ------------------------------------------------------------------

    /** 线性插值两个 ARGB 颜色（含 alpha）。 */
    public static int lerp(int from, int to, float t) {
        float f = Math.max(0f, Math.min(1f, t));
        int a = lerpChannel((from >>> 24) & 0xFF, (to >>> 24) & 0xFF, f);
        int r = lerpChannel((from >>> 16) & 0xFF, (to >>> 16) & 0xFF, f);
        int g = lerpChannel((from >>> 8) & 0xFF, (to >>> 8) & 0xFF, f);
        int b = lerpChannel(from & 0xFF, to & 0xFF, f);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lerpChannel(int from, int to, float t) {
        return Math.max(0, Math.min(255, Math.round(from + (to - from) * t)));
    }

    /** 按比例缩放 alpha（0..1）。 */
    public static int alpha(int argb, float factor) {
        int a = Math.round(((argb >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, factor)));
        return (a << 24) | (argb & 0x00FFFFFF);
    }

    /** 替换 alpha 通道。 */
    public static int withAlpha(int argb, int alpha) {
        return ((Math.max(0, Math.min(255, alpha)) & 0xFF) << 24) | (argb & 0x00FFFFFF);
    }

    /** Choose the fixed ink with the strongest sRGB contrast on an opaque fill. */
    public static int contrastText(int background) {
        double luminance = relativeLuminance(background);
        return contrastRatio(luminance, relativeLuminance(DARK_INK))
            >= contrastRatio(luminance, relativeLuminance(LIGHT_INK)) ? DARK_INK : LIGHT_INK;
    }

    private static double relativeLuminance(int color) {
        return 0.2126 * linearChannel((color >>> 16) & 0xFF)
            + 0.7152 * linearChannel((color >>> 8) & 0xFF)
            + 0.0722 * linearChannel(color & 0xFF);
    }

    private static double linearChannel(int channel) {
        double value = channel / 255.0;
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    private static double contrastRatio(double first, double second) {
        return (Math.max(first, second) + 0.05) / (Math.min(first, second) + 0.05);
    }

    /** 是否具备可见 alpha（0 表示完全不画）。 */
    public static boolean visible(int argb) {
        return (argb & 0xFF000000) != 0;
    }

    /** 纸上叠一层极淡的信号黄（用于"当前阶段"这类需要被注意的区域）。 */
    public static int accentWash(float strength) {
        return withAlpha(ACCENT, Math.round(255f * Math.max(0f, Math.min(1f, strength))));
    }

    /** 墨色块上的强调文字：纸底用深金，墨底用信号黄。 */
    public static int accentOn(boolean darkSurface) {
        return darkSurface ? ACCENT : ACCENT_ON_PAPER;
    }
}
