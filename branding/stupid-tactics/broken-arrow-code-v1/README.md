# 胡闹战场 / Stupid Tactics Minecraft

代码绘制字标 v1，2026-11-09。

本版仅依据用户所附 Broken Arrow 字标的双行节奏、上轻下重和少量笔画切口设计，全部通过函数绘制；没有调用生图模型。

## 文件

- `preview.png`：1600 × 1000，深色底展示。
- `stupid-tactics-hunaobattlefield.svg`：1400 × 760 浅色矢量主标，透明底。
- `stupid-tactics-hunaobattlefield-transparent.png`：2800 × 1520 透明 PNG。
- `stupid-tactics-hunaobattlefield-dark-ink.svg`：同构图深墨色矢量，用于浅底。
- `stupid-tactics-hunaobattlefield-dark-ink-transparent.png`：深墨色透明 PNG。
- `stupid-tactics-small.svg`：去除切口的小尺寸版本。
- `small-size-check.png`：360px 缩小检查。
- `draw-logo.ps1`：字形转轮廓、排版、切口与预览函数。
- `render-logo.cjs`：Sharp 矢量栅格化导出函数。

## 设计

STUPID / TACTICS 为双行主标题；中文名「胡闹战场」置于下方；MINECRAFT 为最小一级副标题。
主字标为单色，预览底色渐变不属于透明主标。切口通过独立 SVG mask 定义，可整体关闭或单独调整。
矢量中所有可见文字都已转为 path，不需要接收方安装字体，没有内嵌图片。

字形基于本机 Bahnschrift Regular、Impact Regular、DengXian Regular 和 Arial Bold 的轮廓，另做尺寸、字距、排版与切口处理。包内不含字体文件。

## 重绘

Windows PowerShell 运行：

```powershell
.\draw-logo.ps1
# Node.js 环境需能 resolve 已安装的 sharp 包。
node .\render-logo.cjs
```

脚本默认在自身目录中重新导出同名生成文件。可通过 `-OutputDirectory` 指定矢量导出目录。
本次导出已检查 PNG 完整性、透明通道、中文内容、SVG 路径独立性与小尺寸可读性。
