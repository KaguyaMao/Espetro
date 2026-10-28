# 非指挥官也能看到/使用技能按钮 —— 修复记录

## 现象（用户报告）
J 菜单（战术面板）左侧那列技能按钮，在非指挥官玩家身上也出现；点击还会走技能触发流程。

## 根因
服务端本身按 `usableBy` 过滤（`CommanderSkillManager.canPlayerUseSkill` → `getSkillViewsFor` 只返回可用技能），
问题是客户端拿到的「是否显示技能入口」来自 `CommanderSkillSyncPacket.showSkillsEntry = isCommander || !views.isEmpty()`，
而该包**只在入服、当选指挥官、发放技能时**单点下发：

- `GameStateManager` 玩家 join 时
- `VoteManager.settleTeam` 选出指挥官后，仅对当选者 `sendCommanderSkillSync`
- `CommanderGovernanceManager.assignCommander` 仅对新任者

从未在「卸任 / 弹劾 / 回合重置」时重新下发 → 客户端 `TacticalRadialController.cachedIsCommander` 残留 `true`，
于是曾经的指挥官（以及换回合后掉身份的玩家）一直看到 J 菜单技能列。
`UnifiedDeployScreen.buildSkillColumn` 的可见性判断又只信这个残留值：

```java
if (!TacticalRadialController.hasSkillSnapshot()
    || !TacticalRadialController.isCommanderSnapshot()) { ... return; }
```

## 修复
### 客户端（权威字段）
`UnifiedDeployScreen.buildSkillColumn` 改为以部署屏同步包里的 `isCommander`（服务端 `voteManager.isCommanderOf(uuid, viewTeam)`）
为准，技能快照只用来取列表：

```java
if (!isCommander || !TacticalRadialController.hasSkillSnapshot()) { lastSkillSignature = Integer.MIN_VALUE; return; }
```

### 服务端（同步时机补齐）
新增 `NetworkManager.syncCommanderSkillsToAll(MinecraftServer)`，在下列时机全服重推技能同步包：

- `VoteManager.settleTeam(...)` 结算结束（换任/新当选）
- `VoteManager.reset()` 清空指挥官后
- `CommanderGovernanceManager.assignCommander(...)`（替换原来只推单人）
- `CommanderGovernanceManager.clearCommander(...)`（弹劾/卸任原来完全不推）

服务端 `activateSkill` 的权限校验本来就是硬门槛，这次只影响可见性与入口收回。

## 构建与部署
- 构建：`gradlew build publishToMavenLocal -x test --offline … -Papricityui_version=1.2.6`
- 产物 `build/libs/espetro-1.1.3-l.jar`，16,869,644 B，SHA256 `7F4C1C6140E45ACB642A8A75C66DEC6FF1548D3DB867D2DE97F02E8A0DF9E339`
- 客户端：`D:\minecraft\squadMC预发布测试\versions\Squad预发布测试\mods\espetro-1.1.3-x.jar`（已覆盖，16,869,644 B）
- 服务端：`/mods/espetro-1.1.3-x.jar` 已上传（回读大小一致），旧件改名为 `espetro-1.1.3-x.jar.bak-skillgate`
- 字节码校验：`UnifiedDeployScreen` 已无 `isCommanderSnapshot` 引用，`buildSkillColumn` 读字段 `isCommander`；
  `NetworkManager.syncCommanderSkillsToAll` 已在 jar 中
- 无新增网络包（包序号表不变）
- 回滚：两份 `espetro-1.1.3-x.jar.bak-skillgate`（服务端）与客户端同名备份改回原名

## 生效条件
- 客户端侧判断：玩家换上新客户端 jar 后**立即生效**（无需重启服务端）
- 服务端主动收回入口：需服务端重启加载新 jar

## 已知取舍
技能列现在只认「指挥官」，小队长类技能（`usableBy` 含 `SQUAD_LEADER`）不会在 J 菜单列出，
只在轮盘技能页显示（轮盘用的是服务端过滤后的技能列表）。当前编制里没有小队长技能，暂无实际影响。
