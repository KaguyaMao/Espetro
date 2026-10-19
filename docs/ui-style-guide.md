# Espetro UI 风格指南 — 「终末地」工业编辑风 · 暗色（Endfield Editorial / Dark）

> 历史版本：`ui/tactical-glass-v1`（深色玻璃，已归档 `docs/archive/tactical-glass/`）；
> 纸白版见提交 `f38fe650`。本文件描述当前**暗色**版本。

## 1. 设计语言

参照《明日方舟：终末地》：**黑玻璃底 + 冷白字 + 信号黄**，直角、发丝线、编号、刻度、贴边排版。
整体像一台作战终端上的工程文件，而不是游戏 HUD；结构（图框、页头、卡片、按钮语义）
完全沿用纸白版，只把"纸/墨"换成"黑玻璃/冷白"。

| 原则 | 做法 |
| --- | --- |
| 暗面优先 | 全屏底是近黑渐变（`BACKDROP_TOP→BOTTOM`）+ 24px 白色方格 + 噪点；卡片是更亮一档的黑玻璃 |
| 冷白文字 | 正文 `TEXT`(#F2F5F8)、次级 `TEXT_SECOND`(#B4BCC6)、微标签 `TEXT_THIRD`(#868E99) |
| 信号黄只做信号 | `ACCENT`(#FFF500) 只用于 3–6px 方块、边线、选中条、进度块；不做大面积底色 |
| 直角 | `RADIUS_*`/`CHAMFER_*` 全为 0；只有圆点、环形菜单允许圆弧 |
| 发丝线 | `HAIRLINE`(14% 白)/`HAIRLINE_STRONG`(24% 白) 分隔一切；表格化、贴边 |
| 编号与刻度 | 微标签带 1px 字距；`01 / HUB`、`END-01`、`CH 07`；屏幕四周有图框与刻度尺 |
| 排版层级 | 微标签 0.8 → 正文 1.0 → 小标题 1.1 → 标题 1.25 → 数字 1.5 |
| 硬阴影 | 卡片右下 3px 实心黑影（`UiPaint.card`），不是高斯模糊 |

## 2. 令牌（`org.espetro.client.gui.UiTheme`）

- 底：`BACKDROP_TOP` #0A0C0E / `BACKDROP_BOTTOM` #111418（带 alpha）
- 面：`SURFACE_0` #0D1013 / `CARD_TOP` #171B20 → `CARD_BOTTOM` #101419 / `SURFACE_2` #1E242A / `SURFACE_3` #2A3138 / `SUNKEN` #07090B
- 字：`TEXT` #F2F5F8 / `TEXT_SECOND` #B4BCC6 / `TEXT_THIRD` #868E99 / `TEXT_DISABLED` #565E68
- 反相块：`INK_FILL` #F2F5F8 + `ON_INK` #0A0C0E（主按钮、悬停反相、选中条）
- 黄：`ACCENT` #FFF500 / `ACCENT_DIM` / `ACCENT_SOFT`(18% ) / `ACCENT_LINE` / `ACCENT_GLOW`
- 暖强调：`GOLD` #FFE066（标题、数值、板块强调线 `GOLD_LINE`）
- 语义：`ATTACK` #FF6B5E / `DEFEND` #5C8CFF / `POSITIVE` #57D38C / `WARNING` #FFC24B / `NEGATIVE` #FF5C5C / `PURPLE` #C08CFF / `SQUAD` #67A7FF
- 度量：`ACCENT_BAR` 3、`TICK_LENGTH` 6、`PAD_CARD` 12、`CONTROL_HEIGHT` 20、`TYPE_*`、`LETTERSPACING_MICRO` 1
- 动效：`MOTION_HOVER_MS` 90 / `MOTION_SELECT_MS` 120 / `MOTION_ENTER_MS` 160

## 3. 绘制原语（`UiPaint`）

`chamfer` 系列（切角矩形，radius>0 时 45° 切角而非圆弧）、`card`（硬投影+黑玻璃+发丝边）、
`accentCard`、`sunken`、`hairline`、`hairlineV`、`divider`、`frame`（图框）、`brackets`（四角 L 标）、
`ticksH/ticksV`（刻度尺）、`grid`、`hatch`、`progress`（方块+刻度）、`glow`（黄框微光）、
`backdrop`、`vignette`、`grain`、`label`（微标签）、`text/textCentered/textRight/textSpaced`。

> `retone()` 是文案色码重映射钩子：暗色下 Minecraft 原生色码（`§f` 白、`§7` 灰、`§e` 黄…）
> 本身适合深底，因此当前为恒等映射；将来若再换浅色主题，只改这里一张映射表即可。

## 4. 控件（`EspetroAuiWidgets`）

- `panel/card`：黑玻璃 + 发丝边 + 可选强调条（顶部 2px 黄或左侧 3px 黄）
- `button`：默认透明底 + 24% 白发丝边；悬停 → 亮实心块 + 深色字 + 底边 2px 黄；选中 → 信号黄块 + 深色字
- `primaryButton`：亮实心块 + 深色字，悬停变黄块
- `chip`：1px 边 + 语义色小方块 + 微标签
- `stat`：上微标签、下大号数字
- `bar`：4px 凹槽 + 方块填充 + 右上 1px 刻度
- `sectionLabel`：微标签 + 右侧渐隐细线
- `phase header`：左对齐 —— 左侧信号黄方块、第二行大标题、右上状态微标签、底部通栏发丝线
- `Text` 支持 `setAlignRight` / `setSpacing` / `setTextScale`

## 5. 屏幕骨架

所有阶段界面调用 `EspetroAuiWidgets.drawScreenShade()`：黑玻璃底 → 压暗 → 噪点 → `drawChrome()`。
`drawChrome` 画 5px 内缩图框、四角 L 标、左右刻度尺、左上信号方块 + `ESPETRO OPS`、
右上 `END-01`、左下 `CH 07 // SECURE`、右下模组版本号。**新界面不要再自己画全屏底**。

主城 `HubScreen` 是「浮在世界上的一块黑玻璃」：只画卡片硬投影，不压大面积黑幕。

### 5.1 战术面板：毛玻璃视野底

`UnifiedDeployScreen` 不再铺战术地图照片，改为 **截屏降采样模糊**：

- `FrostedBackdrop.draw()` 在界面内容之前调用，此时主帧缓冲里只有世界画面；
- 每秒最多一次 `glReadPixels` 读主目标，CPU 块平均到 256×144（同时上下翻转），
  上传 `DynamicTexture` 后用双线性放大铺满屏幕 → 近似高斯模糊；
- 之上再压一层 `UiTheme.FROST_SCRIM`（`0x9A07090B`）和 3% 颗粒；
- 图框由 `UnifiedDeployScreen#renderTerminalFrame` 画（细框 + 四角 L 标 + 侧边刻度 + 信号黄方块）。

**新界面不要每帧读回帧缓冲**：`FrostedBackdrop` 有 1s 节流，只有需要"透出世界"的页面才用。
半透明分层令牌：`GLASS_PANEL`（板块/按钮 55%）、`GLASS_PANEL_SOFT`（输入框 36%）、`COLUMN_SCRIM`（90% 压暗）。
卡片 `UiPaint.card()` 是**纯色平涂**（无渐变、无高光），不要再往卡片里叠半透明渐变，否则会重新出现"玻璃感"。

## 6. 已知限制 / 待办

- `EsRadial` 轮盘（`RadioRadialController`、`TacticalRadialController`、`VehicleWheelController`）
  的样式由 **EsRadial 模组**自己绘制，本模组只提供图标与文案，暂不受本风格影响。
- `UnifiedDeployScreen` 内部仍有历史布局假设（三栏固定宽度、状态栏 13px），改版优先调列宽/留白。
- HUD 类（`FobSupplyHud`、`OutpostSupplyHud`、`VehicleSupplyHud`、`TutorialHudOverlay`）
  已切到黑玻璃卡；若压在亮色地形（雪地/沙漠）上可读性不足，优先加 `sunken` 底而不是改字色。
