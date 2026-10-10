# 普通玩家头顶标识 —— 设计方案（待审批）

> 目标：除指挥官 / 小队长 / 火力组长之外，为**所有友军**加头顶标识；普通标识为队长标识的 **1/5 大小**。

---

## 1. 需求映射

| 关系 | 贴图 | 数字 | 尺寸 |
|---|---|---|---|
| 其它小队成员 | `squad_leader.png` | 无 | 队长标识的 1/5 |
| 本队 **A** 组成员 | `self_squad_leader.png` | 无 | 1/5 |
| 本队 **B** 组成员 | `fireteammate_b.png`（你新加） | 无 | 1/5 |
| 本队 **C** 组成员 | `fireteammate_c.png`（你新加） | 无 | 1/5 |

## 2. 现状（`client/LeaderOverheadRenderer.java`，190 行）

- **数据来源**：`ClientTacticalState.getMarker(玩家名)` → `MarkerInfo{squadId, displayId, fireteam, leader, commander, fireteamLeader}`。
  服务端只下发**己方阵营**的小队名单（`NetworkManager.buildSquadInfoList(team)` → `getSquadSnapshots(team)`），
  因此现有的"其它小队"= **友军其它小队**，不存在给敌人显示标识的问题。
- **分组编码**：`Fireteam.A=0 / B=1 / C=2`（`Fireteam.toNetwork()`），包字段为 byte。
- **现有三类标识**：
  | 类型 | 判定 | 贴图 | 半径 | 数字 |
  |---|---|---|---|---|
  | 指挥官 | `commander` | `commander.png` | 200 格 | — |
  | 小队长 | `leader && squadId>0` | 本队 `self_squad_leader.png` / 其它队 `squad_leader.png` | 本队无限、其它 50 格 | **画小队编号** |
  | 火力组长 | `fireteamLeader && (ft==1‖2)` | B→`fireteam_b.png`、C→`fireteam_c.png` | 本队 + 50 格 | — |
  | **普通成员** | 目前 `resolve()` 返回 null → **完全没有标识** | — | — | — |
- **绘制方式**：`RenderType.textSeeThrough(贴图)`，±1 四边形 × `HALF = 0.14`（世界宽度 0.28），
  UV 水平翻转避免背面剔除；高度 = 实体高度 + 1.0。
- **车内规则**：每辆车只画"军衔最高"的那一个标识（`rankOrdinal`：指挥官 0 < 队长 1 < 组长 2）。
- **跳过**：自己、旁观、隐身、死亡、非对局阶段。
- **贴图尺寸**：7 张全是 **64×64**（含你新加的两张）→ 沿用同一 ±1 四边形不会被拉伸变形。

## 3. 改动设计（只动客户端一个文件）

1. 新增枚举值 `T.MEMBER`，`rankOrdinal()` 返回 **3**（车内取最高军衔时优先级最低，不会盖掉任何现有标识）。
2. `resolve(p)`：指挥官 / 小队长 / 组长的判定**保持原样**；三者都不满足且 `m.squadId() > 0` 时返回 `MEMBER`
   → 于是"所有友军小队成员"都有标识。
3. `textureFor(MEMBER)`：
   - `squadId != mySquadId` → `SQUAD_LEADER_TEX`（`squad_leader.png`，别人小队的成员）
   - `squadId == mySquadId && fireteam == 0(A)` → `SELF_SQUAD_LEADER_TEX`
   - `fireteam == 1(B)` → `FIRETEAMMATE_B_TEX`（新）
   - `fireteam == 2(C)` → `FIRETEAMMATE_C_TEX`（新）
   - 兜底 → `SQUAD_LEADER_TEX`
4. `inRange(MEMBER)`：本队 → 无距离限制（与小队长一致）；其它小队 → ≤50 格（与"其它小队队长"一致）。
5. `render()`：按类型选尺寸——现有三类继续用 `HALF = 0.14`，`MEMBER` 用 `HALF / 5 = 0.028`（**正好 1/5**）；
   编号渲染条件仍是"仅 SQUAD_LEADER" → 普通标识**天然不带数字**，无需额外处理。
6. 新增两个贴图常量 `FIRETEAMMATE_B_TEX` / `FIRETEAMMATE_C_TEX` 指向你新建的 PNG
   （`espetro:textures/gui/overhead/fireteammate_b.png` / `..._c.png`）。

## 4. 影响面

- **性能**：每人每帧一个四边形，最多 59 个友军 → 开销可忽略（现有实现本来就在遍历全部玩家）。
- **安全性**：数据只含己方阵营 → 不会暴露敌人位置。
- **发布方式**：纯客户端渲染 + 新增贴图资源 → **必须随 jar 一起发**（玩家要换客户端）；
  但**不改任何网络包，`PROTOCOL_VERSION` 不必升**（旧客户端只是看不到普通成员标识，不会报错/崩）。
- 观战/主城（非对局）不显示，保持与现有标识一致。

## 5. 待你确认（4 项）

1. **A 组组长现状是"无标识"**：代码里组长标识只对 `ft==1(B) / ft==2(C)` 生效，**A 组组长目前什么都不显示**。
   按新规则他会落进"普通成员"桶 → 变成 1/5 的 A 组标识。是否要给 A 组组长一个**组长级**标识
   （用 `self_squad_leader.png` 全尺寸？还是你再画一张 `fireteam_a.png`）？
2. **距离**：其它小队成员 ≤50 格、本队成员无限制 —— 可以吗？（也可统一 50/75 格）
3. **车内成员**：现在每辆车只画"军衔最高"的一个标识；若车内全是普通成员，就会画一个小标识。
   要不要**车内不画普通成员标识**（车内只保留指挥官/队长/组长）？
4. **本队成员是否需要距离上限**：本队人多时（一个满编小队 8 人 + 其它小队）会同时出现很多小标识，
   要不要本队也给个上限（例如 100 格）以免刷屏？

## 6. 落地步骤（审批后）

1. 改 `LeaderOverheadRenderer`（上节 6 处）；
2. `gradlew build`（贴图已在你源码里，会自动打进 jar）；
3. 同步服务端 `mods/espetro-1.1.3-x.jar` + 你的本地客户端（服务端其实不需要这个渲染改动，
   但两端 jar 保持一致可避免混淆；协议不变）；
4. 本地/单机或进服实测：四个人分到 A/B/C 组 + 另一支小队，检查四种贴图与 1/5 大小、无数字、
   车内取最高军衔、无敌方标识。
