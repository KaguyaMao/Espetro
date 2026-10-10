# 装备完全解锁模式（Free Unlock）——已实现

> 状态：**已实现并打包上传，等待重启生效**（协议 1.34 → **1.35**，玩家需同步换客户端 jar）。

## 最终决策（审核结果）

| 项目 | 决定 |
|---|---|
| 实现路线 | **A：加协议字段 `freeUnlock`**（客户端尊重该字段，显示正常） |
| 解锁范围 | 7 项人数限制 + `teammates_need` 全部解锁 |
| 保留 | `leader_only`（仅队长）、职业切换冷却、位置限制、必须先入队、编制归属、载具换职弹药消耗、兵力值结算 |
| 生效期 | **仅本局**：对局结束（回城 / 强停 / 重置）自动关闭；服务器重启自然关闭 |
| 指令 | `/espetro freeunlock <on|off|status>`（permission 2） |

## 实现位置

| 文件 | 改动 |
|---|---|
| `team/FreeUnlockManager.java`（新增） | 开关状态、广播、日志、`refreshClients()`、`onRoundEnded()` |
| `team/ClassCountManager.selectClassVariant()` | 7 项人数判定 + `teammates_need` 用 `if (!freeUnlock && ...)` 包起来（权威闸门） |
| `network/NetworkManager.sendVehicleClassSelect()` | 开启时不再产生人数类 `denial`；`variantEnabled` 放行 |
| `network/NetworkManager.resendDeployScreen()`（新增） | 开关变化后重发部署界面（不打开面板） |
| `network/RadioRadialPacket`（列表构建） | 同上（Radio 轮盘路径） |
| `network/UnifiedDeployScreenPacket` | 新增包级字段 `freeUnlock` + getter + 读写 |
| `network/NetworkManager.PROTOCOL_VERSION` | `1.34` → `1.35` |
| `client/ClientPacketHandlers.handleUnifiedDeployScreen` | 应用 `freeUnlock`，变化时刷新职业按钮 |
| `client/gui/UnifiedDeployScreen` | 静态 `freeUnlockMode` + `applyFreeUnlock()` + `refreshForFreeUnlock()`；`isClassButtonDisabled()` / `resolveClassDenialMessage()` 在开启时只保留位置/冷却/入队判定 |
| `command/EspetroCommand` | 新增 `freeunlock` 子命令（on/off/status） |
| `team/GameStateManager` | `resetGame()`、强制结束、`beginCleanup()` 三处调用 `FreeUnlockManager.onRoundEnded()` |

> 遗留：`OpenClassSelectionPacket` + 旧 `ClassSelectionScreen` 里还有一份客户端人数判定，
> 但它们已经**没有发送方**（`sendClassSelectionScreen` / `broadcastClassSelectionScreen` 无调用者），
> 当前部署流程只走 `UnifiedDeployScreen`，故未改动；若以后重新启用旧界面需要一并处理。

## 行为要点

- 服务端仍是唯一权威：客户端放开只是 UI；真正放行发生在 `ClassCountManager`。
- 人数计数照旧累加（可能超过上限），只是不再拦截；`3/2` 这类显示保留原样（A 路线的意义）。
- 开关切换即时生效：广播全员 + 重发部署界面与 Radio 职业列表，无需重开界面。
- 关闭后立刻恢复原有拦截（同一局内可反复开关）。
- 对局结束自动关闭并广播"本局结束，装备完全解锁模式已自动关闭"。

## 待办

- 重启后按测试计划验证（3 人同选 `maxPlayers=1` 的职业、界面不置灰、关闭即恢复、
  位置/入队/冷却/队长限定仍在、对局结束自动关闭）。


---

## 1. 现状：锁定发生在 4 个地方

| # | 位置 | 作用 | 性质 |
|---|---|---|---|
| 1 | `ClassCountManager.selectClassVariant()`（213–322） | **真正的准入判定**（`ClassSelectPacket` 是唯一调用方） | 服务端权威 |
| 2 | `NetworkManager.sendVehicleClassSelect()`（535–590） | 部署/载具界面的职业列表包（`enabled` + `denial` 文案） | 服务端下发 |
| 3 | `RadioRadialPacket`（245–334） | Radio 轮盘路径的同款列表（逻辑与 #2 重复的一份） | 服务端下发 |
| 4 | 客户端 `UnifiedDeployScreen`：`isClassButtonDisabled()`（3146–3178）、`resolveClassDenialMessage()`（3088–3130）、`selectClass()`（3325–3342） | 部署界面**本地重算**置灰与拒绝原因，点击时先本地拦一次 | 客户端 |

**关键点**：部署界面的置灰**不看**服务端 `enabled`，而是拿 `ClassInfo` 的
`teammatesNeed / unlockMinSquad / unlockPerN / teamCount / maxPlayers / maxPerSquad /
squadCurrentCount / currentCount` 自己算。而 Radio 轮盘那条路是服务端给的 `enabled/denial`。
→ 这就是下面两种路线的由来。

---

## 2. 解锁范围（逐项）

**人数类（要解锁）**
1. `teammates_need`：小队至少 N 人才能选
2. `unlock_min_squad`：小队满 N 人解锁该职业
3. `unlock_per_n`：每 N 人解锁 1 个名额 + 名额用尽
4. `team_count=true` 职业的小队上限 `maxPlayers`
5. 非 `team_count` 职业的全队上限 `maxPlayers`
6. `max_per_squad`：小队上限
7. `strict_count` 变体（倍镜/机瞄等）的独立上限 `maxPlayers`

**保留不动（非人数限制）**
- 位置限制（只能在部署点 / 主基地附近 / 己方 Radio 范围内选）
- 必须先加入小队（`REQUIRES_SQUAD`）——客户端与服务端都拦，属于流程要求
- 职业切换冷却（见问题 3）
- `leader_only`（仅队长可选）——属于权限而非人数（见问题 2）
- 编制归属校验（不能选别的编制的职业）
- 载具换职的弹药消耗（照扣）
- `troopValue` / 兵力结算（不动）

---

## 3. 两条实现路线

### 路线 A：加协议字段（体验正确，需要客户端换包）
- `UnifiedDeployScreenPacket` 增加 `freeUnlock`；客户端 `isClassButtonDisabled()` 在开启时
  只保留「位置 / 冷却 / 入队」三项，其它一律放行，`resolveClassDenialMessage()` 返回空串。
- `RadioRadialPacket`：服务端直接给 `enabled=true, denial=""` 即可（这条本来就由服务端控制，无需改客户端结构）。
- **协议 `1.34 → 1.35`，玩家必须换客户端 jar**（上次刚换过一次）。
- 显示完全正常：人数照常显示 `3/2`，只是不再置灰、不再拦点击。

### 路线 B：不改客户端，服务端"伪装数字"（零客户端改动）
- 开启时下发"无限制"的数值：`maxPlayers=9999`、`maxPerSquad=0`、`unlockPerN=0`、
  `unlockMinSquad=0`、`teammatesNeed=0`；Radio 路径直接 `enabled=true, denial=""`。
- 客户端本地判定自然全部通过，按钮变亮、点击不再被拦。
- **代价**：部署界面会显示 `3/9999` 这类数字（`getSquadDisplayCap` 也会返回大数），观感较差；
  要修显示就又得动客户端。
- **不需要协议变更、不需要玩家换包**，随时可上可撤。

> 我的建议：**A**（干净、显示正确）。如果你近期不想让玩家再换一次包，就用 **B**，
> 我把数字显示的处理留到下次客户端更新时一并修。

---

## 4. 指令、状态与反馈

- 指令：`/espetro unlock <on|off|status>`（permission 2，沿用 `/espetro` 根命令的权限门）。
  也可叫 `/espetro freeunlock`，或加中文别名。
- 状态：新增 `FreeUnlockManager`（`static volatile boolean enabled`）：
  - `isEnabled()` / `setEnabled(boolean)` / `toggle()`；
  - 默认**不持久化**（重启即恢复关闭）——更安全的默认；需要常开的话可以落盘（见问题 4）。
- 切换时：
  1. 广播全员：`§e[系统] 装备完全解锁模式已开启/关闭`；
  2. **立即给在场玩家重发职业列表包**（`UnifiedDeployScreenPacket` + `RadioRadialPacket`），
     否则要重开界面才会变；
  3. 服务器日志记录：谁、何时、开/关。
- （A 路线可选）部署界面顶部显示一个「完全解锁」标识，避免管理员/玩家困惑。

---

## 5. 实施清单（审核通过后）

**新增**
- `org/espetro/team/FreeUnlockManager.java`（开关状态 + 变更回调）
- （A 路线）`UnifiedDeployScreenPacket` 的 `freeUnlock` 字段与读写

**修改**
| 文件 | 改动 |
|---|---|
| `ClassCountManager.selectClassVariant()` | 7 项人数判定用 `if (!FreeUnlockManager.isEnabled())` 包起来 |
| `NetworkManager.sendVehicleClassSelect()` | 开启时 `denial=""`、`enabled=true`、`variantEnabled=true`；（B 路线）数值伪装 |
| `RadioRadialPacket`（列表构建） | 同上 |
| `NetworkManager`（`UnifiedDeployScreenPacket` 构建，~1407） | （A）带上 `freeUnlock`；（B）数值伪装 |
| `client/gui/UnifiedDeployScreen` | （A）`isClassButtonDisabled`/`resolveClassDenialMessage` 尊重 `freeUnlock` |
| `EspetroCommand` | 新增 `unlock` 子命令 + 帮助文案 |
| （A 路线）`NetworkManager.PROTOCOL_VERSION` | `1.34 → 1.35` |

**测试计划**
1. 开 `unlock on` → 3 人同选 `maxPlayers=1` 的职业，全部成功（服务端不再拒）。
2. 部署界面：原本灰色的职业格子变可点（A/B 两种路线分别验）。
3. Radio 轮盘路径同样可选（`enabled/denial` 正确）。
4. `unlock off` → 立即恢复原有限制（同一局内切换也生效，无需重开界面）。
5. 保留项仍在：不在部署点/Radio 范围不能选；没入队不能选；切换冷却仍在（若按你的选择保留）。
6. 换变体（倍镜/机瞄）在 `strict_count` 下也不再被拦。
7. 重启后状态符合预期（默认关闭 / 或按你选择的持久化）。
8. 载具换职路径仍然正常扣弹药。

---

## 6. 待你确认

1. **路线 A（+客户端换包，显示正确）还是 B（不改客户端，界面会显示 9999）？**
2. `leader_only`（仅队长可选）要不要一起解锁？
3. 职业切换冷却要不要一并去掉？
4. 模式是否**重启后保留**？（默认不保留）
5. `teammates_need`（小队至少 N 人）算不算人数限制一起解锁？（我倾向算）
6. 指令名 `/espetro unlock <on|off|status>` 可以吗？
