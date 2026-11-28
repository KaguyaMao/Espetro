# 胡闹战场 / Stupid Tactics Minecraft

原创矢量标识 v1 · 2026-11-09

## 概念：出格的一块

从服务器名称与方块世界的战术玩法出发，画出一组阶梯状 ST：
S 像由方块搭出的折线路径，T 像一处搭建完整的阵地。
右上角多放的一块珊瑚橙方块，表达“认真配合，偶尔出格”的胡闹感。
它不影响主体的稳定，单色版本也能靠错位轮廓辨识。

中文名为主要字标；「闹」字与那块出格方块使用同一强调色，建立图形与文字的联系。
下方保留 STUPID TACTICS 英文名称与 MINECRAFT 平台副标。

本版未查阅或使用外部 Logo 参考。标记由坐标、直角路径和一个方块直接构造，未调用生图模型。

## 文件

| 文件 | 用途 |
| --- | --- |
| `hunaobattlefield-primary.svg` | 石墨色 / 珊瑚橙主标，透明底 |
| `hunaobattlefield-reverse.svg` | 反白主标，透明底，适合深色背景 |
| `hunaobattlefield-monochrome.svg` | 单色主标 |
| `hunaobattlefield-symbol.svg` | 独立 ST 图形，无文字 |
| `hunaobattlefield-avatar.svg` | 方形头像，深色底 |
| `preview.png` / `preview-dark.png` | 亮底和暗底展示 |
| `hunaobattlefield-*-transparent.png` | 2480 × 1000 透明 PNG |
| `avatar.png` | 896 × 896 头像 PNG |
| `server-icon.png` | 64 × 64 原生服务器图标 PNG |
| `symbol-check-32.png` | 32 像素宽缩小检查 |
| `draw-brand.ps1` | 原生几何、字距与字形轮廓绘制函数 |
| `render-brand.cjs` | Sharp 栅格化与不同尺寸导出 |

## 配色与结构

- 石墨：`#1C2930`
- 珊瑚橙：`#ED6547`
- 暖纸白：`#F3F1E7`
- 暗底强调色：`#FF8266`
- 图形主体厚度：56 单位；最小负形间隔：32 单位。
- SVG 可无限缩放，形状与字形均为矢量路径，没有内嵌位图或外部字体链接。

中文与辅助英文基于 Windows 本机 Microsoft YaHei / Arial 字形轮廓排版，包内不含字体文件。
字体轮廓已固化在 SVG 中；打开成品无需安装上述字体。

## 重绘

在有上述字体的 Windows 环境中运行：

```powershell
.\draw-brand.ps1
# Node.js 应能 resolve 已安装的 sharp 包。
node .\render-brand.cjs
```

脚本默认重写同目录下的生成文件；SVG 可通过 `-OutputDirectory` 导出至另一目录。
主色、几何和字标分别由 `Draw-FieldMark`、`Draw-Lockup`、`Draw-Lettering` 函数控制。

已核对主标文字、亮暗展示、64px 图标、32px 图形、PNG 透明通道及 SVG 的纯路径结构。
本次仅交付品牌素材，没有写入服务器配置。
