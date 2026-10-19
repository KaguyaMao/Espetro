package org.espetro.client.gui;

/**
 * Espetro UI design tokens — "Tactical Glass".
 *
 * <p>单一样式来源：所有界面的颜色、圆角、间距、字号、动效时长都必须从这里取，
 * 组件与页面上不再出现裸写的 ARGB 常量（语义色如阵营红/蓝也在此集中定义）。
 *
 * <p>设计语言要点：
 * <ul>
 *   <li><b>冷调深色玻璃</b>：表面是半透明的蓝黑色叠层，靠"内高光 + 细描边"分层，
 *       而不是靠更亮的底色；卡片有柔和外阴影，边缘 1px 发丝线。</li>
 *   <li><b>克制的强调色</b>：默认离子青（{@link #ACCENT}）只用于交互/选中，
 *       金色 {@link #GOLD} 保留为品牌色与比分数值，语义色仅表达状态。</li>
 *   <li><b>现代排版层次</b>：极小号全大写微标签（{@link #TYPE_MICRO}）→ 正文
 *       （{@link #TYPE_BODY}）→ 标题（{@link #TYPE_TITLE}）→ 大数值（{@link #TYPE_NUMERAL}），
 *       微标签带字距，数值带阴影。</li>
 *   <li><b>几何</b>：4px 网格，卡片圆角 6、按钮/标签圆角 4、内嵌元素圆角 2。</li>
 * </ul>
 */
public final class UiTheme {

    private UiTheme() {
    }

    // ------------------------------------------------------------------
    // 表面（Surfaces）：从最底的全屏底到最上层的浮起元素
    // ------------------------------------------------------------------

    /** 全屏渐变底：顶部略深、底部略亮，模拟环境光。 */
    public static final int BACKDROP_TOP = 0xF5070A0E;
    public static final int BACKDROP_BOTTOM = 0xF80C1118;

    /** 页面主容器（大卡片）。 */
    public static final int SURFACE_0 = 0xE60D1219;

    /** 标准卡片：顶部略亮 → 底部略暗的玻璃渐变。 */
    public static final int CARD_TOP = 0xE2131A24;
    public static final int CARD_BOTTOM = 0xE80C1118;

    /** 浮起/悬停态卡片。 */
    public static final int SURFACE_2 = 0xF01A2431;

    /** 按下态。 */
    public static final int SURFACE_3 = 0xF5222E3E;

    /** 内凹区域（列表底、输入框、进度槽）。 */
    public static final int SUNKEN = 0xB3080B10;

    /** 卡片柔和外阴影（多层叠加，由近到远）。 */
    public static final int SHADOW_1 = 0x30000000;
    public static final int SHADOW_2 = 0x1C000000;
    public static final int SHADOW_3 = 0x0E000000;

    // ------------------------------------------------------------------
    // 描边（Strokes）
    // ------------------------------------------------------------------

    /** 发丝线：卡片/分隔线的主描边。 */
    public static final int HAIRLINE = 0x16FFFFFF;

    /** 更强的描边（悬停、聚焦）。 */
    public static final int HAIRLINE_STRONG = 0x2EFFFFFF;

    /** 卡片顶部内高光。 */
    public static final int SHEEN = 0x14FFFFFF;

    /** 底部压暗线，让卡片"坐"在背景上。 */
    public static final int HAIRLINE_DARK = 0x66000000;

    // ------------------------------------------------------------------
    // 文字（Text）
    // ------------------------------------------------------------------

    public static final int TEXT = 0xFFF3F6FA;
    public static final int TEXT_SECOND = 0xFFA9B4C4;
    public static final int TEXT_THIRD = 0xFF6E7A8A;
    public static final int TEXT_DISABLED = 0xFF4C5666;
    public static final int TEXT_ON_ACCENT = 0xFF04121A;

    // ------------------------------------------------------------------
    // 强调色与语义色
    // ------------------------------------------------------------------

    /** 默认强调色：离子青。交互、选中、聚焦。 */
    public static final int ACCENT = 0xFF5AD7FF;
    public static final int ACCENT_DIM = 0xFF2E8AA6;
    public static final int ACCENT_SOFT = 0x2E5AD7FF;
    public static final int ACCENT_LINE = 0x8C5AD7FF;
    public static final int ACCENT_GLOW = 0x225AD7FF;

    /** 品牌金：标题、比分数值、奖励。 */
    public static final int GOLD = 0xFFF2C15E;
    public static final int GOLD_SOFT = 0x2EF2C15E;
    public static final int GOLD_LINE = 0x8CF2C15E;

    public static final int ATTACK = 0xFFFF6B5E;
    public static final int DEFEND = 0xFF5C8CFF;
    public static final int POSITIVE = 0xFF57D38C;
    public static final int WARNING = 0xFFFFC24B;
    public static final int NEGATIVE = 0xFFFF5C5C;
    public static final int PURPLE = 0xFFC08CFF;
    public static final int SQUAD = 0xFF67A7FF;

    public static final int ATTACK_SOFT = 0x2EFF6B5E;
    public static final int DEFEND_SOFT = 0x2E5C8CFF;
    public static final int POSITIVE_SOFT = 0x2E57D38C;
    public static final int WARNING_SOFT = 0x2EFFC24B;
    public static final int NEGATIVE_SOFT = 0x2EFF5C5C;

    // ------------------------------------------------------------------
    // 几何（Geometry）：4px 网格
    // ------------------------------------------------------------------

    /** 卡片圆角。 */
    public static final int RADIUS_CARD = 6;
    /** 按钮、标签圆角。 */
    public static final int RADIUS_CONTROL = 4;
    /** 内嵌小块圆角。 */
    public static final int RADIUS_INNER = 2;

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
    // 排版（Typography）：与原版字体 9px 行高配套的缩放档位
    // ------------------------------------------------------------------

    /** 微标签：全大写 + 字距，用于分组标题、字段名。 */
    public static final float TYPE_MICRO = 0.8f;
    /** 正文。 */
    public static final float TYPE_BODY = 1.0f;
    /** 小标题。 */
    public static final float TYPE_HEADING = 1.1f;
    /** 页面标题。 */
    public static final float TYPE_TITLE = 1.25f;
    /** 大数值（比分、人口、弹药）。 */
    public static final float TYPE_NUMERAL = 1.5f;

    /** 微标签字距（像素，仅在 1x 缩放下叠加）。 */
    public static final int LETTERSPACING_MICRO = 1;
    /** 正文轻微字距。 */
    public static final int LETTERSPACING_BODY = 0;

    // ------------------------------------------------------------------
    // 动效（Motion）
    // ------------------------------------------------------------------

    /** 悬停过渡时长。 */
    public static final long MOTION_HOVER_MS = 140L;
    /** 选中/切换过渡时长。 */
    public static final long MOTION_SELECT_MS = 180L;
    /** 面板入场时长。 */
    public static final long MOTION_ENTER_MS = 220L;

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
        return luminance > 0.6 ? 0xFF081018 : TEXT;
    }

    /** 是否具备可见 alpha（0 表示完全不画）。 */
    public static boolean visible(int argb) {
        return (argb & 0xFF000000) != 0;
    }

    /** 强调色"环境光"洗色：给全屏底叠一层极淡的青色光，让底不再死黑。 */
    public static int accentWash(float strength) {
        return withAlpha(ACCENT, Math.round(255f * Math.max(0f, Math.min(1f, strength))));
    }
}
