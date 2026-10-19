# Espetro UI 风格指南 —— Tactical Glass

所有界面共用一套「战术玻璃」设计语言：冷调深色半透明表面 + 1px 发丝描边 + 柔和阴影，
强调色只用于交互与状态，排版靠字号层次而不是靠颜色堆叠。

## 1. 令牌（唯一色源）

一律使用 `org.espetro.client.gui.UiTheme` 的常量，**禁止**在界面里新写 ARGB 字面量
（`0xFF123456`、`0x80191C1E` 之类）。允许的例外：把旧字面量替换成语义最接近的令牌。

| 用途 | 令牌 |
| --- | --- |
| 全屏底 上/下 | `BACKDROP_TOP` / `BACKDROP_BOTTOM` |
| 卡片 上/下渐变 | `CARD_TOP` / `CARD_BOTTOM` |
| 页面主容器 | `SURFACE_0` |
| 悬停 / 按下 | `SURFACE_2` / `SURFACE_3` |
| 内凹槽（列表底、进度槽） | `SUNKEN` |
| 阴影 近→远 | `SHADOW_1` / `SHADOW_2` / `SHADOW_3` |
| 发丝线 / 强描边 / 顶部高光 / 底部压暗 | `HAIRLINE` / `HAIRLINE_STRONG` / `SHEEN` / `HAIRLINE_DARK` |
| 文字 主/次/三级/禁用 | `TEXT` / `TEXT_SECOND` / `TEXT_THIRD` / `TEXT_DISABLED` |
| 强调（交互、选中） | `ACCENT`、`ACCENT_SOFT`、`ACCENT_LINE`、`ACCENT_GLOW` |
| 品牌金（标题、比分） | `GOLD`、`GOLD_SOFT`、`GOLD_LINE` |
| 语义 攻/守/正/警/负 | `ATTACK` / `DEFEND` / `POSITIVE` / `WARNING` / `NEGATIVE`（各有 `_SOFT`） |
| 其他 | `PURPLE`、`SQUAD` |
| 圆角 | `RADIUS_CARD`=6、`RADIUS_CONTROL`=4、`RADIUS_INNER`=2 |
| 间距 | `GAP_XS`=4、`GAP_S`=6、`GAP_M`=10、`GAP_L`=16、`PAD_CARD`=12 |
| 控件高度 | `CONTROL_HEIGHT`=20、`CONTROL_HEIGHT_S`=16 |
| 字号 | `TYPE_MICRO`=0.8、`TYPE_BODY`=1.0、`TYPE_HEADING`=1.1、`TYPE_TITLE`=1.25、`TYPE_NUMERAL`=1.5 |
| 字距 | `LETTERSPACING_MICRO`=1（只给拉丁微标签用，中文不加字距） |

工具：`UiTheme.lerp(a,b,t)`、`UiTheme.alpha(argb,f)`、`UiTheme.withAlpha(argb,0xAARRGGBB 的 alpha 通道值)`、
`UiTheme.contrastText(bg)`、`UiTheme.visible(argb)`。

## 2. 绘制原语 `UiPaint`

直角 `graphics.fill` 只能用于逐行/极细的辅助线；所有"面"都用原语：

- `roundRect(g,x,y,w,h,radius,color)` —— 圆角实心
- `roundRectGradient(g,x,y,w,h,radius,top,bottom)` —— 圆角渐变
- `strokeRoundRect(g,x,y,w,h,radius,color)` —— 1px 圆角描边
- `circle / ring(g,cx,cy,r,color)` —— 圆盘 / 圆环
- `shadow(g,x,y,w,h,radius)` —— 柔和外阴影（3 层）
- `card(g,x,y,w,h[,radius,top,bottom])` —— 标准玻璃卡片（阴影+渐变+发丝边+内高光+底压暗）
- `sunken(g,x,y,w,h,radius)` —— 内凹容器
- `hairline / divider / dividerV` —— 细线 / 水平渐变分割线 / 竖直分割线
- `accentBarLeft / accentBarBottom` —— 强调色条（上下/左右渐隐）
- `pill / pillOutline` —— 胶囊
- `progress(g,x,y,w,h,progress,fillColor)` —— 进度条（内凹槽+渐变填充+高光）
- `glow(g,x,y,w,h,radius,accent)` —— 选中发光圈
- `backdrop / vignette / grain / screenBackdrop` —— 屏幕级背景
- 文本：`text / textShadowed / textScaled / textCentered / textSpaced / spacedWidth`

## 3. 控件 `EspetroAuiWidgets`

旧 API（`panel/button/text/centeredText/textBlock/rect/drawScreenShade/addMutablePhaseHeader/
PhaseHeader/trimToWidth/drawScaledString/stripFormatting/teamColor/teamName/teamPrefix`）全部保留，
内部已按新风格绘制，**不需要改动调用方**。

新增（优先使用）：

- `card(x,y,w,h)` —— 玻璃卡片；`cardAccent(x,y,w,h,accent)` —— 左侧强调条
- `sunken(x,y,w,h)` —— 内凹容器
- `chip(x,y,label,accent)` —— 胶囊标签（状态、标签、计数）
- `stat(x,y,w,label,value,color)` —— 微标签 + 数值（数值自动加阴影）
- `bar(x,y,w,h,fillColor)` + `.setProgress(0..1)` —— 进度条
- `sectionLabel(x,y,w,label,accent)` —— 分区标题（微标签 + 渐隐横线）
- `divider(x,y,w)` / `dividerV(...)` —— 分割线元素
- `primaryButton(x,y,w,h,label,action)` —— 主行动按钮（强调色）
- `microLabel(x,y,w,text)` —— 微标签文本
- `Text.setTextScale(0.5..2.0)`、`setShadow(bool)`、`setSpacing(int)`；
  `Panel.setRadius / setAccentColor / setGlass / setSunken`；`ActionButton.setAccentColor`；
  `Chip.setSolid(bool)`、`TextBlock`、`ImageButton` 同上。

页头：`addPhaseHeader(...)` / `addMutablePhaseHeader(...)` 版式已升级（标题 1.25、状态 0.95、
说明微标签，居中，高 44）。

## 4. 迁移规则（逐屏应用）

1. **全屏底**：阶段界面在 `renderBeforeMenu` 里调用 `EspetroAuiWidgets.drawScreenShade(g,w,h)`
   （保持不透明，遮住世界）；需要透视世界的界面（如主城）不铺底，改为卡片周围柔和压暗。
2. **大底板**：`graphics.fill(...)` 满屏/大面板 → `UiPaint.roundRect` / `card` / `sunken`。
3. **按钮**：继续用 `button/primaryButton`，不要自己画填充；`setColors` 只在语义需要时覆盖，
   覆盖值也取自 `UiTheme`。
4. **状态**：文字状态（"等待中/已就绪/已锁定"）→ `chip`，颜色用语义色。
5. **数字**：比分、人数、票数、弹药 → `stat` 或 `TYPE_NUMERAL` + 阴影。
6. **分组**：区块之间加 `sectionLabel` 或 `divider`，间距取 `GAP_M` / `GAP_L`。
7. **列表项**：hover 用 `SURFACE_2`，选中用 `ACCENT_SOFT` + `ACCENT_LINE` 描边 + `UiPaint.glow`。
8. **描边**：别再 `graphics.renderOutline`（直角 1px）；用 `strokeRoundRect`。
9. **悬停反馈**：按钮/卡片 hover 只换色，不做位移，避免布局抖动。

## 5. 硬性约束

- 不改任何业务逻辑、网络包、状态机、公开方法签名；只改绘制与配色、以及在必要处微调内边距。
- 不删除既有控件或改变其位置语义；新增的装饰元素不得遮挡点击区域（放在背景层，先 addChild）。
- 文本必须保持可读：深底上的正文用 `TEXT`，次级用 `TEXT_SECOND`，禁用 `TEXT_DISABLED`。
- 中文标签不要加字距；`setSpacing` 只用于全大写拉丁微标签。
- 不新增依赖、不改 `build.gradle`、不跑 gradle（由主代理统一编译验证）。
