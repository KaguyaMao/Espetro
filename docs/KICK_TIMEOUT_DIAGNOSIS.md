# 「创建小队失败 + 被踢出」诊断报告（2026-09-30）

结论先行：
1. **不是被 OP 踢、不是封禁、不是崩溃** —— 服务端踢人原因统一是 `lost connection: Timed out`（30 秒没收到客户端心跳）。
2. **小队其实创建成功了**：客户端聊天里明确有 `已创建小队 小队2，你是队长（火力组 A 组长）`；
   之后报的 `你已经在小队中，不能重复创建小队` 属正常提示。真正的坑是**反复掉线**：每次重连服务端都执行
   `已离开班组小队，背包和职业已清空。请重新选择职业。`，小队被清掉，看起来就像"建不了队"。
3. 掉线的直接原因指向 **EsPoints 战术地图瓦片/预览重传风暴**：客户端每次断线前都在收大瓦片并上传纹理；
   服务端把**同一张 277 KB 预览瓦片重发了 46 次（≈12 MB）**；同时服务端出现**每 66 秒一次、每次 2.6–5.1 秒**的主线程卡顿，
   时间窗与"战术地图激活/生成瓦片"完全吻合。

## 1. 服务端侧证据（`logs/latest.log`，二进制下载核对）
玩家当天进出（服务端时间；客户端时间 = 服务端 + 8 小时）：

| 进服 | 掉线 | 时长 | 原因 |
|---|---|---|---|
| 13:06:20 | 13:37:09 | 31 min | Timed out |
| 14:12:10 | 14:25:31 | 13 min | Timed out |
| 14:26:04 | 14:27:04 | 60 s | Timed out |
| 14:28:13 | 14:28:43 | 30 s | Timed out |
| 14:44:01 | 14:44:31 | 30 s | Timed out |
| 14:44:52 | 14:45:22 | 30 s | Timed out |

- 服务端**没有任何异常/报错**，也没有 `/kick` 记录；`ly01_lin` 退出原因全部是 `Timed out`。
- 14:28:31 还被自动任命为 ATTACK 指挥官（`vacancy_auto_fallback`），12 秒后就因超时掉线，日志紧接着
  `清除 ATTACK 指挥官 (disconnect)`。
- 14:44:52 那次会话里，战术地图相关日志共 91 行：**45 次 descriptor + 46 次同一张预览瓦片**
  （`TacticalMapTileKey[session=1, level=3, x=0, y=0]`，每张 277043 B），一直发到超时那一刻。

## 2. 服务端卡顿（与地图强相关）
`Can't keep up! Is the server overloaded? Running 4427ms or 88 ticks behind` 等共 11 次：

```
13:08:56  2001ms/40   13:10:37  3593ms/71                         ← 开服预热
14:10:15  2722ms/54   14:12:29  2052ms/41
14:13:35  2699ms/53   14:15:48  4427ms/88
14:16:55  2630ms/52   14:18:02  5078ms/101
14:19:08  4593ms/91   14:20:15  5079ms/101
14:29:08  2627ms/52
```

- 14:13–14:29 之间**稳定每 66.0–66.7 秒卡一次**（≈1200 game tick 的周期任务在慢服上表现为 66 秒）；
- 14:13:26 日志正是 `战术地图已激活: session=1 2990x2578 previewLevel=3`、随后 `战术地图瓦片缓存命中: tiles=50`；
- 14:29 之后（瓦片缓存建完）卡顿消失，直到没人再触发地图。

## 3. 客户端侧证据（客户端 `latest.log` / `debug.log`；客户端时间）
```
22:26:26 [CHAT] 已创建小队 小队2，你是队长（火力组 A 组长）      ← 建队成功
22:26:35 [CHAT] 你已经在小队中，不能重复创建小队                ← 正常提示
22:44:55 战术地图JSON配置已应用: server map: .../TacticalMap.json
22:44:58 客户端已收到战术地图瓦片 level=3 x=0 y=0 374x323 (277043 bytes)
22:44:58 客户端已上传战术地图预览瓦片 374x323
22:45:29 客户端已收到战术地图瓦片 level=2 x=0 y=0 512x512 (593242 bytes)
22:45:31 客户端已收到战术地图瓦片 level=2 x=1 y=0 236x512 (231016 bytes)
22:45:33 voicechat Stopping / 22:45:38 World map finalized / client disconnected
```
- 服务端在 22:45:22（客户端时钟）已判超时并断开，客户端直到 22:45:33 才发现 → 这 30 秒里服务端**完全没收到**客户端的心跳包；
- 客户端每次断线前都在处理地图瓦片（277 KB / 593 KB，纹理上传 374×323、512×512）；
- 另有 `ModernFix: Total time to load game and open world was 53.13 seconds`（客户端进服负载很重）。

## 4. 代码级根因（EsPoints 源码定位）
重发回路的触发链：

1. 客户端持有地图订阅，**TTL 只有 120 tick**：`CapturePointManager.TACTICAL_MAP_SUBSCRIPTION_TTL_TICKS = 120L`，
   因此需要周期性续订；
2. 每次续订/订阅请求，服务端都会重发 descriptor 并**重新入队预览瓦片**：
   - `network/TacticalMapSubscriptionMessage.java:77` → `SyncTacticalMapBackgroundMessage.sendDescriptorOnly(sender)`
   - `network/SyncTacticalMapBackgroundMessage.java:100`（发 descriptor 日志）、`:111`、`:127` → `TacticalMapTileService.enqueuePreviewOnce(player)`
3. 客户端收到 descriptor 后，**即使 session+sha256 完全相同也会重新请求并重新加载预览**：
   `client/ClientTacticalMapTileCache.java:95-99`（相同则 `request(maxLevel,0,0)` + `tryLoadLocalPreview` 后 return）
4. 服务端发送器只要看到 waiter 就发整包：`tile/TacticalMapTileService.java:297-306`（277 KB/张），
   于是形成 ~4–6 秒一轮、每轮 277 KB 的重传风暴。

## 5. 建议动作（按优先级）
1. **修重发回路（最有效）**
   - 服务端：descriptor + 预览瓦片**按 (玩家, session, sha256) 只发一次**；订阅续订只做 TTL 续期，不重发。
     （`lastPreviewEnqueueAt` 已是节流用的 map，改成"一次性"语义即可。）
   - 客户端：`applyDescriptor` 在 session+sha256 未变时**不要**重新 `request` 预览、也不要重建预览纹理。
2. **削峰**：预览瓦片/首屏 LOD 推送延后到玩家真正打开地图时；大地图瓦片按需拉取并限流
   （`ModConfig.tacticalMapPlayerTransferKiBps / globalTransferKiBps` 已有预算，可下调）。
3. **服务端卡顿**：确认瓦片生成是否在主线程做（`ESPoints-TacticalMapTile` 线程已有，但"每 66 秒卡 5 秒"说明仍有主线程工作），
   把生成/缓存写入彻底移出主线程并限速；服务器已装 `spark`，可用 `/spark profiler start --timeout 60` 抓一次 66 秒周期做精确定位。
4. **链路排查（低成本）**：断线那 30 秒服务端完全收不到你的包，而你的下行正常（还在收瓦片）。
   建议直连 `218.1.146.12:23400`（语音服用的就是这个 IP）或换线路再测一局，排除端口映射/NAT 的上行问题。
5. **顺手修的小问题**：ApricityUI 每 250 秒报
   `Failed to reflectively persist LocalStorage … NoSuchMethodException: CompoundTag.putString(String,String)`
   （`config/apricityui/localStorage.nbt` 持久化失败），若小队界面走 ApricityUI，属于隐患。

## 5.1 为什么"网络差就更容易发生"（机制说明）
- 掉线判据本身就是**网络活性检查**：原版服务端每 15 秒发一次 KeepAlive，若 30 秒内收不到回应就断开
  （`disconnect.timeout`）。这解释了观测到的**恰好 30 秒**（14:26/14:28/14:44/14:45 四次）和 60 秒（跨两个周期）——
  与客户端是否卡顿无关，只要**客户端→服务端**方向出现 ≥30 秒的空档就会被踢。
- 地图瓦片把这条链路推向极限：单连接 TCP 下，277 KB～593 KB 的瓦片与几十字节的 KeepAlive 共用一条流，
  弱网/上行拥塞时小包会被大包**队头阻塞**；一旦触发重传，延迟进一步放大 → 30 秒预算很快耗光。
  本次会话实测瓦片重传 ≈12 MB（同一张 277 KB 发了 46 次），属于纯浪费的带宽。
- 服务端每 66 秒卡 2.6–5.1 秒，也会吃掉一部分 30 秒预算（服务器线程忙时无法及时处理你发来的心跳）。
- 因此：**网络差 = 触发条件，地图重传/服务端卡顿 = 放大器**。好网络能掩盖这个 bug（前 31 分钟没掉线），
  网络一抖就会立刻以 `Timed out` 的形式暴露。
- 需要区分两种可能（本次证据更偏向网络）：
  - **上行空档**：断线前客户端仍在**接收**瓦片（下行正常），而服务端完全收不到包 → 不对称丢包/上行拥塞；
  - **客户端主线程卡死**：若心跳处理被排在主线程，处理大瓦片/图集时也可能无法回应。
  区分办法：下次掉线瞬间抓客户端 `jstack`；或降低瓦片流量后再看是否还掉线。
- 可用的缓解手段（按成本）：修重发回路（流量降一个量级）→ 下调 `ModConfig.tacticalMapPlayerTransferKiBps/globalTransferKiBps`
  限流 → 换线路/直连 `218.1.146.12:23400` 排除端口映射上行问题 → 必要时在 GScode 里加 mixin 放宽
  `ServerGamePacketListenerImpl` 的 30 秒超时阈值（原版硬编码，无配置项）。

## 6. 复核方式
- 下次掉线时立刻抓客户端线程快照（`jstack <客户端PID>`），看主线程是否卡在地图纹理/图集处理；
- 或临时把 `EsWorld/CREATE_PLUS/EsConfig/TacticalMap.json` 停用（不激活地图）后测一局，若不再掉线即可确认因果。

## 8. 修复记录（2026-09-30 已实施）
### 8.1 服务端（EsPoints）
- `tile/TacticalMapTileService.java`
  - 新增 `pushedTiles`（玩家 → session → 已推送瓦片集合）、`descriptorFingerprint/descriptorSentAt`；
  - `enqueueViewport()`/`enqueuePreviewOnce()` 推送前先查"是否已推给该玩家"，已推过就跳过
    （**这是流量主因**：客户端每隔 ≤250ms 续订一次，原来每次续订都会把可见瓦片整批重新入队）；
  - 发送成功后 `markPushed(...)` 记账；`removePlayer()` 与 `clearLocked()`（session 切换/注销）清空；
  - 新增 `shouldSendDescriptor(player, session, sha256)`：同 (session, sha256) 在 30 秒自愈周期内不重复下发
    （descriptor 仅几百字节，30 秒兜底用于客户端缓存丢失时自愈）；
  - **显式请求路径（`RequestTacticalMapTileMessage`）不受上述限制**，因此客户端丢纹理后仍能自我修复。
- `network/SyncTacticalMapBackgroundMessage.java`
  - `sendDescriptorOnly()` 接入上述判断；`sendToPlayer()`（登录）**不再主动推送预览瓦片**；
    `broadcastToAll()` 改为按玩家逐个判断下发，且不再推送预览。
  - 效果：登录/续订不再"一进服就灌 277 KB 大包"，预览与首屏瓦片改为打开地图后按需拉取/推送。
### 8.2 客户端（EsPoints）
- `client/ClientTacticalMapTileCache.java`：`applyDescriptor()` 在 session+sha256 未变时**直接返回**，
  不再 `request(preview)`、不再重建纹理（此前每收一次 descriptor 就重拉一次 277 KB 预览 → 一次会话 46 次 ≈12 MB）。
### 8.3 构建与部署
- 构建：`cd D:\minecraft\modp\EsPoints; $env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11';
  .\gradlew.bat build -x test "-Papricityui_version=1.2.2-hotfix1"` → `BUILD SUCCESSFUL`，
  产物 `build/libs/espoints-1.1.1b.jar`（1,000,948 B）；
- `META-INF/mods.toml` 与线上旧 jar **逐字节一致**（依赖 apricityui/tetrachordlib/espetro 等未变，避免版本不匹配崩溃）；
- 服务端：上传 `mods/espoints-1.1.1b.jar` → 回下载 SHA256 **一致**，随后重启（`Done`，无新报错）；
- 客户端：已替换客户端 mods 内同名 jar（**需要重启客户端才会生效**）；
- 回滚点：旧 jar `build/tmp/srv-espoints-old.jar`（1,000,103 B）。

### 8.4 待确认/待办
- **需玩家实机验证**：进服 → 打开战术地图 → 观察是否还掉线；服务端日志里
  `发送战术地图预览瓦片` 不应再同一张反复出现（现在是"每 session 一次"）。
- **限流**：服务端 `tacticalMapPlayerTransferKiBps` 目前为 256（客户端值；默认 512），
  可用面板把它降到 128 左右；注意面板对 `world/serverconfig/` 返回 `Illegal access path`，需在面板文件管理器里改。
- **spark**：已验证可用（`spark tps` 会写入日志）。当前 0 人无地图活动时：TPS 20.0、
  1 分钟 Tick 时长 min/med/95%/max = 0.5/2.7/16.7/274.6 ms。要抓 66 秒周期卡顿需**有人在地图内**时执行
  `spark profiler start --timeout 70`（可让我来发，然后拿链接分析）。
- **链路**：直连 `218.1.146.12:23400` 或换网络测一局，排除上行/NAT 问题。

## 10. 弱网专项优化（第二轮，2026-09-30 已实施）
第一轮修掉了地图重发，但"网络本来就特别差"的情况下仍会被踢——因为**原版有两处 30 秒硬阈值**，与流量大小无关：

### 10.1 找到并放宽的两处硬阈值
| 位置 | 原版 | 改为 | 说明 |
|---|---|---|---|
| netty `ReadTimeoutHandler(30)`（`Connection$1` 客户端 / `ServerConnectionListener$1` 服务端） | 30 秒收不到任何字节即断开 | **120 秒** | 双向都有；弱网抖包时最先掐断的就是它 |
| `ServerGamePacketListenerImpl.tick()` 的 KeepAlive 判定 | `>= 15000L` 未回应即 `disconnect("Timed out")` | **60000L** | 心跳间隔变长（流量更小），容忍窗口约 120 秒 |

实现：Espetro 新增 3 个 mixin（`org.espetro.mixin.network.*`，常量集中在 `EspetroNetworkTuning`，改回 30 / 15000L 即可还原）：
`KeepAliveTimeoutMixin`（ModifyConstant 15000L）、`ClientReadTimeoutMixin`（ModifyConstant 30）、
`ServerReadTimeoutMixin`（Redirect `ServerConnectionListener.READ_TIMEOUT`）。
已用 CFR 反编译线上 Forge 类确认注入点存在，并核对构建产物 refmap：`tick => m_9933_()V` ✓、
两个内部类目标 ✓。部署后启动日志无任何 mixin 失败。

### 10.2 服务端网络参数（`server.properties`，已改并回下载确认）
| 项 | 原值 | 新值 | 影响 |
|---|---|---|---|
| `view-distance` | 32 | **12** | 进服与平时的区块流量降约 7 倍（32→12 是 65²→25² 区块） |
| `simulation-distance` | 24 | **8** | 服务端负载与广播量下降 |
| `entity-broadcast-range-percentage` | 1000 | **150** | 原值是默认的 10 倍，实体（载具/玩家/弹丸）广播范围被放大 10 倍 |

备份：`build/tmp/net-tuning/server.properties.bak`（原值 32 / 24 / 1000）。

### 10.3 EsPoints 地图传输预算
`config/espoints-common.toml`：`tacticalMapPlayerTransferKiBps` 256 → **64**，`tacticalMapGlobalTransferKiBps` 4096 → **1024**
（备份 `build/tmp/net-tuning/espoints-common.toml.bak`）。限流后再叠加第一轮的去重，地图不会再瞬间打满上行。

### 10.4 部署
- 构建 `espetro-1.1.3-i.jar`（`BUILD SUCCESSFUL`；内部版本仍为 `1.1.3-i`，满足 espoints 的精确依赖）；
- 服务端以同名 `mods/espetro-1.1.3-x.jar` 覆盖（避免重复 modId），回下载 SHA256 **一致**；客户端同路径替换 ✓；
- 服务端已重启（`Done (3.057s)`，`EsFactions 已冻结: 15 个编制, 217 个职业`，方向抗性 122 辆/176 条），无 mixin 失败；
- **客户端必须重启**才会加载客户端侧 mixin（读超时/心跳）。

### 10.5 仍可优化（未做，待你决定）
1. **客户端 `options.txt`：`renderDistance:32` / `simulationDistance:32`** → 建议 12–16
   （服务端已限到 12，客户端再开 32 只是白耗内存/CPU；改法：游戏内视频设置，或关客户端后我帮你改）。
2. **Xaero 世界地图服务端共享**：`config/xaero/world-map-server.toml`（面板禁止读取子目录配置，我读不到）。
   若开启了 `serverMapSharing`/发送地图数据，会额外推大量地图区块数据 —— 建议你打开确认并关掉。
3. **TaCZ 枪包缓存 1.1 MB/次进服**（你说没问题，未动）：若要省，可在客户端持久化缓存 + 只在哈希变化时重发。
4. **voicechat**：`codec=VOIP`、`mtu_size=1275`、`keep_alive=1000` 已是合理值，无需调整。

## 11. 相关命令/脚本
- 日志：`build/tmp/server-check.log`（服务端二进制副本）、`build/tmp/client-latest.log`、`build/tmp/client-debug-cur.log`
- 脚本：`build/tmp/lag-timeline.mjs`（卡顿时间轴 + 玩家进出）、`build/tmp/log-window.mjs`（按时间窗过滤）、
  `build/tmp/analyze-client-latest.mjs`（客户端连接/异常/末尾）、`build/tmp/read-gz-log.mjs`（解压归档日志）
