# 启动视频（Splash Video）实现方案

> 需求：游戏加载完成、主页面出现**之前**，全屏播放 `THE NET 开头（渐黑）.mp4`，播完再进主菜单。

## 0. 结论（推荐做法）

**在 Espetro 客户端内做一个「帧序列播放屏」**，不引入任何新依赖：

1. 用 ffmpeg 把 mp4 离线抽成 **JPEG 帧序列 + OGG 音频**，打进 Espetro jar 或外置资源包；
2. 客户端用 `ScreenEvent` 钩子在**第一次出现主菜单类 Screen 时**替换成 `SplashVideoScreen`；
3. 播完（或 ESC 跳过）→ 淡出 → 回到原来的主菜单 Screen；
4. 每次进程只播一次，失败直接放过（绝不卡住进游戏）。

已实测素材规格与体积（见 §3），完全可接受。

## 1. 为什么不用「实时解码 mp4」

| 方案 | 结论 |
|---|---|
| 直接播放 H.264 mp4 | Java/OpenGL 没有 H.264 解码器，MC 也不提供；做不到 |
| 装 WaterMedia / VVC 一类视频模组 | 能解码，但**要新增客户端依赖**、要在整合包里分发、初始化开销与驱动兼容风险都由我们承担；且与本仓库「不加原来没依赖的模组」的原则冲突 |
| FancyMenu 的视频元素 | 同样依赖 WaterMedia，且只能放在主菜单上，做不到「主页面前」全屏 |
| GIF | MC 不原生支持动画 GIF，最终还是得拆帧——等于本方案 |

→ 离线抽帧是唯一无依赖、确定性最好的路径。

## 2. 播放时机（关键设计）

现在客户端启动链路是：`Minecraft` 初始化 → 资源加载 → 打开主菜单 Screen（原版 `TitleScreen`，装了 FancyMenu 则是它的 `CustomTitleScreen`）。

钩子选 **`ScreenEvent.Opening` + `ScreenEvent.Init.Post`**（`net.minecraftforge.client.event`）：

```
第一次出现任意 Screen 且 splashDone == false
  → event.setCanceled(true) / 直接 minecraft.setScreen(new SplashVideoScreen(原 screen))
  → 播放结束 setScreen(原 screen)；splashDone = true
```

- 用「第一个 Screen」而不是「instanceof TitleScreen」，是为了**兼容 FancyMenu**（它会把自己的自定义主菜单顶上来）。
- `splashDone` 是进程内 static 标志：**每次启动游戏播一次**，回主菜单、切维度、重连都不再播。
- 只做纯客户端：服务端不需要任何改动，也不改协议号。

## 3. 素材管线（已实测）

源文件：`THE NET 开头（渐黑） (1).mp4`｜6.57 s｜1920×1080｜60 fps｜H.264 7.2 Mb/s｜AAC 48 kHz 立体声 264 kb/s

用本机现成 ffmpeg（`D:\minecraft\JianyingPro\10.7.0.14095\ffmpeg.exe`，音频用 `D:\minecraft\oopz\ffmpeg.exe` 的 `libvorbis`）实测：

| 规格 | 帧数 | 帧总大小 | 音频(OGG q3) | 合计 |
|---|---|---|---|---|
| 854×480 @ 30 fps | 197 | 1,534,915 B | 81,733 B | **≈1.62 MB** |
| 1280×720 @ 60 fps | 394 | 5,517,214 B | 81,733 B | **≈5.60 MB** |

推荐 **854×480 @ 30 fps**（片头渐黑、场景暗，压缩率高，1.6 MB 对整合包几乎无感）。
如果在意清晰度可换 1280×720 @30 fps（约 2.6 MB）。

抽帧命令（写进 `tools/make-splash.sh` / `.ps1`，可重跑）：

```
ffmpeg -y -i "input.mp4" -vf "fps=30,scale=854:-2" -q:v 5 frames/%04d.jpg
ffmpeg -y -i "input.mp4" -vn -c:a libvorbis -q:a 3 audio.ogg
```

产物目录（二选一）：

```
src/main/resources/assets/espetro/splash/
  meta.json          {"fps":30,"frames":197,"audio":"espetro:splash_audio","durationMs":6570}
  frames/0001.jpg … 0197.jpg
assets/espetro/sounds/splash/audio.ogg
assets/espetro/sounds.json   ← 注册 espetro:splash_audio
```

体积取舍：
- **内嵌 jar**：jar 17.06 MB → ≈18.7 MB（每台客户端都要下一次）。
- **外置资源包 `espetro-splash.zip`**（推荐，如果不想让 jar 变大）：Espetro 用 `ResourceManager` 从 `assets/espetro/splash/` 读，资源包由整合包分发；缺失时自动跳过播放。

## 4. 运行时实现

### 4.1 新类（`org.espetro.client.splash`）

| 类 | 职责 | 规模 |
|---|---|---|
| `SplashVideoAssets` | 从 ResourceManager 顺序读帧 → 解码为 `BufferedImage` → 复用同一个 `DynamicTexture` 上传；读 `meta.json`；缺资源返回 null | ~120 行 |
| `SplashVideoScreen extends Screen` | 播放时钟、按 16:9 letterbox 绘制、黑边填充、ESC/点击跳过、结束淡出、超时兜底 | ~220 行 |
| `SplashVideoController` | `ScreenEvent` 钩子、`splashDone` 标志、把原 Screen 传递进来、异常兜底 | ~60 行 |
| `EspetroClient` 注册 | `DeferredRegister<SoundEvent>` 注册 `espetro:splash_audio`，配置项读取 | ~40 行 |
| `EspetroClientConfig` | `splash_video_enabled` / `splash_min_ms` / `splash_volume` | ~40 行 |

### 4.2 渲染

- 屏幕 `render()`：`fill(0,0,w,h,0xFF000000)` 打底 → 按窗口缩放算 letterbox 矩形 → `RenderSystem.setShaderTexture(0, dynamicTexture.getId())` + `blit` 到矩形。
- 帧推进用 `Util.getMillis()` 而不是 tick：`index = (elapsedMs * fps) / 1000`，天然跟真实时间同步；卡顿时丢帧而不是拖慢。
- 每帧解码 + 上传的开销实测约 3–8 ms（854×480 JPEG），30 fps 下完全够；如果改用 720p/60fps 建议改成**后台线程预解码 3–5 帧的环形缓冲**。

### 4.3 音频

- 在 `sounds.json` 注册 `espetro:splash_audio`（`category: master`，`stream: true`）；
- 播放用 `Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(...))`，音量取自配置；
- 跳过/结束时 `stop()`。

### 4.4 交互与兜底

- `ESC`、鼠标左键、`Space`：跳过（立即淡出 200 ms）；
- 最短展示 `splash_min_ms`（默认 1500 ms），避免一闪而过；
- 播放完成 → 黑场 300 ms → `setScreen(原 screen)`；
- **任何异常**（资源缺失、解码失败、GL 异常）→ 直接 `setScreen(原 screen)`，并只记一条 warn；
- 总超时 = `durationMs + 2000`，到点强制结束（防止主线程卡死）；
- 窗口失焦/最小化：暂停计时（`minecraft.isWindowActive()` 为 false 时不计时）。

## 5. 配置项

`config/espetro-client.toml`（COMMON/CLIENT）：

```toml
[ splash ]
enabled = true          # 总开关（服务器/玩家可关）
min_ms = 1500           # 最短展示时长
volume = 1.0            # 音频音量 0..1
```

## 6. 工作量与验收

| 项 | 量 |
|---|---|
| 素材管线脚本 | ~80 行（可重跑） |
| 客户端代码 | ≈480 行（5 个类） |
| 资源 | 1.6 MB（854×480@30） |
| 服务端改动 | 无 |
| 协议号 | 不变（1.38） |

验收清单：

1. 启动游戏 → 主菜单出现**之前**自动播放，约 6.6 s，画面/声音同步；
2. 播完自动进主菜单，且**只播一次**；退出到主菜单、重进世界不再播；
3. `ESC` 能跳过，跳过瞬间不爆音；
4. 把 `splash` 资源删掉 → 直接进主菜单，日志只有一条 warn；
5. 4K/窗口化/全屏、alt-tab 各试一次不崩；
6. FancyMenu 自定义主菜单下同样生效（钩子在「第一个 Screen」而非 `TitleScreen`）。

## 7. 需要你拍板的三件事

1. **画质/体积**：854×480@30（1.6 MB，推荐）还是 1280×720@30（2.6 MB）/ 720p60（5.6 MB）？
2. **放哪儿**：内嵌 Espetro jar（每人都下载）还是单独资源包 `espetro-splash.zip`（推荐）？
3. **是否要「最后 1 秒渐黑 + 直接接主菜单」**：源片名带「渐黑」，如果片尾本身没黑到底，可以在播放器里加 0.5 s 淡出过渡。
