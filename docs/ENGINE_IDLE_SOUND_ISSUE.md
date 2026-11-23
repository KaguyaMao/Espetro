# dragonrise 载具「熄火后仍有怠速声」问题总结

> 状态：**仅诊断，未修复**（用户要求先存档）
> 记录时间：2026-11-19 01:00（Asia/Shanghai）
> 涉及：`dragonrise_reforge`（GScode 仓库）客户端音效系统、`superbwarfare` 原生引擎声、`Espetro` 部署期引擎锁

---

## 1. 现象

玩家在服务器上把 dragonrise 载具（示例 `m1a2sepv1`、`zbd04a`）**熄火**后，仍能听到发动机怠速声；声音来源无法从游戏内判断（没有字幕/来源提示）。

## 2. 复现环境

| 项 | 值 |
|---|---|
| 客户端 mods | `D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\dragonrise_reforge-1.5.1-20261118.jar`（144,842,484 B，sha256 `1d60e88e23ed62e25a097acc65daa8fbf41be596ed7eda69087ce132d0b7a609`） |
| 服务端 mods | `mods/dragonrise_reforge-1.5.1-20261118.jar`（144,842,484 B，同一 sha256，逐字节一致） |
| 服务端其他 | `espetro-1.1.3-x.jar` 9,043,378 B（含部署期引擎锁）、`fcp-2.0.0.jar` 180,838,976 B（multicrew 模式） |
| 客户端 kubejs | `...\versions\Squad预发布测试\kubejs\client_scripts\zz_engprobe_client.js`（临时诊断脚本，见 §6） |

> 说明：`dragonrise` 与 `fcp` 都有同一套 Squad 移植音效系统（`SquadEngineSounds` / `SquadEngineProfiles` / `SquadEngineProfile` / `EngineKeyHandler`），两边代码几乎相同，只是命名空间与 profile 不同。

## 3. 已确认的事实（都有证据）

### 3.1 客户端实测读数（我方探针，1 号版）
```
[engprobe-c] nearby=1
[engprobe-c] dragonrise_reforge:m1a2sepv1 id=86 state=0 applies=true squadSound=true
             engineStart=false power=0 engineRunning=false nativeVol=0
```
- `state=0` = `EngineControl.OFF`（同步字节）；`applies=true` 表示该车受引擎状态机管辖；`squadSound=true` 表示 `SQUAD_SOUND_VEHICLES` 命中（我的声组接管）。
- `engineRunning=false`、`nativeVol=0` 说明 **superbwarfare 原生引擎声在这台车上既不满足播放条件、音量也被压成 0**。

### 3.2 服务端实测读数
在服务端用一次性 KubeJS 脚本扫全服 40 台载具，全部为：
`state=0 / engineStart=false / power=0 / squadSound=false`（`squadSound=false` 因为该集合由**客户端**在资源重载时填充，服务端为空是正常的）。

### 3.3 两套原代码的门控（都能排除“熄火还响”）
- **superbwarfare 原生声**（`VehicleSoundInstance$EngineSound`）
  - `canPlay(vehicle)` = `vehicle.engineRunning()`
  - `engineRunning()` 字节码：`getVehicleType() == AIRSHIP ? getHealth() > 0 : Math.abs(getPower()) > 0`
  - `getVolume()` = `getEngineSoundVolume()`；后者在 `EngineType` 为 `EMPTY`/`FIXED` 时返回 0
  - 我们的 mixin `VehicleEngineControlMixin#getEngineSoundVolume` HEAD 在 `EngineControl.usesSquadSound(vehicle)` 为真时**直接返回 0**
  - ⇒ `power=0` 时 `engineRunning()=false`，原生声不会播
- **我方声组**（`com.redabysslucia.dragonrise_reforge.client.sound.SquadEngineSounds`）
  - `RANGE = 128.0`、`HELICOPTER_RANGE = 512.0`、`IDLE_HANDOVER = 6`
  - `EngineAudio` 构造器：`level = lastState == RUNNING ? 1f : 0f`
  - `EngineAudio.tick()` 第 188 行：`level = state == OFF ? approach(level, 0f, 0.125f) : targetLevel`
  - 第 223-227 行：`loops.removeIf(sound -> { if (wantsLoop(sound.layer) || sound.getVolume() > 0f) return false; sound.finish(); return true; })`
  - 第 268-270 行：`wantsLoop(layer) = layer.movement.isEmpty() ? level > 0f : movementAmount(layer) > 0f`
  - ⇒ `state == OFF` 时 `level` 每 tick 减 0.125，约 8 tick 归零，之后循环声被 `finish()` 移除

### 3.4 引擎状态机与同步
- `EngineControl`：`OFF=0 / STARTING=1 / RUNNING=2`；`START_TICKS` 里 `zbd04a=56`；`state(v)` = `v instanceof EngineControlled ? dragonrise$getEngineState() : RUNNING`
- 状态是**同步实体数据**（`DRAGONRISE_ENGINE_STATE`，`VehicleEngineControlMixin#defineSynchedData` 定义为 `OFF`），NBT 里 `DragonriseEngineRunning` 决定读档值
- 11-18 的修复：`EngineControl.tick()` 现在对**所有**管辖车镜像 `setEngineStart(running)` / `setEngineStartOver(running)`（原来只有直升机），原先地面车 SBW 侧永远认为引擎在转
- 客户端 `debug.log` 确认 mixin 已注入 `com.atsuishu.superbwarfare.entity.vehicle.base.VehicleEntity`

### 3.5 数据包侧（kubejs 覆盖）与引擎的关系
- 我们的 `kubejs/data/dragonrise_reforge/sbw/vehicles/*.json`（169 个覆盖）是**整份替换**，会把 jar 新增的 `SoundInfo` 子键盖掉；11-18 已补齐 111 个文件 / 569 个键（`VehicleReload`、`VehicleReload3p`、`VehicleReloadSoundTime`、`CancellableSounds`、`Change`）
- 覆盖文件与 jar 在 `EngineType`、`EngineInfo` **键集合**上完全一致（只有数值差异）；数据包里**没有**任何引擎音效开关

## 4. 已排除

| 假设 | 排除依据 |
|---|---|
| SBW 原生引擎声没被静音 | `nativeVol=0` 且 `engineRunning=false` |
| `SQUAD_SOUND_VEHICLES` 为空导致静音钩子失效 | 客户端读数 `squadSound=true` |
| FCP 的声组误命中 dragonrise 车 | `assets/fcp/squad_engines/*.json` 的 `vehicles` 只列 `fcp:*`（如 abrams 是 `fcp:m1a1/m1a2/...`），不含 `dragonrise_reforge:*` |
| 数据包覆盖改变了引擎状态机 | 覆盖只改数值/几何，状态是同步字节；`EngineType` 两边一致 |
| 部署期引擎锁把状态卡在 STARTING | 锁只在 `DEPLOYING` 阶段生效，且探针读数 `state=0` |

## 5. 待验证的假设（按可能性排序）

1. **熄火时播放的是「停车音效」而不是怠速**（最可能）
   `SquadEngineSounds` 第 166 行：状态 `RUNNING → OFF` 时 `playOneShot(mc, profile.stopInt/stopExt)`，而 `assets/dragonrise_reforge/squad_engines/abrams.json` 里 `stopExt` 的 `duration` 是 **29.26 秒**、`stopInt` **24.06 秒**。这段长录音本身就是发动机从高转降下来的声音，玩家很容易听成“还在怠速”。
   → 验证：看 `[engprobe-c] sound` 里正在播的 id 是否是 `.../abrams/exterior_stop`。

2. **客户端残留循环实例**（次可能）
   若某个 `LayerSound` 因 `finish()`/`stop()` 调用顺序问题没有被真正停掉（尤其切维度、断线重连、载具被移除时），实例会以最后设置过的音量继续播放。
   → 验证：`[engprobe-c] sound` 里出现 `.../exterior_idle` 且 `loops=0`（说明不在我方管理列表里）。

3. **驾驶舱内外切换/直升机转子过渡音**（较小）
   `beginRotorTransition` 与 `inside` 切换会创建新实例，旧实例依赖 `clearRotorTransition()` 收尾。

4. **其他模组/环境音**（较小）
   需要 `soundTotal` 全量列表比对。

## 6. 临时诊断工具（已放好，等下一次客户端启动加载）

文件：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\kubejs\client_scripts\zz_engprobe_client.js`

每 40 tick 打印三类信息（前缀 `[engprobe-c]`）：
1. `nearby=N` 与每台车的 `state / engineStart / squadSound / power`
2. 我方声组内部状态（反射 `SquadEngineSounds.ACTIVE`）：每台在管的车的 `level / lastState / loops 数量` 及每个 loop 的 `location + volume`
3. 引擎里**真实在播**的实例：`sound <location> loop=<bool> vol=<float>` 与 `soundTotal=N`

注意（都是踩过的坑）：
- 客户端脚本**只在启动时加载**，`/kubejs reload client_scripts` 不会重载；改脚本必须重启客户端
- KubeJS/Rhino：嵌套块里重复 `var` 会报 `redeclaration of var`；`console.info` 被 ApricityUI 接管，必须用 `console.error`
- 读日志：`...\versions\Squad预发布测试\logs\latest.log`（GBK，用 UTF-8 看中文会乱，关键字用 ASCII）

## 7. 修复候选（等 §5 结论再动手）

| 假设 | 修法 |
|---|---|
| 停车音效太长被误认为怠速 | 缩短 `stopInt/stopExt` 的 `duration` 或改为短淡出；或在 `stop` 一次性音效播放后 3–5 秒强制 `stop()` |
| 残留循环实例 | `EngineAudio.stop()` 里改为 `setVolume(0)` → `finish()` → `mc.getSoundManager().stop(location)`；并在 `LoggingOut`/维度切换时 `stopAll()` |
| 状态不同步 | 在 `SquadEngineSounds.EngineAudio.tick()` 加 `if (state == OFF && loops 非空) 强制清理` 兜底 |

## 8. 关键代码位置

| 文件 | 作用 |
|---|---|
| `GScode/src/main/java/com/redabysslucia/dragonrise_reforge/client/sound/SquadEngineSounds.java` | 声组主控（`ACTIVE`、`EngineAudio`、`Loops`、`wantsLoop`、一次性音效） |
| `.../client/sound/SquadEngineProfiles.java` | 客户端资源重载时读 `assets/<ns>/squad_engines/*.json`，填充 `EngineControl.SQUAD_SOUND_VEHICLES` |
| `.../entity/vehicle/engine/EngineControl.java` | 状态机（`OFF/STARTING/RUNNING`、`START_TICKS`、`tick()` 镜像 SBW、`usesSquadSound`） |
| `.../mixin/VehicleEngineControlMixin.java` | 同步数据 `DRAGONRISE_ENGINE_STATE`、NBT、`baseTick` 调 `EngineControl.tick`、`getEngineSoundVolume` 静音钩子 |
| `Espetro/src/main/java/org/espetro/vehicle/EngineStartGate.java` | 部署阶段禁止发动（反射调用两套 `EngineControl`） |
| superbwarfare `VehicleSoundInstance$EngineSound` / `VehicleEntity#engineRunning` | 原生引擎声门控 |
