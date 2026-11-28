# 胡闹战场 FancyMenu · 无边框毛玻璃

客户端：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试`。

延续简约版左侧全高渐隐黑幕、暖白文字与天青色 TN 标。
六个主菜单按钮采用无边框半透明底色：主按钮 22%、其他按钮 10%、悬停 30%、禁用 4%。
所有状态均无描边、箭头、切角；文字始终为暖白。主按钮通过略高的透明底色保留层级。

每个按钮背后放置与原生按钮同锚点、同尺寸的 FancyMenu `shape` 元素，开启 `blur_enabled = true`、
`blur_radius = 4.0`，由 FancyMenu 原生 GuiBlurRenderer 实时模糊区域内的背景。
使用原生按钮处理点击、键盘导航和悬停，模糊图形仅负责绘制。
加载页仅保留实景背景和天青色 TN Logo；不使用渐变黑幕、毛玻璃底托或额外加载提示。
加载页移除 `progress_bar` 自定义记录，尺寸、位置及绘制全部交还 Drippy/Minecraft 的原生进度条。
天青色 TN 为 `#06DEE5`，无生图模型；没有改动开场视频、声音和资源加载时序。

主菜单绘制顺序：实景背景 → 渐隐黑幕 → 局部模糊 → Logo → 原生控件。
加载页绘制顺序：实景背景 → Logo → 原生进度条。
两个布局均验证 `render_custom_elements_behind_vanilla = true`，避免遮暗按钮与进度条。
矩形模糊类型、字段、颜色与渲染调用均参照已安装 FancyMenu 3.9.9 字节码。

依次运行 `build_menu.py`、`node render_assets.cjs`、`verify_preview.py` 后使用 `deploy_menu.py` 部署。
验证包括资源解码、UTF-8、透明度、所有按钮状态无描边、7 组逻辑尺寸边界与交叠、
按钮与模糊区域逐一对齐、原生控件 ID/动作/可见性不变、实际绘制顺序字段。
预览按实际配置坐标合成，实时模糊以 GaussianBlur 近似，原生字体以系统字体近似；不是实机截图。
实机着色器显示效果和性能尚待启动客户端核验。

部署先备份当前两份布局，再安装新增 `assets/stm_glass/`，最后原子替换布局。
备份路径和文件哈希见 `deployment.json`；备份中的 `restore.ps1` 可恢复上一版。
