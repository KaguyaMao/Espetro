# 普通成员使用 Ping-Wheel 原版标点 —— 实施方案（已批准并实施）

> 目标：**所有人都能用 Ping-Wheel 原版标点**；队长/组长/载具乘客在保留原版标点的同时，
> 仍能用长按进入 EsPoints 战术标点轮盘。
> 改动只在 **EsPoints**（1 个文件 + 既有 mixin），Espetro 与协议不变。

---

## 1. 现状（代码级证据）

`EsPoints` 的 `com.example.espoints.client.TacticalMarkRadialController`：

- `tick()` 每 tick 无条件 `PingController.revokePingAction()`；
- `shouldSuppressDefaultPing()` 只判断"是否活跃战场"，**不看身份** →
  `mixin/pingwheel/PingControllerMixin` 在 `PingController.pollPingAction` HEAD 把
  **所有人的**原版标点都 cancel 掉；
- 之后 EsPoints 用**同一个按键**（Ping-Wheel 自己的 `key.pingwheel.ping_location`）打开自己的
  战术轮盘，而该轮盘只允许指挥官/小队长/火力组长 →
  **普通成员按键只弹一句 `§c当前身份不能放置战术标点。`，按键被吞到松手**。

Ping-Wheel 1.12.1 机制（反编译确认）：

| 事实 | 证据 |
|---|---|
| 标点键默认是**鼠标侧键 4** | `InputUtils` 静态初始化：`new KeyMapping("key.pingwheel.ping_location", InputConstants.Type.MOUSE, 4, ...)` |
| 按住期间**每 tick** 排队 | `CommonClient.onTickStart`：`if (InputUtils.consumePingHotkey()) queuePingAction();`，而 `consumePingHotkey()` 返回 `KEY_BINDING_PING.isDown()` |
| 排队后立刻发射射线并发包 | `PingController.pollPingAction` → `performPingAction`（raycast + `PingLocationC2SPacket`） |
| 撤销只是清布尔量 | `revokePingAction()` = `pingQueued = false`；`queuePingAction()` = `true` |
| 没有"轮盘/环"UI | jar 内无任何 wheel/radial 类，只有 `PingView` + 三种渲染器 |

⇒ 因此可以在"按住"与"松开"之间自由接管/放行，实现 **短按=原版标点、长按=战术轮盘**。

另外核对：`PingViewMixin` / `PingLocationRendererMixin` / `DirectionIndicatorRendererMixin` /
`DrawContextMixin` 都只对 **EsPoints 自己托管**的 `PingView`（战术标点贴图替换、生命周期）
生效，**不会隐藏原版标点**。

## 2. 实施内容（`EsPoints/client/TacticalMarkRadialController.java`）

1. **`shouldSuppressDefaultPing()`** 改为三重条件：
   ```java
   isActiveBattlefield(mc) && InputUtils.KEY_BINDING_PING.isDown() && canLocalPlace()
   ```
   - 普通成员（无战术标点权限）→ false → **原版标点完全不拦截**；
   - 有权者按住期间 → true → 原版标点被压制，改由战术轮盘接管；
   - **松开后立即放行**（关键：补发的原版标点必须能通过）。
2. **移除 `tick()` 里无条件的 `PingController.revokePingAction()`**（改由 mixin 在 poll 时精确压制）。
3. **按键分流**：
   - `canLocalPlace() == false`（普通成员）：直接 `return`，**不碰按键、不提示、不吞键**；
   - `canLocalPlace() == true`：
     - 按住 `heldTicks < OPEN_DELAY_TICKS` 后松开 → `PingController.queuePingAction()`
       补发一次原版标点（**短按 = 原版标点**）；
     - 按住 ≥ `OPEN_DELAY_TICKS` → 打开 AuraTip 战术标点轮盘；
     - 轮盘打开后继续按住 ≥ `RADIAL_MIN_HOLD_TICKS` 再松开 → 确认悬停槽位（原行为）；
     - 轮盘刚打开就松开 → 视为"慢速短按"：关闭轮盘 + 补发原版标点，
       **避免误放战术标点**。
4. 删除普通成员的 `§c当前身份不能放置战术标点。` 提示（已无意义）。

常量（可按手感调整）：

| 常量 | 值 | 含义 |
|---|---|---|
| `OPEN_DELAY_TICKS` | `6`（300ms，原 4=200ms） | 超过该按住时长才开轮盘 |
| `RADIAL_MIN_HOLD_TICKS` | `3`（150ms） | 轮盘打开后至少再按住这么久才在松开时确认槽位 |

## 3. 改后行为矩阵

| 场景 | 短按（<300ms） | 长按（≥300ms） |
|---|---|---|
| 普通成员 | **Ping-Wheel 原版标点**（照常，按住期间持续） | 同左（原版标点） |
| 指挥官 / 小队长 / 火力组长 | **原版标点** | **EsPoints 战术标点轮盘** |
| 载具内乘客（含普通成员） | **原版标点** | **战术轮盘** |
| 主城 / 非战局 / 主世界 | 原版标点（本来就未接管） | 原版标点 |

其他不变：EsPoints 战术标点（创建）服务端仍只允许领导类；`PingWheelMarkerBridge` 只管理
自己的 PingView，不清除原版标点；Ping-Wheel 服务端配置 `defaultChannelMode: AUTO`
（Espetro 记分板队伍 → 自动走队伍频道）无需改动。

## 4. 部署与验证

- 构建命令（**必须带 apricityui 覆盖**，见下方陷阱）：
  ```powershell
  cd D:\minecraft\modp\EsPoints
  $env:JAVA_HOME='C:\Program Files\Java\jdk-21.0.11'
  .\gradlew.bat build -x test "-Papricityui_version=1.2.2-hotfix1"
  ```
  > ⚠️ **陷阱**：仓库 `gradle.properties` 写的是 `apricityui_version=1.2.3.1`，但服务端与客户端
  > 装的都是 `[晴雪UI] ApricityUI-forge-1.20.1-1.2.2-hotfix1.jar`。直接构建会让 mods.toml 声明
  > `versionRange="[1.2.3.1]"`，服务端启动即崩：
  > `Mod 'espoints' requires 'apricityui' '1.2.3.1' / Currently 'apricityui' is '1.2.2-hotfix1'`。
  > 用 `-P` 覆盖即为旧 jar 完全一致的依赖声明（已比对 mods.toml 无差异）。
  > PowerShell 里该参数**必须加引号**，否则会被拆成 `.2.2-hotfix1` 任务名而报
  > `Task '.2.2-hotfix1' not found`。
- 构建 `EsPoints` → `espoints-1.1.1b.jar`（**版本号不变**，协议不变）；
- 上传服务端 `mods/` 并重启（客户端侧逻辑，服务端保持一致）；
- 同步本地客户端 `versions/Squad预发布测试/mods/`；
- 验证清单：
  1. 普通成员战局内短按标点键 → **原版标点出现**，队友可见、有声音/方向指示；
  2. 普通成员**不再**收到"当前身份不能放置战术标点"提示；
  3. 小队长短按 → 原版标点；长按 → 战术标点轮盘，选类型可放；
  4. 载具内乘客短按 → 原版标点；长按 → 战术轮盘；
  5. 主城/非战局 → 原版标点；
  6. 不出现"两个轮盘同时弹出"、不出现松开后残留、不出现误放战术标点。
