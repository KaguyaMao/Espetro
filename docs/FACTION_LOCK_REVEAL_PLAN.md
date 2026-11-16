# 指定编制地图：跳过编制投票 + 5 秒展示页 —— 实现方案

> 目标：地图若已指定某一方的编制（当前来自 `amphibious.json` 的 `assault_faction`），该方**不再进行编制投票**，
> 改为展示 5 秒（版式同“地图投票结束”的揭晓页），随后进入另一方的编制投票；若两侧都被指定，则展示后直接进入现有
> 双方编制揭示（`FACTION_REVEAL`）。

## 实现状态（2026-11-11，已上线）

| 项 | 状态 |
|---|---|
| `GamePhase.FACTION_LOCK_REVEAL` | ✅ 新增 |
| `ClassSelectManager.factionLockFor(team)` / `isFactionLockUsable` / `forceFaction` | ✅ |
| `GameStateManager.startFactionLockReveal` / `finishFactionLockReveal` / tick 分支 | ✅ |
| `FactionLockRevealPacket` + `NetworkManager.broadcastFactionLockReveal` | ✅ |
| `client/gui/FactionLockRevealScreen`（主标题 / 副标题“已自动选择唯一可用编制” / 编制名 / 倒计时） | ✅ |
| `game.json` → `faction_lock_reveal_seconds`（默认 5） | ✅ `GameSettingsSnapshot` + `GameConfig` |
| 协议号 | **1.38**（`79 个网络包，判别号 0~78`） |
| jar | `espetro-1.1.3-l.jar` 17,056,110 B，双端同字节；12:33 重启 `Done (16.950s)!` |
| 顺带修 | `TutorialStep.primaryForPhase` 与 `UnifiedDeployScreen.refreshTitleTimer` 的穷举 switch 补新相位 |

待实机验收：island 开一局，确认攻方不再弹编制投票、5 秒展示页文案与图片正确、随后进入守方投票。

## 0. 结论 / 改动规模

| 项 | 内容 |
|---|---|
| 新增相位 | `GamePhase.FACTION_LOCK_REVEAL`（5s） |
| 新增数据包 | `FactionLockRevealPacket`（服务端 → 客户端） |
| 新增界面 | `client/gui/FactionLockRevealScreen`（照 `MapRevealScreen` 版式） |
| 改动文件 | 8 个，约 320 行 |
| 协议号 | `1.37 → 1.38`（新增包，客户端必须同版本） |
| 配置 | `game.json` 新可选键 `faction_lock_reveal_seconds`（默认 5） |

## 1. 现状（已核对源码）

阶段链（`src/main/java/org/espetro/team/GamePhase.java`）：

```
LOBBY → MAP_VOTE → MAP_REVEAL(5s) → MAP_LOADING → TEAM_SELECT → TEAM_ASSIGN_SHOW(5s)
      → COMMANDER_VOTE → [DEFEND|ATTACK]_FACTION_SELECT(30s) → FACTION_REVEAL(5s)
      → DEPLOYING → BATTLE → ROUND_END → CLEANUP → LOBBY
```

- 编制锁已存在但只作用于“候选池”：`ClassSelectManager.getAvailableFactionPoolForTeam(team)`
  （`ClassSelectManager.java:161-166`）对 `ATTACK` 返回单元素列表 `[assault_faction]`。
  于是攻方仍然要**等满 30 秒**，且界面仍是一个可点击的投票框。
- 阶段推进：`GameStateManager.startFirstFactionSelect()`（`:699`）→ `startFactionSelectForTeam()`
  （`:711`）→ `startAttackFactionSelect()/startDefendFactionSelect()`（`:683-694`）；
  选择结束 → `onFactionSelectFinished()`（`:719`）→ 若先手方刚结束则切另一方，否则 `startFactionReveal()`。
- 计时：`onServerTick()` 的 `DEFEND_FACTION_SELECT / ATTACK_FACTION_SELECT` 分支（`:1335-1346`）
  调 `ClassSelectManager.isCurrentSelectTimedOut()`；`MAP_REVEAL` 分支（`:1290-1296`）是 5s 展示页范式。
- 5 秒展示页现有实现：`MapRevealPacket` + `MapRevealScreen`（640×360 图 + 倒计时），
  由 `GameStateManager.mapRevealTickCounter >= 5 * TICKS_PER_SECOND` 推进（`:1292-1295`）。
- 编制显示名/图片：`NetworkManager.getFactionDisplayName(id)` / `getFactionSelectionImage(id)`（`:1289-1292` 已在用）。

## 2. 设计

### 2.1 跳过投票的判定点

新增服务端私有分派，替换 `startFactionSelectForTeam`：

```java
private void startFactionSelectForTeam(String team) {
    String locked = ClassSelectManager.factionLockFor(team);   // 新增，按队返回锁定编制
    if (locked != null) {
        ClassSelectManager.getInstance().forceFaction(team, locked);  // 直接定案，不开投票
        startFactionLockReveal(team);
        return;
    }
    if ("DEFEND".equals(team)) startDefendFactionSelect(); else startAttackFactionSelect();
}
```

`startFactionLockReveal(String team)`：

```java
setPhase(GamePhase.FACTION_LOCK_REVEAL);
lockRevealTeam = team;
lockRevealTickCounter = 0;
NetworkManager.broadcastFactionLockReveal(team, lockedId, GameConfig.getFactionLockRevealSeconds());
Espetro.LOGGER.info("{} 编制由地图指定: {}，展示 {} 秒", team, name, seconds);
```

`onServerTick()` 新分支：

```java
case FACTION_LOCK_REVEAL:
    if (++lockRevealTickCounter >= seconds * TICKS_PER_SECOND) finishFactionLockReveal();
    break;
```

`finishFactionLockReveal()`：若另一方仍需投票 → `startFactionSelectForTeam(otherTeam)`；
否则 → `startFactionReveal()`（现有双方揭示页）。

### 2.2 两侧都被指定（预留）

`FACTION_REVEAL` 前会依次走两次 `FACTION_LOCK_REVEAL`（每侧 5s）。若以后觉得拖沓，可改成一次
双栏展示（复用 `FactionRevealPacket`），属可选优化。

### 2.3 `ClassSelectManager` 新增 API

```java
/** 该队是否被地图锁定编制（锁存在且与地图兼容时返回编制 id，否则 null）。 */
public static String factionLockFor(String team);

/** 现有 isLockUsable 提升为 public。 */
public static boolean isFactionLockUsable(String factionId);

/** 把指定队伍的最终编制直接定案（不投票）。 */
public void forceFaction(String team, String factionId);
```

`forceFaction` 内部：`selectingActive = false`、`currentSelectingTeam = team`、
`finalAttackClass/finalDefendClass = factionId`、聊天播报
`§6===== 攻方编制已由地图指定: §c<名字>§6 =====`、`LOGGER.info`。

### 2.4 数据包 + 客户端界面

`FactionLockRevealPacket`（`org.espetro.network`）：

| 字段 | 类型 | 说明 |
|---|---|---|
| `team` | `String` | `ATTACK` / `DEFEND` |
| `factionDisplayName` | `String` | 编制显示名 |
| `factionId` | `String` | 用于取选择图（客户端按需） |
| `durationSeconds` | `int` | 倒计时秒数 |

`write/read` 用 `writeUtf`/`writeVarInt`；`handle` 反射调
`ClientPacketHandlers.handleFactionLockReveal(this)`（与 `MapRevealPacket` 同款写法）。

`FactionLockRevealScreen extends EspetroMenuScreen`：版式对齐 `MapRevealScreen`（图片框 640×360、
标题一行、倒计时一行），自上而下四行文本：

- 主标题：`§e<守方/攻方>编制已指定`
- **副标题：`§7已自动选择唯一可用编制`**（固定文案，说明“没有投票、直接选定”的原因）
- 编制名：`§c<编制显示名>`（`UiTheme` 主色，字号最大）
- 图片：`faction_selection_image` 纹理（复用 `MapRevealScreen` 的加载/缓存写法）
- 底部：`§7<N> 秒后进入<下一步>`

副标题文案不带队伍名，两队被锁时显示一致；若要区分也可改成
`已自动选择唯一可用的<守方/攻方>编制`（实现时按需一行切换）。

`ClientPacketHandlers.handleFactionLockReveal` → `Minecraft.getInstance().setScreen(...)`。

### 2.5 配置项

- `game.json`：`"faction_lock_reveal_seconds": 5`（缺省 5，clamp 1–30）。
- `GameSettingsSnapshot` 新字段 `factionLockRevealSeconds`（构造器末尾追加，`defaults()` 传 5，
  `parse` 读 `faction_lock_reveal_seconds`）；`GameConfig.getFactionLockRevealSeconds()` + `applySnapshot` 复制。
- `config/espetro` 的模板默认值同步（`src/main/resources` 里默认 game.json 若有）。

### 2.6 协议

`NetworkManager.PROTOCOL_VERSION` **1.37 → 1.38**（`NetworkManager.java:31`）。
服务端与客户端 jar 必须同时更新；旧客户端会在握手阶段被拒（预期行为，报错信息已友好）。

### 2.7 观察者 / 中途加入

- `notifySpectatorsDuringVoting()` 的 `voting` 集合补入 `FACTION_LOCK_REVEAL`（`:1256-1261`）。
- 展示页仅 5 秒，中途加入者不补发（进入下一相位后按现有 `broadcast*` 逻辑同步）。

## 3. 边界与回退

| 情况 | 行为 |
|---|---|
| 锁定的编制被删 / 与地图不兼容 | `factionLockFor` 返回 null → 回退正常投票，并 `LOGGER.warn("锁定编制 {} 不可用，攻方回退投票")` |
| 地图没有 `assault_faction` | 行为与现在**完全一致**（不新增相位） |
| RAAS（对称模式）先手随机 | 先手若被锁 → 直接展示并跳到另一侧；未涉及 `firstFactionSelectTeam` 的现有语义 |
| 玩家在展示期间退出/重连 | 5 秒后照常推进，不影响相位 |

## 4. 验收清单

1. **island**（`assault_faction = plamc_5th_amphibious_assault`）：
   - 指挥官投票结束 → **不再出现攻方编制投票界面**；
   - 出现 5 秒展示页：主标题 `攻方编制已指定`，**副标题 `已自动选择唯一可用编制`**，编制名
     `第5海军陆战联合兵种旅`；
   - 随后进入**守方**编制投票；守方选完 → 现有双方揭示页。
2. **非两栖图**（如 CREATE_PLUS）：流程与现在逐帧一致（攻方 30s 投票照旧）。
3. 日志关键字：`编制由地图指定`、`FACTION_LOCK_REVEAL`、`锁定展示结束，进入 DEFEND 编制投票`。
4. `updatePlayerFactions` 后每个攻方玩家的 `faction` 均为锁定编制；开局载具/职业正确。
5. 锁不可用时回退投票（可临时改 `assault_faction` 成不存在的 id 验证告警）。

## 5. 工作量

| 文件 | 改动 | 行数 |
|---|---|---|
| `team/GamePhase.java` | 新枚举值 + `getActiveTeam`/`isFactionSelectPhase` 兼容 | ~10 |
| `team/GameStateManager.java` | 分派、`startFactionLockReveal`、tick 分支、`finishFactionLockReveal`、观察者集合 | ~90 |
| `team/ClassSelectManager.java` | `factionLockFor` / `isFactionLockUsable` / `forceFaction` | ~50 |
| `network/FactionLockRevealPacket.java` | 新文件 | ~70 |
| `client/gui/FactionLockRevealScreen.java` | 新文件 | ~80 |
| `client/ClientPacketHandlers.java` | 新处理入口 | ~10 |
| `network/NetworkManager.java` | 注册 + `broadcastFactionLockReveal` + 协议号 | ~20 |
| `config/GameConfig.java` + `mapconfig/GameSettingsSnapshot.java` | 新配置键 | ~20 |

合计约 **350 行 / 8 文件**。
