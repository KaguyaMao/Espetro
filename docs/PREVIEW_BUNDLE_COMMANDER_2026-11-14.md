# 内置编制/地图图片及紧凑指挥官投票 · 2026-11-14

已构建并部署到 `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`。

## 图片

从指定客户端收录全部9张 `EsFactions` 图片及4张 `EsWorld` 图片，共13张；只收录 PNG/JPG/JPEG，不收录世界区块、配置和其它文件。原尺寸、透明度和解码后像素保持一致，PNG 使用无损压缩，JPEG 保留原始字节。

编制图存放在 `assets/espetro/textures/gui/factions/`，与已有资源位置共用一份文件，当前客户端的同名图片替换旧内置版本。地图图存放在 `espetro_previews/files/EsWorld/`；保留 `CREATE_PLUS.png`、`island.png`、`server_battlefield.png`、`越南.png` 原名。

`BundledPreviewInstaller` 在客户端初始化时按独立 manifest 同步到 `EsFactions/` 和 `EsWorld/`，缺图补齐，内容相同跳过，不同则先备份再替换。复用菜单安装器的全量暂存校验、路径检查、备份、原子替换及失败回滚，不新增 mixin 或修改网络协议。原有投票、揭晓、队伍选择和地图模糊背景继续读取原来的目录。已有 JSON 配置和世界数据不在同步范围内。

清单：`src/main/resources/espetro_previews/manifest.tsv`。维护工具：`tools/package-client-previews.py --client <客户端目录> --report <核验JSON>`（需 Pillow）。

## 指挥官投票按钮

仅指挥官候选区固定四列，从左到右、从上到下排列。单候选人仅占左上一个按钮，不再拉满整行；按钮宽度按当前玩家名称、自己标记、选中符号及票数所需文字测量，高度为单行文字加上下各3 GUI像素留白。窄窗口会缩小文字以适配四列，列表超出继续滚动，其它投票布局不变。

## 核验

- 构建和 Maven 本地发布成功，21项相关测试通过，包含新增4项图片同步测试。
- 实际发布 jar 的全部13个图片有效载荷大小/SHA256通过，与客户端原图尺寸和 RGBA像素一致。
- 使用实际 jar 在全新临时目录完成13张图片安装及哈希核验，第二次全部跳过；修改1张图片后，正确备份并修复。该测试不启动 Minecraft、不改动真实客户端图片。
- jar：210,483,622 字节；SHA256：`8ca1dfa8cb31ed973fc67ba0f61a78d277cdec24578738c9b39a6742a7be909f`。
- 备份：客户端 `espetro-backups/20261114-preview-bundle-commander/`，保存旧jar、相关源码及旧内置编制纹理。

日志和数据：`build/preview-commander-build.log`、`build/preview-bundle-report.json`、`build/preview-bundle-verification.json`、`build/preview-install-probe.log`。离线客户端渲染检查日志：`build/preview-commander-runtime.log`。

真实离线客户端启动和渲染检查通过，8人显示为4列2行，单人仅显示左上单行按钮。截图在 `design/commander-compact-2026-11-14/`。首次启动实际同步12张PNG，内容相同的JPEG跳过；旧图片保存至客户端 `espetro-backups/20261114-122429-menu-auto-b7813606-f9ee-4631-8aef-95280f3960b0/`。启动后13张客户端图片均与内置清单SHA256一致。测试进程已退出，临时KubeJS脚本已移出客户端并归档至 `docs/archive/preview-commander-probe-2026-11-14/`。
