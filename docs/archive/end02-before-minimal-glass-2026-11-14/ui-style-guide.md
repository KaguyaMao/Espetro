# Espetro UI — END-02 终末地工业风 / 亮暗双模式

参照《明日方舟：终末地》的工业终端视觉：纸白或石墨底、直角卡片、细线、编号与信号黄。
上一版固定暗色的令牌、原语、控件和指南已保存到 `docs/archive/endfield-dark-v1/`。
更早的 Tactical Glass 版本保留在 `docs/archive/tactical-glass/`。
视觉参考：[终末地官方网站](https://endfield.gryphline.com/en-us)。

## 使用

| 客户端指令 | 功能 |
| --- | --- |
| `/esui theme light` | 纸白亮色 |
| `/esui theme dark` | 石墨暗色（默认） |
| `/esui theme toggle` | 亮暗互换 |
| `/esui` 或 `/esui theme` | 当前主题与用法 |

指令无需 OP，仅影响自己的客户端。切换立即生效；通过聊天返回原菜单时，首帧重建带颜色的控件。
设置保存于游戏实例的 `config/espetro-ui.properties`（`theme=light` 或 `theme=dark`），客户端启动时载入。
非法、损坏或无法读取的设置回退暗色并写日志。保存失败时当前主题仍生效，聊天提示无法保存。
安装新版模组 jar 需要重启客户端；此后的主题切换不需要 `/reload` 或重启。

## 令牌与视觉

所有 ARGB 色值只放在 `client/gui/UiTheme.java`，绘制层允许 alpha/通道位运算掩码。
亮暗模式共用几何、字号与布局；`setMode()` 更新语义色并递增 `revision()`。
不要在其他类的 `static final` 字段或数组中缓存主题颜色。

| 语义 | LIGHT 亮色 | DARK 暗色 |
| --- | --- | --- |
| 页底 `BACKDROP_TOP/BOTTOM` | #F2F2EC | #111410 |
| 容器 `SURFACE_0` | #F0F0E9 | #191D18 |
| 卡片 `CARD_TOP/BOTTOM` | #FAFAF4 | #22271F |
| 悬停 `SURFACE_2` | #E8E9DF | #2D3329 |
| 选中底 `SURFACE_3` | #DCDDD2 | #3B4235 |
| 凹槽 `SUNKEN` | #E0E1D8 | #10140F |
| 正文 `TEXT` | #171A16 | #F0F2E8 |
| 次级 `TEXT_SECOND` | #4B5046 | #C0C6B8 |
| 微标签 `TEXT_THIRD` | #62675C | #9CA591 |
| 反相块 `INK_FILL / ON_INK` | 深墨 / 纸白 | 纸白 / 深墨 |
| 信号图形 `ACCENT` | #FFE500 | #FFE500 |
| 强调文字 `GOLD` | #6B5C00 | #FFE579 |
| 攻 / 守 | #B23E33 / #2E58A6 | #FF6B5E / #5C8CFF |
| 成功 / 警告 / 错误 | #256B42 / #865500 / #B22E32 | #57D38C / #FFC24B / #FF5C5C |

- 平涂表面与短硬投影；不用渐变高光制造玻璃反射。
- `RADIUS_* = 0`、`CHAMFER_* = 0`；细框 1px、强调条 3px、卡片内边距 12px。
- 字号：微标签 0.8、正文 1.0、小标题 1.1、标题 1.25、数值 1.5。
- 信号黄作图形和选中块，亮底强调文字必须用 `GOLD`。`readableText()` 也会保护原语里传入的黄色文字。
- `contrastText()` 根据实际底色选择固定深墨或纸白，不随当前模式错误反转。
- `WORLD_*` 用于世界名牌、地图标记、原版暗底提示等独立于页面底色的内容。
- 白色遮罩图标随文字染色；需要保留原图颜色的图标放在固定 `ICON_PLATE` 上。

## 绘制与控件契约

`UiTheme` 是唯一配色源。`UiPaint` 只读令牌，不判断模式；保留卡片、凹槽、框线、角标、刻度、进度和文本原语。
`EspetroAuiWidgets` 保留原工厂、构造器、setter 与颜色别名；旧别名通过主题变更监听刷新。

普通按钮使用细边与实色层，悬停有黄色边线，选中用黄色块和深墨字；禁用用凹槽底与弱字。
主按钮使用反相块，悬停变黄色且保持深字。语义操作使用淡语义色底，避免同色字压同色实底。
输入框采用直角凹槽与焦点描边，保留原版输入行为。

旧数据文案仍可能携带 Minecraft 色码，必须经过 `UiPaint.themed()` / 文本原语：
颜色转为当前语义令牌，保留粗体等格式，重置回传入基色；带字距文本也解析格式，不能逐字画出色码。
原版聊天和原版 tooltip 保持其固定深底的颜色体系。

## 覆盖与缓存

- 阶段页、主城、队伍、攻守/地图/编制选择、揭晓、部署、职业、载具、指挥官投票、分数板与结算共享亮暗色板。
- `AuiScreen` 在主题版本变化后的首帧重建控件树；不重新 `init()`，密码输入框对象与输入值保留。
- 战术部署页的静态颜色缓存改为即时读取，职业、变体、小队、治理弹窗走统一文字原语。
- 补给、兵力、教程、工事进度 HUD 读实时令牌；FOB 位图缓存跟随主题版本失效。
- 地图照片与模糊世界底图用 `FROST_SCRIM` 做纸白/石墨处理。同步读回最多每 2500ms 一次，失败也节流；失败只画遮罩，不画占位网格。
- 各轮盘槽/环/图标传入颜色由令牌提供，主题版本变化时刷新缓存。

### 外部绘制边界

EsRadial 前置的中心、返回按钮、面包屑和标签 CSS 由 EsRadial 自绘，标签内容被其转成纯字符串，
无法从 Espetro 完整替换为亮色。为保持它的固定白字可读，轮盘环底使用固定深色；本模组可控的强调色、图标色和槽状态跟随令牌。
`com/example/hcrpoints/**` 被本 jar 排除，属于独立 EsPoints，修改它需要在对应项目构建。
原版 Minecraft 菜单与第三方模组界面不属于此模组的控件树。

### FancyMenu 主菜单 · 2026-11-14

预发布客户端的标题页与普通加载页使用 `design/fancymenu-glass-2026-11-14/` 中的独立资源；此前简约版保存在 `design/fancymenu-simple-2026-11-14/`。
颜色从本文件对应的 `UiTheme.java` 暗色令牌提取。菜单按钮按用户要求采用无边框半透明毛玻璃，保持直角和暖白文字；悬停仅提高底色透明度。
主菜单左侧全高黑幕向右渐隐，不设独立外框、切角、箭头或科技感装饰；Logo 使用简化轮廓，文字为暖白，TN 标保留品牌天青色 `#06DEE5`。
FancyMenu 的每个原生按钮下方配置同尺寸的矩形 `shape`，开启 `blur_enabled` 和 4.0 模糊半径，避免以普通透明贴图冒充背景模糊。
加载页只保留实景背景和天青色 TN Logo，不加渐变黑幕或毛玻璃底托；移除 `progress_bar` 自定义记录，使用原生进度条的尺寸、位置及绘制。
FancyMenu 资源固定为暗色，不跟随 `/esui theme`；游戏内控件仍按原指令切换亮暗。
标题页和加载页的 `layout-meta` 必须明确设置 `render_custom_elements_behind_vanilla = true`，
保证全屏黑幕在原生按钮后方。配置解析器必须支持容器名中的连字符，并在部署前验证实际写出的字段。
保留原按钮 ID/动作、实景轮播和真实加载进度，不修改启动视频或加载时序。

### 原版菜单扩展 · 2026-11-14

配置源为 `design/vanilla-menus-2026-11-14/`，覆盖 74 个原版菜单及 Forge/Embeddium 的相关入口。
使用按屏幕限定的 `custom_button` 模板分别匹配原生 buttons/sliders，无边框半透明暖白底、悬停提亮；不改全局皮肤。
模板不覆盖宽高、位置、文字、可见性或启用状态，动作列表为空，继续使用原生按钮和滑块操作。
每个屏幕的 world/menu 布局通过 `IsWorldLoadedRequirement` 互斥：菜单外沿用实景轮播，世界内保留当前画面。
单页用一个全屏矩形做实时背景模糊与暗色底，始终在原生控件后方绘制。
标题页和加载页保持当前独立配置；聊天、背包/容器、HUD 不注册此背景。
原生输入框、页签、列表和 Embeddium 自绘控件保留各自绘制；仅其背景和原生按钮/滑块按此样式统一。

## 验证

自动验证覆盖两套模式的正文/次级文字对比度、反相与黄底文字、重复切换和全部动态颜色恢复；
覆盖配置缺失、大小写、非法值、损坏内容、不可读路径、保存与失败清理。
构建参数：JDK 21，离线，`-Papricityui_version=1.2.6`，
`-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar`。

游戏内仍需实景核对：J 部署页、地图投票、组队密码框、FOB/载具 HUD，分别执行 light/dark/toggle，
检查聊天返回、窗口缩放和资源重载；实际客户端画面未作为自动测试结果。

### 2026-10-19 验证记录

- `compileJava`、`build`、`publishToMavenLocal` 成功；构建日志 `build/ui-final-build.log`。
- 最终限定 UI 套件：32 项通过，0 失败；其中新增主题/格式/配置 17 项。
- jar ZIP 完整性及命令、配置、主题、屏幕主类打包检查通过，ApricityUI 元数据为 1.2.6。
- 可安装产物：`build/libs/espetro-1.1.3-l.jar`，未向运行中的 Minecraft 实例部署。
- 此次未启动游戏做实景截图验收。

现有全量测试的两类问题未修改：
`VehicleSeatAccessPolicyTest` 引用已经移除的 `legacyVehicleCrewSeatCount`，阻断全量测试编译；
`ResupplyRadialControllerTest` 两个旧分页断言按 5 项分页，而现有实现 `PAGE_SIZE=512` 为单页。
首次扩展回归为 35 项中 33 项通过、上述两项失败；最终通过的 32 项不包含该旧分页测试类。

重现限定验证可在离线构建参数前添加 `-I tools/ui-tests.init.gradle`，
并使用 `test --tests '*UiThemeTest' --tests '*UiPaintTextTest' --tests '*ClientUiPreferencesTest'`
运行新增套件。初始化脚本只对显式调用生效，不改变正常项目配置；
它限定 UI 测试源，并关闭本机被 Windows 内存映射锁住的 Gradle 增量快照写入。
