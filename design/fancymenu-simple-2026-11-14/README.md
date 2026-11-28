# 胡闹战场 FancyMenu 简约版 · 2026-11-14

客户端：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试`。

配色直接从 Espetro 的 `UiTheme.java` 暗色令牌提取，使用暖白文字、石墨底、暖白反相主按钮和少量黄色悬停反馈。
不改变游戏内 GUI、主题指令或模组 jar。此 FancyMenu 资源采用固定暗色，适配照片背景。
左侧使用全高纯黑遮罩，向右平滑衰减到透明，约在画面宽度 60% 处完全透明；右侧不加暗幕。
去除独立卡片、青色描边、斜切角、箭头、角标与微标签。Logo 保留原有文字和图形几何，文字为暖白，TN 标保留品牌天青色 `#06DEE5`。
原有服务器实景轮播、原生按钮、功能和可见性保持原样。
普通加载页同步应用简化 Logo、黑幕与文字；保留实际加载进度。

## 绘制层级修复

上一版生成脚本用 `[a-z_]+` 匹配顶层容器，遗漏带连字符的 `layout-meta`，
使 intended `render_custom_elements_behind_vanilla = true` 未写入，实际仍为 false。
本版正则改为 `[a-z_-]+`，并对两个输出布局明确断言该字段为 true。
黑幕属于自定义背景元素，在原生按钮之前绘制，不再用覆盖按钮的后景图模拟背景。
字段解析与背景/前景集合绘制顺序已根据已安装 FancyMenu 3.9.9 的 Layout 和 ScreenCustomizationLayer 字节码确认。

## 构建与验证

依次运行 `build_menu.py`、`node render_assets.cjs` 和 `verify_preview.py`。
所有新视觉由 SVG 几何和字体轮廓绘制，无生图模型。
验证涵盖资源可解码、UTF-8 无 BOM、原生按钮 ID/动作/可见性不变、7 组逻辑 GUI 尺寸的按钮边界与交叠、
黑幕左侧 alpha/右侧透明/全高一致性、主按钮颜色与不透明度，以及实际写出的渲染顺序字段。
布局预览依据配置坐标合成，Minecraft 原生字体使用系统字体近似，并非实机截图。
本轮未找到运行中的 Minecraft 窗口，部署后需要启动客户端做实机核验。

`deploy_menu.py` 先备份当前两份布局，安装新增 `assets/stm_simple/` 资源，再原子替换布局。
`deployment.json` 记录备份路径及前后哈希；备份目录中的 `restore.ps1` 可恢复上一版。
原轮播图片、旧版设计素材、开场视频及音频、游戏资源加载时序均未修改。
