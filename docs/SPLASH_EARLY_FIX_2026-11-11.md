# 加载期启动视频修复记录（2026-11-11）

## 追加修复：音频与片尾回退（当前已安装版本）

用户反馈没有音频，且片尾有时停留到资源加载结束。读取该客户端最新日志后确认：

- 20:40:09 开始显示片头；20:40:13 进入声音引擎及 FancyMenu 音频资源应用，渲染日志直到 20:40:20 才继续。这期间渲染线程被资源应用占用，停留在已显示的视频帧。
- `SoundEngineLoadEvent` 实际属于 MOD 事件总线，旧代码注册在 Forge 事件总线，所以 `alReady` 没有被正确置位。该事件也会在 SoundEngine 构造阶段、OpenAL 尚未初始化时触发，不能仅凭事件到达就开启门闩。

当前更改：

- 加载期使用 `SplashPcmAudio`，在后台预备并通过 JDK Java Sound 播放内置 PCM WAV，独立于 Minecraft/OpenAL。首帧出现时启动音频；设备就绪较晚时按视频时钟偏移；关闭操作不等待设备，不阻塞渲染线程。
- 保留主菜单播放器的 OpenAL 回退；将事件监听移至 MOD 总线，并通过 `SoundEngine.loaded` accessor 确认引擎实际已加载，构造事件不会提前打开门闩。
- 初次客户端资源重载的后台准备照常进行；使用原有 initial-stage future 的组合，延后依赖该阶段的 apply。片头结束后先显示并交换普通加载页，再放行应用任务，避免耗时的声音引擎/FancyMenu 初始化把片头画面留在前缓冲。20 秒超时自动放行；之后的资源重载不受影响；不包装或替换屏幕/覆盖层。
- 播放时间轴在 6567 ms 后开始 400 ms 淡出，到期关闭，不再等待末帧解码而延长片尾。

13 项专项测试通过，包括真实 Java Sound 设备播放状态（以零增益 PCM 静音验证）、设备迟就绪时的同步偏移、设备打开期间非阻塞关闭、实际 Minecraft `SimpleReloadInstance` 的 prepare/apply 时序、窗口上屏后才释放 apply、播放结束期限，以及此前的 GPU 像素和 OpenAL 门闩测试。

构建及生产 reobf 成功后，已立即原子替换并核对 SHA-256：

- 构建文件：`D:\minecraft\modp\Espetro\build\libs\espetro-1.1.3-l.jar`
- 已安装：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`
- 大小：22,921,969 字节。
- SHA-256：`8100a5bb49d9249ec6894cd618fce7c4baa2bcf80b058c93303c974488f140bc`。
- 旧版备份：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\espetro-backups\20261111-204940-before-audio-fix\`。

替换时客户端仍在运行，没有终止游戏进程；新版本在下一次启动时生效。完整整合包的新版冷启动视听效果仍需重启实测。新诊断日志包括 `PCM audio started: backend=JavaSound`、`Startup reload apply deferred`、`Loading page presented; startup reload apply released`。

## 首轮修复记录（以下产物信息为历史版本）

已构建 `build/libs/espetro-1.1.3-l.jar`，22,912,343 字节。

SHA-256：`5bfa409c4ddf5eeda8b0fab1a4f5cc9eae3618bbc989fef13587e02623f7f772`。

## 已确认的问题

依据本机缓存中的 Forge 1.20.1 / 47.4.22 源码及原渲染实现：

1. `Minecraft.runTick` 的 TAIL 在主渲染目标复制到窗口、`Window.updateDisplay` 交换缓冲之后。旧钩子画在已经交换后的缓冲上，后续主画面复制又会覆盖它。
2. 旧路径只生成纹理 ID，然后调用 `NativeImage.upload`。该方法执行 `glTexSubImage2D`，没有预先分配纹理存储，不能完成有效上传。
3. 旧正交投影的 near/far 为 1000/3000，单位模型视图矩阵下的四边形却位于 z=0，落在裁剪范围外。

## 当前实现

- 唯一启用的启动视频钩子为 `SplashBufferSwapMixin`：在 `RenderSystem.flipFrame(long)` 排空渲染队列后、调用 `glfwSwapBuffers` 前绘制。旧的两个 TAIL 钩子已从 Mixin 配置移除。
- `SplashVideoBlitter` 用独立 GLSL 150 着色器、NDC 全屏四边形绘制到窗口默认后缓冲，不依赖加载中的 Minecraft GUI 着色器或投影矩阵。
- 上传前用 `glTexImage2D` 分配 RGBA8 存储；维持比例并留黑。每次上传和绘制后恢复 framebuffer、程序、VAO、纹理/采样器、像素解包、混合、深度、裁剪、颜色掩码等被修改的 GL 状态。
- 时间轴从实际首次绘制开始；必须真正显示最后一帧并达到 6567 ms 后才开始 400 ms 淡出。解码结果携带实际帧号，避免把滞后的旧帧当成末帧。
- 解码任务和缓存合计最多 20 帧，提前解码范围 12 帧；关闭时释放本地图片，允许加载繁忙时丢帧。
- 不替换加载覆盖层或屏幕。早期失败时保留主菜单播放器回退；预备和播放阶段均有超时保护。
- 保留 `SoundEngineLoadEvent` 的 `alReady` 门闩；未就绪不调用 OpenAL。音频较晚就绪时按当前视频时间偏移播放。
- 启动路径使用独立日志器，避免仅因日志引用而提前初始化模组主类及注册表。

## 已完成验证

7 项专项 JUnit 测试全部通过：

- 解码等待不消耗播放时长；按 60 fps 推进目标帧。
- 旧帧不能触发结束；最后一帧至少保持至完整时长；淡出不会被重复绘制重置。
- 实际隐藏 GLFW 窗口及 OpenGL 3.2 Core 上读取后缓冲像素：上下方向、比例留黑、半透明淡出和透明结束正确。
- 在另一 framebuffer 已绑定、深度/裁剪/剔除/光栅丢弃已开启、颜色掩码和解包状态被修改、外部采样器已绑定时，连续换帧仍写入窗口后缓冲，且返回后恢复原状态，无 GL 错误。
- 从 classpath 实际解码第 1 帧及第 394 帧，核对尺寸、缓存上限和关闭后的清理。
- 声音引擎未就绪时，两种播放入口均返回 false，停止操作安全返回。

GPU：NVIDIA GeForce RTX 3060 Laptop GPU，OpenGL `3.2.0 NVIDIA 616.92`。

`build -x test` 与生产 reobf 打包成功。最终 jar 已通过 ZIP 完整性检查，确认：

- 只启用 `client.SplashBufferSwapMixin` 这一处启动视频钩子。
- reobf 后仍指向 `flipFrame` 内的 `GLFW.glfwSwapBuffers(J)V`，`remap=false`、`require=1`。
- 394 张 JPEG 连续完整，共 5,855,904 字节；WAV 为 1,159,142 字节；Mixin JSON 无 BOM。

尚未启动完整 DrippyLoading / FancyMenu 整合包进行冷启动实测，因此不能把上述专项验证视为整合包视觉验收。未覆盖客户端 mods 文件，也未操作服务器。

## 复现构建

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
$env:GRADLE_USER_HOME='C:\Users\Administrator\.gradle'
$env:JDK_JAVA_OPTIONS='-Dnet.minecraftforge.gradle.check.certs=false -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8'
.\gradlew.bat test --offline --console=plain --no-daemon --max-workers=2 -I tools/splash-tests.init.gradle '-Papricityui_version=1.2.6' '-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar'
.\gradlew.bat build -x test --offline --console=plain --no-daemon --max-workers=2 -I tools/splash-tests.init.gradle '-Papricityui_version=1.2.6' '-PauratipJar=D:\minecraft\Java\Projects\AuraTip\build\libs\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar'
```

专项 init 脚本只选取启动视频测试，避开仓库已有的无关测试旧 API 编译错误；同时关闭增量编译，避免 Windows 旧快照映射锁问题。没有修改产品依赖或服务端逻辑。

完整整合包验收时，检查日志中的 `hook=beforeSwap`、`加载期叠加开始`、`presented=` 和 `加载期叠加结束`，并核实加载期间持续播放、末帧和淡出后自然返回原加载画面/主菜单，以及声音引擎正常初始化。
