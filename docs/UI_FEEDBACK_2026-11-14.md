# 截图反馈修复与内置菜单 · 2026-11-14

该轮构建已由 [启动修复版](UI_STARTUP_FIX_2026-11-14.md) 更新，当前成品哈希与启动验证以修复记录为准。

## 五项调整

1. 实际选中职业使用黄色实底、深墨图标和文字；每行分类图标使用黄色。专用选择态不经过玻璃底透明度限制。
2. 载具补给的弹药与建材分段同为 5px 厚度，去除弹药和建材图标的方形底板。
3. 所有轮盘环底固定半透明黑色，不随亮暗主题变白。通过 Espetro 内置客户端 mixin 替换 EsRadial 0.3.2 的硬编码白色 hover 与空白扇区底，不需要修改外部 jar。
4. FOB、载具与前哨继续使用原有 `ammo_supply.png` / `construction_supply.png` 素材，不替换图案。FOB 使用高分辨率原图采样缓存，保留原来的事件标脏、资源重载和释放生命周期；去除额外方形底板。
5. 投票与揭晓页共用居中主副标题；候选紧凑居中，末行独立居中；底部五段轨道按“地图投票 → 指挥官投票 → 进攻方投票 → 防守方投票 → 投票结果公示”排列，倒计时位于其下。高亮与完成态依据真实阶段及已确定编制数据，不改变 AAS/RAAS 的实际先手流程，不伪造耗时。

## 菜单随 jar 分发

内置 283 个 FancyMenu 配置及素材，约 199 MB 原始数据，位于 `assets/espetro/ui-bundle/`。其中包括标题页、加载页、原版菜单皮肤和背景轮播。保留 TN 天青色、标题页左侧渐隐黑幕、加载页无黑幕及原版进度条。

菜单由已有 `EspetroMixinPlugin.onLoad()` 的 CLIENT 分支早期安装，正常客户端初始化提供幂等重试。旧的 FancyMenu 主类注入因提前类加载冲突已移除；详见 [启动修复](UI_STARTUP_FIX_2026-11-14.md)。服务端不执行菜单文件写入。

`BundledMenuInstaller` 先核对清单与各文件 SHA256。只对缺失/变化文件写入；所有新内容先暂存并验证，通过后备份旧文件再原子替换。替换过程中失败会尝试回滚已提交文件。已有相同内容不重复写入。只管理清单列出的菜单文件，不覆盖 `options.txt`、用户变量数据库或编辑器个人设置。

被覆盖的旧文件保存在实例 `espetro-backups/<时间>-menu-auto-<唯一编号>/config/fancymenu/`。新版启动会修复外部菜单改动；要永久改变内置风格，应更新 jar 内资源及清单。

更新源资源的方法：

```powershell
python tools/package-client-menus.py --client 'D:\minecraft\squadMC预发布测试\versions\Squad预发布测试'
```

该命令仅更新仓库中的资源快照，不改写客户端。正常 Gradle 构建直接使用仓库资源，不要求本机存在测试实例。

## 备份与验证

修改前备份：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\espetro-backups\20261114-022133-before-ui-feedback`。

`build`、`publishToMavenLocal` 成功，38 项相关测试通过。4 项安装器测试覆盖旧文件备份、个人选项保留、重复启动不读 payload、缺失或改动文件恢复、损坏资源不覆盖原文件、路径越界与重复条目拒绝。其余 34 项覆盖主题、对比度、文本、模糊变换、AUI、HUD 与原图缩放的透明边缘行为。布局做了 36 组尺寸/候选组合和 126 个卡片边界静态核对；尚未启动实际游戏做视觉和 mixin 运行验收。

jar ZIP 完整性通过；283 个内置文件哈希及 24 个本地素材引用完整；原备份 961 文件的哈希匹配。相对于本轮前客户端 jar，43 个已有客户端类变化，服务端类不变。详细清单、测试数量、SHA256 与部署状态见 [JSON 记录](UI_FEEDBACK_2026-11-14.json)。

日志：`build/ui-feedback-build.log`。

## 最终部署

已在 Minecraft 关闭且目标 jar 可独占访问时，通过部署工具覆盖 `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`。与 Maven 产物哈希一致：

`0c697bc99f18e85c3ac04151e3b9e32a6615e8ab444942173fcdfe6d9cd4a7f3`

最终大小 217336328 字节。FOB 面板加宽至 120 GUI 像素，14px 原素材图标与两列五位数保持间距。原两张 PNG 已与本轮前备份逐字节对比一致。额外缩放测试日志为 `build/ui-feedback-tests.log`。本次没有启动实际客户端验收。
