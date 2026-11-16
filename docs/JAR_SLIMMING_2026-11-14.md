# Espetro jar 资源瘦身 · 2026-11-14

已构建并部署到 `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`。

| 项目 | 结果 |
| --- | --- |
| 原大小 | 217,349,297 字节（217.35 MB） |
| 新大小 | 151,983,321 字节（151.98 MB） |
| 减少 | 65,365,976 字节，约 30.1% |
| 移除 | 86 个未引用菜单资源 |
| PNG 无损压缩 | 21 张，逐张比较解码后 RGBA 像素和尺寸，完全相同 |
| 代码 | 734 个运行时 class 与旧 jar 字节完全相同 |
| 菜单资源 | 197 个，清单大小/哈希及所有本地路径引用检查通过 |
| 回归检查 | BundledMenuInstallerTest 4 项通过；jar CRC 通过 |

原 jar 未包含备份压缩包、日志或 PSD。清理对象为不再被当前菜单引用的 `stm_simple` / `stm_restyle` 旧素材、SVG 源稿、旧按钮/旧 logo、未引用的加载字幕，以及未引用的5首菜单音乐。启动视频音频（WAV/OGG）、全部轮播图、在用 UI、补给图标、编制预览图、示例地图和示例配置保留。

本次没有缩小分辨率、降低色数或有损重编码。21 张 PNG 保留像素、透明度及色彩/分辨率元数据。两张 8096 像素宽的轮播原图仍是包体最大部分；为保持画质，本次保留原尺寸。

`tools/package-client-menus.py` 改为按菜单文件引用筛选 assets，仍完整保留按目录读取的轮播图片；`--lossless-png` 为可选无损压缩参数（需 Pillow）。`tools/png-lossless.py` 可独立压缩模组纹理。Gradle jar 打包按菜单 manifest 限定有效载荷，防止 `processResources` 输出中残留的旧资源混入，同时排除常见编辑源稿、备份和临时文件。客户端已有的未引用音乐/旧素材不作删除，瘦身针对发布 jar。

核验日志/数据：`build/jar-slim-build.log`、`build/jar-audit-before.json`、`build/jar-audit-after.json`、`build/jar-slim-verification.json`、`build/menu-slim-report.json`、`build/texture-slim-report.json`。

SHA256：`2488c221ea0ff563fa7ab2ba3b02b9c0abf2cfa22dbcda270200a8b0d70c8fef`

原 jar 和资源源码已备份在客户端目录下 `espetro-backups/20261114-jar-slim/`。备份位于 jar 之外。部署前确认 Minecraft 未运行并取得目标 jar 独占访问；部署后哈希与构建产物一致。此次只改资源和构建规则，未再次启动游戏。
