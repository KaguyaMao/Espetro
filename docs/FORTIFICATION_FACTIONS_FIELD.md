# 工事「编制专属」字段：`requirements.factions`

## 位置
`config/espetro/fortifications.json` → `fortifications[].requirements.factions`

## 语义
| 写法 | 效果 |
|---|---|
| 省略，或 `"factions": []` | **所有编制**都能部署该工事（默认值，向后兼容，不影响任何现有配置） |
| `"factions": ["pla_112th_brigade", "us_1th_ar"]` | **只有列出的编制**能部署该工事；其它编制在轮盘里看不到它，直接发包也会被服务端拒绝 |

- 取值 = `EsFactions/<id>.json` 的**文件名**（例：`pla_112th_brigade`、`pla_195th`、`us_1th_ar`、`ru_3th` …），大小写不敏感
- 与 `usable_by`（角色：`commander` / `squad_leader` / `fireteam_leader`）是**与**关系：
  角色满足 **且** 编制在列表里，才能部署
- 兼容别名：`usable_factions`、`faction_ids`

## 示例
把「载具补给站」限定为只有第 112 旅和美军第 1 装甲营能部署：

```json
{
  "id": "espetro:vehicle_supply_station",
  "display_name": "载具补给站",
  "behavior": "vehicle_supply_station",
  "cost": { "construction": 200, "ammunition": 0 },
  "requirements": {
    "require_radio_range": true,
    "usable_by": ["commander", "squad_leader", "fireteam_leader"],
    "factions": ["pla_112th_brigade", "us_1th_ar"]
  }
}
```

改完**重启服务端**生效（工事配置在启动时事务冻结）。

## 生效链路（三处一致）
1. **服务端权威校验**：`FortificationManager.canUse(...)` → `roleOk && factionAllowed(...)`；
   `validateSelectionRole(...)` 在**开始预览**和**确认放置**两处都会再校验一次
2. **客户端轮盘**：服务端下发的 `FortificationCatalogPacket` 按 `canUse` 逐条过滤 →
   不符合的玩家在「建造工事」页里**根本看不到**该工事（不是点了才被拒）
3. **拒绝提示**：角色不够 → `§c你没有权限建造该工事。`；编制不符 → `§c你的编制无法部署该工事。`

## 排错
- 配置加载时会**去空、去重**；若此时编制表已加载且列表里的 id 找不到，会打一条**告警**（不致命，不会剔除该工事定义）：
  ```
  WARN 工事配置 fortifications[N].requirements.factions: 未找到编制 xxx（该工事将无人能部署）
  ```
- 编制 id 想确认的话，看服务端 `EsFactions/` 目录里的文件名即可
- 别名 `legacy_ids` 与迁移无关，不要混用

## 本次实现涉及的代码
| 文件 | 改动 |
|---|---|
| `FortificationConfig.java` | `Requirements` 新增 `factions` 字段（含别名）+ `normalizeFactions(path)`（去空去重 + 编制存在性告警）；新生成的默认配置里带 `"factions": []` 作为示例 |
| `FortificationManager.java` | `canUse(...)` 增加编制检查并抽出 `factionAllowed(...)`；`validateSelectionRole(...)` 对"编制不符"给出专门提示 |

> 说明：`factions` 是**可选**字段，不写就是"所有编制可用"，因此现有 10 个工事定义的行为完全不变。
