# 功能记录：职业/部署菜单「重新部署」按钮（2026-10-03）

## 需求
在玩家职业选择（统一部署）菜单中，**载具信息右侧**增加一个**红色**「重新部署」按钮：
点击后**二次确认**，确认后**立刻击杀玩家（无论在哪）**；
**部署阶段**重新部署**不扣票**，**对战阶段**重新部署**扣票**。

## 实现（Espetro 模组）
| 文件 | 改动 |
|---|---|
| `src/main/java/org/espetro/client/gui/UnifiedDeployScreen.java` | 地图页脚（载具信息右侧）新增红色按钮 + 二次确认弹窗；颜色常量 `REDEPLOY_BG_NORMAL/HOVER` |
| `src/main/java/org/espetro/network/RedeployRequestPacket.java` | **新增** C→S 空包，服务端收到即处理 |
| `src/main/java/org/espetro/network/NetworkManager.java` | 注册新包 + 客户端 `requestRedeploy()` |
| `src/main/java/org/espetro/team/RedeployService.java` | **新增**：校验阶段 → 广播 → `player.kill()` |
| `src/main/java/org/espetro/team/TroopCountManager.java` | 把阵亡扣票逻辑抽成 `applyDeathTicketCost(ServerPlayer)` 供重新部署复用 |

### 界面
- 位置：地图区页脚按钮行，`载具信息` 右侧（对战阶段）；部署阶段 `载具信息` 不显示时紧随 `玩家分数板/发起弹劾`。
- 样式：红底（`0xFF7A1B1B`，悬停 `0xFFA32626`）+ 白色加粗 `重新部署`。
- 二次确认：复用现有居中确认框（红标题 + `确定`/`取消`，默认高亮取消防误触）。
  - 对战阶段文案：`对战阶段重新部署会立刻阵亡，并按本职业扣除兵力。确定重新部署？`
  - 部署阶段文案：`部署阶段重新部署不会扣除兵力，会立刻阵亡并重选部署点。确定？`
- 可用性：与既有「前哨重新部署」一致 —— 仅在**玩家活着（非等待部署点）**时可点，避免已阵亡时重复击杀。

### 服务端票数规则
- **部署阶段（DEPLOYING）**：只击杀，不扣兵力（`TroopCountManager` 的死亡钩子本就只在 BATTLE 生效）。
- **对战阶段（BATTLE）**：按**阵亡规则**扣兵力 —— 该职业 `troop_value`，指挥官再额外扣 `commander_death_penalty`，
  并广播战报与检查胜负。战场地图内的死亡由 `LivingDeathEvent` 自动扣；
  若玩家对战期间不在战场地图内（如留在主城），死亡事件不触发，`RedeployService` 显式补扣一次，避免换地图规避扣票。
  两条路径互斥，保证**只扣一次**。
- 击杀后走原有死亡流程：等待兵站/部署点选择 → 复活 → 统一部署界面（职业可重选，受原有换职冷却约束）。
- 无额外冷却（未在需求中提出；对战阶段本身有票数成本）。
- 底部状态栏原有的「重新部署」（仅防守方 / 仅布防期 / 60s 冷却 / 走 `/outpost redeploy`）已**改名为「前哨重部署」**，
  宽度 66→78px，避免与新按钮同名混淆。

## 构建与部署
- 构建：`.\gradlew.bat build -x test "-PauratipJar=...\auratip-forge-1.20.1-1.1.3-beta-espetro.1.jar"` → `build/libs/espetro-1.1.3-i.jar`
  （改名后最终产物 16,603,191 B，SHA256 `903B61494707EC13…`；`mods.toml` 内部版本仍为 `1.1.3-i`）
- **同源性校验**：线上活动 jar（`mods/espetro-1.1.3-x.jar`，16,599,142 B）与首次新构建逐条目比对，
  差异**仅**为本次 5 处改动（新增 2 个 class、修改 3 个 class），无 A-only 条目 → 工作区源码与线上完全同源。
- 部署：服务端 `mods/espetro-1.1.3-x.jar`（覆盖，回下载 SHA256 一致 `903B61494707EC13…`）；
  旧 jar 另存 `mods/espetro-1.1.3-x.jar.bak-redeploy`；
  客户端 `…\Squad预发布测试\mods\espetro-1.1.3-x.jar` 同步替换（客户端需**重启游戏**才会加载新 jar）。
- 重启验证：`Done (3.246s)`，除既有 TACZ 数据包噪音（`suffuse:knife`、`ciblr:cib_smith_table`）外无新增报错。

## 回滚点
- `build/tmp/srv-espetro-active.jar`（改动前的线上 jar，SHA256 `2F4BE607EF12CFDC…`）
- 服务端 `mods/espetro-1.1.3-x.jar.bak-redeploy`

## 备注 / 可调整项
- 现有「前哨重部署」按钮（仅防守方、仅布防期、带 60s 冷却、走 `/outpost redeploy`）仍在底部状态栏，已改名区分。
- 如果需要冷却时间、固定扣票数（而非职业兵力值）、或允许在等待部署点时也按（当前禁用），说一声即可调整。
- 服务端 `kubejs/server_scripts/` 下仍有两个未禁用的历史探针脚本（`zz_angle_probe2.js`、`zz_impact_tag_probe.js`），
  每次启动/运行时会在日志里输出调试信息；如不再需要可改名 `.disabled`（未擅自处理，可能仍在用于方向抗性调试）。
