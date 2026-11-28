# 胡闹战场 · 主菜单同款方形图标

使用现有主菜单的简约标题矢量路径，保留暖白文字、天青色 TN 标、“胡闹战场 / MINECRAFT”副标题。背景选自主菜单幻灯片 image_6.png（坦克与木塔），裁为正方形，加56%黑色半透明遮罩；没有额外边框或科技装饰。制作过程为代码合成，无生图模型。

| 文件 | 用途 |
| --- | --- |
| avatar-1024.png | 交流群头像及通用方形原图 |
| avatar-512.png | 512像素群头像 |
| client-icon.png | 256像素客户端/启动器图标 |
| client-icon.ico | Windows客户端快捷方式图标，含16/24/32/48/64/128/256像素 |
| server-icon.png | Minecraft服务端图标，已校验为64×64 PNG |
| master-2048.png | 2048像素大图 |
| stupid-tactics-square.svg | 自包含SVG，内嵌背景，标题保持可编辑矢量路径 |
| preview.png | 方形、圆形裁切和小尺寸检查 |

服务端使用时把 server-icon.png 放在 server.properties 所在目录，随服务器重启加载。客户端图标可在启动器中选 client-icon.png，Windows快捷方式选 client-icon.ico。此交付只制作图标文件，未修改服务器或启动器配置，也未加入模组jar。

复现：依次运行 compose.py、render.cjs、finalize.py。依赖Pillow及现有sharp环境。配置、输入来源和校验分别记录在 composition.json、verification.json。
