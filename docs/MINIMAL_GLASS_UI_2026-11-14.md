# 模组全套 UI 简约毛玻璃改版 · 2026-11-14

将已确认的菜单风格应用到 Espetro 自有界面、共用控件和 HUD。保留现有布局、交互、网络与服务端业务逻辑；使用原生 AUI 控件树，FancyMenu 提供客户端实时背景模糊。

## 覆盖

- 22 个直接继承 AUI 菜单的类（含基类及队伍密码子页）统一背景和控件；37 个 UI 源文件涉及本次迁移。
- 部署、职业、变体、技能、小队、地图投票与揭晓、阵营、指挥官、分数板及结算采用统一无边框透明层。
- FOB/载具/据点补给、教学、工事进度、快捷栏、名称输入框、滚动列表和轮盘可控参数统一风格。
- 亮暗指令及设置持久化保留，普通按钮使用透明底，悬停和选择仅改变底色强度。
- TN 天青色、加载页面无渐变黑幕及原版进度条约定保留。本次没有重新写入已完成的 FancyMenu 菜单配置。

EsRadial/AuraTip 自绘内部仍属于各自前置，仅协调其开放参数。独立 EsPoints 不在本 jar 构建范围。详见 [风格指南](ui-style-guide.md) 和 [FancyMenu API 核验](FANCYMENU_GLASS_BRIDGE_API_AUDIT_2026-11-14.md)。

## 备份与回滚

完整备份目录：

`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\espetro-backups\20261114-011106-before-mod-glass-ui`

`existing-ui.zip` 共 925 文件，内含 SHA256 清单；原客户端 jar 同时单独保存在此目录。具体恢复方法见该目录 `README.md`。核心源码与原风格指南快照另存于 `docs/archive/end02-before-minimal-glass-2026-11-14/`。

## 构建与验证

使用 JDK 21、Forge 1.20.1，离线构建并发布至本机 Maven。FancyMenu 声明为客户端必需前置，范围 `[3.9.9,4)`；测试实例现有 3.9.9 满足要求，服务端无需新增依赖。

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
$env:JDK_JAVA_OPTIONS='-Dnet.minecraftforge.gradle.check.certs=false'
& .\gradlew.bat test --tests '*UiThemeTest' --tests '*UiPaintTextTest' --tests '*ClientUiPreferencesTest' --tests '*FancyMenuGlassGeometryTest' --tests '*CurrentMapBackgroundRendererTest' --tests '*FobSupplyHudTest' --tests '*VanillaHudLayoutTest' --tests '*AuiScreenHostTest' build publishToMavenLocal --offline --no-daemon --max-workers=2 -I tools/ui-tests.init.gradle '-Papricityui_version=1.2.6' '-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar'
```

31 项相关测试通过，0 失败、0 错误。构建日志：`build/ui-minimal-glass-build.log`。初始化脚本限定 UI 相关测试源，避开仓库已有的移除 API 和旧分页断言问题，没有修改那些无关测试。

产物、SHA256、类差异、测试数量和部署结果统一记录于 [JSON 核验记录](MINIMAL_GLASS_UI_2026-11-14.json)。未启动实际游戏做 OpenGL 视觉验收；需要重启后核对部署页、地图投票、队伍密码框、补给 HUD、亮暗切换和窗口缩放。

## 部署结果

部署前确认 `NO_JAVA`，通过 `deploy-client-mods.mjs` 覆盖测试客户端的原文件名：

`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`

新 jar 大小 22927500 字节，客户端与 Maven 产物 SHA256 一致：

`c6b075c15a2d951577c51e75a114114a4e0cf03dbe60063a50b80377fbe0d083`

与备份 jar 比较，77 个已有客户端类变化、636 个已有类不变、4 个新增类；服务端类无变化。备份旧 jar 已保留，现有 297 项客户端配置与备份哈希一致。构建源码 UTF-8 解码及无 BOM 检查通过。
