package org.espetro.client.gui;

/**
 * Espetro UI design tokens — "END-01 · 纸墨工业"（参照《明日方舟：终末地》的工业编辑风）。
 *
 * <p>上一版 "Tactical Glass"（冷调深色玻璃 / 圆角 / 青强调色）已归档：
 * git tag {@code ui/tactical-glass-v1}，源码快照见 {@code docs/archive/tactical-glass/}。
 *
 * <p>本版三条硬规则：
 * <ul>
 *   <li><b>纸与墨是主体，强调色是信号。</b>表面是带暖灰的"纸"（{@link #PAPER}），
 *       文字是"墨"（{@link #INK}）；信号黄 {@link #ACCENT} 只出现在需要被看见的那一处——
 *       选中、悬停、焦点、关键数值、强调条。</li>
 *   <li><b>全直角。</b>圆角会把界面推向"柔和产品"，直角把它推向"工程图纸"。
 *       只有本身是圆的东西（状态点、头像、环形进度）保留圆形；
 *       卡片允许切角（{@link #CHAMFER_CARD}），不使用圆弧。</li>
 *   <li><b>靠线与字距分层，不靠阴影。</b>1px 发丝线、角标、刻度、字距微标签构成层次；
 *       阴影只保留 1~2px 的硬投影，用来把卡片"钉"在纸上。</li>
 * </ul>
 *
 * <p>对比度：墨 {@code #101110} 压在纸 {@code #F2F2EC} 上约 17:1；
 * 信号黄作为文字在纸上不可读，故纸上文字用深金 {@link #GOLD}（{@code #6B5D00}，约 5.4:1），
 * 黄色只作底色/描边/图形（黄底压墨字约 16.5:1）。
 */
public final class UiTheme {

    private UiTheme() {
    }

    // ------------------------------------------------------------------
    // 表面（Surfaces）：暗阶，从全屏底到浮层
    // ------------------------------------------------------------------

    /** 全屏底：近黑玻璃，顶部略亮、底部略沉，遮住世界但保留一点场景层次。 */
    public static final int BACKDROP_TOP = 0xF60A0C0E;
    public static final int BACKDROP_BOTTOM = 0xF8111418;

    /** 页面主容器。 */
    public static final int SURFACE_0 = 0xF20D1013;

    /** 标准卡片：黑玻璃的竖向微渐变（上亮下暗，制造"压在底面上"的厚度）。 */
    public static final int CARD_TOP = 0xFF171B20;
    public static final int CARD_BOTTOM = 0xFF101419;

    /** 浮起 / 悬停态（比卡片再亮一档）。 */
    public static final int SURFACE_2 = 0xFF1E242A;

    /** 按下 / 选中态（比卡片更亮，作为选中块）。 */
    public static final int SURFACE_3 = 0xFF2A3138;

    /** 内凹区域（列表底、输入框、进度槽、表格行）。 */
    public static final int SUNKEN = 0xFF07090B;

    /** 纯色深灰页底：没有地图/世界可露出的页面（组队匹配）用一整块实色。 */
    public static final int SOLID_PAGE = 0xFF15181C;

    /** 黑色半透明栏底：战术面板各栏、浮在地图上的信息栏（不是玻璃，只是压暗）。 */
    public static final int COLUMN_SCRIM = 0xB8060A0D;

    /** 硬投影（无模糊，只向下偏 2px）：把卡片钉在暗面上。 */
    public static final int SHADOW_1 = 0x66000000;
    public static final int SHADOW_2 = 0x47000000;
    public static final int SHADOW_3 = 0x2E000000;

    // ------------------------------------------------------------------
    // 描边（Strokes）
    // ------------------------------------------------------------------

    /** 发丝线：卡片、表格、分隔线的主描边。 */
    public static final int HAIRLINE = 0x22FFFFFF;

    /** 更强的描边（悬停、聚焦、分组框）。 */
    public static final int HAIRLINE_STRONG = 0x3DFFFFFF;

    /** 顶面内高光（很轻，只是让卡片"有厚度"）。 */
    public static final int SHEEN = 0x0FFFFFFF;

    /** 底部压暗线。 */
    public static final int HAIRLINE_DARK = 0x66000000;

    /** 网格线：技术图纸的底纹。 */
    public static final int GRID = 0x10FFFFFF;
    /** 网格主线（每 5 格一条）。 */
    public static final int GRID_MAJOR = 0x1CFFFFFF;
    /** 角标 / 刻度的颜色。 */
    public static final int TICK = 0xFF6E757E;

    // ------------------------------------------------------------------
    // 文字（Ink）
    // ------------------------------------------------------------------

    /** 正文（冷白）。 */
    public static final int TEXT = 0xFFF2F5F8;
    /** 次要文字。 */
    public static final int TEXT_SECOND = 0xFFB4BCC6;
    /** 三级文字（字段名、单位、注释）。 */
    public static final int TEXT_THIRD = 0xFF868E99;
    /** 禁用。 */
    public static final int TEXT_DISABLED = 0xFF565E68;
    /** 压在信号黄上的字（近黑）。 */
    public static final int TEXT_ON_ACCENT = 0xFF0A0C0E;

    /** 高对比实心块（主按钮、悬停反相）；暗色下就是"亮块"。 */
    public static final int INK_FILL = 0xFFF2F5F8;
    /** 压在高对比块上的字。 */
    public static final int ON_INK = 0xFF0A0C0E;
    /** 压在高对比块上的次级字。 */
    public static final int ON_INK_SECOND = 0xFF646C77;

    // ------------------------------------------------------------------
    // 强调色与语义色
    // ------------------------------------------------------------------

    /** 信号黄：交互、选中、焦点、强调条。大面积铺色只用于"当前项"。 */
    public static final int ACCENT = 0xFFFFF500;
    /** 加深一档（悬停、按下、描边）。 */
    public static final int ACCENT_DIM = 0xFFE0D700;
    /** 兼容名：浅色主题时期"纸上可读的深黄"，暗色下等同 ACCENT。 */
    public static final int ACCENT_ON_PAPER = 0xFFFFF500;
    /** 半透明信号底色块。 */
    public static final int ACCENT_SOFT = 0x2EFFF500;
    /** 强调描边（1px）。 */
    public static final int ACCENT_LINE = 0xFFFFF500;
    /** 强调"光环"：用于选中外框的第二圈，很轻。 */
    public static final int ACCENT_GLOW = 0x33FFF500;

    /** 暖强调（标题、数值、板块强调线）：比纯信号黄柔和的暖黄。 */
    public static final int GOLD = 0xFFFFE066;
    public static final int GOLD_SOFT = 0x2EFFE066;
    public static final int GOLD_LINE = 0x66FFE066;

    /** 语义色（暗底可读档）。 */
    public static final int ATTACK = 0xFFFF6B5E;
    public static final int DEFEND = 0xFF5C8CFF;
    public static final int POSITIVE = 0xFF57D38C;
    public static final int WARNING = 0xFFFFC24B;
    public static final int NEGATIVE = 0xFFFF5C5C;
    public static final int PURPLE = 0xFFC08CFF;
    public static final int SQUAD = 0xFF67A7FF;

    public static final int ATTACK_SOFT = 0x26FF6B5E;
    public static final int DEFEND_SOFT = 0x265C8CFF;
    public static final int POSITIVE_SOFT = 0x2657D38C;
    public static final int WARNING_SOFT = 0x26FFC24B;
    public static final int NEGATIVE_SOFT = 0x26FF5C5C;

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

    /** 取反色（用于强调色底上的文字）。 */
    public static int contrastText(int background) {
        int r = (background >>> 16) & 0xFF;
        int g = (background >>> 8) & 0xFF;
        int b = background & 0xFF;
        double luminance = (0.2126 * r + 0.7152 * g + 0.0722 * b) / 255.0;
        return luminance > 0.55 ? TEXT : ON_INK;
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
