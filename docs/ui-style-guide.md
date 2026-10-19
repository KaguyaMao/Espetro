# Espetro UI 风格指南 — 「终末地」工业编辑风（Endfield Editorial）

> 旧版「战术玻璃 Tactical Glass」已归档：tag `ui/tactical-glass-v1`，源码副本见
> `docs/archive/tactical-glass/`。本文件只描述当前版本。

## 1. 设计语言

参照《明日方舟：终末地》的宣传物料 / 界面稿：**纸白底 + 纯黑墨字 + 信号黄**，
直角、发丝线、编号、刻度、贴边排版。整体像一份「作战文件」而不是游戏 HUD。

| 原则 | 做法 |
| --- | --- |
| 纸面优先 | 全屏底是米白纸渐变（`BACKDROP_TOP→BOTTOM`）+ 24px 方格 + 纸纤维噪点；不再有大面积深色蒙层 |
| 墨字 | 正文 `TEXT`(#101110)、次级 `TEXT_SECOND`(#4A4C48)、微标签 `TEXT_THIRD`(#7C7E78) |
| 信号黄只做信号 | `ACCENT`(#FFF500) 只用于 3–6px 的方块、边线、选中条、进度条；**不做大面积底色、不做正文颜色** |
| 直角 | `RADIUS_*`/`CHAMFER_*` 全为 0；只有圆形进度、圆点、环形菜单允许圆弧 |
| 发丝线 | 1px `HAIRLINE`(#D5D6D0)/`HAIRLINE_STRONG`(#9A9C96) 分隔一切；表格化、贴边 |
| 编号与刻度 | 微标签带 1px 字距，用 `01 / HUB`、`END-01`、`CH 07` 这类编号；屏幕四周有图框与刻度尺 |
| 排版层级 | 微标签 0.8 → 正文 1.0 → 小标题 1.1 → 标题 1.25 → 数字 1.5 |
| 硬阴影 | 卡片右侧/下方 3px 实心投影（`UiPaint.card`），不是高斯模糊 |

## 2. 令牌（`org.espetro.client.gui.UiTheme`）

- 底色：`BACKDROP_TOP #E9E9E3` / `BACKDROP_BOTTOM #E3E4DD`（带 alpha 的 ARGB）
- 面：`SURFACE_0`(纸) / `CARD_TOP` #F5F5EF → `CARD_BOTTOM` #EDEDE7 / `SURFACE_2` #FAFAF4 / `SURFACE_3` #DCDDD6（凹槽）/ `SUNKEN` #E2E3DC
- 墨：`TEXT` / `TEXT_SECOND` / `TEXT_THIRD` / `TEXT_DISABLED` / `INK_FILL` #141513 / `ON_INK` #F5F5F0
- 黄：`ACCENT` #FFF500 / `ACCENT_DIM` / `ACCENT_ON_PAPER` #6B5D00（纸上可读的深黄）/ `ACCENT_SOFT`(33 alpha) / `ACCENT_LINE` / `ACCENT_GLOW`
- 语义：`ATTACK` #B3352A / `DEFEND` #1F5FA8 / `POSITIVE` #2F7A3E / `WARNING` #A8611A / `NEGATIVE` #B32626 / `PURPLE` #6B4FA8 / `SQUAD` #2B6CB0 / `GOLD` = `ACCENT_ON_PAPER`
- 度量：`ACCENT_BAR` 3、`TICK_LENGTH` 6、`PAD_CARD` 12、`CONTROL_HEIGHT` 20、`TYPE_*`、`LETTERSPACING_MICRO` 1
- 动效：`MOTION_HOVER_MS` 90 / `MOTION_SELECT_MS` 120 / `MOTION_ENTER_MS` 160

## 3. 绘制原语（`UiPaint`）

`chamfer` 系列（切角矩形，radius>0 时按 45° 切角而非圆弧）、`card`（硬投影+纸面+发丝边）、
`accentCard`、`sunken`、`hairline`、`hairlineV`、`divider`、`frame`（图框）、`brackets`（四角 L 标）、
`ticksH/ticksV`（刻度尺）、`grid`、`hatch`、`progress`（方块+刻度）、`glow`（黄框微光）、
`backdrop`、`vignette`、`grain`、`label`（微标签）、`text/textCentered/textRight/textSpaced`。

> `retone()`：把旧文案里的 `§f/§e/§b/§d` 重映射为纸面可读的 `§0/§8/§9/§5`，
> 所有 `UiPaint.text*` 入口自动调用，历史界面无需逐句改文案。

## 4. 控件（`EspetroAuiWidgets`）

- `panel/card`：纸面 + 发丝边 + 可选强调条（顶部 2px 黄或左侧 3px 黄）
- `button`：默认透明底 + `HAIRLINE_STRONG` 边；悬停 → 墨色实心块 + 纸色字 + 底边 2px 黄；选中 → 信号黄块 + 墨字
- `primaryButton`：墨块 + 纸字，悬停变黄块
- `chip`：1px 边 + 语义色小方块 + 微标签
- `stat`：上微标签、下大号数字
- `bar`：4px 凹槽 + 方块填充 + 右上 1px 刻度
- `sectionLabel`：微标签 + 右侧渐隐细线
- `phase header`：左对齐 —— 左侧信号黄方块、第二行大标题、右上状态微标签、底部通栏发丝线
- `Text` 支持 `setAlignRight` / `setSpacing` / `setTextScale`

## 5. 屏幕骨架

所有阶段界面调用 `EspetroAuiWidgets.drawScreenShade()`：纸底 → 压暗 → 纸纤维 → `drawChrome()`。
`drawChrome` 画 5px 内缩图框、四角 L 标、左右刻度尺、左上信号方块 + `ESPETRO OPS`、
右上 `END-01`、左下 `CH 07 // SECURE`、右下模组版本号。**新界面不要再自己画全屏底**。

主城 `HubScreen` 是「浮在世界上的一张纸」：只画卡片硬投影，不压大面积黑幕。

## 6. 已知限制 / 待办

- `EsRadial` 轮盘（`RadioRadialController`、`TacticalRadialController`、`VehicleWheelController`）
  的可视样式由 **EsRadial 模组**自己绘制，本模组只提供图标与文案，暂不受本风格影响。
- `UnifiedDeployScreen` 内部仍有少量历史布局假设（三栏固定宽度、状态栏高度 13px），
  改版时优先调整列宽/留白而非重写布局。
- HUD 类（`FobSupplyHud`、`OutpostSupplyHud`、`VehicleSupplyHud`、`TutorialHudOverlay`）
  已切到纸卡；若压在亮色地形上仍嫌可读性不足，优先加 `sunken` 底而不是改字色。
