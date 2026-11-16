# 任务提示词：Minecraft 1.20.1 Forge 客户端「启动视频」提前到资源加载期播放

> 2026-11-11 更新：已修正绘制问题，并追加修复音频与片尾回退；13 项专项回归测试通过，新 jar 已安装到用户指定客户端。
> 下面保留原交接背景；最新实现、产物校验和与验证范围见 [修复记录](SPLASH_EARLY_FIX_2026-11-11.md)。完整整合包冷启动效果尚待实机验收。

> 这份文档是写给接手的 agent 的完整任务书。项目代号 Espetro（Minecraft Forge 1.20.1 战场服客户端/服务端模组）。
> 你只需要处理**客户端启动视频**这一件事，不要碰服务端玩法逻辑。

---

## 0. 一句话目标

让客户端在**资源加载期**（窗口已出现、主菜单尚未出现、原版/DrippyLoading/FancyMenu 正在显示加载幕布时）播放
`THE NET` 片头视频（1280×720 @60fps，394 帧 JPEG + 一段音频），**必须完整播完**，**每次启动都播**；
视频播完后如果资源还没加载完，就自然回落到原本的加载幕布；加载完成后进入主菜单。

## 1. 已经达成（不要回退这些）

- **视频素材已在模组 jar 内**，classpath 直读，不依赖资源包：
  - `src/main/resources/assets/espetro/splash/frames/0001.jpg … 0394.jpg`（共 5,855,904 B）
  - `src/main/resources/assets/espetro/splash/meta.json`
    → `{"fps":60,"frames":394,"durationMs":6567,"width":1280,"height":720,"audio":"espetro:splash_audio"}`
  - `src/main/resources/assets/espetro/splash/audio.wav`（PCM16 / 44.1 kHz / 立体声 / 1,159,142 B）
- **主菜单阶段的播放已经完全可用**（画面 + 声音），走的是 `SplashVideoScreen`（`Screen`）+ `SplashWavAudio`。
  这条路径已验证：用户确认「有声音了」，画面正常。
- **加载期不碰 OpenAL 是硬约束**，已实现门闩：`SplashWavAudio.alReady` 只由 Forge 的
  `SoundEngineLoadEvent` 置位（`EspetroClient.onSoundEngineLoad`）；未就绪时 `play()` 直接返回 false。
  （曾经在加载期直接调 `AL10`，导致 `AL$ICDStatic` 永久损坏 → 加载尾声原版 `SoundEngine` 初始化时
  `NoClassDefFoundError` 把客户端崩掉。）

## 2. 未达成的目标（当前唯一卡点）

**加载期的视频画面出不来。** 最新一次实测（jar `22,902,921 B`）：用户反馈「本次仍然没有播放」。

已经排除的，不要再花时间：
- 不是素材缺失 / 不是资源包问题（日志有 `来源=classpath`、`394 帧 @ 60 fps`）；
- 不是启动判定不触发：日志已证明 `started=true`、`finished=true`，时间轴完整走完；
- 不是崩溃（客户端正常退出，无 crash-report）。

## 3. 现状：代码在哪、怎么接的

客户端包 `org.espetro.client.splash`：

| 文件 | 作用 |
|---|---|
| `SplashVideoAssets.java` | 读 `splash/meta.json`；`open(true)` = 只从 classpath 读；3~4 个守护线程按需解码 JPEG 到 `NativeImage`；`take(index)` 未就绪返回 null；`prewarm()` |
| `SplashVideoScreen.java` | 主菜单阶段用的 `Screen` 实现（已验证可用：帧推进、letterbox、ESC 跳过、`SplashWavAudio`） |
| `SplashVideoController.java` | 旧触发点：`ScreenEvent.Init.Post` 且目标 `instanceof TitleScreen` → `setScreen(new SplashVideoScreen(target))`；`markPlayed()/wasPlayedByScreen()` |
| `SplashEarlyRenderer.java` | **加载期叠加渲染器**（当前在调试的类）：`renderOverlay(Minecraft)` 每帧调用；内部 `start()` / `upload(NativeImage)` / `stop()`；`get()` 单例；`onClientTick` 只做超时兜底 |
| `SplashWavAudio.java` | 直连 LWJGL `AL10` 播 WAV（因为 `sounds.json` 事件在加载期拿不到），带 `alReady` 门闩 |

Mixin（`src/main/resources/espetro.mixins.json` 的 `client` 列表）：

| Mixin 类 | 目标 | 现状 |
|---|---|---|
| `client.MinecraftRenderMixin` | `Minecraft#runTick(Z)V` @TAIL | 当前版本；**画面仍不可见** |
| `client.GameRendererRenderMixin` | `GameRenderer#render(FJZ)V` @TAIL | 上一版主钩子，同样不可见（保留作备用） |

`EspetroClient` 里注册了 `SplashEarlyRenderer::onClientTick`。

## 4. 关键日志（判断依据，务必先看）

客户端日志（**GBK 编码**，中文在 UTF-8 下是乱码，**grep 请用 ASCII 关键字**如 `started=`、`#300`、`espetro`）：

```
[启动视频] 渲染钩子 #1:   started=false, failed=false, finished=false, level=false, screen=null, overlay=ForgeLoadingOverlay
[启动视频] 资源就绪: 394 帧 @ 60 fps / 6567 ms, 来源=classpath
[启动视频] 加载期叠加开始: 394 帧 @ 60 fps
[启动视频] 渲染钩子 #300: started=true, failed=false, finished=true, level=false, screen=TitleScreen, overlay=ForgeLoadingOverlay
```

解读：`renderOverlay` 被调用（#1…#300，#300 在 16 秒后，即加载期约 **18 fps**），`start()` 成功，
时间轴跑完，但**用户只看到过一帧**。也就是「画了，几乎每帧都被盖住 / 或根本没落到可见缓冲」。

## 5. 已经试过并失败的方案（别重复）

1. **抢占 `Screen`**（`setScreen` 到 `TitleScreen` 之前）→ 只能等加载完才触发，等于没提前。
2. **包 `Minecraft#overlay`**（mixin `setOverlay` HEAD，把加载幕布包进自己的 `Overlay`）→ 黑屏 + 打乱
   DrippyLoading / FancyMenu 时序（加载完直接落到原版「设置」界面）。
3. **`GameRenderer#render` TAIL 即时绘制** → 用户只看到一帧：被加载幕布的 `GuiGraphics` 批量顶点盖掉
   （幕布的 flush 发生在 `runTick` 收尾）。
4. **`Minecraft#runTick` 里 `GameRenderer.render` 调用之后注入** → 同样只闪一帧。
5. **当前：`Minecraft#runTick(Z)` @TAIL** → 仍然没有画面。

## 6. 我（上一个 agent）的判断，供你参考但不要盲信

- 现象一致性最好解释是**渲染顺序/缓冲区问题**：幕布的顶点在 `runTick` 末尾 flush，而我们的即时绘制落在它之前，
  或者落到了已经被交换走的 back buffer 上。
- 另一个待验证的可能：`GuiGraphics` 的最终 `flush()` 发生在 `Minecraft#runTick` 的**最后一次**批量提交之后，
  而我们注入的 TAIL 位于其之前/之后需要实测确认（可以在 mixin 里打一行日志，记录
  `GL11.glGetError` / 当前绑定的 framebuffer / `RenderSystem.getShader()` 状态，或直接对比「同一帧只画一次」与
  「每帧画一次」的可见性）。
- 也有可能是**绘制本身无效**（纹理未真正上传、投影矩阵被后续 `RenderSystem` 调用重置、混合/深度状态不对），
  只是恰好在第一帧赶上了可用状态。建议用一个「恒定颜色全屏 quad」先验证「能不能在我的注入点画出任何东西」，
  再叠视频纹理。

## 7. 建议的下一步（按性价比排序）

1. **最小可见性实验**：在 `SplashEarlyRenderer.renderOverlay` 里先画一个**纯红色全屏 quad**（不绑纹理、不用投影矩阵，
   直接 `RenderSystem.setShader(GameRenderer::getPositionColorShader)` + `Tesselator`），只验证「这个注入点能不能上屏」。
   - 能看到红色 → 问题在纹理/投影；看不到 → 问题在注入点/缓冲区。
2. **换注入点**：`Minecraft#runTick` 的 TAIL 之后若仍不行，试 `Window#updateDisplay` 之前（mixin `Window#updateDisplay` HEAD，
   在 swap 之前画）——这是「最后一刻」，理论上不可能被任何后续绘制覆盖。
3. **换思路：接管加载幕布的渲染内容**，而不是叠加。DrippyLoading 的做法可参考：
   解包 `drippyloadingscreen_forge_3.1.5_MC_1.20.1.jar`，看 `MixinLoadingOverlay.afterRenderDrippy` 与
   `DrippyOverlayScreen extends Screen`（`m_88315_`）如何替换画面。我们可以在 Forge 的
   `ForgeLoadingOverlay#render` 与其委托的原版 `LoadingOverlay#render` 上做同样的事。
4. 若加载期实在拿不到可靠绘制（第三方加载器太多），退一步：**保留主菜单阶段播放**（已验证可用），
   并把加载期改为「黑底 + 视频第一帧静态图」这种低风险方案，或者直接告诉用户技术上不可行。

## 8. 运行/构建/部署环境（照抄即可）

- 构建（**`-D...` 参数必须加引号**，否则 PowerShell 会当成 gradle 任务名）：
  ```powershell
  cd D:\minecraft\modp\Espetro
  $env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
  .\gradlew.bat build -x test --offline --console=plain "-Dnet.minecraftforge.gradle.check.certs=false" "-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar"
  ```
  产物：`build\libs\espetro-1.1.3-l.jar`（当前约 22.9 MB）
- 客户端 mods 目录（唯一一套，直接覆盖）：
  `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`
- 客户端日志：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\logs\latest.log`（GBK）
- 崩溃报告：同目录下 `crash-reports\`；PCL 启动器日志 `D:\minecraft\PCL\Log1.txt`
- 服务端 jar 位于远端面板（`build\tmp\pf2.mjs`），**本次任务不涉及服务端**，改客户端即可。

## 9. 验收标准

- 冷启动客户端：加载幕布一出现，视频开始播放（不是等主菜单）；
- 视频 6.5 秒完整播完（加载慢时可接受掉帧，但不能只出现一帧）；
- 播放期间画面不闪、不被加载幕布盖住；
- 播完若仍在加载，自然回到原加载幕布，不残留、不黑屏；
- 不破坏 DrippyLoading / FancyMenu 的加载与主菜单切换；
- 不崩溃（尤其不能再出现 `AL$ICDStatic` / `SoundEngine` 相关异常）；
- 日志出现 `[启动视频] 加载期叠加开始: 394 帧 @ 60 fps`。

## 10. 硬约束

- 只用 Forge 1.20.1 / 47.4.22 已有 API 与 LWJGL，不新增依赖；
- 不修改加载幕布本身的行为（不允许再出现「加载完进错界面」）；
- 加载期绝不允许碰 OpenAL（`alReady` 门闩必须保留）；
- 源码文件写 UTF-8 **无 BOM**（PowerShell 的 `Set-Content -Encoding UTF8` 会写入 BOM，曾导致
  `espetro.mixins.json` 解析失败、客户端启动即崩；用 `[System.IO.File]::WriteAllText(path, text, New-Object System.Text.UTF8Encoding($false))`）。
