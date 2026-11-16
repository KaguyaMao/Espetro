# 启动视频：提前到「资源加载之前」的方案

> 需求（m16424）：视频从窗口出现就开始播，盖掉加载幕布；**视频播完若资源还没加载完，回落到现有加载幕布**。
> 本文只给方案，未改代码。

## 0. 结论

不要把视频做成 `Screen` 去抢屏幕，而是**接管 `Minecraft` 的 overlay 槽位**：
用一个 `SplashOverlay implements Overlay` 包住原本要显示的加载幕布（`LoadingOverlay` 或 FancyMenu / DrippyLoading 自己的实现），
视频期间只画视频帧，视频结束后**把每一帧原样交还给被包住的原 overlay**——加载幕布自然接着显示。

改动量 ≈ 300 行（1 个 mixin + 1 个 Overlay 类 + 3 处小改），不含资源，协议号不变，服务端零改动。

## 1. 为什么是 overlay 而不是 screen

| | 抢 `Screen` | 包 `Overlay` |
|---|---|---|
| 加载期可见性 | 加载幕布是 overlay，画在 screen **之上**，会被盖住 | 直接占据 overlay 槽位，天然在最上层 |
| 加载完成时 | Minecraft 会 `setScreen(new TitleScreen())` 把我们的屏顶掉 | overlay 置空时我们被移除，行为一致 |
| 回落加载幕布 | 需要自己找加载进度 |  delegates 回去即可，进度条/文本全由原实现负责 |
| 第三方加载器 | 与 FancyMenu 抢屏，容易互相顶掉 | 包住它们的 overlay 实现，不关心它们长什么样 |

`Minecraft#setOverlay(Overlay)` 是 public 的，`Overlay` 只有 `render(GuiGraphics,int,int,float)` 与 `isPauseScreen()`，接口极窄，适合做包装。

## 2. 时序模型

```
窗口建立 / GL 就绪
   └─ Minecraft.reloadResourcePacks() 开始
        └─ setOverlay(LoadingOverlay)   ← 我们在这里拦
             ├─ 阶段 A：SplashOverlay 只画视频（0 … 6.6 s）
             ├─ 视频结束 & 仍在加载 → 每帧 delegate.render()（原有加载幕布，含进度条）
             └─ 加载结束 → Minecraft.setOverlay(null) → 我们的 wrapper 出栈
                  └─ 随后 setScreen(TitleScreen)
        （若加载比视频快：overlay 提前被置空，视频随之中止——可选 B2 见 §6）
```

## 3. 实现

### 3.1 新增 mixin（`src/main/java/org/espetro/mixin/client/MinecraftOverlayMixin.java`，≈60 行）

```java
@Mixin(Minecraft.class)
public abstract class MinecraftOverlayMixin {
    @Inject(method = "setOverlay", at = @At("HEAD"), cancellable = true)
    private void espetro$wrapLoadingOverlay(Overlay overlay, CallbackInfo ci) {
        Overlay wrapped = SplashOverlayController.wrap((Minecraft) (Object) this, overlay);
        if (wrapped != overlay) {
            ((Minecraft) (Object) this).setOverlay(wrapped); // 注意防重入
            ci.cancel();
        }
    }
}
```

- `SplashOverlayController.wrap` 只在「首次、且 `overlay != null`、且开关打开」时返回新 wrapper，其余一律原样返回（避免反复包装 / 重入）。
- 已在 `espetro.mixins.json` 的 `client` 列表加一行。

### 3.2 新增 `client/splash/SplashOverlay.java`（≈150 行）

```java
public final class SplashOverlay implements Overlay {
    private final Overlay delegate;     // 被包住的原加载幕布
    private final long startNanos;
    private boolean handedBack;

    @Override public void render(GuiGraphics gui, int mouseX, int mouseY, float partial) {
        if (!handedBack && SplashVideoAssets.hasFrame(now())) {
            drawFrame(gui, now());       // letterbox 全屏，纯 DynamicTexture，无文字
        } else {
            handedBack = true;
            if (delegate != null) delegate.render(gui, mouseX, mouseY, partial);
        }
    }
    @Override public boolean isPauseScreen() { return true; }
}
```

关键点：

- **不画任何文字**：资源加载期 `font` / 语言表 / 九宫格贴图都还不可用。要「跳过提示」就得等加载完成后再画（不建议）。
- 画面换算用 `window.getGuiScaledWidth()/getGuiScaledHeight()` 每帧重算，适配窗口缩放与全屏。
- 音频沿用现有直连 OpenAL 通路（`SplashWavAudio`）：`SoundEngine` 在 `Minecraft` 构造期就建好 OpenAL 设备，早于资源加载，可用；设备没就绪就静默，不抛错。
- 视频结束**不立刻**销毁纹理：交还后仍保留到 overlay 出栈，防止某些加载器最后一帧仍调用我们的 `render`。

### 3.3 `SplashVideoAssets` 的小改

- 新增 `prewarmFirst()`：安装 overlay 时同步解码第 0 帧，避免第一帧闪黑。
- 新增「早期模式」帧步进：加载期 CPU 被数据包/模型加载占满，解码可能跟不上。方案是早期阶段**每 2 帧取 1 帧**（等效 30 fps），进入阶段 B 或主菜单路径时恢复 60 fps。帧序列本身不用重导。

### 3.4 `SplashVideoController` 的小改

- 新增 `earlyConsumed` 静态标记：早期阶段播过 → 现有 `onScreenInit`（主菜单前的播放）直接返回，避免播两遍。
- 开关 `before_resource_load = false` 时，行为完全回到现在的「主菜单前播放」。

### 3.5 配置（`espetro-client.toml` 或现有 splash 配置节）

```toml
[splash]
enabled = true
before_resource_load = true   # 新增：true=加载期就播（默认），false=主菜单前播
volume = 0.8
```

## 4. 兜底与边界

| 情况 | 处理 |
|---|---|
| `splash/meta.json` 缺失 / 帧解码失败 | `wrap()` 直接返回原 overlay，不接管；日志一条 warn |
| 渲染期异常（GL 状态异常） | `try/catch` 后 `handedBack = true`，立刻 delegate，永久禁用早期阶段 |
| 加载器反复 `setOverlay(...)` | 只包第一次；之后透传（否则会重置视频计时） |
| 加载比视频快 | overlay 提前置空，视频中止（正常行为）。若要「必须播完」见 §6 B2 |
| FancyMenu / DrippyLoading 自定义加载画面 | 我们包的是它们的 `Overlay` 实例，视频结束后原样交还，它们的画面继续 |
| 窗口缩放 / 全屏切换 | 每帧按 `getGuiScaled*` 重算 quad 与 letterbox |
| 无显卡加速 / GL 不可用 | 不接管 |

## 5. 验收清单

1. 启动客户端：窗口出现后立刻是视频，**看不到**原版泥土/加载幕布；
2. 视频播完时若仍在加载 → 自动出现原有加载幕布（含进度条与文字），加载完进主菜单；
3. 资源加载快于视频（热缓存）→ 视频播完即进主菜单（或按 B2 播完再进）；
4. 音量与画面同步，无爆音；
5. 删除 `assets/espetro/splash/` → 直接走原版加载流程，日志一条 warn；
6. FancyMenu 自定义主菜单包下同样成立；
7. 窗口化 / 全屏 / 4K / alt-tab 各一次不崩。

## 6. 可选项

- **B2「必须播完」**：在 `setOverlay(null)` 时也拦截，若视频未播完则保留我们的 wrapper 到结束再放行。代价是加载完成后可能仍多黑 1–2 s，且与「加载完成立刻进主菜单」的直觉冲突，**默认不做**。
- **渐黑收尾**：视频最后 0.4 s 叠一层 alpha 渐黑，接加载幕布或主菜单都不突兀。
- **首启才播**：用 `config/espetro/splash.json` 记 `lastPlayedVersion`，只有版本变化才播（避免每次启动都等 6.6 s）。

## 7. 工作量

| 项 | 量 |
|---|---|
| `MinecraftOverlayMixin` | ≈60 行（+ mixins.json 一行） |
| `SplashOverlay` | ≈150 行 |
| `SplashVideoAssets`（预热 + 早期 30fps 步进） | ≈40 行 |
| `SplashVideoController`（earlyConsumed + 开关） | ≈30 行 |
| 配置项 | 1 个 |
| 资源 / 协议 / 服务端 | 无改动 |

## 8. 实现状态（2026-11-11，已上线等实测）

用户定案：**必须播完**（加载先完成也要等视频结束）、**每次启动都播**。

已实现：

- `src/main/java/org/espetro/mixin/client/MinecraftOverlayMixin.java`：`@Inject(method = "setOverlay", at = @At("HEAD"), cancellable = true)`；
  refmap 已验证映射到 `Minecraft.m_91150_(Lnet/minecraft/client/gui/screens/Overlay;)V`，jar 内 `espetro.refmap.json` 包含该条。
- `src/main/java/org/espetro/client/splash/SplashOverlayController.java`：
  `decide()` 首次把加载幕布包进 `SplashOverlay`；播放期间游戏所有 `setOverlay(...)`（含 `null`）记为 `pending` 并拒绝生效
  → 满足“必须播完”；播完后 `notifyFinished()` 在主线程应用 `pending`：`null` 则清空 overlay 进主菜单，非空则把原加载幕布显示回来。
  另加 `onClientTick` 兜底：超过 `时长 + 5 s` 强制交还（防窗口最小化时渲染停摆卡住加载）。
- `src/main/java/org/espetro/client/splash/SplashOverlay.java`：自带 GL 纹理（不走 `TextureManager`，避免资源重载把纹理 close 掉）、
  逐帧 letterbox、末 400 ms 淡出、首帧 1.5 s 解码超时直接交还。
- `SplashVideoAssets`：新增 `openClasspath()`，资源加载期完全绕开资源包管理器直接用 classpath 读 `splash/meta.json` 与帧。
- `SplashWavAudio`：`readBytes()` 也改为 classpath 优先，加载期也能出声。
- `SplashVideoController`：早期接管时 `markPlayed()`，主菜单前那条旧路径不会再播第二遍。
- `espetro.mixins.json` client 列表加入 `client.MinecraftOverlayMixin`。

产物 `espetro-1.1.3-l.jar` = **22,903,729 B**，服务端 `mods/espetro-1.1.3-x.jar` 与客户端同名 jar 均已替换（SHA 比对一致）。

